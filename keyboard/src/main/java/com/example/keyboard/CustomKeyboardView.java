package com.example.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 键盘类
 */
public class CustomKeyboardView extends KeyboardView {

    private static final String TAG = "SafeKeyboardView";

    private int specialKeyBgResId = R.drawable.keyboard_change_trans;

    private final Context mContext;
    private boolean isCap;
    private boolean isCapLock;
    private boolean enableVibrate;
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    private Drawable upDrawableLock;
    private Keyboard lastKeyboard;
    /**
     * 按键的宽高至少是图标宽高的倍数
     */
    private static final int ICON2KEY = 2;

    // 键盘的一些自定义属性
    private boolean rememberLastType;

    // 是否按下按键
    public static boolean isClickDown = false;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.mContext = context;

        initAttrs(context, attrs, 0);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.mContext = context;

        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SafeKeyboardView, defStyleAttr, 0);
            array.recycle();
        }
    }

    private void init(Context mContext) {
        this.isCap = false;
        this.isCapLock = false;
        // 默认三种图标
        if (delDrawable == null) {
            delDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_del);
        }
        if (lowDrawable == null) {
            lowDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_capital_default);
        }
        if (upDrawable == null) {
            upDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_capital_selected);
        }
        if (upDrawableLock == null) {
            upDrawableLock = ContextCompat.getDrawable(mContext, R.drawable.icon_capital_selected_lock);
        }
        this.lastKeyboard = null;
    }

    public void setRememberLastType(boolean remember) {
        rememberLastType = remember;
    }

    public boolean isRememberLastType() {
        return rememberLastType;
    }

    public boolean isVibrateEnable() {
        return enableVibrate;
    }

    public void enableVibrate() {
        this.enableVibrate = true;
    }

    public void disableVibrate() {
        this.enableVibrate = false;
    }

    @Override
    public void setKeyboard(Keyboard keyboard) {
        super.setKeyboard(keyboard);
        this.lastKeyboard = keyboard;
    }

    public Keyboard getLastKeyboard() {
        return lastKeyboard;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                switch (key.codes[0]) {
                    case Keyboard.KEYCODE_DELETE:
                    case KeyboardUtil.DELETE: {
                        // 删除
                        onDrawDeleteBg(canvas, key);
                        break;
                    }
                    case KeyboardUtil.EMPTY: {
                        // 空白位置
                        onEmptyDrawBg(canvas, key);
                        break;
                    }
                    case Keyboard.KEYCODE_SHIFT: {
                        //大小写字母切换
                        Drawable dr = (Drawable) mContext.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist_lower); //小写字母
                        Drawable dr_da = (Drawable) mContext.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist_upper);//大写字母
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr_da.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);

                        if (KhKeyboardView.isUpper) {
                            //大写字母
                            dr_da.draw(canvas);
                        } else {
                            //小写字母
                            dr.draw(canvas);
                        }
                        break;
                    }
                    case KeyboardUtil.BLANK: {
                        // 空格键
                        onDrawClickBg(canvas, key);
                        drawText(canvas, key, Color.parseColor("#000000"));
                        break;
                    }
                    case Keyboard.KEYCODE_MODE_CHANGE:
                    case KeyboardUtil.SYMBOL_ONE:
                    case KeyboardUtil.SYMBOL_TWO:
                    case KeyboardUtil.NUMBER:
                    case KeyboardUtil.WORD: {
                        //大小写字母和特殊字符切换键
                        if (key.codes[0] == KhKeyboardView.boardKey) {
                            if (KhKeyboardView.isClickBoard) {
                                onBufferDrawOne(canvas, key);
                            } else {
                                onBufferDrawTwo(canvas, key);
                            }
                        } else {
                            onBufferDrawTwo(canvas, key);
                        }
                        drawText(canvas, key, Color.parseColor("#000000"));
                        break;
                    }
                    default: {
                        // 其他文字
                        if (key.codes[0] == KhKeyboardView.boardKey) {
                            if (KhKeyboardView.isClickBoard) {
                                onBufferDrawTwo(canvas, key);
                            } else {
                                onBufferDrawOne(canvas, key);
                            }
                        } else {
                            onBufferDrawOne(canvas, key);
                        }
                        drawText(canvas, key, Color.parseColor("#000000"));
                        break;
                    }

                    case 100860: {
                        drawSpecialKey(canvas, key);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        switch (me.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClickDown = true;
                break;
            case MotionEvent.ACTION_UP:
                isClickDown = false;
                break;
        }
        return super.onTouchEvent(me);
    }

    /**
     * 绘制删除键
     *
     * @param canvas
     * @param key
     */
    private void onDrawDeleteBg(Canvas canvas, Keyboard.Key key) {
        Bitmap bitmap;
        if (key.codes[0] == KhKeyboardView.boardKey) {
            if (isClickDown) {
                onBufferDrawOne(canvas, key);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_keyboard_del_selected);
            } else {
                onBufferDrawTwo(canvas, key);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_keyboard_del_default);
            }
        } else {
            onBufferDrawTwo(canvas, key);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_keyboard_del_default);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        float width = key.width / 2 - dpToPx(mContext, 23) / 2;
        float height = key.height / 2 - dpToPx(mContext, 17) / 2;
        canvas.drawBitmap(bitmap, key.x + width, key.y + height, paint);
    }


    /**
     * 绘制点击和抬起时候的背景
     *
     * @param canvas
     * @param key
     */
    private void onDrawClickBg(Canvas canvas, Keyboard.Key key) {
        if (key.codes[0] == KhKeyboardView.boardKey) {
            if (isClickDown) {
                onBufferDrawTwo(canvas, key);
            } else {
                onBufferDrawOne(canvas, key);
            }
        } else {
            onBufferDrawOne(canvas, key);
        }
    }


    /**
     * 绘制背景
     */
    private void onBufferDrawOne(Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = (Drawable) mContext.getResources().getDrawable(R.drawable.keyboard_selector_bg_one);
        keyBackground.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        keyBackground.draw(canvas);
    }

    /**
     * 空白键
     *
     * @param canvas
     * @param key
     */
    private void onEmptyDrawBg(Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = (Drawable) mContext.getResources().getDrawable(R.drawable.keyboard_selector_bg_three);
        keyBackground.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        keyBackground.draw(canvas);
    }


    /**
     * 绘制背景
     */
    private void onBufferDrawTwo(Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = (Drawable) mContext.getResources().getDrawable(R.drawable.keyboard_selector_bg_two);
        keyBackground.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        keyBackground.draw(canvas);
    }

    /**
     * 绘制文字
     *
     * @param canvas
     * @param key    字体的键值
     * @param color  字体颜色
     */
    private void drawText(Canvas canvas, Keyboard.Key key, int color) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);

            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setColor(color);

            if (key.label != null) {
                String label = key.label.toString();
                Field field;

                //绘制字体
                field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                field.setAccessible(true);
                int labelTextSize = (int) field.get(this);
                // 设置字体
                if (key.codes[0] == KeyboardUtil.BLANK || key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE
                        || key.codes[0] == KeyboardUtil.SYMBOL_ONE || key.codes[0] == KeyboardUtil.SYMBOL_TWO
                        || key.codes[0] == KeyboardUtil.NUMBER || key.codes[0] == KeyboardUtil.WORD
                        || key.codes[0] == KeyboardUtil.LINE_FEED) {
                    paint.setTextSize(sp2px(mContext, 15));
                } else {
                    paint.setTextSize(labelTextSize);
                }
                paint.setTypeface(Typeface.DEFAULT);

                paint.getTextBounds(label, 0, key.label.toString().length(), bounds);
                Rect rect = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
                Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
                canvas.drawText(label, rect.centerX(), baseline, paint);

            } else if (key.icon != null) {
                key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                        key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * dp转换成px
     */
    public float dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * sp转换成px
     */
    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /*************************************************************************************************/


    private void drawSpecialKey(Canvas canvas, Keyboard.Key key) {
        int color = Color.WHITE;
        if (key.codes[0] == -5) {
            drawKeyBackground(specialKeyBgResId, canvas, key);
            drawTextAndIcon(canvas, key, delDrawable, color);
        } else if (key.codes[0] == -2 || key.codes[0] == 100860 || key.codes[0] == 100861) {
            drawKeyBackground(specialKeyBgResId, canvas, key);
            drawTextAndIcon(canvas, key, null, color);
        } else if (key.codes[0] == -1) {
            if (isCapLock) {
                drawKeyBackground(specialKeyBgResId, canvas, key);
                drawTextAndIcon(canvas, key, upDrawableLock, color);
            } else if (isCap) {
                drawKeyBackground(specialKeyBgResId, canvas, key);
                drawTextAndIcon(canvas, key, upDrawable, color);
            } else {
                drawKeyBackground(specialKeyBgResId, canvas, key);
                drawTextAndIcon(canvas, key, lowDrawable, color);
            }
        }
    }


    private void drawKeyBackground(int id, Canvas canvas, Keyboard.Key key) {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = mContext.getResources().getDrawable(id);
        int[] state = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            drawable.setState(state);
        }
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }

    private void drawTextAndIcon(Canvas canvas, Keyboard.Key key, @Nullable Drawable drawable, int color) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(color);

            if (key.label != null) {
                String label = key.label.toString();

                Field field;

                if (label.length() > 1 && key.codes.length < 2) {
                    int labelTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField(getContext().getString(R.string.mLabelTextSize));
                        field.setAccessible(true);
                        Object obj = field.get(this);
                        labelTextSize = obj == null ? 0 : (int) obj;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(labelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int keyTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField(getContext().getString(R.string.mLabelTextSize));
                        field.setAccessible(true);
                        Object obj = field.get(this);
                        keyTextSize = obj == null ? 0 : (int) obj;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(keyTextSize + 10);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (1.0f * key.width / 2),
                        (key.y + 1.0f * key.height / 2) + 1.0f * bounds.height() / 2, paint);
            }
            if (drawable == null) return;
            // 约定: 最终图标的宽度和高度都需要在按键的宽度和高度的二分之一以内
            // 如果: 图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 那就不需要变换, 否则就需要等比例缩小
            int iconSizeWidth, iconSizeHeight;
            key.icon = drawable;
            int iconH = px2dip(mContext, key.icon.getIntrinsicHeight());
            int iconW = px2dip(mContext, key.icon.getIntrinsicWidth());
            if (key.width >= (ICON2KEY * iconW) && key.height >= (ICON2KEY * iconH)) {
                //图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 不需要缩放, 因为图片已经够小或者按键够大
                setIconSize(canvas, key, iconW, iconH);
            } else {
                //图标的实际宽度和高度至少有一个不在按键的宽度或高度的二分之一以内, 需要等比例缩放, 因为此时图标的宽或者高已经超过按键的二分之一
                //需要把超过的那个值设置为按键的二分之一, 另一个等比例缩放
                //不管图标大小是多少, 都以宽度width为标准, 把图标的宽度缩放到和按键一样大, 并同比例缩放高度
                double multi = 1.0 * iconW / key.width;
                int tempIconH = (int) (iconH / multi);
                if (tempIconH <= key.height) {
                    //宽度相等时, 图标的高度小于等于按键的高度, 按照现在的宽度和高度设置图标的最终宽度和高度
                    iconSizeHeight = tempIconH / ICON2KEY;
                    iconSizeWidth = key.width / ICON2KEY;
                } else {
                    //宽度相等时, 图标的高度大于按键的高度, 这时按键放不下图标, 需要重新按照高度缩放
                    double mul = 1.0 * iconH / key.height;
                    int tempIconW = (int) (iconW / mul);
                    iconSizeHeight = key.height / ICON2KEY;
                    iconSizeWidth = tempIconW / ICON2KEY;
                }
                setIconSize(canvas, key, iconSizeWidth, iconSizeHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIconSize(Canvas canvas, Keyboard.Key key, int iconSizeWidth, int iconSizeHeight) {
        int left = key.x + (key.width - iconSizeWidth) / 2;
        int top = key.y + (key.height - iconSizeHeight) / 2;
        int right = key.x + (key.width + iconSizeWidth) / 2;
        int bottom = key.y + (key.height + iconSizeHeight) / 2;
        key.icon.setBounds(left, top, right, bottom);
        key.icon.draw(canvas);
        key.icon = null;
    }

    public void setCap(boolean cap) {
        isCap = cap;
    }

    public void setCapLock(boolean isCapLock) {
        this.isCapLock = isCapLock;
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
    }

    public void setLowDrawable(Drawable lowDrawable) {
        this.lowDrawable = lowDrawable;
    }

    public void setUpDrawable(Drawable upDrawable) {
        this.upDrawable = upDrawable;
    }

    public void setUpDrawableLock(Drawable upDrawableLock) {
        this.upDrawableLock = upDrawableLock;
    }

    public void setSpecialKeyBgResId(int specialKeyBgResId) {
        this.specialKeyBgResId = specialKeyBgResId;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
