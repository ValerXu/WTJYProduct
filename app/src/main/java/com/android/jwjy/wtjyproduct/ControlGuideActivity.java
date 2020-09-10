package com.android.jwjy.wtjyproduct;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dayuer on 19/7/2.
 * 功能 引导页
 */
public class ControlGuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    //ViewPager滑动冲突: 直接调用: disableWhenHorizontalMove()
    private ViewPager mViewPager;
    private List<View> mViews = new ArrayList<>();//引导页容器，存储所有引导页视图
    private List<ImageView> mImageViews = new ArrayList<>();//引导页图片容器，存储所有引导页图片
    private int[]  imgRes = new int[]{//引导页图片索引容器，存储所有引导页图片索引
            R.mipmap.guide_1,R.mipmap.guide_2,R.mipmap.guide_3
    };
    private Button mButton;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelStatusBarUtil.setStatusBarColor(this,R.color.white);
//        ModelViewUtils.setImmersionStateMode(this);
        setContentView(R.layout.guide_layout);
        initpoint();
        initImg();
        mViewPager = findViewById(R.id.guide_viewpager);
        mViewPager.setAdapter(new viewpagerAdapter());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(true,new ModelDepthPageTransformer());
        mButton = findViewById(R.id.guide_start);
        mButton.setOnClickListener(view->{
            Intent intent = new Intent(ControlGuideActivity.this,ControlMainActivity.class);
            intent.putExtra("isXieyi",true);
            startActivity(intent);
            finish();
        });
    }

    //初始化图片
    private void initImg() {
        for (int i = 0; i < imgRes.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.viewpager_item_view,null);
            ImageView imageView = view.findViewById(R.id.guide_imageview);
            imageView.setBackgroundResource(imgRes[i]);
            mViews.add(view);
        }
    }


    //初始化圆点
    private void initpoint() {
        //获取layout
        mLayout = findViewById(R.id.point_ly);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置每一个view即圆点的对左的偏移量
        params.setMargins(15,0,0,0);
        //根据图片多少来确定个数
        for (int i = 0; i < imgRes.length; i++) {

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.point_normal);
            imageView.setLayoutParams(params); //把上面的控件属性设置到LinearLayout中
            if (i == 0){ //默认第一张为红色圆点
                imageView.setSelected(true);
            }else{
                imageView.setSelected(false);
            }
            //把圆点这个子视图导入我们的LinearLayout里面
            mLayout.addView(imageView);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = 30;
            lp.width = 30;
            imageView.setLayoutParams(lp);
            mImageViews.add(imageView);//跟着viewpager变换颜色
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //滑动时改变圆点的状态
        for (int i = 0; i < mImageViews.size(); i++) {
            if (i == position){
                mImageViews.get(i).setSelected(true);
            }else{
                mImageViews.get(i).setSelected(false);
            }
        }
        //当为最后一个时，显示button，并隐藏圆点
        if (position == mImageViews.size() -1){
            mLayout.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mButton,"alpha",0f,1f);
            animator.setDuration(1000);
            animator.start();
        }else{
            mLayout.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class viewpagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // return super.instantiateItem(container, position);
            container.addView(mViews.get(position));
            return  mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
            container.removeView(mViews.get(position));
        }
    }

}
