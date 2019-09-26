(ns eth.currency
  (:require [bignumber.core :as bnc :refer [fixed]]
            [eth.util :refer [->bn]]))

; -- currency units
(def ETH {:type "ETH" :symbol "Ξ" :decimals 18})
;;(def DAI {:name "DAI" :symbol "⬙" :erc20 true :decimals 18})

; -- currency denominations
(def WEI {:denom "wei" :precision 0})
(def GWEI {:denom "gwei" :precision 9})
(def UNIT {:denom "unit" :precision 18})

(defn round-to-precision [bn p]
  "returns a bignumber n which is fixed to p decimal places, 
   rounding all after the precision to 0"
  (->bn (bnc/fixed bn p 1)))

(defn process-amount
  [amount precision decimals]
  (round-to-precision (round-to-precision amount precision) decimals))

; TODO Add validation that wei can't exist when token is
; only 9 decimals
(defn build-currency
  [amount denom type]
  (merge {:amount (process-amount amount
                                  (:precision denom)
                                  (:decimals type))}
         denom
         type))

(defn bn->wei [amount type]
  (build-currency amount WEI type))

(defn bn->gwei [amount type]
  (build-currency amount GWEI type))

(defn bn->unit [amount type]
  (build-currency amount UNIT type))

(defn wei->gwei [c]
  (build-currency (bnc/* (:amount c) (bnc/pow 10 (- (:precision GWEI))))
                  GWEI
                  ETH))

(defn wei->unit [c]
  (build-currency (bnc/* (:amount c) (bnc/pow 10 (- (:precision UNIT))))
                  UNIT
                  ETH))

(defn gwei->wei [c]
  (build-currency (bnc/* (:amount c) (bnc/pow 10 (:precision GWEI)))
                  WEI
                  ETH))

(defn gwei->unit [c]
  (build-currency (bnc/* (:amount c) (bnc/pow 10 (- (:precision GWEI))))
                  UNIT
                  ETH))

(defn unit->wei [c]
  (build-currency (bnc/* (:amount c) (bnc/pow 10 (:precision UNIT)))
                  UNIT
                  ETH))

(defn unit->gwei [c]
  (build-currency (bnc/* (:amount c) (bnc/pow 10 (:precision GWEI)))
                  UNIT
                  ETH))

(defn stringify [c]
  (str (:symbol c)
       " "
       (:amount c)
       (if (= (:denom c) "unit")
         (str " " (:type c))
         (str " " (:denom c)))))
