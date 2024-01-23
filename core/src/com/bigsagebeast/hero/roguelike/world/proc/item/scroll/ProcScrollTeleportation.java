package com.bigsagebeast.hero.roguelike.world.proc.item.scroll;

import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.GameSpecials;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;
import com.bigsagebeast.hero.util.Point;

public class ProcScrollTeleportation extends ImmutableProc {

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        Point before = actor.pos;
        GameSpecials.teleportRandomly(actor);
        Point after = actor.pos;
        if (before.equals(after)) {
            Game.announce("Nothing happens.");
        } else {
            Game.announce("Suddenly, you stand somewhere else.");
        }
        entity.identifyItemType();
        if (entity.getBeatitude() == Beatitude.CURSED) {
            Game.announceBad("Something was wrong with that scroll!");
            ProcEffectConfusion proc = new ProcEffectConfusion();
            proc.turnsRemaining = 5;
            actor.addProc(proc);
        }
    }
}
