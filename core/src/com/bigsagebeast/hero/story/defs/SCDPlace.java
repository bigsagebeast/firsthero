package com.bigsagebeast.hero.story.defs;

import com.bigsagebeast.hero.story.*;

public class SCDPlace extends StoryCardDefinition {
    public SCDPlace() {
        type = StoryCardType.PLACE_TYPE;
        addLink(new StoryCardLink(StoryCardType.POPULATION, StoryLinkSeekType.OPTIONAL, "populace", "lair"));
        addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.OPTIONAL, "resident", "home"));
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.OPTIONAL, "location", "sublocation"));
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.OPTIONAL, "sublocation", "location"));

        addDefaultDescSelf("There is a place, %1n");
        addDefaultDescSelf("There is a place named %1n");

        // TODO these should handle plurals
        addDefaultDescLink("populace", "are the lair of %2n");
        addDefaultDescLink("resident", "are the home of %2n");
        addDefaultDescLink("location", "are located in %2n");
        addDefaultDescLink("sublocation", "contain %2n");
    }

    @Override
    public void giveName(StoryCard storyCard) {
        if (forceName == null) {
            storyCard.shortName = "the Crystal Caves";
        } else {
            storyCard.shortName = forceName;
        }
    }
}
