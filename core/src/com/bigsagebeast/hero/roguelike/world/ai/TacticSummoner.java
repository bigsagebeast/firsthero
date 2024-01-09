package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcHasMinions;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcCaster;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcMonster;

import java.util.List;
import java.util.stream.Collectors;

public class TacticSummoner extends Tactic {

    private ProcCaster caster;
    public int castPercent = 50;
    // TODO: Maybe change this to use SP?
    public int charges = 3;
    public int maxCharges = 3;
    public int chargeTimer = 15;
    public int maxChargeTimer = 15;

    @Override
    public boolean execute(Entity e, ProcMover pm) {
        if (charges < maxCharges) {
            if (--chargeTimer <= 0) {
                chargeTimer = maxChargeTimer;
                charges++;
            }
        }
        if (caster == null) {
            caster = (ProcCaster)e.getProcByType(ProcCaster.class);
            if (caster == null) {
                throw new RuntimeException("Trying to use TacticSummoner without ProcCaster");
            }
        }

        minionsHunt(e);
        if (pm.targetEntityId == EntityTracker.NONE) {
            if (lastSeen == null) {
                return idle(e, pm);
            } else {
                if (Game.random.nextInt(100) < castPercent && canSummon(e, pm)) {
                    summon(e, pm);
                    minionsHunt(e);
                    return true;
                } else {
                    return huntLastSeen(e, pm);
                }
            }
        } else {
            Entity target = EntityTracker.get(pm.targetEntityId);
            if (Game.random.nextInt(100) < castPercent && canSummon(e, pm)) {
                summon(e, pm);
                minionsHunt(e);
                return true;
            }
            // TODO: add a method for 'is adjacent'
            if (target.pos.distance(e.pos) < 2) {
                return chaseSeenPlayer(e, pm);
            }
            return guard(e, pm);
        }
    }

    private boolean canSummon(Entity e, ProcMover pm) {
        if (charges == 0) {
            return false;
        }
        ProcHasMinions minions = (ProcHasMinions)e.getProcByType(ProcHasMinions.class);
        if (minions == null) {
            minions = new ProcHasMinions();
            e.addProc(minions);
        }
        minions.clean();
        return minions.ownedEntities.size() < ProcHasMinions.MAX_MINIONS;
    }

    private void summon(Entity e, ProcMover pm) {
        charges--;
        // TODO: Filter by 'only summoning spells'?
        List<Spell> summoningSpells = caster.getSpells(e);
        Spell chosenSpell = summoningSpells.get(Game.random.nextInt(summoningSpells.size()));
        chosenSpell.castPersonal(e);
        pm.setDelay(e, Game.ONE_TURN);
    }

    private void minionsHunt(Entity e) {
        ProcHasMinions minions = (ProcHasMinions)e.getProcByType(ProcHasMinions.class);
        if (minions == null) {
            minions = new ProcHasMinions();
            e.addProc(minions);
        }
        minions.clean();
        for (Entity minion : minions.ownedEntities.stream().map(EntityTracker::get).collect(Collectors.toList())) {
            ProcMonster monster = (ProcMonster)minion.getProcByType(ProcMonster.class);
            // TODO is this working?
            monster.tactic.lastSeen = lastSeen;
        }
    }
}
