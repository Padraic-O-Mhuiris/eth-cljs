(ns eth.main
  (:require [eth.net :as net]
            [eth.util :as util]
            [eth.api :as api]))

(defn start [build]
  (println build)
  (net/init))

