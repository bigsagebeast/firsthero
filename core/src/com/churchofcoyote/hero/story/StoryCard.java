package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.util.Gender;

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
    boolean descIntroduced = false;
    HashMap<String, Boolean> linkDescribed = new HashMap<>();
    public String shortName;
    public Gender gender = Gender.AGENDER;

    public StoryCard(StoryCardDefinition definition) {
        this.definition = definition;
        for (String link : definition.links.keySet()) {
            links.put(link, new ArrayList<>());
            linkDescribed.put(link, false);
        }
        id = Game.random.nextInt(Integer.MAX_VALUE);
    }

    public StoryCard clone() {
        StoryCard newCard = new StoryCard(definition);
        newCard.links = links.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new));
        newCard.linkDescribed = linkDescribed.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new));
        return newCard;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(" + id + ":");
        sb.append((descIntroduced ? "D" : "U") + ":");
        sb.append(definition.title + ":");
        for (String linkKey : links.keySet()) {
            sb.append("[Link " + linkKey + ":");
            sb.append(linkDescribed.get(linkKey) ? "D" : "U");
            sb.append(":");
            for (int i=0; i<links.get(linkKey).size(); i++) {
                sb.append(" " + links.get(linkKey).get(i).id);
            }
            sb.append("]");
        }
        for (String key : hardLinks.keySet()) {
            sb.append(" HL:" + key + ":" + hardLinks.get(key));
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

    public boolean isFullyDescribed() {
        for (Boolean b : linkDescribed.values()) {
            if (!b) {
                return false;
            }
        }
        return descIntroduced;
        // should always have been set TRUE while exploring links, unless this card is somehow standalone?
    }

    public boolean isPlural() {
        return gender == Gender.PLURAL;
    }
}
