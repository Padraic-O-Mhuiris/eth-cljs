(ns eth.api
  (:require
   [eth.db :as db]
   [eth.net :refer [rpc]]
   [eth-lib :refer [bytes]]
   [eth.logger :refer [log]]
   [eth.util :refer [hex?]]
   [eth.transducers :refer [check-http-status
                            check-http-message
                            convert-number
                            convert-bn
                            convert-wei
                            convert-gwei
                            convert-unit
                            infer-client-name]]))

(def LATEST "latest")

(defn block-number []
  (rpc "eth_blockNumber" (db/url) convert-number))

(defn net-listening? []
  (rpc "net_listening" (db/url)))

(defn network-id []
  (rpc "net_version" (db/url) convert-number))

(defn client-version []
  (rpc "web3_clientVersion" (db/url) infer-client-name))

(defn peer-count []
  (rpc "net_peerCount" (db/url) convert-number))

(defn gas-price []
  (rpc "eth_gasPrice" (db/url) (comp convert-bn
                                     convert-gwei)))

(defn eth-balance
  ([address]
   (eth-balance address LATEST))
  ([address block]
   (rpc "eth_getBalance"
        (db/url)
        (comp convert-bn
              convert-unit)
        address
        block)))

(defn block
  ([] (block LATEST true))
  ([number full-tx]
   (rpc "eth_getBlockByNumber" (db/url) nil number full-tx)))

(def eth-node-keys
  {:privateKey "0x2d7bdb58c65480ac5aee00b20d3558fb18a916810d298ed97174cc01bb809cdd"
   :address "0x9575eb2a7804c43f68dc7998eb0f250832df9f10"})

(defn sign [address message]
  (let [hash (if (hex? message)
               message
               (.. bytes (fromAscii message)))]
    (rpc "eth_sign" (db/url) nil address hash)))
