(ns sorted-falnyd.airlock.connection
  (:require
   [clojure.tools.logging :as log]
   [jsonista.core :as json]
   [promesa.core :as p]
   [clojure.core.async :as a]
   [clj-sse-client.sse :as sse]
   [clj-sse-client.client :as client]
   [sorted-falnyd.airlock.action :as action]
   [sorted-falnyd.airlock.http :as http]
   [sorted-falnyd.airlock.cache :as cache]))

;;; Implementation

(defn -on-subscribe
  [_this state]
  (log/debug "Initializing subscription with state:" state))

(defn -on-error
  [_this state ^Throwable t] (log/error t "Error with state:" state))

(defn -on-complete
  [_this state] (log/info "Subscription completed with state:" state))

(defn -on-next
  [this eff]
  (let [data (json/read-value (:data eff))]
    (log/debug data)
    (http/handle-sse-response! this data)
    (a/put! (:channel this) data)
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

(def login-poke
  (action/poke "hood" "helm-hi" "Opening airlock, welcome to Mars."))

(defn -channel-name [] (System/currentTimeMillis))

(defn make-connection
  "Make a connection for a specific channel"
  ([ship] (make-connection ship {}))
  ([ship {:keys [client channel callbacks buf-or-n]
          :or {buf-or-n 1024}}]
   (let [channel (or channel (-channel-name))
         chan (a/chan buf-or-n)
         uri (str (:uri ship) "/~/channel/" channel)
         cache (cache/->Cache p/resolve! p/deferred)
         callbacks (merge default-callbacks callbacks)
         client (or client (client/client))]
     (merge
      ship
      {:channel-uri uri
       :cache cache
       :channel chan
       :sse-client client
       :sse-callbacks callbacks
       :channel-name channel}))))

(defn build!
  [{:keys [sse-client channel-uri cookie sse-callbacks]
    :as this}]
  (assoc
   this
   :sse-connection
   (sse/sse-connection
    sse-client
    {:uri channel-uri
     :method :get
     :headers {"Cookie" cookie}}
    (prepare-callbacks this sse-callbacks)
    {})))

(defn start!
  [{:keys [sse-connection] :as this}]
  (http/send! this login-poke)
  (assoc this :sse-subscription (sse/connect sse-connection)))
