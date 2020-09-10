package com.android.jwjy.wtjyproduct.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.imageload.GlideImageLoader;


/**
 * 图片查看器
 * Created by ccy on 2018/6/11 14:52.
 */

public class PhotoDialog extends Dialog implements View.OnClickListener {
    private  ImageView shrinkIV;
    private Context mContext;
    private ImageView photoIV;

    public PhotoDialog(final Context context) {
        super(context, R.style.InputDialog);
        mContext = context;
        setContentView(R.layout.phone_pop_view);
        setCanceledOnTouchOutside(true);
        photoIV =findViewById(R.id.iv_photo);
        shrinkIV =findViewById(R.id.iv_shrink);
        shrinkIV.setOnClickListener(this);
    }
    public void setImageUrl(String url){
        GlideImageLoader.create(photoIV).load(url);
    }



    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }
}
