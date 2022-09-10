[![Clojars Project](https://img.shields.io/clojars/v/io.github.sorted-falnyd/clj-airlock-api.svg)](https://clojars.org/io.github.sorted-falnyd/clj-airlock-api)
[![CI Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fatrox%2Fsync-dotenv%2Fbadge&style=flat)](https://actions-badge.atrox.dev/atrox/sync-dotenv/goto)

# io.github.sorted-falnyd/clj-airlock-api

Urbit Airlock API implementation in Clojure

WIP

## Usage

### Leiningen

```clojure
[io.github.sorted-falnyd/clj-airlock-api "..."]
```

### Deps

```clojure
io.github.sorted-falnyd/clj-airlock-api {:mvn/version "..."}
```

### Require

```clojure
(ns user
  (:require
   [sorted-falnyd.airlock.client.api :as api]))
```
### Set up a connection

```clojure
(def conn
  (api/client
   {:port 80
    ;; :ship-name "sampel-planet"
    ;; :code "topse-cret"
    ;; :uri "http://sampel-planet.arvo.network"
    }))

```

### Actions

```clojure
(require '[sorted-falnyd.airlock.action :as action])
```

#### Poke

```clojure
(api/send! conn (action/poke "hood" "helm-hi" "Knock knock"))
```

#### Subscribe

```clojure
(api/send! conn (action/subscribe "graph-store" "/updates"))
(api/send! conn (action/subscribe "metadata-store" "/all"))
```

#### Send posts

```clojure
(require '[sorted-falnyd.airlock.graph :as graph])
(->> [{:text "I am the post"}]
     (graph/make-post "zod")
     (graph/add-post "zod" "my-chat-1686")
     (api/send! conn))
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
