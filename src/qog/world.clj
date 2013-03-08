(ns qog.world)
(use  '[clojure.string :only (lower-case split join)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-inventory []
	(def inv {})
;	(def inv {:copper_key {:des "a large copper key" :regex #"copper|key|large" } :meat "a hunk of meat" :lit_lantern "a lit lantern" :journal "a leatherbound journal"})
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-world []
	(def not_done true)
	(def riddle-answered {:pword_room false, :sphinx false})
	(def door-open {:door_to_cave false, :door_to_silver_key_room false, :door_to_crossroads false})
	(def location :starting_chamber)
	(def world

;room
		{:starting_chamber {:des "You are in a small, bare room with an empty chest in the corner. There are doors heading in all four directions.", 
							:con {:n :kitchen, :s :yard, :e :bedroom, :w :cellar_stairs}
							:rinv {}}
		:kitchen {:des "You are in a kitchen with a stove, cupboards and shelves.",
							:con {:s :starting_chamber}
							:rinv {:meat "a hunk of meat"}}
		:bedroom {:des "You are in a bedroom with a large bed, and a bedside table.",
							:con {:w :starting_chamber}
							:rinv {:lantern "a lantern"}}
		:cellar_stairs {:des "You are at the top of a dank staircase that leads down.",
							:con {:d :cellar, :e :starting_chamber}
							:rinv {:brick "a brick"}}
		:cellar {:des "You are in a damp, dimly lit cellar with shelves containing many different things.",
							:con {:u :cellar_stairs}
							:rinv {:match "a match", :coin_bag {:des "a small cloth bag of silver coins" :regex #"coin|coins|bag|cloth|silver|small"}}}
		:yard {:des "You are in a yard lit by your lantern. To the South, there is a gate to the outside. To the North there is the house.",
							:con {:n :starting_chamber, :s :outside}
							:rinv {}
							:robj {:dog "a hungry looking dog"}}
		:outside {:des "You are outside. There is a massive tree here.",
							:con {:n :yard, :u :tree, :s :road}
							:rinv {}}
		:tree {:des "You are at the top of a huge oak tree. You can see all around you: To the South there's a road that leads East and West, and to the North there is the house.",
							:con {:d :outside}
							:rinv {}}
		:road {:des "You are on a medium sized, well traveled, dirt road. It leads to the East and West.",
							:con {:w :merchant_shop_entrance, :e :road2, :n :outside}
							:rinv {}}
		:merchant_shop_entrance {:des "You are on the porch of a merchant's shop. There is a door to your West that goes into the shop. You see light through the crack under the door.",
							:con {:e :road, :w :merchant_shop, :n :secret_room}
							:rinv {}}
		:merchant_shop {:des "You are in a merchant's shop. There are chests all around the room filled with merchandise. The merchant says \"Hello, adventurer. If you need anything, I can sell it to you here.\" There is an exit to the East.",
							:con {:e :merchant_shop_entrance}
							:rinv {:copper_key {:des "a large copper key" :regex #"copper|key|large" }}}
	   	:secret_room {:des "You are in a secret room. Don't be too proud of your self, it's not that special.",
							:con {:s :merchant_shop_entrance}
							:rinv {:doohickey {:des "a useless doohickey" :regex #"doohickey|useless"}}}
		:road2 {:des "You are on the road that runs from East to West. The road continues to the West. To the East, there is a forest, with a path leading into it. You can see a clearing at the end of the path.",
							:con {:w :road :e :cave_door}
							:rinv {}}
		:cave_door {:des "You are in a clearing in a dense forest. To the East is a huge door that is set in a cliff. In the center of the door there is a large keyhole inlaid with copper. To the North you can see a overgrown path.",
							:con {:w :road2 :e :cave :n :pool}
							:rinv {}}
		:pool {:des "You are on the edge of a pool of crystal clear water surrounded by dense forest. To the South, a path leads back to the clearing. To the West you can see a faint light through the trees",
							:con {:s :cave_door, :w :forest}
							:rinv {:water_bottle {:des "a glass bottle containing a quantity of water" :regex #"water|bottle|glass"}}}
		:forest {:des "You managed to bushwhack your way through the forest and you come to a small clearing.",
							:con {:e :pool}
							:rinv {:black_pebble {:des "a round, black pebble" :regex #"rock|round|stone|pebble|black"}}}						
		:cave {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. You can see the outlines of strange markings on the walls.",
							:con {:w :cave_door, :u :archive}
							:rinv {}}
		:archive {:des "You are in a small stone room. Empty scroll cubbies line the walls and several barren bookshelves are arranged along the walls.",
							:con {:d :cave_update}
							:rinv {:journal "a leather-bound journal"}}
	 	:cave_update {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. The strange markings have begun to glow and you can now faintly make out a few of the letters \"Th  D ng  n.\" A circular trapdoor has opened in the floor.",
							:con {:w :cave_door, :u :archive, :d :d_entrance}
							:rinv {}}
	    :d_entrance {:des "You are at the bottom of a moist stone chute. The surface is too slippery for you to climb up. To the West, a long dim hallway extends. You can see a faint light at the end.",
							:con {:w :d_hall}
							:rinv {}}
		:d_hall {:des "You are in a long dim hallway. It continues on to the West and far along it you can just make out the silhouette of a hulking shape against a bright light.",
							:con {:w :sphinx, :e :d_entrance}
							:rinv {}}
		:sphinx {:des "You are in a dim hallway. In front of you stands a Sphinx. It says \"There are two sisters: one gives birth to the other and she, in turn, gives birth to the first. Who are the two sisters? Answer the riddle or face your doom!\" Behind the Sphinx is a blinding light.",
							:con {:w :l_en, :e :d_hall}
							:rinv {}}
		:l_en {:des "The blinding light makes it impossible to see.",
							:con {:s :l_1, :n :l_01, :e :sphinx}
							:rinv {}}
		:l_1 {:des "The blinding light makes it impossible to see.",
							:con {:s :l_2, :n :l_en}
							:rinv {}}
		:l_2 {:des "The blinding light makes it impossible to see.",
							:con {:n :l_1, :w :l_3}
							:rinv {}}
		:l_3 {:des "The blinding light makes it impossible to see.",
							:con {:n :l_4, :e :l_2, :w :l_03}
							:rinv {}}
		:l_4 {:des "The blinding light makes it impossible to see.",
							:con {:n :l_ex, :s :l_3}
							:rinv {}}
		:l_01 {:des "The blinding light makes it impossible to see.",
							:con {:s :l_en, :w :l_02}
							:rinv {}}
		:l_02 {:des "The blinding light makes it impossible to see.",
							:con {:e :l_01}
							:rinv {}}
		:l_03 {:des "The blinding light makes it impossible to see.",
							:con {:e :l_3}
							:rinv {}}
		:l_ex {:des "The blinding light makes it impossible to see.",
							:con {:s :l_4, :w :mud_room}
							:rinv {}}
		:mud_room {:des "You are in a large rectangular room. The floor is completely covered with thick, squishy mud, up to your ankles. A door leads to the North.",
							:con {:e :l_ex, :n :zegg_room}
							:rinv {:gray_pebble {:des "a round, gray pebble" :regex #"rock|round|stone|pebble|gray"}}}
		:zegg_room {:des "You are in a small square room. In the center of the room is a round pedestal. On it sits a beautiful jewel encrusted egg. To the West there is a door",
							:con {:s :mud_room, :w :pebble_hint}
							:rinv {:zegg {:des "a jewel-encrusted egg" :regex #"jewel|egg|encrusted"}}}
		:pebble_hint {:des "You are in a small cramped room. Dust covers the floor and walls and there are cobwebs on the ceiling. A ladder leads down.",
							:con {:e :zegg_room, :d :pit_room}
							:rinv {:hint_note {:des "a note on a sheaf of yellow paper" :regex #"note|paper|sheaf"}}}
		:pit_room {:des "You are in a medium sized room. Almost all of the room's floor is taken up by a deep dark hole. Only a small ledge surrounds the pit. There is a ladder coming down from above, but you cannot reach it. Doors lead East and South.",
							:con {:e :zegg_pit, :s :study}
							:rinv {}}
		:zegg_pit {:des "You are in a grimy pit. The floor is covered with old bones and refuse. There is no way back up, but there is a door to the West.",
							:con {:w :pit_room}
							:rinv {}}
		:study {:des "You are in a large study with walls, floor and ceiling made of wooden paneling. There is a desk and a chair, and a mural of a hearty adventurer holding a solid silver rock in a dark dungeon covers the east wall. A door leads to the West.",
							:con {:n :pit_room, :w :clock_room}
							:rinv {}}
		:clock_room {:des "You are in a round, rough-walled room. You can hear a loud \"tic, tock, tic, tock...\" sound from around you. To the North, there is a thick stone door with no handle or keyhole. Instead, it has three round holes that go into the door. To the West there is a doorway into another room.",
							:con {:n :silver_key_room, :w :d_room_1, :e :study}
							:rinv {}}
		:silver_key_room {:des "You are in a safe-room. Empty vaults line the walls and a small metal chandelier hangs from the ceiling, it's candles have long been un-lit.",
							:con {:s :clock_room}
							:rinv {:silver_key {:des "a small silver key" :regex #"silver|key|small" }}}
		:d_room_1 {:des "You are in a medium sized, nondescript, rectangular room. There is a large ironbound, oaken door to the West. It has a small silver keyhole in it. A hallway leads to the North.",
							:con {:e :clock_room, :n :pword_hall, :w :crossroads}
							:rinv {}}
		:pword_hall {:des "You are in a short hallway that leads North and South.",
							:con {:s :d_room_1, :n :pword_room}
							:rinv {}}
		:pword_room {:des "You are in a long, thin room. At the North end of the room, there is a square stone door. On the East wall, an inscription is written:\n\"say the password\neht lanruoj sdloh eht yek\"",
							:con {:s :pword_hall, :n :white_pebble_room}
							:rinv {}}
		:white_pebble_room {:des "You are in a small cavern-like room. A small stream runs through center of the space, making a quiet trickling noise.",
							:con {:s :pword_room}
							:rinv {:white_pebble {:des "a round, white pebble" :regex #"rock|round|stone|pebble|white"}}}
		:crossroads {:des "You are in a passageway that splits off. One way leads North and one way South. The passage also goes back to the East.",
							:con {:e :d_room_1, :n :bee_room, :s :mineshaft_top}
							:rinv {}}
		:mineshaft_top {:des "You are at the top of a large, vertical mineshaft. It goes strait down into the earth as far as you can see. A metal ladder leads down the side of the mineshaft. To the east, there is an old elevator cage suspended to the ceiling by rusty metal cables.",
							:con {:e :mineshaft_elevator, :d :mineshaft_mid, :n :crossroads}
							:rinv {}}
		:mineshaft_elevator {:des "You are inside a unsteady, rusted elevator cage. Above you there is a system of pulleys and cables that suspend the elevator from the ceiling. There is no obvious way to control the elevator, except a tiny, red keyhole with the words \"In case of emergency\" enscribed below it. There is an exit to the West.",
							:con {:w :mineshaft_top}
							:rinv {}}
		:mineshaft_mid {:des "You are on a small metal platform on the South side of a mineshaft. A metal ladder leads down through a circular hole in the platform, and back up the mineshaft. A thin catwalk stretches North, and into a tunnel across from the metal platform.",
							:con {:u :mineshaft_top, :d :mineshaft_bottom, :n :mineshaft_overlook}
							:rinv {}}
		:mineshaft_bottom {:des "You are at the bottom of a very tall mineshaft. A metal ladder leads up, and there is a room to the South",
							:con {:u :mineshaft_mid, :s :mine_room_1}
							:rinv {}}
		:mineshaft_overlook {:des "You are on a long viewing area looking over a massive cavern filled with a complex of chutes, minecart tracks, and metal catwalks. A few minecarts, piled with gold ore, are sitting motionless on their tracks. The viewing are continues to the East.",
							:con {:s :mineshaft_mid, :e :mineshaft_overlook_2}
							:rinv {}}
		:mineshaft_overlook_2 {:des "You are at the East end of a long viewing area looking over a huge mining district in a cavern. To the West, is the rest of the viewing platform.",
							:con {:w :mineshaft_overlook}
							:rinv {}}
		:mine_room_1 {:des "You are in a medium sized, well lit room. A doorway leads East, and you can hear beautiful singing and lyre music from this direction. A ladder goes down, through the floor, and out of sight.",
							:con {:n :mineshaft_bottom, :e :lyre_room}
							:rinv {:gold_bar {:des "a shiny gold bar" :regex #"gold|bar|shiny"}}}
		:lyre_room {:des "Small room. Exit West.",
							:con {:w :mine_room_1}
							:rinv {}}
		}
	)
)

(defn set-location [con]
	(def location con))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-item-description [item inventory]
	(let [inv-val (get inventory item)]
		(if (string? inv-val) inv-val (get inv-val :des))
	))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;human readable list of inventorys
(defn get-inventory-descriptions [inventory]
	(join ",\n" (map #(get-item-description %  inventory)(keys inventory))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;inventory stuff
(defn get-inventory-patterns [inventory]
	(map (fn [[key val]] (if (string? val) [key (re-pattern (name key))] [key (get val :regex)] ) ) inventory)
	)

(defn search-any-inv [item-str inventory]
	(let [inv-patterns (get-inventory-patterns inventory) 
		  found-items (sort-by (fn [[_ s]] (- (count s)))  (map (fn [[item pattern]] [item (re-find pattern item-str)]) inv-patterns))
		  num-items (count found-items)]
	 (cond (= num-items 1) (first (first found-items))
		   (> num-items 1) (let [[first-item first-match] (first found-items)
								 [second-item second-match] (second found-items)]
								(if (not (= (count first-match) (count second-match)))
									first-item
									:_unclear_
									)
								)
			
		
		   true nil
		)))

(defn search-rinv [item-str room]
	(search-any-inv item-str (get room :rinv)))

(defn search-inv [item-str]
	(search-any-inv item-str inv))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;"general inv contains" check
(defn inv-contains? [room-name item inv-type]
	(let [room (get world room-name)
		  inv (get room inv-type)]
		(if (nil? inv) false (contains? inv item))))

;"room inv contains" check
(defn rinv-contains? [room-name item]
	(inv-contains? room-name item :rinv))

;"room objects contains" check
(defn robj-contains? [room-name item]
	(inv-contains? room-name item :robj))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;inv
(defn invadd [item item-text]
	(def inv (assoc inv item item-text)))
(defn invrm [item]
	(def inv (dissoc inv item)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;remove item from room
(defn rm-inv-from-room [room item-name inv-type]
	(let [old-inv (get room inv-type)
		  item-text (get old-inv item-name)
		  new-inv (dissoc old-inv item-name)
		  room-sin-inv (dissoc room inv-type)]
		[(assoc room-sin-inv inv-type new-inv) item-text])
	)


(defn rm-item-from-room [room item-name]
	(rm-inv-from-room room item-name :rinv)
	)

(defn rm-obj-from-room [room item-name]
	(rm-inv-from-room room item-name :robj)
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;add item to room
(defn add-item-to-room [room item-name item-text]
	(let [old-inv (get room :rinv)
		  new-inv (assoc old-inv item-name item-text)
		  room-sin-inv (dissoc room :rinv)]
		(assoc room-sin-inv :rinv new-inv))
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;update world
(defn update-world [room-name room]
	(def world (assoc world room-name room)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;change description of a room
(defn change-room-des [room-name new-des]
	(let [old-room (get world room-name)
		  new-room (assoc old-room :des new-des)]
		 (update-world room-name new-room)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;remove item from world and transfer it to your inventory
(defn take-item-from-world [room-name item]
	(let [old-room (get world room-name)
	 	  [new-room item-text] (rm-item-from-room old-room item)]
		(invadd item item-text)
		(update-world room-name new-room)))

;completely delete an item from the world
(defn zap-item-from-world [room-name item]
	(let [old-room (get world room-name)
	 	  [new-room item-text] (rm-item-from-room old-room item)]
		(update-world room-name new-room)))
		
;remove obj from world
(defn rm-obj-from-world [room-name item]
	(let [old-room (get world room-name)
	 	  [new-room item-text] (rm-obj-from-room old-room item)]
		(update-world room-name new-room)))

;give item to world from your inventory
(defn give-item-to-world [room-name item-name]
	(let [old-room (get world room-name)
		  item-text (get inv item-name)
	 	  new-room (add-item-to-room old-room item-name item-text)]
		(invrm item-name)
		(update-world room-name new-room)))
		
;move items from rooms into other rooms
(defn move-item [src-room-name dest-room-name item-name]
	(let [src-room (get world src-room-name)
		  dest-room (get world dest-room-name)
		  [new-src-room item-text] (rm-item-from-room src-room item-name)
	 	  new-dest-room (add-item-to-room dest-room item-name item-text)]
	(update-world src-room-name new-src-room)
	(update-world dest-room-name new-dest-room)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;riddles
(defn riddle-unanswered? [room-name]
	(not (get riddle-answered room-name)))

(defn set-riddle-answered [room-name]
	(def riddle-answered (assoc riddle-answered room-name true)
	))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;door locks
(defn door-closed? [door-name]
	(not (get door-open door-name)))

(defn set-door-open [door-name text]
	(def door-open (assoc door-open door-name true))
	(if (not (nil? text))(println text))
	)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;room printing
(defn print-stuff-in-room [inv-type]
	(let [inv (inv-type (location world))]
		(if (and (not (nil? inv)) (not (empty? inv)))
			(println (str "You see:\n" (get-inventory-descriptions inv)))
			)))

(defn print-items-in-room [] 
	(print-stuff-in-room :rinv)
	(print-stuff-in-room :robj)
	)			

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;defining not done (for quitting)
(defn set-not-done [v]
	(def not_done v))