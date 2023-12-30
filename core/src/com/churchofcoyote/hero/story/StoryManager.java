package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.roguelike.game.Game;

import java.util.*;

public class StoryManager {
    public int nextCard;
    public HashMap<Integer, StoryCard> cards;
    public StoryDeck deck = new StoryDeck();

    public StoryManager() {
        reset();

        /*
        StoryCardDefinition leaderCardDef = deck.deck.get(StoryCardType.PERSON_TYPE).get(0);
        StoryCard leaderCard = new StoryCard(leaderCardDef);
        insert(leaderCard);
         */

        StoryCard preDefBoss = new StoryCard(deck.find("preconditionBoss"));
        StoryCard preDefDungeon = new StoryCard(deck.find("preconditionDungeon"));
        StoryCard preDefSword = new StoryCard(deck.find("preconditionSword"));
        preDefBoss.hardLinks.put("precondition", "A");
        preDefDungeon.hardLinks.put("precondition.resident", "A");
        preDefSword.hardLinks.put("precondition.wielder", "A");
        insert(preDefBoss);
        insert(preDefDungeon);
        insert(preDefSword);

        List<StoryGap> gaps = findGaps();
        while (!gaps.isEmpty()) {
            //System.out.println("Gaps: " + gaps.size());
            Collections.shuffle(gaps);
            Collections.sort(gaps, Comparator.comparing(g -> -g.getLongestHardLink()));
            for (StoryGap gap : gaps) {
                System.out.println("  " + gap.toString());
            }
            StoryGap gap = gaps.get(0);
            //System.out.println("Fill first");
            StoryCard filler = draw(gap);
            gaps = findGaps();
        }
        for (Integer key : cards.keySet()) {
            cards.get(key).definition.giveName(cards.get(key));
            System.out.println("Card: " + cards.get(key).toString());
        }
        System.out.println();

        StoryDescriber describer = new StoryDescriber(cards.values());
    }

    public void reset() {
        nextCard = 0;
        cards = new HashMap<>();
    }

    public List<StoryGap> findGaps() {
        ArrayList<StoryGap> gaps = new ArrayList<>();
        HashMap<String, StoryGap> terminalHardLinks = new HashMap<>();
        for (StoryCard card : cards.values()) {
            for (String key : card.links.keySet()) {
                if (card.links.get(key).isEmpty()) {
                    List<String> terminalHardLinksInGap = card.getTerminalHardLinksAcrossWalk(key);

                    if (terminalHardLinksInGap.isEmpty() && (card.definition.links.get(key).seekType == StoryLinkSeekType.NO_SEEK || card.definition.links.get(key).seekType == StoryLinkSeekType.OPTIONAL)) {
                        continue;
                    }

                    HashMap<String, String> hardLinksInGap = card.getHardLinksAcrossWalk(key);
                    // assume 0 or 1 links
                    String hardLinkName = terminalHardLinksInGap.isEmpty() ? null : terminalHardLinksInGap.get(0);
                    StoryGap gap;
                    if (hardLinkName == null || !terminalHardLinks.containsKey(hardLinkName)) {
                        gap = new StoryGap();
                        gap.type = card.definition.links.get(key).type;
                        gap.hardLinks = hardLinksInGap;
                    } else {
                        gap = terminalHardLinks.get(hardLinkName);
                    }
                    gap.link.add(card);
                    gap.linkName.add(card.definition.links.get(key).key);
                    gap.requirements.addAll(card.definition.links.get(key).requirements);
                    if (hardLinkName == null || !terminalHardLinks.containsKey(hardLinkName)) {
                        gaps.add(gap);
                    }
                    if (hardLinkName != null && !terminalHardLinks.containsKey(hardLinkName)) {
                        terminalHardLinks.put(hardLinkName, gap);
                        gap.terminalHardLinks.add(hardLinkName);
                    }
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
            HashMap<String, String> hardLinksAfterWalk = linkedCard.getHardLinksAcrossWalk(forwardKey);
            for (String key : hardLinksAfterWalk.keySet()) {
                card.hardLinks.put(key, hardLinksAfterWalk.get(key));
            }
        }


        card.id = nextCard++;
        cards.put(card.id, card);
        return card;
    }
}
