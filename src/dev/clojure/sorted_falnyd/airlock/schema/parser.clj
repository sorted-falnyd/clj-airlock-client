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
                        [:empty :nil])
                      #_[(keyword k) v])))
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
(defmulti parse-metadata-update first)

(defmethod parse-response :poke/ack [[_ v]] v)
(defmethod parse-response :poke/nack [[_ v]] v)
(defmethod parse-response :watch/ack [[_ v]] v)
(defmethod parse-response :watch/nack [[_ v]] v)
(defmethod parse-response :diff [[_ v]] (parse-diff (:json v)))

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

(defmethod parse-diff :metadata-update [[_ {:keys [metadata-update]}]]
  (parse-metadata-update metadata-update))

(defmulti parse-graph-update first)

(defmethod parse-diff :graph-update [[_ {:keys [graph-update]}]]
  (parse-graph-update graph-update))

(defmulti parse-graph-contents first)

(defmethod parse-graph-contents :Graph.TextContent [[_ {:keys [text]}]]
  {:graph.post/content text
   :graph.post.content/type :graph.post.content.type/text})

(defmethod parse-graph-contents :Graph.UrlContent [[_ {:keys [url]}]]
  {:graph.post/content url
   :graph.post.content/type :graph.post.content.type/url})


(defmethod parse-graph-contents :Graph.CodeContent [[_ {:keys [code]}]]
  (cond->
      {:graph.post/content (:expression code)
       :graph.post.content/type :graph.post.content.type/code}
    (:output code) (assoc :graph.post.code/output (:output code))))

(defmethod parse-graph-contents :Graph.MentionContent [[_ {:keys [mention emphasis]}]]
  (cond->
      {:graph.post/content mention
       :graph.post.content/type :graph.post.content.type/mention}
    emphasis (assoc :graph.post.mention/emphasis emphasis)))

(defmethod parse-graph-contents :Graph.ReferenceContent [[_ {:keys [app graph group]}]]
  (or
   (and app
        {:graph.post.content/reference app
         :graph.post.content/type :graph.post.content.type/app-reference})
   (and graph
        {:graph.post.content/reference graph
         :graph.post.content/type :graph.post.content.type/graph-reference})
   (and group
        {:graph.post.content/reference group
         :graph.post.content/type :graph.post.content.type/group-reference})
   ))

(defn parse-graph-node
  [{children :children
    {:keys [index signatures hash author time-sent contents]} :post}]
  {:graph.post/children children
   :graph.post/index index
   :graph.post/signatures (mapv (fn [{:keys [signature ship life]}]
                                  {:graph.post.signature/signature signature
                                   :graph.post.signature/ship ship
                                   :graph.post.signature/life life})
                                signatures)
   :graph.post/contents (mapv parse-graph-contents contents)
   :graph.post/hash hash
   :graph.post/author author
   :graph.post/time-sent time-sent
   })

(defmethod parse-graph-update :add-nodes [[_ {:keys [add-nodes]}]]
  ())

(defmulti parse-md-config first)

(defmethod parse-md-config :Metadata.GroupConfig [[_ {:keys [group] :as m}]]
  (if (= :empty (first group))
    nil
    (let [{:keys [resource app-name]} (second group)]
      {:metadata.config.group/resource resource
       :metadata.config.group/app-name (keyword "metadata.config.group.app-name" app-name)})))

(defmethod parse-md-config :Metadata.GraphConfig
  [[_ {:keys [graph]}]]
  {:metadata.config/graph graph})

(defn parse-association
  [{resource :resource
    app-name :app-name
    group :group
    {:keys [description date-created creator color config title preview hidden picture vip]} :metadata}]
  (merge
   {:graph/group group
    :graph/app-name app-name
    :graph/resource resource
    :metadata/description description,
    :metadata/date-created date-created
    :metadata/creator creator
    :metadata/color color
    :metadata/title title,
    :metadata/preview preview,
    :metadata/hidden hidden,
    :metadata/picture picture,
    :metadata/vip vip}
   (parse-md-config config)))

(defmethod parse-metadata-update :Metadata.MetadataUpdateInitial
  [[_ {:keys [associations]}]]
  (reduce-kv
   (fn [m k v]
     (assoc m k (parse-association v)))
   {}
   associations))

(defmethod parse-metadata-update :Metadata.MetadataUpdateAdd [_ {}])
(defmethod parse-metadata-update :Metadata.MetadataUpdateUpdate [_ {}])
(defmethod parse-metadata-update :Metadata.MetadataUpdateRemove [_ {}])
(defmethod parse-metadata-update :Metadata.MetadataUpdateEdit [_ {}])

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

(mapv parse-response (mapv f sample))
