package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by dayuer on 19/9/2.
 * imageview 可加阴影和圆角效果
 */
public class ControllerCustomRoundAngleImageView extends AppCompatImageView {

    private float mRadius; //圆角
    private float mShadowRadius;//圆角阴影
    private int mShadowColor;//阴影颜色
    private boolean mIsCircle;//是否为圆形
    private boolean mIsShadow;//是否打开阴影
    private int width;
    private int height;
    private int imageWidth;
    private int imageHeight;
    private Paint mPaint;

    public ControllerCustomRoundAngleImageView(final Context context) {
        this(context, null);
    }

    public ControllerCustomRoundAngleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerCustomRoundAngleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ScaleType.FIT_XY); //设置图片适应模式
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.ControllerCustomRoundAngleImageView, defStyle, 0);
        if (ta != null){ //从布局文件中获取设置参数
            mRadius = ta.getDimension(R.styleable.ControllerCustomRoundAngleImageView_image_radius, 0);
            mShadowRadius = ta.getDimension(R.styleable.ControllerCustomRoundAngleImageView_image_shadow_radius, 0);
            mIsCircle = ta.getBoolean(R.styleable.ControllerCustomRoundAngleImageView_image_circle, false);
            mIsShadow = ta.getBoolean(R.styleable.ControllerCustomRoundAngleImageView_image_shadow, false);
            mShadowColor = ta.getInteger(R.styleable.ControllerCustomRoundAngleImageView_shadow_color,0xffe4e4e4);
            ta.recycle();
        }else {
            mRadius = 0;
            mShadowRadius = 0;
            mIsCircle = false;
            mIsShadow = false;
            mShadowColor = 0xffe4e4e4;
        }

    }
    @Override
    public void onDraw(Canvas canvas) {
        width = canvas.getWidth() - getPaddingLeft() - getPaddingRight();//控件实际大小
        height = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        if (!mIsShadow) //如果不使用阴影将阴影弧度置为0
            mShadowRadius = 0;

        //实际图片的宽高应该减去左右阴影或上下阴影
        imageWidth = width - (int) mShadowRadius * 2;
        imageHeight = height - (int) mShadowRadius * 2;

        Bitmap image = drawableToBitmap(getDrawable());
        if (image == null){
            return;
        }
        Bitmap reSizeImage = reSizeImage(image, imageWidth, imageHeight); //重置图片大小
        initPaint();


        if (mIsCircle) { //判断是否画圆
            canvas.drawBitmap(createCircleImage(reSizeImage),
                    getPaddingLeft(), getPaddingTop(), null);

        } else {
            canvas.drawBitmap(createRoundImage(reSizeImage),
                    getPaddingLeft(), getPaddingTop(), null);
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    //创建带弧度的图片
    private Bitmap createRoundImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap can't be null");
        }
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Bitmap targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBitmap);

        mPaint.setShader(bitmapShader);

        RectF rect = new RectF(0, 0, imageWidth, imageHeight);
        targetCanvas.drawRoundRect(rect, mRadius, mRadius, mPaint);

        if (mIsShadow){  //判断是否画阴影
            mPaint.setShader(null);
            mPaint.setColor(mShadowColor);
            mPaint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
            Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);

            RectF rectF = new RectF(mShadowRadius, mShadowRadius, width - mShadowRadius, height - mShadowRadius);
            canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setShadowLayer(0, 0, 0, 0xffffff);
            canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint);
            return target;
        }else {
            return targetBitmap;
        }

    }

    //创建圆形的图片
    private Bitmap createCircleImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap can't be null");
        }
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Bitmap targetBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBitmap);

        mPaint.setShader(bitmapShader);

        targetCanvas.drawCircle(imageWidth / 2, imageWidth / 2, Math.min(imageWidth, imageHeight) / 2,
                mPaint);

        if (mIsShadow){  //判断是否画阴影
            mPaint.setShader(null);
            mPaint.setColor(mShadowColor);
            mPaint.setShadowLayer(mShadowRadius, 1, 1, mShadowColor);
            Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);

            canvas.drawCircle(width / 2, height / 2, Math.min(imageWidth, imageHeight) / 2,
                    mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setShadowLayer(0, 0, 0, 0xffffff);
            canvas.drawBitmap(targetBitmap, mShadowRadius, mShadowRadius, mPaint);
            return target;
        }else {
            return targetBitmap;
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicHeight(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 重设Bitmap的宽高
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap reSizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算出缩放比
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 矩阵缩放bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }


}
