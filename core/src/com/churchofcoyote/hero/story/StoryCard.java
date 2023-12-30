package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.roguelike.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoryCard {
    StoryCardDefinition definition;
    HashMap<String, ArrayList<StoryCard>> links = new HashMap<>();
    //HashMap<String, String> reflexiveLinkName = new HashMap<>();
    HashMap<String, String> hardLinks = new HashMap<>();
    int id;

    public StoryCard(StoryCardDefinition definition) {
        this.definition = definition;
        for (String link : definition.links.keySet()) {
            links.put(link, new ArrayList<>());
        }
        id = Game.random.nextInt(Integer.MAX_VALUE);
    }

    public StoryCard clone() {
        StoryCard newCard = new StoryCard(definition);
        newCard.links = links.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new));
        return newCard;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(" + id + ":");
        sb.append(definition.title + ":");
        for (String linkKey : links.keySet()) {
            sb.append("[Link " + linkKey + ":");
            for (int i=0; i<links.get(linkKey).size(); i++) {
                sb.append(" " + links.get(linkKey).get(i).id);
            }
            sb.append("]");
        }
        sb.append(")");
        return sb.toString();
    }

    public List<String> getTerminalHardLinksAcrossWalk(String walk) {
        ArrayList<String> terminals = new ArrayList<>();
        for (String key : hardLinks.keySet()) {
            if (key.equals(walk)) {
                terminals.add(hardLinks.get(key));
            }
        }
        if (terminals.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : terminals) {
                sb.append(" " + s);
            }
            throw new RuntimeException("Too many terminals on card " + definition.title + ": " + sb.toString());
        }
        return terminals;
    }

    public HashMap<String, String> getHardLinksAcrossWalk(String walk) {
        String prefix = walk + ".";
        HashMap<String, String> walkLinks = new HashMap<>();
        for (String key : hardLinks.keySet()) {
            if (key.startsWith(prefix)) {
                walkLinks.put(key.substring(prefix.length()), hardLinks.get(key));
            }
        }
        return walkLinks;
    }
}
