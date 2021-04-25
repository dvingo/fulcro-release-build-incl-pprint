(ns my-app
  (:require
    ["react" :as react]
    ["react-dom" :as react-dom]
    #_[com.fulcrologic.fulcro.application :as app]
    #_[com.fulcrologic.fulcro.components :as c :refer [defsc]]))

;(defsc Hello [this props]
;  {:query         []
;   :initial-state (fn [_])}
;  (react/createElement "div" #js{} "Hello worlds"))

;(defonce app (app/fulcro-app {:render-root! react-dom/render}))

(defn ^:export init []
  (react-dom/render
    (react/createElement "div" #js{} "Hello world")
    (js/document.getElementById "app"))
  ;(app/mount! app Hello "app")
  )
