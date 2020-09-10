package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.android.jwjy.wtjyproduct.R;
import com.talkfun.widget.PopView;

/**
 * 奖励弹框
 * Created by ccy on 2019/5/27/11:12
 */
public class OTMAwardPopView extends PopView implements Animation.AnimationListener {
    private ImageView bgIV;
    private RotateAnimation rotateAnimation;

    public OTMAwardPopView(Context context) {
        super(context);
        View mContentView = LayoutInflater.from(context).inflate(R.layout.pop_otm_award, null);
        setContentView(mContentView);
        bgIV = mContentView.findViewById(R.id.iv_award_bg);

    }

    @Override
    public void showAtAnchorView(@NonNull View anchor, int vertGravity, int horizGravity) {
        super.showAtAnchorView(anchor, vertGravity, horizGravity);
        startAnimation();
    }

    private void startAnimation() {
        rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3_000);
        rotateAnimation.setAnimationListener(this);
        bgIV.setAnimation(rotateAnimation);
        rotateAnimation.start();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        dismiss();
    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
