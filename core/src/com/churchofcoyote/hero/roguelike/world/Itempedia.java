package com.churchofcoyote.hero.roguelike.world;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.world.proc.ProcItem;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMonster;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.roguelike.world.proc.PropPopupOnSeen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Itempedia {
    public Map<String, ItemType> map = new HashMap<String, ItemType>();

    public Itempedia() {
        ItemType pitchfork = new ItemType();
        pitchfork.name = "pitchfork";
        pitchfork.glyph = new Glyph('/', Color.LIGHT_GRAY);
        pitchfork.setup.add((e) -> {e.addProc(new PropPopupOnSeen(e, "Pick up your weapon"));});
        map.put("pitchfork", pitchfork);
    }

    public Entity create(String key, String name) {
        Entity e = new Entity();

        ItemType t = map.get(key);
        if (name != null) {
            e.name = name;
        } else {
            e.name = t.name;
        }
        e.glyph = t.glyph;

        ProcItem pi = new ProcItem(e);
        e.addProc(pi);

        for (Consumer<Entity> consumer : t.setup) {
            consumer.accept(e);
        }

        return e;
    }
}
