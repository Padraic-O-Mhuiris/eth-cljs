(ns eth.main
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<! chan close!]]))

(def ganache-url "http://localhost:8545")
(def eth-block-number {:jsonrpc "2.0"
                       :method "eth_blockNumber"
                       :params []
                       :id "3"})

(def state (atom 0))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(defn poll-block-numbers  []
  (go-loop []
    (<! (timeout 2000))
    (let [response (<! (http/post ganache-url {:json-params eth-block-number}))]
      (reset! state (int (:result (:body response)))))
    (recur)))

(defn initialize-connection []
  (poll-block-numbers))

(defn start [build]
  (println "using build" build))
