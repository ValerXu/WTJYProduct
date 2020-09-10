package com.android.jwjy.wtjyproduct;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 登录模块
 */
public class ModelLogIn extends Fragment {
    private static ControlMainActivity mControlMainActivity;
    //要显示的页面
    static private int FragmentPage;
    private View mview ;
    private int height = 1344;
    private int width = 720;
    private boolean mLoginIsOpenEye = false;
    private boolean mForgetPasswordIsOpenEye = false;
    private boolean mRegisterIsOpenEye = false;
    private boolean mRegisterRepeatIsOpenEye = false;
    private CountDownTimer mRegisterSMSCodeCountDownTimer = null;
    private CountDownTimer mLoginSMSCodeCountDownTimer = null;

    public  static Fragment newInstance(ControlMainActivity context,int iFragmentPage){
        mControlMainActivity = context;
        ModelLogIn myFragment = new ModelLogIn();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage,container,false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        LogInMainInit();
        ForgetPasswordMainInit();
        RegisterMainInit();
        LogInMainShow();
        return mview;
    }

    @Override
    public void onDestroy() {
        if (mRegisterSMSCodeCountDownTimer != null){
            mRegisterSMSCodeCountDownTimer.cancel();
            mRegisterSMSCodeCountDownTimer = null;
        }
        if (mLoginSMSCodeCountDownTimer != null){
            mLoginSMSCodeCountDownTimer.cancel();
            mLoginSMSCodeCountDownTimer = null;
        }
        super.onDestroy();
    }

    //展示登录主界面
    public void LogInMainShow(){
        HideAllLayout();
        LinearLayout login_main = mview.findViewById(R.id.login_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_main.getLayoutParams();
        LP.width = FrameLayout.LayoutParams.MATCH_PARENT;
        LP.height = FrameLayout.LayoutParams.MATCH_PARENT;
        login_main.setLayoutParams(LP);
        login_main.setVisibility(View.VISIBLE);
        ScrollView login_mainScrollView = mview.findViewById(R.id.login_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayoutlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        login_mainScrollView.setLayoutParams(LinearLayoutlp);
    }

    //忘记密码？  切换界面
    public void ForgetPasswordShow(){
        HideAllLayout();
        LinearLayout login_forgetpassword_main = mview.findViewById(R.id.login_forgetpassword_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_forgetpassword_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        login_forgetpassword_main.setLayoutParams(LP);
        login_forgetpassword_main.setVisibility(View.VISIBLE);
        ScrollView login_forgetpassword_mainScrollView = mview.findViewById(R.id.login_forgetpassword_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_forgetpassword_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayoutlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        login_forgetpassword_mainScrollView.setLayoutParams(LinearLayoutlp);
    }

    //注册  切换界面
    public void RegisterShow(){
        HideAllLayout();
        LinearLayout login_register_main = mview.findViewById(R.id.login_register_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_register_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        login_register_main.setLayoutParams(LP);
        login_register_main.setVisibility(View.VISIBLE);
        ScrollView login_register_mainScrollView = mview.findViewById(R.id.login_register_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_register_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayoutlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        login_register_mainScrollView.setLayoutParams(LinearLayoutlp);
    }

    //主页-获取验证码倒计时
    public void SMSCodeGet(){
        if (mview == null){
            Toast.makeText(mControlMainActivity,"系统错误！",Toast.LENGTH_LONG).show();
            return;
        }
        EditText login_forgetpassword_username_edittext = mview.findViewById(R.id.login_forgetpassword_username_edittext);
        String tel = login_forgetpassword_username_edittext.getText().toString();
        //判断手机号格式是否正确
        if (!mControlMainActivity.isTelNumber(tel)){
            Toast.makeText(mControlMainActivity,"手机号码格式不正确，请检查后重试！",Toast.LENGTH_LONG).show();
            return;
        }
        TextView forgetpassword_getsmscode = mview.findViewById(R.id.forgetpassword_getsmscode);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) forgetpassword_getsmscode.getLayoutParams();
        lp.width = 0;
        lp.leftMargin = 0;
        lp.height = 0;
        forgetpassword_getsmscode.setLayoutParams(lp);
        TextView forgetpassword_getsmscodecountdown = mview.findViewById(R.id.forgetpassword_getsmscodecountdown);
        lp = (RelativeLayout.LayoutParams) forgetpassword_getsmscodecountdown.getLayoutParams();
        lp.width = width / 4;
        lp.leftMargin = width / 25;
        lp.height = width / 12;
        forgetpassword_getsmscodecountdown.setLayoutParams(lp);
        /** 倒计时60秒，一次1秒 */
        if (mLoginSMSCodeCountDownTimer != null){
            mLoginSMSCodeCountDownTimer.cancel();
            mLoginSMSCodeCountDownTimer = null;
        }
        mLoginSMSCodeCountDownTimer = new CountDownTimer(60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                forgetpassword_getsmscodecountdown.setText(millisUntilFinished/1000+"秒后重新获取验证码");
            }

            @Override
            public void onFinish() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) forgetpassword_getsmscodecountdown.getLayoutParams();
                lp.width = 0;
                lp.leftMargin = 0;
                lp.height = 0;
                forgetpassword_getsmscodecountdown.setLayoutParams(lp);
                lp = (RelativeLayout.LayoutParams) forgetpassword_getsmscode.getLayoutParams();
                lp.width = width / 4;
                lp.leftMargin = width / 25;
                lp.height = width / 12;
                forgetpassword_getsmscode.setLayoutParams(lp);
            }
        }.start();
        VerificationCode(tel);
    }

    //注册-获取验证码倒计时
    public void RegisterSMSCodeGet(){
        if (mview == null){
            Toast.makeText(mControlMainActivity,"系统错误！",Toast.LENGTH_LONG).show();
            return;
        }
        EditText login_register_username_edittext = mview.findViewById(R.id.login_register_username_edittext);
        String tel = login_register_username_edittext.getText().toString();
        //判断手机号格式是否正确
        if (!mControlMainActivity.isTelNumber(tel)){
            Toast.makeText(mControlMainActivity,"手机号码格式不正确，请检查后重试！",Toast.LENGTH_LONG).show();
            return;
        }
        TextView register_getsmscode = mview.findViewById(R.id.register_getsmscode);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) register_getsmscode.getLayoutParams();
        lp.width = 0;
        lp.leftMargin = 0;
        lp.height = 0;
        register_getsmscode.setLayoutParams(lp);
        TextView register_getsmscodecountdown = mview.findViewById(R.id.register_getsmscodecountdown);
        lp = (RelativeLayout.LayoutParams) register_getsmscodecountdown.getLayoutParams();
        lp.width = width / 4;
        lp.leftMargin = width / 25;
        lp.height = width / 12;
        register_getsmscodecountdown.setLayoutParams(lp);
        /** 倒计时60秒，一次1秒 */
        if (mRegisterSMSCodeCountDownTimer != null){
            mRegisterSMSCodeCountDownTimer.cancel();
            mRegisterSMSCodeCountDownTimer = null;
        }
        mRegisterSMSCodeCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                register_getsmscodecountdown.setText(millisUntilFinished/1000+"秒后重新获取验证码");
            }

            @Override
            public void onFinish() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) register_getsmscodecountdown.getLayoutParams();
                lp.width = 0;
                lp.leftMargin = 0;
                lp.height = 0;
                register_getsmscodecountdown.setLayoutParams(lp);
                lp = (RelativeLayout.LayoutParams) register_getsmscode.getLayoutParams();
                lp.width = width / 4;
                lp.leftMargin = width / 25;
                lp.height = width / 12;
                register_getsmscode.setLayoutParams(lp);
            }
        }.start();
        VerifyPhoneNumber(tel);
    }


    //获取用户id
    public String UserIdGet(){
        EditText login_username_edittext = mview.findViewById(R.id.login_username_edittext);
        return login_username_edittext.getText().toString();
    }

    //获取用户密码
    public String UserPasswordGet(){
        EditText login_password_edittext = mview.findViewById(R.id.login_password_edittext);
        return login_password_edittext.getText().toString();
    }

    //隐藏所有图层
    private void HideAllLayout(){
        LinearLayout login_main = mview.findViewById(R.id.login_main);
        FrameLayout.LayoutParams LP = (FrameLayout.LayoutParams) login_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        login_main.setLayoutParams(LP);
        login_main.setVisibility(View.INVISIBLE);
        ScrollView login_mainScrollView = mview.findViewById(R.id.login_mainScrollView);
        LinearLayout.LayoutParams LinearLayoutlp = (LinearLayout.LayoutParams) login_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = 0;
        LinearLayoutlp.height = 0;
        login_mainScrollView.setLayoutParams(LinearLayoutlp);
        LinearLayout login_forgetpassword_main = mview.findViewById(R.id.login_forgetpassword_main);
        LP = (FrameLayout.LayoutParams) login_forgetpassword_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        login_forgetpassword_main.setLayoutParams(LP);
        login_forgetpassword_main.setVisibility(View.INVISIBLE);
        ScrollView login_forgetpassword_mainScrollView = mview.findViewById(R.id.login_forgetpassword_mainScrollView);
        LinearLayoutlp = (LinearLayout.LayoutParams) login_forgetpassword_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = 0;
        LinearLayoutlp.height = 0;
        login_forgetpassword_mainScrollView.setLayoutParams(LinearLayoutlp);
        LinearLayout login_register_main = mview.findViewById(R.id.login_register_main);
        LP = (FrameLayout.LayoutParams) login_register_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        login_register_main.setLayoutParams(LP);
        login_register_main.setVisibility(View.INVISIBLE);
        ScrollView login_register_mainScrollView = mview.findViewById(R.id.login_register_mainScrollView);
        LinearLayoutlp = (LinearLayout.LayoutParams) login_register_mainScrollView.getLayoutParams();
        LinearLayoutlp.width = 0;
        LinearLayoutlp.height = 0;
        login_register_mainScrollView.setLayoutParams(LinearLayoutlp);
    }

    //初始化登录主界面
    public void LogInMainInit(){
        RelativeLayout login_returnRelativeLayout = mview.findViewById(R.id.login_returnRelativeLayout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) login_returnRelativeLayout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 25;
        lp.rightMargin = width / 10;
        lp.height = width / 10;
        login_returnRelativeLayout.setLayoutParams(lp);
        //返回
        ImageView login_return_button = mview.findViewById(R.id.login_return_button);
        RelativeLayout.LayoutParams login_return_buttonLp = (RelativeLayout.LayoutParams) login_return_button.getLayoutParams();
        login_return_buttonLp.height = width / 15;
        login_return_buttonLp.width = width / 15;
        login_return_button.setLayoutParams(login_return_buttonLp);
        //火种logo
        ControllerCustomRoundAngleImageView logoImageView = mview.findViewById(R.id.huozhonglogo);
        logoImageView.setImageDrawable(getResources().getDrawable(R.mipmap.logo2));
        lp = (LinearLayout.LayoutParams) logoImageView.getLayoutParams();
        lp.topMargin = width / 3;
        lp.bottomMargin = width / 3;
        lp.height = width / 3;
        lp.width = width / 3;
        logoImageView.setLayoutParams(lp);
        //用户名
        RelativeLayout login_username_layout = mview.findViewById(R.id.login_username_layout);
        lp = (LinearLayout.LayoutParams) login_username_layout.getLayoutParams();
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_username_layout.setLayoutParams(lp);
        //用户名图标
        ImageView login_username_imageview = mview.findViewById(R.id.login_username_imageview);
        RelativeLayout.LayoutParams login_username_imageviewLp = (RelativeLayout.LayoutParams) login_username_imageview.getLayoutParams();
        login_username_imageviewLp.height = width / 15;
        login_username_imageviewLp.width = width / 15;
        login_username_imageviewLp.rightMargin = width / 25;
        login_username_imageview.setLayoutParams(login_username_imageviewLp);
        //用户名输入框
        EditText login_username_edittext = mview.findViewById(R.id.login_username_edittext);
        RelativeLayout.LayoutParams login_username_edittextLp = (RelativeLayout.LayoutParams) login_username_edittext.getLayoutParams();
        login_username_edittextLp.width = width - width / 15 * 4;
        login_username_edittext.setLayoutParams(login_username_edittextLp);
        //分界线1
        View login_line1 = mview.findViewById(R.id.login_line1);
        lp = (LinearLayout.LayoutParams) login_line1.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_line1.setLayoutParams(lp);
        //密码
        RelativeLayout login_password_layout = mview.findViewById(R.id.login_password_layout);
        lp = (LinearLayout.LayoutParams) login_password_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_password_layout.setLayoutParams(lp);
        //密码图标
        ImageView login_password_imageview = mview.findViewById(R.id.login_password_imageview);
        RelativeLayout.LayoutParams login_password_imageviewLp = (RelativeLayout.LayoutParams) login_password_imageview.getLayoutParams();
        login_password_imageviewLp.height = width / 15;
        login_password_imageviewLp.width = width / 15;
        login_password_imageviewLp.rightMargin = width / 25;
        login_password_imageview.setLayoutParams(login_password_imageviewLp);
        //密码输入框
        EditText login_password_edittext = mview.findViewById(R.id.login_password_edittext);
        RelativeLayout.LayoutParams login_password_edittextLp = (RelativeLayout.LayoutParams) login_password_edittext.getLayoutParams();
        login_password_edittextLp.width = width - width / 15 - width / 12 * 4;
        login_password_edittext.setLayoutParams(login_password_edittextLp);
        login_password_edittext.setText("");
        //密码是否明码按钮
        ImageView login_password_visible_imageview = mview.findViewById(R.id.login_password_visible_imageview);
        RelativeLayout.LayoutParams login_password_visible_imageviewLp = (RelativeLayout.LayoutParams) login_password_visible_imageview.getLayoutParams();
        login_password_visible_imageviewLp.width = width / 12;
        login_password_visible_imageviewLp.leftMargin = width / 25;
        login_password_visible_imageviewLp.height = width / 12;
        login_password_visible_imageview.setLayoutParams(login_password_visible_imageviewLp);
        //分界线2
        View login_line2 = mview.findViewById(R.id.login_line2);
        lp = (LinearLayout.LayoutParams) login_line2.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_line2.setLayoutParams(lp);
        //设置登录按钮
        Button login_button = mview.findViewById(R.id.login_button);
        lp = (LinearLayout.LayoutParams) login_button.getLayoutParams();
        lp.topMargin = width / 15;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 7;
        login_button.setLayoutParams(lp);
        RelativeLayout function_button_layout = mview.findViewById(R.id.function_button_layout);
        lp = (LinearLayout.LayoutParams) function_button_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        function_button_layout.setLayoutParams(lp);
        //设置密码不可见
        login_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //密码是否可见
        login_password_visible_imageview.setOnClickListener(v ->{
            if(!mLoginIsOpenEye) {
                login_password_visible_imageview.setSelected(true);
                mLoginIsOpenEye = true;
                //密码可见
                login_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                login_password_visible_imageview.setSelected(false);
                mLoginIsOpenEye = false;
                //密码不可见
                login_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            login_password_edittext.setSelection(login_password_edittext.getText().toString().length());
        });
    }

    //初始化忘记密码主界面
    public void ForgetPasswordMainInit(){
        RelativeLayout login_forgetpassword_returnRelativeLayout = mview.findViewById(R.id.login_forgetpassword_returnRelativeLayout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) login_forgetpassword_returnRelativeLayout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 25;
        lp.rightMargin = width / 10;
        lp.height = width / 10;
        login_forgetpassword_returnRelativeLayout.setLayoutParams(lp);
        //返回按钮
        ImageView login_forgetpassword_return_button = mview.findViewById(R.id.login_forgetpassword_return_button);
        RelativeLayout.LayoutParams login_forgetpassword_return_buttonLp = (RelativeLayout.LayoutParams) login_forgetpassword_return_button.getLayoutParams();
        login_forgetpassword_return_buttonLp.height = width / 15;
        login_forgetpassword_return_buttonLp.width = width / 15;
        login_forgetpassword_return_button.setLayoutParams(login_forgetpassword_return_buttonLp);
        //logo
        ControllerCustomRoundAngleImageView forgetpassword_huozhonglogoImageView = mview.findViewById(R.id.forgetpassword_huozhonglogo);
        forgetpassword_huozhonglogoImageView.setImageDrawable(getResources().getDrawable(R.mipmap.logo2));
        lp = (LinearLayout.LayoutParams) forgetpassword_huozhonglogoImageView.getLayoutParams();
        lp.topMargin = width / 3;
        lp.bottomMargin = width / 3;
        lp.height = width / 3;
        lp.width = width / 3;
        forgetpassword_huozhonglogoImageView.setLayoutParams(lp);
        //用户名
        RelativeLayout login_forgetpassword_username_layout = mview.findViewById(R.id.login_forgetpassword_username_layout);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_username_layout.getLayoutParams();
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_forgetpassword_username_layout.setLayoutParams(lp);
        ImageView login_forgetpassword_username_imageview = mview.findViewById(R.id.login_forgetpassword_username_imageview);
        RelativeLayout.LayoutParams login_forgetpassword_username_imageviewLp = (RelativeLayout.LayoutParams) login_forgetpassword_username_imageview.getLayoutParams();
        login_forgetpassword_username_imageviewLp.height = width / 15;
        login_forgetpassword_username_imageviewLp.width = width / 15;
        login_forgetpassword_username_imageviewLp.rightMargin = width / 25;
        login_forgetpassword_username_imageview.setLayoutParams(login_forgetpassword_username_imageviewLp);
        EditText login_forgetpassword_username_edittext = mview.findViewById(R.id.login_forgetpassword_username_edittext);
        RelativeLayout.LayoutParams login_forgetpassword_username_edittextLp = (RelativeLayout.LayoutParams) login_forgetpassword_username_edittext.getLayoutParams();
        login_forgetpassword_username_edittextLp.width = width - width / 15 * 4;
        login_forgetpassword_username_edittext.setLayoutParams(login_forgetpassword_username_edittextLp);
        //分界线1
        View login_forgetpassword_line1 = mview.findViewById(R.id.login_forgetpassword_line1);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_line1.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_forgetpassword_line1.setLayoutParams(lp);
        //验证码
        RelativeLayout login_forgetpassword_smscode_layout = mview.findViewById(R.id.login_forgetpassword_smscode_layout);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_smscode_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_forgetpassword_smscode_layout.setLayoutParams(lp);
        //验证码图标
        ImageView login_forgetpassword_smscode_imageview = mview.findViewById(R.id.login_forgetpassword_smscode_imageview);
        RelativeLayout.LayoutParams login_forgetpassword_smscode_imageviewLp = (RelativeLayout.LayoutParams) login_forgetpassword_smscode_imageview.getLayoutParams();
        login_forgetpassword_smscode_imageviewLp.height = width / 15;
        login_forgetpassword_smscode_imageviewLp.width = width / 15;
        login_forgetpassword_smscode_imageviewLp.rightMargin = width / 25;
        login_forgetpassword_smscode_imageview.setLayoutParams(login_forgetpassword_smscode_imageviewLp);
        //验证码输入框
        EditText login_forgetpassword_smscode_edittext = mview.findViewById(R.id.login_forgetpassword_smscode_edittext);
        RelativeLayout.LayoutParams login_forgetpassword_smscode_edittextLp = (RelativeLayout.LayoutParams) login_forgetpassword_smscode_edittext.getLayoutParams();
        login_forgetpassword_smscode_edittextLp.width = width - width / 15 - width / 12 * 3 - width / 4;
        login_forgetpassword_smscode_edittext.setLayoutParams(login_forgetpassword_smscode_edittextLp);
        login_forgetpassword_smscode_edittext.setText("");
        //获取验证码按钮
        TextView forgetpassword_getsmscode = mview.findViewById(R.id.forgetpassword_getsmscode);
        RelativeLayout.LayoutParams forgetpassword_getsmscodeLp = (RelativeLayout.LayoutParams) forgetpassword_getsmscode.getLayoutParams();
        forgetpassword_getsmscodeLp.width = width / 4;
        forgetpassword_getsmscodeLp.leftMargin = width / 25;
        forgetpassword_getsmscodeLp.height = width / 12;
        forgetpassword_getsmscode.setLayoutParams(forgetpassword_getsmscodeLp);
        //分界线2
        View login_forgetpassword_line2 = mview.findViewById(R.id.login_forgetpassword_line2);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_line2.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_forgetpassword_line2.setLayoutParams(lp);
        //新密码
        RelativeLayout login_forgetpassword_password_layout = mview.findViewById(R.id.login_forgetpassword_password_layout);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_password_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_forgetpassword_password_layout.setLayoutParams(lp);
        //新密码图标
        ImageView login_forgetpassword_password_imageview = mview.findViewById(R.id.login_forgetpassword_password_imageview);
        RelativeLayout.LayoutParams login_forgetpassword_password_imageviewLp = (RelativeLayout.LayoutParams) login_forgetpassword_password_imageview.getLayoutParams();
        login_forgetpassword_password_imageviewLp.height = width / 15;
        login_forgetpassword_password_imageviewLp.width = width / 15;
        login_forgetpassword_password_imageviewLp.rightMargin = width / 25;
        login_forgetpassword_password_imageview.setLayoutParams(login_forgetpassword_password_imageviewLp);
        //新密码输入框
        EditText login_forgetpassword_password_edittext = mview.findViewById(R.id.login_forgetpassword_password_edittext);
        RelativeLayout.LayoutParams login_forgetpassword_password_edittextLp = (RelativeLayout.LayoutParams) login_forgetpassword_password_edittext.getLayoutParams();
        login_forgetpassword_password_edittextLp.width = width - width / 15 - width / 12 * 4;
        login_forgetpassword_password_edittext.setLayoutParams(login_forgetpassword_password_edittextLp);
        login_forgetpassword_password_edittext.setText("");
        //密码是否明码按钮
        ImageView login_forgetpassword_password_visible_imageview = mview.findViewById(R.id.login_forgetpassword_password_visible_imageview);
        RelativeLayout.LayoutParams login_forgetpassword_password_visible_imageviewLp = (RelativeLayout.LayoutParams) login_forgetpassword_password_visible_imageview.getLayoutParams();
        login_forgetpassword_password_visible_imageviewLp.width = width / 12;
        login_forgetpassword_password_visible_imageviewLp.leftMargin = width / 25;
        login_forgetpassword_password_visible_imageviewLp.height = width / 12;
        login_forgetpassword_password_visible_imageview.setLayoutParams(login_forgetpassword_password_visible_imageviewLp);
        //分界线3
        View login_forgetpassword_line3 = mview.findViewById(R.id.login_forgetpassword_line3);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_line3.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_forgetpassword_line3.setLayoutParams(lp);
        //设置登录按钮
        Button login_forgetpassword_button = mview.findViewById(R.id.login_forgetpassword_button);
        lp = (LinearLayout.LayoutParams) login_forgetpassword_button.getLayoutParams();
        lp.topMargin = width / 15;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 7;
        login_forgetpassword_button.setLayoutParams(lp);
        //设置密码不可见
        login_forgetpassword_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //密码是否可见
        login_forgetpassword_password_visible_imageview.setOnClickListener(v ->{
            if(!mForgetPasswordIsOpenEye) {
                login_forgetpassword_password_visible_imageview.setSelected(true);
                mForgetPasswordIsOpenEye = true;
                //密码可见
                login_forgetpassword_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                login_forgetpassword_password_visible_imageview.setSelected(false);
                mForgetPasswordIsOpenEye = false;
                //密码不可见
                login_forgetpassword_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            login_forgetpassword_password_edittext.setSelection(login_forgetpassword_password_edittext.getText().toString().length());
        });
    }

    //初始化注册主界面
    public void RegisterMainInit(){
        RelativeLayout login_register_returnRelativeLayout = mview.findViewById(R.id.login_register_returnRelativeLayout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) login_register_returnRelativeLayout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 25;
        lp.rightMargin = width / 10;
        lp.height = width / 10;
        login_register_returnRelativeLayout.setLayoutParams(lp);
        //返回按钮
        ImageView login_register_return_button = mview.findViewById(R.id.login_register_return_button);
        RelativeLayout.LayoutParams login_register_return_buttonLp = (RelativeLayout.LayoutParams) login_register_return_button.getLayoutParams();
        login_register_return_buttonLp.height = width / 15;
        login_register_return_buttonLp.width = width / 15;
        login_register_return_button.setLayoutParams(login_register_return_buttonLp);
        //logo
        ControllerCustomRoundAngleImageView register_huozhonglogo = mview.findViewById(R.id.register_huozhonglogo);
        register_huozhonglogo.setImageDrawable(getResources().getDrawable(R.mipmap.logo2));
        lp = (LinearLayout.LayoutParams) register_huozhonglogo.getLayoutParams();
        lp.topMargin = width / 4;
        lp.bottomMargin = width / 4;
        lp.height = width / 4;
        lp.width = width / 4;
        register_huozhonglogo.setLayoutParams(lp);
        //用户名
        RelativeLayout login_register_username_layout = mview.findViewById(R.id.login_register_username_layout);
        lp = (LinearLayout.LayoutParams) login_register_username_layout.getLayoutParams();
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_register_username_layout.setLayoutParams(lp);
        ImageView login_register_username_imageview = mview.findViewById(R.id.login_register_username_imageview);
        RelativeLayout.LayoutParams login_register_username_imageviewLp = (RelativeLayout.LayoutParams) login_register_username_imageview.getLayoutParams();
        login_register_username_imageviewLp.height = width / 15;
        login_register_username_imageviewLp.width = width / 15;
        login_register_username_imageviewLp.rightMargin = width / 25;
        login_register_username_imageview.setLayoutParams(login_register_username_imageviewLp);
        EditText login_register_username_edittext = mview.findViewById(R.id.login_register_username_edittext);
        RelativeLayout.LayoutParams login_register_username_edittextLp = (RelativeLayout.LayoutParams) login_register_username_edittext.getLayoutParams();
        login_register_username_edittextLp.width = width - width / 15 * 4;
        login_register_username_edittext.setLayoutParams(login_register_username_edittextLp);
        //分界线1
        View login_register_line1 = mview.findViewById(R.id.login_register_line1);
        lp = (LinearLayout.LayoutParams) login_register_line1.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_register_line1.setLayoutParams(lp);
        //验证码
        RelativeLayout login_register_smscode_layout = mview.findViewById(R.id.login_register_smscode_layout);
        lp = (LinearLayout.LayoutParams) login_register_smscode_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_register_smscode_layout.setLayoutParams(lp);
        //验证码图标
        ImageView login_register_smscode_imageview = mview.findViewById(R.id.login_register_smscode_imageview);
        RelativeLayout.LayoutParams login_register_smscode_imageviewLp = (RelativeLayout.LayoutParams) login_register_smscode_imageview.getLayoutParams();
        login_register_smscode_imageviewLp.height = width / 15;
        login_register_smscode_imageviewLp.width = width / 15;
        login_register_smscode_imageviewLp.rightMargin = width / 25;
        login_register_smscode_imageview.setLayoutParams(login_register_smscode_imageviewLp);
        //验证码输入框
        EditText login_register_smscode_edittext = mview.findViewById(R.id.login_register_smscode_edittext);
        RelativeLayout.LayoutParams login_register_smscode_edittextLp = (RelativeLayout.LayoutParams) login_register_smscode_edittext.getLayoutParams();
        login_register_smscode_edittextLp.width = width - width / 15 - width / 12 * 3 - width / 4;
        login_register_smscode_edittext.setLayoutParams(login_register_smscode_edittextLp);
        login_register_smscode_edittext.setText("");
        //获取验证码按钮
        TextView register_getsmscode = mview.findViewById(R.id.register_getsmscode);
        RelativeLayout.LayoutParams register_getsmscodeLp = (RelativeLayout.LayoutParams) register_getsmscode.getLayoutParams();
        register_getsmscodeLp.width = width / 4;
        register_getsmscodeLp.leftMargin = width / 25;
        register_getsmscodeLp.height = width / 12;
        register_getsmscode.setLayoutParams(register_getsmscodeLp);
        //分界线2
        View login_register_line2 = mview.findViewById(R.id.login_register_line2);
        lp = (LinearLayout.LayoutParams) login_register_line2.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_register_line2.setLayoutParams(lp);
        //新密码
        RelativeLayout login_register_password_layout = mview.findViewById(R.id.login_register_password_layout);
        lp = (LinearLayout.LayoutParams) login_register_password_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_register_password_layout.setLayoutParams(lp);
        //新密码图标
        ImageView login_register_password_imageview = mview.findViewById(R.id.login_register_password_imageview);
        RelativeLayout.LayoutParams login_register_password_imageviewLp = (RelativeLayout.LayoutParams) login_register_password_imageview.getLayoutParams();
        login_register_password_imageviewLp.height = width / 15;
        login_register_password_imageviewLp.width = width / 15;
        login_register_password_imageviewLp.rightMargin = width / 25;
        login_register_password_imageview.setLayoutParams(login_register_password_imageviewLp);
        //新密码输入框
        EditText login_register_password_edittext = mview.findViewById(R.id.login_register_password_edittext);
        RelativeLayout.LayoutParams login_register_password_edittextLp = (RelativeLayout.LayoutParams) login_register_password_edittext.getLayoutParams();
        login_register_password_edittextLp.width = width - width / 15 - width / 12 * 4;
        login_register_password_edittext.setLayoutParams(login_register_password_edittextLp);
        login_register_password_edittext.setText("");
        //密码是否明码按钮
        ImageView login_register_password_visible_imageview = mview.findViewById(R.id.login_register_password_visible_imageview);
        RelativeLayout.LayoutParams login_register_password_visible_imageviewLp = (RelativeLayout.LayoutParams) login_register_password_visible_imageview.getLayoutParams();
        login_register_password_visible_imageviewLp.width = width / 12;
        login_register_password_visible_imageviewLp.leftMargin = width / 25;
        login_register_password_visible_imageviewLp.height = width / 12;
        login_register_password_visible_imageview.setLayoutParams(login_register_password_visible_imageviewLp);
        //分界线3
        View login_register_line3 = mview.findViewById(R.id.login_register_line3);
        lp = (LinearLayout.LayoutParams) login_register_line3.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_register_line3.setLayoutParams(lp);
        //确认密码
        RelativeLayout login_register_repeatpassword_layout = mview.findViewById(R.id.login_register_repeatpassword_layout);
        lp = (LinearLayout.LayoutParams) login_register_repeatpassword_layout.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 15;
        login_register_repeatpassword_layout.setLayoutParams(lp);
        //确认密码图标
        ImageView login_register_repeatpassword_imageview = mview.findViewById(R.id.login_register_repeatpassword_imageview);
        RelativeLayout.LayoutParams login_register_repeatpassword_imageviewLp = (RelativeLayout.LayoutParams) login_register_repeatpassword_imageview.getLayoutParams();
        login_register_repeatpassword_imageviewLp.height = width / 15;
        login_register_repeatpassword_imageviewLp.width = width / 15;
        login_register_repeatpassword_imageviewLp.rightMargin = width / 25;
        login_register_repeatpassword_imageview.setLayoutParams(login_register_repeatpassword_imageviewLp);
        //确认密码输入框
        EditText login_register_repeatpassword_edittext = mview.findViewById(R.id.login_register_repeatpassword_edittext);
        RelativeLayout.LayoutParams login_register_repeatpassword_edittextLp = (RelativeLayout.LayoutParams) login_register_repeatpassword_edittext.getLayoutParams();
        login_register_repeatpassword_edittextLp.width = width - width / 15 - width / 12 * 4;
        login_register_repeatpassword_edittext.setLayoutParams(login_register_repeatpassword_edittextLp);
        login_register_repeatpassword_edittext.setText("");
        //确认密码是否明码按钮
        ImageView login_register_repeatpassword_visible_imageview = mview.findViewById(R.id.login_register_repeatpassword_visible_imageview);
        RelativeLayout.LayoutParams login_register_repeatpassword_visible_imageviewLp = (RelativeLayout.LayoutParams) login_register_repeatpassword_visible_imageview.getLayoutParams();
        login_register_repeatpassword_visible_imageviewLp.width = width / 12;
        login_register_repeatpassword_visible_imageviewLp.leftMargin = width / 25;
        login_register_repeatpassword_visible_imageviewLp.height = width / 12;
        login_register_repeatpassword_visible_imageview.setLayoutParams(login_register_repeatpassword_visible_imageviewLp);
        //分界线4
        View login_register_line4 = mview.findViewById(R.id.login_register_line4);
        lp = (LinearLayout.LayoutParams) login_register_line4.getLayoutParams();
        lp.topMargin = width / 25;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        login_register_line4.setLayoutParams(lp);
        //设置注册按钮
        Button login_register_button = mview.findViewById(R.id.login_register_button);
        lp = (LinearLayout.LayoutParams) login_register_button.getLayoutParams();
        lp.topMargin = width / 15;
        lp.leftMargin = width / 10;
        lp.rightMargin = width / 10;
        lp.height = width / 7;
        login_register_button.setLayoutParams(lp);
        //设置密码不可见
        login_register_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        login_register_repeatpassword_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //密码是否可见
        login_register_password_visible_imageview.setOnClickListener(v ->{
            if(!mRegisterIsOpenEye) {
                login_register_password_visible_imageview.setSelected(true);
                mRegisterIsOpenEye = true;
                //密码可见
                login_register_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                login_register_password_visible_imageview.setSelected(false);
                mRegisterIsOpenEye = false;
                //密码不可见
                login_register_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            login_register_password_edittext.setSelection(login_register_password_edittext.getText().toString().length());
        });
        login_register_repeatpassword_visible_imageview.setOnClickListener(v ->{
            if(!mRegisterRepeatIsOpenEye) {
                login_register_repeatpassword_visible_imageview.setSelected(true);
                mRegisterRepeatIsOpenEye = true;
                //密码可见
                login_register_repeatpassword_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                login_register_repeatpassword_visible_imageview.setSelected(false);
                mRegisterRepeatIsOpenEye = false;
                //密码不可见
                login_register_repeatpassword_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            login_register_repeatpassword_edittext.setSelection(login_register_repeatpassword_edittext.getText().toString().length());
        });
    }
    //将用户登录密码做md5加密
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    //登录
    public void LogIn(String username,String password){
        if (password.length() < 6){
            Toast.makeText(mControlMainActivity,"密码不能少于6位",Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",username);
        paramsMap.put("stu_pass",getMd5Value(password));
//        paramsMap.put("stu_pass",password);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.PasswordLogin(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (loginBean.getErrorCode() == 205){
                    Toast.makeText(mControlMainActivity,"密码错误",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else if (loginBean.getErrorCode() == 207){
                    if (loginBean.getMsg() == null){
                        Toast.makeText(mControlMainActivity, "登录失败", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity, loginBean.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else if (loginBean.getErrorCode() == 208){
                    Toast.makeText(mControlMainActivity,"密码错误",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (loginBean.getData() == null){
                    Toast.makeText(mControlMainActivity,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (loginBean.getData().get("stu_id") == null){
                    Toast.makeText(mControlMainActivity,"登录失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //如果登录成功，跳转到我的界面，并存储token
                String token = String.valueOf(loginBean.getData().get("token"));
                String stu_id = String.valueOf(loginBean.getData().get("stu_id"));
                if (!stu_id.equals("")) {
                    mControlMainActivity.LogInSuccess(token,stu_id);
                } else {
                    Toast.makeText(mControlMainActivity,"登录失败",Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"登录超时",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //注册
    public void Register(){
        if (mview == null){
            Toast.makeText(mControlMainActivity,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        //判断密码是否为6~12位数字或字母
        EditText login_register_repeatpassword_edittext = mview.findViewById(R.id.login_register_repeatpassword_edittext);
        EditText login_register_password_edittext = mview.findViewById(R.id.login_register_password_edittext);
        EditText login_register_username_edittext = mview.findViewById(R.id.login_register_username_edittext);
        EditText login_register_smscode_edittext = mview.findViewById(R.id.login_register_smscode_edittext);
        if (login_register_repeatpassword_edittext == null || login_register_password_edittext == null ||
                login_register_username_edittext == null || login_register_smscode_edittext == null){
            Toast.makeText(mControlMainActivity,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        String username = login_register_username_edittext.getText().toString();
        if (username.equals("")){
            Toast.makeText(mControlMainActivity,"用户名不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        String phonecode = login_register_smscode_edittext.getText().toString();
        if (phonecode.equals("")){
            Toast.makeText(mControlMainActivity,"请输入验证码",Toast.LENGTH_LONG).show();
            return;
        }
        if (!login_register_repeatpassword_edittext.getText().toString().equals(login_register_password_edittext.getText().toString())){
            Toast.makeText(mControlMainActivity,"两次输入的密码不一致",Toast.LENGTH_LONG).show();
            return;
        }
        String password = login_register_repeatpassword_edittext.getText().toString();
//        if (!isLetterDigit(password)){
//            Toast.makeText(mControlMainActivity,"密码格式不正确，请改为包含大小写字母及数字且在6-12位",Toast.LENGTH_LONG).show();
//            return;
//        }
        if (password.length()<6){
            Toast.makeText(mControlMainActivity,"密码不能少于6位",Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",username);
        paramsMap.put("stu_pass",password);
        paramsMap.put("sms_code",phonecode);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.telRegister(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"注册失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code == 200) {
                    mControlMainActivity.onClickImmediatelyLogin("login");
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                } else if (code == 201) {
                    Toast.makeText(mControlMainActivity,"该手机号已存在",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else if (code == 203) {
                    Toast.makeText(mControlMainActivity,"注册失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else if (code == 205) {
                    Toast.makeText(mControlMainActivity,"手机号验证码错误",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else {
                    Toast.makeText(mControlMainActivity,"注册失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"注册超时",Toast.LENGTH_LONG).show();
            }
        });
    }

    //忘记密码
    public void RetrievePassword(){
        if (mview == null){
            Toast.makeText(mControlMainActivity,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        //判断密码是否为6~12位数字或字母
        EditText login_forgetpassword_password_edittext = mview.findViewById(R.id.login_forgetpassword_password_edittext);
        EditText login_forgetpassword_username_edittext = mview.findViewById(R.id.login_forgetpassword_username_edittext);
        EditText login_forgetpassword_smscode_edittext = mview.findViewById(R.id.login_forgetpassword_smscode_edittext);
        if (login_forgetpassword_password_edittext == null || login_forgetpassword_username_edittext == null ||
                login_forgetpassword_smscode_edittext == null){
            Toast.makeText(mControlMainActivity,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        String username = login_forgetpassword_username_edittext.getText().toString();
        if (username.equals("")){
            Toast.makeText(mControlMainActivity,"用户名不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        String phonecode = login_forgetpassword_smscode_edittext.getText().toString();
        if (phonecode.equals("")){
            Toast.makeText(mControlMainActivity,"请输入验证码",Toast.LENGTH_LONG).show();
            return;
        }
        String password = login_forgetpassword_password_edittext.getText().toString();
//        if (!isLetterDigit(password)){
//            Toast.makeText(mControlMainActivity,"密码格式不正确，请改为包含大小写字母及数字且在6-12位",Toast.LENGTH_LONG).show();
//            return;
//        }
        if (password.length()<6){
            Toast.makeText(mControlMainActivity,"密码不能少于6位",Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",username);
        paramsMap.put("stu_pass",password);
        paramsMap.put("sms_code",phonecode);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.retrievePassword(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Toast.makeText(mControlMainActivity,"找回密码失败",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code == 200) {
                    mControlMainActivity.onClickImmediatelyLogin("login");
                } else {
                    Toast.makeText(mControlMainActivity,"找回密码失败",Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"找回密码超时",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取验证码
    private void VerificationCode(String tel){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",tel);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getVerificationCode(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"获取验证码失败",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code != 200) {
                    Toast.makeText(mControlMainActivity,"获取验证码失败",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                } else {
                    HeaderInterceptor.cookie = response.headers().get("set-cookie");
                    Log.e("NetWork=>headers", HeaderInterceptor.cookie);
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"获取验证码超时",Toast.LENGTH_LONG).show();
                Log.e("httperror","" + t.getStackTrace().toString());
                //停止倒计时
                if (mLoginSMSCodeCountDownTimer != null){
                    mLoginSMSCodeCountDownTimer.onFinish();
                    mLoginSMSCodeCountDownTimer = null;
                }
                if (mRegisterSMSCodeCountDownTimer != null){
                    mRegisterSMSCodeCountDownTimer.onFinish();
                    mRegisterSMSCodeCountDownTimer = null;
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //判断手机号是否可用
    private void VerifyPhoneNumber(String tel){
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,String> paramsMap= new HashMap<>();
        paramsMap.put("tel",tel);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getVerifyPhoneNumber(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"手机号码不可用",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //网络请求数据成功
                int code = loginBean.getErrorCode();
                //如果注册成功，跳转到登录界面
                if (code != 200 ) {
                    Toast.makeText(mControlMainActivity,"手机号码不可用",Toast.LENGTH_LONG).show();
                    //停止倒计时
                    if (mLoginSMSCodeCountDownTimer != null){
                        mLoginSMSCodeCountDownTimer.onFinish();
                        mLoginSMSCodeCountDownTimer = null;
                    }
                    if (mRegisterSMSCodeCountDownTimer != null){
                        mRegisterSMSCodeCountDownTimer.onFinish();
                        mRegisterSMSCodeCountDownTimer = null;
                    }
                } else {
                    VerificationCode(tel);
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"验证手机号码超时",Toast.LENGTH_LONG).show();
                //停止倒计时
                if (mLoginSMSCodeCountDownTimer != null){
                    mLoginSMSCodeCountDownTimer.onFinish();
                    mLoginSMSCodeCountDownTimer = null;
                }
                if (mRegisterSMSCodeCountDownTimer != null){
                    mRegisterSMSCodeCountDownTimer.onFinish();
                    mRegisterSMSCodeCountDownTimer = null;
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }
    /**
     * 包含大小写字母及数字且在6-12位
     * 是否包含
     *
     * @param str
     * @return
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]{6,12}$";
        boolean isRight = isDigit && isLetter && str.matches(regex);
        return isRight;
    }

    class BaseBean {
        private String data;
        private int code;
        private String msg;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return msg;
        }

        public void setErrorMsg(String msg) {
            this.msg = msg;
        }
    }
}
