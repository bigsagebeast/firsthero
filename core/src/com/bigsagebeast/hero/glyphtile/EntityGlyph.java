package com.bigsagebeast.hero.glyphtile;

import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityGlyph {

    private static Map<Entity, GlyphTile> map = new HashMap<>();

    public static GlyphTile getGlyph(Entity e) {
        if (!map.containsKey(e)) {
            updateEntity(e);
        }
        return map.get(e);
    }

    public static void updateEntity(Entity e) {
        BaseGlyph b = GlyphIndex.get(e.glyphName);
        GlyphTile t = b.create(e.palette, e.glyphFlipH);
        map.put(e, t);
    }

    public static void forget(Entity e) {
        map.remove(e);
    }
}
