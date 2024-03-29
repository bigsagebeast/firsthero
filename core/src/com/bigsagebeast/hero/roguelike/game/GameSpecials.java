package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.dialogue.TextEntryBox;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameSpecials {
    public static boolean teleportRandomly(Entity entity) {
        Point destination = null;
        for (int i=0; i<5; i++) {
            destination = Game.getLevel().findOpenTile();
            if (destination != null && destination.distance(entity.pos) > 10) break;
        }
        if (destination == null || destination.equals(entity.pos)) {
            GameLoop.warn("Couldn't find a teleport destination!");
            return false;
        } else {
            entity.pos = destination;
            return true;
        }
    }

    public static boolean blink(Entity entity, int minRange, int maxRange) {
        Point destination = Game.getLevel().findOpenTileWithinRange(entity.pos, minRange, maxRange);
        if (destination == null) {
            GameLoop.warn("Couldn't find a blink destination!");
            return false;
        } else {
            entity.pos = destination;
            return true;
        }
    }

    public static void wish() {
        TextEntryBox box = new TextEntryBox()
                .withTitle("What do you wish for?")
                .withMargins(60, 60)
                .withMaxLength(40)
                .autoHeight();
        GameLoop.textEntryModule.openTextEntryBox(box, GameSpecials::handleWish);
    }

    public static void handleWish(String text) {
        Entity entity = null;
        if (Itempedia.map.containsKey(text)) {
            entity = Itempedia.create(text);
        } else {
            for (String key : Itempedia.map.keySet()) {
                ItemType t = Itempedia.get(key);
                if ((t.name != null && t.name.equalsIgnoreCase(text)) ||
                        (t.unidentifiedName != null && t.unidentifiedName.equalsIgnoreCase(text))) {
                    entity = Itempedia.create(key);
                }
            }
        }
        if (entity == null) {
            Game.announce("I don't know that item.");
            return;
        }
        Game.getPlayerEntity().receiveItem(entity);
        Game.announce("You receive " + entity.getVisibleNameIndefiniteOrSpecific() + ".");
    }

    public static void wishSummon() {
        TextEntryBox box = new TextEntryBox()
                .withTitle("What do you wish to summon?")
                .withMargins(60, 60)
                .withMaxLength(40)
                .autoHeight();
        GameLoop.textEntryModule.openTextEntryBox(box, GameSpecials::handleWishSummon);
    }

    public static void handleWishSummon(String text) {
        Entity entity = null;
        List<Point> spawnPoints = Game.getLevel().surroundingTiles(Game.getPlayerEntity().pos)
                .stream().filter(p -> Game.getLevel().isSpawnable(p)).collect(Collectors.toList());
        if (spawnPoints.isEmpty()) {
            Game.announce("Nowhere to summon!");
            return;
        }
        Collections.shuffle(spawnPoints);
        Point spawnPoint = spawnPoints.get(0);

        if (Bestiary.map.containsKey(text)) {
            entity = Bestiary.create(text);
        } else {
            for (String key : Bestiary.map.keySet()) {
                Phenotype p = Bestiary.get(key);
                if (p.name != null && p.name.equalsIgnoreCase(text)) {
                    entity = Bestiary.create(key);
                }
            }
        }
        if (entity == null) {
            Game.announce("I don't know that entity.");
            return;
        }
        Game.getLevel().addEntityWithStacking(entity, spawnPoint);
        Game.announce("You summon " + entity.getVisibleNameIndefiniteOrSpecific() + ".");
    }

    public static void announceStatChange(Stat stat, int delta) {
        if (stat == null) {
            GameLoop.error("Changed unknown stat");
            return;
        }
        switch (stat) {
            case STRENGTH:
                if (delta > 0) {
                    Game.announceGood("You feel stronger.");
                } else if (delta < 0) {
                    Game.announceBad("You feel weaker.");
                }
                break;
            case DEXTERITY:
                if (delta > 0) {
                    Game.announceGood("You feel more dexterous.");
                } else if (delta < 0) {
                    Game.announceBad("You feel more clumsy.");
                }
                break;
        }
    }
}
