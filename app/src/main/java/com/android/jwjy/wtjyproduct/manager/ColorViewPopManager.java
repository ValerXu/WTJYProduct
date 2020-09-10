package com.android.jwjy.wtjyproduct.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.ColorAdapter;
import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.util.EventBusUtil;
import com.talkfun.sdk.HtSdk;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;
import com.talkfun.whiteboard.presenter.draw.IWhiteBoardOperator;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ccy on 2018/9/11/16:26
 */
public class ColorViewPopManager {
    private Context mContext;
    private PopView popWindow;
    @BindView(R.id.color_gv)
    GridView colorGV;
    private ColorAdapter mColorAdapter;
    private String[] mColorValueArr = {ColorValue.RED, ColorValue.YELLOW, ColorValue.GREEN, ColorValue.CYAN, ColorValue.PURPLE, ColorValue.BLUE, ColorValue.BLACK, ColorValue.WHITE};
    private IWhiteBoardOperator opetator;
    private int selectPaintColor = Color.parseColor(ColorValue.RED);

    //颜色值
    class ColorValue {
        private static final String BLACK = "#000000";
        private static final String RED = "#FF0000";
        private static final String YELLOW = "#fff440";
        private static final String GREEN = "#69f0ae";
        private static final String CYAN = "#00ffff";
        private static final String BLUE = "#d879e8";
        private static final String PURPLE = "#77a4ff";
        private static final String WHITE = "#ffffff";
    }

    public ColorViewPopManager(Context context) {
        this.mContext = context;
        initView();
    }

    public void initView() {
        View rootView = View.inflate(mContext, R.layout.pop_small_room_color, null);
        popWindow = new PopView(mContext).setContentView(rootView).setFocusable(true).setOutsideTouchable(true).setFocusAndOutsideEnable(true).createPopup();
        ButterKnife.bind(this, rootView);
        setAdapter();
        initData();
        initEvent();
    }

    public void setColor() {
        if (opetator != null) {
            opetator.setPaintColor(selectPaintColor);
        }
    }

    private void initEvent() {
        colorGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPaintColor = Color.parseColor(mColorValueArr[position]);
                opetator.setPaintColor(Color.parseColor(mColorValueArr[position]));
                mColorAdapter.setSelectSingleItem(position);
                EventBusUtil.postEvent(new Event(EventType.SMALL_ROOM_POP_COLOR, Color.parseColor(mColorValueArr[position])));
            }
        });
    }

    private void initData() {
        opetator = HtSdk.getInstance().getWhiteboardOperator();
        opetator.setPaintColor(Color.parseColor(mColorValueArr[0]));
    }

    private void setAdapter() {
        mColorAdapter = new ColorAdapter(mContext);
        colorGV.setAdapter(mColorAdapter);
        List<String> mColorList = Arrays.asList(mColorValueArr);
        mColorAdapter.addItems(mColorList);
    }

    /**
     * 面板开关
     */
    @SuppressLint("WrongConstant")
    public void show(View view) {
        if (popWindow == null) {
            return;
        }
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        } else {
            popWindow.showAtAnchorView(view, VerticalGravity.ALIGN_BOTTOM, HorizontalGravity.LEFT, 0, 0);
        }
    }

    public void dismiss() {
        if (!popWindow.isShowing())
            return;
        popWindow.dismiss();
    }

}
