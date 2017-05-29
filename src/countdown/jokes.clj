(ns countdown.jokes
  (:require [clojure
             [spec :as s]
             [string :as string]
             [walk :refer [postwalk]]]
            [clojure.spec.gen :as gen]
            [com.rpl.specter :refer [select walker]]))

(s/def ::person #{"Susie Dent"
                  "Jon Richardson"
                  "Jimmy Carr"
                  "Joe Wilkinson"
                  "Rachel Riley"
                  "Sean Lock"
                  "the player"})

(s/def ::topic (s/cat
                 :other-modifier (s/? #{"conspicuous absence of"})
                 :adjective (s/* #{"distinctive"
                                   "overwhelming"
                                   "embarrassing"
                                   "weird"
                                   "new"
                                   "stupefying"})
                 :characteristic #{"tax status"
                                   "sex life"
                                   "dullness"
                                   "book"
                                   "dietary restrictions"
                                   "deepest fears"
                                   "hair"
                                   "laugh"}))

(s/def ::context #{"Dictionary Corner"
                   "the bonus round"
                   "the opening monologue"})

(s/def ::verb #{"mocks"
                "performs a song about"
                "performs a poem about"})

(s/def ::subject ::person)

(s/def ::object ::person)

(s/def ::joke (s/keys :req [::context ::subject ::verb ::object ::topic]))

;; gotta use a macro, I guess o_O. I mean, if that even works... :'((

(def jokes (volatile! [])) ;;only mutated at read time, so no need for atom semantics

(defmacro defjoke [joke-kw fmt]
  (let [ks (select (walker keyword?) fmt)]
    `(do
       (s/def ~joke-kw (s/keys :req ~ks))
       (vswap! jokes conj [~joke-kw ~fmt]))))

(defjoke ::basic ["In " ::context ", " ::subject " " ::verb " " ::object "'s " ::topic])

(defn gen-joke! []
  (let [[kw fmt] (rand-nth @jokes)
        content (gen/generate (s/gen kw))]
    (postwalk #(cond
                 (keyword? %) (% content)
                 (sequential? %) (string/join "" %)
                 :else %) fmt)))

(gen-joke!)

(defn format-joke-bad [fmt]
  (let [specs (vec (filter keyword? fmt))
        content (gen/generate (s/gen (s/keys :req ~`specs)))]
    (println "keys: " (filter keyword? fmt))
    (s/keys :req (filter keyword? fmt))
    (println "content: " content)
    (string/join " " (replace content fmt))))

(def my-fmt [::person ::verb "their own" ::topic])

(format-joke my-fmt)

(s/describe (s/keys :req ~'(into [] (filter keyword? my-fmt))))

(defn gen-joke-bad! []
  (let [{:keys [::context
               ::subject
               ::verb
               ::object
               ::topic]} (gen/generate (s/gen ::joke))]
    ;; TODO: Sugar this process better. Make semantically-related things go together.
   ;; Have a DSL for grammars. I dunno. Go wild. Decomplect joke generation and grammar
   ;; definition. In the process, probably recomplect the different components of grammar
   ;; definition that we separated here.
        (string/join " " ["In" (str context ",")
                          subject verb (str object "'s") (str (string/join " " topic) ".")])))
