package com.bigsagebeast.hero.roguelike.world.proc.item.potion;

import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.enums.ResistanceLevel;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.game.Game;

public class ProcPotionAcid extends ImmutableProc {

    @Override
    public Boolean targetForQuaff(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeQuaffed(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeQuaffed(Entity entity, Entity actor) {
        int hurtAmount = 10 + Game.random.nextInt(10);
        switch (entity.getItem().beatitude) {
            case BLESSED:
                hurtAmount *= 0.67f;
                break;
            case CURSED:
                hurtAmount *= 1.5f;
                break;
        }
        burnMessage(actor);
        actor.hurt(hurtAmount, DamageType.ACID, "an acid potion");
        entity.identifyItemType();
    }

    private void burnMessage(Entity actor) {
        ResistanceLevel acidResistance = actor.getDamageTypeResist(DamageType.ACID);
        switch (acidResistance) {
            case WEAK:
                Game.announceVisLoud(actor, null, "It burns excruciatingly!",
                        null,
                        actor.getVisibleNameDefinite() + " is badly burned!",
                        null);
                break;
            case NORMAL:
                Game.announceVisLoud(actor, null, "It burns!",
                        null,
                        actor.getVisibleNameDefinite() + " is burned!",
                        null);
                break;
            case RESISTANT:
            case VERY_RESISTANT:
                Game.announceVisLoud(actor, null, "It burns a little.",
                        null,
                        actor.getVisibleNameDefinite() + " is burned a little.",
                        null);
                break;
            case IMMUNE:
                Game.announceVis(actor, null, "There is no effect.",
                        null,
                        actor.getVisibleNameDefinite() + " is not affected.",
                        null);
                break;
        }
    }
}
