(ns eth.transducers
  (:require
   [eth.errors :as errors]
   [eth.util :refer [error?]]
   [eth.db :as db]
   [eth.channels :refer [error-chan]]
   [cljs.core.async :refer [put!]]))

;; (def check-http-status
;;   (map (fn [input]
;;          (if (= (:status input) 200)
;;            input
;;            (errors/response->status input)))))

(def check-http-status
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (if (= (:status input) 200)
         (rf result input)
         (do
           (put! error-chan (errors/http-status input))
           result))))))

(def check-http-message
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (if (contains? (:body input) :error)
         (do
           (put! error-chan (errors/http-message input))
           result)
         (rf result (get-in input [:body :result])))))))

(def convert-integer
  (map int))
