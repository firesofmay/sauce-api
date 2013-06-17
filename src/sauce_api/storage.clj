(ns ^{:doc "Storage related functions."
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.storage
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn storage-upload-file
  "Uploads a given file to sauce-labs.
   Temporary storage retains files for only 24 hours.
   During tests, use a special URL for the file, in the following format: 'sauce-storage:your_file_name'
   Optional boolean Parameter :
   overwrite? -> overwrite the file if already exists when true."
  ([usr access-key file-path]
     (storage-upload-file usr access-key file-path false))
  ([usr access-key file-path overwrite?]
     (let [url (if overwrite?
                 (str utils/base-url "storage/" usr "/" (utils/get-filename file-path) "?overwrite=" true)
                 (str utils/base-url "storage/" usr "/" (utils/get-filename file-path)))]
       (clj-httpe/clj-post url
                      {:basic-auth [usr access-key]
                       :body (clojure.java.io/file file-path)
                       :content-type "application/octet-stream"}))))

(defn storage-upload-dir
  "Uploads all the files in a given directory to sauce-labs.
   Temporary storage retains files for only 24 hours.
   During tests, use a special URL for the file, in the following format: 'sauce-storage:your_file_name'
   Optional boolean Parameter :
   overwrite? -> overwrite the file if already exists when true."
  ([usr access-key dir-path]
     (storage-upload-dir usr access-key dir-path false))
  ([usr access-key dir-path overwrite?]
     (doseq [file-path (utils/get-all-files-path dir-path)]
       (storage-upload-file usr access-key file-path overwrite?))))
