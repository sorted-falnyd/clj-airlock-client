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

### Parsing updates

The updates coming over a channel and responses to HTTP requests are
structurally encoded.

To get back decoded, tagged data, construct a decoder:

```clojure
(require '[sorted-falnyd.airlock.schema.parser :as parser])
(def decoder (parser/decoder))
```

A successfully decoded datum will return as a tagged tuple, otherwise, a
map containing the error.

A decoded response can be parsed:

```clojure
(parser/parse-response (decoder datum))
```

A parsed response will contain a unique key for each response type,
`:urbit.airlock/response`.

Further actions can dispatch on the key instead of parsing the data.

#### Currently implemented parsers

- dm hook
- harks
- graph update
- contact update
- metadata update
- group update

## Development

### Schemas

Using ts-json-schema-generator on the urbit API, emit schemas that can be parsed

```bash
./node_modules/.bin/ts-json-schema-generator -e all --additional-properties true -f tsconfig.json --type 'Graph'  > ~/Urbit/clj-airlock-api/src/dev/resources/graph-update2.json
```

Parse the schema to a basic malli schema

```clojure
(parse-definitions (json/read-value (slurp "graph.json")))
```

Merge the registries and parse incoming events

## License

Copyright © 2021-2022 sorted-falnyd

Distributed under the Eclipse Public License version 1.0.
