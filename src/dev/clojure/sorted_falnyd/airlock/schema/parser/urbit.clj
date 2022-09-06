(ns sorted-falnyd.airlock.schema.parser.urbit
  (:require
   [clojure.string :as str]))

(defn parse-resource
  [r]
  (cond
    (string? r) (apply keyword (str/split (str/replace-first r "/ship/~" "") #"/"))
    (keyword? r) r
    :else r))

(defn parse-ship
  [r]
  (cond
    (string? r) (str/replace-first r "~" "")
    (keyword? r) (name r)
    :else r))

(defn make-resource
  [ship name]
  (keyword (parse-ship ship) name))
