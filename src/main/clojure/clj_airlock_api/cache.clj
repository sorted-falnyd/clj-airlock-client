(ns clj-airlock-api.cache)

(defprotocol ICache
  (-add! [this id p])
  (-get! [this id])
  (-remove! [this id]))

(defprotocol IPromiseCache
  (-promise! [this id])
  (-deliver! [this id v]))

(defn ->Cache
  "Create a cache with `deliver!` function which delivers results to a
  promise and a promise factory `pf` which creates the promise."
  [deliver! pf]
  (let [a (atom {})]
    (reify
      clojure.lang.IDeref
      (deref [_] @a)
      ICache
      (-add! [this id p] (swap! a assoc id p) p)
      (-get! [this id] (get @a id))
      (-remove! [this id] (swap! a dissoc id))
      IPromiseCache
      (-promise! [this id] (let [p (pf)] (-add! this id p)))
      (-deliver! [this id v]
        (when-let [p (-get! this id)]
          (deliver! p v)
          (-remove! this id))))))
