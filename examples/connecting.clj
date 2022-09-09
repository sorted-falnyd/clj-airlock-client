(ns connecting
  (:require
   [sorted-falnyd.airlock.action :as action]
   [sorted-falnyd.airlock.graph :as graph]
   [sorted-falnyd.airlock.client.api :as api]
   [clojure.core.async :as a]))

(def conn
  (api/client
   {:port 80
    ;; :ship-name "sampel-planet"
    ;; :code "something-important"
    ;; :uri "http://sampel-planet.arvo.network"
    }))

(def r (api/send! conn (action/poke "hood" "helm-hi" "Knock knock")))
(.get r)
(def r (api/send! conn (action/subscribe "contact-pull-hook" "/nacks")))
(def r (api/send! conn (action/subscribe "contact-store" "/all")))
(def r (api/send! conn (action/subscribe "dm-hook" "/updates")))
(def r (api/send! conn (action/subscribe "graph-store" "/keys")))
(def r (api/send! conn (action/subscribe "graph-store" "/updates")))
(def r (api/send! conn (action/subscribe "group-store" "/groups")))
(def r (api/send! conn (action/subscribe "group-view" "/all")))
(def r (api/send! conn (action/subscribe "hark-graph-hook" "/updates")))
(def r (api/send! conn (action/subscribe "hark-store" "/updates")))
(def r (api/send! conn (action/subscribe "metadata-store" "/all")))
(def r (api/send! conn (action/subscribe "chat-view" "/primary")))

(def q (into [] (take-while identity) (repeatedly #(a/poll! (:channel conn)))))
(def q2 (into [] (take-while identity) (repeatedly #(a/poll! (:channel conn)))))

(spit "samples.edn" q)

(map #(get % "json")(filter (fn [x] (= "diff" (get x "response"))) q))

(def r
  (->> [{"text" "Hello, mars"}]
       (graph/make-post "zod")
       (graph/add-post "zod" "my-chat-1686")
       (api/send! conn)))
(.get r)
