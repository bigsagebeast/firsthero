package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.ItemStatus;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcItem extends Proc {

    public ItemStatus status;
    public boolean identified;
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
    public Proc clone() {
        ProcItem pi = new ProcItem();
        pi.status = status;
        pi.identified = identified;
        // careful to reset this afterwards if you're destacking
        pi.quantity = quantity;
        return pi;
    }
}
