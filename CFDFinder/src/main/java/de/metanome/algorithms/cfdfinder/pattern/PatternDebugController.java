package de.metanome.algorithms.cfdfinder.pattern;

public class PatternDebugController {
    private static boolean debugEnabled = false;
    private static long counter = 0;

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void resetCounter() {
        counter = 0;
    }

    public static long next() {
        return counter++;
    }

    public static long getCurrentCounter() {
        return counter;
    }
}
