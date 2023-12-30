package com.churchofcoyote.hero.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryDeck {
    public Map<StoryCardType, List<StoryCardDefinition>> deck = new HashMap<>();

    public StoryDeck() {
        for (StoryCardType type : StoryCardType.values()) {
            deck.put(type, new ArrayList<>());
        }

        StoryCardDefinition preconditionBoss = new StoryCardDefinition();
        preconditionBoss.type = StoryCardType.PRECONDITION;
        preconditionBoss.title = "preconditionBoss";
        StoryCardLink preconditionBossToBoss = new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.REQUIRED, "precondition", "precondition");
        preconditionBossToBoss.requirements.add(new StoryLinkRequirementTagRequired("boss"));
        preconditionBoss.links.put("precondition", preconditionBossToBoss);
        addCard(preconditionBoss);

        StoryCardDefinition preconditionDungeon = new StoryCardDefinition();
        preconditionDungeon.type = StoryCardType.PRECONDITION;
        preconditionDungeon.title = "preconditionDungeon";
        StoryCardLink preconditionDungeonToDungeon = new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.REQUIRED, "precondition", "precondition");
        preconditionDungeonToDungeon.requirements.add(new StoryLinkRequirementTagRequired("dungeon"));
        preconditionDungeon.links.put("precondition", preconditionDungeonToDungeon);
        addCard(preconditionDungeon);

        StoryCardDefinition goblinLeader = new StoryCardDefinition();
        goblinLeader.type = StoryCardType.PERSON_TYPE;
        goblinLeader.title = "goblinLeader";
        goblinLeader.tags.add("boss");
        StoryCardLink leaderToHq = new StoryCardLink();
        StoryLinkRequirement leaderToHqTagReq = new StoryLinkRequirementTagRequired("goblin");
        leaderToHq.type = StoryCardType.PLACE_TYPE;
        leaderToHq.requirements.add(leaderToHqTagReq);
        leaderToHq.key = "home";
        leaderToHq.backKey = "resident";
        goblinLeader.links.put("home", leaderToHq);
        StoryCardLink leaderToPreconditionBoss = new StoryCardLink();
        leaderToPreconditionBoss.type = StoryCardType.PRECONDITION;
        leaderToPreconditionBoss.seekType = StoryLinkSeekType.NO_SEEK;
        leaderToPreconditionBoss.key = "precondition";
        leaderToPreconditionBoss.backKey = "precondition";
        goblinLeader.links.put("precondition", leaderToPreconditionBoss);
        addCard(goblinLeader);

        StoryCardDefinition goblinCaves = new StoryCardDefinition();
        goblinCaves.type = StoryCardType.PLACE_TYPE;
        goblinCaves.title = "goblinCaves";
        goblinCaves.tags.add("goblin");
        goblinCaves.tags.add("dungeon");
        goblinCaves.addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.OPTIONAL, "resident", "home"));
        goblinCaves.addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition"));
        addCard(goblinCaves);

        StoryCardDefinition goblinPits = new StoryCardDefinition();
        goblinPits.type = StoryCardType.PLACE_TYPE;
        goblinPits.title = "goblinPits";
        goblinPits.tags.add("goblin");
        goblinPits.tags.add("dungeon");
        goblinPits.addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.OPTIONAL, "resident", "home"));
        goblinPits.addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition"));
        addCard(goblinPits);
    }

    public Map<StoryCardDefinition, Float> search(StoryGap gap) {
        HashMap<StoryCardDefinition, Float> matches = new HashMap<>();
        for (StoryCardDefinition def : deck.get(gap.type)) {
            float weight = 1.0f;
            for (StoryLinkRequirement req : gap.requirements) {
                weight *= req.weight(def);
            }
            if (weight > 0f) {
                matches.put(def, weight);
            }
        }
        return matches;
    }

    public void addCard(StoryCardDefinition def) {
        deck.get(def.type).add(def);
    }

    public StoryCardDefinition find(String title) {
        for (StoryCardType type : deck.keySet()) {
            for (StoryCardDefinition def : deck.get(type)) {
                if (def.title.equals(title)) {
                    return def;
                }
            }
        }
        return null;
    }
}
