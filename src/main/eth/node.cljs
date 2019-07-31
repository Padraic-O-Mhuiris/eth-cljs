(ns eth.node
  (:require [eth.main :refer [start]]
            ["xhr2" :as xhr2]))

;; workaround for dealing with http requests when the library is ran in node.js
(set! js/XMLHttpRequest xhr2)

(defn init []
  (start "node"))
