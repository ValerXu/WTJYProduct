package com.android.jwjy.wtjyproduct.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.RtcChatAdapter;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.view.InputTextMsgDialog;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.widget.PopView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ccy on 2018/9/8/11:52
 */
public class RtcChatPopManager implements InputTextMsgDialog.OnTextSendListener {
    private Context mContext;
    private PopView mChatPopView;
    @BindView(R.id.small_room_chat_lv)
    ListView mChatLV;
    @BindView(R.id.chat_message_edit)
    TextView mChatMessageTV;
    @BindView(R.id.send_message_btn)
    Button sendMessageBtn;
    private RtcChatAdapter chatAdapter;
    private int popWidth;
    private int popHeight;
    private InputTextMsgDialog inputTextMsgDialog;


    public RtcChatPopManager(Context context) {
        if (context == null) {
            return;
        }
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        mContext = contextWeakReference.get();
        popWidth = (int) (DimensionUtils.getScreenWidth(mContext) * 0.34);
        popHeight = (int) (DimensionUtils.getScreenHeight(mContext) * 0.75);
        initView();
        initListener();
    }

    private void initListener() {
//        mChatMessageTV.setOnClickListener(this);
    }

    public void initView() {
        View rootView = View.inflate(mContext, R.layout.adapter_small_room_chat, null);
        mChatPopView = new PopView(mContext).setContentView(rootView, popWidth, popHeight).setFocusable(true).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                /*.setOutsideTouchable(true)*/.setFocusAndOutsideEnable(true)
                .createPopup();
        ButterKnife.bind(this, rootView);
        setAdapter();
    }


    private void setAdapter() {
        chatAdapter = new RtcChatAdapter(mContext);
        mChatLV.setAdapter(chatAdapter);
    }

    public void receiveMessage(ChatEntity chatEntity) {
        if (chatAdapter != null) {
            chatAdapter.appendList(chatEntity);
        }
        if (mChatLV != null)
            mChatLV.setSelection(chatAdapter.getCount() - 1);
    }


    /**
     * 面板开关
     */
    @SuppressLint("WrongConstant")
    public void show(View view) {
        if (mChatPopView == null) {
            return;
        }
        if (mChatPopView.isShowing()) {
            mChatPopView.dismiss();
        } else {
            mChatPopView.showAtAnchorView(view, VerticalGravity.CENTER, HorizontalGravity.ALIGN_RIGHT, -170, 2);
        }
    }

    public boolean isShow() {
        return mChatPopView != null && mChatPopView.isShowing();
    }

    public void dismiss() {
        if (mChatPopView == null) {
            return;
        }
        if (!mChatPopView.isShowing())
            return;
        mChatPopView.dismiss();
    }

    public void inputTextMsgDialogDismiss() {
        if (inputTextMsgDialog != null) {
            inputTextMsgDialog.dismiss();
        }
    }

    @OnClick({R.id.chat_message_edit, R.id.send_message_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_message_edit://弹出软键盘
                initChatSoftDialog();
                resetFullScreenDialogWH();
                if (inputTextMsgDialog != null) {
                    inputTextMsgDialog.show();
                }
                break;
            case R.id.send_message_btn:
                if (!TextUtils.isEmpty(mChatMessageTV.getText())) {
                    sendMessage(mChatMessageTV.getText().toString());
                    if (inputTextMsgDialog != null) {
                        inputTextMsgDialog.clearMsg();
                    }
                }
                break;
        }


    }

    private void initChatSoftDialog() {
        if (inputTextMsgDialog == null) {
            inputTextMsgDialog = new InputTextMsgDialog(mContext, R.style.InputDialog);
            inputTextMsgDialog.setOnTextSendListener(this);
            inputTextMsgDialog.setCancelable(true);
            inputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

    }

    /**
     * 类似华为手机的虚拟键盘，可能有问题，所以显示的时候重新计算下
     */
    private void resetFullScreenDialogWH() {
        if (inputTextMsgDialog != null && mContext != null) {
            WindowManager windowManager = ((Activity) mContext).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = inputTextMsgDialog.getWindow().getAttributes();
            lp.width = (display.getWidth());
            inputTextMsgDialog.getWindow().setAttributes(lp);
        }

    }

    /**
     * 发送聊天数据
     *
     * @param msg
     */
    @Override
    public void onChatMessageSend(final String msg) {
        sendMessage(msg);
    }

    @Override
    public void onChatMessage(String msg) {
        mChatMessageTV.setText(msg);
        sendMessageBtn.setBackgroundColor(mContext.getResources().getColor(R.color.activity_small_scheme));
    }

    private void sendMessage(String msg) {
        if (!TextUtils.isEmpty(msg) || (HtSdk.getInstance() == null)) {
            HtSdk.getInstance().emit(BroadcastCmdType.CHAT_SEND, msg, new Callback() {
                @Override
                public void success(Object result) {
                    mChatMessageTV.setText("");
                    sendMessageBtn.setBackgroundColor(mContext.getResources().getColor(R.color.send_message_btn_default_bg));
                }

                @Override
                public void failed(String failed) {
                    if (!TextUtils.isEmpty(failed) && mContext != null) {
                        Toast.makeText(mContext, failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
