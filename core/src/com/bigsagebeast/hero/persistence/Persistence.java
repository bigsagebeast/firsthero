package com.bigsagebeast.hero.persistence;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Persistence {
    private static final String saveLocation = "C:\\temp\\hero\\";
    private static final String levelPrefix = "level_";
    private static final String profilePrefix = "profile";
    private static final String suffix = ".sav";

    public static void saveProfile() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(
                objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        );

        PersistentProfile profile = new PersistentProfile();
        profile.save();

        String filename = saveLocation + profilePrefix + suffix;
        File fileHandle = new File(filename);
        if (fileHandle.exists()) {
            fileHandle.delete();
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileHandle, profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveLevel(Level level) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(
                objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        );

        PersistentLevel pl = new PersistentLevel(Game.getLevel());
        String filename = saveLocation + levelPrefix + level.getKey() + suffix;
        File levelFileHandle = new File(filename);
        System.out.println("Saving game as " + levelFileHandle.getAbsolutePath());
        if (levelFileHandle.exists()) {
            levelFileHandle.delete();
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(levelFileHandle, pl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PersistentProfile loadProfile() {
        String filename = saveLocation + profilePrefix + suffix;
        File fileHandle = new File(filename);
        if (!fileHandle.exists()) {
            throw new RuntimeException("Failed to load profile: missing " + filename);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        PersistentProfile profile;
        try {
            profile = objectMapper.readValue(fileHandle, PersistentProfile.class);
        } catch (IOException e) {
            throw new RuntimeException("IO failure while loading profile", e);
        }

        profile.load();

        return profile;
    }

    public static Level loadLevel(String levelName) {
        String filename = saveLocation + levelPrefix + levelName + suffix;
        File fileHandle = new File(filename);
        if (!fileHandle.exists()) {
            throw new RuntimeException("Failed to load level: missing " + filename);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        PersistentLevel persistentLevel;
        try {
            persistentLevel = objectMapper.readValue(fileHandle, PersistentLevel.class);
        } catch (IOException e) {
            throw new RuntimeException("IO failure while loading level " + levelName, e);
        }

        return persistentLevel.unfreeze();
    }
}

