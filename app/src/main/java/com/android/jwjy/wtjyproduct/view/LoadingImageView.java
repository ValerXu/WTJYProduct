package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;

/**
 * Created by ccy on 2018/9/14/14:28
 */
public class LoadingImageView extends android.support.v7.widget.AppCompatImageView {
    private static final int DEFAULT_MIN_WIDTH = 400; //View默认最小宽度
    private static final float doughnutRaduisPercent = 0.9f; //圆环外圆半径占View最大半径的百分比
    private static final float doughnutWidthPercent = 0.2f; //圆环宽度占View最大半径的百分比
    //圆环颜色
    private static int[] doughnutColors = new int[]{Color.WHITE, Color.parseColor("#9956acf5")};
    private Paint paint = new Paint(); //画笔
    private float width; //自定义view的宽度
    private float height; //自定义view的高度
    private float currentAngle = 0f; //当前旋转角度
    private float raduis; //自定义view的最大半径
    private boolean isRotate;
    private float doughnutWidth;
    private boolean isFirst = true;
    private RectF rectF;

    public LoadingImageView(Context context) {
        this(context, null);
    }

    public LoadingImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        initPaint();
    }

    private void resetParams() {
        width = getWidth();
        height = getHeight();
        raduis = Math.min(width, height) / 2;
        doughnutWidth = raduis * doughnutWidthPercent;//圆环宽度
        paint.setStrokeWidth(doughnutWidth);
        rectF = new RectF(-raduis * doughnutRaduisPercent, -raduis * doughnutRaduisPercent, raduis * doughnutRaduisPercent, raduis * doughnutRaduisPercent);
        isFirst = false;
        paint.setShader(new SweepGradient(0, 0, doughnutColors, null));
    }

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRotate) {
            if (isFirst) {
                resetParams();
            }
            rotate(canvas);
        }

    }

    private void rotate(Canvas canvas) {
        canvas.translate(width / 2, height / 2);
        canvas.rotate(currentAngle, 0, 0);
        currentAngle = currentAngle >= 360f ? currentAngle - 360f : currentAngle + 5f;
        //圆环外接矩形
        canvas.drawArc(rectF, 0, 360, false, paint);
        if (handler != null) {
            handler.postDelayed(runnable, 16);//16毫秒后启动子线程
        }
    }


    /**
     * 当布局为wrap_content时设置默认长宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();//刷新界面
        }
    };

    /**
     * 设置是否显示加载圈
     *
     * @param isRotate
     */
    public void setIsRotate(boolean isRotate) {
        this.isRotate = isRotate;
        invalidate();
    }

    public boolean isRotate() {
        return this.isRotate;
    }
}