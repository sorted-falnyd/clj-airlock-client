(ns sorted-falnyd.airlock.schema.parser.dm-hook)

(defn parse-dm-hook [{:keys [pendings accept]}]
  (cond
    pendings {:dm/pendings pendings}
    accept {:dm/accept accept}))
