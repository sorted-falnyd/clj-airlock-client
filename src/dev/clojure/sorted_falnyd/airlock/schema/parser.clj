(ns sorted-falnyd.airlock.schema.parser
  (:require
   [sorted-falnyd.airlock.schema.parser.metadata :as md]
   [sorted-falnyd.airlock.schema.parser.graph :as g]
   [sorted-falnyd.airlock.schema.parser.group :as group]
   [sorted-falnyd.airlock.schema.parser.hark :as hark]
   [sorted-falnyd.airlock.schema.parser.dm-hook :as dm]
   [malli.core :as m]
   [malli.transform :as mt]
   [clojure.walk :as walk]
   [clojure.edn :as edn]))

(defmulti parse-response first)
(defmulti parse-diff first)

(defmethod parse-response :poke/ack [[_ v]] v)
(defmethod parse-response :poke/nack [[_ v]] v)
(defmethod parse-response :watch/ack [[_ v]] v)
(defmethod parse-response :watch/nack [[_ v]] v)
(defmethod parse-response "diff" [[_ v]] (parse-diff v))

(defmethod parse-diff "metadata-update-2" [[_ {:keys [json]}]]
  (md/parse-metadata-update (:metadata-update json)))

(defmethod parse-diff "graph-update-3" [[_ {:keys [json]}]]
  (g/parse-graph-update (:graph-update json)))

(defmethod parse-diff "group-update-0" [[_ {:keys [json]}]]
  (group/parse-group-update (:groupUpdate json)))

(defmethod parse-diff "hark-update" [[_ {:keys [json]}]]
  (mapv hark/parse-hark-update (:more json)))

(defmethod parse-diff "dm-hook-action" [[_ {:keys [json]}]]
  (dm/parse-dm-hook (:dm-hook-action json)))

(defn go
  ([schema]
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
  ([schema value]
   ((go schema) value)))

(defn provide-groups
  [p g]
  (reduce-kv (fn [m k v] (assoc m k (p v))) {} g))

(comment
  (require 'malli.provider)

  (def provider (malli.provider/provider))

  (def updates (edn/read-string (slurp "updates.edn")))
  (def updates2 (edn/read-string (slurp "updates2.edn")))
  (require '[sorted-falnyd.airlock.schema.parser.registry :as reg])
  (def reg (reg/master-registry))
  (def f (go [:schema {:registry reg} "Response"]))

  (remove :schema (mapv f updates2))
  (go [:schema {:registry reg} "Group.GroupUpdateInitial"]
      {"initial"
       {"/ship/~zod/group-1"
        {"members" ["zod" "bus"],
         "tags" {"admin" ["zod"]},
         "policy" {"open" {"banRanks" [], "banned" []}},
         "hidden" false}}}, ,)


  (provide-groups
   provider
   (group-by :mark (keep (comp walk/keywordize-keys :value f) updates)))

  (parse-response
   ["diff"
    ["dm-hook-action"
     {:json {:dm-hook-action {:pendings ["bus"]}},
      :id 13,
      :response "diff",
      :mark "dm-hook-action"}]])

  (let [d (remove :value  (map f updates))]
    (map parse-response d))

  )

