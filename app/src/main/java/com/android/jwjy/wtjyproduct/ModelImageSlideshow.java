package com.android.jwjy.wtjyproduct;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dayuer on 19/7/2.
 * 轮播图模块
 */
public class ModelImageSlideshow extends FrameLayout {

    private static final String TAG = "ImageSlideshow";

    private Context context;
    private View contentView;
    private ViewPager vpImageTitle;
    private LinearLayout llDot;
    private int count;
    private List<View> viewList;
    private boolean isAutoPlay;
    private Handler handler;
    private int currentItem;
    private Animator animatorToLarge;
    private Animator animatorToSmall;
    private SparseBooleanArray isLarge;
    private List<ImageTitleBean> imageTitleBeanList;
    private int dotSize = 12;
    private int dotSpace = 12;
    private int delay = 1000;
    private int height = 0;
    private int width = 0;

    public ModelImageSlideshow(Context context) {
        this(context, null);
    }

    public ModelImageSlideshow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModelImageSlideshow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // 初始化View
        initView();
        // 初始化Animator
        initAnimator();
        // 初始化数据
        initData();
    }

    private void initData() {
        imageTitleBeanList = new ArrayList<>();
    }

    private void initAnimator() {
        animatorToLarge = AnimatorInflater.loadAnimator(context, R.animator.scale_to_large);
        animatorToSmall = AnimatorInflater.loadAnimator(context, R.animator.scale_to_small);
    }

    /**
     * 初始化View
     */
    private void initView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.modelimageslideshow_2, this, true);
        vpImageTitle = findViewById(R.id.vp_image_title);
        llDot = findViewById(R.id.ll_dot);
        DisplayMetrics dm = context.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
//        //设置使用控件的宽高
        LayoutParams LP = (LayoutParams) llDot.getLayoutParams();
        LP.topMargin = height / 5;
        llDot.setLayoutParams(LP);
    }

    // 设置小圆点的大小
    public void setDotSize(int dotSize) {
        this.dotSize = dotSize;
    }

    // 设置小圆点的间距
    public void setDotSpace(int dotSpace) {
        this.dotSpace = dotSpace;
    }

    // 设置图片轮播间隔时间
    public void setDelay(int delay) {
        this.delay = delay;
    }

    // 添加图片
    public void addImageUrl(String imageUrl) {
        ImageTitleBean imageTitleBean = new ImageTitleBean();
        imageTitleBean.setImageUrl(imageUrl);
        imageTitleBeanList.add(imageTitleBean);
    }
    // 添加图片和跳转的url
    public void addImageUrlAndSkipUrl(String imageSkipUrl,String imageUrl) {
        ImageTitleBean imageTitleBean = new ImageTitleBean();
        imageTitleBean.setImageUrl(imageUrl);
        if (imageSkipUrl == null){
            imageSkipUrl = "";
        }
        imageTitleBean.setImageSkipUrl(imageSkipUrl);
        imageTitleBeanList.add(imageTitleBean);
    }
    // 添加本地图片
    public void addImageUrl(int imageId) {
        ImageTitleBean imageTitleBean = new ImageTitleBean();
        imageTitleBean.setImageId(imageId);
        imageTitleBeanList.add(imageTitleBean);
    }
    // 添加图片和标题
    public void addImageTitle(String imageUrl, String title, int imageId) {
        ImageTitleBean imageTitleBean = new ImageTitleBean();
        imageTitleBean.setImageUrl(imageUrl);
        imageTitleBean.setTitle(title);
        imageTitleBean.setImageId(imageId);
        imageTitleBeanList.add(imageTitleBean);
    }

    // 添加图片和标题的JavaBean
    public void addImageTitleBean(ImageTitleBean imageTitleBean) {
        imageTitleBeanList.add(imageTitleBean);
    }

    // 设置图片和标题的JavaBean数据列表
    public void setImageTitleBeanList(List<ImageTitleBean> imageTitleBeanList) {
        this.imageTitleBeanList = imageTitleBeanList;
    }

    // 设置完后最终提交
    public void commit() {
        if (imageTitleBeanList != null) {
            count = imageTitleBeanList.size();
            // 设置ViewPager
            setViewPager(imageTitleBeanList);
            // 设置指示器
            setIndicator();
            // 开始播放
            starPlay();
        } else {
            Log.e(TAG, "数据为空");
        }
    }

    /**
     * 设置指示器
     */
    private void setIndicator() {
        if (count <= 1) { //如果只有一张图片或没有图片  不加载原点
            return;
        }
        isLarge = new SparseBooleanArray();
        // 记得创建前先清空数据，否则会受遗留数据的影响。
        llDot.removeAllViews();
        for (int i = 0; i < count; i ++) {
            View view = new View(context);
            view.setBackgroundResource(R.drawable.dot_unselected);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dotSize * 3, dotSize);
            layoutParams.leftMargin = dotSpace ;
            layoutParams.rightMargin = dotSpace ;
            layoutParams.topMargin = dotSpace / 2;
            layoutParams.bottomMargin = dotSpace / 2;
            llDot.addView(view, layoutParams);
            isLarge.put(i, false);
        }
        if (llDot.getChildAt(0) != null) {
            llDot.getChildAt(0).setBackgroundResource(R.drawable.dot_selected);
            animatorToLarge.setTarget(llDot.getChildAt(0));
            animatorToLarge.start();
            isLarge.put(0, true);
        }
    }

    /**
     * 开始自动播放图片
     */
    private void starPlay() {
        // 如果少于2张就不用自动播放了
        if (count < 2) {
            isAutoPlay = false;
        } else {
            isAutoPlay = true;
            handler = new Handler();
            handler.postDelayed(task, delay);
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                // 位置循环
                currentItem = currentItem % (count + 1) + 1;
                // 正常每隔3秒播放一张图片
                vpImageTitle.setCurrentItem(currentItem);
                handler.postDelayed(task, delay);
            } else {
                // 如果处于拖拽状态停止自动播放，会每隔5秒检查一次是否可以正常自动播放。
                handler.postDelayed(task, 5000);
            }
        }
    };

//    // 创建监听器接口
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    // 声明监听器
//    private OnItemClickListener onItemClickListener;
//
//    // 提供设置监听器的公共方法
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        onItemClickListener = listener;
//    }

    class ImageTitlePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = viewList.get(position);
            // 设置Item的点击监听器
            view.setOnClickListener(v -> {
                // 注意：位置是position-1
//                    onItemClickListener.onItemClick(v, position - 1);
                if (position < imageTitleBeanList.size() && position == 0){ //只有一张图片的时候
                    try {
                        if (imageTitleBeanList.get(position).imageSkipUrl.equals("")){
                            return;
                        }
                        Uri uri = Uri.parse(imageTitleBeanList.get(position).imageSkipUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } catch (Exception e){
//                        Toast.makeText(context,"图片链接：" + imageTitleBeanList.get(position).imageSkipUrl + "，图片链接不正确！",Toast.LENGTH_LONG).show();
                    }
                } else if ((position - 1) < imageTitleBeanList.size() && (position - 1) >= 0){ //大于一张图片的时候
                    try {
                        if (imageTitleBeanList.get((position - 1)).imageSkipUrl.equals("")){
                            return;
                        }
                        Uri uri = Uri.parse(imageTitleBeanList.get((position - 1)).imageSkipUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } catch (Exception e){
//                        Toast.makeText(context,"图片链接：" + imageTitleBeanList.get(position).imageSkipUrl + "，图片链接不正确！",Toast.LENGTH_LONG).show();
                    }
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    /**
     * 设置ViewPager
     *
     * @param imageTitleBeanList
     */
    private void setViewPager(List<ImageTitleBean> imageTitleBeanList) {
        // 设置View列表
        setViewList(imageTitleBeanList);
        vpImageTitle.setAdapter(new ImageTitlePagerAdapter());
        // 从第1张图片开始（位置刚好也是1，注意：0位置现在是最后一张图片）
        currentItem = 1;
        vpImageTitle.setCurrentItem(1);
        vpImageTitle.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 遍历一遍子View，设置相应的背景。
                for (int i = 0; i < llDot.getChildCount(); i++) {
                    if (i == position - 1) {// 被选中
                        llDot.getChildAt(i).setBackgroundResource(R.drawable.dot_selected);
                        if (!isLarge.get(i)) {
                            animatorToLarge.setTarget(llDot.getChildAt(i));
                            animatorToLarge.start();
                            isLarge.put(i, true);
                        }
                    } else {// 未被选中
                        llDot.getChildAt(i).setBackgroundResource(R.drawable.dot_unselected);
                        if (isLarge.get(i)) {
                            animatorToSmall.setTarget(llDot.getChildAt(i));
                            animatorToSmall.start();
                            isLarge.put(i, false);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (imageTitleBeanList.size() <= 1){
                    return;
                }
                switch (state) {
                    // 闲置中
                    case ViewPager.SCROLL_STATE_IDLE:
                        // “偷梁换柱”
                        if (vpImageTitle.getCurrentItem() == 0) {
                            vpImageTitle.setCurrentItem(count, false);
                        } else if (vpImageTitle.getCurrentItem() == count + 1) {
                            vpImageTitle.setCurrentItem(1, false);
                        }
                        currentItem = vpImageTitle.getCurrentItem();
                        isAutoPlay = true;
                        break;
                    // 拖动中
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        isAutoPlay = false;
                        break;
                    // 设置中
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isAutoPlay = true;
                        break;
                }
            }
        });
    }

    /**
     * 根据出入的数据设置View列表
     *
     * @param imageTitleBeanList
     * 使用Glide框架加载图片
     */
    private void setViewList(List<ImageTitleBean> imageTitleBeanList) {
        viewList = new ArrayList<>();
        for (int i = 0; i < count + 2; i ++) {
            View view = LayoutInflater.from(context).inflate(R.layout.modelimageslideshow_1, null);
            ControllerCustomRoundAngleImageView ivImage = (ControllerCustomRoundAngleImageView) view.findViewById(R.id.iv_image);
            ivImage.setImageDrawable(getResources().getDrawable(R.drawable.defaultslideimage1));//如果没有url，加载默认图片
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivImage.getLayoutParams();
            lp.leftMargin = width / 50;
            lp.rightMargin = width / 50;
            lp.topMargin = width / 50;
            ivImage.setLayoutParams(lp);
//            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            if (i == 0) {// 将最前面一页设置成本来最后的那页
                String mm = null;
                if (imageTitleBeanList.size() > (count - 1) && (count - 1) >= 0){
                    mm = imageTitleBeanList.get(count - 1).getImageUrl();
                }
                if (mm != null){
                    Glide.with(context).
                            load(mm).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    })
                            .error(getResources().getDrawable(R.drawable.defaultslideimage1)).into(ivImage);
//                tvTitle.setText(imageTitleBeanList.get(count - 1).getTitle());
                }
            } else if (i == count + 1) {// 将最后面一页设置成本来最前的那页
                String mm = null;
                if ( imageTitleBeanList.size() > 0){
                    mm = imageTitleBeanList.get(0).getImageUrl();
                }
                if (mm != null) {
                    Glide.with(context).
                            load(mm).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    })
                            .error(getResources().getDrawable(R.drawable.defaultslideimage1)).into(ivImage);
                    //                tvTitle.setText(imageTitleBeanList.get(0).getTitle());
                }
            } else {
                String mm = null;
                if ( imageTitleBeanList.size() > (i - 1)&& (i - 1) >= 0){
                    mm = imageTitleBeanList.get(i - 1).getImageUrl();
                }
                if (mm != null){
                    Glide.with(context).
                            load(mm).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain","加载失败 errorMsg:"+(e!=null?e.getMessage():"null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain","成功  Drawable Name:"+resource.getClass().getCanonicalName());
                            return false;
                        }
                    })
                            .error(getResources().getDrawable(R.drawable.defaultslideimage1)).into(ivImage);
//                tvTitle.setText(imageTitleBeanList.get(i - 1).getTitle());
                }
            }
            // 将设置好的View添加到View列表中
            viewList.add(view);
            if (count == 0 || count == 1) { // 如果没有图片或只有一张图片时，只添加一张默认图片
                break;
            }
        }
    }

    /**
     * 释放资源
     */
    public void releaseResource() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            context = null;
        }
    }

    private class ImageTitleBean {

        private String imageUrl;
        private String imageSkipUrl;  //点击图片跳转的url
        private String title;
        private int imageId;


        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }


        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageSkipUrl() {
            return imageSkipUrl;
        }

        public void setImageSkipUrl(String imageSkipUrl) {
            this.imageSkipUrl = imageSkipUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
