package com.example.keyboard;

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

import java.lang.reflect.Field;
import java.util.List;


/**
 * 键盘类
 */
public class CustomKeyboardView extends KeyboardView {

    private static final String TAG = "SafeKeyboardView";
    private final Context mContext;

    private Keyboard lastKeyboard;

    // 键盘的一些自定义属性
    private boolean rememberLastType;

    // 是否按下按键
    public static boolean isClickDown = false;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initAttrs(context, attrs, 0);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SafeKeyboardView, defStyleAttr, 0);
            array.recycle();
        }
    }

    public void setRememberLastType(boolean remember) {
        rememberLastType = remember;
    }

    public boolean isRememberLastType() {
        return rememberLastType;
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
//                        onDrawCapitalBg(canvas, key);
                        break;
                    }
                    case KeyboardUtil.BLANK: {
                        // 空格键
                        onDrawClickBg(canvas, key);
                        drawText(canvas, key, Color.parseColor("#000000"));
                        break;
                    }
                    case KeyboardUtil.SYMBOL_THREE: {
                        // ₵₱§ 按键
                        if(KhKeyboardView.isSpecialTwoBoard){
                            onBufferDrawTwo(canvas, key);
                        } else {
                            onBufferDrawOne(canvas, key);
                        }
                        drawText(canvas, key, Color.parseColor("#000000"));
                        break;
                    }
                    case KeyboardUtil.SYMBOL_TWO:{
                        if(KhKeyboardView.isSpecialTwoBoard){
                            if(KhKeyboardView.isSpecialOneBoard){
                                onBufferDrawTwo(canvas, key);
                            } else {
                                onBufferDrawOne(canvas, key);
                            }
                        } else {
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
                        }
                        drawText(canvas, key, Color.parseColor("#000000"));
                        break;
                    }
                    case Keyboard.KEYCODE_MODE_CHANGE:
                    case KeyboardUtil.SYMBOL_ONE:
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
     * 绘制大小写切换键
     *
     * @param canvas
     * @param key
     */
    private void onDrawCapitalBg(Canvas canvas, Keyboard.Key key) {
        Bitmap dr = BitmapFactory.decodeResource(getResources(), R.drawable.icon_keyboard_capital_default);  //小写字母
        Bitmap dr_da = BitmapFactory.decodeResource(getResources(), R.drawable.icon_keyboard_capital_selected);  //大写字母

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        if (KhKeyboardView.isUpper) {
            //大写字母
            float width = key.width / 2f - dr_da.getWidth() / 2f;
            float height = key.height / 2f - dr_da.getHeight() / 2f;
            onBufferDrawOne(canvas, key);
            canvas.drawBitmap(dr_da, key.x + width, key.y + height, paint);
        } else {
            //小写字母
            float width = key.width / 2f - dr.getWidth() / 2f;
            float height = key.height / 2f - dr.getHeight() / 2f;
            onBufferDrawTwo(canvas, key);
            canvas.drawBitmap(dr, key.x + width, key.y + height, paint);
        }
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
        float width = key.width / 2f - bitmap.getWidth() / 2f;
        float height = key.height / 2f - bitmap.getHeight() / 2f;
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
     * 绘制背景
     */
    private void onBufferDrawTwo(Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = (Drawable) mContext.getResources().getDrawable(R.drawable.keyboard_selector_bg_two);
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
                if (isSpecialBtn(key.codes[0])) {
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
     * 是否是特殊按键
     *
     * @param code
     * @return
     */
    public boolean isSpecialBtn(int code) {
        return code == KeyboardUtil.BLANK || code == Keyboard.KEYCODE_MODE_CHANGE
                || code == KeyboardUtil.SYMBOL_ONE || code == KeyboardUtil.SYMBOL_TWO
                || code == KeyboardUtil.NUMBER || code == KeyboardUtil.WORD
                || code == KeyboardUtil.LINE_FEED || code == KeyboardUtil.SYMBOL_THREE;
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
}
