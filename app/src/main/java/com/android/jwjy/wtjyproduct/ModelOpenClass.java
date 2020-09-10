package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.consts.PlayType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 公开课模块
 */
public class ModelOpenClass extends Fragment implements View.OnClickListener {
    private static ControlMainActivity mControlMainActivity;
    private static String mContext="xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mview ;
    private int height = 1344;
    private int width = 720;
    private int mLastTabIndex = 1;
    private String mCurrentTab = "all";
    private String CurrentTime;
    private static final String TAG = "ModelOpenClass";
    private SimpleDateFormat simpleDateFormat;
    private SmartRefreshLayout mSmart_openclass_layout;
    //公开课列表分页查询
    private int mOpenClassCurrentPage = 0;
    private int mOpenClassPageCount = 10;
    private int mOpenClassSum = 0; //公开课总数

    public  static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage){
        mContext = context;
        mControlMainActivity = content;
        ModelOpenClass myFragment = new ModelOpenClass();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage,container,false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;

        //控件的刷新效果
        mSmart_openclass_layout = mview.findViewById(R.id.Smart_openclass_layout);
        mSmart_openclass_layout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mOpenClassSum <= mOpenClassCurrentPage * mOpenClassPageCount){
                    LinearLayout openclass_end = mview.findViewById(R.id.openclass_end);
                    openclass_end.setVisibility(View.VISIBLE);
                    return;
                }
                mSmart_openclass_layout.finishLoadMore();
                if (mCurrentTab.equals("all")) {
                    getOpenClassBeanMore("全部");
                } else if (mCurrentTab.equals("haveinhand")) {
                    getOpenClassBeanMore("已开始");
                } else if (mCurrentTab.equals("begininaminute")) {
                    getOpenClassBeanMore("未开始");
                } else if (mCurrentTab.equals("over")) {
                    getOpenClassBeanMore("已结束");
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mCurrentTab.equals("all")) {
                    getOpenClassBean("全部");
                } else if (mCurrentTab.equals("haveinhand")) {
                    getOpenClassBean("进行中");
                } else if (mCurrentTab.equals("begininaminute")) {
                    getOpenClassBean("未开始");
                } else if (mCurrentTab.equals("over")) {
                    getOpenClassBean("已结束");
                }
            }
        });
        TextView openclass_tab_all = mview.findViewById(R.id.openclass_tab_all);
        TextView openclass_tab_haveinhand = mview.findViewById(R.id.openclass_tab_haveinhand);
        TextView openclass_tab_begininaminute = mview.findViewById(R.id.openclass_tab_begininaminute);
        TextView openclass_tab_over = mview.findViewById(R.id.openclass_tab_over);
        openclass_tab_all.setOnClickListener(this);
        openclass_tab_haveinhand.setOnClickListener(this);
        openclass_tab_begininaminute.setOnClickListener(this);
        openclass_tab_over.setOnClickListener(this);
        CourseMainShow();
        return mview;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //公开课主界面展示
    public void CourseMainShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        //默认游标位置在全部
        ImageView openclass_cursor1 = mview.findViewById(R.id.openclass_cursor1);
        int x = width / 8 - mview.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        openclass_cursor1.setX(x);
        //默认选中的为全部      默认选中为全部
        mLastTabIndex = 1;
        mCurrentTab = "all";
        TextView openclass_tab_all = mview.findViewById(R.id.openclass_tab_all);
        TextView openclass_tab_haveinhand = mview.findViewById(R.id.openclass_tab_haveinhand);
        TextView openclass_tab_begininaminute = mview.findViewById(R.id.openclass_tab_begininaminute);
        TextView openclass_tab_over = mview.findViewById(R.id.openclass_tab_over);
        openclass_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize18));
        openclass_tab_haveinhand.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
        openclass_tab_begininaminute.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
        openclass_tab_over.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
        getOpenClassBean("全部");
    }

    //隐藏所有图层
    private void HideAllLayout(){
//        RelativeLayout course_mainLayout = mview.findViewById(R.id.course_mainLayout);
//        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_mainLayout.getLayoutParams();
//        LP.width = 0;
//        LP.height = 0;
//        course_mainLayout.setLayoutParams(LP);
//        course_mainLayout.setVisibility(View.INVISIBLE);
//        RelativeLayout course_searchlayout = mview.findViewById(R.id.course_searchlayout);
//        LP = (LinearLayout.LayoutParams) course_searchlayout.getLayoutParams();
//        LP.width = 0;
//        LP.height = 0;
//        course_searchlayout.setLayoutParams(LP);
//        course_searchlayout.setVisibility(View.INVISIBLE);
//        RelativeLayout course_details1 = mview.findViewById(R.id.course_details1);
//        LP = (LinearLayout.LayoutParams) course_details1.getLayoutParams();
//        LP.width = 0;
//        LP.height = 0;
//        course_details1.setLayoutParams(LP);
//        course_details1.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.openclass_tab_all:{
                if (!mCurrentTab.equals("all")) {
                    //全部
                    ImageView openclass_cursor1 = mview.findViewById(R.id.openclass_cursor1);
                    Animation animation = new TranslateAnimation(( mLastTabIndex - 1)  * width / 4,0 , 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    openclass_cursor1.startAnimation(animation);
                    TextView openclass_tab_all = mview.findViewById(R.id.openclass_tab_all);
                    TextView openclass_tab_haveinhand = mview.findViewById(R.id.openclass_tab_haveinhand);
                    TextView openclass_tab_begininaminute = mview.findViewById(R.id.openclass_tab_begininaminute);
                    TextView openclass_tab_over = mview.findViewById(R.id.openclass_tab_over);
                    openclass_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    openclass_tab_haveinhand.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_begininaminute.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_over.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                   getOpenClassBean("全部");//文件加载全部数据
                }
                mLastTabIndex = 1;
                mCurrentTab = "all";
                break;
            }
            //进行中
            case R.id.openclass_tab_haveinhand:{
                if (!mCurrentTab.equals("haveinhand")) {
                    ImageView openclass_cursor1 = mview.findViewById(R.id.openclass_cursor1);
                    Animation animation = new TranslateAnimation(( mLastTabIndex - 1)  * width / 4,width / 4 , 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    openclass_cursor1.startAnimation(animation);
                    TextView openclass_tab_all = mview.findViewById(R.id.openclass_tab_all);
                    TextView openclass_tab_haveinhand = mview.findViewById(R.id.openclass_tab_haveinhand);
                    TextView openclass_tab_begininaminute = mview.findViewById(R.id.openclass_tab_begininaminute);
                    TextView openclass_tab_over = mview.findViewById(R.id.openclass_tab_over);
                    openclass_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_haveinhand.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    openclass_tab_begininaminute.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_over.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    getOpenClassBean("进行中");
                }
                mLastTabIndex = 2;
                mCurrentTab = "haveinhand";
                break;
            }
            case R.id.openclass_tab_begininaminute:{
                if (!mCurrentTab.equals("begininaminute")) {
                    ImageView openclass_cursor1 = mview.findViewById(R.id.openclass_cursor1);
                    Animation animation = new TranslateAnimation(( mLastTabIndex - 1)  * width / 4,width * 2 / 4, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    openclass_cursor1.startAnimation(animation);
                    TextView openclass_tab_all = mview.findViewById(R.id.openclass_tab_all);
                    TextView openclass_tab_haveinhand = mview.findViewById(R.id.openclass_tab_haveinhand);
                    TextView openclass_tab_begininaminute = mview.findViewById(R.id.openclass_tab_begininaminute);
                    TextView openclass_tab_over = mview.findViewById(R.id.openclass_tab_over);
                    openclass_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_haveinhand.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_begininaminute.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    openclass_tab_over.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    getOpenClassBean("未开始");
                }
                mLastTabIndex = 3;
                mCurrentTab = "begininaminute";
                break;
            }
            case R.id.openclass_tab_over:{
                if (!mCurrentTab.equals("over")) {
                    ImageView openclass_cursor1 = mview.findViewById(R.id.openclass_cursor1);
                    Animation animation = new TranslateAnimation(( mLastTabIndex - 1)  * width / 4,width * 3 / 4, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    openclass_cursor1.startAnimation(animation);
                    TextView openclass_tab_all = mview.findViewById(R.id.openclass_tab_all);
                    TextView openclass_tab_haveinhand = mview.findViewById(R.id.openclass_tab_haveinhand);
                    TextView openclass_tab_begininaminute = mview.findViewById(R.id.openclass_tab_begininaminute);
                    TextView openclass_tab_over = mview.findViewById(R.id.openclass_tab_over);
                    openclass_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_haveinhand.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_begininaminute.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    openclass_tab_over.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mview.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    getOpenClassBean("已结束");
                }
                mLastTabIndex = 4;
                mCurrentTab = "over";
                break;
            }
            default:{
                break;
            }
        }
    }
    //获取openclass的数据
   public void getOpenClassBean(String type){
       LoadingDialog.getInstance(mControlMainActivity).show();
       LinearLayout openclass_end = mview.findViewById(R.id.openclass_end);
       openclass_end.setVisibility(View.INVISIBLE);
       LinearLayout openclass_content = mview.findViewById(R.id.openclass_content);
       openclass_content.removeAllViews();
       Retrofit retrofit = new Retrofit.Builder()
               .addConverterFactory(GsonConverterFactory.create())
               .baseUrl(mControlMainActivity.mIpadress)
               .client(ModelObservableInterface.client)
               .build();
       ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
       mOpenClassCurrentPage = 1;
       Gson gson = new Gson();
       HashMap<String, Integer> paramsMap = new HashMap<>();
       paramsMap.put("pageNum", mOpenClassCurrentPage);//第几页
       paramsMap.put("pageSize",mOpenClassPageCount);//每页几条
       HashMap<String, String> paramsMap1 = new HashMap<>();
       paramsMap1.put("type", type);
       String strEntity = gson.toJson(paramsMap);
       String strEntity1 = gson.toJson(paramsMap1);
       strEntity1 = strEntity1.replace("{","");
       strEntity = strEntity.replace("}","," + strEntity1);
       RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageOpenclass(body)
            .enqueue(new Callback<ModelOpenclassBean>() {

                @Override
                public void onResponse(Call<ModelOpenclassBean> call, Response<ModelOpenclassBean> response) {
                    ModelOpenclassBean openclassBean = response.body();
                    if (openclassBean == null){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    if (!HeaderInterceptor.IsErrorCode(openclassBean.getCode(),"")){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    if (openclassBean.getCode() != 200){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    ModelOpenclassBean.DataBean data = openclassBean.getData();
                    if (data == null){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    mOpenClassSum = data.getTotal();
                    List<ModelOpenclassBean.DataBean.ListBean> list = data.getList();
                    if (list == null){
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                    for (int i = 0; i < list.size(); i ++){
                        ModelOpenclassBean.DataBean.ListBean listBean = list.get(i);
                        if (listBean == null){
                            continue;
                        }
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.openclass_layout1, null);
                        openclass_content.addView(view);
                        //加载公开课封面
                        ImageView openclass_cover = view.findViewById(R.id.openclass_cover);
                        Glide.with(mControlMainActivity).load(listBean.getPc_cover()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Warn","加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Warn","成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(openclass_cover);
                        //公开课名称
                        TextView openclass_classname = view.findViewById(R.id.openclass_classname);
                        openclass_classname.setText(listBean.getPc_name());
                        //公开课的开始和结束时间
                        TextView openclass1_time = view.findViewById(R.id.openclass1_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(listBean.begin_time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                            Date date1 = null;
                            try {
                                date1 = df1.parse(date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null) {
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                listBean.begin_time = df2.format(date1).toString();
                            }
                        }
                        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(listBean.end_time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                            Date date1 = null;
                            try {
                                date1 = df1.parse(date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null) {
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                listBean.end_time = df2.format(date1).toString();
                            }
                        }
                        openclass1_time.setText(listBean.getBegin_time() + " " + listBean.getEnd_time());
                        //公开课的状态
                        TextView openclass1_state = view.findViewById(R.id.openclass1_state);
                        openclass1_state.setText(listBean.getStatus());
                        //公开课当前logo
                        ImageView openclass1_logo = view.findViewById(R.id.openclass1_logo);
                        if (listBean.getStatus().equals("即将开始")){
                            openclass1_logo.setBackground(view.getResources().getDrawable(R.drawable.button_openclass_start));
                            openclass1_state.setTextColor(view.getResources().getColor(R.color.holo_red_dark));
                        }else if (listBean.getStatus().equals("已结束")){
                            openclass1_logo.setBackground(view.getResources().getDrawable(R.drawable.button_openclass_over));
                            openclass1_state.setTextColor(view.getResources().getColor(R.color.color_69));
                            //为每个公开课设置监听
                            view.setOnClickListener(v->{
                                mControlMainActivity.LoginLiveOrPlayback(listBean.public_class_id,1,PlayType.PLAYBACK);
                            });
                        } else { //直播中
                            //为每个公开课设置监听
                            view.setOnClickListener(v->{
                                mControlMainActivity.LoginLiveOrPlayback(listBean.public_class_id,1,PlayType.LIVE);
                            });
                        }

                    }
                    if (mSmart_openclass_layout != null){
                        mSmart_openclass_layout.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }

                @Override
                public void onFailure(Call<ModelOpenclassBean> call, Throwable t) {
                    Log.e(TAG, "onFailure:数据是 "+t.getMessage()+"" );
                    if (mSmart_openclass_layout != null){
                        mSmart_openclass_layout.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }
            });
   }

    //获取openclass的数据
    public void getOpenClassBeanMore(String type){
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout openclass_content = mview.findViewById(R.id.openclass_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        mOpenClassCurrentPage = mOpenClassCurrentPage + 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mOpenClassCurrentPage);//第几页
        paramsMap.put("pageSize",mOpenClassPageCount);//每页几条
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCoursePackageOpenclass(body)
                .enqueue(new Callback<ModelOpenclassBean>() {

                    @Override
                    public void onResponse(Call<ModelOpenclassBean> call, Response<ModelOpenclassBean> response) {
                        ModelOpenclassBean openclassBean = response.body();
                        if (openclassBean == null){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(openclassBean.getCode(),"")){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (openclassBean.getCode() != 200){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        ModelOpenclassBean.DataBean data = openclassBean.getData();
                        if (data == null){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mOpenClassSum = data.getTotal();
                        List<ModelOpenclassBean.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_openclass_layout != null){
                                mSmart_openclass_layout.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i ++){
                            ModelOpenclassBean.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.openclass_layout1, null);
                            openclass_content.addView(view);
                            //加载公开课封面
                            ImageView openclass_cover = view.findViewById(R.id.openclass_cover);
                            Glide.with(mControlMainActivity).load(listBean.getPc_cover()).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d("Warn","加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d("Warn","成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                    return false;
                                }
                            }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(openclass_cover);
                            //公开课名称
                            TextView openclass_classname = view.findViewById(R.id.openclass_classname);
                            openclass_classname.setText(listBean.getPc_name());
                            //公开课的开始和结束时间
                            TextView openclass1_time = view.findViewById(R.id.openclass1_time);
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.begin_time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listBean.begin_time = df2.format(date1).toString();
                                }
                            }
                            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.end_time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listBean.end_time = df2.format(date1).toString();
                                }
                            }
                            openclass1_time.setText(listBean.getBegin_time() + " " + listBean.getEnd_time());
                            //公开课的状态
                            TextView openclass1_state = view.findViewById(R.id.openclass1_state);
                            openclass1_state.setText(listBean.getStatus());
                            //公开课当前logo
                            ImageView openclass1_logo = view.findViewById(R.id.openclass1_logo);
                            String s = openclass1_state.getText().toString();
                            if (s.equals("即将开始")){
                                openclass1_logo.setBackground(view.getResources().getDrawable(R.drawable.button_openclass_start));
                                openclass1_state.setTextColor(view.getResources().getColor(R.color.holo_red_dark));
                            }else if (listBean.getStatus().equals("已结束")){
                                openclass1_logo.setBackground(view.getResources().getDrawable(R.drawable.button_openclass_over));
                                openclass1_state.setTextColor(view.getResources().getColor(R.color.color_69));
                                //为每个公开课设置监听
                                view.setOnClickListener(v->{
                                    mControlMainActivity.LoginLiveOrPlayback(listBean.public_class_id,1,PlayType.PLAYBACK);
                                });
                            } else { //直播中
                                //为每个公开课设置监听
                                view.setOnClickListener(v->{
                                    mControlMainActivity.LoginLiveOrPlayback(listBean.public_class_id,1,PlayType.LIVE);
                                });
                            }
                        }
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelOpenclassBean> call, Throwable t) {
                        Log.e(TAG, "onFailure:数据是 "+t.getMessage()+"" );
                        if (mSmart_openclass_layout != null){
                            mSmart_openclass_layout.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }


    //公开课的数据类
    public static class ModelOpenclassBean{
        /**
         * code : 200
         * data : {"total":4,"list":[{"pc_name":"江山","end_time":"2019-12-11T00:00:00.000+0800","begin_time":"2019-12-11T00:00:00.000+0800","pc_cover":"E:/upload111/1576151514113.png","public_class_id":21,"status":"已结束"},{"pc_name":"公开课11","end_time":"2019-12-09T11:29:00.000+0800","begin_time":"2019-12-09T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":18,"status":"已结束"},{"pc_name":"公开课22","end_time":"2019-11-07T10:29:00.000+0800","begin_time":"2019-11-07T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":17,"status":"已结束"},{"pc_name":"公开课33","end_time":"2017-02-02T00:00:00.000+0800","begin_time":"2017-02-02T00:00:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":12,"status":"已结束"}],"pageNum":1,"pageSize":4,"size":4,"startRow":0,"endRow":3,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 4
             * list : [{"pc_name":"江山","end_time":"2019-12-11T00:00:00.000+0800","begin_time":"2019-12-11T00:00:00.000+0800","pc_cover":"E:/upload111/1576151514113.png","public_class_id":21,"status":"已结束"},{"pc_name":"公开课11","end_time":"2019-12-09T11:29:00.000+0800","begin_time":"2019-12-09T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":18,"status":"已结束"},{"pc_name":"公开课22","end_time":"2019-11-07T10:29:00.000+0800","begin_time":"2019-11-07T10:29:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":17,"status":"已结束"},{"pc_name":"公开课33","end_time":"2017-02-02T00:00:00.000+0800","begin_time":"2017-02-02T00:00:00.000+0800","pc_cover":"E:/upload111/1573110163820.jpg","public_class_id":12,"status":"已结束"}]
             * pageNum : 1
             * pageSize : 4
             * size : 4
             * startRow : 0
             * endRow : 3
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * pc_name : 江山
                 * end_time : 2019-12-11T00:00:00.000+0800
                 * begin_time : 2019-12-11T00:00:00.000+0800
                 * pc_cover : E:/upload111/1576151514113.png
                 * public_class_id : 21
                 * status : 已结束
                 */

                private String pc_name;
                private String end_time;
                private String begin_time;
                private String pc_cover;
                private int public_class_id;
                private String status;

                public String getPc_name() {
                    return pc_name;
                }

                public void setPc_name(String pc_name) {
                    this.pc_name = pc_name;
                }

                public String getEnd_time() {
                    return end_time;
                }

                public void setEnd_time(String end_time) {
                    this.end_time = end_time;
                }

                public String getBegin_time() {
                    return begin_time;
                }

                public void setBegin_time(String begin_time) {
                    this.begin_time = begin_time;
                }

                public String getPc_cover() {
                    return pc_cover;
                }

                public void setPc_cover(String pc_cover) {
                    this.pc_cover = pc_cover;
                }

                public int getPublic_class_id() {
                    return public_class_id;
                }

                public void setPublic_class_id(int public_class_id) {
                    this.public_class_id = public_class_id;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }
            }
        }
    }
}
