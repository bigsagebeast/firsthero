package com.churchofcoyote.hero.roguelike.world.proc;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;

public class ProcItem extends Proc {
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
        return new TextBlock(entity.name, Color.WHITE);
    }
}
