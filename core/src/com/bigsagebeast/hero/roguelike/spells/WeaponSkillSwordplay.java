package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEffectGuaranteedHit;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.Collections;
import java.util.Map;

public class WeaponSkillSwordplay extends Spell {
    public WeaponSkillSwordplay() {
        scaling.put(Stat.AGILITY, new EquipmentScaling());
        scaling.get(Stat.AGILITY).duration = 0.5f;
    }

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
        return WeaponType.BROAD_BLADE;
    }

    @Override
    public String getName() {
        return "Swordplay";
    }

    @Override
    public String getDescription() { return "Confuse an enemy with deft footwork and maneuvering. Deals no damage. Does not stack."; };

    @Override
    public int getBaseCost(Entity caster) { return 10; }

    @Override
    public Float getBaseDuration() {
        return 6f;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.WATER, 2); }

    @Override
    public void castDirectionally(Entity caster, Compass dir) {
        caster.getMover().setDelay(caster, Game.ONE_TURN);
        announceCast(caster, null);
        Point targetPoint = dir.from(caster.pos);

        Entity target = Game.getLevel().moverAt(targetPoint);
        if (target != null) {
            if (target.getProcByType(ProcEffectConfusion.class) != null) {
                ProcEffectConfusion confusionProc = new ProcEffectConfusion();
                confusionProc.turnsRemaining = getDuration(caster);
                target.addProc(confusionProc);
            }
        } else {
            Game.announceVis(caster, null, "There's nothing there to target.", null, null, null);
        }

        Game.turn();
    }
}
