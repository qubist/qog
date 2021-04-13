; -----------------------------
; QUEST FOR THE OJERAN GEMERALD
; -----------------------------
;
; Quest for the Ojeran Gemerald is a text-based, role-playing, adventure game.
; Use the Run button up there ^^ to start the game.
;
; Instructions:
; In the game, you use text commands to move around and interact with the world.
; For example, use the command "n" to move North, "u" to move up, etc. Playing
; with a pad of paper and a pencil nearby might be helpful.
;
; Using the command "help" will give you a list of commands and what they do.
; If you type "help" and then one of the commands on the list, it will give
; you detailed information on that command
;
; https://github.com/qubist/qog
;


(use  '[clojure.string :only (lower-case split join)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; WORLD
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-inventory []
	(def inv {})
;	(def inv {:copper_key {:des "a large copper key" :regex #"copper key|copper|key" } :meat "a hunk of meat" :lit_lantern "a lit lantern" :journal "a leatherbound journal" :black_pebble {:des "a round black pebble" :regex #"round black pebble|black pebble|round pebble|pebble"} :gray_pebble {:des "a round gray pebble" :regex #"round gray pebble|gray pebble|round pebble|pebble"}})
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-world []
	(def not_done true)
	(def riddle-answered {:pword_room false, :sphinx false})
	(def door-open {:door_to_cave false, :door_to_silver_key_room false, :door_to_crossroads false, :door_to_cath_crypt_web false, :door_to_overlook_ladder false, :door_to_bee_ladder false, :door_to_space false})
	(def location :starting_chamber)

	(def world
;rooms
		{:starting_chamber {:des "You are in a small, bare room. There are doors heading in all four directions.",
							:con {:n :kitchen, :s :yard, :e :bedroom, :w :cellar_stairs}
							:rinv {}}
		:kitchen {:des "You are in a meagerly sized kitchen, that is completely barren of any cooking utensils, or supplies. To the west there is a window that has been boarded up with thick wooden planks from the outside.",
							:con {:s :starting_chamber}
							:rinv {:meat "a hunk of meat"}}
		:bedroom {:des "You are in a bedroom with a large bed, and a bedside table.",
							:con {:w :starting_chamber}
							:rinv {:lantern "a lantern"}}
		:cellar_stairs {:des "You are at the top of a dank staircase that leads down.",
							:con {:d :cellar, :e :starting_chamber}
							:rinv {}}
		:cellar {:des "You are in a damp, dimly lit cellar. Empty shelves line the walls, all of them covered with a thick layer of dust.",
							:con {:u :cellar_stairs}
							:rinv {:match "a match", :coin_bag {:des "a small cloth bag of silver coins" :regex #"small cloth bag of silver coins|small bag of silver coins|cloth bag of silver coins|bag of silver coins|silver coins|coins"}}}
		:yard {:des "You are in a yard lit by your lantern. To the south, there is a gate to the outside. To the north there is the house.",
							:con {:n :starting_chamber, :s :outside, :w :dog_path_en}
							:rinv {}
							:robj {:dog "a hungry looking dog"}}
		:dog_path_en {:des "You are on a small beaten path in the thick bushes that surround the house. The yard is to the east, and the path goes north.",
							:con {:e :yard, :n :dog_path_w}
							:rinv {}}
		:dog_path_w {:des "You are on a beaten path that goes through the thick bushes that are around the house. To the east, the path leads into a clearing, and to the south the path heads back towards the yard.",
							:con {:s :dog_path_en, :e :dog_path_clearing}
							:rinv {}}
		:dog_path_clearing {:des "You are in a small clearing in the thick bushes that are planted around the house. A dog is sleeping soundly in the center of the clearing. To the south you can vaguely see the house's boarded up windows through the bushes, but they are impossible to get to. To the west, the path leads out of the clearing.",
							:con {:w :dog_path_w}
							:rinv {:collar {:des "a small red dog collar" :regex #"small red dog collar|small dog collar|red dog collar|small red collar|dog collar|collar"}}}
		:outside {:des "You are outside. There is a massive tree here. There is a yard to the north, and a road to the south.",
							:con {:n :yard, :u :tree, :s :road}
							:rinv {}}
		:tree {:des "You are at the top of a huge oak tree. You can see all around you: To the south there's a road that leads east and west, and to the north there is the house.",
							:con {:d :outside}
							:rinv {}}
		:road {:des "You are on a medium sized, well traveled, dirt road. It leads to the east and west. To the north there is an open outside area.",
							:con {:w :merchant_shop_entrance, :e :road_2, :n :outside}
							:rinv {}}
		:merchant_shop_entrance {:des "You are on the porch of a merchant's shop. There is a door to your west that goes into the shop. You see light through the crack under the door.",
							:con {:e :road, :w :merchant_shop, :n :secret_room}
							:rinv {}}
		:merchant_shop {:des "You are in a merchant's shop that is thoroughly abandoned. There are chests all around the room, but they are empty, old, and half rotted. There is an exit to the east.",
							:con {:e :merchant_shop_entrance}
							:rinv {:copper_key {:des "a large copper key" :regex #"copper key|key" }}}
	   	:secret_room {:des "You are in a secret room. Don't be too proud of yourself, it's not that special.",
							:con {:s :merchant_shop_entrance}
							:rinv {:doohickey {:des "a useless doohickey" :regex #"useless doohickey|doohickey"}}}
		:road_2 {:des "You are on the road that runs from east to west. The road continues to the west. To the east, there is a forest, with a path leading into it. You can see a clearing at the end of the path.",
							:con {:w :road :e :cave_door}
							:rinv {}}
		:cave_door {:des "You are in a clearing in a dense forest. To the east is a massive cliff with a huge door set into it. In the center of the door there is a large keyhole inlaid with copper. On either side of the door there are lit torches fastened to the cliff. To the north you can see a overgrown path.",
							:con {:w :road_2 :e :cave :n :pool}
							:rinv {}}
		:pool {:des "You are on the edge of a pool of crystal-clear water surrounded by dense forest. To the south, a path leads back to the clearing. To the west you can see a faint light through the trees",
							:con {:s :cave_door, :w :forest}
							:rinv {:water_bottle {:des "a glass bottle containing a quantity of water" :regex #"glass water bottle|glass bottle|water bottle|water|bottle"}}}
		:forest {:des "You find yourself in a small clearing surrounded by a dense forest.",
							:con {:e :pool}
							:rinv {:black_pebble {:des "a round black pebble" :regex #"round black pebble|black pebble|round pebble|pebble"}}}
		:cave {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. You can see the outlines of strange markings on the walls.",
							:con {:w :cave_door, :u :archive}
							:rinv {}}
		:archive {:des "You are in a small stone room. Empty scroll cubbies line the walls and several barren bookshelves are arranged along the walls.",
							:con {:d :cave_update}
							:rinv {:journal "a leather-bound journal"}}
	 	:cave_update {:des "You are in a cave lit by torches fastened to the walls. There is a rickety wooden ladder that leads up. The strange markings have begun to glow but you can still only make out a few of the letters: \"Th  D ng  n.\" A circular trapdoor has opened in the floor.",
							:con {:w :cave_door, :u :archive, :d :d_entrance}
							:rinv {}}
	    :d_entrance {:des "You are at the bottom of a moist stone chute. The surface is too slippery for you to climb up. To the west, a long dim hallway extends. You can see a faint light at the end.",
							:con {:w :d_hall}
							:rinv {}}
		:d_hall {:des "You are in a long dim hallway. It continues on to the west and far along it you can just make out the silhouette of a hulking shape against a bright light. The hallway also leads back to the east.",
							:con {:w :sphinx, :e :d_entrance}
							:rinv {}}
		:sphinx {:des "You are in a dim hallway running from east to west. In front of you stands a Sphinx. It says \"There are two sisters: one gives birth to the other and she, in turn, gives birth to the first. Who are the two sisters? Answer the riddle or face your doom!\" Behind the Sphinx–to the west–it is pitch black.",
							:con {:w :l_en, :e :d_hall}
							:rinv {}}
		:l_en {:des "The pitch blackness makes impossible to see, and you must fumble your way around in darkness.",
							:con {:s :l_3, :n :l_2, :e :sphinx}
							:rinv {}}
		:l_2 {:des "The pitch blackness makes impossible to see, and you must fumble your way around in darkness.",
							:con {:s :l_en, :w :l_ex}
							:rinv {}}
		:l_3 {:des "The pitch blackness makes impossible to see, and you must fumble your way around in darkness.",
							:con {:n :l_en, :w :l_4}
							:rinv {}}
		:l_4 {:des "The pitch blackness makes impossible to see, and you must fumble your way around in darkness.",
							:con {:n :l_5, :e :l_3}
							:rinv {}}
		:l_5 {:des "The pitch blackness makes impossible to see, and you must fumble your way around in darkness.",
							:con {:n :l_ex, :s :l_4}
							:rinv {}}
		:l_ex {:des "The pitch blackness makes impossible to see, and you must fumble your way around in darkness.",
							:con {:s :l_5, :e :l_2, :w :mud_room}
							:rinv {}}
		:mud_room {:des "You are in a large rectangular room. The floor is completely covered with thick, squishy mud that comes up to your ankles. A door leads to the north.",
							:con {:e :l_ex, :n :zegg_room}
							:rinv {:gray_pebble {:des "a round gray pebble" :regex #"round gray pebble|gray pebble|round pebble|pebble"}}}
		:zegg_room {:des "You are in a small square room. In the center of the room is a round pedestal. On it sits a beautiful jewel encrusted egg. To the west there is a door",
							:con {:s :mud_room, :w :pebble_hint}
							:rinv {:zegg {:des "a jewel-encrusted egg" :regex #"jewel encrusted egg|encrusted egg|egg"}}}
		:pebble_hint {:des "You are in a small cramped room. Dust covers the floor and walls and there are cobwebs on the ceiling. A ladder leads down.",
							:con {:e :zegg_room, :d :pit_room}
							:rinv {:hint_note {:des "a note on a scrap of yellow paper" :regex #"yellow paper|paper|note"}}}
		:pit_room {:des "You are in a medium sized room. Almost all of the room's floor is taken up by a deep dark hole. Only a small ledge surrounds the pit. There is a ladder coming down from above, but you cannot reach it. Doors lead east and south.",
							:con {:e :zegg_pit, :s :study}
							:rinv {}}
		:zegg_pit {:des "You are in a grimy pit. The floor is covered with old bones and refuse. There is no way up, but there is a door to the west.",
							:con {:w :pit_room}
							:rinv {}}
		:study {:des "You are in a large study with it's walls and ceiling made of wooden paneling. There is a desk and a chair, and a mural of a hearty adventurer holding a solid silver rock in a dark dungeon covers the east wall. Doors lead to the west and north.",
							:con {:n :pit_room, :w :clock_room}
							:rinv {}}
		:clock_room {:des "You are in a round, rough-walled room. You can hear a loud \"tic, tock, tic, tock...\" sound from all around you. To the north, there is a thick stone door with no handle or keyhole. Instead, it has three round holes that go into the door. To the west there is a doorway into another room.",
							:con {:n :silver_key_room, :w :d_room_1, :e :study}
							:rinv {:match "a match"}}
		:silver_key_room {:des "You are in a safe-room. Empty vaults line the walls and a small metal chandelier hangs from the ceiling; its candles have long been un-lit. There is a door to the south.",
							:con {:s :clock_room}
							:rinv {:silver_key {:des "a small silver key" :regex #"silver key|key" }}}
		:d_room_1 {:des "You are in a medium sized, nondescript, rectangular room. There is a large ironbound, oaken door to the west. It has a small silver keyhole in it. A hallway leads to the north, and a doorway goes east.",
							:con {:e :clock_room, :n :pword_hall, :w :crossroads}
							:rinv {}}
		:pword_hall {:des "You are in a short hallway that leads north and south.",
							:con {:s :d_room_1, :n :pword_room}
							:rinv {}}
		:pword_room {:des "You are in a long, thin room. At the north end of the room, there is a square stone door. On the east wall, an inscription is written:\n\"say the password\neht lanruoj sdloh eht yek\"",
							:con {:s :pword_hall, :n :white_pebble_room}
							:rinv {}}
		:white_pebble_room {:des "You are in a small cavern-like room. A small stream runs through center of the space, making a quiet trickling noise.",
							:con {:s :pword_room}
							:rinv {:white_pebble {:des "a round white pebble" :regex #"round white pebble|white pebble|round pebble|pebble"}}}
		:crossroads {:des "You are in a passageway that splits off. One way leads north and one way south. There are two signs here, one pointing north, and one pointing south. The sign pointing north says \"Hither\" The sign pointing south says \"Yon\" The passage also goes back to the east.",
							:con {:e :d_room_1, :n :bee_hall, :s :mineshaft_top}
							:rinv {}}
		:mineshaft_top {:des "You are at the top of a large, vertical mineshaft. It goes straight down into the earth as far as you can see. A metal ladder leads down the side of the mineshaft. To the east, there is an old elevator cage suspended to the ceiling by rusty metal cables. The passageway leads back to the north, but is blocked by a gigantic stone slab.",
							:con {:e :mineshaft_elevator, :d :mineshaft_mid}
							:rinv {}}
		:mineshaft_elevator {:des "You are inside a unsteady, rusted elevator cage. Above you there is a system of pulleys and cables that suspend the elevator from the ceiling. There is no obvious way to control the elevator, except a tiny keyhole with the words \"In case of emergency\" inscribed below it. There is an exit to the west.",
							:con {:w :mineshaft_top}
							:rinv {}}
		:mineshaft_mid {:des "You are on a small metal platform on the south side of a mineshaft. A metal ladder leads down through a circular hole in the platform, and back up the mineshaft. A thin catwalk stretches north, and into a tunnel across from the metal platform.",
							:con {:u :mineshaft_top, :d :mineshaft_bottom, :n :mineshaft_overlook}
							:rinv {}}
		:mineshaft_bottom {:des "You are at the bottom of a very tall mineshaft. A metal ladder leads up, and there is a room to the south",
							:con {:u :mineshaft_mid, :s :mine_room_1}
							:rinv {}}
		:mineshaft_overlook {:des "You are on a long viewing area looking over a massive cavern filled with a complex of chutes, mine-cart tracks, and metal catwalks. A few mine-carts, piled with gold ore, are sitting motionless on their tracks. The viewing area continues to the east, and there is a tunnel to the south.",
							:con {:s :mineshaft_mid, :e :mineshaft_overlook_2}
							:rinv {}}
		:mineshaft_overlook_2 {:des "You are at the east end of a long viewing area looking over a huge mining district in a cavern. To the west, is the rest of the viewing platform.",
							:con {:w :mineshaft_overlook}
							:rinv {}}
		:mine_room_1 {:des "You are in a medium sized, well lit room. A doorway leads east, and you can hear beautiful singing and lyre music from this direction. There is a hole in the floor that leads down into the dark.",
							:con {:n :mineshaft_bottom, :e :lyre_room, :d :flooded_room_1}
							:rinv {:gold_bar {:des "a shiny gold bar" :regex #"shiny gold bar|gold bar|gold|bar"}}}
		:lyre_room {:des "I am typing words right now on a keyboard and they are green.",
							:con {:w :mine_room_1}
							:rinv {}}
		:flooded_room_1 {:des "You are in a large room that is filled with water up to your knees. A doorway leads to the west and there is a hole in the ceiling, but no way to reach it.",
							:con {:w :flooded_room_2}
							:rinv {}}
		:flooded_room_2 {:des "You are in a very small, square room that is flooded with water up to your knees. The water is stagnant and brown, and a few huge mushroom spores float in it. A water-worn ladder goes up.",
							:con {:e :flooded_room_1, :u :mush_room_entrance}
							:rinv {}}
		:mush_room_entrance {:des "You are in a medium sized room with a door to the north and a doorway to a hall to the west. The way north smells damp and earthy. A ladder leads down into a room full of water.",
							:con {:n :s_mush_room, :w :crystal_hall_1, :d :flooded_room_2}
							:rinv {}}
		:s_mush_room {:des "You are at the south end of a long, dim room. The room is filled with huge mushrooms that are growing out of the floor, which is made of dirt. At the opposite end of the room, to the north, there is a mushroom that is at least twice as big as the others.",
							:con {:n :n_mush_room, :s :mush_room_entrance}
							:rinv {}}
		:n_mush_room {:des "You are at the north side of a long, dim room. The room is filled with huge mushrooms, growing from the floor. There is a giant mushroom here, that looks like it could be climbable.",
							:con {:s :s_mush_room, :u :mushroom}
							:rinv {}}
		:mushroom {:des "You find yourself on the top of a huge mushroom. You have a wonderful view of a small, dank room filled with mushrooms.",
							:con {:d :n_mush_room}
							:rinv {:crystal_key {:des "a key made of red crystal" :regex #"red crystal key|crystal key|red key|key"}}}
		:crystal_hall_1 {:des "You are in a damp hall that leads south and also back east.",
							:con {:e :mush_room_entrance, :s :crystal_hall_2}
							:rinv {:crystal {:des "a fist sized crimson crystal" :regex #"crimson crystal|crystal"}}}
		:crystal_hall_2 {:des "You are in a nondescript hallway that leads to the north. There is a thick iron door to the west, but luckily it has rusted from the moisture, and there is a hole big enough to climb through in the center of it.",
							:con {:n :crystal_hall_1, :w :crystal_room}
							:rinv {}}
		:crystal_room {:des "You find yourself in a large square room. A strange contraption stands in the center of the room. It has wires and tubes all running into the walls away from an empty holder for something about the size of your fist. A hallway leads east.",
							:con {:n :overlook_ladder, :e :crystal_hall_2}
							:rinv {}}
		:overlook_ladder {:des "You are in a tiny room. A ladder leads up into a tube.",
							:con {:s :crystal_room, :u :mineshaft_overlook_2}
							:rinv {}}
		:bee_hall {:des "You are in a hallway that leads to the north where there is a door with a golden keyhole in it. To the west there is a crack in the wall just large enough for you to crawl through. You hear an ominous humming sound coming from inside the crack. The hallway also goes back to the south, but the path is blocked by a gargantuan stone slab.",
							:con {:n :bee_ladder, :w :bee_nest}
							:rinv {:rotten_wood {:des "a chunk of rotten wood" :regex #"chunk of rotten wood|chunk of wood|rotten wood|wood"}}}
		:bee_nest {:des "You are in a large crack in the wall of a hallway. Another crack branches off to the south, but the way is blocked by a bee's nest in and out of which are flying a multitude of angry looking bees. There is an exit to the east.",
							:con {:e :bee_hall, :s :bee_crack}
							:rinv {}
							:robj {:bees ""}}
		:bee_crack {:des "You are in a small crack in a wall. There is an exit to the north.",
							:con {:n :bee_nest}
							:rinv {:gold_key {:des "a gold key" :regex #"gold key|key"}}}
		:bee_ladder {:des "You are in small room with a ladder leading up. You can make out a faint light at the top of the ladder. There is a door to the south.",
							:con {:u :boulder_field, :s :bee_hall}
							:rinv {}}
		:boulder_field {:des "You find yourself in a grassy field. In the center of the field stands a huge boulder. It has been worn smooth by the wind and looks unclimbable. Paths lead north and south through the thick wilderness of forest that surrounds the clearing, and a ladder leads down, into the ground.",
							:con {:d :bee_ladder, :n :sunf, :s :cath_courtyard}
							:rinv {}}
		:sunf {:des "You find yourself in a field of sunflowers that are each at least double your height. They are all facing towards the east. The edge of the sunflowers is to the south, and to the west is a path that leads through the sunflowers.",
							:con {:s :boulder_field, :w :sunf_clearing}
							:rinv {}}
		:sunf_clearing {:des "You are in a clearing in a field of sunflowers. A path leads to the east.",
							:con {:e :sunf}
							:rinv {:iron_key {:des "a large iron key" :regex #"iron key|key"}}}
		:cath_courtyard {:des "You are in the very small courtyard of a very small cathedral. So small, in fact, that you would have to duck in order to pass through the ornate double doors that lie to the east. A path leads to the north.",
							:con {:n :boulder_field, :e :cath_nave}
							:rinv {}}
		:cath_nave {:des "You are in the nave (large part where the people sit; who taught you history?) of a very small cathedral. So small, in fact, that your head almost touches the ceiling. The exit lies to the west, and to the east is a very small altar that stands in the center of the cathedral.",
							:con {:w :cath_courtyard, :e :cath_altar}
							:rinv {}}
		:cath_altar {:des "You are at the very small altar in the center of a very small cathedral. So small, in fact, that you have to stoop slightly, and cannot stand up straight for fear of breaking the roof of the cathedral. To the west there is the nave of the cathedral, to the east, the choir, and to the north and south, the transepts.",
							:con {:w :cath_nave, :n :cath_ntransc, :s :cath_stransc, :e :cath_choir}
							:rinv {}}
		:cath_choir {:des "You are in the very small choir (the part behind the altar where the relics are; who taught you history?) of a very small cathedral. There are five round chapels (small areas where the relics are kept; who taught you history?) here, but sadly, they are all empty, the relics probably having been stolen by some band of thieves long ago. The altar lies to the west",
							:con {:w :cath_altar}
							:rinv {}}
		:cath_ntransc {:des "You are in the north part of a very small transept (the part of the cathedral crossing at a right angle to the nave; who taught you history?) in a tiny cathedral. There is a huge and magnificent stained glass window on the wall that is to the north. A metal plaque on the wall says: \"Drim denok nus yek\" The altar lies to the south.",
							:con {:s :cath_altar}
							:rinv {}}
		:cath_stransc {:des "You are in the south part of a tiny transept (the part of the cathedral crossing at a right angle to the nave; who taught you history?) in a very small cathedral. There is a large, oaken, ironbound trapdoor on the floor. The altar lies to the north.",
							:con {:n :cath_altar, :d :cath_crypt_web}
							:rinv {}}
		:cath_crypt_web {:des "You are in an underground tunnel that leads south. It is completely filled with spider webs. A ladder leads up and out of the tunnel.",
							:con {:u :cath_stransc, :s :cath_crypt_main}
							:rinv {}}
		:cath_crypt_main {:des "You are in a cramped underground room with many stone coffins lining the walls. There is a very faint odor of rot in the air, overridden by a strong smell of dust and old things. On one of the coffins there is a strange carving of what looks like a dagger next to a small bottle. Hallways extend out from the room in all directions.",
							:con {:n :cath_crypt_web, :s :cath_crypt_s, :w :cath_crypt_w, :e :cath_hall}
							:rinv {}}
		:cath_crypt_w {:des "You are in a small underground room with stone walls, and a stone floor. There is a strong smell of dust in the air. A hallway leads to the east.",
							:con {:e :cath_crypt_main}
							:rinv {}}
		:cath_crypt_s {:des "You are in a small underground room that is slightly damp and smells like damp dirt. A hallway leads to the north.",
							:con {:n :cath_crypt_main}
							:rinv {}}
		:cath_hall {:des "You are in a hall that runs east to west. To the east, you can see a slightly bright light.",
							:con {:e :cath_stairs, :w :cath_crypt_main}
							:rinv {}}
		:cath_stairs {:des "You are at the bottom of a long flight of stairs that lead up. At the top you can see what appears to be sunlight, but it is a little brighter, and much whiter.",
							:con {:w :cath_hall, :u :snow_forest}
							:rinv {}}
		:snow_forest {:des "You find yourself in a snow-covered forest. The evergreen trees shine in the harsh winter sun, and the snow glitters like a thousand tiny diamonds. Wow, that was sorta corny. A stone staircase leads down into the ground, and to the north, there is a clearing of rocky ground.",
							:con {:n, :snow_cliff, :d :cath_stairs}
							:rinv {}}
		:snow_cliff {:des "You are in a small patch of rocky ground in a thick forest of snow-covered pine trees. To the north is the edge of a cliff, and you can see no easier way down than a set of small indentations, where many generations of footsteps have climbed up and down the sheer cliff face. To the south there is a path through the forest.",
							:con {:d :snow_cliff_bottom, :s :snow_forest}
							:rinv {}}
		:snow_cliff_bottom {:des "You are at the bottom of a sheer cliff made of smooth, gray rock. There is no way up because the few footholds that were there have recently been broken by some bumbling idiot of an adventurer. To the south is an entrance to a pitch black cave that leads into the cliff. The forest looks a bit thinner to the east.",
							:con {:s :snow_cliff_cave_passage, :e :snow_cliff_dead}
							:rinv {}}
		:snow_cliff_dead {:des "You are in a dense forest at the bottom of a sheer cliff. The forest is impenetrable and there is no way up the cliff. To the west lies a small clearing at the bottom of the cliff.",
							:con {:w :snow_cliff_bottom}
							:rinv {}}
		:snow_cliff_cave_passage {:des "You are in a long dark cave that leads straight south, into the depths of the cliff. The exit to the outside lies to the north.",
							:con {:n :snow_cliff_bottom, :s :snow_cliff_cave_1}
							:rinv {}}
		:snow_cliff_cave_1 {:des "You are in a round cave-like room. A passage leads to the north, out of the cave, and a doorway leads to the west. To the east is another doorway, and you can see the room beyond it is somehow lit with bright sunlight.",
							:con {:e :snow_cliff_cave_sun, :n :snow_cliff_cave_passage, :w :snow_cliff_cave_web}
							:rinv {}}
		:snow_cliff_cave_web {:des "You are in a smallish dead-end hallway that has a huge spider web across it. The rest of the cave lies to the east.",
							:con {:e :snow_cliff_cave_1}
							:rinv {}}
		:snow_cliff_cave_sun {:des "You are in a square stone-walled room with a hole in the ceiling. A beam of warm sunlight is shining through the hole and it lands on an empty stone pedestal. A hallway goes to the south, and there is a doorway to the west",
							:con {:w :snow_cliff_cave_1, :s :snow_cliff_cave_passage_2}
							:rinv {}}
		:snow_cliff_cave_passage_2 {:des "You find yourself in a stone passageway that goes to the south and to the north.",
							:con {:n :snow_cliff_cave_sun, :s :mird_entrance}
							:rinv {}}
		:mird_entrance {:des "You are in a tiny anteroom. sticks and small bits of jewelry and precious metals are littered about the floor. A hallway goes north, and there is a doorway to the south. From that direction, you hear a faint breathing noise.",
							:con {:s :mird, :n :snow_cliff_cave_passage_2}
							:rinv {}}
		:mird {:des "You are in a expansive cave with a floor covered with sticks and even a few bones. In the center of the room is a giant nest. In the nest is a huge hoard of riches, gold, and jewels, guarded by a huge monster with head and wings of a bird, but the body, tail, and legs of a hairy monkey. The monster watches you threateningly. There is a hallway to the north, and to the south there is a massive doorway through which you can see green grass and sunlight.",
							:con {:s :mird_hillb, :n :mird_entrance}
							:robj {:mird ""}}
		:mird_hillb {:des "You are at the bottom of a super steep grassy slope that lies to the north. To the west there is a path that leads through the forest that surrounds you.",
							:con {:w :elevator_field, :n :mird}
							:rinv {}}
		:elevator_field {:des "You are in a grassy, shrub-filled field. To the north there is the rusted shell of an old elevator cage, a quarter buried in the ground. You see no sign of how it could have gotten there. You can hear a faint squawking noise through the thick forest to the North. Paths lead to the east and south, away from the clearing.",
							:con {:e :mird_hillb, :n :outside_elevator, :s :ravine_path}
							:rinv {}}
		:outside_elevator {:des "You are inside the rusted shell of an old elevator cage that is one quarter buried in the ground. It is worn down and rusted with age, and on one wall you see a tiny keyhole, the words written beneath it so rusted that there are only a few letters visible: \"I  c se  f e  rgen y.\" The exit lies to the south.",
							:con {:s :elevator_field}
							:rinv {}}
		:ravine_path {:des "You are on a path that leads away from a clearing that is to the north. The path leads down a steep slope and into a ravine. You can hear quiet trickling water from that direction.",
							:con {:d :ravine_1, :n :elevator_field}
							:rinv {}}
		:ravine_1 {:des "You are in a red rock ravine that has a large stream running through the center of it. A path leads up and out of the ravine, and another path leads north, toward a small cave in the side of the ravine. To the west, there is a small footbridge that crosses the stream.",
							:con {:u :ravine_path, :n :ravine_cave_en, :w :ravine_2}
							:rinv {}}
		:ravine_cave_en {:des "You are at the mouth of a small cave in the side of the reddish rock of the ravine. The main path lies to the south, and the cave is to the east.",
							:con {:s :ravine_1, :e :ravine_cave}
							:rinv {}}
		:ravine_cave {:des "You are in a cave carved out of the red rock of a ravine. There is nothing in the cave but a pictogram on the wall: a square divided into four equal sections. The section on the top left has an up-pointing arrow in its center. The cave exit lies to the west",
							:con {:w :ravine_cave_en,}
							:rinv {}}
		:ravine_2 {:des "You are by the side of a large stream that is flowing through a ravine made of red rock. To the east, a bridge bridges the stream, and a path leads up and out of the ravine.",
							:con {:u :ravine_ex, :e :ravine_1}
							:rinv {}}
		:ravine_ex {:des "You find yourself on the edge of a ravine that has a large stream running through it. All around you is thick muddy marshlands, but a path leads to the north, down a slight incline. A path also goes back down into the ravine.",
							:con {:n :swamp_path, :d :ravine_2}
							:rinv {}}
		:swamp_path {:des "You are on a path that goes east and south, through the dense, sticky swamp that surrounds you. To the south, the path goes up a slight incline, and to the east it goes down",
							:con {:s :ravine_ex, :e :swamp_en}
							:rinv {}}
		:swamp_en {:des "You are in the entrance to a large open area of swampland. The swampy water comes up to your ankles, and tall swamp grass surrounds you. You smell the strong odor of mud rich in organic materials. To the north is the large open area of swampland, and a path lies to the west.",
							:con {:n :swamp_s, :w :swamp_path}
							:rinv {}}
		:swamp_s {:des "You are in the southern part of a large area of swampy land. To the north, there is a house that is on stilts so it can stand in the wet environment of the swamp.",
							:con {:n :swamp_house, :s :swamp_en}
							:rinv {}}
		:swamp_e {:des "asdf. This is a filleeeer, filler yeah, not enough content for a whole video. LOL.",
							:con {}
							:rinv {}}
		:swamp_house {:des "You are in the cramped space under a small wooden house that is built up on stilts. Open swampland lies to the south and east, and a ladder leads up into the house.",
							:con {:u :swamp_house_dr, :s :swamp_s, :e :swamp_e}
							:rinv {}}
		:swamp_house_dr {:des "You are in the entrance room of a square wooden house that is on stilts. There is a small bench in the corner. There are more rooms to the north and west, and a ladder leads down, out of the house.",
							:con {:d :swamp_house, :w :swamp_house_dl, :n :swamp_house_ur}
							:rinv {}}
		:swamp_house_ur {:des "You are in the dining room of an abandoned wooden house on stilts. Here there is a small table and two half-rotten chairs. There are rooms to the south and west",
							:con {:s :swamp_house_dr, :w :swamp_house_ul}
							:rinv {}}
		:swamp_house_dl {:des "You are in the kitchen of a small wooden house on stilts. There is almost nothing in this room except a counter. There are more rooms to the north and to the east.",
							:con {:n :swamp_house_ul, :e :swamp_house_dr}
							:rinv {}}
		:swamp_house_ul {:des "You are in the bedroom of a small house on stilts. There is a tiny straw cot here. Other rooms are to the south and east.",
							:con {:s :swamp_house_dl, :e :swamp_house_ur, :u :swamp_house_roof}
							:rinv {}}
		:swamp_house_roof {:des "Another filleeer, filler yeah, not enough content for a whole video. LOL LOL LOL.",
							:con {}
							:rinv {}}
		:end_main {:des "You are in a hallway with smooth, bright white walls. It leads north into a white room, and south to a door.",
							:con {:s :space, :n :end_1}
							:rinv {}}
		:space {:des "You float along in this huge room untill a sudden draft pulls you back to the doorway. You stand, unable to comprehent the sheer beauty and depth of the image that you are seeing. Your small brain's neurons are exploding, having reached their capacity for thought, and you are quickly melting, sinking into the floor. You are a puddle. You are very small. You hear a voice–your voice–say \"Wake up you moron! There's no time for silly fantasies\"! You snap to attention, dreading the rest of your journey in life, knowing that everything left will look gray and bland compared to the experience you have just had. \"For God's Sake man!\" Says the voice, \"Get the *bleep* up!\"",
							:con {:n :end_main}
							:rinv {}}
		:end_1 {:des "You are in the south end of an empty room with smooth, bright white walls. To the north, on the other side of the room, is a shiny black pedestal. On it sits a large green gem.",
							:con {:s :end_main, :n :end_2}
							:rinv {}}
		:end_2 {:des "You are in the north end of a room with bright white walls. There is a shiny black pedestal here. On it sits a large, glowing green gem–the Ojeran Gemerald.",
							:con {:s :end_1}
							:rinv {:gem {:des "a large green gem" :regex #"large green gem|green gem|large gem|gem"}}}
		:credits_1 {:des "You are floating in the black abyss. Huge white letters are hanging in front of you. They say:\n\nCredits\n\nCoder:\nWill Harris-Braun\n\nWritten by:\nWill Harris-Braun\n\nMentor:\nEric Harris-Braun\n\n(Move in any direction)",
							:con {:n :credits_2, :s :credits_2, :e :credits_2, :w :credits_2, :u :credits_2, :d :credits_2,}
							:rinv {}}
		:credits_2 {:des "\nTesters:\nTimo Carlin-Burns\nJesse Harris-Braun\nMilo Robbins-Zust\nCasen Waldorf\nJack Gibson\nAsa Mervis\nLucas Braun\nEric Harris-Braun\nEllen Harris-Braun\nEugene Braun\nArthur Brock\nEric Bear\n\n(Move in any direction)",
							:con {:n :credits_3, :s :credits_3, :e :credits_3, :w :credits_3, :u :credits_3, :d :credits_3,}
							:rinv {}}
		:credits_3 {:des "\nMusic:\nWill Harris-Braun\n\nEnthusiasts:\nTimo Carlin-Burns\nLucas Braun\nJulian Lindenmaier\nJack Gibson\n\nInspirations:\nZork\nColossal Cave Adventure\n\nTools:\nClojure\nGitHub\nOSX\nUbuntu\nText Mate\nPen and Pencil\nMy Brain\n\n(Move in any direction)",
							:con {:n :credits_4, :s :credits_4, :e :credits_4, :w :credits_4, :u :credits_4, :d :credits_4,}
							:rinv {}}
		:credits_4 {:des "The End!",
							:con {:n :credits_4, :s :credits_4, :e :credits_4, :w :credits_4, :u :credits_4, :d :credits_4,}
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
		  matched-items (map (fn [[item pattern]] [item (re-find pattern item-str)]) inv-patterns)
		  found-items (filter (fn [[_ match]] (not (nil? match))) matched-items)
		  sorted-items (sort-by (fn [[_ s]] (- (count s)))  found-items)
		  num-items (count found-items)]
;	 (println (str "Patterns: " inv-patterns))
;	 (println (str "item-str: " item-str))
;	 (println (str "matched-items: " matched-items))
;	 (println (str "found-items: " found-items))
;	 (println (str "sorted-items" sorted-items))
	 (cond (= num-items 1) (first (first found-items))
		   (> num-items 1) (let [[first-item first-match] (first sorted-items)
								 [second-item second-match] (second sorted-items)]
;	 							(println (str "First: " first-item))
;		 						(println (str "Second: " second-item))
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
			(let [dess (get-inventory-descriptions inv)]
				(if (not (= dess "")) (println (str "You see:\n" dess))))
			)))

(defn print-items-in-room []
	(print-stuff-in-room :rinv)
	(print-stuff-in-room :robj)
	)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;defining not done (for quitting)
(defn set-not-done [v]
	(def not_done v))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; COMMANDS
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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; CORE
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
	(println "In the game, you use text commands to move around and interact with the world. For example, use the command \"n\" to move North, \"u\" to move up, etc. Playing with a pad of paper and a pencil nearby might be helpful.")
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
	(initialize)
	(while not_done
		(l)))

; run on run
(-main)
