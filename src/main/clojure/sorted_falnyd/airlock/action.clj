(ns sorted-falnyd.airlock.action
  "Action constructors.

  Should be sent to a connection via `send!`."
  (:require
   [clojure.spec.alpha :as s]
   [sorted-falnyd.airlock.spec :as as])
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
  "Subscribe to `app` on `path`.
  Example:
  (subscribe \"graph-store\" \"/updates\")"
  [app path]
  {:action :subscribe
   :app app
   :path path})

;;; ACK

(s/def :ack/action #{:ack})
(s/def ::event-id pos-int?)

(s/def :action/ack
  (s/keys :req-un [:ack/action
                   :action/id
                   ::event-id]))

(defmethod action-spec :ack [_] :action/ack)

(defn ack
  "Ack `event-id`.
  Sent automatically by a client."
  [event-id]
  {:event-id event-id
   :action :ack})

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

;;; DELETE

(s/def :delete/action #{:delete})
(s/def :action/delete
  (s/keys :req-un [:action/id :delete/action]))

(defmethod action-spec :delete [_] :action/delete)

(defn delete
  ([] {:action :delete})
  ([_] {:action :delete})
  ([_ & _] {:action :delete}))

;;; Scry

(defn scry
  "Scry an `app` on `path`.
  Useful by more advanced constructors."
  [app path]
  {:action :scry
   :app app
   :path path})

;;; Thread

(defn thread
  ([input-mark output-mark thread-name body]
   (thread input-mark output-mark thread-name body "base"))
  ([input-mark output-mark thread-name body desk]
   {:action :thread
    :input-mark input-mark
    :output-mark output-mark
    :thread-name thread-name
    :desk desk
    :body body}))
