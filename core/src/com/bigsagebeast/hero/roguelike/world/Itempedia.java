package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.roguelike.game.Game;
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
        } else if (t.glyphName != null) {
            e.glyphNames = new String[] { t.glyphName };
        } else {
            e.glyphNames = null;
        }
        e.hide = t.hide;
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

        if (Game.random.nextInt(100) < t.blessChance) {
            e.getItem().beatitude = Beatitude.BLESSED;
        } else if (Game.random.nextInt(100) < t.curseChance) {
            e.getItem().beatitude = Beatitude.CURSED;
        }

        return e;
    }

    public static Entity create(String key, int quantity) {
        ItemType t = map.get(key);
        t.spawnCount++;
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
