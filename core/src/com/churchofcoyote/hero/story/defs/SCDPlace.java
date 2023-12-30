package com.churchofcoyote.hero.story.defs;

import com.churchofcoyote.hero.story.*;

public class SCDPlace extends StoryCardDefinition {
    public SCDPlace() {
        type = StoryCardType.PLACE_TYPE;
        addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.OPTIONAL, "resident", "home"));
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.OPTIONAL, "location", "sublocation"));
        addLink(new StoryCardLink(StoryCardType.PLACE_TYPE, StoryLinkSeekType.OPTIONAL, "sublocation", "location"));

        addDefaultDescSelf("There is a place, %1n");
        addDefaultDescSelf("There is a place named %1n");
        /*
        addDefaultDescLink("resident", "In %1n lived %2n");
        addDefaultDescLink("resident", "At %1n lived %2n");
        addDefaultDescLink("location", "%1n was in %2n");
        addDefaultDescLink("sublocation", "%1n contained %2n");
         */
        addDefaultDescLink("resident", "is the home of %2n");
        addDefaultDescLink("location", "is located in %2n");
        addDefaultDescLink("sublocation", "contains %2n");
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
