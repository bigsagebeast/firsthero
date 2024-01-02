package com.bigsagebeast.hero.enums;

public enum Gender {

    MALE("his", "he", "him", "his", "himself"),
    FEMALE("hers", "she", "her", "her", "herself"),
    AGENDER("its", "it", "it", "its", "itself"),
    PLURAL("theirs", "them", "them", "their", "themselves"),
    NONBINARY("theirs", "them", "them", "their", "themself"),

    // assign male or female on entity creation
    RANDOM("theirs", "them", "them", "their", "themselves"),
    // pull values from profile
    CUSTOM(null, null, null, null, null);


    public String absolute;
    public String subjective;
    public String objective;
    public String possessive;
    public String reflexive;

    Gender(String absolute, String subjective, String objective, String possessive, String reflexive) {
        this.absolute = absolute;
        this.subjective = subjective;
        this.objective = objective;
        this.possessive = possessive;
        this.reflexive = reflexive;
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
}
