(ns sorted-falnyd.airlock.client.graph.api
  (:require
   [sorted-falnyd.airlock.schema.parser.urbit :refer [unparse-resource]]
   [sorted-falnyd.airlock.client.api :as api]
   [sorted-falnyd.airlock.graph :as graph]))

(def ^:dynamic *author* nil)
(def ^:dynamic *resource* nil)

(defmacro with-author
  [name body]
  `(binding [*author* ~name]
     ~@body))

(defmacro with-resource
  [resource body]
  `(binding [*resource* ~resource]
     ~@body))

(defn- prepare-opts
  [opts]
  (merge
   {:post/author *author*}
   (unparse-resource *resource*)
   opts))

(defn chat!
  {:argslists
   '([conn {:keys [post/author resource/name resource/ship post/content]}])}
  "Post to a chat with connection.
  Author and resource can be dynamically bound, but will take precedence if present in `opts`."
  [conn opts]
  (let [{:keys [post/author resource/name resource/ship post/content]}
        (prepare-opts opts)]
    (->> content
         (graph/make-post author)
         (graph/add-post ship name)
         (api/send! conn))))

(defn post!
  {:arglists
   '([conn {:keys [post/author resource/name resource/ship post/title post/body] :as opts}])}
  "Post to a notebook with connection.
  Author and resource can be dynamically bound, but will take precedence if present in `opts`."
  [conn opts]
  (let [{:keys [post/author resource/name resource/ship post/title post/body]}
        (prepare-opts opts)]
    (->> body
         (graph/new-post author title)
         (graph/add-nodes ship name)
         (api/send! conn))))
