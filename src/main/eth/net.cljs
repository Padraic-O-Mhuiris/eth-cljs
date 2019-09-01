(ns eth.net
  (:require
   [eth.db :as db]
   [eth.macros :refer [defeth]]
   [eth.transducers :refer [check-http-status
                            check-http-message]]
   [cljs-http.client :as http]
   [cljs.core.async :refer [chan]]))

(def handle-http-request
  (comp check-http-status
        check-http-message))

(defn- build-rpc-opts
  [method xf params]
  {:with-credentials? false
   :json-params {:jsonrpc "2.0"
                 :method method
                 :params params
                 :id (:rpc-id @db/state)}
   :channel (chan 1 xf)})

(defn- build-http-transducers
  [xf]
  (if xf
    (comp handle-http-request
          xf)
    handle-http-request))

(defn rpc 
  [method url & [xf & params]]
  (http/post url (build-rpc-opts
                  method
                  (build-http-transducers xf)
                  (into [] (remove nil? params)))))
