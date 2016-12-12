(ns om-tutorial.app-database.exercises)

(def cars-table {:cars/by-id {1 {:id 1 :make "Nissan" :model "Leaf"}
                              2 {:id 2 :make "Dodge" :model "Dart"}
                              3 {:id 3 :make "Ford" :model "Mustang"}}})

(def favorites
  {:cars/by-id {1 {:id 1 :make "Nissan" :model "Leaf"}
                2 {:id 2 :make "Dodge" :model "Dart"}
                3 {:id 3 :make "Ford" :model "Mustang"}}
   :favorite-car [:cars/by-id 1]})

(def ex3-uidb {:main-panel {:toolbar {:tools [{:id 1 :label "Cut"}
                                              {:id 2 :label "Copy"}]}
                            :canvas  {:data [{:id 5 :x 1 :y 3}]}}})
