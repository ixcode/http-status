(ns status-codes.core-test
  (:require [clojure.test :refer :all]
            [status-codes.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(def test-data [:h3 :p :p :p :h3 :p :p :h3 :p])

(def test-partitioned-data (partition-by header-test test-data))

(print (map debug-el sections))

(defn debug-el [el]
  (str (:tag el) " " (html/text el) "\n"))

(def first-section (first (h3-sections page)))

(map :tag (nth sections 11))


(def matcher (re-matcher #" (\d+) (.*)" " 505 HTTP Version Not Supported"))
