package com.talkfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.talkfun.libwidget.R;


public class RoundCornerLayout extends FrameLayout {
    private static float DEFAULT_CORNER_RADIUS= 0f;
    private float mRadius = 0f;
    private float mTopLeftRadius = 0f;
    private float mTopRightRadius = 0f;
    private float mBottomLeftRadius = 0f;
    private float mBottomRightRadius = 0f;
    private Path mPath;

    public RoundCornerLayout(@NonNull Context context) {
        this(context,null);
    }

    public RoundCornerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundCornerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerLayout);
        mRadius = array.getDimension(R.styleable.RoundCornerLayout_cornerRadius,DEFAULT_CORNER_RADIUS);
        mTopLeftRadius = array.getDimension(R.styleable.RoundCornerLayout_cornerTopLeftRadius,DEFAULT_CORNER_RADIUS);
        mTopRightRadius = array.getDimension(R.styleable.RoundCornerLayout_cornerTopRightRadius,DEFAULT_CORNER_RADIUS);
        mBottomLeftRadius = array.getDimension(R.styleable.RoundCornerLayout_cornerBottomLeftRadius,DEFAULT_CORNER_RADIUS);
        mBottomRightRadius = array.getDimension(R.styleable.RoundCornerLayout_cornerBottomRightRadius,DEFAULT_CORNER_RADIUS);
        mPath = new Path();
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.reset();
        RectF rectF = new RectF(getPaddingLeft(),getPaddingTop(),w - getPaddingRight(),h - getPaddingBottom());
        if(mRadius > 0f){
            mPath.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CW);
        }else if(mTopLeftRadius > 0f ||
                mTopRightRadius > 0f ||
                mBottomLeftRadius > 0f ||
                mBottomRightRadius > 0f){
            mPath.addRoundRect(rectF,new float[]{ mTopLeftRadius, mTopLeftRadius,
                                                    mTopRightRadius,mTopRightRadius,
                                                    mBottomRightRadius,mBottomRightRadius,
                                                    mBottomLeftRadius,mBottomLeftRadius},
                                Path.Direction.CW);
        }else{
            mPath.addRect(rectF, Path.Direction.CW);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(mPath);
        super.draw(canvas);
    }


/*
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
    }*/
}
