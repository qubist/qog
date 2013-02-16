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
	(print-items-in-room)
	(print "> ")
	(flush)
	(let [input (read-line)]
		(do-commands input)
	)
)

;(random-answer ["What's % mean?" "You said:%. That means nothing to me. xyzz%."] input)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;code for exiting
(defn p []
	(rl)
	(initialize)
	(while not_done
		(l)))