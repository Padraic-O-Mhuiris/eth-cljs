(ns eth.channels
  (:require
   [cljs.core.async :refer [chan]]))

(def error-chan (chan 1))
