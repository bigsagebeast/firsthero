package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.roguelike.world.proc.Proc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class LoadProc {
    private static final String BASE_PACKAGE = "com.churchofcoyote.hero.roguelike.world.proc";
    private String procName;
    private Map<String, String> fields;

    public LoadProc(String procName, Map<String, String> fields) {
        this.procName = procName;
        this.fields = fields;
    }

    public void apply(Entity entity) {
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
                } else if (procField.getType().isAssignableFrom(Boolean.class)) {
                    procField.set(proc, Boolean.valueOf(fields.get(fieldName)));
                } else if (procField.getType().isAssignableFrom(String.class)) {
                    procField.set(proc, fields.get(fieldName));
                } else if (procField.getType().isAssignableFrom(int.class) || procField.getType().isAssignableFrom(Integer.class)) {
                    procField.set(proc, Integer.valueOf(fields.get(fieldName)));
                } else if (procField.getType().isAssignableFrom(float.class) || procField.getType().isAssignableFrom(Float.class)) {
                    procField.set(proc, Float.valueOf(fields.get(fieldName)));
                } else {
                    throw new RuntimeException("Couldn't identify field type for " + procName + "." + fieldName);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        entity.addProc(proc);
        proc.initialize();
    }
}
