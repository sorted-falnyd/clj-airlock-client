(ns parsing
  (:require
   [clojure.edn :as edn]
   [sorted-falnyd.airlock.schema.parser.registry :as reg]
   [sorted-falnyd.airlock.schema.parser :as parser]))

(def updates (edn/read-string (slurp "updates.edn")))
(def updates2 (edn/read-string (slurp "updates2.edn")))
(def samples (edn/read-string (slurp "samples.edn")))
(def reg (reg/master-registry))
(def decoder (parser/decoder [:schema {:registry reg} "Response"]))

(mapv parser/parse-response (remove :schema (mapv decoder samples)))

(parser/parse-response
 (decoder {"json" connecting/newest-json
           "id" Integer/MAX_VALUE
           "response" "diff"
           "mark" "graph-update-3"}))


