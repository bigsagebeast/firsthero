package com.bigsagebeast.hero.util;

public enum Gender {

    MALE("his", "he", "him", "his", "himself"),
    FEMALE("hers", "she", "her", "her", "herself"),
    AGENDER("its", "it", "it", "its", "itself"),
    PLURAL("theirs", "them", "them", "their", "themself"),
    NONBINARY("theirs", "them", "them", "their", "themself");


    Gender(String absolute, String subjective, String objective, String possessive, String reflexive) {
        this.absolute = absolute;
        this.subjective = subjective;
        this.objective = objective;
        this.possessive = possessive;
        this.reflexive = reflexive;
    }

    public String absolute;
    public String subjective;
    public String objective;
    public String possessive;
    public String reflexive;
}
