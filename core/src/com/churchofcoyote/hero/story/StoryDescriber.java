package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.roguelike.game.Game;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StoryDescriber {
    public StoryDescriber(Collection<StoryCard> cards) {
        List<StoryCard> cardList = cards.stream().collect(Collectors.toList());


        for (StoryCard card : cardList) {
            if (!card.definition.doDescribe) {
                continue;
            }
            if (!card.descIntroduced) {
                String intro = rand(card.definition.describeSelf());
                intro = substitute(intro, card, null);
                System.out.println(capitalize(intro) + ".");
                card.descIntroduced = true;
            }

            for (String link : card.links.keySet().stream().filter(k -> !card.links.get(k).isEmpty()).collect(Collectors.toList())) {
                for (StoryCard target : card.links.get(link)) {
                    if (!card.definition.links.get(link).doDescribe) {
                        continue;
                    }
                    String linkDesc = rand(card.definition.describeLink(link));
                    linkDesc = substitute(linkDesc, card, target);
                    System.out.println(capitalize(linkDesc) + ".");
                    // TODO this doesn't work with multiple cards for a link
                    target.linkDescribed.put(card.definition.links.get(link).backKey, true);
                }
                card.linkDescribed.put(link, true);
            }
        }


    }


    public String rand(String[] options) {
        return options[Game.random.nextInt(options.length)];
    }

    public String capitalize(String string) {
        return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1);
    }

    public String substitute(String string, StoryCard card1, StoryCard card2) {
        string = string.replace("%1n", card1.shortName);
        string = string.replace("%1a", card1.gender.absolute);
        string = string.replace("%1s", card1.gender.subjective);
        string = string.replace("%1o", card1.gender.objective);
        string = string.replace("%1p", card1.gender.possessive);
        string = string.replace("%1r", card1.gender.reflexive);
        string = string.replace("%1N", capitalize(card1.shortName));
        string = string.replace("%1A", capitalize(card1.gender.absolute));
        string = string.replace("%1S", capitalize(card1.gender.subjective));
        string = string.replace("%1O", capitalize(card1.gender.objective));
        string = string.replace("%1P", capitalize(card1.gender.possessive));
        string = string.replace("%1R", capitalize(card1.gender.reflexive));
        if (card2 != null) {
            string = string.replace("%2n", card2.shortName);
            string = string.replace("%2a", card2.gender.absolute);
            string = string.replace("%2s", card2.gender.subjective);
            string = string.replace("%2o", card2.gender.objective);
            string = string.replace("%2p", card2.gender.possessive);
            string = string.replace("%2r", card2.gender.reflexive);
            string = string.replace("%2N", capitalize(card2.shortName));
            string = string.replace("%2A", capitalize(card2.gender.absolute));
            string = string.replace("%2S", capitalize(card2.gender.subjective));
            string = string.replace("%2O", capitalize(card2.gender.objective));
            string = string.replace("%2P", capitalize(card2.gender.possessive));
            string = string.replace("%2R", capitalize(card2.gender.reflexive));
        }
        return string;
    }
}
