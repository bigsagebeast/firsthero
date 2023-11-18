package com.churchofcoyote.hero.glyphtile;

import com.churchofcoyote.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityGlyph {

    public Map<Entity, GlyphTile> map = new HashMap<>();

    public GlyphTile getGlyph(Entity e) {
        if (!map.containsKey(e)) {
            updateEntity(e);
        }
        return map.get(e);
    }

    public void updateEntity(Entity e) {
        BaseGlyph b = GlyphIndex.get(e.glyphName);
        GlyphTile t = b.create(e.palette);
        map.put(e, t);
    }

    public void forget(Entity e) {
        map.remove(e);
    }
}
