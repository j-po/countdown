(ns countdown.core-test
  (:require [clojure.java.io :as io]
            [clojure.spec.test :as stest]
            [clojure.test :refer :all]
            [countdown.core :refer :all]))

(testing "check-solution"
  (testing "valid solutions are valid"
  (is (check-solution 2 '[+ 1 1]))
  (is (check-solution 6 '[+ 1 [+ 2 3]])))
  (testing "can't run arbitrary code"
  (is 
       ;;TODO: check file hasn't been written, or remove that part
      (nil? (check-solution 2 '[do [binding [*out* [io/writer [io/resource "hax.txt"] :append true]]] [print "FAILED"] "FAILED"])))))
