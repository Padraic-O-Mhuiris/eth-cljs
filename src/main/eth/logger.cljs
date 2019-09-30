(ns eth.logger
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [eth.util :refer [error? channel?]]
   [cljs.core.async :refer [<!]]))

(defn -log [s]
  (println)
  (println s))

(defn -log-error [e]
  (. js/console (error e)))

(defn log
  [f]
  (go
    (cond
      (channel? f) (-log (<! f))
      (error? f) (-log-error f)
      :ELSE (-log f))))
