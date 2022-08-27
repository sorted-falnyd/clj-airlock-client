(ns sorted-falnyd.airlock.schema.parser.hark)

(defmacro collect
  [& kvs]
  `(cond-> {} ~@(mapcat (fn [[k v]] `(~v (assoc ~k ~v))) (partition 2 kvs))))

(defn -unread-count
  [{{desk :desk path :path} :place inc? :inc c :count}]
  (collect
   :hark/hark :hark/unread-count
   :hark/count c
   :hark/inc? inc?
   :hark/desk desk
   :hark/path path))

(defn -saw-place
  [{{desk :desk path :path} :place time :time}]
  (collect
   :hark/hark :hark/saw-place
   :hark/time time
   :hark/desk desk
   :hark/path path))

(defn -read-count
  [{{desk :desk path :path} :place inc? :inc c :count}]
  (collect
   :hark/hark :hark/read-count
   :hark/desk desk
   :hark/path path
   :hark/inc? inc?
   :hark/count c))

(defn -added
  [{body :body {{desk :desk ppath :path} :place path :path} :bin time :time}]
  (collect
   :hark/hark :hark/added
   :hark/time time
   :hark.bin/path path
   :hark.place/path ppath
   :hark.place/desk desk
   :hark/body body))

(defn -archived
  [{body :body {{desk :desk ppath :path} :place path :path} :bin time :time}]
  (collect
   :hark/hark :hark/archived
   :hark/time time
   :hark.bin/path path
   :hark.place/path ppath
   :hark.place/desk desk
   :hark/body body))

(defn -opened
  [{o :opened}]
  (cond->
      {:hark/hark :hark/opened}
    o (assoc :hark/opened o)))

(defn parse-hark-update
  [{:keys [unread-count
           saw-place
           read-count
           added
           archived
           opened]}]
  (cond
    unread-count (-unread-count unread-count)
    saw-place (-saw-place saw-place)
    read-count (-read-count read-count)
    added (-added added)
    archived (-archived archived)
    opened (-opened opened)))
