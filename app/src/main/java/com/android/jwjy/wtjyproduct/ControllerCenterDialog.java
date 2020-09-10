package com.android.jwjy.wtjyproduct;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
/**
 * 居中对话框的控件
 * Created by dayuer on 2019/12/4.
 */
public class ControllerCenterDialog extends Dialog {
    //    style引用style样式
    public ControllerCenterDialog(Context context, int width, int height, View layout, int style) {

        super(context, style);

        setContentView(layout);

        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;   //设置弹出窗口居中显示

        window.setAttributes(params);
    }
}