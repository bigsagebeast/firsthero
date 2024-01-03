package com.bigsagebeast.hero.persistence;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.Terrain;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.roguelike.world.dungeon.LevelCell;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PersistentLevel {
    private String name;
    private int width, height;
    private HashMap<String, String> terrainDesignatorsByTerrain = new HashMap<>();
    private HashMap<String, String> terrainDesignatorsByDesignator = new HashMap<>();
    private String[] compressedMap;
    private static final int charsPerTile = 3;
    private Collection<Entity> entities = new HashSet<Entity>();

    // for deserialization
    private PersistentLevel() {}

    public PersistentLevel(Level level) {
        this.name = level.getKey();
        this.width = level.getWidth();
        this.height = level.getHeight();

        collectEntities(level.getEntities());

        int lastDesignatorIndex = 0;

        for (LevelCell cell : level.getCellStream()) {
            if (!terrainDesignatorsByTerrain.containsKey(cell.terrain.getName())) {
                terrainDesignatorsByTerrain.put(cell.terrain.getName(), getTerrainDesignator(lastDesignatorIndex));
                terrainDesignatorsByDesignator.put(getTerrainDesignator(lastDesignatorIndex), cell.terrain.getName());
                lastDesignatorIndex++;
            }
        }

        compressedMap = new String[height];
        for (int row=0; row<height; row++) {
            compressedMap[row] = "";
            for (int col=0; col<width; col++) {
                String designator = terrainDesignatorsByTerrain.get(level.cell(col, row).terrain.getName());
                String flags = getFlagString(level.cell(col, row));
                String cellString = designator + flags;
                if (cellString.length() != charsPerTile) {
                    throw new RuntimeException("Malformed cell string while saving: \"" + designator + "\" \"" + flags + "\"");
                }
                compressedMap[row] = compressedMap[row] + cellString;
            }
            //System.out.println(rowString);
        }
    }

    public Level unfreeze() {
        Level level = new Level(name, width, height);

        for (Entity ent : entities) {
            EntityTracker.load(ent);
            /*
            for (Proc p : ent.procs) {
               p.entity = EntityTracker.get(p.entityId);
            }
             */
            // TODO: Better way of tracking which entities are on-map and which are off?
            if (ent.pos != null) {
                level.addEntityWithStacking(ent, ent.pos, false);
            }
        }

        for (int row=0; row<height; row++) {
            String data = compressedMap[row];
            for (int col=0; col<width; col++) {
                LevelCell cell = new LevelCell();
                String terrainDesignator = data.substring(col * charsPerTile, (col * charsPerTile) + 2);
                String flagString = data.substring((col * charsPerTile) + 2, (col * charsPerTile) + 3);
                String terrainName = terrainDesignatorsByDesignator.get(terrainDesignator);
                int flags = getFlagInteger(flagString);
                cell.terrain = Terrain.get(terrainName);
                cell.explored = (flags & 1) > 0;
                level.putCell(col, row, cell);
            }
        }
        level.prepare();
        return level;
    }

    private String getTerrainDesignator(int index) {
        //return Character.forDigit();
        char letter = (char)('A' + index);
        return "A" + letter;
    }

    private String getFlagString(LevelCell cell) {
        // make sure it's just one char
        int flags = (cell.explored ? 1 : 0);
        char flagChar = (char)('0' + flags);
        return "" + flagChar;
    }

    private int getFlagInteger(String flagString) {
        // TODO everything
        return flagString.equals("1") ? 1 : 0;
    }

    private void collectEntities(Collection<Entity> entsToCollect) {
        for (Entity e : entsToCollect) {
            collectEntity(e);
        }
    }

    private void collectEntity(Entity ent) {
        if (entities.contains(ent)) {
            throw new RuntimeException("Save failed: Entity collected twice (" + ent.entityId + ", " + ent.name + ")");
        }
        entities.add(ent);
        for (Entity e : ent.getInventoryEntities()) {
            collectEntity(e);
        }
        for (Entity e : ent.getEquippedEntities()) {
            collectEntity(e);
        }
    }


}
