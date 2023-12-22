package com.churchofcoyote.hero.roguelike.world.dungeon;

import java.util.ArrayList;

public class DungeonPlanRestrictions {
    // required: ALL must be present
    ArrayList<String> moverRequiredTags = new ArrayList<>();
    // allowed: At least one must be present
    ArrayList<String> moverAllowedTags = new ArrayList<>();
    ArrayList<String> moverForbiddenTags = new ArrayList<>();

    ArrayList<String> itemRequiredTags = new ArrayList<>();
    ArrayList<String> itemAllowedTags = new ArrayList<>();
    ArrayList<String> itemForbiddenTags = new ArrayList<>();

    int moverMinLevel;
    int moverMaxLevel;
    int itemMinLevel;
    int itemMaxLevel;
}
