(ns eth.db)

(def presets {:main 1
              :kovan 42
              :test 999})

(def clients [{:name "ganache" :regex #"TestRPC"}
              {:name "parity" :regex #"Parity"}
              {:name "geth" :regex #"Geth"}])

(def init-state {:config {:url ""
                          :preset ""}
                 :initialized false
                 :rpc-id 0
                 :client ""})

(def state (atom init-state))

(defn- update-state! [params]
  (swap! state (fn [old new] (merge old new)) params))

(defn inc-rpc-id! []
  (swap! state update-in [:rpc-id] inc))

(defn revert! []
  (reset! state init-state))

(defn initialized? []
  (get-in @state [:initialized]))

(defn url []
  (get-in @state [:config :url]))

(defn preset []
  (get-in @state [:config :preset]))

(defn set-client! [client]
  (swap! state assoc-in [:client] client))

(defn init [config]
  (swap! state assoc-in [:config] config))
