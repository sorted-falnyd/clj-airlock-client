(ns sorted-falnyd.airlock.client.graph.api
  (:require
   [sorted-falnyd.airlock.client.api :as api]
   [sorted-falnyd.airlock.graph :as graph]))

(defn chat!
  "Post to a chat with connection."
  [conn {:keys [post/author resource/name resource/ship post/content]}]
  (->> content
       (graph/make-post author)
       (graph/add-post ship name)
       (api/send! conn)))

(defn post!
  "Post to a notebook with connection."
  [conn {:keys [post/author resource/name resource/ship post/title post/body]}]
  (->> body
       (graph/new-post author title)
       (graph/add-nodes ship name)
       (api/send! conn)))
