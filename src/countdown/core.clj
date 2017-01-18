(ns countdown.core
  (:require [clojure
             [edn :as edn]
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
  (first (filter (every-pred integer? pos?)
                 (map #(% n m) (shuffle ops)))))

(defn gen-target [xs]
  (let [ops [+ - * /]]
    (reduce (partial operate-rand ops)
                         ;; use between 2 and 6 (all) of the numbers
                         (take (+ 2 (rand-int 5))
                               (shuffle xs)))))

(defn check-solution [target solution]
  (when (s/valid? :countdown.spec/prefix-solution solution)
    (->> solution
         (postwalk #(if (sequential? %) (seq %) %))
         eval
         (= target))))

(defn user-input
  ([] (user-input "exit"))
  ([exit] (let [in (read-line)]
            (when (not= exit in)
              in))))

(defn validate-input-int! []
  (loop []
    (try
      (Integer/parseInt (user-input))
      (catch java.lang.NumberFormatException _
        (do
          (println "That's not a valid number. Try again?")
          (recur (user-input)))))))

(defn validate-num-big!
  "Validate user's input is a valid number
  of big numbers to have."
  [pred]
  (loop [in (validate-input-int!)]
    (let [num-big (parse-int in)]
        (if (< 0 num-big 5)
          num-big
          (do
            (println
              "You're allowed between 1 and 4 big numbers (inclusive)")
            (recur (validate-input-int!)))))))

(defn -main []
  (loop [input ""
         game-state {:stage :begin
                     :score 0}]
    (when input
      (case (:stage game-state)
        :begin (do
                 (println "LET'S PLAY COUNTDOWN!!")
                 (println "How many big ones?")
                 (recur (validate-num-big!)
                        (conj game-state
                              {:stage :gen-numbers})))
        :gen-numbers (let [nums (numbers input)
                           target (gen-target nums)]
                       (println (format "Alright, %d big and %d little:"
                                        input
                                        (- 6 input)))
                       (println nums)
                       (println "And your target:")
                       (println target)
                       ;; TODO: do something for the timer
                       (println "What did you get?")
                       (recur (validate-input-int!)
                              (conj game-state
                                    {:stage :declare-answer
                                     :nums nums
                                     :target target})))
        :declare-answer (do
                          (println "Alright, walk us through it.")
                          (recur (comp edn/read-string user-input)
                                 (conj game-state
                                       {:stage :check-solution
                                        :user-num input})))
        :check-solution (if (check-solution (:user-num game-state) input)
                          (let [{:keys [user-num target]} game-state]
                            (println "It's right.")
                            (case (- target user-num))))
        :next-thing-placeholder nil))))
