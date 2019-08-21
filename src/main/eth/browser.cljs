(ns eth.browser
  (:require [eth.main :refer [start]]))

(def configuration {:url "http://localhost:8545"
                    :preset "test"})

(defn init []
  (start configuration))
