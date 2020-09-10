package com.talkfun.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.talkfun.libwidget.R;

import java.util.ArrayList;

/**
 * Created by ccy on 2019/5/18/12:08
 */

public class RippleView extends RelativeLayout {
    //涟漪的颜色
    private int rippleColor = Color.parseColor("#ff0000");
    //最里面涟漪的实心圆
    private float ripplePercent = 0.5f;
    //自定义的动画开始与结束接口
    private AnimationListener mAnimationProgressListener;
    //画笔
    private Paint paint;
    //动画标志
    private boolean animationRunning = false;
    //动画集合
    private AnimatorSet animatorSet;
    //自定义view集合
    private ArrayList<mRipplView> rippleViewList = new ArrayList<>();
    //每次动画的时间
    private int rippleDurationTime = 4000;
    //涟漪条目
    private int rippleAmount = 4;
    //每条涟漪依次出现的延迟
    private int rippleDelay;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    private void resloveAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleColor = typedArray.getColor(R.styleable.RippleView_rColor, rippleColor);
        ripplePercent = typedArray.getFloat(R.styleable.RippleView_rCirCilePercent, ripplePercent);
        rippleAmount = typedArray.getInt(R.styleable.RippleView_rAmount, rippleAmount);
        rippleDurationTime = typedArray.getInt(R.styleable.RippleView_rDurationTime, rippleDurationTime);
        typedArray.recycle();
    }

    private void init() {
        //画每个圆的时间间隔为一个圆的动画时间除以总共出现圆的个数，达到每个圆出现的时间间隔一致
        rippleDelay = rippleDurationTime / rippleAmount;
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rippleColor);
        //布局 管理器，让圆剧中显示
        LayoutParams rippleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(rippleDurationTime);
        //加速插值器
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //圆圈的集合
        ArrayList<Animator> animatorList = new ArrayList<>();
        //缩放、渐变动画
        for (int i = 0; i < rippleAmount; i++) {
            mRipplView rippleView = new mRipplView(getContext());
            addView(rippleView, rippleParams);
            rippleViewList.add(rippleView);
            //伸缩动画
            float rippleScale = 1 / ripplePercent;
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
            scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleXAnimator);

            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
            scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);//ObjectAnimator.RESTART
            scaleYAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleYAnimator);

            //透明度动画
            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0.1f);
            alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(alphaAnimator);

        }
        //开始动画
        animatorSet.playTogether(animatorList);
        //动画的监听
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (mAnimationProgressListener != null) {
                    mAnimationProgressListener.startAnimation();
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mAnimationProgressListener != null) {
                    mAnimationProgressListener.EndAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animatorSet != null) {
            animatorSet.end();
            animationRunning = false;
        }
    }

    //画一个圆
    private class mRipplView extends View {
        int radius;

        mRipplView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            radius = (Math.min(getWidth(), getHeight())) / 2;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(radius, radius, radius * ripplePercent, paint);

        }
    }

    /**
     * 对外的开始动画
     */
    public void startRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (mRipplView rippleView : rippleViewList) {
                rippleView.setVisibility(VISIBLE);
            }
            if (animatorSet != null) {
                animatorSet.start();
                animationRunning = true;
            }
        }
    }

    /**
     * 对面外的结束动画
     */
    public void stopRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (mRipplView rippleView : rippleViewList) {
                rippleView.setVisibility(INVISIBLE);
            }
            if (animatorSet != null) {
                animatorSet.end();
                animationRunning = false;
            }
        }
    }

    private boolean isRippleAnimationRunning() {
        return animationRunning;
    }

    /**
     * 对外的接口
     */
    public interface AnimationListener {
        void startAnimation();

        void EndAnimation();
    }

    public void setAnimationProgressListener(AnimationListener mAnimationProgressListener) {
        this.mAnimationProgressListener = mAnimationProgressListener;
    }

}