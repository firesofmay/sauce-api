(ns ^{:doc "Tunnel related functions"
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.tunnels
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn get-all-ids
  "Get ids of all the active tunnels."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/tunnels")
                                     {:basic-auth [usr access-key]})))

(defn get-id-info
  "Get info on a given id."
  [usr access-key tunnel-id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/tunnels/" tunnel-id)
                                     {:basic-auth [usr access-key]})))

(defn delete-id
  "Deletes a running tunnel session."
  [usr access-key tunnel-id]
  (utils/get-body (clj-httpe/clj-delete (str utils/base-url usr "/tunnels/" tunnel-id)
                                        {:basic-auth [usr access-key]})))
