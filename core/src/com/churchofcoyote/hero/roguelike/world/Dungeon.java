package com.churchofcoyote.hero.roguelike.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.util.Point;

public class Dungeon {
	public Map<String, Level> levels = new HashMap<String, Level>();
	
	public Level getLevel(String key) {
		return levels.get(key);
	}
	
	
	public void generateCavern(String key, int width, int height) {
		Level level = new Level(width, height);
		
		
		
		levels.put(key, level);
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
				levels.put(key, generateFromDataV1(input));
				return;
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read dungeon data", e);
		}
		throw new RuntimeException("Unknown dungeon data version");
	}
	
	public Level generateFromDataV1(BufferedReader input) throws IOException {
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
		Level level = new Level(width, height);

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
			String name = null;
			if (creatureSplit.length > 3) {
				name = creatureSplit[3].replace("_", " ");
			}
			Entity c = Game.bestiary.create(phenotype, name);
			c.pos = new Point(x, y);
			level.addEntity(c);
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
				if (level.cell(fromX, fromY).terrain.getGlyphForTile(fromX, fromY, 0).getSymbol() != '<') {
					throw new RuntimeException("Invalid transition: no up-stair at " + fromX + ", " + fromY);
				}
				level.addTransition(new LevelTransition("up", new Point(fromX, fromY), destination, new Point(toX, toY)));
			} else if (transitionType.equals("down")) {
				if (level.cell(fromX, fromY).terrain.getGlyphForTile(fromX, fromY, 0).getSymbol() != '>') {
					throw new RuntimeException("Invalid transition: no down-stair at " + fromX + ", " + fromY);
				}
				level.addTransition(new LevelTransition("down", new Point(fromX, fromY), destination, new Point(toX, toY)));
			} else {
				throw new RuntimeException("Invalid transition type: " + transitionType);
			}
		}
		return level;
	}
}
