(ns parsing
  (:require
   [clojure.edn :as edn]
   [sorted-falnyd.airlock.schema.parser :as parser]))

(def updates (edn/read-string (slurp "updates.edn")))
(def updates2 (edn/read-string (slurp "updates2.edn")))
(def samples (edn/read-string (slurp "samples.edn")))
(def decoder (parser/decoder))

(mapv parser/parse-response (remove :schema (mapv decoder samples)))
