package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.enums.StatusType;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.SwingResult;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;

public class ProcDoppelganger extends Proc {
    public int chance;
    public int duration;
    public int difficulty;

    @Override
    public void postLoad(Entity entity) {
        entity.glyphNames = Game.getPlayerEntity().glyphNames;
        entity.palette = Game.getPlayerEntity().palette;
        entity.glyphFlipH = true;
        entity.gender = Game.getPlayerEntity().gender;
        EntityGlyph.updateEntity(entity);
    }

    @Override
    public void postDoHit(Entity entity, Entity target, Entity tool, SwingResult result) {
        if (chance < Game.random.nextInt(100)) {
            return;
        }
        Game.announceVis(entity, target,
                "You mimick " + target.getVisibleNameDefinite() + "'s movements!",
                entity.getVisibleNameDefinite() + " mimicks your movements!",
                entity.getVisibleNameDefinite() + " mimicks " + target.getVisibleNameDefinite() + "'s movements!",
                null);
        if (target.getProcByType(ProcEffectConfusion.class) != null) {
            // already confused - don't reset duration
            return;
        }

        boolean resist = entity.testResistStatus(StatusType.CONFUSION);
        resist |= CombatLogic.tryResist(target, difficulty, target.getStat(Stat.WILLPOWER));
        if (!resist) {
            ProcEffectConfusion confusionProc = new ProcEffectConfusion();
            confusionProc.turnsRemaining = (int)(duration * (0.5f + Game.random.nextFloat()));
            target.addProc(confusionProc);
        }
    }
}
