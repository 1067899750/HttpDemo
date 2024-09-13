package com.example.keyboard;

import android.content.Context;
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
    private Context context;

    // 是否按下按键
    public static boolean isClickDown = false;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            Keyboard keyboard = getKeyboard();
            if (keyboard == null) return;
            List<Keyboard.Key> keys = keyboard.getKeys();
            for (Keyboard.Key key : keys) {
                switch (key.codes[0]) {
                    case Keyboard.KEYCODE_DELETE:
                    case KeyboardUtil.DELETE: {
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
                        Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist_lower); //小写字母
                        Drawable dr_da = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist_upper);//大写字母
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
        float width = key.width / 2 - dpToPx(context, 23) / 2;
        float height = key.height / 2 - dpToPx(context, 17) / 2;
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
        Drawable keyBackground = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_selector_bg_one);
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
        Drawable keyBackground = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_selector_bg_three);
        keyBackground.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        keyBackground.draw(canvas);
    }


    /**
     * 绘制背景
     */
    private void onBufferDrawTwo(Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_selector_bg_two);
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
                    paint.setTextSize(sp2px(context, 15));
                } else {
                    paint.setTextSize(labelTextSize);
                }
                paint.setTypeface(Typeface.DEFAULT_BOLD);

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
}
