(ns countdown.core
  (:require [clojure
             [pprint :refer [pprint]]
             [spec :as s]
             [walk :refer [postwalk]]]
            [countdown.spec]))

(defn seventy-fives [n]
  (pprint
    (for [i (range 1 (inc n))]
      [i (* 75 i)])))

(defn numbers [nbig]
  (let [big (take nbig (shuffle [100 75 50 25]))
        little (take (- 6 nbig) (repeatedly #(inc (rand-int 9))))]
    (concat big little)))

(defn operate-rand
  "Perform a random operation on two numbers,
  but retry if the result is not an integer."
  [ops n m]
  (first (filter integer? (map #(% n m) (shuffle ops)))))

(defn goal [xs]
  (let [ops [+ - * /]]
    (repeatedly #(reduce operate-rand
                         ;; use between 2 and 6 (all) of the numbers
                         (take (+ 2 (rand-int 5))
                               (shuffle xs))))))

(defn check-solution [goal solution]
  (when (s/valid? :countdown.spec/prefix-solution solution)
    (->> solution
         (postwalk #(if (sequential? %) (seq %) %))
         eval
         (= goal))))
