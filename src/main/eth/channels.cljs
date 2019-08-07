(ns eth.channels
  (:require
   [cljs.core.async :refer [chan]]
   [eth.transducers :refer [response->error?
                            response->result]]))

(def http
  (chan 1 (map response->error?)))

(def result
  (chan 1 (map response->result)))
