(ns eth.transducers-spec
  (:require
   [cljs.test :refer (deftest is testing)]
   [eth.db :as db]
   [eth.transducers :as transducers]
   [eth.mock :as mock]
   [eth.util :refer [error?]]))

(deftest response->error?-test
  (testing "transducers/response->error? should"
    (let [res1 (transducers/response->error? mock/success-response)
          res2 (transducers/response->error? mock/status-error-response)
          res3 (transducers/response->error? mock/message-error-response)]
      (testing "return response if success"
        (is (= res1 mock/success-response)))
      (testing "return status error if response status does not equal 200"
        (is (error? res2))
        (is (= (str res2) mock/status-error)))
      (testing "return msg error if response body contains error"
        (is (error? res3))
        (is (= (str res3) mock/message-error))))))

(deftest response->result
  (testing "transducers/response->result should"
    (let [id (:rpc-id @db/state)
          res (transducers/response->result mock/success-response)]
      (testing "return response result from body"
        (is (= res "0x1")))
      (testing "increment state rpc-id"
        (is (= (+ id 1) (:rpc-id @db/state)))))))
