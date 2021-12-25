(ns clj-airlock-api.graph
  (:require
   [clj-airlock-api.util :as u]
   [clj-airlock-api.action :as action]
   [clojure.string :as str]))

(defn make-post
  ([author contents] (make-post author contents nil))
  ([author contents {:keys [parent-index index now]
              :or {parent-index ""}}]
   (let [now (or now (System/currentTimeMillis))
         index (str parent-index "/" (or index (u/unix->da now)))]
     {:author (u/ensig author)
      :index index
      :time-sent now
      :contents contents
      :signatures []
      :hash nil})))

(def graph-update-version 3)
(def graph-update-mark (str "graph-update-" graph-update-version))

(defn store
  [data]
  (action/poke
   "graph-store"
   (str "graph-update-" graph-update-version)
   data))

(defn push-hook
  [data]
  (action/poke "graph-push-hook" "graph-update" data))

(defn view
  [thread-name action]
  (action/thread "graph-view-action" "json" thread-name action))

(defn dm
  [data]
  (action/poke "dm-hook-action" "dm-hook" data))

(defn resource [ship name]
  {:name name :ship ship})

(defn join
  [ship name]
  (let [ship (u/ensig ship)]
    (view
     "graph-join"
     {:join
      {:resource (resource ship name)
       :ship ship}})))

(defn delete
  [ship name]
  (view
   "graph-delete"
   {:delete (resource (u/ensig ship) name)}))

(defn leave
  [ship name]
  (let [ship (u/ensig ship)]
    (view
     "graph-leave"
     {:leave
      {:resource (resource ship name)
       :ship ship}})))

(defn path->resource
  [path]
  (let [[_ _ ship name] (str/split path #"/")]
    (when (and ship name)
      (resource ship name))))

(defn groupify
  ([ship name]
   (groupify ship name nil))
  ([ship name to-path]
   (let [ship (u/ensig ship)
         to (when to-path (path->resource to-path))
         r (resource ship name)
         g (if to
             {:resource r :to to}
             {:resource r})]
     (view "graph-groupify" {:groupify g}))))

(defn add
  [ship name graph mark]
  (store
   {:add-graph
    {:resource (resource (u/ensig ship) name)
     :graph graph
     :mark mark}}))

(defn add-nodes
  [ship name nodes]
  (action/thread
    (str "graph-update-" graph-update-version)
    "graph-view-action"
    "graph-add-nodes"
    {:add-nodes
     {:resource (resource (u/ensig ship) name)
      :nodes nodes}}))

(defn add-post
  [ship name post]
  (let [nodes {(:index post) {:post post :children nil}}]
    (add-nodes ship name nodes)))

(defn add-node
  [ship name node]
  (add-nodes ship name {(-> node :post :index) node}))

(defn create-group-feed
  [group vip]
  (action/thread
    "graph-view-action"
    "resource"
    "graph-create-group-feed"
    {:create-group-feed
     {:resource group
      :vip vip}}))

(defn disable-group-feed
  [group]
  (action/thread
    "graph-view-action"
    "disable"
    "graph-disable-group-feed"
    {:disable-group-feed
     {:resource group}}))

(defn accept-dm [ship] (dm {:accept ship}))
(defn decline-dm [ship] (dm {:decline ship}))

(defn remove-posts
  [ship name indices]
  (push-hook
   {:remove-posts
    {:resource (resource (u/ensig ship) name)}
    :indices indices}))

(let [xf (comp (map u/dec->ud)
            (interpose "/"))]
  (defn encode-index
    [idx]
    (u/into-string xf (str/split idx #"/"))))

(defn -scry [path] (action/scry "graph-store" path))
(defn -keys [] (action/scry "graph-store" "/keys"))
(defn scry [path] (-scry (str "/graph" path)))
(defn -get [ship name] (scry (str "/" (u/ensig ship) "/" name)))

(defn newest
  ([ship name count index]
   (-scry
    (u/-str
     "/graph/"
     (u/ensig ship) "/"
     name
     "/node/siblings/newest/lone/"
     count
     (encode-index index)))))
