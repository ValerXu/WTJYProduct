package com.android.jwjy.wtjyproduct.manager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.OTMChatAdapter;
import com.android.jwjy.wtjyproduct.util.ExpressionUtil;
import com.android.jwjy.wtjyproduct.util.ToastUtil;
import com.android.jwjy.wtjyproduct.view.OTMChatInputDialog;
import com.android.jwjy.wtjyproduct.view.RecycleViewDivider;
import com.android.jwjy.wtjyproduct.viewmodel.LiveOneToMultiViewModel;
import com.talkfun.sdk.event.Callback;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import org.json.JSONObject;

/**
 * 1V16聊天
 * Created by ccy on 2019/5/20/15:56
 */
public class OTMChatManager implements View.OnClickListener, OTMChatInputDialog.OnSendMessageListener {
    private final View rootView;
    private int height;
    private int width;
    private LiveOneToMultiViewModel liveOneToMultiViewModel;
    private PopView mPopView;
    private RecyclerView chatRV;
    private OTMChatAdapter mLiveRtcChatAdapter;
    private Context context;
    private OTMChatInputDialog msgDialog;
    private TextView mMsgTV;
    private TextView mSendTV;
    private int lastHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private boolean isChange;
//    public OTMChatManager(Context context, LiveOneToMultiViewModel liveOneToMultiViewModel) {
//        this(context, liveOneToMultiViewModel, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }

    /**
     * @param context
     * @param liveOneToMultiViewModel
     * @param width
     * @param height
     * @param isIPad
     */
    public OTMChatManager(Context context, LiveOneToMultiViewModel liveOneToMultiViewModel, int width, int height, boolean isIPad) {
        this.context = context;
        this.liveOneToMultiViewModel = liveOneToMultiViewModel;
        rootView = View.inflate(context, R.layout.pop_live_otm_chat, null);
        this.width = width;
        this.height = lastHeight = height;
        chatRV = rootView.findViewById(R.id.chat_layout);
        View chatParentLL = rootView.findViewById(R.id.ll_chat_parent);
        chatParentLL.setVisibility(isIPad ? View.GONE : View.VISIBLE);
        mMsgTV = rootView.findViewById(R.id.tv_msg);
        mSendTV = rootView.findViewById(R.id.btn_send);
        chatRV.setLayoutManager(new LinearLayoutManager(context));
        mSendTV.setOnClickListener(this);
        mMsgTV.setOnClickListener(this);
        rootView.findViewById(R.id.iv_emoticon).setOnClickListener(this);
        setAdapter();

    }

    public void setHeight(int height) {
        if (lastHeight == height) {
            isChange = false;
            return;
        }
        isChange = true;
        this.lastHeight = height;
    }

    private void setAdapter() {
        mLiveRtcChatAdapter = new OTMChatAdapter();
        chatRV.setAdapter(mLiveRtcChatAdapter);
        RecyclerView.ItemDecoration itemDecoration = new RecycleViewDivider.Build().orientation(LinearLayout.VERTICAL).
                context(context).dividerColor(Color.parseColor(
                "#293039")).dividerHeight(getDimension(R.dimen.dp_0_5)).rightOffset(getDimension(R.dimen.dp_5)).leftOffset(getDimension(R.dimen.dp_5)).create();
        chatRV.addItemDecoration(itemDecoration);
    }

    public void receiveChatEntity(Object value) {
        mLiveRtcChatAdapter.addData(value);
        if (mLiveRtcChatAdapter.getItemCount() >= 100) {
            mLiveRtcChatAdapter.getDataList().remove(0);
        }
        mLiveRtcChatAdapter.notifyDataSetChanged();
        chatRV.scrollToPosition(mLiveRtcChatAdapter.getItemCount() - 1);
    }

    public void receiveAwardEntity(Object value) {
        receiveChatEntity(value);
    }

    public void show(@NonNull View anchor, @VerticalGravity int vertGravity, @HorizontalGravity int horizGravity) {
        initPopView();
        if (mPopView.isShowing()) {
            mPopView.dismiss();
            return;
        }
        mPopView.showAtAnchorView(anchor, vertGravity, horizGravity);
    }

    public void dismiss() {
        if (mPopView == null || !mPopView.isShowing()) {
            return;
        }
        mPopView.dismiss();

    }

    private void initPopView() {
        if (mPopView == null || isChange) {
            mPopView = new PopView(context).setContentView(rootView).setWidth(width).setHeight(lastHeight)
                    .setFocusable(true).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                    .setFocusAndOutsideEnable(true).setAnimationStyle(R.style.pop_enter_exit_anim).createPopup();
        }

    }

    public boolean isShowing() {
        return mPopView == null ? false : mPopView.isShowing();
    }

    private int getDimension(int id) {
        return (int) context.getResources().getDimension(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_msg:
                initMsgDialog();
                showDialog(true);
                break;
            case R.id.iv_emoticon:
                initMsgDialog();
                showDialog(false);
                break;
            case R.id.btn_send:
                if (TextUtils.isEmpty(mMsgTV.getText())) {
                    return;
                }
                sendMsg(mMsgTV.getText().toString());
                break;
        }
    }

    private void initMsgDialog() {
        if (msgDialog == null) {
            msgDialog = new OTMChatInputDialog(context);
            msgDialog.setOnSendMessageListener(this);
            msgDialog.setCancelable(true);
            msgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void showDialog(boolean showSoft) {
        msgDialog.showSoft(showSoft);
        msgDialog.setMessageEdit(mMsgTV.getText().toString());
        msgDialog.show();
    }

    /**
     * 发送聊天
     *
     * @param msg
     */
    private void sendMsg(String msg) {
        liveOneToMultiViewModel.sendMessage(msg, new Callback<JSONObject>() {
            @Override
            public void success(JSONObject result) {
                resetMsgTextAndSendBtn();
            }

            @Override
            public void failed(String failed) {
                ToastUtil.show(context, failed, Toast.LENGTH_SHORT);
            }
        });
    }

    public boolean isShow() {
        return mPopView == null ? false : mPopView.isShowing();
    }

    private void resetMsgTextAndSendBtn() {
        mMsgTV.setText("");
        msgDialog.setMessageEdit("");
    }

    @Override
    public void onSendMsg(String msg) {
        sendMsg(msg);
        if (msgDialog != null) {
            msgDialog.dismiss();
        }
    }

    @Override
    public void onMsg(String msg) {
        mMsgTV.setText(ExpressionUtil.getExpressionString(context, msg, "mipmap"));
    }
}
