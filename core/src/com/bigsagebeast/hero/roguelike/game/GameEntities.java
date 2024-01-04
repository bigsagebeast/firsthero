package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Phenotype;

public class GameEntities {
    public static boolean isTelepathicallyVisible(Entity entity) {
        Phenotype phenotype = Bestiary.get(entity.phenotypeName);
        return !phenotype.tags.contains("undead") && !phenotype.tags.contains("construct");
    }
}
