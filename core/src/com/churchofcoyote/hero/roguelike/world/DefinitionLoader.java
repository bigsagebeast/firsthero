package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DefinitionLoader {
    public static void loadFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            throw new RuntimeException("Definition filename " + filename + " does not exist");
        }
        ObjectMapper om = new ObjectMapper();
        JsonNode root;
        try {
            root = om.readTree(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            JsonNode items = root.get("items");
            if (items != null) {
//            for (JsonNode itemNode : items) {
                for (Iterator<String> itemNames = items.fieldNames(); itemNames.hasNext(); ) {
                    String itemName = itemNames.next();
                    JsonNode itemNode = items.get(itemName);
                    ItemType itemType = new ItemType();
                    itemType.keyName = itemName;
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
                                    procFields.put(procFieldName, procNameNode.get(procFieldName).asText());
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
                                paletteEntries.add(Palette.stringMap.get(entry.textValue()));
                            }
                            while (paletteEntries.size() < 4) {
                                paletteEntries.add(Palette.COLOR_TRANSPARENT);
                            }
                            itemType.palette = new PaletteEntry(paletteEntries.get(0), paletteEntries.get(1),
                                    paletteEntries.get(2), paletteEntries.get(3));
                        } else {
                            Field itemTypeField = ItemType.class.getDeclaredField(fieldName);
                            itemTypeField.setAccessible(true);

                            if (itemTypeField.getType().isAssignableFrom(String.class)) {
                                itemTypeField.set(itemType, nodeField.asText());
                            } else if (itemTypeField.getType().isAssignableFrom(int.class) || itemTypeField.getType().isAssignableFrom(Integer.class)) {
                                itemTypeField.set(itemType, nodeField.asInt());
                            } else if (itemTypeField.getType().isAssignableFrom(float.class) || itemTypeField.getType().isAssignableFrom(Float.class)) {
                                itemTypeField.set(itemType, Float.valueOf(nodeField.asText()));
                            }

                        }
                    }
                    Itempedia.map.put(itemName, itemType);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
