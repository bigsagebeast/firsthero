package com.bigsagebeast.hero.roguelike.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bigsagebeast.hero.roguelike.game.Dice;
import com.bigsagebeast.hero.roguelike.game.GameEntities;
import com.bigsagebeast.hero.roguelike.world.dungeon.DungeonPlan;
import com.bigsagebeast.hero.roguelike.world.dungeon.DungeonPlanFloor;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.roguelike.world.dungeon.Room;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.Generator;
import com.bigsagebeast.hero.roguelike.world.proc.environment.ProcStairs;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;

public class DungeonGenerator {

	public static final int NUM_MONSTERS = 15;
	public static final int NUM_ITEMS = 6;
	public static final int SPAWN_CHANCE_PER_ROOM = 50;
	public Map<String, Level> levels = new HashMap<String, Level>();
	
	public Level getLevel(String key) {
		if (!levels.containsKey(key)) {
			generateClassic(key);
		}

		return levels.get(key);
	}

	public static void populate(Level level) {
		if (level.threat < 0) {
			return;
		}

		for (Room r : level.rooms) {
			// TODO separate specialCorridors from special spawn rules
			if (Game.random.nextInt(100) < SPAWN_CHANCE_PER_ROOM && !r.roomType.specialCorridors) {
				String chosenMonster = getAllowedMonster(null, level, true);
				if (chosenMonster == null) {
					System.out.println("No allowed monsters");
					return;
				}
				Entity e = Bestiary.create(chosenMonster);
				e.wanderer = true;
				e.getTactic().canWander = true;
				Point pos = level.findEmptyTileInRoomForMover(r.roomId, e);
				level.addEntityWithStacking(e, pos, false);
				int packSize = (int) (Bestiary.map.get(chosenMonster).packSize * (Game.random.nextFloat() + 0.4f));
				for (int j = 1; j < packSize; j++) {
					Point packSpawnPos = level.findPackSpawnTile(pos, Bestiary.map.get(chosenMonster).packSpawnArea);
					if (packSpawnPos != null) {
						Entity packmember = Bestiary.create(chosenMonster);
						packmember.wanderer = true;
						level.addEntityWithStacking(packmember, packSpawnPos, false);
					}
				}
			}
		}
		List<String> boons = getBoons(level);
		for (int i=0; i<NUM_ITEMS; i++) {
			Point pos = level.findOpenTile();
			Entity e;
			if (!boons.isEmpty()) {
				e = Itempedia.createWithRandomBeatitude(boons.remove(0));
			} else {
				e = spawnLoot(level);
			}
			if (e == null) {
				continue;
			}
			level.addEntityWithStacking(e, pos, false);
		}
		int goldPiles = Game.random.nextInt(4) + 4;
		for (int i=0; i<goldPiles; i++) {
			int quantity = Dice.roll(level.threat * 2 + 1, 8, 5);
			Point pos = level.findOpenTile();
			Entity e = Itempedia.create("gold", quantity);
			level.addEntityWithStacking(e, pos, false);
		}
	}



	public static List<String> getAllowedMonsters(List<String> requiredTags, int minThreat, int maxThreat, Level level, boolean wandering) {
		if (level.threat < 0) {
			return Collections.EMPTY_LIST;
		}
		ArrayList<String> allowedEntities = new ArrayList<>();
		for (String key : Bestiary.map.keySet()) {
			Phenotype p = Bestiary.map.get(key);
			if (p.peaceful) continue;
			if ((p.wandering || !wandering) && p.threat >= minThreat && p.threat <= maxThreat) {
				if (requiredTags != null) {
					boolean missingTag = false;
					for (String requiredTag : requiredTags) {
						if (!p.tags.contains(requiredTag)) {
							missingTag = true;
							break;
						}
					}
					if (missingTag) {
						continue;
					}
				}
				if (GameEntities.overpopulated(p)) {
					continue;
				}
				for (int i = 0; i < p.frequency; i++) {
					allowedEntities.add(key);
				}
			}
		}
		return allowedEntities;
	}

	public static String getAllowedMonster(List<String> requiredTags, Level level, boolean wandering) {
		return getAllowedMonster(requiredTags, level, 0, wandering);
	}

	public static String getAllowedMonster(List<String> requiredTags, Level level, int threatMod, boolean wandering) {
		int min = Math.max(0, threatMod + level.threat/2);
		int max = Math.max(0, threatMod + level.threat);
		String key = getAllowedMonster(requiredTags, min, max, level, wandering);
		if (key == null) {
			key = getAllowedMonster(requiredTags, 0, max, level, wandering);
		}
		return key;
	}

	public static String getAllowedMonster(List<String> requiredTags, int minThreat, int maxThreat, Level level, boolean wandering) {
		List<String> allowedEntities = getAllowedMonsters(requiredTags, minThreat, maxThreat, level, wandering);
		if (allowedEntities.isEmpty()) {
			allowedEntities = getAllowedMonsters(requiredTags, 0, maxThreat, level, wandering);
		}
		if (allowedEntities.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("No monsters within threat range 0 or " + minThreat + " to " + maxThreat + " with tags:");
			for (String tag : requiredTags) {
				sb.append(" " + tag);
			}
			//GameLoop.warn(sb);
			return null;
		}
		int index = Game.random.nextInt(allowedEntities.size());
		return allowedEntities.get(index);
	}

	public static List<String> getAllowedItems(Level level) {
		if (level.threat < 0) {
			return Collections.EMPTY_LIST;
		}
		int minLevelAllowed = 0;
		int maxLevelAllowed = level.threat + 1;
		ArrayList<String> allowedEntities = new ArrayList<>();
		for (String key : Itempedia.map.keySet()) {
			ItemType p = Itempedia.map.get(key);
			if (p.level < 0) continue;
			if (p.level >= minLevelAllowed && p.level <= maxLevelAllowed) {
				// TODO cache this
				for (int i = 0; i < p.frequency; i++) {
					allowedEntities.add(key);
				}
			}
		}
		return allowedEntities;
	}

	public static String getAllowedItem(List<String> requiredTags, int minThreat, int maxThreat, Level level) {
		List<ItemType> allowedEntities = getTaggedItemTypes(requiredTags, Collections.EMPTY_LIST, minThreat, maxThreat, level);
		if (allowedEntities.isEmpty()) {
			return null;
		}
		int index = Game.random.nextInt(allowedEntities.size());
		return allowedEntities.get(index).keyName;
	}

	public static String getAllowedItem(Level level) {
		List<String> allowedEntities = getAllowedItems(level);
		if (allowedEntities.isEmpty()) {
			return null;
		}
		int index = Game.random.nextInt(allowedEntities.size());
		return allowedEntities.get(index);
	}

	public static List<ItemType> getTaggedItemTypes(List<String> requiredTags, List<String> forbiddenTags, int minThreat, int maxThreat, Level level) {
		List<ItemType> taggedTypes = new ArrayList<>();
		// first, find tag restrictions - these are absolute
		for (ItemType it : Itempedia.map.values()) {
			boolean disallowed = false;
			for (String tag : requiredTags) {
				if (!it.tags.contains(tag)) {
					disallowed = true;
					break;
				}
			}
			for (String tag : forbiddenTags) {
				if (it.tags.contains(tag)) {
					disallowed = true;
					break;
				}
			}
			if (disallowed) {
				continue;
			}
			taggedTypes.add(it);
		}
		List<ItemType> levelledTypes = new ArrayList<>();
		// next, try to find something between min and max threat - less absolute
		for (ItemType it : taggedTypes) {
			if (it.level >= minThreat && it.level <= maxThreat) {
				levelledTypes.add(it);
			}
		}
		// if nothing in range, then search lower
		if (levelledTypes.isEmpty()) {
			for (int i=minThreat-1; i>=0; i--) {
				for (ItemType it : taggedTypes) {
					if (it.level == i) {
						levelledTypes.add(it);
					}
				}
				if (levelledTypes.isEmpty()) {
					break;
				}
			}
		}
		return levelledTypes;
	}

	public static String getUnspawnedItem(List<String> requiredTags, List<String> forbiddenTags, Level level) {
		List<ItemType> types = getTaggedItemTypes(requiredTags, forbiddenTags, level.threat, level.threat, level);
		types = types.stream().filter(it -> it.spawnCount == 0).collect(Collectors.toList());
		if (types.isEmpty()) {
			types = getTaggedItemTypes(requiredTags, forbiddenTags, level.threat / 2, level.threat, level);
			types = types.stream().filter(it -> it.spawnCount == 0).collect(Collectors.toList());
		}
		List<ItemType> typesFiltered = new ArrayList<>();
		for (ItemType type : types) {
			LoadProc procLoader = type.procLoaders.stream().filter(pl -> pl.procName.equals("item.book.ProcBookSpell")).findAny().orElse(null);
			if (procLoader != null) {
				String spell = procLoader.fields.get("spell");
				if (Game.spellbook.spells.contains(spell)) {
					continue;
				}
			}
			typesFiltered.add(type);
		}
		Collections.shuffle(typesFiltered);

		if (typesFiltered.isEmpty()) {
			return null;
		}
		return typesFiltered.get(0).keyName;
	}

	public static List<String> getBoons(Level level) {
		List<String> boons = new ArrayList<>();
		String weapon = getUnspawnedItem(Arrays.asList("weapon", "generic-fantasy"), Arrays.asList(), level);
		String armor = getUnspawnedItem(Arrays.asList("armor", "generic-fantasy"), Arrays.asList(), level);
		String jewelry = getUnspawnedItem(Arrays.asList("jewelry", "generic-fantasy"), Arrays.asList(), level);
		String ranged = getUnspawnedItem(Arrays.asList("ranged", "generic-fantasy"), Arrays.asList(), level);
		String spellbook = getUnspawnedItem(Arrays.asList("spellbook", "generic-fantasy"), Arrays.asList(), level);
		if (weapon != null) {
			boons.add(weapon);
		}
		if (armor != null) {
			boons.add(armor);
		}
		List<Integer> choice = Arrays.asList(0, 1, 2);
		Collections.shuffle(choice);
		for (Integer i : choice) {
			if (boons.size() >= 3) {
				break;
			}
			if (i == 0 && jewelry != null) {
				boons.add(jewelry);
			}
			if (i == 1 && ranged != null) {
				boons.add(ranged);
			}
			if (i == 2 && spellbook != null) {
				boons.add(spellbook);
			}
		}
		return boons;
	}

	public static Entity spawnLoot(Level level) {
		String itemKey = getAllowedItem(level);
		if (itemKey == null) {
			System.out.println("No allowed items");
			return null;
		}
		ItemType itemType = Itempedia.get(itemKey);
		int quantity = itemType.minCount + Game.random.nextInt(itemType.maxCount - itemType.minCount + 1);
		Entity loot = Itempedia.createWithRandomBeatitude(itemKey, quantity);
		return loot;
	}

	public void generateClassic(String key) {
		String[] components = key.split("\\.");
		if (components.length != 2) {
			throw new RuntimeException("Invalid level name: " + key);
		}
		String dungeon = components[0];
		int depth = Integer.parseInt(components[1]);

		DungeonPlan plan = Game.dungeonPlans.get(dungeon);
		DungeonPlanFloor planFloor = plan.floor(depth - 1);

		Generator generator = new Generator();
		Level level = null;
		while (level == null) {
			level = generator.generate(key, planFloor, 70, 50, depth);
		}
		level.neverbeastCountdown = 1500;

		// Remember to add the level to the map before generating it
		levels.put(key, level);
		populate(level);
		if (depth == 1) {
			generator.addUpstairTo("out");
		} else {
			generator.addUpstairTo(dungeon + "." + (depth-1));
		}
		generator.addDownstairTo(dungeon + "." + (depth+1));
	}
	
	public void generateFromFile(String key, String filename) {
		FileHandle file = Gdx.files.internal(filename);
		BufferedReader reader = new BufferedReader(file.reader());
		generateFromData(key, reader);
	}
	
	public void generateFromData(String key, BufferedReader input) {
		String version;
		try {
			version = input.readLine();
			if (version.equals("V1")) {
				levels.put(key, generateFromDataV1(key, input));
				return;
			} else if (version.equals("V2")) {
				levels.put(key, generateFromDataV2(key, input));
				return;
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read dungeon data", e);
		}
		throw new RuntimeException("Unknown dungeon data version");
	}
	
	public Level generateFromDataV1(String name, BufferedReader input) throws IOException {
		String terrainHeader = input.readLine();
		String[] terrainSplit = terrainHeader.split(" ");
		if (terrainSplit.length != 2 || !terrainSplit[0].equals("TERRAIN")) {
			throw new RuntimeException("Invalid format: terrain header");
		}
		int terrainCount = Integer.parseInt(terrainSplit[1]);
		Map<String, Terrain> terrainMap = new HashMap<String, Terrain>();
		for (int i=0; i<terrainCount; i++) {
			String terrainString = input.readLine();
			String mapFrom = terrainString.substring(0, 1);
			String terrainName = terrainString.substring(2);
			Terrain mapTo = Terrain.map.get(terrainName);
			terrainMap.put(mapFrom, mapTo);
		}

		String mapHeader = input.readLine();
		String[] mapSplit = mapHeader.split(" ");
		if (mapSplit.length != 3 || !mapSplit[0].equals("MAP")) {
			throw new RuntimeException("Invalid format: map header");
		}
		int width = Integer.parseInt(mapSplit[1]);
		int height = Integer.parseInt(mapSplit[2]);
		Level level = new Level(name, width, height);

		for (int y=0; y<height; y++) {
			String row = input.readLine();
			for (int x=0; x<width; x++) {
				Terrain cellTerrain = terrainMap.get(row.substring(x, x+1));
				level.cell(x, y).terrain = cellTerrain;
			}
		}

		String creatureHeader = input.readLine();
		String[] creatureHeaderSplit = creatureHeader.split(" ");
		if (creatureHeaderSplit.length != 2 || !creatureHeaderSplit[0].equals("CREATURES")) {
			throw new RuntimeException("Invalid format: creatures header");
		}
		int creatureCount = Integer.parseInt(creatureHeaderSplit[1]);
		for (int i=0; i<creatureCount; i++) {
			String creatureString = input.readLine();
			String[] creatureSplit = creatureString.split(" ");
			String phenotype = creatureSplit[0];
			int x = Integer.parseInt(creatureSplit[1]);
			int y = Integer.parseInt(creatureSplit[2]);
			String creatureName = null;
			if (creatureSplit.length > 3) {
				creatureName = creatureSplit[3].replace("_", " ");
			}
			Entity c = Bestiary.create(phenotype, creatureName);
			level.addEntityWithStacking(c, new Point(x, y));
		}

		String transitionHeader = input.readLine();
		String[] transitionHeaderSplit = transitionHeader.split(" ");
		if (transitionHeaderSplit.length != 2 || !transitionHeaderSplit[0].equals("TRANSITIONS")) {
			throw new RuntimeException("Invalid format: transitions header");
		}
		int transitionCount = Integer.parseInt(transitionHeaderSplit[1]);
		for (int i=0; i < transitionCount; i++) {
			String transitionString = input.readLine();
			String[] transitionSplit = transitionString.split(" ");
			String transitionType = transitionSplit[0];
			int fromX = Integer.parseInt(transitionSplit[1]);
			int fromY = Integer.parseInt(transitionSplit[2]);
			String destination = transitionSplit[3];
			int toX = Integer.parseInt(transitionSplit[4]);
			int toY = Integer.parseInt(transitionSplit[5]);

			if (transitionType.equals("up")) {
				Entity stairsUp = Itempedia.create("feature.stairsUp");
				ProcStairs procStairs = (ProcStairs)stairsUp.getProcByType(ProcStairs.class);
				procStairs.upToMap = destination;
				procStairs.upToPos = new Point(toX, toY);
				level.addEntityWithStacking(stairsUp, new Point(fromX, fromY));
			} else if (transitionType.equals("down")) {
				Entity stairsDown = Itempedia.create("feature.stairsDown");
				ProcStairs procStairs = (ProcStairs)stairsDown.getProcByType(ProcStairs.class);
				procStairs.downToMap = destination;
				procStairs.downToPos = new Point(toX, toY);
				level.addEntityWithStacking(stairsDown, new Point(fromX, fromY));
			} else {
				throw new RuntimeException("Invalid transition type: " + transitionType);
			}
		}
		return level;
	}


	public Level generateFromDataV2(String name, BufferedReader input) throws IOException {
		String terrainHeader = input.readLine();
		String[] terrainSplit = terrainHeader.split(" ");
		if (terrainSplit.length != 2 || !terrainSplit[0].equals("TERRAIN")) {
			throw new RuntimeException("Invalid format: terrain header");
		}
		int terrainCount = Integer.parseInt(terrainSplit[1]);
		Map<String, Terrain> terrainMap = new HashMap<String, Terrain>();
		for (int i=0; i<terrainCount; i++) {
			String terrainString = input.readLine();
			String mapFrom = terrainString.substring(0, 1);
			String terrainName = terrainString.substring(2);
			Terrain mapTo = Terrain.map.get(terrainName);
			terrainMap.put(mapFrom, mapTo);
		}

		String mapHeader = input.readLine();
		if (!mapHeader.equals("MAP")) {
			throw new RuntimeException("Invalid format: map header");
		}
		List<String> mapChunks = new ArrayList<>();
		String lastString = input.readLine();
		while (!lastString.equals("END")) {
			mapChunks.add(lastString);
			lastString = input.readLine();
		}
		int width = mapChunks.get(0).length();
		int height = mapChunks.size();
		Level level = new Level(name, width, height);

		for (int y=0; y<height; y++) {
			String row = mapChunks.get(y);
			for (int x=0; x<width; x++) {
				Terrain cellTerrain = terrainMap.get(row.substring(x, x+1));
				level.cell(x, y).terrain = cellTerrain;
			}
		}

		HashMap<String, String> creatureTypes = new HashMap<>();
		String creatureHeader = input.readLine();
		if (!creatureHeader.equals("CREATURES")) {
			throw new RuntimeException("Invalid format: creature header");
		}
		lastString = input.readLine();
		while (!lastString.equals("END")) {
			String[] creatureSplit = lastString.split(" ");
			if (creatureSplit.length != 2) {
				throw new RuntimeException("Invalid format: creature string");
			}
			creatureTypes.put(creatureSplit[0], creatureSplit[1]);
			lastString = input.readLine();
		}

		HashMap<String, String> itemTypes = new HashMap<>();
		String itemHeader = input.readLine();
		if (!itemHeader.equals("ITEMS")) {
			throw new RuntimeException("Invalid format: creature header");
		}
		lastString = input.readLine();
		while (!lastString.equals("END")) {
			String[] itemSplit = lastString.split(" ");
			if (itemSplit.length != 2) {
				throw new RuntimeException("Invalid format: item string");
			}
			itemTypes.put(itemSplit[0], itemSplit[1]);
			lastString = input.readLine();
		}

		String entityMapHeader = input.readLine();
		if (!entityMapHeader.equals("ENTITYMAP")) {
			throw new RuntimeException("Invalid format: creature map header");
		}

		List<String> creatureChunks = new ArrayList<>();
		lastString = input.readLine();
		while (!lastString.equals("END")) {
			creatureChunks.add(lastString);
			lastString = input.readLine();
		}

		for (int y=0; y<height; y++) {
			String row = creatureChunks.get(y);
			for (int x=0; x<width; x++) {
				String phenotype = creatureTypes.get(row.substring(x, x+1));
				String itemType = itemTypes.get(row.substring(x, x+1));
				if (phenotype != null) {
					Entity creature = Bestiary.create(phenotype, null);
					level.addEntityWithStacking(creature, new Point(x, y));
				}
				if (itemType != null) {
					Entity item = Itempedia.create(itemType, null);
					level.addEntityWithStacking(item, new Point(x, y));
				}

			}
		}


		String transitionHeader = input.readLine();
		String[] transitionHeaderSplit = transitionHeader.split(" ");
		if (transitionHeaderSplit.length != 2 || !transitionHeaderSplit[0].equals("TRANSITIONS")) {
			throw new RuntimeException("Invalid format: transitions header");
		}
		int transitionCount = Integer.parseInt(transitionHeaderSplit[1]);
		for (int i=0; i < transitionCount; i++) {
			String transitionString = input.readLine();
			String[] transitionSplit = transitionString.split(" ");
			String transitionType = transitionSplit[0];
			int fromX = Integer.parseInt(transitionSplit[1]);
			int fromY = Integer.parseInt(transitionSplit[2]);
			String destination = transitionSplit[3];
			int toX = Integer.parseInt(transitionSplit[4]);
			int toY = Integer.parseInt(transitionSplit[5]);

			if (transitionType.equals("up")) {
				Entity stairsUp = Itempedia.create("feature.stairsUp");
				ProcStairs procStairs = (ProcStairs)stairsUp.getProcByType(ProcStairs.class);
				procStairs.upToMap = destination;
				procStairs.upToPos = new Point(toX, toY);
				level.addEntityWithStacking(stairsUp, new Point(fromX, fromY));
			} else if (transitionType.equals("down")) {
				Entity stairsDown = Itempedia.create("feature.stairsDown");
				ProcStairs procStairs = (ProcStairs)stairsDown.getProcByType(ProcStairs.class);
				procStairs.downToMap = destination;
				procStairs.downToPos = new Point(toX, toY);
				level.addEntityWithStacking(stairsDown, new Point(fromX, fromY));
			} else {
				throw new RuntimeException("Invalid transition type: " + transitionType);
			}
		}
		return level;
	}
}
