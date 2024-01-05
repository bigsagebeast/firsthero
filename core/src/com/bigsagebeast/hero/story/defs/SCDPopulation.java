package com.bigsagebeast.hero.story.defs;

import com.bigsagebeast.hero.story.*;

public class SCDPopulation extends StoryCardDefinition {
    public SCDPopulation() {
        type = StoryCardType.POPULATION;
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.REQUIRED, "lair", "populace"));
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));

        addDefaultDescSelf("There is a population, %1n");
        addDefaultDescSelf("There is a population of %1n");
        addDefaultDescLink("lair", "occupy %2n");
        addDefaultDescLink("lair", "reside in %2n");
    }

    @Override
    public String introToStreamingConnector() {
        return "who";
    }

    @Override
    public void giveName(StoryCard storyCard) {
        if (forceName == null) {
            storyCard.shortName = "goblins";
        } else {
            storyCard.shortName = forceName;
        }
    }

}
