(ns eth.mock)

(def status-error
  "Error: net - response code 0")
(def message-error
  "Error: net - msg error")

(def success-response
  {:status 200
   :success true
   :body {:id 0
          :jsonrpc "2.0"
          :result "0x1"}
   :error-code :no-error
   :error-text ""})

(def message-error-response
  {:status 200
   :success true
   :body {:id 0
          :jsonrpc "2.0"
          :error {:message "msg error" :code -32000}}
   :error-code :no-error
   :error-text ""})

(def status-error-response
  {:status 0
   :success false
   :body nil
   :headers {}
   :error-code :http-error
   :error-text " [0]"})
