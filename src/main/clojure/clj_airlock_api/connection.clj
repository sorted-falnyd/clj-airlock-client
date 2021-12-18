(ns clj-airlock-api.connection
  (:require
   [clojure.tools.logging :as log]
   [jsonista.core :as json]
   [clojure.spec.alpha :as s]
   [clj-sse-client.util :as cu]
   [clj-sse-client.sse :as sse]
   [clj-sse-client.body-handler :as bh]
   [clj-sse-client.client :as http]
   [clj-airlock-api.action :as action])
  (:import
   #_(java.util.function BiFunction)
   #_(java.util.concurrent CompletableFuture)
   (java.util.concurrent.atomic AtomicInteger)))

(defonce counter (let [i (AtomicInteger. 0)] (fn [] (.incrementAndGet i))))

(s/def ::code #(boolean (re-matches #"^[a-z]{6}-[a-z]{6}-[a-z]{6}-[a-z]{6}$" %)))
(s/def :login/body #(boolean (re-matches #"^password=[a-z]{6}-[a-z]{6}-[a-z]{6}-[a-z]{6}$" %)))

(s/def :method/post #{:post})

(s/def ::login-request
  (s/keys :req-un [::http/uri ::code]
          :opt-un [::http/port]))

(defn -login-request
  "Build request to obtain a cookie"
  [{:keys [uri code]}]
  {:uri (str uri "/~/login")
   :body (str "password=" code)
   :method :post})

(s/fdef -login-request
  :args (s/cat :login-request ::login-request)
  :ret (s/keys :req-un [::http/uri :method/post :login/body]))

(defn extract-cookie
  [resp]
  (let [c (http/strip-cookie (first (http/find-set-cookie resp)))]
    (str (.getName c) "=" (.getValue c))))

(defprotocol IntoRequestFormat
  (-into-request-format [this]))

(extend-protocol IntoRequestFormat
  String
  (-into-request-format [this] this)
  clojure.lang.Sequential
  (-into-request-format [this] (json/write-value-as-string this))
  clojure.lang.IPersistentMap
  (-into-request-format [this] (-into-request-format [this])))

(s/def ::http-client #(instance? java.net.http.HttpClient %))
(s/def ::cookie string?)
(s/def ::ship-name string?)

(s/def ::ship
  (s/keys :req-un [::http/uri ::ship-name]
          :opt-un [::http/client ::cookie]))

;;; HTTP Client behavior

(defprotocol IClient
  (-request [this request])
  (-put [this uri body]))

(defprotocol IAsyncClient
  (-request-async [this request])
  (-put-async [this uri body])
  (-post-async [this uri body]))

(defprotocol IntoRequest
  (-into-put [this body] [this uri body])
  (-into-get [this] [this uri])
  (-into-post [this body] [this uri body]))

;;; Lifecycle

(defprotocol IShip
  (-login! [ship]))

(defprotocol IConnection
  (-build! [this])
  (-start! [this])
  (-stop! [this]))

;;; Implementation

(defn ensure-body-format
  [{:keys [body] :as request}]
  (cond-> request body (assoc :body (-into-request-format body))))

(defrecord Ship
    [^::http/uri uri
     ship-name
     client
     cookie
     code]

  IShip
  (-login! [this]
    (let [r (-login-request (assoc this :code code))
          resp (-request this r)]
      (cond-> this
        (not (http/failed? resp))
        (assoc :cookie (extract-cookie resp)))))

  IntoRequest
  (-into-put [_ body]
    {:body body
     :uri uri
     :method :put
     :headers {"Cookie" cookie}})

  (-into-put [_ uri body]
    {:body body
     :uri uri
     :method :put
     :headers {"Cookie" cookie}})

  (-into-post [_ body]
    {:body body
     :uri uri
     :method :post
     :headers {"Cookie" cookie}})

  (-into-post [_ uri body]
    {:body body
     :uri uri
     :method :post
     :headers {"Cookie" cookie}})

  (-into-get [_]
    {:uri uri
     :method :get
     :headers {"Cookie" cookie}})

  (-into-get [_ uri]
    {:uri uri
     :method :get
     :headers {"Cookie" cookie}})

  IAsyncClient
  (-request-async [_ request]
    (http/send-async!
     client
     (ensure-body-format request)
     (bh/of-byte-array)))

  (-put-async [this uri body]
    (-request-async this (-into-put this uri body)))

  (-post-async [this uri body]
    (-request-async this (-into-post this uri body)))

  IClient

  (-request [_ request]
    (log/debug "Request:" request)
    (let [resp
          (http/send!
           client
           (ensure-body-format request)
           (bh/of-byte-array))]
      (log/debug "Response:" resp)
      resp))

  (-put [this uri body]
    (-request this (-into-put this uri body)))

  action/IPoke
  (-poke! [this uri app mark json]
    (-put-async this uri (action/poke mark app ship-name json))))

(defn ensure-port
  [uri port]
  (let [uri (cu/-uri uri)]
    (if (neg? (.getPort ^java.net.URI uri))
      (cu/-uri (str uri ":" port))
      uri)))

(defn make-ship
  ([] (make-ship {}))
  ([{:keys [uri port ship-name client code]
     :or {port 8080
          ship-name "zod"
          code "lidlut-tabwed-pillex-ridrup"
          uri "http://localhost"}}]
   (assert (s/valid? ::http/uri uri))
   (assert (s/valid? ::http/port port))
   (assert (s/valid? ::ship-name ship-name))
   (assert (s/valid? ::code code))
   (assert (s/valid? (s/nilable ::http-client) client))
   (let [uri (ensure-port uri port)
         client (or client (http/client))]
     (Ship. uri ship-name client nil code))))

(defn login!
  ([ship] (-login! ship))
  ([ship code] (-> ship (assoc :code code) -login!)))

(defn -on-subscribe
  [_this state]
  (log/debug "Initializing subscription with state:" state))

(defn -on-error
  [_this state ^Throwable t] (log/error t "Error with state:" state))

(defn -on-complete
  [_this state] (log/info "Subscription completed with state:" state))

(defn -on-next
  [this eff]
  (let [data (json/read-value (:data eff) json/keyword-keys-object-mapper)]
    (log/debug data)
    (action/ack! this (:id data))
    true))

(def default-callbacks
  {:on-next #'-on-next
   :on-complete #'-on-complete
   :on-error #'-on-error
   :on-subscribe #'-on-subscribe})

(defn prepare-callbacks
  [this callbacks]
  (into
   {}
   (map
    (fn [[k f]]
      [k (fn
           ([x] (f this x))
           ([x y] (f this x y)))]))
   callbacks))

(defrecord Connection
    [ship
     uri
     client
     sse-client
     sse-callbacks
     sse-connection
     subscription
     channel]

  IConnection
  (-build! [this]
    (assoc
     this
     :sse-connection
     (sse/sse-connection
      sse-client
      {:uri uri
       :method :get
       :headers {"Cookie" (:cookie ship)}}
      (prepare-callbacks this sse-callbacks)
      {})))

  (-start! [this]
    (.get (action/poke! this "hood" "helm-hi" "Opening airlock, welcome to Mars."))
    (assoc this :subscription (sse/connect sse-connection)))

  (-stop! [this] (.close subscription) this)

  action/IPoke
  (-poke! [_ app mark json] (action/poke! ship uri app mark json))

  action/IAck
  (-ack! [_ event-id] (-put-async ship uri (action/ack event-id)))

  action/ISubscribe
  (-subscribe! [_ app path]
    (->> (action/subscribe (:ship-name ship) app path)
         (-into-put ship uri)
         (-request-async ship)))

  action/IUnsubscribe
  (-unsubscribe! [_ subscription]
    (->> subscription
         action/unsubscribe
         (-into-put ship uri)
         (-request-async ship)))

  action/IDelete
  (-delete! [_]
    (->> (action/delete)
         (-into-put ship uri)
         (-request-async ship))))

(defn -channel-name [] (System/currentTimeMillis))

(defn make-connection
  "Make a connection for a specific channel"
  ([ship] (make-connection ship {}))
  ([ship {:keys [client channel callbacks]}]
   (let [channel (or channel (-channel-name))
         uri (str (:uri ship) "/~/channel/" channel)
         callbacks (merge default-callbacks callbacks)
         client (or client (http/client))]
     (map->Connection
      {:ship ship
       :uri uri
       :client (:client ship)
       :sse-client client
       :sse-callbacks callbacks
       ;; sse-connection
       ;; subscription
       :channel channel
       }))))

(comment
  (def tot (-> {} make-ship login! make-connection -build! -start!))
  (def r (action/subscribe! tot "graph-store" "/updates"))
  (.get r)
  )


