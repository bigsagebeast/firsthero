package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.BodySize;
import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEffectGuaranteedHit;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.Collections;
import java.util.Map;

public class WeaponSkillLaunchOver extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.MELEE;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WEAPON_SKILL;
    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.BLUDGEON;
    }

    @Override
    public String getName() {
        return "Launch Over";
    }

    @Override
    public String getDescription() { return "Flip an enemy over your head, positioning it in the space behind you. Doesn't work on enemies that are larger than you."; };

    @Override
    public int getCost(Entity caster) { return 15; }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.LIGHTNING, 2); }

    @Override
    public void castDirectionally(Entity caster, Compass dir) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        announceCast(caster, null);
        Point targetPoint = dir.from(caster.pos);
        Point landingPoint = Compass.reverse(dir).from(caster.pos);

        Entity target = Game.getLevel().moverAt(targetPoint);
        if (target != null) {
            ProcEffectGuaranteedHit proc = new ProcEffectGuaranteedHit();
            caster.procs.add(proc);
            Game.attack(caster, target);
            caster.procs.remove(proc);
            if (target.dead) {
                return;
            }

            if (target.getPhenotype().size == BodySize.LARGE || target.getPhenotype().size == BodySize.XL) {
                Game.announceVis(caster, target,
                        target.getVisibleNameDefinite() + " is too big to launch.",
                        "You are too big to launch.",
                        target.getVisibleNameDefinite() + " is too big to launch.",
                        null);
                return;
            }

            if (!Game.isBlockedByAnything(target, landingPoint)) {
                Game.announceVis(caster, target,
                        target.getVisibleNameDefinite() + " is launched over your head!",
                        "You are launched above " + caster.gender.objective + "!",
                        target.getVisibleNameDefinite() + " is launched over your head!",
                        null);
                target.pos = landingPoint;
            } else {
                Game.announceVis(caster, target,
                        Util.capitalize(target.gender.subjective) + " isn't launched because there is no room.",
                        "You aren't launched because there is no room.",
                        Util.capitalize(target.gender.subjective) + " isn't launched because there is no room.",
                        null);
            }
        } else {
            Game.announceVis(caster, null, "There's nothing there to target.", null, null, null);
        }
        Game.turn();
    }
}
