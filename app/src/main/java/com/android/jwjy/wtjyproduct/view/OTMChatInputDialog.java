package com.android.jwjy.wtjyproduct.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.BR;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.entity.ExpressionEntity;
import com.android.jwjy.wtjyproduct.event.OnExpressionListener;
import com.android.jwjy.wtjyproduct.util.ExpressionUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 1V16 聊天输入 弹框
 * Created by ccy on 2018/6/11 14:52.
 */

public class OTMChatInputDialog extends Dialog implements View.OnClickListener, OnExpressionListener, BaseDatabindingAdapter.OnItemClickListener<String> {
    private final TextView usefulExpressionTV;
    private final View parentView;
    private ImageView popInputIV;
    private ImageView mEmotionIV;
    private ExpressionLayout mEmotionLayout;
    private Button sendMessgeBtn;
    private EditText messageEdit;
    private InputMethodManager imm;
    private OnSendMessageListener onSendMessageListener;
    private static final int SHOW_SOFTKEYBOARD = 1;
    private static final int SHOW_USEFUL_EXPRESSION = 2;
    private static final int SHOW_EMOTION = 3;
    private static final int HIDE_EMOTION_AND_USEFUL_EXPRESSION = 4;
    private boolean isShowSoft;
    /**
     * 常用语
     */
    private RecyclerView usefulExpressionRV;
    private Adapter adapter;

    public OTMChatInputDialog(final Context context) {
        super(context, R.style.InputDialog);
        setContentView(R.layout.dialog_otm_input_text);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        parentView = findViewById(R.id.parent_rl);
        messageEdit = (EditText) this.findViewById(R.id.edtInput);

        messageEdit.setFocusableInTouchMode(true);
        sendMessgeBtn = (Button) this.findViewById(R.id.send_message_btn);
        mEmotionLayout = (ExpressionLayout) this.findViewById(R.id.layout_expression);
        mEmotionIV = (ImageView) this.findViewById(R.id.iv_emoticons);
        usefulExpressionRV = findViewById(R.id.rv_useful_expression);
        usefulExpressionTV = findViewById(R.id.tv_useful_expression);
        popInputIV = findViewById(R.id.iv_pop_input);

        addListener();
    }

    private void addListener() {
        this.findViewById(R.id.ll_button_edit).setOnClickListener(this);
        sendMessgeBtn.setOnClickListener(this);
        mEmotionIV.setOnClickListener(this);
        usefulExpressionTV.setOnClickListener(this);
        parentView.setOnClickListener(this);
        popInputIV.setOnClickListener(this);
        messageEdit.setOnClickListener(this);
        mEmotionLayout.setOnEmotionSelectedListener(this);
        messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessgeBtn.setEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onSendMessageListener != null) {
                    onSendMessageListener.onMsg(s.toString());
                }
            }
        });
    }

    private String getMessage() {
        if (messageEdit == null) {
            return "";
        }
        return messageEdit.getText().toString().trim();
    }

    public void showSoft(boolean b) {
        this.isShowSoft = b;
        showEmotion(!b);
    }

    /**
     * 表情显示
     *
     * @param isShow
     */
    private void showEmotion(boolean isShow) {
        mEmotionLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void show() {
        super.show();
        setWidthMatch();
        messageEdit.setFocusable(isShowSoft);
        mEmotionIV.setSelected(isShowSoft);
        if (isShowSoft) {
            mhandler.sendEmptyMessageDelayed(SHOW_SOFTKEYBOARD, 50);
        }
    }

    boolean isFirst;

    private void setWidthMatch() {
        if (!isFirst) {
            Window window = getWindow();
            // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
            isFirst = true;
        }

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mhandler.removeCallbacksAndMessages(null);

    }

    public void setOnSendMessageListener(OnSendMessageListener onSendMessageListener) {
        this.onSendMessageListener = onSendMessageListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_btn:
                sendMsg();
                break;
            case R.id.edtInput:
                recoverDefault();
                showSoft();
                break;
            case R.id.parent_rl:
                recoverDefault();
                hideSoft();
                super.dismiss();
                break;
            case R.id.tv_useful_expression:
                usefulExpressionTV.setVisibility(View.GONE);
                popInputIV.setVisibility(View.VISIBLE);
                hideSoft();
                mhandler.sendEmptyMessageDelayed(SHOW_USEFUL_EXPRESSION,100);
                break;
            case R.id.iv_pop_input:
                usefulExpressionTV.setVisibility(View.VISIBLE);
                popInputIV.setVisibility(View.GONE);
                mhandler.sendEmptyMessage(HIDE_EMOTION_AND_USEFUL_EXPRESSION);
                showSoft();
                break;
            case R.id.iv_emoticons:
                if (mEmotionIV.isSelected()) {
                    hideSoft();
                    mhandler.sendEmptyMessageDelayed(SHOW_EMOTION,100);
                } else {
                    mhandler.sendEmptyMessage(HIDE_EMOTION_AND_USEFUL_EXPRESSION);
                    showSoft();
                }
                break;
        }
    }

    public void emotionVisibility(int visibility) {
        mEmotionLayout.setVisibility(visibility);
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoft() {
        if (imm != null && messageEdit != null) {
            imm.hideSoftInputFromWindow(messageEdit.getWindowToken(), 0);
        }
    }

    private void showSoft() {
        if (imm != null && messageEdit != null) {
            messageEdit.setFocusable(true);
            messageEdit.setFocusableInTouchMode(true);
            messageEdit.requestFocus();
            imm.showSoftInput(messageEdit, 0);
        }
        mEmotionIV.setSelected(true);
    }

    private android.os.Handler mhandler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_SOFTKEYBOARD: //显示软键盘
                    showSoft();
                    break;
                case SHOW_USEFUL_EXPRESSION: //显示常用语
                    initUsefulExpressionAdapter();
                    mEmotionIV.setSelected(true);
                    emotionVisibility(View.GONE);
                    usefulExpressionRV.setVisibility(View.VISIBLE);
                    break;
                case SHOW_EMOTION: //显示表情
                    usefulExpressionRV.setVisibility(View.GONE);
                    usefulExpressionTV.setVisibility(View.VISIBLE);
                    popInputIV.setVisibility(View.GONE);
                    mEmotionIV.setSelected(false);
                    emotionVisibility(View.VISIBLE);
                    break;
                case HIDE_EMOTION_AND_USEFUL_EXPRESSION://隐藏表情与常用语
                    usefulExpressionRV.setVisibility(View.GONE);
                    mEmotionIV.setSelected(true);
                    emotionVisibility(View.GONE);
                    break;
            }

            return false;
        }
    });

    @Override
    public void dismiss() {
        recoverDefault();
        hideSoft();
        messageEdit.setText("");
        super.dismiss();
    }

    private void recoverDefault() {
        emotionVisibility(View.GONE);
        usefulExpressionRV.setVisibility(View.GONE);
        usefulExpressionTV.setVisibility(View.VISIBLE);
        popInputIV.setVisibility(View.GONE);
    }

    /**
     * 发送聊天信息
     */
    private void sendMsg() {
        String msg = getMessage();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(getContext(), "消息不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (onSendMessageListener != null) {
            onSendMessageListener.onSendMsg(msg);
        }
    }

    public void setMessageEdit(String msg) {
        if (messageEdit == null) {
            return;
        }
        messageEdit.setText(ExpressionUtil.getExpressionString(getContext(), msg, "mipmap"));
        Editable etext = messageEdit.getText();
        Selection.setSelection(etext, etext.length());
    }

    @Override
    public void OnExpressionSelected(ExpressionEntity entity) {
        if (entity == null) return;
        //添加表情
        String content = getMessage();
        content += entity.character;
        messageEdit.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
        messageEdit.setSelection(content.length());
    }

    @Override
    public void OnExpressionRemove() {
        if (messageEdit == null) {
            return;
        }
        String content = getMessage();
        if (!TextUtils.isEmpty(content)) {
            int selectionStart = messageEdit.getSelectionStart();
            content = messageEdit.getText().delete(selectionStart - ExpressionUtil.dealContent(getContext(), content, selectionStart), selectionStart).toString();
            messageEdit.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
            messageEdit.setSelection(content.length());
        }
    }

    @Override
    public void onItemClick(String data, int position) {
        if (onSendMessageListener != null & data != null) {
            onSendMessageListener.onSendMsg(data);
        }
    }

    /**
     * 聊天数据监听
     */
    public interface OnSendMessageListener {
        void onSendMsg(String msg);

        void onMsg(String msg);
    }

    private void initUsefulExpressionAdapter() {
        if (adapter == null) {
            List<String> expression = new ArrayList<>();
            expression.add("你能看到我画面吗?");
            expression.add("你能听到我声音吗?");
            expression.add("我听不到你的声音。");
            expression.add("我看不到你的画面。");
            adapter = new Adapter();
            adapter.setDataList(expression);
            adapter.setOnItemClickListener(this);
            usefulExpressionRV.setLayoutManager(new FlowLayoutManager());
            usefulExpressionRV.addItemDecoration(new SpaceItemDecoration((int) getContext().getResources().getDimension(R.dimen.dp_5)));
            usefulExpressionRV.setAdapter(adapter);
        }
    }

    public static class Adapter extends BaseDatabindingAdapter<String> {

        @Override
        protected int getLayoutId() {
            return R.layout.item_pop_useful_expressions;
        }

        @Override
        protected int getVariableId() {
            return BR.tips;
        }
    }
}
