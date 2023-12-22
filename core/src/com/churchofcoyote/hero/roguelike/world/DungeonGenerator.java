package com.churchofcoyote.hero.roguelike.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.churchofcoyote.hero.roguelike.game.Dice;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.dungeon.generation.Brogue;
import com.churchofcoyote.hero.roguelike.world.dungeon.generation.Generator;
import com.churchofcoyote.hero.util.Point;

public class DungeonGenerator {

	public static final int NUM_MONSTERS = 15;
	public static final int NUM_ITEMS = 10;
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
		for (int i = 0; i < NUM_MONSTERS; i++) {
			String chosenMonster = getAllowedMonster(level);
			if (chosenMonster == null) {
				System.out.println("No allowed monsters");
				return;
			}
			Point pos = level.findOpenTile();
			Entity e = Game.bestiary.create(chosenMonster);
			e.wanderer = true;
			level.addEntityWithStacking(e, pos);
			int packSize = (int) (Bestiary.map.get(chosenMonster).packSize * (Game.random.nextFloat() + 0.4f));
			for (int j = 1; j < packSize; j++) {
				Point packSpawnPos = level.findPackSpawnTile(pos, Bestiary.map.get(chosenMonster).packSpawnArea);
				if (packSpawnPos != null) {
					Entity packmember = Game.bestiary.create(chosenMonster);
					packmember.wanderer = true;
					level.addEntityWithStacking(packmember, packSpawnPos);
				}
			}
		}
		for (int i=0; i<NUM_ITEMS; i++) {
			Point pos = level.findOpenTile();
			Entity e = spawnLoot(level);
			if (e == null) {
				continue;
			}
			level.addEntityWithStacking(e, pos);
		}
		int goldPiles = Game.random.nextInt(4) + 4;
		for (int i=0; i<goldPiles; i++) {
			int quantity = Dice.roll(level.threat * 2 + 1, 8, 5);
			Point pos = level.findOpenTile();
			Entity e = Game.itempedia.create("gold", quantity);
			level.addEntityWithStacking(e, pos);
		}
	}



	public static List<String> getAllowedMonsters(Level level) {
		if (level.threat < 0) {
			return Collections.EMPTY_LIST;
		}
		int minThreatAllowed = Math.max(0, level.threat - 1);
		int maxThreatAllowed = level.threat + 1;
		ArrayList<String> allowedEntities = new ArrayList<>();
		for (String key : Game.bestiary.map.keySet()) {
			Phenotype p = Game.bestiary.map.get(key);
			if (p.peaceful) continue;
			if (p.wandering && p.threat >= minThreatAllowed && p.threat <= maxThreatAllowed) {
				for (int i = 0; i < p.frequency; i++) {
					allowedEntities.add(key);
				}
			}
		}
		return allowedEntities;
	}

	public static String getAllowedMonster(Level level) {
		List<String> allowedEntities = getAllowedMonsters(level);
		if (allowedEntities.isEmpty()) {
			return null;
		}
		int index = Game.random.nextInt(allowedEntities.size());
		return allowedEntities.get(index);
	}

	public static List<String> getAllowedItems(Level level) {
		if (level.threat < 0) {
			return Collections.EMPTY_LIST;
		}
		int minLevelAllowed = Math.max(0, level.threat - 1);
		int maxLevelAllowed = level.threat + 1;
		ArrayList<String> allowedEntities = new ArrayList<>();
		for (String key : Game.itempedia.map.keySet()) {
			ItemType p = Game.itempedia.map.get(key);
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

	public static String getAllowedItem(Level level) {
		List<String> allowedEntities = getAllowedItems(level);
		if (allowedEntities.isEmpty()) {
			return null;
		}
		int index = Game.random.nextInt(allowedEntities.size());
		return allowedEntities.get(index);
	}

	public static Entity spawnLoot(Level level) {
		String itemKey = getAllowedItem(level);
		if (itemKey == null) {
			System.out.println("No allowed items");
			return null;
		}
		ItemType itemType = Itempedia.get(itemKey);
		int quantity = itemType.minCount + Game.random.nextInt(itemType.maxCount - itemType.minCount + 1);
		Entity loot = Game.itempedia.create(itemKey, quantity);
		return loot;
	}

	public void generateClassic(String key) {
		String[] components = key.split("\\.");
		if (components.length != 2) {
			throw new RuntimeException("Invalid level name: " + key);
		}
		String dungeon = components[0];
		int depth = Integer.valueOf(components[1]);

		Generator generator = new Generator();
		Level level = generator.generate(key, 60, 40);
		// Remember to add the level to the map before generating it
		levels.put(key, level);
		level.threat = depth-1;
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
			Entity c = Game.bestiary.create(phenotype, creatureName);
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
				level.addTransition(new LevelTransition("up", new Point(fromX, fromY), destination, new Point(toX, toY)));
			} else if (transitionType.equals("down")) {
				level.addTransition(new LevelTransition("down", new Point(fromX, fromY), destination, new Point(toX, toY)));
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
					Entity creature = Game.bestiary.create(phenotype, null);
					level.addEntityWithStacking(creature, new Point(x, y));
				}
				if (itemType != null) {
					Entity item = Game.itempedia.create(itemType, null);
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
				level.addTransition(new LevelTransition("up", new Point(fromX, fromY), destination, new Point(toX, toY)));
			} else if (transitionType.equals("down")) {
				level.addTransition(new LevelTransition("down", new Point(fromX, fromY), destination, new Point(toX, toY)));
			} else {
				throw new RuntimeException("Invalid transition type: " + transitionType);
			}
		}
		return level;
	}
}
