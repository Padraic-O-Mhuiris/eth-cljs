(ns eth.util
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [<!]]))

(defn log [s]
  (println (str s)))

(defn log-chan
  [f]
  (go
    (log (<! (f)))))
