package com.android.jwjy.wtjyproduct.bean;

/**
 * Created by ccy on 2018/11/29/14:09
 */
public class LogBean {
    private int level;
    private String message;

    public LogBean(int level, String message) {
        this.level = level;
        this.message = message;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
