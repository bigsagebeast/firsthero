package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.chat.ChatLink;
import com.churchofcoyote.hero.dialogue.ChatBox;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Profile;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.story.StoryDescriber;
import com.churchofcoyote.hero.story.StoryManager;

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
            story.append(" " + line);
        }

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("This is a story about...", null)
                .withText(story.toString().trim());

        ArrayList<ChatLink> links = new ArrayList<>();
        ChatLink linkOk = new ChatLink();
        linkOk.text = "Enter world";
        linkOk.codeClass = "com.churchofcoyote.hero.roguelike.world.proc.environment.ProcWorldPortal";
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
