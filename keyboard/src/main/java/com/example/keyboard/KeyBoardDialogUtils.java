package com.example.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.InputType;
import android.text.Selection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * 键盘工具类
 */
public class KeyBoardDialogUtils implements View.OnClickListener {
    protected View view;
    protected Dialog popWindow;
    protected Activity mContext;
    private KhKeyboardView keyboardViw;

    /**
     * 和键盘绑定的 EditText
     */
    private EditText mBoardEt;

    public KeyBoardDialogUtils(Activity context, EditText et) {
        try {
            this.mContext = context;

            this.mBoardEt = et;

            if (popWindow == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.keyboard_key_board_popu, null);
                popWindow = new Dialog(mContext, R.style.keyboard_popupAnimation);
                view.findViewById(R.id.keyboard_finish).setOnClickListener(this);
                view.findViewById(R.id.keyboard_back_hide).setOnClickListener(this);
            }
            popWindow.setContentView(view);
            popWindow.setCanceledOnTouchOutside(true);
            Window mWindow = popWindow.getWindow();
            if (mWindow == null) return;
            mWindow.setWindowAnimations(R.style.keyboard_popupAnimation);
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mWindow.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
            mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mBoardEt != null && mBoardEt.isFocused()) {
                        mBoardEt.clearFocus();
                    }
                }
            });
            initView();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        try {
            if (keyboardViw == null) {
                keyboardViw = new KhKeyboardView(mContext, view);
            }

            mBoardEt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int inputType = mBoardEt.getInputType();
                    mBoardEt.setInputType(inputType);
                    //设定光标位置
                    Selection.setSelection(mBoardEt.getText(), mBoardEt.getText().length());
                    showKeyBoard(mBoardEt);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
        // 如果软键盘已经显示，则隐藏
        InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     *
     * @param editText
     */
    public void showKeyBoard(final EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        hideSystemSoftKeyboard(editText);
        popWindow.show();
        keyboardViw.showKeyboard(editText);
    }

    /**
     * 隐藏键盘
     */
    public void dismissKeyBoard() {
        keyboardViw.hideKeyboard();
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            int i = v.getId();
            if (i == R.id.keyboard_finish) {
                //完成按键
                dismissKeyBoard();

            } else if (i == R.id.keyboard_back_hide) {
                dismissKeyBoard();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}









