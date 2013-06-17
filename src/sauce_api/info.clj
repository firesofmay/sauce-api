(ns ^{:doc "Functions to get public information related to Sauce."
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.info
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn- get-browser-list
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs.
   browser-list -> :all, :selenium-rc, :webdriver"
  [browser-list]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "info/browsers/" (name browser-list)))))

(defn get-browser-list-all
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs."
  []
  (get-browser-list :all))

(defn get-browser-list-selenium-rc
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs for selenium-rc."
  []
  (get-browser-list :selenium-rc))

(defn get-browser-list-webdriver
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs for webdriver."
  []
  (get-browser-list :webdriver))

(defn get-sauce-status
  "Checks if sauce Labs service is working okay or not.
   Returns a map of:
   wait_time, service_operational, status_message"
  []
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "info/status"))))

(defn get-sauce-up?
  "Returns a boolean if sauce is up and running or not."
  []
  ((utils/get-body (clj-httpe/clj-get (str utils/base-url "info/status"))) "service_operational"))

(defn get-test-counter
  "Gets the counter of tests executed so far on sauce-labs"
  []
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "info/counter"))))
