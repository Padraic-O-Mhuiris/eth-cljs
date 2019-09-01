(ns eth.errors
  (:require [eth.db :as db]
            [eth.util :as util]))

(def actions
  "WARNING - This is a low level error which is not a problem for the library to
  recover from. Example: rpc-id returned from a request did not match/conflicted
  with the expected result. Notify the user with console.warn

  MISTAKE - This action should behave as a notification to the user that a non-
  fatal error has occurred. Example: Calling an rpc method which does not exist
  should notify the user the resulting error message from the node. However, it
  should not prevent the application making further rpc requests.

  FAILURE - This is classified as a critical error which is deemed to be
  unrecoverable without user intervention. Example: Initialising the application
  with an incorrect rpc url will cause most of the application not to function
  correctly. Therefore an error should be thrown and the process ended"
  {:WARN "WARNING"
   :MISTAKE "MISTAKE"
   :FAILURE "FAILURE"})

(def topics
  {:CONFIG "configuration"
   :NET "eth.net"
   :DATABASE "eth.db"})

(defn detect-client-version [input]
  {:action (:WARN actions)
   :topic (:DATABASE topics)
   :error (str "Could not detect client type for node at "
               (db/url)
               ". Response was: "
               input)})

(defn system-uninitialized []
  {:action (:MISTAKE actions)
   :topic (:CONFIG topics)
   :error "Attempted to call ethereum function before client is initialised"})

(defn contradicting-url-network [id]
  {:action (:FAILURE actions)
   :topic (:CONFIG topics)
   :error (str "The node at "
               (if (zero? (count (db/url)))
                 "<NIL>"
                 (db/url))
               " responded with a network id: "
               (if (number? id)
                 id
                 "<NIL>")
               " which conflicts with expected "
               (db/preset)
               " network id: "
               (util/preset->net-id (db/preset)))})

(defn http-status [res]
  {:action (:FAILURE actions)
   :topic (:NET topics)
   :error (str "Response code "
               (:status res)
               ". Is the node active?")})

(defn http-message-error [res]
  {:action (:MISTAKE actions)
   :topic (:NET topics)
   :error (get-in res [:body :error :message])})

(defn http-no-result [res]
  {:action (:MISTAKE actions)
   :topic (:NET topics)
   :error "No result contained in body of response"})

(defn response->id [res]
  {:action (:WARN actions)
   :topic (:NET topics)
   :error "The JSON-RPC id sent from the chain client did not respond with the expected id"})

(defn matching-id?
  [res]
  (not (= (:rpc-id @db/state) (get-in res [:body :id]))))

(defn action? [a err]
  (= (:action err) (a actions)))

(defn build-error-msg [e]
  (str (:topic e)
       " - "
       (:error e)))
