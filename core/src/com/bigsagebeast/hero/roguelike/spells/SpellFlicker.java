package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.GameSpecials;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellFlicker extends Spell {
    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public String getName() {
        return "Flicker";
    }

    @Override
    public String getDescription() {
        return "Randomly teleports you a short distance to a point at least 3 tiles away, even through walls.";
    }

    @Override
    public Float getBaseRange() {
        return 8.0f;
    }

    @Override
    public int getCost(Entity caster) {
        return 10;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.FIRE, 2);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        boolean canSeeBefore = Game.getPlayerEntity().canSee(target);
        boolean result = GameSpecials.blink(target, 3, getRange(actor).intValue());
        if (result) {
            boolean canSeeAfter = Game.getPlayerEntity().canSee(target);
            if (Game.getPlayerEntity() == actor) {
                Game.announce("You flicker to a new location.");
            } else {
                if (canSeeBefore && canSeeAfter) {
                    Game.announce(target.getVisibleNameDefinite() + " flickers to a new location.");
                } else if (canSeeBefore) {
                    Game.announce(target.getVisibleNameDefinite() + " flickers out of sight.");
                } else if (canSeeAfter) {
                    Game.announce(target.getVisibleNameDefinite() + " flickers into sight.");
                }
            }
        } else {
            if (canSeeBefore) {
                // presume that a message about the casting has already been generated
                Game.announce("Nothing happens.");
            }
        }
    }
}
