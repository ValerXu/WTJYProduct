package com.android.jwjy.wtjyproduct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dayuer on 19/7/2.
 * 首页
 */
public class ModelHomePage extends Fragment{
    private static int FragmentPage;
    private ModelImageSlideshow mImageSlideshow;
    private boolean mIsUseImageSlideShow = true;
    private boolean mIsUseFunctionShow = true;
    private View mView = null;

    private static ControlMainActivity mControlMainActivity;
    private LayoutInflater inflater;
    private ViewGroup container;

    //点击查看课程详情需要用到的课程对象
    private ModelCourseCover mModelCourseCover = null;

    //数据
    private HomePageBean.HomePageDataBean mHomePageDataBean ;

    //刷新控件
    private SmartRefreshLayout mSmart_homepage_layout1 = null;

    private class FunctionButtonInfo{
        String mFunctionalModule; // 1：课程包 2：公开课 3：题库 4：问答 5：课程表 6：新闻资讯 7：课程 8：我的
        String mButtonName; //按钮名称
    }
    //要显示的页面
//    private int FragmentPage;
    public  static  Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage){
        mControlMainActivity = content;
        ModelHomePage myFragment = new ModelHomePage();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(FragmentPage,container,false);
        this.inflater = inflater;
        this.container = container;
        getHomePageDatas();
        HomePageShow();
        return mView;
    }

    //隐藏所有图层
    private void HideAllLayout(){
        LinearLayout homepage_layout_main = mView.findViewById(R.id.homepage_layout_main);
        homepage_layout_main.removeAllViews();
        return;
    }

    //首页展示
    public void HomePageShow(){
        if (mView == null){
            return;
        }
        HideAllLayout();
        LinearLayout homepage_layout_main = mView.findViewById(R.id.homepage_layout_main);
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.homepage_layout1, null);
        homepage_layout_main.addView(view);
        //Smart_homepage_layout1
        mSmart_homepage_layout1 = view.findViewById(R.id.Smart_homepage_layout1);
        mSmart_homepage_layout1.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //控件加载更多
                mSmart_homepage_layout1.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //控件的刷新方法
                getHomePageDatas();
            }
        });
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        if (mIsUseImageSlideShow) {
            mImageSlideshow = view.findViewById(R.id.is_gallery);
            //设置使用控件的宽高
            LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) mImageSlideshow.getLayoutParams();
            LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
            LP.height = height / 4;
            mImageSlideshow.setLayoutParams(LP);
            // 初始化轮播图数据
            initSlideImageData();
            // 为ImageSlideshow设置数据
            mImageSlideshow.setDotSpace(12);
            mImageSlideshow.setDotSize(12);
            mImageSlideshow.setDelay(3000);
            mImageSlideshow.commit();
        }
        if (mIsUseFunctionShow){
            List<FunctionButtonInfo> FunctionButtonInfoList = initNavigationData();
            while (FunctionButtonInfoList.size() > 8){ //功能按钮不能多于GridLayout的所创个数
                FunctionButtonInfoList.remove(FunctionButtonInfoList.size() - 1);
            }
            GridLayout mfunctionButton = view.findViewById(R.id.functionButton);
            for (int i = 0; i < FunctionButtonInfoList.size(); i ++){ //循环添加功能按钮
                FunctionButtonInfo functionButtonInfo = FunctionButtonInfoList.get(i);
                View functionbutton = inflater.inflate(R.layout.homepage_layout_functionbutton,container,false);
                LinearLayout mfunctionbuttonLinearLayout = functionbutton.findViewById(R.id.LinearLayout_functionbutton);
                //添加完以后重置按钮布局的大小与间隔
                FrameLayout.LayoutParams mfunctionbuttonLinearLayoutLayoutParams = (FrameLayout.LayoutParams) mfunctionbuttonLinearLayout.getLayoutParams();
                mfunctionbuttonLinearLayoutLayoutParams.width = width / 4;
                mfunctionbuttonLinearLayoutLayoutParams.topMargin = width / 14 / 4;
                mfunctionbuttonLinearLayout.setLayoutParams(mfunctionbuttonLinearLayoutLayoutParams);
                mfunctionButton.addView(functionbutton);
                TextView TextView_functionbutton = functionbutton.findViewById(R.id.TextView_functionbutton);
                //重置按钮名称
                TextView_functionbutton.setText(functionButtonInfo.mButtonName);
                LinearLayout.LayoutParams TextView_functionbuttonLayoutParams = (LinearLayout.LayoutParams) TextView_functionbutton.getLayoutParams();
                TextView_functionbuttonLayoutParams.topMargin = 0;
                TextView_functionbutton.setLayoutParams(TextView_functionbuttonLayoutParams);
                ImageView ImageView_functionbutton = functionbutton.findViewById(R.id.ImageView_functionbutton);
                LinearLayout.LayoutParams ImageView_functionbuttonLayoutParams = (LinearLayout.LayoutParams) ImageView_functionbutton.getLayoutParams();
                ImageView_functionbuttonLayoutParams.width = width / 8;
                ImageView_functionbuttonLayoutParams.height = width / 8;
                ImageView_functionbutton.setLayoutParams(ImageView_functionbuttonLayoutParams);
                //判断按钮的id 来加载不同按钮的图片   1：课程包 2：公开课 3：题库 4：问答 5：课程表 6：新闻资讯 7：课程 8：我的
                if (functionButtonInfo.mFunctionalModule.equals("课程包")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_coursepacketbutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("公开课")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_openclassbutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("题库")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_questionbankbutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("问答")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_answersbutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("课程表")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_classchedulecardbutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("新闻资讯")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_newsbutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("课程")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_coursebutton));
                } else if (functionButtonInfo.mFunctionalModule.equals("我的")){
                    ImageView_functionbutton.setImageDrawable(getResources().getDrawable(R.drawable.functionbutton_mybutton));
                }
                mfunctionbuttonLinearLayout.setOnClickListener(v -> {
                    //判断点击什么按钮，跳转什么功能界面1：课程包 2：公开课 3：题库 4：问答 5：课程表 6：新闻资讯 7：课程 8：我的
                    if (functionButtonInfo.mFunctionalModule.equals("课程包")){
                        mControlMainActivity.Page_MoreCoursePacket();
                    } else if (functionButtonInfo.mFunctionalModule.equals("公开课")){
//                        mControlMainActivity.LoginLiveOrPlayback("391068","dadada","",PlayType.LIVE);
//                        mControlMainActivity.LoginLiveOrPlayback("365061","dadada","799723",PlayType.PLAYBACK);
                        mControlMainActivity.Page_OpenClass();
                    } else if (functionButtonInfo.mFunctionalModule.equals("题库")){
                        mControlMainActivity.Page_QuestionBank();
                    } else if (functionButtonInfo.mFunctionalModule.equals("问答")){
                        mControlMainActivity.Page_CommunityAnswer();
                    } else if (functionButtonInfo.mFunctionalModule.equals("课程表")){
                        mControlMainActivity.Page_ClassCheduleCard();
                    } else if (functionButtonInfo.mFunctionalModule.equals("新闻资讯")){
                        mControlMainActivity.Page_News();
                    } else if (functionButtonInfo.mFunctionalModule.equals("课程")){
                        mControlMainActivity.Page_Course();
                    } else if (functionButtonInfo.mFunctionalModule.equals("我的")){
                        mControlMainActivity.Page_My();
                    }
                });
            }
        }
        //更多课程
        RelativeLayout morecourseRelativeLayout = view.findViewById(R.id.morecourse);
        LinearLayout.LayoutParams morecourseRelativeLayoutLp = (LinearLayout.LayoutParams) morecourseRelativeLayout.getLayoutParams();
        morecourseRelativeLayoutLp.topMargin = width / 25;
        morecourseRelativeLayoutLp.rightMargin = width / 28;
        morecourseRelativeLayoutLp.leftMargin = width / 28;
        morecourseRelativeLayout.setLayoutParams(morecourseRelativeLayoutLp);
        List<CourseInfo> CourseInfoList = initRecommendCourseData();
        LinearLayout courseModelLinearLayout = view.findViewById(R.id.coursemodel);
        View course_line1 = null;
        for (int i = 0; i < CourseInfoList.size(); i ++){
            CourseInfo courseInfo = CourseInfoList.get(i);
            if (courseInfo == null){
                continue;
            }
            ModelCourseCover modelCourseCover = new ModelCourseCover();
            View modelCourseView = modelCourseCover.ModelCourseCover(mControlMainActivity,courseInfo);
            modelCourseView.setOnClickListener(v->{ //点击某一课程
                ModelCourseCover modelCourseCover1 = new ModelCourseCover();
                View modelCourseView1 = modelCourseCover1.ModelCourseCover(mControlMainActivity,courseInfo);
                modelCourseCover1.CourseDetailsShow();
                HideAllLayout();
                homepage_layout_main.addView(modelCourseView1);
                mControlMainActivity.onClickCourseDetails();
                mModelCourseCover = modelCourseCover1;
            });
            courseModelLinearLayout.addView(modelCourseView);
            course_line1 = modelCourseView.findViewById(R.id.course_line1);
            Log.e("TAG", "onError: initRecommendCourseData4");
        }
        if (course_line1 != null){
            course_line1.setVisibility(View.INVISIBLE);
        }
        View courseline = view.findViewById(R.id.courseline);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) courseline.getLayoutParams();
        lp.rightMargin = width / 25;
        lp.leftMargin = width / 25;
        courseline.setLayoutParams(lp);
        //更多课程包
        RelativeLayout morecoursepacketRelativeLayout = view.findViewById(R.id.morecoursepacket);
        LinearLayout.LayoutParams morecoursepacketRelativeLayoutLp = (LinearLayout.LayoutParams) morecoursepacketRelativeLayout.getLayoutParams();
        morecoursepacketRelativeLayoutLp.topMargin = width / 25;
        morecoursepacketRelativeLayoutLp.rightMargin = width / 28;
        morecoursepacketRelativeLayoutLp.leftMargin = width / 28;
        morecoursepacketRelativeLayout.setLayoutParams(morecoursepacketRelativeLayoutLp);
        List<CoursePacketInfo> CoursePacketInfoList = initRecommendCoursePacketData();
        LinearLayout coursePacketModelLinearLayout = view.findViewById(R.id.coursepacketmodel);
        for (int i = 0; i < CoursePacketInfoList.size(); i ++){
            CoursePacketInfo CoursePacketInfo = CoursePacketInfoList.get(i);
            if (CoursePacketInfo == null){
                continue;
            }
            ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
            View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity,CoursePacketInfo);
            modelCoursePacketView.setOnClickListener(v->{ //点击某一课程包
                ModelCoursePacketCover modelCoursePacketCover1 = new ModelCoursePacketCover();
                View modelCoursePacketView1 = modelCoursePacketCover1.ModelCoursePacketCover(mControlMainActivity,CoursePacketInfo);
                modelCoursePacketCover1.CoursePacketDetailsShow();
                HideAllLayout();
                homepage_layout_main.addView(modelCoursePacketView1);
                mControlMainActivity.onClickCoursePacketDetails();
            });
            coursePacketModelLinearLayout.addView(modelCoursePacketView);
        }

        View line = view.findViewById(R.id.line);
        LinearLayout.LayoutParams lineRelativeLayoutLp = (LinearLayout.LayoutParams) line.getLayoutParams();
        lineRelativeLayoutLp.height = height / 11;
        line.setLayoutParams(lineRelativeLayoutLp);
        return;
    }
    /**
     * 初始化轮播图数据
     */
    private void initSlideImageData() {
        if (mHomePageDataBean == null){
            return;
        }
        if (mHomePageDataBean.rotationChartInfo == null){
            return;
        }
        //如果有url,使用远程图片
        for (int i = 0; i < mHomePageDataBean.rotationChartInfo.size(); i ++) {
            HomePageBean.HomePageRotationChartInfoBean homePageRotationChartInfoBean = mHomePageDataBean.rotationChartInfo.get(i);
            if (homePageRotationChartInfoBean == null){
                continue;
            }
//            if (!homePageRotationChartInfoBean.link_type.equals("外部链接")){//暂只处理外部链接数据
//                continue;
//            }
            if (homePageRotationChartInfoBean.server_address == null){ //没有轮播图链接
                continue;
            }
            mImageSlideshow.addImageUrlAndSkipUrl(homePageRotationChartInfoBean.link_num,homePageRotationChartInfoBean.server_address);
        }
        return;
    }

    /**
     * 初始化精钢区导航数据
     */
    private List<FunctionButtonInfo> initNavigationData() {
        List<FunctionButtonInfo> FunctionButtonInfoList = new ArrayList<>();
        if (mHomePageDataBean == null){
            return FunctionButtonInfoList;
        }
        if (mHomePageDataBean.allHomeNavigationAndbBottomMenu == null){
            return FunctionButtonInfoList;
        }
        for (int i = 0; i < mHomePageDataBean.allHomeNavigationAndbBottomMenu.size(); i ++) {
            HomePageBean.HomePageAllHomeNavigationAndbBottomMenuBean homePageAllHomeNavigationAndbBottomMenuBean = mHomePageDataBean.allHomeNavigationAndbBottomMenu.get(i);
            if (homePageAllHomeNavigationAndbBottomMenuBean == null){
                continue;
            }
            if (!homePageAllHomeNavigationAndbBottomMenuBean.nvigation_type.equals("首页导航")){//只查询精钢区导航按钮
                continue;
            }
            FunctionButtonInfo functionButtonInfo = new FunctionButtonInfo();
            functionButtonInfo.mFunctionalModule = homePageAllHomeNavigationAndbBottomMenuBean.functional_module;
            functionButtonInfo.mButtonName = homePageAllHomeNavigationAndbBottomMenuBean.custom_name;
            FunctionButtonInfoList.add(functionButtonInfo);
        }
        return FunctionButtonInfoList;
    }

    /**
     * 初始化推荐课程数据
     */
    private List<CourseInfo> initRecommendCourseData() {
        Log.e("TAG", "onError: initRecommendCourseData");
        List<CourseInfo> CourseInfoList = new ArrayList<>();
        if (mHomePageDataBean == null){
            return CourseInfoList;
        }
        Log.e("TAG", "onError: initRecommendCourseData1");
        if (mHomePageDataBean.allRecommendCourseInfo == null){
            return CourseInfoList;
        }
        Log.e("TAG", "onError: initRecommendCourseData2");
        for (int i = 0; i < mHomePageDataBean.allRecommendCourseInfo.size(); i ++) {
            HomePageBean.HomePageAllRecommendCourseInfoBean homePageAllRecommendCourseInfoBean = mHomePageDataBean.allRecommendCourseInfo.get(i);
            if (homePageAllRecommendCourseInfoBean == null){
                continue;
            }
            if (homePageAllRecommendCourseInfoBean.cover == null){
                homePageAllRecommendCourseInfoBean.cover = "";
            }
            CourseInfo CourseInfo = new CourseInfo();
            CourseInfo.mCourseId = String.valueOf(homePageAllRecommendCourseInfoBean.course_id);
            CourseInfo.mCourseCover = homePageAllRecommendCourseInfoBean.cover;
            CourseInfo.mCourseLearnPersonNum = String.valueOf(homePageAllRecommendCourseInfoBean.buying_base_number);
            CourseInfo.mCourseName = homePageAllRecommendCourseInfoBean.course_name;
            CourseInfo.mCoursePrice = String.valueOf(homePageAllRecommendCourseInfoBean.special_price);
            CourseInfo.mCoursePriceOld = String.valueOf(homePageAllRecommendCourseInfoBean.price);
            CourseInfo.mCourseType = homePageAllRecommendCourseInfoBean.course_type;
            CourseInfoList.add(CourseInfo);
            Log.e("TAG", "onError: initRecommendCourseData3");
        }
        return CourseInfoList;
    }

    /**
     * 初始化推荐课程包数据
     */
    private List<CoursePacketInfo> initRecommendCoursePacketData() {
        List<CoursePacketInfo> CoursePacketInfoList = new ArrayList<>();
        if (mHomePageDataBean == null){
            return CoursePacketInfoList;
        }
        if (mHomePageDataBean.allRecommendCoursePackageInfo == null){
            return CoursePacketInfoList;
        }
        for (int i = 0; i < mHomePageDataBean.allRecommendCoursePackageInfo.size(); i ++) {
            HomePageBean.HomePageAllRecommendCoursePackageInfoBean homePageAllRecommendCoursePacketInfoBean = mHomePageDataBean.allRecommendCoursePackageInfo.get(i);
            if (homePageAllRecommendCoursePacketInfoBean == null){
                continue;
            }
            if (homePageAllRecommendCoursePacketInfoBean.cover == null){
                homePageAllRecommendCoursePacketInfoBean.cover = "";
            }
            CoursePacketInfo CoursePacketInfo = new CoursePacketInfo();
            CoursePacketInfo.mCoursePacketId = String.valueOf(homePageAllRecommendCoursePacketInfoBean.course_package_id);
            CoursePacketInfo.mCoursePacketCover = homePageAllRecommendCoursePacketInfoBean.cover;
            CoursePacketInfo.mCoursePacketLearnPersonNum = String.valueOf(homePageAllRecommendCoursePacketInfoBean.buying_base_number);
            CoursePacketInfo.mCoursePacketPriceOld = String.valueOf(homePageAllRecommendCoursePacketInfoBean.total_price);
            CoursePacketInfo.mCoursePacketName = homePageAllRecommendCoursePacketInfoBean.cp_name;
            CoursePacketInfo.mCoursePacketPrice = String.valueOf(homePageAllRecommendCoursePacketInfoBean.favorable_price);
            CoursePacketInfo.mCoursePacketStageNum = String.valueOf(homePageAllRecommendCoursePacketInfoBean.stageNum);
            CoursePacketInfo.mCoursePacketCourseNum = String.valueOf(homePageAllRecommendCoursePacketInfoBean.courseNum);
            CoursePacketInfoList.add(CoursePacketInfo);
        }
        return CoursePacketInfoList;
    }

    //轮播图排序
    public List<HomePageBean.HomePageRotationChartInfoBean> getRotationChartSortASCList(List<HomePageBean.HomePageRotationChartInfoBean> list) {
        Collections.sort(list, (o1, o2) -> Integer.valueOf(o1.getRotation_chart_sort()).compareTo(Integer.valueOf(o2.getRotation_chart_sort())));
        return list;
    }

    //首页导航和底部菜单排序
    public List<HomePageBean.HomePageAllHomeNavigationAndbBottomMenuBean> getHomeNavigationAndbBottomMenuASCList(List<HomePageBean.HomePageAllHomeNavigationAndbBottomMenuBean> list) {
        Collections.sort(list, (o1, o2) -> Integer.valueOf(o1.getNvigation_sort()).compareTo(Integer.valueOf(o2.getNvigation_sort())));
        return list;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        return;
    }
    @Override
    public void onDestroy() {
        // 释放资源
        if (mImageSlideshow != null) {
            mImageSlideshow.releaseResource();
        }
        super.onDestroy();
        return;
    }

    public void ModelCourseCoverQuestionPictureAdd(Intent data){ //添加图片成功后，转到图片界面
        if (mModelCourseCover != null){
            mModelCourseCover.ModelCourseCoverQuestionPictureAdd(data);
        }
        return;
    }

    //获取首页数据
    private void getHomePageDatas() {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        final Observable<HomePageBean> data = modelObservableInterface.queryHomePageInfo();
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HomePageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomePageBean value) {
                        //网络请求数据成功
                        mHomePageDataBean = value.getData();
                        if (mHomePageDataBean != null) {
                            if (mHomePageDataBean.rotationChartInfo != null) {
                                //先将轮播图排序
                                mHomePageDataBean.rotationChartInfo = getRotationChartSortASCList(mHomePageDataBean.rotationChartInfo);
                            }
                            //先将首页导航和底部菜单排序
                            if (mHomePageDataBean.allHomeNavigationAndbBottomMenu != null) {
                                mHomePageDataBean.allHomeNavigationAndbBottomMenu = getHomeNavigationAndbBottomMenuASCList(mHomePageDataBean.allHomeNavigationAndbBottomMenu);
                            }
                        }
                        //刷新界面
                        HomePageShow();
                        //需要结束刷新头
                        if (mSmart_homepage_layout1 != null) {
                            mSmart_homepage_layout1.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        if (mHomePageDataBean != null) {
                            Log.e("TAG", "onError1: " + mHomePageDataBean.toString());
                        } else {
                            Log.e("TAG", "onError: mHomePageDataBean.toString()" + value.code );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mControlMainActivity,"刷新数据失败",Toast.LENGTH_LONG).show();
                        //需要结束刷新头
                        if (mSmart_homepage_layout1 != null) {
                            mSmart_homepage_layout1.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return;
    }

    public static class HomePageBean{
        private HomePageDataBean data;  //返回数据
        private int code;
//        private String errorMsg;

        public HomePageDataBean getData() {
            return data;
        }


        public void setData(HomePageDataBean data) {
            this.data = data;
            return;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
            return;
        }

//        public String getErrorMsg() {
//            return errorMsg;
//        }

        //        public void setErrorMsg(String errorMsg) {
//            this.errorMsg = errorMsg;
//        }
        public static class HomePageDataBean {
            private List<HomePageAllHomeNavigationAndbBottomMenuBean> allHomeNavigationAndbBottomMenu;  //精钢区和底部按钮
            private List<HomePageAllRecommendCoursePackageInfoBean> allRecommendCoursePackageInfo;      //推荐课程包
            private List<HomePageAllRecommendCourseInfoBean> allRecommendCourseInfo;                    //推荐课程
            private List<HomePageRotationChartInfoBean> rotationChartInfo;                              //轮播图
            public List<HomePageAllHomeNavigationAndbBottomMenuBean> getAllHomeNavigationAndbBottomMenuDatas() {
                return allHomeNavigationAndbBottomMenu;
            }
            public List<HomePageAllRecommendCoursePackageInfoBean> getAllRecommendCoursePackageDatas() {
                return allRecommendCoursePackageInfo;
            }
            public List<HomePageAllRecommendCourseInfoBean> getAllRecommendCourseInfoDatas() {
                return allRecommendCourseInfo;
            }
            public List<HomePageRotationChartInfoBean> getRotationChartInfoDatas() {
                return rotationChartInfo;
            }
            public void setAllHomeNavigationAndbBottomMenuDatas(List<HomePageAllHomeNavigationAndbBottomMenuBean> allHomeNavigationAndbBottomMenu) {
                this.allHomeNavigationAndbBottomMenu = allHomeNavigationAndbBottomMenu;
            }
            public void setAllRecommendCoursePackageDatas(List<HomePageAllRecommendCoursePackageInfoBean> allRecommendCoursePackageInfo) {
                this.allRecommendCoursePackageInfo = allRecommendCoursePackageInfo;
            }
            public void setAllRecommendCourseInfoDatas(List<HomePageAllRecommendCourseInfoBean> allRecommendCourseInfo) {
                this.allRecommendCourseInfo = allRecommendCourseInfo;
            }
            public void setRotationChartInfoDatas(List<HomePageRotationChartInfoBean> rotationChartInfo) {
                this.rotationChartInfo = rotationChartInfo;
            }
        }
        public static class HomePageAllHomeNavigationAndbBottomMenuBean{
            private int nvigation_id;           //首页导航/底部菜单id
            private String custom_name;         //名称
            private int nvigation_sort;         //顺序
            private String nvigation_type;      //类型（首页导航/底部菜单）
            private String functional_module;   //功能模块
            public int getNvigation_id(){
                return this.nvigation_id;
            }
            public void setNvigation_id(int nvigation_id){
                this.nvigation_id = nvigation_id;
            }
            public String getCustom_name(){
                return this.custom_name;
            }
            public void setCustom_name(String custom_name){
                this.custom_name = custom_name;
            }
            public int getNvigation_sort(){
                return this.nvigation_sort;
            }
            public void setNvigation_sort(int nvigation_sort){
                this.nvigation_sort = nvigation_sort;
                return;
            }
            public String getNvigation_type(){
                return this.nvigation_type;
            }
            public void setNvigation_type(String nvigation_type){
                this.nvigation_type = nvigation_type;
                return;
            }
            public String getFunctional_module(){
                return this.functional_module;
            }
            public void setFunctional_module(String functional_module){
                this.functional_module = functional_module;
                return;
            }
        }

        public static class HomePageAllRecommendCoursePackageInfoBean{
            private String cover;               //封面
            private int stageNum;               //阶段数量
            private int course_package_id;      //课程包id
            private float total_price;          //总价格
            private int courseNum;              //课程数量
            private String cp_name;             //课程包名字
            private float favorable_price;      //优惠价格
            private int buying_base_number;     //购买人数
            public String getCover(){
                return this.cover;
            }
            public void setCover(String cover){
                this.cover = cover;
            }
            public int getStageNum(){
                return this.stageNum;
            }
            public void setStageNum(int stageNum){
                this.stageNum = stageNum;
            }
            public int getCourse_package_id(){
                return this.course_package_id;
            }
            public void setCourse_package_id(int course_package_id){
                this.course_package_id = course_package_id;
            }
            public float getTotal_price(){
                return this.total_price;
            }
            public void setTotal_price(float total_price){
                this.total_price = total_price;
            }
            public int getCourseNum(){
                return this.courseNum;
            }
            public void setCourseNum(int courseNum){
                this.courseNum = courseNum;
            }
            public String getCp_name(){
                return this.cp_name;
            }
            public void setCp_name(String cp_name){
                this.cp_name = cp_name;
            }
            public float getFavorable_price(){
                return this.favorable_price;
            }
            public void setFavorable_price(float favorable_price){
                this.favorable_price = favorable_price;
            }
            public int getBuying_base_number(){
                return this.buying_base_number;
            }
            public void setBuying_base_number(int buying_base_number){
                this.buying_base_number = buying_base_number;
            }
        }
        public static class HomePageAllRecommendCourseInfoBean{
            private String cover;               //封面
            private int course_id;              //课程id
            private String course_type;         //课程类型（直播/录播/混合）
            private String course_name;         //课程名称
            private float special_price;        //优惠价格
            private float price;                //价格
            private int buying_base_number;     //学习人数
            public String getCover(){
                return this.cover;
            }
            public void setCover(String cover){
                this.cover = cover;
            }
            public int getCourse_id(){
                return this.course_id;
            }
            public void setCourse_id(int course_id){
                this.course_id = course_id;
            }
            public String getCourse_type(){
                return this.course_type;
            }
            public void setCourse_type(String course_type){
                this.course_type = course_type;
            }
            public String getCourse_name(){
                return this.course_name;
            }
            public void setCourse_name(String course_name){
                this.course_name = course_name;
            }
            public float getSpecial_price(){
                return this.special_price;
            }
            public void setSpecial_price(float special_price){
                this.special_price = special_price;
            }
            public float getPrice() {
                return price;
            }
            public void setPrice(float price){
                this.price = price;
            }
            public int getBuying_base_number(){
                return this.buying_base_number;
            }
            public void setBuying_base_number(int buying_base_number) {
                this.buying_base_number = buying_base_number;
            }
        }
        public static class HomePageRotationChartInfoBean{
            private String link_type;           //链接类型（课程/课程包/外部链接）
            private String link_num;            //链接值
            private int rotation_chart_sort;    //轮播图顺序
            private String server_address;      //服务器地址（即封面）
            private int rotation_chart_id;      //轮播图id

            public String getLink_type() {
                return link_type;
            }

            public void setLink_type(String link_type) {
                this.link_type = link_type;
            }

            public String getLink_num() {
                return link_num;
            }

            public void setLink_num(String link_num) {
                this.link_num = link_num;
            }

            public int getRotation_chart_sort() {
                return rotation_chart_sort;
            }

            public void setRotation_chart_sort(int rotation_chart_sort) {
                this.rotation_chart_sort = rotation_chart_sort;
            }

            public String getServer_address() {
                return server_address;
            }

            public void setServer_address(String server_address) {
                this.server_address = server_address;
            }

            public int getRotation_chart_id() {
                return rotation_chart_id;
            }

            public void setRotation_chart_id(int rotation_chart_id) {
                this.rotation_chart_id = rotation_chart_id;
            }
        }
    }
}