package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.SwingResult;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcShield extends Proc {
    int deflectionMelee = 20;
    int deflectionRanged = 40;
    int armorClass = 0;
    int armorThickness = 0;
    public ProcShield() { super(); }
    public ProcShield(int armorClass, int armorThickness, int deflectionMelee, int deflectionRanged) {
        this();
        this.armorClass = armorClass;
        this.armorThickness = armorThickness;
        this.deflectionMelee = deflectionMelee;
        this.deflectionRanged = deflectionRanged;
    }

    @Override
    public int provideArmorClass(Entity entity) {
        return armorClass;
    }

    @Override
    public int provideArmorThickness(Entity entity) {
        return armorThickness;
    }

    @Override
    public Boolean preBeHit(Entity entity, Entity actor, Entity tool, SwingResult result) {
        int deflectionRoll = Game.random.nextInt(100);
        Entity target = entity.getTopLevelContainer();
        if (tool != null && tool.containsProc(ProcWeaponAmmo.class) && deflectionRoll < deflectionRanged) {
            Game.announceVis(actor, target,
                    "Your shot is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s shot is deflected by your " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s shot is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    null);
            return Boolean.FALSE;
        } else if (deflectionRoll < deflectionMelee) {
            Game.announceVis(actor, target,
                    "Your blow is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s blow is deflected by your " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s blow is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    null);
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcShield(armorClass, armorThickness, deflectionMelee, deflectionRanged);
    }
}
