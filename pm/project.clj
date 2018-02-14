(defproject pm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [seesaw "1.4.5"]
                 [cider/cider-nrepl "0.11.0-SNAPSHOT"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [java-jdbc/dsl "0.1.3" ]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [honeysql "0.9.1"]
                 [org.clojure/data.csv "0.1.4"]
                 [incanter/incanter-core "1.9.1"]
                 [javax.help/javahelp "2.0.05"]]
  :main ^:skip-aot pm.core

  
  :target-path "target/%s"
  :repl-options {:nrepl-middleware
                 [cider.nrepl.middleware.apropos/wrap-apropos
                  cider.nrepl.middleware.classpath/wrap-classpath
                  cider.nrepl.middleware.complete/wrap-complete
                  cider.nrepl.middleware.info/wrap-info
                  cider.nrepl.middleware.inspect/wrap-inspect
                  cider.nrepl.middleware.macroexpand/wrap-macroexpand
                  cider.nrepl.middleware.ns/wrap-ns
                  cider.nrepl.middleware.resource/wrap-resource
                  cider.nrepl.middleware.stacktrace/wrap-stacktrace
                  cider.nrepl.middleware.test/wrap-test
                  cider.nrepl.middleware.trace/wrap-trace
                  cider.nrepl.middleware.undef/wrap-undef]}
  :profiles {:uberjar {:aot :all}}
  :resource-paths ["resources"])
