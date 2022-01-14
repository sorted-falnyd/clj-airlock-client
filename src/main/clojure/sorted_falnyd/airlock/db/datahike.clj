(ns sorted-falnyd.airlock.db.datahike
  (:require
   [clojure.edn :as edn]
   [sorted-falnyd.airlock.response :as r]
   [sorted-falnyd.airlock.db.schema :refer [schema]]
   [datahike.api :as d]))

(def cfg {:store {:backend :mem :id "ship" :attribute-refs? true}})

(d/delete-database cfg)
(d/create-database cfg)

(def -conn (d/connect cfg))

(def tx (d/transact -conn {:tx-data schema}))

(def tx2
  (d/transact
   -conn
   {:tx-data
    (mapcat r/handle (edn/read-string (slurp "sample.edn")))}))


(d/q
 '[:find ?r ?title ?c
   :where
   [?e :graph/resource ?r]
   [?e :metadata/title ?title]
   [?p :graph/resource ?r]
   [?p :post/contents ?c]]
 (d/db -conn))
