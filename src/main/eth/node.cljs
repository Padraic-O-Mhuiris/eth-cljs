(ns eth.node
  (:require [eth.main :refer [start]]
            ["xhr2" :as xhr2]
            ["crypto" :as crypto]))

;; workaround for dealing with http requests when the library is ran in node.js
(set! js/XMLHttpRequest xhr2)
(set! js/crypto crypto)

(def configuration {:url "http://localhost:8545"
                    :preset "test"})

(defn init []
  (start configuration))
