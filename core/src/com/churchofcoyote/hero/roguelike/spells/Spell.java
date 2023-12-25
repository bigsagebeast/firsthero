package com.churchofcoyote.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;
import com.churchofcoyote.hero.util.Raycasting;

import java.util.*;

import static com.churchofcoyote.hero.roguelike.game.Game.announce;

public abstract class Spell {
    public enum TargetType {
        BOLT("Bolt"),
        BEAM("Beam"),
        MELEE("Melee"),
        BALL("Ball"),
        OTHER("OTHER");

        public String name;
        TargetType(String name) {
            this.name = name;
        }
    }

    public abstract TargetType getTargetType();

    public abstract String getName();

    public String getTypeDescription() {
        return getTargetType().name;
    }

    public abstract float getRange(Entity caster);

    public abstract int getCost(Entity caster);

    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.emptyMap(); }

    public void playerStartSpell() {
        if (Game.getPlayerEntity().spellPoints < getCost(Game.getPlayerEntity())) {
            Game.announce("You don't have enough SP for that spell.");
            return;
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
        GameLoop.directionModule.begin("Select a direction, or space to cancel.",
                this::handlePlayerStartSpell);
    }

    public void handlePlayerStartSpell(Compass dir) {
        if (dir == Compass.OTHER) {
            announce("Cancelled.");
            return;
        }
        Game.getPlayerEntity().spellPoints -= getCost(Game.getPlayerEntity());
        Map<Element, Integer> elementCost = getElementCost(Game.getPlayerEntity());
        for (Element element : elementCost.keySet()) {
            Game.getPlayer().changeCharges(element, -elementCost.get(element));
        }
        castDirectionally(Game.getPlayerEntity(), dir);
    }

    public void castDirectionally(Entity caster, Compass dir) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        announceCast(caster, null);
        List<Point> ray = Raycasting.createOrthogonalRay(Game.getLevel(), caster.pos, Math.round(getRange(caster)), dir);
        if (ray.size() <= 1) {
            announce("Nothing happens.");
            return;
        }
        Point endpoint = ray.get(ray.size() - 2);
        List<Entity> targets;
        if (getTargetType() == TargetType.BEAM) {
            targets = Raycasting.findAllMoversAlongRay(Game.getLevel(), ray);
        } else if (getTargetType() == TargetType.BOLT) {
            targets = Raycasting.findFirstMoversAlongRay(Game.getLevel(), ray);
            if (!targets.isEmpty()) {
                endpoint = targets.get(0).pos;
            }
        } else if (getTargetType() == TargetType.MELEE) {
            targets = Collections.EMPTY_LIST;
            Game.announce("UNIMPLEMENTED MELEE SPELL");
        } else {
            throw new RuntimeException("Tried to directionally target an invalid spell");
        }
        GameLoop.targetingModule.animate(ray.get(0), endpoint, getAnimationColor(), isAnimationStars());

        affectTargets(caster, targets, dir);
    }

    public void affectTargets(Entity caster, Collection<Entity> targets, Compass dir) {
        for (Entity target : targets) {
            affectTarget(caster, target, dir);
        }
    }

    public abstract void affectTarget(Entity caster, Entity target, Compass dir);

    public Color getAnimationColor() {
        return Color.WHITE;
    }

    public boolean isAnimationStars() {
        return false;
    }

    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You cast " + getName() + ".",
                null,
                caster.getVisibleNameThe() + " casts " + getName() + ".", "You hear someone muttering.");
    }

    public void announceDodged(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You dodge the spell!",
                target.getVisibleNameThe() + " dodges the spell!",
                target.getVisibleNameThe() + " dodges the spell!", null);
    }

    public void announceResisted(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You resist the spell!",
                target.getVisibleNameThe() + " resists the spell!",
                target.getVisibleNameThe() + " resists the spell!", null);
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are hit by the spell!",
                target.getVisibleNameThe() + " is hit by the spell!",
                target.getVisibleNameThe() + " is hit by the spell!", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are killed by the spell!",
                target.getVisibleNameThe() + " is killed by the spell!",
                target.getVisibleNameThe() + " is killed by the spell!", null);
    }

}
