(ns eth.transducers
  (:require
   [eth.errors :as errors]
   [eth.util :refer [error?
                     string->number
                     ->bn]]
   [eth.db :as db]
   [eth.channels :refer [error-chan]]
   [cljs.core.async :refer [put!]]
   [eth.currency :as currency :refer [ETH]]))

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
           (put! error-chan (errors/http-message-error input))
           result)
         (if (contains? (:body input) :result)
           (rf result (get-in input [:body :result]))
           (do
             (put! error-chan (errors/http-no-result input))
             result)))))))

(def convert-number
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (if (number? input)
         (rf result input)
         (rf result (string->number input)))))))

(def convert-bn
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (rf result (->bn input))))))

(def convert-wei
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (rf result (currency/bn->wei input ETH))))))

(def convert-gwei
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (rf result (-> (currency/bn->wei input ETH)
                      (currency/wei->gwei)))))))

(def convert-unit
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (rf result (-> (currency/bn->wei input ETH)
                      (currency/wei->unit)))))))

(def infer-client-name
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (let [re-matches (fn [x y]
                          (not (nil? (re-find (:regex x) y))))
             match (:name
                    (into {}
                          (for [client db/clients
                                :when (re-matches client input)]
                            client)))]
         (if (nil? match)
           (do
             (put! error-chan (errors/detect-client-version input))
             result)
           (rf result match)))))))
