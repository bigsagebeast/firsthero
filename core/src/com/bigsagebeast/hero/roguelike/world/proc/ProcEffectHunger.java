package com.bigsagebeast.hero.roguelike.world.proc;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Satiation;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.Player;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Terrain;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Point;

import java.util.List;

public class ProcEffectHunger extends Proc {

    public Satiation satiation = Satiation.FULL;

    public ProcEffectHunger() { super(); }

    public void setSatiation(Satiation satiation) {
        this.satiation = satiation;
    }

    public float getSpeedMultiplier(Entity entity, Entity actor) {
        if (satiation == Satiation.STARVING || satiation == Satiation.STUFFED) {
            return 0.9f;
        }
        return 1;
    }

    public int getStatModifier(Entity entity, Entity actor, Stat stat) {
        if (satiation == Satiation.STARVING) {
            if (stat == Stat.STRENGTH) {
                return -4;
            } else if (stat == Stat.AGILITY || stat == Stat.DEXTERITY) {
                return -2;
            }
        } else if (satiation == Satiation.HUNGRY) {
            if (stat == Stat.STRENGTH) {
                return -2;
            }
        } else if (satiation == Satiation.STUFFED) {
            if (stat == Stat.AGILITY) {
                return -2;
            }
        }
        return 0;
    }


    @Override
    public TextBlock getStatusBlock(Entity entity) {
        switch (satiation) {
            case DEAD:
                return new TextBlock("Starved", Color.RED);
            case STARVING:
                return new TextBlock("Starving", Color.RED);
            case HUNGRY:
                return new TextBlock("Hungry", Color.YELLOW);
            case PECKISH:
                return new TextBlock("Peckish", Color.WHITE);
            case FULL:
                break;
            case SATIATED:
                return new TextBlock("Satiated", Color.WHITE);
            case STUFFED:
                return new TextBlock("Stuffed", Color.WHITE);
        }
        return null;
    }

}
