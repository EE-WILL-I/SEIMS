package ru.seims.utils.properties;

import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;
import org.apache.commons.configuration.PropertiesConfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class PropertyReader {
    public static final String FILE_POSTFIX = ".properties";
    private static InputStream fileInputStream;
    private static Properties PROPERTIES;
    private static final Map<String, Properties> PROPERTIES_MAP = new HashMap<>();
    private static final String PROPERTIES_PATH = FileResourcesUtils.RESOURCE_PATH + "properties/";

    public static String getPropertyValue(PropertyType property, String key) {
        if (getProperties(property) == null) {
            Logger.log(PropertyReader.class, "Cannot load key: " + key, 2);
            return "";
        }
        String value = PROPERTIES.getProperty(key);
        if(value != null) {
            Logger.log(PropertyReader.class, "Key loaded: " + key, 4);
            return value;
        }
        Logger.log(PropertyReader.class, "Key not present: " + key, 4);
        return "";
    }

    public static Properties getProperties(PropertyType property) {
        String propertyName = property.name().toLowerCase(Locale.ROOT);
        if(PROPERTIES_MAP.containsKey(propertyName)){
            PROPERTIES = PROPERTIES_MAP.get(propertyName);
        }
        else {
            if(!readProperty(propertyName)) {
                return null;
            }
        }
        return PROPERTIES;
    }

    private static boolean readProperty(String property) {
        PROPERTIES_MAP.put(property, loadProperty(PROPERTIES_PATH + property + FILE_POSTFIX));
        Logger.log(PropertyReader.class, "Property loaded: " + property, 4);
        return true;
    }

    public static Properties loadServerProps() {
        PROPERTIES = loadProperty(FileResourcesUtils.RESOURCE_PATH + PropertyType.APPLICATION.toString().toLowerCase(Locale.ROOT) + FILE_POSTFIX);
        PROPERTIES_MAP.put("application", PROPERTIES);
        PROPERTIES = loadProperty(PROPERTIES_PATH + PropertyType.SERVER.toString().toLowerCase(Locale.ROOT) + FILE_POSTFIX);
        String logLvl = PROPERTIES.getProperty("app.loggingLevel");
        if(logLvl == null)
            logLvl = "4";
        Logger.loggingLevel = Byte.parseByte(logLvl);
        if (getPropertyValue(PropertyType.SERVER, "app.disableSecurity").toLowerCase(Locale.ROOT).equals("true"))
            Logger.log(PropertyReader.class, "WARNING! Security system is disabled. See property \"app.disableSecurity\"", 3);
        if (getPropertyValue(PropertyType.SERVER, "app.disableDatabase").toLowerCase(Locale.ROOT).equals("true"))
            Logger.log(PropertyReader.class, "DB connection disabled. See property \"app.disableDatabase\"", 3);
        return PROPERTIES;
    }

    public static Properties loadProperty(String path) {
        try {
            fileInputStream = FileResourcesUtils.getFileAsStream(path);
            PROPERTIES = new Properties();
            PROPERTIES.load(fileInputStream);
            if(PROPERTIES.isEmpty())
                System.out.println("Empty property file at: " + path);
        } catch (Exception e) {
            Logger.log(PropertyReader.class, "Cannot read property at: " + path, 2);
            e.printStackTrace();
            return null;
        } finally {
            if (fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return PROPERTIES;
    }

    public static void saveProperty(PropertyType type, String property, String value) throws Exception {
        PropertiesConfiguration configuration = new PropertiesConfiguration(PROPERTIES_PATH + type.name().toLowerCase(Locale.ROOT) + FILE_POSTFIX);
        configuration.setProperty(property, value);
        configuration.save();
    }
}
