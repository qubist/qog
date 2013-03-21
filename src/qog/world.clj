(ns qog.world)
(use  '[clojure.string :only (lower-case split join)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-inventory []
	(def inv {})
;	(def inv {:copper_key {:des "a large copper key" :regex #"copper key|copper|key" } :meat "a hunk of meat" :lit_lantern "a lit lantern" :journal "a leatherbound journal"})
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-world []
	(def not_done true)
	(def riddle-answered {:pword_room false, :sphinx false})
	(def door-open {:door_to_cave false, :door_to_silver_key_room false, :door_to_crossroads false, :door_to_cath_crypt_web false, :door_to_overlook_ladder false})
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
							:rinv {:match "a match", :coin_bag {:des "a small cloth bag of silver coins" :regex #"coin|coins|bag|silver"}}}
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
							:rinv {:copper_key {:des "a large copper key" :regex #"copper key|key" }}}
	   	:secret_room {:des "You are in a secret room. Don't be too proud of your self, it's not that special.",
							:con {:s :merchant_shop_entrance}
							:rinv {:doohickey {:des "a useless doohickey" :regex #"doohickey|useless"}}}
		:road2 {:des "You are on the road that runs from East to West. The road continues to the West. To the East, there is a forest, with a path leading into it. You can see a clearing at the end of the path.",
							:con {:w :road :e :cave_door}
							:rinv {}}
		:cave_door {:des "You are in a clearing in a dense forest. To the East is a huge door that is set in a cliff. In the center of the door there is a large keyhole inlaid with copper. To the North you can see a overgrown path.",
							:con {:w :road2 :e :cave :n :pool}
							:rinv {}}
		:pool {:des "You are on the edge of a pool of crystal-clear water surrounded by dense forest. To the South, a path leads back to the clearing. To the West you can see a faint light through the trees",
							:con {:s :cave_door, :w :forest}
							:rinv {:water_bottle {:des "a glass bottle containing a quantity of water" :regex #"water|bottle|glass"}}}
		:forest {:des "You find yourself in a small clearing surrounded by a dense forest.",
							:con {:e :pool}
							:rinv {:black_pebble {:des "a round, black pebble" :regex #"round black pebble|black pebble|round pebble|pebble"}}}						
		:cave {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. You can see the outlines of strange markings on the walls.",
							:con {:w :cave_door, :u :archive}
							:rinv {}}
		:archive {:des "You are in a small stone room. Empty scroll cubbies line the walls and several barren bookshelves are arranged along the walls.",
							:con {:d :cave_update}
							:rinv {:journal "a leather-bound journal"}}
	 	:cave_update {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. The strange markings have begun to glow but you can still only make out a few of the letters: \"Th  D ng  n.\" A circular trapdoor has opened in the floor.",
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
							:rinv {:gray_pebble {:des "a round, gray pebble" :regex #"round gray pebble|gray pebble|round pebble|pebble"}}}
		:zegg_room {:des "You are in a small square room. In the center of the room is a round pedestal. On it sits a beautiful jewel encrusted egg. To the West there is a door",
							:con {:s :mud_room, :w :pebble_hint}
							:rinv {:zegg {:des "a jewel-encrusted egg" :regex #"jewel|egg|encrusted"}}}
		:pebble_hint {:des "You are in a small cramped room. Dust covers the floor and walls and there are cobwebs on the ceiling. A ladder leads down.",
							:con {:e :zegg_room, :d :pit_room}
							:rinv {:hint_note {:des "a note on a sheaf of yellow paper" :regex #"note|paper"}}}
		:pit_room {:des "You are in a medium sized room. Almost all of the room's floor is taken up by a deep dark hole. Only a small ledge surrounds the pit. There is a ladder coming down from above, but you cannot reach it. Doors lead East and South.",
							:con {:e :zegg_pit, :s :study}
							:rinv {}}
		:zegg_pit {:des "You are in a grimy pit. The floor is covered with old bones and refuse. There is no way up, but there is a door to the West.",
							:con {:w :pit_room}
							:rinv {}}
		:study {:des "You are in a large study with walls, floor and ceiling made of wooden paneling. There is a desk and a chair, and a mural of a hearty adventurer holding a solid silver rock in a dark dungeon covers the East wall. Doors lead to the West and North.",
							:con {:n :pit_room, :w :clock_room}
							:rinv {}}
		:clock_room {:des "You are in a round, rough-walled room. You can hear a loud \"tic, tock, tic, tock...\" sound from all around you. To the North, there is a thick stone door with no handle or keyhole. Instead, it has three round holes that go into the door. To the West there is a doorway into another room.",
							:con {:n :silver_key_room, :w :d_room_1, :e :study}
							:rinv {}}
		:silver_key_room {:des "You are in a safe-room. Empty vaults line the walls and a small metal chandelier hangs from the ceiling; its candles have long been un-lit. There is a door to the South.",
							:con {:s :clock_room}
							:rinv {:silver_key {:des "a small silver key" :regex #"silver key|key" }}}
		:d_room_1 {:des "You are in a medium sized, nondescript, rectangular room. There is a large ironbound, oaken door to the West. It has a small silver keyhole in it. A hallway leads to the North, and a doorway goes East.",
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
							:rinv {:white_pebble {:des "a round, white pebble" :regex #"round white pebble|white pebble|round pebble|pebble"}}}
		:crossroads {:des "You are in a passageway that splits off. One way leads North and one way South. There are two signs here, one pointing North, and one pointing South. The sign pointing North says \"Hither\" The sign pointing South says \"Yon\" The passage also goes back to the East.",
							:con {:e :d_room_1, :n :bee_hall, :s :mineshaft_top}
							:rinv {}}
		:mineshaft_top {:des "You are at the top of a large, vertical mineshaft. It goes straight down into the earth as far as you can see. A metal ladder leads down the side of the mineshaft. To the East, there is an old elevator cage suspended to the ceiling by rusty metal cables. The passageway leads back to the North",
							:con {:e :mineshaft_elevator, :d :mineshaft_mid, :n :crossroads}
							:rinv {}}
		:mineshaft_elevator {:des "You are inside a unsteady, rusted elevator cage. Above you there is a system of pulleys and cables that suspend the elevator from the ceiling. There is no obvious way to control the elevator, except a tiny, red keyhole with the words \"In case of emergency\" inscribed below it. There is an exit to the West.",
							:con {:w :mineshaft_top}
							:rinv {}}
		:mineshaft_mid {:des "You are on a small metal platform on the South side of a mineshaft. A metal ladder leads down through a circular hole in the platform, and back up the mineshaft. A thin catwalk stretches North, and into a tunnel across from the metal platform.",
							:con {:u :mineshaft_top, :d :mineshaft_bottom, :n :mineshaft_overlook}
							:rinv {}}
		:mineshaft_bottom {:des "You are at the bottom of a very tall mineshaft. A metal ladder leads up, and there is a room to the South",
							:con {:u :mineshaft_mid, :s :mine_room_1}
							:rinv {}}
		:mineshaft_overlook {:des "You are on a long viewing area looking over a massive cavern filled with a complex of chutes, mine-cart tracks, and metal catwalks. A few mine-carts, piled with gold ore, are sitting motionless on their tracks. The viewing are continues to the East, and there is a tunnel to the South.",
							:con {:s :mineshaft_mid, :e :mineshaft_overlook_2}
							:rinv {}}
		:mineshaft_overlook_2 {:des "You are at the East end of a long viewing area looking over a huge mining district in a cavern. To the West, is the rest of the viewing platform.",
							:con {:w :mineshaft_overlook}
							:rinv {}}
		:mine_room_1 {:des "You are in a medium sized, well lit room. A doorway leads East, and you can hear beautiful singing and lyre music from this direction. There is a hole in the floor that leads down into the dark.",
							:con {:n :mineshaft_bottom, :e :lyre_room, :d :flooded_room_1}
							:rinv {:gold_bar {:des "a shiny gold bar" :regex #"shiny gold bar|gold bar|gold|bar"}}}
		:lyre_room {:des "I am typing words right now on a keyboard and they are green.",
							:con {:w :mine_room_1}
							:rinv {}}
		:flooded_room_1 {:des "You are in a large room that is filled with water up to your knees. A doorway leads to the West and there is a hole in the ceiling, but no way to reach it.",
							:con {:w :flooded_room_2}
							:rinv {}}
		:flooded_room_2 {:des "You are in a very small, square room that is flooded with water up to your knees. The water is stagnant and brown, and a few huge mushroom spores float in it. A water-worn ladder goes up.",
							:con {:e :flooded_room_1, :u :mush_room_entrance}
							:rinv {}}
		:mush_room_entrance {:des "You are in a medium sized room with a door to the North and a doorway to a hall to the West.",
							:con {:n :s_mush_room, :w :crystal_hall_1}
							:rinv {}}
		:s_mush_room {:des "You are at the South end of a long, dim room. The room is filled with huge mushrooms that are growing out of the floor, which is made of dirt. At the opposite end of the room, to the North, there is a mushroom that is at least twice as big as the others.",
							:con {:n :n_mush_room, :s :mush_room_entrance}
							:rinv {}}
		:n_mush_room {:des "You are at the North side of a long, dim room. The room is filled with huge mushrooms, growing from the floor. There is a giant mushroom here, that looks like it could be climbable.",
							:con {:s :s_mush_room, :u :mushroom}
							:rinv {}}
		:mushroom {:des "You find yourself on the top of a huge mushroom. You have a wonderful view of a small, dank room filled with mushrooms.",
							:con {:d :n_mush_room}
							:rinv {:crystal_key {:des "a key made of red crystal" :regex #"red crystal key|crystal key|red key|key"}}}
		:crystal_hall_1 {:des "You are in a damp hall that leads South and also back East.",
							:con {:e :mush_room_entrance, :s :crystal_hall_2}
							:rinv {:crystal {:des "a fist sized crimson crystal" :regex #"crimson crystal|crystal"}}}
		:crystal_hall_2 {:des "You are in a nondescript hallway that leads to the North. There is a thick iron door to the West, but luckily it has rusted from the moisture, and there is a hole big enough to climb through in the center of it.",
							:con {:n :crystal_hall_1, :w :crystal_room}
							:rinv {}}
		:crystal_room {:des "You find yourself in a large square room. A strange contraption stands in the center of the room. It has wires and tubes all running into the walls away from an empty holder for something about the size of your fist. A hallway leads East.",
							:con {:n :overlook_ladder, :e :crystal_hall_2}
							:rinv {}}
		:overlook_ladder {:des "You are in a tiny room. A ladder leads up into a tube.",
							:con {:s :crystal_room, :u :mineshaft_overlook_2}
							:rinv {}}
		:bee_hall {:des "You are in a hallway that leads South, back to the crossroads. There is an door to the North with a broken golden keyhole in it. To the West there is a crack in the wall just large enough for you to crawl through. You don't hear an ominous humming sound coming from the crack.",
							:con {:n :bee_ladder, :s :crossroads, :w :bee_nest}
							:rinv {}}
		:bee_nest {:des "You are in a large crack in the wall of a hallway. Another crack branches off to the South, but the path is blocked by an empty bee's nest. There is an exit to the East.",
							:con {:e :bee_hall, :s :bee_crack}
							:rinv {}}
		:bee_crack {:des "You are in a small crack in a wall. There is an exit to the North.",
							:con {:n :bee_nest}
							:rinv {:gold_key {:des "a gold key" :regex #"gold key|key"}}}
		:bee_ladder {:des "You are in small room with a ladder leading up. You can make out a faint light at the top of the ladder. There is a door to the South.",
							:con {:u :boulder_field, :s :bee_hall}
							:rinv {}}
		:boulder_field {:des "You find yourself in a grassy field. In the center of the field stands a huge boulder. It has been worn smooth by the wind and looks unclimbable. Paths lead North and South through the thick wilderness of forest that surrounds the clearing, and a ladder leads down, into the ground.",
							:con {:d :bee_ladder, :n :sunf, :s :cath_courtyard}
							:rinv {}}
		:sunf {:des "You find yourself in a field of sunflowers that are each at least double your height. They are all facing towards the East. The edge of the sunflowers is to the South, and to the West is a path that leads through the sunflowers.",
							:con {:s :boulder_field, :w :sunf_clearing}
							:rinv {}}
		:sunf_clearing {:des "You are in a clearing in a field of sunflowers. To the East, a path leads.",
							:con {:e :sunf}
							:rinv {:iron_key {:des "a large iron key" :regex #"iron key|key"}}}
		:cath_courtyard {:des "You are in the very small courtyard of a very small cathedral. So small, in fact, that you would have to duck in order to pass through the ornate double doors that lie to the East. A path leads to the North.",
							:con {:n :boulder_field, :e :cath_nave}
							:rinv {}}
		:cath_nave {:des "You are in the nave (large part where the people sit; who taught you history?) of a very small cathedral. So small, in fact, that your head almost touches the ceiling. The exit lies to the West, and to the East is a very small altar that stands in the center of the cathedral.",
							:con {:w :cath_courtyard, :e :cath_altar}
							:rinv {}}
		:cath_altar {:des "You are at the very small altar in the center of a very small cathedral. So small, in fact, that you have to stoop slightly, and cannot stand up straight for fear of breaking the roof of the cathedral. To the West there is the nave of the cathedral, to the East, the choir, and to the North and South, the transepts.",
							:con {:w :cath_nave, :n :cath_ntransc, :s :cath_stransc, :e :cath_choir}
							:rinv {}}
		:cath_choir {:des "You are in the very small choir (the part behind the altar where the relics are; who taught you history?) of a very small cathedral. There are five round chapels (small areas where the relics are kept; who taught you history?) here, but sadly, they are all empty, the relics probably having been stolen by some band of thieves long ago. The altar lies to the West",
							:con {:w :cath_altar}
							:rinv {}}
		:cath_ntransc {:des "You are in the North part of a very small transept (the part of the cathedral crossing at a right angle to the nave; who taught you history?) in a tiny cathedral. There is a huge and magnificent stained glass window on the wall that is to the North. A metal plaque on the wall says: \"Drim denok nus yek\" The altar lies to the South.",
							:con {:s :cath_altar}
							:rinv {}}
		:cath_stransc {:des "You are in the South part of a tiny transept (the part of the cathedral crossing at a right angle to the nave; who taught you history?) in a very small cathedral. There is a large, oaken, ironbound trapdoor on the floor. The altar lies to the North.",
							:con {:n :cath_altar, :d :cath_crypt_web}
							:rinv {}}
		:cath_crypt_web {:des "You are in an underground tunnel that leads South. It is completely filled with spider webs. A ladder leads up and out of the tunnel.",
							:con {:u :cath_stransc, :s :cath_crypt_main}
							:rinv {}}
		:cath_crypt_main {:des "You are in a cramped underground room with many stone coffins lining the walls. There is a very faint odor of rot in the air, overridden by a strong smell of dust and old things. On one of the coffins there is a strange carving of what looks like a dagger next to a small bottle. Hallways extend out from the room in all directions.",
							:con {:n :cath_crypt_web, :s :cath_crypt_s, :w :cath_crypt_w, :e :cath_hall}
							:rinv {}}
		:cath_crypt_w {:des "You are in a small underground room with stone walls, and a stone floor. There is a strong smell of dust in the air. A hallway leads to the East.",
							:con {:e :cath_crypt_main}
							:rinv {}}
		:cath_crypt_s {:des "You are in a small underground room that is slightly damp and smells like damp dirt. A hallway leads to the North.",
							:con {:n :cath_crypt_main}
							:rinv {}}
		:cath_hall {:des "You are in a hall that runs East to West. To the East, you can see a slightly bright light.",
							:con {:e :cath_stairs, :w :cath_crypt_main}
							:rinv {}}
		:cath_stairs {:des "You are at the bottom of a long flight of stairs that lead up. At the top you can see what appears to be sunlight, but it is a little brighter, and much whiter.",
							:con {:w :cath_hall_1, :u :snow_forest}
							:rinv {}}
		:snow_forest {:des "You find yourself in a snow-covered forest. The evergreen trees shine in the harsh winter sun, and the snow glitters like a thousand tiny diamonds. Wow, that was sorta corny. A stone staircase leads down into the ground, and to the North, there is a clearing of rocky ground.",
							:con {:n, :snow_cliff, :d :cath_stairs}
							:rinv {}}
		:snow_cliff {:des "You are in a small patch of rocky ground in a thick forest of snow-covered pine trees. To the North is the edge of a cliff, and you can see no easier way down than a set of small indentations, where many generations of footsteps have climbed up and down the sheer cliff face. To the South there is a path through the forest.",
							:con {:d :snow_cliff_bottom, :s :snow_forest}
							:rinv {}}
		:snow_cliff_bottom {:des "You are at the bottom of a sheer cliff made of smooth, gray rock. There is no way up because the few footholds that were there have recently been broken by some bumbling idiot of an adventurer. To the South is an entrance to a pitch black cave that leads into the cliff.",
							:con {:s :snow_cliff_passage, :e :snow_cliff_dead}
							:rinv {}}
		:snow_cliff_dead {:des "You are in a dense forest at the bottom of a sheer cliff. The forest is impenetrable and there is no way up the cliff. To the West lies a small clearing at the bottom of the cliff.",
							:con {:w :snow_cliff_bottom}
							:rinv {}}
		:snow_cliff_cave {:des "You are in a dark cave in a cliff. The exit to the outside lies to the North. FIXME",
							:con {:n :snow_cliff_bottom}
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