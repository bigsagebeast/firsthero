package com.churchofcoyote.hero.glyphtile;

import com.churchofcoyote.hero.roguelike.world.Terrain;

import java.util.HashMap;
import java.util.Map;

public class TerrainGlyph {
    public Map<Terrain, GlyphTile[]> map = new HashMap<>();

    public TerrainGlyph(Map<String, Terrain> terrainMap) {
        for (Terrain t : terrainMap.values()) {
            BaseGlyph b = GlyphIndex.get(t.getGlyphName());
            GlyphTile[] blockJoins;
            if (t.getBlockCategory() == null)
            {
                blockJoins = new GlyphTile[1];
                blockJoins[0] = b.create(t.getPaletteEntry());
            } else {
                blockJoins = new GlyphTile[BlockJoin.SIZE];
                for (int i=0; i<BlockJoin.SIZE; i++) {
                    blockJoins[0] = b.create(t.getPaletteEntry());
                }
            }
            map.put(t, blockJoins);
        }
    }

    public GlyphTile[] getGlyphTile(Terrain t) {
        return map.get(t);
    }
}
