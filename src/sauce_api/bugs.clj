(ns ^{:doc "Bugs tracking related functions"
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.bugs
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn get-types
  "Get list of available bug types."
  []
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "bugs/types"))))

(defn get-type-desc
  "Get description of each field for a particular bug type."
  [id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "bugs/types/" id))))

(defn get-id-info
  "Get detailed info for a particular bug."
  [usr access-key id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/bugs/detail/" id)
                                     {:basic-auth [usr access-key]})))
