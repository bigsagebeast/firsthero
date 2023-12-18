package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.roguelike.spells.*;

import java.util.Collection;
import java.util.HashMap;

public class Spellpedia {
    private static HashMap<String, Spell> map = new HashMap<>();

    static {
        map.put("magic missile", new SpellMagicMissile());
        map.put("firebeam", new SpellFirebeam());
        map.put("water blast", new SpellWaterBlast());
        map.put("root spear", new SpellRootSpear());
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
