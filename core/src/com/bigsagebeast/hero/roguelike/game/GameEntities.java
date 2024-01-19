package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;
import com.bigsagebeast.hero.roguelike.world.proc.intrinsic.ProcTelepathy;
import com.bigsagebeast.hero.roguelike.world.proc.item.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        boolean plural = item.quantity > 1;
        switch (targetBeatitude) {
            case CURSED:
                announce(entity.getVisibleNameDefinite() + " " + (plural ? "glow" : "glows") + " in a harsh black light.");
                break;
            case UNCURSED:
                announce(entity.getVisibleNameDefinite() + " " + (plural ? "glow" : "glows") + " white.");
                break;
            case BLESSED:
                announce(entity.getVisibleNameDefinite() + " " + (plural ? "glow" : "glows") + " in a smooth blue light.");
                break;
        }
        item.beatitude = targetBeatitude;
        if (entity.containingEntity >= 0) {
            EntityTracker.get(entity.containingEntity).restack(entity);
        }
        entity.identifyItemBeatitude();
    }

    public static boolean isTelepathic(Entity entity) {
        return entity.getProcByTypeIncludingEquipment(ProcTelepathy.class) != null ||
                entity.getProcByTypeIncludingEquipment(ProcEffectTimedTelepathy.class) != null;
    }

    public static float hitPointsPerLevel(Entity entity) {
        return entity.getStat(Stat.TOUGHNESS) / 2.0f;
    }

    public static float spellPointsPerLevel(Entity entity) {
        return (entity.getStat(Stat.WILLPOWER) + entity.getStat(Stat.ARCANUM)) / 4.0f;
    }

    public static float divinePoints(Entity entity) {
        return entity.getStat(Stat.AVATAR) * 50;
    }


    public static String describeItem(Entity ent) {
        StringBuilder sb = new StringBuilder();
        ProcItem pi = ent.getItem();

        List<Proc> procs = ent.procs.stream()
                .sorted(Comparator.comparingInt(proc -> proc.getDescriptionPriority(ent)))
                .collect(Collectors.toList());
        for (Proc p : procs) {
            if (pi.hasIdentifiedStats(ent)) {
                String iden = p.getIdenDescription(ent);
                if (iden != null) {
                    sb.append("\n").append(iden);
                }
            }
            String unid = p.getUnidDescription(ent);
            if (unid != null) {
                sb.append("\n").append(unid);
            }
        }
        // delete the first \n
        sb.delete(0, 1);

        return sb.toString();
    }

    public static boolean overpopulated(Phenotype phenotype) {
		if (phenotype.maxOnLevel > 0) {
            return Game.getLevel().getEntities().stream().filter(c -> c.phenotypeName != null && c.phenotypeName.equals(phenotype.key)).count() >= phenotype.maxOnLevel;
        }
        return false;
    }

}
