package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProcCorpse extends Proc {

    public int age = 0;
    public int satiation = 500;
    public String corpseMessage;
    public String corpseMethod;

    public ProcCorpse() { super(); }
    public ProcCorpse(String corpseMessage, String corpseMethod) {
        this.corpseMessage = corpseMessage;
        this.corpseMethod = corpseMethod;
    }

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
        if (corpseMessage != null) {
            Game.announce(corpseMessage);
        }
        if (corpseMethod != null) {
            try {
                Method method = ProcCorpse.class.getDeclaredMethod(corpseMethod, Entity.class, Entity.class);
                method.invoke(this, entity, actor);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown corpse method " + corpseMethod);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        // Can anything else eat?
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().changeSatiation(satiation);
        }
    }

    public void gainFireSmall(Entity entity, Entity actor) {
        Game.getPlayer().gainStatElement(Element.FIRE, 1, 6);
    }

    public void gainWaterSmall(Entity entity, Entity actor) {
        Game.getPlayer().gainStatElement(Element.WATER, 1, 6);
    }

    public void gainElectricSmall(Entity entity, Entity actor) {
        Game.getPlayer().gainStatElement(Element.LIGHTNING, 1, 6);
    }

    public void gainPlantSmall(Entity entity, Entity actor) {
        Game.getPlayer().gainStatElement(Element.PLANT, 1, 6);
    }
}
