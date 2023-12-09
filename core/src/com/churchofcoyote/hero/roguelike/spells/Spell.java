package com.churchofcoyote.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;
import com.churchofcoyote.hero.util.Raycasting;

import java.util.Collections;
import java.util.List;

import static com.churchofcoyote.hero.roguelike.game.Game.announce;

public abstract class Spell {
    public enum TargetType {
        RAY("Ray"),
        LINE("Line"),
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

    public void playerStartSpell() {
        if (Game.getPlayerEntity().spellPoints < getCost(Game.getPlayerEntity())) {
            Game.announce("You don't have enough SP for that spell.");
            return;
        }
        GameLoop.directionModule.begin("Select a direction to cast " + getName() + ", or space to cancel.",
                this::handlePlayerStartSpell);
    }

    public void handlePlayerStartSpell(Compass dir) {
        if (dir == Compass.OTHER) {
            announce("Cancelled.");
            return;
        }
        Game.getPlayerEntity().spellPoints -= getCost(Game.getPlayerEntity());
        castDirectionally(Game.getPlayerEntity(), dir);
    }

    public void castDirectionally(Entity caster, Compass dir) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        List<Point> ray = Raycasting.createOrthogonalRay(Game.getLevel(), caster.pos, dir);
        if (ray.size() <= 1) {
            announce("Nothing happens.");
            return;
        }
        Point endpoint = ray.get(ray.size()-2);
        List<Entity> targets;
        if (getTargetType() == TargetType.LINE) {
            targets = Raycasting.findAllMoversAlongRay(Game.getLevel(), ray);
        } else if (getTargetType() == TargetType.RAY) {
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

        for (Entity target : targets) {
            affectTarget(caster, target);
        }
    }

    public abstract void affectTarget(Entity caster, Entity target);

    public Color getAnimationColor() {
        return Color.WHITE;
    }

    public boolean isAnimationStars() {
        return false;
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
