package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 水平进度条
 * Created by dayuer on 2019/12/4.
 */
public class ControllerHorizontalProgressBar extends View {

    private Paint mBgtPaint;//底部背景画笔
    private Paint mProgressPaint;//progress画笔
    private int startX;//起始X坐标，保持不变
    private int endX;//终点坐标,保持不变
    private int currentX;//当前坐标,progress换算而来，不断增大

    private float mProgressBgHeight = 60;//进度条背景高度
    private float mProgressHeight = 50;//进度条高度
    private int mProgressBgColor = Color.BLACK;//进度条背景颜色
    private int mProgressColor = Color.BLUE;//进度条颜色
    protected Paint mPaint = new Paint();
    protected int mTextColor = Color.BLACK;//文字颜色
    //    /**
//     * size of text (sp)
//     */
    protected int mTextSize = 0;
    public ControllerHorizontalProgressBar(Context context){
        this(context, null);
    }

    public ControllerHorizontalProgressBar(Context context, @Nullable AttributeSet attrs){
        this(context, attrs, 0);

    }

    public ControllerHorizontalProgressBar(Context context,  @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ControllerHorizontalProgressBar, defStyleAttr, 0);

        mProgressBgHeight = a.getDimension(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresUnReachHeight, mProgressBgHeight);
        mProgressHeight = a.getDimension(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachHeight,mProgressHeight);

        mProgressBgColor = a.getColor(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresUnReachColor,mProgressBgColor);
        mProgressColor = a.getColor(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachColor, mProgressColor);
        mTextColor = a.getColor(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachTextColor, mTextColor);
        mTextSize = a.getColor(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachTextSize, mTextSize);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        a.recycle();

        init();
    }


    private void init() {

        //初始化画笔
        mBgtPaint = new Paint();
        mBgtPaint.setAntiAlias(true);//抗锯齿
        mBgtPaint.setStyle(Paint.Style.STROKE);
        //注意：这是笔尖样式的属性，Paint.Cap.ROUND设置笔尖为圆形，默认方形，
        mBgtPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgtPaint.setColor(mProgressBgColor);
//        mBgtPaint.setColor(Color.parseColor("#1F1F26"));//设置画笔颜色，这里是深灰色
        mBgtPaint.setStrokeWidth(60);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setColor(mProgressColor);
//        mProgressPaint.setColor(Color.parseColor("#54FC00"));
        mProgressPaint.setStrokeWidth(50);//这里的线条宽度比背景线条窄一些。

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //这里根据屏幕的宽高获取坐标，可以设置成自定义属性，让用户设置
//        startX = getWidth() / 2 - getWidth() / 3;
        endX = getWidth() / 2 + getWidth() / 3;
        startX = 0;
        currentX = startX + (int)(currentX * (endX - startX) /100 );
//        endX = getWidth() / 2 + getWidth() / 3;
//        startX = getWidth() / 2;
//        endX = getWidth() / 2;
//        currentX = startX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制底部背景线条(深灰色，区别于View背景色)
        canvas.drawLine(startX, getHeight() / 2, endX,
                getHeight() / 2, mBgtPaint);

        //绘制当前的progress
        canvas.drawLine(startX, getHeight() / 2, currentX,
                getHeight() / 2, mProgressPaint);

        float mm = (startX - currentX) / (startX - endX);
        //绘制的文本
        String text = mm + "%";

//        //拿到字体的宽度和高度
//        float textWidth = mPaint.measureText(text);
//        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
//        float radio = mm * 1.0f / getMax();
//        //如果到达最后，则未到达的进度条不需要绘制
//        if (progressPosX + textWidth > mRealWidth)
//        {
//            progressPosX = mRealWidth - textWidth;
//            noNeedBg = true;
//        }
//        // 绘制文本
//        if (mIfDrawText)
//        {
//            float progressPosX = (int) (mRealWidth * radio);
//            mPaint.setColor(mTextColor);
//            canvas.drawText(text, progressPosX, -textHeight, mPaint);
//        }
    }

    //更新进度
    public void updateProgress(int progress) {

        //这里progress的长度分成100个单位，再计算坐标，其实progress可以是实时的下载进度，
        //计算下载量百分比，然后再计算坐标，会相对精确
        if (progress <= 100){
            currentX = progress;
//            endX = getWidth() / 2 + getWidth() / 3;
            invalidate();//重新绘制才能生效
        }
    }
}

//{
//
//    private static final int DEFAULT_TEXT_SIZE = 10;
//    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
//    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
//    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
//    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
//    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
//
//    /**
//     * painter of all drawing things
//     */
//    protected Paint mPaint = new Paint();
//    /**
//     * color of progress number
//     */
//    protected int mTextColor = DEFAULT_TEXT_COLOR;
//    /**
//     * size of text (sp)
//     */
//    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
//
//    /**
//     * offset of draw progress
//     */
//    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);
//
//    /**
//     * height of reached progress bar
//     */
//    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
//
//    /**
//     * color of reached bar
//     */
//    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
//    /**
//     * color of unreached bar
//     */
//    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
//    /**
//     * height of unreached progress bar
//     */
//    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
//    /**
//     * view width except padding
//     */
//    protected int mRealWidth;
//
//    protected boolean mIfDrawText = true;
//
//    protected static final int VISIBLE = 0;
//
//    public ControllerHorizontalProgressBar(Context context, AttributeSet attrs)
//    {
//        this(context, attrs, 0);
//    }
//
//    public ControllerHorizontalProgressBar(Context context, AttributeSet attrs,
//                                           int defStyle)
//    {
//        super(context, attrs, defStyle);
//
//        setHorizontalScrollBarEnabled(true);
//
//        obtainStyledAttributes(attrs);
//
//        mPaint.setTextSize(mTextSize);
//        mPaint.setColor(mTextColor);
//
//    }
//
//    /**
//     * get the styled attributes
//     *
//     * @param attrs
//     */
//    private void obtainStyledAttributes(AttributeSet attrs) {
//        // init values from custom attributes
//        final TypedArray attributes = getContext().obtainStyledAttributes(
//                attrs, R.styleable.ControllerHorizontalProgressBar);
//
//        mTextColor = attributes
//                .getColor(
//                        R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachTextColor,
//                        DEFAULT_TEXT_COLOR);
//        mTextSize = (int) attributes.getDimension(
//                R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachTextSize,
//                mTextSize);
//
//        mReachedBarColor = attributes
//                .getColor(
//                        R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachColor,
//                        mTextColor);
//        mUnReachedBarColor = attributes
//                .getColor(
//                        R.styleable.ControllerHorizontalProgressBar_HorizontalProgresUnReachColor,
//                        DEFAULT_COLOR_UNREACHED_COLOR);
//        mReachedProgressBarHeight = (int) attributes
//                .getDimension(
//                        R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachHeight,
//                        mReachedProgressBarHeight);
//        mUnReachedProgressBarHeight = (int) attributes
//                .getDimension(
//                        R.styleable.ControllerHorizontalProgressBar_HorizontalProgresUnReachHeight,
//                        mUnReachedProgressBarHeight);
//        mTextOffset = (int) attributes
//                .getDimension(
//                        R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachTextOffset,
//                        mTextOffset);
//
//        int textVisible = attributes
//                .getInt(R.styleable.ControllerHorizontalProgressBar_HorizontalProgresReachTextVisibility,
//                        VISIBLE);
//        if (textVisible != VISIBLE) {
//            mIfDrawText = false;
//        }
//        attributes.recycle();
//    }
//
//    @Override
//    protected synchronized void onMeasure(int widthMeasureSpec,
//                                          int heightMeasureSpec)
//    {
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        if (heightMode != MeasureSpec.EXACTLY)
//        {
//
//            float textHeight = (mPaint.descent() + mPaint.ascent());
//            int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() + Math
//                    .max(Math.max(mReachedProgressBarHeight,
//                            mUnReachedProgressBarHeight), Math.abs(textHeight)));
//
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
//                    MeasureSpec.EXACTLY);
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//    }
//    @Override
//    protected synchronized void onDraw(Canvas canvas)
//    {
//        canvas.save();
//        //画笔平移到指定paddingLeft， getHeight() / 2位置，注意以后坐标都为以此为0，0
//        canvas.translate(getPaddingLeft(), getHeight() / 2);
//
//        boolean noNeedBg = false;
//        //当前进度和总值的比例
//        float radio = getProgress() * 1.0f / getMax();
//        //已到达的宽度
//        float progressPosX = (int) (mRealWidth * radio);
//        //绘制的文本
//        String text = getProgress() + "%";
//
//        //拿到字体的宽度和高度
//        float textWidth = mPaint.measureText(text);
//        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
//
//        //如果到达最后，则未到达的进度条不需要绘制
//        if (progressPosX + textWidth > mRealWidth)
//        {
//            progressPosX = mRealWidth - textWidth;
//            noNeedBg = true;
//        }
//
//        // 绘制已到达的进度
//        float endX = progressPosX - mTextOffset / 2;
//        if (endX > 0)
//        {
//            mPaint.setColor(mReachedBarColor);
//            mPaint.setStrokeWidth(mReachedProgressBarHeight);
//            canvas.drawLine(0, 0, endX, 0, mPaint);
//        }
//
//        // 绘制文本
//        if (mIfDrawText)
//        {
//            mPaint.setColor(mTextColor);
//            canvas.drawText(text, progressPosX, -textHeight, mPaint);
//        }
//
//        // 绘制未到达的进度条
//        if (!noNeedBg)
//        {
//            float start = progressPosX + mTextOffset / 2 + textWidth;
//            mPaint.setColor(mUnReachedBarColor);
//            mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
//            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
//        }
//
//        canvas.restore();
//
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh)
//    {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mRealWidth = w - getPaddingRight() - getPaddingLeft();
//
//    }
//    /**
//     * dp 2 px
//     *
//     * @param dpVal
//     */
//    protected int dp2px(int dpVal)
//    {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                dpVal, getResources().getDisplayMetrics());
//    }
//
//    /**
//     * sp 2 px
//     *
//     * @param spVal
//     * @return
//     */
//    protected int sp2px(int spVal)
//    {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
//                spVal, getResources().getDisplayMetrics());
//
//    }
//
//}
