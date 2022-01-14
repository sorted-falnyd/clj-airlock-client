(ns sorted-falnyd.airlock.db.schema)

(def schema
  [{:db/ident :graph/app-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :graph/group
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}
   {:db/ident :graph/resource
    :db/valueType :db.type/keyword
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident :resource/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :resource/ship
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :metadata/hidden
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/picture
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/vip
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/preview
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/creator
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/date-created
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata.config/graph
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/color
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :metadata/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :node/children
    :db/valueType :db.type/ref ;; TODO
    :db/cardinality :db.cardinality/many}
   {:db/ident :post/hash
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :post/index
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :post/author
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :post/time-sent
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :post/signatures
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}
   {:db/ident :post.signature/signature
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :post.signature/ship
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :post.signature/life
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :post/contents
    :db/valueType :db.type/tuple
    :db/cardinality :db.cardinality/many
    :db/tupleTypes [:db.type/keyword :db.type/string]} ;; TODO
   ]
  )
