package com.android.jwjy.wtjyproduct.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
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
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;


/**
 * Created by ccy on 2018/6/11 14:52.
 */

public class InputMsgDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Button sendMessgeBtn;
    private EditText messageEdit;
    private InputMethodManager imm;
    private OnSendMessageListener onSendMessageListener;
    private static final int what = 1;
    private  View parentView;
    public InputMsgDialog(final Context context) {
        super(context, R.style.InputDialog);
        mContext = context;
        setContentView(R.layout.dialog_oto_input_text);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        parentView = findViewById(R.id.parent_rl);
        messageEdit = (EditText) findViewById(R.id.edtInput);
        sendMessgeBtn = (Button) findViewById(R.id.send_message_btn);
        sendMessgeBtn.setOnClickListener(this);
        findViewById(R.id.parent_rl).setOnClickListener(this);
        findViewById(R.id.soft_down_iv).setOnClickListener(this);
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

    private String message() {
        if (messageEdit == null) {
            return "";
        }
        return messageEdit.getText().toString().trim();
    }

    @Override
    public void show() {
        super.show();
        setWidthMatch();
        mhandler.sendEmptyMessageDelayed(what, 50);
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
        mhandler.removeMessages(what, null);

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
            case R.id.parent_rl:
                allDismiss();
                break;
            case R.id.soft_down_iv:
                hideSoft();
                break;
        }

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
    }

    private android.os.Handler mhandler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            showSoft();
            return false;
        }
    });

    private void allDismiss() {
        hideSoft();
        dismiss();
    }

    /**
     * 发送聊天信息
     */
    private void sendMsg() {
        String msg = message();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(mContext, "消息不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (onSendMessageListener != null) {
            onSendMessageListener.onSendMsg(msg);
        }
        messageEdit.setText("");
        allDismiss();
    }

    public void setMessageEdit(String msg) {
        if (TextUtils.isEmpty(msg) || messageEdit == null) {
            return;
        }
        messageEdit.setText(msg);
        Editable etext = messageEdit.getText();
        Selection.setSelection(etext, etext.length());
    }

    /**
     * 聊天数据监听
     */
    public interface OnSendMessageListener {
        void onSendMsg(String msg);

        void onMsg(String msg);
    }
}
