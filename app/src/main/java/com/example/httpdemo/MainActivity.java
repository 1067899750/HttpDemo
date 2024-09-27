package com.example.httpdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.httpdemo.untils.HttpUtil;
import com.example.keyboard.KhKeyboardView;
import com.example.keyboard.SafeKeyboardConfig;
import com.example.mylibrary.HttpHelper;
import com.franmontiel.persistentcookiejar.persistence.SerializableCookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String url = HttpUtil.GET_LOGIN1;
    private String url2 = HttpUtil.GIT_USER_MERCHANT;


    private String password = "e10adc3949ba59abbe56e057f20f883e";
    private String mobile = "18503970627";
    private String cid = "09164b5a280eda07839aabbd9e3c5961";

    private KhKeyboardView safeKeyboard;

    private EditText mEditText;

//    private String password = "e10adc3949ba59abbe56e057f20f883e";
//    private String mobile = "18503970627";
//    private String cid = "591b8d3c615160c2890bdb1e8fa4415d";

    @Override
    protected void initView() {
        //测试提交数据
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.other_btn).setOnClickListener(this);
        findViewById(R.id.other_keyboard).setOnClickListener(this);

        LinearLayout keyboardContainer = findViewById(R.id.safe_keyboard_place);
        mEditText = findViewById(R.id.key_board);
        View rootView = findViewById(R.id.main_root);
        View scrollLayout = findViewById(R.id.scroll_layout);
        SafeKeyboardConfig config = SafeKeyboardConfig.getDefaultConfig();
        safeKeyboard = new KhKeyboardView(getApplicationContext(), keyboardContainer, rootView, scrollLayout, config);
        safeKeyboard.setVibrateEnable(true);
        safeKeyboard.putEditText(mEditText);
        safeKeyboard.putRandomEdit(findViewById(R.id.key_board2));

        EditText safeEdit3 = findViewById(R.id.safeEditText3);
        safeKeyboard.putRandomEdit(safeEdit3);
        safeKeyboard.putEditText2IdCardType(safeEdit3);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                HashMap<String, String> loginMap = new HashMap<>();
                loginMap.put("password", password);
                loginMap.put("mobile", mobile);
                loginMap.put("cid", cid);
                loginMap.put("loginType", "1");
                HttpHelper.getInstance().get(url, loginMap, this);
                break;

            case R.id.other_btn:
                HashMap<String, String> otherMap = new HashMap<>();
                HttpHelper.getInstance().post(url2, otherMap, this);
                break;
            case R.id.other_keyboard:
                startActivity(new Intent(this, KeyBoardActivity2.class));
                break;

        }
    }

    @Override
    public void onSuccess(String tag, String message) {
        if (tag.equals("login")) {
            System.out.println("--->" + Thread.currentThread());
            toast(message);
        } else if (tag.equals("billHomePage")) {
            System.out.println("--->" + Thread.currentThread());
            toast(message);
        }
    }

    @Override
    public void onFailed(String message) {
        System.out.println("--->" + Thread.currentThread());
        toast(message);
    }

    @Override
    public void onError(int code, String message) {
        System.out.println("--->" + Thread.currentThread());
        toast(code + ":" + message);
    }


    public void toast(String string) {
        ArrayList<String> cookies = new ArrayList<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            cookies.add(new SerializableCookie().decode((String) map.get(key)).value());
        }
        ((TextView) findViewById(R.id.text_tv)).setText(string);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (safeKeyboard != null) {
            safeKeyboard.release();
            safeKeyboard = null;
        }
        super.onDestroy();
    }
}



























