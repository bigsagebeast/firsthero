package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEffectGuaranteedHit;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.Collections;
import java.util.Map;

public class WeaponSkillRecoil extends Spell {
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
        return WeaponType.THIN_BLADE;
    }

    @Override
    public String getName() {
        return "Recoil";
    }

    @Override
    public String getDescription() { return "Strike an adjacent opponent, then duck backward before they can retaliate. Your blow is guaranteed to land."; };

    @Override
    public int getBaseCost(Entity caster) { return 10; }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.WATER, 2); }

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
        } else {
            Game.announceVis(caster, null, "There's nothing there to target.", null, null, null);
        }

        if (Game.isBlockedByAnything(caster, landingPoint)) {
            Game.announceVis(caster, null, "You can't back up.", null, null, null);
        } else {
            caster.pos = landingPoint;
        }
        Game.turn();
    }
}
