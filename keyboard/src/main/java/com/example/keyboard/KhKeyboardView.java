package com.example.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.method.BaseKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 键盘试图管理类
 */
public class KhKeyboardView {
    private Context mContext;

    //电话
    public static final String PHONE_TYPE = "phoneType";

    //身份证
    public static final String CARD_TYPE = "cardType";

    // 金额
    public static final String MONEY_TYPE = "moneyType";

    //其他
    public static final String OTHER_TYPE = "otherType";

    @StringDef(value = {PHONE_TYPE, CARD_TYPE, OTHER_TYPE, MONEY_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NumberType {
    }

    /**
     * 保存键盘类型
     */
    private HashMap<String, String> mBoardTypeMap;

    private static final String TAG = "KhKeyboardView";
    private final SafeKeyboardConfig keyboardConfig;

    /**
     * 是否大写，true:大写，false:小写
     */
    public static boolean isUpper = false;

    /**
     * 键盘点击的key值
     */
    public static int boardKey = -19991888;

    /**
     * 键盘是否点击
     */
    public static boolean isClickBoard = false;


    /**
     * true: #+=选中
     */
    public static boolean isSpecialOneBoard = true;

    /**
     * true:₵₱§:选中
     */
    public static boolean isSpecialTwoBoard = false;

    /**
     * 键盘容器
     */
    private final LinearLayout keyboardOuterContainer;

    //自定义键盘的容器View
    private View keyContainer;

    //键盘的View
    private CustomKeyboardView keyboardView;

    /**
     * 符号 + 数字键盘
     */
    private Keyboard mSymbolOneKeyboard;

    /**
     * 符号键盘
     */
    private Keyboard mSymbolTwoKeyboard;

    /**
     * 符号键盘
     */
    private Keyboard mSymbolThreeKeyboard;

    /**
     * 电话键盘
     */
    private Keyboard mPhoneKeyboard;

    /**
     * 金额
     */
    private Keyboard mMoneyKeyboard;

    /**
     * 生份证键盘
     */
    private Keyboard mCardKeyboard;

    /**
     * 字母键盘
     */
    private Keyboard mLetterKeyboard;

    private boolean isShowStart = false;
    private boolean isHideStart = false;

    // SafeKeyboard 键盘类型
    private BoardType keyboardType = BoardType.DEFAULT_TYPE;


    private final Handler safeHandler = new Handler(Looper.getMainLooper());

    /**
     * 只起到延时开始显示的作用
     */
    private Runnable showRun;

    private Runnable hideRun;

    private Runnable hideEnd;

    private Runnable showEnd;

    private TranslateAnimation showAnimation;
    private TranslateAnimation hideAnimation;
    private EditText mCurrentEditText;

    // 保存 otherType 缓存键盘
    private HashMap<String, BoardType> mEditLastKeyboardTypeMap;

    private HashMap<String, EditText> mEditMap;
    private Set<String> mVibrateEditTagSet;
    private View.OnTouchListener onEditTextTouchListener;
    private final View rootView;
    private final View mScrollLayout;
    private ViewTreeObserver.OnGlobalFocusChangeListener onGlobalFocusChangeListener;
    private ViewTreeObserver treeObserver;
    private ViewPoint downPoint;
    private ViewPoint upPoint;
    private int mScreenHeight;
    // 往上移动的距离, 为负值
    private float toBackSize;
    private int[] originalScrollPosInScr;
    private int[] originalScrollPosInPar;

    /**
     * 设置字母键盘是否随机 (暂时不需要)
     */
    private boolean isRandom = false;

    /**
     * SafeKeyboard 构造方法, 传入必要的参数, 已精简传入参数
     * 使用 SafeKeyboard 布局为默认布局 layout_keyboard_container
     *
     * @param mContext               上下文 Context
     * @param keyboardOuterContainer 使用 SafeKeyboard 的界面上显示 SafeKeyboard 的 容器 View, 这里写死只能是 LinearLayout
     * @param rootView               含有使用了 SafeKeyboard 的 EditText 的界面根布局 View
     *                               传入目的是为了获取 rootView 下所有的 EditText 以便对焦点事件进行监测和处理
     * @param scrollLayout           目标 EditText 父布局 View
     *                               ( 多个 EditText 共用 SafeKeyboard 但其直接父布局不是同一个 View 时, 传入公共父布局)
     *                               传入目的是：当 EditText 需要被顶起的时候, 顶起该布局, 以达到输入时可以显示已输入内容的功能
     *                               注意, 可以是 EditText 本身, 不过需要传入 View 类型的 EditText
     */
    public KhKeyboardView(Context mContext, LinearLayout keyboardOuterContainer, @NonNull View rootView, @NonNull View scrollLayout) {
        this.mContext = mContext;
        this.keyboardConfig = SafeKeyboardConfig.getDefaultConfig();
        this.keyboardOuterContainer = keyboardOuterContainer;
        this.rootView = rootView;
        this.mScrollLayout = scrollLayout;

        initRunnable();
        initData();
        initKeyboardAndFindView();
        setListeners();
        initAnimation();
    }

    private void initRunnable() {
        showRun = new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        };

        hideRun = new Runnable() {
            @Override
            public void run() {
                hideKeyboard();
            }
        };

        hideEnd = new Runnable() {
            @Override
            public void run() {
                doHideEnd();
            }
        };
        showEnd = new Runnable() {
            @Override
            public void run() {
                doShowEnd();
            }
        };
    }

    private void initData() {
        toBackSize = 0;
        downPoint = new ViewPoint();
        upPoint = new ViewPoint();
        mEditMap = new HashMap<>();
        mBoardTypeMap = new HashMap<>();
        mVibrateEditTagSet = new HashSet<>();
        mEditLastKeyboardTypeMap = new HashMap<>();
        originalScrollPosInScr = new int[]{0, 0, 0, 0};
        originalScrollPosInPar = new int[]{0, 0, 0, 0};

        // 获取 WindowManager 实例, 得到屏幕的操作权
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            // 给 metrics 赋值
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            // 设备屏幕的宽度,高度变量
            mScreenHeight = metrics.heightPixels;
        }
    }

    /**
     * 键盘实例数据
     */
    private void initKeyboardAndFindView() {
        // 把键盘布局添加到 LinearLayout 中
        keyContainer = LayoutInflater.from(mContext).inflate(R.layout.layout_keyboard_container,
                keyboardOuterContainer, true);
        keyboardView = keyContainer.findViewById(R.id.safeKeyboardViewId);
        keyContainer.setVisibility(View.GONE);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);

        //实例化数字键盘
        mSymbolOneKeyboard = new Keyboard(mContext, R.xml.keyboard_symbol_one);
        mSymbolTwoKeyboard = new Keyboard(mContext, R.xml.keyboard_symbol_two);
        mSymbolThreeKeyboard = new Keyboard(mContext, R.xml.keyboard_symbol_three);

        // 纯数字键盘
        mPhoneKeyboard = new Keyboard(mContext, R.xml.keyborad_phone_numbers);

        mMoneyKeyboard = new Keyboard(mContext, R.xml.keyboard_money);

        //实例化字母键盘
        mLetterKeyboard = new Keyboard(mContext, R.xml.keyboard_word);

        //实例化 IdCard(中国身份证) 键盘
        mCardKeyboard = new Keyboard(mContext, R.xml.keyborad_card_numbers);
    }

    /**
     * 设置键盘
     */
    public void putEditText(EditText editText) {
        putEditText(editText, KhKeyboardView.OTHER_TYPE);
    }

    /**
     * 设置键盘
     *
     * @param editText
     * @param type     键盘类型
     */
    @SuppressLint("ClickableViewAccessibility")
    public void putEditText(EditText editText, @NumberType String type) {
        if (mEditMap == null) mEditMap = new HashMap<>();
        if (mBoardTypeMap == null) mBoardTypeMap = new HashMap<>();
        editText.setTag(Integer.toHexString(System.identityHashCode(editText)));
        mEditMap.put(editText.getTag().toString(), editText);
        mBoardTypeMap.put(editText.getTag().toString(), type);
        // 移除 digits 属性
        editText.setKeyListener(new BaseKeyListener() {
            @Override
            public int getInputType() {
                return editText.getInputType();
            }
        });
        editText.setOnTouchListener(onEditTextTouchListener);
        disableCopyAndPaste(editText);
    }

    /**
     * 监听器
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        keyboardView.setOnKeyboardActionListener(listener);

        // 关闭键盘
        keyContainer.findViewById(R.id.keyboard_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKeyboardShown()) {
                    safeHandler.removeCallbacks(hideRun);
                    safeHandler.removeCallbacks(showRun);
                    safeHandler.postDelayed(hideRun, keyboardConfig.hideDelay);
                }
            }
        });

        if (rootView != null) {
            treeObserver = rootView.getViewTreeObserver();
            onGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    if (newFocus instanceof EditText) {
                        EditText newEdit = (EditText) newFocus;
                        forceChangeEditImeOptNextToDone(newEdit);
                    }
                    if (oldFocus instanceof EditText) {
                        // 上一个获得焦点的为 EditText
                        EditText oldEdit = (EditText) oldFocus;
                        if (isEditMapContainThisEdit(oldEdit)) {
                            // 前 EditText 使用了 SafeKeyboard
                            // 新获取焦点的是 EditText
                            if (newFocus instanceof EditText) {
                                EditText newEdit = (EditText) newFocus;
                                if (isEditMapContainThisEdit(newEdit)) {
                                    // 该 EditText 也使用了 SafeKeyboard
                                    Log.i(TAG, "Safe --> Safe, 开始检查是否需要手动 show");
                                    keyboardPreShow(newEdit);
                                } else {
                                    // 该 EditText 没有使用 SafeKeyboard, 则隐藏 SafeKeyboard
                                    Log.i(TAG, "Safe --> 系统, 开始检查是否需要手动 hide");

                                    // 说明: 如果 EditText 外被 ScrollView 包裹, 切换成系统输入法的时候, SafeKeyboard 会被异常顶起
                                    // 需要在 Activity 的声明中增加 android:windowSoftInputMode="stateAlwaysHidden|adjustPan" 语句
                                    keyboardPreHide();
                                }
                            } else {
                                // 新获取焦点的不是 EditText, 则隐藏 SafeKeyboard
                                Log.i(TAG, "Safe --> 其他, 开始检查是否需要手动 hide");
                                keyboardPreHide();
                            }
                        } else {
                            // 前 EditText 没有使用 SafeKeyboard
                            // 新获取焦点的是 EditText
                            if (newFocus instanceof EditText) {
                                EditText newEdit = (EditText) newFocus;
                                // 该 EditText 使用了 SafeKeyboard, 则显示
                                if (isEditMapContainThisEdit(newEdit)) {
                                    Log.i(TAG, "系统 --> Safe, 开始检查是否需要手动 show");
                                    keyboardPreShow(newEdit);
                                } else {
                                    Log.i(TAG, "系统 --> 系统, 开始检查是否需要手动 hide");
                                    keyboardPreHide();
                                }
                            } else {
                                // ... 否则不需要管理此次事件, 但是为保险起见, 可以隐藏一次 SafeKeyboard, 当然隐藏前需要判断是否已显示
                                Log.i(TAG, "系统 --> 其他, 开始检查是否需要手动 hide");
                                keyboardPreHide();
                            }
                        }
                    } else {
                        // 新获取焦点的是 EditText
                        if (newFocus instanceof EditText) {
                            EditText newEdit = (EditText) newFocus;
                            // 该 EditText 使用了 SafeKeyboard, 则显示
                            if (isEditMapContainThisEdit(newEdit)) {
                                Log.i(TAG, "其他 --> Safe, 开始检查是否需要手动 show");
                                keyboardPreShow(newEdit);
                            } else {
                                Log.i(TAG, "其他 --> 系统, 开始检查是否需要手动 hide");
                                keyboardPreHide();
                            }
                        } else {
                            // ... 否则不需要管理此次事件, 但是为保险起见, 可以隐藏一次 SafeKeyboard, 当然隐藏前需要判断是否已显示
                            Log.i(TAG, "其他 --> 其他, 开始检查是否需要手动 hide");
                            keyboardPreHide();
                        }
                    }
                }
            };
            treeObserver.addOnGlobalFocusChangeListener(onGlobalFocusChangeListener);
        } else {
            Log.e(TAG, "Root View is null!");
        }

        onEditTextTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v instanceof EditText) {
                    EditText mEditText = (EditText) v;
                    // 隐藏系统键盘关键代码
                    hideSystemKeyBoard(mEditText);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downPoint.setCoo_x((int) event.getRawX());
                        downPoint.setCoo_y((int) event.getRawY());
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        upPoint.setCoo_x((int) event.getRawX());
                        upPoint.setCoo_y((int) event.getRawY());
                        if (isTouchConsiderClick(downPoint, upPoint, mEditText) && mEditText.hasFocus()) {
                            if (mCurrentEditText == mEditText && isShow()) {
                                return false;
                            }
                            // 显示自定义键盘
                            keyboardPreShow(mEditText);
                        }
                        downPoint.clearPoint();
                        upPoint.clearPoint();
                    }
                }
                return false;
            }
        };
    }

    private void forceChangeEditImeOptNextToDone(EditText newEdit) {
        // 强制非使用 SafeKeyboard 的 EditText 且下一项、完成、搜索等被设置为 下一项的 设置为完成,
        // 否则可能会导致点下一项时, SafeKeyboard 和 系统键盘 同时出现
        // Log.w(TAG, "ime: " + newEdit.getImeOptions());
        if (!isEditMapContainThisEdit(newEdit)
                && (newEdit.getImeOptions() == EditorInfo.IME_ACTION_NEXT
                || newEdit.getImeOptions() == EditorInfo.IME_ACTION_UNSPECIFIED
                || newEdit.getImeOptions() == EditorInfo.IME_ACTION_NONE)) {
            newEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
            Object tagObj = newEdit.getTag();
            if (tagObj != null) {
                // 其实这里一般不会进入, 除非调用者 主动设置了 tag
                Log.w(TAG, "Tag 为: " + newEdit.getTag().toString() + " 的 EditText 未使用 SafeKeyboard," +
                        " ImeOptions 属性为 IME_ACTION_NEXT-IME_ACTION_UNSPECIFIED-IME_ACTION_NONE 三者之一, " +
                        "为避免软键盘显示出错, 现强制设置为 IME_ACTION_DONE--完成");
            } else {
                String msg = "这个 EditText 未使用 SafeKeyboard, 因此未设置 Tag! 此 EditText id 为: " +
                        newEdit.getId() + ", ImeOptions 属性为 IME_ACTION_NEXT-" +
                        "IME_ACTION_UNSPECIFIED-IME_ACTION_NONE 三者之一, 为避免软键盘显示出错, 现强制设置为 IME_ACTION_DONE--完成";
                Log.w(TAG, msg);
            }
        }
    }

    /**
     * 动画
     */
    private void initAnimation() {
        showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        showAnimation.setDuration(keyboardConfig.showDuration);
        hideAnimation.setDuration(keyboardConfig.hideDuration);

        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShowStart = true;
                // 在这里设置可见, 会出现第一次显示键盘时直接闪现出来, 没有动画效果, 后面正常
                // keyContainer.setVisibility(View.VISIBLE);
                // 动画持续时间 SHOW_TIME 结束后, 不管什么操作, 都需要执行, 把 isShowStart 值设为 false; 否则
                // 如果 onAnimationEnd 因为某些原因没有执行, 会影响下一次使用
                safeHandler.removeCallbacks(showEnd);
                safeHandler.postDelayed(showEnd, keyboardConfig.showDuration);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowStart = false;
                keyContainer.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isHideStart = true;
                // 动画持续时间 HIDE_TIME 结束后, 不管什么操作, 都需要执行, 把 isHideStart 值设为 false; 否则
                // 如果 onAnimationEnd 因为某些原因没有执行, 会影响下一次使用
                safeHandler.removeCallbacks(hideEnd);
                safeHandler.postDelayed(hideEnd, keyboardConfig.hideDuration);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                safeHandler.removeCallbacks(hideEnd);
                if (isHideStart) {
                    // isHideStart 未被置为初试状态, 说明还没有执行 hideEnd 内容, 这里手动执行一下
                    doHideEnd();
                }
                // 说明已经被执行了不需要在执行一遍了, 下面就什么都不用管了
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 用来计算按下和抬起时的两点位置的关系, 是否可以将此次 Touch 事件 看作 Click 事件
     * 两点各自的 x/y 轴距离不超过 10, 且两点中心点在目标 EditText 上 时, 返回 true, 否则 false
     *
     * @param down      按下时的位置点
     * @param up        抬起时的位置点
     * @param mEditText 目标 EditText
     * @return 是否考虑此次为点击事件
     */
    private boolean isTouchConsiderClick(ViewPoint down, ViewPoint up, EditText mEditText) {
        boolean flag = false;
        if (Math.abs(down.getCoo_x() - up.getCoo_x()) < 10 && Math.abs(down.getCoo_y() - up.getCoo_y()) < 10) {
            int[] position = new int[2];
            mEditText.getLocationOnScreen(position);
            int width = mEditText.getWidth();
            int height = mEditText.getHeight();
            int x = (down.getCoo_x() + up.getCoo_x()) / 2;
            int y = (down.getCoo_y() + up.getCoo_y()) / 2;
            if (position[0] + width >= x && position[1] + height >= y)
                flag = true;
        }

        return flag;
    }

    /**
     * 隐藏自定义键盘
     */
    private void keyboardPreHide() {
        safeHandler.removeCallbacks(hideRun);
        safeHandler.removeCallbacks(showRun);
        getOriginalScrollLayoutPos();
        if (stillNeedOptManually(false)) {
            safeHandler.postDelayed(hideRun, keyboardConfig.hideDelay);
        }
    }

    /**
     * 显示自定义键盘
     *
     * @param mEditText
     */
    private void keyboardPreShow(final EditText mEditText) {
        safeHandler.removeCallbacks(showRun);
        safeHandler.removeCallbacks(hideRun);
        getOriginalScrollLayoutPos();
        if (stillNeedOptManually(true)) {
            setCurrentEditText(mEditText);
            safeHandler.postDelayed(showRun, keyboardConfig.showDelay);
        } else {
            // 说明不需要再手动显示, 只需要切换键盘模式即可 (甚至不用切换)
            // 这里需要检查当前 EditText 的显示是否合理
            final long delay = doScrollLayoutBack(false, mEditText) ? keyboardConfig.hideDuration + 50 : 0;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 如果已经显示了, 那么切换键盘即可
                    setCurrentEditText(mEditText);
                    setKeyboard(getKeyboardByInputType());
                }
            }, delay);
        }
    }


    /**
     * 更新 mScrollLayout 原始位置, 且只获取一次
     */
    private void getOriginalScrollLayoutPos() {
        if (originalScrollPosInScr[0] == 0 && originalScrollPosInScr[1] == 0) {
            int[] pos = new int[]{0, 0};
            mScrollLayout.getLocationOnScreen(pos);
            originalScrollPosInScr[0] = pos[0];
            originalScrollPosInScr[1] = pos[1];
            originalScrollPosInScr[2] = pos[0] + mScrollLayout.getWidth();
            originalScrollPosInScr[3] = pos[1] + mScrollLayout.getHeight();
        }

        if (originalScrollPosInPar[0] == 0 && originalScrollPosInPar[1] == 0
                && originalScrollPosInPar[2] == 0 && originalScrollPosInPar[3] == 0) {
            originalScrollPosInPar[0] = mScrollLayout.getLeft();
            originalScrollPosInPar[1] = mScrollLayout.getTop();
            originalScrollPosInPar[2] = mScrollLayout.getRight();
            originalScrollPosInPar[3] = mScrollLayout.getBottom();
        }
    }

    /**
     * 设置键盘点击监听
     */
    private final KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onPress(int primaryCode) {
            keyboardView.setPreviewEnabled(false);
            isClickBoard = true;
            boardKey = primaryCode;
        }

        @Override
        public void onRelease(int primaryCode) {
            isClickBoard = false;
            boardKey = primaryCode;
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            try {
                if (mCurrentEditText == null)
                    return;
                Editable editable = mCurrentEditText.getText();
                int start = mCurrentEditText.getSelectionStart();
                int end = mCurrentEditText.getSelectionEnd();
                switch (primaryCode) {
                    case Keyboard.KEYCODE_CANCEL: {
                        // 隐藏键盘
                        safeHandler.removeCallbacks(hideRun);
                        safeHandler.removeCallbacks(showRun);
                        safeHandler.post(hideRun);
                        break;
                    }
                    case Keyboard.KEYCODE_DELETE:
                    case KeyboardUtil.DELETE: {
                        // 回退键,删除字符
                        if (editable != null && editable.length() > 0) {
                            if (start == end) { //光标开始和结束位置相同, 即没有选中内容
                                editable.delete(start - 1, start);
                            } else { //光标开始和结束位置不同, 即选中EditText中的内容
                                editable.delete(start, end);
                            }
                        }
                        break;
                    }
                    case Keyboard.KEYCODE_SHIFT: {
                        //  键盘内容才会变化(切换大小写)
                        keyboardType = BoardType.WORD_TYPE;
                        changeKeyboard();
                        break;
                    }
                    case KeyboardUtil.NUMBER:
                    case KeyboardUtil.SYMBOL_ONE: {
                        // 数字 + 符号
                        if (isSpecialTwoBoard) {
                            keyboardType = BoardType.NUM_AND_SYMBOL_TYPE_TWO;
                        } else {
                            keyboardType = BoardType.NUM_AND_SYMBOL_TYPE_ONE;
                        }
                        switchKeyboard();
                        break;
                    }
                    case KeyboardUtil.SYMBOL_TWO: {
                        //显示符号键盘
                        if (isSpecialTwoBoard) {
                            isSpecialOneBoard = true;
                            isSpecialTwoBoard = false;
                            keyboardType = BoardType.NUM_AND_SYMBOL_TYPE_ONE;
                        } else {
                            //走纯特殊字符逻辑
                            keyboardType = BoardType.SYMBOL_TYPE;
                        }
                        switchKeyboard();
                        break;
                    }
                    case KeyboardUtil.SYMBOL_THREE: { // 特殊键盘 ₵₱§
                        // 键盘 #+= 和 按键 ₵₱§ 互斥
                        if (!isSpecialTwoBoard) {
                            // 但按键 ₵₱§ 点击的时候，不能在触发点击
                            isSpecialOneBoard = false;
                            isSpecialTwoBoard = true;
                            keyboardType = BoardType.NUM_AND_SYMBOL_TYPE_TWO;
                            switchKeyboard();
                        }
                        break;
                    }
                    case KeyboardUtil.WORD: {
                        // 大小写字符
                        keyboardType = BoardType.WORD_TYPE;
                        switchKeyboard();
                        break;
                    }
                    case KeyboardUtil.CLEAR: {
                        //清除
                        editable.clear();
                        break;
                    }
                    case KeyboardUtil.LINE_FEED: {   //换行键
                        break;
                    }
                    default: {
                        // 输入键盘值
                        // editable.insert(start, Character.toString((char) primaryCode));
                        char a = '„';
                        int aa = (int)a;
                        editable.replace(start, end, Character.toString((char) primaryCode));
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
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
    };


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
                // 如果是大写字母 +32，转化成小写字母
                if (key.label != null && isUpperLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {
            // 小写切换成大写
            isUpper = true;
            for (Keyboard.Key key : keyList) {
                // 如果是小写字母 -32，转化成大写字母
                if (key.label != null && isLoweLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }


    /**
     * 禁止粘贴复制
     *
     * @param editText
     */
    @SuppressLint("ClickableViewAccessibility")
    public void disableCopyAndPaste(final EditText editText) {
        try {
            if (editText == null) {
                return;
            }
            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            editText.setLongClickable(false);
            editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
     * 判断是否是小写
     */
    private boolean isLoweLetter(String str) {
        String wordStr = "abcdefghijklmnopqrstuvwxyz";
        return wordStr.contains(str);
    }

    /**
     * 判断是否是大写
     */
    private boolean isUpperLetter(String str) {
        String wordStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return wordStr.contains(str);
    }

    /**
     * 切换键盘
     */
    private void switchKeyboard() {
        switch (keyboardType) {
            case WORD_TYPE:
                setKeyboard(mLetterKeyboard);
                break;
            case NUM_AND_SYMBOL_TYPE_ONE:
                setKeyboard(mSymbolOneKeyboard);
                break;
            case NUM_AND_SYMBOL_TYPE_TWO:
                setKeyboard(mSymbolThreeKeyboard);
                break;
            case SYMBOL_TYPE:
                setKeyboard(mSymbolTwoKeyboard);
                break;
            case DEFAULT_TYPE:
            default:
                Log.e(TAG, "ERROR keyboard type");
                break;
        }
    }

    /**
     * 输入类型分类
     * 1. 字母键盘
     * 2. 数字 + 符号键盘
     * 3. 符号键盘
     *
     * @param keyboard 键盘
     */
    private void setKeyboard(Keyboard keyboard) {
        Object tag = mCurrentEditText.getTag();
        // 初始化键盘类型
        String numberType = mBoardTypeMap.get(tag.toString());
        if (numberType == null) {
            numberType = KhKeyboardView.OTHER_TYPE;
        }
        if (numberType.equals(KhKeyboardView.OTHER_TYPE)) {
            BoardType type;
            if (keyboard == mLetterKeyboard) {
                // 英文大小写字符
                type = BoardType.WORD_TYPE;
            } else if (keyboard == mSymbolTwoKeyboard) {
                // 特使符号
                type = BoardType.SYMBOL_TYPE;
            } else if (keyboard == mSymbolOneKeyboard) {
                // 特殊符号+数字
                type = BoardType.NUM_AND_SYMBOL_TYPE_ONE;
            } else if (keyboard == mSymbolThreeKeyboard) {
                // 特殊符号+数字
                type = BoardType.NUM_AND_SYMBOL_TYPE_TWO;
            } else {
                type = BoardType.DEFAULT_TYPE;
            }
            mEditLastKeyboardTypeMap.put(mCurrentEditText.getTag().toString(), type);
            keyboardType = type;
        }
        keyboardView.setKeyboard(keyboard);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        keyContainer.clearAnimation();
        keyContainer.startAnimation(hideAnimation);
    }

    private void doShowEnd() {
        isShowStart = false;
        // 在迅速点击不同输入框时, 造成自定义软键盘和系统软件盘不停的切换, 偶尔会出现停在使用系统键盘的输入框时, 没有隐藏
        // 自定义软键盘的情况, 为了杜绝这个现象, 加上下面这段代码
        if (!mCurrentEditText.isFocused()) {
            safeHandler.removeCallbacks(hideRun);
            safeHandler.removeCallbacks(showRun);
            safeHandler.postDelayed(hideRun, keyboardConfig.hideDelay);
        }

        // 这个只能在 keyContainer 显示后才能调用, 只有这个时候才能获取到 keyContainer 的宽、高值
        doScrollLayout();
    }

    private void doHideEnd() {
        isHideStart = false;

        doScrollLayoutBack(true, null);

        keyContainer.clearAnimation();
        if (keyContainer.getVisibility() != View.GONE) {
            keyContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 回落
     *
     * @param isHide 回落的同时, SafeKeyboard 是否隐藏
     */
    private boolean doScrollLayoutBack(final boolean isHide, EditText mEditText) {
        int thisScrollY = 0;
        if (!isHide && mEditText != null) {
            // 这种情况说明是点击了一个 EditText, 则需要判断是否需要移动 mScrollLayout 来适应 SafeKeyboard 的显示
            int[] mEditPos = new int[2];
            mEditText.getLocationOnScreen(mEditPos);
            Log.e("SafeKeyboard_Scroll", "0: " + mEditPos[0] + ", 1: " + mEditPos[1]);

            int keyboardHeight = keyContainer.getHeight();
            int keyStartY = mScreenHeight - keyboardHeight;
            getOriginalScrollLayoutPos();

            if (mEditText.getHeight() + 10 > keyStartY - originalScrollPosInScr[1]) {
                // mEditText 的高度 大于 SafeKeyboard 上边界到 mScrollLayout 上边界的距离, 即 mEditText 无法完全显示
                // TODO... 添加一个长文本输入功能

                return false;
            } else {
                // 可以正常显示
                if (mEditPos[1] < originalScrollPosInScr[1]) {
                    // 说明当前的 mEditText 的 top 位置已经被其他布局遮挡, 需要布局往下滑动一点, 使 mEditText 可以完全显示
                    thisScrollY = originalScrollPosInScr[1] - mEditPos[1] + 10; // 正值
                } else if (mEditPos[1] + mEditText.getHeight() > keyStartY) {
                    // 说明当前的 mEditText 的 bottom 位置已经被其他布局遮挡, 需要布局往上滑动一点, 使 mEditText 可以完全显示
                    thisScrollY = keyStartY - mEditPos[1] - mEditText.getHeight(); //负值
                } else {
                    // 各项均正常, 不需要重新滑动
                    Log.i("SafeKeyboard_LOG", "Need not to scroll");
                    return false;
                }
            }
        }

        toBackSize += thisScrollY;
        if (isHide) {
            mScrollLayout.animate().setDuration(keyboardConfig.showDuration).translationYBy(-toBackSize).start();
            toBackSize = 0;
        } else {
            mScrollLayout.animate().setDuration(keyboardConfig.showDuration).translationYBy(thisScrollY).start();
        }

        return true;
    }

    /**
     * 顶起
     */
    private void doScrollLayout() {
        // 计算 SafeKeyboard 显示后是否会遮挡住 EditText
        editNeedScroll(mCurrentEditText);
    }

    private void showKeyboard() {
        Keyboard mKeyboard = getKeyboardByInputType();
        setKeyboard(mKeyboard == null ? mLetterKeyboard : mKeyboard);
        keyContainer.setVisibility(View.VISIBLE);
        keyContainer.clearAnimation();
        keyContainer.startAnimation(showAnimation);
    }

    /**
     * @param mEditText 目标 EditText
     */
    private void editNeedScroll(EditText mEditText) {
        int keyboardHeight = keyContainer.getHeight();      // 获取键盘布局的高度
        int keyStartY = mScreenHeight - keyboardHeight;
        int[] position = new int[2];
        mEditText.getLocationOnScreen(position);
        int mEditTextBottomY = position[1] + mEditText.getHeight();
        if (mEditTextBottomY > keyStartY) {
            // 说明这个 EditText 的底部在 键盘 View 顶部以下, 即 EditText 被键盘遮挡了
            final float to = keyStartY - mEditTextBottomY - 10; // 为负值, 需要往上移动的距离, 往上为负值, 往下为正值
            if (position[1] + to < originalScrollPosInScr[1]) {
                // 说明, scrollLayout 被往上顶起之后, EditText 所在位置可能会被 scrollLayout 上面的其他 View 遮挡或者重合了导致显示不准确,
                // 那么顶起操作在这里就显示不合适了, 所以这里最好是添加一个长文本显示功能
                // 说明往上顶起之后 mEditText 会被遮挡, 即 mEditText 的 top 距离顶部的距离 小于 要移动的距离
                // 这里就不需要顶起了, 需要显示一个长文本显示页面
                // TODO... 添加一个长文本显示功能, 不过这里的长文本显示似乎没有什么意义
                return;
            }
            toBackSize = to;
            mScrollLayout.animate().translationYBy(toBackSize).setDuration(keyboardConfig.showDuration).start();
        }
    }

    private Keyboard getKeyboardByInputType() {
        Object tag = mCurrentEditText.getTag();
        // 初始化键盘类型
        String numberType = mBoardTypeMap.get(tag.toString());
        // 默认字母键盘
        Keyboard lastKeyboard = mLetterKeyboard;
        switch (numberType) {
            case KhKeyboardView.CARD_TYPE: {//身份证
                lastKeyboard = setRandomNumberKeyboard(mCardKeyboard, isRandom);
                break;
            }
            case KhKeyboardView.MONEY_TYPE: { //金额
                lastKeyboard = setRandomNumberKeyboard(mMoneyKeyboard, isRandom);
                break;
            }
            case KhKeyboardView.PHONE_TYPE: {//电话
                lastKeyboard = setRandomNumberKeyboard(mPhoneKeyboard, isRandom);
                break;
            }
            case KhKeyboardView.OTHER_TYPE: {
                // 这里其实不可能是 null
                BoardType type = BoardType.WORD_TYPE;
                if (tag != null) {
                    BoardType boardType = mEditLastKeyboardTypeMap.get(tag.toString());
                    type = boardType == null ? BoardType.WORD_TYPE : boardType;
                }
                switch (type) {
                    case WORD_TYPE: //英文大小写字符
                        lastKeyboard = mLetterKeyboard;
                        break;
                    case SYMBOL_TYPE: //特使符号
                        lastKeyboard = mSymbolTwoKeyboard;
                        break;
                    case NUM_AND_SYMBOL_TYPE_ONE://特殊符号+数字
                        lastKeyboard = mSymbolOneKeyboard;
                        break;
                    case NUM_AND_SYMBOL_TYPE_TWO://特殊符号+数字
                        lastKeyboard = mSymbolThreeKeyboard;
                        break;
                    default:
                        Log.e(TAG, "ERROR keyboard type");
                        break;
                }
                break;
            }
        }
        return lastKeyboard;
    }

    private boolean isEditMapContainThisEdit(EditText mEditText) {
        Object tagObj = mEditText.getTag();
        return tagObj != null && mEditMap.containsKey(tagObj.toString());
    }

    private void setCurrentEditText(EditText mEditText) {
        mCurrentEditText = mEditText;
    }

    public boolean isShow() {
        return isKeyboardShown();
    }

    /**
     * 隐藏系统键盘关键代码
     *
     * @param edit
     */
    private void hideSystemKeyBoard(EditText edit) {
        this.mCurrentEditText = edit;
        InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }

        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            edit.setInputType(0);
        } else {
            try {
                Method setShowSoftInputOnFocus = EditText.class.getMethod(methodName, Boolean.TYPE);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit, Boolean.FALSE);
            } catch (NoSuchMethodException e) {
                edit.setInputType(0);
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException |
                     IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 键盘是否显示
     *
     * @return
     */
    private boolean isKeyboardShown() {
        return keyContainer.getVisibility() == View.VISIBLE;
    }

    /**
     * 是否显示键盘
     *
     * @param preferShow
     * @return
     */
    public boolean stillNeedOptManually(boolean preferShow) {
        boolean flag;
        if (preferShow) {
            // 想要显示
            flag = isHideStart || (!isKeyboardShown() && !isShowStart);
        } else {
            // 想要隐藏
            flag = isShowStart || (isKeyboardShown() && !isHideStart);
        }
        return flag;
    }

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
                    // 报 ASCII 码转化成值，添加到 label
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
                && code != KeyboardUtil.DELETE && code != KeyboardUtil.CLEAR
                && code != KeyboardUtil.EMPTY;
    }

    /**
     * 还原字母+特殊符号数据键盘数据
     */
    public void onPause() {
        boardKey = -19991888;
        isClickBoard = false;
        // 恢复特殊符号
        isSpecialOneBoard = true;
        isSpecialTwoBoard = false;
        // 恢复默认键盘
        keyboardType = BoardType.WORD_TYPE;
        mEditLastKeyboardTypeMap.put(mCurrentEditText.getTag().toString(), keyboardType);
        //大写回复成小写
        // 转化方法先把isUpper设置成 true,然后在切换
        isUpper = true;
        changeKeyboard();
        mEditLastKeyboardTypeMap.put(mCurrentEditText.getTag().toString(), keyboardType);
        // 如果显示键盘，进行隐藏
        if (stillNeedOptManually(false)) {
            hideKeyboard();
        }
    }

    /**
     * 清空数据
     */
    public void release() {
        isSpecialOneBoard = true;
        isSpecialTwoBoard = false;
        isUpper = false;
        boardKey = -19991888;
        isClickBoard = false;
        mContext = null;
        toBackSize = 0;
        onEditTextTouchListener = null;
        if (treeObserver != null && onGlobalFocusChangeListener != null && treeObserver.isAlive()) {
            treeObserver.removeOnGlobalFocusChangeListener(onGlobalFocusChangeListener);
        }
        treeObserver = null;
        onGlobalFocusChangeListener = null;
        if (mEditLastKeyboardTypeMap != null) {
            mEditLastKeyboardTypeMap.clear();
            mEditLastKeyboardTypeMap = null;
        }
        if (mEditMap != null) {
            mEditMap.clear();
            mEditMap = null;
        }
        if (mBoardTypeMap != null) {
            mBoardTypeMap.clear();
            mBoardTypeMap = null;
        }
        if (mVibrateEditTagSet != null) {
            mVibrateEditTagSet.clear();
            mVibrateEditTagSet = null;
        }

        mScreenHeight = 0;
        originalScrollPosInScr = null;
        originalScrollPosInPar = null;
    }
}
