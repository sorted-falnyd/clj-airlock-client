(ns datascript
  (:require
   [jsonista.core :as json]
   [sorted-falnyd.airlock.action :as action]
   [sorted-falnyd.airlock.graph :as graph]
   [sorted-falnyd.airlock.client.api :as api]
   [clojure.core.async :as a]
   [sorted-falnyd.airlock.schema.parser :as parser]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [datascript.core :as d]))

(comment
  ;;; either
  (def client (api/client {:port 80}))
  ;;; or
  (require '[home :refer [client]]))

(comment
  (api/send! client (action/delete))
  (.subscriber (:sse-connection client)))


(def decoder (parser/decoder))
(def recording (atom []))
(def channel1 (a/chan 4096))
(a/pipeline
 1
 channel1
 (map (fn [x]
        (fipp x)
        (swap! recording conj x)
        x))
 (:channel client))

(defmethod parser/parse-response :default [o]
  {:urbit.airlock/response :urbit.airlock/unrecognized
   :response o})

(defn decode+parse [x] (-> x decoder parser/parse-response))

(mapv decode+parse (deref recording))

(def meta-resp
  (api/send! client (action/subscribe "metadata-store" "/all")))
(def keys-resp
  (api/send! client (action/subscribe "graph-store" "/keys")))
(def contact-resp
  (api/send! client (action/subscribe "contact-store" "/all")))
(def graph-all-resp
  (api/send! client (action/subscribe "graph-store" "/updates")))

(defmulti handle-response (fn [_conn m] (:urbit.airlock/response m)))

(methods handle-response)

(defn -entity-ref?
  [%]
  (and (vector? %)
       (= 2 (count %))
       (keyword? (first %))))

(let [xf (comp
          (mapcat vals)
          (filter -entity-ref?)
          (distinct)
          (map (fn [[k v]] {k v})))]
  (defn -collect-refs [xs] (into [] xf xs)))

(defn -facts-and-refs
  [xs]
  (concat (-collect-refs xs) xs))

(defmethod handle-response :urbit.airlock.graph.update/add-keys
  [conn m]
  (d/transact! conn (-facts-and-refs (:keys m))))

(defmethod handle-response :urbit.airlock.metadata.update/initial,
  [conn m]
  (d/transact! conn (-facts-and-refs (:metadata/associations m))))

(defmethod handle-response :urbit.airlock.graph.update/add-nodes
  [conn m]
  (d/transact! conn (-facts-and-refs (:nodes m))))

(defmethod handle-response :urbit.airlock.graph.update/remove-posts
  [conn {:keys [indices]}]
  (->> indices
       (mapv (fn [{:graph.post/keys [id]}] [:db/retract [:graph.post/id id]]))
       (d/transact! conn)))

(def schema {
             :graph.post/id {:db/unique :db.unique/identity :db/index true}
             :graph.post/signatures {:db/cardinality :db.cardinality/many}
             :graph.resource/ship {:db/valueType :db.type/ref :db/index true},
             :graph.post/author {:db/valueType :db.type/ref :db/index true}
             :graph/group {:db/valueType :db.type/ref :db/index true}
             :graph/resource {:db/valueType :db.type/ref :db/index true},

             :group.policy/ban-ranks {:db/cardinality :db.cardinality/many},
             :group.policy/banned {:db/cardinality :db.cardinality/many}
             :group/members {:db/cardinality :db.cardinality/many}

             :metadata/creator {:db/valueType :db.type/ref :db/index true},

             :urbit/resource {:db/unique :db.unique/identity :db/index true}
             :urbit/ship {:db/unique :db.unique/identity :db/index true}
             })

(def conn (d/create-conn schema))

;;; Look at data
(->> recording
     deref
     (mapv decode+parse))

;;; Ingest data
(doseq [resp
        (->> recording
             deref
             (mapv decode+parse)
             (filter (comp (methods handle-response) :urbit.airlock/response)))]
  (handle-response conn resp))


;;; All resources
(d/datoms (d/db conn) :avet :urbit/resource)

;;; PULL
(d/pull-many (d/db conn) '[*] (mapv :e (d/datoms (d/db conn) :avet :urbit/resource)))

;;; Pull all resources in another way

(d/q '[:find (pull ?rid [*])
       :where
       [?rid :urbit/resource]]
     (d/db conn))


(d/q '[:find (pull ?r [:metadata/title {:graph/group [:metadata/title]} :urbit/resource])
       :where
       [?r :urbit/resource]]
     (d/db conn))

(d/q '[:find (pull ?r [*])
       :where
       [?r :urbit/resource]]
     (d/db conn))

(d/q '[:find ?group ?title
       :where
       [?e :metadata/creator [:urbit/ship "zod"]]
       [?e :metadata/title ?title]
       [?e :graph/group ?g]
       [?g :metadata/title ?group]]
     (d/db conn))


(let [db (d/db conn)]
  (d/entid db [:urbit/resource :dopzod/urbit-help]))

;;; All entities referring to resource
(let [db (d/db conn)]
  (d/pull db '[{:graph/_resource [*]}] (d/entid db [:urbit/resource :dopzod/urbit-help])))
