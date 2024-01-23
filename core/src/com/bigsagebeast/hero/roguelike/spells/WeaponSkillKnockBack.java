package com.bigsagebeast.hero.roguelike.spells;

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

public class WeaponSkillKnockBack extends Spell {
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
        return "Knock Back";
    }

    @Override
    public String getDescription() { return "Attack an adjacent enemy for a guaranteed hit and to knock them back up to three squares."; };

    @Override
    public int getBaseCost(Entity caster) { return 15; }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.NATURAE, 2); }

    @Override
    public void castDirectionally(Entity caster, Compass dir) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        announceCast(caster, null);
        Point targetPoint = dir.from(caster.pos);
        Point landingPoint1 = dir.from(targetPoint);
        Point landingPoint2 = dir.from(landingPoint1);
        Point landingPoint3 = dir.from(landingPoint2);

        Entity target = Game.getLevel().moverAt(targetPoint);
        if (target != null) {
            ProcEffectGuaranteedHit proc = new ProcEffectGuaranteedHit();
            caster.procs.add(proc);
            Game.attack(caster, target);
            caster.procs.remove(proc);
            if (target.dead) {
                return;
            }

            if (!Game.isBlockedByAnything(target, landingPoint1)) {
                Game.announceVis(caster, target,
                        target.getVisibleNameDefinite() + " is knocked back!",
                        "You are knocked back!",
                        target.getVisibleNameDefinite() + " is knocked back!",
                        null);
                if (!Game.isBlockedByAnything(target, landingPoint2)) {
                    if (!Game.isBlockedByAnything(target, landingPoint3)) {
                        target.pos = landingPoint3;
                    } else {
                        target.pos = landingPoint2;
                    }
                } else {
                    target.pos = landingPoint1;
                }
            } else {
                Game.announceVis(caster, target,
                        Util.capitalize(target.gender.subjective) + " isn't knocked back.",
                        "You aren't knocked back.",
                        Util.capitalize(target.gender.subjective) + " isn't knocked back.",
                        null);
            }
        } else {
            Game.announceVis(caster, null, "There's nothing there to target.", null, null, null);
        }
        Game.turn();
    }
}
