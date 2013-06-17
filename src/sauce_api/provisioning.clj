(ns ^{:doc "Provisioning related functions"
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.provisioning
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn get-account-limits
  "Check account limits in terms of concurrency of tests."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/limits")
                                     {:basic-auth [usr access-key]})))

(defn get-account-details
  "Get accounts details:
   access-key, minutes, id, subscribed and can_run_manual values."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "users/" usr)
                                     {:basic-auth [usr access-key]})))
