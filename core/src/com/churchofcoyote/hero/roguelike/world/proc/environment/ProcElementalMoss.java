package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcElementalMoss extends Proc {
    public Element element = Element.PLANT;
    private static final int maxCharges = 2;
    private static final int maxRechargeTimer = 50;
    public int charges = maxCharges;
    public int rechargeTimer = 50;
    private PaletteEntry paletteGrown = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN);
    private PaletteEntry paletteDead = new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_LIGHTGREEN);

    @Override
    public void turnPassed(Entity entity) {
        if (charges >= maxCharges) {
            return;
        } else if (charges < maxCharges) {
            rechargeTimer--;
        }
        if (rechargeTimer <= 0) {
            updatePalette(entity);
            rechargeTimer = maxRechargeTimer;
            charges++;
        }
    }

    @Override
    public Element providesElement(Entity entity) {
        return (charges > 0) ? element : null;
    }

    @Override
    public int drawElement(Entity entity, Entity actor, int requested) {
        if (requested > charges) {
            requested = charges;
        }
        charges -= requested;
        updatePalette(entity);
        return requested;
    }

    // TODO does this need to be executed on load?
    public void updatePalette(Entity entity) {
        if (charges == 0) {
            entity.palette = paletteDead;
        } else {
            entity.palette = paletteGrown;
        }
    }
}
