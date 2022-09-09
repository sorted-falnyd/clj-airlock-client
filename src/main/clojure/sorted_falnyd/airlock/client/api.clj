(ns sorted-falnyd.airlock.client.api
  (:require
   [sorted-falnyd.airlock.ship :as ship]
   [sorted-falnyd.airlock.connection :as conn]
   [sorted-falnyd.airlock.http :as http]))

(defn client
  "Construct an API Client.

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
     see: [[sorted-falnyd.airlock.connection/default-callbacks]]"
  ([] (client {}))
  ([options]
   (->
    options
    ship/make-ship
    ship/login!
    conn/make-connection
    conn/build!
    conn/start!)))

(defn send!
  [client action]
  (http/send! client action))
