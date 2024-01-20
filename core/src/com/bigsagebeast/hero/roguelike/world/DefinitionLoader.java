package com.bigsagebeast.hero.roguelike.world;

import com.badlogic.gdx.files.FileHandle;
import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.roguelike.game.LoadingTips;
import com.bigsagebeast.hero.roguelike.world.dungeon.RoomType;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.SpecialSpawner;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.Theme;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.ThemeRoom;
import com.bigsagebeast.hero.roguelike.world.dungeon.generation.Themepedia;
import com.bigsagebeast.hero.SetupException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefinitionLoader {
    public static void loadFile(FileHandle handle) throws SetupException {
        ObjectMapper om = new ObjectMapper();
        om.getFactory().enable(JsonParser.Feature.ALLOW_COMMENTS);
        JsonNode root;
        try {
            root = om.readTree(new BufferedReader(new InputStreamReader(handle.read())));
        } catch (IOException e) {
            throw new RuntimeException("Error in " + handle.path(), e);
        }
        try {
            JsonNode items = root.get("items");
            if (items != null) {
                loadItems(items, handle.path());
            }
            JsonNode movers = root.get("movers");
            if (movers != null) {
                loadMovers(movers, handle.path());
            }
            JsonNode themes = root.get("themes");
            if (themes != null) {
                loadThemes(themes, handle.path());
            }
            JsonNode tips = root.get("tips");
            if (tips != null) {
                loadTips(tips, handle.path());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadItems(JsonNode items, String path) throws NoSuchFieldException, IllegalAccessException, SetupException {
        for (Iterator<String> itemNames = items.fieldNames(); itemNames.hasNext(); ) {
            String itemName = itemNames.next();
            JsonNode itemNode = items.get(itemName);
            ItemType itemType = new ItemType();
            itemType.keyName = itemName;
            itemType.file = path;
            // TODO: This check is impossible, because Jackson strips duplicates.  Find another way.
            if (Itempedia.map.get(itemType.keyName) != null) {
                throw new SetupException("Duplicate item key: " + itemName);
            }
            for (Iterator<String> defFieldName = itemNode.fieldNames(); defFieldName.hasNext(); ) {
                String fieldName = defFieldName.next();
                JsonNode nodeField = itemNode.get(fieldName);
                if (fieldName == "equipmentFor") {
                    String equipmentForString = nodeField.asText();
                    BodyPart bp = BodyPart.getPart(equipmentForString);
                    itemType.equipmentFor = bp;
                } else if (fieldName == "category") {
                    String categoryString = nodeField.asText();
                    ItemCategory category = ItemCategory.categories.stream().filter(
                                    cat -> cat.key.equals(categoryString)).findFirst()
                            .orElseThrow(() -> new RuntimeException("Invalid item category " + categoryString));
                    itemType.category = category;
                } else if (fieldName == "procs") {
                    for (Iterator<String> procNames = nodeField.fieldNames(); procNames.hasNext(); ) {
                        String procName = procNames.next();
                        HashMap<String, String> procFields = new HashMap<>();
                        JsonNode procNameNode = nodeField.get(procName);
                        for (Iterator<String> procField = procNameNode.fieldNames(); procField.hasNext(); ) {
                            String procFieldName = procField.next();
                            JsonNode procFieldNode = procNameNode.get(procFieldName);
                            String procFieldOutput;
                            if (procFieldNode.isObject()) {

                                StringBuilder mapStringBuilder = new StringBuilder();

                                Iterator<Map.Entry<String, JsonNode>> entries = procFieldNode.fields();
                                while (entries.hasNext()) {
                                    Map.Entry<String, JsonNode> entry = entries.next();
                                    String key = entry.getKey();
                                    JsonNode valuesNode = entry.getValue();

                                    // Append enum constant and separator
                                    mapStringBuilder.append(key).append(":");

                                    // Append field-value pairs
                                    Iterator<Map.Entry<String, JsonNode>> values = valuesNode.fields();
                                    while (values.hasNext()) {
                                        Map.Entry<String, JsonNode> valueEntry = values.next();
                                        mapStringBuilder.append(valueEntry.getKey()).append("=").append(valueEntry.getValue().asText()).append(",");
                                    }

                                    // Remove the trailing comma before appending entry separator
                                    if (valuesNode.size() > 0) {
                                        mapStringBuilder.deleteCharAt(mapStringBuilder.length() - 1);
                                    }

                                    // Append entry separator
                                    mapStringBuilder.append(";");
                                }
                                if (procFieldNode.size() > 0) {
                                    // Remove the trailing semicolon if the scaling node is not empty
                                    mapStringBuilder.deleteCharAt(mapStringBuilder.length() - 1);
                                }
                                procFieldOutput = mapStringBuilder.toString();

                            } else {
                                procFieldOutput = procFieldNode.asText();
                            }
                            procFields.put(procFieldName, procFieldOutput);
                        }
                        LoadProc procLoader = new LoadProc(procName, procFields);
                        itemType.procLoaders.add(procLoader);
                    }
                } else if (fieldName == "palette") {
                    if (!nodeField.isArray()) {
                        throw new RuntimeException("Palette was not an array");
                    }
                    ArrayList<Integer> paletteEntries = new ArrayList<>();
                    for (JsonNode entry : nodeField) {
                        if (Palette.stringMap.get(entry.textValue()) == null) {
                            throw new SetupException("Unknown color in palette: " + entry.textValue() +
                                    " for item: " + itemName);
                        }
                        paletteEntries.add(Palette.stringMap.get(entry.textValue()));
                    }
                    while (paletteEntries.size() < 4) {
                        paletteEntries.add(Palette.COLOR_TRANSPARENT);
                    }
                    itemType.palette = new PaletteEntry(paletteEntries.get(0), paletteEntries.get(1),
                            paletteEntries.get(2), paletteEntries.get(3));
                } else if (fieldName == "tags") {
                    if (!nodeField.isArray()) {
                        throw new RuntimeException("Tags was not an array");
                    }
                    for (JsonNode entry : nodeField) {
                        itemType.tags.add(entry.textValue());
                    }
                } else {
                    Field itemTypeField = ItemType.class.getDeclaredField(fieldName);
                    itemTypeField.setAccessible(true);

                    if (itemTypeField.getType().isAssignableFrom(String.class)) {
                        itemTypeField.set(itemType, nodeField.asText());
                    } else if (itemTypeField.getType().isAssignableFrom(int.class) || itemTypeField.getType().isAssignableFrom(Integer.class)) {
                        itemTypeField.set(itemType, nodeField.asInt());
                    } else if (itemTypeField.getType().isAssignableFrom(float.class) || itemTypeField.getType().isAssignableFrom(Float.class)) {
                        itemTypeField.set(itemType, Float.valueOf(nodeField.asText()));
                    } else if (itemTypeField.getType().isAssignableFrom(boolean.class) || itemTypeField.getType().isAssignableFrom(Boolean.class)) {
                        itemTypeField.set(itemType, Boolean.valueOf(nodeField.asText()));
                    }

                }
            }
            // special handling
            if (itemType.isFeature && itemType.sortOrder == 1) {
                itemType.sortOrder = -1;
            }
            Itempedia.map.put(itemName, itemType);
        }
    }


    public static void loadMovers(JsonNode movers, String path) throws NoSuchFieldException, IllegalAccessException, SetupException {
        for (Iterator<String> moverNames = movers.fieldNames(); moverNames.hasNext(); ) {
            String moverName = moverNames.next();
            JsonNode moverNode = movers.get(moverName);
            Phenotype phenotype = new Phenotype();
            phenotype.key = moverName;

            for (Iterator<String> defFieldName = moverNode.fieldNames(); defFieldName.hasNext(); ) {
                String fieldName = defFieldName.next();
                JsonNode nodeField = moverNode.get(fieldName);
                if (fieldName == "procs") {
                    for (Iterator<String> procNames = nodeField.fieldNames(); procNames.hasNext(); ) {
                        String procName = procNames.next();
                        HashMap<String, String> procFields = new HashMap<>();
                        JsonNode procNameNode = nodeField.get(procName);
                        for (Iterator<String> procField = procNameNode.fieldNames(); procField.hasNext(); ) {
                            String procFieldName = procField.next();
                            procFields.put(procFieldName, procNameNode.get(procFieldName).asText());
                        }
                        LoadProc procLoader = new LoadProc(procName, procFields);
                        phenotype.procLoaders.add(procLoader);
                    }
                } else if (fieldName == "tactic") {
                    HashMap<String, String> tacticFields = new HashMap<>();
                    String tacticName = null;
                    for (Iterator<String> tacticField = nodeField.fieldNames(); tacticField.hasNext(); ) {
                        String tacticFieldName = tacticField.next();
                        if (tacticFieldName.equals("name")) {
                            tacticName = nodeField.get(tacticFieldName).asText();
                        } else {
                            tacticFields.put(tacticFieldName, nodeField.get(tacticFieldName).asText());
                        }
                    }
                    LoadTactic tacticLoader = new LoadTactic(tacticName, tacticFields);
                    phenotype.tacticLoader = tacticLoader;
                } else if (fieldName == "palette") {
                    if (!nodeField.isArray()) {
                        throw new RuntimeException("Palette was not an array");
                    }
                    ArrayList<Integer> paletteEntries = new ArrayList<>();
                    for (JsonNode entry : nodeField) {
                        if (Palette.stringMap.get(entry.textValue()) == null) {
                            throw new SetupException("Unknown color in palette: " + entry.textValue() +
                                    " for item: " + moverName);
                        }
                        paletteEntries.add(Palette.stringMap.get(entry.textValue()));
                    }
                    while (paletteEntries.size() < 4) {
                        paletteEntries.add(Palette.COLOR_TRANSPARENT);
                    }
                    phenotype.palette = new PaletteEntry(paletteEntries.get(0), paletteEntries.get(1),
                            paletteEntries.get(2), paletteEntries.get(3));
                } else if (fieldName == "tags") {
                    if (!nodeField.isArray()) {
                        throw new RuntimeException("Tags was not an array");
                    }
                    for (JsonNode entry : nodeField) {
                        phenotype.tags.add(entry.textValue());
                    }
                } else if (fieldName == "glyphNames") {
                    if (!nodeField.isArray()) {
                        throw new RuntimeException("glyphNames was not an array");
                    }
                    for (JsonNode entry : nodeField) {
                        phenotype.glyphNames.add(entry.textValue());
                    }
                } else {
                    Field phenotypeField = Phenotype.class.getDeclaredField(fieldName);
                    phenotypeField.setAccessible(true);

                    if (phenotypeField.getType().isEnum()) {
                        Class<? extends Enum> enumClass = (Class<? extends Enum>) phenotypeField.getType();
                        Enum<?> enumValue = Enum.valueOf(enumClass, nodeField.asText());
                        phenotypeField.set(phenotype, enumValue);
                    } else if (phenotypeField.getType().isAssignableFrom(String.class)) {
                        phenotypeField.set(phenotype, nodeField.asText());
                    } else if (phenotypeField.getType().isAssignableFrom(int.class) || phenotypeField.getType().isAssignableFrom(Integer.class)) {
                        phenotypeField.set(phenotype, nodeField.asInt());
                    } else if (phenotypeField.getType().isAssignableFrom(float.class) || phenotypeField.getType().isAssignableFrom(Float.class)) {
                        phenotypeField.set(phenotype, Float.valueOf(nodeField.asText()));
                    } else if (phenotypeField.getType().isAssignableFrom(boolean.class) || phenotypeField.getType().isAssignableFrom(Boolean.class)) {
                        phenotypeField.set(phenotype, Boolean.valueOf(nodeField.asText()));
                    } else if (phenotypeField.getType().isArray() && phenotypeField.getType().getComponentType().isAssignableFrom(String.class)) {
                        // TODO broken, needs special casing
                        ArrayList<String> stringList = new ArrayList<>();
                        for (JsonNode elementNode : nodeField) {
                            stringList.add(elementNode.asText());
                        }
                        phenotypeField.set(phenotype, stringList.toArray());
                    }
                }
            }
            Bestiary.map.put(moverName, phenotype);
        }
    }

    public static void loadThemes(JsonNode themes, String path) throws NoSuchFieldException, IllegalAccessException, SetupException {
        for (Iterator<String> themeNames = themes.fieldNames(); themeNames.hasNext(); ) {
            String themeName = themeNames.next();
            JsonNode themeNode = themes.get(themeName);

            Theme theme = new Theme();
            Themepedia.put(themeName, theme);
            theme.key = themeName;
            theme.totalRooms = getAsInt(themeNode, "totalRooms", 8);
            JsonNode themeRoomsNode = themeNode.get("rooms");
            if (themeRoomsNode != null) {
                for (Iterator<String> defFieldName = themeRoomsNode.fieldNames(); defFieldName.hasNext(); ) {
                    String fieldName = defFieldName.next();
                    JsonNode roomNode = themeRoomsNode.get(fieldName);
                    String roomName = getAsString(roomNode, "roomName", "ROOM NAME: " + fieldName);
                    String roomDescription = getAsString(roomNode, "roomDesc", "ROOM DESCRIPTION: " + fieldName);
                    int softCap = getAsInt(roomNode, "softCap", 1);
                    int hardCap = getAsInt(roomNode, "hardCap", 1);
                    int depth = getAsInt(roomNode, "depth", 1);
                    float priority = getAsFloat(roomNode, "priority", 1.0f);
                    String loopString = getAsString(roomNode, "loops", "OKAY");
                    Theme.ThemeLoopsPreferred loops = Theme.ThemeLoopsPreferred.OKAY;
                    switch (loopString) {
                        case "NEVER":
                            loops = Theme.ThemeLoopsPreferred.NEVER;
                            break;
                        case "DEAD_END":
                            loops = Theme.ThemeLoopsPreferred.DEAD_END;
                            break;
                        case "PREFERRED":
                            loops = Theme.ThemeLoopsPreferred.PREFERRED;
                            break;
                        case "OKAY":
                            loops = Theme.ThemeLoopsPreferred.OKAY;
                            break;
                    }
                    ThemeRoom themeRoom = new ThemeRoom(new RoomType(roomName, roomDescription, true), fieldName,
                            softCap, hardCap, depth, priority, loops);
                    theme.add(themeRoom);
                    JsonNode spawn = roomNode.get("spawn");
                    if (spawn != null) {
                        if (!spawn.isArray()) {
                            throw new RuntimeException("Spawn list was not an array");
                        }
                        for (JsonNode entry : spawn) {
                            themeRoom.type.spawners.add(loadThemeRoomSpawner(entry));
                        }
                    }
                }
            }
        }
    }

    public static SpecialSpawner loadThemeRoomSpawner(JsonNode node) {
        SpecialSpawner spawner = new SpecialSpawner();
        spawner.isMover = getAsString(node, "type", "item").equals("mover");
        spawner.key = getAsString(node, "key", null);
        spawner.threatModifier = getAsInt(node, "level", 0);
        spawner.percentChance = getAsInt(node, "chance", 100);
        spawner.quantity = getAsInt(node, "quantity", 1);
        spawner.quantityMax = getAsInt(node, "quantityMax", -1);
        JsonNode tags = node.get("tags");
        if (tags != null) {
            if (!tags.isArray()) {
                throw new RuntimeException("Tags was not an array");
            }
            for (JsonNode entry : tags) {
                spawner.tags.add(entry.textValue());
            }
        }

        return spawner;
    }

    public static void loadTips(JsonNode tipsNode, String path) throws NoSuchFieldException, IllegalAccessException, SetupException {
        if (tipsNode != null) {
            if (!tipsNode.isArray()) {
                throw new RuntimeException("Tip list was not an array");
            }
            for (JsonNode entry : tipsNode) {
                LoadingTips.Tip tip = new LoadingTips.Tip();
                tip.title = getAsString(entry, "title", "");
                tip.message = getAsString(entry, "message", "");
                if (tip.title.isEmpty() || tip.message.isEmpty()) {
                    throw new SetupException("Malformed loading tip");
                }
                LoadingTips.tips.add(tip);
            }
        }
    }

    private static int getAsInt(JsonNode node, String key, int defaultValue) {
        JsonNode child = node.get(key);
        if (child == null) {
            return defaultValue;
        }
        return child.asInt(defaultValue);
    }

    private static float getAsFloat(JsonNode node, String key, float defaultValue) {
        JsonNode child = node.get(key);
        if (child == null) {
            return defaultValue;
        }
        return (float)child.asDouble(defaultValue);
    }

    private static String getAsString(JsonNode node, String key, String defaultValue) {
        JsonNode child = node.get(key);
        if (child == null) {
            return defaultValue;
        }
        return child.asText(defaultValue);
    }
}

