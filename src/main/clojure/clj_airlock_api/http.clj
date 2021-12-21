(ns clj-airlock-api.http
  (:require
   [jsonista.core :as json]
   [clj-airlock-api.cache :as cache]
   [clj-airlock-api.util :as u]
   [clj-sse-client.client :as http]
   [clj-sse-client.body-handler :as bh]
   [clj-airlock-api.action :as action]))

(defprotocol IClient
  (-request [this request])
  (-put [this uri body]))

(defprotocol IAsyncClient
  (-request-async [this request])
  (-put-async [this uri body])
  (-post-async [this uri body])
  (-get-async [this uri]))

(defprotocol IntoRequest
  "Contextual request constructors which ensure relatively correct URIs
  and cookies are injected."
  (-into-put [this body] [this uri body])
  (-into-get [this] [this uri])
  (-into-post [this body] [this uri body]))

(defprotocol IntoRequestFormat
  (-into-request-format [this]))

(extend-protocol IntoRequestFormat
  String
  (-into-request-format [this] this)
  clojure.lang.Sequential
  (-into-request-format [this] (json/write-value-as-bytes this))
  clojure.lang.IPersistentMap
  (-into-request-format [this] (-into-request-format [this])))

(defn ensure-body-format
  [{:keys [body] :as request}]
  (cond-> request body (assoc :body (-into-request-format body))))

(defmulti -into-request (fn [action _actor] (:action action)))

(defmethod -into-request :default
  [action {:keys [channel-uri ship-name cookie] :as _actor}]
  {:method :put
   :uri channel-uri
   :headers {"Cookie" cookie}
   :body (assoc action :ship ship-name)})

(defmethod -into-request :thread
  [{:keys [input-mark output-mark thread-name body]}
   {:keys [uri cookie] :as _actor}]
  {:method :post
   :uri (str uri "/spider/" input-mark "/" thread-name "/" output-mark)
   :headers {"Cookie" cookie}
   :body body})

(defmethod -into-request :scry
  [{:keys [app path]}
   {:keys [uri cookie] :as _actor}]
  {:method :get
   :uri (str uri "/~/scry/" app path ".json")
   :headers {"Cookie" cookie}})

(defn -send!
  [this action]
  (http/send!
   (:client this)
   (-> action
       (-into-request this)
       ensure-body-format)
   (bh/of-byte-array)))

(defn send-sse-action!
  [this action]
  (let [id (u/counter)
        p (cache/-promise! (:cache this) id)]
    (-send! this (assoc action :id id))
    p))

(defmulti send! (fn [_client action] (:action action)))
(defmethod send! :default [client action] (send-sse-action! client action))
(defmethod send! :ack [client action] (-send! client action))
(defmethod send! :scry [client action] (-send! client action))
(defmethod send! :thread [client action] (-send! client action))

(defn handle-sse-response!
  [{:keys [cache] :as client} {:keys [id]}]
  (send! client (action/ack id))
  (cache/-deliver! cache id))
