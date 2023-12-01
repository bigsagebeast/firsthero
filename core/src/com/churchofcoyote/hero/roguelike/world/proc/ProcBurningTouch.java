package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Dice;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcBurningTouch extends Proc {
    int dice;
    int sides;
    int modifier;

    public ProcBurningTouch(Entity e, int dice, int sides, int modifier) {
        super(e);
        this.dice = dice;
        this.sides = sides;
        this.modifier = modifier;
    }

    @Override
    public void postBeHit(Entity actor, Entity tool) {
        Game.announceVis(actor, entity,
                "It burns!",
                "They burn!",
                actor.getVisibleNameWithQuantity() + " is burned!",
                null);
        actor.hurt(Dice.roll(dice, sides, modifier));
    }

    @Override
    public void postDoHit(Entity target, Entity tool) {
        Game.announceVis(entity, target,
                "They burn!",
                "You burn!",
                target.getVisibleNameWithQuantity() + " is burned!",
                null);
        target.hurt(Dice.roll(dice, sides, modifier));
    }
}
