package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcChargeRegen extends Proc {
    private int chargeAccumulator;
    private int rate;
    private Element element;

    public ProcChargeRegen() { super(); }
    @Override
    public void turnPassed(Entity entity) {
        // TODO isEquipped
        super.turnPassed(entity);
        if (entity.containingEntity == -1 || !entity.isEquipped()) {
            return;
        }
        Entity containingEntity = EntityTracker.get(entity.containingEntity);
        if (containingEntity != Game.getPlayerEntity()) {
            return;
        }
        int effectiveRate = rate;
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            effectiveRate *= 5 / 4;
        } else if (entity.getBeatitude() == Beatitude.CURSED) {
            effectiveRate *= 2;
        }
        chargeAccumulator++;
        if (chargeAccumulator >= effectiveRate) {
            chargeAccumulator = 0;
            if (Game.getPlayer().elementMissing(element) > 0) {
                Game.getPlayer().changeCharges(element, 1);
                Game.announce("Your " + entity.getVisibleName() + " pulses with power.");
            }
        }
    }

}
