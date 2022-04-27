package ru.seims.utils.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONReader {
    private static final JSONParser jsonParser = new JSONParser();

    public static String getArgumentValue(String argument, String json) {
        if (argument == null || json == null)
            return "";
        if (argument.isEmpty() || json.isEmpty())
            return "";
        try {
            JSONObject data = (JSONObject) jsonParser.parse(json);
            String value = (String) data.get(argument);
            if (value != null && !value.isEmpty())
                return value;
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}