(ns countdown.core-test
  (:require [clojure.java.io :as io]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]
            [clojure.test :refer :all]
            [countdown.core :refer :all]
            [countdown.numbers :refer :all]))

(deftest test-gen-target
  (testing "gen-target"
    (is (not (:failure (stest/check `gen-target))))))

(deftest test-multiset
  (testing "sub-multiset?"
    (are [is? nums] (= is? (boolean (sub-multiset? (frequencies nums) (frequencies [1 1 1 1 3 2]))))
         true [1 1 1 2]
         false [5]
         false [1 1 1 1 1])))

(deftest test-check-solution
  (testing "check-solution"
    (testing "valid solutions are valid"
      (is (check-solution [1 1] 2 '[+ 1 1]))
      (is (check-solution [1 2 3 10] 6 '[+ 1 [+ 2 3]])))
    (testing "invalid solutions are invalid, return nil"
      (are [answer nums solution] (nil? (check-solution nums answer solution))
           [1 1] 2 [- 1 1]
           [1 2 3] 6 [- 1 [+ 2 3]]))
;; TODO: test solutions involving bad numbers
    (testing "can't run arbitrary code"
      (is
        (nil? (let [state (atom nil)]
                (do
                  (check-solution [420 69] 2 '[swap! state 0xdeadbeef])
                  @state)))))))

;; TODO: more complete coverage, preferably including the game loop
