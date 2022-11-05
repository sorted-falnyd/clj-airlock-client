(ns sorted-falnyd.airlock.schema.parser.graph
  (:require
   [sorted-falnyd.airlock.schema.parser.urbit :refer [parse-resource]]))

(defmulti parse-graph-update first)

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

(defmulti parse-graph-reference first)

(defn parse-graph-ref [{:keys [index group graph]}]
  (let [group (parse-resource group)
        graph (parse-resource graph)
        ship (namespace graph) name (name graph)
        id (str ship "/" name index)]
    {:graph/resource [:urbit/resource graph]
     :graph/group [:urbit/resource group]
     :graph.resource/name name
     :graph.resource/ship [:urbit/ship ship]
     :graph.post/id id}))

(defmethod parse-graph-reference :Graph.GraphReference [[_ {:keys [graph]}]]
  {:graph.post.content/reference (parse-graph-ref graph)
   :graph.post.content/type :graph.post.content.type/graph-reference})

(defmethod parse-graph-reference :Graph.AppReference [[_ {:keys [app]}]]
  {:graph.post.content/reference app
   :graph.post.content/type :graph.post.content.type/app-reference})

(defmethod parse-graph-reference :Graph.GroupReference [[_ {:keys [group]}]]
  {:graph.post.content/reference group
   :graph.post.content/type :graph.post.content.type/group-reference})

(defmethod parse-graph-contents :Graph.ReferenceContent
  [[_ o]]
  (parse-graph-reference (:reference o)))

(defn parse-graph-node
  [{children :children
    {:keys [index signatures hash author time-sent contents] :as post} :post
    {:keys [name ship] :as resource} :resource}]
  (cond->
      (if (string? post)
        {:graph.post/tombstone :graph.post/tombstone}
        {:graph.post/index index
         :graph.post/signatures (mapv (fn [{:keys [signature ship life]}]
                                        {:graph.post.signature/signature signature
                                         :graph.post.signature/ship ship
                                         :graph.post.signature/life life})
                                      signatures)
         :graph.post/contents (mapv parse-graph-contents contents)
         :graph.post/author [:urbit/ship author]
         :graph.post/time-sent time-sent})
    children (assoc :graph.post/children children)
    hash (assoc :graph.post/hash hash)
    resource (->
              (cond-> index (assoc :graph.post/id (str ship "/" name index)))
              (assoc :graph/resource [:urbit/resource (keyword ship name)]
                     :graph.resource/name name
                     :graph.resource/ship [:urbit/ship ship]))))

(defmethod parse-graph-update :add-nodes [[_ {:keys [add-nodes]}]]
  {:urbit.airlock/response :urbit.airlock.graph.update/add-nodes
   :nodes
   (let [{:keys [nodes resource]} add-nodes]
     (reduce-kv
      (fn [m _ v]
        (conj m (parse-graph-node (assoc v :resource resource))))
      []
      nodes))})

(defmethod parse-graph-update :add-graph [[_ {:keys [add-graph]}]]
  {:urbit.airlock/response :urbit.airlock.graph.update/add-graph
   :graph
   (let [{:keys [_mark graph resource]} add-graph]
     (reduce-kv (fn [m k v] (assoc m k (parse-graph-node (assoc v :resource resource)))) {} graph))})

(defmethod parse-graph-update :keys [[_ {ks :keys}]]
  {:urbit.airlock/response :urbit.airlock.graph.update/add-keys
   :keys (mapv (fn [{:keys [name ship]}]
                 {:urbit/resource (keyword ship name)
                  :graph.resource/name name
                  :graph.resource/ship [:urbit/ship ship]}) ks)})

(defmethod parse-graph-update :remove-posts [[_ {:keys [remove-posts]}]]
  {:urbit.airlock/response :urbit.airlock.graph.update/remove-posts
   :indices
   (let [{:keys [indices resource]} remove-posts
         {:keys [ship name]} resource]
     (mapv
      (fn [ index]
        {:graph.post/id (str ship "/" name index)
         :graph/resource [:urbit/resource (keyword ship name)]
         :graph.resource/name name
         :graph.resource/ship [:urbit/ship ship]})
      indices))})
