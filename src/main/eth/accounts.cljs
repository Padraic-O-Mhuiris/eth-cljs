(ns eth.accounts
  (:require [eth-lib :refer [account bytes hash]]
            [eth.util :refer [obj->map
                              hex?]]))

(.log js/console eth-lib)

(def preamble "\u0019Ethereum Signed Message:\n")

(defn create
  ([]
   (create nil))
  ([entropy]
   (obj->map (.. account (create entropy)))))

(defn from-private [pvk]
  (obj->map (.. account (fromPrivate pvk))))

(def eth-node-keys
  {:privateKey "0x2d7bdb58c65480ac5aee00b20d3558fb18a916810d298ed97174cc01bb809cdd"
   :address "0x9575eb2a7804c43f68dc7998eb0f250832df9f10"})

(def exkey
  {:address "0x2c7536E3605D9C16a7a3D7b1898e529396a65c23",
   :privateKey "0x4c0883a69102937d6231471b5dbb6204fe5129617082792ae468d01a3f362318"})

(defn sign [acc data]
  (let [hexed-data (if (hex? data)
                     data
                     (.. bytes (fromAscii data)))
        messageHash (-> (str preamble (count data))
                        (#(.. bytes (fromString %)))
                        (#(.. bytes (concat % hexed-data)))
                        (#(.. hash (keccak256s %))))
        sig (.. account (sign messageHash (:privateKey acc)))
        vrs (js->clj (.. account (decodeSignature sig)))]
    {:message data
     :messageHash messageHash
     :v (nth vrs 0)
     :r (nth vrs 1)
     :s (nth vrs 2)
     :signature sig}))
