(ns eth.main
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:require [eth.net :as net]
            [eth.util :as util]
            [eth.api :as api]
            [eth.db :as db]
            [eth.errors :as errors :refer [action? build-error-msg]]
            [eth.logger :refer [log]]
            [eth.channels :refer [error-chan]]
            [cljs.core.async :refer [<! >!]]))

;; (defn validate-config []
;;   (go
;;     (let [id (int (<! (api/network-id)))]
;;       (if (= id (util/preset->net-id (db/preset)))
;;         (db/initialize!)
;;         (>! chan/errors (errors/contradicting-url-network id))))))

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
  (error-loop)
  (db/init config)
  (go
    (.log js/console (<! (api/block-number)))
    (.log js/console (<! (api/net-listening?)))
    (.log js/console (<! (api/network-id)))))
