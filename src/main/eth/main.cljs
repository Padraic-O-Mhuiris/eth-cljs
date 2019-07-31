(ns eth.main
  (:require [eth.net :as net]))

(defn start [build]
  (println build)
  (net/init))

