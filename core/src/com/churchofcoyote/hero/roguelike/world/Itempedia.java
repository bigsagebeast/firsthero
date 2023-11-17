package com.churchofcoyote.hero.roguelike.world;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.world.proc.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Itempedia {
    public Map<String, ItemType> map = new HashMap<String, ItemType>();

    public Itempedia() {
        ItemType pitchfork = new ItemType();
        pitchfork.name = "pitchfork";
        pitchfork.glyph = new Glyph('/', Color.LIGHT_GRAY);
        pitchfork.equipmentFor = BodyPart.ANY_HAND;
        pitchfork.setup.add((e) -> {e.addProc(new PropPopupOnSeen(e, "Pick up your weapon"));});
        pitchfork.setup.add((e) -> {e.addProc(new PropMessageOnStepOn(e, "Press 'comma' to pick it up."));});
        pitchfork.setup.add((e) -> {e.addProc(new PropMessageOnPickup(e, "Press 'w' to wield it."));});
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

        for (Consumer<Entity> consumer : t.setup) {
            consumer.accept(e);
        }

        if (!e.containsProc(ProcItem.class)) {
            e.addProc(new ProcItem(e));
        }

        if (t.equipmentFor != null && !e.containsProc(ProcEquippable.class)) {
            e.addProc(new ProcEquippable(e, t.equipmentFor));
        }

        return e;
    }
}
