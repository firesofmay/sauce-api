(ns ^{:doc "Sauce API does not comply with HTTP Spec.
            Clj-http extended to work with Sauce API."
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.clj-http-extended
  (:require [clj-http.client :as client]
            [clj-http.core :as core]))


(def request
  (-> #'core/request
      client/wrap-redirects
      client/wrap-decompression
      client/wrap-input-coercion
      client/wrap-output-coercion
      client/wrap-content-type
      client/wrap-form-params
      client/wrap-query-params
      client/wrap-basic-auth
      client/wrap-accept
      client/wrap-accept-encoding
      client/wrap-method
      client/wrap-url))


(defn clj-get
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (client/check-url! url)
  (request (merge req {:method :get :url url})))

(defn clj-post
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (client/check-url! url)
  (request (merge req {:method :post :url url})))

(defn clj-put
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (client/check-url! url)
  (request (merge req {:method :put :url url})))

(defn clj-delete
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (client/check-url! url)
  (request (merge req {:method :delete :url url})))
