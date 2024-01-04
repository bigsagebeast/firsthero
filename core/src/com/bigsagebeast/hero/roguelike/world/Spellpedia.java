package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.roguelike.spells.*;

import java.util.Collection;
import java.util.HashMap;

public class Spellpedia {
    private static HashMap<String, Spell> map = new HashMap<>();

    static {
        map.put("magic missile", new SpellMagicMissile());
        map.put("firebeam", new SpellFirebeam());
        map.put("water blast", new SpellWaterBlast());
        map.put("root spear", new SpellRootSpear());
        map.put("monster spark weak", new SpellMonsterSparkWeak());
        map.put("monster fire weak", new SpellMonsterFireWeak());
        map.put("monster water weak", new SpellMonsterWaterWeak());
        map.put("monster plant weak", new SpellMonsterPlantWeak());
    }

    public static Spell get(String key) {
        if (!map.containsKey(key)) {
            throw new RuntimeException("Invalid spell: " + key);
        }
        return map.get(key);
    }

    public static Collection<String> keys() {
        return map.keySet();
    }

    public static Collection<Spell> all() {
        return map.values();
    }
}