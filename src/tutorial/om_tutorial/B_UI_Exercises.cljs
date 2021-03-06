(ns om-tutorial.B-UI-Exercises
  (:require-macros [cljs.test :refer [is]])
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [devcards.core :as dc :refer-macros [defcard defcard-doc]]))

(defcard-doc
  "# UI Exercises

  In this tutorial we are going to build an app with just enough complexity to
  exercise the most significant features of Om. That said, we want it to be
  tractable.

  ")

(defn person [{:keys [person/name person/mate]}]
  (dom/li nil
    (dom/input #js {:type "checkbox"})
    name
    (dom/button nil "X")
    (when mate (dom/ul nil (person mate)))))

(defn people-list [people]
  (dom/div nil
    (dom/button nil "Save")
    (dom/button nil "Refresh List")
    (dom/ul nil (map #(person %) people))))

(defn root [state-atom]
  (let [{:keys [last-error people new-person] :as ui-data} @state-atom]
    (dom/div nil
      (dom/div nil (when (not= "" last-error) (str "Error " last-error)))
      (dom/div nil
        (dom/div nil
          (if (= nil people)
            (dom/span nil "Loading...")
            (people-list people))
          (dom/input {:type "text" :value new-person})
          (dom/button nil "Add Person"))))))

(defcard overall-goal
  "## Overall Goal

  In the following exercises we'll build a UI using Om defui.
  Once the UI is built, we'll add in a little component local state and callback handling.

  The UI will show a list of people and their
  partners. Additionally we're going to add a place to show error messages,
  controls for adding a new person,
  saving the list, and requesting a refresh from the server.

  The suggested solution is in the source at the beginning of the next
  section's exercises (as it is used by them).

  The overall UI
  should look as shown below (which is built with plain React dom elements
  and function composition):

  "
  (fn [state-atom _]
    (root state-atom))
  {:last-error "Some error message"
   :new-person ""
   :people     [
                {:db/id 1 :person/name "Joe" :person/mate {:db/id 2 :person/name "Sally"}}
                {:db/id 2 :person/name "Sally" :person/mate {:db/id 1 :person/name "Joe"}}]}
  {:inspect-data false})

(declare om-person)

(defui Person
  Object (initLocalState [this] {:checked true})
  (render [this]
    (let [{:keys [person/name person/mate]} (om/props this)
          {:keys [checked]} (om/get-state this)
          ;; obtain the 'computed' onDelete handler
          {:keys [onDelete]} (om/get-computed this)]
      (dom/li nil
        (dom/input #js {:type    "checkbox"
                        :onClick
                        (fn [e]
                          ;; Both set-state! and update-state! works

                          ;; Set component local state.
                          ;; Will schedule the component for re-render.
                          (om.next/set-state! this {:checked (not checked)})

                          ;; Update a component's local state.
                          ;; Similar to Clojure(Script)'s swap!.
                          #_(om.next/update-state!
                             this update :checked (fn [] (not checked))))
                        :checked checked
                        })
        (dom/span #js {:style #js {:fontWeight (if checked "bold" "normal")}}
         name)
        (dom/button (clj->js {:onClick
                              (fn [e]
                                ;; call onDelete handler, if present
                                (if onDelete
                                  (onDelete name)))}) "X")
        (when mate (dom/ul nil (om-person (om/computed mate {:onDelete onDelete}))))))))

(def om-person (om/factory Person))

(defcard exercise-1
  "## Exercise 1 - A UI Component

  Create an Om Person UI component. No need to add a query yet. The main
  task is to make sure you understand how to get the properties via
  `om/props`.

  The template is in this tutorial file just above this card.

  You've got it right when the following card renders a person and their mate:
  "
  (fn [state-atom _]
    (om-person @state-atom))
  {:db/id 1 :person/name "Joe" :person/mate {:db/id 2 :person/name "Sally"}}
  {:inspect-data true})

(defui PeopleWidget
  Object
  (render
   [this]
   (let [{:keys [people]} (om/props this)
         ;; Create a deletePerson function
         deletePerson (fn [p] (println "delete p:" p))]
      (dom/div nil
        (if (= nil people)
          (dom/span nil "Loading...")
          (dom/div nil
            (dom/button #js {} "Save")
            (dom/button #js {} "Refresh List")
            ;; pass deletePerson as the onDelete handler to person element
            (dom/ul nil (map #(om-person
                               (om/computed % {:onDelete deletePerson}))
                             people))))))))

(def people-widget (om/factory PeopleWidget))

(defui Root
  Object
  (render [this]
    (let [{:keys [widget new-person last-error]} (om/props this)]
      (dom/div nil
        (dom/div nil (when (not= "" last-error) (str "Error " last-error)))
        (dom/div nil
          (people-widget widget)
          (dom/input #js {:type "text"})
          (dom/button #js {} "Add Person"))))))

(def om-root (om/factory Root))

(defcard exercise-2
  "## Exercise 2 - A UI Tree

  Continue and build out two more components as seen in the source just above this file.

  NOTE: If you look in the
  data below, you'll see our desired UI tree in data form. Use `om/props` to pull out the
  correct pieces at each level of the rendered UI. When you do this correctly, the
  card should render properly. Be careful around the `:widget` nesting.
  "
  (fn [state-atom _]
    (om-root @state-atom))
  {:last-error "Some error message"
   :new-person "something typed by the user"
   :widget     {:people [
                         {:db/id 1 :person/name "Joe" :person/mate {:db/id 2 :person/name "Sally"}}
                         {:db/id 2 :person/name "Sally" :person/mate {:db/id 1 :person/name "Joe"}}]}}
  {:inspect-data true})

(defcard exercise-3
  "
  ## Exercise 3 - Component local state

  Components can store local information without using the global app state managed by Om.
  This is useful in cases where you don't wish to combine component local
  concerns with the overall app state. We'll see the disadvantages of this later, but you can
  do this by adding:

  ```
  (initLocalState [this] { map-of-data-to-store })
  ```

  in the Object section of your UI. Then use `om/get-state`, `om/update-state!`, and `om/set-state!` to
  work with the state.

  Add component local state to your Person class, and update the UI so that when
  the person is checked their name becomes bold.

  The proper attributes for the checkbox input are `:checked` and `:onClick`.

  To ensure you got the initial state right make sure it is the default that a person is checked.
  "
  (fn [state-atom _]
    (om-person @state-atom))
  {:db/id 1 :person/name "Joe"}
  {:inspect-data true})

(defcard exercise-4
  "
  ## Exercise 4 - Computed properties

  In Om, you should not try to pass callback directly through props. While this
  would technically work, this combines the state management with computed
  attributes (e.g. callbacks).

  Instead, callbacks and other UI-generated data should be passed into an
  Om component using `om/computed`:

  ```
  (om-component (om/computed props { :computed-thing 4 }))
  ```

  and may be retrieved using `om/get-computed` on either the `props` or `this`
  passed to render.

  Internally, computed just places this data in a side-band area (e.g. metadata) so
  that it doesn't interfere with other features of Om.

  In Om, it turns out that you should manage the modification of lists from the owner
  of the list; however, in our UI the delete button for a person is in the Person
  component. Declare a placeholder function in PeopleWidget called `deletePerson`
  that just logs a message to the javascript console (e.g. `(js/console.log \"delete\" p)`,
  where p is the argument passed to the function).

  Pass that function through to each person as `onDelete`, and hook it up to the `X` button.

  Verify it works by checking the console for the messages.
  "
  (fn [state-atom _]
    (om-root @state-atom))
  {:last-error ""
   :new-person ""
   :widget     {:people [
                         {:db/id 1 :person/name "Joe" :person/mate {:db/id 2 :person/name "Sally"}}
                         {:db/id 2 :person/name "Sally" :person/mate {:db/id 1 :person/name "Joe"}}]}}
  {:inspect-data true})

(defcard-doc "[Next: App Database](#!/om_tutorial.C_App_Database)")
