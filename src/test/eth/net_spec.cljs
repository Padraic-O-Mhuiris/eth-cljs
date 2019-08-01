(ns eth.net-spec
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer (deftest is async testing)]
            [eth.net :as net]
            [cljs.core.async :refer [<! >!]]))

(defn channel?
  [x]
  (satisfies? cljs.core.async.impl.protocols/Channel x))

(def err-status
  "Error: net - response code 0")
(def err-msg
  "Error: net - msg error")

(def mock-success-response
  {:status 200
   :success true,
   :body {:id 0,
          :jsonrpc "2.0",
          :result "1"},
   :error-code :no-error,
   :error-text ""})

(def mock-err-msg-response
  {:status 200,
   :success true,
   :body {:id 0,
          :jsonrpc "2.0",
          :error {:message "msg error", :code -32000,}}
   :error-code :no-error,
   :error-text ""})

(def mock-err-status-response
  {:status 0,
   :success false,
   :body nil,
   :headers {},
   :error-code :http-error,
   :error-text " [0]"})

(defn error? [x]
  (or (instance? js/Error x)
      (instance? (.-Error js/global) x)))

(defn juxtapose?
  [a b]
  (= (str a) (str b)))

(deftest response-status-err-test
  (testing "net/response-status-err should"
    (let [err (net/response-status-err mock-err-status-response)]
      (testing "return error object"
        (is (error? err)))
      (testing "prints correct message"
        (is (juxtapose? err err-status))))))

(deftest response-msg-err-test
  (testing "net/response-msg-err should"
    (let [err (net/response-msg-err mock-err-msg-response)]
      (testing "return error object"
        (is (error? err)))
      (testing "prints correct message"
        (is (juxtapose? err err-msg))))))

(deftest response-status-err?-test
  (testing "net/response-status-err? should"
    (let [res1 (net/response-status-err? mock-success-response)
          res2 (net/response-status-err? mock-err-status-response)]
      (testing "return false for status 200"
        (is (false? res1)))
      (testing "return true for non-200 status code"
        (is (true? res2))))))

(deftest response-msg-err?-test
  (testing "net/response-msg-err? should"
    (let [res1 (net/response-msg-err? mock-success-response)
          res2 (net/response-msg-err? mock-err-msg-response)]
      (testing "return false if no :error key exists in message body"
        (is (false? res1)))
      (testing "return true for :error key exists in message body"
        (is (true? res2))))))

(deftest handle-rpc-error-test
  (testing "net/handle-rpc-error should"
    (let [res1 (net/handle-rpc-error mock-success-response)
          res2 (net/handle-rpc-error mock-err-status-response)
          res3 (net/handle-rpc-error mock-err-msg-response)]
      (testing "return response if success"
        (is (= res1 mock-success-response)))
      (testing "return status error if response status does not equal 200"
        (is (error? res2))
        (is (juxtapose? res2 err-status)))
      (testing "return msg error if response body contains error"
        (is (error? res3))
        (is (juxtapose? res3 err-msg))))))
;; (deftest rpc-chan-test
;;   (async done
;;          (go
;;            (testing "net/rpc-chan"
;;              (let [old-id @net/rpc-id]
;;                (>! net/rpc-chan {:body {:result "123"}})
;;                (testing "should pull result from response"
;;                  (is (= (<! net/rpc-chan) "123")))
;;                (testing "should increment rpc-id"
;;                  (is (= (+ old-id 1) @net/rpc-id)))
;;                (done))))))

;; (deftest build-rpc-opts-test
;;   (testing "net/build-rpc-opts should"
;;     (let [opts-1 (net/build-rpc-opts "fake-method")
;;           opts-2 (net/build-rpc-opts "fake-method" 1 2 3)
;;           opts-3 (net/build-rpc-opts "fake-method" nil)]
;;       (testing "return correct json-params calling single method"
;;         (is (= (get-in opts-1 [:json-params :jsonrpc]) "2.0"))
;;         (is (= (get-in opts-1 [:json-params :params]) []))
;;         (is (= (get-in opts-1 [:json-params :id])) @net/rpc-id))
;;       (testing "return correct json-params calling method + params"
;;         (is (= (get-in opts-2 [:json-params :jsonrpc]) "2.0"))
;;         (is (= (get-in opts-2 [:json-params :params]) [1 2 3]))
;;         (is (= (get-in opts-2 [:json-params :id]) @net/rpc-id)))
;;       (testing "return correct json-params calling method + nil param"
;;         (is (= (get-in opts-3 [:json-params :jsonrpc]) "2.0"))
;;         (is (= (get-in opts-3 [:json-params :params]) []))
;;         (is (= (get-in opts-3 [:json-params :id]) @net/rpc-id)))
;;       (testing "return a channel in both cases"
;;         (is (channel? (:channel opts-1)))
;;         (is (channel? (:channel opts-2)))
;;         (is (channel? (:channel opts-3)))))))
