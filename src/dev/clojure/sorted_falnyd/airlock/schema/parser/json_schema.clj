(ns sorted-falnyd.airlock.schema.parser.json-schema
  (:require
   [jsonista.core :as json]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.walk :as walk])
  (:import
   (java.net URLDecoder)))

(defn graph [] (json/read-value (io/resource "./graph-update2.json")))
(defn contact [] (json/read-value (io/resource "./contacts-update2.json")))
(defn metadata [] (json/read-value (io/resource "./metadata-update2.json")))
(defn group [] (json/read-value (io/resource "./group-update.json")))

(defn qualify-definitions
  [d prefix]
  (let [p (str prefix ".")
        p' (str "#/definitions/" p)]
    (into
     {}
     (map (fn [[k v]]
            [(str p k)
             (walk/postwalk
              (fn [o]
                (if (string? o)
                  (str/replace o "#/definitions/" p')
                  o))
              v)]))
     d)))

(defmulti json-schema-type #(get % "type"))

(defmethod json-schema-type "object"
  [{:strs [properties required]}]
  (let [required (set required)]
    (cond
      (seq properties)
      (into
       [:map]
       (map (fn [[k v]]
              [(keyword k)
               {:optional (not (contains? required k))}
               (json-schema-type v)]))
       properties)

      :else [:ref "empty"])))

(defn maybe-zip
  [& kvs]
  (into {} (comp (partition-all 2) (filter second)) kvs))

(defn parse-array
  [{:strs [items maxItems minItems]}]
  (conj
   (if (or maxItems minItems)
     [:vector (maybe-zip :max maxItems :min minItems)]
     [:vector])
   (json-schema-type items)))

(defn parse-tuple
  [{:strs [items]}]
  (into [:tuple] (map json-schema-type) items))

(defmethod json-schema-type "array"
  [{:strs [items] :as m}]
  (if (map? items) (parse-array m) (parse-tuple m)))

(defn parse-ref
  [m]
  (when-let [ref (get m "$ref")]
    [:ref (URLDecoder/decode (str/replace-first ref "#/definitions/" ""))]))

(defn parse-any-of
  [m]
  (when-let [any-of (get m "anyOf")]
    (let [ts (mapv json-schema-type any-of)]
      (cond
        (and (= (count ts) 2) (some #{:nil} ts))
        [:maybe (first (remove #{:nil} ts))]

        (some #(and (vector? %)
                    (= :map (first %))
                    (< 2 (count %))) ts)
        (into [:or] ts)

        :else (into [:orn]
                    (comp
                     (map (fn [[t k :as v]]
                            (cond
                              (and (vector? v) (= :map t)) [(first k) v]
                              (and (vector? v) (= :ref t)) [(keyword k) v]
                              k [(keyword k) v]
                              :else [:empty :nil]))))
                    ts)))))

(defn parse-all-of
  [m]
  (when-let [all-of (get m "allOf")]
    (into [:and] (map json-schema-type) all-of)))

(defn -parse-default
  [m]
  (or (parse-ref m) (parse-any-of m) (parse-all-of m)))

(defmethod json-schema-type :default [m] (-parse-default m))
(defmethod json-schema-type nil [m] (-parse-default m))

(defmethod json-schema-type "string"
  [{:keys [enum]}]
  (cond
    enum (into [:enum] enum)
    :else :string))

(defmethod json-schema-type ["string" "null"] [_] [:maybe :string])
(defmethod json-schema-type "null" [_] :nil)

(defmethod json-schema-type "number" [_] :int)
(defmethod json-schema-type "boolean" [_] :boolean)

(defn type-parameters
  [s]
  (when-let [s (re-find #"<(\S+)>" s)]
    (str/split (second s) #",")))

(defn parse-definitions
  [m prefix]
  (->>
   (-> m (get "definitions") (qualify-definitions prefix))
   (reduce-kv
    (fn [m k v]
      (if (str/starts-with? k "Record<")
        (let [[ks vs] (type-parameters k)]
          (assoc m k [:map-of ks vs]))
        (assoc m k (or (json-schema-type v) :any))))
    {})))
