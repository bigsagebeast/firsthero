package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.roguelike.world.proc.environment.ProcDoor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Itempedia {

    public static Map<String, ItemType> map = new HashMap<String, ItemType>();

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
        pitchfork.level = -1;
        pitchfork.setup.add((e) -> {e.addProc(new ProcPopupOnSeen(e, "Pick up your weapon"));});
        pitchfork.setup.add((e) -> {e.addProc(new ProcMessageOnStepOn(e, "Press 'comma' to pick it up."));});
        pitchfork.setup.add((e) -> {e.addProc(new ProcMessageOnPickup(e, "Press 'w' to wield it."));});
        pitchfork.setup.add((e) -> {
            ProcWeapon pw = new ProcWeapon(e);
            pw.averageDamage = 5;
            pw.toHitBonus = 2;
            e.addProc(pw);
        });

        map.put(pitchfork.name, pitchfork);

        ItemType shortsword = new ItemType();
        shortsword.name = "short sword";
        shortsword.glyphName = "weapon.shortsword";
        shortsword.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        shortsword.equipmentFor = BodyPart.ANY_HAND;
        shortsword.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        shortsword.level = 1;
        shortsword.setup.add((e) -> {
            ProcWeapon pw = new ProcWeapon(e);
            pw.averageDamage = 6;
            pw.toHitBonus = 3;
            e.addProc(pw);
        });
        map.put(shortsword.name, shortsword);

        ItemType longsword = new ItemType();
        longsword.name = "longsword";
        longsword.glyphName = "weapon.longsword";
        longsword.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        longsword.equipmentFor = BodyPart.ANY_HAND;
        longsword.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        longsword.level = 2;
        longsword.setup.add((e) -> {
            ProcWeapon pw = new ProcWeapon(e);
            pw.averageDamage = 8;
            pw.toHitBonus = 2;
            e.addProc(pw);
        });
        map.put(longsword.name, longsword);

        ItemType dagger = new ItemType();
        dagger.name = "dagger";
        dagger.glyphName = "weapon.dagger";
        dagger.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        dagger.equipmentFor = BodyPart.ANY_HAND;
        dagger.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        dagger.level = 0;
        dagger.setup.add((e) -> {
            ProcWeapon pw = new ProcWeapon(e);
            pw.averageDamage = 5;
            pw.toHitBonus = 3;
            e.addProc(pw);
        });
        map.put(dagger.name, dagger);

        ItemType buckler = new ItemType();
        buckler.name = "buckler";
        buckler.glyphName = "armor.buckler";
        buckler.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_GRAY, Palette.COLOR_WHITE);
        buckler.equipmentFor = BodyPart.ANY_HAND;
        buckler.category = ItemCategory.CATEGORY_SHIELDS;
        buckler.level = 1;
        buckler.setup.add((e) -> {
            e.addProc(new ProcArmor(e, 2, 0));
        });
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
