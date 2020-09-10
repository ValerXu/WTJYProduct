package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.ScreenSwitchUtils;

/**
 * Created by Wallace on 2017/3/14.
 */
public class LargeImagePopupWindow extends PopupWindow {
    private Context context;
    private ImageView largeImageView;

    public LargeImagePopupWindow(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public void initView(){
        View view = View.inflate(context, R.layout.popupwindow_image_enlarge,null);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        largeImageView = (ImageView) view.findViewById(R.id.iv_large_image);
        setContentView(view);
        setWidth(DimensionUtils.getScreenWidth(context));
        setHeight(DimensionUtils.getScreenHeight(context));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(Drawable drawable,View view){
        if(drawable!=null&&largeImageView!=null)
            largeImageView.setImageDrawable(drawable);
        if(!isShowing()){
            ScreenSwitchUtils.getInstance(context).isOpenSwitchAuto(false); //不可旋转
            showAtLocation(view, Gravity.TOP,0,0);
            update();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ScreenSwitchUtils.getInstance(context).isOpenSwitchAuto(true); //不可旋转
    }
}
