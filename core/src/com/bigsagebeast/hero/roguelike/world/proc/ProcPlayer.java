package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.Player;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Terrain;

import java.util.List;

public class ProcPlayer extends ProcMover {

    public ProcPlayer() { super(); }

    @Override
    public void turnPassed(Entity entity) {
        Player player = Game.getPlayer();

        float hungerRate = 1.5f;
        player.changeSatiation(-hungerRate);
    }

    @Override
    public void onAction(Entity entity) {
        Player player = Game.getPlayer();
        Entity playerEntity = Game.getPlayerEntity();

        Terrain water = Terrain.get("water");
        if (!player.areElementsFull()) {
            for (Point p : Game.getLevel().surroundingAndCurrentTiles(entity.pos)) {
                // draw from entities
                for (Entity e : Game.getLevel().getEntitiesOnTile(p)) {
                    for (Proc proc : e.procs) {
                        Element element = proc.providesElement(e);
                        if (element != null) {
                            int missing = player.elementMissing(element);
                            if (missing > 0) {
                                int retrieved = proc.drawElement(e, playerEntity, missing);
                                if (retrieved > 0) {
                                    Game.announce("You draw " + retrieved + " " + element.name + " charge" + (retrieved == 1 ? "" : "s") + " from " + e.getVisibleNameDefinite() + ".");
                                    Game.getPlayer().changeCharges(element, retrieved);
                                }
                            }
                        }
                    }
                }

                // draw from terrain, TODO more modular so we can do lava
                if (Game.getLevel().cell(p).terrain == water) {
                    int missing = Game.getPlayer().elementMissing(Element.WATER);
                    if (missing > 0) {
                        Game.announce("You draw " + missing + " water charge" + (missing == 1 ? "" : "s") + " from the water.");
                        Game.getPlayer().changeCharges(Element.WATER, missing);
                    }
                }
            }
        }
    }

    @Override
    public Boolean preDoPickup(Entity entity, Entity target) { return Boolean.TRUE; }
    @Override
    public void postDoPickup(Entity entity, Entity target) {
        //Game.announce("You pick up the " + target.name + ".");
    }

    @Override
    public Boolean wantsMoverLos() { return Boolean.TRUE; }

    @Override
    public void handleMoverLos(Entity entity, List<Entity> movers) {

    }

    @Override
    public void postDoKill(Entity entity, Entity target, Entity tool) {
        entity.experience += target.experienceAwarded;
        if (target.peaceful) {
            Game.announce("If only talking was implemented.");
        }
        if (entity.experience >= entity.experienceToNext) {
            levelUp(entity);
        }
    }

    private void levelUp(Entity entity) {
        entity.level++;
        entity.experience -= entity.experienceToNext;
        entity.experienceToNext *= 2;
        //entity.hitPoints += 15;
        //entity.maxHitPoints += 15;
        entity.recalculateSecondaryStats();
        //entity.healingDelay = 300 / entity.maxHitPoints;

        Game.announce("You have reached level " + entity.level + "!");
    }

}
