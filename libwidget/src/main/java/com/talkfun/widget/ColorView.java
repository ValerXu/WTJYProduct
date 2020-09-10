package com.talkfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.talkfun.libwidget.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class ColorView extends View {

    private int mColor = Color.parseColor("#ff0000");  //颜色内容
    private int mBorderColor = Color.parseColor("#cccccc"); //边框颜色
    private int mCheckedBorderColor = Integer.MIN_VALUE;
    private int mCheckedRingColor = Integer.MIN_VALUE; //选中环颜色
    private int mCheckedRingWidth = Integer.MIN_VALUE; //选中环宽度
    private float mCheckedRingWidthPercent = 0.4f; //选中环百分比
    private boolean mChecked = false;
    private int mBorderWidth = 2; //边框宽度
    private float cornerRadius;//圆角半径
    private Paint mPaint;
    private float mRadius;
    private @ShapeTypeIntDef
    int shapeType;

    public final static int TYPE_CIRCLE = 0;
    public final static int TYPE_RECTANGLE = 1;

    @IntDef({TYPE_CIRCLE, TYPE_RECTANGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShapeTypeIntDef {
    }

    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    private void resloveAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorView);
        mChecked = typedArray.getBoolean(R.styleable.ColorView_checked, false);
        mBorderColor = typedArray.getColor(R.styleable.ColorView_border_color, mBorderColor);
        mBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.ColorView_border_width, mBorderWidth);
        mColor = typedArray.getColor(R.styleable.ColorView_color, mColor);
        mCheckedBorderColor = typedArray.getColor(R.styleable.ColorView_checked_border_color, Integer.MIN_VALUE);
        mCheckedRingColor = typedArray.getColor(R.styleable.ColorView_checked_ring_color, Integer.MIN_VALUE);
        mCheckedRingWidth = typedArray.getDimensionPixelOffset(R.styleable.ColorView_checked_ring_width, Integer.MIN_VALUE);
        mCheckedRingWidthPercent = typedArray.getFloat(R.styleable.ColorView_checked_ring_width_percent, mCheckedRingWidthPercent);
        shapeType = typedArray.getInt(R.styleable.ColorView_color_shape_type, TYPE_CIRCLE);
        cornerRadius = typedArray.getDimensionPixelOffset(R.styleable.ColorView_corner_radius, -1);
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
        mRadius = Math.min(width, height) * 0.5f;
        float cx = width * 0.5f + paddingLeft;
        float cy = height * 0.5f + paddingTop;

        if (mChecked) {
            mPaint.setColor(mCheckedBorderColor != Integer.MIN_VALUE ? mCheckedBorderColor : mColor);
            if (shapeType == TYPE_CIRCLE) {
                canvas.drawCircle(cx, cy, mRadius, mPaint);
            } else {
                RectF rectF = new RectF(cx - mRadius, cy - mRadius, cx + mRadius, cy + mRadius);
                if (cornerRadius > 0) {
                    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, mPaint);
                } else {
                    canvas.drawRect(rectF, mPaint);
                }
            }

            mPaint.setColor(mCheckedRingColor != Integer.MIN_VALUE ? mCheckedRingColor : Color.parseColor("#ffffff"));
            if (shapeType == TYPE_CIRCLE) {
                canvas.drawCircle(cx, cy, mRadius - mBorderWidth, mPaint);
            } else {
                RectF rectF = new RectF(cx - mRadius + mBorderWidth, cy - mRadius + mBorderWidth, cx + mRadius - mBorderWidth, cy + mRadius - mBorderWidth);
                if (cornerRadius > 0) {
                    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, mPaint);
                } else {
                    canvas.drawRect(rectF, mPaint);
                }
            }
            mPaint.setColor(mColor);
            float tmpRadius = mRadius - mBorderWidth;
            float ringWidth = mCheckedRingWidth > 0 ? mCheckedRingWidth : ((mCheckedRingWidthPercent > 0f && mCheckedRingWidth < 1f) ? tmpRadius * mCheckedRingWidthPercent : 0);
            if (shapeType == TYPE_CIRCLE) {
                canvas.drawCircle(cx, cy, tmpRadius - ringWidth, mPaint);
            } else {
                RectF rectF = new RectF(cx - tmpRadius + ringWidth, cy - tmpRadius + ringWidth, cx + tmpRadius - ringWidth, cy + tmpRadius - ringWidth);
                if (cornerRadius > 0) {
                    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, mPaint);
                } else {
                    canvas.drawRect(rectF, mPaint);
                }

            }
        } else {
            mPaint.setColor(mBorderColor);
            if (shapeType == TYPE_CIRCLE) {
                canvas.drawCircle(cx, cy, mRadius, mPaint);
            } else {
                RectF rectF = new RectF(cx - mRadius, cy - mRadius, cx + mRadius, cy + mRadius);
                if (cornerRadius > 0) {
                    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, mPaint);
                } else {
                    canvas.drawRect(rectF, mPaint);
                }
            }
            mPaint.setColor(mColor);
            if (shapeType == TYPE_CIRCLE) {
                canvas.drawCircle(cx, cy, mRadius - mBorderWidth, mPaint);
            } else {
                RectF rectF = new RectF(cx - mRadius + mBorderWidth, cy - mRadius + mBorderWidth, cx + mRadius - mBorderWidth, cy + mRadius - mBorderWidth);
                if (cornerRadius > 0) {
                    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, mPaint);
                } else {
                    canvas.drawRect(rectF, mPaint);

                }
            }

        }
    }


    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
        invalidate();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
        invalidate();
    }


    public int getCheckedBorderColor() {
        return mCheckedBorderColor;
    }

    public void setCheckedBorderColor(int checkedBorderColor) {
        this.mCheckedBorderColor = checkedBorderColor;
        invalidate();
    }

    public int getCheckedRingColor() {
        return mCheckedRingColor;
    }

    public void setCheckedRingColor(int checkedRingColor) {
        this.mCheckedRingColor = checkedRingColor;
        invalidate();
    }

    public int getCheckedRingWidth() {
        return mCheckedRingWidth;
    }

    public void setCheckedRingWidth(int width) {
        this.mCheckedRingWidth = width;
        invalidate();
    }

    public float getCheckedRingWidthPercent() {
        return mCheckedRingWidthPercent;
    }

    public void setCheckedRingWidthPercent(float mCheckedRingWidthPercent) {
        this.mCheckedRingWidthPercent = mCheckedRingWidthPercent;
        if (mCheckedRingWidth <= 0)
            invalidate();
    }

    public void setShapeType(@ShapeTypeIntDef int type) {
        this.shapeType = type;
    }

   /* public void setDuration(@Duration int duration) {
        mDuration = duration;
    }*/


}
