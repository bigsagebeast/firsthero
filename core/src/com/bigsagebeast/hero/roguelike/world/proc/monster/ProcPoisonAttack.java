package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.roguelike.game.Dice;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.SwingResult;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectPoisoned;

public class ProcPoisonAttack extends Proc {
    int strength;
    int duration;
    public ProcPoisonAttack() { super(); }

    @Override
    public void postDoHit(Entity entity, Entity target, Entity tool, SwingResult result) {
        if (result.penetrationFailed) {
            return;
        }
        ProcEffectPoisoned existingProc = (ProcEffectPoisoned)target.getProcByType(ProcEffectPoisoned.class);
        if (existingProc != null) {
            // TODO: This works when the poison durations and strengths are fairly similar.
            // But it might be a problem when adding a very strong poison to a weak one or vice versa.
            int addedStrength = strength * duration;
            int addingDuration = (addedStrength / existingProc.strength) / 2;

            // Don't stack more than triple duration - doesn't fit well with the above
            int maxDuration = duration * 3;
            if (addingDuration + existingProc.turnsRemaining > maxDuration) {
                return;
            }

            existingProc.increaseDuration(target, addingDuration);
        } else {
            ProcEffectPoisoned proc = new ProcEffectPoisoned();
            proc.turnsRemaining = duration;
            proc.strength = strength;
            target.addProc(proc);
        }
    }
}
