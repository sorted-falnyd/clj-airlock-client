(ns sorted-falnyd.airlock.client.graph.api
  (:require
   [sorted-falnyd.airlock.schema.parser.urbit :refer [unparse-resource]]
   [sorted-falnyd.airlock.client.api :as api]
   [sorted-falnyd.airlock.graph :as graph]))

(def ^:dynamic *author* nil)
(def ^:dynamic *resource* nil)

(defmacro with-author
  [name & body]
  `(binding [*author* ~name]
     ~@body))

(defmacro with-resource
  [resource & body]
  `(binding [*resource* ~resource]
     ~@body))

(defn- prepare-opts
  [{:keys [ship-name] :as _conn} opts]
  (merge
   {:post/author (or *author* ship-name)}
   (unparse-resource *resource*)
   opts))

(defn chat!
  "Post to a chat with connection.
  Author and resource can be dynamically bound, but will take precedence if present in `opts`.
  If no author is provided, it will be taken from `:ship-name` in `conn`."
  {:arglists
   '([conn {:keys [post/author resource/name resource/ship post/content]}])}
  [conn opts]
  (let [{:keys [post/author resource/name resource/ship post/content]}
        (prepare-opts conn opts)]
    (->> content
         (graph/make-post author)
         (graph/add-post ship name)
         (api/send! conn))))

(defn post!
  "Post to a notebook with connection.
  Author and resource can be dynamically bound, but will take precedence if present in `opts`.
  If no author is provided, it will be taken from `:ship-name` in `conn`."
  {:arglists
   '([conn {:keys [post/author resource/name resource/ship post/title post/body] :as opts}])}
  [conn opts]
  (let [{:keys [post/author resource/name resource/ship post/title post/body]}
        (prepare-opts conn opts)]
    (->> body
         (graph/new-post author title)
         (graph/add-nodes ship name)
         (api/send! conn))))

(defn remove!
  "Remove posts from resource by indices."
  {:arglists
   '([conn {:keys [post/indices resource/ship resource/name]}])}
  [conn opts]
  (let [{:keys [post/indices resource/ship resource/name]}
        (prepare-opts conn opts)]
    (api/send! (graph/remove-posts ship name indices))))

(defn post-link!
  "Post a link to a collection with optional title."
  {:argslists
   '([conn {:keys [post/author resource/name resource/ship post/title post/url]}])}
  [conn opts]
  (let [{:keys [post/author resource/name resource/ship post/title post/url]}
        (prepare-opts conn opts)]
    (->> [{:text (or title "")} {:url url}]
         (graph/make-post author)
         (graph/add-post ship name)
         (api/send! conn))))

(defn comment!
  "Comment on post with `parent-index`.
  Otherwise similar to [[post!]]"
  {:arglists
   '([conn
      parent-index
      [post/author resource/name resource/ship post/contents]])}
  [conn parent-index opts]
  (let [{:keys [post/author resource/name resource/ship post/contents]}
        (prepare-opts conn opts)]
    (->> contents
         (graph/new-comment author parent-index)
         (graph/add-nodes ship name)
         (api/send! conn))))
