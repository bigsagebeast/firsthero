package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcDoor extends Proc {
    public ProcDoor(Entity e) {
        super(e);
        open();
    }

    public boolean isOpen;

    @Override
    public Boolean preBeOpened(Entity actor) {
        if (!isOpen) {
            return Boolean.TRUE;
        }
        Game.announceVis(actor, null, "The door is already open.", null, null, null);
        return Boolean.FALSE;
    }

    @Override
    public Boolean preBeClosed(Entity actor) {
        if (isOpen) {
            return Boolean.TRUE;
        }
        Game.announceVis(actor, null, "The door is already closed.", null, null, null);
        return Boolean.FALSE;
    }

    @Override
    public void postBeOpened(Entity actor) {
        Game.announceVis(actor, null, "You open the door.", null, actor.name + " opens the door.", "You hear a door being opened.");
        open();
    }

    @Override
    public void postBeClosed(Entity actor) {
        Game.announceVis(actor, null, "You close the door.", null, actor.name + " closes the door.", "You hear a door being closed.");
        close();
    }

    @Override
    public Boolean isObstructive() {
        return !isOpen;
    }

    @Override
    public Boolean isObstructiveToManipulators() {
        // humanoids can open doors, so moving will be turned into opening
        return Boolean.FALSE;
    }

    @Override
    public Boolean isObstructiveToVision() {
        return !isOpen;
    }

    @Override
    public void postBeSteppedOn(Entity e) {
        Game.announceVis(e, null, "You pass through a door.", null, null, null);
    }

    public void open() {
        entity.glyphName = "terrain.door_open";
        entity.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT, Palette.COLOR_BROWN);
        entity.name = "open door";
        EntityGlyph.updateEntity(entity);
        isOpen = true;
    }

    public void close() {
        entity.glyphName = "terrain.door_closed";
        entity.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_TAN, Palette.COLOR_BROWN);
        entity.name = "closed door";
        EntityGlyph.updateEntity(entity);
        isOpen = false;
    }
}
