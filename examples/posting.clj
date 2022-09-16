(ns posting
  (:require
   [sorted-falnyd.airlock.client.api :as api]
   [sorted-falnyd.airlock.client.graph.api :as graph.api]))

(def conn
  (api/client
   {:port 80
    ;; :ship-name "sampel-planet"
    ;; :code "something-important"
    ;; :uri "http://sampel-planet.arvo.network"
    }))

(def r
  (graph.api/chat!
   conn
   {:post/author "zod"
    :post/content [{"text" "Hello, mars"}]
    :resource/name "channel-1-6870"
    :resource/ship "zod"}))

(def r
  (graph.api/post!
   conn
   {:post/author "zod"
    :post/title "Hello world!"
    :post/body [{"text" "Hello, mars"}]
    :resource/name "the-notebook-1437"
    :resource/ship "zod"}))

;;; Author taken from connection

(def r
  (graph.api/chat!
   conn
   {:post/content [{"text" "Hello, mars"}]
    :resource/name "channel-1-6870"
    :resource/ship "zod"}))

(def r
  (graph.api/post!
   conn
   {:post/title "Hello world!"
    :post/body [{"text" "Hello, mars"}]
    :resource/name "the-notebook-1437"
    :resource/ship "zod"}))


;;; Dynamic resource

(graph.api/with-resource {:resource/name "channel-1-6870"
                          :resource/ship "zod"}
  (graph.api/chat!
   conn
   {:post/content [{"text" "Hello, Mars!"}]}))


(graph.api/with-resource #:resource{:name "coll-9061" :ship "zod"}
  (graph.api/post-link!
   conn
   #:post{:title "Instaparse" :url "https://clojars.org/instaparse"}))
