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

(defn error? [x]
  (or (instance? js/Error x)
      (instance? (.-Error js/global) x)))

(defn channel?
  [x]
  (satisfies? cljs.core.async.impl.protocols/Channel x))

(defn hex? [s]
  (let [x (re-find #"^0x[a-fA-F0-9]*" s)]
    (= x s)))
