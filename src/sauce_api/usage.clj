(ns ^{:doc "Usage related functions."
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.usage
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn get-current-activity
  "Access current account activity.
   Returns active job counts broken down by job status and subaccount."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/activity")
                                     {:basic-auth [usr access-key]})))

(defn get-historical-data
  "Returns array of ['YYYY-MM-DD', [<jobs>, <seconds>]] pairs for each day that had activity."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "users/" usr "/usage")
                                     {:basic-auth [usr access-key]})))
