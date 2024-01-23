package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.roguelike.game.Dice;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.SwingResult;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcBurningTouch extends Proc {
    int dice;
    int sides;
    int modifier;
    public ProcBurningTouch() { super(); }

    public ProcBurningTouch(int dice, int sides, int modifier) {
        this();
        this.dice = dice;
        this.sides = sides;
        this.modifier = modifier;
    }

    @Override
    public void postBeHit(Entity entity, Entity actor, Entity tool, SwingResult result) {
        if (tool != null && tool.getEquippable().equipmentFor == BodyPart.RANGED_AMMO) {
            return;
        }
        Game.announceVisLoud(actor, entity,
                "You are burned!",
                "%S %l burned!",
                actor.getVisibleNameWithQuantity() + " is burned!",
                null);
        actor.hurt(Dice.roll(dice, sides, modifier), entity.getVisibleNameIndefiniteOrSpecific() + "'s burning touch");
    }

    @Override
    public void postDoHit(Entity entity, Entity target, Entity tool, SwingResult result) {
        Game.announceVisLoud(entity, target,
                "%S %l burned!",
                "You are burned!",
                target.getVisibleNameWithQuantity() + " is burned!",
                null);
        target.hurt(Dice.roll(dice, sides, modifier), entity.getVisibleNameIndefiniteOrSpecific() + "'s burning touch");
    }
}
