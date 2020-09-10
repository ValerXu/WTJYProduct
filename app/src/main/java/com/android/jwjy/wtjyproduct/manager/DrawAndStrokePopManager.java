package com.android.jwjy.wtjyproduct.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.DrawAdapter;
import com.android.jwjy.wtjyproduct.adapter.StrokeAdapter;
import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.util.EventBusUtil;
import com.talkfun.sdk.HtSdk;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;
import com.talkfun.whiteboard.config.DrawType;
import com.talkfun.whiteboard.presenter.draw.IWhiteBoardOperator;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wallace on 2017/3/3.
 */
public class DrawAndStrokePopManager {
    @BindView(R.id.draw_gv)
    GridView drawGV;
    @BindView(R.id.stroke_gv)
    GridView strokeGV;
    //    @BindView(R.id.color_gv)
//    GridView colorGV;
    private Context mContext;
    private StrokeAdapter mStrokeAdapter;
    //    private ColorAdapter mColorAdapter;
    private DrawAdapter mDrawAdapter;
    private IWhiteBoardOperator opetator;
    private int[] drawTypeArr = {DrawType.DRAW_PATH_MODE, DrawType.DRAW_LINE_MODE, DrawType.DRAW_OVAL_MODE, DrawType.DRAW_RECTANGLE_MODE, DrawType.DRAW_CLEAR_MODE};
    private Integer[] drawIconArr = {R.mipmap.panel_draw, R.mipmap.panel_line, R.mipmap.panel_oval, R.mipmap.panel_rectangle};
    private Integer[] drawIconSelectArr = {R.mipmap.panel_draw_select, R.mipmap.panel_line_select, R.mipmap.panel_oval_select, R.mipmap.panel_rectangle_select};
    private int[] strokeSizeArr = {StrokeSize.STROKE_TWO, StrokeSize.STROKE_FOUR, StrokeSize.STROKE_SIX, StrokeSize.STROKE_EIGHT, StrokeSize.STROKE_TEN};
    private Float[] strokeSizeFloatArr = {0.2f, 0.3f, 0.4f, 0.5f, 0.6f};

    public static final int CMD_TYPE = 0;
    public static final int STROKE_TYPE = 1;
    public static final int COLOR_TYPE = 2;
    private PopView popWindow;

    /**
     * cmd
     * 最后一次选择的下角标
     */
    private int lastPosition = 0;
    private int selectDrawType = DrawType.DRAW_PATH_MODE;
    private int selectStrokeWidth = StrokeSize.STROKE_TWO;


    //粗细值
    class StrokeSize {
        private static final int STROKE_TWO = 5;
        private static final int STROKE_FOUR = 7;
        private static final int STROKE_SIX = 9;
        private static final int STROKE_EIGHT = 11;
        private static final int STROKE_TEN = 13;
    }
//
//    //颜色值
//    class ColorValue {
//        private static final String BLACK = "#000000";
//        private static final String RED = "#f34747";
//        private static final String YELLOW = "#ffea38";
//        private static final String GREEN = "#10e329";
//        private static final String CYAN = "#10e329";
//        private static final String BLUE = "#1188ff";
//        private static final String PURPLE = "#81511c";
//        private static final String WHITE = "#ffffff";
//    }


    public DrawAndStrokePopManager(Context context) {
        mContext = context;
        initView();
    }

    public void initView() {
        View rootView = View.inflate(mContext, R.layout.panelview_layout, null);
        popWindow = new PopView(mContext).setContentView(rootView).setFocusable(true).setOutsideTouchable(true).setFocusAndOutsideEnable(true).createPopup();
        ButterKnife.bind(this, rootView);
        setAdapter();
        initData();
        initEvent();
    }


    private void setAdapter() {
        mDrawAdapter = new DrawAdapter(mContext);
        mStrokeAdapter = new StrokeAdapter(mContext);
        strokeGV.setAdapter(mStrokeAdapter);
        drawGV.setAdapter(mDrawAdapter);

    }

    /**
     * 设置默认值
     */
    private void initData() {
        opetator = HtSdk.getInstance().getWhiteboardOperator();
        opetator.setStrokeWidth(strokeSizeArr[0]);
        opetator.setDrawType(drawTypeArr[0]);
        List<Float> mStrokeFloatList = Arrays.asList(strokeSizeFloatArr);
        mStrokeAdapter.addItems(mStrokeFloatList);
        List<Integer> mCmdIconList = Arrays.asList(drawIconArr);
        mDrawAdapter.addItems(mCmdIconList);
    }

    /**
     * 刷新后重新设置类型和粗细
     */
    public void setDrawTypeAndStroke() {
        if (opetator != null) {
            opetator.setDrawType(selectDrawType);
            opetator.setStrokeWidth(selectStrokeWidth);
        }

    }

    private void initEvent() {
        drawGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectDrawType = drawTypeArr[position];
                opetator.setDrawType(drawTypeArr[position]);
                lastPosition = position;
                EventBusUtil.postEvent(new Event(EventType.SMALL_ROOM_POP_CMD, drawIconSelectArr[position]));
                mDrawAdapter.setSelectSingleItem(position);
            }
        });

        strokeGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectStrokeWidth = strokeSizeArr[position];
                opetator.setStrokeWidth(strokeSizeArr[position]);
                mStrokeAdapter.setSelectSingleItem(position);
                EventBusUtil.postEvent(new Event(EventType.SMALL_ROOM_POP_STROKE, 1 - strokeSizeFloatArr[position]));
            }
        });
    }

    public void setEraser(boolean isSelect) {
        if (opetator != null) {
            opetator.setDrawType(isSelect ? DrawType.DRAW_CLEAR_MODE : drawTypeArr[lastPosition]);
        }

    }

    public void dismiss() {
        if (!popWindow.isShowing())
            return;
        popWindow.dismiss();
    }

    /**
     * 设置显示的类型
     *
     * @param type
     */
    public void setShowType(int type) {
        switch (type) {
            case CMD_TYPE:
                drawGV.setVisibility(View.VISIBLE);
                strokeGV.setVisibility(View.GONE);
                break;
            case STROKE_TYPE:
                strokeGV.setVisibility(View.VISIBLE);
                drawGV.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 面板开关
     */
    @SuppressLint("WrongConstant")
    public void show(View view) {
        if (popWindow.isShowing()) {
            dismiss();
        } else {
            popWindow.showAtAnchorView(view, VerticalGravity.ALIGN_BOTTOM, HorizontalGravity.LEFT, 0, 0);
        }
    }
}
