package com.bigsagebeast.hero.roguelike.game;

import java.util.HashMap;

public class Profile {
    // godName: Name of your god
    // avatarName: Name of the avatar in the current world (may not be necessary?)
    public static HashMap<String, Integer> dataInt = new HashMap<>();
    public static HashMap<String, Float> dataFloat = new HashMap<>();
    public static HashMap<String, String> dataString = new HashMap<>();
    public static HashMap<String, Boolean> dataFlag = new HashMap<>();

    static {
        // defaults
        setString("godName", "the God of Heroes");
    }

    public static boolean save() {
        return false;
    }

    public static boolean load() {
        return false;
    }

    public static void setInt(String key, Integer value) {
        dataInt.put(key, value);
    }

    public static void setFloat(String key, Float value) {
        dataFloat.put(key, value);
    }

    public static void setString(String key, String value) {
        dataString.put(key, value);
    }

    public static void setFlag(String key, Boolean value) {
        dataFlag.put(key, value);
    }

    public static int getInt(String key) {
        Integer val = dataInt.get(key);
        if (val == null) {
            return 0;
        }
        return val;
    }

    public static float getFloat(String key) {
        Float val = dataFloat.get(key);
        if (val == null) {
            return 0;
        }
        return val;
    }

    public static String getString(String key) {
        String val = dataString.get(key);
        if (val == null) {
            return null;
        }
        return val;
    }

    public static boolean getFlag(String key) {
        Boolean val = dataFlag.get(key);
        if (val == null) {
            return false;
        }
        return val;
    }
}
