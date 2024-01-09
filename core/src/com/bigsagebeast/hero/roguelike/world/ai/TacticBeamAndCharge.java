package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcCaster;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;

import java.util.List;
import java.util.stream.Collectors;

public class TacticBeamAndCharge extends Tactic {

    private ProcCaster caster;
    public int castPercent = 50;

    @Override
    public boolean execute(Entity e, ProcMover pm) {
        if (caster == null) {
            caster = (ProcCaster)e.getProcByType(ProcCaster.class);
            if (caster == null) {
                throw new RuntimeException("Trying to use TacticBeamAndCharge without ProcCaster");
            }
        }

        if (pm.targetEntityId == EntityTracker.NONE || Game.random.nextInt(100) > castPercent) {
            return executeChaseAndMelee(e, pm);
        }
        Entity target = EntityTracker.get(pm.targetEntityId);
        Compass aimDir = Compass.findDir(e.pos, target.pos);
        if (aimDir == null) {
            return executeChaseAndMelee(e, pm);
        }
        List<Spell> validSpells = canHitWith(e, target);
        if (validSpells.isEmpty()) {
            return executeChaseAndMelee(e, pm);
        }
        Spell chosenSpell = validSpells.get(Game.random.nextInt(validSpells.size()));
        chosenSpell.castDirectionally(e, aimDir);
        pm.setDelay(e, Game.ONE_TURN);

        return true;
    }


    public List<Spell> canHitWith(Entity entity, Entity target) {
        return caster.getSpells(entity).stream().filter(s -> s.getRange(entity) >= entity.pos.distance(target.pos)).collect(Collectors.toList());
    }


    public boolean executeChaseAndMelee(Entity e, ProcMover pm) {
        if (pm.targetEntityId == EntityTracker.NONE) {
            if (lastSeen != null) {
                if (!huntLastSeen(e, pm)) {
                    return idle(e, pm);
                }
            }
            else {
                return idle(e, pm);
            }
        } else {
            return chaseSeenPlayer(e, pm);
        }
        return false;
    }
}
