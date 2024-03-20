package com.example.keyboard;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 键盘类
 */
public class KhKeyboardView {
    private Activity mContext;
    private View parentView;
    /**
     * 字母键盘view
     */
    private KeyboardView mLetterView;
    /**
     * 数字键盘View
     */
    private KeyboardView mNumberView;
    /**
     * 数字键盘
     */
    private Keyboard mNumberKeyboard;
    /**
     * 字母键盘
     */
    private Keyboard mLetterKeyboard;
    /**
     * 符号键盘
     */
    private Keyboard mSymbolKeyboard;
    /**
     * 是否数字键盘
     */
    private boolean isNumber = true;
    /**
     * 是否大写
     */
    public static boolean isUpper = false;
    /**
     * 是否是符号
     */
    private boolean isSymbol = false;
    private EditText mEditText;
    private View headerView;

    /**
     * 设置字母键盘是否随机
     */
    private boolean isRandom;

    public void setEditText(EditText text) {
        mEditText = text;
    }

    public KhKeyboardView(Activity context, View view) {
        this(context, view, false);
    }

    public KhKeyboardView(Activity context, View view, boolean isRandom) {
        this.mContext = context;
        this.parentView = view;
        this.isRandom = isRandom;

        mNumberKeyboard = new Keyboard(mContext, R.xml.keyboard_numbers);
        mLetterKeyboard = new Keyboard(mContext, R.xml.keyboard_word);
        mSymbolKeyboard = new Keyboard(mContext, R.xml.keyboard_symbol);

        mNumberView = (KeyboardView) parentView.findViewById(R.id.keyboard_view);
        mLetterView = (KeyboardView) parentView.findViewById(R.id.keyboard_view_2);

        //初始化数字键盘
        mNumberView.setKeyboard(mNumberKeyboard);
        mNumberView.setEnabled(true);
        mNumberView.setPreviewEnabled(false);
        mNumberView.setOnKeyboardActionListener(listener);

        //初始化字母键盘
        mLetterView.setKeyboard(mLetterKeyboard);
        mLetterView.setEnabled(true);
        //设置是否显示浮框
        mLetterView.setPreviewEnabled(true);
        mLetterView.setOnKeyboardActionListener(listener);

        headerView = parentView.findViewById(R.id.keyboard_header);

    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
            Log.d("primaryCode", "onPress--" + primaryCode);
            switch (primaryCode) {
                case Keyboard.KEYCODE_SHIFT:
                    List<Keyboard.Key> keyList = mLetterKeyboard.getKeys();
                    mLetterView.setPreviewEnabled(false);
                    break;
                case Keyboard.KEYCODE_DELETE:
                case Keyboard.KEYCODE_MODE_CHANGE:
                case 32:
                case 90001:
                    mLetterView.setPreviewEnabled(false);
                    break;
                default:
                    mLetterView.setPreviewEnabled(true);
                    break;
            }

        }

        @Override
        public void onRelease(int primaryCode) {
            Log.d("=====>", "onRelease--" + primaryCode);

//            if(primaryCode== -1){
//                if(isUpper){
//                    isUpper=false;
//                }else {
//                    isUpper=true;
//                }
//            }
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Log.d("=====>", "onKey--" + primaryCode);
            try {
                if (mEditText == null)
                    return;
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                    // 隐藏键盘
                    hideKeyboard();

                } else if (primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == KeyboardUtil.DELETE) {
                    // 回退键,删除字符
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }

                } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                    // 大小写切换
                    changeKeyboard();
                    mLetterView.setKeyboard(mLetterKeyboard);

                } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                    // 数字与字母键盘互换
                    if (isNumber) {
                        showLetterView();
                        showLetterView2();
                    } else {
                        showNumberView();
                    }

                } else if (primaryCode == KeyboardUtil.SYMBOL_WORD) {
                    //字母与符号切换
                    if (isSymbol) {
                        showLetterView2();
                    } else {
                        showSymbolView();
                    }

                } else {
                    // 输入键盘值
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onText(CharSequence text) {
            Log.d("=====>", "onText");
        }

        @Override
        public void swipeLeft() {
            Log.d("=====>", "swipeLeft");
        }

        @Override
        public void swipeRight() {
            Log.d("=====>", "swipeRight");
        }

        @Override
        public void swipeDown() {
            Log.d("=====>", "swipeDown");
        }

        @Override
        public void swipeUp() {
            Log.d("=====>", "swipeUp");
        }
    };

    /**
     * 设置随机键盘
     */
    private Keyboard setRandomNumberKeyboard(Keyboard keyboard, boolean isRandom) {
        if (isRandom) {
            ArrayList<Character> keyCodes = new ArrayList<>();
            // 这里以数字键盘为例  获取到键盘原有的按键 随机排列 然后在重新赋值
            for (Keyboard.Key item : keyboard.getKeys()) {
                int code = item.codes[0];
                if (isRandomKey(code)) {
                    keyCodes.add((char) code);
                }
            }
            // 随机排序数字
            Collections.shuffle(keyCodes);

            // 遍历所有的按键
            List<Keyboard.Key> keys = keyboard.getKeys();
            int index = 0;
            for (Keyboard.Key key : keys) {
                int code = key.codes[0];
                // 如果按键是数字 去除左下角和右下角的非数字键
                if (isRandomKey(code)) {
                    char keyCode = keyCodes.get(index++);
                    key.codes[0] = keyCode;
                    key.label = Character.toString(keyCode);
                }
            }
        }
        return keyboard;
    }

    /**
     * @param code
     * @return 是否过滤随机键
     */
    boolean isRandomKey(int code) {
        return code != Keyboard.KEYCODE_DELETE && code != Keyboard.KEYCODE_MODE_CHANGE
                && code != KeyboardUtil.DELETE;
    }


    /**
     * 字母-符号,显示字母
     */
    private void showLetterView2() {
        if (mLetterView != null) {
            isSymbol = false;
            mLetterView.setKeyboard(mLetterKeyboard);
        }
    }

    /**
     * 字母-符号,显示符号
     */
    private void showSymbolView() {
        try {
            if (mLetterKeyboard != null) {
                isSymbol = true;
                mLetterView.setKeyboard(mSymbolKeyboard);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 数字-字母,显示字母键盘
     */
    private void showLetterView() {
        try {
            if (mLetterView != null && mNumberView != null) {
                isNumber = false;
                mLetterView.setVisibility(View.VISIBLE);
                mNumberView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 数字-字母, 显示数字键盘
     */
    private void showNumberView() {
        try {
            if (mLetterView != null && mNumberView != null) {
                isNumber = true;
                mLetterView.setVisibility(View.GONE);
                mNumberView.setVisibility(View.VISIBLE);
                mNumberView.setKeyboard(setRandomNumberKeyboard(mNumberKeyboard, isRandom));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 切换大小写
     */
    private void changeKeyboard() {
        List<Keyboard.Key> keyList = mLetterKeyboard.getKeys();
        if (isUpper) {
            // 大写切换小写
            isUpper = false;
            for (Keyboard.Key key : keyList) {
                Drawable icon = key.icon;

                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {
            // 小写切换成大写
            isUpper = true;
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    /**
     * 判断是否是字母
     */
    private boolean isLetter(String str) {
        String wordStr = "abcdefghijklmnopqrstuvwxyz";
        return wordStr.contains(str.toLowerCase());
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        try {
            int visibility = mLetterView.getVisibility();
            if (visibility == View.VISIBLE) {
                headerView.setVisibility(View.GONE);
                mLetterView.setVisibility(View.GONE);
            }
            visibility = mNumberView.getVisibility();
            if (visibility == View.VISIBLE) {
                headerView.setVisibility(View.GONE);
                mNumberView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示键盘
     *
     * @param editText
     */
    public void showKeyboard(EditText editText) {
        try {
            this.mEditText = editText;
            int visibility = 0;
            int inputText = mEditText.getInputType();
            headerView.setVisibility(View.VISIBLE);
            switch (inputText) {
                case InputType.TYPE_CLASS_NUMBER:
                case InputType.TYPE_CLASS_PHONE:
                case InputType.TYPE_NUMBER_FLAG_DECIMAL:
                    showNumberView();
                    break;
                default:
                    showLetterView();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
