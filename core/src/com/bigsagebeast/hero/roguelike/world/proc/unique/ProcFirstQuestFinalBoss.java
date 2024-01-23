package com.bigsagebeast.hero.roguelike.world.proc.unique;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.MusicPlayer;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProcFirstQuestFinalBoss extends Proc {
    public static boolean dead = false;

    // TODO: This should go in a ProcSummonAtHealth, but proc defs need to be refactored as arrays first.
    public String message;
    public String messageFailed;
    public String listen;
    public String listenFailed;
    public boolean aroundPlayer;
    public boolean reached;
    public float threshold = 0.5f;
    public int quantity = 1;
    public String minionKey;

    public boolean chatTriggered;

    @Override
    public void actPlayerLos(Entity entity) {
        if (!chatTriggered) {
            chatTriggered = true;

            ChatBox chatBox = new ChatBox()
                    .withMargins(60, 60)
                    .withTitle("The voice of Nemesis", null)
                    .withText("The goblin leader speaks with a voice that is not its own.\n\n\"Ah, sibling, you've made it to me! Wherever you go and whatever you do, it is my sacred duty to oppose you. You will not draw essence from this world without a fight. Now, strike me down, if you can!\"");

            ArrayList<ChatLink> links = new ArrayList<>();
            ChatLink linkOk = new ChatLink();
            linkOk.text = "OK";
            linkOk.terminal = true;
            links.add(linkOk);

            GameLoop.chatModule.openArbitrary(chatBox, links);
        }
    }

    @Override
    public void postBeKilled(Entity entity, Entity actor, Entity tool) {
        if (dead) {
            // protect against this being called more than once
            return;
        }
        dead = true;
        Game.halted = true;
        MusicPlayer.playLoop();

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("An Enemy Falls", null)
                .withText("As G'Chakk hits the ground, you feel a sense of divine empowerment fill your body and soul. Your mission is accomplished, and you have regained some of your lost essence.");

        ArrayList<ChatLink> links = new ArrayList<>();
        ChatLink linkOk = new ChatLink();
        linkOk.text = "OK";
        linkOk.runnable = this::handleVictory;
        links.add(linkOk);

        GameLoop.chatModule.openArbitrary(chatBox, links);
    }

    public void handleVictory() {
        Game.startAurex();
    }

    @Override
    public void turnPassed(Entity entity) {
        if (reached || entity.summoned) {
            return;
        }
        if (entity.maxHitPoints * threshold < entity.hitPoints) {
            return;
        }
        reached = true;

        boolean summonedAny = false;
        // TODO tags instead?
        for (int i=0; i<quantity; i++) {
            Entity minion = Bestiary.create(minionKey);
            minion.summoned = true;
            Collection<Point> spawnPoints;
            if (aroundPlayer) {
                // TODO: This should be around the target instead of the player, probably
                spawnPoints = Game.getLevel().surroundingTiles(Game.getPlayerEntity().pos);
            } else {
                spawnPoints = Game.getLevel().surroundingTiles(entity.pos);
            }
            spawnPoints.removeIf(p -> !Game.getLevel().cell(p).terrain.isSpawnable());
            spawnPoints.removeIf(p -> !Game.getLevel().getMoversOnTile(p).isEmpty());
            if (spawnPoints.isEmpty()) {
                break;
            }
            summonedAny = true;
            List<Point> pointList = new ArrayList(spawnPoints);
            Point spawnPoint = pointList.get(Game.random.nextInt(pointList.size()));
            Game.getLevel().addEntityWithStacking(minion, spawnPoint);
            if (message == null) {
                Game.announceSeen(minion,minion.getVisibleNameIndefiniteOrSpecific() + " appears!");
            }
        }
        if (summonedAny && message != null) {
            Game.announceVis(entity, null, null, null, message, listen);
        }
        if (!summonedAny && messageFailed != null) {
            Game.announceVis(entity, null, null, null, messageFailed, listenFailed);
        }
        Game.turn();
    }
}
