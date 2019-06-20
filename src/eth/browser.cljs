(ns eth.browser)

(def value-a 1)

(defonce value-b 2)

(defn reload []
  (println "Code updated.")
  (println "Trying values:" value-a value-b))

(defn init []
  (println "Browser loaded!"))
