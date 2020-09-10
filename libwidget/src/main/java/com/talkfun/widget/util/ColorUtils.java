package com.talkfun.widget.util;

import android.text.TextUtils;

/**
 * 颜色工具类
 * Created by ccy on 2019/5/29/17:41
 */
public class ColorUtils {
    /**
     * 16进制转GRB颜色值方法
     *
     * @param hex
     */
    public static int[] toRGB(String hex) {
        if (TextUtils.isEmpty(hex)) {
            return new int[]{255, 255, 255};
        }
        int color = Integer.parseInt(hex.replace("#", ""), 16);
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        return new int[]{red, green, blue};
    }
    /**
     * 16进制转GRB颜色值方法
     *
     * @param color
     */
    public static int[] toRGB(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        return new int[]{red, green, blue};
    }

    /**
     * GRB转16进制颜色值方法
     *
     * @param red
     * @param green
     * @param blue
     */
    public static String toHex(int red, int green, int blue) {
        String hr = Integer.toHexString(red);
        String hg = Integer.toHexString(green);
        String hb = Integer.toHexString(blue);
//        System.out.println("#" + hr + hg + hb);
        return new StringBuilder().append("#").append(hr).append(hg).append(hb).toString();
    }
}
