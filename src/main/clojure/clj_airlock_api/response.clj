(ns clj-airlock-api.response
  (:require
   [clojure.spec.alpha :as s]
   [clj-airlock-api.spec :as as]))

(s/def :response/id pos-int?)

(s/def :poke-ack/ok string?)
(s/def :poke-ack/err string?)
(s/def :poke-ack/response #{"poke"})

(s/def :response/poke-p-ack (s/keys :req-un [:response/id :poke-ack/ok :poke-ack/response]))
(s/def :response/poke-n-ack (s/keys :req-un [:response/id :poke-ack/err :poke-ack/response]))

(defmulti poke-ack-spec (comp boolean :ok))
(s/def :response/poke-ack (s/multi-spec poke-ack-spec (comp boolean :ok)))

(defmethod poke-ack-spec true [_] :response/poke-p-ack)
(defmethod poke-ack-spec false [_] :response/poke-n-ack)

(defmulti response-spec :response)
(s/def ::response (s/multi-spec response-spec :response))

(defmethod response-spec "poke" [_] :response/poke-ack)
