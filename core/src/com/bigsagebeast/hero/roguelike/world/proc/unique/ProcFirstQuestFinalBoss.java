package com.bigsagebeast.hero.roguelike.world.proc.unique;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.MusicPlayer;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

import java.util.ArrayList;

public class ProcFirstQuestFinalBoss extends Proc {
    public static boolean dead = false;

    @Override
    public void postBeKilled(Entity entity, Entity actor, Entity tool) {
        if (dead) {
            // protect against this being called more than once
            return;
        }
        dead = true;
        MusicPlayer.playLoop();

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("An Enemy Falls", null)
                .withText("As G'Chakk hits the ground, you feel a sense of divine empowerment fill your body and soul. Your mission is accomplished, and you have regained some of your lost power.");

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
}
