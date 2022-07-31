(ns sorted-falnyd.airlock.schema.parser
  (:require
   [jsonista.core :as json]
   [malli.core :as m]
   [malli.transform :as mt]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [clojure.edn :as edn])
  (:import
   (java.net URLDecoder)))

(defonce graph (json/read-value (io/resource "./graph-update2.json")))
(defonce contact (json/read-value (io/resource "./contacts-update2.json")))
(defonce metadata (json/read-value (io/resource "./metadata-update2.json")))

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
    (into
     [:map]
     (map (fn [[k v]]
            [(keyword k)
             {:optional (not (contains? required k))}
             (json-schema-type v)]))
     properties)))

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
      (if (and (= (count ts) 2) (some #{:nil} ts))
        [:maybe (first (remove #{:nil} ts))]
        (into [:orn]
              (comp
               (map json-schema-type)
               (map (fn [[_ k :as v]]
                      (if k
                        [(keyword k) v]
                        [:empty :nil]))))
              any-of)))))

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
        (assoc m k (json-schema-type v))))
    {})))

(def gr
  (-> graph
      (parse-definitions "Graph")
      (assoc "Graph.Resource" :string
             "Graph.Mark" :string
             "Graph.Index" :string
             "Graph.Graph" [:map-of [:ref "Graph.Index"] [:ref "Graph.GraphNode"]])))

(def cr (parse-definitions contact "Contact"))
(def mr
  (-> metadata
      (parse-definitions "Metadata")
      (assoc "Metadata.ResourceAssociations" [:map-of [:ref "Metadata.Path"] [:ref "Metadata.Association"]])))

(def additional-reg
  {"string" :string})

(def responses
  {"Response"
   [:orn
    [:poke/ack [:map
                [:id pos-int?]
                [:response [:= "poke"]]
                [:ok string?]]]
    [:poke/nack [:map
                 [:id pos-int?]
                 [:response [:= "poke"]]
                 [:err string?]]]
    [:watch/ack [:map
                 [:id pos-int?]
                 [:response [:= "subscribe"]]
                 [:ok string?]]]
    [:watch/nack [:map
                  [:id pos-int?]
                  [:response [:= "subscribe"]]
                  [:err string?]]]
    [:diff [:map
            [:id pos-int?]
            [:response [:= "diff"]]
            [:json [:ref "Json"]]]]]})

(defmulti parse-response first)
(defmulti parse-diff first)

(defmethod parse-response :poke/ack [[_ v]] v)
(defmethod parse-response :poke/nack [[_ v]] v)
(defmethod parse-response :watch/ack [[_ v]] v)
(defmethod parse-response :watch/nack [[_ v]] v)
(defmethod parse-response :diff [[_ v]] (parse-diff v))

(def actions
  {"Graph.GraphUpdate"
   [:orn
    [:add-graph [:map
                 [:add-graph
                  [:map
                   [:resource [:ref "Graph.Resource"]]
                   [:graph [:ref "Graph.Graph"]]
                   [:mark [:ref "Graph.Mark"]]]]]]
    [:add-nodes [:map
                 [:add-nodes
                  [:map
                   [:nodes
                    [:map-of
                     [:ref "Graph.Index"]
                     [:ref "Graph.GraphNode"]]]]]]]
    [:remove-posts [:map
                    [:remove-posts
                     [:map
                      [:resource [:ref "Graph.Resource"]]
                      [:indices [:set [:ref "Graph.Index"]]]]]]]]
   "Json"
   [:orn
    [:graph-update [:map [:graph-update [:ref "Graph.GraphUpdate"]]]]
    [:metadata-update [:map [:metadata-update [:ref "Metadata.MetadataUpdate"]]]]
    [:contact-update [:map [:contact-update [:ref "Contact.ContactUpdate"]]]]]})

#_(defmethod parse-diff :metadata-update [[_ {:keys [metadata-update]}]]
  (parse-metadata-update metadata-update))

(def master-reg
  (merge
   responses
   actions
   additional-reg
   gr
   cr
   mr))

(defn go
  [schema]
  (let [schema (m/schema schema)
        decoder (m/decoder schema (mt/transformer
                                   (mt/key-transformer {:decode keyword})))
        parser (m/parser schema)
        explainer (m/explainer schema)]
    (fn [x]
      (let [x (decoder x)
            ret (parser x)]
        (if (identical? :malli.core/invalid ret)
          (explainer x)
          ret)))))

(defonce sample
  (edn/read-string (slurp "./sample.edn")))

(m/schema [:schema {:registry master-reg} "Response"])

(def f (go [:schema {:registry master-reg} "Response"]))

(mapv f sample)



