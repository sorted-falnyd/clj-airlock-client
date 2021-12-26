(ns sorted-falnyd.airlock.spec
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]))

(s/def ::id pos-int?)
(s/def ::app string?)

;; "/updates"
(s/def ::path string?)

;; "~sampel-planet"
(s/def ::patp (s/and string? #(str/starts-with? % "~")))

;; "sampel-planet"
(s/def ::patp* (s/and string? #(str/starts-with? % "~")))

;;; Name of a clay mark
;; "graph-update"
(s/def ::mark string?)

;; "graph-store"
(s/def ::gall-agent string?)

(s/def ::ship ::patp*)
