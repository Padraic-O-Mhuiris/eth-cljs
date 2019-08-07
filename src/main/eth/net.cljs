(ns eth.net
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [eth.db :as db]
   [eth.channels :as chan]
   [cljs-http.client :as http]
   [eth.macros :refer [<?]]
   [cljs.core.async :refer [<! >!]]))

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
         (>! chan/http))
    (try
      (let [res (<? chan/http)]
        (>! chan/result res))
      (catch js/Error e
        (. js/console (error e))))))

(defn init []
  (println "initialising net"))
