package com.example.httpdemo;

import android.content.Context;
import android.content.SharedPreferences;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.httpdemo.untils.HttpUtil;
import com.example.keyboard.KeyBoardDialogUtils;
import com.example.mylibrary.HttpHelper;
import com.franmontiel.persistentcookiejar.persistence.SerializableCookie;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String url = HttpUtil.GET_LOGIN1;
    private String url2 = HttpUtil.GIT_USER_MERCHANT;


    private String password = "e10adc3949ba59abbe56e057f20f883e";
    private String mobile = "18503970627";
    private String cid = "09164b5a280eda07839aabbd9e3c5961";

    private KeyBoardDialogUtils keyBoardDialogUtils;

    private EditText mEditText;

//    private String password = "e10adc3949ba59abbe56e057f20f883e";
//    private String mobile = "18503970627";
//    private String cid = "591b8d3c615160c2890bdb1e8fa4415d";

    @Override
    protected void initView() {
        keyBoardDialogUtils = new KeyBoardDialogUtils(this);

        //测试提交数据
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.other_btn).setOnClickListener(this);
        mEditText = findViewById(R.id.key_board);
        mEditText.setOnClickListener(this);


        //        HttpHelper.getInstance().get(url, params, new HttpCallback<Login>() {
//            @Override
//            public void onFailed(String message) {
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//
//            }
//
//            @Override
//            public void onSuccess(Login login, String tag) {
//
//            }
//        });
        Observable.interval(500, TimeUnit.MINUTES).subscribe();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1 next");
                emitter.onNext("2 next");
                emitter.onComplete();
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s + "Rxjava";
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        String s1 = s;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        String s1 = throwable.getMessage();
                    }
                });


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

            case R.id.key_board:
                keyBoardDialogUtils.show(mEditText);
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


}



























