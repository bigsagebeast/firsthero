package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcEquippable;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcItem;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Itempedia {

    public static Map<String, ItemType> map = new HashMap<String, ItemType>();

    public Itempedia() {

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

        ItemType gold = new ItemType();
        gold.keyName = "gold";
        gold.name = "gold piece";
        gold.glyphName = "misc.gold";
        gold.palette = new PaletteEntry(Palette.COLOR_ORANGE, Palette.COLOR_YELLOW, Palette.COLOR_WHITE);
        gold.category = ItemCategory.CATEGORY_GOLD;
        gold.level = -1;
        gold.frequency = 0;
        gold.hasBeatitude = false;
        map.put(gold.keyName, gold);
    }

    public static Entity create(String key) {
        return create(key, null);
    }

    public static Entity create(String key, String name) {
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
        if (t.glyphNames != null) {
            e.glyphNames = t.glyphNames;
        } else {
            e.glyphNames = new String[] { t.glyphName };
        }
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

    public static Entity create(String key, int quantity) {
        ItemType t = map.get(key);
        if (t == null) {
            throw new RuntimeException("Creating invalid item: " + key);
        }
        if (!t.stackable && quantity != 1) {
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
