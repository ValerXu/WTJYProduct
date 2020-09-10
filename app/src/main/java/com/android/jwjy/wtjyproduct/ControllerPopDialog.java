package com.android.jwjy.wtjyproduct;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by dayuer
 * 弹出对话框
 */

public class ControllerPopDialog extends Dialog {
    private ControlMainActivity mControlMainActivity;
    private int mLayout = 0;
//    public ControllerPopDialog(@NonNull Context context) {
//        this(context, 0);
//    }

    public ControllerPopDialog(@NonNull Context context, int themeResId,int layout) {
        super(context, themeResId);
        mControlMainActivity = (ControlMainActivity) context;
        mLayout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayout);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
