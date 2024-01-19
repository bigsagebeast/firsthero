package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.world.dungeon.Room;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoadProc {
    private static final String BASE_PACKAGE = "com.bigsagebeast.hero.roguelike.world.proc";
    public String procName;
    public Map<String, String> fields;

    public LoadProc(String procName, Map<String, String> fields) {
        this.procName = procName;
        this.fields = fields;
    }

    public void apply(Entity entity) {
        entity.addProc(apply());
        // Is it dangerous to call initialize here, when some procs could refer to another entity that isn't loaded yet?
    }

    public void apply(Room room) {
        room.addProc(apply());
    }

    public Proc apply() {
        Class<?> clazz;
        Proc proc;
        try {
            clazz = Class.forName(BASE_PACKAGE + "." + procName);
            proc = (Proc)clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find proc " + procName, e);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("No base constructor for " + procName, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Couldn't instantiate " + procName, e);
        }
        //proc.entity = entity;
        try {
            for (String fieldName : fields.keySet()) {
                Field procField = clazz.getDeclaredField(fieldName);
                procField.setAccessible(true);
                if (procField.getType().isEnum()) {
                    Class<? extends Enum> enumClass = (Class<? extends Enum>) procField.getType();
                    Enum<?> enumValue = Enum.valueOf(enumClass, fields.get(fieldName));
                    procField.set(proc, enumValue);
                } else if (procField.getType().isAssignableFrom(Boolean.class) || procField.getType().isAssignableFrom(boolean.class)) {
                    procField.set(proc, Boolean.valueOf(fields.get(fieldName)));
                } else if (procField.getType().isAssignableFrom(String.class)) {
                    procField.set(proc, fields.get(fieldName));
                } else if (procField.getType().isAssignableFrom(int.class) || procField.getType().isAssignableFrom(Integer.class)) {
                    procField.set(proc, Integer.valueOf(fields.get(fieldName)));
                } else if (procField.getType().isAssignableFrom(float.class) || procField.getType().isAssignableFrom(Float.class)) {
                    procField.set(proc, Float.valueOf(fields.get(fieldName)));
                } else if (procField.getType().isArray() && procField.getType().getComponentType().isAssignableFrom(String.class)) {
                    procField.set(proc, Arrays.stream(fields.get(fieldName).split(",")).map(s -> s.trim()).toArray(String[]::new));
                } else if (procField.getType().isAssignableFrom(Map.class)) {
                    ParameterizedType genericType = (ParameterizedType) procField.getGenericType();
                    Class<?> keyType = (Class<?>) genericType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>) genericType.getActualTypeArguments()[1];

                    if (keyType == Stat.class && valueType == EquipmentScaling.class) {
                        Map<Stat, EquipmentScaling> mapValue = parseMapString(fields.get(fieldName));
                        procField.set(proc, mapValue);
                    } else {
                        throw new RuntimeException("Unsupported map type for " + procName + "." + fieldName);
                    }
                } else {
                    throw new RuntimeException("Couldn't identify field type for " + procName + "." + fieldName);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return proc;
    }


    private EquipmentScaling parseEquipmentScaling(String scalingString) {
        EquipmentScaling equipmentScaling = new EquipmentScaling();

        String[] valuePairs = scalingString.split(",");
        for (String valuePair : valuePairs) {
            String[] keyValue = valuePair.split("=");
            String fieldName = keyValue[0].trim();
            float value = Float.parseFloat(keyValue[1].trim());

            // Use reflection to set the field value
            setField(equipmentScaling, fieldName, value);
        }

        return equipmentScaling;
    }

    private void setField(EquipmentScaling equipmentScaling, String fieldName, float value) {
        try {
            Field field = EquipmentScaling.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setFloat(equipmentScaling, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error setting field " + fieldName + " in EquipmentScaling.", e);
        }
    }

    private Map<Stat, EquipmentScaling> parseMapString(String mapString) {
        Map<Stat, EquipmentScaling> result = new HashMap<>();

        String[] entries = mapString.split(";");
        for (String entry : entries) {
            String[] keyValue = entry.split(":");
            Stat key = Enum.valueOf(Stat.class, keyValue[0]);
            EquipmentScaling value = parseEquipmentScaling(keyValue[1]);
            result.put(key, value);
        }

        return result;
    }
}
