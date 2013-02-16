(ns tbadventure.world)
(use  '[clojure.string :only (lower-case split join)])
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn initialize-world []
	(def not_done true)
	(def riddle-answered false)
	(def location :starting_chamber)
	(def world

;rooms
		{:starting_chamber {:des "You are in a small, bare room with an empty chest in the corner. There are doors heading in all four directions.", 
							:con {:n :kitchen, :s :yard, :e :bedroom, :w :cellar_stairs}
							:rinv {}}
		:kitchen {:des "You are in a kitchen with a stove, cupboards and shelves.",
							:con {:s :starting_chamber}
							:rinv {:meat "a hunk of meat"}}
		:bedroom {:des "You are in a bedroom with a large bed, and a bedside table.",
							:con {:w :starting_chamber}
							:rinv {:lantern "a lantern"}}
		:cellar_stairs {:des "You are in a dank staircase that leads down.",
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
		:cave_door {:des "You are outside of a huge door that is set in a cliff. In the center of the door there is a large keyhole inlaid with copper. To the North you can see a overgrown path.",
							:con {:w :road2 :e :cave :n :pool}
							:rinv {}}
		:pool {:des "You are on the edge of a pool of crystal clear water surrounded by dense forest. To the South, a path leads back to the clearing. To the West you can see a faint light through the trees",
							:con {:s :cave_door, :w :forest}
							:rinv {:water_bottle {:des "a glass bottle containing a quantity of water" :regex #"water|bottle|glass"}}}
		:forest {:des "You managed to bushwhack your way through the forest and you come to a small clearing.",
							:con {:e :pool}
							:rinv {:black_pebble {:des "a round, black pebble" :regex #"rock|round|stone|pebble|black"}}}						
		:cave {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. You can see the outlines of strange markings on the walls, but you cannot read them",
							:con {:w :cave_door, :u :archive}
							:rinv {}}
		:archive {:des "You are in a small stone room. empty scroll cubbys line the walls and several barren bookshelves are arranged along the walls.",
							:con {:d :cave_update}
							:rinv {:journal "a leatherbound journal"}}
	 	:cave_update {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. The strange markings have begun to glow and you can now faintly make out the letters \"Th  D ng  n.\" A circular trapdoor has opened in the floor.",
							:con {:w :cave_door, :u :archive, :d :d_entrance}
							:rinv {}}
	    :d_entrance {:des "You are at the bottom of a moist stone chute. the surface is too slippery for you to climb up. To the West, a long dim halway extends. You can see a faint light at the end.",
							:con {:w :d_hall}
							:rinv {}}
		:d_hall {:des "You are in a long dim hallway. It continues on to the East and far along it you can just make out the silhouette of a hulking shape against a bright light.",
							:con {:w :sphinx, :e :d_entrance}
							:rinv {}}
		:sphinx {:des "You are in a dim hallway. In front of you stands a Sphinx. It says \"There are two sisters: one gives birth to the other and she, in turn, gives birth to the first. Who are the two sisters? Answer or face your doom!\" Behind the Sphinx is a blinding light.",
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
							:con {:s :l_4}
							:rinv {}}
		}
	)
)

(defn set-location [con]
	(def location con))

(defn initialize-inventory []
;	(def inv {})
	(def inv {:copper_key "k" :meat "m" :lit_lantern "ll" :journal "j"})
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-item-description [item inventory]
	(let [inv-val (get inventory item)]
		(if (string? inv-val) inv-val (get inv-val :des))
	))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;human readable list of inventorys
(defn get-inventory-descriptions [inventory]
	(join ", " (map #(get-item-description %  inventory)(keys inventory))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;inventory stuff
(defn get-inventory-patterns [inventory]
	(map (fn [[key val]] (if (string? val) [key (re-pattern (name key))] [key (get val :regex)] ) ) inventory)
	)

(defn search-any-inv [item-str inventory]
	(let [inv-patterns (get-inventory-patterns inventory) 
		  found-item (some (fn [[item pattern]] (if (re-find pattern item-str) item nil)) inv-patterns)]
	 (if (nil? found-item) nil (keyword found-item))))

(defn search-rinv [item-str room]
	(search-any-inv item-str (get room :rinv)))

(defn search-inv [item-str]
	(search-any-inv item-str inv))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

;completely delete and item from the world
(defn zap-item-from-world [room-name item]
	(let [old-room (get world room-name)
	 	  [new-room item-text] (rm-item-from-room old-room item)]
		(update-world room-name new-room)))
		
;		  item-room-and-text (rm-item-from-room old-room item)
;		  new-room (first item-room-and-text)
;		  item-text (second item-room-and-text)

;remove obj from world
(defn rm-obj-from-world [room-name item]
	(let [old-room (get world room-name)
	 	  [new-room item-text] (rm-obj-from-room old-room item)]
		(update-world room-name new-room)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;add item to world from your inventory
(defn add-item-to-world [room-name item-name]
	(let [old-room (get world room-name)
		  item-text (get inv item-name)
	 	  new-room (add-item-to-room old-room item-name item-text)]
		(invrm item-name)
		(update-world room-name new-room)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;riddles
(defn riddle-unanswered? []
	(not riddle-answered))

(defn set-riddle-answered []
	(def riddle-answered true)
;	(set-description :sphinx "You are in a dim hallway. In front of you lies a sleeping Sphinx. Behind the Sphinx is a blinding light.")
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;room printing
(defn print-stuff-in-room [inv-type]
	(let [inv (inv-type (location world))]
		(if (and (not (nil? inv)) (not (empty? inv)))
			(println (str "You see: " (get-inventory-descriptions inv)))
			)))

(defn print-items-in-room [] 
	(print-stuff-in-room :rinv)
	(print-stuff-in-room :robj)
	)			
