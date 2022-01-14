(ns sorted-falnyd.airlock.response
  (:require
   [meander.epsilon :as m]
   [clojure.spec.alpha :as s]
   [sorted-falnyd.airlock.util :as u]
   [sorted-falnyd.airlock.spec :as as]))

(s/def :response/id ::as/id)
(s/def :response/ok string?)
(s/def :response/err string?)

(defmulti response-spec :response)
(s/def ::response (s/multi-spec response-spec :response))

(s/def :poke/response #{"poke"})

(s/def :response/poke-ack
  (s/or
   :positive (s/keys :req-un [:response/id :poke/response :response/ok])
   :negative (s/keys :req-un [:response/id :poke/response :response/err])))

(defmethod response-spec "poke" [_] :response/poke-ack)

(comment
  (s/valid? ::response {:id 1 :ok "ok" :response "poke"})
  (s/valid? ::response {:id 1 :err "ok" :response "poke"})
  (s/conform ::response {:id 1 :err "ok" :response "poke"}))

(s/def :subscribe/response #{"subscribe"})

(s/def :response/subscribe-ack
  (s/or
   :positive (s/keys :req-un [:response/id :subscribe/response :response/ok])
   :negative (s/keys :req-un [:response/id :subscribe/response :response/err])))

(defmethod response-spec "subscribe" [_] :response/subscribe-ack)

(s/def :diff/json any?)
(s/def :diff/response #{"diff"})

(s/def :response/diff
  (s/keys :req-un [:response/id :diff/response :response/ok]))

(defmethod response-spec "diff" [_] :response/diff)

(s/def :quit/response #{"diff"})

(s/def :response/quit
  (s/keys :req-un [:response/id :quit/response :response/ok]))

(defmethod response-spec "quit" [_] :response/quit)


(defn handle-dm [_data])
(defn handle-invite [_data])
(defn handle-metadata [_data])
(defn handle-metadata-hook [_data])
(defn handle-launch [_data])
(defn handle-location [_data])
(defn handle-group-view [_data])
(defn handle-graph [_data])
(defn handle-group [_data])
(defn handle-contact [_data])
(defn handle-s3 [_data])
(defn handle-hark [_data])
(defn handle-hark-graph [_data])
(defn handle-hark-group [_data])
(defn handle-settings-event [_data])
(defn handle-herm [_data])

(defn handle-graph-keys [_d])
(defn handle-remove-graph [_d])
(defn handle-add-nodes [_d])
(defn handle-add-graph [_d])
(defn handle-remove-posts [_d])


(def graph-handlers
  {:keys handle-graph-keys
   :add-graph handle-add-graph
   :remove-graph handle-remove-graph
   :add-nodes handle-add-nodes
   :remove-posts handle-remove-posts
   })

(def json-handlers
  {:dm-hook-action  handle-dm
   :invite-update  handle-invite
   :metadata-update  handle-metadata
   :metadata-hook-update  handle-metadata-hook
   :launch-update  handle-launch
   :location  handle-location
   :group-view-update  handle-group-view
   :graph-update  handle-graph
   :groupUpdate  handle-group
   :contact-update  handle-contact
   :s3-update  handle-s3
   :harkUpdate  handle-hark
   :hark-graph-hook-update  handle-hark-graph
   :hark-group-hook-update  handle-hark-group
   :settings-event  handle-settings-event
   :hop  handle-herm
   :mor  handle-herm
   :lin  handle-herm})

(defn parse-content
  [m]
  (m/rewrite m
    {"text" (m/some ?text)} [:content/text ?text]

    {"reference"
     {"graph"
      {"index" ?index
       "group" ?group
       "graph" (m/some ?graph)}}}

    [:content/reference
     {:reference/index ?index
      :reference/group (m/app u/decode-resource ?group)
      :reference/graph (m/app u/decode-resource ?graph)}]

    {"reference" {"group" ?group}}
    [:content/reference {:reference/group (m/app u/decode-resource ?group)}]

    {"reference"
     {"app"
      {"ship" ?ship
       "desk" ?desk
       "path" (m/some ?path)}}}
    [:content/reference {:reference/ship ?ship
                         :reference/desk ?desk
                         :reference/path ?path}]))

(comment
  (parse-content
   {"reference"
    {"graph"
     {"index" "/170141184505412613642185257347102801920",
      "group" "/ship/~zod/test-group",
      "graph" "/ship/~zod/my-chat-1686"}}}))

(defn handle-graph-update
  [d]
  (m/rewrite d

    {"add-nodes"
     {"resource" {"name" ?name "ship" ?ship}
      "nodes" {& (m/seqable [!_ !vs] ...)}}}

    [(m/cata
      {:resource/name ?name
       :resource/ship ?ship
       :graph/resource (m/app keyword ?ship ?name)
       & (m/cata !vs)}) ...]

    {"json"
     {"graph-update"
      {"remove-posts"
       {"resource" {"name" ?name "ship" ?ship}
        "indices" [!index ...]}}}}

    [[:db/retract
      [:db/id (m/app str ?ship "/" ?name !index)]] ...]


    {"children" ?children
     "post"
     {"hash" ?hash
      "index" ?index
      "author" ?author
      "time-sent" ?time
      "signatures" [{"signature" !sig "ship" !ship "life" !life} ...]
      "contents" [!content ...]
      }}

    {:post/index ?index
     :post/author ?author
     :post/time-sent ?time
     :post/signatures [{:post.signature/signature !sig
                        :post.signature/ship !ship
                        :post.signature/life !life} ...]
     :post/contents [(m/app parse-content !content) ...]
     & ~[(when ?hash [:post/hash ?hash])
         (when ?children [:node/children ?children])]}

    {:post/index (m/some ?index)
     :resource/name ?name
     :resource/ship ?ship
     & ?more}


    {:post/index ?index
     :resource/name ?name
     :resource/ship ?ship
     :db/id (m/app str ?ship "/" ?name ?index)
     & ?more}


    ))

(comment
  (handle-graph-update
   {"add-nodes"
    {"resource" {"name" "my-chat-1686", "ship" "zod"},
     "nodes"
     {"/170141184505440352665948149754808500224"
      {"children" nil,
       "post"
       {"index" "/170141184505440352665948149754808500224",
        "signatures" [],
        "hash" nil,
        "author" "zod",
        "time-sent" 1641925783073,
        "contents" [{"text" "Hello, mars"}]}}}}},)

  (handle-graph-update
   {"remove-posts"
    {"resource" {"name" "my-chat-1686", "ship" "zod"},
     "indices"
     ["/170141184505440352665948149754808500224"]}},)

  (handle-graph-update
   {"add-nodes"
    {"resource" {"name" "the-notebook-9730", "ship" "zod"},
     "nodes"
     {"/170141184505441981304418377828558438400"
      {"children" nil,
       "post"
       {"index" "/170141184505441981304418377828558438400",
        "signatures"
        [{"signature"
          "0x3c52.f9c2.f405.5bf3.adb7.0ace.f79a.a679.7f00.aecc.4a94.947e.6a6d.0742.095b.92ef.e57e.9a1c.ab56.37b6.9e54.920e.24c9.d9bb.6e37.3a0a.43c1.40d7.af9c.e8ce.3d76.2f90.5c0d.ed74.b8f1.531e.802a.b21a.48c8.d4db.08df.7001",
          "ship" "zod",
          "life" 1}],
        "hash" "0x78a5.f385.e80a.b7e7.5b6e.159d.ef35.4cf2",
        "author" "zod",
        "time-sent" 1642014071739,
        "contents" []}},
      "/170141184505441981304418377828558438400/1"
      {"children" nil,
       "post"
       {"index" "/170141184505441981304418377828558438400/1",
        "signatures"
        [{"signature"
          "0x1.014f.4a46.f2c9.0116.b54c.f620.ba5d.01b8.0200.9b0d.dcf2.ff0a.32b4.de3e.901f.eb16.fee1.171a.8436.e8d9.063d.e1ec.8bf3.c722.7f1c.b711.fd86.f1f1.f150.07cc.149b.e6fc.0ee6.e77f.0f34.cd35.ebcc.c512.8af2.1057.07bf.7001",
          "ship" "zod",
          "life" 1}],
        "hash" "0x80a7.a523.7964.808b.5aa6.7b10.5d2e.80dc",
        "author" "zod",
        "time-sent" 1642014071739,
        "contents" []}},
      "/170141184505441981304418377828558438400/1/1"
      {"children" nil,
       "post"
       {"index" "/170141184505441981304418377828558438400/1/1",
        "signatures"
        [{"signature"
          "0x19b5.0252.814a.107e.60a1.c97e.fa24.872b.ff80.65ad.2950.b28d.402a.36bf.ef0f.943a.c730.65b1.20d3.abaa.02c2.a4b7.3324.fbfc.e1f1.48c8.1603.df04.40cd.4b88.7f37.e08b.4342.aa12.141e.fdaf.b681.40ce.a3d8.5a32.5a7f.5001",
          "ship" "zod",
          "life" 1}],
        "hash" "0x66d4.094a.0528.41f9.8287.25fb.e892.1caf",
        "author" "zod",
        "time-sent" 1642014071739,
        "contents"
        [{"text" "New Poast"}
         {"text"
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."}]}},
      "/170141184505441981304418377828558438400/2"
      {"children" nil,
       "post"
       {"index" "/170141184505441981304418377828558438400/2",
        "signatures"
        [{"signature"
          "0x1.014f.4a46.f2c9.0116.b54c.f620.ba5d.01b8.0200.9b0d.dcf2.ff0a.32b4.de3e.901f.eb16.fee1.171a.8436.e8d9.063d.e1ec.8bf3.c722.7f1c.b711.fd86.f1f1.f150.07cc.149b.e6fc.0ee6.e77f.0f34.cd35.ebcc.c512.8af2.1057.07bf.7001",
          "ship" "zod",
          "life" 1}],
        "hash" "0x80a7.a523.7964.808b.5aa6.7b10.5d2e.80dc",
        "author" "zod",
        "time-sent" 1642014071739,
        "contents" []}}}}},)

  )

(defn metadata-config
  [d]
  (m/rewrite d

    {"graph" (m/some (m/and ?graph (m/guard (string? ?graph))))}
    {:metadata.config/graph ?graph}

    {"group" (m/and ?m (m/guard (empty? ?m)))}
    {}

    {"group" {"resource" ?resource
              "app-name" (m/and (m/or "groups" "graph")
                                ?app-name)}}
    {:metadata.config.group/resource (m/app u/decode-resource ?resource)
     :metadata.config.group/app-name (m/app keyword "metadata.config.group.app-name" ?app-name)}

    ))

(comment
  (metadata-config {"group" nil})
  (metadata-config {"group" {}})
  (metadata-config {"group" {"resource" "/ship/~zod/chat" "app-name" "graph"}})
  (metadata-config {"graph" "graph"})
  )

(defn -handle-metadata-update
  [msg]
  (m/rewrite msg

    {"associations" {& (m/seqable [!ks !vs] ...)}}

    [{& (m/cata !vs)} ...]

    {"group" (m/app u/decode-resource ?group)
     "app-name" ?app-name
     "resource" (m/app u/decode-resource ?resource)
     "metadata"
     {"description" ?description,
      "date-created" ?date-created
      "creator" ?creator
      "color" ?color
      "config" ?config,
      "title" ?title,
      "preview" ?preview,
      "hidden" ?hidden,
      "picture" ?picture,
      "vip" ?vip}}

    {:graph/group ?group
     :graph/app-name ?app-name
     :graph/resource ?resource
     :metadata/description ?description,
     :metadata/date-created ?date-created
     :metadata/creator ?creator
     :metadata/color ?color
     :metadata/title ?title,
     :metadata/preview ?preview,
     :metadata/hidden ?hidden,
     :metadata/picture ?picture,
     :metadata/vip ?vip
     & (m/app metadata-config ?config)}

    ))

(defn handle-json
  [m]
  (m/rewrite m
    {"graph-update" (m/some ?m)} (m/app handle-graph-update ?m)
    {"metadata-update" (m/some ?m)} (m/app -handle-metadata-update ?m)))

(defn handle
  [m]
  (m/rewrite m
    {"json" ?m} (m/app handle-json ?m)
    ))
