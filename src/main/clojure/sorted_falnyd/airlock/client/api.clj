(ns sorted-falnyd.airlock.client.api
  (:require
   [sorted-falnyd.airlock.ship :as ship]
   [sorted-falnyd.airlock.connection :as conn]
   [sorted-falnyd.airlock.http :as http]
   [sorted-falnyd.airlock.action :as action]
   [sorted-falnyd.airlock.graph :as graph]
   [clojure.core.async :as a]))

(defn client
  ([] (client {}))
  ([options]
   (->
    options
    ship/make-ship
    ship/login!
    conn/make-connection
    conn/build!
    conn/start!)))

(defn send!
  [client action]
  (http/send! client action))
