package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.ItemStatus;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.text.TextBlock;

public class ProcItem extends Proc {

    public ItemStatus status;
    public boolean identified;
    public int quantity = 1;

    protected ProcItem() {}
    public ProcItem(Entity e) {
        super(e);
    }

    @Override
    public Boolean preBePickedUp(Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity actor) {}

    @Override
    public TextBlock getNameBlock() {
        return new TextBlock(entity.getVisibleNameWithQuantity(), Color.WHITE);
    }

    @Override
    public Proc clone(Entity other) {
        ProcItem pi = new ProcItem(other);
        pi.status = status;
        pi.identified = identified;
        // careful to reset this afterwards if you're destacking
        pi.quantity = quantity;
        return pi;
    }
}
