package com.example.httpdemo.bean;

/**
 * 
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/5/13 14:31
 */

public class BaseBean {
    protected String err_no;
    protected String err_msg;

    public String getErr_no() {
        return err_no;
    }

    public void setErr_no(String err_no) {
        this.err_no = err_no;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }
}
