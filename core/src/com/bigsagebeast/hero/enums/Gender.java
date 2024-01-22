package com.bigsagebeast.hero.enums;

public enum Gender {

    MALE("his", "he", "him", "his", "himself", "is"),
    FEMALE("hers", "she", "her", "her", "herself", "is"),
    AGENDER("its", "it", "it", "its", "itself", "is"),
    PLURAL("theirs", "they", "them", "their", "themselves", "are"),
    NONBINARY("theirs", "they", "them", "their", "themself", "is"),

    // assign male or female on entity creation
    RANDOM("theirs", "they", "them", "their", "themselves", "is"),
    // pull values from profile
    CUSTOM(null, null, null, null, null, "is");


    public String absolute;
    public String subjective;
    public String objective;
    public String possessive;
    public String reflexive;
    public String linking;

    Gender(String absolute, String subjective, String objective, String possessive, String reflexive, String linking) {
        this.absolute = absolute;
        this.subjective = subjective;
        this.objective = objective;
        this.possessive = possessive;
        this.reflexive = reflexive;
        this.linking = linking;
    }

    public String a() {
        if (absolute != null) {
            return absolute;
        } else {
            return null;
        }
    }

    public String s() {
        if (subjective != null) {
            return subjective;
        } else {
            return null;
        }
    }

    public String o() {
        if (objective != null) {
            return objective;
        } else {
            return null;
        }
    }

    public String p() {
        if (possessive != null) {
            return possessive;
        } else {
            return null;
        }
    }

    public String r() {
        if (reflexive != null) {
            return reflexive;
        } else {
            return null;
        }
    }

    public String l() {
        if (linking != null) {
            return linking;
        } else {
            return null;
        }
    }
}
