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

(s/def ::topic #{"tax"
                 "sex life"
                 "overwhelming dullness"
                 "new book"
                 "dietary restrictions"
                 "deepest fears"
                 "hair"
                 "weird laugh"})

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
        (string/join " " ["In" (str context ",") subject verb (str object "'s") (str topic ".")])))
