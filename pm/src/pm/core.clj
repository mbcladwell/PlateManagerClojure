(ns pm.core
  (:use [seesaw core table dev mig border])
  (:use [pm db mm util])
  (:require [clojure.java.io :as io] )
 (:use [clojure.string ])
  (:import [javax.swing JFileChooser JEditorPane JFrame JScrollPane BorderFactory AbstractButton]
           java.awt.Font java.awt.Toolkit )
  (:import [java.net.URL])
  (:gen-class))



;;https://github.com/daveray/seesaw/blob/develop/src/seesaw/core.clj#L3512

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;Dialogs
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn open-platewells-dialog
  "Launched from menu bar. View wells and samples for all plates in a plate set (no slection),
    or selected plates from a plate set."
  [ list-of-plates plate-set-id ]
  (->
 (frame :title (str "WELLS'Samples in plate set "  plate-set-id )
              :on-close :hide
              :size [640 :by 200]
              :minimum-size [640 :by 200]
              
              :content  (mig-panel
                         :items [
                                 [(seesaw.core/scrollable
                                    (seesaw.core/table
                                    :id :tblpws
                                    :auto-resize :all-columns
                                    :selection-mode :single
                                    :model [:columns [
                                                      {:key :plate_sys_name :text "Plate ID"}
                                                      {:key :well_name :text "Well"}
                                                      {:key :sample_sys_name :text "Sample"}
                                                      ]
                                            :rows (get-plate-well-sample list-of-plates)]
                                    )) "span, width 640:640:640, grow "]])

              :icon (clojure.java.io/file "../pm/resources/mwplate.png")
              :menubar (menubar
                        :items [(menu :text "Plates"
                                       
                                       :items [(action :handler ()
                                                       :name "Export"
                                                       :key "menu E"
                                                       :tip "View wells and samples.")
                                               (action :handler ( )  
                                                       :name "Assays"
                                                       :key "menu A"
                                                       :tip "View associated assays.")
                                               
                                               (action :handler (fn [e] (hide! (to-root e)))
                                                       :name "Close"
                                                       :tip "Close plate set dialog.")
                                               ])
                                 (menu :text "Help"
                                       :halign :right
                                       :items [(action :handler ()
                                                       :name "Help"
                                                       :key "menu H"
                                                       :tip "Launch help app.")
                                                       ])]))
        pack! show! (move! :by [ 300, 300])))



(defn open-plate-set-dialog [ plate-set-table ]
  (->
 (frame :title (str "PLATES for plate set "  (get (value-at  plate-set-table (selection plate-set-table)) :plateset_sys_name ) )
              :on-close :hide
              :size [640 :by 200]
              :minimum-size [640 :by 200]
              
              :content  (mig-panel
                         :items [[(label :text "Description:") ]
                                 [ (label :text (get (value-at  plate-set-table (selection plate-set-table)) :descr )  )   "wrap"  ]
                                 [(label :text "Number of plates:") ]
                                 [ (label :text (get (value-at  plate-set-table (selection plate-set-table)) :num_plates ) ) "wrap"   ]
                               ;  [(label :text "Format:") ]
                               ;  [ (label :text (get (value-at  plate-set-table (selection plate-set-table)) :format ) )  "wrap"   ]
                                 [(label :text "Timestamp:") ]
                                 [ (label :text (get (value-at  plate-set-table (selection plate-set-table)) :date ) )  "wrap"   ]
                                 [(seesaw.core/scrollable
                                    (seesaw.core/table
                                    :id :tblplates
                                    :auto-resize :all-columns
                                    :selection-mode :multi-interval
                                    :model [:columns [
                                                      {:key :plate_sys_name :text "ID"}
                                                      {:key :type :text "Type"}
                                                      {:key :plate_seq_num :text "Order"}
                                                      {:key :format :text "Format"}
                                                      ]
                                            :rows (pm.db/get-plates (get (value-at  plate-set-table (selection plate-set-table)) :id ))]
                                    )) "span, width 640:640:640, grow "]])

              :icon (clojure.java.io/file "../pm/resources/mwplate.png")
              :menubar (menubar
                        :items [(menu :text "View"
                                       
                                      :items [(action :handler (fn [e]
                                                                 (let [
                                                                       table-object (select (to-root e)[:#tblplates])
                                                                        selected-rows (value-at table-object (selection table-object {:multi? true}))
                                                                        selected-rows-id  (map  :id  selected-rows)
                                                                        all-rows (value-at table-object (range (row-count table-object  )))
                                                                       all-rows-id (map  :id  all-rows)
                                                                       ids-to-send (if (= selected-rows-id ()) all-rows-id selected-rows-id )
                                                                       dummy (prn ids-to-send)
                                                                       ]ids-to-send
                                                                 (open-platewells-dialog ids-to-send (get (value-at  plate-set-table (selection plate-set-table)) :plateset_sys_name )))   )       
                                                       :name "Wells"
                                                       :key "menu W"
                                                       :tip "View wells and samples.")
                                               (action :handler ( )  
                                                       :name "Assays"
                                                       :key "menu A"
                                                       :tip "View associated assays.")
                                               
                                               (action :handler (fn [e] (hide! (to-root e)))
                                                       :name "Close"
                                                       :tip "Close plate set dialog.")
                                               ])
                                 (menu :text "Help"
                                       :halign :right
                                       :items [(action :handler ()
                                                       :name "Help"
                                                       :key "menu H"
                                                       :tip "Launch help app.")
                                                       ])]))
        pack! show! (move! :by [ 300, 300])))

;;(str (get (first '({:a x :b y :c z})) :a))


(defn create-project-dialog [ tbl ]
  (->
   (frame :title "Create Project",
          :on-close :hide
          :id :f1
          :content   (mig-panel
                      :constraints ["wrap 2"
                                    "[right]10px[400, grow, fill]"   ;;column
                                    "[]5px[]"]                  ;;row
           
                      :items [ [(label :text "Project Name:") ]
                              [ (text :text "" :id :tb1 )     ]
                              [ (label :text "Owner:")        ]
                              [ (text :text ""  :id :tb2)     ]
                              [ (label :text "Description:") ]
                              [ (text :text ""  :id :tb3)     ]    
                              [(button :text "Cancel"
                                       :listen [:action
                                                (fn [e] (hide! (to-root e))) ]) ]
                              [(button :text "OK"
                                       :listen [:action
                                                (fn [e]
                                                  (do
                                                    (pm.db/create-project                                             
                                                     (value (select (to-root e) [:#tb1]))
                                                     (value (select (to-root e) [:#tb3]))
                                                     (value (select (to-root e) [:#tb2])))
                                                    (config! tbl
                                                             :model [:columns [{:key :project_sys_name :text "ID"}
                                                                               {:key :name :text "Name"}
                                                                               {:key :descr :text "Description"}
                                                                               {:key :owner :text "Owner"}
                                                                               {:key :date :text "Timestamp"}]
                                                                     :rows (pm.db/list-projects)])
                                                    )
                                                  (hide! (to-root e)))] ) ]
                              ])) pack! show! (move! :by [ 300, 30])))

;;(create-project-dialog [ tbl ])

(defn create-plate-set-dialog [ project-id plate-set-table ]
  (->
   (frame :title (str "Create Plate Set for project " (get (first (pm.db/get-project project-id)) :project_sys_name) ), :on-close :hide :id :f1 :content       
       (mig-panel
        :constraints [""
                      "[right]10px[100, fill]"   ;;column
                      "[]5px[]"]                  ;;row
        :items [ 
                 [ (label :text "Name:")             ]
                [ (text :text "" :id :tb1 )   "span 3, wrap"         ]
                [ (label :text "Description:")             ]
                [ (text :text "" :id :tb2 )   "span 3, wrap"         ]
                [ (label :text "Plate size: ")]
                [ (combobox :model [96
                                    384]
                            :id :cb1)]
                [ (label :text "Number of plates:")       ]
                [ (spinner :id :spin1
                           :model (spinner-model 1 :from 1 :to 100 :by 1))  "wrap" ]
                 [ (label :text "Plate type: ")]
                [ (combobox :model  (get-plate-types)
                            :id :cb2)]
                [(button :text "OK"
                         :foreground :green
                         :listen [:action
                                  (fn [e]
                                    (do
                                      (let [ name   (str (value (select (to-root e) [:#tb1])))
                                            descr    (str (value (select (to-root e) [:#tb2])))
                                            format    (cond
                                                        (= (str (value (select (to-root e) [:#cb1]))) 96) 1
                                                        (= (str (value (select (to-root e) [:#cb1]))) 384) 2
                                                        :else nil)
                                            number    (value (select (to-root e) [:#spin1]))
                                            type   (str (value (select (to-root e) [:#cb2])))
                                            plate-set-id  (pm.db/create-plate-set name descr project-id format number type)
                                            dummy      (doall (map  #(create-plate type plate-set-id  %  project-id format  ) (range 1 (+ number 1) )))
                                            ])
                                      (config! plate-set-table
                                               :model [:columns [ {:key :plateset_sys_name :text "ID"}
                                                                 {:key :name :text "Name"}
                                                                 {:key :descr :text "Description"}
                                                                 {:key :num_plates :text "NumPlates"}
                                                                 {:key :date :text "Timestamp"}
                                                                ]
                                                       :rows (pm.db/list-plate-sets project-id)])
                                      (hide! (to-root e))))])]
              [(button :text "Cancel"
                         :foreground :red
                         :listen [:action
                                  (fn [e] (hide! (to-root e))) ])]
                ])) pack! show! (move! :by [ 300, 300])))


;(get {:96-well 1 :384-well 2} (replace (str ":" 384) \" ""))

(defn open-project-dialog 
  "Display Plate Sets for a project."
  [ project-id ]
   (-> (frame :title (str "PLATE SETS for project " (get (first(pm.db/get-project project-id)) :project_sys_name))
              :on-close :hide
              :size [1000 :by 480]
              :minimum-size [640 :by 480]
              
              :content  (mig-panel
 ;                         :constraints ["wrap 2"
 ;                                       "[shrink 0]20px[200, grow, fill]"
 ;                                       "[shrink 0]5px[]"]
                          :items [[(seesaw.core/scrollable
                                    (seesaw.core/table
                                    :id :tblps
                                    :auto-resize :all-columns
                                    :selection-mode :single
                                    :model [:columns [
                                                      {:key :plateset_sys_name :text "ID"}
                                                      {:key :name :text "Name"}
                                                      {:key :descr :text "Description"}
                                                      {:key :num_plates :text "NumPlates"}
                                                      {:key :date :text "Timestamp"}
                                                      ]
                                            :rows (pm.db/list-plate-sets project-id)]
                                    )) "width 640:1000:1000, grow "]])

              :icon (clojure.java.io/file "../pm/resources/mwplate.png")
              :menubar (menubar
                        :items [(menu :text "Plate Set"
                                       
                                       :items [(action :handler ( fn [e] (create-plate-set-dialog  project-id (select (to-root e)[:#tblps ]) ))
                                                       :name "New"
                                                       :key "menu N"
                                                       :tip "Create a new plate set.")
                                               (action :handler (fn [e] (open-plate-set-dialog (select (to-root e)[:#tblps ])) )  
                                                       :name "Open"
                                                       :key "menu O"
                                                       :tip "Open an existing plate set.")
                                               
                                               (action :handler (fn [e] (hide! (to-root e)))
                                                       :name "Close"
                                                       :tip "Close plate set dialog.")
                                               ])
                                 (menu :text "Help"
                                       :halign :right
                                       :items [(action :handler ()
                                                       :name "Help"
                                                       :key "menu H"
                                                       :tip "Launch help app.")
                                                       ])]))
        pack! show! (move! :by [ 300, 300])))

;;(open-project-dialog 3)
;;()

                              
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;Main Window
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn -main [& args]
  (invoke-later
   (-> (frame :title "Plate Manager"
              :on-close :hide
              :size [640 :by 480]
              :minimum-size [640 :by 480]
              
              :content 
                        
                         (mig-panel
                         :items [[ (seesaw.core/scrollable
                                    (seesaw.core/table
                                     :id :tbl1
                                     :auto-resize :all-columns
                                     :selection-mode :single
                                     :model [:columns [{:key :project_sys_name :text "ID"}
                                                       {:key :name :text "Name"}
                                                       {:key :descr :text "Description"}
                                                       {:key :owner :text "Owner"}
                                                       {:key :date :text "Timestamp"}]
                                             :rows (pm.db/list-projects)]
                                     ))"width 640:640:1000, wrap"]

                                 [(mig-panel
;                                   :constraints ["insets 10" "[270:2(.getHeight (.getScreenSize (Toolkit/getDefaultToolkit)))70:][270:270:][40:40:]" "[450][30]"]
;                                    :constraints ["insets 20" "[220:220:][220:220:][40:40:]" "[20:20:20]"]
                                   :border (line-border :color "blue" :thickness 2)                               
                                   :items [[(text :text (str "abcd"  )) "width 480:480:1000"]
                                           [(button :text "Find") ""]]
                                   )]])
              :icon (clojure.java.io/file "../pm/resources/mwplate.png")
              
              :menubar (menubar
                        :items [(menu :text "Project"
                                       
                                       :items [(action :handler (fn [e] (create-project-dialog [ (select (to-root e)[:#tbl1 ])]))
                                                       :name "New"
                                                       :key "menu N"
                                                       :tip "Create a new project.")
                                               (action :handler (fn [e]
                                                                  (let
                                                                      [ row-selected    (get (value-at  (select (to-root e)[:#tbl1]) (selection (select (to-root e)[:#tbl1]))) :id )
                                                                       dummy           (if (nil? row-selected) (alert "First select a project!") (open-project-dialog row-selected))]))
                                                       :name "Open"
                                                       :key "menu O"
                                                       :tip "Open an existing project.")
                                               
                                               (action :handler (fn [e] (System/exit 0))
                                                       :name "Exit"
                                                       :tip "Exit Plate Manager.")
                                               ])
                                 (menu :text "Help"
                                       :halign :right
                                       :items [(action :handler (fn [e]
                                                                  (let [
                                                                        helpURL  (io/resource "helpset/pmhelp.hs")                                                 
                                                                        hs (new javax.help.HelpSet nil helpURL)
                                                                        dum (println (type hs))
                                                                        hb (. hs createHelpBroker)
                                                                        dummy (. hb setHelpSet hs)
                                                                        dummy2 (. hb setCurrentID  "assaytype")
                                                                        dummy3 (. hb setDisplayed  true)
                                                                          ]))
                                                       :name "Help"
                                                       :key "menu H"
                                                       :tip "Launch help app.")
                                               (action :handler ()
                                                       :name "Connect"
                                                       :key "menu C"
                                                       :tip "DB connection configuration.")
                                               
                                                       ])]))
       pack! show! (move! :to [ ( - ( / (.getWidth (.getScreenSize (Toolkit/getDefaultToolkit))) 2) 320),
                                ( - ( / (.getHeight (.getScreenSize (Toolkit/getDefaultToolkit))) 2) 240) ]  ) )))

(-main)

;;menu_help.addActionListener(new CSH.DisplayHelpFromSource(mainHB));
;; HelpBroker mainHB;x`
;;mainHB = mainHS.createHelpBroker();
;;  URL url = HelpSet.findHelpSet(cl, helpsetName);

;; ClassLoader cl = ApiDemo.class.getClassLoader();
;;	    URL url = HelpSet.findHelpSet(cl, helpsetName);
;;	    mainHS = new HelpSet(cl, url);



 ;;(get (value-at  (select (to-root e)[:#tbl1]) (selection (select (to-root e)[:#tbl1]))) :id :descr)
;;; (action :handler (fn [e] (println (value-at  (select (to-root e)[:#tbl1]) (selection (select (to-root e)[:#tbl1]))))) 
;;(get {:id 3, :project "PRJ-3", :descr "description", :name "MyTestProj3"} [:id :name])

;;http://www.eli.sdsu.edu/courses/fall14/cs596/notes/D18SeesawGUI.pdf


;;examples
;; https://github.com/daveray/seesaw/tree/develop/test/seesaw/test/examples

