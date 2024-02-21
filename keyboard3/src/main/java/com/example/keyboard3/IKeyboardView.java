package com.example.keyboard3;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class IKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {
    /**
     * 数字键盘
     */
    private Keyboard keyboardNumber;
    /**
     * 字母键盘
     */
    private Keyboard keyboardLetter;

    //绑定的输入框
    private EditText mEditText;

    /**
     * 是否发生键盘切换
     */
    private boolean changeLetter = false;

    /**
     * 是否为大写
     */
    private boolean isCapital = false;

    private List<Integer> noLists = new ArrayList<>();

    private int[] arrays = new int[]{Keyboard.KEYCODE_SHIFT, Keyboard.KEYCODE_MODE_CHANGE,
            Keyboard.KEYCODE_CANCEL, Keyboard.KEYCODE_DONE, Keyboard.KEYCODE_DELETE,
            Keyboard.KEYCODE_ALT, 32};

    public IKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        keyboardNumber = new Keyboard(getContext(), R.xml.keyboard_num);
        keyboardLetter = new Keyboard(getContext(), R.xml.keyboard_letter);
        //设置一些不需要预览的键位
        for (int i = 0; i < arrays.length; i++) {
            noLists.add(arrays[i]);
        }
        //默认使用数字键盘
        setKeyboard(keyboardNumber);
        //是否启用预览
        setPreviewEnabled(true);
        //键盘动作监听
        setOnKeyboardActionListener(this);
    }

    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }


    /**
     * 判断是否需要预览Key
     *
     * @param primaryCode keyCode
     */
    private void canShowPreview(int primaryCode) {
        if (noLists.contains(primaryCode)) {
            setPreviewEnabled(false);
        } else {
            setPreviewEnabled(true);
        }
    }

    @Override
    public void onPress(int primaryCode) {
        canShowPreview(primaryCode);
    }

    @Override
    public void onRelease(int primaryCode) {
    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        Editable editable = mEditText.getText();
        int start = mEditText.getSelectionStart();
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE://删除
                if (editable != null && editable.length() > 0 && start > 0) {
                    editable.delete(start - 1, start);
                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE://字母键盘与数字键盘切换
                changeKeyBoard(!changeLetter);
                break;
            case Keyboard.KEYCODE_DONE://完成
                changeKeyBoard(!changeLetter);
                break;
            case Keyboard.KEYCODE_SHIFT://大小写切换
                changeCapital(!isCapital);
                setKeyboard(keyboardLetter);
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    /**
     * 切换键盘类型
     */
    private void changeKeyBoard(boolean b) {
        changeLetter = b;
        if (b) {
            setKeyboard(keyboardLetter);
        } else {
            setKeyboard(keyboardNumber);
        }
    }

    /**
     * 切换键盘大小写
     */
    private void changeCapital(boolean b) {
        isCapital = b;
        List<Keyboard.Key> lists = keyboardLetter.getKeys();
        for (Keyboard.Key key : lists) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                } else {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            } else if (key.label != null && key.label.toString().equals("小写")) {
                key.label = "大写";
            } else if (key.label != null && key.label.toString().equals("大写")) {
                key.label = "小写";
            }
        }
    }

    /**
     * 判断此key是否正确，且存在 * * @param key * @return
     */
    private boolean isKey(String key) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (lowercase.indexOf(key.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

}
