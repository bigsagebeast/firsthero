package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Raycasting;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.*;

import static com.bigsagebeast.hero.roguelike.game.Game.announce;

public abstract class Spell {
    public enum TargetType {
        BOLT("Bolt"),
        BEAM("Beam"),
        MELEE("Melee"),
        BALL("Ball"),
        PERSONAL("Personal"),
        OTHER("OTHER");

        public String name;
        TargetType(String name) {
            this.name = name;
        }
    }

    public enum SpellType {
        MONSTER("Monster Ability", "SP"),
        ARCANUM("Arcanum", "SP"),
        WEAPON_SKILL("Weapon Skill", "SP"),
        DIVINE("Divine Power", "DP");

        public String name;
        public String cost;

        SpellType(String name, String cost) {
            this.name = name;
            this.cost = cost;
        }
    }

    public Map<Stat, EquipmentScaling> scaling = new HashMap<>();

    public abstract TargetType getTargetType();

    public abstract SpellType getSpellType();

    public abstract String getName();

    public String getDescription() { return "DESCRIPTION MISSING"; };

    public String getTypeDescription() {
        return getTargetType().name;
    }

    public int getCost(Entity caster) { return 0; };

    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.emptyMap(); }

    public boolean isDodgeable() { return false; }

    public boolean isResistable() { return false; }

    public Float getBaseDamage() { return null; }
    public Float getBaseDuration() { return null; }
    public Float getBaseRange() { return null; }
    public Map<Stat, EquipmentScaling> getScaling() { return scaling; }

    public Float getDamage(Entity caster) {
        if (getBaseDamage() == null) {
            return null;
        }
        float accum = getBaseDamage();
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(caster.statblock.get(stat), scaling.get(stat).damage);
        }
        return accum;
    }

    public Integer getDuration(Entity caster) {
        if (getBaseDuration() == null) {
            return null;
        }
        float accum = getBaseDuration();
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(caster.statblock.get(stat), scaling.get(stat).duration);
        }
        return (int)accum;
    }

    public Float getRange(Entity caster) {
        if (getBaseRange() == null) {
            return null;
        }
        float accum = getBaseRange();
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(caster.statblock.get(stat), scaling.get(stat).range);
        }
        return accum;
    }



    public void playerStartSpell() {
        if (getSpellType() == SpellType.WEAPON_SKILL || getSpellType() == SpellType.ARCANUM) {
            if (Game.getPlayerEntity().spellPoints < getCost(Game.getPlayerEntity())) {
                Game.announce("You don't have enough SP for that spell.");
                return;
            }
        } else {
            if (Game.getPlayerEntity().divinePoints < getCost(Game.getPlayerEntity())) {
                Game.announce("You don't have enough DP for that spell.");
                return;
            }
        }
        Map<Element, Integer> elementCost = getElementCost(Game.getPlayerEntity());
        for (Element element : elementCost.keySet()) {
            if (Game.getPlayer().currentElementCharges.get(element) < elementCost.get(element)) {
                Game.announce("You don't have enough " + element.name + " charges for that.");
                return;
            }
        }
        /*
        GameLoop.directionModule.begin("Select a direction to cast " + getName() + ", or space to cancel.",
                this::handlePlayerStartSpell);
         */
        switch (getTargetType()) {
            case BEAM:
            case BOLT:
            case MELEE:
                GameLoop.directionModule.begin("Select a direction, or space to cancel.",
                        this::handlePlayerStartSpell);
                break;
            case PERSONAL:
                handlePlayerStartSpell(null);
                break;
        }
    }

    public void handlePlayerStartSpell(Compass dir) {
        if (dir == Compass.OTHER) {
            announce("Cancelled.");
            return;
        }
        if (dir != null && Game.getPlayerEntity().isConfused()) {
            dir = Compass.randomDirection();
            Game.announce("You fire the spell in a random direction!");
        }
        if (getSpellType() == SpellType.WEAPON_SKILL || getSpellType() == SpellType.ARCANUM) {
            Game.getPlayerEntity().spellPoints -= getCost(Game.getPlayerEntity());
        } else {
            Game.getPlayerEntity().divinePoints -= getCost(Game.getPlayerEntity());
        }
        Map<Element, Integer> elementCost = getElementCost(Game.getPlayerEntity());
        for (Element element : elementCost.keySet()) {
            Game.getPlayer().changeCharges(element, -elementCost.get(element));
        }
        switch (getTargetType()) {
            case BEAM:
            case BOLT:
            case MELEE:
                castDirectionally(Game.getPlayerEntity(), dir);
                break;
            case PERSONAL:
                castPersonal(Game.getPlayerEntity());
                break;
        }
    }

    public void castDirectionally(Entity caster, Compass dir) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        announceCast(caster, null);
        List<Point> ray = Raycasting.createOrthogonalRay(Game.getLevel(), caster.pos, Math.round(getRange(caster)), dir);
        ray.remove(null);
        if (ray.isEmpty()) {
            announce("Nothing happens.");
            Game.turn();
            return;
        }
        // TODO: Back out from a wall
        Point endpoint = ray.get(ray.size()-1);
        List<Entity> targets;
        if (getTargetType() == TargetType.BEAM) {
            targets = Raycasting.findAllMoversAlongRay(Game.getLevel(), ray);
        } else if (getTargetType() == TargetType.BOLT || getTargetType() == TargetType.MELEE) {
            targets = Raycasting.findFirstMoversAlongRay(Game.getLevel(), ray);
            if (!targets.isEmpty()) {
                endpoint = targets.get(0).pos;
            }
        } else {
            throw new RuntimeException("Tried to directionally target an invalid spell");
        }
        GameLoop.targetingModule.animate(caster.pos, endpoint, getAnimationColor(), getAnimationChar());

        affectTargets(caster, targets, dir);
        Game.turn();
    }

    public void castPersonal(Entity caster) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        announceCast(caster, null);
        affectTargets(caster, Collections.singletonList(caster), null);
        Game.turn();
    }

    public void affectTargets(Entity caster, Collection<Entity> targets, Compass dir) {
        for (Entity target : targets) {
            // TODO: only for harmful spells
            if (caster == Game.getPlayerEntity()) {
                target.getMover().logRecentlyAttacked();
            }
            affectTarget(caster, target, dir);
        }
    }

    public abstract void affectTarget(Entity caster, Entity target, Compass dir);

    public Color getAnimationColor() {
        return Color.WHITE;
    }

    public String getAnimationChar() {
        return null;
    }

    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You cast " + getName() + ".",
                null,
                caster.getVisibleNameDefinite() + " casts " + getName() + ".", "You hear someone muttering.");
    }

    public void announceDodged(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You dodge the spell!",
                target.getVisibleNameDefinite() + " dodges the spell!",
                target.getVisibleNameDefinite() + " dodges the spell!", null);
    }

    public void announceResisted(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You resist the spell!",
                target.getVisibleNameDefinite() + " resists the spell!",
                target.getVisibleNameDefinite() + " resists the spell!", null);
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are hit by the spell!",
                target.getVisibleNameDefinite() + " is hit by the spell!",
                target.getVisibleNameDefinite() + " is hit by the spell!", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are killed by the spell!",
                target.getVisibleNameDefinite() + " is killed by the spell!",
                target.getVisibleNameDefinite() + " is killed by the spell!", null);
    }

}
