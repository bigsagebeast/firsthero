package com.churchofcoyote.hero.storymanager;

import java.util.HashMap;

public class StoryState {
    public static HashMap<String, Integer> intMap = new HashMap<>();
    public static HashMap<String, String> stringMap = new HashMap<>();

    public static boolean compare(StoryComparator comparator) {
        try {
            if (comparator.op == StoryComparator.Operation.EXSTR) {
                return stringMap.containsKey(comparator.variableA);
            } else if (comparator.op == StoryComparator.Operation.NEXSTR) {
                return !stringMap.containsKey(comparator.variableA);
            } else if (comparator.op == StoryComparator.Operation.EX) {
                return intMap.containsKey(comparator.variableA);
            } else if (comparator.op == StoryComparator.Operation.NEX) {
                return !intMap.containsKey(comparator.variableA);
            } else if (comparator.op == StoryComparator.Operation.EQSTR || comparator.op == StoryComparator.Operation.NESTR) {
                String a, b;
                if (comparator.variableA != null) {
                    a = stringMap.get(comparator.variableA);
                } else {
                    a = (String)comparator.valueA;
                }
                if (comparator.variableB != null) {
                    b = stringMap.get(comparator.variableB);
                } else {
                    b = (String)comparator.valueB;
                }
                return comparator.op.operate(a, b);
            } else {
                Integer a, b;
                if (comparator.variableA != null) {
                    a = intMap.get(comparator.variableA);
                } else {
                    a = (Integer)comparator.valueA;
                }
                if (comparator.variableB != null) {
                    b = intMap.get(comparator.variableB);
                } else {
                    b = (Integer)comparator.valueB;
                }
                return comparator.op.operate(a, b);
            }
        } catch (Exception e) {
            throw new StoryException(e);
        }
    }

    public static void execute(StorySetter setter) {
        Integer intVal = null;
        String strVal = null;
        if (setter.value != null && setter.value.getClass().isAssignableFrom(Integer.class)) {
            intVal = (Integer)setter.value;
        } else if (setter.value != null && setter.value.getClass().isAssignableFrom(String.class)) {
            strVal = (String)setter.value;
        }
        switch (setter.op) {
            case ADDINT:
                if (intVal == null) {
                    throw new StoryException("Not an integer: " + setter.toString());
                }
                if (!intMap.containsKey(setter.var)) {
                    intMap.put(setter.var, 0);
                }
                intMap.put(setter.var, intMap.get(setter.var) + intVal);
                break;
            case SUBINT:
                if (intVal == null) {
                    throw new StoryException("Not an integer: " + setter.toString());
                }
                if (!intMap.containsKey(setter.var)) {
                    intMap.put(setter.var, 0);
                }
                intMap.put(setter.var, intMap.get(setter.var) - intVal);
                break;
            case SETINT:
                if (intVal == null) {
                    throw new StoryException("Not an integer: " + setter.toString());
                }
                intMap.put(setter.var, intVal);
                break;
            case REMINT:
                intMap.remove(setter.var);
                break;
            case SETSTR:
                if (strVal == null) {
                    throw new StoryException("Not a string: " + setter.toString());
                }
                stringMap.put(setter.var, strVal);
                break;
            case REMSTR:
                stringMap.remove(setter);
                break;
        }
    }
}
