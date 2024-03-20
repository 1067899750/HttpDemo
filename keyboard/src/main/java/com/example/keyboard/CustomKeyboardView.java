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
        super.onDraw(canvas);
        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
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
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist);
                    Drawable dr_da = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_word_shift_layerlist_da);
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
                    Drawable dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_selector_blue_bg);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    drawText(canvas, key);
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void drawText(Canvas canvas, Keyboard.Key key) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);


            paint.setAntiAlias(true);

            paint.setColor(Color.WHITE);

            if (key.label != null) {
                String label = key.label.toString();

                Field field;

                if (label.length() > 1 && key.codes.length < 2) {
                    int labelTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        labelTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(labelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int keyTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        keyTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2, paint);
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
