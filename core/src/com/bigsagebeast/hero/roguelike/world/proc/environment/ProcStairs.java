package com.bigsagebeast.hero.roguelike.world.proc.environment;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Point;

public class ProcStairs extends Proc {
    // TODO phase special messages out in place of different procs? maybe we don't need them at all once wilderness is in
    public String upSpecialMessage;
    public String downSpecialMessage;
    public String upToMap;
    public String downToMap;
    public Point upToPos;
    public Point downToPos;
    
    public Boolean stairsUp(Entity entity, Entity actor) {
        if (upSpecialMessage != null) {
            Game.announce(upSpecialMessage);
        }
        if (upToMap == null || upToMap.equals("out")) {
            return false;
        }
        if (upToPos != null) {
            Game.changeLevel(Game.dungeon.getLevel(upToMap), upToPos);
        } else {
            Game.changeLevel(upToMap, Game.getLevel().getKey());
        }
        return true;
    }

    public Boolean stairsDown(Entity entity, Entity actor) {
        if (downSpecialMessage != null) {
            Game.announce(downSpecialMessage);
        }
        if (downToMap == null) {
            return false;
        }
        if (downToPos != null) {
            Game.changeLevel(Game.dungeon.getLevel(downToMap), downToPos);
        } else {
            Game.changeLevel(downToMap, Game.getLevel().getKey());
        }
        return true;
    }
}
