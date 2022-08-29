package ru.seims.utils.logging;

import java.time.LocalDateTime;

public class Logger {
    public static byte loggingLevel = LoggingLevel.WARNING;

    public static void log(Object sender, Object data, int level) {
        if(data == null || sender == null)
            return;
        if(level == 0 || level > loggingLevel)
            return;
        String senderName;
        if(sender.getClass().getSimpleName().equals("Class"))
            senderName = ((Class)sender).getName();
        else
            senderName = sender.getClass().getName();
        String logColor;
        switch (level) {
            case 2 : {
                logColor = "\033[0;31m";
                break;
            }
            case 3 : {
                logColor = "\033[0;33m";
                break;
            }
            default: logColor = "";
        }
        String reset = "\033[0m";
        String scolor = "\033[0;34m";
        System.out.println(LocalDateTime.now() + "  " + logColor + LoggingLevel.getStringValue(level) + reset + " " + scolor + senderName + reset + "  : " + data.toString());
    }

    public static void log(Object sender, Object data) { log(sender, data, 1); }
}
