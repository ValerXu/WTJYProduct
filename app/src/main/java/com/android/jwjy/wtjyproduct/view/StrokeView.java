package com.android.jwjy.wtjyproduct.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.android.jwjy.wtjyproduct.R;

/**
 * 画笔粗细的
 */
public class StrokeView extends View {

    private int mBgColor = Color.parseColor("#ffffff");  //背景颜色(默认颜色)
    private int mCheckedColor = Color.parseColor("#4285f4");//选中的颜色(默认颜色)
    private int mUnCheckedColor = Color.parseColor("#BDC0BA");//未选中的颜色(默认颜色)
    private boolean mChecked = false;
    private float innerRingRadiusScale = 0.5f; //内圆比例
    private Paint mPaint;
    private float mRadius;

    public StrokeView(Context context) {
        this(context, null);
    }

    public StrokeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void resloveAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StrokeView);
        mChecked = typedArray.getBoolean(R.styleable.StrokeView_checked, false);
        mBgColor = typedArray.getColor(R.styleable.StrokeView_bg_color, mBgColor);
        mCheckedColor = typedArray.getColor(R.styleable.StrokeView_checked_color, Integer.MIN_VALUE);
        mUnCheckedColor = typedArray.getColor(R.styleable.StrokeView_un_checked_color, Integer.MIN_VALUE);
        innerRingRadiusScale = typedArray.getFloat(R.styleable.StrokeView_inner_cicle_scale, Float.MIN_VALUE);
        //设置最小宽高
        int defaultMini = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()) + 0.5f);
        if (getMinimumWidth() >= 0) {
            setMinimumWidth(defaultMini);
        }
        if (getMinimumHeight() >= 0) {
            setMinimumHeight(defaultMini);
        }
        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
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
        canvas.drawColor(Color.TRANSPARENT);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        mRadius = Math.min(width, height) / 2 * innerRingRadiusScale;
        float cx = width * 0.5f + paddingLeft;
        float cy = height * 0.5f + paddingTop;
        RectF rectF = new RectF(paddingLeft, paddingTop, width, height);
        mPaint.setColor(mBgColor);
        canvas.drawRect(rectF, mPaint);
        mPaint.setColor(mChecked ? mCheckedColor : mUnCheckedColor);
        canvas.drawCircle(cx, cy, mRadius, mPaint);
    }


    public int getBgColor() {
        return mBgColor;
    }

    public void setRadiusScale(float radiusScale) {
        this.innerRingRadiusScale =radiusScale;
    }

    public void setBgColor(int mColor) {
        this.mBgColor = mColor;
        invalidate();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
        invalidate();
    }

    public int getCheckedColor() {
        return mCheckedColor;
    }

    public void setCheckedColor(int checkedColor) {
        this.mCheckedColor = checkedColor;
        invalidate();
    }

    public void setUnCheckedColor(int unCheckedColor) {
        this.mUnCheckedColor = unCheckedColor;
        invalidate();
    }


}
