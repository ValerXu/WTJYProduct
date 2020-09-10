package com.android.jwjy.wtjyproduct;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by dayuer on 19/7/2.
 * 日志打印模块
 */
public class ModelLogUtil {
    public static String tagPrefix = "";
    public static boolean showV = true;
    public static boolean showD = true;
    public static boolean showI = true;
    public static boolean showW = true;
    public static boolean showE = true;
    public static boolean showWTF = true;

    /**
     * 得到tag（所在类.方法（L:行））
     * @return
     */
    private static String generateTag() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = stackTraceElement.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String tag = "%s.%s(L:%d)";
        tag = String.format(tag, new Object[]{callerClazzName, stackTraceElement.getMethodName(), Integer.valueOf(stackTraceElement.getLineNumber())});
        //给tag设置前缀
        tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
        return tag;
    }
    //调试颜色为黑色的，任何消息都会输出，这里的v代表verbose啰嗦的意思，平时使用就是Log.v("","");
    public static void v(String msg) {
        if (showV) {
            String tag = generateTag();
            Log.v(tag, msg);
        }
    }
    //调试颜色为黑色的，任何消息都会输出，这里的v代表verbose啰嗦的意思，平时使用就是Log.v("","");
    public static void v(String msg, Throwable tr) {
        if (showV) {
            String tag = generateTag();
            Log.v(tag, msg, tr);
        }
    }
    //输出颜色是蓝色的，仅输出debug调试的意思，但他会输出上层的信息，过滤起来可以通过DDMS的Logcat标签来选择
    public static void d(String msg) {
        if (showD) {
            String tag = generateTag();
            Log.d(tag, msg);
        }
    }
    //输出颜色是蓝色的，仅输出debug调试的意思，但他会输出上层的信息，过滤起来可以通过DDMS的Logcat标签来选择
    public static void d(String msg, Throwable tr) {
        if (showD) {
            String tag = generateTag();
            Log.d(tag, msg, tr);
        }
    }
    //输出为绿色，一般提示性的消息information，它不会输出Log.v和Log.d的信息，但会显示i、w和e的信息
    public static void i(String msg) {
        if (showI) {
            String tag = generateTag();
            Log.i(tag, msg);
        }
    }
    //输出为绿色，一般提示性的消息information，它不会输出Log.v和Log.d的信息，但会显示i、w和e的信息
    public static void i(String msg, Throwable tr) {
        if (showI) {
            String tag = generateTag();
            Log.i(tag, msg, tr);
        }
    }
    //输出为橙色，可以看作为warning警告，一般需要我们注意优化Android代码，同时选择它后还会输出Log.e的信息
    public static void w(String msg) {
        if (showW) {
            String tag = generateTag();
            Log.w(tag, msg);
        }
    }
    //输出为橙色，可以看作为warning警告，一般需要我们注意优化Android代码，同时选择它后还会输出Log.e的信息
    public static void w(String msg, Throwable tr) {
        if (showW) {
            String tag = generateTag();
            Log.w(tag, msg, tr);
        }
    }
    //输出为红色，可以想到error错误，这里仅显示红色的错误信息，这些错误就需要我们认真的分析，查看栈的信息了
    public static void e(String msg) {
        if (showE) {
            String tag = generateTag();
            Log.e(tag, msg);
        }
    }
    //输出为红色，可以想到error错误，这里仅显示红色的错误信息，这些错误就需要我们认真的分析，查看栈的信息了
    public static void e(String msg, Throwable tr) {
        if (showE) {
            String tag = generateTag();
            Log.e(tag, msg, tr);
        }
    }
    //报告一个永远不可能发生的情况
    public static void wtf(String msg) {
        if (showWTF) {
            String tag = generateTag();
            Log.wtf(tag, msg);
        }
    }
    //报告一个永远不可能发生的情况
    public static void wtf(String msg, Throwable tr) {
        if (showWTF) {
            String tag = generateTag();
            Log.wtf(tag, msg, tr);
        }
    }
}
