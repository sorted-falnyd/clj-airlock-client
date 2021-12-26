(ns sorted-falnyd.airlock.response
  (:require
   [clojure.spec.alpha :as s]
   [sorted-falnyd.airlock.spec :as as]))

(s/def :response/id ::as/id)
(s/def :response/ok string?)
(s/def :response/err string?)

(defmulti response-spec :response)
(s/def ::response (s/multi-spec response-spec :response))

(s/def :poke/response #{"poke"})

(s/def :response/poke-ack
  (s/or
   :positive (s/keys :req-un [:response/id :poke/response :response/ok])
   :negative (s/keys :req-un [:response/id :poke/response :response/err])))

(defmethod response-spec "poke" [_] :response/poke-ack)

(comment
  (s/valid? ::response {:id 1 :ok "ok" :response "poke"})
  (s/valid? ::response {:id 1 :err "ok" :response "poke"})
  (s/conform ::response {:id 1 :err "ok" :response "poke"}))

(s/def :subscribe/response #{"subscribe"})

(s/def :response/subscribe-ack
  (s/or
   :positive (s/keys :req-un [:response/id :subscribe/response :response/ok])
   :negative (s/keys :req-un [:response/id :subscribe/response :response/err])))

(defmethod response-spec "subscribe" [_] :response/subscribe-ack)

(s/def :diff/json any?)
(s/def :diff/response #{"diff"})

(s/def :response/diff
  (s/keys :req-un [:response/id :diff/response :response/ok]))

(defmethod response-spec "diff" [_] :response/diff)

(s/def :quit/response #{"diff"})

(s/def :response/quit
  (s/keys :req-un [:response/id :quit/response :response/ok]))

(defmethod response-spec "quit" [_] :response/quit)
