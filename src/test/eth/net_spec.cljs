(ns eth.net-spec
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer (deftest is async testing)]
            [eth.net :as net]
            [eth.db :as db]
            [eth.util :refer [error?]]
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
          :result "0x1"},
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

(deftest handle-rpc-success-test
  (testing "net/handle-rpc-success should"
    (let [id1 (:rpc-id @db/state)
          res1 (net/handle-rpc-success mock-success-response)]
      (testing "return response result from body if input success"
        (is (= res1 "0x1")))
      (testing "increment state rpc-id if input success"))))

(deftest raw-response-chan-test
  (async done
         (go
           (testing "net/raw-response-chan should"
             (testing "be a channel"
               (is (channel? net/raw-response-chan)))
             (testing "passthrough successful http responses"
               (>! net/raw-response-chan mock-success-response)
               (is (not (error? (<! net/raw-response-chan)))))
             (testing "transduce erroneous http responses as error objects"
               (>! net/raw-response-chan mock-err-status-response)
               (is (error? (<! net/raw-response-chan)))
               (>! net/raw-response-chan mock-err-msg-response)
               (is (error? (<! net/raw-response-chan))))
             (done)))))

(deftest rpc-chan-test
  (async done
         (go
           (testing "net/rpc-chan should"
             (testing "be a channel"
               (is (channel? net/rpc-chan)))
             (testing "return result from http responses"
               (>! net/rpc-chan mock-success-response)
               (is (= "0x1" (<! net/rpc-chan))))
             (done)))))
