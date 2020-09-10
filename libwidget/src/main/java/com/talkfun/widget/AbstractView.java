package com.talkfun.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ccy on 2019/5/18/12:09
 */
public abstract class AbstractView extends View {
    public AbstractView(Context context) {
        this(context, null);
    }

    public AbstractView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    protected abstract void init();

    protected abstract void resloveAttr(Context context, AttributeSet attrs);

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
}
