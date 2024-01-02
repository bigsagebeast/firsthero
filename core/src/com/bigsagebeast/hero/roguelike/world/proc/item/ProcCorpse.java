package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.enums.StatusType;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;

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

    public void procConfusion(Entity entity, Entity actor) {
        if (actor.testResistStatus(StatusType.CONFUSION)) {
            return;
        }
        ProcEffectConfusion proc = new ProcEffectConfusion();
        proc.turnsRemaining = 5;
        actor.addProc(proc);
        proc.initialize(actor);
    }

    public void gainFireSmall(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.FIRE, 1, 6);
        }
    }

    public void gainWaterSmall(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.WATER, 1, 6);
        }
    }

    public void gainElectricSmall(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.LIGHTNING, 1, 6);
        }
    }

    public void gainNaturaeSmall(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.NATURAE, 1, 6);
        }
    }
}
