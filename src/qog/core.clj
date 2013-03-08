;name
(ns qog.core)
(use 'qog.commands)
(use 'qog.world)
(defn rl [] 
	(use 'qog.core :reload)
	(use 'qog.commands :reload)
	(use 'qog.world :reload)
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;initializeation
(defn initialize []
	(initialize-inventory)
	(initialize-world)
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;loop
(defn l []
	(println (:des (location world)))
		(if (and (= location :study) (contains? inv :journal ))
			(println "Your journal glows with a green light."))
	(print-items-in-room)
	(println "")
	(print "> ")
	(flush)
	(let [input (read-line)]
		(do-commands input)
	)
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;code for exiting
(defn p []
	(rl)
	(initialize)
	(while not_done
		(l)))