package com.example.keyboard;


public class SafeKeyboardConfig {
    public static long DEFAULT_SHOW_TIME = 150;
    public static long DEFAULT_HIDE_TIME = 150;
    public static long DEFAULT_DELAY_TIME = 100;
    public static long DEFAULT_SHOW_DELAY = 100;

    public long showDuration = DEFAULT_SHOW_TIME;
    public long hideDuration = DEFAULT_HIDE_TIME;
    public long showDelay = DEFAULT_DELAY_TIME;
    public long hideDelay = DEFAULT_SHOW_DELAY;

    public static SafeKeyboardConfig getDefaultConfig() {
        return new SafeKeyboardConfig();
    }
}
