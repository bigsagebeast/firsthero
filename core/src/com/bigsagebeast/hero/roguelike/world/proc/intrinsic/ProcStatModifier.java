package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.GameSpecials;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcTimedEffect;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcArmor;
import com.bigsagebeast.hero.text.TextBlock;

import java.util.Arrays;
import java.util.List;

public class ProcStatModifier extends ProcTimedEffect {
    public Stat stat;
    public int value;
    public ProcStatModifier() {
    }

    public ProcStatModifier(Stat stat, int value) {
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void postBeEquipped(Entity entity, BodyPart bp, Entity actor) {
        GameSpecials.announceStatChange(stat, value);
        entity.identifyItemFully();
    }

    @Override
    public void postBeUnequipped(Entity entity, BodyPart bp, Entity actor) {
        GameSpecials.announceStatChange(stat, -value);
    }

    @Override
    public int getStatModifier(Entity entity, Entity actor, Stat statCheck) {
        if (statCheck == stat) {
            return value;
        }
        return 0;
    }

    @Override
    public int getDescriptionPriority(Entity entity) {
        return 1;
    }

    @Override
    public String getIdenDescription(Entity entity) {
        // TODO pluralize
        return "It alters your " + stat.description() + " by " + value + ".";
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcStatModifier(stat, value);
    }
}
