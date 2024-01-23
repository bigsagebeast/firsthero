package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEffectGuaranteedHit;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedMissileDeflection;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.Collections;
import java.util.Map;

public class WeaponSkillCleave extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WEAPON_SKILL;
    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.AXE;
    }

    @Override
    public String getName() {
        return "Cleave";
    }

    @Override
    public String getDescription() { return "Make a normal melee attack against each adjacent opponent."; }

    @Override
    public int getBaseCost(Entity caster) { return 10; }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.NATURAE, 2); }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        for (Compass compass : Compass.points()) {
            Entity victim = Game.getLevel().moverAt(compass.from(actor.pos));
            if (victim != null) {
                Game.attack(actor, victim);
            }
        }
    }
}
