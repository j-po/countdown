(ns countdown.core-test
  (:require [clojure.java.io :as io]
            [clojure.spec.test :as stest]
            [clojure.test :refer :all]
            [countdown.core :refer :all]))

(deftest test-check-solution
  (testing "check-solution"
    (testing "valid solutions are valid"
      (is (check-solution 2 '[+ 1 1]))
      (is (check-solution 6 '[+ 1 [+ 2 3]])))
    (testing "invalid solutions are invalid, return nil"
      (are [answer solution] (nil? (check-solution answer solution))
           2 [- 1 1]
           6 [- 1 [+ 2 3]]))
    (testing "can't run arbitrary code"
      (is 
        (nil? (let [state (atom nil)]
                (do
                  (check-solution 2 '[swap! state 0xdeadbeef])
                  @state)))))))
