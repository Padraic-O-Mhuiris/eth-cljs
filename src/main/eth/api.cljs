(ns eth.api
  (:require
   [eth.db :as db]
   [eth.net :refer [rpc]]
   [eth.macros :refer [defeth]]
   [eth.transducers :refer [check-http-status
                            check-http-message
                            convert-integer]]))
(def handle-http-request
  (comp check-http-status
        check-http-message))

(defeth block-number []
  (rpc "eth_blockNumber" (db/url) (comp handle-http-request
                                        convert-integer)))

(defeth net-listening? []
  (rpc "net_listening" (db/url) handle-http-request))

(defeth network-id []
  (rpc "net_version" (db/url) (comp handle-http-request
                                    convert-integer)))
;; (defn client-version []
;;   (go
;;     (rpc "web3_clientVersion")
;;     (<! chan/result)))

;; (defn fake-call []
;;   (go
;;     (rpc "xoxox")
;;     (<! chan/result)))
