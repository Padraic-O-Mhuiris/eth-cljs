(ns eth.net-spec
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer (deftest is async testing)]
            [eth.net :as net]
            [cljs.core.async :refer [<! >!]]))

(defn channel?
  [x]
  (satisfies? cljs.core.async.impl.protocols/Channel x))

(def err-status-code
  "Error: net - response code 0")
(def err-res-msg
  "Error: net - msg error")

(defn error? [x]
  (or (instance? js/Error x)
      (instance? (.-Error js/global) x)))

(deftest response-status-err-test
  (testing "net/response-status-err should"
    (let [err (net/response-status-err {:status 0})]
      (testing "return error object"
        (is (error? err)))
      (testing "prints correct message"
        (is (= (str err) err-status-code))))))

(deftest response-msg-err-test
  (testing "net/response-msg-err should"
    (let [err (net/response-msg-err
               {:body
                {:error
                 {:message "msg error"}}})]
      (testing "return error object"
        (is (error? err)))
      (testing "prints correct message"
        (is (= (str err) err-res-msg))))))

(deftest response-status-err?-test
  (testing "net/response-status-err? should"
    (let [res1 (net/response-status-err? {:status 200})
          res2 (net/response-status-err? {:status 1})]
      (testing "return false for status 200"
        (is (false? res1)))
      (testing "return true for non-200 status code"
        (is (true? res2))))))

(deftest response-msg-err?-test
  (testing "net/response-msg-err? should"
    (let [res1 (net/response-msg-err? {:body {:status ""}})
          res2 (net/response-msg-err? {:body {:error ""}})]
      (testing "return false if no :error key exists in message body"
        (is (false? res1)))
      (testing "return true for :error key exists in message body"
        (is (true? res2))))))

(deftest handle-rpc-error-test
  (testing "net/handle-rpc-error should"
    ))
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
