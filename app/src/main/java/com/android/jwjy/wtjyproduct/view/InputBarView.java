package com.android.jwjy.wtjyproduct.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.entity.ExpressionEntity;
import com.android.jwjy.wtjyproduct.event.OnExpressionListener;
import com.android.jwjy.wtjyproduct.event.OnSendFlowerListener;
import com.android.jwjy.wtjyproduct.event.OnSendMessageListener;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.ExpressionUtil;
import com.android.jwjy.wtjyproduct.util.KeyBoardUtils;
import com.android.jwjy.wtjyproduct.util.ScreenSwitchUtils;
import com.android.jwjy.wtjyproduct.util.SoftKeyboardStateWatcher;

/**
 * Created by Wallace on 2017/2/9.
 */
public class InputBarView extends LinearLayout implements OnExpressionListener, TextWatcher, View.OnClickListener,SoftKeyboardStateWatcher
        .SoftKeyboardStateListener {
    private EditText inputEdt;
    private TextView sendBtn;
    private ImageView expressionBtn;
    private RelativeLayout sendFlower;
    private TextView flowerNumTv;
    private ImageView flowerBtn;
    private ViewGroup.LayoutParams inputParams;
    private String sendContent;
    private int flowerNum = 0;
    private Handler handler;
    private int pptWidth = 0;
    private boolean canInput = true;
    private boolean canSendFlower = true;
    private OnSendMessageListener sendMessageListener;
    private OnSendFlowerListener sendFlowerListener;
    private ExpressionLayout expressionLayout;

    SoftKeyboardStateWatcher softKeyboardStateWatcher;

    public InputBarView(Context context) {
        this(context, null);
    }

    public InputBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new Handler();
        initView();
        initEvent();
    }

    public void initView() {
        View view = View.inflate(getContext(), R.layout.edt, null);
        inputEdt = (EditText) view.findViewById(R.id.edt_input);
        sendBtn = (TextView) view.findViewById(R.id.send_btn);
        expressionBtn = (ImageView) view.findViewById(R.id.iv_expression);
        sendFlower = (RelativeLayout) view.findViewById(R.id.btn_send);
        flowerNumTv = (TextView) view.findViewById(R.id.flower_num);
        flowerBtn = (ImageView) view.findViewById(R.id.flower_btn);
        expressionLayout = (ExpressionLayout) view.findViewById(R.id.layout_expression);
        softKeyboardStateWatcher = new SoftKeyboardStateWatcher(inputEdt.getRootView());
        this.addView(view);
    }

    public void updateInputBarWidth(int width) {
        pptWidth = width;
    }




    public void initEvent() {
        sendBtn.setOnClickListener(this);
        expressionBtn.setOnClickListener(this);
        inputEdt.setOnClickListener(this);
        sendFlower.setOnClickListener(this);
        inputEdt.addTextChangedListener(this);
        expressionLayout.setOnEmotionSelectedListener(this);

    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(getVisibility() != VISIBLE){
            KeyBoardUtils.closeKeybord(inputEdt,getContext());
            expressionLayout.setVisibility(GONE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        softKeyboardStateWatcher.addSoftKeyboardStateListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        softKeyboardStateWatcher.removeSoftKeyboardStateListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                if (!canInput)
                    return;
                sendContent = inputEdt.getText().toString().trim();
                if (ScreenSwitchUtils.getInstance(getContext()).isSensorSwitchLandScreen()) {
                    switchInputAreaLength(false);
                }
                sendMessage(sendContent);
                break;
            case R.id.iv_expression:
                if (!canInput) //禁言模式
                    return;
                showOrCloseExpressionayout();

                break;
            case R.id.btn_send:
                if (!canInput) //禁言模式
                    return;
                sendFlower();
                break;
        }
    }

    //切换全体禁言状态 1 为禁言，0为恢复
    public void setCanInput(boolean value) {
        canInput = value;
        // if (status == 1 && currentItem == 0) {
        if (!canInput) {
            inputEdt.setHint(getResources().getString(R.string.shutUp_input_tip));
            inputEdt.setEnabled(false);
            inputEdt.setMaxLines(1);
            this.setAlpha(0.5f);
        } else {
            inputEdt.setHint(getResources().getString(R.string.input_your_text));
            inputEdt.setEnabled(true);
            inputEdt.setMaxLines(10);
            this.setAlpha(1.0f);
        }
    }

    /**
     * 是否支持输入表情
     *
     * @param enable
     */
    public void setInputExpressionEnable(boolean enable) {
        expressionBtn.setVisibility(enable ? View.VISIBLE : View.GONE);//聊天
        expressionLayout.setVisibility(View.GONE);
    }

    /**
     * 是否支持送花
     *
     * @param enable
     */
    public void setSendFlowerEnable(boolean enable) {
        canSendFlower = enable;
        setSendStatus();
    }

    /**
     * 重置
     */
    public void reset() {
        inputEdt.setText("");
       expressionLayout.setVisibility(GONE);
    }

    //-----------------------------------------发送消息和表情---------------------------------------

    /**
     * 是否显示非全屏表情
     */
    public void showOrCloseExpressionayout() {

       if(expressionLayout.getVisibility() != VISIBLE){
           KeyBoardUtils.closeKeybord(inputEdt,getContext());

           expressionLayout.postDelayed(new Runnable() {
               @Override
               public void run() {
                   expressionLayout.setVisibility(VISIBLE);
               }
           },100);
       }else if(expressionLayout.getVisibility() != GONE){
           expressionLayout.setVisibility(GONE);
           KeyBoardUtils.openKeybord(inputEdt,getContext());
       }
    }


    /**
     * 变换全屏输入框的宽度
     *
     * @param isLong
     */
    public void switchInputAreaLength(boolean isLong) {
        inputParams = (ViewGroup.LayoutParams) this.getLayoutParams();
        if (isLong) {
            inputParams.width = DimensionUtils.getScreenWidth(getContext());
        } else {
            inputParams.width = DimensionUtils.getScreenWidth(getContext()) - pptWidth;
        }
        this.setLayoutParams(inputParams);
    }


    /**
     * 设置是否为发送状态
     */
    private void setSendStatus() {
        if (canSendFlower && TextUtils.isEmpty(inputEdt.getText().toString())) {
            flowerNumTv.setText(flowerNum + "");
            sendBtn.setVisibility(View.INVISIBLE);
            flowerBtn.setVisibility(View.VISIBLE);
            flowerNumTv.setVisibility(View.VISIBLE);
        } else {
            sendBtn.setVisibility(View.VISIBLE);
            flowerBtn.setVisibility(View.INVISIBLE);
            flowerNumTv.setVisibility(View.INVISIBLE);
            sendBtn.setText("发送");
        }
    }


    public void sendMessage(final String content) {
        if (!TextUtils.isEmpty(content)) {
            if (sendMessageListener != null) {
                sendMessageListener.onSendMessage(content);
            }
            inputEdt.setText("");

            KeyBoardUtils.closeKeybord(inputEdt,getContext());
        }
    }

    @Override
    public void OnExpressionSelected(ExpressionEntity entity) {
        if (entity == null) return;
        //添加表情
        String content = inputEdt.getText().toString();
        content += entity.character;
        inputEdt.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
        inputEdt.setSelection(content.length());
    }

    @Override
    public void OnExpressionRemove() {
        String content = inputEdt.getText().toString();
        if (!TextUtils.isEmpty(content)) {//非全屏删除
            int selectionStart = inputEdt.getSelectionStart();
            content = inputEdt.getText().delete(selectionStart - ExpressionUtil.dealContent(getContext(), content, selectionStart), selectionStart).toString();
            inputEdt.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
            inputEdt.setSelection(content.length());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        setSendStatus();
    }

    //-----------------------------------------发送花朵----------------------------------------------

    /**
     * 发送鲜花
     */
    private void sendFlower() {
        if (sendFlowerListener != null) {
            sendFlowerListener.onSendFlower();
        }
    }

    /**
     * 设置花的数目
     */
    public void setFlowerNum(int num) {
        flowerNum = num;
        flowerBtn.setSelected(num == 0);
        if (flowerNumTv != null)
            flowerNumTv.setText(flowerNum + "");
    }

    public void setOnSendMessageListener(OnSendMessageListener listener) {
        this.sendMessageListener = listener;
    }

    public void setOnSendFlowerListener(OnSendFlowerListener listener) {
        this.sendFlowerListener = listener;
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        if(getVisibility() != VISIBLE)
            return;
        if(expressionLayout.getVisibility() == VISIBLE)
            expressionLayout.setVisibility(GONE);
    }

    @Override
    public void onSoftKeyboardClosed() {
        if(getVisibility() != VISIBLE)
            return;

    }
}
