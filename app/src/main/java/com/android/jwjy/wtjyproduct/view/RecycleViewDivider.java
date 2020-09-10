package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ccy on 2019/5/8/17:40
 */
public class RecycleViewDivider extends RecyclerView.ItemDecoration {
    private Context context;
    private int orientation;
    private int dividerHeight = 2;
    private int dividerColor;
    private int leftOffset;
    private int rightOffset;
    private Paint mPaint;

    private RecycleViewDivider() {

    }

    private RecycleViewDivider(Build build) {
        context = build.context;
        orientation = build.orientation;
        dividerHeight = build.dividerHeight;
        dividerColor = build.dividerColor;
        leftOffset = build.leftOffset;
        rightOffset = build.rightOffset;
        setPaint();

    }

    private void setPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public static class Build {
        private Context context;
        private int orientation;
        private int dividerHeight;
        private int dividerColor;
        private int leftOffset;
        private int rightOffset;

        public Build context(Context context) {
            this.context = context;
            return this;
        }

        public Build orientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Build dividerHeight(int dividerHeight) {
            this.dividerHeight = dividerHeight;
            return this;
        }

        public Build dividerColor(@ColorInt int dividerColor) {
            this.dividerColor = dividerColor;
            return this;
        }

        public Build leftOffset(@DimenRes int leftOffset) {
            this.leftOffset = leftOffset;
            return this;
        }

        public Build rightOffset(@DimenRes int rightOffset) {
            this.rightOffset = rightOffset;
            return this;
        }

        public RecycleViewDivider create() {
            return new RecycleViewDivider(this);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, dividerHeight);
        } else {
            outRect.set(0, 0, dividerHeight, 0);
        }
    }
    //绘制分割线

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + layoutParams.rightMargin;
            int right = left + dividerHeight;
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft() + leftOffset;
        int right = parent.getMeasuredWidth() - parent.getPaddingRight() - rightOffset;
        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + dividerHeight;
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
