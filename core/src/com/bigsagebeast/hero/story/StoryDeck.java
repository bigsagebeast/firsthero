package com.bigsagebeast.hero.story;

import com.bigsagebeast.hero.story.defs.SCDPerson;
import com.bigsagebeast.hero.story.defs.SCDPlace;
import com.bigsagebeast.hero.story.defs.SCDPopulation;
import com.bigsagebeast.hero.story.defs.SCDWeapon;

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
        goblinLeader.addDescSelf("there is a wily goblin boss, %1n");
        goblinLeader.addNameIntro("the wily goblin boss, %1n");
        addCard(goblinLeader);

        StoryCardDefinition nightbringer = new SCDWeapon();
        nightbringer.title = "nightbringer";
        nightbringer.tags.add("weapon");
        nightbringer.addDescSelf("forged in darkness is the dread blade %1n");
        nightbringer.addNameIntro("the dread blade %1n");
        addCard(nightbringer);

        StoryCardDefinition goblinCaves = new SCDPlace();
        goblinCaves.title = "goblinCaves";
        goblinCaves.tags.add("goblin");
        goblinCaves.tags.add("dungeon");
        goblinCaves.links.get("location").seekType = StoryLinkSeekType.REQUIRED;
        goblinCaves.links.get("location").requirements.add(new StoryLinkRequirementTagRequired("overworld"));
        goblinCaves.links.get("populace").seekType = StoryLinkSeekType.REQUIRED;
        goblinCaves.forceName = "the Goblin Caves";
        goblinCaves.addDescSelf("Beneath the earth lie the gloomy Goblin Caves");
        goblinCaves.addNameIntro("the gloomy Goblin Caves");
        addCard(goblinCaves);

        StoryCardDefinition goblinPits = new SCDPlace();
        goblinPits.title = "goblinPits";
        goblinPits.tags.add("goblin");
        goblinPits.tags.add("dungeon");
        goblinPits.links.get("location").seekType = StoryLinkSeekType.REQUIRED;
        goblinPits.links.get("location").requirements.add(new StoryLinkRequirementTagRequired("overworld"));
        goblinPits.links.get("populace").seekType = StoryLinkSeekType.REQUIRED;
        goblinPits.forceName = "the Goblin Pits";
        goblinPits.addDescSelf("Beneath the earth lie the gloomy Goblin Pits");
        goblinPits.addNameIntro("the gloomy Goblin Pits");
        addCard(goblinPits);

        StoryCardDefinition woods = new SCDPlace();
        woods.title = "Weeping Woods";
        woods.tags.add("overworld");
        woods.forceName = "the Weeping Woods";
        woods.addDescSelf("Enshrouded in mists are %1n");
        woods.addNameIntro("%1n, enshrouded in mists");
        addCard(woods);

        StoryCardDefinition populationGoblins = new SCDPopulation();
        populationGoblins.title = "populationGoblins";
        populationGoblins.tags.add("goblin");
        populationGoblins.forceName = "the goblin horde";
        populationGoblins.addDescSelf("From across the acid sea have come a goblin horde");
        populationGoblins.addNameIntro("a goblin horde from across the acid sea");
        addCard(populationGoblins);

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
