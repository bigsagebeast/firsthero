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

        StoryCardDefinition goblinLeader = new StoryCardDefinition();
        goblinLeader.type = StoryCardType.PERSON_TYPE;
        goblinLeader.title = "goblinLeader";
        StoryCardLink leaderToHq = new StoryCardLink();
        StoryLinkRequirement leaderToHqTagReq = new StoryLinkRequirementTagRequired("goblin");
        leaderToHq.type = StoryCardType.PLACE_TYPE;
        leaderToHq.requirements.add(leaderToHqTagReq);
        leaderToHq.key = "home";
        leaderToHq.backKey = "resident";
        goblinLeader.links.put("home", leaderToHq);
        addCard(goblinLeader);

        StoryCardDefinition goblinCaves = new StoryCardDefinition();
        goblinCaves.type = StoryCardType.PLACE_TYPE;
        goblinCaves.title = "goblinCaves";
        goblinCaves.tags.add("goblin");
        StoryCardLink cavesToResident = new StoryCardLink();
        cavesToResident.type = StoryCardType.PERSON_TYPE;
        cavesToResident.key = "resident";
        cavesToResident.backKey = "home";
        goblinCaves.links.put("resident", cavesToResident);
        addCard(goblinCaves);

        StoryCardDefinition goblinPits = new StoryCardDefinition();
        goblinPits.type = StoryCardType.PLACE_TYPE;
        goblinPits.title = "goblinPits";
        goblinPits.tags.add("goblin");
        StoryCardLink pitsToResident = new StoryCardLink();
        pitsToResident.type = StoryCardType.PERSON_TYPE;
        pitsToResident.key = "resident";
        pitsToResident.backKey = "home";
        goblinPits.links.put("resident", pitsToResident);
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
}
