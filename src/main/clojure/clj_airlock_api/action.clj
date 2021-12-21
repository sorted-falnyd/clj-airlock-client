(ns clj-airlock-api.action
  (:require
   [clojure.spec.alpha :as s]
   [clj-airlock-api.spec :as as])
  (:import
   (java.util.concurrent.atomic AtomicInteger)))

(defonce counter (let [i (AtomicInteger. 0)] (fn [] (.incrementAndGet i))))

(defmulti action-spec :action)

(s/def ::action (s/multi-spec action-spec :action))

(s/def :action/id ::as/id)
(s/def :action/ship ::as/ship)
(s/def :action/app ::as/app)

;;; POKE

(s/def :poke/mark ::as/mark)

(s/def :poke/json string?)

(s/def :poke/action #{:poke})

(s/def :action/poke
  (s/keys :req-un [:action/id
                   :action/ship
                   :action/app
                   :poke/mark
                   :poke/json
                   :poke/action]))

(defmethod action-spec :poke [_] :action/poke)

(defn poke
  ([app mark json]
   {:action :poke
    :mark mark
    :app app
    :json json})
  ([ship-name app mark json]
   {:action :poke
    :mark mark
    :app app
    :ship ship-name
    :json json}))

(defprotocol IPoke
  (-poke! [this app mark json] [this uri app mark json]))

(defn poke!
  ([this app mark json]
   (-poke! this app mark json))
  ([this uri app mark json]
   (-poke! this uri app mark json)))

;;; SUBSCRIBE

(s/def :subscribe/action #{:subscribe})
(s/def :subscribe/path string?)
(s/def :subscribe/action string?)

(s/def :action/subscribe
  (s/keys :req-un [:action/id
                   :action/ship
                   :subscribe/action
                   :action/app
                   :subscribe/path]))

(defmethod action-spec :subscribe [_] :action/subscribe)

(defn subscribe
  [ship app path]
  {:action :subscribe
   :ship ship
   :app app
   :path path})

(defprotocol ISubscribe
  (-subscribe! [this app path]))

(defn subscribe!
  [this app path]
  (-subscribe! this app path))

;;; ACK

(s/def :ack/action #{:ack})
(s/def ::event-id pos-int?)

(s/def :action/ack
  (s/keys :req-un [:ack/action
                   :action/id
                   ::event-id]))

(defmethod action-spec :ack [_] :action/ack)

(defn ack
  [event-id]
  {:event-id event-id
   :action :ack})

(defprotocol IAck
  (-ack! [this event-id]))

(defn ack!
  [this event-id]
  (-ack! this event-id))

;;; UNSUBSCRIBE

(s/def :unsubscribe/subscription pos-int?)
(s/def :unsubscribe/action #{:unsubscribe})

(s/def :action/unsubscribe
  (s/keys :req-un [:unsubscribe/action
                   :action/id
                   :unsubscribe/subscription]))

(defmethod action-spec :unsubscribe [_] :action/unsubscribe)

(defn unsubscribe
  ([subscription]
   {:action :unsubscribe
    :subscription subscription}))

(defprotocol IUnsubscribe
  (-unsubscribe! [this subscription]))

(defn unsubscribe!
  [this subscription]
  (-unsubscribe! this subscription))

;;; DELETE

(s/def :delete/action #{:delete})
(s/def :action/delete
  (s/keys :req-un [:action/id :delete/action]))

(defmethod action-spec :delete [_] :action/delete)

(defn delete
  ([] {:action :delete})
  ([_] {:action :delete})
  ([_ & _] {:action :delete}))

(defprotocol IDelete
  (-delete! [this]))

(defn delete!
  [this]
  (-delete! this))

;;; Scry

(defn scry
  [app path]
  {:action :scry
   :app app
   :path path})

;;; Thread

(defn thread
  ([input-mark output-mark thread-name body]
   {:action :thread
    :input-mark input-mark
    :output-mark output-mark
    :thread-name thread-name
    :body body})
  ([input-mark output-mark thread-name body desk]
   {:action :thread
    :input-mark input-mark
    :output-mark output-mark
    :thread-name thread-name
    :desk desk
    :body body}))
