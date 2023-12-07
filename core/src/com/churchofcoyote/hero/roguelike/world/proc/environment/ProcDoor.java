package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcDoor extends ProcFeature {
    public ProcDoor() { super(); }

    public boolean isOpen;

    @Override
    public Boolean preBeOpened(Entity entity, Entity actor) {
        if (!isOpen) {
            return Boolean.TRUE;
        }
        Game.announceVis(actor, null, "The door is already open.", null, null, null);
        return Boolean.FALSE;
    }

    @Override
    public Boolean preBeClosed(Entity entity, Entity actor) {
        if (isOpen) {
            return Boolean.TRUE;
        }
        Game.announceVis(actor, null, "The door is already closed.", null, null, null);
        return Boolean.FALSE;
    }

    @Override
    public void postBeOpened(Entity entity, Entity actor) {
        Game.announceVis(actor, null, "You open the door.", null, actor.getVisibleNameThe() + " opens the door.", "You hear a door being opened.");
        open(entity);
    }

    @Override
    public void postBeClosed(Entity entity, Entity actor) {
        Game.announceVis(actor, null, "You close the door.", null, actor.getVisibleNameThe() + " closes the door.", "You hear a door being closed.");
        close(entity);
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
    public void postBeSteppedOn(Entity entity, Entity e) {
        Game.announceVis(e, null, "You pass through a door.", null, null, null);
    }

    public void open(Entity entity) {
        entity.glyphName = "terrain.door_open";
        entity.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT, Palette.COLOR_BROWN);
        entity.name = "open door";
        EntityGlyph.updateEntity(entity);
        isOpen = true;
    }

    public void close(Entity entity) {
        entity.glyphName = "terrain.door_closed";
        entity.palette = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_TAN, Palette.COLOR_BROWN);
        entity.name = "closed door";
        EntityGlyph.updateEntity(entity);
        isOpen = false;
    }
}
