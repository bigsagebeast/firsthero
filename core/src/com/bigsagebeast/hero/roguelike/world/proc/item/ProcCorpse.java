package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.enums.StatusType;
import com.bigsagebeast.hero.roguelike.game.EntityProc;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectPoisoned;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectVomit;
import com.bigsagebeast.hero.roguelike.world.proc.intrinsic.ProcResistDamageType;
import com.bigsagebeast.hero.roguelike.world.proc.intrinsic.ProcTelepathy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProcCorpse extends Proc {

    public int age = 0;
    public int satiation = 500;
    public String corpseMessage;
    public String corpseMethod;
    public String corpseMethodPre;

    public ProcCorpse() { super(); }
    public ProcCorpse(String corpseMessage, String corpseMethod, String corpseMethodPre) {
        this.corpseMessage = corpseMessage;
        this.corpseMethod = corpseMethod;
        this.corpseMethodPre = corpseMethodPre;
    }

    public void turnPassed(Entity entity) {
        age++;
        if (age > 100) {
            entity.destroy();
            Game.announceVis(entity, entity, null, null,
                    "You see " + entity.getVisibleNameIndefiniteOrVague() + " rot away.", null);
        }
    }

    @Override
    public Boolean isEdible(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean preBeEaten(Entity entity, Entity actor) {
        if (corpseMethodPre != null) {
            try {
                Method method = ProcCorpse.class.getDeclaredMethod(corpseMethodPre, Entity.class, Entity.class);
                Boolean result = (Boolean)method.invoke(this, entity, actor);
                return result;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unknown corpse method " + corpseMethod);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
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

    public void procPoison(Entity entity, Entity actor) {
        ProcEffectPoisoned effect = (ProcEffectPoisoned)actor.getProcByType(ProcEffectPoisoned.class);
        if (effect == null) {
            effect = new ProcEffectPoisoned();
            effect.strength = 5;
            effect.turnsRemaining = 13;
            actor.addProc(effect);
            effect.damageCountdown = 3;
        } else {
            effect.increaseDuration(actor, 25);
        }
    }

    public void procConfusion(Entity entity, Entity actor) {
        if (actor.testResistStatus(StatusType.CONFUSION)) {
            return;
        }
        ProcEffectConfusion proc = new ProcEffectConfusion();
        proc.turnsRemaining = 5;
        actor.addProc(proc);
    }

    public void procVomit(Entity entity, Entity actor) {
        ProcEffectVomit proc = new ProcEffectVomit();
        proc.turnsRemaining = 3;
        actor.addProc(proc);
    }

    public Boolean doppelganger(Entity entity, Entity actor) {
        Game.announce("You can't bear to eat yourself.");
        return Boolean.FALSE;
    }

    public void poisonAndGainResistance(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            boolean alreadyResists = actor.getProcsByTypeIncludingEquipment(Collections.singletonList(ProcResistDamageType.class)).stream()
                    .anyMatch(proc -> proc.provideDamageTypeResist(actor).contains(DamageType.POISON));
            if (!alreadyResists) {
                ProcResistDamageType procResistant = new ProcResistDamageType();
                procResistant.damageType = DamageType.POISON;
                actor.addProc(procResistant);
                Game.announce("You build up a tolerance to the poison, and feel resistant.");
            }

            ProcEffectPoisoned procPoisoned = new ProcEffectPoisoned();
            procPoisoned.strength = 3;
            procPoisoned.turnsRemaining = 13;
            actor.addProc(procPoisoned);
        }
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

    public void gainFireMedium(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.FIRE, 1, 8);
        }
    }

    public void gainWaterMedium(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.WATER, 1, 8);
        }
    }

    public void gainElectricMedium(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.LIGHTNING, 1, 8);
        }
    }

    public void gainNaturaeMedium(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.getPlayer().gainStatElement(Element.NATURAE, 1, 8);
        }
    }
}
