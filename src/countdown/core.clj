(ns countdown.core
  (:require [clojure.edn :as edn]
            [countdown.numbers :refer [seventy-fives
                                       gen-numbers
                                       gen-target
                                       check-solution]]))

(defn user-input
  ([] (user-input "exit"))
  ([exit] (let [in (read-line)]
            (when (not= exit in)
              in))))

(defn validate-input-int! []
  (if-let [in (try
                (Integer/parseInt (user-input))
                (catch java.lang.NumberFormatException _
                  (println "That's not a valid number. Try again?")))]
    in
    (recur)))

(defn validate-num-big!
  "Validate user's input is a valid number
  of big numbers to have."
  []
  (loop [num-big (validate-input-int!)]
    (if (< 0 num-big 5)
      num-big
      (do
        (println
          "You're allowed between 1 and 4 big numbers (inclusive)")
        (recur (validate-input-int!))))))

(defn game-loop [input game-state]
  (case (:stage game-state)
    :begin (do
             (println "LET'S PLAY COUNTDOWN!!")
             (println "How many big ones?")
             (recur (validate-num-big!)
                    (conj game-state
                          {:stage :gen-numbers})))
    :gen-numbers (let [nums (gen-numbers input)
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
                      (recur (-> (user-input)
                                 edn/read-string)
                             (conj game-state
                                   {:stage :check-solution
                                    :user-num input})))
    :check-solution (let [{:keys [user-num nums target score]} game-state
                          correct? (check-solution nums user-num input)
                          points (if correct?
                                   (case (- target user-num)
                                     0 10
                                     (1 2 3 4 5) 7
                                     (6 7 8 9 10) 5
                                     0)
                                   0)
                          new-score (+ score points)]
                      (if correct?
                        (println "It's right.")
                        (println "No, sorry, it doesn't add up."))
                      (println
                        (format "That's %d points, leaving your score at %d. Play again?"
                                points
                                new-score))
                      (recur (user-input)
                             (conj game-state
                                   {:stage :play-again?
                                    :score new-score})))
    :play-again? (case input
                   ("y" "yes") (recur ""
                                      {:stage :begin
                                       :score (:score game-state)})
                   ("n" "no") (recur nil {:score {:score game-state}})
                   (do
                     (println "Sorry, didn't catch that. Play again? (y/yes/n/no)")
                     (recur (user-input)
                            game-state)))))

(defn -main []
  (game-loop "" {:stage :begin
                 :score 0}))
