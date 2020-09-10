package com.android.jwjy.wtjyproduct;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidSts;
import com.aliyun.private_service.PrivateService;
import com.aliyun.svideo.common.PublicCommonUtil;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.utils.VcPlayerLog;
import com.aliyun.vodplayerview.constants.PlayParameter;
import com.aliyun.vodplayerview.listener.OnChangeQualityListener;
import com.aliyun.vodplayerview.listener.OnChangeScreenModeListener;
import com.aliyun.vodplayerview.listener.OnReturnListener;
import com.aliyun.vodplayerview.listener.OnStoppedListener;
import com.aliyun.vodplayerview.listener.RefreshStsCallback;
import com.aliyun.vodplayerview.playlist.AlivcVideoInfo;
import com.aliyun.vodplayerview.utils.Common;
import com.aliyun.vodplayerview.utils.FixedToastUtils;
import com.aliyun.vodplayerview.utils.database.DatabaseManager;
import com.aliyun.vodplayerview.utils.database.LoadDbDatasListener;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadInfoListener;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadManager;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadManagerInterface;
import com.aliyun.vodplayerview.utils.download.AliyunDownloadMediaInfo;
import com.aliyun.vodplayerview.view.choice.AlivcShowMoreDialog;
import com.aliyun.vodplayerview.view.control.ControlView;
import com.aliyun.vodplayerview.view.download.AddDownloadView;
import com.aliyun.vodplayerview.view.download.AlivcDialog;
import com.aliyun.vodplayerview.view.download.AlivcDownloadMediaInfo;
import com.aliyun.vodplayerview.view.download.DownloadChoiceDialog;
import com.aliyun.vodplayerview.view.download.DownloadDataProvider;
import com.aliyun.vodplayerview.view.download.DownloadView;
import com.aliyun.vodplayerview.view.gesturedialog.BrightnessDialog;
import com.aliyun.vodplayerview.view.more.AliyunShowMoreValue;
import com.aliyun.vodplayerview.view.more.ShowMoreView;
import com.aliyun.vodplayerview.view.more.SpeedValue;
import com.aliyun.vodplayerview.view.tipsview.ErrorInfo;
import com.aliyun.vodplayerview.widget.AliyunScreenMode;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.android.jwjy.wtjyproduct.activity.LoginJumpActivity;
import com.android.jwjy.wtjyproduct.consts.PlayType;
import com.android.jwjy.wtjyproduct.entity.PlaybackDataConverter;
import com.android.jwjy.wtjyproduct.entity.PrePlaybackEntity;
import com.android.jwjy.wtjyproduct.net.HttpRequest;
import com.android.jwjy.wtjyproduct.util.ActivityUtil;
import com.android.wtjyproduct.R;
import com.google.gson.Gson;
import com.talkfun.sdk.http.PreDataRequestManager;
import com.talkfun.sdk.model.PreDataForPlaybackInitModel;

import net.sqlcipher.Cursor;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dayuer on 19/7/2.
 * 主程序
 */
public class ControlMainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, AliyunDownloadManagerInterface {
    //继承Activity 不会显示APP头上的标题
    private Fragment mModelHomePage,mModelMy,mModelOpenClass,mModelLogIn,mModelSetting,mModelCoursePacket,mModelCourse,mModelClassCheduleCard
            ,mModelQuestionBank,mModelNews,mModelCommunityAnswer;
    private String mPage = ""; //当前显示页面
    private String mBeforePage = ""; //上一个显示界面
    private static ControlMainActivity mThis;
    private BottomNavigationView mBottomNavigationView;  //底部菜单
    /**
     * 6.0版本检测并申请开启摄像头、音频录制、扩展卡读写等权限*/
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO};
    private File cameraSavePath;//拍照照片路径
    private Uri uri;//照片uri
    private long firstTime = 0;
    //订单
    private ModelOrderDetails modelOrderDetails = null;
    //token
    public String mToken = "";
    public String mIpadress = PublicCommonUtil.ipadress;
    private Map<String, String> mIpType = new HashMap<>();
    public String mStuId = "";

    @Override
    public String getUrl() {
        return mIpadress;
    }

    private class MenuItemInfo {
        String mName;  //按钮名称
        int mItemId;   //按钮标识（1：首页 2：课程包 3：课程表 4：我的）
        int mOrder;    //按钮排序
    }


    private final        int CUPREQUEST        = 50;
    private final        int CAMERA            = 10;
    private final        int ALBUM             = 20;
    private Uri uritempFile;
    private String          picPath;
    private File            mOutImage;
    private HttpRequest httpRequest;

    //按键反应状态
    private String mState = "";

    /**
     * 重置App界面的字体大小，fontScale 值为 1 代表默认字体大小
     * @return suyan
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        config.fontScale = 1;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIpType.put("http://wangxiao.jianweijiaoyu.com/","jianwei");
        mIpType.put("http://wangxiao.16zige.com/","yixiao");
        mIpType.put("http://wangxiao.yixiaojiaoyu.com/","yixiao");
        mIpType.put("http://manager.huostudy.com/","jingkao");
        //阿里视频播放下载，必须初始化的服务，必须放在最开始的位置
        PrivateService.initService(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PublicCommonUtil.encryptedAppPath + "/encryptedApp.dat");
        //拷贝encryptedApp.dat文件到所需位置
        copyAssets();
        //阿里云视频播放数据库初始化
        DatabaseManager.getInstance().createDataBase(this);
        //修改状态栏颜色
        ModelStatusBarUtil.setStatusBarColor(this, R.color.white);
//        ModelViewUtils.setImmersionStateMode(this);
        setContentView(R.layout.activity_main);
        //初始化数据库（存储token等数据）
        ModelSearchRecordSQLiteOpenHelper sqLiteOpenHelper = ModelSearchRecordSQLiteOpenHelper.getInstance(ControlMainActivity.this);
        sqLiteOpenHelper.getWritableDatabase(ControlMainActivity.this);
        //从本地查询token
        Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mThis).rawQuery(
                "select * from token_table ", null);
        HeaderInterceptor.context = this;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int tokenIndex = cursor.getColumnIndex("token");
                int stu_idIndex = cursor.getColumnIndex("stu_id");
                int ipadressIndex = cursor.getColumnIndex("ipadress");
                mIpadress = cursor.getString(ipadressIndex);
                mToken = cursor.getString(tokenIndex);
                mStuId = cursor.getString(stu_idIndex);
                HeaderInterceptor.stuId = mStuId;
                HeaderInterceptor.permissioncode = mToken;
                break;
            }
            cursor.close();
        }
        PlayParameter.STS_GET_URL = mIpadress + PlayParameter.STS_GET_URL;
        mThis = this;
        mBottomNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED); //同时显示底部菜单的图标和文字
        //判断底部菜单使用哪些菜单
        List<MenuItemInfo> menuItemList = new ArrayList<>();
        MenuItemInfo MenuItemInfo1 = new MenuItemInfo();
        MenuItemInfo1.mItemId = 1;
        MenuItemInfo1.mOrder = 1;
        MenuItemInfo1.mName = "首页";
        menuItemList.add(MenuItemInfo1);
        MenuItemInfo MenuItemInfo2 = new MenuItemInfo();
        MenuItemInfo2.mItemId = 2;
        MenuItemInfo2.mOrder = 2;
        MenuItemInfo2.mName = "课程包";
        menuItemList.add(MenuItemInfo2);
        MenuItemInfo MenuItemInfo3 = new MenuItemInfo();
        MenuItemInfo3.mItemId = 3;
        MenuItemInfo3.mOrder = 3;
        MenuItemInfo3.mName = "课程表";
        menuItemList.add(MenuItemInfo3);
        MenuItemInfo MenuItemInfo4 = new MenuItemInfo();
        MenuItemInfo4.mItemId = 4;
        MenuItemInfo4.mOrder = 4;
        MenuItemInfo4.mName = "我的";
        menuItemList.add(MenuItemInfo4);
        if (menuItemList.size() == 0){  //如果从后台查询的菜单为0，添加默认菜单
            MenuItemInfo MenuItemInfoDefault1 = new MenuItemInfo();
            MenuItemInfoDefault1.mItemId = 1;
            MenuItemInfoDefault1.mOrder = 1;
            MenuItemInfoDefault1.mName = "首页";
            menuItemList.add(MenuItemInfoDefault1);
            MenuItemInfo MenuItemInfoDefault2 = new MenuItemInfo();
            MenuItemInfoDefault2.mItemId = 2;
            MenuItemInfoDefault2.mOrder = 2;
            MenuItemInfoDefault2.mName = "课程包";
            menuItemList.add(MenuItemInfoDefault2);
            MenuItemInfo MenuItemInfoDefault3 = new MenuItemInfo();
            MenuItemInfoDefault3.mItemId = 3;
            MenuItemInfoDefault3.mOrder = 3;
            MenuItemInfoDefault3.mName = "课程表";
            menuItemList.add(MenuItemInfoDefault3);
            MenuItemInfo MenuItemInfoDefault4 = new MenuItemInfo();
            MenuItemInfoDefault4.mItemId = 4;
            MenuItemInfoDefault4.mOrder = 4;
            MenuItemInfoDefault4.mName = "我的";
            menuItemList.add(MenuItemInfoDefault4);
        }
        for (int i = 0; i < menuItemList.size(); i ++) {
            MenuItemInfo menuItemInfo = menuItemList.get(i);
            if (menuItemInfo == null){
                continue;
            }
            Menu menu = mBottomNavigationView.getMenu();
            if (menu == null){
                continue;
            }
            menu.add(0, menuItemInfo.mItemId, menuItemInfo.mOrder, menuItemInfo.mName);//设置菜单标题(0,编号,顺序,名称)
            MenuItem item = menu.findItem(menuItemInfo.mItemId);
            if (menuItemInfo.mItemId == 1){ //使用首页的图标
                item.setIcon(R.drawable.button_homepage);//设置菜单图片
            } else if (menuItemInfo.mItemId == 2){ //使用课程包的图标
                item.setIcon(R.drawable.button_coursepacket);//设置菜单图片
            } else if (menuItemInfo.mItemId == 3){ //使用课程表的图标
                item.setIcon(R.drawable.button_coursetable);//设置菜单图片
            } else if (menuItemInfo.mItemId == 4){ //使用我的图标
                item.setIcon(R.drawable.button_my);//设置菜单图片
            }
            item.setOnMenuItemClickListener(item1 -> {
                Log.d("123", "onNavigationItemSelected is click: ");
                if (menuItemInfo.mItemId == 1){ //跳转首页的界面
                    Page_HomePage();
                } else if (menuItemInfo.mItemId == 2){ //跳转课程包的界面
                    Page_MoreCoursePacket();
                } else if (menuItemInfo.mItemId == 3){ //跳转课程表的界面
                    Page_ClassCheduleCard();
                } else if (menuItemInfo.mItemId == 4){ //跳转我的界面
                    Page_My();
                }
                return false;
            });
        }
//        //添加职考网校逻辑（如果没有登录，请游客先选择是健康项目还是消防项目）
//        if (mStuId.equals("") ||  mIpadress.equals("")){
//            //
//            TextView main_view_choicetitle1 = findViewById(R.id.main_view_choicetitle1);
//            main_view_choicetitle1.setOnLongClickListener(v -> {
//                View view = mThis.getLayoutInflater().inflate(R.layout.dialog_sure_cancel1, null);
//                ControllerCenterDialog mMyCouponDialog = new ControllerCenterDialog(mThis, 0, 0, view, R.style.DialogTheme);
//                mMyCouponDialog.setCancelable(true);
//                mMyCouponDialog.show();
//                TextView button_cancel = view.findViewById(R.id.button_cancel);
//                button_cancel.setOnClickListener(View->{
//                    mMyCouponDialog.cancel();
//                });
//                TextView button_sure = view.findViewById(R.id.button_sure);
//                button_sure.setOnClickListener(View->{
//                    EditText dialog_content = view.findViewById(R.id.dialog_content);
//                    if (dialog_content.getText().toString().equals("")){
//                        return;
//                    }
//                    mIpadress = "http://" + dialog_content.getText().toString() + "/";
//                    PlayParameter.STS_GET_URL = mIpadress + PlayParameter.STS_GET_URL;
//                    mMyCouponDialog.cancel();
//                });
//                return false;
//            });
//            //将网校系统选项界面显示，隐藏主页
//            RelativeLayout main_view_choice = findViewById(R.id.main_view_choice);
//            main_view_choice.setVisibility(View.VISIBLE);
//            ConstraintLayout main_view1 = findViewById(R.id.main_view1);
//            main_view1.setVisibility(View.INVISIBLE);
//            LinearLayout jiankang_layout =  findViewById(R.id.jiankang_layout);
//            ImageView jiankang = findViewById(R.id.jiankang);
//            jiankang.setBackground(getResources().getDrawable(R.drawable.icon_black));
//            jiankang_layout.setOnClickListener(v->{
//                ImageView xiaofang = findViewById(R.id.xiaofang);
//                ImageView xiaofangsetting = findViewById(R.id.xiaofangsetting);
//                xiaofang.setBackground(getResources().getDrawable(R.drawable.icon_null));
//                xiaofangsetting.setBackground(getResources().getDrawable(R.drawable.icon_null));
//                jiankang.setBackground(getResources().getDrawable(R.drawable.icon_black));
//                mIpadress = "http://wangxiao.jianweijiaoyu.com/";
//                PlayParameter.STS_GET_URL = mIpadress + PlayParameter.STS_GET_URL;
//            });
//            LinearLayout xiaofang_layout =  findViewById(R.id.xiaofang_layout);
//            xiaofang_layout.setOnClickListener(v->{
//                ImageView xiaofang = findViewById(R.id.xiaofang);
//                ImageView xiaofangsetting = findViewById(R.id.xiaofangsetting);
//                xiaofang.setBackground(getResources().getDrawable(R.drawable.icon_black));
//                jiankang.setBackground(getResources().getDrawable(R.drawable.icon_null));
//                xiaofangsetting.setBackground(getResources().getDrawable(R.drawable.icon_null));
//                mIpadress = "http://wangxiao.16zige.com/";
//                PlayParameter.STS_GET_URL = mIpadress + PlayParameter.STS_GET_URL;
//            });
//            LinearLayout xiaofangsetting_layout = findViewById(R.id.xiaofangsetting_layout);
//            xiaofangsetting_layout.setOnClickListener(v->{
//                ImageView xiaofang = findViewById(R.id.xiaofang);
//                ImageView xiaofangsetting = findViewById(R.id.xiaofangsetting);
//                xiaofangsetting.setBackground(getResources().getDrawable(R.drawable.icon_black));
//                jiankang.setBackground(getResources().getDrawable(R.drawable.icon_null));
//                xiaofang.setBackground(getResources().getDrawable(R.drawable.icon_null));
//                mIpadress = "http://wangxiao.16zige.com/";
//                PlayParameter.STS_GET_URL = mIpadress + PlayParameter.STS_GET_URL;
//            });
//            Button button_next = findViewById(R.id.button_next);
//            button_next.setOnClickListener(v->{
//                //将网校系统选项界面隐藏，显示出主页
//                RelativeLayout main_view_choice1 = findViewById(R.id.main_view_choice);
//                main_view_choice1.setVisibility(View.INVISIBLE);
//                ConstraintLayout main_view11 = findViewById(R.id.main_view1);
//                main_view11.setVisibility(View.VISIBLE);
//                //选择好网校系统以后，初始化首页
//                Menu menu = mBottomNavigationView.getMenu();
//                if (menu != null) {
//                    MenuItem MenuItem = menu.getItem(0);
//                    if (MenuItem != null) {
//                        //初次进入app 默认显示menuItemList中的第一个
//                        if (MenuItem.getItemId() == 1) { //首页
//                            Page_HomePage();
//                        } else if (MenuItem.getItemId() == 2) {//课程包
//                            Page_MoreCoursePacket();
//                        } else if (MenuItem.getItemId() == 3) {//课程表
//                            Page_ClassCheduleCard();
//                        } else if (MenuItem.getItemId() == 4) {//我的
//                            Page_My();
//                        }
//                    }
//                }
//                //获取应用权限
//                getPermission();
//                getAndroidVersion(mThis);//查询是否为最新版本,若不是最新版本弹出对话框
//            });
//            return;
//        }
        //初始化极光推送，设置别名，如果是游客登录设置为0，否则设置为学生id
        if (mStuId.equals("")){
            String type = mIpType.get(mIpadress);
            JPushInterface.setAlias(this.getApplicationContext(),1,type + "0");
        } else {
            String type = mIpType.get(mIpadress);
            JPushInterface.setAlias(this.getApplicationContext(),1,type + mStuId);
        }
        //将网校系统选项界面隐藏，显示出主页
        RelativeLayout main_view_choice1 = findViewById(R.id.main_view_choice);
        main_view_choice1.setVisibility(View.INVISIBLE);
        ConstraintLayout main_view11 = findViewById(R.id.main_view1);
        main_view11.setVisibility(View.VISIBLE);
        Menu menu = mBottomNavigationView.getMenu();
        if (menu != null) {
            MenuItem MenuItem = menu.getItem(0);
            if (MenuItem != null) {
                //初次进入app 默认显示menuItemList中的第一个
                if (MenuItem.getItemId() == 1) { //首页
                    Page_HomePage();
                } else if (MenuItem.getItemId() == 2) {//课程包
                    Page_MoreCoursePacket();
                } else if (MenuItem.getItemId() == 3) {//课程表
                    Page_ClassCheduleCard();
                } else if (MenuItem.getItemId() == 4) {//我的
                    Page_My();
                }
            }
        }
        boolean isXieyi = getIntent().getBooleanExtra("isXieyi",false);
        if (isXieyi){
            //点击意见反馈
            Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.dialog_sure_cancel_webview);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView tip = dialog.findViewById(R.id.tip);
            tip.setText("用户协议与隐私政策");
            WebView dialog_content = dialog.findViewById(R.id.dialog_content);
            dialog_content.getSettings().setJavaScriptEnabled(true);
            dialog_content.setWebViewClient(new WebViewClient());
            dialog_content.loadUrl(PublicCommonUtil.YINSI_URL);
            TextView button_cancel = dialog.findViewById(R.id.button_cancel);
            button_cancel.setText("拒绝");
            button_cancel.setOnClickListener(v1->{finish();});
            // 确认更新
            TextView sure = dialog.findViewById(R.id.button_sure);
            sure.setText("同意");
            dialog.findViewById(R.id.button_sure).setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            dialog.show();
        }
        //获取应用权限
        getPermission();
        getAndroidVersion(this);//查询是否为最新版本,若不是最新版本弹出对话框
//        //初始化播放器
//        initAliyunPlayerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);//将这一行注释掉，阻止activity保存fragment的状态
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction){
        if(mModelHomePage!= null){ //隐藏首页
            transaction.hide(mModelHomePage);
        }
        if(mModelMy != null){ //隐藏我的
            transaction.hide(mModelMy);
        }
        if(mModelClassCheduleCard != null){//隐藏课程表
            transaction.hide(mModelClassCheduleCard);
        }
        if (mModelLogIn != null){//隐藏登录
            transaction.hide(mModelLogIn);
        }
        if (mModelSetting != null){//隐藏设置
            transaction.hide(mModelSetting);
        }
        if (mModelCoursePacket != null){//隐藏课程包
            transaction.hide(mModelCoursePacket);
        }
        if (mModelCourse != null){//隐藏课程包
            transaction.hide(mModelCourse);
        }
        if (mModelQuestionBank != null){//隐藏题库
            transaction.hide(mModelQuestionBank);
        }
        if (mModelOpenClass != null){//隐藏公开课
            transaction.hide(mModelOpenClass);
        }
        if (mModelNews != null){//隐藏新闻
            transaction.hide(mModelNews);
        }
        if (mModelCommunityAnswer != null){//隐藏社区问答
            transaction.hide(mModelCommunityAnswer);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) { // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {//隐藏软键盘
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    /**
      * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
      *
      * @param v
      * @param event
      * @return
      */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }
    /**
      * 多种隐藏软件盘方法的其中一种
      *
      * @param token
      */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    @Override
    protected void onDestroy() {
        if (mModelHomePage != null){
            mModelHomePage.onDestroy();
        }
        if (mModelMy != null) {
            mModelMy.onDestroy();
        }
        if (mModelLogIn != null){
            mModelLogIn.onDestroy();
        }
        if (mModelSetting != null){
            mModelSetting.onDestroy();
        }
        if (mModelCoursePacket != null){
            mModelCoursePacket.onDestroy();
        }
        if (mModelCourse != null){
            mModelCourse.onDestroy();
        }
        if (mModelQuestionBank != null){
            mModelQuestionBank.onDestroy();
        }
        if(mModelClassCheduleCard != null){
            mModelClassCheduleCard.onDestroy();
        }
        if (mModelOpenClass != null){
            mModelOpenClass.onDestroy();
        }
        if (mModelNews != null){
            mModelNews.onDestroy();
        }
        if (mModelCommunityAnswer != null){
            mModelCommunityAnswer.onDestroy();
        }
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }

        if (playerHandler != null) {
            playerHandler.removeMessages(DOWNLOAD_ERROR);
            playerHandler = null;
        }

        if (commenUtils != null) {
            commenUtils.onDestroy();
            commenUtils = null;
        }
        if (LoadingDialog.getInstance(this).isShowing()) {
            LoadingDialog.getInstance(this).dismiss();
        }
        if (LoadingDialog.getInstance(this).getContext() instanceof Activity) {
            if (!((Activity) LoadingDialog.getInstance(this).getContext()).isFinishing()) {
                LoadingDialog.getInstance(this).dismiss();
            }
        }
        super.onDestroy();
        if (downloadManager != null && downloadDataProvider != null) {
            ConcurrentLinkedQueue<AliyunDownloadMediaInfo> downloadMediaInfos = new ConcurrentLinkedQueue<>();
            downloadMediaInfos.addAll(downloadDataProvider.getAllDownloadMediaInfo());
            downloadManager.stopDownloads(downloadMediaInfos);
        }
    }

    //点击更多课程
    public void onClickMoreCourse(View view) { Page_Course(); }

    //点击更多课程包
    public void onClickMoreCoursePacket(View view) {
        Page_MoreCoursePacket();
    }

    //点击课程包详情的返回界面
    public void onClickCoursePacketDetailsReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (beforePageS[beforePageS.length - 1].equals("课程包")){ //说明上个界面是课程包界面
            Page_MoreCoursePacket();
        } else if (beforePageS[beforePageS.length - 1].equals("首页")){ //说明上个界面是首页
            Page_HomePage();
        } else if (beforePageS[beforePageS.length - 1].equals("我的课程包")){ //说明上个界面是我的课程包界面
            mPage = "我的课程包";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyClassPacketShow();
            }
        } else if (beforePageS[beforePageS.length - 1].equals("我的收藏")){ //说明上个界面是我的收藏界面
            mPage = "我的收藏";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyCollectShow();
            }
        }
    }

    //点击课程包详情界面
    public void onClickCoursePacketDetails() {
        mBeforePage = mBeforePage + "/" + mPage ;
        if (mBeforePage.contains("课程包详情")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/课程包详情"));
        }
        mPage = "课程包详情";
        if (mBottomNavigationView != null){
            mBottomNavigationView.setVisibility(View.INVISIBLE);
        }
    }

    //点击课程详情界面de返回界面
    public void onClickCourseDetailsReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (beforePageS[beforePageS.length - 1].equals("课程")){ //说明上个界面是课程界面
            Page_Course();
        } else if (beforePageS[beforePageS.length - 1].equals("首页")){ //说明上个界面是首页界面
            Page_HomePage();
        } else if (beforePageS[beforePageS.length - 1].equals("我的课程")){//说明上个界面是我的课程界面
            mPage = "我的课程";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyClassShow();
            }
        } else if (beforePageS[beforePageS.length - 1].equals("我的收藏")){ //说明上个界面是我的收藏界面
            mPage = "我的收藏";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyCollectShow();
            }
        } else if (beforePageS[beforePageS.length - 1].equals("课程包详情")){ //说明上个界面是我的收藏界面
            mBeforePage = "";
            for (int i = 0 ; i < beforePageS.length - 1; i ++){
                if (i == beforePageS.length - 2){
                    mBeforePage = mBeforePage + beforePageS[i];
                } else {
                    mBeforePage = mBeforePage + beforePageS[i] + "/";
                }
            }
            mPage = beforePageS[beforePageS.length - 1];
            Page_MoreCoursePacket();
        }
    }

    //点击课程详情界面
    public void onClickCourseDetails() {
        mBeforePage = mBeforePage + "/" + mPage ;
        if (mBeforePage.contains("课程详情")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/课程详情"));
        }
        mPage = "课程详情";
        //隐藏所有的底部按钮
        mBottomNavigationView.setVisibility(View.INVISIBLE);
    }

//    //点击课程问答
//    public void onClickCourseQuestion(View view) {
//        mBeforePage = mBeforePage + "/" + mPage ; //上个界面是课程详情界面
//        mPage = "课程问答";
//        if (mModelCourse != null){
//            ((ModelCourse)mModelCourse).CourseSearchInit();
//        }
//    }

//    //购买-选择优惠券-返回
//    public void onClickCouponChooseReturn(View view) {
//    }

    //点击公开课列表的返回
    public void onClickOpenClassReturn(View view) {
        if (mPage.equals("公开课") && mBeforePage.equals("首页")) { //如果当前界面是公开课，点击返回按钮，应该返回到首页
            Page_HomePage();
            mBottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    //点击立即登录
    public void onClickImmediatelyLogin(String type) {
        if (type.equals("login")){
            mPage = "登录";
            mBeforePage = "我的";
            Page_LogIn();//弹出登录界面
        } else { //弹出学员信息界面
            mPage = "基本信息";
            mBeforePage = "我的";
            //隐藏所有的底部按钮
            mBottomNavigationView.setVisibility(View.INVISIBLE);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideAllFragment(transaction);
            if(mModelSetting == null){
                mModelSetting = ModelSetting.newInstance(mThis,"设置-基本信息",R.layout.modelsetting);//"设置"
                transaction.add(R.id.framepage,mModelSetting);
            } else {
                transaction.show(mModelSetting);
                ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(0);
            }
            transaction.commit();
        }
    }

    //点击登录
    public void onClickLogin(View view) {
        //获取登录界面的信息
        String userId = ((ModelLogIn)mModelLogIn).UserIdGet();
        if (userId.equals("")){
            Toast.makeText(this,"账户不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        String userPassword = ((ModelLogIn)mModelLogIn).UserPasswordGet();
        if (userPassword.equals("")){
            Toast.makeText(this,"密码不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //将账户和密码发给服务器确认
        ((ModelLogIn)mModelLogIn).LogIn(userId,userPassword);
    }

    //登录成功的回调，存储token和学生id
    public void LogInSuccess(String token,String stu_id){
        mToken = token;
        mStuId = stu_id;
        HeaderInterceptor.stuId = mStuId;
        HeaderInterceptor.permissioncode = mToken;
        String type = mIpType.get(mIpadress);
        JPushInterface.setAlias(this.getApplicationContext(),1,type + mStuId);
        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(this).execSQL("delete from token_table");
        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(this).execSQL("insert into token_table(token,ipadress,stu_id) values('" + token + "','" + mIpadress + "','" + stu_id + "')");
        Page_My();
    }

    //点击设置
    public void onClickSetting(View view) {
        mPage = "设置";
        mBeforePage = "我的";
        //隐藏所有的底部按钮
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelSetting == null){
            mModelSetting = ModelSetting.newInstance(mThis,"设置",R.layout.modelsetting);//"设置"
            transaction.add(R.id.framepage,mModelSetting);
        }else{
            transaction.show(mModelSetting);
            ((ModelSetting)mModelSetting).SettingMainShow(0);
        }
        transaction.commit();
    }

    //点击设置-返回
    public void onClickSettingReturn(View view) {
        Page_My();
    }

    //点击我的课程
    public void onClickMyCourse(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的课程";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyClassShow();
        }
    }

    //我的课程-返回
    public void onClickMyClassReturn(View view) {
        if (mPage.equals("我的课程") && mBeforePage.equals("我的")) { //如果当前界面是我的课程，点击返回按钮，应该返回到我的
            Page_My();
        }
    }

    //协议界面的返回
    public void onClickMyClassAgreementReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (mPage.equals("协议详情") && beforePageS[beforePageS.length - 1].equals("我的课程")) { //如果当前界面是我的课程，点击返回按钮，应该返回到我的
            mBottomNavigationView.setVisibility(View.INVISIBLE);
            mPage = "我的课程";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyClassShow();
            }
        } else if (mPage.equals("协议详情") && beforePageS[beforePageS.length - 1].equals("我的课程包")) { //如果当前界面是我的课程包，点击返回按钮，应该返回到我的
            mPage = "我的课程包";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyClassPacketShow();
            }
        }
    }

    //协议界面
    public void onClickMyAgreement() {
        mBeforePage = mBeforePage + "/" + mPage ;
        if (mBeforePage.contains("协议详情")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/协议详情"));
        }
        mPage = "协议详情";
    }

    //我的课程-课程表
    public void onClickMyCourseClassCheduleCard(View view) {
        mPage = "课程表";
        mBeforePage = "我的课程";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelClassCheduleCard == null){
            mModelClassCheduleCard = ModelClassCheduleCard.newInstance(mThis,"",R.layout.fragment_classchedulecard);//"课程表"
            transaction.add(R.id.framepage,mModelClassCheduleCard);
        }else{
            transaction.show(mModelClassCheduleCard);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((ModelClassCheduleCard)mModelClassCheduleCard).ClassCheduleCardMainInit("");
            }
        }
        transaction.commit();
    }

    //课程表-返回
    public void onClickClassCheduleCardReturn(View view) {
        if (mPage.equals("课程表") && mBeforePage.equals("我的课程")) { //
            if(mModelMy != null){
                Page_My();
//                mBottomNavigationView.setVisibility(View.INVISIBLE);
//                mPage = "我的课程";
//                mBeforePage = "我的";
//                ((ModelMy) mModelMy).MyClassShow();
            }
        }
    }

    //点击登录主界面的返回
    public void onClickLoginReturn(View view) {
        if (mPage.equals("登录") && mBeforePage.equals("我的")){
            Page_My();
        } else if (mPage.equals("登录") && mBeforePage.equals("设置")) { //如果当前界面是登录，点击返回按钮，应该返回到设置
            if (mModelSetting != null) {
                mPage = "设置";
                mBeforePage = "我的";
                //隐藏所有的底部按钮
                mBottomNavigationView.setVisibility(View.INVISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                hideAllFragment(transaction);
                if(mModelSetting == null){
                    mModelSetting = ModelSetting.newInstance(mThis,"设置-基本信息",R.layout.modelsetting);//"设置"
                    transaction.add(R.id.framepage,mModelSetting);
                } else {
                    transaction.show(mModelSetting);
                    ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(0);
                }
                transaction.commit();
            }
        }
    }

    //点击忘记密码
    public void onClickForgetPassword(View view) {
        mPage = "忘记密码";
        mBeforePage = "登录";
        ((ModelLogIn)mModelLogIn).ForgetPasswordShow();
    }

    //点击忘记密码-返回
    public void onClickForgetPasswordReturn(View view) {
        ((ModelLogIn)mModelLogIn).LogInMainShow();
    }

    //点击注册
    public void onClickRegister(View view) {
        mPage = "注册";
        mBeforePage = "登录";
        ((ModelLogIn)mModelLogIn).RegisterShow();
    }

    //点击开始注册
    public void onClickRegisterStart(View view) {
        //服务器注册
        ((ModelLogIn)mModelLogIn).Register();
    }

    //点击获取验证码
    public void onClickSMSCodeGet(View view) {
        //向服务器发命令获取验证码
        //修改界面为倒计时
        ((ModelLogIn)mModelLogIn).SMSCodeGet();
    }

    //注册-获取验证码
    public void onClickRegisterSMSCodeGet(View view) {
        //向服务器发命令获取验证码
        //修改界面为倒计时
        ((ModelLogIn)mModelLogIn).RegisterSMSCodeGet();
    }

    //点击重置密码
    public void onClickResetPassword(View view) {
        //向服务器发送新密码
        ((ModelLogIn)mModelLogIn).RetrievePassword();
    }

    //点击设置-基本信息
    public void onClickSettingEssentialInformation(View view) {
        if (mStuId.equals("")){
            mPage = "登录";
            mBeforePage = "设置";
            //跳转到登录界面
            Page_LogIn();
        } else {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击设置-基本信息-返回
    public void onClickSettingBaseInfoReturn(View view) {
        if (mPage.equals("基本信息") && mBeforePage.equals("我的")){
            Page_My();
        } else if (mPage.equals("基本信息") && mBeforePage.equals("设置")){
            if (mModelSetting != null) {
                mPage = "设置";
                mBeforePage = "我的";
                ((ModelSetting) mModelSetting).SettingMainShow(0);
            }
        }
    }

    //点击上传头像
    public void onClickSettingEssentialInformationIcon(View view) {
        if (mModelSetting != null) {
            ((ModelSetting) mModelSetting).SetttingButtonDialogShow();
        }
    }

    //点击修改名称
    public void onClickSettingEssentialInformationName(View view) {
        if (mModelSetting != null) {
            mPage = "修改名称";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingUserNameUpdateShow();
        }
    }

    //点击修改名称-返回(取消修改，无需保存)
    public void onClickSettingUpdateUserNameReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改名称-保存
    public void onClickSettingUpdateUserNameSave(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).UpdataPersonInfo("username");
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改名称-清除所有
    public void onClickSettingUpdateUserNameClear(View view) {
        if (mModelSetting != null) {
            ((ModelSetting)mModelSetting).SettingUserNameUpdateClear();
        }
    }

    //点击修改昵称
    public void onClickSettingEssentialInformationNick(View view) {
        if (mModelSetting != null) {
            mPage = "修改昵称";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingUserNickUpdateShow();
        }
    }

    //点击修改昵称-返回(取消修改，无需保存)
    public void onClickSettingUpdateUserNickReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改昵称-保存
    public void onClickSettingUpdateUserNickSave(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).UpdataPersonInfo("usernick");
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改昵称-清除所有
    public void onClickSettingUpdateUserNickClear(View view) {
        if (mModelSetting != null) {
            ((ModelSetting)mModelSetting).SettingUserNickUpdateClear();
        }
    }

    //点击修改签名
    public void onClickSettingEssentialInformationSign(View view) {
        if (mModelSetting != null) {
            mPage = "修改签名";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingPersonalStatementUpdateShow();
        }
    }

    //点击修改签名-返回(取消修改，无需保存)
    public void onClickSettingUpdatePersonalStatementReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改签名-保存
    public void onClickSettingUpdatePersonalStatementSave(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).UpdataPersonInfo("usersign");
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改邮箱
    public void onClickSettingEssentialInformationEmail(View view) {
        if (mModelSetting != null) {
            mPage = "修改邮箱";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingEmailUpdateShow();
        }
    }

    //点击修改邮箱-返回(取消修改，无需保存)
    public void onClickSettingUpdateEmailReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改邮箱-保存
    public void onClickSettingUpdateEmailSave(View view) {
        if (mModelSetting != null) {
            String email = ((ModelSetting)mModelSetting).EmailGet();
            if (!email.equals("")){ //如果邮箱不为空的时候，判断邮箱地址是否正确
                if (!isEmail(email)){
                    Toast.makeText(this,"邮箱格式不正确，请重新输入！",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            //将userName 存储与本地和服务器
            ((ModelSetting)mModelSetting).UpdataPersonInfo("email");
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改邮箱-清除所有
    public void onClickSettingUpdateEmailClear(View view) {
        if (mModelSetting != null) {
            ((ModelSetting)mModelSetting).SettingEmailUpdateClear();
        }
    }

    //点击修改电话号码
    public void onClickSettingEssentialInformationTel(View view) {
        if (mModelSetting != null) {
            mPage = "修改电话号码";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingTelNumberUpdateShow();
        }
    }

    //点击修改电话号码-返回(取消修改，无需保存)
    public void onClickSettingUpdateTelNumberReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改电话号码-保存
    public void onClickSettingUpdateTelNumberSave(View view) {
        if (mModelSetting != null) {
            String telNumber = ((ModelSetting)mModelSetting).TelNumberGet();
            if (!telNumber.equals("")){ //如果电话号码不为空的时候，判断电话号码是否正确
                if (!isTelNumber(telNumber)){
                    Toast.makeText(this,"电话号码不正确，请重新输入！",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            //将telNumber 存储与本地和服务器
            ((ModelSetting)mModelSetting).UpdataPersonInfo("telnumber");
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改电话号码-清除所有
    public void onClickSettingUpdateTelNumberClear(View view) {
        if (mModelSetting != null) {
            ((ModelSetting)mModelSetting).SettingTelNumberUpdateClear();
        }
    }

    //点击修改证件号码
    public void onClickSettingEssentialInformationIdNum(View view) {
        if (mModelSetting != null) {
            mPage = "修改证件号码";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingIdNumberUpdateShow();
        }
    }

    //点击修改证件号码-返回(取消修改，无需保存)
    public void onClickSettingUpdateIdNumberReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改证件号码-保存
    public void onClickSettingUpdateIdNumberSave(View view) {
        if (mModelSetting != null) {
            String idNumber = ((ModelSetting)mModelSetting).IdNumberGet();
            if (!idNumber.equals("")){ //如果证件号码不为空的时候，判断证件号码是否正确
                if (!isIdNumber(idNumber)){
                    Toast.makeText(this,"证件号码不正确，请重新输入！",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            //将idNumber 存储与本地和服务器
            ((ModelSetting)mModelSetting).UpdataPersonInfo("idnumber");
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改证件号码-清除所有
    public void onClickSettingUpdateIdNumberClear(View view) {
        if (mModelSetting != null) {
            ((ModelSetting)mModelSetting).SettingIdNumberUpdateClear();
        }
    }

    //点击修改密码
    public void onClickSettingEssentialInformationPasswordUpdata(View view) {
        if (mModelSetting != null) {
            mPage = "修改密码";
            mBeforePage = "基本信息";
            ((ModelSetting) mModelSetting).SettingPasswordUpdateShow();
        }
    }

    //点击修改密码-返回(取消修改，无需保存)
    public void onClickSettingUpdatePasswordReturn(View view) {
        if (mModelSetting != null) {
            mPage = "基本信息";
            mBeforePage = "设置";
            ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
        }
    }

    //点击修改密码-保存密码
    public void onClickSettingUpdatePasswordSave(View view) {
        ((ModelSetting)mModelSetting).NewPasswordSave();
    }

    //点击设置界面的清理缓存
    public void onClickSettingClearCache(View view) {
        final ModelCommonDialog dialog = new ModelCommonDialog(this);
        dialog.setMessage("确认清除缓存吗？")
//                .setImageResId(R.mipmap.ic_launcher)
                .setTitle("提示")
                .setSingle(false).setOnClickBottomListener(new ModelCommonDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                //确定按钮，清空本地缓存
                dialog.dismiss();
            }

            @Override
            public void onNegtiveClick() {
                dialog.dismiss();
            }
        }).show();
    }

    //点击设置界面的允许非WiFi网络播放和缓存视频
    public void onClickSettingAllowNonWifiPlay(boolean isEnable) {
        //isEnable 是否允许在非WiFi网络播放和缓存视频
    }

//    //点击设置界面的版本
//    public void onClickSettingVersion(View view) {
//    }

    //点击设置界面的关于我们
    public void onClickSettingAboutUs(View view) {
        if (mModelSetting != null) {
            mPage = "关于我们";
            mBeforePage = "设置";
            ((ModelSetting) mModelSetting).SettingAboutUsShow();
        }
    }

    //点击设置界面的关于我们-返回(取消修改，无需保存)
    public void onClickSettingAboutUsReturn(View view) {
        if (mModelSetting != null) {
            mPage = "设置";
            mBeforePage = "我的";
            ((ModelSetting) mModelSetting).SettingMainShow(0);
        }
    }

    //点击设置界面的关于我们-检查新版本
    public void onClickSettingCheckVersion(View view){
        getAndroidVersion(this);//查询是否为最新版本,若不是最新版本弹出对话框
    }
//    //点击设置界面的隐私政策
//    public void onClickSettingPrivacyPolicy(View view) {
//    }

    //点击设置界面的退出登录，重置token和学生id
    public void onClickLogout(View view) {
        mToken = "";
        mStuId = "";
        HeaderInterceptor.stuId = null;
        HeaderInterceptor.permissioncode = null;
        String type = mIpType.get(mIpadress);
        JPushInterface.setAlias(this.getApplicationContext(),1,type + "0");
        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(this).execSQL("delete from token_table");
        Page_My();
    }

    public static void onClickLogout() {
        mThis.mToken = "";
        mThis.mStuId = "";
        HeaderInterceptor.stuId = null;
        HeaderInterceptor.permissioncode = null;
        String type = mThis.mIpType.get(mThis.mIpadress);
        JPushInterface.setAlias(mThis.getApplicationContext(),1,type + "0");
        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mThis).execSQL("delete from token_table");
    }

    //点击打开照相机
    public void onClickOpenCamera(View view) {
        goCamera();
    }

    //点击选择本地图片
    public void onClickChooseImg(View view) {
        goPhotoAlbum();
    }

    //点击关闭选择图片对话框
    public void onClickButtonDialogCancel(View view) {
        if (mModelSetting != null) {
            ((ModelSetting) mModelSetting).SetttingButtonDialogCancel();
        }
    }

    //点击课程包主界面的返回按钮
    public void onClickCoursePacketMainReturn(View view){
        Page_HomePage();
        mBottomNavigationView.setVisibility(View.VISIBLE);
    }

    //点击课程包主界面查询按钮
    public void onCoursePacketMainSearch(View view){
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "课程包搜索";
        //将底部菜单隐藏
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        if (mModelCoursePacket != null){
            ((ModelCoursePacket)mModelCoursePacket).CoursePacketMainSearchShow();
        }
    }

    //点击课程包主界面条件查询按钮
    public void onCoursePacketMainSearchCondition(View view) {
        if (mModelCoursePacket != null){
            ((ModelCoursePacket)mModelCoursePacket).CoursePacketMainSearchConditionShow();
        }
    }

    //点击课程主界面的返回按钮
    public void onClickCourseMainReturn(View view){
        Page_HomePage();
        mBottomNavigationView.setVisibility(View.VISIBLE);
    }

    //点击课程主界面查询按钮
    public void onCourseMainSearch(View view){
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "课程搜索";
        if (mModelCourse != null){
            ((ModelCourse)mModelCourse).CourseMainSearchShow();
        }
    }

    //点击课程主界面条件查询按钮
    public void onCourseMainSearchCondition(View view) {
        if (mModelCourse != null){
            ((ModelCourse)mModelCourse).CourseMainSearchConditionShow();
        }
    }

    //点击课程主界面查询按钮
    public void onCommunityAnswerMainSearch(View view){
        mPage = "问答搜索";
        mBeforePage = "社区问答";
    }

    //我的课程包
    public void onClickMyCoursePacket(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的课程包";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyClassPacketShow();
        }
    }

    //我的课程包-返回
    public void onClickMyClassPacketReturn(View view) {
        if (mPage.equals("我的课程包") && mBeforePage.equals("我的")) { //如果当前界面是我的课程包，点击返回按钮，应该返回到我的
            Page_My();
        }
    }

    //我的收藏
    public void onClickMyCollect(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的收藏";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyCollectShow();
        }
    }

    //我的收藏-返回
    public void onClickMyCollectReturn(View view) {
        if (mPage.equals("我的收藏") && mBeforePage.equals("我的")) { //如果当前界面是我的收藏，点击返回按钮，应该返回到我的
            Page_My();
        }
    }

    //我的题库
    public void onClickMyQuestionBank(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的题库";
        mBeforePage = "我的";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelQuestionBank == null){
            mModelQuestionBank = ModelQuestionBank.newInstance(mThis,"我的题库:" + mBeforePage,R.layout.fragment_questionbank);//"我的题库"
            transaction.add(R.id.framepage,mModelQuestionBank);
        } else{
            transaction.show(mModelQuestionBank);
            ((ModelQuestionBank) mModelQuestionBank).QuestionBankMainShow("我的题库");
        }
        transaction.commit();
    }

    //我的订单
    public void onClickMyOrder(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的订单";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyOrderShow();
        }
    }

    //我的订单-返回
    public void onClickOrderBuyReturn() {
        if (mBeforePage.contains("首页")){
            Page_HomePage();
        } else if (mBeforePage.contains("我的")){
            Page_My();
        } else if (mBeforePage.contains("课程包")){
            Page_MoreCoursePacket();
        }
    }

    //我的订单-结果页
    public void onClickOrderResult() {
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "订单结果";
    }

    //点击订单结果的返回按钮
    public void onClickOrderResultReturn(View view) {
        if (mBeforePage.contains("首页")){
            Page_HomePage();
        } else if (mBeforePage.contains("我的")){
            Page_My();
        } else if (mBeforePage.contains("课程包")){
            Page_MoreCoursePacket();
        }
    }

    //我的订单-订单详情
    public void onClickMyOrderDetails() {
        mPage = "订单详情";
        mBeforePage = "我的订单";
    }

    //我的消息
    public void onClickMyMessage(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的消息";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyMessageShow();
        }
    }

    //我的消息-返回
    public void onClickMyMessageReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (mPage.equals("我的消息") && mBeforePage.equals("我的")) { //如果当前界面是我的消息，点击返回按钮，应该返回到我的
            Page_My();
        } else if (mPage.equals("消息详情") && beforePageS[beforePageS.length - 1].equals("我的消息")){//如果当前界面是消息详情，点击返回按钮，应该返回到我的消息
            mPage = "我的消息";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyMessageShow();
            }
        }
    }

    //我的消息-消息详情
    public void onClickMyMessageDetails() {
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "消息详情";
    }

    //我的问答
    public void onClickMyAnswer(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的问答";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyAnswerShow();
        }
    }

    //我的问答-返回
    public void onClickMyAnswerReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (mPage.equals("我的问答") && mBeforePage.equals("我的")) { //如果当前界面是我的问答，点击返回按钮，应该返回到我的
            Page_My();
        } else if (mPage.equals("问答详情") && beforePageS[beforePageS.length - 1].equals("我的问答")){//如果当前界面是消息详情，点击返回按钮，应该返回到我的消息
            mPage = "我的问答";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyAnswerShow();
            }
        }
    }

    //跳转我的问答-问答详情
    public void Page_AnswerDetails(){
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "问答详情";
    }

    //我的缓存
    public void onClickMyCache(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的缓存";
        mBeforePage = "我的";
        if(mModelMy != null){
            if (downloadView == null){
                downloadView = new DownloadView(this);
            }
            ((ModelMy) mModelMy).MyCacheShow(downloadView);
        }
    }

    //我的缓存-返回
    public void onClickMyCacheReturn(View view) {
        if (mPage.equals("我的缓存") && mBeforePage.equals("我的")) { //如果当前界面是我的缓存，点击返回按钮，应该返回到我的
            Page_My();
        }
    }

    //我的优惠券
    public void onClickMyCoupon(View view) {
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的优惠券";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyCouponShow();
        }
    }

    //我的优惠券-返回
    public void onClickMyCouponReturn(View view) {
        if (mPage.equals("我的优惠券") && mBeforePage.equals("我的")) { //如果当前界面是我的优惠券，点击返回按钮，应该返回到我的
            Page_My();
        }
    }

//    //我的余额
//    public void onClickMyBalance(View view) {
//    }

    //跳转订单详情
    public View Page_OrderDetails(ModelOrderDetailsInterface modelOrderDetailsInterface, CourseInfo courseInfo, CoursePacketInfo coursePacketInfo,
                                  ModelMy.MyOrderlistBean.DataBean.ListBean mMyOrderListBean){
        if (modelOrderDetails == null){
            modelOrderDetails = new ModelOrderDetails();
        }
        View view = modelOrderDetails.ModelOrderDetails(modelOrderDetailsInterface,this,courseInfo,coursePacketInfo,mMyOrderListBean);
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "订单详情";
        return view;
    }

    //订单详情-返回
    public void onClickOrderDetailsReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (beforePageS[beforePageS.length - 1].equals("课程详情")){ //说明上个界面是课程详情界面
            mBeforePage = "";
            for (int i = 0 ; i < beforePageS.length - 1; i ++){
                if (i == beforePageS.length - 2){
                    mBeforePage = mBeforePage + beforePageS[i];
                } else {
                    mBeforePage = mBeforePage + beforePageS[i] + "/";
                }
            }
            mPage = beforePageS[beforePageS.length - 1];
        } else if (beforePageS[beforePageS.length - 1].equals("课程包详情")){ //说明上个界面是课程包详情界面
            mBeforePage = "";
            for (int i = 0 ; i < beforePageS.length - 1; i ++){
                if (i == beforePageS.length - 2){
                    mBeforePage = mBeforePage + beforePageS[i];
                } else {
                    mBeforePage = mBeforePage + beforePageS[i] + "/";
                }
            }
            mPage = beforePageS[beforePageS.length - 1];
        } else if (beforePageS[beforePageS.length - 1].equals("我的订单")){ //说明上个界面是我的订单界面
            mBeforePage = "";
            for (int i = 0 ; i < beforePageS.length - 1; i ++){
                if (i == beforePageS.length - 2){
                    mBeforePage = mBeforePage + beforePageS[i];
                } else {
                    mBeforePage = mBeforePage + beforePageS[i] + "/";
                }
            }
            mPage = beforePageS[beforePageS.length - 1];
        }
        modelOrderDetails.onClickOrderDetailsReturn();
    }

    //订单详情-返回
    public void onClickOrderDetailsReturn1(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (beforePageS[beforePageS.length - 1].equals("我的订单")){ //说明上个界面是我的订单界面
            mBeforePage = "";
            for (int i = 0 ; i < beforePageS.length - 1; i ++){
                if (i == beforePageS.length - 2){
                    mBeforePage = mBeforePage + beforePageS[i];
                } else {
                    mBeforePage = mBeforePage + beforePageS[i] + "/";
                }
            }
            mPage = beforePageS[beforePageS.length - 1];
        }
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "我的订单";
        mBeforePage = "我的";
        if(mModelMy != null){
            ((ModelMy) mModelMy).MyOrderShow();
        }
    }

    //跳转订单详情-选择优惠券
    public void Page_OrderDetailsChooseCoupon(){
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "选择优惠券";
    }

    //跳转订单详情-选择优惠券-返回
    public void Page_OrderDetailsChooseCouponReturn(){
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        mBeforePage = "";
        for (int i = 0 ; i < beforePageS.length - 1; i ++){
            if (i == beforePageS.length - 2){
                mBeforePage = mBeforePage + beforePageS[i];
            } else {
                mBeforePage = mBeforePage + beforePageS[i] + "/";
            }
        }
        mPage = beforePageS[beforePageS.length - 1];
    }

    //跳转订单详情-银行卡支付
    public void Page_OrderDetailsBankCard(){
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "银行卡支付";
    }

    //跳转订单详情-银行卡支付-返回
    public void Page_OrderDetailsBankCardReturn(){
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        mBeforePage = "";
        for (int i = 0 ; i < beforePageS.length - 1; i ++){
            if (i == beforePageS.length - 2){
                mBeforePage = mBeforePage + beforePageS[i];
            } else {
                mBeforePage = mBeforePage + beforePageS[i] + "/";
            }
        }
        mPage = beforePageS[beforePageS.length - 1];
    }

    //跳转课程包
    public void Page_MoreCoursePacket(){
        mBottomNavigationView.setVisibility(View.VISIBLE);
        Menu menu = mBottomNavigationView.getMenu();
        if (menu == null){
            return;
        }
        mPage = "课程包";
        mBeforePage = "";
        MenuItem menuItem = menu.findItem(2);
        if (menuItem != null){
            menuItem.setChecked(true);
            mBottomNavigationView.setVisibility(View.VISIBLE);
        } else { //说明底部菜单中没有课程包按钮，将所有按钮隐藏
            mBottomNavigationView.setVisibility(View.INVISIBLE);
            mBeforePage = "首页";
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelCoursePacket == null){
            mModelCoursePacket = ModelCoursePacket.newInstance(mThis,"课程包:" + mBeforePage,R.layout.fragment_coursepacket);//"课程包"
            transaction.add(R.id.framepage,mModelCoursePacket);
        }else{
            transaction.show(mModelCoursePacket);
            if (mBeforePage.equals("")) {
                ((ModelCoursePacket) mModelCoursePacket).CoursePacketMainShow(1);
            } else {
                ((ModelCoursePacket) mModelCoursePacket).CoursePacketMainShow(0);
            }
        }
        transaction.commit();
    }

    //跳转题库
    public void Page_QuestionBank(){
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "题库";
        mBeforePage = "首页";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelQuestionBank == null){
            mModelQuestionBank = ModelQuestionBank.newInstance(mThis,"题库:" + mBeforePage,R.layout.fragment_questionbank);//"题库"
            transaction.add(R.id.framepage,mModelQuestionBank);
        } else{
            transaction.show(mModelQuestionBank);
            ((ModelQuestionBank) mModelQuestionBank).QuestionBankMainShow("题库");
        }
        transaction.commit();
    }

    //题库-返回
    public void onClickQuestionBankReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 1){
            if (mBeforePage.equals("首页") && mPage.equals("题库")){ //说明上个界面是首页
                Page_HomePage();
                mBottomNavigationView.setVisibility(View.VISIBLE);
            } else if (mBeforePage.equals("我的") && mPage.equals("我的题库")){ //说明上个界面是首页
                Page_My();
            }
            return;
        }
        if (beforePageS[beforePageS.length - 1].equals("首页")){ //说明上个界面是首页
            Page_HomePage();
            mBottomNavigationView.setVisibility(View.VISIBLE);
        } else if (beforePageS[beforePageS.length - 1].equals("我的")){ //说明上个界面是我的
            Page_My();
        } else if (beforePageS[beforePageS.length - 1].equals("题库") || beforePageS[beforePageS.length - 1].equals("我的题库")
                || beforePageS[beforePageS.length - 1].equals("题库更多") || beforePageS[beforePageS.length - 1].equals("题库详情")
                || beforePageS[beforePageS.length - 1].equals("做题设置")){ //说明上个界面是题库、做题设置、题库详情、题库更多或我的题库
            mBeforePage = "";
            for (int i = 0 ; i < beforePageS.length - 1; i ++){
                if (i == beforePageS.length - 2){
                    mBeforePage = mBeforePage + beforePageS[i];
                } else {
                    mBeforePage = mBeforePage + beforePageS[i] + "/";
                }
            }
            mPage = beforePageS[beforePageS.length - 1];
            if (mPage.equals("我的题库")){
                onClickMyQuestionBank(null);
            } else {
                Page_QuestionBank();
            }
        }
    }

    //跳转到题库更多界面
    public void onClickQuestionBankMore() {
        if (mBeforePage.contains("题库更多")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/题库更多"));
        } else {
            mBeforePage = mBeforePage + "/" + mPage ;
        }
        mPage = "题库更多";
    }

    //跳转到题库详情
    public void onClickQuestionBankDetails() {
        if (mBeforePage.contains("题库详情")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/题库详情"));
        } else {
            mBeforePage = mBeforePage + "/" + mPage ;
        }
        mPage = "题库详情";
    }

    //跳转到做题设置
    public void onClickQuestionBankSetting() {
        if (mBeforePage.contains("做题设置")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/做题设置"));
        } else {
            mBeforePage = mBeforePage + "/" + mPage ;
        }
        mPage = "做题设置";
    }

    //跳转题库答题卡界面
    public void onClickQuestionBankAnswerQuestionCard(){
        if (mBeforePage.contains("题库答题卡")){
            mBeforePage = mBeforePage.substring(0,mBeforePage.indexOf("/题库答题卡"));
        } else {
            mBeforePage = mBeforePage + "/" + mPage ;
        }
        mPage = "题库答题卡";
    }

    //关闭题库答题卡界面
    public void onClickQuestionBankAnswerQuestionCardReturn(View view) {
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        mBeforePage = mBeforePage.substring(0,mBeforePage.lastIndexOf("/"));
        mPage = beforePageS[beforePageS.length - 1];
        if (mModelQuestionBank != null) {
            ((ModelQuestionBank) mModelQuestionBank).QuestionBankDetailsQuestionModeShow();
        }
    }

    //跳转首页
    public void Page_HomePage(){
        mBottomNavigationView.setVisibility(View.VISIBLE);
        mPage = "首页";
        mBeforePage = "";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelHomePage == null){
            mModelHomePage = ModelHomePage.newInstance(mThis,"首页",R.layout.homepage_layout);
            transaction.add(R.id.framepage,mModelHomePage);
        }else{
            ((ModelHomePage)mModelHomePage).HomePageShow();
            transaction.show(mModelHomePage);
        }
        transaction.commit();
    }

    //跳转社区问答
    public void Page_CommunityAnswer(){
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "社区问答";
        mBeforePage = "首页";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelCommunityAnswer == null){
            mModelCommunityAnswer = ModelCommunityAnswer.newInstance(mThis,"社区问答:" + mBeforePage,R.layout.fragment_communityanswer);//"社区问答"
            transaction.add(R.id.framepage,mModelCommunityAnswer);
        }else{
            transaction.show(mModelCommunityAnswer);
            ((ModelCommunityAnswer) mModelCommunityAnswer).CommunityAnswerMainShow();
        }
        transaction.commit();
    }

    //跳转社区问答-搜索界面
    public void Page_onCommunityAnswerSearch(){
        mPage = "问答搜索";
        mBeforePage = "社区问答";
    }

    //跳转社区问答-添加问答界面
    public void Page_onCommunityAnswerAdd(){
        mPage = "添加问答";
        mBeforePage = "社区问答";
    }

    //跳转社区问答-选择标签界面
    public void Page_onCommunityAnswerChooseSign(){
        mPage = "选择标签";
        mBeforePage = "添加问答";
    }

    //跳转社区问答-问答详情界面
    public void Page_onCommunityAnswerDetails(){
        mPage = "问答详情";
        mBeforePage = "社区问答";
    }

    //社区问答-返回
    public void onClickCommunityAnswerReturn(View view) {
        if (mState.equals("发布问答")){
            Toast.makeText(this,"正在发布问题，请稍后!",Toast.LENGTH_LONG).show();
            return ;
        }
        if (mPage.equals("社区问答") && mBeforePage.equals("首页")) { //如果当前界面是社区问答，点击返回按钮，应该返回到首页
            Page_HomePage();
            mBottomNavigationView.setVisibility(View.VISIBLE);
        } else if (mPage.equals("问答搜索") && mBeforePage.equals("社区问答")) { //如果当前界面是问答搜索，点击返回按钮，应该返回到社区问答
            Page_CommunityAnswer();
        } else if (mPage.equals("添加问答") && mBeforePage.equals("社区问答")) { //如果当前界面是添加问答，点击返回按钮，应该返回到社区问答
            mPage = "社区问答";
            mBeforePage = "首页";
            if (mModelCommunityAnswer != null) {
                ((ModelCommunityAnswer) mModelCommunityAnswer).CommunityAnswerAddReturn();
            }
        } else if (mPage.equals("选择标签") && mBeforePage.equals("添加问答")) { //如果当前界面是选择标签，点击返回按钮，应该返回到添加问答
            if (mModelCommunityAnswer != null) {
                ((ModelCommunityAnswer) mModelCommunityAnswer).CommunityAnswerAddInit(false);
            }
            mPage = "添加问答";
            mBeforePage = "社区问答";
        } else if (mPage.equals("问答详情") && mBeforePage.equals("社区问答")) { //如果当前界面是问答详情，点击返回按钮，应该返回到社区问答
            Page_CommunityAnswer();
        }
    }

    //跳转课程表
    public void Page_ClassCheduleCard(){
        mBottomNavigationView.setVisibility(View.VISIBLE);
        Menu menu = mBottomNavigationView.getMenu();
        if (menu == null){
            return;
        }
        mPage = "课程表";
        mBeforePage = "";
        MenuItem menuItem = menu.findItem(3);
        if (menuItem != null){
            menuItem.setChecked(true);
        } else { //说明底部菜单中没有课程包按钮，将所有按钮隐藏
            mBottomNavigationView.setVisibility(View.INVISIBLE);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelClassCheduleCard == null){
            mModelClassCheduleCard = ModelClassCheduleCard.newInstance(mThis,"首页",R.layout.fragment_classchedulecard);//"课程表"
            transaction.add(R.id.framepage,mModelClassCheduleCard);
        }else{
            transaction.show(mModelClassCheduleCard);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((ModelClassCheduleCard)mModelClassCheduleCard).ClassCheduleCardMainInit("首页");
            }
        }
        transaction.commit();
    }

    //跳转课程
    public void Page_Course(){
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "课程";
        mBeforePage = "首页";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelCourse == null){
            mModelCourse = ModelCourse.newInstance(mThis,"课程:" + mBeforePage,R.layout.fragment_course);//"课程"
            transaction.add(R.id.framepage,mModelCourse);
        }else{
            transaction.show(mModelCourse);
            ((ModelCourse) mModelCourse).CourseMainShow();
        }
        transaction.commit();
    }

    //跳转我的
    public void Page_My(){
        mBottomNavigationView.setVisibility(View.VISIBLE);
        Menu menu = mBottomNavigationView.getMenu();
        if (menu == null){
            return;
        }
        mPage = "我的";
        mBeforePage = "";
        MenuItem menuItem = menu.findItem(4);
        if (menuItem != null){
            menuItem.setChecked(true);
        } else { //说明底部菜单中没有课程包按钮，将所有按钮隐藏
            mBottomNavigationView.setVisibility(View.INVISIBLE);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelMy == null){
            mModelMy = ModelMy.newInstance(mThis,"我的",R.layout.my_layout);//"我的"
            transaction.add(R.id.framepage,mModelMy);
        }else{
            ((ModelMy)mModelMy).getPersonalInfoDatas();
//            ((ModelMy)mModelMy).ModelMyInit();
            if (mModelSetting!=null){
                ((ModelSetting)mModelSetting).getPersonalInfoDatas();
            }
            transaction.show(mModelMy);
        }
        transaction.commit();
    }

    //跳转登录界面
    public void Page_LogIn(){
        //隐藏所有的底部按钮
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelLogIn == null){
            mModelLogIn = ModelLogIn.newInstance(mThis,R.layout.modellogin);//"登录"
            transaction.add(R.id.framepage,mModelLogIn);
        }else{
            transaction.show(mModelLogIn);
            ((ModelLogIn)mModelLogIn).LogInMainShow();
        }
        transaction.commit();
    }

    //跳转公开课
    public void Page_OpenClass(){
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "公开课";
        mBeforePage = "首页";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelOpenClass == null){
            mModelOpenClass = ModelOpenClass.newInstance(mThis,"公开课",R.layout.openclass_layout);
            transaction.add(R.id.framepage,mModelOpenClass);
        }else{
            ((ModelOpenClass)mModelOpenClass).CourseMainShow();
            transaction.show(mModelOpenClass);
        }
        transaction.commit();
    }

    //跳转新闻资讯
    public void Page_News(){
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mPage = "新闻资讯";
        mBeforePage = "首页";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        if(mModelNews == null){
            mModelNews = ModelNews.newInstance(mThis,"新闻资讯",R.layout.news_layout);
            transaction.add(R.id.framepage,mModelNews);
        }else{
            ((ModelNews)mModelNews).NewsMainShow();
            transaction.show(mModelNews);
        }
        transaction.commit();
    }

    //跳转新闻资讯-新闻详情
    public void Page_NewsDetails(){
        mBeforePage = mBeforePage + "/" + mPage ;
        mPage = "新闻详情";
    }

    //点击新闻的返回按钮
    public void onClickNewsReturn(View view) { //新闻的返回
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        } else if (beforePageS.length >= 1){
            if (mPage.equals("新闻详情") && beforePageS[beforePageS.length - 1].equals("新闻资讯")){
                Page_News();
            }
        } else {
            if (mPage.equals("新闻资讯") && mBeforePage.equals("首页")) { //如果当前界面是新闻资讯，点击返回按钮，应该返回到首页
                Page_HomePage();
                mBottomNavigationView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setmState(String state){
        this.mState = state;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAliyunVodPlayerView != null) {
            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
            if (!handler) {
                return false;
            }
        }
        String beforePageS[] = mBeforePage.split("/");
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (mState.equals("发布问答")){
                Toast.makeText(this,"正在发布问题，请稍后!",Toast.LENGTH_LONG).show();
                return false;
            }
            if (beforePageS.length <= 0){
                return false;
            } else if (beforePageS.length >= 1){
                if (mPage.equals("协议详情") && beforePageS[beforePageS.length - 1].equals("我的课程")) { //如果当前界面是我的课程，点击返回按钮，应该返回到我的
                    mBottomNavigationView.setVisibility(View.INVISIBLE);
                    mPage = "我的课程";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyClassShow();
                    }
                    return true;
                } else if (mPage.equals("协议详情") && beforePageS[beforePageS.length - 1].equals("我的课程包")) { //如果当前界面是我的课程包，点击返回按钮，应该返回到我的
                    mPage = "我的课程包";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyClassPacketShow();
                    }
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("课程包") && mPage.equals("课程包详情")){//说明上个界面是课程包界面
                    Page_MoreCoursePacket();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("课程包") && mPage.equals("课程包搜索")){//说明上个界面是课程包界面
                    Page_MoreCoursePacket();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("课程") && mPage.equals("课程搜索")){//说明上个界面是课程界面
                    Page_Course();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("社区问答") && mPage.equals("问答搜索")){//说明上个界面是课程界面
                    Page_CommunityAnswer();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("首页") && mPage.equals("课程包详情")){ //说明上个界面是首页
                    Page_HomePage();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("我的课程包") && mPage.equals("课程包详情")){ //说明上个界面是我的课程包界面
                    mPage = "我的课程包";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyClassPacketShow();
                    }
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("我的收藏") && mPage.equals("课程包详情")){ //说明上个界面是我的收藏界面
                    mPage = "我的收藏";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyCollectShow();
                    }
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("课程") && mPage.equals("课程详情")){ //说明上个界面是课程界面
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                        int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
                        String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
                        mAliyunVodPlayerView.onDestroy();
                        mAliyunVodPlayerView = null;
                        if (!SectionsId.equals("")) {
                            SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
                        }
                    }
                    Page_Course();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("首页") && mPage.equals("课程详情")){ //说明上个界面是首页界面
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                        int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
                        String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
                        mAliyunVodPlayerView.onDestroy();
                        mAliyunVodPlayerView = null;
                        if (!SectionsId.equals("")) {
                            SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
                        }
                    }
                    Page_HomePage();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("我的缓存") && mPage.equals("我的缓存播放")){ //说明上个界面是我的界面
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                        int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
                        String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
                        mAliyunVodPlayerView.onDestroy();
                        mAliyunVodPlayerView = null;
                        if (!SectionsId.equals("")) {
                            SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
                        }
                    }
                    mPage = "我的缓存";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        if (downloadView == null){
                            downloadView = new DownloadView(this);
                        }
                        ((ModelMy) mModelMy).MyCacheShow(downloadView);
                    }
                } else if (beforePageS[beforePageS.length - 1].equals("首页") && mPage.equals("课程")){ //说明上个界面是首页界面
                    Page_HomePage();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("首页") && mPage.equals("课程包")){ //说明上个界面是首页界面
                    Page_HomePage();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("我的课程") && mPage.equals("课程详情")){//说明上个界面是我的课程界面
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                        int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
                        String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
                        mAliyunVodPlayerView.onDestroy();
                        mAliyunVodPlayerView = null;
                        if (!SectionsId.equals("")) {
                            SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
                        }
                    }
                    mPage = "我的课程";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyClassShow();
                    }
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("我的收藏") && mPage.equals("课程详情")){ //说明上个界面是我的收藏界面
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                        int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
                        String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
                        mAliyunVodPlayerView.onDestroy();
                        mAliyunVodPlayerView = null;
                        if (!SectionsId.equals("")) {
                            SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
                        }
                    }
                    mPage = "我的收藏";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyCollectShow();
                    }
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("题库") || beforePageS[beforePageS.length - 1].equals("我的题库")
                        || beforePageS[beforePageS.length - 1].equals("题库更多") || beforePageS[beforePageS.length - 1].equals("题库详情")
                        || beforePageS[beforePageS.length - 1].equals("做题设置")){ //说明上个界面是题库、做题设置、题库详情、题库更多或我的题库
                    mBeforePage = "";
                    for (int i = 0 ; i < beforePageS.length - 1; i ++){
                        if (i == beforePageS.length - 2){
                            mBeforePage = mBeforePage + beforePageS[i];
                        } else {
                            mBeforePage = mBeforePage + beforePageS[i] + "/";
                        }
                    }
                    mPage = beforePageS[beforePageS.length - 1];
                    if (mPage.equals("我的题库")){
                        onClickMyQuestionBank(null);
                    } else {
                        Page_QuestionBank();
                    }
                    return true;
                } else if (mPage.equals("消息详情") && beforePageS[beforePageS.length - 1].equals("我的消息")){//如果当前界面是消息详情，点击返回按钮，应该返回到我的消息
                    mPage = "我的消息";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyMessageShow();
                    }
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("课程详情")){ //说明上个界面是课程详情界面
                    mBeforePage = "";
                    for (int i = 0 ; i < beforePageS.length - 1; i ++){
                        if (i == beforePageS.length - 2){
                            mBeforePage = mBeforePage + beforePageS[i];
                        } else {
                            mBeforePage = mBeforePage + beforePageS[i] + "/";
                        }
                    }
                    mPage = beforePageS[beforePageS.length - 1];
                    modelOrderDetails.onClickOrderDetailsReturn();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("订单详情") && mPage.equals("选择优惠券")){ //说明上个界面是订单详情界面
                    mBeforePage = "";
                    for (int i = 0 ; i < beforePageS.length - 1; i ++){
                        if (i == beforePageS.length - 2){
                            mBeforePage = mBeforePage + beforePageS[i];
                        } else {
                            mBeforePage = mBeforePage + beforePageS[i] + "/";
                        }
                    }
                    mPage = beforePageS[beforePageS.length - 1];
                    modelOrderDetails.onClickOrderDetailsChooseCouponReturn();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("订单详情") && mPage.equals("银行卡支付")){ //说明上个界面是订单详情界面
                    mBeforePage = "";
                    for (int i = 0 ; i < beforePageS.length - 1; i ++){
                        if (i == beforePageS.length - 2){
                            mBeforePage = mBeforePage + beforePageS[i];
                        } else {
                            mBeforePage = mBeforePage + beforePageS[i] + "/";
                        }
                    }
                    mPage = beforePageS[beforePageS.length - 1];
                    modelOrderDetails.onClickOrderDetailsBankCardReturn();
                    return true;
                } else if (beforePageS[beforePageS.length - 1].equals("课程包详情")){ //说明上个界面是课程包详情界面
                    if (!mPage.contains("课程详情")) {
                        mBeforePage = "";
                        for (int i = 0 ; i < beforePageS.length - 1; i ++){
                            if (i == beforePageS.length - 2){
                                mBeforePage = mBeforePage + beforePageS[i];
                            } else {
                                mBeforePage = mBeforePage + beforePageS[i] + "/";
                            }
                        }
                        mPage = beforePageS[beforePageS.length - 1];
                        if (modelOrderDetails != null) {
                            modelOrderDetails.onClickOrderDetailsReturn();
                        }
                    } else { //返回到课程包详情界面
                        if (mAliyunVodPlayerView != null) {
                            mAliyunVodPlayerView.onStop();
                            int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
                            String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
                            mAliyunVodPlayerView.onDestroy();
                            mAliyunVodPlayerView = null;
                            if (!SectionsId.equals("")) {
                                SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
                            }
                        }
                        mBeforePage = "";
                        for (int i = 0 ; i < beforePageS.length - 1; i ++){
                            if (i == beforePageS.length - 2){
                                mBeforePage = mBeforePage + beforePageS[i];
                            } else {
                                mBeforePage = mBeforePage + beforePageS[i] + "/";
                            }
                        }
                        mPage = beforePageS[beforePageS.length - 1];
                        Page_MoreCoursePacket();
                    }
                    return true;
                } else if (mPage.equals("新闻详情") && beforePageS[beforePageS.length - 1].equals("新闻资讯")){//如果当前界面是新闻详情，点击返回按钮，应该返回到新闻资讯
                    Page_News();
                    return true;
                } else if (mPage.equals("登录") && mBeforePage.equals("我的")) { //如果当前界面是登录界面，点击返回按钮，应该返回到我的界面
                    Page_My();
                    return true;
                } else if (mPage.equals("注册") && mBeforePage.equals("登录")) { //如果当前界面是注册，点击返回按钮，应该返回到登录界面
                    mPage = "登录";
                    mBeforePage = "我的";
                    Page_LogIn();
                    return true;
                } else if (mPage.equals("忘记密码") && mBeforePage.equals("登录")) { //如果当前界面是忘记密码，点击返回按钮，应该返回到登录界面.
                    mPage = "登录";
                    mBeforePage = "我的";
                    Page_LogIn();
                    return true;
                } else if (mPage.equals("设置") && mBeforePage.equals("我的")) { //如果当前界面是设置，点击返回按钮，应该返回到我的界面
                    Page_My();
                    return true;
                } else if (mPage.equals("基本信息") && mBeforePage.equals("设置")) { //如果当前界面是设置-基本信息，点击返回按钮，应该返回到设置
                    if (mModelSetting != null) {
                        mPage = "设置";
                        mBeforePage = "我的";
                        ((ModelSetting) mModelSetting).SettingMainShow(0);
                    }
                    return true;
                } else if (mPage.equals("基本信息") && mBeforePage.equals("我的")) { //如果当前界面是设置-基本信息，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("登录") && mBeforePage.equals("设置")) { //如果当前界面是登录，点击返回按钮，应该返回到设置
                    if (mModelSetting != null) {
                        mPage = "设置";
                        mBeforePage = "我的";
                        //隐藏所有的底部按钮
                        mBottomNavigationView.setVisibility(View.INVISIBLE);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        hideAllFragment(transaction);
                        if(mModelSetting == null){
                            mModelSetting = ModelSetting.newInstance(mThis,"设置-基本信息",R.layout.modelsetting);//"设置"
                            transaction.add(R.id.framepage,mModelSetting);
                        } else {
                            transaction.show(mModelSetting);
                        }
                        transaction.commit();
                    }
                    return true;
                } else if (mPage.equals("修改名称") && mBeforePage.equals("基本信息")) { //如果当前界面是修改名称，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("修改昵称") && mBeforePage.equals("基本信息")) { //如果当前界面是修改昵称，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("修改签名") && mBeforePage.equals("基本信息")) { //如果当前界面是修改签名，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("修改邮箱") && mBeforePage.equals("基本信息")) { //如果当前界面是修改邮箱，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("修改电话号码") && mBeforePage.equals("基本信息")) { //如果当前界面是修改电话号码，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("修改证件号码") && mBeforePage.equals("基本信息")) { //如果当前界面是修改证件号码，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("修改密码") && mBeforePage.equals("基本信息")) { //如果当前界面是修改密码，点击返回按钮，应该返回到基本信息
                    if (mModelSetting != null) {
                        mPage = "基本信息";
                        mBeforePage = "设置";
                        ((ModelSetting)mModelSetting).SettingBaseInfoMainShow(1);
                    }
                    return true;
                } else if (mPage.equals("关于我们") && mBeforePage.equals("设置")) { //如果当前界面是关于我们，点击返回按钮，应该返回到设置
                    if (mModelSetting != null) {
                        mPage = "设置";
                        mBeforePage = "我的";
                        ((ModelSetting) mModelSetting).SettingMainShow(0);
                    }
                    return true;
                } else if (mPage.equals("课程") && mBeforePage.equals("首页")) { //如果当前界面是课程，点击返回按钮，应该返回到首页
                    Page_HomePage();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                } else if (mPage.equals("课程包") && mBeforePage.equals("首页")) { //如果当前界面是课程包，点击返回按钮，应该返回到首页
                    Page_HomePage();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                } else if (mPage.equals("题库") && mBeforePage.equals("首页")) { //如果当前界面是题库，点击返回按钮，应该返回到首页
                    Page_HomePage();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                } else if (mPage.equals("题库答题卡") && mBeforePage.equals("题库详情")) { //如果当前界面是题库详情，点击返回按钮，应该返回到题库详情
                    mPage = "题库详情";
                    mBeforePage = "题库";
                    if (mModelQuestionBank != null) {
                        ((ModelQuestionBank) mModelQuestionBank).QuestionBankDetailsQuestionModeShow();
                    }
                    return true;
                } else if (mPage.equals("公开课") && mBeforePage.equals("首页")) { //如果当前界面是公开课，点击返回按钮，应该返回到首页
                    Page_HomePage();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                } else if (mPage.equals("社区问答") && mBeforePage.equals("首页")) { //如果当前界面是社区问答，点击返回按钮，应该返回到首页
                    Page_HomePage();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                } else if (mPage.equals("问答搜索") && mBeforePage.equals("社区问答")) { //如果当前界面是问答搜索，点击返回按钮，应该返回到社区问答
                    Page_CommunityAnswer();
                    return true;
                } else if (mPage.equals("问答详情") && mBeforePage.equals("社区问答")) { //如果当前界面是问答详情，点击返回按钮，应该返回到社区问答
                    Page_CommunityAnswer();
                    return true;
                } else if (mPage.equals("添加问答") && mBeforePage.equals("社区问答")) { //如果当前界面是添加问答，点击返回按钮，应该返回到社区问答
                    mPage = "社区问答";
                    mBeforePage = "首页";
                    if (mModelCommunityAnswer != null) {
                        ((ModelCommunityAnswer) mModelCommunityAnswer).CommunityAnswerAddReturn();
                    }
                    return true;
                } else if (mPage.equals("选择标签") && mBeforePage.equals("添加问答")) { //如果当前界面是选择标签，点击返回按钮，应该返回到添加问答
                    if (mModelCommunityAnswer != null) {
                        ((ModelCommunityAnswer) mModelCommunityAnswer).CommunityAnswerAddInit(false);
                    }
                    mPage = "添加问答";
                    mBeforePage = "社区问答";
                    return true;
                } else if (mPage.equals("我的课程") && mBeforePage.equals("我的")) { //如果当前界面是我的课程，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("我的课程包") && mBeforePage.equals("我的")) { //如果当前界面是我的课程包，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("我的收藏") && mBeforePage.equals("我的")) { //如果当前界面是我的收藏，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("我的题库") && mBeforePage.equals("我的")) { //如果当前界面是我的题库，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("课程表") && mBeforePage.equals("我的课程")) { //如果当前界面是我的课程，点击返回按钮，应该返回到我的
                    if(mModelMy != null){
                        Page_My();
//                        mBottomNavigationView.setVisibility(View.INVISIBLE);
//                        mPage = "我的课程";
//                        mBeforePage = "我的";
//                        ((ModelMy) mModelMy).MyClassShow();
                    }
                    return true;
                } else  if (mPage.equals("我的缓存") && mBeforePage.equals("我的")) { //如果当前界面是我的缓存，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("我的订单") && mBeforePage.equals("我的")) { //如果当前界面是我的订单，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("订单详情") && mBeforePage.contains("我的订单")) { //如果当前界面是订单详情，点击返回按钮，应该返回到我的订单
                    mPage = "我的订单";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyOrderShow();
                    }
                    return true;
                } else if (mPage.equals("我的优惠券") && mBeforePage.equals("我的")) { //如果当前界面是我的优惠券，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("我的消息") && mBeforePage.equals("我的")) { //如果当前界面是我的消息，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("新闻资讯") && mBeforePage.equals("首页")) { //如果当前界面是新闻资讯，点击返回按钮，应该返回到首页
                    Page_HomePage();
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                } else if (mPage.equals("我的问答") && mBeforePage.equals("我的")) { //如果当前界面是我的问答，点击返回按钮，应该返回到我的
                    Page_My();
                    return true;
                } else if (mPage.equals("问答详情") && beforePageS[beforePageS.length - 1].equals("我的问答")){//如果当前界面是消息详情，点击返回按钮，应该返回到我的消息
                    mPage = "我的问答";
                    mBeforePage = "我的";
                    if(mModelMy != null){
                        ((ModelMy) mModelMy).MyAnswerShow();
                    }
                    return true;
                } else if (mPage.equals("订单结果") && beforePageS[beforePageS.length - 1].equals("订单详情")) { //如果当前界面是订单结果，点击返回按钮，应该返回到首页
                    if (mBeforePage.contains("首页")){
                        Page_HomePage();
                    } else if (mBeforePage.contains("我的")){
                        Page_My();
                    } else if (mBeforePage.contains("课程包")){
                        Page_MoreCoursePacket();
                    }
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //判断邮箱格式是否正确
    public static boolean isEmail(String email){
        if (null==email || "".equals(email))
            return false;
        Pattern p =  Pattern.compile("^^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    //判断手机号码是否正确
    public static boolean isTelNumber(String telNumber){
        if (null == telNumber || "".equals(telNumber))
            return false;
        Pattern p = Pattern.compile("^((19[0-9])|(17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(telNumber);
        return m.find();//boolean
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr
     *            身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    public static boolean isIdNumber(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return false;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return false;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                return false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return false;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return false;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return false;
            }
        } else {
            return true;
        }
        // =====================(end)=====================
        return true;
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     *
     * @param str
     * @return
     */
    private static boolean isDataFormat(String str) {
        boolean flag = false;
        // String
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    //获取权限
    private void getPermission() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(this,"请打开系统通知功能",Toast.LENGTH_SHORT).show();
            Intent localIntent = new Intent();
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", getPackageName());
                localIntent.putExtra("app_uid", getApplicationInfo().uid);
            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + getPackageName()));
            } else {
                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                }
            }
            startActivity(localIntent);
        }
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的摄像头、音频录制、扩展卡读写使用权限", 1, permissions);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
//        processExtraData();
//        checkUpdateVersion();
    }
    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }
    //激活相机操作
    private void goCamera() {
        if (mModelSetting != null){
            ((ModelSetting) mModelSetting).SetttingButtonDialogCancel();
        }
        cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //第二个参数为 包名.fileprovider
            uri = FileProvider.getUriForFile(ControlMainActivity.this, PublicCommonUtil.fileProvider, cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        ControlMainActivity.this.startActivityForResult(intent, CAMERA);
    }
    //激活相册操作
    private void goPhotoAlbum() {
        if (mModelSetting != null){
            ((ModelSetting) mModelSetting).SetttingButtonDialogCancel();
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Log.w("", "{onActivityResult}resultCode="+resultCode);
        Log.w("", "{onActivityResult}requestCode="+requestCode);
        if (resultCode == Activity.RESULT_OK) {
            //选择照片
            if(requestCode == ControllerGlobals.CHOOSE_PIC_REQUEST_CODE){
                String beforePageS[] = mBeforePage.split("/");
                if (beforePageS.length < 1){
                    return;
                }
                if (mModelCourse != null && beforePageS[beforePageS.length - 1].equals("课程") && mPage.equals("课程详情")){
                    ((ModelCourse)mModelCourse).ModelCourseCoverQuestionPictureAdd(data);
                } else if (mModelHomePage != null && beforePageS[beforePageS.length - 1].equals("首页") && mPage.equals("课程详情")){
                    ((ModelHomePage)mModelHomePage).ModelCourseCoverQuestionPictureAdd(data);
                } else if (mModelHomePage != null && beforePageS[beforePageS.length - 1].equals("我的课程") && mPage.equals("课程详情")){
                    ((ModelMy)mModelMy).ModelCourseCoverQuestionPictureAdd(data);
                }
//                else if (mModelCommunityAnswer != null && beforePageS[beforePageS.length - 1].equals("首页") && mPage.equals("社区问答")){
//                    ((ModelCommunityAnswer)mModelCommunityAnswer).CommunityAnswerPictureAdd(data);
//                }
                else if (mModelCommunityAnswer != null && beforePageS[beforePageS.length - 1].equals("社区问答") && mPage.equals("添加问答")){
                    ((ModelCommunityAnswer)mModelCommunityAnswer).CommunityAnswerPictureAdd(data);
                }
            }
        }

        switch (requestCode) {
            // 裁剪相机照片
            case CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    picPath = String.valueOf(cameraSavePath);
                } else {
                    picPath = uri.getEncodedPath();
                }
                Log.d("返回图片路径拍照:", picPath);
                mOutImage = new File(picPath);
                setCropPhoto();
                break;
            //裁剪本地相册
            case ALBUM:
                picPath = ModelGetPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
                Log.d("返回图片路径相册:", picPath);
                mOutImage = new File(picPath);
                setCropPhoto();
                break;

            //裁剪完成
            case CUPREQUEST:
                if (data == null) {
                    return;
                }
                if (data.getData() != null){
                    picPath = ModelGetPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
                } else {
                    picPath = "";
                }
                Bundle extras = data.getExtras();
                String s = "";
                if (Build.MANUFACTURER.contains("Xiaomi")){
                    if (uritempFile != null){
                        s = uritempFile.getPath();
                    }
                } else {
                    if (extras != null){
                        Bitmap photo = extras.getParcelable("data");
                        Drawable drawable = new BitmapDrawable(null, photo);
                    }
                }
                if (s.equals("")){
                    s = picPath;
                }
                //s : 裁剪后的图片路径  将界面直接跳转到我的，并且将剪切好的头像上传到服务器
                if (mModelSetting != null){
                    ((ModelSetting)mModelSetting).ModifyingHead(s);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setCropPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果是7.0剪裁图片 同理 需要把uri包装
            //通过FileProvider创建一个content类型的Uri
            Uri inputUri = FileProvider.getUriForFile(ControlMainActivity.this,
                    PublicCommonUtil.fileProvider, mOutImage);
            startPhotoZoom(inputUri);//设置输入类型
        } else {
            Uri inputUri = Uri.fromFile(mOutImage);
            startPhotoZoom(inputUri);
        }
    }

    //裁剪
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //去除默认的人脸识别，否则和剪裁匡重叠
            intent.putExtra("noFaceDetection", false);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        }
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 宽高的比例
        //华为特殊处理 不然会显示圆
        if (Build.MODEL.contains("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        //miui系统 特殊处理 return-data的方式只适用于小图。
        if (Build.MANUFACTURER.contains("Xiaomi")){//裁剪后的图片Uri路径，uritempFile为Uri类变量
            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "eduhead.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        } else {
            intent.putExtra("return-data", true);
            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "eduhead.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CUPREQUEST);
    }

    /**
     * 登录-直播或回放
     */
    public void LoginLiveOrPlayback(int course_id,int is_public,int type) { //String id,String name,String password,int type
        //服务器请求
        String params = "{\"course_id\":" + course_id + "," ;
        if (!mStuId.equals("")) {
            params = params + "\"stu_id\":" + mStuId + ",";
        }
        params = params + "\"is_public\":" + is_public + "}";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),params);
        Call<ModelObservableInterface.BaseBean> call;
        if (!mStuId.equals("")) {
            if (type == PlayType.LIVE) {
                call = modelObservableInterface.getHuanTuoLiveToken(body);
            } else {
                call = modelObservableInterface.getHuanTuoRecToken(body);
            }
        } else {
            if (type == PlayType.LIVE) {
                call = modelObservableInterface.gethuanTuoLiveZeroToken(body);
            } else {
                call = modelObservableInterface.gethuanTuoRecZeroToken(body);
            }
        }
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    Toast.makeText(mThis,"此课程资源不存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    return;
                }
                if (baseBean.getErrorCode() != 200 || baseBean.getData() == null){
                    Toast.makeText(mThis,baseBean.getErrorMsg(),Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> baseBeans = baseBean.getData();
                if (baseBeans.get("access_token") == null || baseBeans.get("access_key") == null){
                    Toast.makeText(mThis,"此课程资源不存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                String access_token = String.valueOf(baseBeans.get("access_token"));
//                String access_key = String.valueOf(baseBeans.get("access_key"));
                Bundle bundle = new Bundle();
                bundle.putString(LoginJumpActivity.TOKEN_PARAM, access_token);
                bundle.putString(LoginJumpActivity.LOG0_PARAM, "");
                bundle.putString(LoginJumpActivity.TITLE_PARAM, "");
                bundle.putInt(LoginJumpActivity.TYPE_PARAM, type);
                bundle.putString(LoginJumpActivity.ID_PARAM, "");
                bundle.putInt(LoginJumpActivity.MODE_TYPE, 0);
                bundle.putInt(LoginJumpActivity.SMALL_TYPE, 0);
                if (type == PlayType.LIVE) {
                    ActivityUtil.jump(ControlMainActivity.this, LoginJumpActivity.class, bundle);
                } else {
                    requestPlaybackType(access_token, bundle);
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mThis,"此课程资源不存在",Toast.LENGTH_SHORT).show();
            }
        });

//        //服务器请求
//        String params = type == PlayType.LIVE ? String.format(MainConsts.LIVE_LOGIN_PARAM, id, password, name, type) : String.format(MainConsts.PLAYBACK_LOGIN_PARAM, id, password, type);
//        if (httpRequest == null)
//            httpRequest = new HttpRequest(this);
//        httpRequest.sendRequestWithPost(MainConsts.LOGIN_URL, params, new HttpRequest.IHttpRequestListener() {
//            @Override
//            public void onRequestCompleted(String responseStr) {
//                try {
//                    JSONObject jsonObject = new JSONObject(responseStr);
//                    int code = jsonObject.optInt("code");
//                    if (code == 0) {
//                        JSONObject data = jsonObject.optJSONObject("data");
//                        if (data != null) {
//                            String token = data.optString("access_token");
//                            String logo = data.optString("logo");
//                            String title = data.optString("title");
//                            int modeType = data.optInt("modetype");
//                            int smallType = data.optInt("smallType");
//                            Bundle bundle = new Bundle();
//                            bundle.putString(LoginJumpActivity.TOKEN_PARAM, token);
//                            bundle.putString(LoginJumpActivity.LOG0_PARAM, logo);
//                            bundle.putString(LoginJumpActivity.TITLE_PARAM, title);
//                            bundle.putInt(LoginJumpActivity.TYPE_PARAM, type);
//                            bundle.putString(LoginJumpActivity.ID_PARAM, id);
//                            bundle.putInt(LoginJumpActivity.MODE_TYPE, modeType);
//                            bundle.putInt(LoginJumpActivity.SMALL_TYPE, smallType);
//                            if (type == PlayType.LIVE) {
//                                ActivityUtil.jump(ControlMainActivity.this, LoginJumpActivity.class, bundle);
//                            } else {
//                                requestPlaybackType(token, bundle);
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onIOError(String errorStr) {
//            }
//        });
    }

    //像欢拓请求回放
    private void requestPlaybackType(String token, final Bundle bundle) {
        PreDataRequestManager preDataRequestManager = new PreDataRequestManager(this);
        preDataRequestManager.requestPlaybackData(token, new PlaybackDataConverter(), new PreDataForPlaybackInitModel.Callback<PrePlaybackEntity>() {
            @Override
            public void success(PrePlaybackEntity result) {
                bundle.putString(LoginJumpActivity.VIDEO_TYPE, result.getVideoType());
                ActivityUtil.jump(ControlMainActivity.this, LoginJumpActivity.class, bundle);
            }

            @Override
            public void failed(int code, String msg) {
//                tvErrorTip.setText(msg);
            }
        });
    }

    //向服务器Android版本号
    private void getAndroidVersion(Context context) {
        LoadingDialog.getInstance(context).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mIpadress)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        final Observable<ModelObservableInterface.BaseBean> data =
                modelObservableInterface.queryAndroidVersion();
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ModelObservableInterface.BaseBean value) {
                        if (value == null){
                            LoadingDialog.getInstance(context).dismiss();
                            return;
                        }
                        if (value.getErrorCode() == 201){
                            LoadingDialog.getInstance(context).dismiss();
                            return;
                        }
                        //网络请求数据成功
                        Map<String,Object> data =  value.getData();
                        if (data == null){
                            LoadingDialog.getInstance(context).dismiss();
                            return;
                        }
                        if (data.get("version_num") == null || data.get("download_address") == null){
                            LoadingDialog.getInstance(context).dismiss();
                            return;
                        }
                        String version_num = String.valueOf(data.get("version_num"));
                        String download_address = String.valueOf(data.get("download_address"));
                        Integer tf_super = (Integer) data.get("tf_super");
                        //检查是否前往更新
                        UpdateVersionController uvc = new UpdateVersionController(context);
                        uvc.forceCheckUpdateInfo(tf_super,version_num,download_address);//运行该方法
                        LoadingDialog.getInstance(context).dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.getInstance(context).dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //------------------------------------------------------------------------aliyunplayer------------------------------------------------------
    private DownloadView downloadView;
    private DownloadView dialogDownloadView;        //下载弹出框
    private AlivcShowMoreDialog showMoreDialog;   //设置弹出对话框

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");  //时间格式
    private List<String> logStrs = new ArrayList<>();   //日志集合

    private AliyunScreenMode currentScreenMode = AliyunScreenMode.Small;  //当前窗口模式
    private AliyunVodPlayerView mAliyunVodPlayerView = null;  //播放器实例

    private ArrayList<AlivcVideoInfo.DataBean.VideoListBean> alivcVideoInfos;
    private ErrorInfo currentError = ErrorInfo.Normal;
    //判断是否在后台
    private boolean mIsInBackground = false;
    /**
     * get StsToken stats
     */
    private boolean inRequest;

    private Common commenUtils;
    private long oldTime;
    private long downloadOldTime;
    private static String preparedVid;
    private DownloadDataProvider downloadDataProvider;
    private AliyunDownloadManager downloadManager;

    private AliyunScreenMode mCurrentDownloadScreenMode;
    private PlayerHandler playerHandler;

    /**
     * 是否需要展示下载界面,如果是恢复数据,则不用展示下载界面
     */
    private boolean showAddDownloadView;

    /**
     * 是否鉴权过期
     */
    private boolean mIsTimeExpired = false;
    /**
     * 判断是否在下载中
     */
    private boolean mDownloadInPrepare = false;

    private static final int DOWNLOAD_ERROR = 1;
    private static final String DOWNLOAD_ERROR_KEY = "error_key";

    //初始化阿里播放器布局
    private void initAliyunPlayerView() {
//        mAliyunVodPlayerView = (AliyunVodPlayerView) findViewById(com.aliyun.vodplayer.R.id.video_view);
//        mAliyunVodPlayerView = new AliyunVodPlayerView(ControlMainActivity.this);
        mAliyunVodPlayerView.setActivetyContext(this);
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
//        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
//        mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);

        //设置准备事件监听
        mAliyunVodPlayerView.setOnPreparedListener(new MyPrepareListener(this));
        //设置网络连接监听
        mAliyunVodPlayerView.setNetConnectedListener(new MyNetConnectedListener(this));
        //设置播放完成事件监听
        mAliyunVodPlayerView.setOnCompletionListener(new MyCompletionListener(this));
        //设置首帧显示事件监听
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new MyFrameInfoListener(this));
        //设置改变清晰度事件监听
        mAliyunVodPlayerView.setOnChangeQualityListener(new MyChangeQualityListener(this));
        //TODO
        //设置停止播放监听
        mAliyunVodPlayerView.setOnStoppedListener(new MyStoppedListener(this));
        //设置播放器view点击事件监听，目前只对外暴露下载按钮和投屏按钮
        mAliyunVodPlayerView.setmOnPlayerViewClickListener(new MyPlayViewClickListener(this));
        //设置屏幕方向改变监听接口
        mAliyunVodPlayerView.setOrientationChangeListener(new MyOrientationChangeListener(this));
//        mAliyunVodPlayerView.setOnUrlTimeExpiredListener(new MyOnUrlTimeExpiredListener(this));
        //设置源超时监听
        mAliyunVodPlayerView.setOnTimeExpiredErrorListener(new MyOnTimeExpiredErrorListener(this));
        //设置横屏下显示更多
        mAliyunVodPlayerView.setOnShowMoreClickListener(new MyShowMoreClickLisener(this));
        //设置播放状态点击监听
        mAliyunVodPlayerView.setOnPlayStateBtnClickListener(new MyPlayStateBtnClickListener(this));
        //设置seek结束监听
        mAliyunVodPlayerView.setOnSeekCompleteListener(new MySeekCompleteListener(this));
        //设置seek开始监听
        mAliyunVodPlayerView.setOnSeekStartListener(new MySeekStartListener(this));
        //设置屏幕亮度监听
        mAliyunVodPlayerView.setOnScreenBrightness(new MyOnScreenBrightnessListener(this));
        //设置错误事件监听
        mAliyunVodPlayerView.setOnErrorListener(new MyOnErrorListener(this));
        //设置当前屏幕亮度
        mAliyunVodPlayerView.setScreenBrightness(BrightnessDialog.getActivityBrightness(ControlMainActivity.this));
        //设置加载状态监听
        mAliyunVodPlayerView.setSeiDataListener(new MyOnSeiDataListener(this));
        //设置改变全屏显示事件监听
        mAliyunVodPlayerView.setOnChangeScreenModeListener(new MyOnScreenModeListener(this));
        //设置返回监听
        mAliyunVodPlayerView.setOnReturnListener(new MyOnRetuenListener(this));
        //开启底层日志
        mAliyunVodPlayerView.enableNativeLog();
        //设置循环播放
//        mAliyunVodPlayerView.setCirclePlay(true);
        //设置自动播放
        mAliyunVodPlayerView.setAutoPlay(true);
    }

//    /**
//     * 请求sts
//     */
//    private void requestVidSts() {
//        Log.e("scar", "requestVidSts: ");
//        if (inRequest) {
//            return;
//        }
//        inRequest = true;
//        if (TextUtils.isEmpty(PlayParameter.PLAY_PARAM_VID)) {
//            return;
//        }
//        Log.e("scar", "requestVidSts:xx ");
//        VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, new MyStsListener(this));
//    }

    private static class MyPrepareListener implements IPlayer.OnPreparedListener {

        private WeakReference<ControlMainActivity> activityWeakReference;

        public MyPrepareListener(ControlMainActivity controlMainActivity) {
            activityWeakReference = new WeakReference<>(controlMainActivity);
        }

        @Override
        public void onPrepared() {
            ControlMainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }
    }
    private void onPrepared() {
        logStrs.add(format.format(new Date()) + getString(com.aliyun.vodplayer.R.string.log_prepare_success));

//        for (String log : logStrs) {
//            tvLogs.append(log + "\n");
//        }
        FixedToastUtils.show(ControlMainActivity.this.getApplicationContext(), com.aliyun.vodplayer.R.string.toast_prepare_success);
    }

    private static class MyStsListener implements VidStsUtil.OnStsResultListener {

        private WeakReference<ControlMainActivity> weakActivity;

        MyStsListener(ControlMainActivity act) {
            weakActivity = new WeakReference<>(act);
        }

        @Override
        public void onSuccess(String vid, final String akid, final String akSecret, final String token) {
            ControlMainActivity activity = weakActivity.get();
            if (activity != null) {
                activity.onStsSuccess(vid, akid, akSecret, token);
            }
        }

        @Override
        public void onFail() {
            ControlMainActivity activity = weakActivity.get();
            if (activity != null) {
                activity.onStsFail();
            }
        }
    }
    public void onStsSuccess(String mVid, String akid, String akSecret, String token) {
        PlayParameter.PLAY_PARAM_VID = mVid;
        PlayParameter.PLAY_PARAM_AK_ID = akid;
        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret;
        PlayParameter.PLAY_PARAM_SCU_TOKEN = token;

        mIsTimeExpired = false;

        inRequest = false;

//        // 视频列表数据为0时, 加载列表
//        if (alivcVideoInfos != null && alivcVideoInfos.size() == 0) {
//            alivcVideoInfos.clear();
//            loadPlayList();
//        }
    }
    private void onStsFail() {

        FixedToastUtils.show(getApplicationContext(), com.aliyun.vodplayer.R.string.request_vidsts_fail);
        inRequest = false;
        //finish();
    }
    /**
     * 判断是否有网络的监听
     */
    private class MyNetConnectedListener implements AliyunVodPlayerView.NetConnectedListener {
        WeakReference<ControlMainActivity> weakReference;

        public MyNetConnectedListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onReNetConnected(isReconnect);
            }
        }

        @Override
        public void onNetUnConnected() {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onNetUnConnected();
            }
        }
    }

    private void onNetUnConnected() {
        currentError = ErrorInfo.UnConnectInternet;
        if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
            ConcurrentLinkedQueue<AliyunDownloadMediaInfo> allDownloadMediaInfo = new ConcurrentLinkedQueue<>();
            List<AliyunDownloadMediaInfo> mediaInfos = downloadDataProvider.getAllDownloadMediaInfo();
            allDownloadMediaInfo.addAll(mediaInfos);
            downloadManager.stopDownloads(allDownloadMediaInfo);
        }
    }

    private void onReNetConnected(boolean isReconnect) {
        currentError = ErrorInfo.Normal;
        if (isReconnect) {
            if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
                int unCompleteDownload = 0;
                for (AliyunDownloadMediaInfo info : aliyunDownloadMediaInfoList) {
                    if (info.getStatus() == AliyunDownloadMediaInfo.Status.Stop) {
                        unCompleteDownload ++;
                    }
                }

                if (unCompleteDownload > 0) {
                    FixedToastUtils.show(this, "网络恢复, 请手动开启下载任务...");
                }
            }
            // 如果当前播放列表为空, 网络重连后需要重新请求sts和播放列表, 其他情况不需要
            if (alivcVideoInfos != null && alivcVideoInfos.size() == 0) {
                VidStsUtil.getVidSts(mIpadress,PlayParameter.PLAY_PARAM_VID, new MyStsListener(this));
            }
        }
    }
    private static class MyCompletionListener implements IPlayer.OnCompletionListener {

        private WeakReference<ControlMainActivity> activityWeakReference;

        public MyCompletionListener(ControlMainActivity controlMainActivity) {
            activityWeakReference = new WeakReference<ControlMainActivity>(controlMainActivity);
        }

        @Override
        public void onCompletion() {

            ControlMainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompletion();
            }
        }
    }

    private void onCompletion() {
        logStrs.add(format.format(new Date()) + getString(com.aliyun.vodplayer.R.string.log_play_completion));
//        for (String log : logStrs) {
//            tvLogs.append(log + "\n");
//        }
        FixedToastUtils.show(ControlMainActivity.this.getApplicationContext(), com.aliyun.vodplayer.R.string.toast_play_compleion);

//        int time1 = mAliyunVodPlayerView.getVideoPostion();
        int time = mAliyunVodPlayerView.CourseSectionsTimeGet();
//        String videoId = mAliyunVodPlayerView.VideoIdGet();
        String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
        if (!SectionsId.equals("")) { //上传上一个视频的播放进度
            SetCourseVideoDuration(Integer.valueOf(SectionsId),time);
        }
        // 当前视频播放结束, 播放下一个视频
        if ("vidsts".equals(PlayParameter.PLAY_PARAM_TYPE)) {
//            onNext();
            mAliyunVodPlayerView.rePlay();
        }
    }
    private int currentVideoPosition;
    /**
     * 播放下一个视频
     */
    private void onNext() {
        if (currentError == ErrorInfo.UnConnectInternet) {
            // 此处需要判断网络和播放类型
            // 网络资源, 播放完自动波下一个, 无网状态提示ErrorTipsView
            // 本地资源, 播放完需要重播, 显示Replay, 此处不需要处理
            if ("vidsts".equals(PlayParameter.PLAY_PARAM_TYPE)) {
                mAliyunVodPlayerView.showErrorTipView(4014, "-1", "当前网络不可用");
            }
            return;
        }

        if (alivcVideoInfos != null) {
            currentVideoPosition++;
            if (currentVideoPosition > alivcVideoInfos.size() - 1) {
                //列表循环播放，如发现播放完成了从列表的第一个开始重新播放
                currentVideoPosition = 0;
            }

            if (alivcVideoInfos.size() > 0) {
                AlivcVideoInfo.DataBean.VideoListBean video = alivcVideoInfos.get(currentVideoPosition);
                if (video != null) {
                    changePlayVidSource(video);
                }
            }
        }
    }
    /**
     * 播放本地资源
     */
    private void changePlayLocalSource(String url, String title) {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        urlSource.setTitle(title);
        mAliyunVodPlayerView.setLocalSource(urlSource);
    }

    /**
     * 切换播放vid资源
     *
     * @param video 要切换的资源
     */
    private void changePlayVidSource(AlivcVideoInfo.DataBean.VideoListBean video) {
        mDownloadInPrepare = true;
        VidSts vidSts = new VidSts();
        PlayParameter.PLAY_PARAM_VID = video.getVideoId();
        mAliyunVodPlayerView.setAutoPlay(!mIsInBackground);
        //切换资源重置下载flag
        mDownloadInPrepare = false;
        /**
         * 如果是鉴权过期
         */
        if (mIsTimeExpired) {
            onTimExpiredError();
        } else {
            vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
            vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
            vidSts.setAccessKeyId(PlayParameter.PLAY_PARAM_AK_ID);
            vidSts.setAccessKeySecret(PlayParameter.PLAY_PARAM_AK_SECRE);
            vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
            vidSts.setTitle(video.getTitle());
            mAliyunVodPlayerView.setVidSts(vidSts);
        }

    }

    public static class MyOnTimeExpiredErrorListener implements AliyunVodPlayerView.OnTimeExpiredErrorListener {

        WeakReference<ControlMainActivity> weakReference;

        public MyOnTimeExpiredErrorListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onTimeExpiredError() {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onTimExpiredError();
            }
        }
    }

//    private void onUrlTimeExpired(String oldVid, String oldQuality) {
//        //requestVidSts();
//        VidSts vidSts = VidStsUtil.getVidSts(oldVid);
//        PlayParameter.PLAY_PARAM_VID = vidSts.getVid();
//        PlayParameter.PLAY_PARAM_AK_SECRE = vidSts.getAccessKeySecret();
//        PlayParameter.PLAY_PARAM_AK_ID = vidSts.getAccessKeyId();
//        PlayParameter.PLAY_PARAM_SCU_TOKEN = vidSts.getSecurityToken();
//
//        if (mAliyunVodPlayerView != null) {
//            mAliyunVodPlayerView.setVidSts(vidSts);
//        }
//    }

    /**
     * 鉴权过期
     */
    private void onTimExpiredError() {
        VidStsUtil.getVidSts(mIpadress,PlayParameter.PLAY_PARAM_VID, new RetryExpiredSts(this));
    }
    /**
     * 因为鉴权过期,而去重新鉴权
     */
    private static class RetryExpiredSts implements VidStsUtil.OnStsResultListener {

        private WeakReference<ControlMainActivity> weakReference;

        public RetryExpiredSts(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(String vid, String akid, String akSecret, String token) {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onStsRetrySuccess(vid, akid, akSecret, token);
            }
        }

        @Override
        public void onFail() {

        }
    }
    private void onStsRetrySuccess(String mVid, String akid, String akSecret, String token) {
        PlayParameter.PLAY_PARAM_VID = mVid;
        PlayParameter.PLAY_PARAM_AK_ID = akid;
        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret;
        PlayParameter.PLAY_PARAM_SCU_TOKEN = token;

        inRequest = false;
        mIsTimeExpired = false;

        VidSts vidSts = new VidSts();
        vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
        vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
        vidSts.setAccessKeyId(PlayParameter.PLAY_PARAM_AK_ID);
        vidSts.setAccessKeySecret(PlayParameter.PLAY_PARAM_AK_SECRE);
        vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);

        mAliyunVodPlayerView.setVidSts(vidSts);
    }

    private static class MyFrameInfoListener implements IPlayer.OnRenderingStartListener {

        private WeakReference<ControlMainActivity> activityWeakReference;

        public MyFrameInfoListener(ControlMainActivity controlMainActivity) {
            activityWeakReference = new WeakReference<ControlMainActivity>(controlMainActivity);
        }

        @Override
        public void onRenderingStart() {
            ControlMainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onFirstFrameStart();
            }
        }
    }

    private void onFirstFrameStart() {
        if (mAliyunVodPlayerView != null) {
            Map<String, String> debugInfo = mAliyunVodPlayerView.getAllDebugInfo();
            if (debugInfo == null) {
                return;
            }
            long createPts = 0;
            if (debugInfo.get("create_player") != null) {
                String time = debugInfo.get("create_player");
                createPts = (long) Double.parseDouble(time);
                logStrs.add(format.format(new Date(createPts)) + getString(com.aliyun.vodplayer.R.string.log_player_create_success));
            }
            if (debugInfo.get("open-url") != null) {
                String time = debugInfo.get("open-url");
                long openPts = (long) Double.parseDouble(time) + createPts;
                logStrs.add(format.format(new Date(openPts)) + getString(com.aliyun.vodplayer.R.string.log_open_url_success));
            }
            if (debugInfo.get("find-stream") != null) {
                String time = debugInfo.get("find-stream");
                long findPts = (long) Double.parseDouble(time) + createPts;
                logStrs.add(format.format(new Date(findPts)) + getString(com.aliyun.vodplayer.R.string.log_request_stream_success));
            }
            if (debugInfo.get("open-stream") != null) {
                String time = debugInfo.get("open-stream");
                long openPts = (long) Double.parseDouble(time) + createPts;
                logStrs.add(format.format(new Date(openPts)) + getString(com.aliyun.vodplayer.R.string.log_start_open_stream));
            }
            logStrs.add(format.format(new Date()) + getString(com.aliyun.vodplayer.R.string.log_first_frame_played));
//            for (String log : logStrs) {
//                tvLogs.append(log + "\n");
//            }
        }
    }

    private static class MyChangeQualityListener implements OnChangeQualityListener {

        private WeakReference<ControlMainActivity> activityWeakReference;

        public MyChangeQualityListener(ControlMainActivity controlMainActivity) {
            activityWeakReference = new WeakReference<ControlMainActivity>(controlMainActivity);
        }

        @Override
        public void onChangeQualitySuccess(String finalQuality) {

            ControlMainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualitySuccess(finalQuality);
            }
        }

        @Override
        public void onChangeQualityFail(int code, String msg) {
            ControlMainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualityFail(code, msg);
            }
        }
    }

    private void onChangeQualitySuccess(String finalQuality) {
        logStrs.add(format.format(new Date()) + getString(com.aliyun.vodplayer.R.string.log_change_quality_success));
        FixedToastUtils.show(ControlMainActivity.this.getApplicationContext(),
                getString(com.aliyun.vodplayer.R.string.log_change_quality_success));
    }

    void onChangeQualityFail(int code, String msg) {
        logStrs.add(format.format(new Date()) + getString(com.aliyun.vodplayer.R.string.log_change_quality_fail) + " : " + msg);
        FixedToastUtils.show(ControlMainActivity.this.getApplicationContext(),
                getString(com.aliyun.vodplayer.R.string.log_change_quality_fail));
    }
    private static class MyStoppedListener implements OnStoppedListener {

        private WeakReference<ControlMainActivity> activityWeakReference;

        public MyStoppedListener(ControlMainActivity controlMainActivity) {
            activityWeakReference = new WeakReference<ControlMainActivity>(controlMainActivity);
        }

        @Override
        public void onStop() {
            ControlMainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }
    private void onStopped() {
        FixedToastUtils.show(ControlMainActivity.this.getApplicationContext(), com.aliyun.vodplayer.R.string.log_play_stopped);
    }
    private class MyPlayViewClickListener implements AliyunVodPlayerView.OnPlayerViewClickListener {

        private WeakReference<ControlMainActivity> weakReference;

        public MyPlayViewClickListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onClick(AliyunScreenMode screenMode, AliyunVodPlayerView.PlayViewType viewType) {
            long currentClickTime = System.currentTimeMillis();
            // 防止快速点击
            if (currentClickTime - oldTime <= 1000) {
                return;
            }
            oldTime = currentClickTime;
            // 如果当前的Type是Download, 就显示Download对话框
            if (viewType == AliyunVodPlayerView.PlayViewType.Download) {
                mCurrentDownloadScreenMode = screenMode;
                ControlMainActivity controlMainActivity = weakReference.get();
                if (controlMainActivity != null) {
                    controlMainActivity.showAddDownloadView = true;
                }

                if (mAliyunVodPlayerView != null) {
                    MediaInfo currentMediaInfo = mAliyunVodPlayerView.getCurrentMediaInfo();
                    if (currentMediaInfo != null && currentMediaInfo.getVideoId().equals(PlayParameter.PLAY_PARAM_VID)) {
                        VidSts vidSts = new VidSts();
                        vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
                        vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
                        vidSts.setAccessKeyId(PlayParameter.PLAY_PARAM_AK_ID);
                        vidSts.setAccessKeySecret(PlayParameter.PLAY_PARAM_AK_SECRE);
                        vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
                        if (!mDownloadInPrepare) {
                            mDownloadInPrepare = true;
                            downloadManager.prepareDownload(vidSts);
                        }
                    }
                }
            }
        }
    }
    private static class MyOrientationChangeListener implements AliyunVodPlayerView.OnOrientationChangeListener {

        private final WeakReference<ControlMainActivity> weakReference;

        public MyOrientationChangeListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void orientationChange(boolean from, AliyunScreenMode currentMode) {
            ControlMainActivity activity = weakReference.get();

            if (activity != null) {
                activity.hideDownloadDialog(from, currentMode);
                activity.hideShowMoreDialog(from, currentMode);

            }
        }
    }

    //隐藏显示更多对话框
    private void hideShowMoreDialog(boolean from, AliyunScreenMode currentMode) {
        if (showMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                showMoreDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    //隐藏下载对话框
    private void hideDownloadDialog(boolean from, AliyunScreenMode currentMode) {

        if (downloadDialog != null) {
            if (currentScreenMode != currentMode) {
                downloadDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }
    private static class MyShowMoreClickLisener implements ControlView.OnShowMoreClickListener {
        WeakReference<ControlMainActivity> weakReference;

        MyShowMoreClickLisener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void showMore() {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                long currentClickTime = System.currentTimeMillis();
                // 防止快速点击
                if (currentClickTime - activity.oldTime <= 1000) {
                    return;
                }
                activity.oldTime = currentClickTime;
                activity.showMore(activity);
//                activity.requestVidSts();
            }

        }
    }

    //显示更多
    private void showMore(final ControlMainActivity activity) {
        showMoreDialog = new AlivcShowMoreDialog(activity);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume((int) mAliyunVodPlayerView.getCurrentVolume());

        ShowMoreView showMoreView = new ShowMoreView(activity, moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        showMoreDialog.show();
        showMoreView.setOnDownloadButtonClickListener(() -> {
            long currentClickTime = System.currentTimeMillis();
            // 防止快速点击
            if (currentClickTime - downloadOldTime <= 1000) {
                return;
            }
            downloadOldTime = currentClickTime;
            // 点击下载
            showMoreDialog.dismiss();
            if ("url".equals(PlayParameter.PLAY_PARAM_TYPE) || "localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
                FixedToastUtils.show(activity, getResources().getString(com.aliyun.vodplayer.R.string.alivc_video_not_support_download));
                return;
            }
            mCurrentDownloadScreenMode = AliyunScreenMode.Full;
            showAddDownloadView = true;
            if (mAliyunVodPlayerView != null) {
                MediaInfo currentMediaInfo = mAliyunVodPlayerView.getCurrentMediaInfo();
                if (currentMediaInfo != null && currentMediaInfo.getVideoId().equals(PlayParameter.PLAY_PARAM_VID)) {
                    VidSts vidSts = new VidSts();
                    vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
                    vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
                    vidSts.setAccessKeyId(PlayParameter.PLAY_PARAM_AK_ID);
                    vidSts.setAccessKeySecret(PlayParameter.PLAY_PARAM_AK_SECRE);
                    vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
                    downloadManager.prepareDownload(vidSts);
                }
            }
        });

        showMoreView.setOnSpeedCheckedChangedListener((group, checkedId) -> {
            // 点击速度切换
            if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_normal) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.One);
            } else if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_onequartern) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.OneQuartern);
            } else if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_onehalf) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.OneHalf);
            } else if (checkedId == com.aliyun.vodplayer.R.id.rb_speed_twice) {
                mAliyunVodPlayerView.changeSpeed(SpeedValue.Twice);
            }

        });

        /**
         * 初始化亮度
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setBrightness(mAliyunVodPlayerView.getScreenBrightness());
        }
        // 亮度seek
        showMoreView.setOnLightSeekChangeListener(new ShowMoreView.OnLightSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                setWindowBrightness(progress);
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setScreenBrightness(progress);
                }
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

        /**
         * 初始化音量
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setVoiceVolume(mAliyunVodPlayerView.getCurrentVolume());
        }
        showMoreView.setOnVoiceSeekChangeListener(new ShowMoreView.OnVoiceSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentVolume(progress / 100.00f);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });
    }
    /**
     * 设置屏幕亮度
     */
    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }
    private static class MyPlayStateBtnClickListener implements AliyunVodPlayerView.OnPlayStateBtnClickListener {
        WeakReference<ControlMainActivity> weakReference;

        MyPlayStateBtnClickListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onPlayBtnClick(int playerState) {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onPlayStateSwitch(playerState);
            }
        }
    }

    /**
     * 播放状态切换
     */
    private void onPlayStateSwitch(int playerState) {
        if (playerState == IPlayer.started) {
//            tvLogs.append(format.format(new Date()) + " 暂停 \n");
        } else if (playerState == IPlayer.paused) {
//            tvLogs.append(format.format(new Date()) + " 开始 \n");
        }
    }
    private static class MySeekCompleteListener implements IPlayer.OnSeekCompleteListener {
        WeakReference<ControlMainActivity> weakReference;

        MySeekCompleteListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSeekComplete() {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onSeekComplete();
            }
        }
    }

    private void onSeekComplete() {
//        tvLogs.append(format.format(new Date()) + getString(R.string.log_seek_completed) + "\n");
    }

    private static class MySeekStartListener implements AliyunVodPlayerView.OnSeekStartListener {
        WeakReference<ControlMainActivity> weakReference;

        MySeekStartListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSeekStart(int position) {
            ControlMainActivity activity = weakReference.get();
            if (activity != null) {
                activity.onSeekStart(position);
            }
        }
    }
    private void onSeekStart(int position) {
//        tvLogs.append(format.format(new Date()) + getString(R.string.log_seek_start) + "\n");
    }

    //课程问答-回复问答
    private void SetCourseVideoDuration(Integer section_id,Integer time) {
        if (mStuId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mStuId));
        paramsMap.put("section_id", Integer.valueOf(section_id));
        paramsMap.put("max_class_time", Integer.valueOf(time / 1000));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.SetCourseVideoDuration(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response == null){
                    Toast.makeText(mThis,"记录录播课程学习进度失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    Toast.makeText(mThis,"记录录播课程学习进度失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                if (baseBean.getErrorCode() != 200){
                    Toast.makeText(mThis,"记录录播课程学习进度失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                LoadingDialog.getInstance(mThis).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mThis,"记录录播课程学习进度失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mThis).dismiss();
            }
        });
    }
    private static class MyOnScreenBrightnessListener implements AliyunVodPlayerView.OnScreenBrightnessListener {

        private WeakReference<ControlMainActivity> weakReference;

        public MyOnScreenBrightnessListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onScreenBrightness(int brightness) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                controlMainActivity.setWindowBrightness(brightness);
                if (controlMainActivity.mAliyunVodPlayerView != null) {
                    controlMainActivity.mAliyunVodPlayerView.setScreenBrightness(brightness);
                }
            }
        }
    }
    /**
     * 播放器出错监听
     */
    private static class MyOnErrorListener implements IPlayer.OnErrorListener {

        private WeakReference<ControlMainActivity> weakReference;

        public MyOnErrorListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(com.aliyun.player.bean.ErrorInfo errorInfo) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                controlMainActivity.onError(errorInfo);
            }
        }
    }

    private static class MyOnSeiDataListener implements IPlayer.OnSeiDataListener{
        private WeakReference<ControlMainActivity> weakReference;

        public MyOnSeiDataListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSeiData(int i, byte[] bytes) {
            ControlMainActivity controlMainActivity = weakReference.get();
            String seiMessage = new String(bytes);
            if (controlMainActivity != null) {
                String log = new SimpleDateFormat("HH:mm:ss.SS").format(new Date())+"SEI:type:"+i+",content:"+seiMessage+"\n";
//                controlMainActivity.tvLogs.append(log);
            }
            Log.e("SEI:", "type:"+i+",content:"+seiMessage);
        }
    }
    private void onError(com.aliyun.player.bean.ErrorInfo errorInfo) {
        //鉴权过期
        if (errorInfo.getCode().getValue() == ErrorCode.ERROR_SERVER_POP_UNKNOWN.getValue()) {
            mIsTimeExpired = true;
        }
    }

    //设置阿里播放器
    public void setmAliyunVodPlayerView(AliyunVodPlayerView aliyunVodPlayerView){
        if (mAliyunVodPlayerView != null && mAliyunVodPlayerView != aliyunVodPlayerView){
            int time1 = mAliyunVodPlayerView.getVideoPostion();
//            String videoId = mAliyunVodPlayerView.VideoIdGet();
            String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
            if (!SectionsId.equals("")) { //上传上一个视频的播放进度
                SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
            }
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
            mAliyunVodPlayerView = aliyunVodPlayerView;
            initAliyunPlayerView();
        } else if (mAliyunVodPlayerView == null){
            mAliyunVodPlayerView = aliyunVodPlayerView;
            PlayParameter.PLAY_PARAM_TYPE = "vidsts";
            initAliyunPlayerView();
        }
        //设置view的布局，宽高之类
        RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
        aliVcVideoViewLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_244);
        aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mAliyunVodPlayerView.setLayoutParams(aliVcVideoViewLayoutParams);
        if (downloadView == null) {
            downloadView = new DownloadView(this);
        }
        //设置播放源
        setPlaySource();
    }
    private static class MyOnScreenModeListener implements OnChangeScreenModeListener {
        private WeakReference<ControlMainActivity> weakReference;

        public MyOnScreenModeListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onChangeScreenModeFull() {
            ControlMainActivity controlMainActivity = weakReference.get();
            if(controlMainActivity != null){
                controlMainActivity.onChangeScreenModeFull();
            }
        }

        @Override
        public void onChangeScreenModeSmall() {
            ControlMainActivity controlMainActivity = weakReference.get();
            if(controlMainActivity != null){
                controlMainActivity.onChangeScreenModeSmall();
            }
        }
    }

    private void onChangeScreenModeFull(){
        if (mAliyunVodPlayerView != null){
        }
    }

    private void onChangeScreenModeSmall(){

    }

    private static class MyOnRetuenListener implements OnReturnListener {
        private WeakReference<ControlMainActivity> weakReference;

        public MyOnRetuenListener(ControlMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReturn() {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null){
                controlMainActivity.onReturn();
            }
        }
    }

    //阿里播放器返回
    private void onReturn(){
        //录播返回
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
            int time1 = mAliyunVodPlayerView.getVideoPostion();
//                        String videoId = mAliyunVodPlayerView.VideoIdGet();
            String SectionsId = mAliyunVodPlayerView.SectionsIdGet();
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
            if (!SectionsId.equals("")) {
                SetCourseVideoDuration(Integer.valueOf(SectionsId),time1);
            }
        }
        String beforePageS[] = mBeforePage.split("/");
        if (beforePageS.length <= 0){
            return;
        }
        if (beforePageS[beforePageS.length - 1].equals("课程")){ //说明上个界面是课程界面
            Page_Course();
        } else if (beforePageS[beforePageS.length - 1].equals("首页")){ //说明上个界面是首页界面
            Page_HomePage();
        } else if (beforePageS[beforePageS.length - 1].equals("我的课程")){//说明上个界面是我的课程界面
            mPage = "我的课程";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyClassShow();
            }
        } else if (beforePageS[beforePageS.length - 1].equals("我的收藏")){ //说明上个界面是我的收藏界面
            mPage = "我的收藏";
            mBeforePage = "我的";
            if(mModelMy != null){
                ((ModelMy) mModelMy).MyCollectShow();
            }
        } else if (beforePageS[beforePageS.length - 1].equals("我的缓存") && mPage.equals("我的缓存播放")){ //说明上个界面是我的界面
            mPage = "我的缓存";
            mBeforePage = "我的";
            if(mModelMy != null){
                if (downloadView == null){
                    downloadView = new DownloadView(this);
                }
                ((ModelMy) mModelMy).MyCacheShow(downloadView);
            }
        }
    }

    //设置播放源
    private void setPlaySource() {
        if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(PlayParameter.PLAY_PARAM_URL);
            //默认是5000
            int maxDelayTime = 5000;
            if (PlayParameter.PLAY_PARAM_URL.startsWith("artp")) {
                //如果url的开头是artp，将直播延迟设置成100，
                maxDelayTime = 100;
            }
            if (mAliyunVodPlayerView != null) {
                PlayerConfig playerConfig = mAliyunVodPlayerView.getPlayerConfig();
                if (playerConfig == null){
//                    mAliyunVodPlayerView.initAliVcPlayer();
                    return;
                }
                playerConfig.mMaxDelayTime = maxDelayTime;
                //开启SEI事件通知
                playerConfig.mEnableSEI = true;
                mAliyunVodPlayerView.setPlayerConfig(playerConfig);
                mAliyunVodPlayerView.setLocalSource(urlSource);
            }

        } else if ("vidsts".equals(PlayParameter.PLAY_PARAM_TYPE)) {
            if (!inRequest) {
                VidSts vidSts = new VidSts();
                vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
                vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
                vidSts.setAccessKeyId(PlayParameter.PLAY_PARAM_AK_ID);
                vidSts.setAccessKeySecret(PlayParameter.PLAY_PARAM_AK_SECRE);
                vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setVidSts(vidSts);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsInBackground = false;
        updatePlayerViewMode();
        updateDownloadView();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(true);
            mAliyunVodPlayerView.onResume();
        }
    }

    //更新下载界面
    private void updateDownloadView() {
        if (downloadView != null && downloadManager != null) {
            ArrayList<AlivcDownloadMediaInfo> downloadMediaInfo = downloadView.getDownloadMediaInfo();
            for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : downloadMediaInfo) {
                AliyunDownloadMediaInfo aliyunDownloadMediaInfo = alivcDownloadMediaInfo.getAliyunDownloadMediaInfo();
                String savePath = aliyunDownloadMediaInfo.getSavePath();
                if (!TextUtils.isEmpty(savePath)) {
                    File file = new File(savePath);
                    if (!file.exists() && aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete) {
                        downloadView.deleteDownloadInfo(aliyunDownloadMediaInfo);
                        downloadManager.deleteFile(aliyunDownloadMediaInfo);
                    }
                }
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsInBackground = true;
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(false);
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerViewMode();
    }

    //更新播放模式（横屏或者竖屏）
    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_244);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
    }

    protected boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

//        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;
    }

    //拷贝encryptedApp.dat文件到所需位置
    private void copyAssets() {
        commenUtils = Common.getInstance(getApplicationContext()).copyAssetsToSD("encrypt", PublicCommonUtil.encryptedAppPath);
        commenUtils.setFileOperateCallback(

                new Common.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PublicCommonUtil.dowmloadVideoSavePath);
                        if (!file.exists()) {
                            file.mkdir();
                        }

                        // 获取AliyunDownloadManager对象
                        downloadManager = AliyunDownloadManager.getInstance(getApplicationContext(),mThis);
                        downloadManager.setEncryptFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PublicCommonUtil.encryptedAppPath + "/encryptedApp.dat");
                        downloadManager.setDownloadDir(file.getAbsolutePath());
                        //设置同时下载个数
                        downloadManager.setMaxNum(4);

                        downloadDataProvider = DownloadDataProvider.getSingleton(getApplicationContext(),mThis);
                        // 更新sts回调
                        downloadManager.setRefreshStsCallback(new MyRefreshStsCallback());

                        // 视频下载的回调
                        downloadManager.setDownloadInfoListener(new MyDownloadInfoListener(mThis));
                        if (downloadView == null){
                            downloadView = new DownloadView(mThis);
                        }
                        downloadViewSetting(downloadView);
                    }

                    @Override
                    public void onFailed(String error) {
                    }
                });
    }
//    /**
//     * init下载(离线视频)tab
//     */
//    private void initDownloadView() {
//        tvTabDownloadVideo = (TextView) findViewById(com.aliyun.vodplayer.R.id.tv_tab_download_video);
//        ivDownloadVideo = (ImageView) findViewById(com.aliyun.vodplayer.R.id.iv_download_video);
//        rlDownloadManagerContent = (RelativeLayout) findViewById(com.aliyun.vodplayer.R.id.rl_download_manager_content);
//        downloadView = (DownloadView) findViewById(com.aliyun.vodplayer.R.id.download_view);
//        //离线下载Tab默认不选择
//        ivDownloadVideo.setActivated(false);
//        tvTabDownloadVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentTab = TAB_DOWNLOAD_LIST;
//                // TODO: 2018/4/10 show download content
//                ivDownloadVideo.setActivated(true);
//                ivLogs.setActivated(false);
//                ivVideoList.setActivated(false);
//                rlLogsContent.setVisibility(View.GONE);
//                llVideoList.setVisibility(View.GONE);
//                rlDownloadManagerContent.setVisibility(View.VISIBLE);
//                //Drawable drawable = getResources().getDrawable(R.drawable.alivc_new_download);
//                //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//
//                updateDownloadTaskTip();
//            }
//        });
//    }
    /**
     * downloadView的配置 里面配置了需要下载的视频的信息, 事件监听等 抽取该方法的主要目的是, 横屏下download dialog的离线视频列表中也用到了downloadView, 而两者显示内容和数据是同步的,
     * 所以在此进行抽取 AliyunPlayerSkinActivity.class#showAddDownloadView(DownloadVie view)中使用
     */
    private void downloadViewSetting(final DownloadView downloadView) {
        downloadDataProvider.restoreMediaInfo(new LoadDbDatasListener() {
            @Override
            public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList) {
                if (downloadView != null) {
                    downloadView.addAllDownloadMediaInfo(dataList);
                }
            }
        });

        downloadView.setOnDownloadViewListener(new DownloadView.OnDownloadViewListener() {
            @Override
            public void onStop(AliyunDownloadMediaInfo downloadMediaInfo) {
                downloadManager.stopDownload(downloadMediaInfo);
            }

            @Override
            public void onStart(AliyunDownloadMediaInfo downloadMediaInfo) {
                downloadManager.startDownload(downloadMediaInfo);
            }

            @Override
            public void onDeleteDownloadInfo(final ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos) {
                // 视频删除的dialog
                final AlivcDialog alivcDialog = new AlivcDialog(ControlMainActivity.this);
                alivcDialog.setDialogIcon(com.aliyun.vodplayer.R.drawable.icon_delete_tips);
                alivcDialog.setMessage(getResources().getString(com.aliyun.vodplayer.R.string.alivc_delete_confirm));
                alivcDialog.setOnConfirmclickListener(getResources().getString(com.aliyun.vodplayer.R.string.alivc_dialog_sure),
                        new AlivcDialog.onConfirmClickListener() {
                            @Override
                            public void onConfirm() {
                                alivcDialog.dismiss();
                                if (alivcDownloadMediaInfos != null && alivcDownloadMediaInfos.size() > 0) {
                                    downloadView.deleteDownloadInfo();
                                    if (downloadView != null) {
                                        for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : alivcDownloadMediaInfos) {
                                            if (alivcDownloadMediaInfo.isCheckedState()) {
                                                downloadView.deleteDownloadInfo(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo());
                                            }
                                        }

                                    }
                                    if (dialogDownloadView != null) {
                                        dialogDownloadView.deleteDownloadInfo();
                                    }
                                    if (downloadManager != null) {
                                        for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : alivcDownloadMediaInfos) {
                                            downloadManager.deleteFile(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo());
                                        }

                                    }
                                    downloadDataProvider.deleteAllDownloadInfo(alivcDownloadMediaInfos);
                                } else {
                                    FixedToastUtils.show(ControlMainActivity.this, "没有删除的视频选项...");
                                }
                            }
                        });
                alivcDialog.setOnCancelOnclickListener(getResources().getString(com.aliyun.vodplayer.R.string.alivc_dialog_cancle),
                        new AlivcDialog.onCancelOnclickListener() {
                            @Override
                            public void onCancel() {
                                alivcDialog.dismiss();
                            }
                        });
                alivcDialog.show();
            }
        });

        downloadView.setOnDownloadedItemClickListener(new DownloadView.OnDownloadItemClickListener() {
            @Override
            public void onDownloadedItemClick(final int positin) {
                ArrayList<AlivcDownloadMediaInfo> allDownloadMediaInfo = downloadView.getAllDownloadMediaInfo();
                if (positin < 0) {
                    FixedToastUtils.show(ControlMainActivity.this, "视频资源不存在");
                    return;
                }
                if (mModelMy != null){
                    boolean misPlay = ((ModelMy)mModelMy).MyCacheShow_Play();
                    if (!misPlay){
                        FixedToastUtils.show(ControlMainActivity.this, "视频播放器不存在");
                        return;
                    }
                    mBeforePage = mBeforePage + "/" + mPage;
                    mPage = "我的缓存播放";

                }
                // 如果点击列表中的视频, 需要将类型改为vid
                AliyunDownloadMediaInfo aliyunDownloadMediaInfo = allDownloadMediaInfo.get(positin).getAliyunDownloadMediaInfo();
                PlayParameter.PLAY_PARAM_TYPE = "localSource";
                if (aliyunDownloadMediaInfo != null) { //点击播放本地缓存
                    PlayParameter.PLAY_PARAM_URL = aliyunDownloadMediaInfo.getSavePath();
                    mAliyunVodPlayerView.updateScreenShow();
                    changePlayLocalSource(PlayParameter.PLAY_PARAM_URL, aliyunDownloadMediaInfo.getTitle());
                }
            }

            @Override
            public void onDownloadingItemClick(ArrayList<AlivcDownloadMediaInfo> infos, int position) {
                AlivcDownloadMediaInfo alivcInfo = infos.get(position);
                AliyunDownloadMediaInfo aliyunDownloadInfo = alivcInfo.getAliyunDownloadMediaInfo();
                AliyunDownloadMediaInfo.Status status = aliyunDownloadInfo.getStatus();
                if (status == AliyunDownloadMediaInfo.Status.Error || status == AliyunDownloadMediaInfo.Status.Wait) {
                    //downloadManager.removeDownloadMedia(aliyunDownloadInfo);
                    downloadManager.startDownload(aliyunDownloadInfo);
                }
            }
        });
    }

    private static class MyRefreshStsCallback implements RefreshStsCallback {

        @Override
        public VidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            VcPlayerLog.d("refreshSts ", "refreshSts , vid = " + vid);
            //NOTE: 注意：这个不能启动线程去请求。因为这个方法已经在线程中调用了。
//            VidSts vidSts = VidStsUtil.getVidSts(mIpadress,vid,new VidStsUtil.OnStsResultListener() {
//                @Override
//                public void onSuccess(String vid, String akid, String akSecret, String token) {
//                    if (downloadManager != null) {
//                        VidSts vidSts = new VidSts();
//                        vidSts.setVid(vid);
//                        vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
//                        vidSts.setAccessKeyId(akid);
//                        vidSts.setAccessKeySecret(akSecret);
//                        vidSts.setSecurityToken(token);
//                        downloadMediaInfo.setVidSts(vidSts);
//                        PlayParameter.PLAY_PARAM_AK_ID = akid;
//                        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret;
//                        PlayParameter.PLAY_PARAM_SCU_TOKEN = token;
//                        downloadManager.prepareDownloadByQuality(downloadMediaInfo, AliyunDownloadManager.INTENT_STATE_START);
//                    }
//                }
//
//                @Override
//                public void onFail() {
//
//                }
//            });
//            if (vidSts == null) {
                return null;
//            } else {
//                vidSts.setVid(vid);
//                vidSts.setQuality(quality, true);
//                vidSts.setTitle(title);
//                return vidSts;
//            }
        }
    }

    /**
     * 下载监听
     */
    private static class MyDownloadInfoListener implements AliyunDownloadInfoListener {

        private WeakReference<ControlMainActivity> weakReference;

        public MyDownloadInfoListener(ControlMainActivity controlMainActivity) {
            weakReference = new WeakReference<>(controlMainActivity);
        }

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
            preparedVid = infos.get(0).getVid();
            Collections.sort(infos, new Comparator<AliyunDownloadMediaInfo>() {
                @Override
                public int compare(AliyunDownloadMediaInfo mediaInfo1, AliyunDownloadMediaInfo mediaInfo2) {
                    if (mediaInfo1.getSize() > mediaInfo2.getSize()) {
                        return 1;
                    }
                    if (mediaInfo1.getSize() < mediaInfo2.getSize()) {
                        return -1;
                    }

                    if (mediaInfo1.getSize() == mediaInfo2.getSize()) {
                        return 0;
                    }
                    return 0;
                }
            });
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                controlMainActivity.mDownloadInPrepare = false;
                controlMainActivity.onDownloadPrepared(infos, controlMainActivity.showAddDownloadView);
            }
        }

        @Override
        public void onAdd(AliyunDownloadMediaInfo info) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                if (controlMainActivity.downloadDataProvider != null) {
                    controlMainActivity.downloadDataProvider.addDownloadMediaInfo(info);
                }
                Toast.makeText(controlMainActivity,"添加下载",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo info) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                if (controlMainActivity.dialogDownloadView != null) {
                    controlMainActivity.dialogDownloadView.updateInfo(info);
                }
                if (controlMainActivity.downloadView != null) {
                    controlMainActivity.downloadView.updateInfo(info);
                }
//                Toast.makeText(controlMainActivity,"开始下载",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo info, int percent) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                if (controlMainActivity.dialogDownloadView != null) {
                    controlMainActivity.dialogDownloadView.updateInfo(info);
                }
                if (controlMainActivity.downloadView != null) {
                    controlMainActivity.downloadView.updateInfo(info);
                }
            }
            Log.e("download","progress:" + percent);
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo info) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                if (controlMainActivity.dialogDownloadView != null) {
                    controlMainActivity.dialogDownloadView.updateInfo(info);
                }
                if (controlMainActivity.downloadView != null) {
                    controlMainActivity.downloadView.updateInfo(info);
                }
            }
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo info) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                synchronized (controlMainActivity) {
                    if (controlMainActivity.downloadView != null) {
                        controlMainActivity.downloadView.updateInfoByComplete(info);
                    }
                    if (controlMainActivity.dialogDownloadView != null) {
                        controlMainActivity.dialogDownloadView.updateInfoByComplete(info);
                    }
                    if (controlMainActivity.downloadDataProvider != null) {
                        controlMainActivity.downloadDataProvider.addDownloadMediaInfo(info);
                    }
                    Toast.makeText(controlMainActivity,"下载完成",Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onError(AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId) {
            ControlMainActivity controlMainActivity = weakReference.get();
            if (controlMainActivity != null) {
                controlMainActivity.mDownloadInPrepare = false;
                if (controlMainActivity.downloadView != null) {
                    controlMainActivity.downloadView.updateInfoByError(info);
                }
                if (controlMainActivity.dialogDownloadView != null) {
                    controlMainActivity.dialogDownloadView.updateInfoByError(info);
                }

                //鉴权过期
                if (code.getValue() == ErrorCode.ERROR_SERVER_POP_UNKNOWN.getValue()) {
                    controlMainActivity.refreshDownloadVidSts(info);
                }
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString(DOWNLOAD_ERROR_KEY, msg);
                message.setData(bundle);
                message.what = DOWNLOAD_ERROR;
                controlMainActivity.playerHandler = new PlayerHandler(controlMainActivity);
                controlMainActivity.playerHandler.sendMessage(message);
            }
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo info) {
//            mPlayerDownloadAdapter.updateData(info);
        }

        @Override
        public void onDelete(AliyunDownloadMediaInfo info) {
//            mPlayerDownloadAdapter.deleteData(info);
        }

        @Override
        public void onDeleteAll() {
//            mPlayerDownloadAdapter.clearAll();
        }

        @Override
        public void onFileProgress(AliyunDownloadMediaInfo info) {

        }
    }

    private static class PlayerHandler extends Handler {
        //持有弱引用ControlMainActivity,GC回收时会被回收掉.
        private final WeakReference<ControlMainActivity> mActivty;

        public PlayerHandler(ControlMainActivity activity) {
            mActivty = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ControlMainActivity activity = mActivty.get();
            super.handleMessage(msg);
            if (activity != null) {
                switch (msg.what) {
                    case DOWNLOAD_ERROR:
                        ToastUtils.show(activity,msg.getData().getString(DOWNLOAD_ERROR_KEY));
                        Log.d("donwload", msg.getData().getString(DOWNLOAD_ERROR_KEY));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfoList = new ArrayList<>();
    private List<AliyunDownloadMediaInfo> currentPreparedMediaInfo = null;

    private void onDownloadPrepared(List<AliyunDownloadMediaInfo> infos, boolean showAddDownloadView) {
        currentPreparedMediaInfo = new ArrayList<>();
        currentPreparedMediaInfo.addAll(infos);
        if (showAddDownloadView) {
            showAddDownloadView(mCurrentDownloadScreenMode);
        }

    }

    private Dialog downloadDialog = null;

    private AliyunDownloadMediaInfo aliyunDownloadMediaInfo;

    /**
     * 显示下载选择项 download 对话框
     *
     * @param screenMode
     */
    /**
     * 显示下载选择项 download 对话框
     *
     * @param screenMode
     */
    private void showAddDownloadView(AliyunScreenMode screenMode) {
        //这个时候视频的状态已经是delete了
        if (currentPreparedMediaInfo != null && currentPreparedMediaInfo.get(0).getVid().equals(preparedVid)) {
            downloadDialog = new DownloadChoiceDialog(this, screenMode);
            final AddDownloadView contentView = new AddDownloadView(this, screenMode);
            contentView.onPrepared(currentPreparedMediaInfo);
            contentView.setOnViewClickListener(viewClickListener);
            final View inflate = LayoutInflater.from(mThis).inflate(
                    com.aliyun.vodplayer.R.layout.alivc_dialog_download_video, null);
            dialogDownloadView = inflate.findViewById(com.aliyun.vodplayer.R.id.download_view);
            downloadDialog.setContentView(contentView);
            downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (dialogDownloadView != null) {
                        dialogDownloadView.setOnDownloadViewListener(null);
                        dialogDownloadView.setOnDownloadedItemClickListener(null);
                    }
                }
            });
            if (!downloadDialog.isShowing()) {
                downloadDialog.show();
            }
            downloadDialog.setCanceledOnTouchOutside(true);

            if (screenMode == AliyunScreenMode.Full) {
                contentView.setOnShowVideoListLisener(new AddDownloadView.OnShowNativeVideoBtnClickListener() {
                    @Override
                    public void onShowVideo() {
                        if (downloadDataProvider != null) {
                            downloadDataProvider.restoreMediaInfo(new LoadDbDatasListener() {
                                @Override
                                public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList) {
                                    if (dialogDownloadView != null) {
                                        dialogDownloadView.addAllDownloadMediaInfo(dataList);
                                    }
                                }
                            });
                        }
                        downloadDialog.setContentView(inflate);
                    }
                });

                dialogDownloadView.setOnDownloadViewListener(new DownloadView.OnDownloadViewListener() {
                    @Override
                    public void onStop(AliyunDownloadMediaInfo downloadMediaInfo) {
                        downloadManager.stopDownload(downloadMediaInfo);
                    }

                    @Override
                    public void onStart(AliyunDownloadMediaInfo downloadMediaInfo) {
                        downloadManager.startDownload(downloadMediaInfo);
                    }

                    @Override
                    public void onDeleteDownloadInfo(final ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos) {
                        // 视频删除的dialog
                        final AlivcDialog alivcDialog = new AlivcDialog(ControlMainActivity.this);
                        alivcDialog.setDialogIcon(com.aliyun.vodplayer.R.drawable.icon_delete_tips);
                        alivcDialog.setMessage(getResources().getString(com.aliyun.vodplayer.R.string.alivc_delete_confirm));
                        alivcDialog.setOnConfirmclickListener(getResources().getString(com.aliyun.vodplayer.R.string.alivc_dialog_sure),
                                new AlivcDialog.onConfirmClickListener() {
                                    @Override
                                    public void onConfirm() {
                                        alivcDialog.dismiss();
                                        if (alivcDownloadMediaInfos != null && alivcDownloadMediaInfos.size() > 0) {
                                            dialogDownloadView.deleteDownloadInfo();
                                            if (mThis.downloadView != null) {
                                                for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : alivcDownloadMediaInfos) {
                                                    if (alivcDownloadMediaInfo.isCheckedState()) {
                                                        downloadView.deleteDownloadInfo(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo());
                                                    }
                                                }
                                            }
                                            if (downloadManager != null) {
                                                for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : alivcDownloadMediaInfos) {
                                                    downloadManager.deleteFile(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo());
                                                }

                                            }
                                            downloadDataProvider.deleteAllDownloadInfo(alivcDownloadMediaInfos);
                                        } else {
                                            FixedToastUtils.show(ControlMainActivity.this, "没有删除的视频选项...");
                                        }
                                    }
                                });
                        alivcDialog.setOnCancelOnclickListener(getResources().getString(com.aliyun.vodplayer.R.string.alivc_dialog_cancle),
                                new AlivcDialog.onCancelOnclickListener() {
                                    @Override
                                    public void onCancel() {
                                        alivcDialog.dismiss();
                                    }
                                });
                        alivcDialog.show();
                    }
                });

                dialogDownloadView.setOnDownloadedItemClickListener(new DownloadView.OnDownloadItemClickListener() {
                    @Override
                    public void onDownloadedItemClick(final int positin) {
                        ArrayList<AlivcDownloadMediaInfo> allDownloadMediaInfo = dialogDownloadView.getAllDownloadMediaInfo();
                        List<AliyunDownloadMediaInfo> dataList = new ArrayList<>();
                        for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : allDownloadMediaInfo) {
                            dataList.add(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo());
                        }
//                List<AliyunDownloadMediaInfo> dataList = downloadDataProvider.getAllDownloadMediaInfo();
                        // 存入顺序和显示顺序相反,  所以进行倒序
                        ArrayList<AliyunDownloadMediaInfo> tempList = new ArrayList<>();
                        int size = dataList.size();
                        for (AliyunDownloadMediaInfo aliyunDownloadMediaInfo : dataList) {
                            if (aliyunDownloadMediaInfo.getProgress() == 100) {
                                tempList.add(aliyunDownloadMediaInfo);
                            }
                        }

                        Collections.reverse(tempList);
                        if ((dataList.size() - 1) < 0 || (dataList.size() - 1) > tempList.size()) {
                            return;
                        }
                        tempList.add(dataList.get(dataList.size() - 1));
                        for (int i = 0; i < size; i++) {
                            AliyunDownloadMediaInfo aliyunDownloadMediaInfo = dataList.get(i);
                            if (!tempList.contains(aliyunDownloadMediaInfo)) {
                                tempList.add(aliyunDownloadMediaInfo);
                            }
                        }

                        if (positin < 0) {
                            FixedToastUtils.show(ControlMainActivity.this, "视频资源不存在");
                            return;
                        }

                        // 如果点击列表中的视频, 需要将类型改为vid
                        AliyunDownloadMediaInfo aliyunDownloadMediaInfo = tempList.get(positin);
                        PlayParameter.PLAY_PARAM_TYPE = "localSource";
                        if (aliyunDownloadMediaInfo != null) {
                            PlayParameter.PLAY_PARAM_URL = aliyunDownloadMediaInfo.getSavePath();
                            mAliyunVodPlayerView.updateScreenShow();
                            changePlayLocalSource(PlayParameter.PLAY_PARAM_URL, aliyunDownloadMediaInfo.getTitle());
                        }
                    }

                    @Override
                    public void onDownloadingItemClick(ArrayList<AlivcDownloadMediaInfo> infos, int position) {
                        AlivcDownloadMediaInfo alivcInfo = infos.get(position);
                        AliyunDownloadMediaInfo aliyunDownloadInfo = alivcInfo.getAliyunDownloadMediaInfo();
                        AliyunDownloadMediaInfo.Status status = aliyunDownloadInfo.getStatus();
                        if (status == AliyunDownloadMediaInfo.Status.Error || status == AliyunDownloadMediaInfo.Status.Wait) {
                            //downloadManager.removeDownloadMedia(aliyunDownloadInfo);
                            downloadManager.startDownload(aliyunDownloadInfo);

                        }
                    }

                });
            }
        }
    }

    /**
     * 开始下载的事件监听
     */
    private AddDownloadView.OnViewClickListener viewClickListener = new AddDownloadView.OnViewClickListener() {
        @Override
        public void onCancel() {
            if (downloadDialog != null) {
                downloadDialog.dismiss();
            }
        }

        @Override
        public void onDownload(AliyunDownloadMediaInfo info) {
            if (downloadDialog != null) {
                downloadDialog.dismiss();
            }
            aliyunDownloadMediaInfo = info;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permission = ContextCompat.checkSelfPermission(ControlMainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {

//                    ActivityCompat.requestPermissions(ControlMainActivity.this, PERMISSIONS_STORAGE,
//                            REQUEST_EXTERNAL_STORAGE);

                } else {
                    addNewInfo(info);
                }
            } else {
                addNewInfo(info);
            }

        }
    };
    private void addNewInfo(AliyunDownloadMediaInfo info) {
        if (downloadManager != null && info != null) {
            if (downloadView != null) {
                boolean hasAdd = downloadView.hasAdded(info);
                if (!hasAdd) {
                    if (downloadView != null && info != null) {
                        downloadView.addDownloadMediaInfo(info);
                    }
                    if (dialogDownloadView != null && info != null) {
                        dialogDownloadView.addDownloadMediaInfo(info);
                    }
                    downloadManager.startDownload(info);
                } else {
                    callDownloadPrepare(info.getVid(), info.getTitle());
                }
            }
        }
    }
    /**
     * 下载重新调用onPrepared方法,否则会出现断点续传的现象
     * 而且断点续传出错
     */
    private void callDownloadPrepare(String vid, String title) {
        VidSts vidSts = new VidSts();
        vidSts.setVid(vid);
        vidSts.setAccessKeyId(PlayParameter.PLAY_PARAM_AK_ID);
        vidSts.setAccessKeySecret(PlayParameter.PLAY_PARAM_AK_SECRE);
        vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
        vidSts.setTitle(title);
        downloadManager.prepareDownload(vidSts);
    }
    /**
     * 刷新下载的VidSts
     */
    private void refreshDownloadVidSts(final AliyunDownloadMediaInfo downloadMediaInfo) {
        VidStsUtil.getVidSts(mIpadress,downloadMediaInfo.getVidSts().getVid(), new VidStsUtil.OnStsResultListener() {
            @Override
            public void onSuccess(String vid, String akid, String akSecret, String token) {
                if (downloadManager != null) {
                    VidSts vidSts = new VidSts();
                    vidSts.setVid(vid);
                    vidSts.setRegion(PlayParameter.PLAY_PARAM_REGION);
                    vidSts.setAccessKeyId(akid);
                    vidSts.setAccessKeySecret(akSecret);
                    vidSts.setSecurityToken(token);
                    downloadMediaInfo.setVidSts(vidSts);
                    PlayParameter.PLAY_PARAM_AK_ID = akid;
                    PlayParameter.PLAY_PARAM_AK_SECRE = akSecret;
                    PlayParameter.PLAY_PARAM_SCU_TOKEN = token;
                    downloadManager.prepareDownloadByQuality(downloadMediaInfo, AliyunDownloadManager.INTENT_STATE_START);
                }
            }

            @Override
            public void onFail() {

            }
        });

    }
}