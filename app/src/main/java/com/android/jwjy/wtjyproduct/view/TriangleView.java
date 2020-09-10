package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.android.jwjy.wtjyproduct.R;


/**
 * Created by ccy on 2019/4/19/15:56
 */
public class TriangleView extends View {
    private Path mpath;
    private Paint mPaint;
    private int color = Color.parseColor("#000000");

    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    private void resloveAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TriangleLayout);
        color = typedArray.getColor(R.styleable.TriangleLayout_bgColor, color);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mpath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasureSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getMeasureSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    public int getMeasureSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        float mRadius = width * 0.5f;
        mpath.moveTo(0, 0);
        mpath.lineTo(width, 0);
        mpath.lineTo(mRadius, height);
        mpath.close();
        canvas.drawPath(mpath, mPaint);
    }
}
