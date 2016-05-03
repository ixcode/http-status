(ns status-codes.core
  (:require [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json])
  (:use [clojure.pprint]))

;; See https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/scrape1.clj

(def ^:dynamic *root-rfc-url* "https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def page (fetch-url *root-rfc-url*))

(def body (:content (first (html/select page [:body]))))

(defn extract-h3-sections [page]
  (html/select page #{[:h3] [:p]}))

(def h3-sections
  "Remove the first P as it comes before the list of sections"
  (rest  (extract-h3-sections page)))

(defn is-h3? [input]  
  (= :h3 (:tag  input)))

(defn group-pairs [input]
  "((:h3) (:p :p :p) (:h3 :p :p)) -> ((:h3 :p :p :p) (:h3 :p :p))"
  (loop [in input out ()]
    (if (empty? in)
      out     
      (recur (drop 2 in) (conj out (flatten (take 2 in)))))))

(def sections  (reverse (group-pairs (partition-by is-h3? h3-sections))))

(defn parse-status-text [status-text]
  (let [matcher (re-matcher #" (\d+) (.*)" status-text)
        groups (re-find matcher)]
    (if (nil? (second groups))
      [nil, (clojure.string/trim status-text)]
      [(second groups) (nth groups 2)])))


(defn map-status-code [section]
  (let [h3-tag (first section)
        status-text (second (:content h3-tag))
        [code text] (parse-status-text status-text)
        section-id (:id (:attrs (first (:content (first section)))))
        description (apply str  (flatten (map html/text (rest section))))] 
    {:code code
     :text text
     :section section-id
     :description description }))

(def status-codes (map map-status-code sections))


(defn -main [& args]
  (print "Going to generate status code db...\n")
  (spit "status-codes.edn" (pr-str status-codes))
  (spit "status-codes.json" (json/write-str status-codes))
  (print "Finished.\n"))
