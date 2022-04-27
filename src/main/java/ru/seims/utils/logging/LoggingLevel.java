package ru.seims.utils.logging;

    public class LoggingLevel {
        static final byte
                NONE = 0,
                INFO = 1,
                ERROR = 2,
                WARNING = 3,
                DEBUG = 4;

        public static String getStringValue(int level) {
            switch (level) {
                case 0:
                    return "NONE";
                case 1:
                    return "INFO";
                case 2:
                    return "ERROR";
                case 3:
                    return "WARNING";
                case 4:
                    return "DEBUG";
            }
            return "NONE";
        }
    }
