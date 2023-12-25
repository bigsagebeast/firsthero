package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcCorpse extends Proc {

    public int age = 0;
    public int satiation = 500;
    public String eatMessage;

    public ProcCorpse() { super(); }

    public void turnPassed(Entity entity) {
        age++;
        if (age > 100) {
            entity.destroy();
            Game.announceVis(entity, entity, null, null,
                    "You see " + entity.getVisibleNameSingularOrVague() + " rot away.", null);
        }
    }

    @Override
    public Boolean isEdible(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeEaten(Entity entity, Entity actor) {
        if (eatMessage != null) {
            Game.announce(eatMessage);
        }
        // Can anything else eat?
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().changeSatiation(satiation);
        }
    }

    public void gainElectricSmall(Entity entity, Entity actor) {
        Game.getPlayer().gainStatElement(Element.LIGHTNING, 1, 6);
    }
}
