(ns qog.commands)
(use  '[clojure.string :only (lower-case split join)])
(use 'qog.world)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;get
(defn get-cmd [item-str room]
	(let [item (search-rinv item-str room)]
		(if (nil? item) 
			(println (str "You can't do that."))
			(do 
				(take-item-from-world location item)
				(println (str "You now have " (get-item-description item inv) ".")))
			)))

			
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;put
(defn put-cmd [item-str]
	(let [item (search-inv item-str)]
		(if (nil? item)
			(println (str "You don't have a " item-str"."))
			(do 
				(println (str "You put down " (get-item-description item inv) "."))
				(add-item-to-world location item)
				(if (and (= location :yard ) (= item :meat))
					(do
						(println "The dog gobbles up the meat and runs off into the bushes")
						(zap-item-from-world :yard :meat)
						(rm-obj-from-world :yard :dog))))
			)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;illegal moves
(defn illegal-move? [con]
	(cond (nil? con) "You can't go that way"
		(and (= con :yard) (not (contains? inv :lit_lantern))) "It's to dark to go there."
		(and (= location :cave_door) (= con :cave) (not (contains? inv :copper_key))) "The door to this room is locked with a key that you don't have."
		(and (= con :outside) (robj-contains? :yard :dog)) "The dog growls and blocks your path"
		(and (= location :sphinx) (= con :l_en) (riddle-unanswered?)) "The Sphinx says \"Answer the riddle, and then you may pass!\""
		true false))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;location and movement
(defn move [direction]
	(let [room (location world)
		  con ((keyword direction) (:con room))]
		(let [error (illegal-move? con)]
			(if error 
				(println error)
				(set-location con)
			)
		)
	)
)



;random answer
(defn random-answer [possible-answers]
	(let [random-number (int (rand (count possible-answers)))]
		(get possible-answers random-number)
	))
				
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn do-commands [input]
	(let [
	  input-lower (lower-case input)
	  x (split input-lower #" ")
	  c (first x)
	  p (join " " (rest x))
	  room (location world)]
	
	(cond
		(or (= c "n") (= c "s") (= c "e") (= c "w") (= c "u") (= c "d")) (move c)
	
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		
		(= c "get") (get-cmd p room)
						
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;							
						
		(= c "put") (put-cmd p)
						
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;		
						
		(= c "inv") (if (empty? inv) (println "You are empty handed.")
									 (println (str "You have: " (get-inventory-descriptions inv) ".")))
		
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;							
									
		(= c "light")
			(let [item-name (keyword p)]
				(cond (not (contains? inv item-name)) (println (str "You don't have a " p "."))
					  (not (contains? inv :match)) (println "You need a match to light things.")
					  (not (= item-name :lantern)) (println (str "You can't light a " p "."))
					  true (do (invrm :match)
							   (invrm item-name)
							   (invadd :lit_lantern {:des "a lit lantern" :regex #"lit lantern|lit|lantern"})
							   (println (str "You have lit a " p))
						   )
				)
			)
		
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

		(= c "read")
			(let [have-readable (contains? inv :journal)]
			  (cond (and (= p "journal") have-readable (not(= location :study))) (println "You open the journal to find that age has worn the already faint marks from the page. You can only make out some of the words and letters, the rest are smudged or faded beyond recognition.You read from the last entry:\n\"M y 12, 174 A. .E. \nI f ar that t ey h  e disc     d our    in  plac . T   Ojer n Gem  ald i  ot saf  here. My fa  e  assu es m  that t   ke  is h  den, an   e wil   e s  e. I am n t so   rtain. Tom r  w  e  will relo  te the    eral  t  a s     po  ti  . It will b  v ry dan    us.\nI l  e  n fe r.\nTh y a e comi g.\"")
					(and (= p "journal") have-readable (= location :study)) (println "The journal emits a green glow from the pages and the letters are reformed by green glowing lines. The passage reads:\n\"May 12, 174 A.C.E. \nI fear that they have discovered our hiding place. The Ojeran Gemerald is not safe here. My fathe  assures me that the key is hidden, and we will be safe. I am not so certain. Tomorrow we  will relocate the Gemerald to a safer position. It will be very dangerous.\nI live in fear.\nThey are coming.\"")
					(= p "") (println "What would you like to read?")
					(not have-readable) (println "You have nothing to read")
				))
		

		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		
		(= c "quit") (def not_done false)
		
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		
		(= c "say")
			(cond (= location :sphinx) (if (re-find #"(night|day).+(night|day)" input-lower)
					(do (println "The Sphinx says \"Correct, you may pass!\" Strangely, it then yawns and goes to sleep.")
						(set-riddle-answered)
						(change-room-des :sphinx "You are in a dim hallway. In front of you lies a sleeping Sphinx. It is snoring heavily. Behind the Sphinx, the rest of the hallway is hard too see because of a blinding light."))
					(println "The Sphinx says \"That is not the answer. Try again\""))
				  
				  true (println "talking to one's self is a sign of impending mental collapse")
					)
			
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		
		(= c "help") (println "\nYa know why they call that section of the book store \"self help?\" Just kidding!\n(HELP TEXT HERE)\n")
		
		;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
		
		(not(= c "")) (println (random-answer 
									(let [x (str "What is this \"" input "\" of which you speak?")]
										[x x x (str "What do you mean, \"" input "?\"") (str "I don't know what \"" input "\" means.")]
									)))
)))