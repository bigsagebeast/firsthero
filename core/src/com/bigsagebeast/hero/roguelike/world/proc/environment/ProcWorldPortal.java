package com.bigsagebeast.hero.roguelike.world.proc.environment;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.roguelike.game.Profile;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.story.StoryDescriber;
import com.bigsagebeast.hero.story.StoryManager;

import java.util.ArrayList;
import java.util.List;

public class ProcWorldPortal extends Proc {

    StoryManager storyManager;

    @Override
    public void postBeSteppedOn(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.announce("Press 'P' to enter the portal.");
        }
    }

    @Override
    public Boolean canPrayAt(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void prayAt(Entity entity, Entity actor) {
        Profile.setString("mode", "enterWorld");

        storyManager = new StoryManager();
        StoryDescriber describer = new StoryDescriber(storyManager.cards.values());
        List<String> storyLines = describer.generateStory();

        StringBuilder story = new StringBuilder();
        for (String line : storyLines) {
            story.append("\n" + line);
        }

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("This is a story about...", null)
                .withText(story.toString().trim());

        ArrayList<ChatLink> links = new ArrayList<>();
        ChatLink linkOk = new ChatLink();
        linkOk.text = "Enter world";
        linkOk.codeClass = "com.bigsagebeast.hero.roguelike.world.proc.environment.ProcWorldPortal";
        linkOk.codeMethod = "acceptWorld";
        ChatLink linkRefuse = new ChatLink();
        linkRefuse.text = "Refuse";
        links.add(linkOk);
        links.add(linkRefuse);

        GameLoop.CHAT_MODULE.openArbitrary(chatBox, links);
    }

    public static void acceptWorld() {
        GameLoop.roguelikeModule.end();
        GameLoop.flowModule.start();
    }
}
