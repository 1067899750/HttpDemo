package com.example.mylibrary.untils;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * Description 服务端响应的数据类型
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/7/15 14:19
 */
public class DataType {

    /*返回数据为String*/
    public static final int STRING = 1;
    /*返回数据为xml类型*/
    public static final int XML = 2;
    /*返回数据为json对象*/
    public static final int JSON_OBJECT = 3;
    /*返回数据为json数组*/
    public static final int JSON_ARRAY = 4;

    /**
     * 自定义一个播放器状态注解
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STRING, XML, JSON_OBJECT, JSON_ARRAY})
    public @interface Type {
    }

}
