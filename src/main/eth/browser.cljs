(ns eth.browser
  (:require [eth.main :refer [start]]))

(defn init []
  (start "browser"))
