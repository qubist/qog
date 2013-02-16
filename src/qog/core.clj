;name
(ns tbadventure.core)
(use 'tbadventure.commands)
(use 'tbadventure.world)
(defn rl [] (use 'tbadventure.core :reload))

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