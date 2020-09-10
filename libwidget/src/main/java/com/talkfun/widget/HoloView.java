package com.talkfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.talkfun.libwidget.R;


/**
 * 光晕圆
 * Created by ccy on 2019/5/17/15:31
 */
public class HoloView extends View {
    /**
     * 如有渐变色
     * 以渐变色为准
     * 背景颜色
     */
    private int mTextbgColor = Color.parseColor("#ffffff");
    /**
     * 渐变 开始颜色
     */
    private int mTextBgColorStart = Color.parseColor("#ffffff");
    /**
     * 渐变 结束颜色
     */
    private int mTextBgColorEnd = Color.parseColor("#ffffff");

    /**
     * 圆环颜色
     */
    private int mRingColor = Color.parseColor("#ffffff");
    /**
     * 圆环的比例
     */
    private float mRingWidthPercent = 0.2f;

    /**
     * 文本
     */
    private String mText;
    /**
     * 文本颜色
     */
    private int mTextColor = Color.parseColor("#000000");
    private Paint mTextBgPaint;
    private Paint mRingPaint;
    protected float mRadius;
    private TextPaint mTextPaint;
    private float mTextSize = 15;
    private float mTextBgRadius;

    public HoloView(Context context) {
        this(context, null);
    }

    public HoloView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HoloView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resloveAttr(context, attrs);
        init();
    }

    private void resloveAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HoloView);
        mTextbgColor = typedArray.getColor(R.styleable.HoloView_textBgColor, mTextbgColor);
        mRingColor = typedArray.getColor(R.styleable.HoloView_ringColor, mRingColor);
        mTextBgColorStart = typedArray.getColor(R.styleable.HoloView_textBgColorStart, -1);
        mTextBgColorEnd = typedArray.getColor(R.styleable.HoloView_textBgColorEnd, -1);
        mRingWidthPercent = typedArray.getFloat(R.styleable.HoloView_ringWidthPercent, mRingWidthPercent);
        mTextColor = typedArray.getColor(R.styleable.HoloView_textColor, mTextColor);
        mTextSize = typedArray.getDimension(R.styleable.HoloView_textSize, mTextSize);
        mText = typedArray.getString(R.styleable.HoloView_text);

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
        mTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextBgPaint.setStyle(Paint.Style.FILL);
        mTextBgPaint.setAntiAlias(true);
        mRingPaint = new Paint(mTextBgPaint);
        mTextPaint = new TextPaint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
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
        mRadius = Math.min(width, height) * 0.5f;
        float cx = width * 0.5f + paddingLeft;
        float cy = height * 0.5f + paddingTop;
        mRingPaint.setColor(mRingColor);
        canvas.drawCircle(cx, cy, mRadius, mRingPaint);
        mTextBgRadius = mRadius * (1 - mRingWidthPercent);
        if (mTextBgColorStart != -1 && mTextBgColorEnd != -1) {//目前仅支持从上到下的线性渐变，之后在拓展
            LinearGradient backGradient = new LinearGradient(0, 0, 0, mTextBgRadius * 2, new int[]{mTextBgColorStart, mTextBgColorEnd}, null, Shader.TileMode.CLAMP);
            mTextBgPaint.setShader(backGradient);
        } else {
            mTextBgPaint.setColor(mTextbgColor);
        }
        canvas.drawCircle(cx, cy, mTextBgRadius, mTextBgPaint);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mText))
            return;
        mTextPaint.setTextSize(getRightSize());
        mTextPaint.setColor(mTextColor);
        textCenter(canvas, mText.split("\n"));
    }
/*    ascent 和descent的距离的一半作为中心位置，然后减去descent的距离就是中心位置到基线y的距离，如下所示
    float offset= (descent - ascent) / 2 - descent; --》mTextPaint.descent() + mTextPaint.ascent()
    这样看100%明白l ，计算出中心位置到基线y的距离，再加上中心位置的y坐标就等于基线的y坐标，如下所示
    float baseY = offset + middle;*/

    private void textCenter(Canvas canvas, String[] texts) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // top绝对值
        float top = Math.abs(fontMetrics.top);
        // ascent绝对值
        float ascent = Math.abs(fontMetrics.ascent);
        // descent，正值
        float descent = fontMetrics.descent;
        // bottom，正值
        float bottom = fontMetrics.bottom;
        // 行数
        int textLines = texts.length;
        // 文本高度
        float textHeight = top + bottom;
        // 文本总高度
        float textTotalHeight = textHeight * textLines;
        // 基数
        float basePosition = (textLines - 1) / 2f;
        int baseX = canvas.getWidth() / 2;
        int baseY = (canvas.getHeight() / 2);
        for (int i = 0; i < textLines; i++) {
            // 文本宽度
            float textWidth = mTextPaint.measureText(texts[i]);
            // 文本baseline在y轴方向的位置
            float baselineY;
            if (i < basePosition) {
                // x轴上，值为负
                // 总高度的/2 - 已绘制的文本高度 - 文本的top值（绝对值）
                baselineY = -(textTotalHeight / 2 - textHeight * i - top);

            } else if (i > basePosition) {
                // x轴下，值为正
                // 总高度的/2 - 未绘制的文本高度 - 文本的bottom值（绝对值）
                baselineY = textTotalHeight / 2 - textHeight * (textLines - i - 1) - bottom;

            } else {
                // x轴中，值为正
                // 计算公式请参考单行文本居中公式
                baselineY = (ascent - descent) / 2;
            }
            canvas.drawText(texts[i], baseX - textWidth / 2, baseY + baselineY, mTextPaint);
        }
    }

    //获得合适的字号
    private float getRightSize() {
        //根据最大值，计算出当前文本占用的宽度
        float preWidth = mTextPaint.measureText(mText);
        //如果文本占用的宽度比画布宽度小，说明不用伸缩，直接返回当前字号
        if (preWidth < mTextBgRadius * 2) {
            return mTextSize;
        }
        //已知当前文本字号、文本占用宽度、画布宽度，计算出合适的字号，并返回
        return mTextSize * mTextBgRadius * 2 / preWidth;
    }

    //实际绘制时，需要使用像素进行绘制，此处提供sp 转 px的方法
    private int sp2px(float spValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    /**
     * 文本背景颜色
     *
     * @param textbgColor
     */
    public void setTextBgColor(@ColorInt int textbgColor) {
        this.mTextbgColor = textbgColor;
        invalidate();
    }

    /**
     * 设置文本颜色
     *
     * @param mTextColor
     */
    public void setTextColor(@ColorInt int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    /**
     * 设置文本背景渐变颜色开始
     */
    public void setTextBgColorStart(@ColorInt int textBgColorStart) {
        this.mTextBgColorStart = textBgColorStart;
        invalidate();
    }

    /**
     * 设置文本背景渐变颜色结束
     */
    public void setTextBgColorEnd(@ColorInt int textBgColorEnd) {
        this.mTextBgColorStart = textBgColorEnd;
        invalidate();
    }
}
