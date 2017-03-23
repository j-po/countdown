(ns countdown.jokes
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [clojure.string :as string]))

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

(defn gen-joke! []
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
