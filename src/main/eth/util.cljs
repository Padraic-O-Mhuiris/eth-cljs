(ns eth.util
  (:require
   [eth.db :as db]
   [cljs.reader :refer [read-string]]
   [bignumber.js :as bn]
   [eth-lib :refer [bytes]]))

(defn obj->map [obj]
  (js->clj obj :keywordize-keys true))

(defn channel?
  [x]
  (satisfies? cljs.core.async.impl.protocols/Channel x))

(defn error? [x]
  (or (instance? js/Error x)))
      ;;(= (keys x) [:action :topic :error])))

(defn hex? [s]
  (let [x (re-find #"^0x[a-fA-F0-9]*" s)]
    (= x s)))

(defn preset->net-id [name]
  ((keyword name) db/presets))

(defn string->number [s]
  (read-string s))

(defn string->hex [s]
  (.. bytes (fromString s)))

(defn ->bn [x]
  (bn. (or x 0)))
