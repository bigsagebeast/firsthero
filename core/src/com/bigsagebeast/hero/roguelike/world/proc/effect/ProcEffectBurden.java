package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Burden;
import com.bigsagebeast.hero.enums.Satiation;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectBurden extends Proc {

    public Burden burden = Burden.UNBURDENED;

    public ProcEffectBurden() { super(); }

    public void setBurden(Burden burden) {
        this.burden = burden;
    }

    public float getSpeedMultiplier(Entity entity, Entity actor) {
        if (burden == Burden.BURDENED) {
            return 0.95f;
        } else if (burden == Burden.STRAINED) {
            return 0.9f;
        } else if (burden == Burden.OVERLOADED) {
            return 0.5f;
        }
        return 1;
    }

    @Override
    public Boolean preCmdMove(Entity entity, Entity actor) {
        if (burden == Burden.OVERLOADED) {
            if (actor == Game.getPlayerEntity()) {
                Game.announceLoud("You are carrying too much to move!");
            }
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        if (burden.description.isEmpty()) {
            return null;
        } else {
            return new TextBlock(burden.description, burden.color);
        }
    }

}
