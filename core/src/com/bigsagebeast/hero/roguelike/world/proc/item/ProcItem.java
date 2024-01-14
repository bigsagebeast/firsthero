package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.world.ItemType;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcItem extends Proc {

    public boolean identifiedBeatitude;
    public boolean identifiedStats;
    public Beatitude beatitude = Beatitude.UNCURSED;
    public int quantity = 1;

    public ProcItem() { super(); }

    @Override
    public int getDescriptionPriority(Entity entity) {
        return -1;
    }

    @Override
    public String getUnidDescription(Entity entity) {
        // unid/iden relates to item stats like flaming swords, not to item type
        ItemType it = entity.getItemType();
        if (!it.identified) {
            return it.unidDescription.trim() + "\n";
        } else {
            return it.description.trim() + "\n";
        }
    }

    @Override
    public String getIdenDescription(Entity entity) {
        return null;
    }

    public boolean hasIdentifiedStats(Entity entity) {
        return !entity.getItemType().hasStatsToIdentify || identifiedStats;
    }

    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {}

    @Override
    public TextBlock getNameBlock(Entity entity) {
        return new TextBlock(entity.getVisibleNameWithQuantity(), Color.WHITE);
    }

    @Override
    public Proc clone(Entity entity) {
        ProcItem pi = new ProcItem();
        pi.identifiedBeatitude = identifiedBeatitude;
        pi.beatitude = beatitude;
        pi.identifiedStats = identifiedStats;
        // careful to reset this afterwards if you're destacking
        pi.quantity = quantity;
        return pi;
    }

    public boolean canStackWith(ProcItem other) {
        return other.identifiedBeatitude == identifiedBeatitude && other.beatitude == beatitude;
    }
}
