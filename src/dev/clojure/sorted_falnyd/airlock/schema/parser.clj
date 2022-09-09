(ns sorted-falnyd.airlock.schema.parser
  (:require
   [sorted-falnyd.airlock.schema.parser.metadata :as md]
   [sorted-falnyd.airlock.schema.parser.graph :as g]
   [sorted-falnyd.airlock.schema.parser.group :as group]
   [sorted-falnyd.airlock.schema.parser.hark :as hark]
   [sorted-falnyd.airlock.schema.parser.dm-hook :as dm]
   [sorted-falnyd.airlock.schema.parser.contact :as contact]
   [sorted-falnyd.airlock.schema.parser.registry :as reg]
   [malli.core :as m]
   [malli.transform :as mt]))

(defmulti parse-response first)
(defmulti parse-diff first)

(defmethod parse-response "poke" [[_ [t v]]]
  (assoc v
         :urbit.airlock/response
         (if (identical? t :poke/ack)
           :urbit.airlock.poke/ack
           :urbit.airlock.poke/nack)))

(comment
  (parse-response ["poke" [:poke/ack {:id 1, :response "poke", :ok "ok"}]]))


(defmethod parse-response "subscribe" [[_ [t v]]]
  (assoc v
         :urbit.airlock/response
         (if (identical? t :watch/ack)
           :urbit.airlock.subscribe/ack
           :urbit.airlock.subscribe/nack)))

(comment
  (parse-response
   ["subscribe" [:watch/ack {:id 3, :response "subscribe", :ok "ok"}]]))

(defmethod parse-response "diff" [[_ v]] (parse-diff v))

(defmethod parse-diff "metadata-update-2" [[_ {:keys [json]}]]
  (md/parse-metadata-update (:metadata-update json)))

(defmethod parse-diff "graph-update-3" [[_ {:keys [json]}]]
  (g/parse-graph-update (:graph-update json)))

(defmethod parse-diff "group-update-0" [[_ {:keys [json]}]]
  (group/parse-group-update (:groupUpdate json)))

(defmethod parse-diff "hark-update" [[_ {:keys [json]}]]
  {:urbit.airlock/response :urbit.airlock.hark.update/hark
   :harks
   (mapv hark/parse-hark-update (:more json))})

(defmethod parse-diff "dm-hook-action" [[_ {:keys [json]}]]
  (merge
   {:urbit.airlock/response :urbit.airlock.dm.hook/action}
   (dm/parse-dm-hook (:dm-hook-action json))))

(defmethod parse-diff "contact-update-0" [[_ {:keys [json]}]]
  (contact/parse-contact-update (:contact-update json)))

(defn decoder
  ([] (decoder [:schema {:registry (reg/master-registry)} "Response"]))
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
   ((decoder schema) value)))
