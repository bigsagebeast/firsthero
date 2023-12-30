package com.churchofcoyote.hero.story;

public enum StoryCardType {
    PLACE_TYPE, // ruined castle, elven village, dungeon
    PLACE_ROLE, // levelling grounds? evil lair? town?
    PERSON_TYPE, // human necromancer, elven maiden
    PERSON_ROLE, // kidnapping victim? evil overlord?
    THING_TYPE, // a sword or something
    ACTOR, // 'this part is played by FOO'
    POPULATION, // 'a group of orcs' for what populates a dungeon
    RELATIONSHIP, // kidnapper->victim, lover->lover, overlord->commanded population, person->lives in town
    PRECONDITION, // initial condition to kick off the grid
}
