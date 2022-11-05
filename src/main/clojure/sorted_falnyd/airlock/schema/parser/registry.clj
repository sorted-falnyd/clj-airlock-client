(ns sorted-falnyd.airlock.schema.parser.registry
  (:require
   [sorted-falnyd.airlock.schema.parser.json-schema :as json-schema]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defn remove-entry
  [m e]
  (into
   []
   (remove
    (fn [x]
      (if (vector? x)
        (= e (first x))
        (= e x))))
   m))

(defn graph-registry
  []
  (-> (json-schema/graph)
      (json-schema/parse-definitions "Graph")
      (update "Graph.Post" (fn [s] [:or s :string])) ;;; tombstone?
      (assoc "Graph.Resource" [:map [:name :string] [:ship :string]]
             "Graph.Mark" :string
             "Graph.Index" :string
             "Graph.Graph" [:map-of [:ref "Graph.Index"] [:ref "Graph.GraphNode"]]
             "Graph.GraphUpdate"
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
                               [:ref "Graph.GraphNode"]]]
                             [:resource [:map [:name string?] [:ship string?]]]]]]]
              [:remove-posts [:map
                              [:remove-posts
                               [:map
                                [:resource [:ref "Graph.Resource"]]
                                [:indices [:set [:ref "Graph.Index"]]]]]]]

               [:keys [:map [:keys [:vector [:map [:name :string] [:ship :string]]]]]]])))

(defn contact-registry
  []
  (-> (json-schema/contact)
      (json-schema/parse-definitions "Contact")
      (assoc "Contact.Rolodex" [:map [:rolodex [:map-of [:ref "Contact.Patp"] [:ref "Contact.Contact"]]]])))

(defn metadata-registry []
  (-> (json-schema/metadata)
      (json-schema/parse-definitions "Metadata")
      (assoc "Metadata.ResourceAssociations" [:map-of [:ref "Metadata.Path"] [:ref "Metadata.Association"]])
      (update "Metadata.AssociationPoke" (fn [m]
                                           [:and
                                            (remove-entry m :resource)
                                            [:ref "Metadata.MdResource"]]))))

(defn group-registry []
  (-> (json-schema/group)
      (json-schema/parse-definitions "Group")
      (assoc "Group.Groups" [:map-of :string [:ref "Group.Group"]])
      (assoc "Group.Tags" [:map-of
                           :string
                           [:vector [:ref "Group.PatpNoSig"]]])))

(defn response-registry []
  {"Response"
   [:and
    [:map
     [:id pos-int?]
     [:response string?]
     [:json {:optional true} any?]
     [:mark {:optional true} string?]
     [:ok {:optional true} string?]
     [:err {:optional true} string?]]
    [:multi {:dispatch :response}
     ["poke" "Poke"]
     ["subscribe" "Watch"]
     ["diff" "Diff"]]]
   "Poke" [:orn
           [:poke/ack [:map
                       [:id pos-int?]
                       [:response [:= "poke"]]
                       [:ok string?]]]
           [:poke/nack [:map
                       [:id pos-int?]
                       [:response [:= "poke"]]
                       [:err string?]]]]
   "Watch" [:orn
            [:watch/ack [:map
                         [:id pos-int?]
                         [:response [:= "subscribe"]]
                         [:ok string?]]]
            [:watch/nack [:map
                          [:id pos-int?]
                          [:response [:= "subscribe"]]
                          [:err string?]]]]
   "Diff" [:multi {:dispatch :mark}
           ["contact-update-0" [:map
                                [:id pos-int?]
                                [:response [:= "diff"]]
                                [:json [:map [:contact-update [:ref "Contact.ContactUpdate"]]]]]]
           ["dm-hook-action" [:ref "DmHookAction"]]
           ["graph-update-3" [:map
                              [:id pos-int?]
                              [:response [:= "diff"]]
                              [:json [:map [:graph-update [:ref "Graph.GraphUpdate"]]]]]]
           ["group-update-0" [:map
                              [:id pos-int?]
                              [:response [:= "diff"]]
                              [:json [:map [:groupUpdate [:ref "Group.GroupUpdate"]]]]]]
           #_["group-view-update" []]
           #_["hark-graph-hook-update" []]
           ["hark-update" [:ref "HarkUpdate"]]
           ["metadata-update-2" [:map
                                 [:id pos-int?]
                                 [:response [:= "diff"]]
                                 [:json [:map [:metadata-update [:ref "Metadata.MetadataUpdate"]]]]]]]})

(defn dm-hook-registry []
  {"DmHookAction" (edn/read-string (slurp (io/resource "dm-hook-action.edn")))})

(defn hark-registry []
  {"HarkUpdate" (edn/read-string (slurp (io/resource "hark-update.edn")))})

(defn master-registry
  []
  (merge
   {"empty" [:or :map :nil]
    "string" :string}
   (dm-hook-registry)
   (hark-registry)
   (response-registry)
   (graph-registry)
   (contact-registry)
   (metadata-registry)
   (group-registry)))

