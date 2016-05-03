(ns status-codes.core
  (:require [net.cgrand.enlive-html :as html]))

;; See https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/scrape1.clj

(def ^:dynamic *root-rfc-url* "https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def page (fetch-url *root-rfc-url*))

(def body (:content (first (html/select page [:body]))))

(defn h3-sections [page]
  (html/select page [:h3]))

(def first-section (first (h3-sections page)))
