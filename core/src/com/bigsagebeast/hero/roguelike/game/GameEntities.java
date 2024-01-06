package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Phenotype;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;
import com.bigsagebeast.hero.roguelike.world.proc.intrinsic.ProcTelepathy;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcItem;

import java.util.List;

import static com.bigsagebeast.hero.roguelike.game.Game.announce;

public class GameEntities {
    public static boolean isTelepathicallyVisible(Entity entity) {
        Phenotype phenotype = Bestiary.get(entity.phenotypeName);
        return !phenotype.tags.contains("undead") && !phenotype.tags.contains("construct");
    }

    public static void changeItemBeatitude(Entity entity, Beatitude targetBeatitude) {
        ProcItem item = entity.getItem();
        if (item.beatitude == targetBeatitude) {
            return;
        }
        switch (targetBeatitude) {
            case CURSED:
                announce(entity.getVisibleNameDefinite() + " glows in a harsh black light.");
                break;
            case UNCURSED:
                announce(entity.getVisibleNameDefinite() + " glows white.");
                break;
            case BLESSED:
                announce(entity.getVisibleNameDefinite() + " glows in a smooth blue light.");
                break;
        }
        item.beatitude = targetBeatitude;
    }

    public static boolean isTelepathic(Entity entity) {
        return entity.getProcByTypeIncludingEquipment(ProcTelepathy.class) != null ||
                entity.getProcByTypeIncludingEquipment(ProcEffectTimedTelepathy.class) != null;
    }
}
