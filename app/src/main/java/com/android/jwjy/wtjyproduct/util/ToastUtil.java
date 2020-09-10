package com.android.jwjy.wtjyproduct.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by ccy on 2018/9/7/16:36
 */
public class ToastUtil {
    private static Toast toast;
    private static WeakReference<Context> mContextWeakReference;
    private static int xOffsetParams = 0;

    public static void show(Context context, String msg) {
        mContextWeakReference = new WeakReference<>(context);
        show(mContextWeakReference.get(), msg, 0, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String msg, int xOffset) {
        mContextWeakReference = new WeakReference<>(context);
        show(mContextWeakReference.get(), msg, xOffset, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String msg, int xOffset, int duration) {
//        if (toast == null) {
//            if (context != null) {
//                toast = Toast.makeText(context.getApplicationContext(), null, duration);//解决在小米手机上第一次进入系统后，会添加上app应用的名称。
//                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, xOffset, 50);
//                toast.setText(msg);
//            }
//        } else {
//            if (xOffsetParams != xOffset) {
//                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, xOffset, 50);
//                xOffsetParams = xOffset;
//            }
//            toast.setText(msg);
//        }
//        toast.show();
        show(context,msg,Gravity.CENTER_HORIZONTAL | Gravity.TOP,xOffset,50,duration);
    }

    public static void show(Context context, String msg, int gravity, int xOffset, int yOffset, int duration) {
        if (toast == null) {
            if (context != null) {
                toast = Toast.makeText(context.getApplicationContext(), null, duration);//解决在小米手机上第一次进入系统后，会添加上app应用的名称。
                toast.setGravity(gravity, xOffset, yOffset);
                toast.setText(msg);
            }
        } else {
            if (xOffsetParams != xOffset) {
                toast.setGravity(gravity, xOffset, yOffset);
                xOffsetParams = xOffset;
            }
            toast.setText(msg);
        }

        toast.show();
    }

    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
