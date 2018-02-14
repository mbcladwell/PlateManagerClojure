(ns pm.sql
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "resources/pmtest.sqlite"})

;;https://github.com/clojure-cookbook/clojure-cookbook/blob/master/06_databases/6-03_manipulating-an-SQL-database.asciidoc

(defn create-plates-table []
  (sql/db-do-commands db
      (sql/create-table-ddl
       :plate
       [:id "INTEGER" "PRIMARY KEY" "AUTOINCREMENT"]
       [:rownum "INTEGER"]
       [:pumba "INTEGER"]
       [:colnum "INTEGER"]
       [:name "VARCHAR(30)"]
       [:type "VARCHAR(30)"])))

(defn drop-plates-table []
  (sql/db-do-commands db
                      (sql/drop-table-ddl :plates :conditional :true)))

(defn dc-plates-table []
  (do
    (drop-plates-table)
    (create-plates-table)))

(dc-plates-table)



;;CREATE TRIGGER plate_sys_name  AFTER INSERT ON plate
;;  BEGIN
;;    UPDATE plate SET plate_sys_name = 'PLT-'||NEW.id WHERE ROWID=last_insert_rowid();
;;  END;



(create-plates-table)

(defn read-plates []
 (sql/with-connection
    db
   (sql/with-query-results res
    ["SELECT * FROM plates"]
 (doall res))))

(defn create-plate [ rownum colnum name type]
 (sql/with-connection
    db
   (sql/insert-values
    :plates
    [ :rownum :colnum :name :type]
    [ rownum colnum name type])))


; plate types are: master, rearray, hits, storage
;dbSendQuery(con, "CREATE TABLE plates (id SERIAL,  rownum INT, colnum INTEGER, name VARCHAR(30), type VARCHAR(30))")
;dbSendQuery(con, "CREATE TABLE contents (id SERIAL,  name VARCHAR(30))")
;dbSendQuery(con, "CREATE TABLE wells (id SERIAL,  content INT, name VARCHAR(30), plate INTEGER,  FOREIGN KEY (plate) REFERENCES plates(id))")
;dbSendQuery(con, "CREATE TABLE plateset (id SERIAL, name VARCHAR(30))")

;; (use 'pm.repel)
;; (start-server)

;;(use 'pm.routes.home)
;;(use 'pm.models.db)
