package com.example.httpdemo;

import com.example.keyboard3.IEditText;
import com.example.keyboard3.IKeyboardView;

public class KeyBoardActivity2 extends BaseActivity{

    private IEditText edit1;
    private IEditText edit2;
    private IKeyboardView keyboardView;

    @Override
    protected int getLayoutId() {
        return R.layout.key_board_activity2;
    }

    @Override
    protected void initView() {
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2);
        keyboardView = findViewById(R.id.keyboardview);

        //默认绑定一个
        keyboardView.setEditText(edit1);
        edit1.setmIKeyboardView(keyboardView);
        edit2.setmIKeyboardView(keyboardView);
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
