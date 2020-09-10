package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayerview.view.download.DownloadView;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 我的模块
 */
public class ModelMy extends Fragment implements ModelOrderDetailsInterface{
    private static ControlMainActivity mControlMainActivity;
    private static String mContext = "xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mview, mMyView, mMyClassView, mMyClassPacketView, mMyCollectView, mMyCacheView, mMyCacheManagementCacheView,
            mMyOrderView, mMyOrderDetailsView, mMyCouponView, mMyMessageView, mMyMessageView0, mMyAnswerView, mMyAnswerDetailsView,
            mAnswerDetailsView;
    private int width = 720;
    //我的收藏当前显示tab，默认为课程
    private int mMyCollectLastTabIndex = 1;
    private String mMyCollectCurrentTab = "course";
    //我的订单当前显示tab，默认为全部
    private int mMyOrderLastTabIndex = 1;
    private String mMyOrderCurrentTab = "all";
    //我的优惠券当前显示tab，默认为未使用
    private int mMyCouponLastTabIndex = 1;
    private String mMyCouponCurrentTab = "notused";
    //我的问答当前显示tab，默认为我的提问
    private int mMyAnswerLastTabIndex = 1;
    private String mMyAnswerCurrentTab = "question";
    private ControllerCenterDialog mMyDialog, mMyCouponDialog;
    private ControllerMyMessage1Adapter adapter;
    private static final String TAG = "ModelMy";
    private ModelCourseCover mModelCourseCover;
    boolean m_isFind = false;  //判断当前已读和未读的状态
    //我的消息的集合
    List<ControllerMyMessage1Adapter.MyMessageInfo> list = new ArrayList<>();
    //个人信息返回数据
    private PersonalInfoBean.PersonalInfoDataBean mPersonalInfoDataBean;

    private Map<String, CourseRecordPlayDownloadInfo> mCourseRecordPlayDownloadInfoMap = new HashMap<>();
    private SmartRefreshLayout mSmart_model_my_myclass;
    private SmartRefreshLayout mSmart_model_my_myclasspacket;
    private SmartRefreshLayout mSmart_model_my_myorder;
//    private String product_name;
//    private String order_status;
//    private String order_num;
//    private int product_price;
    private SmartRefreshLayout mSmart_model_my_mycollect;
    private SmartRefreshLayout mSmart_model_my_myorderdetails;
    private SmartRefreshLayout mSmart_model_my_mymessage;
    private SmartRefreshLayout mSmart_model_my_myanswer;
    private SmartRefreshLayout mSmart_model_my_myanswerdetails;
    private SmartRefreshLayout mSmart_model_my_mycoupon;
    //我的课程列表分页查询
    private int mMyCourseCurrentPage = 0;
    private int mMyCoursePageCount = 10;
    private int mMyCourseSum = 0; //我的课程总数
    //我的课程包列表分页查询
    private int mMyCoursePacketCurrentPage = 0;
    private int mMyCoursePacketPageCount = 10;
    private int mMyCoursePacketSum = 0; //我的课程总数
    //我的收藏列表分页查询
    private int mMyCollectCurrentPage = 0;
    private int mMyCollectPageCount = 10;
    private int mMyCollectSum = 0; //我的收藏课程总数
    //我的收藏课程包列表分页查询
    private int mMyCollectPacketCurrentPage = 0;
    private int mMyCollectPacketPageCount = 10;
    private int mMyCollectPacketSum = 0; //我的收藏课程包总数
    //我的订单列表分页查询
    private int mMyOrderCurrentPage = 0;
    private int mMyOrderPageCount = 10;
    private int mMyOrderSum = 0; //我的订单总数
    //我的优惠券列表分页查询
    private int mMyCouponCurrentPage = 0;
    private int mMyCouponPageCount = 10;
    private int mMyCouponSum = 0; //我的订单总数
    //我的消息列表分页查询
    private int mMyMessageCurrentPage = 0;
    private int mMyMessagePageCount = 10;
    private int mMyMessageSum = 0; //我的消息总数
    private int mMyMessageType = 3; //默认为3  1：系统通知 2：广告通知
    //我的问答列表分页查询
    private int mMyQuestionAndAnswerCurrentPage = 0;
    private int mMyQuestionAndAnswerPageCount = 10;
    private int mMyQuestionAndAnswerSum = 0; //我的问答总数
    //我的问答详情分页查询
    private int mMyQuestionAndAnswerDetailsCurrentPage = 0;
    private int mMyQuestionAndAnswerDetailsPageCount = 10;
    private int mMyQuestionAndAnswerDetailsSum = 0; //我的问答详情总数
    private Integer mAnswerDetailsQuestionId = null;
    private Integer mAnswerDetailsAnswerId = null;

    public static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage) {
        mContext = context;
        mControlMainActivity = content;
        ModelMy myFragment = new ModelMy();
        FragmentPage = iFragmentPage;
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage, container, false);
        getPersonalInfoDatas(); // 查询个人信息详情（我的界面）
        ModelMyInit();    //加载用户头像
        return mview;
    }

    //隐藏所有图层
    private void HideAllLayout() {
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        my_layout_main.removeAllViews();
    }

    //我的界面初始化
    public void ModelMyInit() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyView == null) {
            mMyView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.my_layout_main, null);
            TextView username = mMyView.findViewById(R.id.username);
            username.setOnClickListener(v -> {
                if (mPersonalInfoDataBean == null) {
                    mControlMainActivity.onClickImmediatelyLogin("login");
                } else {
                    mControlMainActivity.onClickImmediatelyLogin("personinfo");
                }
            });
        }
        my_layout_main.addView(mMyView);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        width = dm.widthPixels;
        //加载用户头像
        ControllerCustomRoundAngleImageView headportraitImageView = mMyView.findViewById(R.id.headportrait);
        if (mPersonalInfoDataBean == null) {
            Glide.with(mControlMainActivity).load("").listener(new RequestListener<Drawable>() {
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
            }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(headportraitImageView);
            TextView username = mMyView.findViewById(R.id.username);
            username.setText(mMyView.getResources().getString(R.string.title_loginimmediately));
            TextView userinfo = mMyView.findViewById(R.id.userinfo);
            userinfo.setText(mMyView.getResources().getString(R.string.title_personalstatement));
        } else {
            if (mPersonalInfoDataBean.stu_name != null) {  //显示用户昵称
                TextView username = mMyView.findViewById(R.id.username);
                username.setText(mPersonalInfoDataBean.stu_name);
            } else {
                TextView username = mMyView.findViewById(R.id.username);
                username.setText("");
            }
            if (mPersonalInfoDataBean.autograph != null) {  //显示用户个性签名
                TextView userinfo = mMyView.findViewById(R.id.userinfo);
                userinfo.setText(mPersonalInfoDataBean.autograph);
            }
            Glide.with(mControlMainActivity).load(mPersonalInfoDataBean.head).listener(new RequestListener<Drawable>() {
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
            }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(headportraitImageView);
        }
    }

    //展示我的课程界面
    public void MyClassShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        //创建布局添加刷新控件
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyClassView == null) {
            mMyClassView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclass, null);
            //Smart_model_my_myclass     布局刷新控件

            mSmart_model_my_myclass = mMyClassView.findViewById(R.id.Smart_model_my_myclass);
            mSmart_model_my_myclass.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //先判断是否已获取了全部数据
                    if (mMyCourseSum <= mMyCourseCurrentPage * mMyCoursePageCount){
                        LinearLayout mycourse_end = mMyClassView.findViewById(R.id.mycourse_end);
                        mycourse_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getMyCourseListMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //加载我的课程数据
                    getMyCourseList();
                }
            });
        }
        my_layout_main.addView(mMyClassView);
        //我的课程界面
        TextView modelmy_myclass_main_titletext = mMyClassView.findViewById(R.id.modelmy_myclass_main_titletext);
        modelmy_myclass_main_titletext.setText("我的课程");
        //加载我的课程数据
        getMyCourseList();
    }

    //展示我的课程包界面
    public void MyClassPacketShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyClassPacketView == null) {
            mMyClassPacketView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclasspacket, null);
            //Smart_model_my_myclasspacket  刷新
            mSmart_model_my_myclasspacket = mMyClassPacketView.findViewById(R.id.Smart_model_my_myclasspacket);
            mSmart_model_my_myclasspacket.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //先判断是否已获取了全部数据
                    if (mMyCoursePacketSum <= mMyCoursePacketCurrentPage * mMyCoursePacketPageCount){
                        LinearLayout mycoursepacket_end = mMyClassPacketView.findViewById(R.id.mycoursepacket_end);
                        mycoursepacket_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getMyPacketListMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getMyPacketList();
                }
            });
        }
        my_layout_main.addView(mMyClassPacketView);
        //网络请求并且赋值
        getMyPacketList();
    }

    //展示我的收藏界面
    public void MyCollectShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCollectView == null) {
            mMyCollectView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycollect, null);
            //Smart_model_my_mycollect
            mSmart_model_my_mycollect = mMyCollectView.findViewById(R.id.Smart_model_my_mycollect);
            mSmart_model_my_mycollect.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //先判断是否已获取了全部数据
                    if (mMyCollectCurrentTab.equals("course")) {
                        if (mMyCollectSum <= mMyCollectCurrentPage * mMyCollectPageCount){
                            LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                            mycollect_end.setVisibility(View.VISIBLE);
                            return;
                        }
                        //加载网络数据  刷新页面
                        getModelMyClassCollectionMore();
                    } else if (mMyCollectCurrentTab.equals("coursepacket")) {
                        if (mMyCollectPacketSum <= mMyCollectPacketCurrentPage * mMyCollectPacketPageCount){
                            LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                            mycollect_end.setVisibility(View.VISIBLE);
                            return;
                        }
                        //加载网络数据  刷新页面
                        getModelMyClassPacketCollectionMore();
                    }
                }
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                    mycollect_end.setVisibility(View.INVISIBLE);
                    if (mMyCollectCurrentTab.equals("course")) {
                        //加载网络数据  刷新页面
                        getModelMyClassCollection();
                    } else if (mMyCollectCurrentTab.equals("coursepacket")) {
                        //加载网络数据  刷新页面
                        getModelMyClassPacketCollection();
                    }
                }
            });
          //我的课程控件  
            TextView modelmy_mycollect_tab_course = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_course);
            modelmy_mycollect_tab_course.setOnClickListener(v -> {
                if (!mMyCollectCurrentTab.equals("course")) {
                    ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
                    Animation animation = new TranslateAnimation((mMyCollectLastTabIndex - 1) * width / 2, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_mycollect_cursor1.startAnimation(animation);
                    TextView modelmy_mycollect_tab_coursepacket = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_coursepacket);
                    modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyCollectLastTabIndex = 1;
                mMyCollectCurrentTab = "course";
                //可循环调用以下方法，添加多个课程
                getModelMyClassCollection();
            });
            //课程包
            TextView modelmy_mycollect_tab_coursepacket = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_coursepacket);
            modelmy_mycollect_tab_coursepacket.setOnClickListener(v -> {
                if (!mMyCollectCurrentTab.equals("coursepacket")) {
                    ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
                    Animation animation = new TranslateAnimation((mMyCollectLastTabIndex - 1) * width / 2, width / 2, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_mycollect_cursor1.startAnimation(animation);
                    modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyCollectLastTabIndex = 2;
                mMyCollectCurrentTab = "coursepacket";
                getModelMyClassPacketCollection();
            });
        }
        my_layout_main.addView(mMyCollectView);
        ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
        int x = width / 4 - mMyCollectView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_mycollect_cursor1.setX(x);
        //默认选中的为课程
        mMyCollectLastTabIndex = 1;
        mMyCollectCurrentTab = "course";
        TextView modelmy_mycollect_tab_course = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_course);
        TextView modelmy_mycollect_tab_coursepacket = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_coursepacket);
        modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
        mycollect_end.setVisibility(View.INVISIBLE);
        //加载网络数据  我的课程
        getModelMyClassCollection();
    }

    //展示我的收藏界面-课程
    private View MyCollectShow_MyCourse(LinearLayout modelmy_mycollect_main_content,QueryMyCollectionListBean.DataBean.ListBean listBean) {
        //测试数据我的课程子条目
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclass1, null);
        //加载网络图片封面
        ControllerCustomRoundAngleImageView modelmy_myclass1_cover = view.findViewById(R.id.modelmy_myclass1_cover);
        Glide.with(mControlMainActivity).load(listBean.cover).listener(new RequestListener<Drawable>() {
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
        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_myclass1_cover);
        //课程名称
        TextView modelmy_myclass1_classname = view.findViewById(R.id.modelmy_myclass1_classname);
        modelmy_myclass1_classname.setText(listBean.course_name);
        //stuNum
        TextView modelmy_myclass1_state = view.findViewById(R.id.modelmy_myclass1_state);
        modelmy_myclass1_state.setText(listBean.stuNum + "");
        //类型
        ImageView iv_imagetype = view.findViewById(R.id.iv_imagetype);
        if (listBean.course_type.equals("直播,录播")){
            iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_mix));
        } else if (listBean.course_type.equals("直播")){
            iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_live));
        } else if (listBean.course_type.equals("录播")){
            iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_record));
        }
        modelmy_mycollect_main_content.addView(view);
        //添加每个课程的监听，点击跳转到课程详情
        view.setOnClickListener(v -> {
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.mCourseId = String.valueOf(listBean.course_id);
            courseInfo.mCourseCover = listBean.cover;
            courseInfo.mCourseType = listBean.course_type;
            courseInfo.mCourseName = listBean.course_name;
            courseInfo.mCoursePriceOld = String.valueOf(listBean.price);
            courseInfo.mCoursePrice = String.valueOf(listBean.special_price);
            courseInfo.mCourseLearnPersonNum = String.valueOf(listBean.stuNum);
            //跳转课程详情
            ModelCourseCover modelCourseCover = new ModelCourseCover();
            View modelCourseView = modelCourseCover.ModelCourseCover(mControlMainActivity, courseInfo);
            modelCourseCover.CourseDetailsShow();
            HideAllLayout();
            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
            my_layout_main.addView(modelCourseView);
            mControlMainActivity.onClickCourseDetails();
        });
        //我的收藏不需要显示课程协议，因为没有购买的课程也可以被收藏
        TextView modelmy_myclass1_agreement = view.findViewById(R.id.modelmy_myclass1_agreement);
        modelmy_myclass1_agreement.setVisibility(View.INVISIBLE);
        
        View modelmy_myclass1_line1 = view.findViewById(R.id.modelmy_myclass1_line1);
        return modelmy_myclass1_line1;
    }

    //展示我的收藏界面-课程包
    private View MyCollectShow_MyCoursePacket(LinearLayout modelmy_mycollect_main_content,QueryMyCollectionPacketListBean.DataBean.ListBean listBean) {
        //modelmy_myclasspacket1_line1
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclasspacket1, null);
        //加载网络图片封面
        ControllerCustomRoundAngleImageView modelmy_myclasspacket1_cover = view.findViewById(R.id.modelmy_myclasspacket1_cover);
        Glide.with(mControlMainActivity).load(listBean.cover).listener(new RequestListener<Drawable>() {
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
        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursepacketcover)).into(modelmy_myclasspacket1_cover);
        //课程包名称
        TextView modelmy_myclasspacket1_classname = view.findViewById(R.id.modelmy_myclasspacket1_classname);
        modelmy_myclasspacket1_classname.setText(listBean.cp_name);
        //阶段数量
        TextView modelmy_myclasspacket1_stagecount = view.findViewById(R.id.modelmy_myclasspacket1_stagecount);
        modelmy_myclasspacket1_stagecount.setText(listBean.stageNum + "");
        //课程数量
        TextView modelmy_myclasspacket1_coursecount = view.findViewById(R.id.modelmy_myclasspacket1_coursecount);
        modelmy_myclasspacket1_coursecount.setText(listBean.courseNum + "");
        //学习人数
        TextView modelmy_myclasspacket1_learnpersoncount = view.findViewById(R.id.modelmy_myclasspacket1_learnpersoncount);
        modelmy_myclasspacket1_learnpersoncount.setText(listBean.stuNum + "");
        modelmy_mycollect_main_content.addView(view);
        //刷新我的界面
        view.setOnClickListener(v -> {
            CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
            coursePacketInfo.mCoursePacketId = String.valueOf(listBean.course_package_id);//课程包id
            coursePacketInfo.mCoursePacketCover = listBean.cover;
            coursePacketInfo.mCoursePacketStageNum = String.valueOf(listBean.stageNum);
            coursePacketInfo.mCoursePacketName = listBean.cp_name;   //包名
            coursePacketInfo.mCoursePacketPrice = String.valueOf(listBean.favorable_price);//总价格
            coursePacketInfo.mCoursePacketCourseNum = String.valueOf(listBean.courseNum);
            coursePacketInfo.mCoursePacketPriceOld = String.valueOf(listBean.total_price);//数据原来的价格
            coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(listBean.buying_base_number);//购买人数
            //跳转课程包详情
            ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
            View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
            modelCoursePacketCover.CoursePacketDetailsShow();
            HideAllLayout();
            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
            my_layout_main.addView(modelCoursePacketView);
            mControlMainActivity.onClickCoursePacketDetails();
        });
        
        //我的收藏不需要显示课程协议，因为没有购买的课程也可以被收藏
        TextView modelmy_myclasspacket1_agreement = view.findViewById(R.id.modelmy_myclasspacket1_agreement);
        modelmy_myclasspacket1_agreement.setVisibility(View.INVISIBLE);
        //我的收藏不需要显示学习进度，因为没有购买的课程也可以被收藏
        LinearLayout modelmy_myclasspacket1_learnprogresslayout = view.findViewById(R.id.modelmy_myclasspacket1_learnprogresslayout);
        modelmy_myclasspacket1_learnprogresslayout.setVisibility(View.INVISIBLE);
        //布局控件的线
        View modelmy_myclasspacket1_line1 = view.findViewById(R.id.modelmy_myclasspacket1_line1);
        return modelmy_myclasspacket1_line1;
    }

    //展示我的缓存界面
    public void MyCacheShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCacheView == null) {
            mMyCacheView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycache, null);
            //Smart_model_my_mycache  我的缓存刷新
//            mSmart_model_my_mycache = mMyCacheView.findViewById(R.id.Smart_model_my_mycache);
//            mSmart_model_my_mycache.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//                @Override
//                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                    mSmart_model_my_mycache.finishLoadMore();
//                }
//
//                @Override
//                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                    mSmart_model_my_mycache.finishRefresh();
//                }
//            });
        }
        my_layout_main.addView(mMyCacheView);
        //我的缓存子条目布局
        LinearLayout modelmy_mycache_main_content = mMyCacheView.findViewById(R.id.modelmy_mycache_main_content);
        modelmy_mycache_main_content.removeAllViews();
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycache1, null);
        modelmy_mycache_main_content.addView(view);
        ControllerCustomRoundAngleImageView modelmy_mycache1_cover = view.findViewById(R.id.modelmy_mycache1_cover);
        Glide.with(mControlMainActivity).load("").listener(new RequestListener<Drawable>() {
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
        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_mycache1_cover);

        //监听，如果点击此课程，查看此课程下缓存的详细信息
        LinearLayout modelmy_mycache1_classname_layout = view.findViewById(R.id.modelmy_mycache1_classname_layout);
        modelmy_mycache1_classname_layout.setOnClickListener(v -> {
            MyCache_ManagementCacheShow();
        });
        //布局控件的线
        View modelmy_mycache1_line1 = view.findViewById(R.id.modelmy_mycache1_line1);
        modelmy_mycache1_line1.setVisibility(View.INVISIBLE);
    }

    //展示我的缓存界面
    public void MyCacheShow(DownloadView downloadView) {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCacheView == null) {
            mMyCacheView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycache, null);
        }
        RelativeLayout modelmy_mycache_title = mMyCacheView.findViewById(R.id.modelmy_mycache_title);
        modelmy_mycache_title.setVisibility(View.VISIBLE);
        RelativeLayout modelmy_mycache_aliyunVodPlayerView = mMyCacheView.findViewById(R.id.modelmy_mycache_aliyunVodPlayerView);
        modelmy_mycache_aliyunVodPlayerView.removeAllViews();
        modelmy_mycache_aliyunVodPlayerView.setVisibility(View.INVISIBLE);
        my_layout_main.addView(mMyCacheView);
        if (downloadView.getParent() != null){
            LinearLayout parentView = (LinearLayout) downloadView.getParent();
            parentView.removeAllViews();
        }
        //我的缓存子条目布局
        LinearLayout modelmy_mycache_main_content = mMyCacheView.findViewById(R.id.modelmy_mycache_main_content);
        modelmy_mycache_main_content.removeAllViews();
        if (downloadView != null){
            modelmy_mycache_main_content.addView(downloadView);
//            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) downloadView.getLayoutParams();
//            ll.height = LinearLayout.LayoutParams.MATCH_PARENT;
//            ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
//            downloadView.setLayoutParams(ll);
            downloadView.showDownloadContentView();
//        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycache1, null)
        };
//        modelmy_mycache_main_content.addView(view);
//        ControllerCustomRoundAngleImageView modelmy_mycache1_cover = view.findViewById(R.id.modelmy_mycache1_cover);
//        Glide.with(mControlMainActivity).load("").listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                Log.d("Wain", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                Log.d("Wain", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
//                return false;
//            }
//        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_mycache1_cover);
//
//        //监听，如果点击此课程，查看此课程下缓存的详细信息
//        LinearLayout modelmy_mycache1_classname_layout = view.findViewById(R.id.modelmy_mycache1_classname_layout);
//        modelmy_mycache1_classname_layout.setOnClickListener(v -> {
//            MyCache_ManagementCacheShow();
//        });
//        //布局控件的线
//        View modelmy_mycache1_line1 = view.findViewById(R.id.modelmy_mycache1_line1);
//        modelmy_mycache1_line1.setVisibility(View.INVISIBLE);
    }

    public boolean MyCacheShow_Play(){
        if (mMyCacheView == null){
            return false;
        }
        RelativeLayout modelmy_mycache_title = mMyCacheView.findViewById(R.id.modelmy_mycache_title);
        modelmy_mycache_title.setVisibility(View.INVISIBLE);
        RelativeLayout modelmy_mycache_aliyunVodPlayerView = mMyCacheView.findViewById(R.id.modelmy_mycache_aliyunVodPlayerView);
        if (modelmy_mycache_aliyunVodPlayerView.getVisibility() == View.INVISIBLE){
            modelmy_mycache_aliyunVodPlayerView.removeAllViews();
            AliyunVodPlayerView aliyunVodPlayerView = new AliyunVodPlayerView(mControlMainActivity);
            modelmy_mycache_aliyunVodPlayerView.addView(aliyunVodPlayerView);
            modelmy_mycache_aliyunVodPlayerView.setVisibility(View.VISIBLE);
//        modelmy_mycache_aliyunVodPlayerView.VideoIdSet();
            mControlMainActivity.setmAliyunVodPlayerView(aliyunVodPlayerView);
        }
        return true;
    }
    //展示我的缓存-管理缓存界面
    public void MyCache_ManagementCacheShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCacheManagementCacheView == null) {
            mMyCacheManagementCacheView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download_manager, null);
        }
        my_layout_main.addView(mMyCacheManagementCacheView);
        
        TextView course_downloadmanager_layout_titletext = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_titletext);
        course_downloadmanager_layout_titletext.setText("管理缓存");
        int count = 0;
        LinearLayout course_downloadmanager_layout_content = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_content);
        course_downloadmanager_layout_content.removeAllViews();
//        for (int i = 0; i < CourseInfo1.mCourseChaptersInfoList.size(); i++) {
//            CourseChaptersInfo courseChaptersInfo = CourseInfo1.mCourseChaptersInfoList.get(i);
//            if (courseChaptersInfo == null) {
//                continue;
//            }
//            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download_manager_child, null);
//            TextView course_downloadmanager_child_titletext = view.findViewById(R.id.course_downloadmanager_child_titletext);
//            course_downloadmanager_child_titletext.setText(courseChaptersInfo.mCourseChaptersName);
//            course_downloadmanager_child_titletext.setHint(courseChaptersInfo.mCourseChaptersId);
//            if (courseChaptersInfo.mCourseSectionsInfoList.size() == 0) {
//                View course_downloadmanager_child_line1 = view.findViewById(R.id.course_downloadmanager_child_line1);
//                course_downloadmanager_child_line1.setVisibility(View.INVISIBLE);
//            }
//            course_downloadmanager_layout_content.addView(view);
//            LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
//            course_downloadmanager_child_content.removeAllViews();
//            for (int num = 0; num < courseChaptersInfo.mCourseSectionsInfoList.size(); num++) {
//                CourseSectionsInfo courseSectionsInfo = courseChaptersInfo.mCourseSectionsInfoList.get(num);
//                if (courseSectionsInfo == null) {
//                    continue;
//                }
//                CourseRecordPlayDownloadInfo info = mCourseRecordPlayDownloadInfoMap.get(courseSectionsInfo.mCourseSectionsId);
//                if (info == null) { //没有添加下载的不做处理
//                    continue;
//                }
//                View MyOrderShow_MyOrder_view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download_manager_child1, null);
//                TextView course_downloadmanager_child1_name = MyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child1_name);
//                course_downloadmanager_child1_name.setText(courseSectionsInfo.mCourseSectionsName);
//                course_downloadmanager_child1_name.setHint(courseSectionsInfo.mCourseSectionsId);
//                ProgressBar progress_bar_healthy = MyOrderShow_MyOrder_view2.findViewById(R.id.progress_bar_healthy);
//                int progress = 0;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    progress = Math.toIntExact(Long.valueOf(info.mCourseSectionsDownloadSize)
//                            / Long.valueOf(info.mCourseSectionsSize));
//                }
//                progress_bar_healthy.setProgress(progress);
//                ImageView course_downloadmanager_child1_function = MyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child1_function);
//                TextView course_downloadmanager_child_state = MyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child_state);
//                course_downloadmanager_child1_function.setOnClickListener(v -> {
//                    int id = getV7ImageResourceId(course_downloadmanager_child1_function);
//                    if (id == R.drawable.button_pause_blue) {
//                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_play_blue);
//                        course_downloadmanager_child_state.setText("已暂停");
//                        progress_bar_healthy.setProgressDrawable(MyOrderShow_MyOrder_view2.getResources().getDrawable(R.drawable.progressbar_bg1));
//                    } else if (id == R.drawable.button_play_blue) {
//                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_pause_blue);
////                        course_downloadmanager_child_state.setText("下载中");
////                        progress_bar_healthy.setProgressDrawable(MyOrderShow_MyOrder_view2.getResources().getDrawable(R.drawable.progressbar_bg));
////                    }
////                });
////                course_downloadmanager_child_content.addView(MyOrderShow_MyOrder_view2);
////                count++;
////            }
////        }
        TextView course_downloadmanager_num = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_num);
        course_downloadmanager_num.setText("0");
        TextView course_downloadmanager_sumnum = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_sumnum);
        course_downloadmanager_sumnum.setText("/" + count);
        //获取手机剩余存储空间
        TextView course_downloadmanager_availalesize = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_availalesize);
        long size = getAvailaleSize();
        course_downloadmanager_availalesize.setText("剩余空间：" + size + "G");
        ImageView course_downloadmanager_layout_return_button1 = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_return_button1);
        course_downloadmanager_layout_return_button1.setOnClickListener(v -> { //管理缓存界面的返回
            MyCacheShow();
        });
        TextView course_downloadmanager_all = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_all);
        course_downloadmanager_all.setOnClickListener(v -> { //点击全部暂停
            int num = course_downloadmanager_layout_content.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = course_downloadmanager_layout_content.getChildAt(i);
                LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                int childCount = course_downloadmanager_child_content.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    View childView = course_downloadmanager_child_content.getChildAt(j);
                    ImageView course_downloadmanager_child1_function = childView.findViewById(R.id.course_downloadmanager_child1_function);
                    TextView course_downloadmanager_child_state = childView.findViewById(R.id.course_downloadmanager_child_state);
                    ProgressBar progress_bar_healthy = childView.findViewById(R.id.progress_bar_healthy);
                    int id = getV7ImageResourceId(course_downloadmanager_child1_function);
                    if (id == R.drawable.button_pause_blue) {
                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_play_blue);
                        course_downloadmanager_child_state.setText("已暂停");
                        progress_bar_healthy.setProgressDrawable(childView.getResources().getDrawable(R.drawable.progressbar_bg1));
                    }
                }
            }

        });
        TextView course_downloadmanager_startall = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_startall);
        course_downloadmanager_startall.setOnClickListener(v -> { //点击全部开始
            int num = course_downloadmanager_layout_content.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = course_downloadmanager_layout_content.getChildAt(i);
                LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                int childCount = course_downloadmanager_child_content.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    View childView = course_downloadmanager_child_content.getChildAt(j);
                    ImageView course_downloadmanager_child1_function = childView.findViewById(R.id.course_downloadmanager_child1_function);
                    TextView course_downloadmanager_child_state = childView.findViewById(R.id.course_downloadmanager_child_state);
                    ProgressBar progress_bar_healthy = childView.findViewById(R.id.progress_bar_healthy);
                    int id = getV7ImageResourceId(course_downloadmanager_child1_function);
                    if (id == R.drawable.button_play_blue) {
                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_pause_blue);
                        course_downloadmanager_child_state.setText("下载中");
                        progress_bar_healthy.setProgressDrawable(childView.getResources().getDrawable(R.drawable.progressbar_bg));
                    }
                }
            }
        });
        TextView course_downloadmanager_layout_edit = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_edit);
        LinearLayout course_downloadmanager_function = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_function);
        LinearLayout course_downloadmanager_function1 = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_function1);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_downloadmanager_function.getLayoutParams();
        ll.height = mMyCacheManagementCacheView.getResources().getDimensionPixelSize(R.dimen.dp40);
        course_downloadmanager_function.setLayoutParams(ll);
        ll = (LinearLayout.LayoutParams) course_downloadmanager_function1.getLayoutParams();
        ll.height = 0;
        course_downloadmanager_function1.setLayoutParams(ll);
        //编辑
        course_downloadmanager_layout_edit.setText("编辑");
        course_downloadmanager_layout_edit.setOnClickListener(v -> {
            if (course_downloadmanager_layout_edit.getText().toString().equals("编辑")) { //跳转到编辑界面
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) course_downloadmanager_function.getLayoutParams();
                llp.height = 0;
                course_downloadmanager_function.setLayoutParams(llp);
                llp = (LinearLayout.LayoutParams) course_downloadmanager_function1.getLayoutParams();
                llp.height = mMyCacheManagementCacheView.getResources().getDimensionPixelSize(R.dimen.dp40);
                course_downloadmanager_function1.setLayoutParams(llp);
                course_downloadmanager_layout_edit.setText("完成");
                int num = course_downloadmanager_layout_content.getChildCount();
                for (int i = 0; i < num; i++) {
                    View view = course_downloadmanager_layout_content.getChildAt(i);
                    LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                    int childCount = course_downloadmanager_child_content.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        View childView = course_downloadmanager_child_content.getChildAt(j);
                        ImageView course_downloadmanager_child1_select = childView.findViewById(R.id.course_downloadmanager_child1_select);
                        LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) course_downloadmanager_child1_select.getLayoutParams();
                        LL.width = childView.getResources().getDimensionPixelSize(R.dimen.dp20);
                        LL.leftMargin = childView.getResources().getDimensionPixelSize(R.dimen.dp13);
                        course_downloadmanager_child1_select.setLayoutParams(LL);
                        course_downloadmanager_child1_select.setOnClickListener(View -> {
                            int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                            if (id == R.drawable.button_select_gray) {
                                course_downloadmanager_child1_select.setBackgroundResource(R.drawable.button_select_red);
                            } else {
                                course_downloadmanager_child1_select.setBackgroundResource(R.drawable.button_select_gray);
                            }
                        });
                    }
                }
            } else if (course_downloadmanager_layout_edit.getText().toString().equals("完成")) {  //点击完成，跳转界面
                MyCache_ManagementCacheShow();
            }
        });
        //全部选择
        TextView course_downloadmanager_allselect = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_allselect);
        course_downloadmanager_allselect.setOnClickListener(v -> {
            int num = course_downloadmanager_layout_content.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = course_downloadmanager_layout_content.getChildAt(i);
                LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                int childCount = course_downloadmanager_child_content.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    View childView = course_downloadmanager_child_content.getChildAt(j);
                    ImageView course_downloadmanager_child1_select = childView.findViewById(R.id.course_downloadmanager_child1_select);
                    int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                    if (id == R.drawable.button_select_gray) {
                        course_downloadmanager_child1_select.setBackgroundResource(R.drawable.button_select_red);
                    }
                }
            }
        });
        //删除
        TextView course_downloadmanager_delete = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_delete);
        course_downloadmanager_delete.setOnClickListener(v -> {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view.findViewById(R.id.tip);
            tip.setText("删除所选内容");
            TextView dialog_content = view.findViewById(R.id.dialog_content);
            dialog_content.setText("确定删除所选内容吗？");
            TextView button_cancel = view.findViewById(R.id.button_cancel);
            button_cancel.setText("取消");
            button_cancel.setOnClickListener(View -> {
                mMyDialog.cancel();
            });
            TextView button_sure = view.findViewById(R.id.button_sure);
            button_sure.setText("确定");
            button_sure.setOnClickListener(View -> {
                int num = course_downloadmanager_layout_content.getChildCount();
                for (int i = 0; i < num; i++) {
                    View childView = course_downloadmanager_layout_content.getChildAt(i);
                    LinearLayout course_downloadmanager_child_content = childView.findViewById(R.id.course_downloadmanager_child_content);
                    int childCount = course_downloadmanager_child_content.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        View childMyOrderShow_MyOrder_view2 = course_downloadmanager_child_content.getChildAt(j);
                        ImageView course_downloadmanager_child1_select = childMyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child1_select);
                        int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                        if (id == R.drawable.button_select_red) {//将选中的项目缓存全部清除
                            TextView course_downloadmanager_child1_name = childMyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child1_name);
                            if (this.mCourseRecordPlayDownloadInfoMap.get(course_downloadmanager_child1_name.getHint().toString()) != null) {
                                this.mCourseRecordPlayDownloadInfoMap.remove(course_downloadmanager_child1_name.getHint().toString());
                            }
                        }
                    }
                }
                mMyDialog.cancel();
                MyCache_ManagementCacheShow();
            });
        });
    }

    //获取可用内存大小
    private long getAvailaleSize() {

        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024 / 1024;

        //(availableBlocks * blockSize)/1024      KIB 单位

        //(availableBlocks * blockSize)/1024 /1024  MIB单位
    }

    private static int getV7ImageResourceId(ImageView imageView) {
        int imgid = 0;
        Field[] fields = imageView.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals("mBackgroundTintHelper")) {
                f.setAccessible(true);
                try {
                    Object obj = f.get(imageView);
                    Field[] fields2 = obj.getClass().getDeclaredFields();
                    for (Field f2 : fields2) {
                        if (f2.getName().equals("mBackgroundResId")) {
                            f2.setAccessible(true);
                            imgid = f2.getInt(obj);
                            Log.d("1111", "Image ResourceId:" + imgid);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgid;
    }

    //展示我的订单界面
    public void MyOrderShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyOrderView == null) {
            mMyOrderView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myorder, null);
            //Smart_model_my_myorder   我的订单
            mSmart_model_my_myorder = mMyOrderView.findViewById(R.id.Smart_model_my_myorder);
            mSmart_model_my_myorder.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyOrderSum <= mMyOrderCurrentPage * mMyOrderPageCount){
                        LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                        myorder_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (mMyOrderCurrentTab.equals("all")){
                        getModelMyOrderListMore("全部");
                    } else if (mMyOrderCurrentTab.equals("finished")){
                        getModelMyOrderListMore("已完成");
                    } else if (mMyOrderCurrentTab.equals("unfinish")){
                        getModelMyOrderListMore("未完成");
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                    myorder_end.setVisibility(View.INVISIBLE);
                    if (mMyOrderCurrentTab.equals("all")){
                        getModelMyOrderList("全部");
                    } else if (mMyOrderCurrentTab.equals("finished")){
                        getModelMyOrderList("已完成");
                    } else if (mMyOrderCurrentTab.equals("unfinish")){
                        getModelMyOrderList("未完成");
                    }
                }
            });
            //全部
            TextView modelmy_myorder_tab_all = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_all);
            modelmy_myorder_tab_all.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("all")) {
                    ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
                    Animation animation = new TranslateAnimation((mMyOrderLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_myorder_cursor1.startAnimation(animation);
                    TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
                    TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                    modelmy_myorder_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_myorder_tab_finished.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_unfinish.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyOrderLastTabIndex = 1;
                mMyOrderCurrentTab = "all";
                LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
                modelmy_myorder_main_content.removeAllViews();
                LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                myorder_end.setVisibility(View.INVISIBLE);
                //可循环调用以下方法，添加多个订单
                getModelMyOrderList("全部");
            });
            //已完成
            TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
            modelmy_myorder_tab_finished.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("finished")) {
                    ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
                    Animation animation = new TranslateAnimation((mMyOrderLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_myorder_cursor1.startAnimation(animation);
                    TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                    modelmy_myorder_tab_unfinish.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_finished.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyOrderLastTabIndex = 2;
                mMyOrderCurrentTab = "finished";
                LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
                modelmy_myorder_main_content.removeAllViews();
                LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                myorder_end.setVisibility(View.INVISIBLE);
                //可循环调用以下方法，添加多个订单
                getModelMyOrderList("已完成");
            });
            //未完成
            TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
            modelmy_myorder_tab_unfinish.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("unfinish")) {
                    ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
                    Animation animation = new TranslateAnimation((mMyOrderLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_myorder_cursor1.startAnimation(animation);
                    modelmy_myorder_tab_unfinish.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_myorder_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_finished.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyOrderLastTabIndex = 3;
                mMyOrderCurrentTab = "unfinish";
                LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
                modelmy_myorder_main_content.removeAllViews();
                LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                myorder_end.setVisibility(View.INVISIBLE);
                //可循环调用以下方法，添加多个订单
                getModelMyOrderList("未完成");
            });
        }
        ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
        int x = width / 6 - mMyOrderView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_myorder_cursor1.setX(x);
        //默认选中的为所有
        mMyOrderLastTabIndex = 1;
        mMyOrderCurrentTab = "all";
        my_layout_main.addView(mMyOrderView);
        LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
        myorder_end.setVisibility(View.INVISIBLE);
        //可循环调用以下方法，添加多个订单
        getModelMyOrderList("全部");
    }

    //展示我的订单-子集合
    private void MyOrderShow_MyOrder(LinearLayout modelmy_myorder_main_content,MyOrderlistBean.DataBean.ListBean listBean) {
        //MyOrderShow_MyOrder_view3支付需要控制订单是否取消和是否重新支付
        if (listBean.order_status.equals("未支付")) {
            //全部订单的网络数据
            //待支付
            View MyOrderShow_MyOrder_view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername1.setText(listBean.product_name);
            TextView modelmy_myorder1_orderstate1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate1.setText("待支付");
            modelmy_myorder1_orderstate1.setTextColor(MyOrderShow_MyOrder_view1.getResources().getColor(R.color.text_orange1));
            TextView modelmy_myorder1_ordernumber1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber1.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney1.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view1); //待支付
            //待支付的订单取消和重新支付
            ImageView modelmy_myorder1_cancel = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_cancel);
            ImageView modelmy_myorder1_retrypay = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_retrypay);
            modelmy_myorder1_cancel.setOnClickListener(v -> {
                getCancelOrder(MyOrderShow_MyOrder_view1,listBean.order_id);
            });
            modelmy_myorder1_retrypay.setOnClickListener(v -> {
                HideAllLayout();
                View orderDetails = mControlMainActivity.Page_OrderDetails(this,null,null, listBean);
                LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                my_layout_main.addView(orderDetails);
            });
        } else if (listBean.order_status.equals("支付成功") || listBean.order_status.equals("已支付")|| listBean.order_status.equals("已确认转账")) {
            //已经完成
          // modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view1);
            View MyOrderShow_MyOrder_view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername.setText(listBean.product_name);
            TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate.setText("支付成功");
//            modelmy_myorder1_orderstate.setTextColor(MyOrderShow_MyOrder_view3.getResources().getColor(R.color.green1));
            TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view3); //已完成
            //已完成
            MyOrderShow_MyOrder_view3.setOnClickListener(v -> {
                getModelMyOrderDetails(listBean.order_id,"success");
            });
            RelativeLayout modelmy_myorder1_orderfunction = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderfunction);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction.getLayoutParams();
            rl.height = 0;
            modelmy_myorder1_orderfunction.setLayoutParams(rl);
        } else if (listBean.order_status.equals("支付失败")||listBean.order_status.equals("已失效")||listBean.order_status.equals("订单已取消")||listBean.order_status.equals("取消订单")) {
            //未完成
            View MyOrderShow_MyOrder_view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername.setText(listBean.product_name);
            TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate.setText(listBean.order_status);
            modelmy_myorder1_orderstate.setTextColor(MyOrderShow_MyOrder_view2.getResources().getColor(R.color.holo_red_dark));
            TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view2);
            MyOrderShow_MyOrder_view2.setOnClickListener(v -> {
                getModelMyOrderDetails(listBean.order_id,"fail");
            });
            RelativeLayout modelmy_myorder1_orderfunction = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderfunction);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction.getLayoutParams();
            rl.height = 0;
            modelmy_myorder1_orderfunction.setLayoutParams(rl);
        } else if (listBean.order_status.equals("汇款确认中")) {
            //已经完成
            // modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view1);
            View MyOrderShow_MyOrder_view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername.setText(listBean.product_name);
            TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate.setText("汇款确认中");
            modelmy_myorder1_orderstate.setTextColor(MyOrderShow_MyOrder_view3.getResources().getColor(R.color.holo_red_dark));
            TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view3); //汇款确认中
            //汇款确认中
            MyOrderShow_MyOrder_view3.setOnClickListener(v -> {
                getModelMyOrderDetails(listBean.order_id,"success");
            });
            RelativeLayout modelmy_myorder1_orderfunction = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderfunction);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction.getLayoutParams();
            rl.height = 0;
            modelmy_myorder1_orderfunction.setLayoutParams(rl);
        }
    }
//       //订单待支付
//    private void initOrderAll() {
//        TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordername);
//        modelmy_myorder1_ordername.setText(mMyOrderListBean.product_name);
//        TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderstate);
//        modelmy_myorder1_orderstate.setText(mMyOrderListBean.order_status);
//        modelmy_myorder1_orderstate.setTextColor(MyOrderShow_MyOrder_view2.getResources().getColor(R.color.holo_red_dark));
//        TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordernumber);
//        modelmy_myorder1_ordernumber.setText(mMyOrderListBean.order_num);
//        TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordermoney);
//        modelmy_myorder1_ordermoney.setText(mMyOrderListBean.product_price+"");
//    }
//         //订单支付成功
//    private void initOrderFinish() {
//        TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordername);
//        modelmy_myorder1_ordername.setText(mMyOrderListBean.product_name);
//        TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderstate);
//        modelmy_myorder1_orderstate.setText(mMyOrderListBean.order_status);
//        modelmy_myorder1_orderstate.setTextColor(MyOrderShow_MyOrder_view2.getResources().getColor(R.color.holo_red_dark));
//        TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordernumber);
//        modelmy_myorder1_ordernumber.setText(mMyOrderListBean.order_num);
//        TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordermoney);
//        modelmy_myorder1_ordermoney.setText(mMyOrderListBean.product_price+"");
//    }
//    //订单支付不成功
//        private void initOrderUnFinish() {
//        TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordername);
//        modelmy_myorder1_ordername.setText(mMyOrderListBean.product_name);
//        TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderstate);
//        modelmy_myorder1_orderstate.setText(mMyOrderListBean.order_status);
//        modelmy_myorder1_orderstate.setTextColor(MyOrderShow_MyOrder_view2.getResources().getColor(R.color.holo_red_dark));
//        TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordernumber);
//        modelmy_myorder1_ordernumber.setText(mMyOrderListBean.order_num);
//        TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordermoney);
//        modelmy_myorder1_ordermoney.setText(mMyOrderListBean.product_price+"");
//    }
    //订单详情
    private void MyOrderShow_OrderDetails(MyOrderDetailsBean.DataBean dataBean,String state) {
        if (mview == null || dataBean == null) {
            return;
        }
        mControlMainActivity.onClickMyOrderDetails();
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyOrderDetailsView == null) {
            //订单详情的刷新控件
            mMyOrderDetailsView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myorderdetails, null);
            //Smart_model_my_myorderdetails  刷新控件
            mSmart_model_my_myorderdetails = mMyOrderDetailsView.findViewById(R.id.Smart_model_my_myorderdetails);
            mSmart_model_my_myorderdetails.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    mSmart_model_my_myorderdetails.finishLoadMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    mSmart_model_my_myorderdetails.finishRefresh();
                }
            });

        }
        //订单名称
        TextView modelmy_myorderdetails_name = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_name);
        modelmy_myorderdetails_name.setText(dataBean.product_name);
        //订单价格
        TextView modelmy_myorderdetails_price = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_price);
        modelmy_myorderdetails_price.setText(dataBean.product_price + "");
        //下单时间
        TextView modelmy_myorderdetails_ordertime = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_ordertime);
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = df.parse(dataBean.order_time);
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
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dataBean.order_time = df2.format(date1).toString();
            }
        }
        modelmy_myorderdetails_ordertime.setText(dataBean.order_time);
        //订单编号
        TextView modelmy_myorderdetails_ordernumber = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_ordernumber);
        modelmy_myorderdetails_ordernumber.setText(dataBean.order_num + "");
        //产品金额
        TextView modelmy_myorderdetails_courseprice = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_courseprice);
        modelmy_myorderdetails_courseprice.setText(dataBean.product_price + "");
        //优惠金额
        TextView modelmy_myorderdetails_coursediscountprice = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_coursediscountprice);
        modelmy_myorderdetails_coursediscountprice.setText(dataBean.refund_payment_money + "");
        //实际付款
        TextView modelmy_myorderdetails_payamount = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_payamount);
        modelmy_myorderdetails_payamount.setText(dataBean.refund_payment_money + "");
        //点击复制订单号
        ImageView modelmy_myorderdetails_ordernumbercopy = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_ordernumbercopy);
        modelmy_myorderdetails_ordernumbercopy.setOnClickListener(v -> {
            String modelmy_myorderdetails_ordernumbertext = modelmy_myorderdetails_ordernumber.getText().toString();
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) mControlMainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", modelmy_myorderdetails_ordernumbertext);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(mControlMainActivity, "已将订单号复制到剪贴板", Toast.LENGTH_SHORT).show();
        });
        my_layout_main.addView(mMyOrderDetailsView);
        //文件订单详情   订单判断的时间
        ImageView modelmy_myorderdetails_icon = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_icon);
        ImageView modelmy_myorderdetails_background_fail = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_background_fail);
        TextView modelmy_myorderdetails_invalid = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_invalid);
        ImageView modelmy_myorderdetails_invalidicon = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_invalidicon);
        if (state.equals("success")) { //成功的详情背景
            modelmy_myorderdetails_invalid.setText("支付成功");
            modelmy_myorderdetails_background_fail.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_background_success));
            modelmy_myorderdetails_icon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_success));
            modelmy_myorderdetails_invalidicon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_paysuccess));
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorderdetails_invalidicon.getLayoutParams();
            rl.width = mMyOrderDetailsView.getResources().getDimensionPixelSize(R.dimen.dp_77);
            modelmy_myorderdetails_invalidicon.setLayoutParams(rl);
        } else { //反之失败的详情背景
            modelmy_myorderdetails_invalid.setText("订单已失效");
            modelmy_myorderdetails_background_fail.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_background_fail));
            modelmy_myorderdetails_icon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_fail));
            modelmy_myorderdetails_invalidicon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_invalid));
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorderdetails_invalidicon.getLayoutParams();
            rl.width = mMyOrderDetailsView.getResources().getDimensionPixelSize(R.dimen.dp_58);
            modelmy_myorderdetails_invalidicon.setLayoutParams(rl);
        }
        //文字栅格化
        modelmy_myorderdetails_coursediscountprice.setPaintFlags(modelmy_myorderdetails_coursediscountprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    //展示我的优惠券界面
    public void MyCouponShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCouponView == null) {
            mMyCouponView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycoupon, null);
            //Smart_model_my_mycoupon   我的优惠券列表
            mSmart_model_my_mycoupon = mMyCouponView.findViewById(R.id.Smart_model_my_mycoupon);
            mSmart_model_my_mycoupon.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyCouponSum <= mMyCouponCurrentPage * mMyCouponPageCount){
                        LinearLayout mycoupon_end = mMyCouponView.findViewById(R.id.mycoupon_end);
                        mycoupon_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    //获取网络数据
                    if (mMyCouponCurrentTab.equals("notused")) {
                        getMyCouponListMore("未使用");
                    } else if (mMyCouponCurrentTab.equals("alreadyused")) {
                        getMyCouponListMore("已使用");
                    } else if (mMyCouponCurrentTab.equals("expired")) {
                        getMyCouponListMore("已过期");
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    if (mMyCouponCurrentTab.equals("notused")) {
                        getMyCouponList("未使用");
                    } else if (mMyCouponCurrentTab.equals("alreadyused")) {
                        getMyCouponList("已使用");
                    } else if (mMyCouponCurrentTab.equals("expired")) {
                        getMyCouponList("已过期");
                    }
                }
            });
            TextView modelmy_mycoupon_tab_notused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_notused);
            modelmy_mycoupon_tab_notused.setOnClickListener(v -> {
                if (!mMyCouponCurrentTab.equals("notused")) {
                    ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
                    Animation animation = new TranslateAnimation((mMyCouponLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_mycoupon_cursor1.startAnimation(animation);
                    TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
                    TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                    modelmy_mycoupon_tab_notused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_mycoupon_tab_alreadyused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_expired.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyCouponLastTabIndex = 1;
                mMyCouponCurrentTab = "notused";        //未使用的优惠券
                getMyCouponList("未使用");
            });
            TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
            modelmy_mycoupon_tab_alreadyused.setOnClickListener(v -> {
                if (!mMyCouponCurrentTab.equals("alreadyused")) {
                    ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
                    Animation animation = new TranslateAnimation((mMyCouponLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_mycoupon_cursor1.startAnimation(animation);
                    TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                    modelmy_mycoupon_tab_expired.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_notused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_alreadyused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                //已使用
                mMyCouponLastTabIndex = 2;
                mMyCouponCurrentTab = "alreadyused";
                getMyCouponList("已使用");
            });
            TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
            modelmy_mycoupon_tab_expired.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("expired")) {
                    ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
                    Animation animation = new TranslateAnimation((mMyCouponLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_mycoupon_cursor1.startAnimation(animation);
                    modelmy_mycoupon_tab_expired.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_mycoupon_tab_notused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_alreadyused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                //已过期
                mMyCouponLastTabIndex = 3;
                mMyCouponCurrentTab = "expired";
                getMyCouponList("已过期");
            });
            //点击兑换
            TextView modelmy_mycoupon_main_exchange = mMyCouponView.findViewById(R.id.modelmy_mycoupon_main_exchange);
            modelmy_mycoupon_main_exchange.setOnClickListener(v -> {
                //点击兑换弹出兑换对话框
                View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel1, null);
                mMyCouponDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view, R.style.DialogTheme);
                mMyCouponDialog.setCancelable(true);
                mMyCouponDialog.show();
                TextView button_cancel = view.findViewById(R.id.button_cancel);
                button_cancel.setOnClickListener(View -> {
                    mMyCouponDialog.cancel();
                });
                TextView button_sure = view.findViewById(R.id.button_sure);
                button_sure.setOnClickListener(View -> {
                    //开始兑换优惠码
                    mMyCouponDialog.cancel();
                });
            });
        }
        ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
        int x = width / 6 - mMyCouponView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_mycoupon_cursor1.setX(x);
        //默认选中的未使用
        mMyCouponLastTabIndex = 1;
        mMyCouponCurrentTab = "notused";
        my_layout_main.addView(mMyCouponView);
        getMyCouponList("未使用");
    }

    //展示我的优惠券-优惠券列表
    private void MyCouponShow_MyCoupon(LinearLayout modelmy_myorder_main_content,MyCoupon.DataBean.ListBean listBean) {
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mycoupon1, null);
        modelmy_myorder_main_content.addView(view);
        if (listBean.preferential_way.equals("满减")){
            //添加满减卷
            String price[] = listBean.dc_denomination.split(",");
            if (price.length > 2){
                return;
            }
            TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);//价格
            modelmy_mycoupon1_couponprice.setText(price[0]);
            TextView modelmy_mycoupon1_couponfullreduction = view.findViewById(R.id.modelmy_mycoupon1_couponfullreduction);//优惠券
            modelmy_mycoupon1_couponfullreduction.setText("满减券");
            TextView modelmy_mycoupon1_termofvaliditydata = view.findViewById(R.id.modelmy_mycoupon1_termofvaliditydata);//有效期
            modelmy_mycoupon1_termofvaliditydata.setText(listBean.service_life_end_time);
            TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);//检满价格
            modelmy_mycoupon1_couponrequire.setText("满" + price[1] + "元可用");
            TextView modelmy_mycoupon1_areaofapplication = view.findViewById(R.id.modelmy_mycoupon1_areaofapplication);//使用范围
            modelmy_mycoupon1_areaofapplication.setText(listBean.scope);
        }
        if (listBean.preferential_way.equals("折扣")){
            //添加打折卷
            TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mycoupon1_couponrequire.getLayoutParams();
            ll.height = 0;
            modelmy_mycoupon1_couponrequire.setLayoutParams(ll);
            TextView modelmy_mycoupon1_couponpriceicon = view.findViewById(R.id.modelmy_mycoupon1_couponpriceicon);
            ll = (LinearLayout.LayoutParams) modelmy_mycoupon1_couponpriceicon.getLayoutParams();
            ll.width = 0;
            modelmy_mycoupon1_couponpriceicon.setLayoutParams(ll);
            TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
            modelmy_mycoupon1_couponprice.setText(listBean.dc_denomination + "折");
            TextView modelmy_mycoupon1_couponfullreduction = view.findViewById(R.id.modelmy_mycoupon1_couponfullreduction);
            modelmy_mycoupon1_couponfullreduction.setText("打折卷");
            TextView modelmy_mycoupon1_termofvaliditydata = view.findViewById(R.id.modelmy_mycoupon1_termofvaliditydata);
            modelmy_mycoupon1_termofvaliditydata.setText(listBean.service_life_end_time);
            TextView modelmy_mycoupon1_areaofapplication = view.findViewById(R.id.modelmy_mycoupon1_areaofapplication);
            modelmy_mycoupon1_areaofapplication.setText(listBean.scope);
        }
        if (listBean.preferential_way.equals("抵现")){
            //抵现卷
            TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mycoupon1_couponrequire.getLayoutParams();
            ll.height = 0;
            modelmy_mycoupon1_couponrequire.setLayoutParams(ll);
            TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
            modelmy_mycoupon1_couponprice.setText(listBean.dc_denomination);
            TextView modelmy_mycoupon1_couponfullreduction = view.findViewById(R.id.modelmy_mycoupon1_couponfullreduction);
            modelmy_mycoupon1_couponfullreduction.setText("抵现卷");
            TextView modelmy_mycoupon1_termofvaliditydata = view.findViewById(R.id.modelmy_mycoupon1_termofvaliditydata);
            modelmy_mycoupon1_termofvaliditydata.setText(listBean.service_life_end_time);
            TextView modelmy_mycoupon1_areaofapplication = view.findViewById(R.id.modelmy_mycoupon1_areaofapplication);
            modelmy_mycoupon1_areaofapplication.setText(listBean.scope);
        }
        //未使用列表
//        if (mMyCouponCurrentTab.equals("notused")) {
//
//        } else
            if (mMyCouponCurrentTab.equals("alreadyused")) {
            //已经使用
            {
                //添加满减卷
                //设置背景 字体大小  字体颜色
                TextView modelmy_mycoupon1_couponpriceicon = view.findViewById(R.id.modelmy_mycoupon1_couponpriceicon);
                modelmy_mycoupon1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
                modelmy_mycoupon1_couponprice.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
                modelmy_mycoupon1_couponrequire.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                ImageView modelmy_mycoupon1_couponstate = view.findViewById(R.id.modelmy_mycoupon1_couponstate);
                modelmy_mycoupon1_couponstate.setVisibility(View.VISIBLE);
                modelmy_mycoupon1_couponstate.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_icon_alreadyused));
            }
        } else if (mMyCouponCurrentTab.equals("expired")) {
            //已经过期列表    网络请求获取相应的数据
            {
                //添加满减卷
                TextView modelmy_mycoupon1_couponpriceicon = view.findViewById(R.id.modelmy_mycoupon1_couponpriceicon);
                modelmy_mycoupon1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
                modelmy_mycoupon1_couponprice.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
                modelmy_mycoupon1_couponrequire.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                ImageView modelmy_mycoupon1_couponstate = view.findViewById(R.id.modelmy_mycoupon1_couponstate);
                modelmy_mycoupon1_couponstate.setVisibility(View.VISIBLE);
                modelmy_mycoupon1_couponstate.setBackground(view.getResources().getDrawable(R.drawable.mycoupon_icon_outofdata));
            }
        }
    }

    //展示我的消息界面
    public void MyMessageShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyMessageView == null) {
            mMyMessageView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mymessage, null);
            //Smart_model_my_mymessage   我的消息刷新控件
            mSmart_model_my_mymessage = mMyMessageView.findViewById(R.id.Smart_model_my_mymessage);
            mSmart_model_my_mymessage.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyMessageSum <= mMyMessageCurrentPage * mMyMessagePageCount){
                        LinearLayout mymessage_end = mMyMessageView.findViewById(R.id.mymessage_end);
                        mymessage_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getModelMyMessageListMore(mMyMessageType);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    if (list != null){
                        list.clear();
                    }
                    adapter.notifyDataSetChanged();
                    getModelMyMessageList(mMyMessageType);
                }
            });

            ImageView modelmy_mymessage_clear = mMyMessageView.findViewById(R.id.modelmy_mymessage_clear);
            //点击全部已读
            modelmy_mymessage_clear.setOnClickListener(v -> {
               //modelmy_mymessage_noticemessagecount
                TextView modelmy_mymessage_noticemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_noticemessagecount);
                TextView modelmy_mymessage_advertisemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertisemessagecount);
                if (modelmy_mymessage_noticemessagecount.getVisibility() == View.VISIBLE || modelmy_mymessage_advertisemessagecount.getVisibility() == View.VISIBLE) {  //有未读消息
                    m_isFind = true;
                } else {
                    ControllerListViewForScrollView listView = mMyMessageView0.findViewById(R.id.modelmy_mymessage_main_contentlistview);
                     //获取到相应的子条目的消息数量
                    int childCount = listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View view = listView.getChildAt(i);
                        if (view == null) {
                            continue;
                        }
                        TextView modelmy_mymessage1_messagecount = view.findViewById(R.id.modelmy_mymessage1_messagecount);
                        if (modelmy_mymessage1_messagecount == null) {
                            continue;
                        }
                        if (modelmy_mymessage1_messagecount.getVisibility() == View.VISIBLE) {  //有未读消息
                            m_isFind = true;
                            break;
                        }
                    }
                }
                //如果没有未读消息弹出toast提示
                if (!m_isFind) {
                    Toast.makeText(mControlMainActivity, "暂无未读消息", Toast.LENGTH_LONG).show();
                } else {
                    //弹出提示，是否将未读消息置为全部已读
                    View dialogView = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_notip_sure_cancel, null);
                    mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, dialogView, R.style.DialogTheme);
                    mMyDialog.setCancelable(true);
                    mMyDialog.show();
                    TextView dialog_content = dialogView.findViewById(R.id.dialog_content);
                    dialog_content.setText("是否将未读消息置为全部已读？");
                    TextView button_cancel = dialogView.findViewById(R.id.button_cancel);
                    button_cancel.setText("取消");
                    button_cancel.setOnClickListener(View -> {
                        mMyDialog.cancel();
                    });
                    TextView button_sure = dialogView.findViewById(R.id.button_sure);
                    button_sure.setText("确定");
                    button_sure.setOnClickListener(View -> {
                        //全部已读
//                        modelmy_mymessage_noticemessagecount.setVisibility(android.view.View.INVISIBLE);
//                        modelmy_mymessage_advertisemessagecount.setVisibility(android.view.View.INVISIBLE);
//                        ControllerListViewForScrollView listView = mMyMessageView0.findViewById(R.id.modelmy_mymessage_main_contentlistview);
//                        int childCount = listView.getChildCount();
//                        for (int i = 0; i < childCount; i++) {
//                            View view = listView.getChildAt(i);
//                            if (view == null) {
//                                continue;
//                            }
//                            TextView modelmy_mymessage1_messagecount = view.findViewById(R.id.modelmy_mymessage1_messagecount);
//                            if (modelmy_mymessage1_messagecount == null) {
//                                continue;
//                            }
//                            modelmy_mymessage1_messagecount.setVisibility(android.view.View.INVISIBLE);
//
//                        }
//                        String modelmy_mymessage1_Id = "";
//                        for (int i = 0; i < list.size(); i ++){
//                            ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo = list.get(i);
//                            if (myMessageInfo == null){
//                                continue;
//                            }
//                            modelmy_mymessage1_Id = modelmy_mymessage1_Id + "\"" + myMessageInfo.modelmy_mymessage1_Id + "\",";
//                        }
//                        if (!modelmy_mymessage1_Id.equals("") && modelmy_mymessage1_Id.length() >= 1){
//                            modelmy_mymessage1_Id = modelmy_mymessage1_Id.substring(0,modelmy_mymessage1_Id.length() - 1);
//                        }
//                        ReadMyNews(modelmy_mymessage1_Id);
                        ReadMyNewsAll();
                        mMyDialog.cancel();
                    });
                }
            });
        }
        //点击清空消息
        ImageView modelmy_mymessage_clear = mMyMessageView.findViewById(R.id.modelmy_mymessage_clear);
        modelmy_mymessage_clear.setVisibility(View.VISIBLE);
        my_layout_main.addView(mMyMessageView);
        //我的消息列表
        LinearLayout modelmy_mymessage_main_content = mMyMessageView.findViewById(R.id.modelmy_mymessage_main_content);
        modelmy_mymessage_main_content.removeAllViews();
        if (mMyMessageView0 == null) {
            mMyMessageView0 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mymessage0, null);
        }
        //系统通知
        RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
        modelmy_mymessage_notice.setOnClickListener(v -> {
             //系统的列表
            mMyMessageType = 1;
            getModelMyMessageList(1);
        });
        //广告
        RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
        modelmy_mymessage_advertise.setOnClickListener(v -> {
            //广告的列表
            mMyMessageType = 2;
            getModelMyMessageList(2);
        });
        modelmy_mymessage_main_content.addView(mMyMessageView0);
        mMyMessageType = 3;
        //加网络请求放到网络请求中赋值 刷新适配器
        getModelMyMessageList(3);
    }

    //展示我的问答界面
    public void MyAnswerShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyAnswerView == null) {
            mMyAnswerView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myanswer, null);
            //我的问答刷新控件
            mSmart_model_my_myanswer = mMyAnswerView.findViewById(R.id.Smart_model_my_myanswer);
            mSmart_model_my_myanswer.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyQuestionAndAnswerSum <= mMyQuestionAndAnswerCurrentPage * mMyQuestionAndAnswerPageCount){
                        LinearLayout myanswer_end = mMyAnswerView.findViewById(R.id.myanswer_end);
                        myanswer_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (mMyAnswerCurrentTab.equals("question")) {
                        getModelMyQuestionListMore();
                    } else {
                        getModelMyAnswerListMore();
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    if (mMyAnswerCurrentTab.equals("question")) {
                        getModelMyQuestionList();
                    } else {
                        getModelMyAnswerList();
                    }
                }
            });
            TextView modelmy_myanswer_tab_question = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_question);
            modelmy_myanswer_tab_question.setOnClickListener(v -> {
                if (!mMyAnswerCurrentTab.equals("question")) {
                    ImageView modelmy_myanswer_cursor1 = mMyAnswerView.findViewById(R.id.modelmy_myanswer_cursor1);
                    Animation animation = new TranslateAnimation((mMyAnswerLastTabIndex - 1) * width / 2, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_myanswer_cursor1.startAnimation(animation);
                    TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
                    modelmy_myanswer_tab_question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_myanswer_tab_answer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyAnswerLastTabIndex = 1;
                mMyAnswerCurrentTab = "question";
                getModelMyQuestionList();
            });
            TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
            modelmy_myanswer_tab_answer.setOnClickListener(v -> {
                if (!mMyAnswerCurrentTab.equals("answer")) {
                    ImageView modelmy_myanswer_cursor1 = mMyAnswerView.findViewById(R.id.modelmy_myanswer_cursor1);
                    Animation animation = new TranslateAnimation((mMyAnswerLastTabIndex - 1) * width / 2, width / 2, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    modelmy_myanswer_cursor1.startAnimation(animation);
                    modelmy_myanswer_tab_question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myanswer_tab_answer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyAnswerLastTabIndex = 2;
                mMyAnswerCurrentTab = "answer";
                LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
                modelmy_myanswer_main_content.removeAllViews();
                getModelMyAnswerList();
               // initMyAnswer();
            });
        }
        my_layout_main.addView(mMyAnswerView);
        ImageView modelmy_myanswer_cursor1 = mMyAnswerView.findViewById(R.id.modelmy_myanswer_cursor1);
        int x = width / 4 - mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_myanswer_cursor1.setX(x);
        //默认选中的为我的提问
        mMyAnswerLastTabIndex = 1;
        mMyAnswerCurrentTab = "question";
        TextView modelmy_myanswer_tab_question = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_question);
        TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
        modelmy_myanswer_tab_question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        modelmy_myanswer_tab_answer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyAnswerView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        getModelMyQuestionList();
    }

     //我的问答详情
    private void MyAnswerShow_Details(Integer questions_id,Integer course_type) {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyAnswerDetailsView == null) {
            mMyAnswerDetailsView = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_my_myanswerdetails, null);
            //Smart_model_my_myanswerdetails  我的问答详情
            mSmart_model_my_myanswerdetails = mMyAnswerDetailsView.findViewById(R.id.Smart_model_my_myanswerdetails);
            mSmart_model_my_myanswerdetails.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyQuestionAndAnswerDetailsSum <= mMyQuestionAndAnswerDetailsCurrentPage * mMyQuestionAndAnswerDetailsPageCount){
                        LinearLayout myanswerdetails_end = mMyAnswerDetailsView.findViewById(R.id.myanswerdetails_end);
                        myanswerdetails_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getQueryOneQuestionMore(mAnswerDetailsQuestionId,course_type);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //控件的刷新方法
                    getQueryOneQuestion(mAnswerDetailsQuestionId,course_type);
                }
            });
            //点击删除本条问答
            ImageView modelmy_myanswerdetails_delete = mMyAnswerDetailsView.findViewById(R.id.modelmy_myanswerdetails_delete);
            modelmy_myanswerdetails_delete.setOnClickListener(v -> {
                //点击弹出删除问答提示框
                View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
                mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view, R.style.DialogTheme);
                mMyDialog.setCancelable(true);
                mMyDialog.show();
                TextView tip = view.findViewById(R.id.tip);
                tip.setText("删除问答");
                TextView dialog_content = view.findViewById(R.id.dialog_content);
                dialog_content.setText("确定删除此条问答吗？");
                TextView button_cancel = view.findViewById(R.id.button_cancel);
                button_cancel.setText("取消");
                button_cancel.setOnClickListener(View -> {
                    mMyDialog.cancel();
                });
                TextView button_sure = view.findViewById(R.id.button_sure);
                button_sure.setText("确定");
                button_sure.setOnClickListener(View -> {
                    //关闭本页面 将当前的数据id重集合中删除   刷新页面
                    if (mMyAnswerCurrentTab.equals("question")) {
                        if (mAnswerDetailsQuestionId != null) {
                            DeleteMyQuestion(mAnswerDetailsQuestionId, 1);
                        }
                    } else {
                        if (mAnswerDetailsAnswerId != null) {
                            DeleteMyQuestion(mAnswerDetailsAnswerId, 1);
                        }
                    }
                });
            });
        }
        my_layout_main.addView(mMyAnswerDetailsView);
        LinearLayout modelmy_myanswerdetails_main_content = mMyAnswerDetailsView.findViewById(R.id.modelmy_myanswerdetails_main_content);
        modelmy_myanswerdetails_main_content.removeAllViews();
        mAnswerDetailsView = mControlMainActivity.getLayoutInflater().inflate(R.layout.modelanswerdetails, null);
        modelmy_myanswerdetails_main_content.addView(mAnswerDetailsView);
        getQueryOneQuestion(questions_id,course_type);
        mControlMainActivity.Page_AnswerDetails();
    }

    // 查询个人信息详情（我的界面）
    public void getPersonalInfoDatas() {
        if (mControlMainActivity.mStuId.equals("")){
            mPersonalInfoDataBean = null;
            //重置我的界面
            ModelMyInit();
//            Toast.makeText(mControlMainActivity, "获取个人信息失败", Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<PersonalInfoBean> call = modelObservableInterface.queryModelMyPersonInfo(body);
        call.enqueue(new Callback<PersonalInfoBean>() {
            @Override
            public void onResponse(Call<PersonalInfoBean> call, Response<PersonalInfoBean> response) {
                PersonalInfoBean personalInfoBean = response.body();
                if (personalInfoBean == null) {
                    Toast.makeText(mControlMainActivity, "获取个人信息失败", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(personalInfoBean.code,personalInfoBean.msg)){
                    mPersonalInfoDataBean = null;
                    //重置我的界面
                    ModelMyInit();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //网络请求数据成功
                mPersonalInfoDataBean = personalInfoBean.getData();
                //重置我的界面
                ModelMyInit();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<PersonalInfoBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity, "获取个人信息失败", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    @Override
    public void onRecive() {

    }

    //个人信息详情（我的界面）
    public static class PersonalInfoBean {
        private PersonalInfoDataBean data;
        private int code;
        private String msg;

        public PersonalInfoDataBean getData() {
            return data;
        }

        public void setData(PersonalInfoDataBean data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return msg;
        }

        public void setErrorMsg(String msg) {
            this.msg = msg;
        }

        public static class PersonalInfoDataBean {
            private String autograph;       //个人签名
            private String nickname;        //用户昵称
            private String stu_name;            //姓名
            private String head;          //用户头像
            private String tel;           //手机号码
            private String login_number;    //账号
            private String ID_number;       //身份证号码
            private String email;           //邮箱
        }
    }

    //我的课程列表
    public void getMyCourseList() {
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myclass != null){
                mSmart_model_my_myclass.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCourseCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        paramsMap.put("pageNum", mMyCourseCurrentPage);
        paramsMap.put("pageSize",mMyCoursePageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<QueryMyCourseListBean> call = modelObservableInterface.QueryMyCourseList(body);
        call.enqueue(new Callback<QueryMyCourseListBean>() {
            @Override
            public void onResponse(Call<QueryMyCourseListBean> call, Response<QueryMyCourseListBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                QueryMyCourseListBean queryMyCourseListBean = response.body();
                if (queryMyCourseListBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(queryMyCourseListBean.getCode(),"")){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                QueryMyCourseListBean.DataBean dataBean = queryMyCourseListBean.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<QueryMyCourseListBean.DataBean.ListBean> courseListBeansList = dataBean.getList();
                if (courseListBeansList == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mMyCourseSum = dataBean.getTotal();
                LinearLayout modelmy_myclass_main_content = mMyClassView.findViewById(R.id.modelmy_myclass_main_content);
                modelmy_myclass_main_content.removeAllViews();
                View line = null;
                for (int i = 0; i < courseListBeansList.size(); i++) {
                    QueryMyCourseListBean.DataBean.ListBean courseListBean = courseListBeansList.get(i);
                    if (courseListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclass1, null);
                    //加载网络图片封面
                    ControllerCustomRoundAngleImageView modelmy_myclass1_cover = view.findViewById(R.id.modelmy_myclass1_cover);
                    Glide.with(mControlMainActivity).load(courseListBean.cover).listener(new RequestListener<Drawable>() {
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
                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_myclass1_cover);
                    //课程名称
                    TextView modelmy_myclass1_classname = view.findViewById(R.id.modelmy_myclass1_classname);
                    modelmy_myclass1_classname.setText(courseListBean.course_name);
                    //stuNum
                    TextView modelmy_myclass1_state = view.findViewById(R.id.modelmy_myclass1_state);
                    modelmy_myclass1_state.setText(courseListBean.stuNum + "");
                    //类型
                    ImageView iv_imagetype = view.findViewById(R.id.iv_imagetype);
                    if (courseListBean.course_type.equals("直播,录播")){
                        iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_mix));
                    } else if (courseListBean.course_type.equals("直播")){
                        iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_live));
                    } else if (courseListBean.course_type.equals("录播")){
                        iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_record));
                    }
                    modelmy_myclass_main_content.addView(view);
                    //添加每个课程的监听，点击跳转到课程详情
                    view.setOnClickListener(v -> {
                        CourseInfo courseInfo = new CourseInfo();
                        courseInfo.mCourseId = String.valueOf(courseListBean.course_id);
                        courseInfo.mCourseCover = courseListBean.cover;
                        courseInfo.mCourseType = courseListBean.course_type;
                        courseInfo.mCourseName = courseListBean.course_name;
                        courseInfo.mCoursePriceOld = String.valueOf(courseListBean.price);
                        courseInfo.mCoursePrice = String.valueOf(courseListBean.special_price);
                        courseInfo.mCourseLearnPersonNum = String.valueOf(courseListBean.stuNum);

                        //跳转课程详情
                        ModelCourseCover modelCourseCover = new ModelCourseCover();
                        View modelCourseView = modelCourseCover.ModelCourseCover(mControlMainActivity, courseInfo);
                        modelCourseCover.CourseDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCourseView);
                        mModelCourseCover = modelCourseCover;
                        mControlMainActivity.onClickCourseDetails();
                    });
                    //我的课程协议
                    if (courseListBean.agreement_id != null) {
                        //我的课程协议
                        TextView modelmy_myclass1_agreement = view.findViewById(R.id.modelmy_myclass1_agreement);
                        modelmy_myclass1_agreement.setOnClickListener(v -> { //点击查看协议
                            HideAllLayout();
                            mControlMainActivity.onClickMyAgreement();
                            View myclass_agreement = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_agreement, null);
                            //我的课程网络请求
                            getModelMyMeent(myclass_agreement,courseListBean.agreement_id);
                            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                            my_layout_main.addView(myclass_agreement);
                        });
                    } else {
                        TextView modelmy_myclass1_agreement = view.findViewById(R.id.modelmy_myclass1_agreement);
                        modelmy_myclass1_agreement.setVisibility(View.INVISIBLE);
                    }
                    //view线
                    line = view.findViewById(R.id.modelmy_myclass1_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<QueryMyCourseListBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取我的课程列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //我的课程列表-下拉加载
    public void getMyCourseListMore() {
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myclass != null){
                mSmart_model_my_myclass.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCourseCurrentPage = mMyCourseCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        paramsMap.put("pageNum", mMyCourseCurrentPage);
        paramsMap.put("pageSize",mMyCoursePageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<QueryMyCourseListBean> call = modelObservableInterface.QueryMyCourseList(body);
        call.enqueue(new Callback<QueryMyCourseListBean>() {
            @Override
            public void onResponse(Call<QueryMyCourseListBean> call, Response<QueryMyCourseListBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                QueryMyCourseListBean queryMyCourseListBean = response.body();
                if (queryMyCourseListBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(queryMyCourseListBean.getCode(),"")){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                QueryMyCourseListBean.DataBean dataBean = queryMyCourseListBean.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<QueryMyCourseListBean.DataBean.ListBean> courseListBeansList = dataBean.getList();
                if (courseListBeansList == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mMyCourseSum = dataBean.getTotal();
                LinearLayout modelmy_myclass_main_content = mMyClassView.findViewById(R.id.modelmy_myclass_main_content);
                View line = null;
                for (int i = 0; i < courseListBeansList.size(); i++) {
                    QueryMyCourseListBean.DataBean.ListBean courseListBean = courseListBeansList.get(i);
                    if (courseListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclass1, null);
                    //加载网络图片封面
                    ControllerCustomRoundAngleImageView modelmy_myclass1_cover = view.findViewById(R.id.modelmy_myclass1_cover);
                    Glide.with(mControlMainActivity).load(courseListBean.cover).listener(new RequestListener<Drawable>() {
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
                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_myclass1_cover);
                    //课程名称
                    TextView modelmy_myclass1_classname = view.findViewById(R.id.modelmy_myclass1_classname);
                    modelmy_myclass1_classname.setText(courseListBean.course_name);
                    //stuNum
                    TextView modelmy_myclass1_state = view.findViewById(R.id.modelmy_myclass1_state);
                    modelmy_myclass1_state.setText(courseListBean.stuNum + "");
                    //类型
                    ImageView iv_imagetype = view.findViewById(R.id.iv_imagetype);
                    if (courseListBean.course_type.equals("直播,录播")){
                        iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_mix));
                    } else if (courseListBean.course_type.equals("直播")){
                        iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_live));
                    } else if (courseListBean.course_type.equals("录播")){
                        iv_imagetype.setBackground(view.getResources().getDrawable(R.drawable.button_myclass_record));
                    }
                    modelmy_myclass_main_content.addView(view);
                    //添加每个课程的监听，点击跳转到课程详情
                    view.setOnClickListener(v -> {
                        CourseInfo courseInfo = new CourseInfo();
                        courseInfo.mCourseId = String.valueOf(courseListBean.course_id);
                        courseInfo.mCourseCover = courseListBean.cover;
                        courseInfo.mCourseType = courseListBean.course_type;
                        courseInfo.mCourseName = courseListBean.course_name;
                        courseInfo.mCoursePriceOld = String.valueOf(courseListBean.price);
                        courseInfo.mCoursePrice = String.valueOf(courseListBean.special_price);
                        courseInfo.mCourseLearnPersonNum = String.valueOf(courseListBean.stuNum);

                        //跳转课程详情
                        ModelCourseCover modelCourseCover = new ModelCourseCover();
                        View modelCourseView = modelCourseCover.ModelCourseCover(mControlMainActivity, courseInfo);
                        modelCourseCover.CourseDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCourseView);
                        mModelCourseCover = modelCourseCover;
                        mControlMainActivity.onClickCourseDetails();
                    });
                    if (courseListBean.agreement_id != null) {
                        //我的课程协议
                        TextView modelmy_myclass1_agreement = view.findViewById(R.id.modelmy_myclass1_agreement);
                        modelmy_myclass1_agreement.setOnClickListener(v -> { //点击查看协议
                            HideAllLayout();
                            mControlMainActivity.onClickMyAgreement();
                            View myclass_agreement = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_agreement, null);
                            //我的课程网络请求
                            getModelMyMeent(myclass_agreement,courseListBean.agreement_id);
                            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                            my_layout_main.addView(myclass_agreement);
                        });
                    } else {
                        TextView modelmy_myclass1_agreement = view.findViewById(R.id.modelmy_myclass1_agreement);
                        modelmy_myclass1_agreement.setVisibility(View.INVISIBLE);
                    }
                    //view线
                    line = view.findViewById(R.id.modelmy_myclass1_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<QueryMyCourseListBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取我的课程列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //我的课程包列表请求数据
    public void getMyPacketList() {
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myclasspacket != null){
                mSmart_model_my_myclasspacket.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCoursePacketCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        paramsMap.put("pageNum", mMyCoursePacketCurrentPage);
        paramsMap.put("pageSize",mMyCoursePacketPageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<MyclassPacketList> call = modelObservableInterface.QueryMyCoursePackageList(body);
        call.enqueue(new Callback<MyclassPacketList>() {
            @Override
            public void onResponse(Call<MyclassPacketList> call, Response<MyclassPacketList> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                MyclassPacketList myclassPacketList = response.body();
                if (myclassPacketList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(myclassPacketList.getCode(),"")){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                MyclassPacketList.DataBean dataBean = myclassPacketList.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<MyclassPacketList.DataBean.ListBean> coursePacketListBeansList = dataBean.getList();
                if (coursePacketListBeansList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mMyCoursePacketSum = dataBean.getTotal();
                LinearLayout modelmy_myclasspacket_main_content = mMyClassPacketView.findViewById(R.id.modelmy_myclasspacket_main_content);
                modelmy_myclasspacket_main_content.removeAllViews();
                View line = null;
                for (int i = 0; i < coursePacketListBeansList.size(); i ++) {
                    MyclassPacketList.DataBean.ListBean coursePacketListBean = coursePacketListBeansList.get(i);
                    if (coursePacketListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclasspacket1, null);
                    //加载网络图片封面
                    ControllerCustomRoundAngleImageView modelmy_myclasspacket1_cover = view.findViewById(R.id.modelmy_myclasspacket1_cover);
                    Glide.with(mControlMainActivity).load(coursePacketListBean.cover).listener(new RequestListener<Drawable>() {
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
                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursepacketcover)).into(modelmy_myclasspacket1_cover);
                    //课程包名称
                    TextView modelmy_myclasspacket1_classname = view.findViewById(R.id.modelmy_myclasspacket1_classname);
                    modelmy_myclasspacket1_classname.setText(coursePacketListBean.cp_name);
                    //阶段数量
                    TextView modelmy_myclasspacket1_stagecount = view.findViewById(R.id.modelmy_myclasspacket1_stagecount);
                    modelmy_myclasspacket1_stagecount.setText(coursePacketListBean.stageNum + "");
                    //课程数量
                    TextView modelmy_myclasspacket1_coursecount = view.findViewById(R.id.modelmy_myclasspacket1_coursecount);
                    modelmy_myclasspacket1_coursecount.setText(coursePacketListBean.courseNum + "");
                    //学习人数
                    TextView modelmy_myclasspacket1_learnpersoncount = view.findViewById(R.id.modelmy_myclasspacket1_learnpersoncount);
                    modelmy_myclasspacket1_learnpersoncount.setText(coursePacketListBean.stuNum + "");
                    //学习进度
                    TextView modelmy_myclasspacket1_learnprogresscount = view.findViewById(R.id.modelmy_myclasspacket1_learnprogresscount);
                    modelmy_myclasspacket1_learnprogresscount.setText(coursePacketListBean.rateOfLearning * 100 + "%");
                    modelmy_myclasspacket_main_content.addView(view);
                    //添加每个课程包的监听，点击跳转到课程包详情
                    view.setOnClickListener(v -> {
                        CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                        coursePacketInfo.mCoursePacketId = String.valueOf(coursePacketListBean.course_package_id);//课程包id
                        coursePacketInfo.mCoursePacketCover = coursePacketListBean.cover;
                        coursePacketInfo.mCoursePacketStageNum = String.valueOf(coursePacketListBean.stageNum);
                        coursePacketInfo.mCoursePacketName = coursePacketListBean.cp_name;   //包名
                        coursePacketInfo.mCoursePacketPrice = String.valueOf(coursePacketListBean.favorable_price);//总价格
                        coursePacketInfo.mCoursePacketCourseNum = String.valueOf(coursePacketListBean.courseNum);
                        coursePacketInfo.mCoursePacketPriceOld = String.valueOf(coursePacketListBean.total_price);//数据原来的价格
                        coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(coursePacketListBean.buying_base_number);//购买人数
                        //跳转课程包详情
                        ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                        View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                        modelCoursePacketCover.CoursePacketDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCoursePacketView);
                        mControlMainActivity.onClickCoursePacketDetails();
                    });
                    //我的课程包协议
                    if (coursePacketListBean.agreement_id != null) {
                        //我的课程包协议
                        TextView modelmy_myclasspacket1_agreement = view.findViewById(R.id.modelmy_myclasspacket1_agreement);
                        modelmy_myclasspacket1_agreement.setOnClickListener(v -> { //点击查看协议
                            HideAllLayout();
                            mControlMainActivity.onClickMyAgreement();
                            View myclass_agreement = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_agreement, null);
                            //我的课程包协议请求
                            getModelMyMeent(myclass_agreement,coursePacketListBean.agreement_id);
                            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                            my_layout_main.addView(myclass_agreement);
                        });
                    } else {
                        TextView modelmy_myclasspacket1_agreement = view.findViewById(R.id.modelmy_myclasspacket1_agreement);
                        modelmy_myclasspacket1_agreement.setVisibility(View.INVISIBLE);
                    }
                    //view线
                    line = view.findViewById(R.id.modelmy_myclasspacket1_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<MyclassPacketList> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取我的课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //我的课程包列表请求数据-下拉加载
    public void getMyPacketListMore() {
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myclasspacket != null){
                mSmart_model_my_myclasspacket.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCoursePacketCurrentPage = mMyCoursePacketCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        paramsMap.put("pageNum", mMyCoursePacketCurrentPage);
        paramsMap.put("pageSize",mMyCoursePacketPageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<MyclassPacketList> call = modelObservableInterface.QueryMyCoursePackageList(body);
        call.enqueue(new Callback<MyclassPacketList>() {
            @Override
            public void onResponse(Call<MyclassPacketList> call, Response<MyclassPacketList> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                MyclassPacketList myclassPacketList = response.body();
                if (myclassPacketList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(myclassPacketList.getCode(),"")){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                MyclassPacketList.DataBean dataBean = myclassPacketList.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<MyclassPacketList.DataBean.ListBean> coursePacketListBeansList = dataBean.getList();
                if (coursePacketListBeansList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mMyCoursePacketSum = dataBean.getTotal();
                LinearLayout modelmy_myclasspacket_main_content = mMyClassPacketView.findViewById(R.id.modelmy_myclasspacket_main_content);
                View line = null;
                for (int i = 0; i < coursePacketListBeansList.size(); i ++) {
                    MyclassPacketList.DataBean.ListBean coursePacketListBean = coursePacketListBeansList.get(i);
                    if (coursePacketListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_myclasspacket1, null);
                    //加载网络图片封面
                    ControllerCustomRoundAngleImageView modelmy_myclasspacket1_cover = view.findViewById(R.id.modelmy_myclasspacket1_cover);
                    Glide.with(mControlMainActivity).load(coursePacketListBean.cover).listener(new RequestListener<Drawable>() {
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
                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursepacketcover)).into(modelmy_myclasspacket1_cover);
                    //课程包名称
                    TextView modelmy_myclasspacket1_classname = view.findViewById(R.id.modelmy_myclasspacket1_classname);
                    modelmy_myclasspacket1_classname.setText(coursePacketListBean.cp_name);
                    //阶段数量
                    TextView modelmy_myclasspacket1_stagecount = view.findViewById(R.id.modelmy_myclasspacket1_stagecount);
                    modelmy_myclasspacket1_stagecount.setText(coursePacketListBean.stageNum + "");
                    //课程数量
                    TextView modelmy_myclasspacket1_coursecount = view.findViewById(R.id.modelmy_myclasspacket1_coursecount);
                    modelmy_myclasspacket1_coursecount.setText(coursePacketListBean.courseNum + "");
                    //学习人数
                    TextView modelmy_myclasspacket1_learnpersoncount = view.findViewById(R.id.modelmy_myclasspacket1_learnpersoncount);
                    modelmy_myclasspacket1_learnpersoncount.setText(coursePacketListBean.stuNum + "");
                    //学习进度
                    TextView modelmy_myclasspacket1_learnprogresscount = view.findViewById(R.id.modelmy_myclasspacket1_learnprogresscount);
                    if (coursePacketListBean.rateOfLearning == null){
                        modelmy_myclasspacket1_learnprogresscount.setText(0 + "%");
                    } else {
                        modelmy_myclasspacket1_learnprogresscount.setText(coursePacketListBean.rateOfLearning * 100 + "%");
                    }
                    modelmy_myclasspacket_main_content.addView(view);
                    //添加每个课程包的监听，点击跳转到课程包详情
                    view.setOnClickListener(v -> {
                        CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                        coursePacketInfo.mCoursePacketId = String.valueOf(coursePacketListBean.course_package_id);//课程包id
                        coursePacketInfo.mCoursePacketCover = coursePacketListBean.cover;
                        coursePacketInfo.mCoursePacketStageNum = String.valueOf(coursePacketListBean.stageNum);
                        coursePacketInfo.mCoursePacketName = coursePacketListBean.cp_name;   //包名
                        coursePacketInfo.mCoursePacketPrice = String.valueOf(coursePacketListBean.favorable_price);//总价格
                        coursePacketInfo.mCoursePacketCourseNum = String.valueOf(coursePacketListBean.courseNum);
                        coursePacketInfo.mCoursePacketPriceOld = String.valueOf(coursePacketListBean.total_price);//数据原来的价格
                        coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(coursePacketListBean.buying_base_number);//购买人数
                        //跳转课程包详情
                        ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                        View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                        modelCoursePacketCover.CoursePacketDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCoursePacketView);
                        mControlMainActivity.onClickCoursePacketDetails();
                    });
                    //我的课程包协议
                    if (coursePacketListBean.agreement_id != null) {
                        //我的课程包协议
                        TextView modelmy_myclasspacket1_agreement = view.findViewById(R.id.modelmy_myclasspacket1_agreement);
                        modelmy_myclasspacket1_agreement.setOnClickListener(v -> { //点击查看协议
                            HideAllLayout();
                            mControlMainActivity.onClickMyAgreement();
                            View myclass_agreement = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_agreement, null);
                            //我的课程包协议请求
                            getModelMyMeent(myclass_agreement,coursePacketListBean.agreement_id);
                            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                            my_layout_main.addView(myclass_agreement);
                        });
                    } else {
                        TextView modelmy_myclasspacket1_agreement = view.findViewById(R.id.modelmy_myclasspacket1_agreement);
                        modelmy_myclasspacket1_agreement.setVisibility(View.INVISIBLE);
                    }
                    //view线
                    line = view.findViewById(R.id.modelmy_myclasspacket1_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<MyclassPacketList> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取我的课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //我的收藏列表（课程包）
    public void getModelMyClassPacketCollection(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        modelmy_mycollect_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectPacketCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectPacketCurrentPage);//第几页
        paramsMap.put("pageSize",mMyCollectPacketPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "课程包");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        modelObservableInterface.queryMyCollectionPacketList(body)
                .enqueue(new Callback<QueryMyCollectionPacketListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionPacketListBean> call, Response<QueryMyCollectionPacketListBean> response) {
                        QueryMyCollectionPacketListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(listBean.getCode(),"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (code == 200){
                            QueryMyCollectionPacketListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectPacketSum = data.getTotal();
                                List<QueryMyCollectionPacketListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    View modelmy_myclasspacket1_line1 = null;
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionPacketListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishRefresh();
                                            }
                                            continue;
                                        }
                                        //可循环调用以下方法，添加多个课程包
                                        modelmy_myclasspacket1_line1 = MyCollectShow_MyCoursePacket(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (modelmy_myclasspacket1_line1 != null){
                                        modelmy_myclasspacket1_line1.setVisibility(View.INVISIBLE);
                                    }
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishRefresh();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionPacketListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishRefresh();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"错误是" );
                        Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的收藏列表（课程包）-下拉加载
    public void getModelMyClassPacketCollectionMore(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectPacketCurrentPage = mMyCollectPacketCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectPacketCurrentPage);//第几页
        paramsMap.put("pageSize",mMyCollectPacketPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "课程包");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        modelObservableInterface.queryMyCollectionPacketList(body)
                .enqueue(new Callback<QueryMyCollectionPacketListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionPacketListBean> call, Response<QueryMyCollectionPacketListBean> response) {
                        QueryMyCollectionPacketListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(listBean.getCode(),"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (code == 200){
                            QueryMyCollectionPacketListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectPacketSum = data.getTotal();
                                List<QueryMyCollectionPacketListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    View modelmy_myclasspacket1_line1 = null;
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionPacketListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishLoadMore();
                                            }
                                            continue;
                                        }
                                        //可循环调用以下方法，添加多个课程包
                                        modelmy_myclasspacket1_line1 = MyCollectShow_MyCoursePacket(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (modelmy_myclasspacket1_line1 != null){
                                        modelmy_myclasspacket1_line1.setVisibility(View.INVISIBLE);
                                    }
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishLoadMore();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionPacketListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishLoadMore();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"错误是" );
                        Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的收藏列表（课程）
    public void getModelMyClassCollection(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        modelmy_mycollect_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectCurrentPage);//第几页
        paramsMap.put("pageSize",mMyCollectPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "课程");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<QueryMyCollectionListBean> call = modelObservableInterface.queryMyCollectionList(body);
        call.enqueue(new Callback<QueryMyCollectionListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionListBean> call, Response<QueryMyCollectionListBean> response) {
                        QueryMyCollectionListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code == 200){
                            QueryMyCollectionListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectSum = data.getTotal();
                                List<QueryMyCollectionListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    View modelmy_myclass1_line1 = null;
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishRefresh();
                                            }
                                            continue;
                                        }
                                        modelmy_myclass1_line1 = MyCollectShow_MyCourse(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (modelmy_myclass1_line1 != null){
                                        modelmy_myclass1_line1.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishRefresh();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishRefresh();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"错误是" );
                        Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的收藏列表（课程）-下拉加载
    public void getModelMyClassCollectionMore(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectCurrentPage = mMyCollectCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectCurrentPage);//第几页
        paramsMap.put("pageSize",mMyCollectPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "课程");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        modelObservableInterface.queryMyCollectionList(body)
                .enqueue(new Callback<QueryMyCollectionListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionListBean> call, Response<QueryMyCollectionListBean> response) {
                        QueryMyCollectionListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code == 200){
                            QueryMyCollectionListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectSum = data.getTotal();
                                List<QueryMyCollectionListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    View modelmy_myclass1_line1 = null;
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishLoadMore();
                                            }
                                            continue;
                                        }
                                        modelmy_myclass1_line1 = MyCollectShow_MyCourse(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (modelmy_myclass1_line1 != null){
                                        modelmy_myclass1_line1.setVisibility(View.INVISIBLE);
                                    }
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishLoadMore();
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                return;
                            }
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishLoadMore();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"错误是" );
                        Toast.makeText(mControlMainActivity,"获取我的收藏数据失败",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的消息列表--------删除
    public void getModelMyMessageListDelect(ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo){
        if (myMessageInfo == null){
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String content = "{\"student_information_id\": [\"" + myMessageInfo.modelmy_mymessage1_Id + "\"],\"tf_delete\":1}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), content);
       //queryMyMessageDelectList
        queryMyCourseList.queryMyMessageDelectList(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mControlMainActivity,"删除失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mControlMainActivity,"删除失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
//                        adapter.DeleteItem(myMessageInfo);
                        getModelMyMessageList(mMyMessageType);
                        Toast.makeText(mControlMainActivity,"删除成功",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        Toast.makeText(mControlMainActivity,"删除失败",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //我的消息列表
    public void getModelMyMessageList(Integer info_type){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mymessage != null){
                mSmart_model_my_mymessage.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        if (list != null){
            list.clear();
        }
        ControllerListViewForScrollView listView = mMyMessageView0.findViewById(R.id.modelmy_mymessage_main_contentlistview);
        adapter = new ControllerMyMessage1Adapter(new ControllerMyMessage1Adapter.ControllerMyMessage1AdapterInterface() {
            @Override
            public void deleteItem(ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo) {
                //我的消息删除
                getModelMyMessageListDelect(myMessageInfo);
            }

            @Override
            public void clickItem(ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo) {
                //我的消息点击
                LinearLayout modelmy_mymessage_main_content = mMyMessageView.findViewById(R.id.modelmy_mymessage_main_content);
                modelmy_mymessage_main_content.removeAllViews();
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_my_mymessage2, null);
                ControllerCustomRoundAngleImageView modelmy_mymessage2_cover = view.findViewById(R.id.modelmy_mymessage2_cover);
                if (info_type == 3) {
                    Glide.with(mControlMainActivity).load(myMessageInfo.modelmy_mymessage1_coverurl).listener(new RequestListener<Drawable>() {
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
                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(modelmy_mymessage2_cover);
                } else if (info_type == 2) {
                    Glide.with(mControlMainActivity).load(myMessageInfo.modelmy_mymessage1_coverurl).listener(new RequestListener<Drawable>() {
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
                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.img_mymessage_advertisement)).into(modelmy_mymessage2_cover);
                }
                TextView modelmy_mymessage2_time = view.findViewById(R.id.modelmy_mymessage2_time);
                modelmy_mymessage2_time.setText(myMessageInfo.modelmy_mymessage1_time);
                TextView modelmy_mymessage2_message = view.findViewById(R.id.modelmy_mymessage2_message);
                modelmy_mymessage2_message.setText(myMessageInfo.modelmy_mymessage1_message);
                modelmy_mymessage_main_content.addView(view);
                //修改服务器的已读状态
                ReadMyNews("\"" + myMessageInfo.modelmy_mymessage1_Id + "\"");
            }
        }, mControlMainActivity, list);
        listView.setAdapter(adapter);
        LinearLayout mymessage_end = mMyMessageView.findViewById(R.id.mymessage_end);
        mymessage_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyMessageCurrentPage = 1;
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyMessageCurrentPage);//第几页
        paramsMap.put("pageSize",mMyMessagePageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        paramsMap.put("info_type",info_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyMessageList(body)
               .enqueue(new Callback<MymessageBean>() {
                   @Override
                   public void onResponse(Call<MymessageBean> call, Response<MymessageBean> response) {
                       MymessageBean body = response.body();
                       if (body == null) {
                           if (mSmart_model_my_mymessage != null){
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       int code = body.getCode();
                       if (!HeaderInterceptor.IsErrorCode(code,"")){
                           if (mSmart_model_my_mycollect != null){
                               mSmart_model_my_mycollect.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       if (code != 200){
                           if (mSmart_model_my_mymessage != null){
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       MymessageBean.DataBean dataBean = body.getData();
                       if (dataBean == null){
                           if (mSmart_model_my_mymessage != null){
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       if (info_type != 3) {
                           RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                           modelmy_mymessage_notice.setVisibility(View.INVISIBLE);
                           LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                           ll.height = 0;
                           modelmy_mymessage_notice.setLayoutParams(ll);
                           RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                           modelmy_mymessage_advertise.setVisibility(View.INVISIBLE);
                           ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                           ll.height = 0;
                           modelmy_mymessage_advertise.setLayoutParams(ll);
                       } else {
                           RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                           modelmy_mymessage_notice.setVisibility(View.VISIBLE);
                           LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                           ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                           modelmy_mymessage_notice.setLayoutParams(ll);
                           RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                           modelmy_mymessage_advertise.setVisibility(View.VISIBLE);
                           ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                           ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                           modelmy_mymessage_advertise.setLayoutParams(ll);
                           if (body.AD_sum != null) {
                               TextView modelmy_mymessage_advertisemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertisemessagecount);
                               if (body.AD_sum > 0) {
                                   modelmy_mymessage_advertisemessagecount.setVisibility(View.VISIBLE);
                                   modelmy_mymessage_advertisemessagecount.setText(body.AD_sum + "");
                               }
                           }
                           if (body.Sys_num != null) {
                               TextView modelmy_mymessage_noticemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_noticemessagecount);
                               if (body.Sys_num > 0) {
                                   modelmy_mymessage_noticemessagecount.setVisibility(View.VISIBLE);
                                   modelmy_mymessage_noticemessagecount.setText(body.Sys_num + "");
                               }
                           }
                       }
                       mMyMessageSum = dataBean.getTotal();
                       List<MymessageBean.DataBean.ListBean> listBeans = dataBean.getList();
                       if (listBeans == null) {
                           if (mSmart_model_my_mymessage != null) {
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       //子条目回复问题列表
                       for (int i = 0; i < listBeans.size(); i++) {
                           MymessageBean.DataBean.ListBean listBean = listBeans.get(i);
                           if (listBean == null) {
                               continue;
                           }
                           ControllerMyMessage1Adapter.MyMessageInfo message = new ControllerMyMessage1Adapter.MyMessageInfo();
                           message.modelmy_mymessage1_name = listBean.title;  //我的消息列表标题
                           if (listBean.tf_read == 2) {
                               message.modelmy_mymessage1_messagecount = "1";     //我的消息未读数量
                           } else {
                               message.modelmy_mymessage1_messagecount = "-1";     //我的消息未读数量
                           }
                           message.modelmy_mymessage1_message = listBean.content;//我的消息message
                           message.modelmy_mymessage1_Id = listBean.student_information_id;
                           Date date = null;
                           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                           try {
                               date = df.parse(listBean.time);
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
                                   SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                   listBean.time = df2.format(date1).toString();
                               }
                           }
                           message.modelmy_mymessage1_time = listBean.time;//我的消息时间
                           list.add(message);
                       }
                       adapter.notifyDataSetChanged();
                       if (mSmart_model_my_mymessage != null){
                           mSmart_model_my_mymessage.finishRefresh();
                       }
                       LoadingDialog.getInstance(mControlMainActivity).dismiss();
                   }

                   @Override
                   public void onFailure(Call<MymessageBean> call, Throwable t) {
                   //网络请求失败
                       if (mSmart_model_my_mymessage != null){
                           mSmart_model_my_mymessage.finishRefresh();
                       }
                       LoadingDialog.getInstance(mControlMainActivity).dismiss();
                   }
               });
    }

    //我的消息列表
    public void getModelMyMessageListMore(Integer info_type){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mymessage != null){
                mSmart_model_my_mymessage.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyMessageCurrentPage = mMyMessageCurrentPage + 1;
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyMessageCurrentPage);//第几页
        paramsMap.put("pageSize",mMyMessagePageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        paramsMap.put("info_type",info_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyMessageList(body)
                .enqueue(new Callback<MymessageBean>() {
                    @Override
                    public void onResponse(Call<MymessageBean> call, Response<MymessageBean> response) {
                        MymessageBean body = response.body();
                        if (body == null) {
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = body.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MymessageBean.DataBean dataBean = body.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyMessageSum = dataBean.getTotal();
                        List<MymessageBean.DataBean.ListBean> listBeans = dataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (info_type != 3) {
                            RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                            modelmy_mymessage_notice.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                            ll.height = 0;
                            modelmy_mymessage_notice.setLayoutParams(ll);
                            RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                            modelmy_mymessage_advertise.setVisibility(View.INVISIBLE);
                            ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                            ll.height = 0;
                            modelmy_mymessage_advertise.setLayoutParams(ll);
                        } else {
                            RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                            modelmy_mymessage_notice.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                            ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            modelmy_mymessage_notice.setLayoutParams(ll);
                            RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                            modelmy_mymessage_advertise.setVisibility(View.VISIBLE);
                            ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                            ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            modelmy_mymessage_advertise.setLayoutParams(ll);
                            if (body.AD_sum != null) {
                                TextView modelmy_mymessage_advertisemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertisemessagecount);
                                if (body.AD_sum > 0) {
                                    modelmy_mymessage_advertisemessagecount.setVisibility(View.VISIBLE);
                                    modelmy_mymessage_advertisemessagecount.setText(body.AD_sum + "");
                                }
                            }
                            if (body.Sys_num != null) {
                                TextView modelmy_mymessage_noticemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_noticemessagecount);
                                if (body.Sys_num > 0) {
                                    modelmy_mymessage_noticemessagecount.setVisibility(View.VISIBLE);
                                    modelmy_mymessage_noticemessagecount.setText(body.Sys_num + "");
                                }
                            }
                        }
                        //子条目回复问题列表
                        for (int i = 0; i < listBeans.size(); i++) {
                            MymessageBean.DataBean.ListBean listBean = listBeans.get(i);
                            if (listBean == null) {
                                continue;
                            }
//                           if (listBean.info_type == )
                            ControllerMyMessage1Adapter.MyMessageInfo message = new ControllerMyMessage1Adapter.MyMessageInfo();
//                           message.modelmy_mymessage1_coverurl = listBean.u;
                            message.modelmy_mymessage1_name = listBean.title;  //我的消息列表标题
                            if (listBean.tf_read == 2) {
                                message.modelmy_mymessage1_messagecount = "1";     //我的消息未读数量
                            } else {
                                message.modelmy_mymessage1_messagecount = "-1";     //我的消息未读数量
                            }
                            message.modelmy_mymessage1_message = listBean.content;//我的消息message
                            message.modelmy_mymessage1_Id = listBean.student_information_id;
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.time);
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
                                    listBean.time = df2.format(date1).toString();
                                }
                            }

                            message.modelmy_mymessage1_time = listBean.time;//我的消息时间
                            list.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        if (mSmart_model_my_mymessage != null){
                            mSmart_model_my_mymessage.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MymessageBean> call, Throwable t) {
                        //网络请求失败
                        if (mSmart_model_my_mymessage != null){
                            mSmart_model_my_mymessage.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的问答列表(回答)---------删除
    public void DeleteMyQuestion(int id,int delete){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("questions_id", id);//删除编号
        paramsMap.put("tf_delete",delete);//是否删除
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.DeleteMyQuestion(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"删除失败",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"删除失败",Toast.LENGTH_LONG).show();
                        } else {
                            mMyDialog.cancel();
                            //点击确定删除本条数据
                            mControlMainActivity.onClickMyAnswerReturn(mMyAnswerView);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        Toast.makeText(mControlMainActivity,"删除失败",Toast.LENGTH_LONG).show();
                    }
                });
    }

    //我的问答的提问列表
    public void getModelMyQuestionList(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myanswer != null) {
                mSmart_model_my_myanswer.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        mAnswerDetailsAnswerId = null;
        mAnswerDetailsQuestionId = null;
        LinearLayout myanswer_end = mMyAnswerView.findViewById(R.id.myanswer_end);
        myanswer_end.setVisibility(View.INVISIBLE);
        LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
        modelmy_myanswer_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerCurrentPage);//第几页
        paramsMap.put("pageSize",mMyQuestionAndAnswerPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyQuestionList(body)
               .enqueue(new Callback<MyQuestionsBean>() {
                   @Override
                   public void onResponse(Call<MyQuestionsBean> call, Response<MyQuestionsBean> response) {
                       MyQuestionsBean myQuestionsBean = response.body();
                       if (myQuestionsBean == null){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       if (!HeaderInterceptor.IsErrorCode(myQuestionsBean.getCode(),"")){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       int code = myQuestionsBean.getCode();
                       if (code != 200 ){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       MyQuestionsBean.DataBean dataBean = myQuestionsBean.getData();
                       if (dataBean == null){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       mMyQuestionAndAnswerSum = dataBean.getTotal();
                       if (myQuestionsBean.getHuida_num() != null){
                           TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
                           modelmy_myanswer_tab_answer.setText("我的回答(" + myQuestionsBean.getHuida_num() + ")");
                       }
                       if (myQuestionsBean.getWen_num() != null){
                           TextView modelmy_myanswer_tab_question = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_question);
                           modelmy_myanswer_tab_question.setText("我的提问(" + myQuestionsBean.getWen_num() + ")");
                       }
                       List<MyQuestionsBean.DataBean.ListBean> listBeans = dataBean.getList();
                       if (listBeans == null){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mControlMainActivity).dismiss();
                           return;
                       }
                       //获取网络数据集合for循环
                       for (int i = 0; i < listBeans.size();i ++){
                           MyQuestionsBean.DataBean.ListBean listBean = listBeans.get(i);
                           if (listBean == null){
                               continue;
                           }
                           View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_my_myanswer1, null);
                           TextView modelmy_myanswer1_title = view.findViewById(R.id.modelmy_myanswer1_title);
                           modelmy_myanswer1_title.setText(listBean.title);
                           TextView modelmy_myanswer1_content = view.findViewById(R.id.modelmy_myanswer1_content);
                           new ModelHtmlUtils(mControlMainActivity, modelmy_myanswer1_content).setHtmlWithPic(listBean.content);
                           TextView modelmy_myanswer1_time = view.findViewById(R.id.modelmy_myanswer1_time);
                           Date date = null;
                           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                           try {
                               date = df.parse(listBean.creation_time);
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
                                   listBean.creation_time = df2.format(date1).toString();
                               }
                           }
                           modelmy_myanswer1_time.setText(listBean.creation_time);
                           modelmy_myanswer_main_content.addView(view);
                           if (listBean.questions_id != null) {
                               view.setOnClickListener(V -> {
                                   if (listBean.course_type == null){
                                       Toast.makeText(mControlMainActivity,"此问答已被删除！",Toast.LENGTH_SHORT).show();
                                       return;
                                   }
                                   mAnswerDetailsQuestionId = listBean.questions_id;
                                   MyAnswerShow_Details(listBean.questions_id,listBean.course_type);
                               });
                           }
                       }
                       if (mSmart_model_my_myanswer != null) {
                           mSmart_model_my_myanswer.finishRefresh();
                       }
                       LoadingDialog.getInstance(mControlMainActivity).dismiss();
                   }

                   @Override
                   public void onFailure(Call<MyQuestionsBean> call, Throwable t) {
                       if (mSmart_model_my_myanswer != null) {
                           mSmart_model_my_myanswer.finishRefresh();
                       }
                       LoadingDialog.getInstance(mControlMainActivity).dismiss();
                   }
               });
    }

    //我的问答的提问列表-下拉加载
    public void getModelMyQuestionListMore(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myanswer != null) {
                mSmart_model_my_myanswer.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        mAnswerDetailsAnswerId = null;
        mAnswerDetailsQuestionId = null;
        LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerCurrentPage = mMyQuestionAndAnswerCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerCurrentPage);//第几页
        paramsMap.put("pageSize",mMyQuestionAndAnswerPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyQuestionList(body)
                .enqueue(new Callback<MyQuestionsBean>() {
                    @Override
                    public void onResponse(Call<MyQuestionsBean> call, Response<MyQuestionsBean> response) {
                        MyQuestionsBean myQuestionsBean = response.body();
                        if (myQuestionsBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myQuestionsBean.getCode(),"")){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myQuestionsBean.getCode();
                        if (code != 200 ){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyQuestionsBean.DataBean dataBean = myQuestionsBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerSum = dataBean.getTotal();
                        if (myQuestionsBean.getHuida_num() != null){
                            TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
                            modelmy_myanswer_tab_answer.setText("我的回答(" + myQuestionsBean.getHuida_num() + ")");
                        }
                        if (myQuestionsBean.getWen_num() != null){
                            TextView modelmy_myanswer_tab_question = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_question);
                            modelmy_myanswer_tab_question.setText("我的提问(" + myQuestionsBean.getWen_num() + ")");
                        }
                        List<MyQuestionsBean.DataBean.ListBean> listBeans = dataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //获取网络数据集合for循环
                        for (int i = 0; i < listBeans.size();i ++){
                            MyQuestionsBean.DataBean.ListBean listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_my_myanswer1, null);
                            TextView modelmy_myanswer1_title = view.findViewById(R.id.modelmy_myanswer1_title);
                            modelmy_myanswer1_title.setText(listBean.title);
                            TextView modelmy_myanswer1_content = view.findViewById(R.id.modelmy_myanswer1_content);
                            new ModelHtmlUtils(mControlMainActivity, modelmy_myanswer1_content).setHtmlWithPic(listBean.content);
                            TextView modelmy_myanswer1_time = view.findViewById(R.id.modelmy_myanswer1_time);
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.creation_time);
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
                                    listBean.creation_time = df2.format(date1).toString();
                                }
                            }
                            modelmy_myanswer1_time.setText(listBean.creation_time);
                            modelmy_myanswer_main_content.addView(view);
                            if (listBean.questions_id != null) {
                                view.setOnClickListener(V -> {
                                    if (listBean.course_type == null){
                                        Toast.makeText(mControlMainActivity,"此问答已被删除！",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAnswerDetailsQuestionId = listBean.questions_id;
                                    MyAnswerShow_Details(listBean.questions_id,listBean.course_type);
                                });
                            }
                        }
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionsBean> call, Throwable t) {
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的问答的回答列表
    public void getModelMyAnswerList(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myanswer != null) {
                mSmart_model_my_myanswer.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        mAnswerDetailsAnswerId = null;
        mAnswerDetailsQuestionId = null;
        LinearLayout myanswer_end = mMyAnswerView.findViewById(R.id.myanswer_end);
        myanswer_end.setVisibility(View.INVISIBLE);
        LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
        modelmy_myanswer_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerCurrentPage);//第几页
        paramsMap.put("pageSize",mMyQuestionAndAnswerPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyAnswerList(body)
                .enqueue(new Callback<MyAnswerBean>() {
                    @Override
                    public void onResponse(Call<MyAnswerBean> call, Response<MyAnswerBean> response) {
                        MyAnswerBean myAnswerBean = response.body();
                        if (myAnswerBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myAnswerBean.getCode(),"")){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myAnswerBean.getCode();
                        if (code != 200){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyAnswerBean.DataBean dataBean = myAnswerBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerSum = dataBean.total;
                        if (myAnswerBean.getHuida_num() != null){
                            TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
                            modelmy_myanswer_tab_answer.setText("我的回答(" + myAnswerBean.getHuida_num() + ")");
                        }
                        if (myAnswerBean.getWen_num() != null){
                            TextView modelmy_myanswer_tab_question = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_question);
                            modelmy_myanswer_tab_question.setText("我的提问(" + myAnswerBean.getWen_num() + ")");
                        }
                        List<MyAnswerBean.DataBean.ListBean> listBeans = dataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < listBeans.size() ; i ++){
                            MyAnswerBean.DataBean.ListBean listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_my_myanswer1, null);
                            TextView modelmy_myanswer1_title = view.findViewById(R.id.modelmy_myanswer1_title);
                            modelmy_myanswer1_title.setText(listBean.title);
                            TextView modelmy_myanswer1_content = view.findViewById(R.id.modelmy_myanswer1_content);
                            new ModelHtmlUtils(mControlMainActivity, modelmy_myanswer1_content).setHtmlWithPic(listBean.content);
                            TextView modelmy_myanswer1_time = view.findViewById(R.id.modelmy_myanswer1_time);
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.creation_time);
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
                                    listBean.creation_time = df2.format(date1).toString();
                                }
                            }
                            modelmy_myanswer1_time.setText(listBean.creation_time);
                            modelmy_myanswer_main_content.addView(view);
                            if (listBean.questions_id != null) {
                                view.setOnClickListener(V -> {
                                    if (listBean.course_type == null){
                                        Toast.makeText(mControlMainActivity,"此问答已被删除！",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAnswerDetailsAnswerId = listBean.huida_questions_id;
                                    mAnswerDetailsQuestionId = listBean.questions_id;
                                    MyAnswerShow_Details(listBean.questions_id,listBean.course_type);
                                });
                            }
                        }
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyAnswerBean> call, Throwable t) {
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //我的问答的回答列表-下拉加载
    public void getModelMyAnswerListMore(){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myanswer != null) {
                mSmart_model_my_myanswer.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        mAnswerDetailsAnswerId = null;
        mAnswerDetailsQuestionId = null;
        LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerCurrentPage = mMyQuestionAndAnswerCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerCurrentPage);//第几页
        paramsMap.put("pageSize",mMyQuestionAndAnswerPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyAnswerList(body)
                .enqueue(new Callback<MyAnswerBean>() {
                    @Override
                    public void onResponse(Call<MyAnswerBean> call, Response<MyAnswerBean> response) {
                        MyAnswerBean myAnswerBean = response.body();
                        if (myAnswerBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myAnswerBean.getCode(),"")){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myAnswerBean.getCode();
                        if (code != 200){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyAnswerBean.DataBean dataBean = myAnswerBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerSum = dataBean.total;
                        if (myAnswerBean.getHuida_num() != null){
                            TextView modelmy_myanswer_tab_answer = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_answer);
                            modelmy_myanswer_tab_answer.setText("我的回答(" + myAnswerBean.getHuida_num() + ")");
                        }
                        if (myAnswerBean.getWen_num() != null){
                            TextView modelmy_myanswer_tab_question = mMyAnswerView.findViewById(R.id.modelmy_myanswer_tab_question);
                            modelmy_myanswer_tab_question.setText("我的提问(" + myAnswerBean.getWen_num() + ")");
                        }
                        List<MyAnswerBean.DataBean.ListBean> listBeans = dataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < listBeans.size() ; i ++){
                            MyAnswerBean.DataBean.ListBean listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_my_myanswer1, null);
                            TextView modelmy_myanswer1_title = view.findViewById(R.id.modelmy_myanswer1_title);
                            modelmy_myanswer1_title.setText(listBean.title);
                            TextView modelmy_myanswer1_content = view.findViewById(R.id.modelmy_myanswer1_content);
                            new ModelHtmlUtils(mControlMainActivity, modelmy_myanswer1_content).setHtmlWithPic(listBean.content);
                            TextView modelmy_myanswer1_time = view.findViewById(R.id.modelmy_myanswer1_time);
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.creation_time);
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
                                    listBean.creation_time = df2.format(date1).toString();
                                }
                            }
                            modelmy_myanswer1_time.setText(listBean.creation_time);
                            modelmy_myanswer_main_content.addView(view);
                            if (listBean.questions_id != null) {
                                view.setOnClickListener(V -> {
                                    if (listBean.course_type == null){
                                        Toast.makeText(mControlMainActivity,"此问答已被删除！",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAnswerDetailsAnswerId = listBean.huida_questions_id;
                                    mAnswerDetailsQuestionId = listBean.questions_id;
                                    MyAnswerShow_Details(listBean.questions_id,listBean.course_type);
                                });
                            }
                        }
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyAnswerBean> call, Throwable t) {
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //我的问答详情(社区问答)
    public void getQueryOneQuestion(Integer questions_id,Integer course_type){
        if (questions_id == null){
            if (mSmart_model_my_myanswerdetails != null) {
                mSmart_model_my_myanswerdetails.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout myanswerdetails_end = mMyAnswerDetailsView.findViewById(R.id.myanswerdetails_end);
        myanswerdetails_end.setVisibility(View.INVISIBLE);
        LinearLayout answerdetails_content = mAnswerDetailsView.findViewById(R.id.answerdetails_content);
        answerdetails_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerDetailsCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerDetailsCurrentPage);//第几页
        paramsMap.put("pageSize",mMyQuestionAndAnswerDetailsPageCount);//每页几条
        paramsMap.put("questions_id", questions_id);//问题id
        paramsMap.put("course_type", course_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<ModelCommunityAnswer.CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Response<ModelCommunityAnswer.CommunityDetilsBean> response) {
                        ModelCommunityAnswer.CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(communityDetilsBean.getCode(),communityDetilsBean.getMsg())){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //添加问题的布局
                        if (communityDetilsDataBean.getState() == 0) { //普通
                            //去掉顶
                            ImageView answerdetails_top = mAnswerDetailsView.findViewById(R.id.answerdetails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) answerdetails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_top.setLayoutParams(ll);
                            //去掉精
                            ImageView answerdetails_fine = mAnswerDetailsView.findViewById(R.id.answerdetails_fine);
                            ll = (LinearLayout.LayoutParams) answerdetails_fine.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_fine.setLayoutParams(ll);
                        } else if (communityDetilsDataBean.getState() == 1) {//加精
                            //去掉顶
                            ImageView answerdetails_top = mAnswerDetailsView.findViewById(R.id.answerdetails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) answerdetails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_top.setLayoutParams(ll);
                        } else if (communityDetilsDataBean.getState() == 2) {//置顶
                            //去掉精
                            ImageView answerdetails_fine = mAnswerDetailsView.findViewById(R.id.answerdetails_fine);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) answerdetails_fine.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_fine.setLayoutParams(ll);
                        }
                        ControllerCustomRoundAngleImageView answerdetails_headportrait = mAnswerDetailsView.findViewById(R.id.answerdetails_headportrait);
                        Glide.with(mControlMainActivity).load(communityDetilsDataBean.getHead()).listener(new RequestListener<Drawable>() {
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
                        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(answerdetails_headportrait);
                        TextView answerdetails_name = mAnswerDetailsView.findViewById(R.id.answerdetails_name);
                        answerdetails_name.setText(communityDetilsDataBean.getNicename());
                        TextView answerdetails_time = mAnswerDetailsView.findViewById(R.id.answerdetails_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
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
                                communityDetilsDataBean.setCreation_time(df2.format(date1).toString());
                            }
                        }
                        answerdetails_time.setText(communityDetilsDataBean.getCreation_time());
                        TextView answerdetails_title = mAnswerDetailsView.findViewById(R.id.answerdetails_title);
                        answerdetails_title.setText(communityDetilsDataBean.getTitle());
                        TextView answerdetails_message = mAnswerDetailsView.findViewById(R.id.answerdetails_message);
                        new ModelHtmlUtils(mControlMainActivity, answerdetails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        List<String> strings = communityDetilsDataBean.getSubject_id();
                        if (strings != null) {
                            for (int i = 0; i < strings.size(); i++) {
                                String string = strings.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0) {
                                    TextView answerdetails_sign1 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign1);
                                    answerdetails_sign1.setText(string);
                                    answerdetails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1) {
                                    TextView answerdetails_sign2 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign2);
                                    answerdetails_sign2.setText(string);
                                    answerdetails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2) {
                                    TextView answerdetails_sign3 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign3);
                                    answerdetails_sign3.setText(string);
                                    answerdetails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        GridLayout answerdetails_imagelayout = mAnswerDetailsView.findViewById(R.id.answerdetails_imagelayout);
                        answerdetails_imagelayout.removeAllViews();
                        //集合.size
                        if (communityDetilsDataBean.getPicture() != null) {
                            String pictures[] = communityDetilsDataBean.getPicture().split(";");
                            if (pictures != null) {
                                for (int num = 0; num < pictures.length; num ++) {
                                    if (pictures[num] == null){
                                        continue;
                                    }
                                    if (pictures[num].equals("")){
                                        continue;
                                    }
                                    View imageView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                    ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                    Glide.with(mControlMainActivity).load(pictures[num]).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                            return false;
                                        }
                                    }).error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                    answerdetails_imagelayout.addView(imageView);
                                }
                            }
                        }
                        ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBean communityDetilsAnswerDataBean = communityDetilsDataBean.getHuida();
                        if (communityDetilsAnswerDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerDetailsSum = communityDetilsAnswerDataBean.getTotal();
                        List<ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList> listBeans = communityDetilsAnswerDataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        View line = null;
                        for (int i = 0; i < listBeans.size() ; i ++){
                            ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.modelanswerdetails_child, null);
                            //学员的头像
                            ControllerCustomRoundAngleImageView answerdetails_child_headportrait = view.findViewById(R.id.answerdetails_child_headportrait);
                            Glide.with(getActivity()).load(listBean.getA_head()).into(answerdetails_child_headportrait);
                            //学员名称
                            TextView answerdetails_child_name = view.findViewById(R.id.answerdetails_child_name);
                            answerdetails_child_name.setText(listBean.getQ_nicename());
                            //学员的时间
                            TextView manswerdetails_child_time = view.findViewById(R.id.answerdetails_child_time);
                            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.getCreation_time());
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
                                    listBean.setCreation_time(df2.format(date1).toString());
                                }
                            }
                            manswerdetails_child_time.setText(listBean.getCreation_time());
                            //学员的消息
                            TextView answerdetails_child_message = view.findViewById(R.id.answerdetails_child_message);
                            new ModelHtmlUtils(mControlMainActivity, answerdetails_child_message).setHtmlWithPic(listBean.getContent());
                            answerdetails_content.addView(view);
                            line = view.findViewById(R.id.answerdetails_child_line);
                        }
                        if (line != null) {
                            line.setVisibility(View.INVISIBLE);
                        }
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Throwable t) {
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //我的问答详情-下拉加载
    public void getQueryOneQuestionMore(Integer questions_id,Integer course_type){
        if (questions_id == null){
            if (mSmart_model_my_myanswerdetails != null) {
                mSmart_model_my_myanswerdetails.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout answerdetails_content = mAnswerDetailsView.findViewById(R.id.answerdetails_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerDetailsCurrentPage = mMyQuestionAndAnswerDetailsCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerDetailsCurrentPage);//第几页
        paramsMap.put("pageSize",mMyQuestionAndAnswerDetailsPageCount);//每页几条
        paramsMap.put("questions_id", questions_id);//问题id
        paramsMap.put("course_type", course_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<ModelCommunityAnswer.CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Response<ModelCommunityAnswer.CommunityDetilsBean> response) {
                        ModelCommunityAnswer.CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(communityDetilsBean.getCode(),communityDetilsBean.getMsg())){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean= communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //添加问题的布局
                        ControllerCustomRoundAngleImageView answerdetails_headportrait = mAnswerDetailsView.findViewById(R.id.answerdetails_headportrait);
                        Glide.with(mControlMainActivity).load(communityDetilsDataBean.getHead()).listener(new RequestListener<Drawable>() {
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
                        }).error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(answerdetails_headportrait);
                        TextView answerdetails_name = mAnswerDetailsView.findViewById(R.id.answerdetails_name);
                        answerdetails_name.setText(communityDetilsDataBean.getNicename());
                        TextView answerdetails_time = mAnswerDetailsView.findViewById(R.id.answerdetails_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
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
                                communityDetilsDataBean.setCreation_time(df2.format(date1).toString());
                            }
                        }
                        answerdetails_time.setText(communityDetilsDataBean.getCreation_time());
                        TextView answerdetails_title = mAnswerDetailsView.findViewById(R.id.answerdetails_title);
                        answerdetails_title.setText(communityDetilsDataBean.getTitle());
                        TextView answerdetails_message = mAnswerDetailsView.findViewById(R.id.answerdetails_message);
                        new ModelHtmlUtils(mControlMainActivity, answerdetails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        List<String> strings = communityDetilsDataBean.getSubject_id();
                        if (strings != null) {
                            for (int i = 0; i < strings.size(); i++) {
                                String string = strings.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0) {
                                    TextView answerdetails_sign1 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign1);
                                    answerdetails_sign1.setText(string);
                                    answerdetails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1) {
                                    TextView answerdetails_sign2 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign2);
                                    answerdetails_sign2.setText(string);
                                    answerdetails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2) {
                                    TextView answerdetails_sign3 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign3);
                                    answerdetails_sign3.setText(string);
                                    answerdetails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBean communityDetilsAnswerDataBean = communityDetilsDataBean.getHuida();
                        if (communityDetilsAnswerDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerDetailsSum = communityDetilsAnswerDataBean.getTotal();
                        List<ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList> listBeans = communityDetilsAnswerDataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        View line = null;
                        for (int i = 0; i < listBeans.size() ; i ++){
                            ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.modelanswerdetails_child, null);
                            //学员的头像
                            ControllerCustomRoundAngleImageView answerdetails_child_headportrait = view.findViewById(R.id.answerdetails_child_headportrait);
                            Glide.with(getActivity()).load(listBean.getA_head()).into(answerdetails_child_headportrait);
                            //学员名称
                            TextView answerdetails_child_name = view.findViewById(R.id.answerdetails_child_name);
                            answerdetails_child_name.setText(listBean.getQ_nicename());
                            //学员的时间
                            TextView manswerdetails_child_time = view.findViewById(R.id.answerdetails_child_time);
                            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.getCreation_time());
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
                                    listBean.setCreation_time(df2.format(date1).toString());
                                }
                            }
                            manswerdetails_child_time.setText(listBean.getCreation_time());
                            //学员的消息
                            TextView answerdetails_child_message = view.findViewById(R.id.answerdetails_child_message);
                            new ModelHtmlUtils(mControlMainActivity, answerdetails_child_message).setHtmlWithPic(listBean.getContent());
                            answerdetails_content.addView(view);
                            line = view.findViewById(R.id.answerdetails_child_line);
                        }
                        if (line != null) {
                            line.setVisibility(View.INVISIBLE);
                        }
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Throwable t) {
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //我的问答（我的提问）
    public static class MyQuestionsBean{
        private int code;
        private DataBean data;
        private Integer huida_num;
        private Integer wen_num;

        public Integer getHuida_num() {
            return huida_num;
        }

        public Integer getWen_num() {
            return wen_num;
        }

        public void setHuida_num(Integer huida_num) {
            this.huida_num = huida_num;
        }

        public void setWen_num(Integer wen_num) {
            this.wen_num = wen_num;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public DataBean getData() {
            return data;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
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

                private int fid;
                private int visit_num;
                private Integer questions_id;
                private String creation_time;
                private String wen_rs;
                private String title;
                private String content;
                private Integer course_type;

                public Integer getCourse_type() {
                    return course_type;
                }

                public void setCourse_type(Integer course_type) {
                    this.course_type = course_type;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getTitle() {
                    return title;
                }

                public int getFid() {
                    return fid;
                }

                public Integer getQuestions_id() {
                    return questions_id;
                }

                public int getVisit_num() {
                    return visit_num;
                }

                public String getCreation_time() {
                    return creation_time;
                }

                public String getWen_rs() {
                    return wen_rs;
                }

                public void setCreation_time(String creation_time) {
                    this.creation_time = creation_time;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public void setQuestions_id(Integer questions_id) {
                    this.questions_id = questions_id;
                }

                public void setVisit_num(int visit_num) {
                    this.visit_num = visit_num;
                }

                public void setWen_rs(String wen_rs) {
                    this.wen_rs = wen_rs;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getContent() {
                    return content;
                }
            }
        }
    }

    //我的问答（我的回答）
    public static class MyAnswerBean{
        private int code;
        private DataBean data;
        private Integer huida_num;
        private Integer wen_num;

        public Integer getHuida_num() {
            return huida_num;
        }

        public Integer getWen_num() {
            return wen_num;
        }

        public void setHuida_num(Integer huida_num) {
            this.huida_num = huida_num;
        }

        public void setWen_num(Integer wen_num) {
            this.wen_num = wen_num;
        }
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public DataBean getData() {
            return data;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
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

                private int fid;
                private int visit_num;
                private Integer questions_id;
                private String creation_time;
                private String huida_rs;
                private String title;
                private String content;
                private Integer huida_questions_id;
                private Integer course_type;

                public void setCourse_type(Integer course_type) {
                    this.course_type = course_type;
                }

                public Integer getCourse_type() {
                    return course_type;
                }

                public Integer getHuida_questions_id() {
                    return huida_questions_id;
                }

                public String getHuida_rs() {
                    return huida_rs;
                }

                public void setHuida_questions_id(Integer huida_questions_id) {
                    this.huida_questions_id = huida_questions_id;
                }

                public void setHuida_rs(String huida_rs) {
                    this.huida_rs = huida_rs;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getTitle() {
                    return title;
                }

                public int getFid() {
                    return fid;
                }

                public Integer getQuestions_id() {
                    return questions_id;
                }

                public int getVisit_num() {
                    return visit_num;
                }

                public String getCreation_time() {
                    return creation_time;
                }

                public String gethuida_rs() {
                    return huida_rs;
                }

                public void setCreation_time(String creation_time) {
                    this.creation_time = creation_time;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public void setQuestions_id(Integer questions_id) {
                    this.questions_id = questions_id;
                }

                public void setVisit_num(int visit_num) {
                    this.visit_num = visit_num;
                }

                public void sethuida_rs(String huida_rs) {
                    this.huida_rs = huida_rs;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getContent() {
                    return content;
                }
            }
        }
    }

    //我的问答-详情
    public static class MyAnswerDetailsBean{
        private int code;
        private DataBeanFather data;
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(DataBeanFather data) {
            this.data = data;
        }

        public DataBeanFather getData() {
            return data;
        }
        public static class DataBeanFather{
            private DataBean huida;
            private DataBean.ListBean wenti;

            public DataBean getHuida() {
                return huida;
            }

            public DataBean.ListBean getWenti() {
                return wenti;
            }

            public void setHuida(DataBean huida) {
                this.huida = huida;
            }

            public void setWenti(DataBean.ListBean wenti) {
                this.wenti = wenti;
            }
        }
        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
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
                private Integer st_state;
                private Integer publisher;
                private Integer state;
                private String creation_time;
                private String head;
                private String nicename;
                private String title;
                private String content;
                private String picture;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public void setCreation_time(String creation_time) {
                    this.creation_time = creation_time;
                }

                public String getCreation_time() {
                    return creation_time;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public Integer getPublisher() {
                    return publisher;
                }

                public Integer getSt_state() {
                    return st_state;
                }

                public Integer getState() {
                    return state;
                }

                public String getHead() {
                    return head;
                }

                public String getNicename() {
                    return nicename;
                }

                public String getPicture() {
                    return picture;
                }

                public void setHead(String head) {
                    this.head = head;
                }

                public void setNicename(String nicename) {
                    this.nicename = nicename;
                }

                public void setPicture(String picture) {
                    this.picture = picture;
                }

                public void setPublisher(Integer publisher) {
                    this.publisher = publisher;
                }

                public void setSt_state(Integer st_state) {
                    this.st_state = st_state;
                }

                public void setState(Integer state) {
                    this.state = state;
                }
            }
        }
    }

    //我的消息列表Bean
    public static class MymessageBean{
        private int code;
        private DataBean data;
        private Integer Sys_num;
        private Integer AD_sum;

        public Integer getAD_sum() {
            return AD_sum;
        }

        public Integer getSys_num() {
            return Sys_num;
        }

        public void setAD_sum(Integer AD_sum) {
            this.AD_sum = AD_sum;
        }

        public void setSys_num(Integer sys_num) {
            Sys_num = sys_num;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public DataBean getData() {
            return data;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
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
                 * order_status : 支付成功
                 * product_type : 课程包
                 * order_num : 20190920105823
                 * order_time : 2019-09-20T10:58:40.000+0800
                 * product_price : 1
                 * order_id : 1
                 * product_name : 课程包001
                 */

                private int tf_read;
                private int info_type;
                private int student_information_id;
                private String time;
                private String title;
                private String content;

                public int getInfo_type() {
                    return info_type;
                }

                public int getStudent_information_id() {
                    return student_information_id;
                }

                public int getTf_read() {
                    return tf_read;
                }

                public String getTime() {
                    return time;
                }

                public String getContent() {
                    return content;
                }

                public String getTitle() {
                    return title;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public void setInfo_type(int info_type) {
                    this.info_type = info_type;
                }

                public void setStudent_information_id(int student_information_id) {
                    this.student_information_id = student_information_id;
                }

                public void setTf_read(int tf_read) {
                    this.tf_read = tf_read;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }
    // 我的消息----全部已读
    public void ReadMyNews(String idS){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String content = "{\"student_information_id\": [" + idS + "],\"tf_read\":1}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), content);
        queryMyCourseList.ReadMyNews(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mControlMainActivity,"读取失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mControlMainActivity,"读取失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Toast.makeText(mControlMainActivity,"读取失败",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的消息列表-全部已读
    public void ReadMyNewsAll(){
        if (mControlMainActivity.mStuId.equals("")){
            Toast.makeText(mControlMainActivity,"读取失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        paramsMap.put("info_type", mMyMessageType);//	类型
        paramsMap.put("tf_read", 1);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.ReadMyNewsAll(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"读取失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"读取失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getModelMyMessageList(mMyMessageType);
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        Toast.makeText(mControlMainActivity,"已将所有消息置为已读状态！",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Toast.makeText(mControlMainActivity,"读取失败！",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的协议
    public void getModelMyMeent(View myclass_agreement,int agreement_id){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("agreement_id", agreement_id);//第几页
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCourseModelNewsAgreeMent(body)
                .enqueue(new Callback<MyClassMeent>() {
                    @Override
                    public void onResponse(Call<MyClassMeent> call, Response<MyClassMeent> response) {
                        MyClassMeent classMeent = response.body();
                        if (classMeent == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(classMeent.getCode(),"")){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (classMeent.getCode() == 200){
                            MyClassMeent.DataBean data = classMeent.getData();
                            String agreement_content = data.getAgreement_content();
                            TextView tv1 = myclass_agreement.findViewById(R.id.model_agreement_content);
                            new ModelHtmlUtils(mControlMainActivity, tv1).setHtmlWithPic(agreement_content);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyClassMeent> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的订单--取消订单的支付状态
    public void getCancelOrder(View MyOrderShow_MyOrder_view1,Integer order_id){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{\"order_id\": \"" + order_id + "\"," +
                "    \"stu_id\":" + mControlMainActivity.mStuId + "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyCancelOrderStates(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"取消订单失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"取消订单失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(mControlMainActivity,"取消订单成功",Toast.LENGTH_SHORT).show();
                        if (MyOrderShow_MyOrder_view1 != null) {
                            TextView modelmy_myorder1_orderstate1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderstate);
                            //取消支付，将此订单设置为失效
                            modelmy_myorder1_orderstate1.setText("支付失败");
                            RelativeLayout modelmy_myorder1_orderfunction2 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderfunction);
                            RelativeLayout.LayoutParams r2 = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction2.getLayoutParams();
                            r2.height = 0;
                            modelmy_myorder1_orderfunction2.setLayoutParams(r2);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        Toast.makeText(mControlMainActivity,"取消订单失败",Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //订单列表请求网络数据          queryMyPackageOrderList
    public void getModelMyOrderList(String type){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myorder != null){
                mSmart_model_my_myorder.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
        modelmy_myorder_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyOrderCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyOrderCurrentPage);//第几页
        paramsMap.put("pageSize",mMyOrderPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyPackageOrderList(body)
                .enqueue(new Callback<MyOrderlistBean>() {
                    @Override
                    public void onResponse(Call<MyOrderlistBean> call, Response<MyOrderlistBean> response) {
                        MyOrderlistBean orderlistBean = response.body();
                        if (orderlistBean == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = orderlistBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200) {
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyOrderlistBean.DataBean data = orderlistBean.getData();
                        if (data == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyOrderSum = data.total;
                        if (orderlistBean.getTotal_num() != null){
                            TextView modelmy_myorder_tab_all = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_all);
                            modelmy_myorder_tab_all.setText("全部（" + orderlistBean.getTotal_num() + "）");
                        }
                        if (orderlistBean.getSuccess_num() != null){
                            TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
                            modelmy_myorder_tab_finished.setText("已完成（" + orderlistBean.getSuccess_num() + "）");
                        }
                        if (orderlistBean.getFail_num() != null){
                            TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                            modelmy_myorder_tab_unfinish.setText("未完成（" + orderlistBean.getFail_num() + "）");
                        }
                        List<MyOrderlistBean.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i ++) {
                            MyOrderlistBean.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            MyOrderShow_MyOrder(modelmy_myorder_main_content,listBean);
                        }
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyOrderlistBean> call, Throwable t) {
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //订单列表请求网络数据          queryMyPackageOrderList -下拉加载
    public void getModelMyOrderListMore(String type){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_myorder != null){
                mSmart_model_my_myorder.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyOrderCurrentPage = mMyOrderCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyOrderCurrentPage);//第几页
        paramsMap.put("pageSize",mMyOrderPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyPackageOrderList(body)
                .enqueue(new Callback<MyOrderlistBean>() {
                    @Override
                    public void onResponse(Call<MyOrderlistBean> call, Response<MyOrderlistBean> response) {
                        MyOrderlistBean orderlistBean = response.body();
                        if (orderlistBean == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = orderlistBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200) {
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyOrderlistBean.DataBean data = orderlistBean.getData();
                        if (data == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (orderlistBean.getTotal_num() != null){
                            TextView modelmy_myorder_tab_all = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_all);
                            modelmy_myorder_tab_all.setText("全部（" + orderlistBean.getTotal_num() + "）");
                        }
                        if (orderlistBean.getSuccess_num() != null){
                            TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
                            modelmy_myorder_tab_finished.setText("已完成（" + orderlistBean.getSuccess_num() + "）");
                        }
                        if (orderlistBean.getFail_num() != null){
                            TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                            modelmy_myorder_tab_unfinish.setText("未完成（" + orderlistBean.getFail_num() + "）");
                        }
                        mMyOrderSum = data.total;
                        List<MyOrderlistBean.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i ++) {
                            MyOrderlistBean.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            MyOrderShow_MyOrder(modelmy_myorder_main_content,listBean);
                        }
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyOrderlistBean> call, Throwable t) {
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //获取订单详情
    public void getModelMyOrderDetails(Integer order_id,String state){
        if (order_id == null){
            Toast.makeText(mControlMainActivity, "查询订单详情失败", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("order_id", order_id);//订单id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyOrderDetails(body)
                .enqueue(new Callback<MyOrderDetailsBean>() {
                    @Override
                    public void onResponse(Call<MyOrderDetailsBean> call, Response<MyOrderDetailsBean> response) {
                        MyOrderDetailsBean myOrderDetailsBean = response.body();
                        if (myOrderDetailsBean == null){
                            Toast.makeText(mControlMainActivity, "查询订单详情失败", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myOrderDetailsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200) {
                            Toast.makeText(mControlMainActivity, "查询订单详情失败", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyOrderDetailsBean.DataBean data = myOrderDetailsBean.getData();
                        if (data == null){
                            Toast.makeText(mControlMainActivity, "查询订单详情失败", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyOrderShow_OrderDetails(data,state);
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyOrderDetailsBean> call, Throwable t) {
                        Toast.makeText(mControlMainActivity, "查询订单详情失败", Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }
    //我的收藏列表(课程)
   public static class QueryMyCollectionListBean{
        /**
         * code : 200
         * data : {"total":3,"list":[{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"course_type":"直播,录播","stuNum":201,"course_name":"测试课程002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"course_type":"直播","stuNum":12,"course_name":"江山体验","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"course_type":"直播,录播","stuNum":24,"course_name":"1","buying_base_number":23}],"pageNum":1,"pageSize":10,"size":3,"startRow":1,"endRow":3,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
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
             * total : 3
             * list : [{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"course_type":"直播,录播","stuNum":201,"course_name":"测试课程002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"course_type":"直播","stuNum":12,"course_name":"江山体验","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"course_type":"直播,录播","stuNum":24,"course_name":"1","buying_base_number":23}]
             * pageNum : 1
             * pageSize : 10
             * size : 3
             * startRow : 1
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
             * lastPage : 1
             * firstPage : 1
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
            private int lastPage;
            private int firstPage;
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

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
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
                 * cover : http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg
                 * course_id : 2
                 * course_type : 直播,录播
                 * stuNum : 201
                 * course_name : 测试课程002
                 * buying_base_number : 199
                 */

                private String cover;
                private int course_id;
                private String course_type;
                private int stuNum;
                private String course_name;
                private int buying_base_number;
                private Double special_price;
                private Double price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public String getCourse_type() {
                    return course_type;
                }

                public void setCourse_type(String course_type) {
                    this.course_type = course_type;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public String getCourse_name() {
                    return course_name;
                }

                public void setCourse_name(String course_name) {
                    this.course_name = course_name;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public void setSpecial_price(Double special_price) {
                    this.special_price = special_price;
                }

                public void setPrice(Double price) {
                    this.price = price;
                }

                public Double getPrice() {
                    return price;
                }

                public Double getSpecial_price() {
                    return special_price;
                }
            }
        }
    }
    //我的收藏列表(课程包)
    public static class QueryMyCollectionPacketListBean{
        /**
         * code : 200
         * data : {"total":1,"list":[{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":0,"course_package_id":14,"stuNum":52,"courseNum":0,"cp_name":"江山11","buying_base_number":50}],"pageNum":1,"pageSize":10,"size":1,"startRow":1,"endRow":1,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
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
             * total : 1
             * list : [{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":0,"course_package_id":14,"stuNum":52,"courseNum":0,"cp_name":"江山11","buying_base_number":50}]
             * pageNum : 1
             * pageSize : 10
             * size : 1
             * startRow : 1
             * endRow : 1
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
             * lastPage : 1
             * firstPage : 1
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
            private int lastPage;
            private int firstPage;
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

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
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
                 * cover : http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png
                 * stageNum : 0
                 * course_package_id : 14
                 * stuNum : 52
                 * courseNum : 0
                 * cp_name : 江山11
                 * buying_base_number : 50
                 */

                private String cover;
                private int stageNum;
                private int course_package_id;
                private int stuNum;
                private int courseNum;
                private String cp_name;
                private int buying_base_number;
                private Double favorable_price;
                private Double total_price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getStageNum() {
                    return stageNum;
                }

                public void setStageNum(int stageNum) {
                    this.stageNum = stageNum;
                }

                public int getCourse_package_id() {
                    return course_package_id;
                }

                public void setCourse_package_id(int course_package_id) {
                    this.course_package_id = course_package_id;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public int getCourseNum() {
                    return courseNum;
                }

                public void setCourseNum(int courseNum) {
                    this.courseNum = courseNum;
                }

                public String getCp_name() {
                    return cp_name;
                }

                public void setCp_name(String cp_name) {
                    this.cp_name = cp_name;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public void setTotal_price(Double total_price) {
                    this.total_price = total_price;
                }

                public void setFavorable_price(Double favorable_price) {
                    this.favorable_price = favorable_price;
                }

                public Double getFavorable_price() {
                    return favorable_price;
                }

                public Double getTotal_price() {
                    return total_price;
                }
            }
        }
    }
    //我的课程列表
    public static class QueryMyCourseListBean {
        /**
         * code : 200
         * data : {"total":4,"list":[{"cover":"封面88899","course_id":1,"agreement_id":null,"course_type":"直播","stuNum":53,"course_name":"测试课程","buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"agreement_id":2,"course_type":"直播,录播","stuNum":201,"course_name":"测试课程002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"agreement_id":null,"course_type":"直播","stuNum":12,"course_name":"江山体验","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"agreement_id":null,"course_type":"直播,录播","stuNum":24,"course_name":"1","buying_base_number":23}],"pageNum":1,"pageSize":4,"size":4,"startRow":1,"endRow":4,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
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
             * list : [{"cover":"封面88899","course_id":1,"agreement_id":null,"course_type":"直播","stuNum":53,"course_name":"测试课程","buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"agreement_id":2,"course_type":"直播,录播","stuNum":201,"course_name":"测试课程002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"agreement_id":null,"course_type":"直播","stuNum":12,"course_name":"江山体验","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"agreement_id":null,"course_type":"直播,录播","stuNum":24,"course_name":"1","buying_base_number":23}]
             * pageNum : 1
             * pageSize : 4
             * size : 4
             * startRow : 1
             * endRow : 4
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
                 * cover : 封面88899
                 * course_id : 1
                 * agreement_id : null
                 * course_type : 直播
                 * stuNum : 53
                 * course_name : 测试课程
                 * buying_base_number : 50
                 */

                private String cover;
                private int course_id;
                private Integer agreement_id;
                private String course_type;
                private int stuNum;
                private String course_name;
                private int buying_base_number;
                private double price;
                private double special_price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public Integer getAgreement_id() {
                    return agreement_id;
                }

                public void setAgreement_id(Integer agreement_id) {
                    this.agreement_id = agreement_id;
                }

                public String getCourse_type() {
                    return course_type;
                }

                public void setCourse_type(String course_type) {
                    this.course_type = course_type;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public String getCourse_name() {
                    return course_name;
                }

                public void setCourse_name(String course_name) {
                    this.course_name = course_name;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public double getPrice() {
                    return price;
                }

                public double getSpecial_price() {
                    return special_price;
                }

                public void setPrice(double price) {
                    this.price = price;
                }

                public void setSpecial_price(double special_price) {
                    this.special_price = special_price;
                }
            }
        }
    }

    //我的课程包列表
    public static class MyclassPacketList {
        /**
         * code : 200
         * data : {"total":2,"list":[{"cover":"E:/upload111/1576155322131.png","stageNum":10,"course_package_id":1,"agreement_id":4,"stuNum":52,"courseNum":6,"cp_name":"课程包001","rateOfLearning":0.4521,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":1,"course_package_id":14,"agreement_id":null,"stuNum":52,"courseNum":3,"cp_name":"江山11","rateOfLearning":0.4533,"buying_base_number":50}],"pageNum":1,"pageSize":10,"size":2,"startRow":1,"endRow":2,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
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
             * total : 2
             * list : [{"cover":"E:/upload111/1576155322131.png","stageNum":10,"course_package_id":1,"agreement_id":4,"stuNum":52,"courseNum":6,"cp_name":"课程包001","rateOfLearning":0.4521,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":1,"course_package_id":14,"agreement_id":null,"stuNum":52,"courseNum":3,"cp_name":"江山11","rateOfLearning":0.4533,"buying_base_number":50}]
             * pageNum : 1
             * pageSize : 10
             * size : 2
             * startRow : 1
             * endRow : 2
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
             * lastPage : 1
             * firstPage : 1
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
            private int lastPage;
            private int firstPage;
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

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
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
                 * cover : E:/upload111/1576155322131.png
                 * stageNum : 10
                 * course_package_id : 1
                 * agreement_id : 4
                 * stuNum : 52
                 * courseNum : 6
                 * cp_name : 课程包001
                 * rateOfLearning : 0.4521
                 * buying_base_number : 50
                 */

                private String cover;
                private int stageNum;
                private int course_package_id;
                private Integer agreement_id;
                private int stuNum;
                private int courseNum;
                private String cp_name;
                private Double rateOfLearning;
                private int buying_base_number;
                private double total_price;
                private double favorable_price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getStageNum() {
                    return stageNum;
                }

                public void setStageNum(int stageNum) {
                    this.stageNum = stageNum;
                }

                public int getCourse_package_id() {
                    return course_package_id;
                }

                public void setCourse_package_id(int course_package_id) {
                    this.course_package_id = course_package_id;
                }

                public Integer getAgreement_id() {
                    return agreement_id;
                }

                public void setAgreement_id(Integer agreement_id) {
                    this.agreement_id = agreement_id;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public int getCourseNum() {
                    return courseNum;
                }

                public void setCourseNum(int courseNum) {
                    this.courseNum = courseNum;
                }

                public String getCp_name() {
                    return cp_name;
                }

                public void setCp_name(String cp_name) {
                    this.cp_name = cp_name;
                }

                public Double getRateOfLearning() {
                    return rateOfLearning;
                }

                public void setRateOfLearning(Double rateOfLearning) {
                    this.rateOfLearning = rateOfLearning;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public double getFavorable_price() {
                    return favorable_price;
                }

                public double getTotal_price() {
                    return total_price;
                }

                public void setFavorable_price(double favorable_price) {
                    this.favorable_price = favorable_price;
                }

                public void setTotal_price(double total_price) {
                    this.total_price = total_price;
                }
            }
        }
    }
    //我的课程协议和我的课程包协议     queryCourseModelNewsAgreeMent
    public static  class MyClassMeent{
        /**
         * code : 200
         * data : {"agreement_id":1,"agreement_content":"\r\n学员\r\n \r\n课程\r\n \r\n教学\r\n \r\n运营\r\n \r\n题库\r\n \r\n资源\r\n \r\n财务\r\n \r\n数据\r\n \r\n系统\r\n \r\n帮助\r\n \r\n默认分校 秦羽\r\n \r\n退出\r\n资源\r\n学科\r\n \r\n校区\r\n \r\n教室\r\n \r\n资源库\r\n \r\n课程协议\r\n \r\n教师课时\r\n \r\n课程单元\r\n查看详情\r\n账号\r\n商品名称\r\n火种教育一级注册消防工程师考试【定金班】协议（20191015版）\r\n签署终端\r\n签署时间\r\n一级注册消防工程师考试学习服务协议【定金班】\r\n一级注册消防工程师考试学习服务协议【定金班】\r\n\r\n\u2014\u2014帮助您在火种教育更好地完成学业和收获能力\r\n\r\n亲爱的学员，您好！\r\n\r\n火种教育全体同仁欢迎您选择在这里完成学业并考取证书。\r\n\r\n在您享受课程的过程中，以下守则的遵守和共识的达成，对我们的服务是否及时和有效至关重要。\r\n\r\n请您仔细阅读并签字确认，感谢您的理解和配合。\r\n\r\n一、班级化管理准则\r\n\r\n关于校区：学校以网络直播的授课方式为主，可以随时听课，请大家自我约束并主动学习，请勿自误。直播课堂的学习需要在学员的电脑里安装专用软件，由校方教学运营专员协助学员安装。当您完成报名、培训学习费用缴纳后、学校将向学员发放直播课的会员权限。该权限只允许学员一人使用，不得转借或转让他人。一经发现，学校有权取消该学员听课资格，不承担任何违约责任。\r\n\r\n校方联系方式:     咨询服务微信：[huozhongbanzhuren    ]\r\n\r\n咨询服务邮箱：huozhongedu    @126.com\r\n\r\n                   联系电话：\r\n\r\n                                01060703671 服务时间：09:00-12:00,  13:00-18:00\r\n                                01081705189 服务时间：13:00-18:00,  19:00-22:00\r\n\r\n二、学校提供的产品和服务\r\n\r\n1、在线直播主课程：全年滚动上课\r\n\r\n2、网课：直播结束后上传，可供随时观看\r\n\r\n3、在线答疑（客服类问题）：微信解决学员操作类疑问\r\n\r\n4、1对1私人助教（学习类问题）：教研老师在规定时间内解决\r\n\r\n5、跟踪管理：跟踪学员学习情况，考试情况的定期电话/微信/QQ回访\r\n\r\n6、模拟考试：题库系统模考答题及解析\r\n\r\n7、通关宝典：历年考试重难点浓缩精华\r\n\r\n8、考前答疑：考试前夕名师直播解答学员疑问\r\n\r\n9、模拟卷在学员按规定支付培训费用后由学校邮寄给学员，邮费由学校承担。\r\n\r\n10、学校为学员提供的服务期限从报名之日起至2020年一级注册消防工程师考试结束当天24点止。若学员在服务期限内未通过考试，并符合重修条件的按本协议约定执行。\r\n\r\n三、课程管理准则\r\n\r\n1．开课：学员所报课程在学校安排的第一次上课时间即为开课日期，插班学员款到日期即为开课日期，插班学员可自行补足开课前的课程；在无特殊情况下我们会严格依照原定开课计划履行课程服务。由于各种意外情况或不可抗力导致的无法按时开课的情况，我们将以微信等方式通知学员，后期会安排补课，具体开课时间另行通知。\r\n\r\n2．确认课程安排：学员应及时主动接收微信并查看班主任通知的课表，上课时间及地点，按通知的信息上课。学员因事、病等自身原因不能前来上课，需至少提前6小时请假，请假的学员自行从学校网站观看课程。每个学员在培训期间请假的次数不能超过当期课程的30%。\r\n\r\n3．上课纪律：上课时请遵守课堂纪律。如有疑问，可课下咨询班主任或老师。如学员严重干扰课堂纪律，影响他人上课，班主任有权终止该学员继续听课。该情形达到3次，学校有权取消学员听课资格，学校不承担任何违约责任。\r\n\r\n4．课程有效性：学员在报名时选择相应的考期，考期结束后，学校会关停学员学习账号。若学员通过报名表中报名科目，则本协议终止。若学员未通过此次考试，则学员需向学校申请重修。\r\n\r\n四、缴费标准及方式\r\n\r\n学员应在本协议签订前支付培训课程定金费用，剩余培训费用学员应在收到甲方的协议和定金班课程后2日内付完全款，定金一经缴纳，无论是否补费均不予退还。缴费方式参考报名表。\r\n\r\n五、关于不过退费与延时重修\r\n\r\n延时重修：报名本协议约定班型的学员可免费享受两次延时重修的机会，即学员 2020年参加考试未通过，2021年延时重修（在线课程、网上题库免费，其他资料费用学校另行收取），2021年参加考试仍未通过，2022年还可以延时重修（在线课程、网上题库免费，其他资料费用学校另行收取）。若2022年考试仍未通过，学员想继续学习的，则该学员应重新缴纳足额的课程费用方可继续学习。\r\n\r\n延时重修是学校赠予的课程服务，如果学员因自身原因或不可抗力导致不能重修的，学校不承担任何违约责任，也不予退还任何费用。若学员在重修期间通过考试的，学员不得要求学校继续提供相关的课程服务。因延时重修不等同于重新报名，故延时重修期间不计入前述的服务期限。\r\n\r\n延时重修仅适用于以下班型：基础精讲班、一次取证班、签约取证班。\r\n\r\n不过退费：本协议生效后，学员经过服务期内一次考试及延时重修期间两次考试，共计三次考试均未缺考一级注册消防工程师考试，且每次考试三科成绩均大于0分且均低于合格分，则培训费用全额退还。\r\n\r\n不过退费条款将于2022年一级注册消防工程师考试结果公布后生效，学员将三年考试成绩发送给班主任向学校申请退费。经学校审核学员满足退费条件的，学校按退费约定退还学员费用。\r\n\r\n不过退费仅适用于以下班型：一次取证班、签约取证班。\r\n\r\n不同班型的不过退费金额不一致，具体金额根据补费班型确定。\r\n\r\n退费时，如已开具发票，必须退回完整发票。（一旦奖区联被撕毁或丢失，根据税务局相关规定将扣除相应税费）\r\n\r\n六、考过培训费用全额奖励\r\n\r\n考过培训费用全额奖励：报名本协议约定班型的学员可享受考过培训费用全额奖励，即学员参加2020年一级注册消防工程师考试，3科成绩均高于合格分，可申请培训费用全额奖励。学员须将考试成绩发给学校审核，学校审核确认后按约定退还学员培训费用作为奖励。\r\n\r\n考过培训费用全额奖励仅适用于以下班型：一次取证班、签约取证班。\r\n\r\n七、关于转班\r\n\r\n学员开课后7天内（含第七天），可以免费换为同等价格的其他课程；超过7天到不超过30天的，需要缴纳新课程的费用的10%作为手续费；超过30天但不超过60天的，需要缴纳新课程的费用的20%作为手续费；超过60天但不超过90天的，需要缴纳新课程的费用的30%作为手续费；超过90天但不超过120天的，需要缴纳新课程的费用的40%作为手续费；超过120天后学员不可以更换课程。更换课程不影响课程的有效期，即更换新课程后服务期仍从初始班名班型开始计算服务期。\r\n\r\n八、关于冻结课程\r\n\r\n学员每次选定考期后，所选当期考期考试第一天（算作第一天）向前推30天（含第30天）之内不允许冻结课程，31天向前可以冻结，冻结期限最长为3个月，拥有一次冻结机会。\r\n\r\n九、关于升班\r\n\r\n学员报名后随时可以进行班型升级，升级时需要补齐新班型与老班型之前的差价，服务期从新班型差价补齐开始计算；升级班型后将签订新的协议，本协议自动作废。\r\n\r\n十、信息变更准则\r\n\r\n如有任何授课信息的变动，校方会以QQ/微信等形式通知各位学员，重要通知我们也会通过电话等方式提前6小时告知学员。请学员保证预留的联系方式准确无误。如学员的联系方式变更，请及时致电班主任进行修改确认。否则由该学员承担未及时通知的后果。\r\n\r\n十一、注意事项\r\n\r\n1、为防止内部讲课视频流出和密训题泄露，严禁学员私下建立QQ和微信群，一经发现取消听课资格，有任何事情随时联系学员的班主任，班主任会一对一为学员解答。\r\n\r\n2、学校向学员发出的任何通知均可通过学员预留的任一联系方式发出，学员应及时收悉并回复确认，未及时确认的，通知发出后3小时视为送达。\r\n\r\n3、学员应对学校的商业信息（包括但不限于内部视频讲课、密训题、上课内容、教材等）负有保密义务。未经学校书面同意学员不得泄露或用于其他用途，否则学校有权要求学员支付违约金10000元。\r\n\r\n4、因学员自身原因被取消听课资格或学员自己提出终止听课的，学校不退还学员任何费用。\r\n\r\n5、因不可抗拒（如自然灾害、政府行为、征收、征用、罢工、骚乱等）等特殊情形导致无法完成课程，出现学员退费情形的，学校按学员剩余课时退还学员报名费用。\r\n\r\n6、学员不得私自与班主任达成协议，以金钱或其他方式要求班主任为其上课或私自提供课程资料。一经发现，学校有权取消该学员听课资格并不予退还报名费。\r\n\r\n7、任何一方对本协议的内容或履行有争议的，应协商解决。协商不成任何一方向学校所在地法院起诉。\r\n\r\n8、本协议自学校盖章、学员签字后生效。本协议一式两份，学校、学员各持一份。\r\n\r\n"}
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
             * agreement_id : 1
             * agreement_content :
             学员

             课程

             教学

             运营

             题库

             资源

             财务

             数据

             系统

             帮助

             默认分校 秦羽

             退出
             资源
             学科

             校区

             教室

             资源库

             课程协议

             教师课时

             课程单元
             查看详情
             账号
             商品名称
             火种教育一级注册消防工程师考试【定金班】协议（20191015版）
             签署终端
             签署时间
             一级注册消防工程师考试学习服务协议【定金班】
             一级注册消防工程师考试学习服务协议【定金班】

             ——帮助您在火种教育更好地完成学业和收获能力

             亲爱的学员，您好！

             火种教育全体同仁欢迎您选择在这里完成学业并考取证书。

             在您享受课程的过程中，以下守则的遵守和共识的达成，对我们的服务是否及时和有效至关重要。

             请您仔细阅读并签字确认，感谢您的理解和配合。

             一、班级化管理准则

             关于校区：学校以网络直播的授课方式为主，可以随时听课，请大家自我约束并主动学习，请勿自误。直播课堂的学习需要在学员的电脑里安装专用软件，由校方教学运营专员协助学员安装。当您完成报名、培训学习费用缴纳后、学校将向学员发放直播课的会员权限。该权限只允许学员一人使用，不得转借或转让他人。一经发现，学校有权取消该学员听课资格，不承担任何违约责任。

             校方联系方式:     咨询服务微信：[huozhongbanzhuren    ]

             咨询服务邮箱：huozhongedu    @126.com

             联系电话：

             01060703671 服务时间：09:00-12:00,  13:00-18:00
             01081705189 服务时间：13:00-18:00,  19:00-22:00

             二、学校提供的产品和服务

             1、在线直播主课程：全年滚动上课

             2、网课：直播结束后上传，可供随时观看

             3、在线答疑（客服类问题）：微信解决学员操作类疑问

             4、1对1私人助教（学习类问题）：教研老师在规定时间内解决

             5、跟踪管理：跟踪学员学习情况，考试情况的定期电话/微信/QQ回访

             6、模拟考试：题库系统模考答题及解析

             7、通关宝典：历年考试重难点浓缩精华

             8、考前答疑：考试前夕名师直播解答学员疑问

             9、模拟卷在学员按规定支付培训费用后由学校邮寄给学员，邮费由学校承担。

             10、学校为学员提供的服务期限从报名之日起至2020年一级注册消防工程师考试结束当天24点止。若学员在服务期限内未通过考试，并符合重修条件的按本协议约定执行。

             三、课程管理准则

             1．开课：学员所报课程在学校安排的第一次上课时间即为开课日期，插班学员款到日期即为开课日期，插班学员可自行补足开课前的课程；在无特殊情况下我们会严格依照原定开课计划履行课程服务。由于各种意外情况或不可抗力导致的无法按时开课的情况，我们将以微信等方式通知学员，后期会安排补课，具体开课时间另行通知。

             2．确认课程安排：学员应及时主动接收微信并查看班主任通知的课表，上课时间及地点，按通知的信息上课。学员因事、病等自身原因不能前来上课，需至少提前6小时请假，请假的学员自行从学校网站观看课程。每个学员在培训期间请假的次数不能超过当期课程的30%。

             3．上课纪律：上课时请遵守课堂纪律。如有疑问，可课下咨询班主任或老师。如学员严重干扰课堂纪律，影响他人上课，班主任有权终止该学员继续听课。该情形达到3次，学校有权取消学员听课资格，学校不承担任何违约责任。

             4．课程有效性：学员在报名时选择相应的考期，考期结束后，学校会关停学员学习账号。若学员通过报名表中报名科目，则本协议终止。若学员未通过此次考试，则学员需向学校申请重修。

             四、缴费标准及方式

             学员应在本协议签订前支付培训课程定金费用，剩余培训费用学员应在收到甲方的协议和定金班课程后2日内付完全款，定金一经缴纳，无论是否补费均不予退还。缴费方式参考报名表。

             五、关于不过退费与延时重修

             延时重修：报名本协议约定班型的学员可免费享受两次延时重修的机会，即学员 2020年参加考试未通过，2021年延时重修（在线课程、网上题库免费，其他资料费用学校另行收取），2021年参加考试仍未通过，2022年还可以延时重修（在线课程、网上题库免费，其他资料费用学校另行收取）。若2022年考试仍未通过，学员想继续学习的，则该学员应重新缴纳足额的课程费用方可继续学习。

             延时重修是学校赠予的课程服务，如果学员因自身原因或不可抗力导致不能重修的，学校不承担任何违约责任，也不予退还任何费用。若学员在重修期间通过考试的，学员不得要求学校继续提供相关的课程服务。因延时重修不等同于重新报名，故延时重修期间不计入前述的服务期限。

             延时重修仅适用于以下班型：基础精讲班、一次取证班、签约取证班。

             不过退费：本协议生效后，学员经过服务期内一次考试及延时重修期间两次考试，共计三次考试均未缺考一级注册消防工程师考试，且每次考试三科成绩均大于0分且均低于合格分，则培训费用全额退还。

             不过退费条款将于2022年一级注册消防工程师考试结果公布后生效，学员将三年考试成绩发送给班主任向学校申请退费。经学校审核学员满足退费条件的，学校按退费约定退还学员费用。

             不过退费仅适用于以下班型：一次取证班、签约取证班。

             不同班型的不过退费金额不一致，具体金额根据补费班型确定。

             退费时，如已开具发票，必须退回完整发票。（一旦奖区联被撕毁或丢失，根据税务局相关规定将扣除相应税费）

             六、考过培训费用全额奖励

             考过培训费用全额奖励：报名本协议约定班型的学员可享受考过培训费用全额奖励，即学员参加2020年一级注册消防工程师考试，3科成绩均高于合格分，可申请培训费用全额奖励。学员须将考试成绩发给学校审核，学校审核确认后按约定退还学员培训费用作为奖励。

             考过培训费用全额奖励仅适用于以下班型：一次取证班、签约取证班。

             七、关于转班

             学员开课后7天内（含第七天），可以免费换为同等价格的其他课程；超过7天到不超过30天的，需要缴纳新课程的费用的10%作为手续费；超过30天但不超过60天的，需要缴纳新课程的费用的20%作为手续费；超过60天但不超过90天的，需要缴纳新课程的费用的30%作为手续费；超过90天但不超过120天的，需要缴纳新课程的费用的40%作为手续费；超过120天后学员不可以更换课程。更换课程不影响课程的有效期，即更换新课程后服务期仍从初始班名班型开始计算服务期。

             八、关于冻结课程

             学员每次选定考期后，所选当期考期考试第一天（算作第一天）向前推30天（含第30天）之内不允许冻结课程，31天向前可以冻结，冻结期限最长为3个月，拥有一次冻结机会。

             九、关于升班

             学员报名后随时可以进行班型升级，升级时需要补齐新班型与老班型之前的差价，服务期从新班型差价补齐开始计算；升级班型后将签订新的协议，本协议自动作废。

             十、信息变更准则

             如有任何授课信息的变动，校方会以QQ/微信等形式通知各位学员，重要通知我们也会通过电话等方式提前6小时告知学员。请学员保证预留的联系方式准确无误。如学员的联系方式变更，请及时致电班主任进行修改确认。否则由该学员承担未及时通知的后果。

             十一、注意事项

             1、为防止内部讲课视频流出和密训题泄露，严禁学员私下建立QQ和微信群，一经发现取消听课资格，有任何事情随时联系学员的班主任，班主任会一对一为学员解答。

             2、学校向学员发出的任何通知均可通过学员预留的任一联系方式发出，学员应及时收悉并回复确认，未及时确认的，通知发出后3小时视为送达。

             3、学员应对学校的商业信息（包括但不限于内部视频讲课、密训题、上课内容、教材等）负有保密义务。未经学校书面同意学员不得泄露或用于其他用途，否则学校有权要求学员支付违约金10000元。

             4、因学员自身原因被取消听课资格或学员自己提出终止听课的，学校不退还学员任何费用。

             5、因不可抗拒（如自然灾害、政府行为、征收、征用、罢工、骚乱等）等特殊情形导致无法完成课程，出现学员退费情形的，学校按学员剩余课时退还学员报名费用。

             6、学员不得私自与班主任达成协议，以金钱或其他方式要求班主任为其上课或私自提供课程资料。一经发现，学校有权取消该学员听课资格并不予退还报名费。

             7、任何一方对本协议的内容或履行有争议的，应协商解决。协商不成任何一方向学校所在地法院起诉。

             8、本协议自学校盖章、学员签字后生效。本协议一式两份，学校、学员各持一份。


             */

            private int agreement_id;
            private String agreement_content;

            public int getAgreement_id() {
                return agreement_id;
            }

            public void setAgreement_id(int agreement_id) {
                this.agreement_id = agreement_id;
            }

            public String getAgreement_content() {
                return agreement_content;
            }

            public void setAgreement_content(String agreement_content) {
                this.agreement_content = agreement_content;
            }
        }
    }


    //我的订单列表
    public static class MyOrderlistBean{
        /**
         * code : 200
         * data : {"total":6,"list":[{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}],"pageNum":1,"pageSize":10,"size":6,"startRow":1,"endRow":6,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;
        private Integer total_num;
        private Integer success_num;
        private Integer fail_num;

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

        public Integer getFail_num() {
            return fail_num;
        }

        public Integer getSuccess_num() {
            return success_num;
        }

        public Integer getTotal_num() {
            return total_num;
        }

        public void setFail_num(Integer fail_num) {
            this.fail_num = fail_num;
        }

        public void setSuccess_num(Integer success_num) {
            this.success_num = success_num;
        }

        public void setTotal_num(Integer total_num) {
            this.total_num = total_num;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
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
                 * order_status : 支付成功
                 * product_type : 课程包
                 * order_num : 20190920105823
                 * order_time : 2019-09-20T10:58:40.000+0800
                 * product_price : 1
                 * order_id : 1
                 * product_name : 课程包001
                 */

                private String order_status;
                private String product_type;
                private String order_num;
                private String order_time;
                private double product_price;
                private int order_id;
                private String product_name;
                private Integer CPC_id;

                public Integer getCPC_id() {
                    return CPC_id;
                }

                public void setCPC_id(Integer CPC_id) {
                    this.CPC_id = CPC_id;
                }

                public String getOrder_status() {
                    return order_status;
                }

                public void setOrder_status(String order_status) {
                    this.order_status = order_status;
                }

                public String getProduct_type() {
                    return product_type;
                }

                public void setProduct_type(String product_type) {
                    this.product_type = product_type;
                }

                public String getOrder_num() {
                    return order_num;
                }

                public void setOrder_num(String order_num) {
                    this.order_num = order_num;
                }

                public String getOrder_time() {
                    return order_time;
                }

                public void setOrder_time(String order_time) {
                    this.order_time = order_time;
                }

                public Double getProduct_price() {
                    return product_price;
                }

                public void setProduct_price(Double product_price) {
                    this.product_price = product_price;
                }

                public int getOrder_id() {
                    return order_id;
                }

                public void setOrder_id(int order_id) {
                    this.order_id = order_id;
                }

                public String getProduct_name() {
                    return product_name;
                }

                public void setProduct_name(String product_name) {
                    this.product_name = product_name;
                }
            }
        }

    }

    //我的订单详情
    public static class MyOrderDetailsBean{
        /**
         * code : 200
         * data : {"total":6,"list":[{"order_status":"支付成功","product_type":"课程包","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"课程包001"},{"order_status":"支付成功","product_type":"课程包","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"课程包002"},{"order_status":"支付成功","product_type":"课程","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"测试课程11"},{"order_status":"未支付","product_type":"课程包","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"江山11"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"测试课程"},{"order_status":"支付成功","product_type":"课程","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"测试课程"}],"pageNum":1,"pageSize":10,"size":6,"startRow":1,"endRow":6,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
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

            private Integer course_id;
            private float refund_payment_money;
            private Integer validityTime;
            private String product_type;
            private Integer course_package_id;
            private String order_num;
            private String order_time;
            private float product_price;
            private String product_name;
            private priceInfoBean priceInfo;

            public Integer getCourse_id() {
                return course_id;
            }

            public void setCourse_id(Integer course_id) {
                this.course_id = course_id;
            }

            public float getRefund_payment_money() {
                return refund_payment_money;
            }

            public void setRefund_payment_money(float refund_payment_money) {
                this.refund_payment_money = refund_payment_money;
            }

            public Integer getValidityTime() {
                return validityTime;
            }

            public void setValidityTime(Integer validityTime) {
                this.validityTime = validityTime;
            }

            public String getProduct_type() {
                return product_type;
            }

            public void setProduct_type(String product_type) {
                this.product_type = product_type;
            }

            public Integer getCourse_package_id() {
                return course_package_id;
            }

            public void setCourse_package_id(Integer course_package_id) {
                this.course_package_id = course_package_id;
            }

            public String getOrder_num() {
                return order_num;
            }

            public void setOrder_num(String order_num) {
                this.order_num = order_num;
            }

            public String getOrder_time() {
                return order_time;
            }

            public void setOrder_time(String order_time) {
                this.order_time = order_time;
            }

            public float getProduct_price() {
                return product_price;
            }

            public void setProduct_price(float product_price) {
                this.product_price = product_price;
            }

            public String getProduct_name() {
                return product_name;
            }

            public void setProduct_name(String product_name) {
                this.product_name = product_name;
            }

            public priceInfoBean getPriceInfo() {
                return priceInfo;
            }

            public void setPriceInfo(priceInfoBean priceInfo) {
                this.priceInfo = priceInfo;
            }

            public static class priceInfoBean {
                private float price;
                private float new_price;

                public float getPrice() {
                    return price;
                }

                public void setPrice(float price) {
                    this.price = price;
                }

                public float getNew_price() {
                    return new_price;
                }

                public void setNew_price(float new_price) {
                    this.new_price = new_price;
                }
            }
        }

    }
    //我的优惠券列表网络请求
    public void getMyCouponList(String type){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mycoupon != null){
                mSmart_model_my_mycoupon.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout mycoupon_end = mMyCouponView.findViewById(R.id.mycoupon_end);
        mycoupon_end.setVisibility(View.INVISIBLE);
        LinearLayout modelmy_mycoupon_main_content = mMyCouponView.findViewById(R.id.modelmy_mycoupon_main_content);
        modelmy_mycoupon_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyCouponCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        HashMap<String, Integer> paramsMap = new HashMap<>();
        Gson gson = new Gson();
        paramsMap.put("pageNum", mMyCouponCurrentPage);//第几页
        paramsMap.put("pageSize",mMyCouponPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.getMyCouponListMessage(body)
                .enqueue(new Callback<MyCoupon>() {
                    @Override
                    public void onResponse(Call<MyCoupon> call, Response<MyCoupon> response) {
                        MyCoupon myCoupon = response.body();
                        if (myCoupon == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myCoupon.getCode(),"")){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myCoupon.getCode();
                        if (code != 200){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyCoupon.DataBean data = myCoupon.getData();
                        if (data == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyCouponSum = data.getTotal();
                        if (myCoupon.getNot_used() != null){
                            TextView modelmy_mycoupon_tab_notused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_notused);
                            modelmy_mycoupon_tab_notused.setText("未使用(" + myCoupon.getNot_used() + ")");
                        }
                        if (myCoupon.getAlready_used() != null){
                            TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
                            modelmy_mycoupon_tab_alreadyused.setText("已使用(" + myCoupon.getAlready_used() + ")");
                        }
                        if (myCoupon.getExpired() != null){
                            TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                            modelmy_mycoupon_tab_expired.setText("已过期(" + myCoupon.getExpired() + ")");
                        }
                        List<MyCoupon.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            MyCoupon.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            //可循环调用以下方法，添加多个优惠券
                            MyCouponShow_MyCoupon(modelmy_mycoupon_main_content,listBean);
                        }
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyCoupon> call, Throwable t) {
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的优惠券列表网络请求
    public void getMyCouponListMore(String type){
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_my_mycoupon != null){
                mSmart_model_my_mycoupon.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout modelmy_mycoupon_main_content = mMyCouponView.findViewById(R.id.modelmy_mycoupon_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyCouponCurrentPage = mMyCouponCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        HashMap<String, Integer> paramsMap = new HashMap<>();
        Gson gson = new Gson();
        paramsMap.put("pageNum", mMyCouponCurrentPage);//第几页
        paramsMap.put("pageSize",mMyCouponPageCount);//每页几条
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.getMyCouponListMessage(body)
                .enqueue(new Callback<MyCoupon>() {
                    @Override
                    public void onResponse(Call<MyCoupon> call, Response<MyCoupon> response) {
                        MyCoupon myCoupon = response.body();
                        if (myCoupon == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myCoupon.getCode(),"")){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myCoupon.getCode();
                        if (code != 200){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        MyCoupon.DataBean data = myCoupon.getData();
                        if (data == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyCouponSum = data.getTotal();
                        if (myCoupon.getNot_used() != null){
                            TextView modelmy_mycoupon_tab_notused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_notused);
                            modelmy_mycoupon_tab_notused.setText("未使用(" + myCoupon.getNot_used() + ")");
                        }
                        if (myCoupon.getAlready_used() != null){
                            TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
                            modelmy_mycoupon_tab_alreadyused.setText("已使用(" + myCoupon.getAlready_used() + ")");
                        }
                        if (myCoupon.getExpired() != null){
                            TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                            modelmy_mycoupon_tab_expired.setText("已过期(" + myCoupon.getExpired() + ")");
                        }
                        List<MyCoupon.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            MyCoupon.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            //可循环调用以下方法，添加多个优惠券
                            MyCouponShow_MyCoupon(modelmy_mycoupon_main_content,listBean);
                        }
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyCoupon> call, Throwable t) {
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //我的优惠券列表
    public static class MyCoupon{
        /**
         * code : 200
         * data : {"total":1,"list":[{"subject_id":null,"dc_denomination":"0.02","course_id":0,"service_life_start_time":"2019-10-30","preferential_way":"抵现","stu_id":1,"small_discount_status":"已领取","使用范围":"可用于所有课程","product_type":"课程","course_package_id":null,"project_id":null,"service_life_end_time":"2019-11-01","small_discount_id":9,"preferential_scope":"全部课程"}],"pageNum":1,"pageSize":10,"size":1,"startRow":1,"endRow":1,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;
        private Integer expired;
        private Integer not_used;
        private Integer already_used;

        public Integer getAlready_used() {
            return already_used;
        }

        public Integer getExpired() {
            return expired;
        }

        public Integer getNot_used() {
            return not_used;
        }

        public void setAlready_used(Integer already_used) {
            this.already_used = already_used;
        }

        public void setExpired(Integer expired) {
            this.expired = expired;
        }

        public void setNot_used(Integer not_used) {
            this.not_used = not_used;
        }

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
             * total : 1
             * list : [{"subject_id":null,"dc_denomination":"0.02","course_id":0,"service_life_start_time":"2019-10-30","preferential_way":"抵现","stu_id":1,"small_discount_status":"已领取","使用范围":"可用于所有课程","product_type":"课程","course_package_id":null,"project_id":null,"service_life_end_time":"2019-11-01","small_discount_id":9,"preferential_scope":"全部课程"}]
             * pageNum : 1
             * pageSize : 10
             * size : 1
             * startRow : 1
             * endRow : 1
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
                 * subject_id : null
                 * dc_denomination : 0.02
                 * course_id : 0
                 * service_life_start_time : 2019-10-30
                 * preferential_way : 抵现
                 * stu_id : 1
                 * small_discount_status : 已领取
                 * 使用范围 : 可用于所有课程
                 * product_type : 课程
                 * course_package_id : null
                 * project_id : null
                 * service_life_end_time : 2019-11-01
                 * small_discount_id : 9
                 * preferential_scope : 全部课程
                 */

                private Object subject_id;
                private String dc_denomination;
                private int course_id;
                private String service_life_start_time;
                private String preferential_way;
                private int stu_id;
                private String small_discount_status;
                private String scope;
                private String product_type;
                private Object course_package_id;
                private Object project_id;
                private String service_life_end_time;
                private int small_discount_id;
                private String preferential_scope;

                public Object getSubject_id() {
                    return subject_id;
                }

                public void setSubject_id(Object subject_id) {
                    this.subject_id = subject_id;
                }

                public String getDc_denomination() {
                    return dc_denomination;
                }

                public void setDc_denomination(String dc_denomination) {
                    this.dc_denomination = dc_denomination;
                }

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public String getService_life_start_time() {
                    return service_life_start_time;
                }

                public void setService_life_start_time(String service_life_start_time) {
                    this.service_life_start_time = service_life_start_time;
                }

                public String getPreferential_way() {
                    return preferential_way;
                }

                public void setPreferential_way(String preferential_way) {
                    this.preferential_way = preferential_way;
                }

                public int getStu_id() {
                    return stu_id;
                }

                public void setStu_id(int stu_id) {
                    this.stu_id = stu_id;
                }

                public String getSmall_discount_status() {
                    return small_discount_status;
                }

                public void setSmall_discount_status(String small_discount_status) {
                    this.small_discount_status = small_discount_status;
                }

                public String getscope() {
                    return scope;
                }

                public void setscope(String scope) {
                    this.scope = scope;
                }

                public String getProduct_type() {
                    return product_type;
                }

                public void setProduct_type(String product_type) {
                    this.product_type = product_type;
                }

                public Object getCourse_package_id() {
                    return course_package_id;
                }

                public void setCourse_package_id(Object course_package_id) {
                    this.course_package_id = course_package_id;
                }

                public Object getProject_id() {
                    return project_id;
                }

                public void setProject_id(Object project_id) {
                    this.project_id = project_id;
                }

                public String getService_life_end_time() {
                    return service_life_end_time;
                }

                public void setService_life_end_time(String service_life_end_time) {
                    this.service_life_end_time = service_life_end_time;
                }

                public int getSmall_discount_id() {
                    return small_discount_id;
                }

                public void setSmall_discount_id(int small_discount_id) {
                    this.small_discount_id = small_discount_id;
                }

                public String getPreferential_scope() {
                    return preferential_scope;
                }

                public void setPreferential_scope(String preferential_scope) {
                    this.preferential_scope = preferential_scope;
                }
            }
        }
    }

    public void ModelCourseCoverQuestionPictureAdd(Intent data) {
        if (mModelCourseCover != null) {
            mModelCourseCover.ModelCourseCoverQuestionPictureAdd(data);
        }
    }
}
