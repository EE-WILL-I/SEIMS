package ru.seims.application.context;

import ru.seims.utils.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalApplicationContext {
    private static final LinkedHashMap<String, String> context;
    private static int contextMaxSize = 0;
    private static int contextSize = 0;
    static {
        context = new LinkedHashMap<String, String>();
    }

    public static void setParameter(String key, String value) {
        if (key.isEmpty())
            Logger.log(GlobalApplicationContext.class, String.format("Key \"%s\" is not allowed", key), 4);
        if(contextMaxSize > 0 && contextSize > contextMaxSize) {
            String oldestEntry = (String)context.keySet().toArray()[0];
            context.remove(oldestEntry);
        }
        else if (context.containsKey(key))
            context.replace(key, value);
        else
            context.put(key, value);
        contextSize += value.getBytes(StandardCharsets.UTF_8).length;
        Logger.log(GlobalApplicationContext.class, "Added parameter to GAC: " + key + " : " + value);
    }

    public static void setParameter(String key, boolean value) {
        setParameter(key, String.valueOf(value));
    }

    public static String getParameter(String key) {
        if(key.isEmpty() || !context.containsKey(key)) {
            Logger.log(GlobalApplicationContext.class, String.format("Key \"%s\" is not present in context", key), 4);
            return "";
        }
        return context.get(key);
    }

    public static void setContextMaxSize(int size) {
        contextMaxSize = size;
    }

    public static int getContextSize() {
        return contextSize;
    }

    public static void clearContext() {
        context.clear();
        Logger.log(GlobalApplicationContext.class, "Context cleared.", 1);
    }

    public static boolean hasParameter(String key) {
        return context.containsKey(key);
    }
}
