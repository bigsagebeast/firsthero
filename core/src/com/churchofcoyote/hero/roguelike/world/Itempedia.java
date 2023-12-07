package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.roguelike.world.proc.environment.ProcDoor;
import com.churchofcoyote.hero.roguelike.world.proc.item.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Itempedia {

    public static Map<String, ItemType> map = new HashMap<String, ItemType>();

    public Itempedia() {
        ItemType door = new ItemType();
        door.keyName = "door";
        door.name = "door";
        door.glyphName = "terrain.door_closed";
        door.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_TAN, Palette.COLOR_BROWN);
        door.setup.add((e) -> {e.addProc(new ProcDoor());});
        //map.put(door.keyName, door);

        ItemType pitchfork = new ItemType();
        pitchfork.keyName = "pitchfork";
        pitchfork.name = "pitchfork";
        //pitchfork.glyph = new Glyph('/', Color.LIGHT_GRAY);
        pitchfork.glyphName = "weapon.pitchfork";
        pitchfork.palette = new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_WHITE, Palette.COLOR_WHITE);
        pitchfork.equipmentFor = BodyPart.TWO_HAND;
        pitchfork.category = ItemCategory.CATEGORY_TWO_HANDED_WEAPONS;
        pitchfork.level = -1;
        pitchfork.setup.add((e) -> {e.addProc(new ProcPopupOnSeen("Pick up your weapon"));});
        pitchfork.setup.add((e) -> {e.addProc(new ProcMessageOnStepOn("Press 'comma' to pick it up."));});
        pitchfork.setup.add((e) -> {e.addProc(new ProcMessageOnPickup("Press 'w' to wield it."));});
        pitchfork.setup.add((e) -> {
            ProcWeaponMelee pw = new ProcWeaponMelee();
            pw.averageDamage = 5;
            pw.toHitBonus = 2;
            e.addProc(pw);
        });

        //map.put(pitchfork.keyName, pitchfork);

        ItemType shortsword = new ItemType();
        shortsword.keyName = "short sword";
        shortsword.name = "short sword";
        shortsword.glyphName = "weapon.shortsword";
        shortsword.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        shortsword.equipmentFor = BodyPart.ANY_HAND;
        shortsword.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        shortsword.level = 1;
        shortsword.setup.add((e) -> {
            ProcWeaponMelee pw = new ProcWeaponMelee();
            pw.averageDamage = 6;
            pw.toHitBonus = 3;
            e.addProc(pw);
        });
        map.put(shortsword.keyName, shortsword);

        ItemType longsword = new ItemType();
        longsword.keyName = "longsword";
        longsword.name = "longsword";
        longsword.glyphName = "weapon.longsword";
        longsword.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        longsword.equipmentFor = BodyPart.ANY_HAND;
        longsword.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        longsword.level = 2;
        longsword.setup.add((e) -> {
            ProcWeaponMelee pw = new ProcWeaponMelee();
            pw.averageDamage = 8;
            pw.toHitBonus = 2;
            e.addProc(pw);
        });
        map.put(longsword.keyName, longsword);

        ItemType dagger = new ItemType();
        dagger.keyName = "dagger";
        dagger.name = "dagger";
        dagger.glyphName = "weapon.dagger";
        dagger.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        dagger.equipmentFor = BodyPart.ANY_HAND;
        dagger.category = ItemCategory.CATEGORY_ONE_HANDED_WEAPONS;
        dagger.level = 0;
        dagger.setup.add((e) -> {
            ProcWeaponMelee pw = new ProcWeaponMelee();
            pw.averageDamage = 5;
            pw.toHitBonus = 3;
            e.addProc(pw);
        });
        map.put(dagger.keyName, dagger);

        ItemType buckler = new ItemType();
        buckler.keyName = "buckler";
        buckler.name = "buckler";
        buckler.glyphName = "armor.buckler";
        buckler.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_GRAY, Palette.COLOR_WHITE);
        buckler.equipmentFor = BodyPart.ANY_HAND;
        buckler.category = ItemCategory.CATEGORY_SHIELDS;
        buckler.level = 1;
        buckler.setup.add((e) -> {
            e.addProc(new ProcArmor(2, 0));
        });
        map.put(buckler.keyName, buckler);

        ItemType shortbow = new ItemType();
        shortbow.keyName = "shortbow";
        shortbow.name = "wooden shortbow";
        shortbow.glyphName = "weapon.bow";
        shortbow.palette = new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        shortbow.equipmentFor = BodyPart.RANGED_WEAPON;
        shortbow.category = ItemCategory.CATEGORY_RANGED;
        shortbow.level = 1;
        shortbow.setup.add((e) -> {
            e.addProc(new ProcWeaponRanged(0, 0, 10, AmmoType.ARROW));
        });
        map.put(shortbow.keyName, shortbow);

        ItemType arrow = new ItemType();
        arrow.keyName = "arrow";
        arrow.name = "wooden arrow";
        arrow.glyphName = "weapon.arrow";
        arrow.palette = new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_WHITE);
        arrow.equipmentFor = BodyPart.RANGED_AMMO;
        arrow.category = ItemCategory.CATEGORY_AMMO;
        arrow.level = 1;
        arrow.minCount = 5;
        arrow.maxCount = 10;
        arrow.setup.add((e) -> {
            e.addProc(new ProcWeaponAmmo(5, 0, AmmoType.ARROW));
        });
        map.put(arrow.keyName, arrow);

        ItemType gold = new ItemType();
        gold.keyName = "gold";
        gold.name = "gold piece";
        gold.glyphName = "misc.gold";
        gold.palette = new PaletteEntry(Palette.COLOR_ORANGE, Palette.COLOR_YELLOW, Palette.COLOR_WHITE);
        gold.category = ItemCategory.CATEGORY_GOLD;
        gold.level = -1;
        gold.frequency = 0;
        map.put(gold.keyName, gold);
    }

    public Entity create(String key) {
        return create(key, null);
    }

    public Entity create(String key, String name) {
        ItemType t = map.get(key);
        if (t == null) {
            throw new RuntimeException("Tried to create nonexistent item type " + key);
        }

        Entity e = EntityTracker.create();

        if (name != null) {
            e.name = name;
        } else {
            e.name = t.name;
        }
        e.pluralName = t.pluralName;
        //e.glyph = t.glyph;
        e.glyphName = t.glyphName;
        e.palette = t.palette;
        e.itemTypeKey = t.keyName;

        for (Consumer<Entity> consumer : t.setup) {
            consumer.accept(e);
        }
        if (t.procLoaders != null) {
            for (LoadProc loader : t.procLoaders) {
                loader.apply(e);
            }
        }

        if (!e.containsProc(ProcItem.class)) {
            e.addProc(new ProcItem());
        }

        if (t.equipmentFor != null && !e.containsProc(ProcEquippable.class)) {
            e.addProc(new ProcEquippable(t.equipmentFor));
        }

        return e;
    }

    public Entity create(String key, int quantity) {
        ItemType t = map.get(key);
        if (!t.stackable) {
            throw new RuntimeException("Tried to create a stack of unstackable " + key);
        }
        Entity e = create(key);
        e.getItem().quantity = quantity;
        return e;
    }

    public static ItemType get(String name) {
        return map.get(name);
    }
}
