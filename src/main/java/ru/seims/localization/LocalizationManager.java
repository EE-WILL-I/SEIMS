package ru.seims.localization;

import ru.seims.database.entitiy.User;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import ru.seims.application.servlet.ServletContext;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static final String BUNDLE_BASE_NAME = PropertyReader.getPropertyValue(PropertyType.SERVER,
            "bundle.basename");
    private static ResourceBundle bundle;
    private static final UTF8Control utf8Control = new UTF8Control();
    private static String savedLocaleParam = "";

    public static void setUserLocale(User user) {
        try {
            String localeParam = user.getLocale();
            if(localeParam.equals(savedLocaleParam) && bundle != null)
                return;
            savedLocaleParam = localeParam;
            String language = localeParam.split("\\.")[0];
            String country = localeParam.split("\\.")[1];
            String bundleBase = BUNDLE_BASE_NAME.isEmpty() ? "pagedata" : BUNDLE_BASE_NAME;
            bundle = ResourceBundle.getBundle("resourceBundles." + bundleBase, new Locale(language, country),
                    FileResourcesUtils.getClassLoader());
        } catch (NullPointerException e) {
            Logger.log(ServletContext.class, "User's locale not found", 3);
            bundle = ResourceBundle.getBundle("resourceBundles." + BUNDLE_BASE_NAME,
                    new Locale("ru", "RU"), FileResourcesUtils.getClassLoader(), utf8Control);
        }
    }

    public static String getString(String key) {
        if(bundle == null)
            setUserLocale(null);
        String value = bundle.getString(key);
        return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }
}
