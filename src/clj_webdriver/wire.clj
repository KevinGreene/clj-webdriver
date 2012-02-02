;; JsonWireProtocol work
(ns clj-webdriver.wire
  (:use [cheshire.core :only [parse-string]])
  (:require [clj-http.client :as client])
  (:import [clj_webdriver.remote_server.RemoteServer]))

(def default-wd-url "http://localhost:3001/wd/")

(defn parse-body
  "Body comes back as JSON per protocol. Parse it."
  [resp]
  (parse-string (:body resp)))

(defprotocol IWire
  "JsonWireProtocol implemented in Clojure"
  (execute [server commands])
  (status [server]))

;; this should probably be initially defined in remote.clj
;; with a protocol for functions relating to starting/stopping,
;; in addition to IWire protocol here.
(extend-type RemoteServer
  
  IWire
  (execute [server commands]
    (let [commands (if-not (vector? commands)
                    (vector commands)
                    commands)
          resp (client/get (str (:address server)
                                (apply str (interpose "/" commands))))
          body (parse-body resp)]
      (assoc resp :body body)))

  (status [server]
    (execute server ["status"])))

