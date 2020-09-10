package com.android.jwjy.wtjyproduct;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 设置模块
 */
public class ModelSetting extends Fragment {
    private static ControlMainActivity mControlMainActivity;
    private TextView mTextView;
    //要显示的页面
    static private int FragmentPage;
    private View mview ;
    private int width = 1024;
    private Dialog mCameraDialog = null;
    private static String mContext = "";
    //设置密码是否可见，默认为不可见
    private boolean mOldPasswordIsOpenEye = false;
    private boolean mNewPasswordIsOpenEye = false;
    private boolean mNewAgainPasswordIsOpenEye = false;
    //个人信息返回数据
    private PersonalInfoBean.PersonalInfoDataBean mPersonalInfoDataBean;

    public  static Fragment newInstance(ControlMainActivity content, String context, int iFragmentPage){
        mContext = context;
        mControlMainActivity = content;
        ModelSetting myFragment = new ModelSetting();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage,container,false);
        DisplayMetrics dm = mControlMainActivity.getResources().getDisplayMetrics(); //获取屏幕分辨率
//        height = dm.heightPixels;
        width = dm.widthPixels;
        getPersonalInfoDatas();
        SettingMainInit();
        SetttingButtonDialogInit();
        SettingPersonalStatementUpdateInit();
        SettingUserNameUpdateInit();
        SettingEmailUpdateInit();
        SettingTelNumberUpdateInit();
        SettingIdNumberUpdateInit();
        SettingPasswordUpdateInit();
        SettingAboutUsInit();
        HideAllLayout();
        if (mContext.equals("设置")) {
            RelativeLayout setting_main = mview.findViewById(R.id.setting_main);
            LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_main.getLayoutParams();
            LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
            LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
            setting_main.setLayoutParams(LP);
            setting_main.setVisibility(View.VISIBLE);
            TextView setting_logout_button = mview.findViewById(R.id.setting_logout_button);
            setting_logout_button.setVisibility(View.VISIBLE);
            TextView setting_essentialinformation_textview = mview.findViewById(R.id.setting_essentialinformation_textview);
            setting_essentialinformation_textview.setText(R.string.title_essentialinformation);
            if (mControlMainActivity.mStuId.equals("")) {
                //没登录不显示退出登录按钮
                setting_logout_button.setVisibility(View.INVISIBLE);
                //基本信息 后面改为立即登录
                setting_essentialinformation_textview.setText(R.string.title_loginclick);
            }
        } else if (mContext.equals("设置-基本信息")){
            RelativeLayout setting_essentialinformation_main = mview.findViewById(R.id.setting_essentialinformation_main);
            LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_essentialinformation_main.getLayoutParams();
            LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
            LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
            setting_essentialinformation_main.setLayoutParams(LP);
            setting_essentialinformation_main.setVisibility(View.VISIBLE);
            TextView essentialinformation_id_value_textview = mview.findViewById(R.id.essentialinformation_id_value_textview);
            essentialinformation_id_value_textview.setText("");
            TextView essentialinformation_name_value_textview = mview.findViewById(R.id.essentialinformation_name_value_textview);
            essentialinformation_name_value_textview.setText("");
            TextView essentialinformation_nick_value_textview = mview.findViewById(R.id.essentialinformation_nick_value_textview);
            essentialinformation_nick_value_textview.setText("");
            TextView essentialinformation_sign_value_textview = mview.findViewById(R.id.essentialinformation_sign_value_textview);
            essentialinformation_sign_value_textview.setText("");
            TextView essentialinformation_email_value_textview = mview.findViewById(R.id.essentialinformation_email_value_textview);
            essentialinformation_email_value_textview.setText("");
            TextView essentialinformation_tel_value_textview = mview.findViewById(R.id.essentialinformation_tel_value_textview);
            essentialinformation_tel_value_textview.setText("");
            TextView essentialinformation_idnumber_value_textview = mview.findViewById(R.id.essentialinformation_idnumber_value_textview);
            essentialinformation_idnumber_value_textview.setText("");
            if (mPersonalInfoDataBean != null) { //登录状态
                //账号 后面改为账号
                if (mPersonalInfoDataBean.login_number != null) {
                    essentialinformation_id_value_textview.setText(mPersonalInfoDataBean.login_number);
                }
                //用户名 后面改为用户名
                if (mPersonalInfoDataBean.stu_name != null) {
                    essentialinformation_name_value_textview.setText(mPersonalInfoDataBean.stu_name);
                }
                //昵称 后面改为昵称
                if (mPersonalInfoDataBean.nickname != null) {
                    essentialinformation_nick_value_textview.setText(mPersonalInfoDataBean.nickname);
                }
                //个人说明
                if (mPersonalInfoDataBean.autograph != null) {
                    essentialinformation_sign_value_textview.setText(mPersonalInfoDataBean.autograph);
                }
                //email
                if (mPersonalInfoDataBean.email != null) {
                    essentialinformation_email_value_textview.setText(mPersonalInfoDataBean.email);
                }
                //电话号码
                if (mPersonalInfoDataBean.tel != null) {
                    essentialinformation_tel_value_textview.setText(mPersonalInfoDataBean.tel);
                }
                //证件号码
                if (mPersonalInfoDataBean.ID_number != null) {
                    essentialinformation_idnumber_value_textview.setText(mPersonalInfoDataBean.ID_number);
                }
            }
        }
        return mview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //展示设置主界面
    public void SettingMainShow(int returnString){ // returnString:  0:我的
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_main = mview.findViewById(R.id.setting_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_main.setLayoutParams(LP);
        setting_main.setVisibility(View.VISIBLE);
        TextView setting_logout_button = mview.findViewById(R.id.setting_logout_button);
        setting_logout_button.setVisibility(View.VISIBLE);
        TextView setting_essentialinformation_textview = mview.findViewById(R.id.setting_essentialinformation_textview);
        setting_essentialinformation_textview.setText(R.string.title_essentialinformation);
        if (mControlMainActivity.mStuId.equals("")){
            //没登录不显示退出登录按钮
            setting_logout_button.setVisibility(View.INVISIBLE);
            //基本信息 后面改为立即登录
            setting_essentialinformation_textview.setText(R.string.title_loginclick);
        } else {
            //没登录不显示退出登录按钮
            setting_logout_button.setVisibility(View.VISIBLE);
            //基本信息 后面改为立即登录
            setting_essentialinformation_textview.setText(R.string.title_essentialinformation);
        }
    }
    //显示设置基本信息的详细界面
    public void SettingBaseInfoMainShow(int returnString){ // returnString:  0:我的  1:设置
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_essentialinformation_main = mview.findViewById(R.id.setting_essentialinformation_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_essentialinformation_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_essentialinformation_main.setLayoutParams(LP);
        setting_essentialinformation_main.setVisibility(View.VISIBLE);
        TextView essentialinformation_id_value_textview = mview.findViewById(R.id.essentialinformation_id_value_textview);
        essentialinformation_id_value_textview.setText("");
        TextView essentialinformation_name_value_textview = mview.findViewById(R.id.essentialinformation_name_value_textview);
        essentialinformation_name_value_textview.setText("");
        TextView essentialinformation_nick_value_textview = mview.findViewById(R.id.essentialinformation_nick_value_textview);
        essentialinformation_nick_value_textview.setText("");
        TextView essentialinformation_sign_value_textview = mview.findViewById(R.id.essentialinformation_sign_value_textview);
        essentialinformation_sign_value_textview.setText("");
        TextView essentialinformation_email_value_textview = mview.findViewById(R.id.essentialinformation_email_value_textview);
        essentialinformation_email_value_textview.setText("");
        TextView essentialinformation_tel_value_textview = mview.findViewById(R.id.essentialinformation_tel_value_textview);
        essentialinformation_tel_value_textview.setText("");
        TextView essentialinformation_idnumber_value_textview = mview.findViewById(R.id.essentialinformation_idnumber_value_textview);
        essentialinformation_idnumber_value_textview.setText("");
        if (mPersonalInfoDataBean != null){ //登录状态
            //账号 后面改为账号
            if (mPersonalInfoDataBean.login_number != null) {
                essentialinformation_id_value_textview.setText(mPersonalInfoDataBean.login_number);
            }
            //用户名 后面改为用户名
            if (mPersonalInfoDataBean.stu_name != null) {
                essentialinformation_name_value_textview.setText(mPersonalInfoDataBean.stu_name);
            }
            //昵称 后面改为昵称
            if (mPersonalInfoDataBean.nickname != null) {
                essentialinformation_nick_value_textview.setText(mPersonalInfoDataBean.nickname);
            }
            //个人说明
            if (mPersonalInfoDataBean.autograph != null) {
                essentialinformation_sign_value_textview.setText(mPersonalInfoDataBean.autograph);
            }
            //email
            if (mPersonalInfoDataBean.email != null) {
                essentialinformation_email_value_textview.setText(mPersonalInfoDataBean.email);
            }
            //电话号码
            if (mPersonalInfoDataBean.tel != null) {
                essentialinformation_tel_value_textview.setText(mPersonalInfoDataBean.tel);
            }
            //证件号码
            if (mPersonalInfoDataBean.ID_number != null) {
                essentialinformation_idnumber_value_textview.setText(mPersonalInfoDataBean.ID_number);
            }
        }
    }

    //显示设置基本信息的修改用户名界面
    public void SettingUserNameUpdateShow() {
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_usernameupdate_main = mview.findViewById(R.id.setting_usernameupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_usernameupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_usernameupdate_main.setLayoutParams(LP);
        setting_usernameupdate_main.setVisibility(View.VISIBLE);
        EditText setting_usernameupdate_edittext = mview.findViewById(R.id.setting_usernameupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_usernameupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.stu_name != null) {
            setting_usernameupdate_edittext.setText(mPersonalInfoDataBean.stu_name);
        } else {
            setting_usernameupdate_edittext.setText("");
        }
        setting_usernameupdate_edittext.setEnabled(true);
        setting_usernameupdate_edittext.setFocusable(true);
        setting_usernameupdate_edittext.setFocusableInTouchMode(true);
        setting_usernameupdate_edittext.requestFocus();
        setting_usernameupdate_edittext.setSelection(setting_usernameupdate_edittext.getText().toString().length());
    }

    //显示设置基本信息的修改昵称界面
    public void SettingUserNickUpdateShow() {
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_usernickupdate_main = mview.findViewById(R.id.setting_usernickupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_usernickupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_usernickupdate_main.setLayoutParams(LP);
        setting_usernickupdate_main.setVisibility(View.VISIBLE);
        EditText setting_usernickupdate_edittext = mview.findViewById(R.id.setting_usernickupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_usernickupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.nickname == null) {
            setting_usernickupdate_edittext.setText("");
        } else {
            setting_usernickupdate_edittext.setText(mPersonalInfoDataBean.nickname);
        }
        setting_usernickupdate_edittext.setEnabled(true);
        setting_usernickupdate_edittext.setFocusable(true);
        setting_usernickupdate_edittext.setFocusableInTouchMode(true);
        setting_usernickupdate_edittext.requestFocus();
        setting_usernickupdate_edittext.setSelection(setting_usernickupdate_edittext.getText().toString().length());
    }

    //显示设置基本信息的修改个人说明界面
    public void SettingPersonalStatementUpdateShow() {
        if (mview == null ){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_personalstatementupdate_main = mview.findViewById(R.id.setting_personalstatementupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_personalstatementupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_personalstatementupdate_main.setLayoutParams(LP);
        setting_personalstatementupdate_main.setVisibility(View.VISIBLE);
        EditText setting_personalstatementupdate_edittext = mview.findViewById(R.id.setting_personalstatementupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_personalstatementupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.autograph == null) {
            setting_personalstatementupdate_edittext.setText("");
        } else {
            setting_personalstatementupdate_edittext.setText(mPersonalInfoDataBean.autograph);
        }
        setting_personalstatementupdate_edittext.setEnabled(true);
        setting_personalstatementupdate_edittext.setFocusable(true);
        setting_personalstatementupdate_edittext.setFocusableInTouchMode(true);
        setting_personalstatementupdate_edittext.requestFocus();
        setting_personalstatementupdate_edittext.setSelection(setting_personalstatementupdate_edittext.getText().toString().length());
    }

    //显示设置基本信息的修改邮箱界面
    public void SettingEmailUpdateShow() {
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_emailupdate_main = mview.findViewById(R.id.setting_emailupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_emailupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_emailupdate_main.setLayoutParams(LP);
        setting_emailupdate_main.setVisibility(View.VISIBLE);
        EditText setting_emailupdate_edittext = mview.findViewById(R.id.setting_emailupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_emailupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.email == null) {
            setting_emailupdate_edittext.setText("");
        } else {
            setting_emailupdate_edittext.setText(mPersonalInfoDataBean.email);
        }
        setting_emailupdate_edittext.setEnabled(true);
        setting_emailupdate_edittext.setFocusable(true);
        setting_emailupdate_edittext.setFocusableInTouchMode(true);
        setting_emailupdate_edittext.requestFocus();
        setting_emailupdate_edittext.setSelection(setting_emailupdate_edittext.getText().toString().length());
    }

    //显示设置基本信息的修改手机号码界面
    public void SettingTelNumberUpdateShow() {
        if (mview == null ){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_telnumberupdate_main = mview.findViewById(R.id.setting_telnumberupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_telnumberupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_telnumberupdate_main.setLayoutParams(LP);
        setting_telnumberupdate_main.setVisibility(View.VISIBLE);
        EditText setting_telnumberupdate_edittext = mview.findViewById(R.id.setting_telnumberupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_telnumberupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.tel == null) {
            setting_telnumberupdate_edittext.setText("");
        } else {
            setting_telnumberupdate_edittext.setText(mPersonalInfoDataBean.tel);
        }
        setting_telnumberupdate_edittext.setEnabled(true);
        setting_telnumberupdate_edittext.setFocusable(true);
        setting_telnumberupdate_edittext.setFocusableInTouchMode(true);
        setting_telnumberupdate_edittext.requestFocus();
        setting_telnumberupdate_edittext.setSelection(setting_telnumberupdate_edittext.getText().toString().length());
    }

    //显示设置基本信息的修改证件号码界面
    public void SettingIdNumberUpdateShow() {
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_idnumberupdate_main = mview.findViewById(R.id.setting_idnumberupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_idnumberupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_idnumberupdate_main.setLayoutParams(LP);
        setting_idnumberupdate_main.setVisibility(View.VISIBLE);
        EditText setting_idnumberupdate_edittext = mview.findViewById(R.id.setting_idnumberupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_idnumberupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.ID_number == null) {
            setting_idnumberupdate_edittext.setText("");
        } else {
            setting_idnumberupdate_edittext.setText(mPersonalInfoDataBean.ID_number);
        }
        setting_idnumberupdate_edittext.setEnabled(true);
        setting_idnumberupdate_edittext.setFocusable(true);
        setting_idnumberupdate_edittext.setFocusableInTouchMode(true);
        setting_idnumberupdate_edittext.requestFocus();
        setting_idnumberupdate_edittext.setSelection(setting_idnumberupdate_edittext.getText().toString().length());
    }

    //显示设置基本信息的修改用户密码界面
    public void SettingPasswordUpdateShow() {
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout setting_passwordupdate_main = mview.findViewById(R.id.setting_passwordupdate_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_passwordupdate_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        setting_passwordupdate_main.setLayoutParams(LP);
        setting_passwordupdate_main.setVisibility(View.VISIBLE);
        EditText setting_passwordupdateoldpassword_edittext = mview.findViewById(R.id.setting_passwordupdateoldpassword_edittext);
        setting_passwordupdateoldpassword_edittext.setEnabled(true);
        setting_passwordupdateoldpassword_edittext.setFocusable(true);
        setting_passwordupdateoldpassword_edittext.setFocusableInTouchMode(true);
        setting_passwordupdateoldpassword_edittext.requestFocus();
        setting_passwordupdateoldpassword_edittext.setSelection(setting_passwordupdateoldpassword_edittext.getText().toString().length());
        EditText setting_passwordupdatenew_edittext = mview.findViewById(R.id.setting_passwordupdatenew_edittext);
        EditText setting_passwordupdatenewagain_edittext = mview.findViewById(R.id.setting_passwordupdatenewagain_edittext);
        //设置密码不可见
        mOldPasswordIsOpenEye = false;
        mNewPasswordIsOpenEye = false;
        mNewAgainPasswordIsOpenEye = false;
        setting_passwordupdateoldpassword_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setting_passwordupdatenew_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setting_passwordupdatenewagain_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setting_passwordupdateoldpassword_edittext.setText("");
        setting_passwordupdatenew_edittext.setText("");
        setting_passwordupdatenewagain_edittext.setText("");
    }

    //显示设置-关于我们界面
    public void SettingAboutUsShow() {
        if (mview == null){
            return;
        }
        HideAllLayout();
        RelativeLayout aboutus_main = mview.findViewById(R.id.aboutus_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) aboutus_main.getLayoutParams();
        LP.width = LinearLayout.LayoutParams.MATCH_PARENT;
        LP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        aboutus_main.setLayoutParams(LP);
        aboutus_main.setVisibility(View.VISIBLE);
        //查询版本号，如果有新的版本号 在版本检测后面添加红点
        getAboutUsInfoDatas();
    }

    //隐藏所有图层
    private void HideAllLayout(){
        RelativeLayout setting_main = mview.findViewById(R.id.setting_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) setting_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_main.setLayoutParams(LP);
        setting_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_essentialinformation_main = mview.findViewById(R.id.setting_essentialinformation_main);
        LP = (LinearLayout.LayoutParams) setting_essentialinformation_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_essentialinformation_main.setLayoutParams(LP);
        setting_essentialinformation_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_usernameupdate_main = mview.findViewById(R.id.setting_usernameupdate_main);
        LP = (LinearLayout.LayoutParams) setting_usernameupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_usernameupdate_main.setLayoutParams(LP);
        setting_usernameupdate_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_personalstatementupdate_main = mview.findViewById(R.id.setting_personalstatementupdate_main);
        LP = (LinearLayout.LayoutParams) setting_personalstatementupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_personalstatementupdate_main.setLayoutParams(LP);
        setting_personalstatementupdate_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_emailupdate_main = mview.findViewById(R.id.setting_emailupdate_main);
        LP = (LinearLayout.LayoutParams) setting_emailupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_emailupdate_main.setLayoutParams(LP);
        setting_emailupdate_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_telnumberupdate_main = mview.findViewById(R.id.setting_telnumberupdate_main);
        LP = (LinearLayout.LayoutParams) setting_telnumberupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_telnumberupdate_main.setLayoutParams(LP);
        setting_telnumberupdate_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_idnumberupdate_main = mview.findViewById(R.id.setting_idnumberupdate_main);
        LP = (LinearLayout.LayoutParams) setting_idnumberupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_idnumberupdate_main.setLayoutParams(LP);
        setting_idnumberupdate_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_passwordupdate_main = mview.findViewById(R.id.setting_passwordupdate_main);
        LP = (LinearLayout.LayoutParams) setting_passwordupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_passwordupdate_main.setLayoutParams(LP);
        setting_passwordupdate_main.setVisibility(View.INVISIBLE);
        RelativeLayout aboutus_main = mview.findViewById(R.id.aboutus_main);
        LP = (LinearLayout.LayoutParams) aboutus_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        aboutus_main.setLayoutParams(LP);
        aboutus_main.setVisibility(View.INVISIBLE);
        RelativeLayout setting_usernickupdate_main = mview.findViewById(R.id.setting_usernickupdate_main);
        LP = (LinearLayout.LayoutParams) setting_usernickupdate_main.getLayoutParams();
        LP.width = 0;
        LP.height = 0;
        setting_usernickupdate_main.setLayoutParams(LP);
        setting_usernickupdate_main.setVisibility(View.INVISIBLE);
    }

    //初始化设置主界面
    public void SettingMainInit(){
        //允许非WiFi网络播放/缓存视频
        ModelSwitchButton setting_allownonwifiplay_go = mview.findViewById(R.id.setting_allownonwifiplay_go);
        setting_allownonwifiplay_go.setChecked(true);
        setting_allownonwifiplay_go.setOnCheckedChangeListener((view,isChecked) ->{
            //TODO do your job
            mControlMainActivity.onClickSettingAllowNonWifiPlay(isChecked);
        });
        TextView yijian = mview.findViewById(R.id.yijian);
        yijian.setClickable(true);
        yijian.setOnClickListener(v->{
            //点击意见反馈
            Dialog dialog = new Dialog(mControlMainActivity, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.dialog_input_sure_cancel);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView button_cancel = dialog.findViewById(R.id.button_cancel);
            button_cancel.setOnClickListener(v1->{dialog.dismiss();});
            // 确认更新
            dialog.findViewById(R.id.button_sure).setOnClickListener(v1 -> {
                Toast.makeText(mControlMainActivity,"意见已提交",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    //照片选择对话框
    private void SetttingButtonDialogInit() {
        mCameraDialog = new Dialog(mControlMainActivity, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(mControlMainActivity).inflate(
                R.layout.modelsetting_buttondialog, null);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
    }
    //照片选择对话框展示
    public void SetttingButtonDialogShow(){
        if (mCameraDialog != null){
            mCameraDialog.show();
        }
    }
    //照片选择对话框取消
    public void SetttingButtonDialogCancel(){
        if (mCameraDialog != null){
            mCameraDialog.cancel();
        }
    }

    //设置界面-修改用户名
    private void SettingUserNameUpdateInit(){
        //修改名称的书写框
        EditText setting_usernameupdate_edittext = mview.findViewById(R.id.setting_usernameupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_usernameupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.stu_name != null) {
            setting_usernameupdate_edittext.setText("");
        } else {
            setting_usernameupdate_edittext.setText(mPersonalInfoDataBean.stu_name);
        }
        setting_usernameupdate_edittext.setEnabled(true);
        setting_usernameupdate_edittext.setFocusable(true);
        setting_usernameupdate_edittext.setFocusableInTouchMode(true);
        setting_usernameupdate_edittext.requestFocus();
        setting_usernameupdate_edittext.setSelection(setting_usernameupdate_edittext.getText().toString().length());
    }

    //设置界面-修改用户名-清除
    public void SettingUserNameUpdateClear(){
        if (mview == null){
            return;
        }
        EditText setting_usernameupdate_edittext = mview.findViewById(R.id.setting_usernameupdate_edittext);
        setting_usernameupdate_edittext.setText("");
    }

    //设置界面-获取用户名
    public String UserNameGet(){
        EditText setting_usernameupdate_edittext = mview.findViewById(R.id.setting_usernameupdate_edittext);
        return setting_usernameupdate_edittext.getText().toString();
    }

    //设置界面-获取昵称
    public String UserNickGet(){
        EditText setting_usernickupdate_edittext = mview.findViewById(R.id.setting_usernickupdate_edittext);
        return setting_usernickupdate_edittext.getText().toString();
    }

    //设置界面-修改昵称-清除
    public void SettingUserNickUpdateClear(){
        if (mview == null){
            return;
        }
        EditText setting_usernickupdate_edittext = mview.findViewById(R.id.setting_usernickupdate_edittext);
        setting_usernickupdate_edittext.setText("");
    }

    //设置界面-个人说明初始化
    private void SettingPersonalStatementUpdateInit(){
        //修改个人说明的书写框
        EditText setting_personalstatementupdate_edittext = mview.findViewById(R.id.setting_personalstatementupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_personalstatementupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.autograph != null) {
            setting_personalstatementupdate_edittext.setText("");
        } else {
            setting_personalstatementupdate_edittext.setText(mPersonalInfoDataBean.autograph);
        }
        setting_personalstatementupdate_edittext.setEnabled(true);
        setting_personalstatementupdate_edittext.setFocusable(true);
        setting_personalstatementupdate_edittext.setFocusableInTouchMode(true);
        setting_personalstatementupdate_edittext.requestFocus();
        //设置EditText的显示方式为多行文本输入
        setting_personalstatementupdate_edittext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //改变默认的单行模式
        setting_personalstatementupdate_edittext.setSingleLine(false);
        //水平滚动设置为False
        setting_personalstatementupdate_edittext.setHorizontallyScrolling(false);
        setting_personalstatementupdate_edittext.setSelection(setting_personalstatementupdate_edittext.getText().toString().length());
    }

    //设置界面-获取个人说明
    public String PersonalStatementGet(){
        if (mview == null){
            return "";
        }
        EditText setting_personalstatementupdate_edittext = mview.findViewById(R.id.setting_personalstatementupdate_edittext);
        return setting_personalstatementupdate_edittext.getText().toString();
    }

    //设置界面-修改邮箱
    private void SettingEmailUpdateInit(){
        //修改邮箱的书写框
        EditText setting_emailupdate_edittext = mview.findViewById(R.id.setting_emailupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_emailupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.email != null) {
            setting_emailupdate_edittext.setText("");
        } else {
            setting_emailupdate_edittext.setText(mPersonalInfoDataBean.email);
        }
        setting_emailupdate_edittext.setEnabled(true);
        setting_emailupdate_edittext.setFocusable(true);
        setting_emailupdate_edittext.setFocusableInTouchMode(true);
        setting_emailupdate_edittext.requestFocus();
        setting_emailupdate_edittext.setSelection(setting_emailupdate_edittext.getText().toString().length());
    }

    //设置界面-修改邮箱-清除
    public void SettingEmailUpdateClear(){
        if (mview == null){
            return;
        }
        EditText setting_emailupdate_edittext = mview.findViewById(R.id.setting_emailupdate_edittext);
        setting_emailupdate_edittext.setText("");
    }

    //设置界面-获取邮箱
    public String EmailGet(){
        if (mview == null){
            return "";
        }
        EditText setting_emailupdate_edittext = mview.findViewById(R.id.setting_emailupdate_edittext);
        return setting_emailupdate_edittext.getText().toString();
    }

    //设置界面-修改手机号
    private void SettingTelNumberUpdateInit(){
        //修改手机号的书写框
        EditText setting_telnumberupdate_edittext = mview.findViewById(R.id.setting_telnumberupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_telnumberupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.tel != null) {
            setting_telnumberupdate_edittext.setText("");
        } else {
            setting_telnumberupdate_edittext.setText(mPersonalInfoDataBean.tel);
        }
        setting_telnumberupdate_edittext.setEnabled(true);
        setting_telnumberupdate_edittext.setFocusable(true);
        setting_telnumberupdate_edittext.setFocusableInTouchMode(true);
        setting_telnumberupdate_edittext.requestFocus();
        setting_telnumberupdate_edittext.setSelection(setting_telnumberupdate_edittext.getText().toString().length());
    }

    //设置界面-修改手机号-清除
    public void SettingTelNumberUpdateClear(){
        if (mview == null){
            return;
        }
        EditText setting_telnumberupdate_edittext = mview.findViewById(R.id.setting_telnumberupdate_edittext);
        setting_telnumberupdate_edittext.setText("");
    }

    //设置界面-获取手机号
    public String TelNumberGet(){
        if (mview == null){
            return "";
        }
        EditText setting_telnumberupdate_edittext = mview.findViewById(R.id.setting_telnumberupdate_edittext);
        return setting_telnumberupdate_edittext.getText().toString();
    }

    //设置界面-修改证件号码
    private void SettingIdNumberUpdateInit(){
        //修改证件号码的书写框
        EditText setting_idnumberupdate_edittext = mview.findViewById(R.id.setting_idnumberupdate_edittext);
        if (mPersonalInfoDataBean == null){
            setting_idnumberupdate_edittext.setText("");
        } else if (mPersonalInfoDataBean.ID_number != null) {
            setting_idnumberupdate_edittext.setText("");
        } else {
            setting_idnumberupdate_edittext.setText(mPersonalInfoDataBean.ID_number);
        }
        setting_idnumberupdate_edittext.setEnabled(true);
        setting_idnumberupdate_edittext.setFocusable(true);
        setting_idnumberupdate_edittext.setFocusableInTouchMode(true);
        setting_idnumberupdate_edittext.requestFocus();
        setting_idnumberupdate_edittext.setSelection(setting_idnumberupdate_edittext.getText().toString().length());
    }

    //设置界面-修改证件号码-清除
    public void SettingIdNumberUpdateClear(){
        if (mview == null){
            return;
        }
        EditText setting_idnumberupdate_edittext = mview.findViewById(R.id.setting_idnumberupdate_edittext);
        setting_idnumberupdate_edittext.setText("");
    }

    //设置界面-获取证件号码
    public String IdNumberGet(){
        if (mview == null){
            return "";
        }
        EditText setting_idnumberupdate_edittext = mview.findViewById(R.id.setting_idnumberupdate_edittext);
        return setting_idnumberupdate_edittext.getText().toString();
    }

    //设置界面-修改密码
    private void SettingPasswordUpdateInit(){
        //修改密码的书写框（旧密码）
        EditText setting_passwordupdateoldpassword_edittext = mview.findViewById(R.id.setting_passwordupdateoldpassword_edittext);
        setting_passwordupdateoldpassword_edittext.setEnabled(true);
        setting_passwordupdateoldpassword_edittext.setFocusable(true);
        setting_passwordupdateoldpassword_edittext.setFocusableInTouchMode(true);
        setting_passwordupdateoldpassword_edittext.requestFocus();
        setting_passwordupdateoldpassword_edittext.setSelection(setting_passwordupdateoldpassword_edittext.getText().toString().length());
        //旧密码是否明码显示
        ImageView setting_passwordupdateoldpassword_isopeneye = mview.findViewById(R.id.setting_passwordupdateoldpassword_isopeneye);
        //修改密码的书写框（新密码）
        EditText setting_passwordupdatenew_edittext = mview.findViewById(R.id.setting_passwordupdatenew_edittext);
        setting_passwordupdatenew_edittext.setEnabled(true);
        setting_passwordupdatenew_edittext.setFocusable(true);
        setting_passwordupdatenew_edittext.setFocusableInTouchMode(true);
        setting_passwordupdatenew_edittext.requestFocus();
        setting_passwordupdatenew_edittext.setSelection(setting_passwordupdatenew_edittext.getText().toString().length());
        //修改密码的书写框（确认密码）
        EditText setting_passwordupdatenewagain_edittext = mview.findViewById(R.id.setting_passwordupdatenewagain_edittext);
        setting_passwordupdatenewagain_edittext.setEnabled(true);
        setting_passwordupdatenewagain_edittext.setFocusable(true);
        setting_passwordupdatenewagain_edittext.setFocusableInTouchMode(true);
        setting_passwordupdatenewagain_edittext.requestFocus();
        setting_passwordupdatenewagain_edittext.setSelection(setting_passwordupdatenewagain_edittext.getText().toString().length());
        //确认密码是否明码显示
        ImageView setting_passwordupdatenewagain_isopeneye = mview.findViewById(R.id.setting_passwordupdatenewagain_isopeneye);
        //确认密码是否明码显示
        ImageView setting_passwordupdatenew_isopeneye = mview.findViewById(R.id.setting_passwordupdatenewagain_isopeneye);
        //设置密码不可见
        setting_passwordupdateoldpassword_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setting_passwordupdatenew_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setting_passwordupdatenewagain_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //密码是否可见
        setting_passwordupdateoldpassword_isopeneye.setOnClickListener(v ->{
            if(!mOldPasswordIsOpenEye) {
                setting_passwordupdateoldpassword_isopeneye.setSelected(true);
                mOldPasswordIsOpenEye = true;
                //密码可见
                setting_passwordupdateoldpassword_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                setting_passwordupdateoldpassword_isopeneye.setSelected(false);
                mOldPasswordIsOpenEye = false;
                //密码不可见
                setting_passwordupdateoldpassword_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            setting_passwordupdateoldpassword_edittext.setSelection(setting_passwordupdateoldpassword_edittext.getText().toString().length());
        });
        setting_passwordupdatenew_isopeneye.setOnClickListener(v ->{
            if(!mNewPasswordIsOpenEye) {
                setting_passwordupdatenew_isopeneye.setSelected(true);
                mNewPasswordIsOpenEye = true;
                //密码可见
                setting_passwordupdatenew_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                setting_passwordupdatenew_isopeneye.setSelected(false);
                mNewPasswordIsOpenEye = false;
                //密码不可见
                setting_passwordupdatenew_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            setting_passwordupdatenew_edittext.setSelection(setting_passwordupdatenew_edittext.getText().toString().length());
        });
        setting_passwordupdatenewagain_isopeneye.setOnClickListener(v ->{
            if(!mNewAgainPasswordIsOpenEye) {
                setting_passwordupdatenewagain_isopeneye.setSelected(true);
                mNewAgainPasswordIsOpenEye = true;
                //密码可见
                setting_passwordupdatenewagain_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                setting_passwordupdatenewagain_isopeneye.setSelected(false);
                mNewAgainPasswordIsOpenEye = false;
                //密码不可见
                setting_passwordupdatenewagain_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
            setting_passwordupdatenewagain_edittext.setSelection(setting_passwordupdatenewagain_edittext.getText().toString().length());
        });
    }

    //设置界面-保存新密码
    public void NewPasswordSave(){ //返回值  -1：其他错误 1：原密码输入不正确 2：两次新密码不一致 0：保存新密码
        if (mview == null){
            return ;
        }
        EditText setting_passwordupdateoldpassword_edittext = mview.findViewById(R.id.setting_passwordupdateoldpassword_edittext);
        EditText setting_passwordupdatenew_edittext = mview.findViewById(R.id.setting_passwordupdatenew_edittext);
        EditText setting_passwordupdatenewagain_edittext = mview.findViewById(R.id.setting_passwordupdatenewagain_edittext);
        String origin_stu_pass = setting_passwordupdateoldpassword_edittext.getText().toString();
        String now_stu_pass = setting_passwordupdatenew_edittext.getText().toString();
        String now_stu_pass1 = setting_passwordupdatenewagain_edittext.getText().toString();
        if (origin_stu_pass == null || now_stu_pass == null || now_stu_pass1 == null){
            Toast.makeText(mControlMainActivity,"系统错误，请重新尝试！",Toast.LENGTH_LONG).show();
            return ;
        }
        if (now_stu_pass1.length()<6 || now_stu_pass.length()<6 || origin_stu_pass.length()<6){
            Toast.makeText(mControlMainActivity,"密码不能少于6位数，请重新输入！",Toast.LENGTH_LONG).show();
            return ;
        }
        if (!now_stu_pass1.equals(now_stu_pass)){ //两次新密码输入不一致
            Toast.makeText(mControlMainActivity,"新密码两次输入不一致，请重新输入！",Toast.LENGTH_LONG).show();
            return ;
        }
        UpdateStuPass(origin_stu_pass,now_stu_pass);
        return ;
    }

    public String NewPasswordGet(){
//        return mUserInfo.mUserPassword;
        return "";
    }

    //设置-关于我们界面初始化
    private void SettingAboutUsInit(){
        //主要参数
        int layoutheight = width / 10;
        int leftMargin = width / 25;
        int rightMargin = width / 40;
        int bottomMargin = width / 35;
        RelativeLayout aboutus_returnRelativeLayout = mview.findViewById(R.id.aboutus_returnRelativeLayout);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) aboutus_returnRelativeLayout.getLayoutParams();
        lp.topMargin = leftMargin;
        lp.leftMargin = leftMargin;
        lp.rightMargin = rightMargin;
        lp.bottomMargin = bottomMargin;
        lp.height = layoutheight;
        aboutus_returnRelativeLayout.setLayoutParams(lp);
        //返回
        ImageView aboutus_return_button = mview.findViewById(R.id.aboutus_return_button);
        lp = (RelativeLayout.LayoutParams) aboutus_return_button.getLayoutParams();
        lp.height = width / 15;
        lp.width = width / 15;
        aboutus_return_button.setLayoutParams(lp);
        //火种logo
        ControllerCustomRoundAngleImageView aboutus_huozhonglogo = mview.findViewById(R.id.aboutus_huozhonglogo);
        aboutus_huozhonglogo.setImageDrawable(getResources().getDrawable(R.mipmap.logo2));
        lp = (RelativeLayout.LayoutParams) aboutus_huozhonglogo.getLayoutParams();
        lp.topMargin = width / 11;
        lp.height = width / 3;
        lp.width = width / 3;
        aboutus_huozhonglogo.setLayoutParams(lp);
        //应用名称
        TextView aboutus_appname = mview.findViewById(R.id.aboutus_appname);
        lp = (RelativeLayout.LayoutParams) aboutus_appname.getLayoutParams();
        lp.topMargin = width / 11;
        aboutus_appname.setLayoutParams(lp);
        //应用版本号
        PackageManager manager = mControlMainActivity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(mControlMainActivity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView aboutus_version = mview.findViewById(R.id.aboutus_version);
        if (info != null) {
            aboutus_version.setText(getResources().getString(R.string.title_version) + " " + info.versionName);
        }
        lp = (RelativeLayout.LayoutParams) aboutus_version.getLayoutParams();
        lp.bottomMargin = width / 11;
        aboutus_version.setLayoutParams(lp);
        //功能列表
        //软件版本
        TextView aboutus_checknewversion_textview = mview.findViewById(R.id.aboutus_checknewversion_textview);
        lp = (RelativeLayout.LayoutParams) aboutus_checknewversion_textview.getLayoutParams();
        lp.topMargin = bottomMargin;
        lp.height = layoutheight;
        lp.leftMargin = leftMargin;
        lp.bottomMargin = bottomMargin;
        aboutus_checknewversion_textview.setLayoutParams(lp);
        ImageView aboutus_checknewversion_new = mview.findViewById(R.id.aboutus_checknewversion_new);
        lp = (RelativeLayout.LayoutParams) aboutus_checknewversion_new.getLayoutParams();
        lp.topMargin = (layoutheight + bottomMargin * 2 - width / 12) / 2;
        lp.width = width / 12;
        lp.height = width / 12;
        lp.leftMargin = leftMargin / 2;
        lp.bottomMargin = (layoutheight + bottomMargin * 2 - width / 12) / 2;
        aboutus_checknewversion_new.setLayoutParams(lp);
        TextView aboutus_checknewversion_new_textview = mview.findViewById(R.id.aboutus_checknewversion_new_textview);
        lp = (RelativeLayout.LayoutParams) aboutus_checknewversion_new_textview.getLayoutParams();
        lp.topMargin = (layoutheight + bottomMargin * 2) / 3;
        lp.rightMargin = leftMargin / 2;
        lp.bottomMargin = (layoutheight + bottomMargin * 2) / 3;
        aboutus_checknewversion_new_textview.setLayoutParams(lp);
        ImageView aboutus_checknewversion_go = mview.findViewById(R.id.aboutus_checknewversion_go);
        lp = (RelativeLayout.LayoutParams) aboutus_checknewversion_go.getLayoutParams();
        lp.topMargin = (layoutheight + bottomMargin * 2 - width / 35) / 2;
        lp.rightMargin = rightMargin;
        lp.height = width / 25;
        lp.width = width / 15;
        lp.bottomMargin = (layoutheight + bottomMargin * 2 - width / 25) / 2;
        aboutus_checknewversion_go.setLayoutParams(lp);
        //版权
        LinearLayout aboutus_agreeTerms_layout = mview.findViewById(R.id.aboutus_agreeTerms_layout);
        lp = (RelativeLayout.LayoutParams) aboutus_agreeTerms_layout.getLayoutParams();
        lp.bottomMargin = layoutheight;
        aboutus_agreeTerms_layout.setLayoutParams(lp);
        TextView aboutus_agreeTerms = mview.findViewById(R.id.aboutus_agreeTerms);
        setHtmlStyle(aboutus_agreeTerms);
        aboutus_agreeTerms.setAutoLinkMask(0);
        aboutus_agreeTerms.setLinkTextColor(getResources().getColor(R.color.blue));
        aboutus_agreeTerms.setMovementMethod(LinkMovementMethod.getInstance()); //设置超链接为可点击状态
        TextView aboutus_agreeTerms_1 = mview.findViewById(R.id.aboutus_agreeTerms_1);
        setHtmlStyle(aboutus_agreeTerms_1);
        aboutus_agreeTerms_1.setAutoLinkMask(0);
        aboutus_agreeTerms_1.setLinkTextColor(getResources().getColor(R.color.blue));
        aboutus_agreeTerms_1.setMovementMethod(LinkMovementMethod.getInstance()); //设置超链接为可点击状态
    }

    //去掉链接的下划线
    private static void setHtmlStyle(TextView textView) {
        Spannable s = new Spannable.Factory().newSpannable(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            super.onClick(widget);
        }
    }

    //设置-更新个人信息
    public void UpdataPersonInfo(String type){
        if (type == null){
            return;
        }
        switch (type){
            case "username":
                String username = UserNameGet();
                setPersonalInfoDatas(null,username,null,null,null,null);
                break;
            case "usernick":
                String usernick = UserNickGet();
                setPersonalInfoDatas(usernick,null,null,null,null,null);
                break;
            case "usersign":
                String usersign = PersonalStatementGet();
                setPersonalInfoDatas(null,null,usersign,null,null,null);
                break;
            case "email":
                String email = EmailGet();
                setPersonalInfoDatas(null,null,null,null,email,null);
                break;
            case "telnumber":
                String telnumber = TelNumberGet();
                //先查一下改过的手机号码有没有其他学员在使用
                checkModifyingTel(telnumber);
                break;
            case "idnumber":
                String idnumber = IdNumberGet();
                setPersonalInfoDatas(null,null,null,null,null,idnumber);
                break;
        }
    }

    //设置-获取个人信息
    public void getPersonalInfoDatas() {
        if (mControlMainActivity.mStuId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .client(ModelObservableInterface.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,Integer> paramsMap= new HashMap<>();
        paramsMap.put("stu_id",Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<PersonalInfoBean> call = modelObservableInterface.queryModelSettingPersonInfo(body);
        call.enqueue(new Callback<PersonalInfoBean>() {
            @Override
            public void onResponse(Call<PersonalInfoBean> call, Response<PersonalInfoBean> response) {
                PersonalInfoBean personalInfoBean = response.body();
                if (personalInfoBean == null){
                    Toast.makeText(mControlMainActivity,"获取个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(personalInfoBean.code,personalInfoBean.msg)){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                //网络请求数据成功
                mPersonalInfoDataBean = personalInfoBean.getData();
                if (mview == null){
                    Toast.makeText(mControlMainActivity,"获取个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                TextView essentialinformation_id_value_textview = mview.findViewById(R.id.essentialinformation_id_value_textview);
                essentialinformation_id_value_textview.setText("");
                TextView essentialinformation_name_value_textview = mview.findViewById(R.id.essentialinformation_name_value_textview);
                essentialinformation_name_value_textview.setText("");
                TextView essentialinformation_nick_value_textview = mview.findViewById(R.id.essentialinformation_nick_value_textview);
                essentialinformation_nick_value_textview.setText("");
                TextView essentialinformation_sign_value_textview = mview.findViewById(R.id.essentialinformation_sign_value_textview);
                essentialinformation_sign_value_textview.setText("");
                TextView essentialinformation_email_value_textview = mview.findViewById(R.id.essentialinformation_email_value_textview);
                essentialinformation_email_value_textview.setText("");
                TextView essentialinformation_tel_value_textview = mview.findViewById(R.id.essentialinformation_tel_value_textview);
                essentialinformation_tel_value_textview.setText("");
                TextView essentialinformation_idnumber_value_textview = mview.findViewById(R.id.essentialinformation_idnumber_value_textview);
                essentialinformation_idnumber_value_textview.setText("");
                if (mPersonalInfoDataBean != null){ //登录状态
//                    //账号 后面改为账号
//                    if (mPersonalInfoDataBean.login_number != null) {
//                        essentialinformation_id_value_textview.setText(mPersonalInfoDataBean.login_number);
//                    }
                    //用户名 后面改为用户名
                    if (mPersonalInfoDataBean.stu_name != null) {
                        essentialinformation_name_value_textview.setText(mPersonalInfoDataBean.stu_name);
                    }
                    //昵称 后面改为昵称
                    if (mPersonalInfoDataBean.nickname != null) {
                        essentialinformation_nick_value_textview.setText(mPersonalInfoDataBean.nickname);
                    }
                    //个人说明
                    if (mPersonalInfoDataBean.autograph != null) {
                        essentialinformation_sign_value_textview.setText(mPersonalInfoDataBean.autograph);
                    }
                    //email
                    if (mPersonalInfoDataBean.email != null) {
                        essentialinformation_email_value_textview.setText(mPersonalInfoDataBean.email);
                    }
                    //电话号码
                    if (mPersonalInfoDataBean.tel != null) {
                        mPersonalInfoDataBean.login_number = mPersonalInfoDataBean.tel;
                        essentialinformation_tel_value_textview.setText(mPersonalInfoDataBean.tel);
                        essentialinformation_id_value_textview.setText(mPersonalInfoDataBean.login_number);
                    }
                    //证件号码
                    if (mPersonalInfoDataBean.ID_number != null) {
                        essentialinformation_idnumber_value_textview.setText(mPersonalInfoDataBean.ID_number);
                    }
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<PersonalInfoBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"获取个人信息超时",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //设置-设置个人信息
    private void setPersonalInfoDatas(String nickname, String username, String user_sign, String phone, String email, String idCardNum) {
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String,String> paramsMap = new HashMap<>();
        paramsMap.put("nickname",nickname);
        paramsMap.put("stu_name",username);
        paramsMap.put("autograph",user_sign);
        paramsMap.put("tel",phone);
        paramsMap.put("email",email);
        paramsMap.put("ID_number",idCardNum);
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.updataModelSettingPersonInfo(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response.code() != 200){
                    Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (loginBean.getErrorCode() != 200 ){
                    Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                Toast.makeText(mControlMainActivity,"修改成功",Toast.LENGTH_LONG).show();
                getPersonalInfoDatas();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //检测手机号是否可用
    private void checkModifyingTel(String phone) {
        if (mControlMainActivity.mStuId.equals("")){
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

        HashMap<String,String> paramsMap = new HashMap<>();
        paramsMap.put("tel",phone);
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.checkModifyingTel(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response.code() != 200){
                    Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (loginBean.getErrorMsg().equals("ok")){
                    setPersonalInfoDatas(null,null,null,phone,null,null);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else {
                    Toast.makeText(mControlMainActivity,"修改失败，该手机号已被其他学员使用！",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"修改个人信息失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取关于我们的数据
    private void getAboutUsInfoDatas() {
        //获取版本号，这个版本号为未更新的版本号
        String verCode = "-1";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mControlMainActivity.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    mControlMainActivity.getPackageName(), 0);
            verCode = packInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        final Observable<ModelObservableInterface.BaseBean> data =
                modelObservableInterface.queryAndroidVersion();
        String finalVerCode = verCode;
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ModelObservableInterface.BaseBean value) {
                        //网络请求数据成功
                        if (value == null || mview == null){
                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
                            TextView aboutus_checknewversion_new_textview = mview.findViewById(R.id.aboutus_checknewversion_new_textview);
                            aboutus_checknewversion_new_textview.setText(finalVerCode + "");
                            return;
                        }
                        if (value.getData() != null) {
                            Map<String,Object> data =  value.getData();
                            String version_num = String.valueOf(data.get("version_num"));
                            String download_address = String.valueOf(data.get("download_address"));
                            if (version_num == null || download_address == null){
                                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                                TextView aboutus_checknewversion_new_textview = mview.findViewById(R.id.aboutus_checknewversion_new_textview);
                                aboutus_checknewversion_new_textview.setText(finalVerCode + "");
                                return;
                            }
                            TextView aboutus_checknewversion_new_textview = mview.findViewById(R.id.aboutus_checknewversion_new_textview);
                            aboutus_checknewversion_new_textview.setText(version_num);
                            aboutus_checknewversion_new_textview.setHint(download_address);
                            //更新app版本号比对，info新版本号和当前的版本号versionCode做对比，如果新版本号大于本版本就运行更新方法showUpdataDialog()
                            if ( Float.valueOf(version_num) <= Float.valueOf(finalVerCode)) {
                                ImageView aboutus_checknewversion_new = mview.findViewById(R.id.aboutus_checknewversion_new);
                                aboutus_checknewversion_new.setVisibility(View.INVISIBLE);
                            } else {
                                ImageView aboutus_checknewversion_new = mview.findViewById(R.id.aboutus_checknewversion_new);
                                aboutus_checknewversion_new.setVisibility(View.VISIBLE);
                            }
                        }
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "onError: "+e.getMessage()+"" + "Http:" + "http://192.168.30.141:8080/app/homePage/queryHomePageInfo/");
                        Toast.makeText(mControlMainActivity,"获取关于我们信息超时",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
                        TextView aboutus_checknewversion_new_textview = mview.findViewById(R.id.aboutus_checknewversion_new_textview);
                        aboutus_checknewversion_new_textview.setText(finalVerCode + "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

//        final Observable<AboutUsInfoBean> data =
//                modelObservableInterface.queryAboutUsInfo();
//        data.observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Observer<AboutUsInfoBean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(AboutUsInfoBean value) {
//                        //网络请求数据成功
//                        AboutUsInfoBean.AboutUsInfoDataBean aboutUsInfoDataBean = value.getData();
//                        if (aboutUsInfoDataBean == null || mview == null){
//                            LoadingDialog.getInstance(mControlMainActivity).dismiss();
//                            return;
//                        }
//                        if (aboutUsInfoDataBean.newversion != null) {
//                            TextView aboutus_checknewversion_new_textview = mview.findViewById(R.id.aboutus_checknewversion_new_textview);
//                            aboutus_checknewversion_new_textview.setText(aboutUsInfoDataBean.newversion);
//                        }
//                        if (aboutUsInfoDataBean.sla != null){
//                            TextView aboutus_agreeTerms = mview.findViewById(R.id.aboutus_agreeTerms);
//                            aboutus_agreeTerms.setText(aboutUsInfoDataBean.sla);
//                        }
//                        if (aboutUsInfoDataBean.privacypolicy != null){
//                            TextView aboutus_agreeTerms_1 = mview.findViewById(R.id.aboutus_agreeTerms_1);
//                            aboutus_agreeTerms_1.setText(aboutUsInfoDataBean.privacypolicy);
//                        }
//                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("TAG", "onError: "+e.getMessage()+"" + "Http:" + "http://192.168.30.141:8080/app/homePage/queryHomePageInfo/");
//                        Toast.makeText(mControlMainActivity,"获取关于我们信息超时",Toast.LENGTH_LONG).show();
//                        LoadingDialog.getInstance(mControlMainActivity).dismiss();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    //上传头像
    public void ModifyingHead(String imgPath) {
        if (mControlMainActivity.mStuId.equals("") || mControlMainActivity.mToken.equals("") ){
            Toast.makeText(mControlMainActivity, "请先登录您的账号，再进行此操作!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imgPath.equals("")){
            Toast.makeText(mControlMainActivity, "您选择的图片未找到!", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String uu = UUID.randomUUID().toString();
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "multipart/form-data; boundary=" + uu)
                            .addHeader("Stuid", mControlMainActivity.mStuId)
                            .addHeader("permissioncode", mControlMainActivity.mToken)
                            .build();
                    return chain.proceed(request);
                }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        File file = new File(imgPath);
        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"),strEntity);
        Map<String, RequestBody> params = new HashMap<>() ;
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        params.put("multipartFile\"; filename=\""+ file.getName(), requestBody);
        params.put("str", body);
        retrofit2.Call call = modelObservableInterface.modifyingHead(params);
        call.enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(retrofit2.Call call, retrofit2.Response response) {
                int code = response.code();
                if (code == 200) {
                    Toast.makeText(mControlMainActivity, "上传头像成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mControlMainActivity, "上传头像失败!", Toast.LENGTH_SHORT).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(retrofit2.Call call, Throwable t) {
                Log.d("Tag",t.getMessage().toString());
//                mControlMainActivity.setmState("");
//                mIsPublish = true;
                Toast.makeText(mControlMainActivity, "上传头像失败!", Toast.LENGTH_SHORT).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //修改学员密码
    private void UpdateStuPass(String origin_stu_pass,String now_stu_pass) {
        if (mControlMainActivity.mStuId.equals("") ||origin_stu_pass.equals("") || now_stu_pass.equals("")){
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

        HashMap<String,String> paramsMap = new HashMap<>();
        paramsMap.put("origin_stu_pass",origin_stu_pass);
        paramsMap.put("now_stu_pass",now_stu_pass);
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.updateStuPass(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean loginBean = response.body();//得到解析后的LoginBean对象
                if (loginBean == null){
                    Toast.makeText(mControlMainActivity,"修改密码失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(loginBean.getErrorCode(),loginBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (loginBean.getErrorCode() == 200 ){
                    Toast.makeText(mControlMainActivity,"修改密码成功",Toast.LENGTH_LONG).show();
                    getPersonalInfoDatas();
                    mControlMainActivity.onClickSettingUpdatePasswordReturn(mview);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else if (loginBean.getErrorCode() == 203 ){
                    Toast.makeText(mControlMainActivity,loginBean.getErrorMsg(),Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                } else {
                    Toast.makeText(mControlMainActivity,"修改密码失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"修改密码失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    public static class PersonalInfoBean{
        private PersonalInfoDataBean data;
        private int code;
        private String msg;

        public PersonalInfoDataBean getData() {
            return data;
        }

        public void setData(PersonalInfoDataBean data) {
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
        public static class PersonalInfoDataBean {
            private String autograph;       //个人签名
            private String nickname;        //用户昵称
            private String stu_name;            //姓名
            private String head;          //用户头像
            private String tel;           //手机号码
            private String login_number;    //账号
            private String ID_number;       //身份证号码
            private String email;           //邮箱
        }
    }

    public static class AboutUsInfoBean{
        private AboutUsInfoDataBean data;
        private int code;
        private String msg;

        public AboutUsInfoDataBean getData() {
            return data;
        }

        public void setData(AboutUsInfoDataBean data) {
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
        public static class AboutUsInfoDataBean {
            private String newversion;       //新版本号
            private String sla;        //服务协议
            private String privacypolicy;            //隐私保护指引

            public String getNewversion() {
                return newversion;
            }

            public void setNewversion(String newversion) {
                this.newversion = newversion;
            }

            public String getSla() {
                return sla;
            }

            public void setSlaurl(String sla) {
                this.sla = sla;
            }

            public String getPrivacypolicy() {
                return privacypolicy;
            }

            public void setPrivacypolicy(String privacypolicy) {
                this.privacypolicy = privacypolicy;
            }
        }
    }
}
