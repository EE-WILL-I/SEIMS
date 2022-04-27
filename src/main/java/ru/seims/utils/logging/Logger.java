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
        System.out.println(LocalDateTime.now() + "  " + LoggingLevel.getStringValue(level) + " " + senderName + "  : " + data.toString());
    }

    public static void log(Object sender, Object data) { log(sender, data, 1); }
}
