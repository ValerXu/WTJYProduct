package com.android.jwjy.wtjyproduct.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;


/**
 * Created by zjp on 2018/6/11 14:52.
 */

public class InputTextMsgDialog extends Dialog {

    private Context mContext;
    private Button sendMessgeBtn;
    private EditText messageEdit;
    private InputMethodManager imm;

    private int mLastDiff = 0;
    private OnTextSendListener onTextSendListener;

    public InputTextMsgDialog(final Context context, int theme) {
        super(context, theme);
        mContext = context;
        setContentView(R.layout.dialog_input_text);
        final RelativeLayout rldlgview = (RelativeLayout) findViewById(R.id.rl_outside_view);
        messageEdit = (EditText) findViewById(R.id.edit_chat);
        messageEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        //修改下划线颜色
        messageEdit.getBackground().setColorFilter(context.getResources().getColor(R.color.transparent), PorterDuff.Mode.CLEAR);
        sendMessgeBtn = (Button) findViewById(R.id.send_message_btn);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        sendMessgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    if (onTextSendListener != null) {
                        onTextSendListener.onChatMessageSend(msg);
                    }
                    messageEdit.setText("");
                    dismiss();
                    imm.hideSoftInputFromWindow(messageEdit.getWindowToken(), 0);

                } else {
                    Toast.makeText(mContext, "消息不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });

        messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sendMessgeBtn.setBackgroundColor(mContext.getResources().getColor(R.color.activity_small_scheme));
                } else {
                    sendMessgeBtn.setBackgroundColor(mContext.getResources().getColor(R.color.panel_cmd_bg));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onTextSendListener != null) {
                    onTextSendListener.onChatMessage(s.toString());
                }
            }
        });
        rldlgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(messageEdit.getWindowToken(), 0);
                dismiss();
            }
        });
    }

    public String message() {
        if (messageEdit == null) {
            return "";
        }
        return messageEdit.getText().toString().trim();
    }

    public void clearMsg() {
        if (messageEdit == null) {
            return;
        }
        messageEdit.setText("");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0;
    }

    @Override
    public void show() {
        super.show();
    }

    public void setOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.onTextSendListener = onTextSendListener;
    }

    public interface OnTextSendListener {
        void onChatMessageSend(String msg);

        void onChatMessage(String msg);
    }
}
