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
	(println)
	(println "-----------------------------")
	(println "QUEST FOR THE OJERAN GEMERALD")
	(println "-----------------------------")
	(println)
	(println "Quest for the Ojeran Gemerald is a text-based, role-playing, adventure game. You can play it if you like! It will have background music soon.")
	(println "Instructions:")
	(println "In the game, you use text commands to move around and interact with the world. For example, use the command \"n\" to move North, \"u\" to move up, etc.")
	(println "Using the command \"help\" will give you a list of commands and what they do. If you type \"help\" and then one of the commands on the list, it will give you detailed information on that command")
	(println)

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
(defn -main []
	(rl)
	(initialize)
	(while not_done
		(l)))
