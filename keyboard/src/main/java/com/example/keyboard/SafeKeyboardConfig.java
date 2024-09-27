package com.example.keyboard;


public class SafeKeyboardConfig {

    public static int DEFAULT_RES_ID_ICON_DEL = R.drawable.icon_del;
    public static int DEFAULT_RES_ID_ICON_LOW_LETTER = R.drawable.icon_capital_default;
    public static int DEFAULT_RES_ID_ICON_UP_LETTER = R.drawable.icon_capital_selected;
    public static int DEFAULT_RES_ID_ICON_UP_LETTER_LOCK = R.drawable.icon_capital_selected_lock;
    public static int DEFAULT_RES_ID_SPECIAL_KEY_BG = R.drawable.keyboard_change_trans;
    public static int DEFAULT_LAYOUT_ID_KEYBOARD_CONTAINER = R.layout.layout_keyboard_container;

    public static int DEFAULT_KEYBOARD_DONE_IMG_LAYOUT_BG_RES_ID = R.drawable.bg_keyboard_done_layout_trans;

    public static boolean DEFAULT_LETTER_WITH_NUMBER = false;
    public static boolean DEFAULT_ENABLE_VIBRATE = false;

    public static long DEFAULT_SHOW_TIME = 150;
    public static long DEFAULT_HIDE_TIME = 150;
    public static long DEFAULT_DELAY_TIME = 100;
    public static long DEFAULT_SHOW_DELAY = 100;
    public static long DEFAULT_HIDE_DELAY = 50;

    public static String DEFAULT_KEYBOARD_NUM_ONLY_KEY_NONE_TITLE = "SValence";

    public int iconResIdDel = DEFAULT_RES_ID_ICON_DEL;                              // 删除键资源id
    public int iconResIdLowLetter = DEFAULT_RES_ID_ICON_LOW_LETTER;                 // 小写字母 shift 键资源id
    public int iconResIdUpLetter = DEFAULT_RES_ID_ICON_UP_LETTER;                   // 大写字母 shift 键资源id
    public int iconResIdUpLetterLock = DEFAULT_RES_ID_ICON_UP_LETTER_LOCK;          // 大写字母 shift 键锁定 资源id
    public int keyboardSpecialKeyBgResId = DEFAULT_RES_ID_SPECIAL_KEY_BG;           // 特殊按键背景资源id

    public int keyboardContainerLayoutId = DEFAULT_LAYOUT_ID_KEYBOARD_CONTAINER;    // 键盘显示样式布局资源id

    // 纯数字键盘界面左下角多一个按键, 因为我不想删除这个按键, 所以这里加了这个属性, 用来定义并显示该按键显示的内容, 默认："SValence"
    public String keyboardNumOnlyKeyNoneTitle = DEFAULT_KEYBOARD_NUM_ONLY_KEY_NONE_TITLE;

    public long showDuration = DEFAULT_SHOW_TIME;
    public long hideDuration = DEFAULT_HIDE_TIME;
    public long showDelay = DEFAULT_DELAY_TIME;
    public long hideDelay = DEFAULT_SHOW_DELAY;
    public long delayDuration = DEFAULT_HIDE_DELAY;

    public final boolean letterWithNumber = false; // DEFAULT_LETTER_WITH_NUMBER;                   // 字母键盘是否包含数字
    public boolean enableVibrate = DEFAULT_ENABLE_VIBRATE;                          // 开启按键震动

    public static SafeKeyboardConfig getDefaultConfig() {
        return new SafeKeyboardConfig();
    }
}
