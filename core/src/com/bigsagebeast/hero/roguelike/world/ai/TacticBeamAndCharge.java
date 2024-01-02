package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcCaster;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;

import java.util.List;
import java.util.stream.Collectors;

public class TacticBeamAndCharge extends Tactic {

    private Point lastSeen = null;
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
                List<Point> path = AStar.path(Game.getLevel(), e, e.pos, lastSeen);
                if (path == null || path.size() == 0) {
                    lastSeen = null;
                    return false;
                }
                Point first = path.get(0);
                if (first == null) {
                    lastSeen = null;
                    // wait around
                    return false;
                } else {
                    Compass dir = Compass.to(e.pos, first);
                    if (Game.random.nextInt(8) == 0) {
                        dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
                    }
                    Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
                }
            }
            else {
                if (Math.random() < 0.5) {
                    Compass direction = Compass.randomDirection();
                    if (Game.canMoveTo(e, direction.getX(), direction.getY())) {
                        Game.npcMoveBy(e, pm, direction.getX(), direction.getY());
                    }
                } else {
                    pm.setDelay(e, Game.ONE_TURN);
                }
            }
        } else {
            Entity target = EntityTracker.get(pm.targetEntityId);
            lastSeen = target.pos;
            List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
            if (path == null || path.size() == 0) {
                return false;
            }
            Point first = path.get(0);
            if (first == null) {
                // wait around
                return false;
            } else {
                Compass dir = Compass.to(e.pos, first);
                if (target.pos.equals(first)) {
                    Game.npcAttack(e, pm, dir.getX(), dir.getY());
                } else {
                    if (Game.random.nextInt(8) == 0) {
                        dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
                    }
                    Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
                    return true;
                }
            }
        }
        return false;
    }

}
