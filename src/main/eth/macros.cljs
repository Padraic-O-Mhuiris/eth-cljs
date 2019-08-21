(ns eth.macros
  (:require-macros [eth.macros])
  (:require [cljs.core.async :refer [put!]]
            [eth.db :as db]
            [eth.errors :as errors]
            [eth.channels :refer [error-chan]]))
            
(defn system-check [body-fn]
  (if (db/initialized?)
    body-fn
    (put! error-chan (errors/system-uninitialized))))
