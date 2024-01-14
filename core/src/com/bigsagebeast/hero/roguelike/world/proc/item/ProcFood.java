package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.enums.StatusType;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProcFood extends Proc {

    public int satiation = 500;
    public String eatMessage;
    public String eatMethod;

    public ProcFood() {
        super();
    }
    public ProcFood(String eatMessage, String eatMethod, int satiation) {
        this.eatMessage = eatMessage;
        this.eatMethod = eatMethod;
        this.satiation = satiation;
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
        if (eatMethod != null) {
            try {
                Method method = ProcFood.class.getDeclaredMethod(eatMethod, Entity.class, Entity.class);
                method.invoke(this, entity, actor);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown food method " + eatMethod);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        // Can anything else eat?
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().changeSatiation(satiation);
        }
    }

    @Override
    public ProcFood clone(Entity entity) {
        return new ProcFood(eatMessage, eatMethod, satiation);
    }
}
