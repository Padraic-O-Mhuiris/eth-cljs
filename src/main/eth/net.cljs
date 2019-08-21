(ns eth.net
  (:require
   [eth.db :as db]
   [eth.macros :refer [defeth]]
   [cljs-http.client :as http]
   [cljs.core.async :refer [chan]]))

(defn- build-rpc-opts
  [method xform & params]
  {:json-params {:jsonrpc "2.0"
                 :method method
                 :params (into [] (remove nil? params))
                 :id (:rpc-id @db/state)}
   :channel (chan 1 xform)})

(defn rpc 
  [method url & [xform & params]]
  (http/post url (build-rpc-opts method xform params)))
