package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.roguelike.spells.*;

import java.util.Collection;
import java.util.HashMap;

public class Spellpedia {
    private static HashMap<String, Spell> map = new HashMap<>();

    static {
        map.put("firebeam", new SpellFirebeam());
        map.put("flicker", new SpellFlicker());
        map.put("icebeam", new SpellIceBeam());
        map.put("magic missile", new SpellMagicMissile());
        map.put("oak strength", new SpellOakStrength());
        map.put("root spear", new SpellRootSpear());
        map.put("shocking grasp", new SpellShockingGrasp());
        map.put("voltage cascade", new SpellVoltageCascade());
        map.put("water blast", new SpellWaterBlast());

        map.put("monster spark weak", new SpellMonsterSparkWeak());
        map.put("monster fire weak", new SpellMonsterFireWeak());
        map.put("monster water weak", new SpellMonsterWaterWeak());
        map.put("monster plant weak", new SpellMonsterPlantWeak());
        map.put("monster summon rats", new SpellMonsterSummonRats());
        map.put("monster acid medium", new SpellMonsterAcidMedium());

        map.put("divine banish", new SpellDivineBanish());
        map.put("divine healing", new SpellDivineHealing());
        map.put("divine time stop", new SpellDivineTimeStop());

        map.put("dash through", new WeaponSkillDashThrough());
        map.put("recoil", new WeaponSkillRecoil());
        map.put("deflection", new WeaponSkillDeflection());
        map.put("swordplay", new WeaponSkillSwordplay());
        map.put("berserk", new WeaponSkillBerserk());
        map.put("cleave", new WeaponSkillCleave());
        map.put("knock back", new WeaponSkillKnockBack());
        map.put("launch over", new WeaponSkillLaunchOver());
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
