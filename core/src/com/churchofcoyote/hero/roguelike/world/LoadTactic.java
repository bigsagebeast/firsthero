package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.roguelike.world.ai.Tactic;
import com.churchofcoyote.hero.roguelike.world.proc.monster.ProcMonster;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class LoadTactic {
    private static final String BASE_PACKAGE = "com.churchofcoyote.hero.roguelike.world.ai";
    private String tacticName;
    private Map<String, String> fields;

    public LoadTactic(String tacticName, Map<String, String> fields) {
        this.tacticName = tacticName;
        this.fields = fields;
    }

    public void apply(Entity entity) {
        Class<?> clazz;
        Tactic tactic;
        try {
            clazz = Class.forName(BASE_PACKAGE + "." + tacticName);
            tactic = (Tactic)clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find tactic " + tacticName, e);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("No base constructor for " + tacticName, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Couldn't instantiate " + tacticName, e);
        }
        try {
            for (String fieldName : fields.keySet()) {
                Field tacticsField = clazz.getDeclaredField(fieldName);
                tacticsField.setAccessible(true);
                if (tacticsField.getType().isEnum()) {
                    Class<? extends Enum> enumClass = (Class<? extends Enum>) tacticsField.getType();
                    Enum<?> enumValue = Enum.valueOf(enumClass, fields.get(fieldName));
                    tacticsField.set(tactic, enumValue);
                } else if (tacticsField.getType().isAssignableFrom(Boolean.class)) {
                    tacticsField.set(tactic, Boolean.valueOf(fields.get(fieldName)));
                } else if (tacticsField.getType().isAssignableFrom(String.class)) {
                    tacticsField.set(tactic, fields.get(fieldName));
                } else if (tacticsField.getType().isAssignableFrom(int.class) || tacticsField.getType().isAssignableFrom(Integer.class)) {
                    tacticsField.set(tactic, Integer.valueOf(fields.get(fieldName)));
                } else if (tacticsField.getType().isAssignableFrom(float.class) || tacticsField.getType().isAssignableFrom(Float.class)) {
                    tacticsField.set(tactic, Float.valueOf(fields.get(fieldName)));
                } else {
                    throw new RuntimeException("Couldn't identify field type for " + tacticName + "." + fieldName);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        ((ProcMonster)(entity.getProcByType(ProcMonster.class))).tactic = tactic;
    }
}
