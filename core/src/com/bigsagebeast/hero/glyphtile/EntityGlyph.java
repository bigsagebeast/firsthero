package com.bigsagebeast.hero.glyphtile;

import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.ArrayList;
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
        if (e.glyphNames.length == 1) {
            BaseGlyph b = GlyphIndex.get(e.glyphNames[0]);
            GlyphTile t = b.create(e.palette, e.glyphFlipH);
            map.put(e, t);
        } else {
            ArrayList<BaseGlyph> baseGlyphs = new ArrayList<>();
            for (String glyphName : e.glyphNames) {
                baseGlyphs.add(GlyphIndex.get(glyphName));
            }
            GlyphTile t = BaseGlyph.create(baseGlyphs, e.palette, e.glyphFlipH);
            map.put(e, t);
        }
    }

    public static void forget(Entity e) {
        map.remove(e);
    }
}
