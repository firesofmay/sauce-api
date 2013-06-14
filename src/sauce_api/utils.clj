(ns ^{:doc "Contains utility functions."
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.utils
  (:require [sauce-api.clj-http-extended :as clje]
            [clojure.data.json :as json])
  (:use [clojure.java.io :only (make-output-stream)]
        [clojure.string :only (join)]))

(def base-url "https://saucelabs.com/rest/v1/")

(defn get-body
  "Reads in the json body and returns it as a map."
  [m]
  (json/read-str (:body m)))

(defn get-thread-id
  "Returns current thread id."
  []
  (.getId (Thread/currentThread)))

(defn download-file
  "Downloads the given file and saves it to a given path.
   Ref : https://gist.github.com/hyone/1621163"
  [usr access-key job-id path f]
  (let [dir-path (str path "/" job-id)]
    (println (format "Thread %s: Creating directory: %s" (get-thread-id) dir-path))
    (.mkdirs (java.io.File. dir-path))
    (println (format "Thread %s: Downloading %s ..." (get-thread-id) f))
    (let [res (clje/clj-get (str base-url usr "/jobs/" job-id "/assets/" f)
                            {:basic-auth [usr access-key]
                             :as :byte-array})
          data (:body res)]
      (with-open [w (make-output-stream (join "/" [path job-id f]) {})]
        (.write w data)))))

(defn parallel-downloads
  "Parallely downloads given sequence of files.
   Ref : https://gist.github.com/hyone/1621163"
  [usr access-key job-id path s]
  (let [threads (doall (map #(future (download-file usr access-key job-id path %)) s))]
    (doall (map deref threads))))

(defn get-filename
  "Returns the filename from the given file-path."
  [file-path]
  (last (clojure.string/split file-path #"/")))

(defn get-all-files-path
  "Returns a seq of paths for all the files in a given directory."
  [dir-path]
  (for [file (rest (file-seq (clojure.java.io/file dir-path)))]
    (.getPath file)))
