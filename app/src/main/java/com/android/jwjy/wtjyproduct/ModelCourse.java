package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 课程
 */
public class ModelCourse extends Fragment implements ModelCourseCover.ModelCourseCoverOnClickListener {
    private static ControlMainActivity mControlMainActivity;
    private static String mContext = "xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mview;
    private int height = 1344;
    private int width = 720;
    private ModelSearchView searchView = null;

    //下拉刷新
    private SmartRefreshLayout mSmart_fragment_course = null;

    //弹出窗口（筛选条件）
    private PopupWindow popupWindow;
    //一级搜索
    private String mCourseSelectTemp = "-1";
    private String mCourseSelect = "-1";
    //二级搜索
    private String mCourseSelectTemp1 = "-1";
    private String mCourseSelect1 = "-1";
    //排序方式搜索
    private String mCourseSelectSortTemp = "-1";
    private String mCourseSelectSort = "-1";
    //课程类型搜索
    private String mCourseSelectCourseTypeTemp = "-1";
    private String mCourseSelectCourseType = "-1";

    //课程列表分页查询
    private int mCurrentPage = 0;
    private int mPageCount = 10;
    private int mCourseSum = 0; //课程总数

    public static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage) {
        mContext = context;
        mControlMainActivity = content;
        ModelCourse myFragment = new ModelCourse();
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
        CourseMainShow();
        // 3. 绑定组件
        searchView = mview.findViewById(R.id.course_search_view);
        searchView.init("coursesearchrecords");
        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容,,,,,,,,.
        searchView.setOnClickSearch(string -> {
            System.out.println("我收到了" + string); //只按照关键字搜索，其他查询条件全部重置
            mCourseSelectTemp = "-1";
            mCourseSelect = "-1";
            //二级搜索
            mCourseSelectTemp1 = "-1";
            mCourseSelect1 = "-1";
            //排序方式搜索
            mCourseSelectSortTemp = "-1";
            mCourseSelectSort = "-1";
            //课程类型搜索
            mCourseSelectCourseTypeTemp = "-1";
            mCourseSelectCourseType = "-1";
            String project_id = "";
            String subject_id = "";
            String hour = "1";
            String fever = "1";
            String course_type = "";  //全部默认为空
            if (!mCourseSelect.equals("-1")){
                project_id = mCourseSelect;
            }
            if (!mCourseSelect1.equals("-1")){
                subject_id = mCourseSelect1;
            }
            if (mCourseSelectSort.equals("0")){
                hour = "0";
            } else if (mCourseSelectSort.equals("1")){
                fever = "0";
            }
            if (mCourseSelectCourseType.equals("0")){
                course_type = "直播";
            } else if (mCourseSelectCourseType.equals("1")){
                course_type = "录播";
            } else if (mCourseSelectCourseType.equals("2")){
                course_type = "直播,录播";
            }
            if (mview == null) {
                return;
            }
            HideAllLayout();
            RelativeLayout course_mainLayout = mview.findViewById(R.id.course_mainLayout);
            LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_mainLayout.getLayoutParams();
            LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
            LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
            course_mainLayout.setLayoutParams(LP);
            course_mainLayout.setVisibility(View.VISIBLE);
            ScrollView course_block_menu_scroll_view = mview.findViewById(R.id.course_block_menu_scroll_view);
            course_block_menu_scroll_view.scrollTo(0, 0);
            getCourseDatas(string,project_id,subject_id,hour,fever,course_type);
        });
        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(() -> {
            CourseMainShow();
        });
        mSmart_fragment_course = mview.findViewById(R.id.Smart_fragment_course);
        mSmart_fragment_course.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) { //加载更多
                //先判断是否已获取了全部数据
                if (mCourseSum <= mCurrentPage * mPageCount){
                    LinearLayout course_end = mview.findViewById(R.id.course_end);
                    course_end.setVisibility(View.VISIBLE);
                    return;
                }
                String project_id = "";
                String subject_id = "";
                String hour = "1";
                String fever = "1";
                String course_type = "";  //全部默认为空
                if (!mCourseSelect.equals("-1")){
                    project_id = mCourseSelect;
                }
                if (!mCourseSelect1.equals("-1")){
                    subject_id = mCourseSelect1;
                }
                if (mCourseSelectSort.equals("0")){
                    hour = "0";
                } else if (mCourseSelectSort.equals("1")){
                    fever = "0";
                }
                if (mCourseSelectCourseType.equals("0")){
                    course_type = "直播";
                } else if (mCourseSelectCourseType.equals("1")){
                    course_type = "录播";
                } else if (mCourseSelectCourseType.equals("2")){
                    course_type = "直播,录播";
                }
                getCourseDatasMore("",project_id,subject_id,hour,fever,course_type);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                String project_id = "";
                String subject_id = "";
                String hour = "1";
                String fever = "1";
                String course_type = "";  //全部默认为空
                if (!mCourseSelect.equals("-1")){
                    project_id = mCourseSelect;
                }
                if (!mCourseSelect1.equals("-1")){
                    subject_id = mCourseSelect1;
                }
                if (mCourseSelectSort.equals("0")){
                    hour = "0";
                } else if (mCourseSelectSort.equals("1")){
                    fever = "0";
                }
                if (mCourseSelectCourseType.equals("0")){
                    course_type = "直播";
                } else if (mCourseSelectCourseType.equals("1")){
                    course_type = "录播";
                } else if (mCourseSelectCourseType.equals("2")){
                    course_type = "直播,录播";
                }
                getCourseDatas("",project_id,subject_id,hour,fever,course_type);
            }
        });
        return mview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //课程主界面展示
    public void CourseMainShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        RelativeLayout course_mainLayout = mview.findViewById(R.id.course_mainLayout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_mainLayout.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        course_mainLayout.setLayoutParams(LP);
        course_mainLayout.setVisibility(View.VISIBLE);
        ScrollView course_block_menu_scroll_view = mview.findViewById(R.id.course_block_menu_scroll_view);
        course_block_menu_scroll_view.scrollTo(0, 0);
        String project_id = "";
        String subject_id = "";
        String hour = "1";
        String fever = "1";
        String course_type = "";  //全部默认为空
        if (!mCourseSelect.equals("-1")){
            project_id = mCourseSelect;
        }
        if (!mCourseSelect1.equals("-1")){
            subject_id = mCourseSelect1;
        }
        if (mCourseSelectSort.equals("0")){
            hour = "0";
        } else if (mCourseSelectSort.equals("1")){
            fever = "0";
        }
        if (mCourseSelectCourseType.equals("0")){
            course_type = "直播";
        } else if (mCourseSelectCourseType.equals("1")){
            course_type = "录播";
        } else if (mCourseSelectCourseType.equals("2")){
            course_type = "直播,录播";
        }
        //获取课程列表的数据
        getCourseDatas("",project_id,subject_id,hour,fever,course_type);
    }

    //课程关键字搜索界面展示
    public void CourseMainSearchShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        RelativeLayout course_searchlayout = mview.findViewById(R.id.course_searchlayout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_searchlayout.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        course_searchlayout.setLayoutParams(LP);
        course_searchlayout.setVisibility(View.VISIBLE);
    }

    //隐藏所有图层
    private void HideAllLayout() {
        RelativeLayout course_mainLayout = mview.findViewById(R.id.course_mainLayout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_mainLayout.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        course_mainLayout.setLayoutParams(LP);
        course_mainLayout.setVisibility(View.INVISIBLE);
        RelativeLayout course_searchlayout = mview.findViewById(R.id.course_searchlayout);
        LP = (LinearLayout.LayoutParams) course_searchlayout.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        course_searchlayout.setLayoutParams(LP);
        course_searchlayout.setVisibility(View.INVISIBLE);
        RelativeLayout course_details1 = mview.findViewById(R.id.course_details1);
        LP = (LinearLayout.LayoutParams) course_details1.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        course_details1.setLayoutParams(LP);
        course_details1.setVisibility(View.INVISIBLE);
    }

    private ModelCourseCover mModelCourseCover = null;

    @Override
    public void OnClickListener(View view, ModelCourseCover modelCourseCover) {
        HideAllLayout();
        RelativeLayout course_details1 = mview.findViewById(R.id.course_details1);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_details1.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        course_details1.setLayoutParams(LP);
        course_details1.setVisibility(View.VISIBLE);
        course_details1.removeAllViews();
        LinearLayout course_linearlayout = mview.findViewById(R.id.course_linearlayout);
        course_linearlayout.removeAllViews();
        course_details1.addView(view);
        mControlMainActivity.onClickCourseDetails();
        mModelCourseCover = modelCourseCover;
    }

    //课程问答添加图片
    public void ModelCourseCoverQuestionPictureAdd(Intent data) {
        if (mModelCourseCover != null) {
            mModelCourseCover.ModelCourseCoverQuestionPictureAdd(data);
        }
    }

    //课程下载初始化
    public void CourseDownloadInit() {
        if (mModelCourseCover != null) {
            mModelCourseCover.CourseDownloadInit();
        }
    }

    //课程条件搜索界面展示
    public void CourseMainSearchConditionShow() {
        initPopupWindow();
    }

    /**
     *   * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *   *
     *   
     */
    class popupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    //初始化课程条件搜索界面
    protected void initPopupWindow() {
        View popupWindowView = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_course_selectpop, null);
        int height1 = (int) (getScreenHeight() - mview.getResources().getDimension(R.dimen.dp45) - getStateBar());
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, (int) mview.getResources().getDimension(R.dimen.dp_280), height1, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(mControlMainActivity.getLayoutInflater().inflate(R.layout.activity_main, null), Gravity.RIGHT, 0, 500);
        popupWindow.setBackgroundDrawable(null);
        //设置背景半透明
        backgroundAlpha(0.9f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());
        popupWindowView.setOnTouchListener((v, event) -> {
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            return false;
        });
        //添加一级搜索标签
        ControllerWarpLinearLayout course_select_warpLinearLayout1 = popupWindowView.findViewById(R.id.course_select_warpLinearLayout1);
        course_select_warpLinearLayout1.removeAllViews();
        //添加二级搜索标签
        ControllerWarpLinearLayout course_select_warpLinearLayout2 = popupWindowView.findViewById(R.id.course_select_warpLinearLayout2);
        course_select_warpLinearLayout2.removeAllViews();
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_select_warpLinearLayout2.getLayoutParams();
        ll.height = 0;
        course_select_warpLinearLayout2.setLayoutParams(ll);
        TextView course_select_signtitle2 = popupWindowView.findViewById(R.id.course_select_signtitle2);
        ll = (LinearLayout.LayoutParams) course_select_signtitle2.getLayoutParams();
        ll.height = 0;
        ll.topMargin = 0;
        course_select_signtitle2.setLayoutParams(ll);
        mCourseSelectTemp1 = mCourseSelect1;
        mCourseSelectTemp = mCourseSelect;
        //必须有的标签-全部:默认选中全部
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("全部");
            coursepacket_selectpop_child_signname.setHint("-1");
            course_select_warpLinearLayout1.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout1.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout1.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //清空二级目录的标签
                course_select_warpLinearLayout2.removeAllViews();
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_select_warpLinearLayout2.getLayoutParams();
                ll1.height = 0;
                course_select_warpLinearLayout2.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_select_signtitle2.getLayoutParams();
                ll1.height = 0;
                ll1.topMargin = 0;
                course_select_signtitle2.setLayoutParams(ll1);
                mCourseSelectTemp1 = mCourseSelect1;
                //将选中项置为当前选中项id
                mCourseSelectTemp = hint;
            });
            if (mCourseSelect.equals("-1")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //获取所有的一级目录
        getAllProject(popupWindowView);
        //添加排序方式搜索标签
        ControllerWarpLinearLayout course_select_warpLinearLayout3 = popupWindowView.findViewById(R.id.course_select_warpLinearLayout3);
        course_select_warpLinearLayout3.removeAllViews();
        mCourseSelectSortTemp = mCourseSelectSort;
        //必须有的标签-综合:默认选中综合
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("综合");
            coursepacket_selectpop_child_signname.setHint("-1");
            course_select_warpLinearLayout3.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout3.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout3.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectSortTemp = hint;
            });
            if (mCourseSelectSort.equals("-1")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //必须有的标签-按热度
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("按热度");
            coursepacket_selectpop_child_signname.setHint("0");
            course_select_warpLinearLayout3.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout3.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout3.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectSortTemp = hint;
            });
            if (mCourseSelectSort.equals("0")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //必须有的标签-按时间
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("按时间");
            coursepacket_selectpop_child_signname.setHint("1");
            course_select_warpLinearLayout3.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout3.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout3.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectSortTemp = hint;
            });
            if (mCourseSelectSort.equals("1")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //添加课程类型搜索标签
        ControllerWarpLinearLayout course_select_warpLinearLayout4 = popupWindowView.findViewById(R.id.course_select_warpLinearLayout4);
        course_select_warpLinearLayout4.removeAllViews();
        mCourseSelectCourseTypeTemp = mCourseSelectCourseType;
        //必须有的标签-综合:默认选中综合
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("综合");
            coursepacket_selectpop_child_signname.setHint("-1");
            course_select_warpLinearLayout4.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout4.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout4.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectCourseTypeTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectCourseTypeTemp = hint;
            });
            if (mCourseSelectCourseType.equals("-1")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //必须有的标签-直播
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("直播");
            coursepacket_selectpop_child_signname.setHint("0");
            course_select_warpLinearLayout4.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout4.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout4.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectCourseTypeTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectCourseTypeTemp = hint;
            });
            if (mCourseSelectCourseType.equals("0")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //必须有的标签-录播
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("录播");
            coursepacket_selectpop_child_signname.setHint("1");
            course_select_warpLinearLayout4.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout4.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout4.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectCourseTypeTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectCourseTypeTemp = hint;
            });
            if (mCourseSelectCourseType.equals("1")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //必须有的标签-混合
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("混合");
            coursepacket_selectpop_child_signname.setHint("2");
            course_select_warpLinearLayout4.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = course_select_warpLinearLayout4.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = course_select_warpLinearLayout4.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectCourseTypeTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCourseSelectCourseTypeTemp = hint;
            });
            if (mCourseSelectCourseType.equals("2")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //点击确定
        TextView course_select_buttonsure = popupWindowView.findViewById(R.id.course_select_buttonsure);
        course_select_buttonsure.setOnClickListener(v -> {
            mCourseSelect = mCourseSelectTemp;
            mCourseSelect1 = mCourseSelectTemp1;
            mCourseSelectSort = mCourseSelectSortTemp;
            mCourseSelectCourseType = mCourseSelectCourseTypeTemp;
            popupWindow.dismiss();
            String project_id = "";
            String subject_id = "";
            String hour = "1";
            String fever = "1";
            String course_type = "";  //全部默认为空
            if (!mCourseSelect.equals("-1")){
                project_id = mCourseSelect;
            }
            if (!mCourseSelect1.equals("-1")){
                subject_id = mCourseSelect1;
            }
            if (mCourseSelectSort.equals("0")){
                hour = "0";
            } else if (mCourseSelectSort.equals("1")){
                fever = "0";
            }
            if (mCourseSelectCourseType.equals("0")){
                course_type = "直播";
            } else if (mCourseSelectCourseType.equals("1")){
                course_type = "录播";
            } else if (mCourseSelectCourseType.equals("2")){
                course_type = "直播,录播";
            }
            getCourseDatas("",project_id,subject_id,hour,fever,course_type);
        });
        //点击重置
        TextView course_select_buttonreset = popupWindowView.findViewById(R.id.course_select_buttonreset);
        course_select_buttonreset.setOnClickListener(v -> {
            //将其他置为未选中
            int childcount = course_select_warpLinearLayout1.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = course_select_warpLinearLayout1.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCourseSelectTemp = "-1";
            mCourseSelect = "-1";
            childcount = course_select_warpLinearLayout3.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = course_select_warpLinearLayout3.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCourseSelectSortTemp = "-1";
            mCourseSelectSort = "-1";
            childcount = course_select_warpLinearLayout4.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = course_select_warpLinearLayout4.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectCourseTypeTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCourseSelectCourseTypeTemp = "-1";
            mCourseSelectCourseType = "-1";
            childcount = course_select_warpLinearLayout2.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = course_select_warpLinearLayout2.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectTemp1)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCourseSelectTemp1 = "-1";
            mCourseSelect1 = "-1";
        });
    }

    /**
     *   * 设置添加屏幕的背景透明度
     *   * @param bgAlpha
     *   
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mControlMainActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mControlMainActivity.getWindow().setAttributes(lp);
    }

    //获取屏幕高度 不包含虚拟按键=
    public static int getScreenHeight() {
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //获取状态栏高度
    private int getStateBar() {
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //获取课程列表
    private void getCourseDatas(String course_name,String project_id,String subject_id,String hour,String fever,String course_type) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout course_end = mview.findViewById(R.id.course_end);
        course_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        mCurrentPage = 1;
        HashMap<String,String> paramsMap = new HashMap<>();
        paramsMap.put("course_name",course_name);
        paramsMap.put("project_id",project_id);
        paramsMap.put("subject_id",subject_id);
        paramsMap.put("hour",hour);
        paramsMap.put("fever",fever);
        paramsMap.put("course_type",course_type);
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("pageNum", mCurrentPage);
        paramsMap1.put("pageSize",mPageCount);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CourseBean> call = modelObservableInterface.queryAllCourseInfo(body);
        call.enqueue(new Callback<CourseBean>() {
            @Override
            public void onResponse(Call<CourseBean> call, Response<CourseBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CourseBean courseBean = response.body();
                if (courseBean == null){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseBean.getErrorCode(),"")){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CourseBean.CourseDataBean courseDataBean = courseBean.getData();
                if (courseDataBean == null){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CourseBean.CourseListBean> courseListBeansList = courseDataBean.getData();
                if (courseListBeansList == null){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mCourseSum = courseDataBean.getTotal();
                LinearLayout course_linearlayout = mview.findViewById(R.id.course_linearlayout);
                course_linearlayout.removeAllViews();
                View line = null;
                for (int i = 0; i < courseListBeansList.size(); i++) {
                    CourseBean.CourseListBean courseListBean = courseListBeansList.get(i);
                    if (courseListBean == null) {
                        continue;
                    }
                    CourseInfo courseInfo = new CourseInfo();
                    courseInfo.mCourseId = String.valueOf(courseListBean.course_id);
                    courseInfo.mCourseCover = courseListBean.cover;
                    courseInfo.mCourseType = courseListBean.course_type;
                    courseInfo.mCourseName = courseListBean.course_name;
                    courseInfo.mCoursePriceOld = String.valueOf(courseListBean.price);
                    courseInfo.mCoursePrice = String.valueOf(courseListBean.special_price);
                    courseInfo.mCourseLearnPersonNum = String.valueOf(courseListBean.foke_stu_num);
                    //创建每个课程的视图，添加到课程列表
                    ModelCourseCover modelCourseCover = new ModelCourseCover();
                    modelCourseCover.ModelCourseCoverOnClickListenerSet(ModelCourse.this);
                    View modelCourseView = modelCourseCover.ModelCourseCover(mControlMainActivity, courseInfo);
                    course_linearlayout.addView(modelCourseView);
                    line = modelCourseView.findViewById(R.id.course_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_course != null){
                    mSmart_fragment_course.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CourseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_course != null){
                    mSmart_fragment_course.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //课程列表加载更多
    private void getCourseDatasMore(String course_name,String project_id,String subject_id,String hour,String fever,String course_type) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        mCurrentPage = mCurrentPage + 1;
        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("course_name",course_name);
        paramsMap.put("project_id",project_id);
        paramsMap.put("subject_id",subject_id);
        paramsMap.put("hour",hour);
        paramsMap.put("fever",fever);
        paramsMap.put("course_type",course_type);
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("pageNum", mCurrentPage);
        paramsMap1.put("pageSize",mPageCount);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CourseBean> call = modelObservableInterface.queryAllCourseInfo(body);
        call.enqueue(new Callback<CourseBean>() {
            @Override
            public void onResponse(Call<CourseBean> call, Response<CourseBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CourseBean courseBean = response.body();
                if (courseBean == null){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseBean.getErrorCode(),"")){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CourseBean.CourseDataBean courseDataBean = courseBean.getData();
                if (courseDataBean == null){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CourseBean.CourseListBean> courseListBeansList = courseDataBean.getData();
                if (courseListBeansList == null){
                    if (mSmart_fragment_course != null){
                        mSmart_fragment_course.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                LinearLayout course_linearlayout = mview.findViewById(R.id.course_linearlayout);
                View line = null;
                for (int i = 0; i < courseListBeansList.size(); i ++) {
                    CourseBean.CourseListBean courseListBean = courseListBeansList.get(i);
                    if (courseDataBean == null) {
                        continue;
                    }
                    CourseInfo courseInfo = new CourseInfo();
                    courseInfo.mCourseId = String.valueOf(courseListBean.course_id);
                    courseInfo.mCourseCover = courseListBean.cover;
                    courseInfo.mCourseType = courseListBean.course_type;
                    courseInfo.mCourseName = courseListBean.course_name;
                    courseInfo.mCoursePriceOld = String.valueOf(courseListBean.price);
                    courseInfo.mCoursePrice = String.valueOf(courseListBean.special_price);
                    courseInfo.mCourseLearnPersonNum = String.valueOf(courseListBean.foke_stu_num);
                    ModelCourseCover modelCourseCover = new ModelCourseCover();
                    modelCourseCover.ModelCourseCoverOnClickListenerSet(ModelCourse.this);
                    View modelCourseView = modelCourseCover.ModelCourseCover(mControlMainActivity, courseInfo);
                    course_linearlayout.addView(modelCourseView);
                    line = modelCourseView.findViewById(R.id.course_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_course != null){
                    mSmart_fragment_course.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CourseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_course != null){
                    mSmart_fragment_course.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取项目列表
    private void getAllProject(View popupWindowView) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),"");
        Call<ProjectBean> call = modelObservableInterface.queryAllProject(body);
        call.enqueue(new Callback<ProjectBean>() {
            @Override
            public void onResponse(Call<ProjectBean> call, Response<ProjectBean> response) {
                int code = response.code();
                if (code != 200){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "getAllProject  onErrorCode: " + code);
                    return;
                }
                ProjectBean baseBean = response.body();
                if ( baseBean == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "getAllProject  onErrorCode: " + code);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<ProjectBean.ProjectDataBean> data = baseBean.getData();
                if (data == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "getAllProject  onErrorCode: " + code);
                    return;
                }
                for (int num = 0 ; num < data.size() ; num ++){
                    ProjectBean.ProjectDataBean projectDataBean = data.get(num);
                    if (projectDataBean == null){
                        continue;
                    }
                    View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
                    TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
                    coursepacket_selectpop_child_signname.setText(projectDataBean.pse_name);
                    ControllerWarpLinearLayout course_select_warpLinearLayout1 = popupWindowView.findViewById(R.id.course_select_warpLinearLayout1);
                    course_select_warpLinearLayout1.addView(view);
                    coursepacket_selectpop_child_signname.setHint(projectDataBean.pse_id);
                    view.setOnClickListener(v -> {
                        //将其他置为未选中
                        String hint = "";
                        int childcount = course_select_warpLinearLayout1.getChildCount();
                        for (int i = 0; i < childcount; i++) {
                            View childView = course_select_warpLinearLayout1.getChildAt(i);
                            if (childView == null) {
                                continue;
                            }
                            TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                            if (childView == view) {
                                coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                                hint = coursepacket_selectpop_child_signname1.getHint().toString();
                            } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                                coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                            }
                        }
                        //将选中项置为当前选中项id
                        mCourseSelectTemp1 = "-1";
                        getAllSubjectFromOneProject(popupWindowView, Integer.parseInt(hint));
                        mCourseSelectTemp = hint;
                    });
                    if (mCourseSelect.equals(coursepacket_selectpop_child_signname.getHint().toString())) {
                        coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                        mCourseSelectTemp1 = mCourseSelect1;
                        getAllSubjectFromOneProject(popupWindowView, Integer.parseInt(mCourseSelect));
                    }
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ProjectBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取项目列表失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //查找某一项目下的子项目列表
    private void getAllSubjectFromOneProject(View popupWindowView,int project_id) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("project_id", project_id);
        Gson gson = new Gson();
        String strEntity = gson.toJson(paramsMap1);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ProjectBean> call = modelObservableInterface.queryAllSubjectFromOneProject(body);
        call.enqueue(new Callback<ProjectBean>() {
            @Override
            public void onResponse(Call<ProjectBean> call, Response<ProjectBean> response) {
                int code = response.code();
                if (code != 200){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "getAllSubjectFromOneProject  onErrorCode: " + code);
                    return;
                }
                ProjectBean baseBean = response.body();
                if (baseBean == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<ProjectBean.ProjectDataBean> data = baseBean.getData();
                if (data == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ControllerWarpLinearLayout course_select_warpLinearLayout2 = popupWindowView.findViewById(R.id.course_select_warpLinearLayout2);
                TextView course_select_signtitle2 = popupWindowView.findViewById(R.id.course_select_signtitle2);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_select_warpLinearLayout2.getLayoutParams();
                ll.height = 0;
                course_select_warpLinearLayout2.setLayoutParams(ll);
                ll = (LinearLayout.LayoutParams) course_select_signtitle2.getLayoutParams();
                ll.height = 0;
                ll.topMargin = 0;
                course_select_signtitle2.setLayoutParams(ll);
                course_select_warpLinearLayout2.removeAllViews();
                for (int num = 0 ; num < data.size() ; num ++){
                    ProjectBean.ProjectDataBean projectDataBean = data.get(num);
                    if (projectDataBean == null){
                        continue;
                    }
                    if (num == 0){
                        ll = (LinearLayout.LayoutParams) course_select_warpLinearLayout2.getLayoutParams();
                        ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        course_select_warpLinearLayout2.setLayoutParams(ll);
                        ll = (LinearLayout.LayoutParams) course_select_signtitle2.getLayoutParams();
                        ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        ll.topMargin = popupWindowView.getResources().getDimensionPixelSize(R.dimen.dp45);
                        course_select_signtitle2.setLayoutParams(ll);
                        //必须有的标签-全部:默认选中全部
                        View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
                        TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
                        coursepacket_selectpop_child_signname.setText("全部");
                        coursepacket_selectpop_child_signname.setHint("-1");
                        course_select_warpLinearLayout2.addView(view);
                        view.setOnClickListener(v -> {
                            //将其他置为未选中
                            String hint = "";
                            int childcount = course_select_warpLinearLayout2.getChildCount();
                            for (int i = 0; i < childcount; i++) {
                                View childView = course_select_warpLinearLayout2.getChildAt(i);
                                if (childView == null) {
                                    continue;
                                }
                                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                                if (childView == view) {
                                    coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                    coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                                    hint = coursepacket_selectpop_child_signname1.getHint().toString();
                                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectTemp1)) { // 如果上个找到上一个选中的id，将其置为未选状态
                                    coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                    coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                                }
                            }
                            //将选中项置为当前选中项id
                            mCourseSelectTemp1 = hint;
                        });
                        if (mCourseSelectTemp1.equals("-1")) {
                            coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                            coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                        }
                    }
                    View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
                    TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
                    coursepacket_selectpop_child_signname.setText(projectDataBean.pse_name);
                    course_select_warpLinearLayout2.addView(view);
                    coursepacket_selectpop_child_signname.setHint(projectDataBean.pse_id);
                    view.setOnClickListener(v -> {
                        //将其他置为未选中
                        String hint = "";
                        int childcount = course_select_warpLinearLayout2.getChildCount();
                        for (int i = 0; i < childcount; i++) {
                            View childView = course_select_warpLinearLayout2.getChildAt(i);
                            if (childView == null) {
                                continue;
                            }
                            TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                            if (childView == view) {
                                coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                                hint = coursepacket_selectpop_child_signname1.getHint().toString();
                            } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCourseSelectTemp1)) { // 如果上个找到上一个选中的id，将其置为未选状态
                                coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                            }
                        }
                        //将选中项置为当前选中项id
                        mCourseSelectTemp1 = hint;
                    });
                    if (mCourseSelectTemp1.equals(coursepacket_selectpop_child_signname.getHint().toString())) {
                        coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                    }
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ProjectBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取项目列表失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    public static class CourseBean{
        private CourseDataBean data;  //返回数据
        private int code;
//        private String errorMsg;

        public CourseDataBean getData() {
            return data;
        }
        public void setData(CourseDataBean data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

//        public String getErrorMsg() {
//            return errorMsg;
//        }

        //        public void setErrorMsg(String errorMsg) {
//            this.errorMsg = errorMsg;
//        }
        public static class CourseDataBean {
            private List<CourseListBean> list;
            private int total;
            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }
            public List<CourseListBean> getData() {
                return list;
            }




            public void setData(List<CourseListBean> list) {
                this.list = list;
            }
        }

        public static class CourseListBean {
            private String cover;               //封面
            private int course_id;              //课程id
            private String course_type;         //课程类型（直播/录播/混合）
            private String course_name;         //课程名称
            private float special_price;        //优惠价格
            private float price;                //价格
            private int foke_stu_num;     //学习人数
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
            public void se发tCourse_type(String course_type){
                this.course_type = course_type;
            }
            public String ge(){
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
                return this.foke_stu_num;
            }
            public void setBuying_base_number(int buying_base_number) {
                this.foke_stu_num = buying_base_number;
            }
        }
    }

    public static class ProjectBean {
        private List<ProjectDataBean> data;
        private int code;
        private String msg;

        public List<ProjectDataBean> getData() {
            return data;
        }

        public void setData(List<ProjectDataBean> data) {
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

        public static class ProjectDataBean {
            private String pse_id;
            private String pse_name;

            public String getPse_id() {
                return pse_id;
            }

            public String getPse_name() {
                return pse_name;
            }

            public void setPse_id(String pse_id) {
                this.pse_id = pse_id;
            }

            public void setPse_name(String pse_name) {
                this.pse_name = pse_name;
            }
        }
    }

}
