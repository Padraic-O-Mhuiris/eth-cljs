(ns eth.db)

(def presets {:main 1
              :kovan 42
              :test 999})

(def init-state {:config {:url ""
                          :preset ""}
                 :initialized false
                 :rpc-id 0})

(def state (atom init-state))

(defn inc-rpc-id! []
  (swap! state update-in [:rpc-id] inc))

(defn initialize! []
  (swap! state assoc :initialized true))

(defn initialized? []
  (get-in @state [:initialized]))

(defn url []
  (get-in @state [:config :url]))

(defn preset []
  (get-in @state [:config :preset]))

(defn init [config]
  (swap! state assoc-in [:config] config)
  (initialize!)
  )
