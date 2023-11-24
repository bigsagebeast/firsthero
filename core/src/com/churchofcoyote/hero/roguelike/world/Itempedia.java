package com.churchofcoyote.hero.roguelike.world;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.roguelike.world.proc.environment.ProcDoor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Itempedia {

    private static Map<String, ItemType> map = new HashMap<String, ItemType>();

    public Itempedia() {
        ItemType door = new ItemType();
        door.name = "door";
        door.glyphName = "terrain.door_closed";
        door.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_TAN, Palette.COLOR_BROWN);
        door.setup.add((e) -> {e.addProc(new ProcDoor(e));});
        map.put("door", door);

        ItemType pitchfork = new ItemType();
        pitchfork.name = "pitchfork";
        //pitchfork.glyph = new Glyph('/', Color.LIGHT_GRAY);
        pitchfork.glyphName = "weapon.pitchfork";
        pitchfork.palette = new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_WHITE, Palette.COLOR_WHITE);
        pitchfork.equipmentFor = BodyPart.TWO_HAND;
        pitchfork.category = ItemCategory.CATEGORY_TWO_HANDED_WEAPONS;
        pitchfork.setup.add((e) -> {e.addProc(new PropPopupOnSeen(e, "Pick up your weapon"));});
        pitchfork.setup.add((e) -> {e.addProc(new PropMessageOnStepOn(e, "Press 'comma' to pick it up."));});
        pitchfork.setup.add((e) -> {e.addProc(new PropMessageOnPickup(e, "Press 'w' to wield it."));});
        map.put(pitchfork.name, pitchfork);

        ItemType shortsword = new ItemType();
        shortsword.name = "short sword";
        shortsword.glyphName = "weapon.shortsword";
        shortsword.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        shortsword.equipmentFor = BodyPart.ANY_HAND;
        shortsword.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        map.put(shortsword.name, shortsword);

        ItemType longsword = new ItemType();
        longsword.name = "longsword";
        longsword.glyphName = "weapon.longsword";
        longsword.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        longsword.equipmentFor = BodyPart.ANY_HAND;
        longsword.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        map.put(longsword.name, longsword);

        ItemType dagger = new ItemType();
        dagger.name = "dagger";
        dagger.glyphName = "weapon.dagger";
        dagger.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        dagger.equipmentFor = BodyPart.ANY_HAND;
        dagger.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        map.put(dagger.name, dagger);

        ItemType buckler = new ItemType();
        buckler.name = "buckler";
        buckler.glyphName = "armor.buckler";
        buckler.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_GRAY, Palette.COLOR_WHITE);
        buckler.equipmentFor = BodyPart.ANY_HAND;
        buckler.category = ItemCategory.CATEGORY_SHIELDS;
        map.put(buckler.name, buckler);
    }

    public Entity create(String key) {
        return create(key, null);
    }

    public Entity create(String key, String name) {
        Entity e = EntityTracker.create();

        ItemType t = map.get(key);
        if (name != null) {
            e.name = name;
        } else {
            e.name = t.name;
        }
        //e.glyph = t.glyph;
        e.glyphName = t.glyphName;
        e.palette = t.palette;
        e.itemTypeName = t.name;

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

    public static ItemType get(String name) {
        return map.get(name);
    }
}
