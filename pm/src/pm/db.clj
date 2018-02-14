(ns pm.db
  (:require [clojure.java.jdbc :as sql]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "resources/pm.sqlite"})

(def wells96 ["A01" "B01" "C01" "D01" "E01" "F01" "G01" "H01" "A02" "B02" "C02" "D02" "E02" "F02" "G02" "H02" "A03" "B03" "C03" "D03" "E03" "F03" "G03" "H03" "A04" "B04" "C04" "D04" "E04" "F04" "G04" "H04" "A05" "B05" "C05" "D05" "E05" "F05" "G05" "H05" "A06" "B06" "C06" "D06" "E06" "F06" "G06" "H06" "A07" "B07" "C07" "D07" "E07" "F07" "G07" "H07" "A08" "B08" "C08" "D08" "E08" "F08" "G08" "H08" "A09" "B09" "C09" "D09" "E09" "F09" "G09" "H09" "A10" "B10" "C10" "D10" "E10" "F10" "G10" "H10" "A11" "B11" "C11" "D11" "E11" "F11" "G11" "H11" "A12" "B12" "C12" "D12" "E12" "F12" "G12" "H12"])

(def wells384 ["A01" "B01" "C01" "D01" "E01" "F01" "G01" "H01" "I01" "J01" "K01" "L01" "M01" "N01" "O01" "P01" "A02" "B02" "C02" "D02" "E02" "F02" "G02" "H02" "I02" "J02" "K02" "L02" "M02" "N02" "O02" "P02" "A03" "B03" "C03" "D03" "E03" "F03" "G03" "H03" "I03" "J03" "K03" "L03" "M03" "N03" "O03" "P03" "A04" "B04" "C04" "D04" "E04" "F04" "G04" "H04" "I04" "J04" "K04" "L04" "M04" "N04" "O04" "P04" "A05" "B05" "C05" "D05" "E05" "F05" "G05" "H05" "I05" "J05" "K05" "L05" "M05" "N05" "O05" "P05" "A06" "B06" "C06" "D06" "E06" "F06" "G06" "H06" "I06" "J06" "K06" "L06" "M06" "N06" "O06" "P06" "A07" "B07" "C07" "D07" "E07" "F07" "G07" "H07" "I07" "J07" "K07" "L07" "M07" "N07" "O07" "P07" "A08" "B08" "C08" "D08" "E08" "F08" "G08" "H08" "I08" "J08" "K08" "L08" "M08" "N08" "O08" "P08" "A09" "B09" "C09" "D09" "E09" "F09" "G09" "H09" "I09" "J09" "K09" "L09" "M09" "N09" "O09" "P09" "A10" "B10" "C10" "D10" "E10" "F10" "G10" "H10" "I10" "J10" "K10" "L10" "M10" "N10" "O10" "P10" "A11" "B11" "C11" "D11" "E11" "F11" "G11" "H11" "I11" "J11" "K11" "L11" "M11" "N11" "O11" "P11" "A12" "B12" "C12" "D12" "E12" "F12" "G12" "H12" "I12" "J12" "K12" "L12" "M12" "N12" "O12" "P12" "A13" "B13" "C13" "D13" "E13" "F13" "G13" "H13" "I13" "J13" "K13" "L13" "M13" "N13" "O13" "P13" "A14" "B14" "C14" "D14" "E14" "F14" "G14" "H14" "I14" "J14" "K14" "L14" "M14" "N14" "O14" "P14" "A15" "B15" "C15" "D15" "E15" "F15" "G15" "H15" "I15" "J15" "K15" "L15" "M15" "N15" "O15" "P15" "A16" "B16" "C16" "D16" "E16" "F16" "G16" "H16" "I16" "J16" "K16" "L16" "M16" "N16" "O16" "P16" "A17" "B17" "C17" "D17" "E17" "F17" "G17" "H17" "I17" "J17" "K17" "L17" "M17" "N17" "O17" "P17" "A18" "B18" "C18" "D18" "E18" "F18" "G18" "H18" "I18" "J18" "K18" "L18" "M18" "N18" "O18" "P18" "A19" "B19" "C19" "D19" "E19" "F19" "G19" "H19" "I19" "J19" "K19" "L19" "M19" "N19" "O19" "P19" "A20" "B20" "C20" "D20" "E20" "F20" "G20" "H20" "I20" "J20" "K20" "L20" "M20" "N20" "O20" "P20" "A21" "B21" "C21" "D21" "E21" "F21" "G21" "H21" "I21" "J21" "K21" "L21" "M21" "N21" "O21" "P21" "A22" "B22" "C22" "D22" "E22" "F22" "G22" "H22" "I22" "J22" "K22" "L22" "M22" "N22" "O22" "P22" "A23" "B23" "C23" "D23" "E23" "F23" "G23" "H23" "I23" "J23" "K23" "L23" "M23" "N23" "O23" "P23" "A24" "B24" "C24" "D24" "E24" "F24" "G24" "H24" "I24" "J24" "K24" "L24" "M24" "N24" "O24" "P24" ])




(defn create-sample 
  ;;sample always created in a well i.e. need well ID
  [ project-id plate-id well-id ]
  (let
      [ pre-well-name ( sql/query db [ "SELECT well_name FROM well WHERE id = ?" well-id ])
       well-name      (first (vals (first pre-well-name)))
       sample-sys-name (str "SPL" project-id "-" plate-id well-name )
       pre-sample-id  (sql/insert! db :sample {:well_id well-id
                                               :plate_id plate-id
                                               :sample_sys_name sample-sys-name })
       sample-id    (first(vals (first pre-sample-id)))
       dummy (println sample-sys-name)]
    sample-id))  

 



(defn create-well-with-sample [ well-name plate-id project-id ]
  ;; 1. Create well, get well ID
  ;; 2. Create sample with well reference ID, get sample ID
  ;; 3. Update well with sample ID
   (let [pre-well-id            ( sql/insert! db :well {:well_name well-name
                                                    :plate_id plate-id}) 
        well-id    (first(vals (first pre-well-id))) 
        sample-id  (create-sample project-id plate-id well-id)
         dummy ( sql/update! db :well {:sample_id sample-id}    
                                      [ "id = ?" well-id])
         ]
     well-id))



(defn create-plate [ type psid seqnum project-id plate-format-id ]
  ;;type: assay master etc
  ;;psid: plate-set ID
  ;;seqnum: sequence number
  ;;prjid: project-id
  ;;size: "96 well" or "384 well"

  (let  [ pre-plate-id (sql/insert! db :plate
                                { :type type
                                 :plate_set_id psid
                                 :plate_seq_num seqnum
                                 :project_id project-id
                                 :plate_format_id plate-format-id})
         plate-id   (first(vals (first pre-plate-id)))
         dummy (case plate-format-id
                 1 (doall (map  #(create-well-with-sample  %  plate-id project-id  ) wells96 ))
                 2  (doall (map  #(create-well-with-sample  %  plate-id project-id  ) wells384 )))
         ]
    plate-id))

;(create-plate "assay" 2 3 4 2)

;;(map  #(create-well-with-sample  % 2 1)  wells96)



  
(defn create-well-no-sample [ well_name plate_id]
 (sql/insert! db
    :well
    [  :well_name :plate_id]
    [ well_name plate_id]))


(defn create-project [  name descr owner  ]
   (sql/insert! db
    :project
    [  :descr :name :owner]
    [ descr name owner]))
  


(defn list-projects [  ]
 (sql/query db ["SELECT * FROM project"]))



;;(list-projects)

(defn create-plate-set [ name description project-id format numplates type ]
 (let [ pre-plateset-id (sql/insert! db :plate_set
                                    {   :name name
                                     :descr description
                                     :project_id project-id
                                     :num_plates numplates
                                     :type type
                                     :plate_format_id format})
      plateset-id (first(vals (first pre-plateset-id)))
       ]
   plateset-id)
  )

;;(create-plate-set "nm" "descr" 2  1  2  "array")

(defn create-plate-set-plates [ plateset-id type size number project-id ]
  (let [  plate-num (range  number )
          dummy          (doall (map #(create-plate  type plateset-id % project-id size ) plate-num))
        ])
  )

(defn list-plate-sets [ project-id  ]
     (let [ sqlmap {:select [:*]
                    :from [:plate_set]
                    :where  [:= :project_id project-id ] }
           dbresult       (sql/query db (hsql/format sqlmap))
           ] dbresult))

  ;(list-plate-sets 3)

(defn get-plate-types []
  (map (fn[x](get x :name) ) (sql/query db ["SELECT name FROM plate_type"])))



(defn get-plate-format [ format-id ]
   (let [ sqlmap {:select [:format]
                    :from [:plate_format]
                    :where  [:= :id format-id ] }
           dbresult       (sql/query db (hsql/format sqlmap))
           ] (get (first dbresult) :format)))

;(get-plate-format 2)


(defn get-assay-types []
  (map (fn[x](get x :name) ) (sql/query db ["SELECT name FROM assay_type"])))


(defn get-plates [ plate-set-id ]
     (let [ sqlmap {:select [:*]
                    :from [:plate ]
                    :where  [:= :plate.plate_set_id plate-set-id ]
                    :join [ :plate_format [:= :plate.plate_format_id :plate_format.id]]}
           dbresult       (sql/query db (hsql/format sqlmap))
           ] dbresult))

;;(get-plates 3)


(hsql/format {:select [:plate.plate_sys_name :plate.type :plate_format.format]
                    :from [:plate :plate_format]
                    :where  [:= :plate_set_id 3 ]
                    :join [ :plate [:= :plate.plate_format_id :plate_format.id]]})





(defn get-plate-well-sample [ plate-ids]
   (let [ sqlmap {:select [:plate.plate_sys_name :well.well_name :sample.sample_sys_name]
                  :from [:plate ]
                  :where  [:in :plate.id plate-ids ]
                  :join [ :well [:= :well.plate_id :plate.id]
                         :sample [:= :sample.well_id :well.id]]}
         dbresult       (sql/query db (hsql/format sqlmap))
         ] dbresult))

(get-plate-well-sample '(1 2 3))






(defn get-project [ project-id ]
     (let [ sqlmap {:select [:*]
                    :from [:project]
                    :where  [:= :id project-id ] }
           dbresult       (sql/query db (hsql/format sqlmap))
           ] dbresult))

;;(get-project 3)



(defn read-data-file [ file ]
(with-open [reader (io/reader file)]
  (doall
    (csv/read-csv reader)))
  )


(defn read-plates []
 (sql/query db ["SELECT * FROM plates"]))


(defn associate-assay-data-with-plate[ plate-id data ]

  (let [ sqlmap { :select [:sample_id :well_name]
                 :from [:well]
                 :where  [:= :plate_id plate-id ] }
        dbresult       (sql/query db (hsql/format sqlmap))
        finalresult (merge-with dbresult data)
        ] finalresult))


(associate-assay-data-with-plate 3  (read-data-file  "./resources/wells96data.csv") )




(defn show-selected-plates [ selected-plate-names ]
     (println (str selected-plate-names))

       

  )

;(read-data-file  "./resources/wells96data.csv")
