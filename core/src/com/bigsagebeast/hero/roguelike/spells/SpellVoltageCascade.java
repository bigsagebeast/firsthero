package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Raycasting;

import java.util.*;

import static com.bigsagebeast.hero.roguelike.game.Game.announce;

public class SpellVoltageCascade extends Spell {
    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BEAM;
    }

    @Override
    public String getName() {
        return "Voltage Cascade";
    }

    @Override
    public Float getRange(Entity caster) {
        return 5.0f;
    }

    @Override
    public int getCost(Entity caster) {
        return 10;
    }

    @Override
    public boolean isDodgeable() {
        return true;
    }

    @Override
    public boolean isResistable() {
        return true;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.LIGHTNING, 1);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 10);
            arc(actor, target.pos, dir);
        }
    }

    private void affectArc(Entity caster, Entity target) {
        if (CombatLogic.castAttempt(caster, target, this)) {
            CombatLogic.castDamage(caster, target, this, 8);
        }
    }

    private void arc(Entity caster, Point branchPoint, Compass originalDir) {
        List<Compass> rayDirs = new ArrayList<>();
        rayDirs.addAll(Compass.points());
        rayDirs.remove(originalDir);
        rayDirs.remove(Compass.reverse(originalDir));
        Collections.shuffle(rayDirs);
        rayDirs.remove(0);
        rayDirs.remove(0);
        rayDirs.remove(0);
        for (Compass dir : rayDirs) {
            List<Point> ray = Raycasting.createOrthogonalRay(Game.getLevel(), branchPoint, 2, dir);
            ray.remove(null);
            if (ray.isEmpty()) {
                continue;
            }
            Point endpoint = ray.get(ray.size()-1);
            List<Entity> targets = Raycasting.findAllMoversAlongRay(Game.getLevel(), ray);
            for (Entity target : targets) {
                affectArc(caster, target);
            }
            GameLoop.targetingModule.animate(branchPoint, endpoint, getAnimationColor(), getAnimationChar());
        }
        // TODO: Back out from a wall
    }

    @Override
    public Color getAnimationColor() {
        return Color.YELLOW;
    }

    @Override
    public String getAnimationChar() {
        return "X";
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are jolted.",
                target.getVisibleNameDefinite() + " is jolted.",
                target.getVisibleNameDefinite() + " is jolted.",
                "You hear a jolt.");
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are jolted to death!",
                target.getVisibleNameDefinite() + " is jolted to death!",
                target.getVisibleNameDefinite() + " is jolted to death!",
                "You hear a jolt.");
    }




}
