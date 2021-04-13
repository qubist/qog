(ns qog.commands)
(use  '[clojure.string :only (lower-case split join)])
(use 'qog.world)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;illegal moves
(defn illegal-move? [con]
	(cond
		;can't
		(nil? con) "You can't go that way."
		(and (= location :yard) (= con :dog_path_en) (robj-contains? :yard :dog)) "You can't go that way."
		(and (= location :crystal_room) (= con :overlook_ladder) (door-closed? :door_to_overlook_ladder)) "You can't go that way."

		;dark
		(and (= location :starting_chamber) (= con :yard) (not (contains? inv :lit_lantern))) "It's too dark to go there."

		;door
		(and (= location :cave_door) (= con :cave) (door-closed? :door_to_cave)) "The door is locked."
		(and (= location :d_room_1) (= con :crossroads) (door-closed? :door_to_crossroads)) "The door is locked."
		(and (= location :clock_room) (= con :silver_key_room) (door-closed? :door_to_silver_key_room)) "The door is locked."
		(and (= location :bee_hall) (= con :bee_ladder) (door-closed? :door_to_bee_ladder)) "The door is locked."
		(and (= location :cath_stransc) (= con :cath_crypt_web) (door-closed? :door_to_cath_crypt_web)) "The trapdoor is locked."
		(and (= location :end_main) (= con :space) (door-closed? :door_to_space)) "The door is locked but a huge black key handle protrudes from its center."

		;robj
		(and (= location :yard) (= con :outside) (robj-contains? :yard :dog)) "The dog growls and blocks your path."
		(and (= location :mird) (= con :mird_hillb) (robj-contains? :mird :mird)) "The monkey-bird monster makes a strange monkey-squawk noise and blocks the way."
		(and (= location :bee_nest) (= con :bee_crack) (robj-contains? :bee_nest :bees)) "You step forward. The bees start to buzz louder and fly around faster. You decide to reconsider."

		;riddles
		(and (= location :sphinx) (= con :l_en) (riddle-unanswered? :sphinx)) "The Sphinx says \"Answer the riddle, and then you may pass!\""
		(and (= location :pword_room) (= con :white_pebble_room) (riddle-unanswered? :pword_room)) "The door is locked."

		;trip-text
		(and (= location :mine_room_1) (= con :lyre_room)) (do (set-location :mineshaft_bottom) (if (contains? inv :zegg) (do (invrm :zegg) "As you walk into the room, you get hit by something very heavy. When you wake up, you have a grape sized lump on the back of your head and you feel like you are missing something...") "As you walk into the room, you get hit by something very heavy. When you wake up, you have a grape sized lump on the back of your head."))
		(and (= location :pool) (= con :forest)) (do (set-location :forest) "You manage to bushwack your way through the dense forest with only a small amount of hardship.")
		(and (= location :overlook_ladder) (= con :mineshaft_overlook_2)) (do (set-location :mineshaft_overlook_2) "As you start to climb the ladder, a swift wind shoots you up the tube and out into a cavern filled with mining instruments. You land on a metal platform.")
		(and (= location :mine_room_1) (= con :flooded_room_1)) (do (set-location :flooded_room_1) "You fall through the hole and into shallow water.")
		(and (= location :mird) (= con :mird_hillb)) (do (set-location :mird_hillb) "You step out the massive doorway and immediately trip over a rock and tumble down a steep grassy slope.")
		(and (= location :crossroads) (= con :bee_hall)) (do (set-location :bee_hall) "As soon as you step into the northern passageway, a huge stone slab smashes down behind you sealing the way back.")
		(and (= location :crossroads) (= con :mineshaft_top)) (do (set-location :mineshaft_top) "As soon as you step into the south-leading passageway, a giant stone slab crashes down behind you sealing the way back.")
		(and (= location :cave_update) (= con :d_entrance)) (do (set-location :d_entrance) "You step down the chute-like hole revealed by the trapdoor, expecting there to be a ladder. Therefore, when there isn't one, you fall down the slippery chute and slide to the bottom.")
		(and (= location :road_2) (= con :cave_door) true) (do (set-location :cave_door) (if (contains? inv :lit_lantern) (do (invrm :lit_lantern) "As you start to walk, you trip and fall on your face. During the fall, your lantern slips out of your hand, goes out, and rolls off into the thick bushes to the side of the path. You then pick yourself up and keep going. Fortunately, the sun has just risen, so you can see.")))
		(and (= location :mird_hillb) (= con :mird)) "You attempt to climb the steep grassy slope, but you fall down and slide back to the bottom."
		(and (= location :swamp_house) (= con :swamp_e)) (do (set-location :swamp_house) "You walk east, into the eastern part of the swampy land, but the mud is super deep here and you start to sink in. You are sucked under, and when you push your way to the surface again, you are back under the house.")
		(and (= location :swamp_house_ul) (= con :swamp_house_roof)) (do (set-location :end_main) "Remembering the glyph in the cave a while back, you break a hole in the ceiling and climb out onto the roof. You look to the sky as a storm gathers, black storm clouds swirling in a vortex that seems to be centered around you. Lightning lights up the clouds from inside and you hear the loud cracks of thunder. Suddenly, a huge fork of yellow lightning flashes down from the center of the storm and strikes you unconscious. You wake up...")
		(and (= location :end_main) (= con :space)) (do (set-location :end_main) "You step out the door and float along. A sudden draft pulls you back to the doorway. You stand, unable to comprehent the sheer beauty and depth of the image that you are seeing. Your small brain's neurons are exploding, having reached their maimum capacity for thought. You are quickly melting, sinking into the floor. You are a puddle. You are very small. You hear a voice–your voice–say \"Wake up you moron! There's no time for silly fantasies\"! You snap to attention, dreading the rest of your journey in life, knowing that everything left will look gray and bland compared to the experience you have just had. \"For God's Skake!\" Says the voice, \"Get a grip!\"")

		true false))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;declare functions in order to fix circular dependencys.
(declare find-command)
(declare move)
(declare do-get-item)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;commands
(def commands
	(array-map

	:n {:name "n" :helptext "Description: used to travel to the north\nUsage: n" :fn (fn [_ _] (move "n"))}
	:s {:name "s" :helptext "Description: used to travel to the south\nUsage: s" :fn (fn [_ _] (move "s"))}
	:e {:name "e" :helptext "Description: used to travel to the east\nUsage: e" :fn (fn [_ _] (move "e"))}
	:w {:name "w" :helptext "Description: used to travel to the west\nUsage: w" :fn (fn [_ _] (move "w"))}
	:u {:name "u" :helptext "Description: used to go up\nUsage: u" :fn (fn [_ _] (move "u"))}
	:d {:name "d" :helptext "Description: used to go down\nUsage: d" :fn (fn [_ _] (move "d"))}

	:get {
		:name "get"
		:helptext "Description: used to pick up items\nUsage: get <item>"
	   	:fn (fn [item-str input]
			(let [room (location world)
				  item (search-rinv item-str room)]
				(cond (nil? item) (println "You can't do that.")
					  (= item :_unclear_) (println (str "Which " item-str "? Please be more specific."))

					  ;pedestal traps
					  (and (= location :zegg_room) (= item :zegg)) (do
																   (do-get-item item)
																   (println "The floor opens up from under you and you fall into a pit!")
																   (set-location :zegg_pit))
					  (and (= location :end_2) (= item :gem)) (do
																(println "Just as you reach for the Ojeran Gemerald, it dissapears and becomes a beam of light that shoots upward, through the ceiling. White panels on the walls open and metal mechanical-arms come out and begin dissasembling the room and then themselves. Once the room is gone, you fall through a pitch black abyss filled with bright stars. You stop falling and float stationary.")
																(set-location :credits_1))

					  true (do-get-item item)
						   )))
		}

	:put {
		:name "put"
		:helptext "Description: used to put down items\nUsage: put <item>"
		:fn (fn [item-str input]
				(let [item (search-inv item-str)]
					(cond (nil? item) (println (str "You don't have a " item-str"."))
					      (= item :_unclear_) (println (str "Which " item-str "? Please be more specific."))
						  true (do
									(println (str "You put down " (get-item-description item inv) "."))
									(give-item-to-world location item)
									(if (and (= location :yard ) (= item :meat) (robj-contains? :yard :dog))
										(do
											(println "The dog gobbles up the meat and runs off to the west, into a gap in the bushes")
											(zap-item-from-world :yard :meat)
											(rm-obj-from-world :yard :dog)))
									(if (and (= location :mird ) (robj-contains? :mird :mird) (or (= item :gold_bar) (= item :zegg) (= item :gold_key) (= item :crystal_key) (= item :silver_key) (= item :coin_bag)))
										(do
											(println "The monster grabs your offering in its beak and gingerly sets it on its pile of treasure. It then lies down in its nest and watches you, non-threateningly.")
											(zap-item-from-world :mird item)
											(change-room-des :mird "You are in a expansive cave with a floor covered with sticks and even a few bones. In the center of the room is a giant nest occupied by a resting bird-monkey monster. It is looking at you non-threateningly. In the nest is a huge hoard of riches, gold, and jewels. There is a hallway to the north, and to the south there is a massive doorway through which you can see green grass and sunlight.")
											(rm-obj-from-world :mird :mird)))
									(if (= location :mineshaft_mid)
										(do
											(println "The object slips from your hand and falls down into the mineshaft. You here a echo come up the mineshaft as the item hits the bottom.")
											(move-item :mineshaft_mid :mineshaft_bottom item)))
									(if (= location :mineshaft_overlook)
										(do
											(println "The object slips from your hand and falls down, out of sight.")
											(zap-item-from-world :mineshaft_overlook item)))
									(if (= location :mineshaft_overlook_2)
										(do
											(println "The object slips from your hand and falls down, out of sight.")
											(zap-item-from-world :mineshaft_overlook_2 item)))

									(if (= location :pit_room )
										(do
											(println "The object slips from your hand and tumbles into the black abyss below you.")
											(zap-item-from-world :pit_room item)))
									(if (= location :tree )
										(do
											(println "The object slips from your hand and tumbles down into the grass below the tree.")
											(move-item :tree :outside item)))
									(if (and (= location :crystal_room) (= item :crystal))
										(do
											(println "As you place the crystal into the contraption, It starts to glow, and red light starts to be drawn from the crystal, through the wires and tubes, and into the walls. You hear the sound of machinery starting up.")
											(zap-item-from-world :crystal_room :crystal)
											(set-door-open :door_to_overlook_ladder "A doorway opens in the stone of the north wall of the room.")
											(change-room-des :crystal_room "You find yourself in a large square room. A strange contraption stands in the center of the room. It has wires and tubes all running into the walls away from a glowing, crimson crystal about the size of your fist. Red light is being drawn from the crystal, through the wires and tubes, and into the walls. A hallway leads east, and there is a doorway to the north.")
											(change-room-des :mineshaft_overlook "You are on a long viewing area looking over a massive cavern filled with a complex of chutes, minecart tracks, and metal catwalks. A few minecarts, piled with gold ore, zip along a track, powered by a red glow that seems to pull them along. Machines are chugging, engines whirring and the far off sound of pickaxes can be heard. The viewing are continues to the east, and there is a tunnel to the south.")
											(change-room-des :mineshaft_elevator "You are inside a unsteady, rusted elevator cage. Above you there is a system of pulleys and cables that suspend the elevator from the ceiling. There is no obvious way to control the elevator, except a tiny, red keyhole with the words \"In case of emergency\" enscribed below it. The keyhole is glowing with red light. There is an exit to the west.")))

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
			(if (not (re-find #"door|trapdoor|lock|elevator" p)) (println "You can't unlock that.")
							 (cond

								;regular doors
								(and (= location :cave_door) (contains? inv :copper_key)) (set-door-open :door_to_cave "The door unlocks with a click.")
								(and (= location :bee_hall) (contains? inv :gold_key)) (set-door-open :door_to_bee_ladder "The door unlocks quietly.")
								(and (= location :d_room_1) (contains? inv :silver_key)) (set-door-open :door_to_crossroads "The door unlocks smoothly.")
								(and (= location :cath_stransc) (contains? inv :iron_key)) (set-door-open :door_to_cath_crypt_web "The heavy trapdoor clicks unlocked.")

								;door that do something when they open
								(and (= location :clock_room) (contains? inv (and :black_pebble :gray_pebble :white_pebble))) (do (set-door-open :door_to_silver_key_room "The pebbles fly out of your hand into the holes, and roll smoothly down into the depths of the door. The door swings open.") (invrm :black_pebble) (invrm :gray_pebble) (invrm :white_pebble))
								(and (= location :mineshaft_elevator) (contains? inv :crystal_key)) (do (println "As you turn the key in the lock, the cables supporting the elevator cage snap and you start to plummet down to the bottom of the elevator shaft. Just when you think that you are about to hit the bottom and be turned into a adventurer pancake breakfast for the nearest monster, there is a blinding flash of red light, and you feel yourself being teleported.") (set-location :outside_elevator))
								(= location :end_main) (do (set-door-open :door_to_space "The door unlocks and swings open revealing a black abyss filled with bright, shining stars.") (change-room-des :end_main "You are in a hallway with smooth, bright white walls. It leads north into a white room, and south to a doorway through which is a black abyss filled with stars."))

								;failure notices
								(or (= location :cave_door) (= location :mineshaft_elevator) (= location :d_room_1) (= location :cath_stransc)) (println "You do not have the correct key.")
								(= location :clock_room) (println "You do not have the correct items.")
								(not (or (= location :cave_door) (= location :mineshaft_elevator) (= location :clock_room) (= location :d_room_1) (= location :bee_hall) (= location :d_room_1) (= location :mineshaft_elevator) (= location :cath_stranc) (= location :end_main))) (println "Nothing here is locked.")
							 )
							))}

	:say {
		:name "say"
		:helptext "Description: used to talk to in game\nUsage: say <your text here>"
		:fn (fn [_ input]
			(cond (= location :sphinx) (if (riddle-unanswered? :sphinx) (if (re-find #"(night|day).+(night|day)" (lower-case input))
					(do (println "The Sphinx says \"Correct, you may pass!\" Strangely, it then yawns and goes to sleep.")
						(set-riddle-answered :sphinx)
						(change-room-des :sphinx "You are in a dim hallway running from east to west. In front of you lies a sleeping Sphinx. It is snoring heavily. Behind the Sphinx–to the west–the hallway is hard too see because it is pitch black."))
					(println "The Sphinx says \"That is not the answer. Try again.\""))
					(println "The Sphinx stirs, and mumbles in its sleep."))
				(= location :pword_room) (if (re-find #"sir|act" (lower-case input))
					(do (println "The room shakes violently and the door slides open.")
						(set-riddle-answered :pword_room))
					(println "The room shakes slightly, but the door does not open."))
				(and (= location :cath_crypt_main) (re-find #"romeo|juliet" (lower-case input)))
					(println "Nice try.")
				true (println "talking to one's self is a sign of impending mental collapse.")
			))
		}

	:read {
		:name "read"
		:helptext "Description: used to read items\nUsage: read <item>"
		:fn (fn [p _]
			(let [have-journal (contains? inv :journal)]
			  (cond (and (= p "journal") have-journal (not(= location :study))) (println "You open the journal to find that age has worn the already faint marks from the page. You can only make out some of the words and letters; the rest are smudged or faded beyond recognition. You read from the last entry:\n\"M y 12, 174 A. .E. \nI f ar that t ey h  e disc     d our    in  plac . T   Ojer n Gem  ald i  ot saf  here. My fa  e  asu es m  that t   ke  is h  den, an   e wil   e s  e. I am n t so   rtan. Tom rr w  e  will relo  te the    eral  t  a s     po  ti  . It will b  v ry dan   us.\nI l  e  n fe r. Th y a e comi g.\"")
					(and (= p "journal") have-journal (= location :study)) (println "The journal emits a green glow from the pages and the previously broken letters are completed by green glowing lines. The passage now reads:\n\"May 12, 174 A.C.E. \nI fear that they have discovered our hiding place. The Ojeran Gemerald is not safe here. My father asures me that the key is hidden, and we will be safe. I am not so certan. Tomorrow we will relocate the Gemerald to a safer position. It will be very dangeous.\nI live in fear. They are coming.\"")
					(and (contains? inv :hint_note) (re-find (get (get inv :hint_note) :regex) p)) (println "The paper says:\n\"To open the door, three stones are required.\nNot things of value, just ordinary rocks.\nThe door will open, revealing a key,\nTo help you go on in your adventures.\"")
					(= p "") (println "What would you like to read?")
					true (println (str "You can't do that."))
				))
			)}

	:light {
	:name "light"
	:helptext "Description: used to light items (like lanterns) with a match\nUsage: light <item>"
	:fn (fn [p _]
		(let [item-name (search-inv p)]
			(cond (= p "") (println "What would you like to light?")
				  (not (contains? inv item-name)) (println (str "You don't have a " p "."))
				  (not (contains? inv :match)) (println "You need a match to light things.")
				  (not (or (= item-name :lantern) (= item-name :rotten_wood))) (println (str "You can't light a " p "."))
				  true (do (invrm :match)
						   (if (not (= item-name :rotten_wood)) (invrm item-name))
						   (cond
						   		(= item-name :lantern) (invadd :lit_lantern {:des "a lit lantern" :regex #"lit lantern|lit|lantern"})
								(= item-name :rotten_wood)
									(do (if (and (= location :bee_nest) (robj-contains? :bee_nest :bees))
												(do (rm-obj-from-world :bee_nest :bees)
												 	(change-room-des :bee_nest "You are in a large crack in the wall of a hallway. Another crack branches off to the south. There in a bee's nest here, but all the bees are asleep because of the thick smoke that hangs in the air. There is an exit to the east.")
													(println "The rotten wood billows smoke into the surrounding area, and the bees stop buzzing around and fall asleep." ))
														(println "The rotten wood does not light on fire, but it billows smoke into the surrounding area, which quickly disperses.")))
						)
						   (if (not (= item-name :rotten_wood)) (println (str "You have lit a " p)))
			))))
		}

	:dev {
		:name "dev"
		:helptext "Description: This is definitely NOT an all powerful developer command\nUsage: ERROR clojure.lang.RuntimeException: compiling:(NO_SOURCE_PATH:137)"
		:fn (fn [p _]
			(let [[command param] (split p #" ")]
			(cond
				(= command "goto") (set-location (keyword param))
				true (println (str "\"" p "\"" " is not a dev command"))
				))

			)}

	:help {
		:name "help"
		:helptext "Description: used to display the help menu\nUsage: help OR help <command>"
		:fn (fn [p _]
			(let [command (find-command p)]
			(cond
				(= p "") (println (str "Commands:\n" (join "\n" (map (fn [[key val]] (get val :name)) (dissoc commands :dev)))))
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
					(let [x (str "What is this \"" input "\" of which you speak?")
						  y (str "What do you mean, \"" input "\"?")
						  z (str "I don't know what \"" input "\" means.")
						  a (str "I'm sorry, Dave, I'm afraid I can't do that.")]
						[x x x x x x x x x y y y z z z a]
					)))

		((get command :fn) p input)
	)
))
