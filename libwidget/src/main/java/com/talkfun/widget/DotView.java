package com.talkfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.talkfun.libwidget.R;


/**
 * 圆点
 * Created by ccy on 2019/4/14/23:56
 */
public class DotView extends View {
    private float mRadius;
    private float percent = 1f;
    private Paint mPaint;
    private int mColor = Color.parseColor("#FFFFFF");

    public DotView(Context context) {
        this(context, null);
    }

    public DotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    private void resloveAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotView);
        mColor = typedArray.getColor(R.styleable.DotView_dot_color, mColor);
        percent = typedArray.getColor(R.styleable.DotView_radius_percent, 1);

        //设置最小宽高
        int defaultMini = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()) + 0.5f);
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
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        mRadius = (percent > 0 && percent <= 1) ? Math.min(width, height) * percent * 0.5f : Math.min(width, height) * 0.5f;
        float cx = width * 0.5f + paddingLeft;
        float cy = height * 0.5f + paddingTop;
        mPaint.setColor(mColor);
        canvas.drawCircle(cx, cy, mRadius, mPaint);
    }

    public void setColor(@ColorInt int color) {
        this.mColor = color;
    }

    public void setDotPerent(@FloatRange(from = 0, to = 1) float percent) {
        this.percent = percent;
    }
}
