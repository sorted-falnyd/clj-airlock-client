(ns clj-airlock-api.spec
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::id pos-int?)
(s/def ::ship string?)
(s/def ::app string?)
(s/def ::mark string?)
