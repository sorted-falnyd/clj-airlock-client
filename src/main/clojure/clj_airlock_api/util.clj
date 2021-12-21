(ns clj-airlock-api.util
  (:import
   (java.util.concurrent.atomic AtomicInteger))
  (:require [clojure.string :as str]))

(defonce counter (let [i (AtomicInteger. 0)] (fn [] (.incrementAndGet i))))
(def da-unix-epoch 170141184475152167957503069145530368000N)
(def da-second 18446744073709551616N)
(def offset (/ da-second 2000N))

(defn da->unix
  [da]
  (/ (*' (+' offset (-' da da-unix-epoch)) 1000N) da-second))

(defn unix->da
  [t]
  (-> t
      bigint
      (*' da-second)
      (/ 1000N)
      (+' da-unix-epoch)
      bigint))

(defn da-time
  []
  (-> (System/currentTimeMillis) unix->da))

(defn ensig
  [ship]
  (if (str/starts-with? ship "~")
    ship
    (str "~" ship)))

(defn desig
  [ship]
  (if (str/starts-with? ship "~")
    (subs ship 1)
    ship))

(defn sbrf
  (^StringBuilder [] (StringBuilder.))
  (^String [^StringBuilder sb] (.toString sb))
  (^StringBuilder [^StringBuilder sb x] (.append sb x) sb))

(defn into-string
  [xf s]
  (transduce xf sbrf s))

(let [xf (comp (partition-all 3)
            (interpose '("."))
            cat)]
  (defn dec->ud
    [s]
    (into-string xf s)))

(let [xf (remove nil?)]
  (defn -str
    (^String [] "")
    (^String [^Object x]
     (if (nil? x) "" (. x (toString))))
    (^String [^Object x & xs]
     (transduce xf sbrf (doto (sbrf) (sbrf x)) xs))))
