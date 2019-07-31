(ns eth.db)

(def init-state {:url "http://localhost:8545"
                 :rpc-id 0})

(def state (atom init-state))

(defn inc-rpc-id []
  (swap! state update-in [:rpc-id] inc))
