(ns sorted-falnyd.airlock.schema.parser.group)

(defmulti parse-group-update first)

(defmulti parse-policy first)

(defmethod parse-group-update :Group.GroupUpdateAddMembers
  [[_ {{{s :ship n :name} :resource ships :ships} :addMembers}]]
  {:group/update :group.update/add-members
   :urbit/resource (keyword s n)
   :group/members ships})

(defmethod parse-group-update :Group.GroupUpdateAddGroup
  [[_ {{{s :ship n :name} :resource
        policy :policy
        hidden :hidden} :addGroup}]]
  (-> {:group/update :group.update/add-group
       :urbit/resource (keyword s n)
       :group/hidden hidden}
      (merge (parse-policy policy))))

(defmethod parse-policy :Group.OpenPolicy
  [[_ {{ban-ranks :banRanks banned :banned} :open}]]
  {:group/policy :group.policy/open
   :group.policy/ban-ranks ban-ranks
   :group.policy/banned banned})

(defmethod parse-policy :Group.InvitePolicy
  [[_ {{pending :pending} :invite}]]
  {:group/policy :group.policy/invite
   :group.policy/pending pending})
