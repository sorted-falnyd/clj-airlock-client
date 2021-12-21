(ns clj-airlock-api.ship
  (:require
   [clj-sse-client.client :as http]
   [clj-sse-client.util :as cu]
   [clojure.spec.alpha :as s]))

(s/def ::code #(boolean (re-matches #"^[a-z]{6}-[a-z]{6}-[a-z]{6}-[a-z]{6}$" %)))
(s/def :login/body #(boolean (re-matches #"^password=[a-z]{6}-[a-z]{6}-[a-z]{6}-[a-z]{6}$" %)))

(s/def :method/post #{:post})

(s/def ::cookie string?)
(s/def ::ship-name string?)

(s/def ::ship
  (s/keys :req-un [::http/uri ::ship-name]
          :opt-un [::http/client ::cookie]))

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

(defn ensure-port
  [uri port]
  (let [uri (cu/-uri uri)]
    (if (neg? (.getPort ^java.net.URI uri))
      (cu/-uri (str uri ":" port))
      uri)))

(def zod {:port 8080
          :ship-name "zod"
          :code "lidlut-tabwed-pillex-ridrup"
          :uri "http://localhost"})

(defn make-ship
  ([] (make-ship {}))
  ([{:keys [uri port client]
     :or {port 8080
          uri "http://localhost"}
     :as ship}]
   (let [uri (ensure-port uri port)
         client (or client (http/client))
         ship (merge zod ship {:uri uri :client client})]
     (assert (s/valid? ::ship ship))
     ship)))

(defn login!
  [{:keys [code client] :as ship}]
  (let [r (-login-request (assoc ship :code code))
        resp (http/send-sync! client r)]
    (cond-> ship
      (not (http/failed? resp))
      (assoc :cookie (extract-cookie resp)))))
