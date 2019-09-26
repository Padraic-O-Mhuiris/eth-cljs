(ns eth.browser
  (:require [eth.main :refer [start]]))

(def urls {:test "http://localhost:8545"
           :kovan "https://kovan.infura.io/v3/beabcde3dcb04fb0838ff6b3a1af9805"
           :main "https://mainnet.infura.io/v3/beabcde3dcb04fb0838ff6b3a1af9805"})

(def configuration {:url (:test urls)
                    :preset "test"})

(defn init []
  (start configuration))
