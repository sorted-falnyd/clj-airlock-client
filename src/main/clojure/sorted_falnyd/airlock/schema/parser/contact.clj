(ns sorted-falnyd.airlock.schema.parser.contact
  (:require
   [sorted-falnyd.airlock.schema.parser.urbit :refer [parse-resource parse-ship]]))

(defmulti parse-contact-update first)

(defn parse-contact
  [{:keys [cover bio nickname avatar last-updated status color groups]}]
  #:contact{:cover cover
            :bio bio
            :nickname nickname
            :avatar avatar
            :last-updated last-updated
            :status status
            :color color
            :groups (mapv parse-resource groups)}
  )

(defn parse-rolodex
  [dex]
  (reduce-kv
   (fn [acc patp contact]
     (conj acc (-> contact
                   parse-contact
                   (assoc :contact/name [:urbit/ship (parse-ship patp)]))))
   []
   dex))

(defmethod parse-contact-update :Contact.ContactUpdateInitial [[_ {:keys [initial]}]]
  {:urbit.airlock/response :urbit.airlock.contact.update/initial
   :contacts (parse-rolodex (:rolodex initial))})

(defmethod parse-contact-update :Contact.ContactUpdateAdd
  [[_ {{:keys [ship contact]} :add}]]
  {:urbit.airlock/response :urbit.airlock.contact.update/add
   :contacts [(-> contact
                  parse-contact
                  (assoc :contact/name [:urbit/ship (parse-ship ship)]))]})

(defmethod parse-contact-update :Contact.ContactUpdateRemove
  [[_ {{:keys [ship]} :remove}]] ;; patp
  {:urbit.airlock/response :urbit.airlock.contact.update/remove
   :ship ship})

(defmethod parse-contact-update :Contact.ContactUpdateEdit
  [[_ {{:keys [ship timestamp edit-field]} :edit}]]
  {:urbit.airlock/response :urbit.airlock.contact.update/edit
   :contacts [(-> edit-field
                  (update-keys #(keyword "contact" (name %)))
                  (assoc :contact/last-updated timestamp)
                  (assoc :contact/name [:urbit/ship (parse-ship ship)]))]})

(defmethod parse-contact-update :Contact.ContactUpdateAllowGroup
  [[_ {{:keys [group]} :allow}]] ;; Resource
  {:urbit.airlock/response :urbit.airlock.contact.update/allow-group
   :group group})

(defmethod parse-contact-update :Contact.ContactUpdateAllowShips
  [[_ {{:keys [ships]} :allow}]] ;; vector of patp
  {:urbit.airlock/response :urbit.airlock.contact.update/allow-ships
   :ships ships})

(defmethod parse-contact-update :Contact.ContactUpdateSetPublic
  [[_ {:keys [set-public]}]] ;; boolean
  {:urbit.airlock/response :urbit.airlock.contact.update/set-public
   :public set-public})
