package ru.seims.application.context;

import java.util.HashMap;
import java.util.Map;

public class GlobalApplicationContext {
    protected static Map<String, String> context;
    static {
        context = new HashMap<String, String>();
    }

    public static void setParameter(String key, String value) throws IllegalArgumentException {
        if(key.isEmpty() || context.containsKey(key))
            throw new IllegalArgumentException(String.format("Key \"%s\" is not allowed", key));
        context.put(key, value);
    }

    public static void setParameter(String key, boolean value) {
        setParameter(key, String.valueOf(value));
    }

    public static String getParameter(String key) throws IllegalArgumentException {
        if(key.isEmpty() || !context.containsKey(key))
            throw new IllegalArgumentException(String.format("Key \"%s\" is not present in context", key));
        return context.get(key);
    }

    public static boolean hasParameter(String key) {
        return context.containsKey(key);
    }
}
