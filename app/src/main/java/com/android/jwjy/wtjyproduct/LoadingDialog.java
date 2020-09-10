package com.android.jwjy.wtjyproduct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.wang.avi.AVLoadingIndicatorView;

/**
 * 加载中Dialog
 *
 * @author hzb
 */
public class LoadingDialog extends AlertDialog {

    private static LoadingDialog loadingDialog;
    private AVLoadingIndicatorView avi;

    public static LoadingDialog getInstance(Context context) {
        if (!((Activity)context).isFinishing()){
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(context, R.style.TransparentDialog); //设置AlertDialog背景透明
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
            }
        } else {
            loadingDialog = new LoadingDialog(context, R.style.TransparentDialog); //设置AlertDialog背景透明
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        return loadingDialog;
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context,themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_loading);
        avi = (AVLoadingIndicatorView)this.findViewById(R.id.avi);
    }

    @Override
    public void show() {
        if (loadingDialog.getContext() instanceof Activity) {
            if (!((Activity) loadingDialog.getContext()).isFinishing()) {
                super.show();
            }
        }
//        if (avi == null) {
//            avi = (AVLoadingIndicatorView)this.findViewById(R.id.avi);
//        }
//        avi.show();
    }

    @Override
    public void dismiss() {
        if (loadingDialog.getContext() instanceof Activity) {
            if (!((Activity) loadingDialog.getContext()).isFinishing()) {
                super.dismiss();
            }
        }
//        if (avi == null) {
//            avi = (AVLoadingIndicatorView)this.findViewById(R.id.avi);
//        }
//        avi.hide();
    }
}