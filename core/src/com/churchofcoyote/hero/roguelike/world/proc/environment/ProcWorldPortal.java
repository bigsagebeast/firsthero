package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.roguelike.game.Profile;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.enums.Alignment;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcWorldPortal extends Proc {

    @Override
    public Boolean canPrayAt(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void prayAt(Entity entity, Entity actor) {
        Profile.setString("mode", "enterWorld");
        GameLoop.roguelikeModule.end();
        GameLoop.flowModule.start();
    }
}
