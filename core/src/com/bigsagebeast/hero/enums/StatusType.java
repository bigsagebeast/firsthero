package com.bigsagebeast.hero.enums;

public enum StatusType {
    // For tracking things you can be immune to
    CONFUSION("confusion");

    public String description;

    StatusType(String description) {
        this.description = description;
    }
}
