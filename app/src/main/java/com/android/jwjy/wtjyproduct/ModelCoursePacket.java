package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
 * 课程包
 */
public class ModelCoursePacket extends Fragment implements ModelCoursePacketCover.ModelCoursePacketCoverOnClickListener {
    private static ControlMainActivity mControlMainActivity;
    private static String mContext = "xxxxxxxxxxxxx";
    //要显示的页面
    static private int FragmentPage;
    private View mView;
    private int height = 1344;
    private int width = 720;

    private ModelSearchView searchView = null;
    //弹出窗口（筛选条件）
    private PopupWindow popupWindow;
    //一级搜索
    private String mCoursePacketSelectTemp = "-1";
    private String mCoursePacketSelect = "-1";
    //二级搜索
    private String mCoursePacketSelectTemp1 = "-1";
    private String mCoursePacketSelect1 = "-1";
    //排序方式搜索
    private String mCoursePacketSelectSortTemp = "-1";
    private String mCoursePacketSelectSort = "-1";
    private static final String TAG = "ModelCoursePacket";
    private SmartRefreshLayout mSmart_fragment_coursepacket;

    //课程包列表分页查询
    private int mCurrentPage = 0;
    private int mPageCount = 10;
    private int mCoursePacketSum = 0; //课程包总数

    public static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage) {
        mContext = context;
        mControlMainActivity = content;
        ModelCoursePacket myFragment = new ModelCoursePacket();
        FragmentPage = iFragmentPage;
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(FragmentPage, container, false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        if (mContext.equals("课程包:")) {
            CoursePacketMainShow(1);
        } else {
            CoursePacketMainShow(0);
        }
        // 3. 绑定组件
        searchView = mView.findViewById(R.id.search_view);
        searchView.init("coursepacketsearchrecords");
        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容,.
        searchView.setOnClickSearch((string) -> {
            System.out.println("我收到了" + string);
            if (mView == null) {
                return;
            }
            HideAllLayout();
            RelativeLayout coursepacket_mainLayout = mView.findViewById(R.id.coursepacket_mainLayout);
            LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_mainLayout.getLayoutParams();
            LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
            LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
            coursepacket_mainLayout.setLayoutParams(LP);
            coursepacket_mainLayout.setVisibility(View.VISIBLE);
            ScrollView coursepacket_block_menu_scroll_view = mView.findViewById(R.id.coursepacket_block_menu_scroll_view);
            coursepacket_block_menu_scroll_view.scrollTo(0, 0);
            //搜索的接口数据
            getModelSreachViewData(string);
        });
        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(() -> {
            mControlMainActivity.Page_MoreCoursePacket();
        });

//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) coursepacket_store_house_ptr_frame.getLayoutParams();
//        lp.topMargin = width / 10;
//        coursepacket_store_house_ptr_frame.setLayoutParams(lp);
        mSmart_fragment_coursepacket = mView.findViewById(R.id.Smart_fragment_coursepacket);
        mSmart_fragment_coursepacket.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //先判断是否已获取了全部数据
                if (mCoursePacketSum <= mCurrentPage * mPageCount){
                    LinearLayout course_end = mView.findViewById(R.id.coursepacket_end);
                    course_end.setVisibility(View.VISIBLE);
                    return;
                }
                String project_id = "";
                String subject_id = "";
                String fever = "1";
                String hour = "1";
                if (!mCoursePacketSelect.equals("-1")){
                    project_id = mCoursePacketSelect;
                }
                if (!mCoursePacketSelect1.equals("-1")){
                    subject_id = mCoursePacketSelect1;
                }
                if (!mCoursePacketSelectSort.equals("-1")){
                    hour = "0";
                }
                getConditionQueryMore(project_id,Integer.valueOf(fever),Integer.valueOf(hour));
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                String project_id = "";
                String subject_id = "";
                String fever = "1";
                String hour = "1";
                if (!mCoursePacketSelect.equals("-1")){
                    project_id = mCoursePacketSelect;
                }
                if (!mCoursePacketSelect1.equals("-1")){
                    subject_id = mCoursePacketSelect1;
                }
                if (!mCoursePacketSelectSort.equals("-1")){
                    hour = "0";
                }
                getConditionQuery(project_id,Integer.valueOf(fever),Integer.valueOf(hour));
            }
        });
        return mView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //展示课程包主界面
    public void CoursePacketMainShow(int returnString) { // returnString:  0:显示返回按钮
        if (mView == null) {
            return;
        }
        //主要参数
        HideAllLayout();
        RelativeLayout coursepacket_mainLayout = mView.findViewById(R.id.coursepacket_mainLayout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_mainLayout.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        coursepacket_mainLayout.setLayoutParams(LP);
        coursepacket_mainLayout.setVisibility(View.VISIBLE);
        //关键字的查询
        RelativeLayout coursepacket_titleRelativeLayout = mView.findViewById(R.id.coursepacket_titleRelativeLayout);
        //二级列表查询
        RelativeLayout coursepacket_titleRelativeLayout1 = mView.findViewById(R.id.coursepacket_titleRelativeLayout1);
        if (returnString == 0) {
            mContext = "课程包:首页";
            coursepacket_titleRelativeLayout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) coursepacket_titleRelativeLayout.getLayoutParams();
            lp.height = (int) mView.getResources().getDimension(R.dimen.dp44);
            coursepacket_titleRelativeLayout.setLayoutParams(lp);
            lp = (RelativeLayout.LayoutParams) coursepacket_titleRelativeLayout1.getLayoutParams();
            lp.topMargin = 0;
            coursepacket_titleRelativeLayout1.setLayoutParams(lp);
        } else {
            mContext = "课程包:";
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) coursepacket_titleRelativeLayout1.getLayoutParams();
            lp.height = (int) mView.getResources().getDimension(R.dimen.dp44);
            coursepacket_titleRelativeLayout1.setLayoutParams(lp);
            coursepacket_titleRelativeLayout.setVisibility(View.INVISIBLE);
            lp = (RelativeLayout.LayoutParams) coursepacket_titleRelativeLayout.getLayoutParams();
            lp.height = 0;
            coursepacket_titleRelativeLayout.setLayoutParams(lp);
        }
        ScrollView coursepacket_block_menu_scroll_view = mView.findViewById(R.id.coursepacket_block_menu_scroll_view);
        coursepacket_block_menu_scroll_view.scrollTo(0, 0);

        LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
        coursepacket_linearlayout.removeAllViews();
        //网络数据的集合dataBean
        String project_id = "";
        String subject_id = "";
        String fever = "1";
        String hour = "1";
        if (!mCoursePacketSelect.equals("-1")){
            project_id = mCoursePacketSelect;
        }
        if (!mCoursePacketSelect1.equals("-1")){
            subject_id = mCoursePacketSelect1;
        }
        if (!mCoursePacketSelectSort.equals("-1")){
            hour = "0";
        }
        //网络加载数据传相应的参数 刷新页面
        getConditionQuery(project_id,Integer.valueOf(fever),Integer.valueOf(hour));
    }

    //展示课程包搜索界面
    public void CoursePacketMainSearchShow() {
        if (mView == null) {
            return;
        }
        HideAllLayout();
        RelativeLayout coursepacket_searchlayout = mView.findViewById(R.id.coursepacket_searchlayout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_searchlayout.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        coursepacket_searchlayout.setLayoutParams(LP);
        coursepacket_searchlayout.setVisibility(View.VISIBLE);
    }
    //展示课程包搜索条件界面
    public void CoursePacketMainSearchConditionShow() {
        initPopupWindow();
    }

    //隐藏所有图层
    private void HideAllLayout() {
        RelativeLayout coursepacket_mainLayout = mView.findViewById(R.id.coursepacket_mainLayout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_mainLayout.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        coursepacket_mainLayout.setLayoutParams(LP);
        coursepacket_mainLayout.setVisibility(View.INVISIBLE);
        RelativeLayout coursepacket_searchlayout = mView.findViewById(R.id.coursepacket_searchlayout);
        LP = (LinearLayout.LayoutParams) coursepacket_searchlayout.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        coursepacket_searchlayout.setLayoutParams(LP);
        coursepacket_searchlayout.setVisibility(View.INVISIBLE);
        RelativeLayout coursepacket_details1 = mView.findViewById(R.id.coursepacket_details1);
        LP = (LinearLayout.LayoutParams) coursepacket_details1.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        coursepacket_details1.setLayoutParams(LP);
        coursepacket_details1.setVisibility(View.INVISIBLE);
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

    protected void initPopupWindow() {
        View popupWindowView = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop, null);
        int height1 = (int) (getScreenHeight() - mView.getResources().getDimension(R.dimen.dp45) - getStateBar());
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, (int) mView.getResources().getDimension(R.dimen.dp_280), height1, true);
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
        ControllerWarpLinearLayout coursepacket_select_warpLinearLayout1 = popupWindowView.findViewById(R.id.coursepacket_select_warpLinearLayout1);
        coursepacket_select_warpLinearLayout1.removeAllViews();
        //添加二级搜索标签
        ControllerWarpLinearLayout coursepacket_select_warpLinearLayout2 = popupWindowView.findViewById(R.id.coursepacket_select_warpLinearLayout2);
        coursepacket_select_warpLinearLayout2.removeAllViews();
        mCoursePacketSelectTemp1 = mCoursePacketSelect1;   //二级
        mCoursePacketSelectTemp = mCoursePacketSelect;     //一级
        //必须有的标签-全部:默认选中全部   1级列表标签
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("全部");
            coursepacket_selectpop_child_signname.setHint("-1");
            coursepacket_select_warpLinearLayout1.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = coursepacket_select_warpLinearLayout1.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = coursepacket_select_warpLinearLayout1.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCoursePacketSelectTemp = hint;
            });
            if (mCoursePacketSelect.equals("-1")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        getAllProject(coursepacket_select_warpLinearLayout1);
        //添加排序方式搜索标签
        ControllerWarpLinearLayout coursepacket_select_warpLinearLayout3 = popupWindowView.findViewById(R.id.coursepacket_select_warpLinearLayout3);
        coursepacket_select_warpLinearLayout3.removeAllViews();
        mCoursePacketSelectSortTemp = mCoursePacketSelectSort;
        //必须有的标签-综合:默认选中综合
        {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
            TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
            coursepacket_selectpop_child_signname.setText("综合");
            coursepacket_selectpop_child_signname.setHint("-1");
            coursepacket_select_warpLinearLayout3.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = coursepacket_select_warpLinearLayout3.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = coursepacket_select_warpLinearLayout3.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCoursePacketSelectSortTemp = hint;
            });
            if (mCoursePacketSelectSort.equals("-1")) {
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
            coursepacket_select_warpLinearLayout3.addView(view);
            view.setOnClickListener(v -> {
                //将其他置为未选中
                String hint = "";
                int childcount = coursepacket_select_warpLinearLayout3.getChildCount();
                for (int i = 0; i < childcount; i++) {
                    View childView = coursepacket_select_warpLinearLayout3.getChildAt(i);
                    if (childView == null) {
                        continue;
                    }
                    TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                    if (childView == view) {
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                        hint = coursepacket_selectpop_child_signname1.getHint().toString();
                    } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                        coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                        coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                    }
                }
                //将选中项置为当前选中项id
                mCoursePacketSelectSortTemp = hint;
            });
            if (mCoursePacketSelectSort.equals("0")) {
                coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
            }
        }
        //点击确定
        TextView communityanswer_select_buttonsure = popupWindowView.findViewById(R.id.coursepacket_select_buttonsure);
        communityanswer_select_buttonsure.setOnClickListener(v -> {
            mCoursePacketSelect = mCoursePacketSelectTemp;
            mCoursePacketSelect1 = mCoursePacketSelectTemp1;
            mCoursePacketSelectSort = mCoursePacketSelectSortTemp;
            String project_id = "";
            String subject_id = "";
            String fever = "1";
            String hour = "1";
//            boolean isConditionSelect = false;
            if (!mCoursePacketSelect.equals("-1")){
                project_id = mCoursePacketSelect;
//                isConditionSelect = true;
            }
            if (!mCoursePacketSelect1.equals("-1")){
                subject_id = mCoursePacketSelect1;
//                isConditionSelect = true;
            }
            if (!mCoursePacketSelectSort.equals("-1")){
                hour = "0";
//                isConditionSelect = true;
            }
            //网络加载数据传相应的参数 刷新页面
            getConditionQuery(project_id,Integer.valueOf(fever),Integer.valueOf(hour));
            //返回数据
            popupWindow.dismiss();
        });
        //点击重置
        TextView communityanswer_select_buttonreset = popupWindowView.findViewById(R.id.coursepacket_select_buttonreset);
        communityanswer_select_buttonreset.setOnClickListener(v -> {
            //将其他置为未选中
            int childcount = coursepacket_select_warpLinearLayout1.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = coursepacket_select_warpLinearLayout1.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCoursePacketSelectTemp = "-1";
            mCoursePacketSelect = "-1";
            childcount = coursepacket_select_warpLinearLayout3.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = coursepacket_select_warpLinearLayout3.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectSortTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCoursePacketSelectSortTemp = "-1";
            mCoursePacketSelectSort = "-1";
            childcount = coursepacket_select_warpLinearLayout2.getChildCount();
            for (int i = 0; i < childcount; i++) {
                View childView = coursepacket_select_warpLinearLayout2.getChildAt(i);
                if (childView == null) {
                    continue;
                }
                TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                int padding = (int) childView.getResources().getDimension(R.dimen.dp5);
                if (coursepacket_selectpop_child_signname1.getHint().toString().equals("-1")) {
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.white));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectTemp1)) { // 如果上个找到上一个选中的id，将其置为未选状态
                    coursepacket_selectpop_child_signname1.setBackground(childView.getResources().getDrawable(R.drawable.textview_style_rect));
                    coursepacket_selectpop_child_signname1.setTextColor(childView.getResources().getColor(R.color.grayff999999));
                    coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                }
            }
            mCoursePacketSelectTemp1 = "-1";
            mCoursePacketSelect1 = "-1";
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

    private int getStateBar() {
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void OnClickListener(View view) {
        HideAllLayout();
        RelativeLayout coursepacket_details1 = mView.findViewById(R.id.coursepacket_details1);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_details1.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        coursepacket_details1.setLayoutParams(LP);
        coursepacket_details1.setVisibility(View.VISIBLE);
        coursepacket_details1.removeAllViews();
        LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
        coursepacket_linearlayout.removeAllViews();
        coursepacket_details1.addView(view);
        mControlMainActivity.onClickCoursePacketDetails();
    }

    //数据的条件筛选
    private void getConditionQuery(String project_id,int fever,int hour) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout coursepacket_end = mView.findViewById(R.id.coursepacket_end);
        coursepacket_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCurrentPage = 1;
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize",mPageCount);
        if (!project_id.equals("")){
            paramsMap.put("project_id",Integer.valueOf(project_id));
        }
        paramsMap.put("fever",fever);
        paramsMap.put("hour",hour);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CoursePacketBean> call = modelObservableInterface.queryAllcoursePackageSearchBox(body);
        call.enqueue(new Callback<CoursePacketBean>() {
            @Override
            public void onResponse(Call<CoursePacketBean> call, Response<CoursePacketBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CoursePacketBean coursePacketBean = response.body();
                if (coursePacketBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(coursePacketBean.code,"")){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CoursePacketBean.CoursePacketDataBean coursePacketDataBean = coursePacketBean.getData();
                if (coursePacketDataBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CoursePacketBean.DataBean> coursePacketListBeansList = coursePacketDataBean.getData();
                if (coursePacketListBeansList == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mCoursePacketSum = coursePacketDataBean.getTotal();
                View line = null;
                LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
                coursepacket_linearlayout.removeAllViews();
                for (int i = 0; i < coursePacketListBeansList.size(); i++) {
                    CoursePacketBean.DataBean dataBean = coursePacketListBeansList.get(i);
                    if (dataBean == null) {
                        continue;
                    }
                    ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                    modelCoursePacketCover.ModelCoursePacketCoverOnClickListenerSet(ModelCoursePacket.this);
                    //new 一个相关的实体类直接一个一个赋值
                    //            private int total_price;          //总价格        private int courseNum;    //课程数量
                    //            private String cp_name;           //	课程包名字     private int favorable_price;    //优惠价格
                    //            private int buying_base_number;  //优惠价格
                    CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                    coursePacketInfo.mCoursePacketId = String.valueOf(dataBean.course_package_id);//课程
                    coursePacketInfo.mCoursePacketCover = dataBean.cover;
                    coursePacketInfo.mCoursePacketStageNum = String.valueOf(dataBean.stageNum);
                    coursePacketInfo.mCoursePacketName = dataBean.cp_name;   //包名
                    coursePacketInfo.mCoursePacketPrice = String.valueOf(dataBean.favorable_price);//总价格
                    coursePacketInfo.mCoursePacketCourseNum = String.valueOf(dataBean.courseNum);
                    coursePacketInfo.mCoursePacketPriceOld = String.valueOf(dataBean.total_price);//数据原来的价格
                    coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(dataBean.buying_base_number);//购买人数
                    View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                    coursepacket_linearlayout.addView(modelCoursePacketView);
                    line = modelCoursePacketView.findViewById(R.id.coursepacket_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CoursePacketBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    private void getConditionQueryMore(String project_id,int fever,int hour) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout coursepacket_end = mView.findViewById(R.id.coursepacket_end);
        coursepacket_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCurrentPage = mCurrentPage + 1;
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize",mPageCount);
        if (!project_id.equals("")){
            paramsMap.put("project_id",Integer.valueOf(project_id));
        }
        paramsMap.put("fever",fever);
        paramsMap.put("hour",hour);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CoursePacketBean> call = modelObservableInterface.queryAllcoursePackageSearchBox(body);
        call.enqueue(new Callback<CoursePacketBean>() {
            @Override
            public void onResponse(Call<CoursePacketBean> call, Response<CoursePacketBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CoursePacketBean coursePacketBean = response.body();
                if (coursePacketBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(coursePacketBean.code,"")){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CoursePacketBean.CoursePacketDataBean coursePacketDataBean = coursePacketBean.getData();
                if (coursePacketDataBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CoursePacketBean.DataBean> coursePacketListBeansList = coursePacketDataBean.getData();
                if (coursePacketListBeansList == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                View line = null;
                LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
                for (int i = 0; i < coursePacketListBeansList.size(); i++) {
                    CoursePacketBean.DataBean dataBean = coursePacketListBeansList.get(i);
                    if (dataBean == null) {
                        continue;
                    }
                    ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                    modelCoursePacketCover.ModelCoursePacketCoverOnClickListenerSet(ModelCoursePacket.this);
                    //new 一个相关的实体类直接一个一个赋值
                    //            private int total_price;          //总价格        private int courseNum;    //课程数量
                    //            private String cp_name;           //	课程包名字     private int favorable_price;    //优惠价格
                    //            private int buying_base_number;  //优惠价格
                    CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                    coursePacketInfo.mCoursePacketId = String.valueOf(dataBean.course_package_id);//课程
                    coursePacketInfo.mCoursePacketCover = dataBean.cover;
                    coursePacketInfo.mCoursePacketStageNum = String.valueOf(dataBean.stageNum);
                    coursePacketInfo.mCoursePacketName = dataBean.cp_name;   //包名
                    coursePacketInfo.mCoursePacketPrice = String.valueOf(dataBean.favorable_price);//总价格
                    coursePacketInfo.mCoursePacketCourseNum = String.valueOf(dataBean.courseNum);
                    coursePacketInfo.mCoursePacketPriceOld = String.valueOf(dataBean.total_price);//数据原来的价格
                    coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(dataBean.buying_base_number);//购买人数
                    View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                    coursepacket_linearlayout.addView(modelCoursePacketView);
                    line = modelCoursePacketView.findViewById(R.id.coursepacket_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CoursePacketBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //课程包的关键字搜索
    private void getModelSreachViewData(String packetName){
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout coursepacket_end = mView.findViewById(R.id.coursepacket_end);
        coursepacket_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCurrentPage = 1;
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("cp_name",packetName);
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize",mPageCount);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CoursePacketBean> call = modelObservableInterface.queryAllCoursePackageSelectName(body);
        call.enqueue(new Callback<CoursePacketBean>() {
            @Override
            public void onResponse(Call<CoursePacketBean> call, Response<CoursePacketBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CoursePacketBean coursePacketBean = response.body();
                if (coursePacketBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(coursePacketBean.getCode(),"")){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CoursePacketBean.CoursePacketDataBean coursePacketDataBean = coursePacketBean.getData();
                if (coursePacketDataBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CoursePacketBean.DataBean> coursePacketListBeansList = coursePacketDataBean.getData();
                if (coursePacketListBeansList == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mCoursePacketSum = coursePacketDataBean.getTotal();
                View line = null;
                LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
                coursepacket_linearlayout.removeAllViews();
                for (int i = 0; i < coursePacketListBeansList.size(); i++) {
                    CoursePacketBean.DataBean dataBean = coursePacketListBeansList.get(i);
                    if (dataBean == null) {
                        continue;
                    }
                    ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                    modelCoursePacketCover.ModelCoursePacketCoverOnClickListenerSet(ModelCoursePacket.this);
                    //new 一个相关的实体类直接一个一个赋值
                    //            private int total_price;          //总价格        private int courseNum;    //课程数量
                    //            private String cp_name;           //	课程包名字     private int favorable_price;    //优惠价格
                    //            private int buying_base_number;  //优惠价格
                    CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                    coursePacketInfo.mCoursePacketId = String.valueOf(dataBean.course_package_id);//课程
                    coursePacketInfo.mCoursePacketCover = dataBean.cover;
                    coursePacketInfo.mCoursePacketStageNum = String.valueOf(dataBean.stageNum);
                    coursePacketInfo.mCoursePacketName = dataBean.cp_name;   //包名
                    coursePacketInfo.mCoursePacketPrice = String.valueOf(dataBean.favorable_price);//总价格
                    coursePacketInfo.mCoursePacketCourseNum = String.valueOf(dataBean.courseNum);
                    coursePacketInfo.mCoursePacketPriceOld = String.valueOf(dataBean.total_price);//数据原来的价格
                    coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(dataBean.buying_base_number);//购买人数
                    View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                    coursepacket_linearlayout.addView(modelCoursePacketView);
                    line = modelCoursePacketView.findViewById(R.id.coursepacket_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CoursePacketBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //课程包列表请求
    private void getModelCoursePacketDatas() {
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout coursepacket_end = mView.findViewById(R.id.coursepacket_end);
        coursepacket_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCurrentPage = 1;
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize",mPageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CoursePacketBean> call = modelObservableInterface.queryAllCoursePackageInfo(body);
        call.enqueue(new Callback<CoursePacketBean>() {
            @Override
            public void onResponse(Call<CoursePacketBean> call, Response<CoursePacketBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CoursePacketBean coursePacketBean = response.body();
                if (coursePacketBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(coursePacketBean.getCode(),"")){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CoursePacketBean.CoursePacketDataBean coursePacketDataBean = coursePacketBean.getData();
                if (coursePacketDataBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CoursePacketBean.DataBean> coursePacketListBeansList = coursePacketDataBean.getData();
                if (coursePacketListBeansList == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mCoursePacketSum = coursePacketDataBean.getTotal();
                View line = null;
                LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
                coursepacket_linearlayout.removeAllViews();
                for (int i = 0; i < coursePacketListBeansList.size(); i++) {
                    CoursePacketBean.DataBean dataBean = coursePacketListBeansList.get(i);
                    if (dataBean == null) {
                        continue;
                    }
                    ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                    modelCoursePacketCover.ModelCoursePacketCoverOnClickListenerSet(ModelCoursePacket.this);
                    //new 一个相关的实体类直接一个一个赋值
                    //            private int total_price;          //总价格        private int courseNum;    //课程数量
                    //            private String cp_name;           //	课程包名字     private int favorable_price;    //优惠价格
                    //            private int buying_base_number;  //优惠价格
                    CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                    coursePacketInfo.mCoursePacketId = String.valueOf(dataBean.course_package_id);//课程
                    coursePacketInfo.mCoursePacketCover = dataBean.cover;
                    coursePacketInfo.mCoursePacketStageNum = String.valueOf(dataBean.stageNum);
                    coursePacketInfo.mCoursePacketName = dataBean.cp_name;   //包名
                    coursePacketInfo.mCoursePacketPrice = String.valueOf(dataBean.favorable_price);//总价格
                    coursePacketInfo.mCoursePacketCourseNum = String.valueOf(dataBean.courseNum);
                    coursePacketInfo.mCoursePacketPriceOld = String.valueOf(dataBean.total_price);//数据原来的价格
                    coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(dataBean.buying_base_number);//购买人数
                    View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                    coursepacket_linearlayout.addView(modelCoursePacketView);
                    line = modelCoursePacketView.findViewById(R.id.coursepacket_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CoursePacketBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    private void getModelCoursePacketDatasMore() {
        LoadingDialog.getInstance(mControlMainActivity).show();
        LinearLayout coursepacket_end = mView.findViewById(R.id.coursepacket_end);
        coursepacket_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCurrentPage = mCurrentPage + 1;
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCurrentPage);
        paramsMap.put("pageSize",mPageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CoursePacketBean> call = modelObservableInterface.queryAllCoursePackageInfo(body);
        call.enqueue(new Callback<CoursePacketBean>() {
            @Override
            public void onResponse(Call<CoursePacketBean> call, Response<CoursePacketBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                CoursePacketBean coursePacketBean = response.body();
                if (coursePacketBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(coursePacketBean.getCode(),"")){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CoursePacketBean.CoursePacketDataBean coursePacketDataBean = coursePacketBean.getData();
                if (coursePacketDataBean == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<CoursePacketBean.DataBean> coursePacketListBeansList = coursePacketDataBean.getData();
                if (coursePacketListBeansList == null){
                    if (mSmart_fragment_coursepacket != null){
                        mSmart_fragment_coursepacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                View line = null;
                LinearLayout coursepacket_linearlayout = mView.findViewById(R.id.coursepacket_linearlayout);
                for (int i = 0; i < coursePacketListBeansList.size(); i++) {
                    CoursePacketBean.DataBean dataBean = coursePacketListBeansList.get(i);
                    if (dataBean == null) {
                        continue;
                    }
                    ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
                    modelCoursePacketCover.ModelCoursePacketCoverOnClickListenerSet(ModelCoursePacket.this);
                    //new 一个相关的实体类直接一个一个赋值
                    //            private int total_price;          //总价格        private int courseNum;    //课程数量
                    //            private String cp_name;           //	课程包名字     private int favorable_price;    //优惠价格
                    //            private int buying_base_number;  //优惠价格
                    CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                    coursePacketInfo.mCoursePacketId = String.valueOf(dataBean.course_package_id);//课程
                    coursePacketInfo.mCoursePacketCover = dataBean.cover;
                    coursePacketInfo.mCoursePacketStageNum = String.valueOf(dataBean.stageNum);
                    coursePacketInfo.mCoursePacketName = dataBean.cp_name;   //包名
                    coursePacketInfo.mCoursePacketPrice = String.valueOf(dataBean.favorable_price);//总价格
                    coursePacketInfo.mCoursePacketCourseNum = String.valueOf(dataBean.courseNum);
                    coursePacketInfo.mCoursePacketPriceOld = String.valueOf(dataBean.total_price);//数据原来的价格
                    coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(dataBean.buying_base_number);//购买人数
                    View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mControlMainActivity, coursePacketInfo);
                    coursepacket_linearlayout.addView(modelCoursePacketView);
                    line = modelCoursePacketView.findViewById(R.id.coursepacket_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CoursePacketBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程包列表数据失败",Toast.LENGTH_LONG).show();
                if (mSmart_fragment_coursepacket != null){
                    mSmart_fragment_coursepacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取所有一级目录
    private void getAllProject(View popupWindowView) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),"");
        Call<ModelCourse.ProjectBean> call = modelObservableInterface.queryAllProject(body);
        call.enqueue(new Callback<ModelCourse.ProjectBean>() {
            @Override
            public void onResponse(Call<ModelCourse.ProjectBean> call, Response<ModelCourse.ProjectBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "getAllProject  onErrorCode: " + code);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelCourse.ProjectBean baseBean = response.body();
                if (baseBean == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                List<ModelCourse.ProjectBean.ProjectDataBean> data = baseBean.getData();
                for (int num = 0 ; num < data.size() ; num ++){
                    ModelCourse.ProjectBean.ProjectDataBean projectDataBean = data.get(num);
                    if (projectDataBean == null){
                        continue;
                    }
                    View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.model_coursepacket_selectpop_child, null);
                    TextView coursepacket_selectpop_child_signname = view.findViewById(R.id.coursepacket_selectpop_child_signname);
                    ControllerWarpLinearLayout coursepacket_select_warpLinearLayout1 = popupWindowView.findViewById(R.id.coursepacket_select_warpLinearLayout1);
                    coursepacket_select_warpLinearLayout1.addView(view);
                    coursepacket_selectpop_child_signname.setHint(projectDataBean.getPse_id());
                    coursepacket_selectpop_child_signname.setText(projectDataBean.getPse_name());
                    view.setOnClickListener(v -> {
                        //将其他置为未选中
                        String hint = "";
                        int childcount = coursepacket_select_warpLinearLayout1.getChildCount();
                        for (int i = 0; i < childcount; i++) {
                            View childView = coursepacket_select_warpLinearLayout1.getChildAt(i);
                            if (childView == null) {
                                continue;
                            }
                            TextView coursepacket_selectpop_child_signname1 = childView.findViewById(R.id.coursepacket_selectpop_child_signname);
                            int padding = (int) view.getResources().getDimension(R.dimen.dp5);
                            if (childView == view) {
                                coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.white));
                                coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                                hint = coursepacket_selectpop_child_signname1.getHint().toString();
                            } else if (coursepacket_selectpop_child_signname1.getHint().toString().equals(mCoursePacketSelectTemp)) { // 如果上个找到上一个选中的id，将其置为未选状态
                                coursepacket_selectpop_child_signname1.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                coursepacket_selectpop_child_signname1.setTextColor(view.getResources().getColor(R.color.grayff999999));
                                coursepacket_selectpop_child_signname1.setPadding(padding, padding, padding, padding);
                            }
                        }
                        //将选中项置为当前选中项id
                        mCoursePacketSelectTemp = hint;
                    });
                    if (mCoursePacketSelect.equals(coursepacket_selectpop_child_signname.getHint().toString())) {
                        coursepacket_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                        coursepacket_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                        mCoursePacketSelectTemp = mCoursePacketSelect;
                    }
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelCourse.ProjectBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取项目列表失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //课程包的列表数据结构
    public static class CoursePacketBean {
        /**
         * code : 200
         * data : [{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":7,"course_package_id":1,"total_price":8888,"courseNum":1,"cp_name":"课程包001","favorable_price":1,"buying_base_number":52},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":2,"course_package_id":3,"total_price":8888,"courseNum":null,"cp_name":"课程包003","favorable_price":1,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":0,"course_package_id":4,"total_price":8888,"courseNum":null,"cp_name":"课程包004","favorable_price":1,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":0,"course_package_id":6,"total_price":8888,"courseNum":null,"cp_name":"课程包001","favorable_price":1,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":0,"course_package_id":7,"total_price":8888,"courseNum":null,"cp_name":"课程包001","favorable_price":1,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":0,"course_package_id":8,"total_price":8888,"courseNum":null,"cp_name":"江山","favorable_price":111,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":0,"course_package_id":9,"total_price":8888,"courseNum":null,"cp_name":"江山","favorable_price":111,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":0,"course_package_id":14,"total_price":8888,"courseNum":null,"cp_name":"江山11","favorable_price":111,"buying_base_number":52}]
         */

        private int code;
        private CoursePacketDataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public CoursePacketDataBean getData() {
            return data;
        }

        public void setData(CoursePacketDataBean data) {
            this.data = data;
        }

        public static class CoursePacketDataBean {
            private List<DataBean> list;
            private int total;
            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }
            public List<DataBean> getData() {
                return list;
            }
        }

        public static class DataBean {
            /**
             * cover : http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png
             * stageNum : 7
             * course_package_id : 1
             * total_price : 8888
             * courseNum : 1
             * cp_name : 课程包001
             * favorable_price : 1
             * buying_base_number : 52
             */

            private String cover;   //数据包的图片
            private Integer stageNum;    //阶段数量
            private Integer course_package_id;  //课程包id
            private Double total_price;    //总价格
            private Integer courseNum;    //课程数量
            private String cp_name;     //	课程包名字
            private Double favorable_price;    //优惠价格
            private Double buying_base_number;  //优惠价格

            public void setStageNum(Integer stageNum) {
                this.stageNum = stageNum;
            }

            public void setCourse_package_id(Integer course_package_id) {
                this.course_package_id = course_package_id;
            }

            public Double getTotal_price() {
                return total_price;
            }

            public void setTotal_price(Double total_price) {
                this.total_price = total_price;
            }

            public void setCourseNum(Integer courseNum) {
                this.courseNum = courseNum;
            }

            public void setFavorable_price(Double favorable_price) {
                this.favorable_price = favorable_price;
            }

            public void setBuying_base_number(Double buying_base_number) {
                this.buying_base_number = buying_base_number;
            }

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
        }
    }
    //搜索地实体类
    public static class CoursePacketSearchView{
        /**
         * code : 200
         * data : [{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":7,"course_package_id":1,"total_price":8888,"courseNum":1,"cp_name":"课程包001","favorable_price":1,"buying_base_number":52},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":0,"course_package_id":6,"total_price":8888,"courseNum":null,"cp_name":"课程包001","favorable_price":1,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","stageNum":0,"course_package_id":7,"total_price":8888,"courseNum":null,"cp_name":"课程包001","favorable_price":1,"buying_base_number":50}]
         */
        private int code;
        private List<DataBean> data;

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
             * cover : http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png
             * stageNum : 7
             * course_package_id : 1
             * total_price : 8888.0
             * courseNum : 1
             * cp_name : 课程包001
             * favorable_price : 1.0
             * buying_base_number : 52
             */

            private String cover;
            private int stageNum;
            private int course_package_id;
            private double total_price;
            private int courseNum;
            private String cp_name;
            private double favorable_price;
            private int buying_base_number;

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

            public double getTotal_price() {
                return total_price;
            }

            public void setTotal_price(double total_price) {
                this.total_price = total_price;
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

            public double getFavorable_price() {
                return favorable_price;
            }

            public void setFavorable_price(double favorable_price) {
                this.favorable_price = favorable_price;
            }

            public int getBuying_base_number() {
                return buying_base_number;
            }

            public void setBuying_base_number(int buying_base_number) {
                this.buying_base_number = buying_base_number;
            }
        }
    }


}
