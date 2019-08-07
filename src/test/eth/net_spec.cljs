(ns eth.net-spec
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [eth.net :as net]
   [eth.db :as db]
   [eth.channels :as chan]
   [cljs.test :refer (deftest is async testing)]
   [eth.util :refer [error? channel? hex?]]
   [cljs.core.async :refer [<! >!]]))

(defn test-build-rpc-opts-format []
  (let [opts1 (net/build-rpc-opts "abc123")
        opts-params (:json-params opts1)]
    (testing "builds edn format correctly"
      (is (= (keys opts1) '(:json-params)))
      (is (= (keys (:json-params opts1)) '(:jsonrpc :method :params :id))))
    (testing "generates expected values"
      (is (= (:jsonrpc opts-params) "2.0"))
      (is (= (:method opts-params) "abc123"))
      (is (= (:params opts-params) []))
      (is (number? (:id opts-params))))))

(defn test-build-rpc-opts-params []
  (let [opts2 (:json-params (net/build-rpc-opts "abc123" nil))
        opts3 (:json-params (net/build-rpc-opts "abc123" 1))
        opts4 (:json-params (net/build-rpc-opts "abc123" 1 2 3))]
    (testing "filter out nil params and create empty vector for :param"
      (is (= (:params opts2) [])))
    (testing "create vector of length 1 from 2nd argument for :param"
      (is (= (:params opts3) [1])))
    (testing "create vector of n length from method + n args for :param"
      (is (= (:params opts4) [1 2 3])))))

(defn test-build-rpc-opts-id []
  (let [rpc-id1 (:rpc-id @db/state)
        opts-id1 (get-in (net/build-rpc-opts "abc123") [:json-params :id])
        rpc-id2 (do (db/inc-rpc-id)
                    (:rpc-id @db/state))
        opts-id2 (get-in (net/build-rpc-opts "abc123") [:json-params :id])]
    (testing ":id should inherit from rpc-id of state"
      (is (= rpc-id1 opts-id1))
      (is (= rpc-id2 opts-id2)))))

(deftest build-rpc-opts-test
  (testing "net/build-rpc-opts should"
    (test-build-rpc-opts-format)
    (test-build-rpc-opts-params)
    (test-build-rpc-opts-id)))

(defn test-rpc-node-success []
  (go
    (testing "make request to ganache node and get result from result channel"
      (net/rpc "eth_blockNumber")
      (is (hex? (<! chan/result)))
      (net/rpc "net_listening")
      (is (true? (<! chan/result))))))

(defn test-rpc-node-failure []
  (go
    (testing "make request to non-existent node and catch error")))

(deftest rpc-test
  (async done
         (testing "net/rpc should"
           (test-rpc-node-success))
         (done)))
