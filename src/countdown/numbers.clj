(ns countdown.numbers
  (:require
    [clojure
     [spec :as s]
     [walk :refer [postwalk]]]))

(defn seventy-fives [n]
  (for [i (range 1 (inc n))]
    [i (* 75 i)]))

(defn gen-numbers [nbig]
  (let [big (take nbig (shuffle [100 75 50 25]))
        little (repeatedly (- 6 nbig) #(inc (rand-int 9)))]
    (concat big little)))

(defn operate-rand
  "Perform a random operation on two numbers,
  but retry if the result is not an integer."
  [ops n m]
  (first (filter (every-pred integer? pos?)
                 (map #(% n m) (shuffle ops)))))

(defn gen-target [xs]
  (let [ops [+ - * /]]
    (reduce (partial operate-rand ops)
                         ;; use between 2 and 6 (all) of the numbers
                         (take (+ 2 (rand-int 5))
                               (shuffle xs)))))

(s/def ::num (s/or
               :big-one #{25 50 75 100}
               :little-one (s/and integer? #(< 0 % 11))))

(s/fdef gen-target
        :args (s/cat :xs (s/coll-of ::num :count 6))
        :ret (s/and pos? integer?))

(defn sub-multiset?
  "Is multiset a contained in b?"
  [a b]
  (reduce-kv (fn [is? k v]
               (and is?
                    (b k)
                    (<= v (b k))))
             true
             a))

;;TODO: Figure out why using functions instead of quoted symbols
;; makes everything go weird
(def op? #{'+ '- '* '/})

(s/def ::prefix-solution
  ;;TODO: Add support for more than two arguments
  (s/or :arg ::num
        :expression (s/tuple op?
                              ::prefix-solution
                              ::prefix-solution)))

(defn check-solution [nums target solution]
  (let [solution-vec (postwalk #(if (sequential? %) (vec %) %) solution)
        solution-nums (frequencies (filter number? (flatten solution)))]
    (when (and (s/valid? ::prefix-solution solution-vec)
               (sub-multiset? solution-nums
                              (frequencies nums)))
      (->> solution
           (postwalk #(if (sequential? %) (seq %) %))
           eval
           (= target)))))
