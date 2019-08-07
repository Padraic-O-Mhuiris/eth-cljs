(ns eth.errors)

(defn response->status [res]
  (js/Error (str "net - response code " (:status res))))

(defn response->message [res]
  (js/Error (str "net - " (get-in res [:body :error :message]))))

(defn status?
  [res]
  (not (= (:status res) 200)))

(defn message?
  [res]
  (contains? (:body res) :error))
