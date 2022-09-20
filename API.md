# Table of contents
-  [`sorted-falnyd.airlock.action`](#sorted-falnydairlockaction)  - Action constructors.
    -  [`ack`](#ack) - Ack <code>event-id</code>.
    -  [`action-spec`](#action-spec)
    -  [`counter`](#counter)
    -  [`delete`](#delete)
    -  [`poke`](#poke)
    -  [`scry`](#scry) - Scry an <code>app</code> on <code>path</code>.
    -  [`subscribe`](#subscribe) - Subscribe to <code>app</code> on <code>path</code>.
    -  [`thread`](#thread)
    -  [`unsubscribe`](#unsubscribe)
-  [`sorted-falnyd.airlock.cache`](#sorted-falnydairlockcache) 
    -  [`->Cache`](#->Cache) - Create a cache with <code>deliver!</code> function which delivers results to a promise and a promise factory <code>pf</code> which creates the promise.
    -  [`-add!`](#-add)
    -  [`-deliver!`](#-deliver)
    -  [`-get!`](#-get)
    -  [`-promise!`](#-promise)
    -  [`-remove!`](#-remove)
    -  [`ICache`](#ICache)
    -  [`IPromiseCache`](#IPromiseCache)
-  [`sorted-falnyd.airlock.client.api`](#sorted-falnydairlockclientapi) 
    -  [`client`](#client) - Construct an API Client.
    -  [`send!`](#send)
-  [`sorted-falnyd.airlock.client.graph.api`](#sorted-falnydairlockclientgraphapi) 
    -  [`*author*`](#author)
    -  [`*resource*`](#resource)
    -  [`chat!`](#chat) - Post to a chat with connection.
    -  [`comment!`](#comment) - Comment on post with <code>parent-index</code>.
    -  [`post!`](#post) - Post to a notebook with connection.
    -  [`post-link!`](#post-link) - Post a link to a collection with optional title.
    -  [`remove!`](#remove) - Remove posts from resource by indices.
    -  [`with-author`](#with-author)
    -  [`with-resource`](#with-resource)
-  [`sorted-falnyd.airlock.connection`](#sorted-falnydairlockconnection) 
    -  [`-channel-name`](#-channel-name)
    -  [`-on-complete`](#-on-complete)
    -  [`-on-error`](#-on-error)
    -  [`-on-next`](#-on-next)
    -  [`-on-subscribe`](#-on-subscribe)
    -  [`build!`](#build)
    -  [`default-callbacks`](#default-callbacks)
    -  [`login-poke`](#login-poke)
    -  [`make-connection`](#make-connection) - Make a connection for a specific channel.
    -  [`prepare-callbacks`](#prepare-callbacks)
    -  [`start!`](#start)
-  [`sorted-falnyd.airlock.db.schema`](#sorted-falnydairlockdbschema) 
    -  [`schema`](#schema)
-  [`sorted-falnyd.airlock.graph`](#sorted-falnydairlockgraph)  - Constructors for Graph Store actions.
    -  [`-get`](#-get-1)
    -  [`-keys`](#-keys)
    -  [`-scry`](#-scry)
    -  [`-siblings`](#-siblings)
    -  [`accept-dm`](#accept-dm)
    -  [`add`](#add)
    -  [`add-node`](#add-node)
    -  [`add-nodes`](#add-nodes)
    -  [`add-post`](#add-post)
    -  [`create-group-feed`](#create-group-feed)
    -  [`decline-dm`](#decline-dm)
    -  [`delete`](#delete-1)
    -  [`disable-group-feed`](#disable-group-feed)
    -  [`dm`](#dm)
    -  [`encode-index`](#encode-index)
    -  [`graph-update-mark`](#graph-update-mark)
    -  [`graph-update-version`](#graph-update-version)
    -  [`groupify`](#groupify)
    -  [`join`](#join)
    -  [`leave`](#leave)
    -  [`make-post`](#make-post) - Create a graph store post.
    -  [`new-comment`](#new-comment)
    -  [`new-post`](#new-post) - Create a notebook post.
    -  [`newest-siblings`](#newest-siblings)
    -  [`node`](#node)
    -  [`older-siblings`](#older-siblings)
    -  [`path->resource`](#path->resource)
    -  [`push-hook`](#push-hook)
    -  [`remove-posts`](#remove-posts)
    -  [`resource`](#resource-1)
    -  [`scry`](#scry-1)
    -  [`store`](#store)
    -  [`subset`](#subset)
    -  [`view`](#view)
    -  [`younger-siblings`](#younger-siblings)
-  [`sorted-falnyd.airlock.http`](#sorted-falnydairlockhttp) 
    -  [`-get-async`](#-get-async)
    -  [`-into-get`](#-into-get)
    -  [`-into-post`](#-into-post)
    -  [`-into-put`](#-into-put)
    -  [`-into-request`](#-into-request)
    -  [`-into-request-format`](#-into-request-format)
    -  [`-post-async`](#-post-async)
    -  [`-put`](#-put)
    -  [`-put-async`](#-put-async)
    -  [`-request`](#-request)
    -  [`-request-async`](#-request-async)
    -  [`-send!`](#-send)
    -  [`IAsyncClient`](#IAsyncClient)
    -  [`IClient`](#IClient)
    -  [`IntoRequest`](#IntoRequest) - Contextual request constructors which ensure relatively correct URIs and cookies are injected.
    -  [`IntoRequestFormat`](#IntoRequestFormat)
    -  [`ensure-body-format`](#ensure-body-format)
    -  [`handle-sse-response!`](#handle-sse-response)
    -  [`send!`](#send-1)
    -  [`send-sse-action!`](#send-sse-action)
-  [`sorted-falnyd.airlock.schema.parser`](#sorted-falnydairlockschemaparser) 
    -  [`decoder`](#decoder)
    -  [`parse-diff`](#parse-diff)
    -  [`parse-response`](#parse-response)
-  [`sorted-falnyd.airlock.schema.parser.contact`](#sorted-falnydairlockschemaparsercontact) 
    -  [`parse-contact`](#parse-contact)
    -  [`parse-contact-update`](#parse-contact-update)
    -  [`parse-rolodex`](#parse-rolodex)
-  [`sorted-falnyd.airlock.schema.parser.dm-hook`](#sorted-falnydairlockschemaparserdm-hook) 
    -  [`parse-dm-hook`](#parse-dm-hook)
-  [`sorted-falnyd.airlock.schema.parser.graph`](#sorted-falnydairlockschemaparsergraph) 
    -  [`parse-graph-contents`](#parse-graph-contents)
    -  [`parse-graph-node`](#parse-graph-node)
    -  [`parse-graph-reference`](#parse-graph-reference)
    -  [`parse-graph-update`](#parse-graph-update)
-  [`sorted-falnyd.airlock.schema.parser.group`](#sorted-falnydairlockschemaparsergroup) 
    -  [`parse-group-update`](#parse-group-update)
    -  [`parse-policy`](#parse-policy)
-  [`sorted-falnyd.airlock.schema.parser.hark`](#sorted-falnydairlockschemaparserhark) 
    -  [`-added`](#-added)
    -  [`-archived`](#-archived)
    -  [`-opened`](#-opened)
    -  [`-read-count`](#-read-count)
    -  [`-saw-place`](#-saw-place)
    -  [`-unread-count`](#-unread-count)
    -  [`collect`](#collect)
    -  [`parse-hark-update`](#parse-hark-update)
-  [`sorted-falnyd.airlock.schema.parser.json-schema`](#sorted-falnydairlockschemaparserjson-schema) 
    -  [`-parse-default`](#-parse-default)
    -  [`contact`](#contact)
    -  [`graph`](#graph)
    -  [`group`](#group)
    -  [`json-schema-type`](#json-schema-type)
    -  [`maybe-zip`](#maybe-zip)
    -  [`metadata`](#metadata)
    -  [`parse-all-of`](#parse-all-of)
    -  [`parse-any-of`](#parse-any-of)
    -  [`parse-array`](#parse-array)
    -  [`parse-definitions`](#parse-definitions)
    -  [`parse-ref`](#parse-ref)
    -  [`parse-tuple`](#parse-tuple)
    -  [`qualify-definitions`](#qualify-definitions)
    -  [`type-parameters`](#type-parameters)
-  [`sorted-falnyd.airlock.schema.parser.metadata`](#sorted-falnydairlockschemaparsermetadata) 
    -  [`parse-association`](#parse-association)
    -  [`parse-md-config`](#parse-md-config)
    -  [`parse-metadata-update`](#parse-metadata-update)
-  [`sorted-falnyd.airlock.schema.parser.registry`](#sorted-falnydairlockschemaparserregistry) 
    -  [`contact-registry`](#contact-registry)
    -  [`dm-hook-registry`](#dm-hook-registry)
    -  [`graph-registry`](#graph-registry)
    -  [`group-registry`](#group-registry)
    -  [`hark-registry`](#hark-registry)
    -  [`master-registry`](#master-registry)
    -  [`metadata-registry`](#metadata-registry)
    -  [`remove-entry`](#remove-entry)
    -  [`response-registry`](#response-registry)
-  [`sorted-falnyd.airlock.schema.parser.urbit`](#sorted-falnydairlockschemaparserurbit) 
    -  [`make-resource`](#make-resource)
    -  [`parse-resource`](#parse-resource)
    -  [`parse-ship`](#parse-ship)
    -  [`unparse-resource`](#unparse-resource)
-  [`sorted-falnyd.airlock.ship`](#sorted-falnydairlockship) 
    -  [`-login-request`](#-login-request) - Build request to obtain a cookie.
    -  [`ensure-port`](#ensure-port)
    -  [`extract-cookie`](#extract-cookie)
    -  [`login!`](#login)
    -  [`make-ship`](#make-ship)
    -  [`zod`](#zod)
-  [`sorted-falnyd.airlock.util`](#sorted-falnydairlockutil) 
    -  [`-str`](#-str)
    -  [`counter`](#counter-1)
    -  [`da->unix`](#da->unix) - Convert Urbit date to Unix Epoch.
    -  [`da-second`](#da-second)
    -  [`da-time`](#da-time)
    -  [`da-unix-epoch`](#da-unix-epoch)
    -  [`dec->ud`](#dec->ud)
    -  [`decode-resource`](#decode-resource)
    -  [`desig`](#desig) - Ensure a ship name does not start with <code>~</code>.
    -  [`ensig`](#ensig) - Ensure a ship name starts with <code>~</code>.
    -  [`into-string`](#into-string)
    -  [`offset`](#offset)
    -  [`remove-nils`](#remove-nils)
    -  [`sbrf`](#sbrf)
    -  [`unix->da`](#unix->da) - Convert Unix Epoch to Urbit date.
    -  [`uri`](#uri)
# sorted-falnyd.airlock.action 


Action constructors.

  Should be sent to a connection via `send!`.



## `ack`
``` clojure

(ack event-id)
```


Ack `event-id`.
  Sent automatically by a client.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L88-L93)</sub>
## `action-spec`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L13-L13)</sub>
## `counter`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L11-L11)</sub>
## `delete`
``` clojure

(delete)
(delete _)
(delete _ & _)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L120-L123)</sub>
## `poke`
``` clojure

(poke app mark json)
(poke ship-name app mark json)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L39-L50)</sub>
## `scry`
``` clojure

(scry app path)
```


Scry an `app` on `path`.
  Useful by more advanced constructors.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L127-L133)</sub>
## `subscribe`
``` clojure

(subscribe app path)
```


Subscribe to `app` on `path`.
  Example:
  (subscribe "graph-store" "/updates")
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L67-L74)</sub>
## `thread`
``` clojure

(thread input-mark output-mark thread-name body)
(thread input-mark output-mark thread-name body desk)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L137-L146)</sub>
## `unsubscribe`
``` clojure

(unsubscribe subscription)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/action.clj#L107-L110)</sub>
# sorted-falnyd.airlock.cache 





## `->Cache`
``` clojure

(->Cache deliver! pf)
```


Create a cache with `deliver!` function which delivers results to a
  promise and a promise factory `pf` which creates the promise.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L12-L29)</sub>
## `-add!`
``` clojure

(-add! this id p)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L3-L6)</sub>
## `-deliver!`
``` clojure

(-deliver! this id v)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L8-L10)</sub>
## `-get!`
``` clojure

(-get! this id)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L3-L6)</sub>
## `-promise!`
``` clojure

(-promise! this id)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L8-L10)</sub>
## `-remove!`
``` clojure

(-remove! this id)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L3-L6)</sub>
## `ICache`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L3-L6)</sub>
## `IPromiseCache`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/cache.clj#L8-L10)</sub>
# sorted-falnyd.airlock.client.api 





## `client`
``` clojure

(client)
(client options)
```


Construct an API Client.

  Returns a map describing the connection which contains a channel on
  which all subscription updates and responses are sent.

  Pass to [[send!]] as a first argument.

  Handle incoming updates by subscribing to the connection `:channel`.

  Available options:

  Ship options:
  - `:port` ship port. int(optional, default 8080).
  - `:uri` ship URI. string(optional, default http://localhost).
  - `:code` HTTP login code. Optional but you probably want to provide a
    real one when connecting to a real ship.

  Connection options:

  - `:client` http client. Optional.
  - `:buf-or-n` buffer or size of the connection channel.
  - `:callbacks` - map of callbacks for handling incoming SSE updates.
     see: [[sorted-falnyd.airlock.connection/default-callbacks]]
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/api.clj#L7-L39)</sub>
## `send!`
``` clojure

(send! client action)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/api.clj#L41-L43)</sub>
# sorted-falnyd.airlock.client.graph.api 





## `*author*`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L7-L7)</sub>
## `*resource*`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L8-L8)</sub>
## `chat!`
``` clojure

(chat! conn opts)
```


Post to a chat with connection.
  Author and resource can be dynamically bound, but will take precedence if present in `opts`.
  If no author is provided, it will be taken from `:ship-name` in `conn`.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L27-L39)</sub>
## `comment!`
``` clojure

(comment! conn parent-index [post/author resource/name resource/ship post/contents])
```


Comment on post with `parent-index`.
  Otherwise similar to [[post!]]
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L76-L89)</sub>
## `post!`
``` clojure

(post! conn {:as opts, :keys [post/author resource/name resource/ship post/title post/body]})
```


Post to a notebook with connection.
  Author and resource can be dynamically bound, but will take precedence if present in `opts`.
  If no author is provided, it will be taken from `:ship-name` in `conn`.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L41-L53)</sub>
## `post-link!`
``` clojure

(post-link! conn opts)
```


Post a link to a collection with optional title.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L64-L74)</sub>
## `remove!`
``` clojure

(remove! conn {:keys [post/indices resource/ship resource/name]})
```


Remove posts from resource by indices.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L55-L62)</sub>
## `with-author`
``` clojure

(with-author name & body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L10-L13)</sub>
## `with-resource`
``` clojure

(with-resource resource & body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/client/graph/api.clj#L15-L18)</sub>
# sorted-falnyd.airlock.connection 





## `-channel-name`
``` clojure

(-channel-name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L53-L53)</sub>
## `-on-complete`
``` clojure

(-on-complete _this state)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L22-L23)</sub>
## `-on-error`
``` clojure

(-on-error _this state t)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L19-L20)</sub>
## `-on-next`
``` clojure

(-on-next this eff)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L25-L31)</sub>
## `-on-subscribe`
``` clojure

(-on-subscribe _this state)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L15-L17)</sub>
## `build!`
``` clojure

(build! {:keys [sse-client channel-uri cookie sse-callbacks], :as this})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L75-L87)</sub>
## `default-callbacks`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L33-L37)</sub>
## `login-poke`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L50-L51)</sub>
## `make-connection`
``` clojure

(make-connection ship)
(make-connection ship {:keys [client channel callbacks buf-or-n], :or {buf-or-n 1024}})
```


Make a connection for a specific channel
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L55-L73)</sub>
## `prepare-callbacks`
``` clojure

(prepare-callbacks this callbacks)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L39-L48)</sub>
## `start!`
``` clojure

(start! {:keys [sse-connection], :as this})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/connection.clj#L89-L93)</sub>
# sorted-falnyd.airlock.db.schema 





## `schema`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/db/schema.clj#L3-L85)</sub>
# sorted-falnyd.airlock.graph 


Constructors for Graph Store actions.



## `-get`
``` clojure

(-get ship name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L187-L187)</sub>
## `-keys`
``` clojure

(-keys)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L185-L185)</sub>
## `-scry`
``` clojure

(-scry path)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L184-L184)</sub>
## `-siblings`
``` clojure

(-siblings who ship name count index)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L189-L200)</sub>
## `accept-dm`
``` clojure

(accept-dm ship)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L168-L168)</sub>
## `add`
``` clojure

(add ship name graph mark)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L125-L131)</sub>
## `add-node`
``` clojure

(add-node ship name node)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L145-L147)</sub>
## `add-nodes`
``` clojure

(add-nodes ship name nodes)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L133-L138)</sub>
## `add-post`
``` clojure

(add-post ship name post)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L140-L143)</sub>
## `create-group-feed`
``` clojure

(create-group-feed group vip)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L149-L157)</sub>
## `decline-dm`
``` clojure

(decline-dm ship)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L169-L169)</sub>
## `delete`
``` clojure

(delete ship name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L92-L96)</sub>
## `disable-group-feed`
``` clojure

(disable-group-feed group)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L159-L166)</sub>
## `dm`
``` clojure

(dm data)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L76-L78)</sub>
## `encode-index`
``` clojure

(encode-index idx)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L180-L182)</sub>
## `graph-update-mark`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L62-L62)</sub>
## `graph-update-version`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L61-L61)</sub>
## `groupify`
``` clojure

(groupify ship name)
(groupify ship name to-path)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L113-L123)</sub>
## `join`
``` clojure

(join ship name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L83-L90)</sub>
## `leave`
``` clojure

(leave ship name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L98-L105)</sub>
## `make-post`
``` clojure

(make-post author contents)
(make-post author contents {:keys [parent-index index now], :or {parent-index ""}})
```


Create a graph store post
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L8-L20)</sub>
## `new-comment`
``` clojure

(new-comment author parent-index contents)
(new-comment author parent-index contents {:keys [now]})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L45-L56)</sub>
## `new-post`
``` clojure

(new-post author title body)
(new-post author title body {:keys [now]})
```


Create a notebook post
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L22-L43)</sub>
## `newest-siblings`
``` clojure

(newest-siblings ship name count)
(newest-siblings ship name count index)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L202-L204)</sub>
## `node`
``` clojure

(node ship name index)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L227-L235)</sub>
## `older-siblings`
``` clojure

(older-siblings ship name count)
(older-siblings ship name count index)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L206-L209)</sub>
## `path->resource`
``` clojure

(path->resource path)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L107-L111)</sub>
## `push-hook`
``` clojure

(push-hook data)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L68-L70)</sub>
## `remove-posts`
``` clojure

(remove-posts ship name indices)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L171-L176)</sub>
## `resource`
``` clojure

(resource ship name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L80-L81)</sub>
## `scry`
``` clojure

(scry path)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L186-L186)</sub>
## `store`
``` clojure

(store data)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L64-L66)</sub>
## `subset`
``` clojure

(subset ship name start end)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L217-L225)</sub>
## `view`
``` clojure

(view thread-name action)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L72-L74)</sub>
## `younger-siblings`
``` clojure

(younger-siblings ship name count)
(younger-siblings ship name count index)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/graph.clj#L211-L215)</sub>
# sorted-falnyd.airlock.http 





## `-get-async`
``` clojure

(-get-async this uri)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L14-L18)</sub>
## `-into-get`
``` clojure

(-into-get this)
(-into-get this uri)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L20-L25)</sub>
## `-into-post`
``` clojure

(-into-post this body)
(-into-post this uri body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L20-L25)</sub>
## `-into-put`
``` clojure

(-into-put this body)
(-into-put this uri body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L20-L25)</sub>
## `-into-request`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L42-L42)</sub>
## `-into-request-format`
``` clojure

(-into-request-format this)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L27-L28)</sub>
## `-post-async`
``` clojure

(-post-async this uri body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L14-L18)</sub>
## `-put`
``` clojure

(-put this uri body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L10-L12)</sub>
## `-put-async`
``` clojure

(-put-async this uri body)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L14-L18)</sub>
## `-request`
``` clojure

(-request this request)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L10-L12)</sub>
## `-request-async`
``` clojure

(-request-async this request)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L14-L18)</sub>
## `-send!`
``` clojure

(-send! this action)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L66-L73)</sub>
## `IAsyncClient`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L14-L18)</sub>
## `IClient`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L10-L12)</sub>
## `IntoRequest`

Contextual request constructors which ensure relatively correct URIs
  and cookies are injected.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L20-L25)</sub>
## `IntoRequestFormat`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L27-L28)</sub>
## `ensure-body-format`
``` clojure

(ensure-body-format {:keys [body], :as request})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L38-L40)</sub>
## `handle-sse-response!`
``` clojure

(handle-sse-response! {:keys [cache], :as client} response)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L88-L92)</sub>
## `send!`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L82-L82)</sub>
## `send-sse-action!`
``` clojure

(send-sse-action! this action)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/http.clj#L75-L80)</sub>
# sorted-falnyd.airlock.schema.parser 





## `decoder`
``` clojure

(decoder)
(decoder schema)
(decoder schema value)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser.clj#L62-L77)</sub>
## `parse-diff`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser.clj#L14-L14)</sub>
## `parse-response`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser.clj#L13-L13)</sub>
# sorted-falnyd.airlock.schema.parser.contact 





## `parse-contact`
``` clojure

(parse-contact {:keys [cover bio nickname avatar last-updated status color groups]})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/contact.clj#L7-L17)</sub>
## `parse-contact-update`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/contact.clj#L5-L5)</sub>
## `parse-rolodex`
``` clojure

(parse-rolodex dex)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/contact.clj#L19-L27)</sub>
# sorted-falnyd.airlock.schema.parser.dm-hook 





## `parse-dm-hook`
``` clojure

(parse-dm-hook {:keys [pendings accept]})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/dm_hook.clj#L3-L6)</sub>
# sorted-falnyd.airlock.schema.parser.graph 





## `parse-graph-contents`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/graph.clj#L5-L5)</sub>
## `parse-graph-node`
``` clojure

(parse-graph-node
 {children :children,
  {:keys [index signatures hash author time-sent contents]} :post,
  {:keys [name ship], :as resource} :resource})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/graph.clj#L45-L64)</sub>
## `parse-graph-reference`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/graph.clj#L27-L27)</sub>
## `parse-graph-update`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/graph.clj#L3-L3)</sub>
# sorted-falnyd.airlock.schema.parser.group 





## `parse-group-update`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/group.clj#L5-L5)</sub>
## `parse-policy`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/group.clj#L7-L7)</sub>
# sorted-falnyd.airlock.schema.parser.hark 





## `-added`
``` clojure

(-added {body :body, {{desk :desk, ppath :path} :place, path :path} :bin, time :time})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L33-L41)</sub>
## `-archived`
``` clojure

(-archived {body :body, {{desk :desk, ppath :path} :place, path :path} :bin, time :time})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L43-L51)</sub>
## `-opened`
``` clojure

(-opened {o :opened})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L53-L57)</sub>
## `-read-count`
``` clojure

(-read-count {{desk :desk, path :path} :place, inc? :inc, c :count})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L24-L31)</sub>
## `-saw-place`
``` clojure

(-saw-place {{desk :desk, path :path} :place, time :time})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L16-L22)</sub>
## `-unread-count`
``` clojure

(-unread-count {{desk :desk, path :path} :place, inc? :inc, c :count})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L7-L14)</sub>
## `collect`
``` clojure

(collect & kvs)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L3-L5)</sub>
## `parse-hark-update`
``` clojure

(parse-hark-update {:keys [unread-count saw-place read-count added archived opened]})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/hark.clj#L59-L72)</sub>
# sorted-falnyd.airlock.schema.parser.json-schema 





## `-parse-default`
``` clojure

(-parse-default m)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L101-L103)</sub>
## `contact`
``` clojure

(contact)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L11-L11)</sub>
## `graph`
``` clojure

(graph)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L10-L10)</sub>
## `group`
``` clojure

(group)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L13-L13)</sub>
## `json-schema-type`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L31-L31)</sub>
## `maybe-zip`
``` clojure

(maybe-zip & kvs)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L48-L50)</sub>
## `metadata`
``` clojure

(metadata)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L12-L12)</sub>
## `parse-all-of`
``` clojure

(parse-all-of m)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L96-L99)</sub>
## `parse-any-of`
``` clojure

(parse-any-of m)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L73-L94)</sub>
## `parse-array`
``` clojure

(parse-array {:strs [items maxItems minItems]})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L52-L58)</sub>
## `parse-definitions`
``` clojure

(parse-definitions m prefix)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L125-L135)</sub>
## `parse-ref`
``` clojure

(parse-ref m)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L68-L71)</sub>
## `parse-tuple`
``` clojure

(parse-tuple {:strs [items]})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L60-L62)</sub>
## `qualify-definitions`
``` clojure

(qualify-definitions d prefix)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L15-L29)</sub>
## `type-parameters`
``` clojure

(type-parameters s)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/json_schema.clj#L120-L123)</sub>
# sorted-falnyd.airlock.schema.parser.metadata 





## `parse-association`
``` clojure

(parse-association
 {resource :resource,
  app-name :app-name,
  group :group,
  {:keys [description date-created creator color config title preview hidden picture vip]} :metadata})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/metadata.clj#L20-L39)</sub>
## `parse-md-config`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/metadata.clj#L7-L7)</sub>
## `parse-metadata-update`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/metadata.clj#L5-L5)</sub>
# sorted-falnyd.airlock.schema.parser.registry 





## `contact-registry`
``` clojure

(contact-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L50-L54)</sub>
## `dm-hook-registry`
``` clojure

(dm-hook-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L127-L128)</sub>
## `graph-registry`
``` clojure

(graph-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L18-L48)</sub>
## `group-registry`
``` clojure

(group-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L65-L71)</sub>
## `hark-registry`
``` clojure

(hark-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L130-L131)</sub>
## `master-registry`
``` clojure

(master-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L133-L144)</sub>
## `metadata-registry`
``` clojure

(metadata-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L56-L63)</sub>
## `remove-entry`
``` clojure

(remove-entry m e)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L7-L16)</sub>
## `response-registry`
``` clojure

(response-registry)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/registry.clj#L73-L125)</sub>
# sorted-falnyd.airlock.schema.parser.urbit 





## `make-resource`
``` clojure

(make-resource ship name)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/urbit.clj#L30-L32)</sub>
## `parse-resource`
``` clojure

(parse-resource r)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/urbit.clj#L5-L10)</sub>
## `parse-ship`
``` clojure

(parse-ship r)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/urbit.clj#L23-L28)</sub>
## `unparse-resource`
``` clojure

(unparse-resource r)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/schema/parser/urbit.clj#L12-L21)</sub>
# sorted-falnyd.airlock.ship 





## `-login-request`
``` clojure

(-login-request {:keys [uri code]})
```


Build request to obtain a cookie
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/ship.clj#L23-L28)</sub>
## `ensure-port`
``` clojure

(ensure-port uri port)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/ship.clj#L39-L44)</sub>
## `extract-cookie`
``` clojure

(extract-cookie resp)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/ship.clj#L34-L37)</sub>
## `login!`
``` clojure

(login! {:keys [code client], :as ship})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/ship.clj#L63-L69)</sub>
## `make-ship`
``` clojure

(make-ship)
(make-ship {:keys [uri port client], :or {port 8080, uri "http://localhost"}, :as ship})
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/ship.clj#L51-L61)</sub>
## `zod`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/ship.clj#L46-L49)</sub>
# sorted-falnyd.airlock.util 





## `-str`
``` clojure

(-str)
(-str x)
(-str x & xs)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L61-L66)</sub>
## `counter`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L6-L6)</sub>
## `da->unix`
``` clojure

(da->unix da)
```


Convert Urbit date to Unix Epoch.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L11-L14)</sub>
## `da-second`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L8-L8)</sub>
## `da-time`
``` clojure

(da-time)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L26-L28)</sub>
## `da-unix-epoch`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L7-L7)</sub>
## `dec->ud`
``` clojure

(dec->ud s)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L56-L58)</sub>
## `decode-resource`
``` clojure

(decode-resource s)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L68-L77)</sub>
## `desig`
``` clojure

(desig ship)
```


Ensure a ship name does not start with `~`.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L37-L42)</sub>
## `ensig`
``` clojure

(ensig ship)
```


Ensure a ship name starts with `~`.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L30-L35)</sub>
## `into-string`
``` clojure

(into-string xf s)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L49-L51)</sub>
## `offset`
<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L9-L9)</sub>
## `remove-nils`
``` clojure

(remove-nils m)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L83-L85)</sub>
## `sbrf`
``` clojure

(sbrf)
(sbrf sb)
(sbrf sb x)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L44-L47)</sub>
## `unix->da`
``` clojure

(unix->da t)
```


Convert Unix Epoch to Urbit date.
<br><sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L16-L24)</sub>
## `uri`
``` clojure

(uri & args)
```

<sub>[source](https://github.com/sorted-falnyd/clj-airlock-client/blob/master/src/main/clojure/sorted_falnyd/airlock/util.clj#L79-L81)</sub>
