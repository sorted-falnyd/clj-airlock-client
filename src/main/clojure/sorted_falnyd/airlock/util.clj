(ns sorted-falnyd.airlock.util
  (:import
   (java.util.concurrent.atomic AtomicInteger))
  (:require [clojure.string :as str]))

(defonce counter (let [i (AtomicInteger. 0)] (fn [] (.incrementAndGet i))))
(def da-unix-epoch 170141184475152167957503069145530368000N)
(def da-second 18446744073709551616N)
(def offset (/ da-second 2000N))

(defn da->unix
  "Convert Urbit date to Unix Epoch."
  [da]
  (/ (*' (+' offset (-' da da-unix-epoch)) 1000N) da-second))

(defn unix->da
  "Convert Unix Epoch to Urbit date."
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
  "Ensure a ship name starts with `~`."
  [ship]
  (if (str/starts-with? ship "~")
    ship
    (str "~" ship)))

(defn desig
  "Ensure a ship name does not start with `~`."
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

(defn decode-resource
  [s]
  (cond

    (string? s)
    (let [[_ _ ship group] (str/split s #"/")]
      (keyword (desig ship) group))

    (map? s)
    (let [{:keys [name ship]} s] (keyword ship name))))

(defn uri
  [& args]
  (java.net.URI/create (apply str args)))

(defn remove-nils
  [m]
  (into {} (remove (fn [[_ v]] (nil? v))) m))
