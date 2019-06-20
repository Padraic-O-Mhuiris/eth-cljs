(ns eth.npm)

(def value-a 1)

(defonce value-b 2)

(defn reload []
  (println "Code updated.")
  (println "Trying values:" value-a value-b))

(defn init [x]
  (println "npm loaded!")
  (+ 1 2 x))
