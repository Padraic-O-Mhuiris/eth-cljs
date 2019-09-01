(ns eth.main
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:require [eth.net :as net]
            [eth.util :as util]
            [eth.api :as api]
            [eth.currency :as c]
            [eth.db :as db]
            [eth.errors :as errors :refer [action? build-error-msg]]
            [eth.logger :refer [log]]
            [eth.channels :refer [error-chan]]
            [cljs.core.async :refer [<! put!]]
            [cljs.pprint :refer [pprint]]))

(def x-address "0x6a7d7fe019737ae337a20344b9c6dd00725b66bc")

(defn validate-config []
  (go
    (let [id (<! (api/network-id))
          client (<! (api/client-version))]
      (if (= id (util/preset->net-id (db/preset)))
        (db/update-state! {:initialized true
                           :client client})
        (put! error-chan (errors/contradicting-url-network id))))))

(defn error-loop []
  (go-loop []
    (let [err (<! error-chan)]
      (condp = true
        (action? :WARN err) (.warn js/console (build-error-msg err))
        (action? :MISTAKE err) (.error js/console (build-error-msg err))
        (action? :FAILURE err) (throw (str (build-error-msg err)))
        (do
          (.log js/console "Error was not processed")
          (throw err))))
    (recur)))

(defn start [config]
  (go
    (error-loop)
    (db/init config)
    (<! (validate-config))
    (.log js/console (<! (api/block-number)))
    (.log js/console (<! (api/net-listening?)))
    (.log js/console (<! (api/network-id)))
    (.log js/console (<! (api/client-version)))
    (.log js/console (<! (api/peer-count)))
    (.log js/console (c/stringify (<! (api/gas-price))))
    (.log js/console (c/stringify (<! (api/eth-balance x-address))))
    (.log js/console (<! (api/block)))))
