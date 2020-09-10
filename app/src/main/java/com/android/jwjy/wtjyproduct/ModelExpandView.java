package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
/**
 * Created by dayuer on 19/7/2.
 * 可展开收起视图
 */
public class ModelExpandView extends FrameLayout {


    private Animation mExpandAnimation;
    private Animation mCollapseAnimation;
    private boolean mIsExpand;
    private View mView;

    public ModelExpandView(Context context) {
        this(context,null);
        initExpandView();
        // TODO Auto-generated constructor stub
    }
    public ModelExpandView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        initExpandView();
        // TODO Auto-generated constructor stub
    }
    public ModelExpandView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initExpandView();
    }
    private void initExpandView() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.modellayout_expand, this, true);

        mExpandAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
        mExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.VISIBLE);
            }
        });

        mCollapseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.collapse);
        mCollapseAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.INVISIBLE);
            }
        });

    }
    public void collapse() {
        if (mIsExpand) {
            mIsExpand = false;
            clearAnimation();
            startAnimation(mCollapseAnimation);
            //收缩的时候改变高度
            LinearLayout modellayout_expand = mView.findViewById(R.id.modellayout_expand_main);
            LayoutParams lp = (LayoutParams) modellayout_expand.getLayoutParams();
            lp.height = 0;
            modellayout_expand.setLayoutParams(lp);
        }
    }

    public void expand() {
        if (!mIsExpand) {
            mIsExpand = true;
            clearAnimation();
            startAnimation(mExpandAnimation);
            //展开的时候改变高度
            LinearLayout modellayout_expand = mView.findViewById(R.id.modellayout_expand_main);
            LayoutParams lp = (LayoutParams) modellayout_expand.getLayoutParams();
            lp.height = LayoutParams.WRAP_CONTENT;
            modellayout_expand.setLayoutParams(lp);
        }
    }

    public boolean isExpand() {
        return mIsExpand;
    }

//    public void setContentView(int layout){
//        View view = LayoutInflater.from(getContext()).inflate(layout, null);
//        removeAllViews();
//        addView(view);
//    }
//
//    public void setContentView(View view){
//        removeAllViews();
//        addView(view);
//    }

    public LinearLayout getContentView(){
        LinearLayout modellayout_expand = mView.findViewById(R.id.modellayout_expand_main);
        return modellayout_expand;
    }
}
