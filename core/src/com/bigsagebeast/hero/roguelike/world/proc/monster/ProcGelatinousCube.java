package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.roguelike.game.Dice;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectParalysis;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcCorpse;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcFood;

public class ProcGelatinousCube extends Proc {

    @Override
    public void turnPassed(Entity entity) {
        for (Entity target : Game.getLevel().getEntitiesOnTile(entity.pos)) {
            if (target.containsProc(ProcCorpse.class) || target.containsProc(ProcFood.class)) {
                Game.announceVis(entity, null, null, null,
                        entity.getVisibleNameDefinite() + " consumes " + target.getVisibleNameDefinite() + " with a slurp.",
                        "You hear a slurping sound.");
                target.destroy();
            }
        }
    }

    @Override
    public void postBeHit(Entity entity, Entity actor, Entity tool) {
        if (tool != null && tool.getEquippable().equipmentFor == BodyPart.RANGED_AMMO) {
            return;
        }
        if (actor.containsProc(ProcEffectParalysis.class)) {
            return;
        }
        ProcEffectParalysis proc = new ProcEffectParalysis();
        proc.turnsRemaining = 5;
        actor.addProc(proc);
    }

    @Override
    public void postDoHit(Entity entity, Entity target, Entity tool) {
        if (target.containsProc(ProcEffectParalysis.class)) {
            return;
        }
        ProcEffectParalysis proc = new ProcEffectParalysis();
        proc.turnsRemaining = 5;
        target.addProc(proc);
    }

}
