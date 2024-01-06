package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcItem extends Proc {

    public boolean identified; // split into ID stats vs beatitude?
    public Beatitude beatitude = Beatitude.UNCURSED;
    public int quantity = 1;

    public ProcItem() { super(); }

    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {}

    @Override
    public TextBlock getNameBlock(Entity entity) {
        return new TextBlock(entity.getVisibleNameWithQuantity(), Color.WHITE);
    }

    @Override
    public Proc clone(Entity other) {
        ProcItem pi = new ProcItem();
        pi.identified = identified;
        pi.beatitude = beatitude;
        // careful to reset this afterwards if you're destacking
        pi.quantity = quantity;
        return pi;
    }

    public boolean canStackWith(ProcItem other) {
        return other.identified == identified && other.beatitude == beatitude;
    }
}
