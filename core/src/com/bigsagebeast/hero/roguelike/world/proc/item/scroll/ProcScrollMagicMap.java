package com.bigsagebeast.hero.roguelike.world.proc.item.scroll;

import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.game.GameEntities;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedTelepathy;

public class ProcScrollMagicMap extends ImmutableProc {

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        if (entity.getBeatitude() == Beatitude.CURSED) {
            // explore half of tiles
            Game.getLevel().getCellStream().forEach(c -> c.explored = Game.random.nextInt(3) == 0 || c.explored);
        } else {
            Game.getLevel().getCellStream().forEach(c -> c.explored = true);
        }
        Game.announce("You feel more knowledgeable!");
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            ProcEffectTimedTelepathy proc = new ProcEffectTimedTelepathy();
            proc.turnsRemaining = 3;
            actor.addProc(proc);
        }
    }



}
