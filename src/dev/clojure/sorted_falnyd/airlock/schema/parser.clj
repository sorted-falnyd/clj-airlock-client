(ns sorted-falnyd.airlock.schema.parser
  (:require
   [jsonista.core :as json]
   [malli.core :as m]
   [malli.transform :as mt]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.walk :as walk]))

(defonce graph (json/read-value (io/resource "./graph.json")))
(defonce contact (json/read-value (io/resource "./contacts.json")))
(defonce metadata (json/read-value (io/resource "./metadata-update.json")))

(defmulti json-schema-type #(get % "type"))

(defmethod json-schema-type "object"
  [{:strs [properties]}]
  (into
   [:map]
   (map (fn [[k v]] [(keyword k) (json-schema-type v)]))
   properties))

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
    [:ref (str/replace-first ref "#/definitions/" "")]))

(defn parse-any-of
  [m]
  (when-let [any-of (get m "anyOf")]
    (into [:orn]
          (comp
           (map json-schema-type)
           (map (fn [[_ k :as v]]
                  [(keyword k) v])))
          any-of)))

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

(defmethod json-schema-type "number" [_] :int)
(defmethod json-schema-type "boolean" [_] :boolean)

(defn type-parameters
  [s]
  (when-let [s (re-find #"<(\S+)>" s)]
    (str/split (second s) #",")))

(defn uniq-anon-types
  [d]
  (let [f (memoize (comp str gensym))]
    (walk/postwalk
     (fn [o]
       (if (and (string? o)
                (str/starts-with? o "__type"))
         (f o)
         o))
     d)))

(comment
  (uniq-anon-types graph))

(defn parse-definitions
  [m]
  (->>
   (get m "definitions")
   (reduce-kv
    (fn [m k v]
      (if (str/starts-with? k "Record<")
        (let [[ks vs] (type-parameters k)]
          (assoc m k [:map-of ks vs]))
        (assoc m k (json-schema-type v))))
    {})
   uniq-anon-types))

(def r (parse-definitions graph))
(def cr (parse-definitions contact))
(def cm (parse-definitions metadata))

(def additional-reg
  {"string" :string})

(m/parse
 [:schema {:registry (assoc r "string" :string)}
  "Post"]
 (walk/stringify-keys
  {:index "a"
   :signatures []
   :hash "abc"
   :author "~zod"
   :time-sent 123
   :pending false
   :contents [{:text "Hello"}]}))

[:multi {:dispatch #(get % "response")}
 ["poke" :response/poke]
 ["subscribe" :response/subscribe]
 ["diff" :response/diff]

 ]

{:response/poke [:map
                 ["id" pos-int?]
                 ["response" [:= "poke"]]
                 ]}

{"loggedAction"
 [:orn
  [:add-graph [:map
               [:add-graph
                [:map
                 [:resource [:ref "resource"]]
                 [:graph [:ref "Graph"]]
                 [:mark [:ref "Mark"]]]]]]
  [:add-nodes [:map
               [:add-nodes
                [:map-of
                 [:ref "Index"]
                 [:ref "GraphNode"]]]]]
  [:remove-posts [:map
                  [:remove-posts
                   [:map
                    [:resource [:ref "Resource"]]
                    [:indices [:set [:ref "Index"]]]]]]]
  ]}

(defn go
  [schema]
  (let [schema (m/schema schema)
        decoder (m/decoder schema (mt/transformer
                                   (mt/key-transformer {:decode keyword})))
        parser (m/parser schema)]
    (fn [x]
      (let [x (decoder x)]
        (parser x)))))
