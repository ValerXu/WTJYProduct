package com.android.jwjy.wtjyproduct;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;
/**
 * 自定义对话框的控件（回复或者发布时使用的带输入框和图片选择的对话框）
 * Created by dayuer on 2019/12/4.
 */
public class ControllerCustomDialog extends Dialog {
    private EditText et_input;  //输入框
    private ImageView course_question_respond_layout_commit_button ; //发布按钮
    private ControlMainActivity mControlMainActivity;
    private String mContent = ""; //框中显示内容
    private boolean mIsUseImage = false;  //是否使用图片
    private OnClickPublishOrImage mOnClickPublishOrImage = null;
    public ControllerCustomDialog(@NonNull Context context) {
        this(context, 0,"",false);
    }

    public ControllerCustomDialog(@NonNull Context context, int themeResId,String content,boolean isUseImage) {
        super(context, themeResId);
        mControlMainActivity = (ControlMainActivity) context;
        if (content != null){
            mContent = content;
        }
        mIsUseImage = isUseImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modelcoursedetails_question_respond);
        setCanceledOnTouchOutside(true);  //点击对话框以外的地方要关闭对话框
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        et_input = findViewById(R.id.course_question_respond_layout_edittext);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView course_question_respond_layout_commit_button = findViewById(R.id.course_question_respond_layout_commit_button);
                if (!s.toString().equals("")){ //如果有输入内容，将发布按钮置为可用状态
                    course_question_respond_layout_commit_button.setBackgroundResource(R.drawable.button_publish_blue);
                } else {
                    course_question_respond_layout_commit_button.setBackgroundResource(R.drawable.button_publish_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_input.setHint(mContent);
        //打开软键盘
        openKeyBoard(et_input);
        //点击发布按钮
        course_question_respond_layout_commit_button = findViewById(R.id.course_question_respond_layout_commit_button);
        course_question_respond_layout_commit_button.setOnClickListener(v->{
            if (et_input.getText().toString().equals("")){
                Toast.makeText(mControlMainActivity,"输入内容不允许为空！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (mOnClickPublishOrImage != null && !et_input.getText().toString().equals("")){{ //输入框必须有内容才能发布
                mOnClickPublishOrImage.publish(et_input.getText().toString());
            }}
        });
        //是否启用图片功能
        if (mIsUseImage){
            ImageView course_question_respond_layout_image_button = findViewById(R.id.course_question_respond_layout_image_button);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_question_respond_layout_image_button.getLayoutParams();
            ll.width = (int) this.getContext().getResources().getDimension(R.dimen.dp25);
            ll.rightMargin = (int) this.getContext().getResources().getDimension(R.dimen.dp13);
            course_question_respond_layout_image_button.setLayoutParams(ll);
            course_question_respond_layout_image_button.setOnClickListener(v->{
                if (mOnClickPublishOrImage != null){
                    mOnClickPublishOrImage.image();
                }
            });
        }
    }

    @Override
    public void dismiss() {
        hideInput();
        super.dismiss();
    }

    /**
     * 弹起软键盘
     * @param editText
     */
    public void openKeyBoard(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                                mControlMainActivity.getApplication()
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText,0);
                editText.setSelection(editText.getText().length());
            }
        },200);
    }
    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) mControlMainActivity.getSystemService(INPUT_METHOD_SERVICE);
        View v = mControlMainActivity.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    public void setOnClickPublishOrImagelistener(OnClickPublishOrImage onClickPublishOrImage){
        mOnClickPublishOrImage = onClickPublishOrImage;
    }

    interface OnClickPublishOrImage{
        void publish(String content);
        void image();
    }

    public void setImage(Drawable drawable, float width, float height){
        ImageView course_question_respond_layout_image_button = findViewById(R.id.course_question_respond_layout_image_button);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_question_respond_layout_image_button.getLayoutParams();
        if (width >= 0) {
            ll.width = (int) width;
        }
        if (height >= 0) {
            ll.height = (int) height;
        }
        course_question_respond_layout_image_button.setLayoutParams(ll);
        course_question_respond_layout_image_button.setBackground(drawable);
    }
}
