package com.android.jwjy.wtjyproduct.manager;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.cpiz.android.bubbleview.BubbleLinearLayout;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.OTOColorAdapter;
import com.android.jwjy.wtjyproduct.adapter.OTODrawAdapter;
import com.android.jwjy.wtjyproduct.adapter.OTOStokeAdapter;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.view.GridSpacingItemDecoration;
import com.talkfun.whiteboard.config.DrawType;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import java.util.Arrays;

/**
 * 弹框面板管理
 * Created by ccy on 2019/5/8/18:11
 */
public class RTCPanelManager {
    private final BubbleLinearLayout rootView;
    private Context context;
    //    private LiveOneToMultiViewModel liveOneToOneViewModel;
    private PopView mPopView;
    private RecyclerView colorRV;
    private RecyclerView stokeRV;
    private RecyclerView drawRV;
    private MutableLiveData drawLiveData = new MutableLiveData<Integer>();
    /**
     * 颜色值
     */
    private String[] mColorValueArr = {
            "#FC2F04", "#FF6F00", "#FBD504", "#94CE44", "#01D7CF", "#0096F9",
            "#8D6CC5", "#FC9D9B", "#FFFFFF", "#9B9B9B", "#4C5464", "#000000"
    };
    private Integer[] mDrawValue = {
            DrawType.DRAW_PATH_MODE,
            DrawType.DRAW_LINE_MODE,
            DrawType.DRAW_DOTTED_LINE_MODE,
            DrawType.DRAW_RECTANGLE_MODE,
            DrawType.DRAW_OVAL_MODE
    };
    private Integer[] mDrawValueArr = {
            R.mipmap.pop_panel_draw,
            R.mipmap.pop_panel_line,
            R.mipmap.pop_panel_dottedline,
            R.mipmap.pop_panel_square,
            R.mipmap.pop_panel_circle
    };
    private Integer[] mDrawValueArrSelect = {
            R.mipmap.pop_panel_draw_select,
            R.mipmap.pop_panel_line_select,
            R.mipmap.pop_panel_dottedline_select,
            R.mipmap.pop_panel_square_select,
            R.mipmap.pop_panel_circle_select};
    private Float[] mStokeValueArr = {
            0.5f, 0.6f, 0.8f, 0.9f, 1f
    };
    private Integer[] mStokeValueForSDK = {5, 7, 9, 11, 13};
    private int lastSelectDrawType = DrawType.DRAW_PATH_MODE;
    private OnDrawCmdListener onDrawCmdListener;

    public RTCPanelManager(Context context, boolean isIpad) {
        this.context = context;
        rootView = (BubbleLinearLayout) View.inflate(context, R.layout.pop_draw_panel_otm, null);
        int width = isIpad ? getDimension(R.dimen.dp_150) : getDimension(
                R.dimen.dp_240);
        int height = isIpad ? getDimension(R.dimen.dp_115) : ViewGroup.LayoutParams.WRAP_CONTENT;
        mPopView = new PopView(context).setContentView(rootView).setWidth(width).setHeight(height).setFocusable(true).setFocusAndOutsideEnable(true).createPopup();
        rootView.setArrowDirection(BubbleStyle.ArrowDirection.Down);
        colorRV = rootView.findViewById(R.id.color_rv);
        stokeRV = rootView.findViewById(R.id.stoke_rv);
        drawRV = rootView.findViewById(R.id.draw_rv);
        setAdapter();
        initDefault();
    }

    public RTCPanelManager(Context context, int width, int height) {
        this.context = context;
        rootView = (BubbleLinearLayout) View.inflate(context, R.layout.pop_draw_panel_otm, null);
        mPopView = new PopView(context).setContentView(rootView).setWidth(width).setHeight(height).setFocusable(true).setFocusAndOutsideEnable(true).createPopup();
        colorRV = rootView.findViewById(R.id.color_rv);
        stokeRV = rootView.findViewById(R.id.stoke_rv);
        drawRV = rootView.findViewById(R.id.draw_rv);
        setAdapter();
        initDefault();
    }

    public void setArrowDirection(BubbleStyle.ArrowDirection arrowDirection) {
        rootView.setArrowDirection(arrowDirection);
    }

    public int getLastSelectDrawType() {
        return lastSelectDrawType;
    }

    private void setAdapter() {
        drawAdapter();
        colorAdapter();
        stokeAdapter();
    }

    private void stokeAdapter() {
        stokeRV.setLayoutManager(new GridLayoutManager(context, 5));
        OTOStokeAdapter adapter = new OTOStokeAdapter();
        adapter.setDataList(Arrays.asList(mStokeValueArr));
        adapter.setOnItemClickListener(new BaseDatabindingAdapter.OnItemClickListener<Float>() {
            @Override
            public void onItemClick(Float data, int position) {
                if (onDrawCmdListener == null) {
                    return;
                }
                onDrawCmdListener.onStrokeWidth(mStokeValueForSDK[position]);
            }
        });
        stokeRV.setAdapter(adapter);
    }

    private void colorAdapter() {
        colorRV.setLayoutManager(new GridLayoutManager(context, 6));
        colorRV.addItemDecoration(new GridSpacingItemDecoration(6, (int) getDimension(R.dimen.dp_6), false));
        final OTOColorAdapter OTOColorAdapter = new OTOColorAdapter();
        OTOColorAdapter.setDataList(Arrays.asList(mColorValueArr));
        OTOColorAdapter.setOnItemClickListener(new BaseDatabindingAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(String data, int position) {
                if (onDrawCmdListener == null) {
                    return;
                }
                onDrawCmdListener.onPaintColor(Color.parseColor(data));
            }
        });
        colorRV.setAdapter(OTOColorAdapter);
    }

    private void drawAdapter() {
        drawRV.setLayoutManager(new GridLayoutManager(context, 5));
        final OTODrawAdapter drawAdapter = new OTODrawAdapter();
        drawAdapter.setDataList(Arrays.asList(mDrawValueArr));
        drawAdapter.setOnItemClickListener(new BaseDatabindingAdapter.OnItemClickListener<Integer>() {
            @Override
            public void onItemClick(Integer data, int position) {
                lastSelectDrawType = mDrawValue[position];
                if (onDrawCmdListener != null) {
                    onDrawCmdListener.onDrawType(lastSelectDrawType);
                }
                drawLiveData.setValue(mDrawValueArrSelect[position]);
            }
        });
        drawRV.setAdapter(drawAdapter);
    }

    private void initDefault() {
        if (drawLiveData != null) {
            drawLiveData.setValue(R.mipmap.live_one_to_one_panel_draw_select);
        }
    }

    public void setOnDrawCmdListener(OnDrawCmdListener onDrawCmdListener) {
        this.onDrawCmdListener = onDrawCmdListener;
    }

    public MutableLiveData<Integer> getDrawLiveData() {
        return drawLiveData;
    }

    public void show(View view, @VerticalGravity int vertGravity, @HorizontalGravity int horizGravity, int offX, int offY) {
        if (mPopView == null) {
            return;
        }
        if (mPopView.isShowing()) {
            mPopView.dismiss();
            return;
        }
        mPopView.showAtAnchorView(view, vertGravity, horizGravity, offX, offY);
    }

    public boolean isShowing() {
        return mPopView == null ? false : mPopView.isShowing();
    }

    public void dismiss() {
        if (mPopView != null) {
            mPopView.dismiss();
        }
    }

    private int getDimension(int id) {
        return (int) context.getResources().getDimension(id);
    }

    public interface OnDrawCmdListener {
        /**
         * 设置画笔颜色
         *
         * @param color
         */
        void onPaintColor(@ColorInt int color);

        /**
         * 设置画笔大小
         *
         * @param size
         */
        void onStrokeWidth(int size);

        /**
         * 设置涂鸦类型
         *
         * @return
         */
        void onDrawType(int type);
    }

    public void release() {
        drawLiveData = null;
    }
}
