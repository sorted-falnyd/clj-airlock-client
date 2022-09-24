(ns sorted-falnyd.airlock.schema.parser.metadata
  (:require
   [sorted-falnyd.airlock.schema.parser.urbit
    :refer
    [parse-resource
     parse-ship]]))

(defmulti parse-metadata-update first)

(defmulti parse-md-config first)

(defmethod parse-md-config :Metadata.GroupConfig [[_ {:keys [group] :as _m}]]
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
   {:graph/app-name app-name
    :urbit/resource (parse-resource resource)
    :metadata/description description,
    :metadata/date-created date-created
    :metadata/creator [:urbit/ship (parse-ship creator)]
    :metadata/color color
    :metadata/title title,
    :metadata/preview preview,
    :metadata/hidden hidden,
    :metadata/picture picture,
    :metadata/vip vip}
   (when group
     {:graph/group [:urbit/resource (parse-resource group)]})
   (parse-md-config config)))

(defmethod parse-metadata-update :Metadata.MetadataUpdateInitial
  [[_ {:keys [associations]}]]
  {:urbit.airlock/response :urbit.airlock.metadata.update/initial
   :metadata/associations
   (reduce-kv
    (fn [m _ v]
      (conj m (parse-association v)))
    []
    associations)})

(defmethod parse-metadata-update :Metadata.MetadataUpdateAdd [[_ {:keys [add]}]]
  (assoc
   (parse-association add)
   :urbit.airlock/response :urbit.airlock.metadata.update/add))

(defmethod parse-metadata-update :Metadata.MetadataUpdateUpdate [[_ {u :update}]]
  (assoc
   (parse-association (parse-association u))
   :urbit.airlock/response :urbit.airlock.metadata.update/update))

(defmethod parse-metadata-update :Metadata.MetadataUpdateRemove [[_ {{:keys [resource group]} :remove}]]
  (cond->
      {:urbit.airlock/response :urbit.airlock.metadata.update/remove
       :urbit/resource (parse-resource resource)}
    group (assoc :graph/group [:urbit/resource (parse-resource group)])))

(defmethod parse-metadata-update :Metadata.MetadataUpdateEdit [[_ {{:keys [resource group edit]} :edit}]]
  (-> edit
      (update-keys #(keyword "metadata" (name %)))
      (assoc
       :urbit.airlock/response :urbit.airlock.metadata.update/edit
       :urbit/resource (parse-resource resource))
      (cond->
          group (assoc :graph/group [:urbit/resource (parse-resource group)]))))
