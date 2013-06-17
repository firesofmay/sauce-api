(ns ^{:doc "Jobs related functions"
      :author "Mayank Jain <mayank@helpshift.com>"}
  sauce-api.jobs
  (:require [sauce-api.clj-http-extended :as clj-httpe]
            [sauce-api.utils :as utils]))

(defn get-all-ids
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

(defn get-id
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

(defn update-id
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

(defn stop-id
  "Terminates a running job."
  [usr access-key job-id]
  (utils/get-body (clj-httpe/clj-put (str utils/base-url usr "/jobs/" job-id "/stop")
                          {:basic-auth [usr access-key]
                           :content-type :json
                           :accept :json
                           :form-params {}})))

(defn get-id-assets-list
  "Get details about the static assets collected for a specific job.
   Returns:
   sauce-log: [string] Name of the Sauce log recorded for a job.
   selenium-log: [string] Name of the selenium Server log file produced by a job.
   video: [string] Name of the video file name recorded for a job.
   screenshots: [vector of strings] Vector of screenshot names captured by a job."
  [usr access-key job-id]
  (utils/get-body (clj-httpe/clj-get (str utils/base-url usr "/jobs/" job-id "/assets")
                          {:basic-auth [usr access-key]})))

(defn get-id-assets
  "Downloads various assets to a given folder for a given job id.
   By default, downloads all the assets.
   Optional boolean Parameters (In order):
   video? screenshots? selenium-log? sauce-log?
   Downloads 5 screenshots at at time in parallel for speed."
  ([usr access-key job-id path]
     (get-id-assets usr access-key job-id path true true true true))

  ([usr access-key job-id path video?]
     (get-id-assets usr access-key job-id path video? true true true))

  ([usr access-key job-id path video? screenshots?]
     (get-id-assets usr access-key job-id path video? screenshots? true true))

  ([usr access-key job-id path video? screenshots? selenium-log?]
     (get-id-assets usr access-key job-id path video? screenshots? selenium-log? true))

  ([usr access-key job-id path video? screenshots? selenium-log? sauce-log?]
     (let [assets (get-id-assets-list usr access-key job-id)
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
