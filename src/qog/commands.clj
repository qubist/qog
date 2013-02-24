(ns qog.commands)
(use  '[clojure.string :only (lower-case split join)])
(use 'qog.world)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;illegal moves
(defn illegal-move? [con]
	(cond (nil? con) "You can't go that way"
		(and (= con :yard) (not (contains? inv :lit_lantern))) "It's too dark to go there."
		(and (= location :cave_door) (= con :cave) (door-closed? :door_to_cave)) "The door is locked."
		(and (= location :d_room_1) (= con :crossroads) (door-closed? :door_to_crossroads)) "The door is locked."
		(and (= location :clock_room) (= con :silver_key_room) (door-closed? :door_to_silver_key_room)) "The door is locked"
		(and (= con :outside) (robj-contains? :yard :dog)) "The dog growls and blocks your path"
		(and (= location :sphinx) (= con :l_en) (riddle-unanswered? :sphinx)) "The Sphinx says \"Answer the riddle, and then you may pass!\""
		(and (= location :pword_room) (= con :white_pebble_room) (riddle-unanswered? :pword_room)) "The door is locked"
		true false))

			
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;define find-command in order to fix that circular dependency.
(declare find-command)
(declare move)
(declare do-get-item)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;commands
(def commands
	(array-map
		
	:n {:name "n" :helptext "Description: used to travel to the North\nUsage: n" :fn (fn [_ _] (move "n"))}
	:s {:name "s" :helptext "Description: used to travel to the South\nUsage: s" :fn (fn [_ _] (move "s"))}
	:e {:name "e" :helptext "Description: used to travel to the East\nUsage: e" :fn (fn [_ _] (move "e"))}
	:w {:name "w" :helptext "Description: used to travel to the West\nUsage: w" :fn (fn [_ _] (move "w"))}
	:u {:name "u" :helptext "Description: used to go up\nUsage: u" :fn (fn [_ _] (move "u"))}
	:d {:name "d" :helptext "Description: used to go down\nUsage: d" :fn (fn [_ _] (move "d"))}
	
	:get {
		:name "get"
		:helptext "Description: used to pick up items\nUsage: get <item>"
	   	:fn (fn [item-str input] 
			(let [room (location world)
				  item (search-rinv item-str room)]
				(cond (nil? item) (println (str "You can't do that."))
					  (and (= location :zegg_room) (= item :zegg)) (do 
																   (do-get-item item)
																   (println "The floor opens up from under you and you fall into a pit!")
																   (set-location :zegg_pit))
					  true (do-get-item item)
						   )))
		}
		
	:put {
		:name "put"
		:helptext "Description: used to put down items\nUsage: put <item>"
		:fn (fn [item-str input]
				(let [item (search-inv item-str)]
					(if (nil? item)
						(println (str "You don't have a " item-str"."))
						(do 
							(println (str "You put down " (get-item-description item inv) "."))
							(give-item-to-world location item)
							(if (and (= location :yard ) (= item :meat))
								(do
									(println "The dog gobbles up the meat and runs off into the bushes")
									(zap-item-from-world :yard :meat)
									(rm-obj-from-world :yard :dog)))
							(if (= location :mineshaft_mid)
								(do
									(println "The object slips from your hand and falls down into the mineshaft. You here a echo come up the mineshaft as the item hits the bottom.")
									(move-item :mineshaft_mid :mineshaft_bottom item)))
							(if (= location :mineshaft_overlook)
								(do
									(println "The object slips from your hand and falls down, out of sight.")
									(zap-item-from-world :mineshaft_overlook item)))
							(if (= location :pit_room )
								(do
									(println "The object slips from your hand and tumbles into the black abyss below you.")
									(zap-item-from-world :pit_room item)))
							(if (= location :tree )
								(do
									(println "The object slips from your hand and tumbles down into the grass below the tree.")
									(move-item :tree :outside item)))
					))))}
					
	:inv {
		:name "inv"
		:helptext "Description: used to display the items you are carrying\nUsage: inv"
		:fn (fn [_ _]
			(if (empty? inv) (println "You are empty handed.")
							 (println (str "You have:\n" (get-inventory-descriptions inv) "."))))}
							
	:unlock {
		:name "unlock"
		:helptext "Description: used to unlock doors with keys or other items\nUsage: unlock <target>"
		:fn (fn [p _]
			(if (not (= p "door")) (println "You can't unlock that")
							 (cond
								(and (= location :cave_door) (contains? inv :copper_key)) (set-door-open :door_to_cave "The door unlocks with a click.")
								(and (= location :clock_room) (contains? inv (and :black_pebble :gray_pebble :white_pebble))) (do (set-door-open :door_to_silver_key_room "The pebbles roll smoothly down into the depths of the door, and it swings open.") (invrm :black_pebble) (invrm :gray_pebble) (invrm :white_pebble))
								(and (= location :d_room_1) (contains? inv :silver_key)) (set-door-open :door_to_crossroads "The door unlocks smoothly.")
								(or (= location :cave_door) (= location :d_room_1)) (println "You do not have the correct key.")
								(= location :clock_room) (println "You do not have the correct items.")
								(not (or (= location :cave_door) (= location :clock_room) (= location :d_room_1))) (println "There is no locked door here.")
							 )
							))}
							
	:say {
		:name "say"
		:helptext "Description: used to talk to in game\nUsage: say <your text here>"
		:fn (fn [_ input]
			(cond (= location :sphinx) (if (riddle-unanswered? :sphinx) (if (re-find #"(night|day).+(night|day)" (lower-case input))
					(do (println "The Sphinx says \"Correct, you may pass!\" Strangely, it then yawns and goes to sleep.")
						(set-riddle-answered :sphinx)
						(change-room-des :sphinx "You are in a dim hallway. In front of you lies a sleeping Sphinx. It is snoring heavily. Behind the Sphinx, the rest of the hallway is hard too see because of a blinding light."))
					(println "The Sphinx says \"That is not the answer. Try again.\""))
					(println "The Sphinx stirs, and mumbles in its sleep."))
				(= location :pword_room) (if (re-find #"sir" (lower-case input))
					(do (println "The room shakes violently and the door slides open.")
						(set-riddle-answered :pword_room))
					(println "The room shakes slightly, but the door does not open."))
				true (println "talking to one's self is a sign of impending mental collapse.")
			))
		}
		
	:read {
		:name "read"
		:helptext "Description: used to read items\nUsage: read <item>"
		:fn (fn [p _]
			(let [have-journal (contains? inv :journal)]
			  (cond (and (= p "journal") have-journal (not(= location :study))) (println "You open the journal to find that age has worn the already faint marks from the page. You can only make out some of the words and letters, the rest are smudged or faded beyond recognition.You read from the last entry:\n\"M y 12, 174 A. .E. \nI f ar that t ey h  e disc     d our    in  plac . T   Ojer n Gem  ald i  ot saf  here. My fa  e  asu es m  that t   ke  is h  den, an   e wil   e s  e. I am n t so   rtan. Tom r w  e  will relo  te the    eral  t  a s     po  ti  . It will b  v ry dan    us.\nI l  e  n fe r.\nTh y a e comi g.\"")
					(and (= p "journal") have-journal (= location :study)) (println "The journal emits a green glow from the pages and the letters are reformed by green glowing lines. The passage reads:\n\"May 12, 174 A.C.E. \nI fear that they have discovered our hiding place. The Ojeran Gemerald is not safe here. My father asures me that the key is hidden, and we will be safe. I am not so certan. Tomorow we will relocate the Gemerald to a safer position. It will be very dangerous.\nI live in fear.\nThey are coming.\"")
					(and (contains? inv :hint_note) (re-find (get (get inv :hint_note) :regex) p)) (println "The paper says:\n\"To open the door, three stones are required.\nNot things of value, just ordinary rocks.\nThe door will open, revealing a key,\nTo help you go on in your adventures.\"")
					(= p "") (println "What would you like to read?")
					true (println (str "You can't do that."))
				))
			)}
	:light {
	:name "light"
	:helptext "Description: used to light items (like lanterns) with a match\nUsage: light <item>"
	:fn (fn [p _]
		(let [item-name (keyword p)]
			(cond (= p "") (println "What would you like to light?")
				  (not (contains? inv item-name)) (println (str "You don't have a " p "."))
				  (not (contains? inv :match)) (println "You need a match to light things.")
				  (not (= item-name :lantern)) (println (str "You can't light a " p "."))
				  true (do (invrm :match)
						   (invrm item-name)
						   (invadd :lit_lantern {:des "a lit lantern" :regex #"lit lantern|lit|lantern"})
						   (println (str "You have lit a " p))))))}

	:dev {
		:name "dev"
		:helptext "This is definitely NOT an all powerful developer command"
		:fn (fn [p _]
			(let [[command param] (split p #" ")]
			(cond
				(= command "goto") (set-location (keyword param))
				true (println (str "\"" p "\"" " is not a dev command"))
				))
			
			)}
						
	:help {
		:name "help"
		:helptext "Description: used to display the help menu\nUsage: help"
		:fn (fn [p _]
			(let [command (find-command p)]
			(cond
				(= p "") (println (str "commands:\n" (join "\n" (map (fn [[key val]] (get val :name)) (dissoc commands :dev)))))
				(not (= command nil)) (println (get command :helptext))
				true (println (str "\"" p "\"" " is not a command"))
				)
			))
			}
		
		
			
	:quit {
		:name "quit"
		:helptext "Description: used to exit out of the game\nUsage: quit"
		:fn (fn [_ _]
			(set-not-done false)
			)}
		
	)
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;location and movement
(defn move [direction]
	(let [room (location world)
		  con ((keyword direction) (:con room))]
		(let [error (illegal-move? con)]
			(if error 
				(println error)
				(set-location con)
			))))

;random answer
(defn random-answer [possible-answers]
	(let [random-number (int (rand (count possible-answers)))]
		(get possible-answers random-number)
	))

;code for getting items
(defn do-get-item [item]
	(take-item-from-world location item)
  	   (println (str "You now have " (get-item-description item inv) ".")))

;finding command keys
(defn find-command-key [command-str]
	(some (fn [[key val]] (if (= command-str (get val :name)) key nil)) commands))

;finding command 
(defn find-command [command-str]
	(let [command-key (find-command-key command-str)]
		(if (nil? command-key) nil (get commands command-key))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn do-commands [input]
	(let [
	  input-lower (lower-case input)
	  x (split input-lower #" ")
	  c (first x)
	  p (join " " (rest x))
	  room (location world)
	  command (find-command c)
	]
	(if (nil? command)
		(println (random-answer 
					(let [x (str "What is this \"" input "\" of which you speak?")]
						[x x x (str "What do you mean, \"" input "\"?") (str "I don't know what \"" input "\" means.")]
					)))
		
		((get command :fn) p input)
	)
))