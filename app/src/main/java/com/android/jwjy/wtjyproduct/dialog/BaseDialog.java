package com.android.jwjy.wtjyproduct.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ccy on 2017/11/6.
 */

public abstract class BaseDialog extends BaseDialogFragment implements View.OnClickListener {
    protected View layout;
    @BindView(R.id.iv_icon)
    protected ImageView icon;
    @BindView(R.id.tv_title)
    protected TextView title;
    @BindView(R.id.cancel)
    protected ImageView cancal;
   // @BindView(R.id.fl_content)
    protected FrameLayout contentLayout;
    private Unbinder unbinder;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.base_dialog_fragment, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View contentView = inflater.inflate(getContentLayout(),null);
        contentLayout = (FrameLayout)layout.findViewById(R.id.fl_content);
        contentLayout.addView(contentView);

        unbinder = ButterKnife.bind(this, layout);

        initView();
        initEvent();
        return layout;
    }

    protected void initView() {

    }

    protected void initEvent() {
        cancal.setOnClickListener(this);

    }

    abstract protected int getContentLayout();

   
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
