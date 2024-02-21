package com.example.httpdemo;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.keyboard2.PopupKeyboardUtil;

public class KeyBoardActivity extends BaseActivity {
    EditText edittext1;
    PopupKeyboardUtil smallKeyboardUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.key_board_activity;
    }


    @Override
    protected void initView() {
        smallKeyboardUtil = new PopupKeyboardUtil(this);

        edittext1 = findViewById(R.id.edittext1);

        smallKeyboardUtil.attachTo(edittext1, false);


        edittext1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                smallKeyboardUtil.showSoftKeyboard();
                return false;
            }
        });

        initEvent();
    }

    public void onClickView(View view) {
        if (view.getId() == R.id.btn1) {
            smallKeyboardUtil.showSoftKeyboard();
        }

        if (view.getId() == R.id.btn2) {
            smallKeyboardUtil.hideSoftKeyboard();
        }
    }


    private void initEvent() {

    }

    @Override
    public void onSuccess(String tag, String message) {

    }

    @Override
    public void onFailed(String message) {

    }

    @Override
    public void onError(int code, String message) {

    }


}
