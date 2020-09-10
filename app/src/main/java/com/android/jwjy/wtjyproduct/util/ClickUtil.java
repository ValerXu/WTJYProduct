package com.android.jwjy.wtjyproduct.util;

import android.os.SystemClock;

import java.util.HashMap;

/**
 * 防止多次点击的工具类
 * Created by ccy on 2018/10/9/21:35
 */
public class ClickUtil {
    private HashMap<Integer, Long> laskClickTimeMap = new HashMap<Integer, Long>();
    private volatile static ClickUtil mInstance = null;
    public static int DELAY_IN_MS = 500;

    public static ClickUtil getInstance() {
        if (mInstance == null) {
            synchronized (ClickUtil.class) {
                if (mInstance == null) {
                    mInstance = new ClickUtil();
                }
            }
        }
        return mInstance;
    }

    public boolean isClickable(Integer key) {
        Long keyLong = laskClickTimeMap.get(key);
        if (keyLong == null) {
            laskClickTimeMap.put(key, SystemClock.elapsedRealtime());
            return true;
        } else {
            if (SystemClock.elapsedRealtime() - keyLong.longValue() < DELAY_IN_MS) {
                return false;
            } else {
                laskClickTimeMap.put(key, new Long(SystemClock.elapsedRealtime()));
                return true;
            }
        }
    }

}
