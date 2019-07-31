(ns eth.macros
  (:require-macros [eth.macros]))

(defn throw-err [e]
 (when (instance? js/Error e) (throw e))
  e)
