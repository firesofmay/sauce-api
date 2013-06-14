(ns ^{:doc "Sauce-API."
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.core
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn provisioning-get-account-limits
  "Check account limits in terms of concurrency of tests."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/limits")
                          {:basic-auth [usr access-key]})))

(defn provisioning-get-account-details
  "Get accounts details:
   access-key, minutes, id, subscribed and can_run_manual values."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "users/" usr)
                          {:basic-auth [usr access-key]})))

(defn usage-get-current-activity
  "Access current account activity.
   Returns active job counts broken down by job status and subaccount."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/activity")
                          {:basic-auth [usr access-key]})))

(defn usage-get-historical-data
  "Returns array of ['YYYY-MM-DD', [<jobs>, <seconds>]] pairs for each day that had activity."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "users/" usr "/usage")
                          {:basic-auth [usr access-key]})))

(defn jobs-get-all-ids
  "Parameters (optional):
   :limit -> displays the specified number of jobs, instead of truncating the list at the default 100.
   :full  -> forces full job information to be returned, rather than just IDs.
   :skip  -> skips the specified number of jobs.
   :from  -> returns jobs since the specified time (in epoch time, calculated from UTC).
   :to    -> returns jobs up until the specified time (in epoch time, calculated from UTC)."
  ([usr access-key]
     (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/jobs")
                             {:basic-auth [usr access-key]})))
  ([usr access-key params]
     (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/jobs")
                             {:basic-auth [usr access-key]
                              :query-params params}))))

(defn jobs-get-id
  "Show the full information for a job given its ID.
   Attributes:
   'id': [string] Job Id.
   'owner': [string] Job owner.
   'status': [string] Job status.
   'error': [string] Error (if any) for the job.
   'name': [string] Job name.
   'browser': [string] Browser the job is using.
   'browser_version': [string] Browser version the job is using.
   'os': [string] Operating system the job is using.
   'creation_time': [integer] The time the job was first requested.
   'start_time': [integer] The time the job began executing.
   'end_time': [integer] The time the job finished executing.
   'video_url': [string] Full URL to the video replay of the job.
   'log_url': [string] Full URL to the Selenium log of the job.
   'public': [string or boolean] Visibility mode [public, public restricted, share (true), team (false), private].
   'tags': [vector of strings] Tags assigned to a job."
  [usr access-key job-id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/jobs/" job-id)
                          {:basic-auth [usr access-key]})))

(defn jobs-update-id
  "Changes a pre-existing job.
   Parameters:
   name: [string] Change the job name.
   tags: [vector of strings] Change the job tags.
   public: [string or boolean] Set job visibility to 'public', 'public restricted', 'share' (true), 'team' (false) or 'private'.
   passed: [boolean] Set whether the job passed or not on the user end.
   build: [int] The AUT build number tested by this test.
   custom-data: [map] a set of key-value pairs with any extra info that a user would like to add to the job."
  [usr access-key job-id params]
  (utils/get-body (clj-httpe/clj-put (str utils/base-url usr "/jobs/" job-id)
                          {:basic-auth [usr access-key]
                           :content-type :json
                           :accept :json
                           :form-params params})))

(defn jobs-stop-id
  "Terminates a running job."
  [usr access-key job-id]
  (utils/get-body (clj-httpe/clj-put (str utils/base-url usr "/jobs/" job-id "/stop")
                          {:basic-auth [usr access-key]
                           :content-type :json
                           :accept :json
                           :form-params {}})))

(defn jobs-get-id-assets-list
  "Get details about the static assets collected for a specific job.
   Returns:
   sauce-log: [string] Name of the Sauce log recorded for a job.
   selenium-log: [string] Name of the selenium Server log file produced by a job.
   video: [string] Name of the video file name recorded for a job.
   screenshots: [vector of strings] Vector of screenshot names captured by a job."
  [usr access-key job-id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/jobs/" job-id "/assets")
                          {:basic-auth [usr access-key]})))

(defn jobs-get-id-assets
  "Downloads various assets to a given folder for a given job id.
   By default, downloads all the assets.
   Optional boolean Parameters (In order):
   video? screenshots? selenium-log? sauce-log?
   Downloads 5 screenshots at at time in parallel for speed."
  ([usr access-key job-id path]
     (jobs-get-id-assets usr access-key job-id path true true true true))

  ([usr access-key job-id path video?]
     (jobs-get-id-assets usr access-key job-id path video? true true true))

  ([usr access-key job-id path video? screenshots?]
     (jobs-get-id-assets usr access-key job-id path video? screenshots? true true))

  ([usr access-key job-id path video? screenshots? selenium-log?]
     (jobs-get-id-assets usr access-key job-id path video? screenshots? selenium-log? true))

  ([usr access-key job-id path video? screenshots? selenium-log? sauce-log?]
     (let [assets (jobs-get-id-assets-list usr access-key job-id)
           sauce-log-file (assets "sauce-log")
           video-file (assets "video")
           selenium-log-file (assets "selenium-log")
           screenshot-seq (assets "screenshots")]
       (when sauce-log?
         (utils/download-file usr access-key job-id path sauce-log-file))
       (when video?
         (utils/download-file usr access-key job-id path video-file))
       (when selenium-log?
         (utils/download-file usr access-key job-id path selenium-log-file))
       (when screenshots?
         (doseq [seq-of-5 (partition-all 5 screenshot-seq)]
           (utils/parallel-downloads usr access-key job-id path seq-of-5))))))

(defn tunnels-get-all-ids
  "Get ids of all the active tunnels."
  [usr access-key]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/tunnels")
                          {:basic-auth [usr access-key]})))

(defn tunnels-get-id-info
  "Get info on a given id."
  [usr access-key tunnel-id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/tunnels/" tunnel-id)
                          {:basic-auth [usr access-key]})))

(defn tunnels-delete-id
  "Deletes a running tunnel session."
  [usr access-key tunnel-id]
  (utils/get-body (clj-httpe/clj-delete (str utils/base-url usr "/tunnels/" tunnel-id)
                             {:basic-auth [usr access-key]})))

(defn info-get-sauce-status
  "Checks if sauce Labs service is working okay or not.
   Returns a map of:
   wait_time, service_operational, status_message"
  []
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "info/status"))))

(defn info-get-sauce-up?
  "Returns a boolean if sauce is up and running or not."
  []
  ((utils/get-body (clj-httpe/clj-get (str utils/base-url "info/status"))) "service_operational"))

(defn- info-get-browser-list
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs.
   browser-list -> :all, :selenium-rc, :webdriver"
  [browser-list]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "info/browsers/" (name browser-list)))))

(defn info-get-browser-list-all
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs."
  []
  (info-get-browser-list :all))

(defn info-get-browser-list-selenium-rc
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs for selenium-rc."
  []
  (info-get-browser-list :selenium-rc))

(defn info-get-browser-list-webdriver
  "Returns a list of strings corresponding to all the browsers currently supported on Sauce Labs for webdriver."
  []
  (info-get-browser-list :webdriver))

(defn info-get-test-counter
  "Gets the counter of tests executed so far on sauce-labs"
  []
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "info/counter"))))

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

(defn bugs-get-types
  "Get list of available bug types."
  []
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "bugs/types"))))

(defn bugs-get-type-desc
  "Get description of each field for a particular bug type."
  [id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url "bugs/types/" id))))

(defn bugs-get-id-info
  "Get detailed info for a particular bug."
  [usr access-key id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/bugs/detail/" id)
                          {:basic-auth [usr access-key]})))
