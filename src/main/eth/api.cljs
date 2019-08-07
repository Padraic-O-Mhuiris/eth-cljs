(ns eth.api
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [eth.net :refer [rpc]]
   [eth.channels :as chan]
   [cljs.core.async :refer [<!]]))

(defn block-number []
  (go
    (rpc "eth_blockNumber")
    (int (<! chan/result))))

(defn net-listening? []
  (go
    (rpc "net_listening")
    (<! chan/result)))

(defn client-version []
  (go
    (rpc "web3_clientVersion")
    (<! chan/result)))
