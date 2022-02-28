# io.github.sorted-falnyd/clj-airlock-api

Urbit Airlock API implementation in Clojure

WIP

## Usage

### Require

```clojure
(ns user
  (:require
   [sorted-falnyd.airlock.ship :as ship]
   [sorted-falnyd.airlock.connection :as conn]
   [sorted-falnyd.airlock.http :as http]
   [sorted-falnyd.airlock.action :as action]
   [sorted-falnyd.airlock.graph :as graph]
   [clojure.core.async :as a]))
```
### Set up a connection

```clojure
(def conn
  (->
   {}
   ship/make-ship
   ship/login!
   conn/make-connection
   conn/build!
   conn/start!))
```

### Actions

#### Poke

```clojure
(http/send! conn (action/poke "hood" "helm-hi" "Knock knock"))
```

#### Subscribe

```clojure
(http/send! conn (action/subscribe "graph-store" "/updates"))
(http/send! conn (action/subscribe "metadata-store" "/all"))
```

#### Send posts

```clojure
(->> [{:text "I am the post"}]
     (graph/make-post "zod")
     (graph/add-post "zod" "my-chat-1686")
     (http/send! conn))
```

### Channel

The connection map contains a core.async channel which receives *all*
incoming messages on the initial subscription channel

```clojure
(:channel conn)
```

Further actions, indexing, etc can be performed by subscribing on it.

## License

Copyright © 2021 sorted-falnyd

Distributed under the Eclipse Public License version 1.0.
