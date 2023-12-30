package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.story.defs.SCDPerson;
import com.churchofcoyote.hero.story.defs.SCDPlace;

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
        preconditionBoss.doDescribe = false;
        addCard(preconditionBoss);

        StoryCardDefinition preconditionDungeon = new StoryCardDefinition();
        preconditionDungeon.type = StoryCardType.PRECONDITION;
        preconditionDungeon.title = "preconditionDungeon";
        StoryCardLink preconditionDungeonToDungeon = new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.REQUIRED, "precondition", "precondition");
        preconditionDungeonToDungeon.requirements.add(new StoryLinkRequirementTagRequired("dungeon"));
        preconditionDungeon.links.put("precondition", preconditionDungeonToDungeon);
        preconditionDungeon.doDescribe = false;
        addCard(preconditionDungeon);

        StoryCardDefinition goblinLeader = new SCDPerson();
        goblinLeader.title = "goblinLeader";
        goblinLeader.tags.add("boss");
        goblinLeader.links.get("home").requirements.add(new StoryLinkRequirementTagRequired("goblin"));
        goblinLeader.addDescSelf("there was a wily goblin boss, %1n");
        addCard(goblinLeader);

        StoryCardDefinition goblinCaves = new SCDPlace();
        goblinCaves.title = "goblinCaves";
        goblinCaves.tags.add("goblin");
        goblinCaves.tags.add("dungeon");
        addCard(goblinCaves);

        StoryCardDefinition goblinPits = new StoryCardDefinition();
        goblinPits.type = StoryCardType.PLACE_TYPE;
        goblinPits.title = "goblinPits";
        goblinPits.tags.add("goblin");
        goblinPits.tags.add("dungeon");
        goblinPits.addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.OPTIONAL, "resident", "home"));
        goblinPits.addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition"));
        goblinPits.links.get("precondition").doDescribe = false;
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
