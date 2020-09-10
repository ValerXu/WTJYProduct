package com.android.jwjy.wtjyproduct;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by dayuer on 19/7/2.
 * 加载网络图片
 */
public class ModelHtmlUtils {
//    private static ModelHtmlUtils instance;
    private Activity activity;
    private TextView text;
    private Drawable pic;
    private String resource;
    private static ModelHtmlUtils mInstance;
    public ModelHtmlUtils(Activity activity,TextView text){
        this.activity =activity;
        this.text = text;
    }
    public static ModelHtmlUtils getInstance(Activity activity,TextView text){
        if (mInstance == null){
//            mInstance = new ModelHtmlUtils(activity,text);
            mInstance = new ModelHtmlUtils(activity,text);
        }
        return mInstance;
    }

    public void setHtmlWithPic(String resource) {
        this.resource = resource;
        if (Build.VERSION.SDK_INT >= 24) {
            Spanned span = Html.fromHtml(resource, Html.FROM_HTML_MODE_COMPACT, imageGetter, null);
            if (span.toString().equals("")){
                text.setText(resource);
                return;
            }
            text.setText(Html.fromHtml(resource, Html.FROM_HTML_MODE_COMPACT, imageGetter, null));
        } else {
            Spanned span = Html.fromHtml(resource, imageGetter, null);
            if (span.toString().equals("")){
                text.setText(resource);
                return;
            }
            text.setText(Html.fromHtml(resource, imageGetter, null));
        }
    }
    Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String s) {
            if (pic != null) {
                Log.d("TAG", "显示");
                return pic;
            }
            else {
                Log.d("TAG", "加载"+s);
                getPic(s);
            }
            return null;
        }
    };
    /**
     * 加载网络图片
     * @param s
     */
    private void getPic(final String s) {
        new Thread(() ->{
                try {
                    final Drawable drawable = Drawable.createFromStream(new URL(s).openStream(), "");
                    activity. runOnUiThread(() ->{
                            if (drawable != null) {
                                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                                DisplayMetrics outMetrics = new DisplayMetrics();
                                wm.getDefaultDisplay().getMetrics(outMetrics);
                                float picW = drawable.getIntrinsicWidth();
                                float picH = drawable.getIntrinsicHeight();
                                int width = outMetrics.widthPixels;
                                drawable.setBounds(0,0,(int)picW,(int)picH);
                                pic = drawable;
                                if (Build.VERSION.SDK_INT >= 24) {
                                    Spanned span = Html.fromHtml(resource, Html.FROM_HTML_MODE_COMPACT, imageGetter, null);
                                    if (span.toString().equals("")){
                                        text.setText(resource);
                                        return;
                                    }
                                    text.setText(Html.fromHtml(resource, Html.FROM_HTML_MODE_COMPACT, imageGetter, null));
                                } else {
                                    Spanned span = Html.fromHtml(resource, imageGetter, null);
                                    if (span.toString().equals("")){
                                        text.setText(resource);
                                        return;
                                    }
                                    text.setText(Html.fromHtml(resource, imageGetter, null));
                                }
                            }
                        });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
