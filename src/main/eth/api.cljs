(ns eth.api
  (:require
   [eth.db :as db]
   [eth.net :refer [rpc]]
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

(defn sign )
