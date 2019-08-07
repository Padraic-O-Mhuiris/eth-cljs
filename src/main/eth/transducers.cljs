(ns eth.transducers
  (:require
   [eth.errors :as errors]
   [eth.db :as db]))

;; not actually transducers but should be revised later
;; ----------------------------------
(defn response->error?
  [res]
  (condp = true
    (errors/status? res) (errors/response->status res)
    (errors/message? res) (errors/response->message res)
    res))

(defn response->result
  [res]
  (db/inc-rpc-id)
  (get-in res [:body :result]))
;; ----------------------------------
