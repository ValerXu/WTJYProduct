//package com.talkfun.widget;
//
///**
// * Created by ccy on 2019/5/29/17:15
// */
//
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//
//import com.talkfun.widget.util.ColorUtils;
//
///**
// * 涟漪效果
// * Created by ccy
// */
//public class CircleWaveView extends View {
//    private float maxWaveRadius = 50;//扩散半径
//    private long waveTime = 3000;//一个涟漪扩散的时间
//    private int waveRate = 8;//涟漪的个数
//    private Paint paint;
//    private float[] waveList;//涟漪队列
//    private int centerX;//控件中心x坐标
//    private int centerY;//控件中心y坐标
//    private int mColor = Color.parseColor("#ff0000");
//    private int[] rgbArr;
//    private ValueAnimator valueAnimator;
//
//    public CircleWaveView(Context context) {
//        this(context, null);
//    }
//
//    public CircleWaveView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public CircleWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        centerX = getWidth() / 2;
//        centerY = getHeight() / 2;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        canvas.save();
//        for (Float waveRadius : waveList) {
//            paint.setColor(Color.argb((int) (255 * (1 - waveRadius)), rgbArr[0], rgbArr[1], rgbArr[2]));//根据进度产生一个argb颜色
//            canvas.drawCircle(centerX, centerY, waveRadius * maxWaveRadius, paint);//根据进度计算扩散半径
//        }
//
//    }
//
//    //...构造方法略
//    private void init() {
//        paint = new Paint();
//        waveList = new float[waveRate];
//        rgbArr = ColorUtils.toRGB(mColor);
//        valueAnimator = ValueAnimator.ofFloat(0, 1);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setDuration(waveTime);
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                Float value = (Float) animation.getAnimatedValue();
//                for (int i = 0; i < waveList.length; i++) {
//                    float v = (value - i * 1.0f / waveRate);
//                    if (v < 0 && waveList[i] > 0) {
//                        v += 1;
//                    }
//                    waveList[i] = v;
//                }
//                invalidate();
//            }
//        });
//        valueAnimator.start();
//    }
//
//    /**
//     * 开启涟漪效果
//     */
//    public void startAnimation() {
//        if (valueAnimator == null) {
//            return;
//        }
//        valueAnimator.start();
//    }
//
//    /**
//     * 停止涟漪效果
//     */
//    public void stopAnimation() {
//        if (valueAnimator == null) {
//            return;
//        }
//        valueAnimator.end();
//    }
//
//    /**
//     * 涟漪扩散的时间
//     *
//     * @param waveTime
//     */
//    public void setWaveTime(long waveTime) {
//        this.waveTime = waveTime;
//    }
//
//    /**
//     * 涟漪的个数
//     *
//     * @param waveRate
//     */
//    public void setWaveRate(int waveRate) {
//        this.waveRate = waveRate;
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (valueAnimator != null) {
//            valueAnimator.end();
//        }
//    }
//
//}