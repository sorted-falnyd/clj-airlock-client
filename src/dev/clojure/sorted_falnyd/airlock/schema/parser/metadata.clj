(ns sorted-falnyd.airlock.schema.parser.metadata
  (:require [clojure.string :as str]))

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

(defn- parse-resource
  [r]
  (cond
    (string? r) (apply keyword (str/split (str/replace-first r "/ship/~" "") #"/"))
    (keyword? r) r
    :else r))

(defn parse-association
  [{resource :resource
    app-name :app-name
    group :group
    {:keys [description date-created creator color config title preview hidden picture vip]} :metadata}]
  (merge
   {:graph/group group
    :graph/app-name app-name
    :graph/resource (parse-resource resource)
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

(defmethod parse-metadata-update :Metadata.MetadataUpdateAdd [[_ {:keys [add]}]]
  (parse-association add))

(defmethod parse-metadata-update :Metadata.MetadataUpdateUpdate [[_ {}]])
(defmethod parse-metadata-update :Metadata.MetadataUpdateRemove [[_ {}]])
(defmethod parse-metadata-update :Metadata.MetadataUpdateEdit [[_ {}]])
