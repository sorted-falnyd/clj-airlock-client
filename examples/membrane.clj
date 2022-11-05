(ns membrane
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [membrane.java2d :as backend]
   [membrane.ui :as ui
    :refer [vertical-layout
            translate
            horizontal-layout
            button
            label
            with-color
            bounds
            spacer
            on]]
   [membrane.component :as component
    :refer [defui defeffect]]
   [membrane.basic-components :as basic]))

(def state (atom {}))

(defn rui [v s]
  (reset! state s)
  (backend/run (component/make-app v state)))

(defn draw-arrow [checked?]
  (if checked?
    (ui/with-style ::ui/style-stroke
      (ui/path [8 12] [4 5] [12 5] [8 12]))
    (ui/with-style ::ui/style-stroke
      (ui/path [12 8] [5 12] [5 4] [12 8]))))

(defrecord Arrow [checked?]
    ui/IOrigin
    (-origin [_]
        [0 0])

    ui/IBounds
    (-bounds [this]
        (bounds (draw-arrow checked?)))

    ui/IChildren
    (-children [this]
        [(draw-arrow checked?)]))

(swap! ui/default-draw-impls
       assoc Arrow
       (fn [draw]
         (fn [this]
           (draw (draw-arrow (:checked? this))))))

(defn arrow
  "Graphical elem that will draw a checkbox."
  [checked?]
  (Arrow. checked?))

(defui collapsible-list*
  [{:keys [items selected open]}]
  (let [labels (for [m items
                     v (cons (:label m) (map :label (:children m)))]
                 (ui/label v))
        max-width (+ 15 (reduce max 0 (map ui/width labels)))
        padding-y 8
        padding-x 12

        rows
        (apply
         vertical-layout
         (for [{:keys [children value] :as item} items
               :let [semi-open? (boolean
                                 (or (get open value)
                                     (= selected value)))
                     open? (boolean
                            (or semi-open?
                                (some #(= selected (:value %)) children)))]
               {:keys [label value child?]} (cons item (when open? (map #(assoc % :child? true) children)))]
           (let [hover? (get extra [:hover? value])
                 selected? (= selected value)
                 label (if selected?
                         (ui/with-color [1 1 1]
                           (ui/label label))
                         (ui/label label))

                 [_ h] (bounds label)
                 row-height (+ h 4)
                 row-width (+ max-width (* 2 padding-x))
                 elem (basic/on-hover
                       {:hover? hover?
                        :body
                        (on
                         :mouse-down
                         (fn [_]
                           [[::basic/select $selected value]])

                         [(spacer row-width row-height)
                          (cond

                            selected?
                            (ui/filled-rectangle [0 0.48 1]
                                                 row-width row-height)

                            hover?
                            (ui/filled-rectangle [0.976 0.976 0.976]
                                                 row-width row-height))
                          (translate padding-x 2
                                     label)])})]
             (if child?
               (ui/translate 15 0 elem)
               (horizontal-layout
                (on :mouse-down (fn [_] [[::toggm $open value]]) (arrow semi-open?))
                elem)))))
        [rows-width rows-height] (bounds rows)]
    [(ui/with-style
       ::ui/style-stroke
       (ui/with-color [0.831
                       0.831
                       0.831]
         (ui/rounded-rectangle rows-width
                               (+ rows-height (* 2 padding-y))
                               4)))
     (ui/with-style
       ::ui/style-fill
       (ui/with-color [1 1 1]
         (ui/rounded-rectangle rows-width
                               (+ rows-height (* 2 padding-y))
                               4)))
     (translate 0 (- padding-y 2)
                rows)]))

(defui collapsible-list2*
  [{:keys [items selected open]}]
  (let [labels (for [m items
                     v (cons (:metadata/title m) (map :metadata/title (:graph/_group m)))]
                 (ui/label v))
        max-width (+ 15 (reduce max 0 (map ui/width labels)))
        padding-y 8
        padding-x 12

        rows
        (apply
         vertical-layout
         (for [{:keys [graph/_group urbit/resource] :as item} items
               :let [semi-open? (boolean
                                 (or (get open resource)
                                     (= selected resource)))
                     open? (boolean
                            (or semi-open?
                                (some #(= selected (:value %)) _group)))]
               {:keys [metadata/title urbit/resource child?]} (cons item (when open? (map #(assoc % :child? true) _group)))]
           (let [hover? (get extra [:hover? resource])
                 selected? (= selected resource)
                 label (if selected?
                         (ui/with-color [1 1 1]
                           (ui/label title))
                         (ui/label title))

                 [_ h] (bounds label)
                 row-height (+ h 4)
                 row-width (+ max-width (* 2 padding-x))
                 elem (basic/on-hover
                       {:hover? hover?
                        :body
                        (on
                         :mouse-down
                         (fn [_]
                           [[::basic/select $selected resource]])

                         [(spacer row-width row-height)
                          (cond

                            selected?
                            (ui/filled-rectangle [0 0.48 1]
                                                 row-width row-height)

                            hover?
                            (ui/filled-rectangle [0.976 0.976 0.976]
                                                 row-width row-height))
                          (translate padding-x 2 label)])})]
             (if child?
               (ui/translate 15 0 elem)
               (horizontal-layout
                (on :mouse-down (fn [_] [[::toggm $open resource]]) (arrow semi-open?))
                elem)))))
        [rows-width rows-height] (bounds rows)]
    [(ui/with-style
       ::ui/style-stroke
       (ui/with-color [0.831
                       0.831
                       0.831]
         (ui/rounded-rectangle rows-width
                               (+ rows-height (* 2 padding-y))
                               4)))
     (ui/with-style
       ::ui/style-fill
       (ui/with-color [1 1 1]
         (ui/rounded-rectangle rows-width
                               (+ rows-height (* 2 padding-y))
                               4)))
     (translate 0 (- padding-y 2)
                rows)]))

(defeffect ::toggm [$open value]
  (dispatch! :update $open #(update % value not)))

(comment
  (rui
   #'collapsible-list*
   {:items [{:label "label1"
             :value :value1
             :children [{:value :value1.1
                         :label "label1.1"}
                        {:value :value1.2
                         :label "label1.2"}]}
            {:label "label2"
             :value :value2
             :children [{:value :value2.1
                         :label "label2.1"}
                        {:value :value2.2
                         :label "label2.2"}
                        {:value :value2.3
                         :label "label2.3"}]}]}))

(comment
  (deref state)
  (reset! state {})

  (require '[datascript])
  (def items
    (into [] cat (d/q '[:find (pull ?e [* {:graph/_group [*]}])
                        :where
                        [?e :graph/group ?e]]
                      (d/db datascript/conn))))
  (rui #'collapsible-list2* {:items items}))

(comment
  (def posts
    (let [db (d/db datascript/conn)]
      (->> [:urbit/resource :sorted-falnyd/beginners-820]
           (d/entid db)
           (d/datoms db :avet :graph/resource)
           (mapv :e)
           (d/pull-many db '[{:graph.post/author [:urbit/ship]} :graph.post/contents :graph.post/time-sent])
           (sort-by :graph.post/time-sent)
           (partition-by :graph.post/author)
           (into [] (map (fn [[x :as xs]] {:author (:graph.post/author x) :posts xs}))))))
  (def posts'
    (let [db (d/db datascript/conn)]
      (->> [:urbit/resource :sorted-falnyd/beginners-820]
           (d/entid db)
           (d/datoms db :avet :graph/resource)
           (mapv :e)
           (d/pull-many db '[:graph.post/id
                             {:graph.post/author [:urbit/ship]}
                             :graph.post/contents
                             :graph.post/time-sent])
           (->Eduction (map (fn [m] [(:graph.post/id m) m])))
           (into (sorted-map-by (fn [x y] (compare x y))))))))

(def name-font
  (assoc (ui/font (backend/logical-font->font-family :sans-serif) 14)
         :weight :bold))

(def code-font
  (ui/font (backend/logical-font->font-family :monospace) 14))


(defn wrap*
  [s ^long n]
  (let [words (str/split s #"\ +")
        sb (StringBuilder.)]
    (loop [[^String word & words] words
           i 0]
      (when word
        (let [len (.length word)
              k (+ i len)]
          (cond
            (zero? i)
            (do (doto sb (.append word))
                (recur words k))
            (and (pos? i) (< k n))
            (do (doto sb (.append " ") (.append word))
                (recur words k))
            :else
            (do (doto sb (.append "\n") (.append word) (.append " "))
                (recur words len))))))
    (str sb)))

(defn wrap
  [s n]
  (str/join "\n" (map #(wrap* % n) (str/split s #"\n"))))

(defui post-contents
  [{:keys [graph.post/contents]}]
  (apply vertical-layout
         (for [content contents
               :let [?text (:graph.post/content content)
                     ?ref (str (:graph.post.content/reference content))]
               :when (not= "\n" ?text)
               :let [?text (when ?text (wrap ?text 60))
                     ?ref (when ?ref (wrap ?ref 60))]]
           (condp = (:graph.post.content/type content)
             :graph.post.content.type/text (label ?text)
             :graph.post.content.type/url (label ?text)
             :graph.post.content.type/mention (label ?text name-font)
             :graph.post.content.type/code (label ?text code-font)
             :graph.post.content.type/app-reference (label ?ref)
             :graph.post.content.type/graph-reference (label ?ref)
             :graph.post.content.type/group-reference (label ?ref)))))


(comment
  (-> posts' seq (nth 3) val)
  (rui #'post-contents (-> posts (nth 3) :posts (nth 0))))

(def time-font
  (ui/font (backend/logical-font->font-family :monospace) 10))

(defn time->date
  [t]
  (let [ldt (java.time.LocalDateTime/ofInstant
             (java.time.Instant/ofEpochMilli t)
             (java.time.ZoneId/of "UTC"))]
    [(java.time.LocalDate/from ldt)
     (java.time.LocalTime/from ldt)]))

(defui post-group
  [{:keys [author posts]}]
  (apply
   vertical-layout
   (ui/spacer 0 5)
   (label (:urbit/ship author) name-font)
   (ui/spacer 0 5)
   (for [post posts
         :let [[ld lt] (time->date (:graph.post/time-sent post))]]
     (horizontal-layout
      (vertical-layout
       (label ld time-font)
       (label lt time-font))
      (ui/spacer 10 0)
      (post-contents post)))))

(comment
  (rui #'post-group (-> posts (nth 3)))
  )

(defui chat-log
  [{:keys [chat]}]
  (apply vertical-layout
         (for [pg chat]
           (post-group pg))))

(->> posts'
     (partition-by (fn [[_ v]] (:graph.post/author v)))
     first
     first
     val
     :graph.post/author)

(defui post-contents2
  [{:keys [graph.post/contents]}]
  (apply vertical-layout
         (for [content contents
               :let [?text (:graph.post/content content)
                     ?ref (str (:graph.post.content/reference content))]
               :when (not= "\n" ?text)
               :let [?text (when ?text (wrap ?text 60))
                     ?ref (when ?ref (wrap ?ref 60))]]
           (condp = (:graph.post.content/type content)
             :graph.post.content.type/text (label ?text)
             :graph.post.content.type/url (label ?text)
             :graph.post.content.type/mention (label ?text name-font)
             :graph.post.content.type/code (label ?text code-font)
             :graph.post.content.type/app-reference (label ?ref)
             :graph.post.content.type/graph-reference (horizontal-layout
                                                       (label "Ref:")
                                                       (spacer 10 0)
                                                       (post-contents2 (get (get extra :chat) (:graph.post/id (:graph.post.content/reference content)))))
             :graph.post.content.type/group-reference (label ?ref)))))

(defui post-group2
  [{:keys [posts]}]
  (let [author (-> posts first val :graph.post/author)]
    (apply
     vertical-layout
     (ui/spacer 0 5)
     (label (:urbit/ship author) name-font)
     (ui/spacer 0 5)
     (for [[_ post] posts
           :let [[ld lt] (time->date (:graph.post/time-sent post))]]
       (horizontal-layout
        (vertical-layout
         (label ld time-font)
         (label lt time-font))
        (ui/spacer 10 0)
        (post-contents2 (assoc-in post [:extra :chat] (get extra :chat))))))))

(defui chat-log2
  [{:keys [chat]}]
  (apply vertical-layout
         (for [poasts (partition-by (fn [[_ v]] (:urbit/ship (:graph.post/author v))) chat)]
           (post-group2 {:posts poasts
                         :extra (assoc extra :chat chat)}))))

(comment
  (rui #'chat-log {:chat posts})
  (rui #'chat-log2 {:chat posts'})
  )

(defui chat-message
  [{:keys [content from]}]
  (horizontal-layout
   (label (str from " :") name-font)
   (spacer 10)
   (label content)))

(comment
  (backend/run
    (component/make-app
     #'chat-message
     {:content "Hello, world!"
      :from "Somebody"})))

(defui chat-view
  [{:keys [chat]}]
  (let [items (:items chat)]
    (ui/vertical-layout
     (ui/label (:title chat))
     (basic/scrollview
      {:scroll-bounds [600 200]
       :body
       (apply
        ui/vertical-layout
        (for [{:keys [content from time]} items]
          (vertical-layout
           (spacer 0 5)
           (horizontal-layout
            (label from name-font)
            (spacer 10)
            (label time))
           (spacer 0 5)
           (label content))))}))))

(comment
  (backend/run
    (component/make-app
     #'chat-view
     {:chat
      {:items
       [{:content "Hello, world!"
         :time "2020-08-01 18:00"
         :from "Somebody"}
        {:content "Hello, Bob."
         :time "2020-08-01 18:01"
         :from "Alice"}
        {:content "Hi, Alice!"
         :time "2020-08-01 18:02"
         :from "Bob"}]
       :input ""
       :title "~~~ The Chat ~~~"}})))

(defui gui [{:keys [chat channels input]}]
  (horizontal-layout
   (collapsible-list* {:items channels})
   (vertical-layout
    (chat-view {:chat chat})
    (horizontal-layout
     (ui/fixed-bounds
      [600 100]
      [(with-color [0.65 0.65 0.65]
         (ui/with-style ::ui/style-stroke
           (ui/rectangle 600 100)))
       (basic/textarea {:text input
                        :border? false})])
     (basic/button {:text "SEND"
                    :on-click (fn []
                                [[::add-message $chat input]
                                 [:set $input ""]])})))))

(defeffect ::add-message [$chat input]
  (when-not (.equals "" input)
    (dispatch! :update $chat #(update % :items conj {:content input :time (str (java.time.Instant/now)) :from "Anon"}))))

(comment
  (backend/run
    (component/make-app
     #'gui
     {:chat
      {:items
       [{:content "Hello, world!"
         :time "2020-08-01 18:00"
         :from "Somebody"}
        {:content "Hello, Bob."
         :time "2020-08-01 18:01"
         :from "Alice"}
        {:content "Hi, Alice!"
         :time "2020-08-01 18:02"
         :from "Bob"}]
       :title "~~~ The Chat ~~~"}
      :channels
      [{:label "label1"
        :value :value1
        :children [{:value :value1.1
                    :label "label1.1"}
                   {:value :value1.2
                    :label "label1.2"}]}
       {:label "label2"
        :value :value2
        :children [{:value :value2.1
                    :label "label2.1"}
                   {:value :value2.2
                    :label "label2.2"}
                   {:value :value2.3
                    :label "label2.3"}]}]})))
