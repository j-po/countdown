(ns countdown.spec
  (:require [clojure
             [pprint :refer [pprint]]
             [spec :as s]
             [walk :refer [postwalk]]]
            [clojure.spec.gen :as gen]))

;;TODO: Figure out why using functions instead of quoted symbols
;; makes everything go weird
(def op? #{'+ '- '* '/})

(s/def ::num (s/or
               :big-one #{25 50 75 100}
               :little-one (s/and integer? #(< 0 % 11))))

(s/fdef countdown.core/goal
        :args (s/cat :xs (s/coll-of ::num :count 6))
        :ret (s/and pos? integer?))

(s/def ::prefix-solution
  ;;TODO: Add support for more than two arguments
  (s/or :arg ::num
        :expression (s/tuple op?
                              ::prefix-solution
                              ::prefix-solution)))
