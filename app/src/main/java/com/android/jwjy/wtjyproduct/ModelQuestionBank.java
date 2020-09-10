package com.android.jwjy.wtjyproduct;

import android.graphics.drawable.ColorDrawable;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wtjyproduct.R;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.talkfun.utils.HandlerUtil.runOnUiThread;
/**
 * Created by dayuer on 19/7/2.
 * 题库模块
 */
public class ModelQuestionBank extends Fragment implements View.OnClickListener {
    private static ControlMainActivity mControlMainActivity;
    private static String mContext = "xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mview;
    private int height = 1344;
    private int width = 720;
    private String mCurrentTab = "ChapterExercises", mQuestionRecordCurrentTab = "ChapterExercises";
    private int mLastTabIndex = 1, mQuestionRecordLastTabIndex = 1;
    private View mModelQuestionBankView = null, mModelQuestionBankDetailsView = null, mModelQuestionBankSettingView = null,
            mModelQuestionBankAnswerPaperView = null, mModelQuestionBankAnswerQuestionCardView = null, mModelQuestionBankHandInView = null, mModelQuestionBankHandInAnalysisView = null, mModelQuestionBankWrongQuestionView = null, mModelQuestionBankMyCollectionQuestionView = null, mModelQuestionBankQuestionTypeView = null, mModelQuestionBankQuestionRecordView = null;
    private String mSingleChoiceState = "disable";  //单选状态
    private String mMultiChoiceState = "disable";  //多选状态
    private String mShortAnswerState = "disable";  //简答状态
    private String mMaterialQuestionState = "disable";  //材料题状态
    private String mAllQuestionState = "disable";  //全部题状态
    private String mNotDoneQuestionState = "disable";  //未做题状态
    private String mWrongQuestionState = "disable";  //错题状态
    private String mQuestionType = "AllQuestion";  //做题分类
    private String mTenQuestionState = "disable";  //10道题状态
    private String mTwentyQuestionState = "disable";  //20道题状态
    private String mHundredQuestionState = "disable";  //100道题状态
    private String mQuestionCount = "TenQuestion";  //做题题量
    private String mIbs_id = "";
    private String mIbs_name = "";
    private PopupWindow mPopupWindow, mPointoutPopupWindow;
    private static final long DURATION = 500;
    private static final float START_ALPHA = 0.7f;
    private static final float END_ALPHA = 1f;
    private ModelAnimUtil animUtil, mPointoutAnimUtil;
    private float bgAlpha = 1f, bgPointoutAlpha = 1f;
    private boolean bright = false, bPointoutRight = false;
    private boolean mIsSign = false;  //此题是否被标记
    private String mFontSize = "nomal"; //当前界面的字号大小
    private String mCurrentChapterName = "";//当前选中的章或节的名称
    private Integer mChapter_test_point_id = null;//当前选中的章节考点id
    private int mCurrentIndex = 0;//当前显示的题索引
    private String mCurrentAnswerMode = "test";//当前做题模式
    private boolean mIsCollect = false;  //此题是否被收藏
    private SmartRefreshLayout mSmart_model_questionbank;
    private SmartRefreshLayout mSmart_model_questionbank_sub_detials;
    private static final String TAG = "ModelQuestionBank";
    private SmartRefreshLayout mSmart_model_questionbank_questionrecord;
    private boolean misEmpty;
    private LinearLayout questionbank_sub_details_content;
    private boolean mIsMore = false;

    //练习模式-题库类变量
    private MyQuestionBankExercises.MyQuestionBankExercisesBean mMyQuestionBankExercisesBean = null;
    private Map<Integer,AnswerInfo> mMyQuestionBankExercisesAnswerMap = new HashMap<>();
    private Integer mAnswer_Id = null;
    private List<MyTestPageIssueBean.MyTestPageIssueDataBean> mMyTestPageIssueDataBeans = null;
    private String mTestPageIssueDataTypeScore = "";
    private String mTestPageIssueDataScore = "";
    private Integer mMyTestPageIssueTime = null;
    private List<QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean> mMyFavoriteQuestionDataBeans = null;

    //做题记录翻页
    private int mCurrentPage = 0;
    private int mPageCount = 10;
    private int mSum = 0; //做题记录总数

    private Integer error_num;
    private Integer collection_num;
    private Integer DoRecord_num;
    //存储做题记录表
    private List<QuestionBankAnswerRecordBean.QuestionBankAnswerRecordDataBeanList> questionBankAnswerRecordDataBeanLists = null;

    //是否有未完成的题
    private List<MyQuestionBankGoonBean.MyQuestionBankGoonDataBean> myQuestionBankGoonDataBeans = null;

    private class AnswerInfo {
        String answer = "";
        String result = "";//结果 是对还是错
    }

    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    private Timer mTimer2 = null;
    private TimerTask mTask2 = null;
    private int mTime = 0;

    public static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage) {
        mContext = context;
        mControlMainActivity = content;
        ModelQuestionBank myFragment = new ModelQuestionBank();
        FragmentPage = iFragmentPage;
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage, container, false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        QuestionBankMainShow(mContext);
        //题库的布局刷新控件
        mSmart_model_questionbank = mview.findViewById(R.id.Smart_model_questionbank);
        mSmart_model_questionbank.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mSmart_model_questionbank.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!mIsMore) {
                    //题库界面
                    LinearLayout questionbank_main_content = mModelQuestionBankView.findViewById(R.id.questionbank_main_content);
                    questionbank_main_content.removeAllViews();
                    if (mContext.contains("我的题库")) {//如果是我的题库，显示的题库只有我购买所有课程所包含的项目和科目
                        MyQuestionBankBeanList();
                    } else {
                        getQuestionBankBeanList();
                    }
                } else {
                    if (mSmart_model_questionbank != null){
                        mSmart_model_questionbank.finishRefresh();
                    }
                }
            }
        });
        //让布局向上移来显示软键盘
        mControlMainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return mview;
    }

    //题库界面的主要显示
    public void QuestionBankMainShow(String context) {
        mContext = context;
        if (mview == null) {
            return;
        }
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankView == null) {
            mModelQuestionBankView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankView);
        //题库标题和我的题库标题
        TextView questionbank_main_titletext = mModelQuestionBankView.findViewById(R.id.questionbank_main_titletext);
        if (context.contains("我的题库")) {//如果是我的题库，显示的题库只有我购买所有课程所包含的项目和科目
            questionbank_main_titletext.setText("我的题库");
        } else {
            questionbank_main_titletext.setText("题库");
        }
        mIbs_id = "";
        mCurrentTab = "ChapterExercises";   //练习章
        //题库列表
        ScrollView questionbank_main_content_scroll_view = mModelQuestionBankView.findViewById(R.id.questionbank_main_content_scroll_view);
        questionbank_main_content_scroll_view.scrollTo(0, 0);
        //题库界面
        LinearLayout questionbank_main_content = mModelQuestionBankView.findViewById(R.id.questionbank_main_content);
        questionbank_main_content.removeAllViews();
        //如果没有数据，显示空界面
        LinearLayout questionbank_main_nodata = mModelQuestionBankView.findViewById(R.id.questionbank_main_nodata);
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_main_nodata.getLayoutParams();
        rl.height = 0;
        questionbank_main_nodata.setLayoutParams(rl);
//        if (misEmpty) {
//            questionbank_main_nodata.removeAllViews();
//            View view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
//            questionbank_main_nodata.addView(view2);
//            rl.height = RelativeLayout.LayoutParams.MATCH_PARENT;
//            questionbank_main_nodata.setLayoutParams(rl);
//        } else {
        if (context.contains("我的题库")) {//如果是我的题库，显示的题库只有我购买所有课程所包含的项目和科目
            MyQuestionBankBeanList();
        } else {
            getQuestionBankBeanList();
        }
//        }
    }

    //题库列表传值列表
    public void QuestionBankMainMoreShow(QuestionBankBean.DataBean dataBean,MyQuestionBankBean.MyQuestionBankDataBean dataBean1) {
        if (mview == null) {
            return;
        }
        mIsMore = true;
        mControlMainActivity.onClickQuestionBankMore();
//        HideAllLayout();
//        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
//        if (mModelQuestionBankView == null) {
//            mModelQuestionBankView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank, null);
//        }
//        fragmentquestionbank_main.addView(mModelQuestionBankView);
        LinearLayout questionbank_main_content = mModelQuestionBankView.findViewById(R.id.questionbank_main_content);
        questionbank_main_content.removeAllViews();
        //题库列表的描述
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1, null);
        TextView modelquestionbank_mainquestionbank_id = view.findViewById(R.id.modelquestionbank_mainquestionbank_id);
        //基金法律标题
        TextView modelquestionbank_mainquestionbank_name = view.findViewById(R.id.modelquestionbank_mainquestionbank_name);
        //基金法律message内容
        TextView modelquestionbank_mainquestionbank_describ = view.findViewById(R.id.modelquestionbank_mainquestionbank_describ);
        //更多
        LinearLayout modelquestionbank_mainquestionbank_more = view.findViewById(R.id.modelquestionbank_mainquestionbank_more);
        modelquestionbank_mainquestionbank_more.setVisibility(View.INVISIBLE);
        if (dataBean != null){
            modelquestionbank_mainquestionbank_id.setText(dataBean.getItem_bank_id() + "");
            modelquestionbank_mainquestionbank_name.setText(dataBean.getItem_bank_name());
            modelquestionbank_mainquestionbank_describ.setText(dataBean.getBrief_introduction());
            if (dataBean.getSub_library() != null){
                //子题库赋值
                GridLayout modelquestionbank_mainquestionbank_list = view.findViewById(R.id.modelquestionbank_mainquestionbank_list);
                for (int i = 0; i < dataBean.getSub_library().size() ; i ++){
                    QuestionBankBean.DataBean.SubLibraryBean subLibraryBean = dataBean.getSub_library().get(i);
                    //子标题id和name
                    View view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1_1, null);
                    TextView modelquestionbank_subquestionbank1 = view1.findViewById(R.id.modelquestionbank_subquestionbank1);
                    modelquestionbank_subquestionbank1.setHint(subLibraryBean.getIbs_id() + "");
                    //子题库名称
                    modelquestionbank_subquestionbank1.setText(subLibraryBean.getIbs_name());
                    modelquestionbank_mainquestionbank_list.addView(view1);
                    //        //判断题库是否有做题权限，如果没有不可点击颜色变灰
                    if (dataBean.TF == 1) {
                        modelquestionbank_subquestionbank1.setClickable(true);
                        modelquestionbank_subquestionbank1.setOnClickListener(v -> {
                            mIbs_id = String.valueOf(subLibraryBean.getIbs_id());
                            mIbs_name = subLibraryBean.getIbs_name();
                            //查询此子题库是否有未完成的试题
                            getMyQuestionBankGoon();
                        });
                    } else if (dataBean.TF == 2) {
                        modelquestionbank_subquestionbank1.setTextColor(view1.getResources().getColor(R.color.black999999));
                        modelquestionbank_subquestionbank1.setClickable(true);
                        modelquestionbank_subquestionbank1.setOnClickListener(v -> {
                            //传入相关的id和name
                            Toast.makeText(mControlMainActivity, "您没有此题库的做题权限！", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        } else if (dataBean1 != null){
            modelquestionbank_mainquestionbank_id.setText(dataBean1.getItem_bank_id() + "");
            modelquestionbank_mainquestionbank_name.setText(dataBean1.getItem_bank_name());
            modelquestionbank_mainquestionbank_describ.setText(dataBean1.getBrief_introduction());
            if (dataBean1.getSub_library() != null){
                //子题库赋值
                GridLayout modelquestionbank_mainquestionbank_list = view.findViewById(R.id.modelquestionbank_mainquestionbank_list);
                for (int i = 0; i < dataBean1.getSub_library().size() ; i ++){
                    MyQuestionBankBean.MyQuestionBankSubDataBean subLibraryBean = dataBean1.getSub_library().get(i);
                    //子标题id和name
                    View view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1_1, null);
                    TextView modelquestionbank_subquestionbank1 = view1.findViewById(R.id.modelquestionbank_subquestionbank1);
                    modelquestionbank_subquestionbank1.setHint(subLibraryBean.getIbs_id() + "");
                    //子题库名称
                    modelquestionbank_subquestionbank1.setText(subLibraryBean.getIbs_name());
                    modelquestionbank_mainquestionbank_list.addView(view1);
                    //查询子题库的名称
                    //        //判断题库是否有做题权限，如果没有不可点击颜色变灰
                    //        if (!ibs_name.equals("科目二")) {
                    modelquestionbank_subquestionbank1.setClickable(true);
                    modelquestionbank_subquestionbank1.setOnClickListener(v -> {
                        //问答详情  传值id和name
                        mIbs_id = String.valueOf(subLibraryBean.getIbs_id());
                        mIbs_name = subLibraryBean.getIbs_name();
                        //查询此子题库是否有未完成的试题
                        getMyQuestionBankGoon();
                    });
                    //        } else {
                    //            modelquestionbank_subquestionbank1.setTextColor(view1.getResources().getColor(R.color.black999999));
                    //        }
                }
            }
        }


        questionbank_main_content.addView(view);
    }

    //题库详情------章节练习传值界面
    public void QuestionBankDetailsShow() {
        if (mview == null) {
            return;
        }
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankDetailsView == null) {
            //做题的三种模式
            mModelQuestionBankDetailsView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_sub_detials, null);
            //章节练习
            TextView questionbank_sub_details_tab_chapterexercises = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_chapterexercises);
            //快速做题
            TextView questionbank_sub_details_tab_quicktask = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_quicktask);
            //模拟真题
            TextView questionbank_sub_details_tab_simulated = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_simulated);

            ImageView questionbank_sub_details_buttonmore = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_buttonmore);
            questionbank_sub_details_buttonmore.setClickable(true);
            questionbank_sub_details_buttonmore.setOnClickListener(this);
            questionbank_sub_details_tab_chapterexercises.setOnClickListener(this);
            questionbank_sub_details_tab_quicktask.setOnClickListener(this);
            questionbank_sub_details_tab_simulated.setOnClickListener(this);
            mPopupWindow = new PopupWindow(mControlMainActivity);
            animUtil = new ModelAnimUtil();
            //章节练习的界面刷新
            mSmart_model_questionbank_sub_detials = mModelQuestionBankDetailsView.findViewById(R.id.Smart_model_questionbank_sub_detials);
            mSmart_model_questionbank_sub_detials.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    mSmart_model_questionbank_sub_detials.finishLoadMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //刷新界面
                    mSmart_model_questionbank_sub_detials.finishRefresh();
                    if (mLastTabIndex == 1){
                        //题库详情-章节练习
                        QuestionBankDetailsChapterShow();
                    }
                }
            });

        }
        fragmentquestionbank_main.addView(mModelQuestionBankDetailsView);
        //子题库名称标题
        TextView questionbank_sub_details_titletext = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_titletext);
        questionbank_sub_details_titletext.setText(mIbs_name);
        //默认游标位置在章节练习
        ImageView questionbank_sub_details_cursor1 = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_cursor1);
        int x = width / 6 - mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        questionbank_sub_details_cursor1.setX(x);
        //默认选中的为章节练习
        mLastTabIndex = 1;
        mCurrentTab = "ChapterExercises";
        //章节练习
        TextView questionbank_sub_details_tab_chapterexercises = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_chapterexercises);
        //快速做题
        TextView questionbank_sub_details_tab_quicktask = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_quicktask);
        //模拟真题
        TextView questionbank_sub_details_tab_simulated = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_simulated);
        questionbank_sub_details_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        questionbank_sub_details_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        questionbank_sub_details_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        //如果没有此子题库的做题记录，请将底部功能按钮层隐藏（继续做题）
        LinearLayout questionbank_sub_details_bottomfunction = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_bottomfunction);
        questionbank_sub_details_bottomfunction.setVisibility(View.INVISIBLE);
        if (myQuestionBankGoonDataBeans != null) {
            if (myQuestionBankGoonDataBeans.size() > 0) {
                questionbank_sub_details_bottomfunction.setVisibility(View.VISIBLE);
                TextView goon_time = mModelQuestionBankDetailsView.findViewById(R.id.goon_time);
                MyQuestionBankGoonBean.MyQuestionBankGoonDataBean myQuestionBankGoonDataBean = myQuestionBankGoonDataBeans.get(0);
                Date date = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    date = df.parse(myQuestionBankGoonDataBean.time);
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
                        myQuestionBankGoonDataBean.time = df2.format(date1).toString();
                    }
                }
                goon_time.setText(myQuestionBankGoonDataBean.time);
                String name = "快速做题";
                if (myQuestionBankGoonDataBean.test_paper_name != null && myQuestionBankGoonDataBean.type == 3) {
                    name = myQuestionBankGoonDataBean.test_paper_name;
                }
                if (myQuestionBankGoonDataBean.name != null && myQuestionBankGoonDataBean.type == 2) {
                    name = myQuestionBankGoonDataBean.name;
                }
                TextView goon_name = mModelQuestionBankDetailsView.findViewById(R.id.goon_name);
                goon_name.setText(name);
                TextView goon = mModelQuestionBankDetailsView.findViewById(R.id.goon);
                String finalName = name;
                goon.setOnClickListener(v -> {
                    mAnswer_Id = myQuestionBankGoonDataBean.answer_id;
                    if (myQuestionBankGoonDataBean.type == 1 || myQuestionBankGoonDataBean.type == 2) {
                        getQueryMyQuestionBankContinue(myQuestionBankGoonDataBean.type);
                    } else if (myQuestionBankGoonDataBean.type == 3){
                        getQueryPageContinueTime(finalName);
                    }
                });
            }
        }
        //题库详情-章节练习
        QuestionBankDetailsChapterShow();
    }

    //题库详情-章节练习赋值
    private void QuestionBankDetailsChapterShow() {
        //子题库标题
        TextView questionbank_sub_details_brief = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_brief);
        questionbank_sub_details_brief.setText("自由选择章节知识点各个突破");
        //题库章节考点网络请求赋值
        getMyQuestionBankChapterTest();
    }

    //题库详情-快速做题
    private void QuestionBankDetailsQuickTaskShow() {
        //小标题
        TextView questionbank_sub_details_brief = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_brief);
        questionbank_sub_details_brief.setText("随机抽取一定量的试题 碎片化学习更方便");
        LinearLayout questionbank_sub_details_content = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_content);
        questionbank_sub_details_content.removeAllViews();
        //点击开始
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_sub_detials_quicktask, null);
        questionbank_sub_details_content.addView(view);
        ImageView questionbank_sub_details_quicktask_start = view.findViewById(R.id.questionbank_sub_details_quicktask_start);
        questionbank_sub_details_quicktask_start.setClickable(true);
        questionbank_sub_details_quicktask_start.setOnClickListener(v -> {
//            if (myQuestionBankGoonDataBeans != null){
//                if (myQuestionBankGoonDataBeans.size() != 0) {
//                    Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
            //网络请求开始做题
            getqueryMyQuestionBankQuickIssue();
        });
    }

    //题库详情-模拟真题
    private void QuestionBankDetailsSimulatedShow() {
        //题库标题
        TextView questionbank_sub_details_brief = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_brief);
        questionbank_sub_details_brief.setText("模拟真实考试场景知识点综合评测");
        getQuestionBankTestPaper();
    }

    //显示章 展开下面的节或考点
    private void QuestionBankDetailsChapterExerisesShow(View view, List<MyQuestionBankChapterTestBean.DataBean.JieBean> jie) {
        boolean isFind = false;
        LinearLayout questionbank_sub_details_chapterexercises_content = view.findViewById(R.id.questionbank_sub_details_chapterexercises_content);
        questionbank_sub_details_chapterexercises_content.removeAllViews();
        View questionbank_sub_details_chapterexercises1_line1 = null;
        if (jie != null) {
            for (MyQuestionBankChapterTestBean.DataBean.JieBean jieBean : jie) {
                if (jieBean == null){
                    continue;
                }
                isFind = true;
                //节或者考点的网络请求    节的id或者name
                View view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_sub_detials_chapterexercises1, null);
                //title
                TextView questionbank_sub_details_chapterexercises1_name = view1.findViewById(R.id.questionbank_sub_details_chapterexercises1_name);
                questionbank_sub_details_chapterexercises1_name.setHint(jieBean.chapter_test_point_id + "");
                questionbank_sub_details_chapterexercises1_name.setText(jieBean.name);

                TextView questionbank_sub_details_chapterexercises1_count = view1.findViewById(R.id.questionbank_sub_details_chapterexercises1_count);
                questionbank_sub_details_chapterexercises1_count.setText("共计" + jieBean.num + "道题");
                questionbank_sub_details_chapterexercises1_line1 = view1.findViewById(R.id.questionbank_sub_details_chapterexercises1_line1);
                questionbank_sub_details_chapterexercises1_name.setClickable(true);
                //点击节或考点名称，进行节或考点抽题
                questionbank_sub_details_chapterexercises1_name.setOnClickListener(v -> {
//                    if (myQuestionBankGoonDataBeans != null){
//                        if (myQuestionBankGoonDataBeans.size() != 0) {
//                            Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
                    mCurrentChapterName = jieBean.name;
                    mChapter_test_point_id = jieBean.chapter_test_point_id;
                    getQueryTopicSetting(jieBean.chapter_test_point_id );
                });
                questionbank_sub_details_chapterexercises_content.addView(view1);
            }
        }
        //最后一条线隐藏
        if (questionbank_sub_details_chapterexercises1_line1 != null) {
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_sub_details_chapterexercises1_line1.getLayoutParams();
            rl.topMargin = 0;
            rl.height = 0;
            rl.bottomMargin = 0;
            questionbank_sub_details_chapterexercises1_line1.setLayoutParams(rl);
            questionbank_sub_details_chapterexercises1_line1.setVisibility(View.INVISIBLE);
        }
        ImageView questionbank_sub_details_chapterexercises_arrow_right = view.findViewById(R.id.questionbank_sub_details_chapterexercises_arrow_right);
        ImageView questionbank_sub_details_chapterexercises_arrow_down = view.findViewById(R.id.questionbank_sub_details_chapterexercises_arrow_down);
        ModelExpandView questionbank_sub_details_chapterexercises_expandView = view.findViewById(R.id.questionbank_sub_details_chapterexercises_expandView);
        if (!isFind) {
//            Toast.makeText(mControlMainActivity, "本章下面没有节或考点", Toast.LENGTH_SHORT);
            //收缩隐藏布局
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_sub_details_chapterexercises_expandView.getLayoutParams();
            rl.height = 0;
            questionbank_sub_details_chapterexercises_expandView.setLayoutParams(rl);
            questionbank_sub_details_chapterexercises_expandView.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_right.getLayoutParams();
            ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp6);
            questionbank_sub_details_chapterexercises_arrow_right.setLayoutParams(ll1);
            ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_down.getLayoutParams();
            ll1.width = 0;
            questionbank_sub_details_chapterexercises_arrow_down.setLayoutParams(ll1);
            return;
        }
        questionbank_sub_details_chapterexercises_expandView.expand();
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_sub_details_chapterexercises_expandView.getLayoutParams();
        rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        questionbank_sub_details_chapterexercises_expandView.setLayoutParams(rl);
        questionbank_sub_details_chapterexercises_expandView.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_right.getLayoutParams();
        ll1.width = 0;
        questionbank_sub_details_chapterexercises_arrow_right.setLayoutParams(ll1);
        ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_down.getLayoutParams();
        ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp10);
        questionbank_sub_details_chapterexercises_arrow_down.setLayoutParams(ll1);
    }

    //初始化做题设置界面并展示
    public void QuestionBankQuestionSettingShow( QueryTopicSettingBean.QueryTopicSettingDataBean queryTopicSettingDataBean) {
        if (mview == null) {
            return;
        }
        mControlMainActivity.onClickQuestionBankSetting();
        HideAllLayout();
        //全部题
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankSettingView == null) {
            mModelQuestionBankSettingView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_questionsetting, null);
            //练习模式
            LinearLayout questionbank_questionsetting_questionmode_test = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_questionmode_test);
            //考题模式
            LinearLayout questionbank_questionsetting_questionmode_exam = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_questionmode_exam);
            questionbank_questionsetting_questionmode_test.setClickable(true);
            questionbank_questionsetting_questionmode_test.setOnClickListener(v -> {
                //显示当前的题索引值
                mCurrentIndex = 0;
                //练习模式的详情
                getqueryMyQuestionBankIssue();
            });
            questionbank_questionsetting_questionmode_exam.setOnClickListener(v -> {
                mCurrentIndex = 0;
                //考试模式的详情
                getqueryMyQuestionBankExamIssue();
            });
        }
        fragmentquestionbank_main.addView(mModelQuestionBankSettingView);
        //题库单选题
        TextView questionbank_questionsetting_singlechoice = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_singlechoice);
        //题库多选题
        TextView questionbank_questionsetting_multichoice = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_multichoice);
        //题库简答题
        TextView questionbank_questionsetting_shortanswerchoice = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_shortanswerchoice);
        //题库材料题
        TextView questionbank_questionsetting_materialquestion = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_materialquestion);
        //查询此章节下的题-单选题数量（：1单选题2多选题_3判断题_4简答题_5不定项_6填空题_7材料题___(填空题、判断题、不定项__不要了_)）
        if (queryTopicSettingDataBean.danXuan.zongShu != null) {
            if (queryTopicSettingDataBean.danXuan.zongShu == 0) { //没有单选题
                mSingleChoiceState = "disable";
            } else {
                questionbank_questionsetting_singlechoice.setText("单选题(" + queryTopicSettingDataBean.danXuan.zongShu + ")");
                if (mSingleChoiceState.equals("select")) {
                    //默认将其可选的改为全选
                    questionbank_questionsetting_singlechoice.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    questionbank_questionsetting_singlechoice.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.white));
                } else if (mSingleChoiceState.equals("unselect")) {
                    questionbank_questionsetting_singlechoice.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_singlechoice.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
            }
        }
        if (queryTopicSettingDataBean.duoXuan.zongShu != null) {
            if (queryTopicSettingDataBean.duoXuan.zongShu == 0) { //没有多选题
                mMultiChoiceState = "disable";
            } else {
                questionbank_questionsetting_multichoice.setText("多选题(" + queryTopicSettingDataBean.duoXuan.zongShu + ")");
                //默认将其可选的改为全选
                if (mMultiChoiceState.equals("select")) {
                    questionbank_questionsetting_multichoice.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    questionbank_questionsetting_multichoice.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.white));
                } else if (mMultiChoiceState.equals("unselect")){
                    questionbank_questionsetting_multichoice.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_multichoice.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
            }
        }
        //查询此章节下的题-简答题数量（：1单选题2多选题_3判断题_4简答题_5不定项_6填空题_7材料题___(填空题、判断题、不定项__不要了_)）
        if (queryTopicSettingDataBean.jianDa.zongShu != null) {
            if (queryTopicSettingDataBean.jianDa.zongShu == 0) { //没有简答题
                mShortAnswerState = "disable";
            } else {
                questionbank_questionsetting_shortanswerchoice.setText("简答题(" + queryTopicSettingDataBean.jianDa.zongShu + ")");
                //默认将其可选的改为全选
                if (mShortAnswerState.equals("select")) {
                    questionbank_questionsetting_shortanswerchoice.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    questionbank_questionsetting_shortanswerchoice.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.white));
                } else if (mShortAnswerState.equals("unselect")) {
                    questionbank_questionsetting_shortanswerchoice.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_shortanswerchoice.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
            }
        }
        if (queryTopicSettingDataBean.caiLiao.zongShu != null) {
            if (queryTopicSettingDataBean.caiLiao.zongShu == 0) { //没有材料题
                mMaterialQuestionState = "disable";
            } else {
                questionbank_questionsetting_materialquestion.setText("材料题(" + queryTopicSettingDataBean.caiLiao.zongShu + ")");
                //默认将其可选的改为全选
                if (mMaterialQuestionState.equals("select")) {
                    questionbank_questionsetting_materialquestion.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    questionbank_questionsetting_materialquestion.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.white));
                } else if (mMaterialQuestionState.equals("unselect")) {
                    questionbank_questionsetting_materialquestion.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_materialquestion.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }

            }
        }
        //设置点击事件
        questionbank_questionsetting_singlechoice.setClickable(true);
        questionbank_questionsetting_multichoice.setClickable(true);
        questionbank_questionsetting_shortanswerchoice.setClickable(true);
        questionbank_questionsetting_materialquestion.setClickable(true);
        questionbank_questionsetting_singlechoice.setOnClickListener(v -> {
            if (mSingleChoiceState.equals("select")) {
                //全选改为未选
                mSingleChoiceState = "unselect";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mSingleChoiceState.equals("unselect")) {
                //未选改为全选
                mSingleChoiceState = "select";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mSingleChoiceState.equals("disable")) {
                //不可选
                mSingleChoiceState = "disable";
            }
        });
        questionbank_questionsetting_multichoice.setOnClickListener(v -> {
            if (mMultiChoiceState.equals("select")) {
                //全选改为未选
                mMultiChoiceState = "unselect";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mMultiChoiceState.equals("unselect")) {
                //未选改为全选
                mMultiChoiceState = "select";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mMultiChoiceState.equals("disable")) {
                //不可选
                mMultiChoiceState = "disable";
            }
        });
        questionbank_questionsetting_shortanswerchoice.setOnClickListener(v -> {
            if (mShortAnswerState.equals("select")) {
                //全选改为未选
                mShortAnswerState = "unselect";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mShortAnswerState.equals("unselect")) {
                //未选改为全选
                mShortAnswerState = "select";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mShortAnswerState.equals("disable")) {
                //不可选
                mShortAnswerState = "disable";
            }
        });
        questionbank_questionsetting_materialquestion.setOnClickListener(v -> {
            if (mMaterialQuestionState.equals("select")) {
                //全选改为未选
                mMaterialQuestionState = "unselect";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mMaterialQuestionState.equals("unselect")) {
                //未选改为全选
                mMaterialQuestionState = "select";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mMaterialQuestionState.equals("disable")) {
                //不可选
                mMaterialQuestionState = "disable";
            }
        });
        //判断分类、如果此章/节/考点 有全部题/未做题/错题,将其状态置为可选
        //全部
        TextView questionbank_questionsetting_all = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_all);
        //未做
        TextView questionbank_questionsetting_notdone = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_notdone);
        //错题
        TextView questionbank_questionsetting_wrong = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_wrong);
        int allCount = 0;
        int sumCount = 0;
        if (queryTopicSettingDataBean.caiLiao.zongShu != null){
            sumCount = sumCount + queryTopicSettingDataBean.caiLiao.zongShu;
        }
        if (queryTopicSettingDataBean.jianDa.zongShu != null){
            sumCount = sumCount + queryTopicSettingDataBean.jianDa.zongShu;
        }
        if (queryTopicSettingDataBean.duoXuan.zongShu != null){
            sumCount = sumCount + queryTopicSettingDataBean.duoXuan.zongShu;
        }
        if (queryTopicSettingDataBean.danXuan.zongShu != null) {
            sumCount = sumCount + queryTopicSettingDataBean.danXuan.zongShu;
        }
        int notDoneCount = 0;
        int wrongCount = 0;
        if (mMaterialQuestionState.equals("select")) {
            if (queryTopicSettingDataBean.caiLiao.cuoTi != null) {
                wrongCount = wrongCount + queryTopicSettingDataBean.caiLiao.cuoTi;
            }
            if (queryTopicSettingDataBean.caiLiao.weiZuo != null){
                notDoneCount = notDoneCount + queryTopicSettingDataBean.caiLiao.weiZuo;
            }
            if (queryTopicSettingDataBean.caiLiao.zongShu != null){
                allCount = allCount + queryTopicSettingDataBean.caiLiao.zongShu;
            }
        }
        if (mShortAnswerState.equals("select")){
            if (queryTopicSettingDataBean.jianDa.cuoTi != null){
                wrongCount = wrongCount + queryTopicSettingDataBean.jianDa.cuoTi;
            }
            if (queryTopicSettingDataBean.jianDa.weiZuo != null){
                notDoneCount = notDoneCount + queryTopicSettingDataBean.jianDa.weiZuo;
            }
            if (queryTopicSettingDataBean.jianDa.zongShu != null){
                allCount = allCount + queryTopicSettingDataBean.jianDa.zongShu;
            }
        }
        if (mMultiChoiceState.equals("select")){
            if (queryTopicSettingDataBean.duoXuan.cuoTi != null){
                wrongCount = wrongCount + queryTopicSettingDataBean.duoXuan.cuoTi;
            }
            if (queryTopicSettingDataBean.duoXuan.weiZuo != null){
                notDoneCount = notDoneCount + queryTopicSettingDataBean.duoXuan.weiZuo;
            }
            if (queryTopicSettingDataBean.duoXuan.zongShu != null){
                allCount = allCount + queryTopicSettingDataBean.duoXuan.zongShu;
            }
        }
        if (mSingleChoiceState.equals("select")) {
            if (queryTopicSettingDataBean.danXuan.cuoTi != null) {
                wrongCount = wrongCount + queryTopicSettingDataBean.danXuan.cuoTi;
            }
            if (queryTopicSettingDataBean.danXuan.weiZuo != null) {
                notDoneCount = notDoneCount + queryTopicSettingDataBean.danXuan.weiZuo;
            }
            if (queryTopicSettingDataBean.danXuan.zongShu != null) {
                allCount = allCount + queryTopicSettingDataBean.danXuan.zongShu;
            }
        }

        //查询此章节下的全部题
        if (allCount == 0) { //没有题
            mAllQuestionState = "disable";
            questionbank_questionsetting_all.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect));
            questionbank_questionsetting_all.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.graybcbcbc));
        } else {
            //默认将其可选的改为全选
            if (mQuestionType.equals("AllQuestion")){
                questionbank_questionsetting_all.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
                questionbank_questionsetting_all.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
            } else {
                questionbank_questionsetting_all.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                questionbank_questionsetting_all.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
            }
            mAllQuestionState = "enable";
        }
        questionbank_questionsetting_all.setText("全部题(" + allCount + ")");
        TextView questionbank_questionsetting_count = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_count);
        questionbank_questionsetting_count.setText("共" + sumCount + "道题");
        if (notDoneCount == 0) { //没有题
            mNotDoneQuestionState = "disable";
            questionbank_questionsetting_notdone.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect));
            questionbank_questionsetting_notdone.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.graybcbcbc));
        } else {
            //默认将其可选的改为全选
            mNotDoneQuestionState = "enable";
            if (mQuestionType.equals("NotDoneQuestion")) {
                questionbank_questionsetting_notdone.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
                questionbank_questionsetting_notdone.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
            } else {
                questionbank_questionsetting_notdone.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                questionbank_questionsetting_notdone.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
            }
        }
        questionbank_questionsetting_notdone.setText("未做题(" + notDoneCount + ")");
        if (wrongCount == 0) { //没有题
            mWrongQuestionState = "disable";
            questionbank_questionsetting_wrong.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect));
            questionbank_questionsetting_wrong.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.graybcbcbc));
        } else {
            //默认将其可选的改为全选
            mWrongQuestionState = "enable";
            if (mQuestionType.equals("WrongQuestion")) {
                questionbank_questionsetting_wrong.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
                questionbank_questionsetting_wrong.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
            } else {
                questionbank_questionsetting_wrong.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                questionbank_questionsetting_wrong.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
            }
        }
        questionbank_questionsetting_wrong.setText("错题(" + wrongCount + ")");
        //设置点击事件
        questionbank_questionsetting_all.setClickable(true);
        questionbank_questionsetting_notdone.setClickable(true);
        questionbank_questionsetting_wrong.setClickable(true);
        questionbank_questionsetting_all.setOnClickListener(v -> {
            if (mAllQuestionState.equals("enable")) {
                mQuestionType = "AllQuestion";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mAllQuestionState.equals("disable")) {
                //不可选
            }
        });
        questionbank_questionsetting_notdone.setOnClickListener(v -> {
            if (mNotDoneQuestionState.equals("enable")) {
                mQuestionType = "NotDoneQuestion";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mNotDoneQuestionState.equals("disable")) {
                //不可选
            }
        });
        questionbank_questionsetting_wrong.setOnClickListener(v -> {
            if (mWrongQuestionState.equals("enable")) {
                mQuestionType = "WrongQuestion";
                QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
            } else if (mWrongQuestionState.equals("disable")) {
                //不可选
            }
        });
        //判断题量、如果此章/节/考点 有10道题/20道题/100道题,将其状态置为可选
        TextView questionbank_questionsetting_questioncount1 = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_questioncount1);
        TextView questionbank_questionsetting_questioncount2 = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_questioncount2);
        TextView questionbank_questionsetting_questioncount3 = mModelQuestionBankSettingView.findViewById(R.id.questionbank_questionsetting_questioncount3);
        if (allCount > 0) {
            //默认将其可选的改为全选
            questionbank_questionsetting_questioncount1.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
            questionbank_questionsetting_questioncount1.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
            mTenQuestionState = "enable";
        } else {
            questionbank_questionsetting_questioncount1.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect));
            questionbank_questionsetting_questioncount1.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.graybcbcbc));
            mTenQuestionState = "disable";
        }
        if (allCount > 10) {
            questionbank_questionsetting_questioncount2.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
            questionbank_questionsetting_questioncount2.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
            mTwentyQuestionState = "enable";
        } else {
            questionbank_questionsetting_questioncount2.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect));
            questionbank_questionsetting_questioncount2.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.graybcbcbc));
            mTwentyQuestionState = "disable";
        }
        if (allCount > 20) {
            questionbank_questionsetting_questioncount3.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
            questionbank_questionsetting_questioncount3.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
            mHundredQuestionState = "enable";
        } else {
            questionbank_questionsetting_questioncount3.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect));
            questionbank_questionsetting_questioncount3.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.graybcbcbc));
            mHundredQuestionState = "disable";
        }
        if (mQuestionCount.equals("TenQuestion") && mTenQuestionState.equals("enable")){
            questionbank_questionsetting_questioncount1.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
            questionbank_questionsetting_questioncount1.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
        } else if (mQuestionCount.equals("TwentyQuestion") && mTwentyQuestionState.equals("enable")){
            questionbank_questionsetting_questioncount2.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
            questionbank_questionsetting_questioncount2.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
        } else if (mQuestionCount.equals("HundredQuestion") && mHundredQuestionState.equals("enable")){
            questionbank_questionsetting_questioncount3.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
            questionbank_questionsetting_questioncount3.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
        }
        //设置点击事件
        questionbank_questionsetting_questioncount1.setClickable(true);
        questionbank_questionsetting_questioncount2.setClickable(true);
        questionbank_questionsetting_wrong.setClickable(true);

        questionbank_questionsetting_questioncount1.setOnClickListener(v -> {
            if (mTenQuestionState.equals("enable")) {
                mQuestionCount = "TenQuestion";
                questionbank_questionsetting_questioncount1.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
                questionbank_questionsetting_questioncount1.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
                if (!mTwentyQuestionState.equals("disable")) {
                    questionbank_questionsetting_questioncount2.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_questioncount2.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
                if (!mHundredQuestionState.equals("disable")) {
                    questionbank_questionsetting_questioncount3.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_questioncount3.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
            } else if (mTenQuestionState.equals("disable")) {
                //不可选
            }
        });
        //题量20
        questionbank_questionsetting_questioncount2.setOnClickListener(v -> {
            if (mTwentyQuestionState.equals("enable")) {
                mQuestionCount = "TwentyQuestion";
                questionbank_questionsetting_questioncount2.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
                questionbank_questionsetting_questioncount2.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
                if (!mTenQuestionState.equals("disable")) {
                    questionbank_questionsetting_questioncount1.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_questioncount1.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
                if (!mHundredQuestionState.equals("disable")) {
                    questionbank_questionsetting_questioncount3.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_questioncount3.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
            } else if (mTwentyQuestionState.equals("disable")) {
                //不可选
            }
        });
        questionbank_questionsetting_questioncount3.setOnClickListener(v -> {
            if (mHundredQuestionState.equals("enable")) {
                mQuestionCount = "HundredQuestion";
                questionbank_questionsetting_questioncount3.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_blue1));
                questionbank_questionsetting_questioncount3.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.blue669ef0));
                if (!mTenQuestionState.equals("disable")) {
                    questionbank_questionsetting_questioncount1.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_questioncount1.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
                if (!mTwentyQuestionState.equals("disable")) {
                    questionbank_questionsetting_questioncount2.setBackground(mModelQuestionBankSettingView.getResources().getDrawable(R.drawable.textview_style_rect_black));
                    questionbank_questionsetting_questioncount2.setTextColor(mModelQuestionBankSettingView.getResources().getColor(R.color.collectdefaultcolor3));
                }
            } else if (mHundredQuestionState.equals("disable")) {
                //不可选
            }
        });
    }

    //交卷界面展示
    public void QuestionBankDetailsHandInPaperShow(List<QuestionBankAnswerSheetBean.QuestionBankAnswerSheetDataBean> questionBankAnswerSheetDataBeans) {
        if (mCurrentIndex < 0 || mCurrentIndex >= questionBankAnswerSheetDataBeans.size()) { //不在数组范围直接返回
            return;
        }
        if (mview == null) {
            return;
        }
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        //交题科目5
        mModelQuestionBankHandInView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper, null);
        fragmentquestionbank_main.addView(mModelQuestionBankHandInView);
        ControllerRoundProgressBar coursedetails_handinpaper_accuracyrateprogress = mModelQuestionBankHandInView.findViewById(R.id.coursedetails_handinpaper_accuracyrateprogress);
        if (!mTestPageIssueDataScore.equals("")){
            LinearLayout score_linear = mModelQuestionBankHandInView.findViewById(R.id.score_linear);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) score_linear.getLayoutParams();
            ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            score_linear.setLayoutParams(ll);
            TextView score = mModelQuestionBankHandInView.findViewById(R.id.score);
            score.setText(mTestPageIssueDataScore);
            ll = (LinearLayout.LayoutParams) coursedetails_handinpaper_accuracyrateprogress.getLayoutParams();
            ll.height = 0;
            coursedetails_handinpaper_accuracyrateprogress.setLayoutParams(ll);
        } else {
            LinearLayout score_linear = mModelQuestionBankHandInView.findViewById(R.id.score_linear);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) score_linear.getLayoutParams();
            ll.height = 0;
            score_linear.setLayoutParams(ll);
            ll = (LinearLayout.LayoutParams) coursedetails_handinpaper_accuracyrateprogress.getLayoutParams();
            ll.height = mModelQuestionBankHandInView.getResources().getDimensionPixelSize(R.dimen.dp_137);
            coursedetails_handinpaper_accuracyrateprogress.setLayoutParams(ll);
        }
        TextView questionbank_handinpaper__main_titletext = mModelQuestionBankHandInView.findViewById(R.id.questionbank_handinpaper__main_titletext);
        questionbank_handinpaper__main_titletext.setText(mCurrentChapterName);
        LinearLayout coursedetails_handinpaper_details = mModelQuestionBankHandInView.findViewById(R.id.coursedetails_handinpaper_details);
        //查找题型
        coursedetails_handinpaper_details.removeAllViews();
        //获取题型的种类根据题型的种类进行判断
        View singleView = null;
        View mutilView = null;
        View shortAnswerView = null;
        View materialView = null;
        int rightCount = 0; //正确题数
        for (int i = 0; i < questionBankAnswerSheetDataBeans.size(); i ++) {
            QuestionBankAnswerSheetBean.QuestionBankAnswerSheetDataBean questionBankAnswerSheetDataBean =  questionBankAnswerSheetDataBeans.get(i);
            if (questionBankAnswerSheetDataBean == null){
                continue;
            }
            if (questionBankAnswerSheetDataBean.type == null || questionBankAnswerSheetDataBean.question_id == null){
                continue;
            }
            //  question_type    问题的类型
            if (questionBankAnswerSheetDataBean.type == 1) {
                if (singleView == null) {
                    //单选题界面标题
                    singleView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper1, null);
                    coursedetails_handinpaper_details.addView(singleView);
                }
                GridLayout coursedetails_handinpaper1_questionnumber = singleView.findViewById(R.id.coursedetails_handinpaper1_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper2, null);
                coursedetails_handinpaper1_questionnumber.addView(view);
                TextView questionbank_handin2_select = view.findViewById(R.id.questionbank_handin2_select);
                questionbank_handin2_select.setText("" + (i + 1));
//                if (questionBankAnswerSheetDataBean.tf_marked != null){
//                    if (questionBankAnswerSheetDataBean.tf_marked == 1){//标记此题
//                        ImageView questionbank_handin2_sign = view.findViewById(R.id.questionbank_handin2_sign);
//                        questionbank_handin2_sign.setVisibility(View.VISIBLE);
//                    }
//                }
                if (questionBankAnswerSheetDataBean.TF != null) {
                    if (questionBankAnswerSheetDataBean.mydaan == null){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    } else if (questionBankAnswerSheetDataBean.mydaan.equals(" ")){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    }
                    if (questionBankAnswerSheetDataBean.TF == 1) { //此题为当前正在答的题,改变题的颜色
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                        rightCount = rightCount + 1;
                    }else if (questionBankAnswerSheetDataBean.TF ==2 &&!questionBankAnswerSheetDataBean.mydaan.equals("")){
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_red));
                    }

                }
                int finalCount = i;
                questionbank_handin2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeHandInShow();
                });
            } else if (questionBankAnswerSheetDataBean.type == 2) {
                if (mutilView == null) {
                    mutilView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper1, null);
                    coursedetails_handinpaper_details.addView(mutilView);
                    //单选框
                    TextView coursedetails_handinpaper1_questiontype = mutilView.findViewById(R.id.coursedetails_handinpaper1_questiontype);
                    coursedetails_handinpaper1_questiontype.setText("多选题");
                }
                GridLayout coursedetails_handinpaper1_questionnumber = mutilView.findViewById(R.id.coursedetails_handinpaper1_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper2, null);
                coursedetails_handinpaper1_questionnumber.addView(view);
                TextView questionbank_handin2_select = view.findViewById(R.id.questionbank_handin2_select);
                questionbank_handin2_select.setText("" + (i + 1));
//                if (questionBankAnswerSheetDataBean.tf_marked != null){
//                    if (questionBankAnswerSheetDataBean.tf_marked == 1){//标记此题
//                        ImageView questionbank_handin2_sign = view.findViewById(R.id.questionbank_handin2_sign);
//                        questionbank_handin2_sign.setVisibility(View.VISIBLE);
//                    }
//                }

                if (questionBankAnswerSheetDataBean.TF != null) {
                    if (questionBankAnswerSheetDataBean.mydaan == null){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    } else if (questionBankAnswerSheetDataBean.mydaan.equals(" ")){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    }
                    if (questionBankAnswerSheetDataBean.TF == 1) { //此题为当前正在答的题,改变题的颜色
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                        rightCount = rightCount + 1;
                    }else if (questionBankAnswerSheetDataBean.TF == 2&&!questionBankAnswerSheetDataBean.mydaan.equals("")){
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_red));
                    }
                }
                int finalCount = i;
                questionbank_handin2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeHandInShow();
                });
            } else if (questionBankAnswerSheetDataBean.type == 4) {
                if (shortAnswerView == null) {
                    shortAnswerView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper1, null);
                    coursedetails_handinpaper_details.addView(shortAnswerView);
                    TextView coursedetails_handinpaper1_questiontype = shortAnswerView.findViewById(R.id.coursedetails_handinpaper1_questiontype);
                    coursedetails_handinpaper1_questiontype.setText("简答题");
                }
                GridLayout coursedetails_handinpaper1_questionnumber = shortAnswerView.findViewById(R.id.coursedetails_handinpaper1_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper2, null);
                coursedetails_handinpaper1_questionnumber.addView(view);
                TextView questionbank_handin2_select = view.findViewById(R.id.questionbank_handin2_select);
                questionbank_handin2_select.setText("" + (i + 1));
//                if (questionBankAnswerSheetDataBean.tf_marked != null){
//                    if (questionBankAnswerSheetDataBean.tf_marked == 1){//标记此题
//                        ImageView questionbank_handin2_sign = view.findViewById(R.id.questionbank_handin2_sign);
//                        questionbank_handin2_sign.setVisibility(View.VISIBLE);
//                    }
//                }

                if (questionBankAnswerSheetDataBean.TF != null) {
                    if (questionBankAnswerSheetDataBean.mydaan==null){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    } else if (questionBankAnswerSheetDataBean.mydaan.equals(" ")){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    }
                    if (questionBankAnswerSheetDataBean.TF == 1) { //此题为当前正在答的题,改变题的颜色
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                        rightCount = rightCount + 1;
                    }else if (questionBankAnswerSheetDataBean.TF==2&&!questionBankAnswerSheetDataBean.mydaan.equals("")){
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_red));
                    }
                }
                int finalCount = i;
                questionbank_handin2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeHandInShow();
                });
            } else if (questionBankAnswerSheetDataBean.type == 7) {
                if (materialView == null) {
                    materialView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper1, null);
                    coursedetails_handinpaper_details.addView(materialView);
                    TextView coursedetails_handinpaper1_questiontype = materialView.findViewById(R.id.coursedetails_handinpaper1_questiontype);
                    coursedetails_handinpaper1_questiontype.setText("材料题");
                }
                GridLayout coursedetails_handinpaper1_questionnumber = materialView.findViewById(R.id.coursedetails_handinpaper1_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handinpaper2, null);
                coursedetails_handinpaper1_questionnumber.addView(view);
                TextView questionbank_handin2_select = view.findViewById(R.id.questionbank_handin2_select);
                questionbank_handin2_select.setText("" + (i + 1));
//                if (questionBankAnswerSheetDataBean.tf_marked != null){
//                    if (questionBankAnswerSheetDataBean.tf_marked == 1){//标记此题
//                        ImageView questionbank_handin2_sign = view.findViewById(R.id.questionbank_handin2_sign);
//                        questionbank_handin2_sign.setVisibility(View.VISIBLE);
//                    }
//                }

                if (questionBankAnswerSheetDataBean.TF != null) {
                    if (questionBankAnswerSheetDataBean.mydaan == null){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    } else if (questionBankAnswerSheetDataBean.mydaan.equals(" ")){
                        questionBankAnswerSheetDataBean.mydaan = "";
                    }
                    if (questionBankAnswerSheetDataBean.TF == 1) { //此题为当前正在答的题,改变题的颜色
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                        rightCount = rightCount + 1;
                    }else if (questionBankAnswerSheetDataBean.TF == 2 &&!questionBankAnswerSheetDataBean.mydaan.equals("")){
                        questionbank_handin2_select.setTextColor(view.getResources().getColor(R.color.white));
                        questionbank_handin2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_red));
                    }
                }
                int finalCount = i;
                questionbank_handin2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeHandInShow();
                });
            }
        }

        //进度
        coursedetails_handinpaper_accuracyrateprogress.setProgress(rightCount);
        coursedetails_handinpaper_accuracyrateprogress.setMax(questionBankAnswerSheetDataBeans.size());
    }

    //题库详情展示（区分展示那种模式下的题库详情）
    public void QuestionBankDetailsQuestionModeShow() {
        if (mCurrentAnswerMode.equals("test")) {
            QuestionBankDetailsQuestionModeTestShow();
        } else if (mCurrentAnswerMode.equals("exam")) {
            QuestionBankDetailsQuestionModeExamShow();
        } else if (mCurrentAnswerMode.equals("handin")) {
            QuestionBankDetailsQuestionModeHandInShow();
        } else if (mCurrentAnswerMode.equals("wrong")) {
            QuestionBankDetailsQuestionModeWrongQuestionShow();
        } else if (mCurrentAnswerMode.equals("collection")) {
            QuestionBankDetailsQuestionModeMyCollectionQuestionShow();
        } else if (mCurrentAnswerMode.equals("requestionrecord")) {
            QuestionBankDetailsQuestionModeQuestionRecordShow();
        } else if (mCurrentAnswerMode.equals("testpaper")){
            QuestionBankDetailsQuestionModeTestPaperShow();
        }
    }

    //答题-练习模式界面展示
    private void QuestionBankDetailsQuestionModeTestShow() {
        if (mview == null) {
            return;
        }
        mCurrentAnswerMode = "test";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankAnswerPaperView == null) {
            //练习模式分析内容
            mModelQuestionBankAnswerPaperView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankAnswerPaperView);
        //此题所属章节名称（分析内容标题）
        TextView questionbank_answerpaper_questiontitle = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questiontitle);
        questionbank_answerpaper_questiontitle.setText(mCurrentChapterName);
        //倒计时时间
        TextView questionbank_answerpaper_countdowntimetext = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_countdowntimetext);
        //点击标记
        ImageView questionbank_answerpaper_sign = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_sign);
        questionbank_answerpaper_sign.setOnClickListener(v -> {
            if (mIsSign) {
                getMyQuestionBankflag(questionbank_answerpaper_sign,"","2","");
            } else {
                //网络请求进行标记
                getMyQuestionBankflag(questionbank_answerpaper_sign,"","1","");
            }
        });
        boolean isCailiao = QuestionViewAdd(mMyQuestionBankExercisesBean);
        //点击交卷
        LinearLayout questionbank_answerpaper_commit = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_commit);
//        String finalQuestion_id_group1 = question_id_group;
        questionbank_answerpaper_commit.setOnClickListener(v -> {
            //判断啊当前是否删除
            View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view1.findViewById(R.id.tip);
            tip.setText("交卷");
            TextView dialog_content = view1.findViewById(R.id.dialog_content);
            dialog_content.setText("确认交卷吗？");
            TextView button_cancel = view1.findViewById(R.id.button_cancel);
            button_cancel.setText("再检查一下");
            button_cancel.setOnClickListener(View -> {
                mMyDialog.cancel();
            });
            //点击   网络请求
            TextView button_sure = view1.findViewById(R.id.button_sure);
            button_sure.setText("交卷");
            button_sure.setOnClickListener(View -> {
                //暂停计时器
                if (mTimer2 != null) {
                    mTimer2.cancel();
                }
                if (mTask2 != null) {
                    mTask2.cancel();
                }
                //显示交卷界面
//                QuestionBankDetailsHandInPaperShow(finalQuestion_id_group1);
                getQuestionBankHandInBean(1,mTime);
                mMyDialog.cancel();
            });
        });
        //点击暂停   网络请求暂停
        ImageView questionbank_answerpaper_pause = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_pause);
        questionbank_answerpaper_pause.setOnClickListener(v -> {

            getQuestionBankHandInBean(2,mTime);
            View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure, null);
            ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
            mMyDialog.setCancelable(false);
            mMyDialog.show();
            TextView tip = view1.findViewById(R.id.tip);
            tip.setText("暂停");
            TextView dialog_content = view1.findViewById(R.id.dialog_content);
            dialog_content.setText("哎呦，休息时间到啦");
            TextView button_sure = view1.findViewById(R.id.button_sure);
            button_sure.setText("继续做题");

            button_sure.setOnClickListener(View -> {    //点击继续做题
                mMyDialog.cancel();
                //重新打开计时器
                mTimer2 = new Timer();
                mTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        mTime = mTime + 1;
                        runOnUiThread(() ->
                                questionbank_answerpaper_countdowntimetext.setText(getStringTime(mTime)));
                    }
                };
                mTimer2.schedule(mTask2, 0, 1000);
            });
            //暂停计时器
            if (mTimer2 != null) {
                mTimer2.cancel();
            }
            if (mTask2 != null) {
                mTask2.cancel();
            }
        });
        //        上一题
        LinearLayout button_questionbank_beforquestion = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_beforquestion);
        button_questionbank_beforquestion.setOnClickListener(v -> {
            TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
            if (questionbank_answerpaper_questioncount.getText().toString().equals("1") || questionbank_answerpaper_questioncount.getText().toString().equals("0")) {
                Toast.makeText(mControlMainActivity, "前面没有题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到上一道题
                mCurrentIndex = mCurrentIndex - 1;
                boolean isCailiao1 = false;
                if (mMyQuestionBankExercisesBean != null) {
                    isCailiao1 = QuestionViewAdd(mMyQuestionBankExercisesBean);
                }
                if (isCailiao1){
                    TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
                    coursedetails_answerpaper_analysisbutton.setVisibility(View.INVISIBLE);
                    LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                    coursedetails_answerpaper_analysis.removeAllViews();
                } else {
                    //打开解析按钮
                    TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
                    coursedetails_answerpaper_analysisbutton.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
                    lLayoutParams.height = mModelQuestionBankAnswerPaperView.getResources().getDimensionPixelSize(R.dimen.dp_37);
                    lLayoutParams.topMargin = mModelQuestionBankAnswerPaperView.getResources().getDimensionPixelSize(R.dimen.dp_70);
                    coursedetails_answerpaper_analysisbutton.setLayoutParams(lLayoutParams);
                    LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                    coursedetails_answerpaper_analysis.removeAllViews();
                }
            }
        });
        //         下一题
        LinearLayout button_questionbank_nextquestion = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_nextquestion);
        button_questionbank_nextquestion.setOnClickListener(V -> {
            if (mMyQuestionBankExercisesBean != null) {
                int questionSum = 0;
                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                }
                TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
                if (questionbank_answerpaper_questioncount.getText().toString().equals("" + questionSum)) {
                    Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
                } else { //跳到下一道题
                    mCurrentIndex = mCurrentIndex + 1;
                    boolean isCailiao1 = false;
                    if (mMyQuestionBankExercisesBean != null) {
                        isCailiao1 = QuestionViewAdd(mMyQuestionBankExercisesBean);
                    }
                    if (isCailiao1){
                        TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
                        coursedetails_answerpaper_analysisbutton.setVisibility(View.INVISIBLE);
                        LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                        coursedetails_answerpaper_analysis.removeAllViews();
                    } else {
                        //打开解析按钮
                        TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
                        coursedetails_answerpaper_analysisbutton.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
                        lLayoutParams.height = mModelQuestionBankAnswerPaperView.getResources().getDimensionPixelSize(R.dimen.dp_37);
                        lLayoutParams.topMargin = mModelQuestionBankAnswerPaperView.getResources().getDimensionPixelSize(R.dimen.dp_70);
                        coursedetails_answerpaper_analysisbutton.setLayoutParams(lLayoutParams);
                        LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                        coursedetails_answerpaper_analysis.removeAllViews();
                    }
                }
            }
        });
        //答题卡
        LinearLayout button_questionbank_answerquestioncard = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_answerquestioncard);
        //添加答题卡
        button_questionbank_answerquestioncard.setOnClickListener(v ->{
            mTestPageIssueDataScore = "";
            getQuestionBankAnswerSheet(1);
        });
        //点击字号
        ImageView questionbank_answerpaper_fontsize = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_fontsize);
        questionbank_answerpaper_fontsize.setOnClickListener(v -> {
            ShowPopFontSize(questionbank_answerpaper_fontsize);
        });
        //计时器
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimer2 = null;
        }
        if (mTask2 != null) {
            mTask2.cancel();
            mTask2 = null;
        }
        mTimer2 = new Timer();
        mTask2 = new TimerTask() {
            @Override
            public void run() {
                mTime = mTime + 1;   //显示倒计时的时间
                runOnUiThread(() -> questionbank_answerpaper_countdowntimetext.setText(getStringTime(mTime)));
            }
        };
        mTimer2.schedule(mTask2, 0, 1000);
        //查看解析
        if (isCailiao){ //判断是否为材料题，如果是材料题不显示查看解析按钮
            TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
            coursedetails_answerpaper_analysisbutton.setVisibility(View.INVISIBLE);
        } else {
            TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
            coursedetails_answerpaper_analysisbutton.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
            lLayoutParams.height = mModelQuestionBankAnswerPaperView.getResources().getDimensionPixelSize(R.dimen.dp_37);
            lLayoutParams.topMargin = mModelQuestionBankAnswerPaperView.getResources().getDimensionPixelSize(R.dimen.dp_70);
            coursedetails_answerpaper_analysisbutton.setLayoutParams(lLayoutParams);
            //解析下面的内容
            LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
            coursedetails_answerpaper_analysis.removeAllViews();
            coursedetails_answerpaper_analysisbutton.setOnClickListener(v -> {
                int questionSize = 0;
                if (mMyQuestionBankExercisesBean == null) {
                    Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
                    return;
                }
                MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = null;
                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                    questionSize = questionSize + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    if (mCurrentIndex < questionSize) {
                        myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex);
                    }
                }
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                    questionSize = questionSize + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                    if (myQuestionBankExercisesDataBean == null) {
                        if (mCurrentIndex < questionSize) {
                            int count = 0;
                            if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                            }
                            if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                                myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count);
                            }
                        }
                    }
                }
                if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                    questionSize = questionSize + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                    if (mCurrentIndex < questionSize && myQuestionBankExercisesDataBean == null) {
                        int count = mCurrentIndex;
                        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                            count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                        }
                        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                            count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                        }
                        if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                            myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count);
                        }
                    }
                }
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                    questionSize = questionSize + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                    if (mCurrentIndex < questionSize && myQuestionBankExercisesDataBean == null) {
                        int count = mCurrentIndex;
                        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                            count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                        }
                        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                            count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                        }
                        if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                            count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                        }
                        if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                            myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count);
                        }
                    }
                }
                if (myQuestionBankExercisesDataBean == null) {
                    Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myQuestionBankExercisesDataBean.question_analysis == null){
                    myQuestionBankExercisesDataBean.question_analysis = "";
                }
                if (myQuestionBankExercisesDataBean.question_analysis == null || myQuestionBankExercisesDataBean.question_type == null) {
                    Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (myQuestionBankExercisesDataBean.question_analysis.equals("")) {
//                    Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
                ll.height = 0;
                ll.topMargin = 0;
                coursedetails_answerpaper_analysisbutton.setLayoutParams(ll);
                if (myQuestionBankExercisesDataBean.question_type == 1 || myQuestionBankExercisesDataBean.question_type == 2) {//单选题或多选题
                    if (myQuestionBankExercisesDataBean.optionanswer == null) {
                        myQuestionBankExercisesDataBean.optionanswer = "";
                    }
                    //个人答案
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
                    coursedetails_answerpaper_analysis.addView(view);
                    //修改内容为正确答案
                    String[] optionanswerS = myQuestionBankExercisesDataBean.optionanswer.split("#EDU;");
                    String currentAnswer = "";
                    if (optionanswerS != null) {
                        for (int i = 0; i < optionanswerS.length; i++) {
                            String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                            if (optionanswerS1.length != 3) {
                                break;
                            }
                            if (optionanswerS1[1].equals("是")) {
                                currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                            }
                        }
                    }
//                    if (currentAnswer.equals("")) {
//                        Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    //正确答案
                    TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
                    questionbank_analysis1_rightAnswer.setText(currentAnswer);
//             //修改内容为此题的解析
                    TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
                    if (myQuestionBankExercisesDataBean.question_analysis == null) {
                        myQuestionBankExercisesDataBean.question_analysis = "";
                    }
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(myQuestionBankExercisesDataBean.question_analysis);
                    //个人答案
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
                    TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                        } else {
                            questionbank_analysis1_yourAnswer.setText("");
                        }
                    }
                    //字体大小的设置
                    if (mFontSize.equals("nomal")) {
                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                } else if (myQuestionBankExercisesDataBean.question_type == 4) {//简答题
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
                    coursedetails_answerpaper_analysis.addView(view);
                    //修改内容为正确答案
                    TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(myQuestionBankExercisesDataBean.optionanswer);
                    //修改内容为此题的解析
                    TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
                    if (myQuestionBankExercisesDataBean.question_analysis == null) {
                        myQuestionBankExercisesDataBean.question_analysis = "";
                    }
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(myQuestionBankExercisesDataBean.question_analysis);
                    //修改内容为您的答案
                    TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
//                EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            questionbank_analysis2_yourAnswer.setText(answerInfo.answer);
                        } else {
                            questionbank_analysis2_yourAnswer.setText("");
                        }
                    }
                    if (mFontSize.equals("nomal")) {
                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                }
            });
        }
    }

    //错题本-选完单选直接显示解析
    private void ModeWrongQuestionFindAnalysis( QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean){
        TextView coursedetails_wrongquestion_analysisbutton = mModelQuestionBankWrongQuestionView.findViewById(R.id.coursedetails_wrongquestion_analysisbutton);
        LinearLayout coursedetails_wrongquestion_analysis = mModelQuestionBankWrongQuestionView.findViewById(R.id.coursedetails_wrongquestion_analysis);
        coursedetails_wrongquestion_analysis.removeAllViews();
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_wrongquestion_analysisbutton.getLayoutParams();
        ll.height = 0;
        ll.topMargin = 0;
        coursedetails_wrongquestion_analysisbutton.setLayoutParams(ll);
        if (questionBankMyFavoriteQuestionDataBean.question_type == 1 || questionBankMyFavoriteQuestionDataBean.question_type == 2) {//单选题或多选题
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
            coursedetails_wrongquestion_analysis.addView(view);
            //修改内容为正确答案
            String[] optionanswerS = questionBankMyFavoriteQuestionDataBean.optionanswer.split("#EDU;");
            if (optionanswerS == null) {

            }
            String currentAnswer = "";
            for (int i = 0; i < optionanswerS.length; i++) {
                String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                if (optionanswerS1.length != 3) {
                    break;
                }
                if (optionanswerS1[1].equals("是")) {
                    currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                }
            }
            TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
            questionbank_analysis1_rightAnswer.setText(currentAnswer);
//                    /修改内容为此题的解析
            TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
            if (questionBankMyFavoriteQuestionDataBean.question_analysis == null) {
                questionBankMyFavoriteQuestionDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
            if (mMyQuestionBankExercisesAnswerMap != null) {
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                if (answerInfo != null) {
                    if (answerInfo.answer != null) {
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_yourAnswer).setHtmlWithPic(answerInfo.answer);
                    }
                }
            }
            if (mFontSize.equals("nomal")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {//简答题
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
            coursedetails_wrongquestion_analysis.addView(view);
            //修改内容为正确答案
            TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
            if (questionBankMyFavoriteQuestionDataBean.optionanswer == null) {
                questionBankMyFavoriteQuestionDataBean.optionanswer = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.optionanswer);
            //修改内容为此题的解析
            TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
            if (questionBankMyFavoriteQuestionDataBean.question_analysis == null) {
                questionBankMyFavoriteQuestionDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
            if (mMyQuestionBankExercisesAnswerMap != null) {
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                if (answerInfo != null) {
                    if (answerInfo.answer != null) {
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo.answer);
                    }
                }
            }
            if (mFontSize.equals("nomal")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        }
    }

    //练习模式-选完单选直接显示解析
    private void ModeTestFindAnalysis(){
        if (mCurrentAnswerMode.equals("exam")){
            return;
        }
        TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
        LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
        coursedetails_answerpaper_analysis.removeAllViews();
        int questionSize = 0;
        if (mMyQuestionBankExercisesBean == null) {
            Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
            return;
        }
        MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = null;
        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
            questionSize = questionSize + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
            if (mCurrentIndex < questionSize) {
                myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex);
            }
        }
        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
            questionSize = questionSize + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
            if (myQuestionBankExercisesDataBean == null) {
                if (mCurrentIndex < questionSize) {
                    int count = 0;
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                        myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count);
                    }
                }
            }
        }
        if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
            questionSize = questionSize + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
            if (mCurrentIndex < questionSize && myQuestionBankExercisesDataBean == null) {
                int count = mCurrentIndex;
                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                    count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                    count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                }
                if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                    myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count);
                }
            }
        }
        if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
            questionSize = questionSize + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
            if (mCurrentIndex < questionSize && myQuestionBankExercisesDataBean == null) {
                int count = mCurrentIndex;
                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                    count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                    count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                    count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                }
                if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                    myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count);
                }
            }
        }
        if (myQuestionBankExercisesDataBean == null) {
            Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (myQuestionBankExercisesDataBean.question_analysis == null){
            myQuestionBankExercisesDataBean.question_analysis = "";
        }
        if (myQuestionBankExercisesDataBean.question_analysis == null || myQuestionBankExercisesDataBean.question_type == null) {
            Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
            return;
        }
//            if (myQuestionBankExercisesDataBean.question_analysis.equals("")) {
//                Toast.makeText(mControlMainActivity, "此题暂无解析！", Toast.LENGTH_SHORT).show();
//                return;
//            }
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
        ll.height = 0;
        ll.topMargin = 0;
        coursedetails_answerpaper_analysisbutton.setLayoutParams(ll);
        if (myQuestionBankExercisesDataBean.question_type == 1 || myQuestionBankExercisesDataBean.question_type == 2) {//单选题或多选题
            if (myQuestionBankExercisesDataBean.optionanswer == null) {
                myQuestionBankExercisesDataBean.optionanswer = "";
            }
            //个人答案
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
            coursedetails_answerpaper_analysis.addView(view);
            //修改内容为正确答案
            String[] optionanswerS = myQuestionBankExercisesDataBean.optionanswer.split("#EDU;");
            String currentAnswer = "";
            if (optionanswerS != null) {
                for (int i = 0; i < optionanswerS.length; i++) {
                    String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                    if (optionanswerS1.length != 3) {
                        break;
                    }
                    if (optionanswerS1[1].equals("是")) {
                        currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                    }
                }
            }
            //正确答案
            TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
            questionbank_analysis1_rightAnswer.setText(currentAnswer);
//             //修改内容为此题的解析
            TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
            if (myQuestionBankExercisesDataBean.question_analysis == null) {
                myQuestionBankExercisesDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(myQuestionBankExercisesDataBean.question_analysis);
            //个人答案
            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
            TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
            if (answerInfo != null) {
                if (answerInfo.answer != null) {
                    questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                } else {
                    questionbank_analysis1_yourAnswer.setText("");
                }
            }
            //字体大小的设置
            if (mFontSize.equals("nomal")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        } else if (myQuestionBankExercisesDataBean.question_type == 4) {//简答题
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
            coursedetails_answerpaper_analysis.addView(view);
            //修改内容为正确答案
            TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(myQuestionBankExercisesDataBean.optionanswer);
            //修改内容为此题的解析
            TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
            if (myQuestionBankExercisesDataBean.question_analysis == null) {
                myQuestionBankExercisesDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(myQuestionBankExercisesDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
//                EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
            if (answerInfo != null) {
                if (answerInfo.answer != null) {
                    questionbank_analysis2_yourAnswer.setText(answerInfo.answer);
                } else {
                    questionbank_analysis2_yourAnswer.setText("");
                }
            }
            if (mFontSize.equals("nomal")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        }
    }
    //答题-考试模式界面展示
    private void QuestionBankDetailsQuestionModeExamShow() {
        if (mview == null) {
            return;
        }
        mCurrentAnswerMode = "exam";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankAnswerPaperView == null) {
            mModelQuestionBankAnswerPaperView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankAnswerPaperView);
        //此题所属章节名称
        TextView questionbank_answerpaper_questiontitle = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questiontitle);
        questionbank_answerpaper_questiontitle.setText(mCurrentChapterName);
        //倒计时
        TextView questionbank_answerpaper_countdowntimetext = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_countdowntimetext);

        //点击标记
        ImageView questionbank_answerpaper_sign = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_sign);
        questionbank_answerpaper_sign.setOnClickListener(v -> {
            if (mIsSign) {
                getMyQuestionBankflag(questionbank_answerpaper_sign,"","2","");
            } else {
                //标记
                getMyQuestionBankflag(questionbank_answerpaper_sign,"","1","");
            }
        });
        //添加题,//默认显示第一题
        QuestionViewAdd(mMyQuestionBankExercisesBean);
        //点击交卷
        LinearLayout questionbank_answerpaper_commit = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_commit);
        questionbank_answerpaper_commit.setOnClickListener(v -> {
            View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view1.findViewById(R.id.tip);
            tip.setText("交卷");
            TextView dialog_content = view1.findViewById(R.id.dialog_content);
            dialog_content.setText("确认交卷吗？");
            TextView button_cancel = view1.findViewById(R.id.button_cancel);
            button_cancel.setText("再检查一下");
            button_cancel.setOnClickListener(View -> {
                mMyDialog.cancel();
            });
            TextView button_sure = view1.findViewById(R.id.button_sure);
            button_sure.setText("交卷");
            button_sure.setOnClickListener(View -> {
                //暂停计时器
                if (mTimer2 != null) {
                    mTimer2.cancel();
                }
                if (mTask2 != null) {
                    mTask2.cancel();
                }
                //显示交卷界面
//                QuestionBankDetailsHandInPaperShow(mMyQuestionBankExercisesBean);
                getQuestionBankHandInBean(1,mTime);
                mMyDialog.cancel();
            });
        });
        //点击暂停
        ImageView questionbank_answerpaper_pause = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_pause);
        questionbank_answerpaper_pause.setOnClickListener(v -> {
            getQuestionBankHandInBean(2,mTime);
            View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure, null);
            ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
            mMyDialog.setCancelable(false);
            mMyDialog.show();
            TextView tip = view1.findViewById(R.id.tip);
            tip.setText("暂停");
            TextView dialog_content = view1.findViewById(R.id.dialog_content);
            dialog_content.setText("哎呦，休息时间到啦");
            TextView button_sure = view1.findViewById(R.id.button_sure);
            //点击继续做题
            button_sure.setText("继续做题");
            button_sure.setOnClickListener(View -> {
                mMyDialog.cancel();
                //重新打开计时器
                mTimer2 = new Timer();
                mTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        mTime = mTime + 1;
                        //定时器  判断当前的题库是否继做题
                        runOnUiThread(() ->
                                //显示倒计时的时间
                                questionbank_answerpaper_countdowntimetext.setText(getStringTime(mTime)));
                    }
                };
                mTimer2.schedule(mTask2, 0, 1000);
            });
            //暂停计时器
            if (mTimer2 != null) {
                mTimer2.cancel();
            }
            if (mTask2 != null) {
                mTask2.cancel();
            }
        });
        //上一题
        LinearLayout button_questionbank_beforquestion = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_beforquestion);
        button_questionbank_beforquestion.setOnClickListener(v -> {
            TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
            if (questionbank_answerpaper_questioncount.getText().toString().equals("1") || questionbank_answerpaper_questioncount.getText().toString().equals("0")) {
                Toast.makeText(mControlMainActivity, "前面没有题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到上一道题
                mCurrentIndex = mCurrentIndex - 1;
                if (mMyQuestionBankExercisesBean != null) {
                    QuestionViewAdd(mMyQuestionBankExercisesBean);
                }
                LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                coursedetails_answerpaper_analysis.removeAllViews();
            }
        });
        //下一题
        LinearLayout button_questionbank_nextquestion = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_nextquestion);
        button_questionbank_nextquestion.setOnClickListener(V -> {
            if (mMyQuestionBankExercisesBean != null) {
                int questionSum = 0;
                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                }
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                    questionSum = questionSum + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                }
                TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
                if (questionbank_answerpaper_questioncount.getText().toString().equals("" + questionSum)) {
                    Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
                } else { //跳到下一道题
                    mCurrentIndex = mCurrentIndex + 1;
                    if (mMyQuestionBankExercisesBean != null) {
                        QuestionViewAdd(mMyQuestionBankExercisesBean);
                    }
                    LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                    coursedetails_answerpaper_analysis.removeAllViews();
                }
            }
        });
        //答题卡
        LinearLayout button_questionbank_answerquestioncard = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_answerquestioncard);
        button_questionbank_answerquestioncard.setOnClickListener(v ->{
            mTestPageIssueDataScore = "";
            getQuestionBankAnswerSheet(1);
        });
        //点击字号
        ImageView questionbank_answerpaper_fontsize = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_fontsize);
        questionbank_answerpaper_fontsize.setOnClickListener(v -> {
            ShowPopFontSize(questionbank_answerpaper_fontsize);
        });
        //计时器
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimer2 = null;
        }
        if (mTask2 != null) {
            mTask2.cancel();
            mTask2 = null;
        }
        mTimer2 = new Timer();
        mTask2 = new TimerTask() {
            @Override
            public void run() {
                mTime = mTime + 1;
                runOnUiThread(() -> questionbank_answerpaper_countdowntimetext.setText(getStringTime(mTime)));
            }
        };
        mTimer2.schedule(mTask2, 0, 1000);

        TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
        LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
        lLayoutParams.height = 0;
        lLayoutParams.topMargin = 0;
        coursedetails_answerpaper_analysisbutton.setLayoutParams(lLayoutParams);
    }

    //答题-试卷答题界面展示
    private void QuestionBankDetailsQuestionModeTestPaperShow() {
        if (mview == null) {
            return;
        }
        mCurrentAnswerMode = "testpaper";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankAnswerPaperView == null) {
            mModelQuestionBankAnswerPaperView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankAnswerPaperView);
        //此题所属章节名称
        TextView questionbank_answerpaper_questiontitle = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questiontitle);
        questionbank_answerpaper_questiontitle.setText(mCurrentChapterName);
        //倒计时
        TextView questionbank_answerpaper_countdowntimetext = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_countdowntimetext);

        //点击标记
        ImageView questionbank_answerpaper_sign = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_sign);
        questionbank_answerpaper_sign.setOnClickListener(v -> {
            if (mIsSign) {
                getMyQuestionBankflag(questionbank_answerpaper_sign,"","2","");
            } else {
                //标记
                getMyQuestionBankflag(questionbank_answerpaper_sign,"","1","");
            }
        });
        //查询试卷表中生成的临时题
        //添加题,//默认显示第一题
        TestPaper_QuestionViewAdd();
        //点击交卷
        LinearLayout questionbank_answerpaper_commit = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_commit);
        questionbank_answerpaper_commit.setOnClickListener(v -> {
            View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view1.findViewById(R.id.tip);
            tip.setText("交卷");
            TextView dialog_content = view1.findViewById(R.id.dialog_content);
            dialog_content.setText("确认交卷吗？");
            TextView button_cancel = view1.findViewById(R.id.button_cancel);
            button_cancel.setText("再检查一下");
            button_cancel.setOnClickListener(View -> {
                mMyDialog.cancel();
            });
            TextView button_sure = view1.findViewById(R.id.button_sure);
            button_sure.setText("交卷");
            button_sure.setOnClickListener(View -> {
                //暂停计时器
                if (mTimer2 != null) {
                    mTimer2.cancel();
                }
                if (mTask2 != null) {
                    mTask2.cancel();
                }
                //显示交卷界面
                getQuestionBankHandInTestPaperBean(1,mTime);
                mMyDialog.cancel();
            });
        });
        //点击暂停
        ImageView questionbank_answerpaper_pause = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_pause);
        questionbank_answerpaper_pause.setOnClickListener(v -> {
            getQuestionBankHandInTestPaperBean(2,mTime);
            View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure, null);
            ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
            mMyDialog.setCancelable(false);
            mMyDialog.show();
            TextView tip = view1.findViewById(R.id.tip);
            tip.setText("暂停");
            TextView dialog_content = view1.findViewById(R.id.dialog_content);
            dialog_content.setText("哎呦，休息时间到啦");
            TextView button_sure = view1.findViewById(R.id.button_sure);
            //点击继续做题
            button_sure.setText("继续做题");
            button_sure.setOnClickListener(View -> {
                mMyDialog.cancel();
                //重新打开计时器
                mTimer2 = new Timer();
                mTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        mTime = mTime - 1;
                        //定时器  判断当前的题库是否继做题
                        runOnUiThread(() ->
                                //显示倒计时的时间
                                questionbank_answerpaper_countdowntimetext.setText(getStringTime(mTime)));
                        if (mTime <= 0){
                            //暂停计时器
                            if (mTimer2 != null) {
                                mTimer2.cancel();
                            }
                            if (mTask2 != null) {
                                mTask2.cancel();
                            }
                            //交卷
                            getQuestionBankHandInTestPaperBean(1,mTime);
                        }
                    }
                };
                mTimer2.schedule(mTask2, 0, 1000);
            });
            //暂停计时器
            if (mTimer2 != null) {
                mTimer2.cancel();
            }
            if (mTask2 != null) {
                mTask2.cancel();
            }
        });
        //上一题
        LinearLayout button_questionbank_beforquestion = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_beforquestion);
        button_questionbank_beforquestion.setOnClickListener(v -> {
            TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
            if (questionbank_answerpaper_questioncount.getText().toString().equals("1") || questionbank_answerpaper_questioncount.getText().toString().equals("0")) {
                Toast.makeText(mControlMainActivity, "前面没有题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到上一道题
                mCurrentIndex = mCurrentIndex - 1;
                TestPaper_QuestionViewAdd();
                LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                coursedetails_answerpaper_analysis.removeAllViews();
            }
        });
        //下一题
        LinearLayout button_questionbank_nextquestion = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_nextquestion);
        button_questionbank_nextquestion.setOnClickListener(V -> {
            if (mMyTestPageIssueDataBeans != null) {
                TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
                if (questionbank_answerpaper_questioncount.getText().toString().equals("" + mMyTestPageIssueDataBeans.size())) {
                    Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
                } else { //跳到下一道题
                    mCurrentIndex = mCurrentIndex + 1;
                    TestPaper_QuestionViewAdd();
                    LinearLayout coursedetails_answerpaper_analysis = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysis);
                    coursedetails_answerpaper_analysis.removeAllViews();
                }
            }
        });
        //答题卡
        LinearLayout button_questionbank_answerquestioncard = mModelQuestionBankAnswerPaperView.findViewById(R.id.button_questionbank_answerquestioncard);
        button_questionbank_answerquestioncard.setOnClickListener(v ->{
            mTestPageIssueDataScore = "";
            getQuestionBankAnswerSheet(1);
        });
        //点击字号
        ImageView questionbank_answerpaper_fontsize = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_fontsize);
        questionbank_answerpaper_fontsize.setOnClickListener(v -> {
            ShowPopFontSize(questionbank_answerpaper_fontsize);
        });
        //计时器
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimer2 = null;
        }
        if (mTask2 != null) {
            mTask2.cancel();
            mTask2 = null;
        }
        mTimer2 = new Timer();
        mTask2 = new TimerTask() {
            @Override
            public void run() {
                mTime = mTime - 1;
                runOnUiThread(() -> questionbank_answerpaper_countdowntimetext.setText(getStringTime(mTime)));
                if (mTime <= 0){
                    //暂停计时器
                    if (mTimer2 != null) {
                        mTimer2.cancel();
                    }
                    if (mTask2 != null) {
                        mTask2.cancel();
                    }
                    //交卷
                    getQuestionBankHandInTestPaperBean(1,mTime);
                }
            }
        };
        mTimer2.schedule(mTask2, 0, 1000);

        TextView coursedetails_answerpaper_analysisbutton = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_analysisbutton);
        LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams) coursedetails_answerpaper_analysisbutton.getLayoutParams();
        lLayoutParams.height = 0;
        lLayoutParams.topMargin = 0;
        coursedetails_answerpaper_analysisbutton.setLayoutParams(lLayoutParams);
    }

    //答题-交卷解析模式界面展示
    private void QuestionBankDetailsQuestionModeHandInShow() {
        if (mview == null) {
            return;
        }
        mCurrentAnswerMode = "handin";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankHandInAnalysisView == null) {
            mModelQuestionBankHandInAnalysisView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_handin_analysis, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankHandInAnalysisView);
        //此题所属章节名称
        TextView questionbank_handin_analysis_questiontitle = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_questiontitle);
        questionbank_handin_analysis_questiontitle.setText(mCurrentChapterName);
        //点击收藏
        ImageView questionbank_handin_analysis_collection = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_collection);
        questionbank_handin_analysis_collection.setOnClickListener(v -> {
            if (mIsCollect) {
                getMyQuestionBankflag(questionbank_handin_analysis_collection,"2","","");
            } else {
                getMyQuestionBankflag(questionbank_handin_analysis_collection,"1","","");

            }
        });
        int questionSize = 0;
        MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = null;
        if (mMyQuestionBankExercisesBean != null){
            if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                questionSize = questionSize + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                if (mCurrentIndex < questionSize) {
                    myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex);
                }
            }
            if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                questionSize = questionSize + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                if (myQuestionBankExercisesDataBean == null) {
                    if (mCurrentIndex < questionSize) {
                        int count = 0;
                        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                            count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                        }
                        if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                            myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count);
                        }
                    }
                }
            }
            if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                questionSize = questionSize + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                if (mCurrentIndex < questionSize && myQuestionBankExercisesDataBean == null) {
                    int count = mCurrentIndex;
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                        count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                    }
                    if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                        myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count);
                    }
                }
            }
            if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                questionSize = questionSize + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                if (mCurrentIndex < questionSize && myQuestionBankExercisesDataBean == null) {
                    int count = mCurrentIndex;
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                        count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                    }
                    if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                        count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                    }
                    if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                        myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count);
                    }
                }
            }
        } else if (mMyTestPageIssueDataBeans != null){
            questionSize = mMyTestPageIssueDataBeans.size();
            if (mCurrentIndex < mMyTestPageIssueDataBeans.size()) {
                MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean = mMyTestPageIssueDataBeans.get(mCurrentIndex);
                if (myTestPageIssueDataBean != null) {
                    myQuestionBankExercisesDataBean = new MyQuestionBankExercises.MyQuestionBankExercisesDataBean();
                    myQuestionBankExercisesDataBean.tf_marked = myTestPageIssueDataBean.tf_marked;
                    myQuestionBankExercisesDataBean.tf_collection = myTestPageIssueDataBean.tf_collection;
                    myQuestionBankExercisesDataBean.question_id = myTestPageIssueDataBean.question_id;
                    myQuestionBankExercisesDataBean.optionanswer = myTestPageIssueDataBean.optionanswer;
                    myQuestionBankExercisesDataBean.question_type = myTestPageIssueDataBean.question_type;
                    myQuestionBankExercisesDataBean.question_analysis = myTestPageIssueDataBean.question_analysis;
                    myQuestionBankExercisesDataBean.answer = myTestPageIssueDataBean.answer;
                    myQuestionBankExercisesDataBean.question_name = myTestPageIssueDataBean.question_name;
                    myQuestionBankExercisesDataBean.audio_analysis = myTestPageIssueDataBean.audio_analysis;
                    myQuestionBankExercisesDataBean.video_analysis = myTestPageIssueDataBean.video_analysis;
                    if (myTestPageIssueDataBean.question_id_group1 != null) {
                        for (MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean1 : myTestPageIssueDataBean.question_id_group1) {
                            if (myTestPageIssueDataBean1 == null) {
                                continue;
                            }
                            MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean1 = new MyQuestionBankExercises.MyQuestionBankExercisesDataBean();
                            myQuestionBankExercisesDataBean1.tf_marked = myTestPageIssueDataBean1.tf_marked;
                            myQuestionBankExercisesDataBean1.tf_collection = myTestPageIssueDataBean1.tf_collection;
                            myQuestionBankExercisesDataBean1.question_id = myTestPageIssueDataBean1.question_id;
                            myQuestionBankExercisesDataBean1.optionanswer = myTestPageIssueDataBean1.optionanswer;
                            myQuestionBankExercisesDataBean1.question_type = myTestPageIssueDataBean1.question_type;
                            myQuestionBankExercisesDataBean1.question_analysis = myTestPageIssueDataBean1.question_analysis;
                            myQuestionBankExercisesDataBean1.answer = myTestPageIssueDataBean1.answer;
                            myQuestionBankExercisesDataBean1.question_name = myTestPageIssueDataBean1.question_name;
                            myQuestionBankExercisesDataBean1.audio_analysis = myTestPageIssueDataBean1.audio_analysis;
                            myQuestionBankExercisesDataBean1.video_analysis = myTestPageIssueDataBean1.video_analysis;
                            if (myQuestionBankExercisesDataBean.question_sub_group == null){
                                myQuestionBankExercisesDataBean.question_sub_group = new ArrayList<>();
                            }
                            myQuestionBankExercisesDataBean.question_sub_group.add(myQuestionBankExercisesDataBean1);
                        }
                    }
                }
            }
        }
        if (myQuestionBankExercisesDataBean == null){
            return;
        }
        if (myQuestionBankExercisesDataBean.question_type == null){
            return;
        }
        //添加题
        HandInAnalysisQuestionViewAdd(questionbank_handin_analysis_collection,myQuestionBankExercisesDataBean,questionSize);
        //上一题
        LinearLayout button_handin_analysis_beforquestion = mModelQuestionBankHandInAnalysisView.findViewById(R.id.button_handin_analysis_beforquestion);
        button_handin_analysis_beforquestion.setOnClickListener(v -> {
            TextView questionbank_handin_analysis_questioncount = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_questioncount);
            if (questionbank_handin_analysis_questioncount.getText().toString().equals("1") || questionbank_handin_analysis_questioncount.getText().toString().equals("0")) {
                Toast.makeText(mControlMainActivity, "前面没有题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到上一道题
                mCurrentIndex = mCurrentIndex - 1;
                int questionSize1 = 0;
                MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean1 = null;
                if (mMyQuestionBankExercisesBean != null){
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                        questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                        if (mCurrentIndex < questionSize1) {
                            myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex);
                        }
                    }
                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                        questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                        if (myQuestionBankExercisesDataBean1 == null) {
                            if (mCurrentIndex < questionSize1) {
                                int count = 0;
                                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                    count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                }
                                if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                                    myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count);
                                }
                            }
                        }
                    }
                    if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                        questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                        if (mCurrentIndex < questionSize1 && myQuestionBankExercisesDataBean1 == null) {
                            int count = mCurrentIndex;
                            if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                            }
                            if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                            }
                            if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                                myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count);
                            }
                        }
                    }
                    if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                        questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                        if (mCurrentIndex < questionSize1 && myQuestionBankExercisesDataBean1 == null) {
                            int count = mCurrentIndex;
                            if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                            }
                            if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                            }
                            if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                                count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                            }
                            if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                                myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count);
                            }
                        }
                    }
                } else if (mMyTestPageIssueDataBeans != null){
                    questionSize1 = mMyTestPageIssueDataBeans.size();
                    if (mCurrentIndex < mMyTestPageIssueDataBeans.size()) {
                        MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean = mMyTestPageIssueDataBeans.get(mCurrentIndex);
                        if (myTestPageIssueDataBean != null) {
                            myQuestionBankExercisesDataBean1 = new MyQuestionBankExercises.MyQuestionBankExercisesDataBean();
                            myQuestionBankExercisesDataBean1.tf_marked = myTestPageIssueDataBean.tf_marked;
                            myQuestionBankExercisesDataBean1.tf_collection = myTestPageIssueDataBean.tf_collection;
                            myQuestionBankExercisesDataBean1.question_id = myTestPageIssueDataBean.question_id;
                            myQuestionBankExercisesDataBean1.optionanswer = myTestPageIssueDataBean.optionanswer;
                            myQuestionBankExercisesDataBean1.question_type = myTestPageIssueDataBean.question_type;
                            myQuestionBankExercisesDataBean1.question_analysis = myTestPageIssueDataBean.question_analysis;
                            myQuestionBankExercisesDataBean1.answer = myTestPageIssueDataBean.answer;
                            myQuestionBankExercisesDataBean1.question_name = myTestPageIssueDataBean.question_name;
                            myQuestionBankExercisesDataBean1.audio_analysis = myTestPageIssueDataBean.audio_analysis;
                            myQuestionBankExercisesDataBean1.video_analysis = myTestPageIssueDataBean.video_analysis;
                            if (myTestPageIssueDataBean.question_id_group1 != null) {
                                for (MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean1 : myTestPageIssueDataBean.question_id_group1) {
                                    if (myTestPageIssueDataBean1 == null) {
                                        continue;
                                    }
                                    MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean2 = new MyQuestionBankExercises.MyQuestionBankExercisesDataBean();
                                    myQuestionBankExercisesDataBean2.tf_marked = myTestPageIssueDataBean1.tf_marked;
                                    myQuestionBankExercisesDataBean2.tf_collection = myTestPageIssueDataBean1.tf_collection;
                                    myQuestionBankExercisesDataBean2.question_id = myTestPageIssueDataBean1.question_id;
                                    myQuestionBankExercisesDataBean2.optionanswer = myTestPageIssueDataBean1.optionanswer;
                                    myQuestionBankExercisesDataBean2.question_type = myTestPageIssueDataBean1.question_type;
                                    myQuestionBankExercisesDataBean2.question_analysis = myTestPageIssueDataBean1.question_analysis;
                                    myQuestionBankExercisesDataBean2.answer = myTestPageIssueDataBean1.answer;
                                    myQuestionBankExercisesDataBean2.question_name = myTestPageIssueDataBean1.question_name;
                                    myQuestionBankExercisesDataBean2.audio_analysis = myTestPageIssueDataBean1.audio_analysis;
                                    myQuestionBankExercisesDataBean2.video_analysis = myTestPageIssueDataBean1.video_analysis;
                                    if (myQuestionBankExercisesDataBean1.question_sub_group == null){
                                        myQuestionBankExercisesDataBean1.question_sub_group = new ArrayList<>();
                                    }
                                    myQuestionBankExercisesDataBean1.question_sub_group.add(myQuestionBankExercisesDataBean2);
                                }
                            }
                        }
                    }
                }
                if (myQuestionBankExercisesDataBean1 == null){
                    return;
                }
                if (myQuestionBankExercisesDataBean1.question_type == null){
                    return;
                }
                HandInAnalysisQuestionViewAdd(questionbank_handin_analysis_collection,myQuestionBankExercisesDataBean1,questionSize1);
            }
        });

        //下一题
        LinearLayout button_handin_analysis_nextquestion = mModelQuestionBankHandInAnalysisView.findViewById(R.id.button_handin_analysis_nextquestion);
        Integer finalQuestionSize = questionSize;
        button_handin_analysis_nextquestion.setOnClickListener(V -> {
            if (finalQuestionSize != null) {
                TextView questionbank_handin_analysis_questioncount = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_questioncount);
                if (questionbank_handin_analysis_questioncount.getText().toString().equals("" + finalQuestionSize)) {
                    Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
                } else { //跳到下一道题
                    mCurrentIndex = mCurrentIndex + 1;
                    int questionSize1 = 0;
                    MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean1 = null;
                    if (mMyQuestionBankExercisesBean != null){
                        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                            questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                            if (mCurrentIndex < questionSize1) {
                                myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex);
                            }
                        }
                        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                            questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                            if (myQuestionBankExercisesDataBean1 == null) {
                                if (mCurrentIndex < questionSize1) {
                                    int count = 0;
                                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                    }
                                    if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                                        myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count);
                                    }
                                }
                            }
                        }
                        if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                            questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                            if (mCurrentIndex < questionSize1 && myQuestionBankExercisesDataBean1 == null) {
                                int count = mCurrentIndex;
                                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                    count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                }
                                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                    count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                                }
                                if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                                    myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count);
                                }
                            }
                        }
                        if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                            questionSize1 = questionSize1 + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                            if (mCurrentIndex < questionSize1 && myQuestionBankExercisesDataBean1 == null) {
                                int count = mCurrentIndex;
                                if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                    count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                }
                                if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                    count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                                }
                                if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                                    count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                                }
                                if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                                    myQuestionBankExercisesDataBean1 = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count);
                                }
                            }
                        }
                    } else if (mMyTestPageIssueDataBeans != null){
                        questionSize1 = mMyTestPageIssueDataBeans.size();
                        if (mCurrentIndex < mMyTestPageIssueDataBeans.size()) {
                            MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean = mMyTestPageIssueDataBeans.get(mCurrentIndex);
                            if (myTestPageIssueDataBean != null) {
                                myQuestionBankExercisesDataBean1 = new MyQuestionBankExercises.MyQuestionBankExercisesDataBean();
                                myQuestionBankExercisesDataBean1.tf_marked = myTestPageIssueDataBean.tf_marked;
                                myQuestionBankExercisesDataBean1.tf_collection = myTestPageIssueDataBean.tf_collection;
                                myQuestionBankExercisesDataBean1.question_id = myTestPageIssueDataBean.question_id;
                                myQuestionBankExercisesDataBean1.optionanswer = myTestPageIssueDataBean.optionanswer;
                                myQuestionBankExercisesDataBean1.question_type = myTestPageIssueDataBean.question_type;
                                myQuestionBankExercisesDataBean1.question_analysis = myTestPageIssueDataBean.question_analysis;
                                myQuestionBankExercisesDataBean1.answer = myTestPageIssueDataBean.answer;
                                myQuestionBankExercisesDataBean1.question_name = myTestPageIssueDataBean.question_name;
                                myQuestionBankExercisesDataBean1.audio_analysis = myTestPageIssueDataBean.audio_analysis;
                                myQuestionBankExercisesDataBean1.video_analysis = myTestPageIssueDataBean.video_analysis;
                                if (myTestPageIssueDataBean.question_id_group1 != null) {
                                    for (MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean1 : myTestPageIssueDataBean.question_id_group1) {
                                        if (myTestPageIssueDataBean1 == null) {
                                            continue;
                                        }
                                        MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean2 = new MyQuestionBankExercises.MyQuestionBankExercisesDataBean();
                                        myQuestionBankExercisesDataBean2.tf_marked = myTestPageIssueDataBean1.tf_marked;
                                        myQuestionBankExercisesDataBean2.tf_collection = myTestPageIssueDataBean1.tf_collection;
                                        myQuestionBankExercisesDataBean2.question_id = myTestPageIssueDataBean1.question_id;
                                        myQuestionBankExercisesDataBean2.optionanswer = myTestPageIssueDataBean1.optionanswer;
                                        myQuestionBankExercisesDataBean2.question_type = myTestPageIssueDataBean1.question_type;
                                        myQuestionBankExercisesDataBean2.question_analysis = myTestPageIssueDataBean1.question_analysis;
                                        myQuestionBankExercisesDataBean2.answer = myTestPageIssueDataBean1.answer;
                                        myQuestionBankExercisesDataBean2.question_name = myTestPageIssueDataBean1.question_name;
                                        myQuestionBankExercisesDataBean2.audio_analysis = myTestPageIssueDataBean1.audio_analysis;
                                        myQuestionBankExercisesDataBean2.video_analysis = myTestPageIssueDataBean1.video_analysis;
                                        if (myQuestionBankExercisesDataBean1.question_sub_group == null){
                                            myQuestionBankExercisesDataBean1.question_sub_group = new ArrayList<>();
                                        }
                                        myQuestionBankExercisesDataBean1.question_sub_group.add(myQuestionBankExercisesDataBean2);
                                    }
                                }
                            }
                        }
                    }
                    if (myQuestionBankExercisesDataBean1 == null){
                        return;
                    }
                    if (myQuestionBankExercisesDataBean1.question_type == null){
                        return;
                    }
                    HandInAnalysisQuestionViewAdd(questionbank_handin_analysis_collection,myQuestionBankExercisesDataBean1,questionSize1);
                }
            }
        });
        //答题卡
        LinearLayout button_handin_analysis_answerquestioncard = mModelQuestionBankHandInAnalysisView.findViewById(R.id.button_handin_analysis_answerquestioncard);
        button_handin_analysis_answerquestioncard.setOnClickListener(v ->{
            getQuestionBankAnswerSheet(2);});
        //点击字号
        ImageView questionbank_handin_analysis_fontsize = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_fontsize);
        questionbank_handin_analysis_fontsize.setOnClickListener(v -> {
            ShowPopFontSize( questionbank_handin_analysis_fontsize);
        });
    }

    //答题-错题模式界面展示
    private void QuestionBankDetailsQuestionModeWrongQuestionShow() {
        if (mview == null || mMyFavoriteQuestionDataBeans == null) {
            return;
        }
        mCurrentAnswerMode = "wrong";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankWrongQuestionView == null) {
            mModelQuestionBankWrongQuestionView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_wrongquestions, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankWrongQuestionView);
        //此题所属章节名称
        TextView questionbank_wrongquestion_questiontitle = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questiontitle);
        questionbank_wrongquestion_questiontitle.setText(mCurrentChapterName);
        //点击收藏
        ImageView questionbank_wrongquestion_collection = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_collection);
        questionbank_wrongquestion_collection.setOnClickListener(v -> {
            if (mIsCollect) {
                getMyQuestionBankflag(questionbank_wrongquestion_collection,"2","","");
            } else {
                getMyQuestionBankflag(questionbank_wrongquestion_collection,"1","","");
            }
        });
        //添加题,//默认显示第一题
        WrongQuestionViewAdd(questionbank_wrongquestion_collection);
        //上一题
        LinearLayout button_wrongquestion_beforquestion = mModelQuestionBankWrongQuestionView.findViewById(R.id.button_wrongquestion_beforquestion);
        button_wrongquestion_beforquestion.setOnClickListener(v -> {
            TextView questionbank_wrongquestion_questioncount = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questioncount);
            if (questionbank_wrongquestion_questioncount.getText().toString().equals("1") || questionbank_wrongquestion_questioncount.getText().toString().equals("0")) {
                Toast.makeText(mControlMainActivity, "前面没有题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到上一道题
                mCurrentIndex = mCurrentIndex - 1;
                WrongQuestionViewAdd(questionbank_wrongquestion_collection);
            }
        });
        //下一题
        LinearLayout button_wrongquestion_nextquestion = mModelQuestionBankWrongQuestionView.findViewById(R.id.button_wrongquestion_nextquestion);
        button_wrongquestion_nextquestion.setOnClickListener(V -> {
            if (mMyFavoriteQuestionDataBeans == null) {
                return;
            }
            TextView questionbank_wrongquestion_questioncount = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questioncount);
            if (questionbank_wrongquestion_questioncount.getText().toString().equals("" + mMyFavoriteQuestionDataBeans.size())) {
                Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到下一道题
                mCurrentIndex = mCurrentIndex + 1;
                WrongQuestionViewAdd(questionbank_wrongquestion_collection);
            }
        });
        //题型
        LinearLayout button_wrongquestion_answerquestioncard = mModelQuestionBankWrongQuestionView.findViewById(R.id.button_wrongquestion_answerquestioncard);
        button_wrongquestion_answerquestioncard.setOnClickListener(v ->
                QuestionBankDetailsQuestionTypeShow(2));
        //提交错题
        LinearLayout questionbank_wrongquestion_commit = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_commit);
        questionbank_wrongquestion_commit.setOnClickListener(v -> {
            boolean m_isError = false;
            if (mMyFavoriteQuestionDataBeans != null){
                if (mCurrentIndex < mMyFavoriteQuestionDataBeans.size()) {
                    Integer question_id = mMyFavoriteQuestionDataBeans.get(mCurrentIndex).question_id;
                    if (question_id != null && mMyQuestionBankExercisesAnswerMap != null){
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(question_id);
                        if (answerInfo == null){
                            m_isError = true;
                        } else if (answerInfo.result.equals("错")){
                            m_isError = true;
                        }
                    }
                }
            }
            if (m_isError){  //如果答案不正确，弹出提示框
                Toast.makeText(mControlMainActivity,"回答错误",Toast.LENGTH_SHORT).show();
            } else {
                //弹出提示框，回答正确，是否跳转到下一题，如果是，从错题本中移除此题，并跳转到下一道题；如果不是，对话框消失
                View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
                ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
                mMyDialog.setCancelable(true);
                mMyDialog.show();
                TextView tip = view1.findViewById(R.id.tip);
                tip.setText("提示");
                TextView dialog_content = view1.findViewById(R.id.dialog_content);
                dialog_content.setText("回答正确，是否跳转到下一题？");
                TextView button_cancel = view1.findViewById(R.id.button_cancel);
                button_cancel.setText("否");
                button_cancel.setOnClickListener(View -> {
                    mMyDialog.cancel();
                });
                TextView button_sure = view1.findViewById(R.id.button_sure);
                button_sure.setText("是");
                button_sure.setOnClickListener(View -> {
                    if (mMyFavoriteQuestionDataBeans == null) {
                        mMyDialog.cancel();
                        return;
                    }
                    getMyQuestionBankflag(questionbank_wrongquestion_collection,"","","2");
                    mMyDialog.cancel();
                });
            }
        });
        //点击字号
        ImageView questionbank_wrongquestion_fontsize = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_fontsize);
        questionbank_wrongquestion_fontsize.setOnClickListener(v -> {
            ShowPopFontSize( questionbank_wrongquestion_fontsize);
        });
    }

    //答题-我收藏的题模式界面展示
    private void QuestionBankDetailsQuestionModeMyCollectionQuestionShow() {
        if (mview == null || mMyFavoriteQuestionDataBeans == null) {
            return;
        }
        mCurrentAnswerMode = "collection";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankMyCollectionQuestionView == null) {
            mModelQuestionBankMyCollectionQuestionView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_mycollectionquestions, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankMyCollectionQuestionView);
        //此题所属章节名称
        TextView questionbank_mycollextionquestion_questiontitle = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_questiontitle);
        questionbank_mycollextionquestion_questiontitle.setText(mCurrentChapterName);
        //点击收藏
        ImageView questionbank_mycollextionquestion_collection = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_collection);
        questionbank_mycollextionquestion_collection.setOnClickListener(v -> {
            if (mIsCollect) {
                getMyQuestionBankflag(questionbank_mycollextionquestion_collection,"2","","");
            } else {
                getMyQuestionBankflag(questionbank_mycollextionquestion_collection,"1","","");
            }
        });
        //查询试卷表中生成的临时题
        CollectionQuestionViewAdd(questionbank_mycollextionquestion_collection);
        //上一题
        LinearLayout button_mycollextionquestion_beforquestion = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.button_mycollextionquestion_beforquestion);
        button_mycollextionquestion_beforquestion.setOnClickListener(v -> {
            TextView questionbank_mycollextionquestion_questioncount = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_questioncount);
            if (questionbank_mycollextionquestion_questioncount.getText().toString().equals("1") || questionbank_mycollextionquestion_questioncount.getText().toString().equals("0")) {
                Toast.makeText(mControlMainActivity, "前面没有题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到上一道题
                mCurrentIndex = mCurrentIndex - 1;
                CollectionQuestionViewAdd(questionbank_mycollextionquestion_collection);
            }
        });
        //下一题
        LinearLayout button_mycollextionquestion_nextquestion = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.button_mycollextionquestion_nextquestion);
        button_mycollextionquestion_nextquestion.setOnClickListener(V -> {
            if (mMyFavoriteQuestionDataBeans == null) {
                Toast.makeText(mControlMainActivity, "数据错误", Toast.LENGTH_SHORT).show();
                return;
            }
            TextView questionbank_mycollextionquestion_questioncount = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_questioncount);
            if (questionbank_mycollextionquestion_questioncount.getText().toString().equals("" + mMyFavoriteQuestionDataBeans.size())) {
                Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
            } else { //跳到下一道题
                mCurrentIndex = mCurrentIndex + 1;
                CollectionQuestionViewAdd(questionbank_mycollextionquestion_collection);
            }
        });
        //题型
        LinearLayout button_mycollextionquestion_answerquestioncard = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.button_mycollextionquestion_answerquestioncard);
        button_mycollextionquestion_answerquestioncard.setOnClickListener(v -> QuestionBankDetailsQuestionTypeShow(1));
        //点击字号
        ImageView questionbank_mycollextionquestion_fontsize = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_fontsize);
        questionbank_mycollextionquestion_fontsize.setOnClickListener(v -> {
            ShowPopFontSize( questionbank_mycollextionquestion_fontsize);
        });
    }

    //答题-做题记录模式界面展示
    private void QuestionBankDetailsQuestionModeQuestionRecordShow() {
        if (mview == null) {
            return;
        }
        mCurrentAnswerMode = "requestionrecord";
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankQuestionRecordView == null) {
            mModelQuestionBankQuestionRecordView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_questionrecord, null);
            //章节练习
            TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
            //快速做题
            TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
            //模拟真题
            TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
            questionbank_questionrecords_tab_chapterexercises.setOnClickListener(this);
            questionbank_questionrecords_tab_quicktask.setOnClickListener(this);
            questionbank_questionrecords_tab_simulated.setOnClickListener(this);
            //做题记录的刷新控件
            mSmart_model_questionbank_questionrecord = mModelQuestionBankQuestionRecordView.findViewById(R.id.Smart_model_questionbank_questionrecord);
            mSmart_model_questionbank_questionrecord.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mSum <= mCurrentPage * mPageCount){
                        LinearLayout questionbank_questionrecords_end = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_end);
                        questionbank_questionrecords_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    getQuestionBankAnswerRecordMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getQuestionBankAnswerRecord();
                }
            });

        }
        LinearLayout questionbank_questionrecords_end = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_end);
        questionbank_questionrecords_end.setVisibility(View.INVISIBLE);
        fragmentquestionbank_main.addView(mModelQuestionBankQuestionRecordView);
        //默认游标位置在章节练习
        ImageView questionbank_questionrecords_cursor1 = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_cursor1);
        int x = width / 6 - mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        questionbank_questionrecords_cursor1.setX(x);
        if (mQuestionRecordCurrentTab.equals("ChapterExercises")) {
            Animation animation = new TranslateAnimation((mQuestionRecordLastTabIndex - 1) * width / 3, 0, 0, 0);
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(200);
            questionbank_questionrecords_cursor1.startAnimation(animation);
            TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
            TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
            TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
            questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
            questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
            questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        } else if (mQuestionRecordCurrentTab.equals("QuickTask")) {
            Animation animation = new TranslateAnimation((mQuestionRecordLastTabIndex - 1) * width / 3, width / 3, 0, 0);
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(200);
            questionbank_questionrecords_cursor1.startAnimation(animation);
            TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
            TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
            TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
            questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
            questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
            questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        } else if (mQuestionRecordCurrentTab.equals("Simulated")) {
            Animation animation = new TranslateAnimation((mQuestionRecordLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(200);
            questionbank_questionrecords_cursor1.startAnimation(animation);
            TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
            TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
            TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
            questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
            questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
            questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        }
//        //默认选中的为章节练习
//        mQuestionRecordLastTabIndex = 1;
//        mQuestionRecordCurrentTab = "ChapterExercises";
//        //章节练习
//        TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
//        //快速做题
//        TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
//        //模拟真题
//        TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
//        questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
//        questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
//        questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));

        LinearLayout questionbank_questionrecords_content = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_content);
        questionbank_questionrecords_content.removeAllViews();
        View questionbank_questionrecords_line1 = null;
        for (int i = 0; i < questionBankAnswerRecordDataBeanLists.size(); i ++) {
            QuestionBankAnswerRecordBean.QuestionBankAnswerRecordDataBeanList questionBankAnswerRecordDataBeanList = questionBankAnswerRecordDataBeanLists.get(i);
            if (questionBankAnswerRecordDataBeanList == null){
                continue;
            }
            //试卷名称  测试的名称   网络请求
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_questionrecord1, null);
            //试卷名称
            TextView questionbank_questionrecords_testname = view.findViewById(R.id.questionbank_questionrecords_testname);
            String name = "";
            if (questionBankAnswerRecordDataBeanList.name != null){
                name = questionBankAnswerRecordDataBeanList.name ;
            }
            if (questionBankAnswerRecordDataBeanList.test_paper_name != null){
                name = questionBankAnswerRecordDataBeanList.test_paper_name ;
            }
            if (mQuestionRecordCurrentTab.equals("QuickTask")){
                name = "快速做题";
            }
            questionbank_questionrecords_testname.setText(name);
            questionbank_questionrecords_testname.setHint(questionBankAnswerRecordDataBeanList.answer_id + "");
            //试卷时间
            TextView questionbank_questionrecords_detailstime = view.findViewById(R.id.questionbank_questionrecords_detailstime);
            if (questionBankAnswerRecordDataBeanList.time == null) {
                questionBankAnswerRecordDataBeanList.time = "";
            } else {
                Date date = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    date = df.parse(questionBankAnswerRecordDataBeanList.time);
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
                        questionBankAnswerRecordDataBeanList.time = df2.format(date1).toString();
                    }
                }
            }
            if (questionBankAnswerRecordDataBeanList.used_answer_time == null) {
                questionBankAnswerRecordDataBeanList.used_answer_time = 0;
            }
            if (questionBankAnswerRecordDataBeanList.dui == null) {
                questionBankAnswerRecordDataBeanList.dui = 0;
            }
            if (questionBankAnswerRecordDataBeanList.cuo == null) {
                questionBankAnswerRecordDataBeanList.cuo = 0;
            }

            questionbank_questionrecords_detailstime.setText(questionBankAnswerRecordDataBeanList.time);
            questionbank_questionrecords_content.addView(view);
            //试卷的时间
            TextView questionbank_questionrecords_detailsduring = view.findViewById(R.id.questionbank_questionrecords_detailsduring);
            questionbank_questionrecords_detailsduring.setText(getStringTime(questionBankAnswerRecordDataBeanList.used_answer_time));
            //正确的数量
            TextView questionbank_questionrecords_detailsrightnum = view.findViewById(R.id.questionbank_questionrecords_detailsrightnum);
            questionbank_questionrecords_detailsrightnum.setText(questionBankAnswerRecordDataBeanList.dui + "");
            //错误的数量
            TextView questionbank_questionrecords_detailserrornum = view.findViewById(R.id.questionbank_questionrecords_detailserrornum);
            questionbank_questionrecords_detailserrornum.setText(questionBankAnswerRecordDataBeanList.cuo + "");
            questionbank_questionrecords_line1 = view.findViewById(R.id.questionbank_questionrecords_line1);
            Integer finalUsed_answer_time = questionBankAnswerRecordDataBeanList.used_answer_time;
            String finalName = name;
            view.setOnClickListener(v -> {
                //弹出提示框，回答正确，是否跳转到下一题，如果是，从错题本中移除此题，并跳转到下一道题；如果不是，对话框消失
                View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
                ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
                mMyDialog.setCancelable(true);
                mMyDialog.show();
                TextView tip = view1.findViewById(R.id.tip);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tip.getLayoutParams();
                rl.height = 0;
                tip.setLayoutParams(rl);
                TextView dialog_content = view1.findViewById(R.id.dialog_content);
                dialog_content.setText("该试卷已完成，耗时" + getStringTime(finalUsed_answer_time));
                TextView button_cancel = view1.findViewById(R.id.button_cancel);
                button_cancel.setText("查看解析");
                button_cancel.setOnClickListener(View -> {
                    //跳转到解析界面
                    Toast.makeText(mControlMainActivity, "查看解析内容", Toast.LENGTH_SHORT).show();
                    mAnswer_Id = questionBankAnswerRecordDataBeanList.answer_id;
                    if (mQuestionRecordCurrentTab.equals("ChapterExercises")){
                        getQuestionBankAnswerRecordLookChapter(finalName);
                    } else if (mQuestionRecordCurrentTab.equals("QuickTask")){
                        getQuestionBankAnswerRecordLookChapter(finalName);
                    } else if (mQuestionRecordCurrentTab.equals("Simulated")){
                        getQuestionBankAnswerRecordLook(finalName);
                    }
                    mMyDialog.cancel();
                });
                TextView button_sure = view1.findViewById(R.id.button_sure);
                button_sure.setText("再做一遍");
                button_sure.setOnClickListener(View -> {//将试卷重新调出来，做题
//                    if (myQuestionBankGoonDataBeans != null){
//                        if (myQuestionBankGoonDataBeans.size() != 0) {
//                            Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
                    mAnswer_Id = questionBankAnswerRecordDataBeanList.answer_id;
                    if (mQuestionRecordCurrentTab.equals("ChapterExercises")){
                        mChapter_test_point_id = questionBankAnswerRecordDataBeanList.chapter_test_point_id;
                        getQueryTopicSetting(mChapter_test_point_id);
                    } else if (mQuestionRecordCurrentTab.equals("QuickTask")){
                        getqueryMyQuestionBankQuickIssue();
                    } else if (mQuestionRecordCurrentTab.equals("Simulated")){
                        getQuestionBankAnswerRecordAgain(finalName);
                    }
                    mMyDialog.cancel();
                });
            });
        }
        if (questionbank_questionrecords_line1 != null) {
            questionbank_questionrecords_line1.setVisibility(View.INVISIBLE);
        }
    }

    //添加问题界面
    private boolean QuestionViewAdd(MyQuestionBankExercises.MyQuestionBankExercisesBean myQuestionBankExercisesBean) {
        if (myQuestionBankExercisesBean == null){
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mCurrentIndex < 0 ){
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        View view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
        LinearLayout coursedetails_answerpaper_details = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_details);
        coursedetails_answerpaper_details.removeAllViews();
        coursedetails_answerpaper_details.addView(view2);

        MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = null;
        //字符串分割
        int questionSum = 0;
        if (myQuestionBankExercisesBean.danxuantiQuestion != null){
            questionSum = questionSum + myQuestionBankExercisesBean.danxuantiQuestion.size();
            if (myQuestionBankExercisesDataBean == null) {
                if (mCurrentIndex < questionSum) {
                    myQuestionBankExercisesDataBean = myQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex);
                }
            }
        }
        if (myQuestionBankExercisesBean.duoxuantiQuestion != null){
            questionSum = questionSum + myQuestionBankExercisesBean.duoxuantiQuestion.size();
            if (myQuestionBankExercisesDataBean == null){
                if (mCurrentIndex < questionSum) {
                    int count = 0;
                    if (myQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - myQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (count < myQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                        myQuestionBankExercisesDataBean = myQuestionBankExercisesBean.duoxuantiQuestion.get(count);
                    }
                }
            }
        }
        if (myQuestionBankExercisesBean.jinadatitiQuestion != null){
            questionSum = questionSum + myQuestionBankExercisesBean.jinadatitiQuestion.size();
            if (myQuestionBankExercisesDataBean == null){
                if (mCurrentIndex < questionSum) {
                    int count = mCurrentIndex;
                    if (myQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - myQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (myQuestionBankExercisesBean.duoxuantiQuestion != null) {
                        count = count - myQuestionBankExercisesBean.duoxuantiQuestion.size();
                    }
                    if (count < myQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                        myQuestionBankExercisesDataBean = myQuestionBankExercisesBean.jinadatitiQuestion.get(count);
                    }
                }
            }
        }
        if (myQuestionBankExercisesBean.cailiaotiQuestion != null){
            questionSum = questionSum + myQuestionBankExercisesBean.cailiaotiQuestion.size();
            if (myQuestionBankExercisesDataBean == null){
                if (mCurrentIndex < questionSum) {
                    int count = mCurrentIndex;
                    if (myQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - myQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (myQuestionBankExercisesBean.duoxuantiQuestion != null) {
                        count = count - myQuestionBankExercisesBean.duoxuantiQuestion.size();
                    }
                    if (myQuestionBankExercisesBean.jinadatitiQuestion != null) {
                        count = count - myQuestionBankExercisesBean.jinadatitiQuestion.size();
                    }
                    if (count < myQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                        myQuestionBankExercisesDataBean = myQuestionBankExercisesBean.cailiaotiQuestion.get(count);
                    }
                }
            }
        }
        if (mCurrentIndex >= questionSum) { //不在数组范围直接返回
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (myQuestionBankExercisesDataBean == null){
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        ImageView questionbank_answerpaper_sign = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_sign);
        if (myQuestionBankExercisesDataBean.tf_marked != null) {
            if (myQuestionBankExercisesDataBean.tf_marked == 2) {
                questionbank_answerpaper_sign.setBackground(mModelQuestionBankAnswerPaperView.getResources().getDrawable(R.drawable.button_questionbank_sign));
                mIsSign = false;
            } else if (myQuestionBankExercisesDataBean.tf_marked == 1) {
                //标记
                questionbank_answerpaper_sign.setBackground(mModelQuestionBankAnswerPaperView.getResources().getDrawable(R.drawable.button_questionbank_sign_blue));
                mIsSign = true;
            }
        } else {
            questionbank_answerpaper_sign.setBackground(mModelQuestionBankAnswerPaperView.getResources().getDrawable(R.drawable.button_questionbank_sign));
            mIsSign = false;
        }
        //总体的数量
        TextView questionbank_answerpaper_questioncountsum = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncountsum);
        questionbank_answerpaper_questioncountsum.setText("/" + questionSum);
        if (questionSum > 0) {
            //案列分析的解析
            TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
            questionbank_answerpaper_questioncount.setText(String.valueOf(mCurrentIndex + 1));
            TextView questionbank_answerpaper_single_title = view2.findViewById(R.id.questionbank_answerpaper_single_title);
            if (mFontSize.equals("nomal")) {
                questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title).setHtmlWithPic(myQuestionBankExercisesDataBean.question_name);
            questionbank_answerpaper_single_title.setHint(myQuestionBankExercisesDataBean.question_id + "");
            //判断当前选择题的类型
            TextView questionbank_answerpaper_questiontype = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questiontype);
            if (myQuestionBankExercisesDataBean.question_type == null) {
                return false;
            }
            if (myQuestionBankExercisesDataBean.question_type == 1) {
                questionbank_answerpaper_questiontype.setText("[单选题]");
            } else if (myQuestionBankExercisesDataBean.question_type == 2) {
                questionbank_answerpaper_questiontype.setText("[多选题]");
            } else if (myQuestionBankExercisesDataBean.question_type == 4) {
                questionbank_answerpaper_questiontype.setText("[简答题]");
            } else if (myQuestionBankExercisesDataBean.question_type == 7) {
                questionbank_answerpaper_questiontype.setText("[材料题]");
            }
            LinearLayout questionbank_answerpaper_content = view2.findViewById(R.id.questionbank_answerpaper_content);
            questionbank_answerpaper_content.removeAllViews();
            if (myQuestionBankExercisesDataBean.question_type == 1 || myQuestionBankExercisesDataBean.question_type == 2) { //如果是单选题或多选题添加选项布局
                if (myQuestionBankExercisesDataBean.optionanswer == null){
                    return false;
                }
                String[] optionanswerS = myQuestionBankExercisesDataBean.optionanswer.split("#EDU;");
                if (optionanswerS == null) {
                    return false;
                }
                for (int i = 0; i < optionanswerS.length; i ++) {
                    View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                    String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                    if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                        continue;
                    }
                    TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                    questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                    MyQuestionBankExercises.MyQuestionBankExercisesDataBean finalMyQuestionBankExercisesDataBean = myQuestionBankExercisesDataBean;
                    MyQuestionBankExercises.MyQuestionBankExercisesDataBean finalMyQuestionBankExercisesDataBean1 = myQuestionBankExercisesDataBean;
                    questionbank_answerpaper_option_name.setOnClickListener(v -> {
                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                        if (finalMyQuestionBankExercisesDataBean.question_type == 1) { //如果是单选题，将其他选项置为false
                            int count = questionbank_answerpaper_content.getChildCount();
                            for (int num = 0; num < count; num ++) {
                                View view4 = questionbank_answerpaper_content.getChildAt(num);
                                if (view4 != view3) {
                                    TextView textview = view4.findViewById(R.id.questionbank_answerpaper_option_name);
                                    textview.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                    textview.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                }
                            }
                            AnswerInfo answerInfo = new AnswerInfo();
                            answerInfo.answer = optionanswerS1[0];
                            if (optionanswerS1[1].equals("是")){
                                answerInfo.result = "对";
                            } else {
                                answerInfo.result = "错";
                            }
                            mMyQuestionBankExercisesAnswerMap.put(finalMyQuestionBankExercisesDataBean1.question_id,answerInfo);
                            ModeTestFindAnalysis();
                        } else {
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(finalMyQuestionBankExercisesDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    if (answerInfo.answer.contains(optionanswerS1[0])) {
                                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                        answerInfo.answer = answerInfo.answer.replace(optionanswerS1[0], "");
                                    } else {
                                        answerInfo.answer = answerInfo.answer + optionanswerS1[0];
                                    }
                                    //将选项排序
                                    String[] strArr = new String[answerInfo.answer.length()];
                                    for (int num = 0; num < answerInfo.answer.length(); num ++){
                                        strArr[num] = answerInfo.answer.substring(num,num + 1);
                                    }
                                    Arrays.sort(strArr,String.CASE_INSENSITIVE_ORDER);
                                    answerInfo.answer = Arrays.toString(strArr).replace("[","");
                                    answerInfo.answer = answerInfo.answer.replace("]","");
                                    answerInfo.answer = answerInfo.answer.replaceAll(",","");
                                    answerInfo.answer = answerInfo.answer.replaceAll(" ","");
                                    if (answerInfo.answer.equals(finalMyQuestionBankExercisesDataBean1.answer)){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                } else {
                                    answerInfo.answer = optionanswerS1[0];
                                    if (answerInfo.answer.equals(finalMyQuestionBankExercisesDataBean1.answer)){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                }
                            } else {
                                answerInfo = new AnswerInfo();
                                answerInfo.answer = optionanswerS1[0];
                                if (answerInfo.answer.equals(finalMyQuestionBankExercisesDataBean1.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                            }
                            mMyQuestionBankExercisesAnswerMap.put(finalMyQuestionBankExercisesDataBean1.question_id,answerInfo);
                        }
                    });
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (answerInfo.answer.contains(optionanswerS1[0])) {
                                questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                            }
                        }
                    }
                    TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                    questionbank_answerpaper_content.addView(view3);
                }
            } else if (myQuestionBankExercisesDataBean.question_type == 4) {//如果是简答题
                View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                questionbank_answerpaper_content.addView(view3);
                EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                if (mFontSize.equals("nomal")) {
                    questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                } else if (mFontSize.equals("small")) {
                    questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                } else if (mFontSize.equals("big")) {
                    questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                }
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
                if (answerInfo != null) {
                    if (answerInfo.answer != null) {
                        questionbank_answerpaper_shortansweredittext.setText(answerInfo.answer);
                    }
                }
                MyQuestionBankExercises.MyQuestionBankExercisesDataBean finalMyQuestionBankExercisesDataBean2 = myQuestionBankExercisesDataBean;
                questionbank_answerpaper_shortansweredittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mMyQuestionBankExercisesAnswerMap != null) {
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(finalMyQuestionBankExercisesDataBean2.question_id);
                            if (answerInfo == null) {
                                answerInfo = new AnswerInfo();
                            }
                            answerInfo.answer = s.toString();
                            answerInfo.result = "对";
                            mMyQuestionBankExercisesAnswerMap.put(finalMyQuestionBankExercisesDataBean2.question_id, answerInfo);
                        }
                    }
                });
            } else if (myQuestionBankExercisesDataBean.question_type == 7) {//如果是材料题
                View contentView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                questionbank_answerpaper_content.addView(contentView);
                EditText contentViewedittext = contentView.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                if (myQuestionBankExercisesDataBean.optionanswer != null) {
                    TextView questionbank_answerpaper_shortanswer = contentView.findViewById(R.id.questionbank_answerpaper_shortanswer);
                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                    rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                    rl = (RelativeLayout.LayoutParams) contentViewedittext.getLayoutParams();
                    rl.height = 0;
                    rl.topMargin = 0;
                    contentViewedittext.setLayoutParams(rl);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(myQuestionBankExercisesDataBean.optionanswer);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                }
                if (myQuestionBankExercisesDataBean.question_sub_group != null || myQuestionBankExercisesDataBean.question_id_group1 != null) { // 说明有子题
                    if (myQuestionBankExercisesDataBean.question_id_group1 == null){
                        myQuestionBankExercisesDataBean.question_id_group1 = myQuestionBankExercisesDataBean.question_sub_group;
                    }
                    for (int i = 0; i < myQuestionBankExercisesDataBean.question_id_group1.size(); i++) {
                        MyQuestionBankExercises.MyQuestionBankExercisesDataBean MyQuestionBankExercisesDataBean1 = myQuestionBankExercisesDataBean.question_id_group1.get(i);
                        if (MyQuestionBankExercisesDataBean1 == null) {
                            continue;
                        }
                        String type = "";
                        if (MyQuestionBankExercisesDataBean1.question_type == 11) {
                            type = "[单选题] ";
                        } else if (MyQuestionBankExercisesDataBean1.question_type == 12) {
                            type = "[多选题] ";
                        } else if (MyQuestionBankExercisesDataBean1.question_type == 14) {
                            type = "[简答题] ";
                        }
                        if (MyQuestionBankExercisesDataBean1.question_type == 11 || MyQuestionBankExercisesDataBean1.question_type == 12) { //如果是单选题或多选题添加选项布局
                            if (MyQuestionBankExercisesDataBean1.optionanswer == null) {
                                continue;
                            }
                            String[] optionanswerS = MyQuestionBankExercisesDataBean1.optionanswer.split("#EDU;");
                            if (optionanswerS == null) {
                                continue;
                            }
                            View view5 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
                            questionbank_answerpaper_content.addView(view5);
                            TextView questionbank_answerpaper_single_titlesign = view5.findViewById(R.id.questionbank_answerpaper_single_titlesign);
                            questionbank_answerpaper_single_titlesign.setText("第" + (i + 1) + "题： " + type);
                            LinearLayout questionbank_answerpaper_content1 = view5.findViewById(R.id.questionbank_answerpaper_content);
                            TextView questionbank_answerpaper_single_title1 = view5.findViewById(R.id.questionbank_answerpaper_single_title);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title1).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_name);
                            String currentAnswer = "";
                            for (String string : optionanswerS) {
                                View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                                String[] optionanswerS1 = string.substring(1).split("#");
                                if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                                    continue;
                                }
                                if (optionanswerS1[1].equals("是")) {
                                    currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                                    if (MyQuestionBankExercisesDataBean1.answer == null){
                                        MyQuestionBankExercisesDataBean1.answer = currentAnswer;
                                    }
                                }
                                TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                                questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                                questionbank_answerpaper_option_name.setOnClickListener(v -> { // 选项的点击响应
                                    questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                    questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                    if (MyQuestionBankExercisesDataBean1.question_type == 11) { //如果是单选题，将其他选项置为false
                                        int count = questionbank_answerpaper_content1.getChildCount();
                                        for (int num = 0; num < count; num ++) {
                                            View view4 = questionbank_answerpaper_content1.getChildAt(num);
                                            if (view4 != view3) {
                                                TextView textview = view4.findViewById(R.id.questionbank_answerpaper_option_name);
                                                textview.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                                textview.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                            }
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        answerInfo.answer = optionanswerS1[0];
                                        if (optionanswerS1[1].equals("是")){
                                            answerInfo.result = "对";
                                        } else {
                                            answerInfo.result = "错";
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyQuestionBankExercisesDataBean1.question_id,answerInfo);
                                    } else {
                                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                                        if (answerInfo != null) {
                                            if (answerInfo.answer != null) {
                                                if (answerInfo.answer.contains(optionanswerS1[0])) {
                                                    questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                                    questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                                    answerInfo.answer = answerInfo.answer.replace(optionanswerS1[0], "");
                                                } else {
                                                    answerInfo.answer = answerInfo.answer + optionanswerS1[0];
                                                }
                                                //将选项排序
                                                String[] strArr = new String[answerInfo.answer.length()];
                                                for (int num = 0; num < answerInfo.answer.length(); num ++){
                                                    strArr[num] = answerInfo.answer.substring(num,num + 1);
                                                }
                                                Arrays.sort(strArr,String.CASE_INSENSITIVE_ORDER);
                                                answerInfo.answer = Arrays.toString(strArr).replace("[","");
                                                answerInfo.answer = answerInfo.answer.replace("]","");
                                                answerInfo.answer = answerInfo.answer.replaceAll(",","");
                                                answerInfo.answer = answerInfo.answer.replaceAll(" ","");
                                                if (answerInfo.answer.equals(MyQuestionBankExercisesDataBean1.answer)){
                                                    answerInfo.result = "对";
                                                } else {
                                                    answerInfo.result = "错";
                                                }
                                            } else {
                                                answerInfo.answer = optionanswerS1[0];
                                                if (answerInfo.answer.equals(MyQuestionBankExercisesDataBean1.answer)){
                                                    answerInfo.result = "对";
                                                } else {
                                                    answerInfo.result = "错";
                                                }
                                            }
                                        } else {
                                            answerInfo = new AnswerInfo();
                                            answerInfo.answer = optionanswerS1[0];
                                            if (answerInfo.answer.equals(MyQuestionBankExercisesDataBean1.answer)){
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyQuestionBankExercisesDataBean1.question_id,answerInfo);
                                    }
                                });
                                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                                if (answerInfo != null) {
                                    if (answerInfo.answer != null) {
                                        if (answerInfo.answer.contains(optionanswerS1[0])) {
                                            questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                            questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                        }
                                    }
                                }
                                TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                                if (mFontSize.equals("nomal")) {
                                    questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                } else if (mFontSize.equals("small")) {
                                    questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                } else if (mFontSize.equals("big")) {
                                    questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                }
                                questionbank_answerpaper_content1.addView(view3);
                            }
                            if ( !mCurrentAnswerMode.equals("exam")){ //判断如果是练习模式，显示查看解析的按钮
                                TextView questionbank_answerpaper_single_analysisbutton = view5.findViewById(R.id.questionbank_answerpaper_single_analysisbutton);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_single_analysisbutton.getLayoutParams();
                                rl.height = view5.getResources().getDimensionPixelSize(R.dimen.dp_37);
                                rl.topMargin = view5.getResources().getDimensionPixelSize(R.dimen.dp_70);
                                questionbank_answerpaper_single_analysisbutton.setLayoutParams(rl);
                                String finalCurrentAnswer = currentAnswer;
                                questionbank_answerpaper_single_analysisbutton.setOnClickListener(v->{
                                    //隐藏查看解析的按钮，防止多次点击
                                    RelativeLayout.LayoutParams  questionbank_answerpaper_single_analysisbuttonrl = (RelativeLayout.LayoutParams) questionbank_answerpaper_single_analysisbutton.getLayoutParams();
                                    questionbank_answerpaper_single_analysisbuttonrl.height = 0;
                                    questionbank_answerpaper_single_analysisbuttonrl.topMargin = 0;
                                    questionbank_answerpaper_single_analysisbutton.setLayoutParams(questionbank_answerpaper_single_analysisbuttonrl);
                                    LinearLayout questionbank_answerpaper_single_analysis = view5.findViewById(R.id.questionbank_answerpaper_single_analysis);
                                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
                                    questionbank_answerpaper_single_analysis.addView(view);
                                    //修改内容为正确答案
                                    TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
                                    questionbank_analysis1_rightAnswer.setText(finalCurrentAnswer);
                                    //                    /修改内容为此题的解析
                                    TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
                                    if (MyQuestionBankExercisesDataBean1.question_analysis == null) {
                                        MyQuestionBankExercisesDataBean1.question_analysis = "";
                                    }
                                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_analysis);
                                    //修改内容为您的答案
                                    TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
                                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                                    if (answerInfo != null) {
                                        if (answerInfo.answer != null) {
                                            questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                                        } else {
                                            questionbank_analysis1_yourAnswer.setText("");
                                        }
                                    }
                                    //字体大小的设置
                                    if (mFontSize.equals("nomal")) {
                                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                    } else if (mFontSize.equals("small")) {
                                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                    } else if (mFontSize.equals("big")) {
                                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                    }
                                });
                            }
                            //字体大小的设置
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                        } else if (MyQuestionBankExercisesDataBean1.question_type == 14) {//如果是简答题
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                            questionbank_answerpaper_content.addView(view3);
                            TextView questionbank_answerpaper_shortanswersign = view3.findViewById(R.id.questionbank_answerpaper_shortanswersign);
                            questionbank_answerpaper_shortanswersign.setText("第" + (i + 1) + "题： " + type);
                            TextView questionbank_answerpaper_shortanswer = view3.findViewById(R.id.questionbank_answerpaper_shortanswer);
                            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                            questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_name);
                            EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    questionbank_answerpaper_shortansweredittext.setText(answerInfo.answer);
                                }
                            }
                            questionbank_answerpaper_shortansweredittext.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (mMyQuestionBankExercisesAnswerMap != null) {
                                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                                        if (answerInfo == null) {
                                            answerInfo = new AnswerInfo();
                                        }
                                        answerInfo.answer = s.toString();
                                        answerInfo.result = "对";
                                        mMyQuestionBankExercisesAnswerMap.put(MyQuestionBankExercisesDataBean1.question_id, answerInfo);
                                    }
                                }
                            });
                            if ( !mCurrentAnswerMode.equals("exam")) { //判断如果是练习模式，显示查看解析的按钮
                                TextView questionbank_answerpaper_shortanswer_analysisbutton = view3.findViewById(R.id.questionbank_answerpaper_shortanswer_analysisbutton);
                                rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer_analysisbutton.getLayoutParams();
                                rl.height = view3.getResources().getDimensionPixelSize(R.dimen.dp_37);
                                rl.topMargin = view3.getResources().getDimensionPixelSize(R.dimen.dp_70);
                                questionbank_answerpaper_shortanswer_analysisbutton.setLayoutParams(rl);
                                questionbank_answerpaper_shortanswer_analysisbutton.setOnClickListener(v->{
                                    //隐藏查看解析的按钮，防止多次点击
                                    RelativeLayout.LayoutParams  questionbank_answerpaper_shortanswer_analysisbuttonrl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer_analysisbutton.getLayoutParams();
                                    questionbank_answerpaper_shortanswer_analysisbuttonrl.height = 0;
                                    questionbank_answerpaper_shortanswer_analysisbuttonrl.topMargin = 0;
                                    questionbank_answerpaper_shortanswer_analysisbutton.setLayoutParams(questionbank_answerpaper_shortanswer_analysisbuttonrl);
                                    LinearLayout questionbank_answerpaper_shortanswer_analysis = view3.findViewById(R.id.questionbank_answerpaper_shortanswer_analysis);
                                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
                                    questionbank_answerpaper_shortanswer_analysis.addView(view);
                                    //修改内容为正确答案
                                    TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
                                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(MyQuestionBankExercisesDataBean1.optionanswer);
                                    //修改内容为此题的解析
                                    TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
                                    if (MyQuestionBankExercisesDataBean1.question_analysis == null) {
                                        MyQuestionBankExercisesDataBean1.question_analysis = "";
                                    }
                                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_analysis);
                                    //修改内容为您的答案
                                    TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
                                    AnswerInfo answerInfo1 = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                                    if (answerInfo1 != null) {
                                        if (answerInfo1.answer != null) {
                                            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo1.answer);
                                        } else {
                                            questionbank_analysis2_yourAnswer.setText("");
                                        }
                                    }
                                    if (mFontSize.equals("nomal")) {
                                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                    } else if (mFontSize.equals("small")) {
                                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                    } else if (mFontSize.equals("big")) {
                                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                    }
                                });
                            }
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    //试卷答题-添加问题界面
    private void TestPaper_QuestionViewAdd() {
        if (mMyTestPageIssueDataBeans == null){
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCurrentIndex < 0 ){
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return;
        }
        View view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
        LinearLayout coursedetails_answerpaper_details = mModelQuestionBankAnswerPaperView.findViewById(R.id.coursedetails_answerpaper_details);
        coursedetails_answerpaper_details.removeAllViews();
        coursedetails_answerpaper_details.addView(view2);
        //字符串分割
        if (mCurrentIndex >= mMyTestPageIssueDataBeans.size()) { //不在数组范围直接返回
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return;
        }
        MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean = mMyTestPageIssueDataBeans.get(mCurrentIndex);
        if (myTestPageIssueDataBean == null){
            Toast.makeText(mControlMainActivity, "获取试题失败", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView questionbank_answerpaper_sign = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_sign);
        if (myTestPageIssueDataBean.tf_marked != null) {
            if (myTestPageIssueDataBean.tf_marked == 2) {
                questionbank_answerpaper_sign.setBackground(mModelQuestionBankAnswerPaperView.getResources().getDrawable(R.drawable.button_questionbank_sign));
                mIsSign = false;
            } else if (myTestPageIssueDataBean.tf_marked == 1) {
                //标记
                questionbank_answerpaper_sign.setBackground(mModelQuestionBankAnswerPaperView.getResources().getDrawable(R.drawable.button_questionbank_sign_blue));
                mIsSign = true;
            }
        } else {
            questionbank_answerpaper_sign.setBackground(mModelQuestionBankAnswerPaperView.getResources().getDrawable(R.drawable.button_questionbank_sign));
            mIsSign = false;
        }
        //总体的数量
        TextView questionbank_answerpaper_questioncountsum = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncountsum);
        questionbank_answerpaper_questioncountsum.setText("/" + mMyTestPageIssueDataBeans.size());
        if (mMyTestPageIssueDataBeans.size() > 0) {
            //案列分析的解析
            TextView questionbank_answerpaper_questioncount = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questioncount);
            questionbank_answerpaper_questioncount.setText(String.valueOf(mCurrentIndex + 1));
            TextView questionbank_answerpaper_single_title = view2.findViewById(R.id.questionbank_answerpaper_single_title);
            if (mFontSize.equals("nomal")) {
                questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title).setHtmlWithPic(myTestPageIssueDataBean.question_name);
            questionbank_answerpaper_single_title.setHint(myTestPageIssueDataBean.question_id + "");
            //判断当前选择题的类型
            TextView questionbank_answerpaper_questiontype = mModelQuestionBankAnswerPaperView.findViewById(R.id.questionbank_answerpaper_questiontype);
            if (myTestPageIssueDataBean.question_type == null) {
                return;
            }
            if (myTestPageIssueDataBean.question_type == 1) {
                questionbank_answerpaper_questiontype.setText("[单选题]");
            } else if (myTestPageIssueDataBean.question_type == 2) {
                questionbank_answerpaper_questiontype.setText("[多选题]");
            } else if (myTestPageIssueDataBean.question_type == 4) {
                questionbank_answerpaper_questiontype.setText("[简答题]");
            } else if (myTestPageIssueDataBean.question_type == 7) {
                questionbank_answerpaper_questiontype.setText("[材料题]");
            }
            LinearLayout questionbank_answerpaper_content = view2.findViewById(R.id.questionbank_answerpaper_content);
            questionbank_answerpaper_content.removeAllViews();
            if (myTestPageIssueDataBean.question_type == 1 || myTestPageIssueDataBean.question_type == 2) { //如果是单选题或多选题添加选项布局
                if (myTestPageIssueDataBean.optionanswer == null){
                    return;
                }
                String[] optionanswerS = myTestPageIssueDataBean.optionanswer.split("#EDU;");
                if (optionanswerS == null) {
                    return;
                }
                for (int i = 0; i < optionanswerS.length; i ++) {
                    View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                    String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                    if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                        continue;
                    }
                    TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                    questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                    questionbank_answerpaper_option_name.setOnClickListener(v -> {
                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                        if (myTestPageIssueDataBean.question_type == 1) { //如果是单选题，将其他选项置为false
                            int count = questionbank_answerpaper_content.getChildCount();
                            for (int num = 0; num < count; num ++) {
                                View view4 = questionbank_answerpaper_content.getChildAt(num);
                                if (view4 != view3) {
                                    TextView textview = view4.findViewById(R.id.questionbank_answerpaper_option_name);
                                    textview.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                    textview.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                }
                            }
                            AnswerInfo answerInfo = new AnswerInfo();
                            answerInfo.answer = optionanswerS1[0];
                            if (optionanswerS1[1].equals("是")){
                                answerInfo.result = "对";
                            } else {
                                answerInfo.result = "错";
                            }
                            mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id,answerInfo);
                        } else {
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    if (answerInfo.answer.contains(optionanswerS1[0])) {
                                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                        answerInfo.answer = answerInfo.answer.replace(optionanswerS1[0], "");
                                    } else {
                                        answerInfo.answer = answerInfo.answer + optionanswerS1[0];
                                    }
                                    //将选项排序
                                    String[] strArr = new String[answerInfo.answer.length()];
                                    for (int num = 0; num < answerInfo.answer.length(); num ++){
                                        strArr[num] = answerInfo.answer.substring(num,num + 1);
                                    }
                                    Arrays.sort(strArr,String.CASE_INSENSITIVE_ORDER);
                                    answerInfo.answer = Arrays.toString(strArr).replace("[","");
                                    answerInfo.answer = answerInfo.answer.replace("]","");
                                    answerInfo.answer = answerInfo.answer.replaceAll(",","");
                                    answerInfo.answer = answerInfo.answer.replaceAll(" ","");
                                    if (answerInfo.answer.equals(myTestPageIssueDataBean.answer)){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                } else {
                                    answerInfo.answer = optionanswerS1[0];
                                    if (answerInfo.answer.equals(myTestPageIssueDataBean.answer)){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                }
                            } else {
                                answerInfo = new AnswerInfo();
                                answerInfo.answer = optionanswerS1[0];
                                if (answerInfo.answer.equals(myTestPageIssueDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                            }
                            mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id,answerInfo);
                        }
                    });
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean.question_id);
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (answerInfo.answer.contains(optionanswerS1[0])) {
                                questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                            }
                        }
                    }
                    TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                    questionbank_answerpaper_content.addView(view3);
                }
            } else if (myTestPageIssueDataBean.question_type == 4) {//如果是简答题
                View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                questionbank_answerpaper_content.addView(view3);
                EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                if (mFontSize.equals("nomal")) {
                    questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                } else if (mFontSize.equals("small")) {
                    questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                } else if (mFontSize.equals("big")) {
                    questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                }
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean.question_id);
                if (answerInfo != null) {
                    if (answerInfo.answer != null) {
                        questionbank_answerpaper_shortansweredittext.setText(answerInfo.answer);
                    }
                }
                questionbank_answerpaper_shortansweredittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mMyQuestionBankExercisesAnswerMap != null) {
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean.question_id);
                            if (answerInfo == null) {
                                answerInfo = new AnswerInfo();
                            }
                            answerInfo.answer = s.toString();
                            answerInfo.result = "对";
                            mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id, answerInfo);
                        }
                    }
                });
            } else if (myTestPageIssueDataBean.question_type == 7) {//如果是材料题
                View contentView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                questionbank_answerpaper_content.addView(contentView);
                EditText contentViewedittext = contentView.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                if (myTestPageIssueDataBean.optionanswer != null) {
                    TextView questionbank_answerpaper_shortanswer = contentView.findViewById(R.id.questionbank_answerpaper_shortanswer);
                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                    rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                    rl = (RelativeLayout.LayoutParams) contentViewedittext.getLayoutParams();
                    rl.height = 0;
                    rl.topMargin = 0;
                    contentViewedittext.setLayoutParams(rl);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(myTestPageIssueDataBean.optionanswer);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                }
                if (myTestPageIssueDataBean.question_id_group1 != null ) { // 说明有子题
                    for (int i = 0; i < myTestPageIssueDataBean.question_id_group1.size(); i++) {
                        MyTestPageIssueBean.MyTestPageIssueDataBean MyTestPageIssueDataBean1 = myTestPageIssueDataBean.question_id_group1.get(i);
                        if (MyTestPageIssueDataBean1 == null) {
                            continue;
                        }
                        String type = "";
                        if (MyTestPageIssueDataBean1.question_type == 11) {
                            type = "[单选题] ";
                        } else if (MyTestPageIssueDataBean1.question_type == 12) {
                            type = "[多选题] ";
                        } else if (MyTestPageIssueDataBean1.question_type == 14) {
                            type = "[简答题] ";
                        }
                        if (MyTestPageIssueDataBean1.question_type == 11 || MyTestPageIssueDataBean1.question_type == 12) { //如果是单选题或多选题添加选项布局
                            if (MyTestPageIssueDataBean1.optionanswer == null) {
                                continue;
                            }
                            String[] optionanswerS = MyTestPageIssueDataBean1.optionanswer.split("#EDU;");
                            if (optionanswerS == null) {
                                continue;
                            }
                            View view5 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
                            questionbank_answerpaper_content.addView(view5);
                            TextView questionbank_answerpaper_single_titlesign = view5.findViewById(R.id.questionbank_answerpaper_single_titlesign);
                            questionbank_answerpaper_single_titlesign.setText("第" + (i + 1) + "题： " + type);
                            LinearLayout questionbank_answerpaper_content1 = view5.findViewById(R.id.questionbank_answerpaper_content);
                            TextView questionbank_answerpaper_single_title1 = view5.findViewById(R.id.questionbank_answerpaper_single_title);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title1).setHtmlWithPic(MyTestPageIssueDataBean1.question_name);
                            String currentAnswer = "";
                            for (String string : optionanswerS) {
                                View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                                String[] optionanswerS1 = string.substring(1).split("#");
                                if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                                    continue;
                                }
                                if (optionanswerS1[1].equals("是")) {
                                    currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                                    if (MyTestPageIssueDataBean1.answer == null){
                                        MyTestPageIssueDataBean1.answer = currentAnswer;
                                    }
                                }
                                TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                                questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                                questionbank_answerpaper_option_name.setOnClickListener(v -> { // 选项的点击响应
                                    questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                    questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                    if (MyTestPageIssueDataBean1.question_type == 11) { //如果是单选题，将其他选项置为false
                                        int count = questionbank_answerpaper_content1.getChildCount();
                                        for (int num = 0; num < count; num ++) {
                                            View view4 = questionbank_answerpaper_content1.getChildAt(num);
                                            if (view4 != view3) {
                                                TextView textview = view4.findViewById(R.id.questionbank_answerpaper_option_name);
                                                textview.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                                textview.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                            }
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        answerInfo.answer = optionanswerS1[0];
                                        if (optionanswerS1[1].equals("是")){
                                            answerInfo.result = "对";
                                        } else {
                                            answerInfo.result = "错";
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyTestPageIssueDataBean1.question_id,answerInfo);
                                    } else {
                                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyTestPageIssueDataBean1.question_id);
                                        if (answerInfo != null) {
                                            if (answerInfo.answer != null) {
                                                if (answerInfo.answer.contains(optionanswerS1[0])) {
                                                    questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                                    questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                                    answerInfo.answer = answerInfo.answer.replace(optionanswerS1[0], "");
                                                } else {
                                                    answerInfo.answer = answerInfo.answer + optionanswerS1[0];
                                                }
                                                //将选项排序
                                                String[] strArr = new String[answerInfo.answer.length()];
                                                for (int num = 0; num < answerInfo.answer.length(); num ++){
                                                    strArr[num] = answerInfo.answer.substring(num,num + 1);
                                                }
                                                Arrays.sort(strArr,String.CASE_INSENSITIVE_ORDER);
                                                answerInfo.answer = Arrays.toString(strArr).replace("[","");
                                                answerInfo.answer = answerInfo.answer.replace("]","");
                                                answerInfo.answer = answerInfo.answer.replaceAll(",","");
                                                answerInfo.answer = answerInfo.answer.replaceAll(" ","");
                                                if (answerInfo.answer.equals(MyTestPageIssueDataBean1.answer)){
                                                    answerInfo.result = "对";
                                                } else {
                                                    answerInfo.result = "错";
                                                }
                                            } else {
                                                answerInfo.answer = optionanswerS1[0];
                                                if (answerInfo.answer.equals(MyTestPageIssueDataBean1.answer)){
                                                    answerInfo.result = "对";
                                                } else {
                                                    answerInfo.result = "错";
                                                }
                                            }
                                        } else {
                                            answerInfo = new AnswerInfo();
                                            answerInfo.answer = optionanswerS1[0];
                                            if (answerInfo.answer.equals(MyTestPageIssueDataBean1.answer)){
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyTestPageIssueDataBean1.question_id,answerInfo);
                                    }
                                });
                                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyTestPageIssueDataBean1.question_id);
                                if (answerInfo != null) {
                                    if (answerInfo.answer != null) {
                                        if (answerInfo.answer.contains(optionanswerS1[0])) {
                                            questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                            questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                        }
                                    }
                                }
                                TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                                if (mFontSize.equals("nomal")) {
                                    questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                } else if (mFontSize.equals("small")) {
                                    questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                } else if (mFontSize.equals("big")) {
                                    questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                }
                                questionbank_answerpaper_content1.addView(view3);
                            }
                            //字体大小的设置
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                        } else if (MyTestPageIssueDataBean1.question_type == 14) {//如果是简答题
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                            questionbank_answerpaper_content.addView(view3);
                            TextView questionbank_answerpaper_shortanswersign = view3.findViewById(R.id.questionbank_answerpaper_shortanswersign);
                            questionbank_answerpaper_shortanswersign.setText("第" + (i + 1) + "题： " + type);
                            TextView questionbank_answerpaper_shortanswer = view3.findViewById(R.id.questionbank_answerpaper_shortanswer);
                            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                            questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(MyTestPageIssueDataBean1.question_name);
                            EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                            questionbank_answerpaper_shortansweredittext.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (mMyQuestionBankExercisesAnswerMap != null) {
                                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyTestPageIssueDataBean1.question_id);
                                        if (answerInfo == null) {
                                            answerInfo = new AnswerInfo();
                                        }
                                        answerInfo.answer = s.toString();
                                        answerInfo.result = "对";
                                        mMyQuestionBankExercisesAnswerMap.put(MyTestPageIssueDataBean1.question_id, answerInfo);
                                    }
                                }
                            });
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                        }
                    }
                }
            }
        }
    }

    //添加答题卡-问题界面
    private void HandInAnalysisQuestionViewAdd(ImageView questionbank_handin_analysis_collection,MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean,Integer questionSize) {
        if (questionSize == null || myQuestionBankExercisesDataBean == null){
            return;
        }
        View view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
        LinearLayout coursedetails_handin_analysis_details = mModelQuestionBankHandInAnalysisView.findViewById(R.id.coursedetails_handin_analysis_details);
        coursedetails_handin_analysis_details.removeAllViews();
        coursedetails_handin_analysis_details.addView(view2);
        if (mCurrentIndex < 0 || mCurrentIndex >= questionSize) { //不在数组范围直接返回
            return;
        }
        if (questionSize <= 0) {
            return;
        }
        if (myQuestionBankExercisesDataBean.tf_collection != null && questionbank_handin_analysis_collection != null){
            if (myQuestionBankExercisesDataBean.tf_collection == 1){
                questionbank_handin_analysis_collection.setBackground(mModelQuestionBankHandInAnalysisView.getResources().getDrawable(R.drawable.button_collect_enable));
                mIsCollect = true;
            } else if (myQuestionBankExercisesDataBean.tf_collection == 2){
                questionbank_handin_analysis_collection.setBackground(mModelQuestionBankHandInAnalysisView.getResources().getDrawable(R.drawable.button_collect_disable_black));
                mIsCollect = false;
            }
        }
        TextView questionbank_handin_analysis_questioncountsum = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_questioncountsum);
        questionbank_handin_analysis_questioncountsum.setText("/" + questionSize);
        TextView questionbank_handin_analysis_questioncount = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_questioncount);
        questionbank_handin_analysis_questioncount.setText(String.valueOf(mCurrentIndex + 1));
        TextView questionbank_answerpaper_single_title = view2.findViewById(R.id.questionbank_answerpaper_single_title);
        if (mFontSize.equals("nomal")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
        } else if (mFontSize.equals("small")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
        } else if (mFontSize.equals("big")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
        }
        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title).setHtmlWithPic(myQuestionBankExercisesDataBean.question_name);
        questionbank_answerpaper_single_title.setHint(myQuestionBankExercisesDataBean.question_id + "");
        //单选题或者多选题赋值
        TextView questionbank_handin_analysis_questiontype = mModelQuestionBankHandInAnalysisView.findViewById(R.id.questionbank_handin_analysis_questiontype);
        if (myQuestionBankExercisesDataBean.question_type == 1) {
            questionbank_handin_analysis_questiontype.setText("[单选题]");
        } else if (myQuestionBankExercisesDataBean.question_type == 2) {
            questionbank_handin_analysis_questiontype.setText("[多选题]");
        } else if (myQuestionBankExercisesDataBean.question_type == 4) {
            questionbank_handin_analysis_questiontype.setText("[简答题]");
        } else if (myQuestionBankExercisesDataBean.question_type == 7) {
            questionbank_handin_analysis_questiontype.setText("[材料题]");
        }

        LinearLayout questionbank_answerpaper_content = view2.findViewById(R.id.questionbank_answerpaper_content);
        questionbank_answerpaper_content.removeAllViews();
        if (myQuestionBankExercisesDataBean.question_type == 1 || myQuestionBankExercisesDataBean.question_type == 2) { //如果是单选题或多选题添加选项布局
            String[] optionanswerS = myQuestionBankExercisesDataBean.optionanswer.split("#EDU;");
            if (optionanswerS != null) {
                for (int i = 0; i < optionanswerS.length; i++) {
                    View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                    String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                    if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                        continue;
                    }
                    TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                    questionbank_answerpaper_option_name.setText(optionanswerS1[0]);
                    questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                    TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                    questionbank_answerpaper_content.addView(view3);
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (answerInfo.answer.contains(optionanswerS1[0])){
                                questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                            }
                        }
                    }
                }
            }
        } else if (myQuestionBankExercisesDataBean.question_type == 4) {//如果是简答题
            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
            questionbank_answerpaper_content.addView(view3);
            EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            if (mFontSize.equals("nomal")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
            //设置不可编辑，交卷以后不能输入
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortansweredittext.getLayoutParams();
            rl.height = 0;
            rl.topMargin = 0;
            questionbank_answerpaper_shortansweredittext.setLayoutParams(rl);
        } else if (myQuestionBankExercisesDataBean.question_type == 7) {//如果是材料题
            View contentView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
            questionbank_answerpaper_content.addView(contentView);
            EditText contentViewedittext = contentView.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            if (myQuestionBankExercisesDataBean.optionanswer != null) {
                TextView questionbank_answerpaper_shortanswer = contentView.findViewById(R.id.questionbank_answerpaper_shortanswer);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                rl = (RelativeLayout.LayoutParams) contentViewedittext.getLayoutParams();
                rl.height = 0;
                rl.topMargin = 0;
                contentViewedittext.setLayoutParams(rl);
                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(myQuestionBankExercisesDataBean.optionanswer);
                if (mFontSize.equals("nomal")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                } else if (mFontSize.equals("small")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                } else if (mFontSize.equals("big")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                }
            }
            if (myQuestionBankExercisesDataBean.question_sub_group != null || myQuestionBankExercisesDataBean.question_id_group1 != null) { // 说明有子题
                if (myQuestionBankExercisesDataBean.question_id_group1 == null){
                    myQuestionBankExercisesDataBean.question_id_group1 = myQuestionBankExercisesDataBean.question_sub_group;
                }
                for (int i = 0; i < myQuestionBankExercisesDataBean.question_id_group1.size(); i++) {
                    MyQuestionBankExercises.MyQuestionBankExercisesDataBean MyQuestionBankExercisesDataBean1 = myQuestionBankExercisesDataBean.question_id_group1.get(i);
                    if (MyQuestionBankExercisesDataBean1 == null) {
                        continue;
                    }
                    String type = "";
                    if (MyQuestionBankExercisesDataBean1.question_type == 11) {
                        type = "[单选题] ";
                    } else if (MyQuestionBankExercisesDataBean1.question_type == 12) {
                        type = "[多选题] ";
                    } else if (MyQuestionBankExercisesDataBean1.question_type == 14) {
                        type = "[简答题] ";
                    }
                    if (MyQuestionBankExercisesDataBean1.question_type == 11 || MyQuestionBankExercisesDataBean1.question_type == 12) { //如果是单选题或多选题添加选项布局
                        if (MyQuestionBankExercisesDataBean1.optionanswer == null) {
                            continue;
                        }
                        String[] optionanswerS = MyQuestionBankExercisesDataBean1.optionanswer.split("#EDU;");
                        if (optionanswerS == null) {
                            continue;
                        }
                        View view5 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
                        questionbank_answerpaper_content.addView(view5);
                        TextView questionbank_answerpaper_single_titlesign = view5.findViewById(R.id.questionbank_answerpaper_single_titlesign);
                        questionbank_answerpaper_single_titlesign.setText("第" + (i + 1) + "题： " + type);
                        LinearLayout questionbank_answerpaper_content1 = view5.findViewById(R.id.questionbank_answerpaper_content);
                        TextView questionbank_answerpaper_single_title1 = view5.findViewById(R.id.questionbank_answerpaper_single_title);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title1).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_name);
                        String currentAnswer = "";
                        for (String string : optionanswerS) {
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                            String[] optionanswerS1 = string.substring(1).split("#");
                            if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                                continue;
                            }
                            if (optionanswerS1[1].equals("是")) {
                                currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                                if (MyQuestionBankExercisesDataBean1.answer == null){
                                    MyQuestionBankExercisesDataBean1.answer = currentAnswer;
                                }
                            }
                            TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                            questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    if (answerInfo.answer.contains(optionanswerS1[0])) {
                                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                    }
                                }
                            }
                            TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                            questionbank_answerpaper_content1.addView(view3);
                        }
                        LinearLayout questionbank_answerpaper_single_analysis = view5.findViewById(R.id.questionbank_answerpaper_single_analysis);
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
                        questionbank_answerpaper_single_analysis.addView(view);
                        //修改内容为正确答案
                        TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
                        questionbank_analysis1_rightAnswer.setText(currentAnswer);
//                    /修改内容为此题的解析
                        TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
                        if (MyQuestionBankExercisesDataBean1.question_analysis == null) {
                            MyQuestionBankExercisesDataBean1.question_analysis = "";
                        }
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_analysis);
                        //修改内容为您的答案
                        TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                        if (answerInfo != null) {
                            if (answerInfo.answer != null) {
                                questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                            } else {
                                questionbank_analysis1_yourAnswer.setText("");
                            }
                        }
                        //字体大小的设置
                        if (mFontSize.equals("nomal")) {
                            questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        } else if (mFontSize.equals("small")) {
                            questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        } else if (mFontSize.equals("big")) {
                            questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        }
                    } else if (MyQuestionBankExercisesDataBean1.question_type == 14) {//如果是简答题
                        View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                        questionbank_answerpaper_content.addView(view3);
                        TextView questionbank_answerpaper_shortanswersign = view3.findViewById(R.id.questionbank_answerpaper_shortanswersign);
                        questionbank_answerpaper_shortanswersign.setText("第" + (i + 1) + "题： " + type);
                        TextView questionbank_answerpaper_shortanswer = view3.findViewById(R.id.questionbank_answerpaper_shortanswer);
                        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                        rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_name);
                        EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                        rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortansweredittext.getLayoutParams();
                        rl.height = 0;
                        rl.topMargin = 0;
                        questionbank_answerpaper_shortansweredittext.setLayoutParams(rl);
                        LinearLayout questionbank_answerpaper_shortanswer_analysis = view3.findViewById(R.id.questionbank_answerpaper_shortanswer_analysis);
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
                        questionbank_answerpaper_shortanswer_analysis.addView(view);
                        //修改内容为正确答案
                        TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(MyQuestionBankExercisesDataBean1.optionanswer);
                        //修改内容为此题的解析
                        TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
                        if (MyQuestionBankExercisesDataBean1.question_analysis == null) {
                            MyQuestionBankExercisesDataBean1.question_analysis = "";
                        }
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(MyQuestionBankExercisesDataBean1.question_analysis);
                        //修改内容为您的答案
                        TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(MyQuestionBankExercisesDataBean1.question_id);
                        if (answerInfo != null) {
                            if (answerInfo.answer != null) {
                                new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo.answer);
                            } else {
                                questionbank_analysis2_yourAnswer.setText("");
                            }
                        }
                        if (mFontSize.equals("nomal")) {
                            questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        } else if (mFontSize.equals("small")) {
                            questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        } else if (mFontSize.equals("big")) {
                            questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        }
                    }
                }
            }
        }
        //添加题的解析（解析题的答案）
        LinearLayout coursedetails_handin_analysis_analysis = mModelQuestionBankHandInAnalysisView.findViewById(R.id.coursedetails_handin_analysis_analysis);
        coursedetails_handin_analysis_analysis.removeAllViews();
        if (myQuestionBankExercisesDataBean.question_type == 1 || myQuestionBankExercisesDataBean.question_type == 2) {//单选题或多选题
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
            coursedetails_handin_analysis_analysis.addView(view);
            //修改内容为正确答案
            String[] optionanswerS = myQuestionBankExercisesDataBean.optionanswer.split("#EDU;");
            if (optionanswerS == null) {
                return;
            }
            String currentAnswer = "";
            for (int i = 0; i < optionanswerS.length; i++) {
                String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                if (optionanswerS1.length != 3) {
                    break;
                }
                if (optionanswerS1[1].equals("是")) {
                    currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                }
            }
            //正确答案内容
            TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
            questionbank_analysis1_rightAnswer.setText(currentAnswer);
//                    /修改内容为此题的解析
            TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
            if (myQuestionBankExercisesDataBean.question_analysis == null) {
                myQuestionBankExercisesDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(myQuestionBankExercisesDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
            if (answerInfo != null) {
                if (answerInfo.answer != null) {
                    questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                } else {
                    questionbank_analysis1_yourAnswer.setText("");
                }
            }
            //字体大小的设置
            if (mFontSize.equals("nomal")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        } else if (myQuestionBankExercisesDataBean.question_type == 4) {//简答题
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
            coursedetails_handin_analysis_analysis.addView(view);
            //修改内容为正确答案
            TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
            questionbank_analysis2_rightAnswer.setText(myQuestionBankExercisesDataBean.optionanswer);
            //修改内容为此题的解析
            TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
            if (myQuestionBankExercisesDataBean.question_analysis == null) {
                myQuestionBankExercisesDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(myQuestionBankExercisesDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
            if (answerInfo != null) {
                if (answerInfo.answer != null) {
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo.answer);
                } else {
                    questionbank_analysis2_yourAnswer.setText("");
                }
            }
            if (mFontSize.equals("nomal")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        }
    }

    //添加错题本-问题界面
    private void WrongQuestionViewAdd(ImageView questionbank_wrongquestion_collection) {
        if (mMyFavoriteQuestionDataBeans == null){
            return;
        }
        View view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
        LinearLayout coursedetails_wrongquestion_details = mModelQuestionBankWrongQuestionView.findViewById(R.id.coursedetails_wrongquestion_details);
        coursedetails_wrongquestion_details.removeAllViews();
        coursedetails_wrongquestion_details.addView(view2);
        if (mCurrentIndex < 0 || mCurrentIndex >= mMyFavoriteQuestionDataBeans.size()) { //不在数组范围直接返回
            return;
        }
        TextView questionbank_wrongquestion_questioncountsum = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questioncountsum);
        questionbank_wrongquestion_questioncountsum.setText("/" + mMyFavoriteQuestionDataBeans.size());
        //错题本的数量
        TextView questionbank_wrongquestion_questioncount = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questioncount);
        questionbank_wrongquestion_questioncount.setText(String.valueOf(mCurrentIndex + 1));
        QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean = mMyFavoriteQuestionDataBeans.get(mCurrentIndex);
        if (questionBankMyFavoriteQuestionDataBean == null){
            return;
        }
        if (questionBankMyFavoriteQuestionDataBean.tf_collection != null && questionbank_wrongquestion_collection != null){
            if (questionBankMyFavoriteQuestionDataBean.tf_collection == 1){
                questionbank_wrongquestion_collection.setBackground(mModelQuestionBankWrongQuestionView.getResources().getDrawable(R.drawable.button_collect_enable));
                mIsCollect = true;
            } else if (questionBankMyFavoriteQuestionDataBean.tf_collection == 2){
                questionbank_wrongquestion_collection.setBackground(mModelQuestionBankWrongQuestionView.getResources().getDrawable(R.drawable.button_collect_disable_black));
                mIsCollect = false;
            }
        }
        TextView questionbank_answerpaper_single_title = view2.findViewById(R.id.questionbank_answerpaper_single_title);
        if (mFontSize.equals("nomal")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
        } else if (mFontSize.equals("small")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
        } else if (mFontSize.equals("big")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
        }
        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_name);
        questionbank_answerpaper_single_title.setHint(questionBankMyFavoriteQuestionDataBean.question_id + "");
        TextView questionbank_wrongquestion_questiontype = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questiontype);
        if (questionBankMyFavoriteQuestionDataBean.question_type == 1) {
            questionbank_wrongquestion_questiontype.setText("[单选题]");
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 2) {
            questionbank_wrongquestion_questiontype.setText("[多选题]");
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {
            questionbank_wrongquestion_questiontype.setText("[简答题]");
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 7) {
            questionbank_wrongquestion_questiontype.setText("[材料题]");
        }
        LinearLayout questionbank_answerpaper_content = view2.findViewById(R.id.questionbank_answerpaper_content);
        questionbank_answerpaper_content.removeAllViews();
        if (questionBankMyFavoriteQuestionDataBean.question_type == 1 || questionBankMyFavoriteQuestionDataBean.question_type == 2) { //如果是单选题或多选题添加选项布局
            String[] optionanswerS = questionBankMyFavoriteQuestionDataBean.optionanswer.split("#EDU;");
            if (optionanswerS != null) {
                for (int i = 0; i < optionanswerS.length; i++) {
                    View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                    String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                    if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                        continue;
                    }
                    TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                    questionbank_answerpaper_option_name.setText(optionanswerS1[0]);
                    questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                    TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                    questionbank_answerpaper_option_title.setText(optionanswerS1[2]);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                    questionbank_answerpaper_content.addView(view3);
                    questionbank_answerpaper_option_name.setOnClickListener(v -> {
                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                        if (questionBankMyFavoriteQuestionDataBean.question_type == 1) { //如果是单选题，将其他选项置为false
                            int count = questionbank_answerpaper_content.getChildCount();
                            for (int num = 0; num < count; num++) {
                                View view4 = questionbank_answerpaper_content.getChildAt(num);
                                if (view4 != view3) {
                                    TextView textview = view4.findViewById(R.id.questionbank_answerpaper_option_name);
                                    textview.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                    textview.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                }
                            }
                            AnswerInfo answerInfo = new AnswerInfo();
                            answerInfo.answer = optionanswerS1[0];
                            if (optionanswerS1[1].equals("是")){
                                answerInfo.result = "对";
                            } else {
                                answerInfo.result = "错";
                            }
                            mMyQuestionBankExercisesAnswerMap.put(questionBankMyFavoriteQuestionDataBean.question_id,answerInfo);
                            //单选题选完直接显示解析
                            ModeWrongQuestionFindAnalysis( questionBankMyFavoriteQuestionDataBean);
                        } else {
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    if (answerInfo.answer.contains(optionanswerS1[0])) {
                                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                        answerInfo.answer = answerInfo.answer.replace(optionanswerS1[0], "");
                                    } else {
                                        answerInfo.answer = answerInfo.answer + optionanswerS1[0];
                                    }
                                    //将选项排序
                                    String[] strArr = new String[answerInfo.answer.length()];
                                    for (int num = 0; num < answerInfo.answer.length(); num ++){
                                        strArr[num] = answerInfo.answer.substring(num,num + 1);
                                    }
                                    Arrays.sort(strArr,String.CASE_INSENSITIVE_ORDER);
                                    answerInfo.answer = Arrays.toString(strArr).replace("[","");
                                    answerInfo.answer = answerInfo.answer.replace("]","");
                                    answerInfo.answer = answerInfo.answer.replaceAll(",","");
                                    answerInfo.answer = answerInfo.answer.replaceAll(" ","");
                                    if (answerInfo.answer.equals(questionBankMyFavoriteQuestionDataBean.answer)){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                } else {
                                    answerInfo.answer = optionanswerS1[0];
                                    if (answerInfo.answer.equals(questionBankMyFavoriteQuestionDataBean.answer)){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                }
                            } else {
                                answerInfo = new AnswerInfo();
                                answerInfo.answer = optionanswerS1[0];
                                if (answerInfo.answer.equals(questionBankMyFavoriteQuestionDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                            }
                            mMyQuestionBankExercisesAnswerMap.put(questionBankMyFavoriteQuestionDataBean.question_id,answerInfo);
                        }
                    });
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (answerInfo.answer.contains(optionanswerS1[0])){
                                questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                            }
                        }
                    }
                }
            }
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {//如果是简答题
            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
            questionbank_answerpaper_content.addView(view3);
            //请输入你的答案
            EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            if (mFontSize.equals("nomal")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
            questionbank_answerpaper_shortansweredittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mMyQuestionBankExercisesAnswerMap != null) {
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                        if (answerInfo == null) {
                            answerInfo = new AnswerInfo();
                        }
                        answerInfo.answer = s.toString();
                        answerInfo.result = "对";
                        mMyQuestionBankExercisesAnswerMap.put(questionBankMyFavoriteQuestionDataBean.question_id, answerInfo);
                    }
                }
            });
            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
            if (answerInfo != null) {
                if (answerInfo.answer != null) {
                    questionbank_answerpaper_shortansweredittext.setText(answerInfo.answer);
                } else {
                    questionbank_answerpaper_shortansweredittext.setText("");
                }
            }
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 7) {
            View contentView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
            questionbank_answerpaper_content.addView(contentView);
            EditText contentViewedittext = contentView.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            if (questionBankMyFavoriteQuestionDataBean.optionanswer != null) {
                TextView questionbank_answerpaper_shortanswer = contentView.findViewById(R.id.questionbank_answerpaper_shortanswer);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                rl = (RelativeLayout.LayoutParams) contentViewedittext.getLayoutParams();
                rl.height = 0;
                rl.topMargin = 0;
                contentViewedittext.setLayoutParams(rl);
                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.optionanswer);
                if (mFontSize.equals("nomal")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                } else if (mFontSize.equals("small")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                } else if (mFontSize.equals("big")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                }
            }
            if (questionBankMyFavoriteQuestionDataBean.question_sub_id != null ) { // 说明有子题
                for (int i = 0; i < questionBankMyFavoriteQuestionDataBean.question_sub_id.size(); i++) {
                    QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean QuestionBankMyFavoriteQuestionDataBean1 = questionBankMyFavoriteQuestionDataBean.question_sub_id.get(i);
                    if (QuestionBankMyFavoriteQuestionDataBean1 == null) {
                        continue;
                    }
                    String type = "";
                    if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 11) {
                        type = "[单选题] ";
                    } else if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 12) {
                        type = "[多选题] ";
                    } else if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 14) {
                        type = "[简答题] ";
                    }
                    if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 11 || QuestionBankMyFavoriteQuestionDataBean1.question_type == 12) { //如果是单选题或多选题添加选项布局
                        if (QuestionBankMyFavoriteQuestionDataBean1.optionanswer == null) {
                            continue;
                        }
                        String[] optionanswerS = QuestionBankMyFavoriteQuestionDataBean1.optionanswer.split("#EDU;");
                        if (optionanswerS == null) {
                            continue;
                        }
                        View view5 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
                        questionbank_answerpaper_content.addView(view5);
                        TextView questionbank_answerpaper_single_titlesign = view5.findViewById(R.id.questionbank_answerpaper_single_titlesign);
                        questionbank_answerpaper_single_titlesign.setText("第" + (i + 1) + "题： " + type);
                        LinearLayout questionbank_answerpaper_content1 = view5.findViewById(R.id.questionbank_answerpaper_content);
                        TextView questionbank_answerpaper_single_title1 = view5.findViewById(R.id.questionbank_answerpaper_single_title);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title1).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_name);
                        String currentAnswer = "";
                        for (String string : optionanswerS) {
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                            String[] optionanswerS1 = string.substring(1).split("#");
                            if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                                continue;
                            }
                            if (optionanswerS1[1].equals("是")) {
                                currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                                if (QuestionBankMyFavoriteQuestionDataBean1.answer == null){
                                    QuestionBankMyFavoriteQuestionDataBean1.answer = currentAnswer;
                                }
                            }
                            TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                            questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                            questionbank_answerpaper_option_name.setOnClickListener(v -> { // 选项的点击响应
                                questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 11) { //如果是单选题，将其他选项置为false
                                    int count = questionbank_answerpaper_content1.getChildCount();
                                    for (int num = 0; num < count; num ++) {
                                        View view4 = questionbank_answerpaper_content1.getChildAt(num);
                                        if (view4 != view3) {
                                            TextView textview = view4.findViewById(R.id.questionbank_answerpaper_option_name);
                                            textview.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                            textview.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                        }
                                    }
                                    AnswerInfo answerInfo = new AnswerInfo();
                                    answerInfo.answer = optionanswerS1[0];
                                    if (optionanswerS1[1].equals("是")){
                                        answerInfo.result = "对";
                                    } else {
                                        answerInfo.result = "错";
                                    }
                                    mMyQuestionBankExercisesAnswerMap.put(QuestionBankMyFavoriteQuestionDataBean1.question_id,answerInfo);
                                } else {
                                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                                    if (answerInfo != null) {
                                        if (answerInfo.answer != null) {
                                            if (answerInfo.answer.contains(optionanswerS1[0])) {
                                                questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_gray8099));
                                                questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.black80999999));
                                                answerInfo.answer = answerInfo.answer.replace(optionanswerS1[0], "");
                                            } else {
                                                answerInfo.answer = answerInfo.answer + optionanswerS1[0];
                                            }
                                            //将选项排序
                                            String[] strArr = new String[answerInfo.answer.length()];
                                            for (int num = 0; num < answerInfo.answer.length(); num ++){
                                                strArr[num] = answerInfo.answer.substring(num,num + 1);
                                            }
                                            Arrays.sort(strArr,String.CASE_INSENSITIVE_ORDER);
                                            answerInfo.answer = Arrays.toString(strArr).replace("[","");
                                            answerInfo.answer = answerInfo.answer.replace("]","");
                                            answerInfo.answer = answerInfo.answer.replaceAll(",","");
                                            answerInfo.answer = answerInfo.answer.replaceAll(" ","");
                                            if (answerInfo.answer.equals(QuestionBankMyFavoriteQuestionDataBean1.answer)){
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        } else {
                                            answerInfo.answer = optionanswerS1[0];
                                            if (answerInfo.answer.equals(QuestionBankMyFavoriteQuestionDataBean1.answer)){
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                    } else {
                                        answerInfo = new AnswerInfo();
                                        answerInfo.answer = optionanswerS1[0];
                                        if (answerInfo.answer.equals(QuestionBankMyFavoriteQuestionDataBean1.answer)){
                                            answerInfo.result = "对";
                                        } else {
                                            answerInfo.result = "错";
                                        }
                                    }
                                    mMyQuestionBankExercisesAnswerMap.put(QuestionBankMyFavoriteQuestionDataBean1.question_id,answerInfo);
                                }
                            });
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    if (answerInfo.answer.contains(optionanswerS1[0])) {
                                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                    }
                                }
                            }
                            TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                            questionbank_answerpaper_content1.addView(view3);
                        }
                        //材料题的每个子题都有自己的查看解析按钮
                        TextView questionbank_answerpaper_single_analysisbutton = view5.findViewById(R.id.questionbank_answerpaper_single_analysisbutton);
                        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_single_analysisbutton.getLayoutParams();
                        rl.height = view5.getResources().getDimensionPixelSize(R.dimen.dp_37);
                        rl.topMargin = view5.getResources().getDimensionPixelSize(R.dimen.dp_70);
                        questionbank_answerpaper_single_analysisbutton.setLayoutParams(rl);
                        String finalCurrentAnswer = currentAnswer;
                        questionbank_answerpaper_single_analysisbutton.setOnClickListener(v->{
                            //隐藏查看解析的按钮，防止多次点击
                            RelativeLayout.LayoutParams  questionbank_answerpaper_single_analysisbuttonrl = (RelativeLayout.LayoutParams) questionbank_answerpaper_single_analysisbutton.getLayoutParams();
                            questionbank_answerpaper_single_analysisbuttonrl.height = 0;
                            questionbank_answerpaper_single_analysisbuttonrl.topMargin = 0;
                            questionbank_answerpaper_single_analysisbutton.setLayoutParams(questionbank_answerpaper_single_analysisbuttonrl);
                            LinearLayout questionbank_answerpaper_single_analysis = view5.findViewById(R.id.questionbank_answerpaper_single_analysis);
                            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
                            questionbank_answerpaper_single_analysis.addView(view);
                            //修改内容为正确答案
                            TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
                            questionbank_analysis1_rightAnswer.setText(finalCurrentAnswer);
                            //                    /修改内容为此题的解析
                            TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
                            if (QuestionBankMyFavoriteQuestionDataBean1.question_analysis == null) {
                                QuestionBankMyFavoriteQuestionDataBean1.question_analysis = "";
                            }
                            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_analysis);
                            //修改内容为您的答案
                            TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                                } else {
                                    questionbank_analysis1_yourAnswer.setText("");
                                }
                            }
                            //字体大小的设置
                            if (mFontSize.equals("nomal")) {
                                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                        });
                        //字体大小的设置
                        if (mFontSize.equals("nomal")) {
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        } else if (mFontSize.equals("small")) {
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        } else if (mFontSize.equals("big")) {
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        }
                    } else if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 14) {//如果是简答题
                        View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                        questionbank_answerpaper_content.addView(view3);
                        TextView questionbank_answerpaper_shortanswersign = view3.findViewById(R.id.questionbank_answerpaper_shortanswersign);
                        questionbank_answerpaper_shortanswersign.setText("第" + (i + 1) + "题： " + type);
                        TextView questionbank_answerpaper_shortanswer = view3.findViewById(R.id.questionbank_answerpaper_shortanswer);
                        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                        rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_name);
                        EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                        questionbank_answerpaper_shortansweredittext.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (mMyQuestionBankExercisesAnswerMap != null) {
                                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                                    if (answerInfo == null) {
                                        answerInfo = new AnswerInfo();
                                    }
                                    answerInfo.answer = s.toString();
                                    answerInfo.result = "对";
                                    mMyQuestionBankExercisesAnswerMap.put(QuestionBankMyFavoriteQuestionDataBean1.question_id, answerInfo);
                                }
                            }
                        });
                        //材料题的每个子题都有自己的查看解析按钮
                        TextView questionbank_answerpaper_shortanswer_analysisbutton = view3.findViewById(R.id.questionbank_answerpaper_shortanswer_analysisbutton);
                        rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer_analysisbutton.getLayoutParams();
                        rl.height = view3.getResources().getDimensionPixelSize(R.dimen.dp_37);
                        rl.topMargin = view3.getResources().getDimensionPixelSize(R.dimen.dp_70);
                        questionbank_answerpaper_shortanswer_analysisbutton.setLayoutParams(rl);
                        questionbank_answerpaper_shortanswer_analysisbutton.setOnClickListener(v->{
                            //隐藏查看解析的按钮，防止多次点击
                            RelativeLayout.LayoutParams  questionbank_answerpaper_shortanswer_analysisbuttonrl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer_analysisbutton.getLayoutParams();
                            questionbank_answerpaper_shortanswer_analysisbuttonrl.height = 0;
                            questionbank_answerpaper_shortanswer_analysisbuttonrl.topMargin = 0;
                            questionbank_answerpaper_shortanswer_analysisbutton.setLayoutParams(questionbank_answerpaper_shortanswer_analysisbuttonrl);
                            LinearLayout questionbank_answerpaper_shortanswer_analysis = view3.findViewById(R.id.questionbank_answerpaper_shortanswer_analysis);
                            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
                            questionbank_answerpaper_shortanswer_analysis.addView(view);
                            //修改内容为正确答案
                            TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.optionanswer);
                            //修改内容为此题的解析
                            TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
                            if (QuestionBankMyFavoriteQuestionDataBean1.question_analysis == null) {
                                QuestionBankMyFavoriteQuestionDataBean1.question_analysis = "";
                            }
                            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_analysis);
                            //修改内容为您的答案
                            TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo.answer);
                                } else {
                                    questionbank_analysis2_yourAnswer.setText("");
                                }
                            }
                            if (mFontSize.equals("nomal")) {
                                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                        });
                        if (mFontSize.equals("nomal")) {
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        } else if (mFontSize.equals("small")) {
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        } else if (mFontSize.equals("big")) {
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        }
                    }
                }
            }
        }
        if (questionBankMyFavoriteQuestionDataBean.question_type == 7){ // 材料题暂不需要解析按钮
            TextView coursedetails_wrongquestion_analysisbutton = mModelQuestionBankWrongQuestionView.findViewById(R.id.coursedetails_wrongquestion_analysisbutton);
            coursedetails_wrongquestion_analysisbutton.setVisibility(View.INVISIBLE);
        } else {
            //点击按钮，显示解析
            TextView coursedetails_wrongquestion_analysisbutton = mModelQuestionBankWrongQuestionView.findViewById(R.id.coursedetails_wrongquestion_analysisbutton);
            coursedetails_wrongquestion_analysisbutton.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams) coursedetails_wrongquestion_analysisbutton.getLayoutParams();
            lLayoutParams.height = mModelQuestionBankWrongQuestionView.getResources().getDimensionPixelSize(R.dimen.dp_37);
            lLayoutParams.topMargin = mModelQuestionBankWrongQuestionView.getResources().getDimensionPixelSize(R.dimen.dp_70);
            coursedetails_wrongquestion_analysisbutton.setLayoutParams(lLayoutParams);
            LinearLayout coursedetails_wrongquestion_analysis = mModelQuestionBankWrongQuestionView.findViewById(R.id.coursedetails_wrongquestion_analysis);
            coursedetails_wrongquestion_analysis.removeAllViews();
            coursedetails_wrongquestion_analysisbutton.setOnClickListener(v -> {
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_wrongquestion_analysisbutton.getLayoutParams();
                ll.height = 0;
                ll.topMargin = 0;
                coursedetails_wrongquestion_analysisbutton.setLayoutParams(ll);
                if (questionBankMyFavoriteQuestionDataBean.question_type == 1 || questionBankMyFavoriteQuestionDataBean.question_type == 2) {//单选题或多选题
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
                    coursedetails_wrongquestion_analysis.addView(view);
                    //修改内容为正确答案
                    String[] optionanswerS = questionBankMyFavoriteQuestionDataBean.optionanswer.split("#EDU;");
                    if (optionanswerS == null) {

                    }
                    String currentAnswer = "";
                    for (int i = 0; i < optionanswerS.length; i++) {
                        String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                        if (optionanswerS1.length != 3) {
                            break;
                        }
                        if (optionanswerS1[1].equals("是")) {
                            currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                        }
                    }
                    TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
                    questionbank_analysis1_rightAnswer.setText(currentAnswer);
//                    /修改内容为此题的解析
                    TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
                    if (questionBankMyFavoriteQuestionDataBean.question_analysis == null) {
                        questionBankMyFavoriteQuestionDataBean.question_analysis = "";
                    }
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_analysis);
                    //修改内容为您的答案
                    TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
                    if (mMyQuestionBankExercisesAnswerMap != null) {
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                        if (answerInfo != null) {
                            if (answerInfo.answer != null) {
                                new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_yourAnswer).setHtmlWithPic(answerInfo.answer);
                            }
                        }
                    }
                    if (mFontSize.equals("nomal")) {
                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {//简答题
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
                    coursedetails_wrongquestion_analysis.addView(view);
                    //修改内容为正确答案
                    TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
                    if (questionBankMyFavoriteQuestionDataBean.optionanswer == null) {
                        questionBankMyFavoriteQuestionDataBean.optionanswer = "";
                    }
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.optionanswer);
                    //修改内容为此题的解析
                    TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
                    if (questionBankMyFavoriteQuestionDataBean.question_analysis == null) {
                        questionBankMyFavoriteQuestionDataBean.question_analysis = "";
                    }
                    new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_analysis);
                    //修改内容为您的答案
                    TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
                    if (mMyQuestionBankExercisesAnswerMap != null) {
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(questionBankMyFavoriteQuestionDataBean.question_id);
                        if (answerInfo != null) {
                            if (answerInfo.answer != null) {
                                new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo.answer);
                            }
                        }
                    }
                    if (mFontSize.equals("nomal")) {
                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                }
            });
        }
    }

    //题型展示
    private void QuestionBankDetailsQuestionTypeShow(int type) {
        if (mview == null || mMyFavoriteQuestionDataBeans == null) {
            return;
        }
        mControlMainActivity.onClickQuestionBankDetails();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        if (mModelQuestionBankQuestionTypeView == null) {
            mModelQuestionBankQuestionTypeView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_questiontype, null);
        }
        fragmentquestionbank_main.addView(mModelQuestionBankQuestionTypeView);
        int singleNum = 0;
        int muliNum = 0;
        int shortNum = 0;
        int materNum = 0;
        for (QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean: mMyFavoriteQuestionDataBeans) {
            if (questionBankMyFavoriteQuestionDataBean == null){
                continue;
            }
            if (questionBankMyFavoriteQuestionDataBean.question_type == null){
                continue;
            }
            if (questionBankMyFavoriteQuestionDataBean.question_type == 1){
                singleNum ++;
            } else if (questionBankMyFavoriteQuestionDataBean.question_type == 2){
                muliNum ++;
            } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4){
                shortNum ++;
            } else if (questionBankMyFavoriteQuestionDataBean.question_type == 7){
                materNum ++;
            }
        }
        LinearLayout questionbank_questiontype_singlebutton = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_singlebutton);
        TextView questionbank_questiontype_singlecount = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_singlecount);
        questionbank_questiontype_singlecount.setText("共" + singleNum + "题");
        int finalSingleNum = singleNum;
        questionbank_questiontype_singlebutton.setOnClickListener(v -> { //直接跳到单选题
            if (finalSingleNum == 0){
                Toast.makeText(mControlMainActivity,"您没有单选题",Toast.LENGTH_SHORT).show();
                return;
            }
            int count = 0;
            for (QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean: mMyFavoriteQuestionDataBeans) {
                if (questionBankMyFavoriteQuestionDataBean == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == 1){
                    mCurrentIndex = count;
                    if (type == 1) {
                        QuestionBankDetailsQuestionModeMyCollectionQuestionShow();
                    } else if (type == 2){
                        QuestionBankDetailsQuestionModeWrongQuestionShow();
                    }
                    break;
                }
                count ++;
            }
        });
        LinearLayout questionbank_questiontype_mutilbutton = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_mutilbutton);
        TextView questionbank_questiontype_mutilcount = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_mutilcount);
        questionbank_questiontype_mutilcount.setText("共" + muliNum + "题");
        int finalMuliNum = muliNum;
        questionbank_questiontype_mutilbutton.setOnClickListener(v -> { //直接跳到多选题
            if (finalMuliNum == 0){
                Toast.makeText(mControlMainActivity,"您没有多选题",Toast.LENGTH_SHORT).show();
                return;
            }
            int count = 0;
            for (QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean: mMyFavoriteQuestionDataBeans) {
                if (questionBankMyFavoriteQuestionDataBean == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == 2){
                    mCurrentIndex = count;
                    if (type == 1) {
                        QuestionBankDetailsQuestionModeMyCollectionQuestionShow();
                    } else if (type == 2){
                        QuestionBankDetailsQuestionModeWrongQuestionShow();
                    }
                    break;
                }
                count ++;
            }
        });
        LinearLayout questionbank_questiontype_shortanswerbutton = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_shortanswerbutton);
        TextView questionbank_questiontype_shortanswercount = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_shortanswercount);
        questionbank_questiontype_shortanswercount.setText("共" + shortNum + "题");
        int finalShortNum = shortNum;
        questionbank_questiontype_shortanswerbutton.setOnClickListener(v -> { //直接跳到简答题
            if (finalShortNum == 0){
                Toast.makeText(mControlMainActivity,"您没有简答题",Toast.LENGTH_SHORT).show();
                return;
            }
            int count = 0;
            for (QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean: mMyFavoriteQuestionDataBeans) {
                if (questionBankMyFavoriteQuestionDataBean == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == 4){
                    mCurrentIndex = count;
                    if (type == 1) {
                        QuestionBankDetailsQuestionModeMyCollectionQuestionShow();
                    } else if (type == 2){
                        QuestionBankDetailsQuestionModeWrongQuestionShow();
                    }
                    break;
                }
                count ++;
            }
        });
        LinearLayout questionbank_questiontype_materialbutton = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_materialbutton);
        TextView questionbank_questiontype_materialcount = mModelQuestionBankQuestionTypeView.findViewById(R.id.questionbank_questiontype_materialcount);
        questionbank_questiontype_materialcount.setText("共" + materNum + "题");
        int finalMaterNum = materNum;
        questionbank_questiontype_materialbutton.setOnClickListener(v -> { //直接跳到材料题
            if (finalMaterNum == 0){
                Toast.makeText(mControlMainActivity,"您没有材料题",Toast.LENGTH_SHORT).show();
                return;
            }
            int count = 0;
            for (QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean: mMyFavoriteQuestionDataBeans) {
                if (questionBankMyFavoriteQuestionDataBean == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == null){
                    continue;
                }
                if (questionBankMyFavoriteQuestionDataBean.question_type == 7){
                    mCurrentIndex = count;
                    if (type == 1) {
                        QuestionBankDetailsQuestionModeMyCollectionQuestionShow();
                    } else if (type == 2){
                        QuestionBankDetailsQuestionModeWrongQuestionShow();
                    }
                    break;
                }
                count ++;
            }
        });
    }

    //添加收藏题本-问题界面
    private void CollectionQuestionViewAdd(ImageView questionbank_mycollextionquestion_collection) {
        if (mMyFavoriteQuestionDataBeans == null){
            return;
        }
        View view2 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
        LinearLayout coursedetails_mycollextionquestion_details = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.coursedetails_mycollextionquestion_details);
        coursedetails_mycollextionquestion_details.removeAllViews();
        coursedetails_mycollextionquestion_details.addView(view2);
        if (mCurrentIndex < 0 || mCurrentIndex >= mMyFavoriteQuestionDataBeans.size() || mMyFavoriteQuestionDataBeans.size() <= 0) { //不在数组范围直接返回
            return;
        }
        QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean = mMyFavoriteQuestionDataBeans.get(mCurrentIndex);
        if (questionBankMyFavoriteQuestionDataBean == null){
            return;
        }
        TextView questionbank_mycollextionquestion_questioncountsum = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_questioncountsum);
        questionbank_mycollextionquestion_questioncountsum.setText("/" + mMyFavoriteQuestionDataBeans.size());
        TextView questionbank_mycollextionquestion_questioncount = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_questioncount);
        questionbank_mycollextionquestion_questioncount.setText(String.valueOf(mCurrentIndex + 1));
        TextView questionbank_answerpaper_single_title = view2.findViewById(R.id.questionbank_answerpaper_single_title);
        if (mFontSize.equals("nomal")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
        } else if (mFontSize.equals("small")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
        } else if (mFontSize.equals("big")) {
            questionbank_answerpaper_single_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
        }
        if (questionBankMyFavoriteQuestionDataBean.tf_collection != null && questionbank_mycollextionquestion_collection != null){
            if (questionBankMyFavoriteQuestionDataBean.tf_collection == 1){
                questionbank_mycollextionquestion_collection.setBackground(mModelQuestionBankMyCollectionQuestionView.getResources().getDrawable(R.drawable.button_collect_enable));
                mIsCollect = true;
            } else if (questionBankMyFavoriteQuestionDataBean.tf_collection == 2){
                questionbank_mycollextionquestion_collection.setBackground(mModelQuestionBankMyCollectionQuestionView.getResources().getDrawable(R.drawable.button_collect_disable_black));
                mIsCollect = false;
            }
        }
        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_name);
        questionbank_answerpaper_single_title.setHint(questionBankMyFavoriteQuestionDataBean.question_id + "");
        TextView questionbank_mycollextionquestion_questiontype = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.questionbank_mycollextionquestion_questiontype);
        if (questionBankMyFavoriteQuestionDataBean.question_type == 1) {
            questionbank_mycollextionquestion_questiontype.setText("[单选题]");
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 2) {
            questionbank_mycollextionquestion_questiontype.setText("[多选题]");
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {
            questionbank_mycollextionquestion_questiontype.setText("[简答题]");
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 7) {
            questionbank_mycollextionquestion_questiontype.setText("[材料题]");
        }
        LinearLayout questionbank_answerpaper_content = view2.findViewById(R.id.questionbank_answerpaper_content);
        questionbank_answerpaper_content.removeAllViews();
        if (questionBankMyFavoriteQuestionDataBean.question_type == 1 || questionBankMyFavoriteQuestionDataBean.question_type == 2) { //如果是单选题或多选题添加选项布局
            String[] optionanswerS = questionBankMyFavoriteQuestionDataBean.optionanswer.split("#EDU;");
            if (optionanswerS != null) {
                for (int i = 0; i < optionanswerS.length; i++) {
                    View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                    String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                    if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                        continue;
                    }
                    TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                    questionbank_answerpaper_option_name.setText(optionanswerS1[0]);
                    questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                    TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                    new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                    if (mFontSize.equals("nomal")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                    } else if (mFontSize.equals("small")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                    } else if (mFontSize.equals("big")) {
                        questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                    }
                    if (questionBankMyFavoriteQuestionDataBean.wrong_answer != null){
                        if (questionBankMyFavoriteQuestionDataBean.wrong_answer.contains(optionanswerS1[0])){
                            questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                            questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                        }
                    }
                    questionbank_answerpaper_content.addView(view3);
                }
            }
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {//如果是简答题
            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
            questionbank_answerpaper_content.addView(view3);
            EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            if (mFontSize.equals("nomal")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
            //设置不可编辑，交卷以后不能输入
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortansweredittext.getLayoutParams();
            rl.height = 0;
            rl.topMargin = 0;
            questionbank_answerpaper_shortansweredittext.setLayoutParams(rl);
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 7) {//如果是材料题
            View contentView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
            questionbank_answerpaper_content.addView(contentView);
            EditText contentViewedittext = contentView.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
            if (questionBankMyFavoriteQuestionDataBean.optionanswer != null) {
                TextView questionbank_answerpaper_shortanswer = contentView.findViewById(R.id.questionbank_answerpaper_shortanswer);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                rl = (RelativeLayout.LayoutParams) contentViewedittext.getLayoutParams();
                rl.height = 0;
                rl.topMargin = 0;
                contentViewedittext.setLayoutParams(rl);
                new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.optionanswer);
                if (mFontSize.equals("nomal")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize17));
                } else if (mFontSize.equals("small")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize14));
                } else if (mFontSize.equals("big")) {
                    questionbank_answerpaper_shortanswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view2.getResources().getDimensionPixelSize(R.dimen.textsize20));
                }
            }
            if (questionBankMyFavoriteQuestionDataBean.question_sub_id != null ) { // 说明有子题
                for (int i = 0; i < questionBankMyFavoriteQuestionDataBean.question_sub_id.size(); i++) {
                    QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean QuestionBankMyFavoriteQuestionDataBean1 = questionBankMyFavoriteQuestionDataBean.question_sub_id.get(i);
                    if (QuestionBankMyFavoriteQuestionDataBean1 == null) {
                        continue;
                    }
                    String type = "";
                    if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 11) {
                        type = "[单选题] ";
                    } else if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 12) {
                        type = "[多选题] ";
                    } else if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 14) {
                        type = "[简答题] ";
                    }
                    if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 11 || QuestionBankMyFavoriteQuestionDataBean1.question_type == 12) { //如果是单选题或多选题添加选项布局
                        if (QuestionBankMyFavoriteQuestionDataBean1.optionanswer == null) {
                            continue;
                        }
                        String[] optionanswerS = QuestionBankMyFavoriteQuestionDataBean1.optionanswer.split("#EDU;");
                        if (optionanswerS == null) {
                            continue;
                        }
                        View view5 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_single, null);
                        questionbank_answerpaper_content.addView(view5);
                        TextView questionbank_answerpaper_single_titlesign = view5.findViewById(R.id.questionbank_answerpaper_single_titlesign);
                        questionbank_answerpaper_single_titlesign.setText("第" + (i + 1) + "题： " + type);
                        LinearLayout questionbank_answerpaper_content1 = view5.findViewById(R.id.questionbank_answerpaper_content);
                        TextView questionbank_answerpaper_single_title1 = view5.findViewById(R.id.questionbank_answerpaper_single_title);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_single_title1).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_name);
                        String currentAnswer = "";
                        for (String string : optionanswerS) {
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_option, null);
                            String[] optionanswerS1 = string.substring(1).split("#");
                            if (optionanswerS1.length != 3) {//question_analysisS1的结构应为#A#是#选择A
                                continue;
                            }
                            if (optionanswerS1[1].equals("是")) {
                                currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                                if (QuestionBankMyFavoriteQuestionDataBean1.answer == null){
                                    QuestionBankMyFavoriteQuestionDataBean1.answer = currentAnswer;
                                }
                            }
                            TextView questionbank_answerpaper_option_name = view3.findViewById(R.id.questionbank_answerpaper_option_name);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_name).setHtmlWithPic(optionanswerS1[0]);
                            questionbank_answerpaper_option_name.setHint(optionanswerS1[1]);
                            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                            if (answerInfo != null) {
                                if (answerInfo.answer != null) {
                                    if (answerInfo.answer.contains(optionanswerS1[0])) {
                                        questionbank_answerpaper_option_name.setBackground(view3.getResources().getDrawable(R.drawable.textview_style_circle_blue649cf0));
                                        questionbank_answerpaper_option_name.setTextColor(view3.getResources().getColor(R.color.blue649cf0));
                                    }
                                }
                            }
                            TextView questionbank_answerpaper_option_title = view3.findViewById(R.id.questionbank_answerpaper_option_title);
                            new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_option_title).setHtmlWithPic(optionanswerS1[2]);
                            if (mFontSize.equals("nomal")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            } else if (mFontSize.equals("small")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            } else if (mFontSize.equals("big")) {
                                questionbank_answerpaper_option_title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            }
                            questionbank_answerpaper_content1.addView(view3);
                        }
                        LinearLayout questionbank_answerpaper_single_analysis = view5.findViewById(R.id.questionbank_answerpaper_single_analysis);
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
                        questionbank_answerpaper_single_analysis.addView(view);
                        //修改内容为正确答案
                        TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
                        questionbank_analysis1_rightAnswer.setText(currentAnswer);
//                    /修改内容为此题的解析
                        TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
                        if (QuestionBankMyFavoriteQuestionDataBean1.question_analysis == null) {
                            QuestionBankMyFavoriteQuestionDataBean1.question_analysis = "";
                        }
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_analysis);
                        //修改内容为您的答案
                        TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                        if (answerInfo != null) {
                            if (answerInfo.answer != null) {
                                questionbank_analysis1_yourAnswer.setText(answerInfo.answer);
                            } else {
                                questionbank_analysis1_yourAnswer.setText("");
                            }
                        }
                        //字体大小的设置
                        if (mFontSize.equals("nomal")) {
                            questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        } else if (mFontSize.equals("small")) {
                            questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        } else if (mFontSize.equals("big")) {
                            questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_answerpaper_single_title1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view5.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        }
                    } else if (QuestionBankMyFavoriteQuestionDataBean1.question_type == 14) {//如果是简答题
                        View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_shortanswer, null);
                        questionbank_answerpaper_content.addView(view3);
                        TextView questionbank_answerpaper_shortanswersign = view3.findViewById(R.id.questionbank_answerpaper_shortanswersign);
                        questionbank_answerpaper_shortanswersign.setText("第" + (i + 1) + "题： " + type);
                        TextView questionbank_answerpaper_shortanswer = view3.findViewById(R.id.questionbank_answerpaper_shortanswer);
                        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortanswer.getLayoutParams();
                        rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        questionbank_answerpaper_shortanswer.setLayoutParams(rl);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_answerpaper_shortanswer).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_name);
                        EditText questionbank_answerpaper_shortansweredittext = view3.findViewById(R.id.questionbank_answerpaper_shortansweredittext);
                        rl = (RelativeLayout.LayoutParams) questionbank_answerpaper_shortansweredittext.getLayoutParams();
                        rl.height = 0;
                        rl.topMargin = 0;
                        questionbank_answerpaper_shortansweredittext.setLayoutParams(rl);
                        LinearLayout questionbank_answerpaper_shortanswer_analysis = view3.findViewById(R.id.questionbank_answerpaper_shortanswer_analysis);
                        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
                        questionbank_answerpaper_shortanswer_analysis.addView(view);
                        //修改内容为正确答案
                        TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.optionanswer);
                        //修改内容为此题的解析
                        TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
                        if (QuestionBankMyFavoriteQuestionDataBean1.question_analysis == null) {
                            QuestionBankMyFavoriteQuestionDataBean1.question_analysis = "";
                        }
                        new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(QuestionBankMyFavoriteQuestionDataBean1.question_analysis);
                        //修改内容为您的答案
                        TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(QuestionBankMyFavoriteQuestionDataBean1.question_id);
                        if (answerInfo != null) {
                            if (answerInfo.answer != null) {
                                new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(answerInfo.answer);
                            } else {
                                questionbank_analysis2_yourAnswer.setText("");
                            }
                        }
                        if (mFontSize.equals("nomal")) {
                            questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize17));
                        } else if (mFontSize.equals("small")) {
                            questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize14));
                        } else if (mFontSize.equals("big")) {
                            questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                            questionbank_answerpaper_shortansweredittext.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view3.getResources().getDimensionPixelSize(R.dimen.textsize20));
                        }
                    }
                }
            }
        }
        //添加题的解析   我的收藏题
        LinearLayout coursedetails_mycollextionquestion_analysis = mModelQuestionBankMyCollectionQuestionView.findViewById(R.id.coursedetails_mycollextionquestion_analysis);
        coursedetails_mycollextionquestion_analysis.removeAllViews();

        //我的收藏题网络请求获取的三个参数  optionanswer  question_type question_type
        if (questionBankMyFavoriteQuestionDataBean.question_type == 1 || questionBankMyFavoriteQuestionDataBean.question_type == 2) {//单选题或多选题
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis1, null);
            coursedetails_mycollextionquestion_analysis.addView(view);
            //修改内容为正确答案  分割字符串数组
            String[] optionanswerS = questionBankMyFavoriteQuestionDataBean.optionanswer.split("#EDU;");
            if (optionanswerS == null) {
            }
            //字符串截取正确的答案
            String currentAnswer = "";
            for (int i = 0; i < optionanswerS.length; i++) {
                String[] optionanswerS1 = optionanswerS[i].substring(1).split("#");
                if (optionanswerS1.length != 3) {
                    break;
                }
                if (optionanswerS1[1].equals("是")) {
                    currentAnswer = currentAnswer + optionanswerS1[0] + " ";
                }
            }
            TextView questionbank_analysis1_rightAnswer = view.findViewById(R.id.questionbank_analysis1_rightAnswer);
            questionbank_analysis1_rightAnswer.setText(currentAnswer);
//                    /修改内容为此题的解析
            TextView questionbank_analysis1_content = view.findViewById(R.id.questionbank_analysis1_content);
            if (questionBankMyFavoriteQuestionDataBean.question_analysis == null){
                questionBankMyFavoriteQuestionDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_content).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis1_yourAnswer = view.findViewById(R.id.questionbank_analysis1_yourAnswer);
            if (questionBankMyFavoriteQuestionDataBean.wrong_answer == null){
                questionBankMyFavoriteQuestionDataBean.wrong_answer = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis1_yourAnswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.wrong_answer);
            //修改内容的字体大小
            if (mFontSize.equals("nomal")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis1_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis1_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4) {//简答题
            //简答题的解析
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerpaper_analysis2, null);
            coursedetails_mycollextionquestion_analysis.addView(view);
            //修改内容为正确答案
            TextView questionbank_analysis2_rightAnswer = view.findViewById(R.id.questionbank_analysis2_rightAnswer);
            if (questionBankMyFavoriteQuestionDataBean.optionanswer == null){
                questionBankMyFavoriteQuestionDataBean.optionanswer = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_rightAnswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.optionanswer);
            //修改内容为此题的解析
            TextView questionbank_analysis2_content = view.findViewById(R.id.questionbank_analysis2_content);
            if (questionBankMyFavoriteQuestionDataBean.question_analysis == null){
                questionBankMyFavoriteQuestionDataBean.question_analysis = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_content).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.question_analysis);
            //修改内容为您的答案
            TextView questionbank_analysis2_yourAnswer = view.findViewById(R.id.questionbank_analysis2_yourAnswer);
            if (questionBankMyFavoriteQuestionDataBean.wrong_answer == null){
                questionBankMyFavoriteQuestionDataBean.wrong_answer = "";
            }
            new ModelHtmlUtils(mControlMainActivity, questionbank_analysis2_yourAnswer).setHtmlWithPic(questionBankMyFavoriteQuestionDataBean.wrong_answer);
            if (mFontSize.equals("nomal")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize17));
            } else if (mFontSize.equals("small")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize14));
            } else if (mFontSize.equals("big")) {
                questionbank_analysis2_rightAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_content.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
                questionbank_analysis2_yourAnswer.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, view.getResources().getDimensionPixelSize(R.dimen.textsize20));
            }
        }
    }

    //添加答题卡界面
    private void AnswerQuestionCardViewAdd(List<QuestionBankAnswerSheetBean.QuestionBankAnswerSheetDataBean> questionBankAnswerSheetDataBeans) {
        if (mview == null) {
            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCurrentIndex < 0 || mCurrentIndex >= questionBankAnswerSheetDataBeans.size()) { //不在数组范围直接返回
            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        mControlMainActivity.onClickQuestionBankAnswerQuestionCard();
        HideAllLayout();
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        mModelQuestionBankAnswerQuestionCardView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard, null);
        fragmentquestionbank_main.addView(mModelQuestionBankAnswerQuestionCardView);
        LinearLayout coursedetails_answerquestioncard_details = mModelQuestionBankAnswerQuestionCardView.findViewById(R.id.coursedetails_answerquestioncard_details);
        coursedetails_answerquestioncard_details.removeAllViews();
        //字符串分割
        View singleView = null;
        View mutilView = null;
        View shortAnswerView = null;
        View materialView = null;
        for (int count = 0; count < questionBankAnswerSheetDataBeans.size(); count ++){
            QuestionBankAnswerSheetBean.QuestionBankAnswerSheetDataBean questionBankAnswerSheetDataBean = questionBankAnswerSheetDataBeans.get(count);
            if (questionBankAnswerSheetDataBean == null){
                continue;
            }
            if (questionBankAnswerSheetDataBean.type == null){
                continue;
            }
            if (questionBankAnswerSheetDataBean.type == 1) {
                if (singleView == null) {
                    singleView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard1, null);
                    coursedetails_answerquestioncard_details.addView(singleView);
                }
                GridLayout coursedetails_answerquestioncard_questionnumber = singleView.findViewById(R.id.coursedetails_answerquestioncard_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard2, null);
                coursedetails_answerquestioncard_questionnumber.addView(view);
                //题标序号
                TextView questionbank_answerquestioncard2_select = view.findViewById(R.id.questionbank_answerquestioncard2_select);
                questionbank_answerquestioncard2_select.setText("" + (count + 1));
                if (questionBankAnswerSheetDataBean.tf_marked != null) {
                    if (questionBankAnswerSheetDataBean.tf_marked == 1) {//标记此题
                        ImageView questionbank_answerquestioncard2_sign = view.findViewById(R.id.questionbank_answerquestioncard2_sign);
                        questionbank_answerquestioncard2_sign.setVisibility(View.VISIBLE);
                    }
                }
                if (mCurrentIndex == count) { //此题为当前正在答的题,改变题的颜色
                    questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                    questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_green));
                } else if (mMyQuestionBankExercisesAnswerMap != null) { //判断是已做过还是未做过的题（如果没有答案，说明没做过）
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(Integer.valueOf(questionBankAnswerSheetDataBean.question_id));
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (!answerInfo.answer.equals("")) {
                                questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                                questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                            }
                        }
                    }
                }
                int finalCount = count;
                questionbank_answerquestioncard2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeShow();
                });
            } else if (questionBankAnswerSheetDataBean.type == 2) {
                if (mutilView == null) {
                    mutilView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard1, null);
                    coursedetails_answerquestioncard_details.addView(mutilView);
                    TextView coursedetails_handinpaper1_questiontype = mutilView.findViewById(R.id.coursedetails_answerquestioncard_questiontype);
                    coursedetails_handinpaper1_questiontype.setText("多选题");
                }
                GridLayout coursedetails_answerquestioncard_questionnumber = mutilView.findViewById(R.id.coursedetails_answerquestioncard_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard2, null);
                coursedetails_answerquestioncard_questionnumber.addView(view);
                TextView questionbank_answerquestioncard2_select = view.findViewById(R.id.questionbank_answerquestioncard2_select);
                questionbank_answerquestioncard2_select.setText("" + (count + 1));
                if (questionBankAnswerSheetDataBean.tf_marked != null) {
                    if (questionBankAnswerSheetDataBean.tf_marked == 1) {//标记此题
                        ImageView questionbank_answerquestioncard2_sign = view.findViewById(R.id.questionbank_answerquestioncard2_sign);
                        questionbank_answerquestioncard2_sign.setVisibility(View.VISIBLE);
                    }
                }
                if (mCurrentIndex == count) { //此题为当前正在答的题,改变题的颜色
                    questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                    questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_green));
                } else if (mMyQuestionBankExercisesAnswerMap != null) { //判断是已做过还是未做过的题（如果没有答案，说明没做过）
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(Integer.valueOf(questionBankAnswerSheetDataBean.question_id));
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (!answerInfo.answer.equals("")) {
                                questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                                questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                            }
                        }
                    }
                }
                int finalCount = count;
                questionbank_answerquestioncard2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeShow();
                });
            } else if (questionBankAnswerSheetDataBean.type == 4) {
                if (shortAnswerView == null) {
                    shortAnswerView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard1, null);
                    coursedetails_answerquestioncard_details.addView(shortAnswerView);
                    TextView coursedetails_handinpaper1_questiontypeshortAnswerView = shortAnswerView.findViewById(R.id.coursedetails_answerquestioncard_questiontype);
                    coursedetails_handinpaper1_questiontypeshortAnswerView.setText("简答题");
                }
                GridLayout coursedetails_answerquestioncard_questionnumber = shortAnswerView.findViewById(R.id.coursedetails_answerquestioncard_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard2, null);
                coursedetails_answerquestioncard_questionnumber.addView(view);
                TextView questionbank_answerquestioncard2_select = view.findViewById(R.id.questionbank_answerquestioncard2_select);
                questionbank_answerquestioncard2_select.setText("" + (count + 1));
                if (questionBankAnswerSheetDataBean.tf_marked != null) {
                    if (questionBankAnswerSheetDataBean.tf_marked == 1) {//标记此题
                        ImageView questionbank_answerquestioncard2_sign = view.findViewById(R.id.questionbank_answerquestioncard2_sign);
                        questionbank_answerquestioncard2_sign.setVisibility(View.VISIBLE);
                    }
                }
                if (mCurrentIndex == count) { //此题为当前正在答的题,改变题的颜色
                    questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                    questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_green));
                } else if (mMyQuestionBankExercisesAnswerMap != null) { //判断是已做过还是未做过的题（如果没有答案，说明没做过）
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(Integer.valueOf(questionBankAnswerSheetDataBean.question_id));
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (!answerInfo.answer.equals("")) {
                                questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                                questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                            }
                        }
                    }
                }
                int finalCount = count;
                questionbank_answerquestioncard2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeShow();
                });
            } else if (questionBankAnswerSheetDataBean.type == 7) {
                if (materialView == null) {
                    materialView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard1, null);
                    coursedetails_answerquestioncard_details.addView(materialView);
                    TextView coursedetails_handinpaper1_questiontypematerialView = materialView.findViewById(R.id.coursedetails_answerquestioncard_questiontype);
                    coursedetails_handinpaper1_questiontypematerialView.setText("材料题");
                }
                GridLayout coursedetails_answerquestioncard_questionnumber = materialView.findViewById(R.id.coursedetails_answerquestioncard_questionnumber);
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_answerquestioncard2, null);
                coursedetails_answerquestioncard_questionnumber.addView(view);
                TextView questionbank_answerquestioncard2_select = view.findViewById(R.id.questionbank_answerquestioncard2_select);
                questionbank_answerquestioncard2_select.setText("" + (count + 1));
                if (questionBankAnswerSheetDataBean.tf_marked != null) {
                    if (questionBankAnswerSheetDataBean.tf_marked == 1) {//标记此题
                        ImageView questionbank_answerquestioncard2_sign = view.findViewById(R.id.questionbank_answerquestioncard2_sign);
                        questionbank_answerquestioncard2_sign.setVisibility(View.VISIBLE);
                    }
                }
                if (mCurrentIndex == count) { //此题为当前正在答的题,改变题的颜色
                    questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                    questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_green));
                } else if (mMyQuestionBankExercisesAnswerMap != null) { //判断是已做过还是未做过的题（如果没有答案，说明没做过）
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(Integer.valueOf(questionBankAnswerSheetDataBean.question_id));
                    if (answerInfo != null) {
                        if (answerInfo.answer != null) {
                            if (!answerInfo.answer.equals("")) {
                                questionbank_answerquestioncard2_select.setTextColor(view.getResources().getColor(R.color.white));
                                questionbank_answerquestioncard2_select.setBackground(view.getResources().getDrawable(R.drawable.textview_style_circle_blue));
                            }
                        }
                    }
                }
                int finalCount = count;
                questionbank_answerquestioncard2_select.setOnClickListener(v -> { //点击题号。跳转到指定题
                    mCurrentIndex = finalCount;
                    QuestionBankDetailsQuestionModeShow();
                });
            }
        }

        //点击交卷查看结果
        TextView coursedetails_answerquestioncard_commit = mModelQuestionBankAnswerQuestionCardView.findViewById(R.id.coursedetails_answerquestioncard_commit);
        coursedetails_answerquestioncard_commit.setOnClickListener(v -> {
            //显示交卷界面
            mTestPageIssueDataScore = "";
            getQuestionBankAnswerSheet(2);
        });
    }

    //隐藏所有图层
    private void HideAllLayout() {
        RelativeLayout fragmentquestionbank_main = mview.findViewById(R.id.fragmentquestionbank_main);
        fragmentquestionbank_main.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //章节练习
            case R.id.questionbank_sub_details_tab_chapterexercises: {
                //章节练习
                if (!mCurrentTab.equals("ChapterExercises")) {
                    ImageView questionbank_sub_details_cursor1 = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_cursor1);
                    Animation animation = new TranslateAnimation((mLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    questionbank_sub_details_cursor1.startAnimation(animation);
                    TextView questionbank_sub_details_tab_chapterexercises = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_chapterexercises);
                    TextView questionbank_sub_details_tab_quicktask = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_quicktask);
                    TextView questionbank_sub_details_tab_simulated = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_simulated);
                    questionbank_sub_details_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    questionbank_sub_details_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_sub_details_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }

                mLastTabIndex = 1;
                mCurrentTab = "ChapterExercises";    //章节练习
                QuestionBankDetailsChapterShow();
                break;
            }
            //快速做题
            case R.id.questionbank_sub_details_tab_quicktask: {
                if (!mCurrentTab.equals("QuickTask")) {
                    ImageView questionbank_sub_details_cursor1 = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_cursor1);
                    Animation animation = new TranslateAnimation((mLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    questionbank_sub_details_cursor1.startAnimation(animation);
                    TextView questionbank_sub_details_tab_chapterexercises = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_chapterexercises);
                    TextView questionbank_sub_details_tab_quicktask = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_quicktask);
                    TextView questionbank_sub_details_tab_simulated = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_simulated);
                    questionbank_sub_details_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_sub_details_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    questionbank_sub_details_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                QuestionBankDetailsQuickTaskShow();
                mLastTabIndex = 2;
                //快速做题
                mCurrentTab = "QuickTask";
                break;
            }
            //模拟真题
            case R.id.questionbank_sub_details_tab_simulated: {

                if (!mCurrentTab.equals("Simulated")) {
                    ImageView questionbank_sub_details_cursor1 = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_cursor1);
                    Animation animation = new TranslateAnimation((mLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    questionbank_sub_details_cursor1.startAnimation(animation);
                    TextView questionbank_sub_details_tab_chapterexercises = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_chapterexercises);
                    TextView questionbank_sub_details_tab_quicktask = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_quicktask);
                    TextView questionbank_sub_details_tab_simulated = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_tab_simulated);
                    questionbank_sub_details_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_sub_details_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_sub_details_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                QuestionBankDetailsSimulatedShow();
                mLastTabIndex = 3;
                mCurrentTab = "Simulated";
                break;
            }
            //章节练习
            case R.id.questionbank_questionrecords_tab_chapterexercises: {
                if (!mQuestionRecordCurrentTab.equals("ChapterExercises")) {
                    ImageView questionbank_questionrecords_cursor1 = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_cursor1);
                    Animation animation = new TranslateAnimation((mQuestionRecordLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    questionbank_questionrecords_cursor1.startAnimation(animation);
                    TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
                    TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
                    TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
                    questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mQuestionRecordLastTabIndex = 1;
                mQuestionRecordCurrentTab = "ChapterExercises";
                getQuestionBankAnswerRecord();
                break;
            }
            //快速做题
            case R.id.questionbank_questionrecords_tab_quicktask: {
                if (!mQuestionRecordCurrentTab.equals("QuickTask")) {
                    ImageView questionbank_questionrecords_cursor1 = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_cursor1);
                    Animation animation = new TranslateAnimation((mQuestionRecordLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    questionbank_questionrecords_cursor1.startAnimation(animation);
                    TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
                    TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
                    TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
                    questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mQuestionRecordLastTabIndex = 2;
                mQuestionRecordCurrentTab = "QuickTask";
                getQuestionBankAnswerRecord();
                break;
            }
            //模拟真题
            case R.id.questionbank_questionrecords_tab_simulated: {
                if (!mQuestionRecordCurrentTab.equals("Simulated")) {
                    ImageView questionbank_questionrecords_cursor1 = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_cursor1);
                    Animation animation = new TranslateAnimation((mQuestionRecordLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    questionbank_questionrecords_cursor1.startAnimation(animation);
                    TextView questionbank_questionrecords_tab_chapterexercises = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_chapterexercises);
                    TextView questionbank_questionrecords_tab_quicktask = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_quicktask);
                    TextView questionbank_questionrecords_tab_simulated = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_tab_simulated);
                    questionbank_questionrecords_tab_chapterexercises.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_questionrecords_tab_quicktask.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    questionbank_questionrecords_tab_simulated.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mModelQuestionBankQuestionRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mQuestionRecordLastTabIndex = 3;
                mQuestionRecordCurrentTab = "Simulated";
                getQuestionBankAnswerRecord();
                break;
            }
            //三道杠
            case R.id.questionbank_sub_details_buttonmore: {
                showPop();//popwindow设置
                toggleBright(); //设置动画的时间，长度和距离
                break;
            }
            case R.id.pop_add_mycollect: {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                getQuestionBankMyFavoriteQuestion(2);
                break;
            }
            case R.id.pop_add_mywrong: {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                getQuestionBankMyFavoriteQuestion(1);
                break;
            }
            case R.id.pop_add_myrecord: {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                getQuestionBankAnswerRecord();
                break;
            }
            default:
                break;
        }
    }

    //显示pop的文件
    private void showPop() {
        // 设置布局文件
        mPopupWindow.setContentView(LayoutInflater.from(mControlMainActivity).inflate(R.layout.pop_add, null));
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        mPopupWindow.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        ImageView questionbank_sub_details_buttonmore = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_buttonmore);
        mPopupWindow.showAsDropDown(questionbank_sub_details_buttonmore, -100, 0);
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener(() -> toggleBright());

        TextView pop_add_mycollect = mPopupWindow.getContentView().findViewById(R.id.pop_add_mycollect);
        TextView pop_add_mywrong = mPopupWindow.getContentView().findViewById(R.id.pop_add_mywrong);
        TextView pop_add_myrecord = mPopupWindow.getContentView().findViewById(R.id.pop_add_myrecord);

        pop_add_mycollect.setText("我的收藏(" + collection_num + ")");
        if (collection_num > 99){
            pop_add_myrecord.setText("我的收藏(99+)");
        }
        pop_add_mywrong.setText("错题本(" + error_num + ")");
        if (error_num > 99){
            pop_add_myrecord.setText("错题本(99+)");
        }
        pop_add_myrecord.setText("做题记录(" + DoRecord_num + ")");
        if (DoRecord_num > 99){
            pop_add_myrecord.setText("做题记录(99+)");
        }
        pop_add_mycollect.setOnClickListener(this);
        pop_add_mywrong.setOnClickListener(this);
        pop_add_myrecord.setOnClickListener(this);
    }

    private void ShowPopFontSize(ImageView imageView) {
        if (mPointoutPopupWindow == null) {
            mPointoutPopupWindow = new PopupWindow(mControlMainActivity);
            mPointoutAnimUtil = new ModelAnimUtil();
        }
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        mPointoutAnimUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        mPointoutAnimUtil.addUpdateListener(progress -> {
            // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
            bgPointoutAlpha = bPointoutRight ? progress : (START_ALPHA + END_ALPHA - progress);
            backgroundAlpha(bgPointoutAlpha);
        });
        mPointoutAnimUtil.addEndListner(animator -> {
            // 在一次动画结束的时候，翻转状态
            bPointoutRight = !bPointoutRight;
        });
        mPointoutAnimUtil.startAnimator();
        //字号大小的设置
        View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.pop_pointout, null);
        // 设置布局文件
        mPointoutPopupWindow.setContentView(view);
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPointoutPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPointoutPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPointoutPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        mPointoutPopupWindow.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPointoutPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPointoutPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPointoutPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        mPointoutPopupWindow.showAsDropDown(imageView, -100, 0);
        // 设置pop关闭监听，用于改变背景透明度
        mPointoutPopupWindow.setOnDismissListener(() -> {
            // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
            mPointoutAnimUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
            mPointoutAnimUtil.addUpdateListener(progress -> {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgPointoutAlpha = bPointoutRight ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgPointoutAlpha);
            });
            mPointoutAnimUtil.addEndListner(animator -> {
                // 在一次动画结束的时候，翻转状态
                bPointoutRight = !bPointoutRight;
            });
            mPointoutAnimUtil.startAnimator();
        });
        //A--字体大小的设置
        TextView fontsizesmall = view.findViewById(R.id.fontsizesmall);
        //A 字体大小的设置
        TextView fontsizenomal = view.findViewById(R.id.fontsizenomal);
        //A++ 变大字体大小的设置
        TextView fontsizebig = view.findViewById(R.id.fontsizebig);
        if (mFontSize.equals("small")) {
            fontsizesmall.setTextColor(view.getResources().getColor(R.color.blue649cf0));
            fontsizenomal.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
            fontsizebig.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
        } else if (mFontSize.equals("nomal")) {
            fontsizesmall.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
            fontsizenomal.setTextColor(view.getResources().getColor(R.color.blue649cf0));
            fontsizebig.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
        } else if (mFontSize.equals("big")) {
            fontsizesmall.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
            fontsizenomal.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
            fontsizebig.setTextColor(view.getResources().getColor(R.color.blue649cf0));
        }
        if (mCurrentAnswerMode.equals("handin")) {
            LinearLayout fontsize_main_layout = view.findViewById(R.id.fontsize_main_layout);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) fontsize_main_layout.getLayoutParams();
            ll.rightMargin = view.getResources().getDimensionPixelSize(R.dimen.dp13);
            fontsize_main_layout.setLayoutParams(ll);
            fontsizesmall.setOnClickListener(v -> {
                fontsizesmall.setTextColor(view.getResources().getColor(R.color.blue649cf0));
                fontsizenomal.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizebig.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                mFontSize = "small";
                QuestionBankDetailsQuestionModeShow();
            });
            fontsizenomal.setOnClickListener(v -> {
                fontsizesmall.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizenomal.setTextColor(view.getResources().getColor(R.color.blue649cf0));
                fontsizebig.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                mFontSize = "nomal";
                QuestionBankDetailsQuestionModeShow();
            });
            fontsizebig.setOnClickListener(v -> {
                fontsizesmall.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizenomal.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizebig.setTextColor(view.getResources().getColor(R.color.blue649cf0));
                mFontSize = "big";
                QuestionBankDetailsQuestionModeShow();
            });
        } else {
            fontsizesmall.setOnClickListener(v -> {
                fontsizesmall.setTextColor(view.getResources().getColor(R.color.blue649cf0));
                fontsizenomal.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizebig.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                mFontSize = "small";
                QuestionBankDetailsQuestionModeShow();
            });
            fontsizenomal.setOnClickListener(v -> {
                fontsizesmall.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizenomal.setTextColor(view.getResources().getColor(R.color.blue649cf0));
                fontsizebig.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                mFontSize = "nomal";
                QuestionBankDetailsQuestionModeShow();
            });
            fontsizebig.setOnClickListener(v -> {
                fontsizesmall.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizenomal.setTextColor(view.getResources().getColor(R.color.collectdefaultcolor));
                fontsizebig.setTextColor(view.getResources().getColor(R.color.blue649cf0));
                mFontSize = "big";
                QuestionBankDetailsQuestionModeShow();
            });
        }
    }

    //起始，结束，时间长度
    private void toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(progress -> {
            // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
            bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
            backgroundAlpha(bgAlpha);
        });
        animUtil.addEndListner(animator -> {
            // 在一次动画结束的时候，翻转状态
            bright = !bright;
        });
        animUtil.startAnimator();
    }

    /**
     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mControlMainActivity.getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        mControlMainActivity.getWindow().setAttributes(lp);
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        mControlMainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onDestroyView() {
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimer2 = null;
        }
        if (mTask2 != null) {
            mTask2.cancel();
            mTask2 = null;
        }
        super.onDestroyView();
    }

    //题库--章节考点
    public static class MyQuestionBankChapterTestBean {
        /**
         * msg : 查询成功
         * code : 200
         * data : [{"chapter_test_point_id":1,"ibs_id":1,"num":39,"jie":[{"chapter_test_point_id":6,"ibs_id":1,"num":39,"name":"节1333","father_id":1,"type":2}],"name":"章1222","father_id":0,"type":1},{"chapter_test_point_id":2,"ibs_id":1,"num":38,"jie":[{"chapter_test_point_id":7,"ibs_id":1,"num":36,"name":"节1","father_id":2,"type":2},{"chapter_test_point_id":12,"ibs_id":1,"num":2,"name":"节1","father_id":2,"type":2}],"name":"章2333","father_id":0,"type":1},{"chapter_test_point_id":3,"ibs_id":1,"num":41,"jie":[{"chapter_test_point_id":8,"ibs_id":1,"num":41,"name":"节1","father_id":3,"type":2}],"name":"章3","father_id":0,"type":1},{"chapter_test_point_id":4,"ibs_id":1,"num":47,"jie":[{"chapter_test_point_id":9,"ibs_id":1,"num":47,"name":"节1","father_id":4,"type":2}],"name":"章4","father_id":0,"type":1}]
         */

        private String msg;
        private int code;
        private List<DataBean> data;
        private Integer error_num;
        private Integer collection_num;
        private Integer DoRecord_num;

        public Integer getError_num() {
            return error_num;
        }

        public void setError_num(Integer error_num) {
            this.error_num = error_num;
        }

        public Integer getCollection_num() {
            return collection_num;
        }

        public void setCollection_num(Integer collection_num) {
            this.collection_num = collection_num;
        }

        public Integer getDoRecord_num() {
            return DoRecord_num;
        }

        public void setDoRecord_num(Integer doRecord_num) {
            DoRecord_num = doRecord_num;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * chapter_test_point_id : 1
             * ibs_id : 1
             * num : 39
             * jie : [{"chapter_test_point_id":6,"ibs_id":1,"num":39,"name":"节1333","father_id":1,"type":2}]
             * name : 章1222
             * father_id : 0
             * type : 1
             */

            private int chapter_test_point_id;
            private int num;
            private String name;
            private int father_id;
            private int type;
            private List<JieBean> jie;

            public int getChapter_test_point_id() {
                return chapter_test_point_id;
            }

            public void setChapter_test_point_id(int chapter_test_point_id) {
                this.chapter_test_point_id = chapter_test_point_id;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getFather_id() {
                return father_id;
            }

            public void setFather_id(int father_id) {
                this.father_id = father_id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public List<JieBean> getJie() {
                return jie;
            }

            public void setJie(List<JieBean> jie) {
                this.jie = jie;
            }

            public static class JieBean {
                /**
                 * chapter_test_point_id : 6
                 * ibs_id : 1
                 * num : 39
                 * name : 节1333
                 * father_id : 1
                 * type : 2
                 */

                private int chapter_test_point_id;
                private int num;
                private String name;
                private int father_id;
                private int type;

                public int getChapter_test_point_id() {
                    return chapter_test_point_id;
                }

                public void setChapter_test_point_id(int chapter_test_point_id) {
                    this.chapter_test_point_id = chapter_test_point_id;
                }

                public int getNum() {
                    return num;
                }

                public void setNum(int num) {
                    this.num = num;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getFather_id() {
                    return father_id;
                }

                public void setFather_id(int father_id) {
                    this.father_id = father_id;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }
            }
        }
    }

    //题库--做题设置
    public class QueryTopicSettingBean {
        private int code ;
        private String msg;
        private QueryTopicSettingDataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(QueryTopicSettingDataBean data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public QueryTopicSettingDataBean getData() {
            return data;
        }

        class QueryTopicSettingDataBean {
            private QueryTopicSettingNumberDataBean duoXuan;
            private QueryTopicSettingNumberDataBean danXuan;
            private QueryTopicSettingNumberDataBean caiLiao;
            private QueryTopicSettingNumberDataBean jianDa;

            public QueryTopicSettingNumberDataBean getCaiLiao() {
                return caiLiao;
            }

            public QueryTopicSettingNumberDataBean getDanXuan() {
                return danXuan;
            }

            public QueryTopicSettingNumberDataBean getDuoXuan() {
                return duoXuan;
            }

            public QueryTopicSettingNumberDataBean getJianDa() {
                return jianDa;
            }

            public void setCaiLiao(QueryTopicSettingNumberDataBean caiLiao) {
                this.caiLiao = caiLiao;
            }

            public void setDanXuan(QueryTopicSettingNumberDataBean danXuan) {
                this.danXuan = danXuan;
            }

            public void setDuoXuan(QueryTopicSettingNumberDataBean duoXuan) {
                this.duoXuan = duoXuan;
            }

            public void setJianDa(QueryTopicSettingNumberDataBean jianDa) {
                this.jianDa = jianDa;
            }
        }
        class QueryTopicSettingNumberDataBean {
            private Integer zongShu;
            private Integer weiZuo;
            private Integer cuoTi;

            public Integer getCuoTi() {
                return cuoTi;
            }

            public Integer getWeiZuo() {
                return weiZuo;
            }

            public Integer getZongShu() {
                return zongShu;
            }

            public void setCuoTi(Integer cuoTi) {
                this.cuoTi = cuoTi;
            }

            public void setWeiZuo(Integer weiZuo) {
                this.weiZuo = weiZuo;
            }

            public void setZongShu(Integer zongShu) {
                this.zongShu = zongShu;
            }
        }
    }


    //我的题库----列表
    public static class MyQuestionBankBean {
        private int code ;
        private String msg;
        List<MyQuestionBankDataBean> data;

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setData(List<MyQuestionBankDataBean> data) {
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public List<MyQuestionBankDataBean> getData() {
            return data;
        }

        class MyQuestionBankDataBean {
            private Integer item_bank_id;
            private String item_bank_name;
            private String icon;
            private List<MyQuestionBankSubDataBean> sub_library;
            private String brief_introduction;

            public Integer getItem_bank_id() {
                return item_bank_id;
            }

            public List<MyQuestionBankSubDataBean> getSub_library() {
                return sub_library;
            }

            public String getBrief_introduction() {
                return brief_introduction;
            }

            public String getIcon() {
                return icon;
            }

            public String getItem_bank_name() {
                return item_bank_name;
            }

            public void setBrief_introduction(String brief_introduction) {
                this.brief_introduction = brief_introduction;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public void setItem_bank_id(Integer item_bank_id) {
                this.item_bank_id = item_bank_id;
            }

            public void setItem_bank_name(String item_bank_name) {
                this.item_bank_name = item_bank_name;
            }

            public void setSub_library(List<MyQuestionBankSubDataBean> sub_library) {
                this.sub_library = sub_library;
            }
        }
        class MyQuestionBankSubDataBean {
            private Integer ibs_id;
            private String ibs_name;

            public Integer getIbs_id() {
                return ibs_id;
            }

            public String getIbs_name() {
                return ibs_name;
            }

            public void setIbs_id(Integer ibs_id) {
                this.ibs_id = ibs_id;
            }

            public void setIbs_name(String ibs_name) {
                this.ibs_name = ibs_name;
            }
        }
    }

    //首页-----查看试卷
    public static class QuestionBankTestPaperBean {
        private Integer code;
        private List<QuestionBankTestPaperDataBean> data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public void setData(List<QuestionBankTestPaperDataBean> data) {
            this.data = data;
        }

        public List<QuestionBankTestPaperDataBean> getData() {
            return data;
        }

        class QuestionBankTestPaperDataBean {
            private Integer answer_time;
            private Integer test_paper_type;
            private Integer test_paper_id;
            private Double total_score;
            private String test_paper_name;

            public Double getTotal_score() {
                return total_score;
            }

            public Integer getAnswer_time() {
                return answer_time;
            }

            public Integer getTest_paper_id() {
                return test_paper_id;
            }

            public Integer getTest_paper_type() {
                return test_paper_type;
            }

            public String getTest_paper_name() {
                return test_paper_name;
            }

            public void setAnswer_time(Integer answer_time) {
                this.answer_time = answer_time;
            }

            public void setTest_paper_id(Integer test_paper_id) {
                this.test_paper_id = test_paper_id;
            }

            public void setTest_paper_name(String test_paper_name) {
                this.test_paper_name = test_paper_name;
            }

            public void setTest_paper_type(Integer test_paper_type) {
                this.test_paper_type = test_paper_type;
            }

            public void setTotal_score(Double total_score) {
                this.total_score = total_score;
            }
        }
    }

    //首页-----题库列表(包括子题库)
    public static class QuestionBankBean {
        /**
         * msg : 查询成功
         * code : 200
         * data : [{"item_bank_id":1,"item_bank_name":"题库名22","icon":"./assets/images/tiku9.svg","brief_introduction":"这是题库一","sub_library":[{"ibs_id":1,"ibs_name":"name222"},{"ibs_id":4,"ibs_name":"科目四"},{"ibs_id":7,"ibs_name":"name1"},{"ibs_id":8,"ibs_name":"name2"},{"ibs_id":9,"ibs_name":"name3"},{"ibs_id":10,"ibs_name":"name4"},{"ibs_id":11,"ibs_name":"name5"},{"ibs_id":12,"ibs_name":"name6"},{"ibs_id":13,"ibs_name":"name72"},{"ibs_id":17,"ibs_name":"222"},{"ibs_id":18,"ibs_name":"1334"},{"ibs_id":19,"ibs_name":"江山111"},{"ibs_id":20,"ibs_name":"test11"}]},{"item_bank_id":2,"item_bank_name":"题库二","icon":"./assets/images/tiku9.svg","brief_introduction":"这是题库二","sub_library":[{"ibs_id":2,"ibs_name":"科目二"}]},{"item_bank_id":3,"item_bank_name":"题库三","icon":"./assets/images/tiku7.svg","brief_introduction":"这是题库三","sub_library":[{"ibs_id":3,"ibs_name":"科目三"},{"ibs_id":16,"ibs_name":"江山"}]},{"item_bank_id":6,"item_bank_name":"www","icon":"./assets/images/tiku9.svg","brief_introduction":"www","sub_library":[]}]
         */
        private String msg;
        private int code;
        private List<DataBean> data;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * item_bank_id : 1
             * item_bank_name : 题库名22
             * icon : ./assets/images/tiku9.svg
             * brief_introduction : 这是题库一
             * sub_library : [{"ibs_id":1,"ibs_name":"name222"},{"ibs_id":4,"ibs_name":"科目四"},{"ibs_id":7,"ibs_name":"name1"},{"ibs_id":8,"ibs_name":"name2"},{"ibs_id":9,"ibs_name":"name3"},{"ibs_id":10,"ibs_name":"name4"},{"ibs_id":11,"ibs_name":"name5"},{"ibs_id":12,"ibs_name":"name6"},{"ibs_id":13,"ibs_name":"name72"},{"ibs_id":17,"ibs_name":"222"},{"ibs_id":18,"ibs_name":"1334"},{"ibs_id":19,"ibs_name":"江山111"},{"ibs_id":20,"ibs_name":"test11"}]
             */

            private int item_bank_id;
            private String item_bank_name;
            private String icon;
            private String brief_introduction;
            private List<SubLibraryBean> sub_library;
            private Integer TF;

            public Integer getTF() {
                return TF;
            }

            public void setTF(Integer TF) {
                this.TF = TF;
            }

            public int getItem_bank_id() {
                return item_bank_id;
            }

            public void setItem_bank_id(int item_bank_id) {
                this.item_bank_id = item_bank_id;
            }

            public String getItem_bank_name() {
                return item_bank_name;
            }

            public void setItem_bank_name(String item_bank_name) {
                this.item_bank_name = item_bank_name;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getBrief_introduction() {
                return brief_introduction;
            }

            public void setBrief_introduction(String brief_introduction) {
                this.brief_introduction = brief_introduction;
            }

            public List<SubLibraryBean> getSub_library() {
                return sub_library;
            }

            public void setSub_library(List<SubLibraryBean> sub_library) {
                this.sub_library = sub_library;
            }

            public static class SubLibraryBean {
                /**
                 * ibs_id : 1
                 * ibs_name : name222
                 */

                private int ibs_id;
                private String ibs_name;

                public int getIbs_id() {
                    return ibs_id;
                }

                public void setIbs_id(int ibs_id) {
                    this.ibs_id = ibs_id;
                }

                public String getIbs_name() {
                    return ibs_name;
                }

                public void setIbs_name(String ibs_name) {
                    this.ibs_name = ibs_name;
                }
            }
        }
    }

    //判断当前任务是否继续
    public static class MyQuestionBankGoonBean {
        private Integer code;
        private String msg;
        private List<MyQuestionBankGoonDataBean> data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<MyQuestionBankGoonDataBean> getData() {
            return data;
        }

        public void setData(List<MyQuestionBankGoonDataBean> data) {
            this.data = data;
        }

        class MyQuestionBankGoonDataBean {
            private String name;
            private String time;
            private Integer type;
            private Integer answer_id;
            private String test_paper_name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public Integer getAnswer_id() {
                return answer_id;
            }

            public void setAnswer_id(Integer answer_id) {
                this.answer_id = answer_id;
            }

            public String getTest_paper_name() {
                return test_paper_name;
            }

            public void setTest_paper_name(String test_paper_name) {
                this.test_paper_name = test_paper_name;
            }
        }
    }

    //题库-做题记录Bean
    public static class QuestionBankAnswerRecordBean{
        private String msg;
        private Integer code;
        private QuestionBankAnswerRecordDataBean data;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public QuestionBankAnswerRecordDataBean getData() {
            return data;
        }

        public void setData(QuestionBankAnswerRecordDataBean data) {
            this.data = data;
        }

        class QuestionBankAnswerRecordDataBean{
            private Integer total;
            private List<QuestionBankAnswerRecordDataBeanList> list;

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public List<QuestionBankAnswerRecordDataBeanList> getList() {
                return list;
            }

            public void setList(List<QuestionBankAnswerRecordDataBeanList> list) {
                this.list = list;
            }
        }
        class QuestionBankAnswerRecordDataBeanList {
            private Integer used_answer_time;
            private Integer type;
            private Integer answer_id;
            private Integer chapter_test_point_id;
            private String name;
            private Integer dui;
            private Integer test_paper_id;
            private String time;
            private Integer cuo;
            private String test_paper_name;
            public Integer getUsed_answer_time() {
                return used_answer_time;
            }

            public void setUsed_answer_time(Integer used_answer_time) {
                this.used_answer_time = used_answer_time;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public Integer getAnswer_id() {
                return answer_id;
            }

            public void setAnswer_id(Integer answer_id) {
                this.answer_id = answer_id;
            }

            public Integer getChapter_test_point_id() {
                return chapter_test_point_id;
            }

            public void setChapter_test_point_id(Integer chapter_test_point_id) {
                this.chapter_test_point_id = chapter_test_point_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getDui() {
                return dui;
            }

            public void setDui(Integer dui) {
                this.dui = dui;
            }

            public Integer getTest_paper_id() {
                return test_paper_id;
            }

            public void setTest_paper_id(Integer test_paper_id) {
                this.test_paper_id = test_paper_id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public Integer getCuo() {
                return cuo;
            }

            public void setCuo(Integer cuo) {
                this.cuo = cuo;
            }
        }
    }


    //题库-做题记录
    public void  getQuestionBankAnswerRecord(){
        if (mControlMainActivity.mStuId.equals("") || mIbs_id.equals("")){
            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
            if (mSmart_model_questionbank_questionrecord != null){
                mSmart_model_questionbank_questionrecord.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        mCurrentPage = 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("student_id", Integer.valueOf(mControlMainActivity.mStuId));//做题记录的参数
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize", mPageCount);
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));
        int type = 2;
        if (mQuestionRecordCurrentTab.equals("QuickTask")){
            type = 1;
        } else if (mQuestionRecordCurrentTab.equals("Simulated")){
            type = 3;
        }
        paramsMap.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryQuestionBankAnswerRecord(body)
                .enqueue(new Callback<QuestionBankAnswerRecordBean>() {
                    @Override
                    public void onResponse(Call<QuestionBankAnswerRecordBean> call, Response<QuestionBankAnswerRecordBean> response) {
                        QuestionBankAnswerRecordBean questionBankAnswerRecordBean = response.body();
                        if (questionBankAnswerRecordBean == null){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(questionBankAnswerRecordBean.code,questionBankAnswerRecordBean.msg)){
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerRecordBean.code != 200){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerRecordBean.data == null){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerRecordBean.data.total == null || questionBankAnswerRecordBean.data.list == null){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mSum = questionBankAnswerRecordBean.data.total;
                        questionBankAnswerRecordDataBeanLists = questionBankAnswerRecordBean.data.list;
                        mMyFavoriteQuestionDataBeans = null;
                        mAnswer_Id = null;
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyQuestionBankExercisesBean = null;
                        mCurrentIndex = 0;
                        QuestionBankDetailsQuestionModeQuestionRecordShow();
                        if (mSmart_model_questionbank_questionrecord != null){
                            mSmart_model_questionbank_questionrecord.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<QuestionBankAnswerRecordBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                        if (mSmart_model_questionbank_questionrecord != null){
                            mSmart_model_questionbank_questionrecord.finishRefresh();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //题库-做题记录-加载更多
    public void  getQuestionBankAnswerRecordMore(){
        if (mControlMainActivity.mStuId.equals("") || mIbs_id.equals("")){
            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
            if (mSmart_model_questionbank_questionrecord != null){
                mSmart_model_questionbank_questionrecord.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        mCurrentPage = mCurrentPage + 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("student_id", Integer.valueOf(mControlMainActivity.mStuId));//做题记录的参数
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize", mPageCount);
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));
        int type = 2;
        if (mQuestionRecordCurrentTab.equals("QuickTask")){
            type = 1;
        } else if (mQuestionRecordCurrentTab.equals("Simulated")){
            type = 3;
        }
        paramsMap.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryQuestionBankAnswerRecord(body)
                .enqueue(new Callback<QuestionBankAnswerRecordBean>() {
                    @Override
                    public void onResponse(Call<QuestionBankAnswerRecordBean> call, Response<QuestionBankAnswerRecordBean> response) {
                        QuestionBankAnswerRecordBean questionBankAnswerRecordBean = response.body();
                        if (questionBankAnswerRecordBean == null){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(questionBankAnswerRecordBean.code,questionBankAnswerRecordBean.msg)){
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerRecordBean.code != 200){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerRecordBean.data == null){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerRecordBean.data.total == null || questionBankAnswerRecordBean.data.list == null){
                            Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                            if (mSmart_model_questionbank_questionrecord != null){
                                mSmart_model_questionbank_questionrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mSum = questionBankAnswerRecordBean.data.total;
                        questionBankAnswerRecordDataBeanLists.addAll(questionBankAnswerRecordBean.data.list);
                        View questionbank_questionrecords_line1 = null;
                        for (int i = 0; i < questionBankAnswerRecordBean.data.list.size(); i ++) {
                            QuestionBankAnswerRecordBean.QuestionBankAnswerRecordDataBeanList questionBankAnswerRecordDataBeanList = questionBankAnswerRecordBean.data.list.get(i);
                            if (questionBankAnswerRecordDataBeanList == null){
                                continue;
                            }
                            //试卷名称  测试的名称   网络请求
                            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_questionrecord1, null);
                            //试卷名称
                            TextView questionbank_questionrecords_testname = view.findViewById(R.id.questionbank_questionrecords_testname);
                            String name = "";
                            if (questionBankAnswerRecordDataBeanList.name != null){
                                name = questionBankAnswerRecordDataBeanList.name;
                            }
                            if (questionBankAnswerRecordDataBeanList.test_paper_name != null){
                                name = questionBankAnswerRecordDataBeanList.test_paper_name;
                            }
                            if (mQuestionRecordCurrentTab.equals("QuickTask")){
                                name = "快速做题";
                            }
                            questionbank_questionrecords_testname.setText(name);
                            questionbank_questionrecords_testname.setHint(questionBankAnswerRecordDataBeanList.answer_id + "");
                            //试卷时间
                            TextView questionbank_questionrecords_detailstime = view.findViewById(R.id.questionbank_questionrecords_detailstime);
                            if (questionBankAnswerRecordDataBeanList.time == null) {
                                questionBankAnswerRecordDataBeanList.time = "";
                            } else {
                                Date date = null;
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                try {
                                    date = df.parse(questionBankAnswerRecordDataBeanList.time);
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
                                        questionBankAnswerRecordDataBeanList.time = df2.format(date1).toString();
                                    }
                                }
                            }
                            if (questionBankAnswerRecordDataBeanList.used_answer_time == null) {
                                questionBankAnswerRecordDataBeanList.used_answer_time = 0;
                            }
                            if (questionBankAnswerRecordDataBeanList.dui == null) {
                                questionBankAnswerRecordDataBeanList.dui = 0;
                            }
                            if (questionBankAnswerRecordDataBeanList.cuo == null) {
                                questionBankAnswerRecordDataBeanList.cuo = 0;
                            }

                            questionbank_questionrecords_detailstime.setText(questionBankAnswerRecordDataBeanList.time);
                            LinearLayout questionbank_questionrecords_content = mModelQuestionBankQuestionRecordView.findViewById(R.id.questionbank_questionrecords_content);
                            questionbank_questionrecords_content.addView(view);
                            //试卷的时间
                            TextView questionbank_questionrecords_detailsduring = view.findViewById(R.id.questionbank_questionrecords_detailsduring);
                            questionbank_questionrecords_detailsduring.setText(getStringTime(questionBankAnswerRecordDataBeanList.used_answer_time));
                            //正确的数量
                            TextView questionbank_questionrecords_detailsrightnum = view.findViewById(R.id.questionbank_questionrecords_detailsrightnum);
                            questionbank_questionrecords_detailsrightnum.setText(questionBankAnswerRecordDataBeanList.dui + "");
                            //错误的数量
                            TextView questionbank_questionrecords_detailserrornum = view.findViewById(R.id.questionbank_questionrecords_detailserrornum);
                            questionbank_questionrecords_detailserrornum.setText(questionBankAnswerRecordDataBeanList.cuo + "");
                            questionbank_questionrecords_line1 = view.findViewById(R.id.questionbank_questionrecords_line1);
                            Integer finalUsed_answer_time = questionBankAnswerRecordDataBeanList.used_answer_time;
                            String finalName = name;
                            view.setOnClickListener(v -> {
                                //弹出提示框，
                                View view1 = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
                                ControllerCenterDialog mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view1, R.style.DialogTheme);
                                mMyDialog.setCancelable(true);
                                mMyDialog.show();
                                TextView tip = view1.findViewById(R.id.tip);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) tip.getLayoutParams();
                                rl.height = 0;
                                tip.setLayoutParams(rl);
                                TextView dialog_content = view1.findViewById(R.id.dialog_content);
                                dialog_content.setText("该试卷已完成，耗时" + getStringTime(finalUsed_answer_time));
                                TextView button_cancel = view1.findViewById(R.id.button_cancel);
                                button_cancel.setText("查看解析");
                                button_cancel.setOnClickListener(View -> {
                                    //跳转到解析界面
                                    Toast.makeText(mControlMainActivity, "查看解析内容", Toast.LENGTH_SHORT).show();
                                    mAnswer_Id = questionBankAnswerRecordDataBeanList.answer_id;
                                    if (mQuestionRecordCurrentTab.equals("ChapterExercises")){
                                        getQuestionBankAnswerRecordLookChapter(finalName);
                                    } else if (mQuestionRecordCurrentTab.equals("QuickTask")){
                                        getQuestionBankAnswerRecordLookChapter(finalName);
                                    } else if (mQuestionRecordCurrentTab.equals("Simulated")){
                                        getQuestionBankAnswerRecordLook(finalName);
                                    }
                                    mMyDialog.cancel();
                                });
                                TextView button_sure = view1.findViewById(R.id.button_sure);
                                button_sure.setText("再做一遍");
                                button_sure.setOnClickListener(View -> {//将试卷重新调出来，做题
//                                    if (myQuestionBankGoonDataBeans != null){
//                                        if (myQuestionBankGoonDataBeans.size() != 0) {
//                                            Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                                            return;
//                                        }
//                                    }
                                    mAnswer_Id = questionBankAnswerRecordDataBeanList.answer_id;
                                    if (mQuestionRecordCurrentTab.equals("ChapterExercises")){
                                        mChapter_test_point_id = questionBankAnswerRecordDataBeanList.chapter_test_point_id;
                                        getQueryTopicSetting(mChapter_test_point_id);
                                    } else if (mQuestionRecordCurrentTab.equals("QuickTask")){
                                        getqueryMyQuestionBankQuickIssue();
                                    } else if (mQuestionRecordCurrentTab.equals("Simulated")){
                                        getQuestionBankAnswerRecordAgain(finalName);
                                    }
                                    mMyDialog.cancel();
                                });
                            });
                        }
                        if (questionbank_questionrecords_line1 != null) {
                            questionbank_questionrecords_line1.setVisibility(View.INVISIBLE);
                        }
                        if (mSmart_model_questionbank_questionrecord != null){
                            mSmart_model_questionbank_questionrecord.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<QuestionBankAnswerRecordBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mControlMainActivity,"查询做题记录失败！",Toast.LENGTH_SHORT).show();
                        if (mSmart_model_questionbank_questionrecord != null){
                            mSmart_model_questionbank_questionrecord.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //题库-做题记录-再做一遍
    public void  getQuestionBankAnswerRecordAgain(String name){
        if (mAnswer_Id == null){
            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
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
        paramsMap.put("answer_id", mAnswer_Id);//做题记录的参数
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryQuestionBankAnswerRecordAgain(body)
                .enqueue(new Callback<MyTestPageIssueBean>() {
                    @Override
                    public void onResponse(Call<MyTestPageIssueBean> call, Response<MyTestPageIssueBean> response) {
                        MyTestPageIssueBean myTestPageIssueBean = response.body();
                        if (myTestPageIssueBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myTestPageIssueBean.code,myTestPageIssueBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myTestPageIssueBean.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (myTestPageIssueBean.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mMyTestPageIssueDataBeans = myTestPageIssueBean.data;
                        mTestPageIssueDataTypeScore = myTestPageIssueBean.question_type_score;
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyQuestionBankExercisesBean = null;
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mAnswer_Id = Integer.valueOf(myTestPageIssueBean.answer_id);
                        if (myTestPageIssueBean.answer_time == null) {
                            mTime = 0;
                        } else {
                            mMyTestPageIssueTime = myTestPageIssueBean.answer_time;
                            mTime = myTestPageIssueBean.answer_time - myTestPageIssueBean.used_answer_time;
                        }
                        mCurrentChapterName = name;
                        QuestionBankDetailsQuestionModeTestPaperShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyTestPageIssueBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //题库-做题记录-查看解析试卷
    public void  getQuestionBankAnswerRecordLook(String name){
        if (mAnswer_Id == null){
            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
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
        paramsMap.put("answer_id", mAnswer_Id);//做题记录的参数
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryQuestionBankAnswerRecordLook(body)
                .enqueue(new Callback<MyTestPageIssueBean>() {
                    @Override
                    public void onResponse(Call<MyTestPageIssueBean> call, Response<MyTestPageIssueBean> response) {
                        MyTestPageIssueBean myTestPageIssueBean = response.body();
                        if (myTestPageIssueBean == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myTestPageIssueBean.code,myTestPageIssueBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myTestPageIssueBean.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (myTestPageIssueBean.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mMyTestPageIssueDataBeans = myTestPageIssueBean.data;
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyQuestionBankExercisesBean = null;
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mAnswer_Id = Integer.valueOf(myTestPageIssueBean.answer_id);
                        if (myTestPageIssueBean.answer_time == null) {
                            mTime = 0;
                        } else {
                            mMyTestPageIssueTime = myTestPageIssueBean.answer_time;
                            mTime = myTestPageIssueBean.answer_time - myTestPageIssueBean.used_answer_time;
                        }
                        mCurrentChapterName = name;
                        for (MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean: mMyTestPageIssueDataBeans){
                            if (myTestPageIssueDataBean == null){
                                continue;
                            }
                            if (myTestPageIssueDataBean.question_id == null){
                                continue;
                            }
//                            myTestPageIssueDataBean.myAnswer = "";
                            if (myTestPageIssueDataBean.question_type == 1 || myTestPageIssueDataBean.question_type == 2) {
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myTestPageIssueDataBean.myAnswer;
                                if (myTestPageIssueDataBean.myAnswer.equals(myTestPageIssueDataBean.answer)) {
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id, answerInfo);
                            } else if (myTestPageIssueDataBean.question_type == 4){
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myTestPageIssueDataBean.myAnswer;
                                if (!myTestPageIssueDataBean.myAnswer.equals("")){ //简答题只要答案不为空全是正确
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id, answerInfo);
                            } else if (myTestPageIssueDataBean.question_type == 7){
                                if (myTestPageIssueDataBean.question_id_group1 != null) { // 说明有子题
                                    for (int num = 0; num < myTestPageIssueDataBean.question_id_group1.size(); num ++) {
                                        MyTestPageIssueBean.MyTestPageIssueDataBean MyTestPageIssueDataBean1 = myTestPageIssueDataBean.question_id_group1.get(num);
                                        if (MyTestPageIssueDataBean1 == null) {
                                            continue;
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        answerInfo.answer = MyTestPageIssueDataBean1.myAnswer;
                                        if (MyTestPageIssueDataBean1.question_type == 14){
                                            if (!MyTestPageIssueDataBean1.myAnswer.equals("")) {//简答题只要答案不为空全是正确
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        } else {
                                            if (MyTestPageIssueDataBean1.myAnswer.equals(MyTestPageIssueDataBean1.answer)) {
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyTestPageIssueDataBean1.question_id,answerInfo);
                                    }
                                }
                            }
                        }
                        if (myTestPageIssueBean.score == null){
                            myTestPageIssueBean.score = 0;
                        } else {
                            mTestPageIssueDataScore = String.valueOf(myTestPageIssueBean.score);
                        }

                        QuestionBankDetailsQuestionModeHandInShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyTestPageIssueBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //题库-做题记录-查看解析章节考点、快速出题
    public void  getQuestionBankAnswerRecordLookChapter(String name){
        if (mAnswer_Id == null){
            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
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
        paramsMap.put("answer_id", mAnswer_Id);//做题记录的参数
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryQuestionBankAnswerRecordLookChapter(body)
                .enqueue(new Callback<MyQuestionBankExercises>() {
                    @Override
                    public void onResponse(Call<MyQuestionBankExercises> call, Response<MyQuestionBankExercises> response) {
                        MyQuestionBankExercises myQuestionBankExercises = response.body();
                        if (myQuestionBankExercises == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myQuestionBankExercises.code,myQuestionBankExercises.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (myQuestionBankExercises.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyQuestionBankExercisesBean = myQuestionBankExercises.data;
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mAnswer_Id = Integer.valueOf(myQuestionBankExercises.answer_id);
                        if (myQuestionBankExercises.used_answer_time == null) {
                            mTime = 0;
                        } else {
                            mTime = myQuestionBankExercises.used_answer_time;
                        }
                        mCurrentChapterName = name;
                        if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                            for (MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean:mMyQuestionBankExercisesBean.cailiaotiQuestion){
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.question_id == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.question_sub_group != null || myQuestionBankExercisesDataBean.question_id_group1 != null) { // 说明有子题
                                    if (myQuestionBankExercisesDataBean.question_id_group1 == null) {
                                        myQuestionBankExercisesDataBean.question_id_group1 = myQuestionBankExercisesDataBean.question_sub_group;
                                    }
                                    for (int num = 0; num < myQuestionBankExercisesDataBean.question_id_group1.size(); num ++) {
                                        MyQuestionBankExercises.MyQuestionBankExercisesDataBean MyQuestionBankExercisesDataBean1 = myQuestionBankExercisesDataBean.question_id_group1.get(num);
                                        if (MyQuestionBankExercisesDataBean1 == null) {
                                            continue;
                                        }
                                        if (MyQuestionBankExercisesDataBean1.myAnswer == null){
                                            MyQuestionBankExercisesDataBean1.myAnswer = "";
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        answerInfo.answer = MyQuestionBankExercisesDataBean1.myAnswer;
                                        if (MyQuestionBankExercisesDataBean1.question_type == 14){
                                            if (!MyQuestionBankExercisesDataBean1.myAnswer.equals("")) {//简答题只要答案不为空全是正确
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        } else {
                                            if (MyQuestionBankExercisesDataBean1.myAnswer.equals(MyQuestionBankExercisesDataBean1.answer)) {
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyQuestionBankExercisesDataBean1.question_id,answerInfo);
                                    }
                                }
                            }
                        }
                        if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                            for (MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean:mMyQuestionBankExercisesBean.jinadatitiQuestion){
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.myAnswer == null || myQuestionBankExercisesDataBean.answer == null){
                                    continue;
                                }
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myQuestionBankExercisesDataBean.myAnswer;
                                if (myQuestionBankExercisesDataBean.myAnswer.equals(myQuestionBankExercisesDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myQuestionBankExercisesDataBean.question_id,answerInfo);
                            }
                        }
                        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                            for (MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean:mMyQuestionBankExercisesBean.duoxuantiQuestion){
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.myAnswer == null || myQuestionBankExercisesDataBean.answer == null){
                                    continue;
                                }
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myQuestionBankExercisesDataBean.myAnswer;
                                if (myQuestionBankExercisesDataBean.myAnswer.equals(myQuestionBankExercisesDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myQuestionBankExercisesDataBean.question_id,answerInfo);
                            }
                        }
                        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                            for (MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean:mMyQuestionBankExercisesBean.danxuantiQuestion){
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.myAnswer == null || myQuestionBankExercisesDataBean.answer == null){
                                    continue;
                                }
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myQuestionBankExercisesDataBean.myAnswer;
                                if (myQuestionBankExercisesDataBean.myAnswer.equals(myQuestionBankExercisesDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myQuestionBankExercisesDataBean.question_id,answerInfo);
                            }
                        }
                        QuestionBankDetailsQuestionModeHandInShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankExercises> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mControlMainActivity,"试卷信息查询失败！",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //题库-答题卡Bean
    public static class QuestionBankAnswerSheetBean {
        private Integer code;
        private String msg;
        private  Integer mode;
        private List<QuestionBankAnswerSheetDataBean> data;

        public Integer getMode() {
            return mode;
        }

        public void setMode(Integer mode) {
            this.mode = mode;
        }

        public void setData(List<QuestionBankAnswerSheetDataBean> data) {
            this.data = data;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<QuestionBankAnswerSheetDataBean> getData() {
            return data;
        }

        class QuestionBankAnswerSheetDataBean {
            private Integer TF;
            private String question_id;
            private Integer tf_marked;
            private Integer type;
            private String mydaan;

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public void setTf_marked(Integer tf_marked) {
                this.tf_marked = tf_marked;
            }

            public Integer getTf_marked() {
                return tf_marked;
            }

            public void setTF(Integer TF) {
                this.TF = TF;
            }

            public Integer getTF() {
                return TF;
            }

            public void setQuestion_id(String question_id) {
                this.question_id = question_id;
            }

            public String getQuestion_id() {
                return question_id;
            }
        }
    }

    //题库-答题卡
    public void getQuestionBankAnswerSheet(int type) {
        if (mAnswer_Id == null){
            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
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
        paramsMap.put("answer_id", mAnswer_Id);//	答题卡参数
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryQuestionBankAnswerSheet(body)
                .enqueue(new Callback<QuestionBankAnswerSheetBean>() {
                    @Override
                    public void onResponse(Call<QuestionBankAnswerSheetBean> call, Response<QuestionBankAnswerSheetBean> response) {
                        QuestionBankAnswerSheetBean questionBankAnswerSheetBean = response.body();
                        if (questionBankAnswerSheetBean == null) {
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(questionBankAnswerSheetBean.code,questionBankAnswerSheetBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerSheetBean.code != 200){
                            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankAnswerSheetBean.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (questionBankAnswerSheetBean.mode == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"查询答题卡信息失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //判断是不是练习模式
                        int cunt = -1 ;
                        if (questionBankAnswerSheetBean.mode==1){
                            for (int i = 0; i < questionBankAnswerSheetBean.data.size(); i++) {
                                //判断我的答案是不是空如果是空的话给一个空串防止再判断闪退
                                if (questionBankAnswerSheetBean.data.get(i).mydaan==null){
                                    questionBankAnswerSheetBean.data.get(i).mydaan = "";
                                }
                                if (!questionBankAnswerSheetBean.data.get(i).mydaan.equals("")){
                                    cunt = i;
                                }
                            }
                            if (cunt!=-1 ){
                                while (questionBankAnswerSheetBean.data.size() > (cunt + 1)){//看过了没有答案也算做，移除没有做的题目
                                    questionBankAnswerSheetBean.data.remove(cunt+1);
                                }
                            }
                        }

                        if (type == 1) { //普通答题卡
                            AnswerQuestionCardViewAdd(questionBankAnswerSheetBean.data);
                        } else if (type == 2) { //答题后答题卡
                            QuestionBankDetailsHandInPaperShow(questionBankAnswerSheetBean.data);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<QuestionBankAnswerSheetBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //判断当前的任务是否继续
    public void getMyQuestionBankGoon() {
        if (mIbs_id.equals("") || mControlMainActivity.mStuId.equals("")){
            myQuestionBankGoonDataBeans = null;
            //问答详情  传值id和name
            QuestionBankDetailsShow();
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
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));//	子题库的id
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankGoon(body)
                .enqueue(new Callback<MyQuestionBankGoonBean>() {
                    @Override
                    public void onResponse(Call<MyQuestionBankGoonBean> call, Response<MyQuestionBankGoonBean> response) {
                        MyQuestionBankGoonBean bankGoonBean = response.body();
                        if (bankGoonBean == null) {
                            myQuestionBankGoonDataBeans = null;
                            //问答详情  传值id和name
                            QuestionBankDetailsShow();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(bankGoonBean.code,bankGoonBean.msg)){
                            myQuestionBankGoonDataBeans = null;
                            //问答详情  传值id和name
                            QuestionBankDetailsShow();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (bankGoonBean.code != 211) {
                            myQuestionBankGoonDataBeans = null;
                            //问答详情  传值id和name
                            QuestionBankDetailsShow();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        myQuestionBankGoonDataBeans = bankGoonBean.data;
                        //问答详情  传值id和name
                        QuestionBankDetailsShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankGoonBean> call, Throwable t) {
                        myQuestionBankGoonDataBeans = null;
                        //问答详情  传值id和name
                        QuestionBankDetailsShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    public void getQueryMyQuestionBankContinue(int type){
        if (mAnswer_Id == null){
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
        paramsMap.put("answer_id", mAnswer_Id);//	子题库的id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankContinue(body)
                .enqueue(new Callback<MyQuestionBankExercises>() {

                    @Override
                    public void onResponse(Call<MyQuestionBankExercises> call, Response<MyQuestionBankExercises> response) {
                        MyQuestionBankExercises myQuestionBankExercises = response.body();
                        if (myQuestionBankExercises == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myQuestionBankExercises.code,myQuestionBankExercises.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.used_answer_time == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesBean = myQuestionBankExercises.data;
                        if (mMyQuestionBankExercisesBean == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesAnswerMap.clear();
                        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null){
                            for (int i = 0; i < mMyQuestionBankExercisesBean.danxuantiQuestion.size(); i ++){
                                MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.danxuantiQuestion.get(i);
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.myAnswer == null || myQuestionBankExercisesDataBean.question_id == null){
                                    continue;
                                }
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myQuestionBankExercisesDataBean.myAnswer;
                                if (myQuestionBankExercisesDataBean.myAnswer.equals(myQuestionBankExercisesDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myQuestionBankExercisesDataBean.question_id,answerInfo);
                            }
                        }
                        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null){
                            for (int i = 0; i < mMyQuestionBankExercisesBean.duoxuantiQuestion.size(); i ++){
                                MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(i);
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.myAnswer == null || myQuestionBankExercisesDataBean.question_id == null){
                                    continue;
                                }
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myQuestionBankExercisesDataBean.myAnswer;
                                if (myQuestionBankExercisesDataBean.myAnswer.equals(myQuestionBankExercisesDataBean.answer)){
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myQuestionBankExercisesDataBean.question_id,answerInfo);
                            }
                        }
                        if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null){
                            for (int i = 0; i < mMyQuestionBankExercisesBean.jinadatitiQuestion.size(); i ++){
                                MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(i);
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.myAnswer == null || myQuestionBankExercisesDataBean.question_id == null){
                                    continue;
                                }
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = myQuestionBankExercisesDataBean.myAnswer;
                                if (!myQuestionBankExercisesDataBean.myAnswer.equals("")){ //简答题只要答案不为空全是正确
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myQuestionBankExercisesDataBean.question_id,answerInfo);
                            }
                        }
                        if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null){
                            for (int i = 0; i < mMyQuestionBankExercisesBean.cailiaotiQuestion.size(); i ++){
                                MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(i);
                                if (myQuestionBankExercisesDataBean == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.question_id == null){
                                    continue;
                                }
                                if (myQuestionBankExercisesDataBean.question_sub_group != null || myQuestionBankExercisesDataBean.question_id_group1 != null) { // 说明有子题
                                    if (myQuestionBankExercisesDataBean.question_id_group1 == null) {
                                        myQuestionBankExercisesDataBean.question_id_group1 = myQuestionBankExercisesDataBean.question_sub_group;
                                    }
                                    for (int num = 0; num < myQuestionBankExercisesDataBean.question_id_group1.size(); num ++) {
                                        MyQuestionBankExercises.MyQuestionBankExercisesDataBean MyQuestionBankExercisesDataBean1 = myQuestionBankExercisesDataBean.question_id_group1.get(num);
                                        if (MyQuestionBankExercisesDataBean1 == null) {
                                            continue;
                                        }
                                        if (MyQuestionBankExercisesDataBean1.myAnswer == null){
                                            MyQuestionBankExercisesDataBean1.myAnswer = "";
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        answerInfo.answer = MyQuestionBankExercisesDataBean1.myAnswer;
                                        if (MyQuestionBankExercisesDataBean1.question_type == 14){
                                            if (!MyQuestionBankExercisesDataBean1.myAnswer.equals("")) {//简答题只要答案不为空全是正确
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        } else {
                                            if (MyQuestionBankExercisesDataBean1.myAnswer.equals(MyQuestionBankExercisesDataBean1.answer)) {
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyQuestionBankExercisesDataBean1.question_id,answerInfo);
                                    }
                                }
                            }
                        }
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mTime = myQuestionBankExercises.used_answer_time;
                        if (type == 1) {
                            mCurrentChapterName = "快速做题";
                            QuestionBankDetailsQuestionModeExamShow();
                        } else if (type == 2){
                            QuestionBankDetailsQuestionModeTestShow();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankExercises> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    public void getQueryPageContinueTime (String name){
        if (mAnswer_Id == null){
            Toast.makeText(mControlMainActivity,"做题记录查询失败！",Toast.LENGTH_SHORT).show();
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
        paramsMap.put("answer_id", mAnswer_Id);//	子题库的id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryPageContinueTime(body)
                .enqueue(new Callback<MyTestPageIssueBean>() {

                    @Override
                    public void onResponse(Call<MyTestPageIssueBean> call, Response<MyTestPageIssueBean> response) {
                        MyTestPageIssueBean myTestPageIssueBean = response.body();
                        if (myTestPageIssueBean == null){
                            Toast.makeText(mControlMainActivity,"做题记录查询失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myTestPageIssueBean.code,myTestPageIssueBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myTestPageIssueBean.code != 200){
                            Toast.makeText(mControlMainActivity,"做题记录查询失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myTestPageIssueBean.used_answer_time == null){
                            Toast.makeText(mControlMainActivity,"做题记录查询失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyTestPageIssueDataBeans = myTestPageIssueBean.data;
                        mTestPageIssueDataTypeScore = myTestPageIssueBean.question_type_score;
                        if (mMyTestPageIssueDataBeans == null){
                            Toast.makeText(mControlMainActivity,"做题记录查询失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesAnswerMap.clear();
                        for (MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean: mMyTestPageIssueDataBeans){
                            if (myTestPageIssueDataBean == null){
                                continue;
                            }
                            if (myTestPageIssueDataBean.question_id == null){
                                continue;
                            }
                            if (myTestPageIssueDataBean.question_type == 1 || myTestPageIssueDataBean.question_type == 2) {
                                AnswerInfo answerInfo = new AnswerInfo();
                                if (myTestPageIssueDataBean.myAnswer == null){
                                    myTestPageIssueDataBean.myAnswer = "";
                                }
                                answerInfo.answer = myTestPageIssueDataBean.myAnswer;
                                if (myTestPageIssueDataBean.myAnswer.equals(myTestPageIssueDataBean.answer)) {
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id, answerInfo);
                            } else if (myTestPageIssueDataBean.question_type == 4){
                                AnswerInfo answerInfo = new AnswerInfo();
                                if (myTestPageIssueDataBean.myAnswer == null){
                                    myTestPageIssueDataBean.myAnswer = "";
                                }
                                answerInfo.answer = myTestPageIssueDataBean.myAnswer;
                                if (!myTestPageIssueDataBean.myAnswer.equals("")){ //简答题只要答案不为空全是正确
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(myTestPageIssueDataBean.question_id, answerInfo);
                            } else if (myTestPageIssueDataBean.question_type == 7){
                                if (myTestPageIssueDataBean.question_id_group1 != null) { // 说明有子题
                                    for (int num = 0; num < myTestPageIssueDataBean.question_id_group1.size(); num ++) {
                                        MyTestPageIssueBean.MyTestPageIssueDataBean MyTestPageIssueDataBean1 = myTestPageIssueDataBean.question_id_group1.get(num);
                                        if (MyTestPageIssueDataBean1 == null) {
                                            continue;
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        if (MyTestPageIssueDataBean1.myAnswer == null){
                                            MyTestPageIssueDataBean1.myAnswer = "";
                                        }
                                        answerInfo.answer = MyTestPageIssueDataBean1.myAnswer;
                                        if (MyTestPageIssueDataBean1.question_type == 14){
                                            if (!MyTestPageIssueDataBean1.myAnswer.equals("")) {//简答题只要答案不为空全是正确
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        } else {
                                            if (MyTestPageIssueDataBean1.myAnswer.equals(MyTestPageIssueDataBean1.answer)) {
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(MyTestPageIssueDataBean1.question_id,answerInfo);
                                    }
                                }
                            }
                        }
                        mMyQuestionBankExercisesBean = null;
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mMyTestPageIssueTime = myTestPageIssueBean.answer_time * 60;
                        mTime = myTestPageIssueBean.answer_time * 60 - myTestPageIssueBean.used_answer_time;
                        mCurrentChapterName = name;
                        QuestionBankDetailsQuestionModeTestPaperShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyTestPageIssueBean> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    public static class QuestionBankMyFavoriteQuestionBean{
        private Integer code;
        private String msg;
        private List<QuestionBankMyFavoriteQuestionDataBean> data;

        public void setData(List<QuestionBankMyFavoriteQuestionDataBean> data) {
            this.data = data;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<QuestionBankMyFavoriteQuestionDataBean> getData() {
            return data;
        }

        class QuestionBankMyFavoriteQuestionDataBean {
            public Integer getTf_wrong() {
                return tf_wrong;
            }

            public void setTf_wrong(Integer tf_wrong) {
                this.tf_wrong = tf_wrong;
            }

            public Integer getQuestion_type() {
                return question_type;
            }

            public void setQuestion_type(Integer question_type) {
                this.question_type = question_type;
            }

            public String getVideo_analysis() {
                return video_analysis;
            }

            public void setVideo_analysis(String video_analysis) {
                this.video_analysis = video_analysis;
            }

            public Integer getQuestion_id() {
                return question_id;
            }

            public void setQuestion_id(Integer question_id) {
                this.question_id = question_id;
            }

            public String getWrong_answer() {
                return wrong_answer;
            }

            public void setWrong_answer(String wrong_answer) {
                this.wrong_answer = wrong_answer;
            }

            public String getAudio_analysis() {
                return audio_analysis;
            }

            public void setAudio_analysis(String audio_analysis) {
                this.audio_analysis = audio_analysis;
            }

            public Integer getTf_marked() {
                return tf_marked;
            }

            public void setTf_marked(Integer tf_marked) {
                this.tf_marked = tf_marked;
            }

            public Integer getDifficulty() {
                return difficulty;
            }

            public void setDifficulty(Integer difficulty) {
                this.difficulty = difficulty;
            }

            public Integer getTf_collection() {
                return tf_collection;
            }

            public void setTf_collection(Integer tf_collection) {
                this.tf_collection = tf_collection;
            }

            public String getOptionanswer() {
                return optionanswer;
            }

            public void setOptionanswer(String optionanswer) {
                this.optionanswer = optionanswer;
            }

            public String getQuestion_analysis() {
                return question_analysis;
            }

            public void setQuestion_analysis(String question_analysis) {
                this.question_analysis = question_analysis;
            }

            public String getQuestion_name() {
                return question_name;
            }

            public void setQuestion_name(String question_name) {
                this.question_name = question_name;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public List<QuestionBankMyFavoriteQuestionDataBean> getQuestion_sub_id() {
                return question_sub_id;
            }

            public void setQuestion_sub_id(List<QuestionBankMyFavoriteQuestionDataBean> question_sub_id) {
                this.question_sub_id = question_sub_id;
            }

            private Integer tf_wrong;
            private Integer question_type;
            private String video_analysis;
            private Integer question_id;
            private String wrong_answer;
            private String audio_analysis;
            private Integer tf_marked;
            private Integer difficulty;
            private Integer tf_collection;
            private String optionanswer;
            private String question_analysis;
            private String question_name;
            private String answer;
            private List<QuestionBankMyFavoriteQuestionDataBean> question_sub_id;
        }
    }


    //题库  我的收藏题
    public void getQuestionBankMyFavoriteQuestion(Integer type){ //类型 1错题2收藏
        if (mControlMainActivity.mStuId.equals("") || mIbs_id.equals("")){
            if (type == 1){
                Toast.makeText(mControlMainActivity,"查询错题失败！",Toast.LENGTH_SHORT).show();
            } else if (type == 2){
                Toast.makeText(mControlMainActivity,"查询收藏的题失败！",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("student_id", Integer.valueOf(mControlMainActivity.mStuId));
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));//	子题库的id
        paramsMap.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryQuestionBankMyFavoriteQuestion(body)
                .enqueue(new Callback<QuestionBankMyFavoriteQuestionBean>() {
                    @Override
                    public void onResponse(Call<QuestionBankMyFavoriteQuestionBean> call, Response<QuestionBankMyFavoriteQuestionBean> response) {
                        QuestionBankMyFavoriteQuestionBean questionBankMyFavoriteQuestionBean = response.body();
                        if (questionBankMyFavoriteQuestionBean == null) {
                            if (type == 1) {
                                Toast.makeText(mControlMainActivity, "查询错题失败！", Toast.LENGTH_SHORT).show();
                            } else if (type == 2) {
                                Toast.makeText(mControlMainActivity, "查询收藏的题失败！", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(questionBankMyFavoriteQuestionBean.code,questionBankMyFavoriteQuestionBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankMyFavoriteQuestionBean.code != 200) {
                            if (type == 1) {
                                Toast.makeText(mControlMainActivity, "查询错题失败！", Toast.LENGTH_SHORT).show();
                            } else if (type == 2) {
                                Toast.makeText(mControlMainActivity, "查询收藏的题失败！", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (questionBankMyFavoriteQuestionBean.data == null) {
                            if (type == 1) {
                                Toast.makeText(mControlMainActivity, "查询错题失败！", Toast.LENGTH_SHORT).show();
                            } else if (type == 2) {
                                Toast.makeText(mControlMainActivity, "查询收藏的题失败！", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyFavoriteQuestionDataBeans = questionBankMyFavoriteQuestionBean.data;
                        mAnswer_Id = null;
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyQuestionBankExercisesAnswerMap.clear();
                        for (QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean :mMyFavoriteQuestionDataBeans){
                            if (questionBankMyFavoriteQuestionDataBean == null){
                                continue;
                            }
                            if (questionBankMyFavoriteQuestionDataBean.question_id == null){
                                continue;
                            }
                            if (questionBankMyFavoriteQuestionDataBean.tf_wrong == null){
                                questionBankMyFavoriteQuestionDataBean.tf_wrong = 1;
                            }
                            questionBankMyFavoriteQuestionDataBean.wrong_answer = "";
                            if (questionBankMyFavoriteQuestionDataBean.question_type == 1 || questionBankMyFavoriteQuestionDataBean.question_type == 2) {
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = questionBankMyFavoriteQuestionDataBean.wrong_answer;
                                if (questionBankMyFavoriteQuestionDataBean.tf_wrong != 1) {
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(questionBankMyFavoriteQuestionDataBean.question_id, answerInfo);
                            } else if (questionBankMyFavoriteQuestionDataBean.question_type == 4){
                                AnswerInfo answerInfo = new AnswerInfo();
                                answerInfo.answer = questionBankMyFavoriteQuestionDataBean.wrong_answer;
                                if (questionBankMyFavoriteQuestionDataBean.tf_wrong != 1){ //简答题只要答案不为空全是正确
                                    answerInfo.result = "对";
                                } else {
                                    answerInfo.result = "错";
                                }
                                mMyQuestionBankExercisesAnswerMap.put(questionBankMyFavoriteQuestionDataBean.question_id, answerInfo);
                            } else if (questionBankMyFavoriteQuestionDataBean.question_type == 7){
                                if (questionBankMyFavoriteQuestionDataBean.question_sub_id != null) { // 说明有子题
                                    for (int num = 0; num < questionBankMyFavoriteQuestionDataBean.question_sub_id.size(); num ++) {
                                        QuestionBankMyFavoriteQuestionBean.QuestionBankMyFavoriteQuestionDataBean questionBankMyFavoriteQuestionDataBean1 = questionBankMyFavoriteQuestionDataBean.question_sub_id.get(num);
                                        if (questionBankMyFavoriteQuestionDataBean1 == null) {
                                            continue;
                                        }
                                        AnswerInfo answerInfo = new AnswerInfo();
                                        answerInfo.answer = questionBankMyFavoriteQuestionDataBean1.wrong_answer;
                                        if (questionBankMyFavoriteQuestionDataBean1.question_type == 14){
                                            if (questionBankMyFavoriteQuestionDataBean.tf_wrong != 1) {//简答题只要答案不为空全是正确
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        } else {
                                            if (questionBankMyFavoriteQuestionDataBean.tf_wrong != 1) {
                                                answerInfo.result = "对";
                                            } else {
                                                answerInfo.result = "错";
                                            }
                                        }
                                        mMyQuestionBankExercisesAnswerMap.put(questionBankMyFavoriteQuestionDataBean1.question_id,answerInfo);
                                    }
                                }
                            }
                        }
                        mMyQuestionBankExercisesBean = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        if (type == 1) {
                            mCurrentChapterName = "错题本";
                            QuestionBankDetailsQuestionModeWrongQuestionShow();
                        } else if (type == 2){
                            mCurrentChapterName = "收藏的题";
                            QuestionBankDetailsQuestionModeMyCollectionQuestionShow();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<QuestionBankMyFavoriteQuestionBean> call, Throwable t) {
                        Log.e(TAG, "ModelQuestionBank”s Failure: "+t.getMessage());
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //queryMyQuestionBankChapterTest 题库章节考点
    public void getMyQuestionBankChapterTest() {
        if (mIbs_id.equals("") || mControlMainActivity.mStuId.equals("")){
            Toast.makeText(mControlMainActivity,"查询章节列表失败",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout questionbank_sub_details_content = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_content);
        questionbank_sub_details_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));//	子题库的id
        paramsMap.put("student_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankChapterTest(body)
                .enqueue(new Callback<MyQuestionBankChapterTestBean>() {

                    @Override
                    public void onResponse(Call<MyQuestionBankChapterTestBean> call, Response<MyQuestionBankChapterTestBean> response) {
                        MyQuestionBankChapterTestBean chapterTestBean = response.body();
                        if (chapterTestBean == null) {
                            Toast.makeText(mControlMainActivity,"查询章节列表失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(chapterTestBean.getCode(),chapterTestBean.getMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = chapterTestBean.getCode();
                        if (code != 200) {
                            Toast.makeText(mControlMainActivity,"查询章节列表失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        String msg = chapterTestBean.getMsg();
                        Log.d(TAG, "onResponse: " + msg);
                        if (chapterTestBean.error_num == null){
                            chapterTestBean.error_num = 0;
                        }
                        if (chapterTestBean.collection_num == null){
                            chapterTestBean.collection_num = 0;
                        }
                        if (chapterTestBean.DoRecord_num == null){
                            chapterTestBean.DoRecord_num = 0;
                        }
                        error_num = chapterTestBean.error_num;
                        collection_num = chapterTestBean.collection_num;
                        DoRecord_num = chapterTestBean.DoRecord_num;
                        List<MyQuestionBankChapterTestBean.DataBean> data = chapterTestBean.getData();
                        if (data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        for (int i = 0; i < data.size(); i ++) {
                            if (data.get(i) == null){
                                continue;
                            }
                            //章赋值//ModelExpandListView展开列表   章节练习网络请求
                            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_sub_detials_chapterexercises, null);
                            //基金法律法规
                            TextView questionbank_sub_details_chapterexercises_name = view.findViewById(R.id.questionbank_sub_details_chapterexercises_name);
                            questionbank_sub_details_chapterexercises_name.setText(data.get(i).getName());  //章名字
                            questionbank_sub_details_chapterexercises_name.setHint(data.get(i).getChapter_test_point_id() + "");//章id
                            TextView questionbank_sub_details_chapterexercises_count = view.findViewById(R.id.questionbank_sub_details_chapterexercises_count);
                            questionbank_sub_details_chapterexercises_count.setText("共计" + data.get(i).getNum() + "道题");
                            //默认全部展开
                            ImageView questionbank_sub_details_chapterexercises_arrow_right = view.findViewById(R.id.questionbank_sub_details_chapterexercises_arrow_right);
                            //显示章 展开下面的节或考点
                            ImageView questionbank_sub_details_chapterexercises_arrow_down = view.findViewById(R.id.questionbank_sub_details_chapterexercises_arrow_down);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_right.getLayoutParams();
                            ll.width = 0;
                            questionbank_sub_details_chapterexercises_arrow_right.setLayoutParams(ll);
                            //更多
                            ModelExpandView questionbank_sub_details_chapterexercises_expandView = view.findViewById(R.id.questionbank_sub_details_chapterexercises_expandView);
                            questionbank_sub_details_chapterexercises_arrow_right.setClickable(true);
                            questionbank_sub_details_chapterexercises_arrow_down.setClickable(true);
                            // //显示章 展开下面的节或考点    节或者考点的id
                            QuestionBankDetailsChapterExerisesShow(view, data.get(i).getJie());
                            int finalI = i;
                            questionbank_sub_details_chapterexercises_arrow_down.setOnClickListener(v -> {
                                // TODO Auto-generated method stub
                                if (questionbank_sub_details_chapterexercises_expandView.isExpand()) {
                                    questionbank_sub_details_chapterexercises_expandView.collapse();
                                    //收缩隐藏布局
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_sub_details_chapterexercises_expandView.getLayoutParams();
                                    rl.height = 0;
                                    questionbank_sub_details_chapterexercises_expandView.setLayoutParams(rl);
                                    questionbank_sub_details_chapterexercises_expandView.setVisibility(View.INVISIBLE);
                                    LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_right.getLayoutParams();
                                    ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp6);
                                    questionbank_sub_details_chapterexercises_arrow_right.setLayoutParams(ll1);
                                    ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_down.getLayoutParams();
                                    ll1.width = 0;
                                    questionbank_sub_details_chapterexercises_arrow_down.setLayoutParams(ll1);
                                } else {
                                    //题库的详情
                                    QuestionBankDetailsChapterExerisesShow(view, data.get(finalI).getJie());
                                }
                            });
                            questionbank_sub_details_chapterexercises_arrow_right.setOnClickListener(v -> {
                                // TODO Auto-generated method stub
                                if (questionbank_sub_details_chapterexercises_expandView.isExpand()) {
                                    questionbank_sub_details_chapterexercises_expandView.collapse();
                                    //收缩隐藏布局
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) questionbank_sub_details_chapterexercises_expandView.getLayoutParams();
                                    rl.height = 0;
                                    questionbank_sub_details_chapterexercises_expandView.setLayoutParams(rl);
                                    questionbank_sub_details_chapterexercises_expandView.setVisibility(View.INVISIBLE);
                                    LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_right.getLayoutParams();
                                    ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp6);
                                    questionbank_sub_details_chapterexercises_arrow_right.setLayoutParams(ll1);
                                    ll1 = (LinearLayout.LayoutParams) questionbank_sub_details_chapterexercises_arrow_down.getLayoutParams();
                                    ll1.width = 0;
                                    questionbank_sub_details_chapterexercises_arrow_down.setLayoutParams(ll1);
                                } else {
                                    QuestionBankDetailsChapterExerisesShow(view, data.get(finalI).getJie());
                                }
                            });
                            //点击章名称，进行章抽题
                            questionbank_sub_details_chapterexercises_name.setClickable(true);
                            int finalI1 = i;
                            questionbank_sub_details_chapterexercises_name.setOnClickListener(v -> {
//                                if (myQuestionBankGoonDataBeans != null) {
//                                    if (myQuestionBankGoonDataBeans.size() != 0) {
//                                        Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                }
                                mCurrentChapterName = questionbank_sub_details_chapterexercises_name.getText().toString();
                                mChapter_test_point_id = data.get(finalI1).chapter_test_point_id;
                                getQueryTopicSetting(data.get(finalI1).chapter_test_point_id);
                            });
                            questionbank_sub_details_content.addView(view);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankChapterTestBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        Toast.makeText(mControlMainActivity,"查询章节列表失败",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        return;
                    }
                });
    }

    //题库-设置    做题设置-查询题型错题和未做的题数
    public void getQueryTopicSetting(Integer chapter_test_point_id) {
        if (mControlMainActivity.mStuId.equals("") || chapter_test_point_id == null || mIbs_id.equals("")){
            Toast.makeText(mControlMainActivity,"查询做题设置失败",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        paramsMap.put("chapter_test_point_id", chapter_test_point_id);
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryTopicSetting(body)
                .enqueue(new Callback<QueryTopicSettingBean>() {

                    @Override
                    public void onResponse(Call<QueryTopicSettingBean> call, Response<QueryTopicSettingBean> response) {
                        QueryTopicSettingBean queryTopicSettingBean = response.body();
                        if (queryTopicSettingBean == null) {
                            Toast.makeText(mControlMainActivity,"查询做题设置失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(queryTopicSettingBean.getCode(),queryTopicSettingBean.getMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
//                        String msg = queryTopicSettingBean.getMsg();
                        int code = queryTopicSettingBean.getCode();
                        if (code != 200) {
                            Toast.makeText(mControlMainActivity,"查询做题设置失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        QueryTopicSettingBean.QueryTopicSettingDataBean queryTopicSettingDataBean = queryTopicSettingBean.getData();
                        if (queryTopicSettingDataBean == null){
                            Toast.makeText(mControlMainActivity,"查询做题设置失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (queryTopicSettingDataBean.duoXuan == null || queryTopicSettingDataBean.danXuan == null
                                || queryTopicSettingDataBean.jianDa == null || queryTopicSettingDataBean.caiLiao == null){
                            Toast.makeText(mControlMainActivity,"查询做题设置失败",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mSingleChoiceState = "disable";  //单选状态
                        mMultiChoiceState = "disable";  //多选状态
                        mShortAnswerState = "disable";  //简答状态
                        mMaterialQuestionState = "disable";  //材料题状态
                        mQuestionType = "AllQuestion";
                        if (queryTopicSettingDataBean.duoXuan.zongShu != null){
                            if (queryTopicSettingDataBean.duoXuan.zongShu != 0)
                                mMultiChoiceState = "select";
                        }
                        if (queryTopicSettingDataBean.danXuan.zongShu != null){
                            if (queryTopicSettingDataBean.danXuan.zongShu != 0)
                                mSingleChoiceState = "select";
                        }
                        if (queryTopicSettingDataBean.caiLiao.zongShu != null){
                            if (queryTopicSettingDataBean.caiLiao.zongShu != 0)
                                mMaterialQuestionState = "select";
                        }
                        if (queryTopicSettingDataBean.jianDa.zongShu != null){
                            if (queryTopicSettingDataBean.jianDa.zongShu != 0)
                                mShortAnswerState = "select";
                        }
                        mModelQuestionBankSettingView = null;
                        //初始化做题设置界面并展示
                        QuestionBankQuestionSettingShow(queryTopicSettingDataBean);
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<QueryTopicSettingBean> call, Throwable t) {
                        Log.e(TAG, "onFail我的错误是+" + t.getMessage());
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                });
    }

    //MyQuestionBankflag       题库标记和取消标记  提交错题
    public void getMyQuestionBankflag(ImageView questionbank_answerpaper_sign,String tf_collection,String tf_marked,String tf_wrong) {
        if (mControlMainActivity.mStuId.equals("")){
            if (tf_collection.equals("1")){
                Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_collection.equals("2")){
                Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_marked.equals("1")){
                Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_marked.equals("2")){
                Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_wrong.equals("2")){
                Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (mCurrentIndex < 0 ){
            if (tf_collection.equals("1")){
                Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_collection.equals("2")){
                Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_marked.equals("1")){
                Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_marked.equals("2")){
                Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_wrong.equals("2")){
                Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Integer question_id = null;
        //字符串分割
        final int[] questionSum = {0};
        if (mMyQuestionBankExercisesBean != null) {
            if (mMyQuestionBankExercisesBean.danxuantiQuestion != null && question_id == null) {
                questionSum[0] = questionSum[0] + mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                if (mCurrentIndex < questionSum[0]) {
                    question_id = mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex).question_id;
                }
            }
            if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null && question_id == null) {
                questionSum[0] = questionSum[0] + mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                if (mCurrentIndex < questionSum[0]) {
                    int count = 0;
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                        question_id = mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count).question_id;
                    }
                }
            }
            if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null && question_id == null) {
                questionSum[0] = questionSum[0] + mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                if (mCurrentIndex < questionSum[0]) {
                    int count = mCurrentIndex;
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                        count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                    }
                    if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                        question_id = mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count).question_id;
                    }
                }
            }
            if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null && question_id == null) {
                questionSum[0] = questionSum[0] + mMyQuestionBankExercisesBean.cailiaotiQuestion.size();
                if (mCurrentIndex < questionSum[0]) {
                    int count = mCurrentIndex;
                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                    }
                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                        count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                    }
                    if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                        count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                    }
                    if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                        question_id = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count).question_id;
                    }
                }
            }
            if (mCurrentIndex >= questionSum[0]) { //不在数组范围直接返回
                if (tf_collection.equals("1")) {
                    Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_collection.equals("2")) {
                    Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_marked.equals("1")) {
                    Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_marked.equals("2")) {
                    Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_wrong.equals("2")){
                    Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                return;
            }
        } else if (mMyTestPageIssueDataBeans != null){
            questionSum[0] = mMyTestPageIssueDataBeans.size();
            if (mCurrentIndex >= questionSum[0]) { //不在数组范围直接返回
                if (tf_collection.equals("1")) {
                    Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_collection.equals("2")) {
                    Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_marked.equals("1")) {
                    Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_marked.equals("2")) {
                    Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_wrong.equals("2")){
                    Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                return;
            }
            question_id = mMyTestPageIssueDataBeans.get(mCurrentIndex).question_id;
        } else if (mMyFavoriteQuestionDataBeans != null){
            questionSum[0] = mMyFavoriteQuestionDataBeans.size();
            if (mCurrentIndex >= questionSum[0]) { //不在数组范围直接返回
                if (tf_collection.equals("1")) {
                    Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_collection.equals("2")) {
                    Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_marked.equals("1")) {
                    Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_marked.equals("2")) {
                    Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                } else if (tf_wrong.equals("2")){
                    Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                return;
            }
            question_id = mMyFavoriteQuestionDataBeans.get(mCurrentIndex).question_id;
        }
        if (question_id == null){
            if (tf_collection.equals("1")){
                Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_collection.equals("2")){
                Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_marked.equals("1")){
                Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_marked.equals("2")){
                Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
            } else if (tf_wrong.equals("2")){
                Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
            }
            LoadingDialog.getInstance(mControlMainActivity).dismiss();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("question_id", String.valueOf(question_id));
        if (!tf_collection.equals("")) {
            paramsMap.put("tf_collection", tf_collection);
        }
        paramsMap.put("stu_id", mControlMainActivity.mStuId);
        if (!tf_marked.equals("")) {
            paramsMap.put("tf_marked", tf_marked);
        }
        if (!tf_wrong.equals("")) {
            paramsMap.put("tf_wrong", tf_wrong);
        }
        String wrong_answer = "";
        if (mMyQuestionBankExercisesAnswerMap != null){
            AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(question_id);
            if (answerInfo != null){
                if (answerInfo.answer != null){
                    wrong_answer = answerInfo.answer;
                }
            }
        }
        if (!wrong_answer.equals("")){
            paramsMap.put("wrong_answer", wrong_answer);
        }
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankflag(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null) {
                            if (tf_collection.equals("1")){
                                Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_collection.equals("2")){
                                Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_marked.equals("1")){
                                Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_marked.equals("2")){
                                Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_wrong.equals("2")){
                                Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            if (tf_collection.equals("1")){
                                Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_collection.equals("2")){
                                Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_marked.equals("1")){
                                Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_marked.equals("2")){
                                Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_wrong.equals("2")){
                                Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (mCurrentIndex < 0 ){
                            if (tf_collection.equals("1")){
                                Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_collection.equals("2")){
                                Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_marked.equals("1")){
                                Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_marked.equals("2")){
                                Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                            } else if (tf_wrong.equals("2")){
                                Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (tf_collection.equals("1")){
                            if (questionbank_answerpaper_sign != null) {
                                questionbank_answerpaper_sign.setBackground(mModelQuestionBankView.getResources().getDrawable(R.drawable.button_collect_enable));
                            }
                            mIsCollect = true;
                            Toast.makeText(mControlMainActivity, "收藏试题成功", Toast.LENGTH_SHORT).show();
                        } else if (tf_collection.equals("2")){
                            if (questionbank_answerpaper_sign != null) {
                                questionbank_answerpaper_sign.setBackground(mModelQuestionBankView.getResources().getDrawable(R.drawable.button_collect_disable_black));
                            }
                            mIsCollect = false;
                            Toast.makeText(mControlMainActivity, "取消收藏试题成功", Toast.LENGTH_SHORT).show();
                        } else if (tf_marked.equals("1")){
                            if (questionbank_answerpaper_sign != null) {
                                questionbank_answerpaper_sign.setBackground(mModelQuestionBankView.getResources().getDrawable(R.drawable.button_questionbank_sign_blue));
                            }
                            mIsSign = true;
                            Toast.makeText(mControlMainActivity, "标记试题成功", Toast.LENGTH_SHORT).show();
                        } else if (tf_marked.equals("2")){
                            if (questionbank_answerpaper_sign != null) {
                                questionbank_answerpaper_sign.setBackground(mModelQuestionBankView.getResources().getDrawable(R.drawable.button_questionbank_sign));
                            }
                            mIsSign = false;
                            Toast.makeText(mControlMainActivity, "取消标记试题成功", Toast.LENGTH_SHORT).show();
                        } else if (tf_wrong.equals("2")){
                            TextView questionbank_wrongquestion_questioncount = mModelQuestionBankWrongQuestionView.findViewById(R.id.questionbank_wrongquestion_questioncount);
                            if (questionbank_wrongquestion_questioncount.getText().toString().equals("" + mMyFavoriteQuestionDataBeans.size())) {
//                                Toast.makeText(mControlMainActivity, "此题已经是最后一道题啦", Toast.LENGTH_SHORT).show();
                                mMyFavoriteQuestionDataBeans.remove(mCurrentIndex);
                                mCurrentIndex = mCurrentIndex - 1;
                                WrongQuestionViewAdd(questionbank_answerpaper_sign);
                            } else { //跳到下一道题
                                mMyFavoriteQuestionDataBeans.remove(mCurrentIndex);
//                                mCurrentIndex = mCurrentIndex + 1;
                                WrongQuestionViewAdd(questionbank_answerpaper_sign);
                            }
                            Toast.makeText(mControlMainActivity, "提交错题成功", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        //字符串分割
                        if (mMyQuestionBankExercisesBean != null) {
                            if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                if (mCurrentIndex < mMyQuestionBankExercisesBean.danxuantiQuestion.size()) {
                                    if (tf_collection.equals("1") || tf_collection.equals("2")) {
                                        mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex).tf_collection = Integer.valueOf(tf_collection);
                                    } else if (tf_marked.equals("1") || tf_marked.equals("2")) {
                                        mMyQuestionBankExercisesBean.danxuantiQuestion.get(mCurrentIndex).tf_marked = Integer.valueOf(tf_marked);
                                    }
                                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                    return;
                                }
                            }
                            if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                if (mCurrentIndex < questionSum[0]) {
                                    int count = 0;
                                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                    }
                                    if (count < mMyQuestionBankExercisesBean.duoxuantiQuestion.size()) {
                                        if (tf_collection.equals("1") || tf_collection.equals("2")) {
                                            mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count).tf_collection = Integer.valueOf(tf_collection);
                                        } else if (tf_marked.equals("1") || tf_marked.equals("2")) {
                                            mMyQuestionBankExercisesBean.duoxuantiQuestion.get(count).tf_marked = Integer.valueOf(tf_marked);
                                        }
                                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                        return;
                                    }
                                }
                            }
                            if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                                if (mCurrentIndex < questionSum[0]) {
                                    int count = mCurrentIndex;
                                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                    }
                                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                        count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                                    }
                                    if (count < mMyQuestionBankExercisesBean.jinadatitiQuestion.size()) {
                                        if (tf_collection.equals("1") || tf_collection.equals("2")) {
                                            mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count).tf_collection = Integer.valueOf(tf_collection);
                                        } else if (tf_marked.equals("1") || tf_marked.equals("2")) {
                                            mMyQuestionBankExercisesBean.jinadatitiQuestion.get(count).tf_marked = Integer.valueOf(tf_marked);
                                        }
                                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                        return;
                                    }
                                }
                            }
                            if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null) {
                                if (mCurrentIndex < questionSum[0]) {
                                    int count = mCurrentIndex;
                                    if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
                                        count = mCurrentIndex - mMyQuestionBankExercisesBean.danxuantiQuestion.size();
                                    }
                                    if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
                                        count = count - mMyQuestionBankExercisesBean.duoxuantiQuestion.size();
                                    }
                                    if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
                                        count = count - mMyQuestionBankExercisesBean.jinadatitiQuestion.size();
                                    }
                                    if (count < mMyQuestionBankExercisesBean.cailiaotiQuestion.size()) {
                                        if (tf_collection.equals("1") || tf_collection.equals("2")) {
                                            mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count).tf_collection = Integer.valueOf(tf_collection);
                                        } else if (tf_marked.equals("1") || tf_marked.equals("2")) {
                                            mMyQuestionBankExercisesBean.cailiaotiQuestion.get(count).tf_marked = Integer.valueOf(tf_marked);
                                        }
                                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                        return;
                                    }
                                }
                            }
                        } else if (mMyTestPageIssueDataBeans != null){
                            if (mCurrentIndex < mMyTestPageIssueDataBeans.size()) {
                                if (tf_collection.equals("1") || tf_collection.equals("2")) {
                                    mMyTestPageIssueDataBeans.get(mCurrentIndex).tf_collection = Integer.valueOf(tf_collection);
                                } else if (tf_marked.equals("1") || tf_marked.equals("2")) {
                                    mMyTestPageIssueDataBeans.get(mCurrentIndex).tf_marked = Integer.valueOf(tf_marked);
                                }
                            }
                        } else if (mMyFavoriteQuestionDataBeans != null){
                            if (mCurrentIndex < mMyFavoriteQuestionDataBeans.size()) {
                                if (tf_collection.equals("1") || tf_collection.equals("2")) {
                                    mMyFavoriteQuestionDataBeans.get(mCurrentIndex).tf_collection = Integer.valueOf(tf_collection);
                                } else if (tf_marked.equals("1") || tf_marked.equals("2")) {
                                    mMyFavoriteQuestionDataBeans.get(mCurrentIndex).tf_marked = Integer.valueOf(tf_marked);
                                }
                            }
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        if (tf_collection.equals("1")){
                            Toast.makeText(mControlMainActivity, "收藏试题失败", Toast.LENGTH_SHORT).show();
                        } else if (tf_collection.equals("2")){
                            Toast.makeText(mControlMainActivity, "取消收藏试题失败", Toast.LENGTH_SHORT).show();
                        } else if (tf_marked.equals("1")){
                            Toast.makeText(mControlMainActivity, "标记试题失败", Toast.LENGTH_SHORT).show();
                        } else if (tf_marked.equals("2")){
                            Toast.makeText(mControlMainActivity, "取消标记试题失败", Toast.LENGTH_SHORT).show();
                        } else if (tf_wrong.equals("2")){
                            Toast.makeText(mControlMainActivity, "提交错题失败", Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库 交卷接口
    public void getQuestionBankHandInBean(int state,double used_answer_time) {
        if (mAnswer_Id == null ||mControlMainActivity.mStuId.equals("") || mMyQuestionBankExercisesBean == null){
            if (state == 1){
                Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        String error_num = "";
        if (mMyQuestionBankExercisesBean.danxuantiQuestion != null) {
            for (int num = 0; num < mMyQuestionBankExercisesBean.danxuantiQuestion.size(); num++) {
                if (mMyQuestionBankExercisesBean.danxuantiQuestion.get(num) == null) {
                    continue;
                }
                if (mMyQuestionBankExercisesBean.danxuantiQuestion.get(num).question_id == null) {
                    continue;
                }
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(mMyQuestionBankExercisesBean.danxuantiQuestion.get(num).question_id);
                if (answerInfo != null) {
                    String result = answerInfo.result + "#";
                    if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                        result = result + " ";
                    }
                    error_num = error_num + "#" + mMyQuestionBankExercisesBean.danxuantiQuestion.get(num).question_id + "#" + result + answerInfo.answer + ";";
                } else {
                    error_num = error_num + "#" + mMyQuestionBankExercisesBean.danxuantiQuestion.get(num).question_id + "#错# ;";
                }
            }
        }
        if (mMyQuestionBankExercisesBean.duoxuantiQuestion != null) {
            for (int num = 0; num < mMyQuestionBankExercisesBean.duoxuantiQuestion.size(); num++) {
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion.get(num) == null) {
                    continue;
                }
                if (mMyQuestionBankExercisesBean.duoxuantiQuestion.get(num).question_id == null) {
                    continue;
                }
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(mMyQuestionBankExercisesBean.duoxuantiQuestion.get(num).question_id);
                if (answerInfo != null) {
                    String result = answerInfo.result + "#";
                    if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                        result = result + " ";
                    }
                    error_num = error_num + "#" + mMyQuestionBankExercisesBean.duoxuantiQuestion.get(num).question_id + "#" + result + answerInfo.answer + ";";
                } else {
                    error_num = error_num + "#" + mMyQuestionBankExercisesBean.duoxuantiQuestion.get(num).question_id + "#错# ;";
                }
            }
        }
        if (mMyQuestionBankExercisesBean.jinadatitiQuestion != null) {
            for (int num = 0; num < mMyQuestionBankExercisesBean.jinadatitiQuestion.size(); num++) {
                if (mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num) == null) {
                    continue;
                }
                if (mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num).question_id == null) {
                    continue;
                }
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num).question_id);
                if (answerInfo != null) {
                    String result = answerInfo.result + "#";
                    if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                        result = result + " ";
                    }
                    error_num = error_num + "#" + mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num).question_id + "#" + result + answerInfo.answer + ";";
                } else {
                    error_num = error_num + "#" + mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num).question_id + "#错# ;";
                }
            }
        }
        if (mMyQuestionBankExercisesBean.cailiaotiQuestion != null) {
            for (int num = 0; num < mMyQuestionBankExercisesBean.cailiaotiQuestion.size(); num ++) {
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num) == null) {
                    continue;
                }
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id == null) {
                    continue;
                }
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id_group1 == null) {
                    mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id_group1 = mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_sub_group;
                }
                if (mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id_group1 != null) {
                    boolean isWrong = true;
                    for (MyQuestionBankExercises.MyQuestionBankExercisesDataBean myQuestionBankExercisesDataBean :
                            mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id_group1) {
                        if (myQuestionBankExercisesDataBean == null) {
                            continue;
                        }
                        if (myQuestionBankExercisesDataBean.question_id == null) {
                            continue;
                        }
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myQuestionBankExercisesDataBean.question_id);
                        if (answerInfo != null) {
                            String result = answerInfo.result + "#";
                            if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                                result = result + " ";
                            }
                            if (answerInfo.result.equals("错")){
                                isWrong = false;
                            }
                            error_num = error_num + "#" + myQuestionBankExercisesDataBean.question_id + "#" + result + answerInfo.answer + ";";
                        } else {
                            isWrong = false;
                            error_num = error_num + "#" + myQuestionBankExercisesDataBean.question_id + "#错# ;";
                        }
                    }
                    if (error_num.substring(error_num.length() - 1).equals(";")) {
                        error_num = error_num.substring(0, error_num.length() - 1) + ";";
                    }
                    //只要有一道题错，此材料题就算错误
                    if (!isWrong) {
                        error_num = error_num + "#" + mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id + "#对# ;";
                    } else {
                        error_num = error_num + "#" + mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id + "#错# ;";
                    }
                } else {
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(mMyQuestionBankExercisesBean.cailiaotiQuestion.get(num).question_id);
                    if (answerInfo != null) {
                        String result = answerInfo.result + "#";
                        if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                            result = result + " ";
                        }
                        error_num = error_num + "#" + mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num).question_id + "#" + result + answerInfo.answer + ";";
                    } else {
                        error_num = error_num + "#" + mMyQuestionBankExercisesBean.jinadatitiQuestion.get(num).question_id + "#错# ;";
                    }
                }
            }
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        if (used_answer_time < 0){
            used_answer_time = 0;
        }
        String strEntity = "{" +
                " \"answer_id\": " + mAnswer_Id +
                ",\"score\": 0" +
                ",\"error_num\":\"" + error_num +
                "\",\"state\":" + state +   //1已完成,2正在答题
                ",\"used_answer_time\":" + used_answer_time +
                ",\"stu_id\":" + mControlMainActivity.mStuId +
                "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankHandIn(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            if (state == 1){
                                Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            if (state == 1){
                                Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }

                        if (state == 1){
                            Toast.makeText(mControlMainActivity,"交卷成功！",Toast.LENGTH_SHORT).show();
                            mTestPageIssueDataScore = "";
                            getQuestionBankAnswerSheet(2);
                        } else {
                            Toast.makeText(mControlMainActivity,"做题记录提交成功！",Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        if (state == 1){
                            Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库-考试 交卷接口
    public void getQuestionBankHandInTestPaperBean(int state,double used_answer_time) {
        if (mAnswer_Id == null ||mControlMainActivity.mStuId.equals("") || mMyTestPageIssueDataBeans == null){
            if (state == 1){
                Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        float score = 0;
        String error_num = "";
        if (mTestPageIssueDataTypeScore == null){
            mTestPageIssueDataTypeScore = "";
        }
        int singleScore = 0;
        int multiScore = 0;
        int shortScore = 0;
        int metScore = 0;
        if (!mTestPageIssueDataTypeScore.equals("")){
            String types[] = mTestPageIssueDataTypeScore.split("EDU;");
            if (types != null){
                for (String type:
                        types) {
                    String scores[] = type.split("#");
                    if (scores.length == 3){
                        if (scores[1].equals("单选") && !scores[2].equals("")){
                            singleScore = Integer.valueOf(scores[2]);
                        } else if (scores[1].equals("多选") && !scores[2].equals("")){
                            multiScore = Integer.valueOf(scores[2]);
                        } else if (scores[1].equals("简答题") && !scores[2].equals("")){
                            shortScore = Integer.valueOf(scores[2]);
                        } else if (scores[1].equals("材料题") && !scores[2].equals("")){
                            metScore = Integer.valueOf(scores[2]);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < mMyTestPageIssueDataBeans.size(); i ++){
            MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean = mMyTestPageIssueDataBeans.get(i);
            if (myTestPageIssueDataBean == null){
                continue;
            }
            if (myTestPageIssueDataBean.question_id == null || myTestPageIssueDataBean.question_type == null) {
                continue;
            }
            if (myTestPageIssueDataBean.question_type == 7){
                if (myTestPageIssueDataBean.question_id_group1 == null){
                    AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean.question_id);
                    if (answerInfo != null) {
                        String result = answerInfo.result + "#";
                        if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                            result = result + " ";
                        }
                        error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#" + result + answerInfo.answer + ";";
                    } else {
                        error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#错# ;";
                    }
                } else {
                    boolean isWrong = true;
                    for (MyTestPageIssueBean.MyTestPageIssueDataBean myTestPageIssueDataBean1 :
                            myTestPageIssueDataBean.question_id_group1) {
                        if (myTestPageIssueDataBean1 == null) {
                            continue;
                        }
                        if (myTestPageIssueDataBean1.question_id == null) {
                            continue;
                        }
                        AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean1.question_id);
                        if (answerInfo != null) {
                            String result = answerInfo.result + "#";
                            if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                                result = result + " ";
                            }
                            if (answerInfo.result.equals("错")){
                                isWrong = false;
                            }
                            error_num = error_num + "#" + myTestPageIssueDataBean1.question_id + "#" + result + answerInfo.answer + ";";
                        } else {
                            error_num = error_num + "#" + myTestPageIssueDataBean1.question_id + "#错# ;";
                            isWrong = false;
                        }
                    }
                    if (error_num.substring(error_num.length() - 1).equals(";")) {
                        error_num = error_num.substring(0, error_num.length() - 1) + ";";
                    }
                    //如果材料题子题有一道为错，那么此题为错题
                    if (!isWrong) {
                        score = score + metScore;
                        error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#对# ;";
                    } else {
                        error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#错# ;";
                    }
                }
            } else {
                AnswerInfo answerInfo = mMyQuestionBankExercisesAnswerMap.get(myTestPageIssueDataBean.question_id);
                if (answerInfo != null) {
//                    error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#" + answerInfo.result + "#" + answerInfo.answer + ";";
                    String result = answerInfo.result + "#";
                    if (answerInfo.result.equals("错") && answerInfo.answer.equals("")){
                        result = result + " ";
                    }
                    if (answerInfo.result.equals("对")){
                        if (myTestPageIssueDataBean.question_type == 1){
                            score = score + singleScore;
                        } else if (myTestPageIssueDataBean.question_type == 2){
                            score = score + multiScore;
                        } else if (myTestPageIssueDataBean.question_type == 4){
                            score = score + shortScore;
                        }
                    }
                    error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#" + result + answerInfo.answer + ";";
                } else {
                    error_num = error_num + "#" + myTestPageIssueDataBean.question_id + "#错# ;";
                }
            }
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        used_answer_time = mMyTestPageIssueTime - used_answer_time;
        if (used_answer_time > mMyTestPageIssueTime){ //判断已使用的时间大于试卷时间时，已使用的时间等于试卷时间
            used_answer_time = mMyTestPageIssueTime;
        }
        String strEntity = "{" +
                " \"answer_id\": " + mAnswer_Id +
                ",\"score\": " + score +
                ",\"error_num\":\"" + error_num +
                "\",\"state\":" + state +   //1已完成,2正在答题
                ",\"used_answer_time\":" + used_answer_time +
                ",\"stu_id\":" + mControlMainActivity.mStuId +
                "}";
        mTestPageIssueDataScore = String.valueOf(score);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankHandIn(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            if (state == 1){
                                Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            if (state == 1){
                                Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }

                        if (state == 1){
                            Toast.makeText(mControlMainActivity,"交卷成功！",Toast.LENGTH_SHORT).show();
                            getQuestionBankAnswerSheet(2);
                        } else {
                            Toast.makeText(mControlMainActivity,"做题记录提交成功！",Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        if (state == 1){
                            Toast.makeText(mControlMainActivity,"交卷失败！",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mControlMainActivity,"做题记录提交失败！",Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库-出题   是不是练习模式Bean类
    public static class MyQuestionBankExercises {

        private Integer code;
        private String answer_id;
        private String msg;
        private MyQuestionBankExercisesBean data;
        private Integer used_answer_time;

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setData(MyQuestionBankExercisesBean data) {
            this.data = data;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public MyQuestionBankExercisesBean getData() {
            return data;
        }

        public String getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_id(String answer_id) {
            this.answer_id = answer_id;
        }

        class  MyQuestionBankExercisesBean {
            private List<MyQuestionBankExercisesDataBean> cailiaotiQuestion;
            private List<MyQuestionBankExercisesDataBean> jinadatitiQuestion;
            private List<MyQuestionBankExercisesDataBean> duoxuantiQuestion;
            private List<MyQuestionBankExercisesDataBean> danxuantiQuestion;

            public List<MyQuestionBankExercisesDataBean> getCailiaotiQuestion() {
                return cailiaotiQuestion;
            }

            public List<MyQuestionBankExercisesDataBean> getDanxuantiQuestion() {
                return danxuantiQuestion;
            }

            public List<MyQuestionBankExercisesDataBean> getDuoxuantiQuestion() {
                return duoxuantiQuestion;
            }

            public List<MyQuestionBankExercisesDataBean> getJinadatitiQuestion() {
                return jinadatitiQuestion;
            }

            public void setCailiaotiQuestion(List<MyQuestionBankExercisesDataBean> cailiaotiQuestion) {
                this.cailiaotiQuestion = cailiaotiQuestion;
            }

            public void setDanxuantiQuestion(List<MyQuestionBankExercisesDataBean> danxuantiQuestion) {
                this.danxuantiQuestion = danxuantiQuestion;
            }

            public void setDuoxuantiQuestion(List<MyQuestionBankExercisesDataBean> duoxuantiQuestion) {
                this.duoxuantiQuestion = duoxuantiQuestion;
            }

            public void setJinadatitiQuestion(List<MyQuestionBankExercisesDataBean> jinadatitiQuestion) {
                this.jinadatitiQuestion = jinadatitiQuestion;
            }
        }
        static class MyQuestionBankExercisesDataBean {
            private Integer difficulty;
            private Integer tf_collection;
            private Integer tf_marked;
            private String optionanswer;
            private String question_analysis;
            private String video_analysis;
            private Integer question_type;
            private String question_name;
            private String audio_analysis;
            private Integer question_id;
            private String question_sub_id;
            private List<MyQuestionBankExercisesDataBean> question_sub_group;
            private List<MyQuestionBankExercisesDataBean> question_id_group1;
            private String answer;
            private String myAnswer;

            public Integer getTf_marked() {
                return tf_marked;
            }

            public void setTf_marked(Integer tf_marked) {
                this.tf_marked = tf_marked;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public String getMyAnswer() {
                return answer;
            }

            public void setMyAnswer(String answer) {
                this.answer = answer;
            }

            public List<MyQuestionBankExercisesDataBean> getQuestion_sub_group() {
                return question_sub_group;
            }

            public void setQuestion_sub_group(List<MyQuestionBankExercisesDataBean> question_sub_group) {
                this.question_sub_group = question_sub_group;
            }

            public Integer getDifficulty() {
                return difficulty;
            }

            public Integer getQuestion_id() {
                return question_id;
            }

            public Integer getQuestion_type() {
                return question_type;
            }

            public Integer getTf_collection() {
                return tf_collection;
            }

            public String getAudio_analysis() {
                return audio_analysis;
            }

            public String getOptionanswer() {
                return optionanswer;
            }

            public String getQuestion_analysis() {
                return question_analysis;
            }

            public String getQuestion_name() {
                return question_name;
            }

            public String getQuestion_sub_id() {
                return question_sub_id;
            }

            public String getVideo_analysis() {
                return video_analysis;
            }

            public void setAudio_analysis(String audio_analysis) {
                this.audio_analysis = audio_analysis;
            }

            public void setDifficulty(Integer difficulty) {
                this.difficulty = difficulty;
            }

            public void setOptionanswer(String optionanswer) {
                this.optionanswer = optionanswer;
            }

            public void setQuestion_analysis(String question_analysis) {
                this.question_analysis = question_analysis;
            }

            public void setQuestion_id(Integer question_id) {
                this.question_id = question_id;
            }

            public void setQuestion_name(String question_name) {
                this.question_name = question_name;
            }

            public void setQuestion_sub_id(String question_sub_id) {
                this.question_sub_id = question_sub_id;
            }

            public void setQuestion_type(Integer question_type) {
                this.question_type = question_type;
            }

            public void setTf_collection(Integer tf_collection) {
                this.tf_collection = tf_collection;
            }

            public void setVideo_analysis(String video_analysis) {
                this.video_analysis = video_analysis;
            }
        }
    }

    public class MyTestPageIssueBean {
        private Integer answer_time;
        private String msg;
        private Integer code;
        private Integer used_answer_time;
        private Integer total_score;
        private Integer score;
        private String question_type_score;
        private Integer tf_temporary;
        private String question_id_group;
        private String answer_id;
        private List<MyTestPageIssueDataBean> data;

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public void setData(List<MyTestPageIssueDataBean> data) {
            this.data = data;
        }

        public void setTotal_score(Integer total_score) {
            this.total_score = total_score;
        }

        public void setAnswer_time(Integer answer_time) {
            this.answer_time = answer_time;
        }

        public Integer getAnswer_time() {
            return answer_time;
        }

        public void setAnswer_id(String answer_id) {
            this.answer_id = answer_id;
        }

        public String getAnswer_id() {
            return answer_id;
        }

        public Integer getTf_temporary() {
            return tf_temporary;
        }

        public Integer getTotal_score() {
            return total_score;
        }

        public Integer getUsed_answer_time() {
            return used_answer_time;
        }

        public List<MyTestPageIssueDataBean> getData() {
            return data;
        }

        public String getQuestion_id_group() {
            return question_id_group;
        }

        public String getQuestion_type_score() {
            return question_type_score;
        }

        public void setQuestion_id_group(String question_id_group) {
            this.question_id_group = question_id_group;
        }

        public void setQuestion_type_score(String question_type_score) {
            this.question_type_score = question_type_score;
        }

        public void setTf_temporary(Integer tf_temporary) {
            this.tf_temporary = tf_temporary;
        }

        public void setUsed_answer_time(Integer used_answer_time) {
            this.used_answer_time = used_answer_time;
        }

        class MyTestPageIssueDataBean {
            private Integer difficulty;
            private Integer tf_collection;
            private Integer tf_marked;
            private String optionanswer;
            private String question_analysis;
            private Integer question_type;
            private String video_analysis;
            private String question_name;
            private String question_sub_id;
            private Integer question_id;
            private String audio_analysis;
            private List<MyTestPageIssueDataBean> question_id_group1;
            private String answer;
            private String myAnswer;

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public String getAnswer() {
                return answer;
            }

            public Integer getTf_marked() {
                return tf_marked;
            }

            public void setTf_marked(Integer tf_marked) {
                this.tf_marked = tf_marked;
            }

            public void setQuestion_id(Integer question_id) {
                this.question_id = question_id;
            }

            public void setVideo_analysis(String video_analysis) {
                this.video_analysis = video_analysis;
            }

            public void setTf_collection(Integer tf_collection) {
                this.tf_collection = tf_collection;
            }

            public void setQuestion_type(Integer question_type) {
                this.question_type = question_type;
            }

            public void setQuestion_sub_id(String question_sub_id) {
                this.question_sub_id = question_sub_id;
            }

            public void setQuestion_name(String question_name) {
                this.question_name = question_name;
            }

            public void setQuestion_analysis(String question_analysis) {
                this.question_analysis = question_analysis;
            }

            public void setOptionanswer(String optionanswer) {
                this.optionanswer = optionanswer;
            }

            public void setDifficulty(Integer difficulty) {
                this.difficulty = difficulty;
            }

            public void setAudio_analysis(String audio_analysis) {
                this.audio_analysis = audio_analysis;
            }

            public String getVideo_analysis() {
                return video_analysis;
            }

            public String getQuestion_sub_id() {
                return question_sub_id;
            }

            public String getQuestion_name() {
                return question_name;
            }

            public String getQuestion_analysis() {
                return question_analysis;
            }

            public String getOptionanswer() {
                return optionanswer;
            }

            public String getAudio_analysis() {
                return audio_analysis;
            }

            public Integer getTf_collection() {
                return tf_collection;
            }

            public Integer getQuestion_type() {
                return question_type;
            }

            public Integer getQuestion_id() {
                return question_id;
            }

            public Integer getDifficulty() {
                return difficulty;
            }

            public List<MyTestPageIssueDataBean> getQuestion_id_group1() {
                return question_id_group1;
            }

            public void setQuestion_id_group1(List<MyTestPageIssueDataBean> question_id_group1) {
                this.question_id_group1 = question_id_group1;
            }
        }
    }

    //题库-出题   练习模式
    public void getqueryMyQuestionBankIssue() {
        if (mControlMainActivity.mStuId.equals("") || mChapter_test_point_id == null){
            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String question_type_group = "1;2;7;4;";
        if (!mSingleChoiceState.equals("select")){
            question_type_group = question_type_group.replace("1;","");
        }
        if (!mMultiChoiceState.equals("select")){
            question_type_group = question_type_group.replace("2;","");
        }
        if (!mShortAnswerState.equals("select")){
            question_type_group = question_type_group.replace("4;","");
        }
        if (!mMaterialQuestionState.equals("select")){
            question_type_group = question_type_group.replace("7;","");
        }
        String classification = "";
        if (mQuestionType.equals("AllQuestion")){
            classification = "1";
        } else if (mQuestionType.equals("NotDoneQuestion")){
            classification = "2";
        } else if (mQuestionType.equals("WrongQuestion")){
            classification = "3";
        }
        String questionNum = "";
        if (mQuestionCount.equals("TenQuestion")){
            questionNum = "10";
        } else if (mQuestionCount.equals("TwentyQuestion")){
            questionNum = "20";
        } else if (mQuestionCount.equals("HundredQuestion")){
            questionNum = "100";
        }
        String strEntity = "{" +
                " \"stu_id\": " + mControlMainActivity.mStuId +
                ",\"chapter_test_point_id\": " + mChapter_test_point_id +
                ",\"question_type_group\":\"" + question_type_group +
                "\",\"classification\":" + classification +
                ",\"questionNum\":" + questionNum +
                ",\"tf_Practice\":1" +
                "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyQuestionBankIssue(body)
                .enqueue(new Callback<MyQuestionBankExercises>() {

                    @Override
                    public void onResponse(Call<MyQuestionBankExercises> call, Response<MyQuestionBankExercises> response) {
                        MyQuestionBankExercises myQuestionBankExercises = response.body();
                        if (myQuestionBankExercises == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myQuestionBankExercises.code;
                        if (!HeaderInterceptor.IsErrorCode(myQuestionBankExercises.code,myQuestionBankExercises.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (code != 200){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.answer_id == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.answer_id.equals("")){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mAnswer_Id = Integer.valueOf(myQuestionBankExercises.answer_id);
                        mMyQuestionBankExercisesBean = myQuestionBankExercises.data;
                        if (mMyQuestionBankExercisesBean == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mTime = 0;
                        QuestionBankDetailsQuestionModeTestShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankExercises> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库-出题   考试模式
    public void getqueryMyQuestionBankExamIssue() {
        if (mControlMainActivity.mStuId.equals("") || mChapter_test_point_id == null){
            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String question_type_group = "1;2;7;4;";
        if (!mSingleChoiceState.equals("select")){
            question_type_group = question_type_group.replace("1;","");
        }
        if (!mMultiChoiceState.equals("select")){
            question_type_group = question_type_group.replace("2;","");
        }
        if (!mShortAnswerState.equals("select")){
            question_type_group = question_type_group.replace("4;","");
        }
        if (!mMaterialQuestionState.equals("select")){
            question_type_group = question_type_group.replace("7;","");
        }
        String classification = "";
        if (mQuestionType.equals("AllQuestion")){
            classification = "1";
        } else if (mQuestionType.equals("NotDoneQuestion")){
            classification = "2";
        } else if (mQuestionType.equals("WrongQuestion")){
            classification = "3";
        }
        String questionNum = "";
        if (mQuestionCount.equals("TenQuestion")){
            questionNum = "10";
        } else if (mQuestionCount.equals("TwentyQuestion")){
            questionNum = "20";
        } else if (mQuestionCount.equals("HundredQuestion")){
            questionNum = "100";
        }
        String strEntity = "{" +
                " \"stu_id\": " + mControlMainActivity.mStuId +
                ",\"chapter_test_point_id\": " + mChapter_test_point_id +
                ",\"question_type_group\":\"" + question_type_group +
                "\",\"classification\":" + classification +
                ",\"questionNum\":" + questionNum +
                ",\"tf_Practice\":2" +
                "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyQuestionBankIssue(body)
                .enqueue(new Callback<MyQuestionBankExercises>() {

                    @Override
                    public void onResponse(Call<MyQuestionBankExercises> call, Response<MyQuestionBankExercises> response) {
                        MyQuestionBankExercises myQuestionBankExercises = response.body();
                        if (myQuestionBankExercises == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myQuestionBankExercises.code,myQuestionBankExercises.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myQuestionBankExercises.code;
                        if (code != 200){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.answer_id == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.answer_id.equals("")){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mAnswer_Id = Integer.valueOf(myQuestionBankExercises.answer_id);
                        mMyQuestionBankExercisesBean = myQuestionBankExercises.data;
                        if (mMyQuestionBankExercisesBean == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mTime = 0;
                        QuestionBankDetailsQuestionModeExamShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankExercises> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库-试卷出题
    public void getQueryTestPageIssue(String test_paper_name,Integer test_paper_id) {
        if (mControlMainActivity.mStuId.equals("") || mIbs_id.equals("")){
            Toast.makeText(mControlMainActivity, "出题失败", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{" +
                " \"stu_id\": " + mControlMainActivity.mStuId +
                ",\"test_paper_id\": " + test_paper_id +
                "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryTestPageIssue(body)
                .enqueue(new Callback<MyTestPageIssueBean>() {

                    @Override
                    public void onResponse(Call<MyTestPageIssueBean> call, Response<MyTestPageIssueBean> response) {
                        MyTestPageIssueBean myTestPageIssueBean = response.body();
                        if (myTestPageIssueBean == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myTestPageIssueBean.code,myTestPageIssueBean.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myTestPageIssueBean.code;
                        if (code != 200){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myTestPageIssueBean.answer_id == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myTestPageIssueBean.answer_id.equals("")){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mAnswer_Id = Integer.valueOf(myTestPageIssueBean.answer_id);
                        mMyTestPageIssueDataBeans = myTestPageIssueBean.data;
                        mTestPageIssueDataTypeScore = myTestPageIssueBean.question_type_score;
                        if (mMyTestPageIssueDataBeans == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyQuestionBankExercisesBean = null;
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mMyTestPageIssueTime = myTestPageIssueBean.answer_time * 60;
                        mTime = myTestPageIssueBean.answer_time * 60;
                        mCurrentChapterName = test_paper_name;
                        QuestionBankDetailsQuestionModeTestPaperShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyTestPageIssueBean> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库-快速出题
    public void getqueryMyQuestionBankQuickIssue() {
        if (mControlMainActivity.mStuId.equals("") || mIbs_id.equals("")){
            Toast.makeText(mControlMainActivity, "出题失败", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{" +
                " \"stu_id\": " + mControlMainActivity.mStuId +
                ",\"ibs_id\": " + mIbs_id +
                "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryQuickTask(body)
                .enqueue(new Callback<MyQuestionBankExercises>() {

                    @Override
                    public void onResponse(Call<MyQuestionBankExercises> call, Response<MyQuestionBankExercises> response) {
                        MyQuestionBankExercises myQuestionBankExercises = response.body();
                        if (myQuestionBankExercises == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myQuestionBankExercises.code,myQuestionBankExercises.msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        int code = myQuestionBankExercises.code;
                        if (code != 200){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.answer_id == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (myQuestionBankExercises.answer_id.equals("")){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mAnswer_Id = Integer.valueOf(myQuestionBankExercises.answer_id);
                        mMyQuestionBankExercisesBean = myQuestionBankExercises.data;
                        if (mMyQuestionBankExercisesBean == null){
                            Toast.makeText(mControlMainActivity,"出题失败！",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        mMyQuestionBankExercisesAnswerMap.clear();
                        mMyTestPageIssueDataBeans = null;
                        mTestPageIssueDataTypeScore = "";
                        mMyFavoriteQuestionDataBeans = null;
                        questionBankAnswerRecordDataBeanLists = null;
                        mCurrentIndex = 0;
                        mTime = 0;
                        mCurrentChapterName = "快速做题";
                        QuestionBankDetailsQuestionModeExamShow();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankExercises> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }
                });
    }

    //题库----查看试卷
    public void getQuestionBankTestPaper() {
        if (mIbs_id.equals("")){
            Toast.makeText(mControlMainActivity,"查询试卷列表失败",Toast.LENGTH_SHORT).show();
            questionbank_sub_details_content = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_content);
            questionbank_sub_details_content.removeAllViews();
            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
            questionbank_sub_details_content.addView(view3);
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        questionbank_sub_details_content = mModelQuestionBankDetailsView.findViewById(R.id.questionbank_sub_details_content);
        questionbank_sub_details_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("ibs_id", Integer.valueOf(mIbs_id));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyQuestionBankTestPaper
        queryMyCourseList.queryMyQuestionBankTestPaper(body)
                .enqueue(new Callback<QuestionBankTestPaperBean>() {
                    @Override
                    public void onResponse(Call<QuestionBankTestPaperBean> call, Response<QuestionBankTestPaperBean> response) {
                        QuestionBankTestPaperBean testPaperBean = response.body();
                        if (testPaperBean == null) {
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
                            questionbank_sub_details_content.addView(view3);
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"查询试卷列表失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(testPaperBean.code,"")){
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
                            questionbank_sub_details_content.addView(view3);
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        if (testPaperBean.code == 214){
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
                            questionbank_sub_details_content.addView(view3);
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"暂无模拟真题",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (testPaperBean.code != 200){
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
                            questionbank_sub_details_content.addView(view3);
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"查询试卷列表失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<QuestionBankTestPaperBean.QuestionBankTestPaperDataBean> questionBankTestPaperDataBeans = testPaperBean.data;
                        if (questionBankTestPaperDataBeans == null){
                            View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
                            questionbank_sub_details_content.addView(view3);
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            Toast.makeText(mControlMainActivity,"查询试卷列表失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < questionBankTestPaperDataBeans.size(); i ++){
                            QuestionBankTestPaperBean.QuestionBankTestPaperDataBean questionBankTestPaperDataBean = questionBankTestPaperDataBeans.get(i);
                            if (questionBankTestPaperDataBean == null){
                                continue;
                            }
                            //模拟真题界面
                            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_sub_detials_simulate, null);
                            //模拟真题界面-name   title标题
                            TextView questionbank_simulated_name = view.findViewById(R.id.questionbank_simulated_name);
                            questionbank_simulated_name.setText(questionBankTestPaperDataBean.test_paper_name);
                            questionbank_simulated_name.setHint(questionBankTestPaperDataBean.test_paper_id + "");
                            //时间长度
                            TextView questionbank_simulated_time = view.findViewById(R.id.questionbank_simulated_time);
                            questionbank_simulated_time.setText(questionBankTestPaperDataBean.answer_time + "分钟");
                            //总分
                            TextView questionbank_simulated_score = view.findViewById(R.id.questionbank_simulated_score);
                            questionbank_simulated_score.setText(questionBankTestPaperDataBean.total_score + "分");
                            //点击去做题
                            TextView questionbank_simulated_go = view.findViewById(R.id.questionbank_simulated_go);
                            questionbank_simulated_go.setClickable(true);
                            questionbank_simulated_go.setOnClickListener(v -> {
//                                if (myQuestionBankGoonDataBeans != null){
//                                    if (myQuestionBankGoonDataBeans.size() != 0) {
//                                        Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                }
                                //开始真题做题
                                getQueryTestPageIssue(questionBankTestPaperDataBean.test_paper_name,questionBankTestPaperDataBean.test_paper_id);
                            });
                            //点击去做题
                            ImageView questionbank_simulated_goimage = view.findViewById(R.id.questionbank_simulated_goimage);
                            questionbank_simulated_goimage.setClickable(true);
                            questionbank_simulated_goimage.setOnClickListener(v -> {
//                                if (myQuestionBankGoonDataBeans != null){
//                                    if (myQuestionBankGoonDataBeans.size() != 0) {
//                                        Toast.makeText(mControlMainActivity, "您有未完成的试卷，请继续答题或提交未完成的试卷！", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                }
                                //开始真题做题
                                getQueryTestPageIssue(questionBankTestPaperDataBean.test_paper_name,questionBankTestPaperDataBean.test_paper_id);
                            });
                            questionbank_sub_details_content.addView(view);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onFailure(Call<QuestionBankTestPaperBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        View view3 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_pointout, null);
                        questionbank_sub_details_content.addView(view3);
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        Toast.makeText(mControlMainActivity,"查询试卷列表失败",Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    //我的题库----题库列表   网络请求
    public void MyQuestionBankBeanList() {
        if (mControlMainActivity.mStuId.equals("")){
            if (mSmart_model_questionbank != null){
                mSmart_model_questionbank.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        mIsMore = false;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));//学生id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyQuestionBankList(body)
                .enqueue(new Callback<MyQuestionBankBean>() {
                    @Override
                    public void onResponse(Call<MyQuestionBankBean> call, Response<MyQuestionBankBean> response) {
                        MyQuestionBankBean bankBean = response.body();
                        if (bankBean == null) {
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            if (mSmart_model_questionbank != null){
                                mSmart_model_questionbank.finishRefresh();
                            }
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(bankBean.getCode(),bankBean.getMsg())){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            if (mSmart_model_questionbank != null){
                                mSmart_model_questionbank.finishRefresh();
                            }
                            return;
                        }
                        if (bankBean.code != 200){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            if (mSmart_model_questionbank != null){
                                mSmart_model_questionbank.finishRefresh();
                            }
                            return;
                        }
                        if (bankBean.data == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            if (mSmart_model_questionbank != null){
                                mSmart_model_questionbank.finishRefresh();
                            }
                            return;
                        }
                        for (int i = 0;i < bankBean.data.size() ; i ++){
                            MyQuestionBankBean.MyQuestionBankDataBean dataBean = bankBean.data.get(i);
                            if (dataBean == null){
                                continue;
                            }
                            //题库界面赋值
                            //题库子条目标签
                            View item_bank_view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1, null);
                            //题库子条目id
                            TextView modelquestionbank_mainquestionbank_id = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_id);
                            modelquestionbank_mainquestionbank_id.setText(dataBean.getItem_bank_id() + "");
                            //题库子条目的标题
                            TextView modelquestionbank_mainquestionbank_name = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_name);
                            modelquestionbank_mainquestionbank_name.setText(dataBean.getItem_bank_name());
                            //题库子条目的描述
                            TextView modelquestionbank_mainquestionbank_describ = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_describ);
                            modelquestionbank_mainquestionbank_describ.setText(dataBean.getBrief_introduction());
                            //题库点击更多
                            LinearLayout modelquestionbank_mainquestionbank_more = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_more);
                            modelquestionbank_mainquestionbank_more.setClickable(true);
                            modelquestionbank_mainquestionbank_more.setOnClickListener(v -> {
                                //题库列表详情   将id name
                                QuestionBankMainMoreShow(null,dataBean);
                            });
                            //子题库的数据
                            List<MyQuestionBankBean.MyQuestionBankSubDataBean> sub_library = dataBean.getSub_library();
                            if (sub_library == null){
                                continue;
                            }
                            GridLayout modelquestionbank_mainquestionbank_list = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_list);
                            modelquestionbank_mainquestionbank_list.removeAllViews();
                            for (int j = 0; j < sub_library.size(); j ++) {
                                MyQuestionBankBean.MyQuestionBankSubDataBean subLibraryBean = sub_library.get(j);
                                if (subLibraryBean == null){
                                    continue;
                                }
                                if (j >= 2){
                                    break;
                                }
                                //子题库的数据
                                View item_bank_view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1_1, null);
                                TextView modelquestionbank_subquestionbank1 = item_bank_view1.findViewById(R.id.modelquestionbank_subquestionbank1);
                                modelquestionbank_subquestionbank1.setHint(subLibraryBean.getIbs_id() + "");
                                modelquestionbank_subquestionbank1.setText(subLibraryBean.getIbs_name());
                                modelquestionbank_mainquestionbank_list.addView(item_bank_view1);
                                modelquestionbank_subquestionbank1.setOnClickListener(v->{
                                    mIbs_id = String.valueOf(subLibraryBean.getIbs_id());
                                    mIbs_name = subLibraryBean.getIbs_name();
                                    //查询此子题库是否有未完成的试题
                                    getMyQuestionBankGoon();
                                });
                            }
                            LinearLayout questionbank_main_content = mModelQuestionBankView.findViewById(R.id.questionbank_main_content);
                            questionbank_main_content.addView(item_bank_view);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        if (mSmart_model_questionbank != null){
                            mSmart_model_questionbank.finishRefresh();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyQuestionBankBean> call, Throwable t) {
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        if (mSmart_model_questionbank != null){
                            mSmart_model_questionbank.finishRefresh();
                        }
                    }
                });
    }

    //首页-----题库列表(包括子题库)
    public void getQuestionBankBeanList() {
        LoadingDialog.getInstance(mControlMainActivity).show();
        mIsMore = false;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{}";
        if (!mControlMainActivity.mStuId.equals("")) {
            strEntity = "{\"stu_id\":" + mControlMainActivity.mStuId + "}";
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryQuestionBankList(body)
                .enqueue(new Callback<QuestionBankBean>() {

                    @Override
                    public void onResponse(Call<QuestionBankBean> call, Response<QuestionBankBean> response) {
                        QuestionBankBean questionBankBean = response.body();
                        if (questionBankBean == null) {
                            if (mSmart_model_questionbank != null) {
                                mSmart_model_questionbank.finishRefresh();
                            }
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            return;
                        }
                        String msg = questionBankBean.getMsg();
                        int code = questionBankBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,msg)){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            if (mSmart_model_questionbank != null) {
                                mSmart_model_questionbank.finishRefresh();
                            }
                            return;
                        }
                        if (code != 200) {
                            if (mSmart_model_questionbank != null) {
                                mSmart_model_questionbank.finishRefresh();
                            }
                            return;
                        }
                        List<QuestionBankBean.DataBean> beanList = questionBankBean.getData();
                        for (int i = 0; i < beanList.size(); i ++) {
                            QuestionBankBean.DataBean dataBean = beanList.get(i);
                            if (dataBean == null){
                                continue;
                            }
                            //题库界面赋值
                            //题库子条目标签
                            View item_bank_view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1, null);
                            //题库id
                            TextView modelquestionbank_mainquestionbank_id = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_id);
                            modelquestionbank_mainquestionbank_id.setText(dataBean.getItem_bank_id() + "");
                            //题库的标题
                            TextView modelquestionbank_mainquestionbank_name = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_name);
                            modelquestionbank_mainquestionbank_name.setText(dataBean.getItem_bank_name());
                            //题库的描述
                            TextView modelquestionbank_mainquestionbank_describ = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_describ);
                            modelquestionbank_mainquestionbank_describ.setText(dataBean.getBrief_introduction());
                            //题库点击更多
                            LinearLayout modelquestionbank_mainquestionbank_more = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_more);
                            modelquestionbank_mainquestionbank_more.setClickable(true);
                            modelquestionbank_mainquestionbank_more.setOnClickListener(v -> {
                                //题库点击更多   将id name
                                QuestionBankMainMoreShow(dataBean,null);
                            });
                            //子题库的数据
                            GridLayout modelquestionbank_mainquestionbank_list = item_bank_view.findViewById(R.id.modelquestionbank_mainquestionbank_list);
                            modelquestionbank_mainquestionbank_list.removeAllViews();
                            //子题库的数据
                            List<QuestionBankBean.DataBean.SubLibraryBean> sub_library = dataBean.getSub_library();
                            for (int j = 0; j < sub_library.size(); j ++) {
                                QuestionBankBean.DataBean.SubLibraryBean subLibraryBean = sub_library.get(j);
                                if (subLibraryBean == null){
                                    continue;
                                }
                                if (j >= 2){
                                    break;
                                }
                                View item_bank_view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.model_questionbank_1_1, null);
                                TextView modelquestionbank_subquestionbank1 = item_bank_view1.findViewById(R.id.modelquestionbank_subquestionbank1);
                                modelquestionbank_subquestionbank1.setHint(subLibraryBean.getIbs_id() + "");
                                modelquestionbank_subquestionbank1.setText(subLibraryBean.getIbs_name());
                                modelquestionbank_mainquestionbank_list.addView(item_bank_view1);
                                //判断题库是否有做题权限，如果没有不可点击颜色变灰
                                if (dataBean.TF == 1) {
                                    modelquestionbank_subquestionbank1.setClickable(true);
                                    modelquestionbank_subquestionbank1.setOnClickListener(v -> {
                                        //传入相关的id和name
                                        mIbs_id = String.valueOf(subLibraryBean.getIbs_id());
                                        mIbs_name = subLibraryBean.getIbs_name();
                                        //查询此子题库是否有未完成的试题
                                        getMyQuestionBankGoon();
                                    });
                                } else if (dataBean.TF == 2) {
                                    modelquestionbank_subquestionbank1.setTextColor(item_bank_view1.getResources().getColor(R.color.black999999));
                                    modelquestionbank_subquestionbank1.setClickable(true);
                                    modelquestionbank_subquestionbank1.setOnClickListener(v -> {
                                        //传入相关的id和name
                                        Toast.makeText(mControlMainActivity, "您没有此题库的做题权限！", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                            LinearLayout questionbank_main_content = mModelQuestionBankView.findViewById(R.id.questionbank_main_content);
                            questionbank_main_content.addView(item_bank_view);
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        if (mSmart_model_questionbank != null) {
                            mSmart_model_questionbank.finishRefresh();
                        }
                    }

                    @Override
                    public void onFailure(Call<QuestionBankBean> call, Throwable t) {
                        Log.e(TAG, "onFail我的错误是+" + t.getMessage());
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        if (mSmart_model_questionbank != null) {
                            mSmart_model_questionbank.finishRefresh();
                        }
                    }

                });
    }
}
