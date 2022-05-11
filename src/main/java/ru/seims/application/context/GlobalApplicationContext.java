package ru.seims.application.context;

import ru.seims.utils.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class GlobalApplicationContext {
    protected static Map<String, String> context;
    static {
        context = new HashMap<String, String>();
    }

    public static void setParameter(String key, String value){
        if(key.isEmpty() || context.containsKey(key))
           Logger.log(GlobalApplicationContext.class, String.format("Key \"%s\" is not allowed", key), 2);
        else
            context.put(key, value);
    }

    public static void setParameter(String key, boolean value) {
        setParameter(key, String.valueOf(value));
    }

    public static String getParameter(String key) {
        if(key.isEmpty() || !context.containsKey(key)) {
            Logger.log(GlobalApplicationContext.class, String.format("Key \"%s\" is not present in context", key), 2);
            return "";
        }
        return context.get(key);
    }

    public static void clearContext() {
        context.clear();
        Logger.log(GlobalApplicationContext.class, "Context cleared.", 1);
    }

    public static boolean hasParameter(String key) {
        return context.containsKey(key);
    }
}
