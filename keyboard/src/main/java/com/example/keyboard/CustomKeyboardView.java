package com.example.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 键盘类
 */
public class CustomKeyboardView extends KeyboardView {
    private Context context;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }


    @Override
    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        try {
            Keyboard keyboard = getKeyboard();
            if (keyboard == null) return;
            List<Keyboard.Key> keys = keyboard.getKeys();
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                    //字符和特殊字符删除键
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_del_layerlist);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == KeyboardUtil.DELETE) {
                    //字母和身份证删除键
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_del_layerlist2);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
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

                } else if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE || key.codes[0] == KeyboardUtil.SYMBOL_WORD) {
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
                    drawText(canvas, key, Color.WHITE);
                } else {
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制背景
     */
    private void onBufferDrawOne(Canvas canvas, Keyboard.Key key) {
        Drawable keyBackground = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_selector_bg_one);;
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
                if (label.length() > 1 && key.codes.length < 2) {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    int labelTextSize = (int) field.get(this);
                    paint.setTextSize(labelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);

                } else {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    int keyTextSize = (int) field.get(this);
                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                }

                paint.getTextBounds(label, 0, key.label.toString().length(), bounds);

                Rect rect = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
                Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
                canvas.drawText(label, rect.centerX(), baseline, paint);

//                canvas.drawText(label, key.x + (key.width / 2), (key.y + key.height / 2) + bounds.height() / 2, paint);
            } else if (key.icon != null) {
                key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                        key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
