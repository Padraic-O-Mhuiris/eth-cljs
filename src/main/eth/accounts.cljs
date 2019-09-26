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

(defn sign [acc data]
  (when (hex? data)
    (let [hash (-> (str preamble (count data))
                   (#(.. bytes (fromString %)))
                   (#(.. bytes (concat % data)))
                   (#(.. hash (keccak256 %))))
          sig (.. account (sign (:privateKey acc) hash))
          vrs (js->clj (.. account (decodeSignature sig)))]
      {:message data
       :messageHash hash
       :v (nth vrs 0)
       :r (nth vrs 1)
       :s (nth vrs 2)
       :signature sig})))
