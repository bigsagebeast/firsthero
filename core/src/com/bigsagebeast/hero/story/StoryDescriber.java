package com.bigsagebeast.hero.story;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class StoryDescriber {
    List<StoryCard> cardList;
    public StoryDescriber(Collection<StoryCard> cards) {
        cardList = cards.stream().collect(Collectors.toList());
        Collections.shuffle(cardList);
    }

    // TODO: This stuff is incomplete, we need to be willing to fully describe a card after it's been mentioned
    public List<String> generateStory() {
        List<String> storyLines = new ArrayList<>();
        List<StoryCard> preferred = new ArrayList<>();
        while (!cardList.isEmpty()) {
            // ideally, pick a card we just visited
            preferred.retainAll(cardList);
            StoryCard card;
            if (!preferred.isEmpty()) {
                card = preferred.get(0);
            } else {
                card = cardList.get(0);
            }
            cardList.remove(card);
            boolean alreadyIntroduced = card.descIntroduced;

            if (!card.definition.doDescribe) {
                continue;
            }
            if (card.isFullyDescribed()) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            if (!card.descIntroduced) {
                card.descIntroduced = true;
                String intro = rand(card.definition.describeSelf());
                intro = substitute(intro, card, null);
                sb.append(Util.capitalize(intro));
            } else {
                sb.append(Util.capitalize(card.shortName));
            }

            int numLinks = 0;
            List<String> linkParts = new ArrayList<>();
            preferred.clear();
            for (String link : card.links.keySet().stream().filter(k -> !card.links.get(k).isEmpty()).collect(Collectors.toList())) {
                for (StoryCard target : card.links.get(link)) {
                    preferred.add(target); // next card should ideally be one we just mentioned
                    if (!card.definition.links.get(link).doDescribe || card.linkDescribed.get(link)) {
                        continue;
                    }
                    String linkDesc = rand(card.definition.describeLink(link));
                    linkDesc = substitute(linkDesc, card, target);
                    target.descIntroduced = true;
                    linkParts.add(linkDesc);
                    // TODO this doesn't work with multiple cards for a link
                    target.linkDescribed.put(card.definition.links.get(link).backKey, true);
                }
                card.linkDescribed.put(link, true);
            }
            if (!alreadyIntroduced || linkParts.size() > 0) {
                for (String linkPart : linkParts) {
                    if (numLinks == 0) {
                        if (alreadyIntroduced) {
                            sb.append(" ");
                        } else {
                            sb.append(", " + card.definition.introToStreamingConnector() + " ");
                        }
                    } else if (numLinks == linkParts.size() - 1) {
                        sb.append(", and ");
                    } else {
                        // decided that 'and' was better at each connector
                        sb.append(", and ");
                    }
                    numLinks++;
                    sb.append(linkPart);
                }
                sb.append(".");
                //System.out.println(sb.toString());
                storyLines.add(sb.toString());
            }
        }

        return storyLines;
    }


    public String rand(String[] options) {
        return options[Game.random.nextInt(options.length)];
    }

    public String substitute(String string, StoryCard card1, StoryCard card2) {
        String name1 = card1.shortName;
        if (!card1.descIntroduced) {
            String[] introNames = card1.definition.getNameIntro(true);
            if (introNames != null) {
                name1 = rand(introNames);
            }
        }
        String name2 = null;
        if (card2 != null) {
            name2 = card2.shortName;
            if (!card2.descIntroduced) {
                String[] introNames = card2.definition.getNameIntro(true);
                if (introNames != null) {
                    name2 = rand(introNames);
                }
            }
        }

        if (name1 != null) {
            name1 = name1.replace("%1n", card1.shortName);
            name1 = name1.replace("%1N", Util.capitalize(card1.shortName));
        }
        if (name2 != null) {
            name2 = name2.replace("%1n", card2.shortName);
            name2 = name2.replace("%1N", Util.capitalize(card2.shortName));
        }

        string = string.replace("%1n", name1);
        string = string.replace("%1N", Util.capitalize(name1));
        if (card2 != null) {
            string = string.replace("%2n", name2);
            string = string.replace("%2N", Util.capitalize(name2));
        }
        string = Util.substitute(string, card1.gender, card2 == null ? null : card2.gender);
        return string;
    }
}
