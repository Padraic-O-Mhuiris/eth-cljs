(ns eth.net
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [eth.db :as db]
   [eth.util :refer [log error?]]
   [cljs-http.client :as http]
   [eth.macros :refer [<?]]
   [cljs.core.async :refer [<! >! chan]]))

(defn response-status-err [res]
  (js/Error (str "net - response code " (:status res))))

(defn response-msg-err [res]
  (js/Error (str "net - " (get-in res [:body :error :message]))))

(defn response-status-err?
  [res]
  (not (= (:status res) 200)))

(defn response-msg-err?
  [res]
  (contains? (:body res) :error))

(defn handle-rpc-error
  [res]
  (condp = true
    (response-status-err? res) (response-status-err res)
    (response-msg-err? res) (response-msg-err res)
    res))

(defn handle-rpc-success
  [res]
  (db/inc-rpc-id)
  (get-in res [:body :result]))

(def raw-response-chan
  (chan 1 (map handle-rpc-error)))

(def rpc-chan
  (chan 1 (map handle-rpc-success)))

(defn build-rpc-opts
  [method & params]
  {:json-params {:jsonrpc "2.0"
                 :method method
                 :params (into [] (remove nil? params))
                 :id (:rpc-id @db/state)}})

(defn rpc 
  [method & params]
  (go
    (->> (<! (http/post (:url @db/state) (build-rpc-opts method params)))
         (>! raw-response-chan))
    (try
      (let [res (<? raw-response-chan)]
        (>! rpc-chan res))
      (catch js/Error e
        (. js/console (error e))))))

(defn block-number []
  (go
    (rpc "eth_blockNumber")
    (int (<! rpc-chan))))

(defn net-listening? []
  (go
    (rpc "net_listening")
    (<! rpc-chan)))

(defn client-version []
  (go
    (rpc "web3_clientVersion")
    (<! rpc-chan)))

(defn init []
  (println "initialising net"))
