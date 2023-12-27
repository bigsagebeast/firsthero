package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.roguelike.game.Game;

import java.util.*;

public class StoryManager {
    public int nextCard;
    public HashMap<Integer, StoryCard> cards;
    public StoryDeck deck = new StoryDeck();

    public StoryManager() {
        reset();

        StoryCardDefinition leaderCardDef = deck.deck.get(StoryCardType.PERSON_TYPE).get(0);
        StoryCard leaderCard = new StoryCard(leaderCardDef);
        insert(leaderCard);

        List<StoryGap> gaps = findGaps();
        while (!gaps.isEmpty()) {
            System.out.println("Gaps: " + gaps.size());
            Collections.shuffle(gaps);
            StoryGap gap = gaps.get(0);
            System.out.println("Fill: " + gap.toString());
            /*
            Map<StoryCardDefinition, Float> fillers = deck.search(gap);
            for (StoryCardDefinition key : fillers.keySet()) {
                System.out.println("Filler: " + key + ": " + fillers.get(key));
            }
            */
            StoryCard filler = draw(gap);
            gaps = findGaps();
        }
        System.out.println();
        for (Integer key : cards.keySet()) {
            System.out.println("Card: " + cards.get(key).toString());
        }
    }

    public void reset() {
        nextCard = 0;
        cards = new HashMap<>();
    }

    public List<StoryGap> findGaps() {
        ArrayList<StoryGap> gaps = new ArrayList<>();
        for (StoryCard card : cards.values()) {
            for (String key : card.links.keySet()) {
                if (card.links.get(key).isEmpty()) {
                    StoryGap gap = new StoryGap();
                    gap.type = card.definition.links.get(key).type;
                    gap.link.add(card);
                    gap.linkName.add(card.definition.links.get(key).key);
                    gap.requirements.addAll(card.definition.links.get(key).requirements);
                    gaps.add(gap);
                }
            }
        }
        return gaps;
    }

    public void insert(StoryCard card) {
        card.id = nextCard++;
        cards.put(card.id, card);
    }

    public StoryCard draw(StoryGap gap) {
        Map<StoryCardDefinition, Float> results = deck.search(gap);
        List<StoryCardDefinition> choices = new ArrayList<>();
        for (StoryCardDefinition result : results.keySet()) {
            int picks = (int)(results.get(result) * 100);
            for (int i=0; i<picks; i++) {
                choices.add(result);
            }
        }
        if (choices.isEmpty()) {
            throw new RuntimeException("Couldn't find appropriate card for gap " + gap.toString());
        }
        StoryCard card = new StoryCard(choices.get(Game.random.nextInt(choices.size())));

        for (int i=0; i<gap.link.size(); i++) {
            StoryCard linkedCard = gap.link.get(i);
            String forwardKey = gap.linkName.get(i);
            StoryCardLink linkToThis = linkedCard.definition.links.get(forwardKey);
            linkedCard.links.get(forwardKey).add(card);
            String reflexive = linkToThis.backKey;
            card.links.get(reflexive).add(linkedCard);
            //String doubleReflexive = linkedCard.reflexiveLinkName.get(backLink);
            //String doubleReflexive = linkToThis.backKey;
            //linkedCard.links.get(backLink).add(card);
            //card.links.get(doubleReflexive).add(linkedCard);
        }


        card.id = nextCard++;
        cards.put(card.id, card);
        return card;
    }
}
