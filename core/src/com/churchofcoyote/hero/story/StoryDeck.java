package com.churchofcoyote.hero.story;

import com.churchofcoyote.hero.story.defs.SCDPerson;
import com.churchofcoyote.hero.story.defs.SCDPlace;
import com.churchofcoyote.hero.story.defs.SCDWeapon;

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

        StoryCardDefinition preconditionSword = new StoryCardDefinition();
        preconditionSword.type = StoryCardType.PRECONDITION;
        preconditionSword.title = "preconditionSword";
        preconditionSword.addLink(new StoryCardLink(StoryCardType.THING_TYPE, StoryLinkSeekType.REQUIRED, "precondition", "precondition"));
        preconditionSword.links.get("precondition").requirements.add(new StoryLinkRequirementTagRequired("weapon"));
        preconditionSword.doDescribe = false;
        addCard(preconditionSword);

        StoryCardDefinition goblinLeader = new SCDPerson();
        goblinLeader.title = "goblinLeader";
        goblinLeader.tags.add("boss");
        goblinLeader.links.get("home").requirements.add(new StoryLinkRequirementTagRequired("goblin"));
        goblinLeader.addDescSelf("there was a wily goblin boss, %1n");
        addCard(goblinLeader);

        StoryCardDefinition nightbringer = new SCDWeapon();
        nightbringer.title = "nightbringer";
        nightbringer.tags.add("weapon");
        addCard(nightbringer);

        StoryCardDefinition goblinCaves = new SCDPlace();
        goblinCaves.title = "goblinCaves";
        goblinCaves.tags.add("goblin");
        goblinCaves.tags.add("dungeon");
        goblinCaves.links.get("location").seekType = StoryLinkSeekType.REQUIRED;
        goblinCaves.links.get("location").requirements.add(new StoryLinkRequirementTagRequired("overworld"));
        goblinCaves.forceName = "the Goblin Caves";
        addCard(goblinCaves);

        StoryCardDefinition goblinPits = new SCDPlace();
        goblinPits.title = "goblinPits";
        goblinPits.tags.add("goblin");
        goblinPits.tags.add("dungeon");
        goblinPits.links.get("location").seekType = StoryLinkSeekType.REQUIRED;
        goblinPits.links.get("location").requirements.add(new StoryLinkRequirementTagRequired("overworld"));
        goblinPits.forceName = "the Goblin Pits";
        addCard(goblinPits);

        StoryCardDefinition woods = new SCDPlace();
        woods.title = "Weeping Woods";
        woods.tags.add("overworld");
        woods.forceName = "the Weeping Woods";
        addCard(woods);
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
