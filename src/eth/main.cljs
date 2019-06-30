(ns eth.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def ganache-url "http://localhost:8545")

(defn start [build]
  (println "using build" build))

(defn initialize-provider []
  ;; connect to blockchain
  (println "connect to blockchain")
  )

(defn getBlockNumber
  []
  (go (let [response (<! (http/post ganache-url
                                    {:json-params {:jsonrpc "2.0"
                                                   :method "eth_blockNumber"
                                                   :params []
                                                   :id "3"}}))]
        (println (:result (:body response)))))
  )
