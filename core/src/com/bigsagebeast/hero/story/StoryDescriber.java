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

    public List<String> generateStory() {
        List<String> storyLines = new ArrayList<>();
        for (StoryCard card : cardList) {
            if (!card.definition.doDescribe) {
                continue;
            }
            if (card.isFullyDescribed()) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            if (!card.descIntroduced) {
                String intro = rand(card.definition.describeSelf());
                intro = substitute(intro, card, null);
                sb.append(Util.capitalize(intro));
            } else {
                sb.append(Util.capitalize(card.shortName));
            }

            int numLinks = 0;
            List<String> linkParts = new ArrayList<>();
            for (String link : card.links.keySet().stream().filter(k -> !card.links.get(k).isEmpty()).collect(Collectors.toList())) {
                for (StoryCard target : card.links.get(link)) {
                    if (!card.definition.links.get(link).doDescribe || card.linkDescribed.get(link)) {
                        continue;
                    }
                    target.descIntroduced = true;
                    String linkDesc = rand(card.definition.describeLink(link));
                    linkDesc = substitute(linkDesc, card, target);
                    //sb.append(linkDesc);
                    linkParts.add(linkDesc);
                    // TODO this doesn't work with multiple cards for a link
                    target.linkDescribed.put(card.definition.links.get(link).backKey, true);
                }
                card.linkDescribed.put(link, true);
            }
            if (!card.descIntroduced || linkParts.size() > 0) {
                for (String linkPart : linkParts) {
                    if (numLinks == 0) {
                        if (card.descIntroduced) {
                            sb.append(" ");
                        } else {
                            sb.append(", " + card.definition.introToStreamingConnector() + " ");
                        }
                    } else if (numLinks == linkParts.size() - 1) {
                        sb.append(", and ");
                    } else {
                        sb.append(", ");
                    }
                    numLinks++;
                    sb.append(linkPart);
                }
                sb.append(".");
                System.out.println(sb.toString());
                storyLines.add(sb.toString());
            }
            card.descIntroduced = true;
        }

        return storyLines;
    }


    public String rand(String[] options) {
        return options[Game.random.nextInt(options.length)];
    }

    public String substitute(String string, StoryCard card1, StoryCard card2) {
        string = string.replace("%1n", card1.shortName);
        string = string.replace("%1N", Util.capitalize(card1.shortName));
        if (card2 != null) {
            string = string.replace("%2n", card2.shortName);
            string = string.replace("%2N", Util.capitalize(card2.shortName));
        }
        string = Util.substitute(string, card1.gender, card2 == null ? null : card2.gender);
        return string;
    }
}
