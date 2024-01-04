package com.bigsagebeast.hero.roguelike.world.proc.unique;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.Profile;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcIntroAltar extends Proc {
    public boolean seen = false;

    @Override
    public Boolean canPrayAt(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void prayAt(Entity entity, Entity actor) {
        GameLoop.popupModule.createPopup("The farmboy prays to the God of Heroes.", 4f, null, 1f);
        GameLoop.popupModule.createPopup("A blinding light begins to grow.", 4f, null, 1f);
        GameLoop.popupModule.createPopup("Far away, something wakes...", 4f, null, 1f, this::postPray);
    }

    public void postPray() {
        Profile.setString("mode", "newGameCutscene2");
        GameLoop.roguelikeModule.end();
        GameLoop.flowModule.start();
    }

    @Override
    public void postBeSteppedOn(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.announce("Press 'p' to pray.");
        }
    }

    @Override
    public void actPlayerLos(Entity entity) {
        if (!seen) {
            GameLoop.popupModule.createPopup("Pray at the mysterious altar", 4f, entity, 1f);
        }
        seen = true;
    }

    @Override
    public Float getJitter(Entity entity) { return 2f; }

}