(ns pm.utils
  (:import [javax.help.*])
  (:require [clojure.java.io]))


(defn get-help [ nm ]
  (let [
        ;;hs (clojure.java.io/helpset "Help.hs")
        thr (Thread/currentThread)
    ldr (.getContextClassLoader thr)
   
     strem2 (ClassLoader/getSystemResource nm)
    dummy (println strem2)
    ]))

  (pm.utils/get-help "pm/helpset/Help.hs")

;;(get-help)



