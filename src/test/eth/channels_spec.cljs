(ns eth.channels-spec
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [eth.channels :as chan]
   [cljs.test :refer (deftest is async testing)]
   [eth.util :refer [error? channel?]]
   [cljs.core.async :refer [<! >!]]
   [eth.mock :as mock]))

(deftest http-test
  (async done
         (go
           (testing "channels/http should"
             (testing "be a channel"
               (is (channel? chan/http)))
             (testing "passthrough successful http responses"
               (>! chan/http mock/success-response)
               (is (not (error? (<! chan/http)))))
             (testing "transduce erroneous http responses as error objects"
               (>! chan/http mock/status-error-response)
               (is (error? (<! chan/http)))
               (>! chan/http mock/message-error-response)
               (is (error? (<! chan/http))))
             (done)))))

(deftest result-test
  (async done
         (go
           (testing "channels/result should"
             (testing "be a channel"
               (is (channel? chan/result)))
             (testing "return result from http responses"
               (>! chan/result mock/success-response)
               (is (= "0x1" (<! chan/result))))
             (done)))))
