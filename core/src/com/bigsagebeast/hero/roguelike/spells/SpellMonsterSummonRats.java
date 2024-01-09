package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcHasMinions;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpellMonsterSummonRats extends Spell {
    @Override
    public SpellType getSpellType() {
        return SpellType.MONSTER;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public String getName() {
        return "Monster Summon Rats";
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        ProcHasMinions minions = (ProcHasMinions)actor.getProcByType(ProcHasMinions.class);
        if (minions == null) {
            minions = new ProcHasMinions();
            actor.addProc(minions);
        }
        minions.clean();

        if (minions.ownedEntities.size() >= ProcHasMinions.MAX_MINIONS) {
            GameLoop.warn("Summoned minions while at the limit");
            Game.announceVis(actor, null, "Nothing happens.", null, "Nothing happens.", null);
            return;
        }

        List<Point> spawnPoints = Game.getLevel().surroundingTiles(actor.pos)
                .stream().filter(p -> Game.getLevel().isSpawnable(p)).collect(Collectors.toList());
        if (spawnPoints.isEmpty()) {
            Game.announceVis(actor, null, "Nothing happens.", null, "Nothing happens.", null);
            return;
        }
        Collections.shuffle(spawnPoints);
        boolean isGiant = Game.random.nextBoolean();
        if (isGiant) {
            Game.announceVis(actor, null, "A giant rat appears!", null, "A giant rat appears!", "You hear squeaking!");
            Entity rat = Bestiary.create("rat.giant");
            rat.summoned = true;
            Game.getLevel().addEntityWithStacking(rat, spawnPoints.get(0));
            minions.ownedEntities.add(rat.entityId);
        } else {
            if (spawnPoints.size() > 1) {
                Game.announceVis(actor, null, "Large rats appear!", null, "Large rats appear!", "You hear squeaking!");
            } else {
                Game.announceVis(actor, null, "A large rat appears!", null, "A large rat appears!", "You hear squeaking!");
            }
            for (int i=0; i<3 && i<spawnPoints.size(); i++) {
                Entity rat = Bestiary.create("rat.large");
                rat.summoned = true;
                Game.getLevel().addEntityWithStacking(rat, spawnPoints.get(0));
                minions.ownedEntities.add(rat.entityId);
            }
        }

    }

    @Override
    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You cast " + getName() + ".",
                null,
                caster.getVisibleNameDefinite() + " utters words of summoning.", "You hear someone muttering.");
    }
}
