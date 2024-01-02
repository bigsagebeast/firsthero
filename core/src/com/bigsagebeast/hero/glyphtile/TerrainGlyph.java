package com.bigsagebeast.hero.glyphtile;

import com.bigsagebeast.hero.roguelike.world.Terrain;

import java.util.HashMap;
import java.util.Map;

public class TerrainGlyph {
    public Map<Terrain, GlyphTile[]> map = new HashMap<>();

    public TerrainGlyph(Map<String, Terrain> terrainMap) {
        for (Terrain t : terrainMap.values()) {
            GlyphTile[] blockJoins;
            if (t.getBlockCategory() == null)
            {
                blockJoins = new GlyphTile[1];
                BaseGlyph b = GlyphIndex.get(t.getGlyphName());
                blockJoins[0] = b.create(t.getPaletteEntry());
            } else {
                blockJoins = new GlyphTile[BlockJoin.SIZE];
                for (int i=0; i<BlockJoin.SIZE; i++) {
                    BaseGlyph b = GlyphIndex.get(t.getGlyphName(), i);
                    if (b == null) {
                        throw new RuntimeException("Couldn't get glyph for " + t.getGlyphName() + ": " + i);
                    }
                    blockJoins[i] = b.create(t.getPaletteEntry());
                }
            }
            map.put(t, blockJoins);
        }
    }

    public GlyphTile[] getGlyphTile(Terrain t) {
        return map.get(t);
    }
}
