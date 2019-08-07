(ns eth.errors-spec
  (:require
   [cljs.test :refer (deftest is testing)]
   [eth.errors :as errors]
   [eth.mock :as mock]
   [eth.util :refer [error?]]))

(deftest response->status-test
  (testing "errors/response->status should"
    (let [err (errors/response->status mock/status-error-response)]
      (testing "return error object"
        (is (error? err)))
      (testing "prints correct message"
        (is (= (str err) mock/status-error))))))

(deftest response->msg
  (testing "errors/response->msg should"
    (let [err (errors/response->message mock/message-error-response)]
      (testing "return error object"
        (is (error? err)))
      (testing "prints correct message"
        (is (= (str err) mock/message-error))))))

(deftest status?-test
  (testing "errors/status? should"
    (let [res1 (errors/status? mock/success-response)
          res2 (errors/status? mock/status-error-response)]
      (testing "return false for status 200"
        (is (false? res1)))
      (testing "return true for non-200 status code"
        (is (true? res2))))))

(deftest message?-test
  (testing "errors/message? should"
    (let [res1 (errors/message? mock/success-response)
          res2 (errors/message? mock/message-error-response)]
      (testing "return false if no :error key exists in message body"
        (is (false? res1)))
      (testing "return true for :error key exists in message body"
        (is (true? res2))))))
