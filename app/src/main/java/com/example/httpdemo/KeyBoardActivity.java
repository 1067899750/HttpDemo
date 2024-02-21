package com.example.httpdemo;

import android.widget.Toast;

import com.example.keyboard2.Keyboard;
import com.example.keyboard2.PayEditText;

public class KeyBoardActivity extends BaseActivity {
    private static final String[] KEY = new String[] {
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "<<", "0", "完成"
    };

    private PayEditText payEditText;
    private Keyboard keyboard;

    @Override
    protected int getLayoutId() {
        return R.layout.key_board_activity;
    }


    @Override
    protected void initView() {
        payEditText = findViewById(R.id.payEditText_pay);
        keyboard = findViewById(R.id.keyboardView_pay);

        //设置键盘
        keyboard.setKeyboardKeys(KEY);

        initEvent();
    }


    private void initEvent() {
        keyboard.setOnClickKeyboardListener(new Keyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (position < 11 && position != 9) {
                    payEditText.add(value);
                } else if (position == 9) {
                    payEditText.remove();
                }else if (position == 11) {
                    //当点击完成的时候，也可以通过payEditText.getText()获取密码，此时不应该注册OnInputFinishedListener接口
                    Toast.makeText(getApplication(), "您的密码是：" + payEditText.getText(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText.setOnInputFinishedListener(new PayEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                Toast.makeText(getApplication(), "您的密码是：" + password, Toast.LENGTH_SHORT).show();
            }
        });
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
