(ns om-tutorial.client-remoting
  (:require [om-tutorial.parsing :as p]
            [om-tutorial.simulated-server :refer [simulated-server]]
            [om.next :as om]))

(defn read-remote
  "The read function used by Om parsing to determine if a remote load should happen for given data.

  The helper functions `recurse-remote` and `fetch-if-missing` take a good deal of the complexity out.
  "
  [{:keys [ast] :as env} key params]
  (println "ENTER READ REMOTE: " (dissoc ast :children))
  (case key
    :widget (p/recurse-remote env key true)
    :people (p/fetch-if-missing env key :make-root)
    (println "PRUNED: " key) ; prune everything else from the parse
    )
  )

(defn dbg [v] (println v) v)

(defn send [remote-queries cb]
  (let [payload (:my-server remote-queries)
        {:keys [query rewrite]} (om/process-roots payload)
        _ (println "Suggested query: " payload)
        _ (println "REMOTE payload (after re-root): " query)
        server-response (simulated-server query)
        ]
    (js/setTimeout (fn []
                     (println "SERVER response is: " server-response)
                     (println "state to merge " (rewrite server-response))
                     (cb (rewrite server-response))
                     ) 100)
    )
  )

