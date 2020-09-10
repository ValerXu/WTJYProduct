package com.android.jwjy.wtjyproduct.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.talkfun.sdk.module.VideoModeType;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ccy on 2018/12/3/10:00
 */
public class SwitchModePopManager {
    private Context mContext;
    private PopView mChatPopView;
    @BindView(R.id.mode_text_tv)
    TextView modeTV;
    private String modeText = "正在切换班课模式";

    public SwitchModePopManager(Context context) {
        if (context == null) {
            return;
        }
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        mContext = contextWeakReference.get();
        initView();
    }

    public void initView() {
        View rootView = View.inflate(mContext, R.layout.swith_mode_pop, null);
        mChatPopView = new PopView(mContext).setContentView(rootView).setFocusable(true).setFocusAndOutsideEnable(true)
                .createPopup();
        ButterKnife.bind(this, rootView);

    }

    public void setMode(int mode) {
        if (mode == VideoModeType.DESKTOP_MODE) {
            modeText = "正在切換桌面模式";
        } else if (mode == VideoModeType.RTC_MODE) {
            modeText = "正在切换班课模式";
        } else {
            modeText = "正在切换观看模式";
        }
        modeTV.setText(modeText);
    }

    /**
     * 面板开关
     */
    @SuppressLint("WrongConstant")
    public void show(View view) {
        if (mChatPopView == null) {
            return;
        }
        if (mChatPopView.isShowing()) {
            mChatPopView.dismiss();
        } else {
            mChatPopView.showAtAnchorView(view, VerticalGravity.CENTER, HorizontalGravity.CENTER);
        }
    }

    public boolean isShow() {
        return mChatPopView != null && mChatPopView.isShowing();
    }

    public void dismiss() {
        if (mChatPopView == null) {
            return;
        }
        if (!mChatPopView.isShowing())
            return;
        mChatPopView.dismiss();
    }
}
