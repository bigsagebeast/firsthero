package com.churchofcoyote.hero.story.defs;

import com.churchofcoyote.hero.story.*;

public class SCDPlace extends StoryCardDefinition {
    public SCDPlace() {
        type = StoryCardType.PLACE_TYPE;
        addLink(new StoryCardLink(StoryCardType.PERSON_TYPE, StoryLinkSeekType.OPTIONAL, "resident", "home"));
        addLink(new StoryCardLink(StoryCardType.PRECONDITION, StoryLinkSeekType.NO_SEEK, "precondition", "precondition", false));

        addDefaultDescSelf("There was a place, %1n");
        addDefaultDescSelf("There was a place named %1n");
        addDefaultDescLink("resident", "In %1n lived %2n");
        addDefaultDescLink("resident", "At %1n lived %2n");
    }

    @Override
    public void giveName(StoryCard storyCard) {
        storyCard.shortName = "the Crystal Caves";
    }
}
