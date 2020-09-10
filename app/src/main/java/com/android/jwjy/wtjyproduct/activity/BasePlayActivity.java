package com.android.jwjy.wtjyproduct.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.dialog.AlertDialogFactory;
import com.android.jwjy.wtjyproduct.dialog.AlertDialogFragment;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.helper.AudioManagerHelper;
import com.android.jwjy.wtjyproduct.net.NetMonitor;
import com.android.jwjy.wtjyproduct.net.NetWorkStateReceiver;
import com.android.jwjy.wtjyproduct.util.ActivityUtil;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.EventBusUtil;
import com.android.jwjy.wtjyproduct.util.GestureLayoutWrapper;
import com.android.jwjy.wtjyproduct.util.ScreenSwitchUtils;
import com.android.jwjy.wtjyproduct.util.ViewUtil;
import com.android.jwjy.wtjyproduct.view.InputBarView;
import com.talkfun.sdk.HtSdk;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BasePlayActivity extends BaseActivity implements View.OnTouchListener {
    protected String mToken;
    private ScheduledExecutorService lance;
    protected boolean isTitleBarShow = false;
    public PowerManager.WakeLock wakeLock;
    /**
     * 视频是否跟ppt对调
     */
    protected boolean isExchangeViewContainer = false;
    /**
     * 状态栏高度
     */
    public int statusBarHeight;
    //  @Bind(R.id.ppt_Layout)
    RelativeLayout pptLayout; // 白板区域层（包含白板容器、桌面分享视频容器、控制按钮层）
    //@Bind(R.id.ppt_container)
    FrameLayout pptContainer;   // 添加白板的布局容器
    // @Bind(R.id.desktop_video_container)
    FrameLayout desktopVideoContainer;//添加桌面分享视频播放器的布局容器
    // @Bind(R.id.video_container)
    FrameLayout videoViewContainer;  // 添加摄像头视频播放器的布局容器
    // @Bind(R.id.inputEdt_layout)
    InputBarView vgInputLayout;
    // @Bind(R.id.play_container)
    LinearLayout linearContainer;
    // @Bind(R.id.tab_container)
    LinearLayout tab_container;

    TextView tvWatermak;//防盗录水印

    protected boolean isLongShowTitleBar = false;
    public InputMethodManager mInputMethodManager;
    //显示退出对话框
    private AlertDialog exitDialog;
    /**
     * 记录摄像头显隐
     */
    protected boolean userVideoShow = true;
    private Unbinder unbinder;
    protected int width = 0;
    protected int screenHeight = 0;
    protected NetWorkStateReceiver netWorkStateReceiver;//网络监听
    protected Handler handler = new Handler();

    GestureLayoutWrapper gestureLayoutWrapper;
    AudioManagerHelper audioManagerHelper;
    private boolean mReceiverTag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        init();
        initView();

        EventBusUtil.register(this);
    }

    abstract protected int getLayoutId();

    protected void init() {
        statusBarHeight = DimensionUtils.getStatusBarHeightone(this);
        PowerManager powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, this.getClass().getName());
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ScreenSwitchUtils.getInstance(this).setIsPortrait(true);

        mToken = getIntent().getStringExtra("token");
    }

    protected void initView() {
        pptLayout = findViewById(R.id.ppt_Layout);
        pptContainer = findViewById(R.id.ppt_container);
        desktopVideoContainer = findViewById(R.id.desktop_video_container);
        videoViewContainer = findViewById(R.id.video_container);
        vgInputLayout = findViewById(R.id.inputEdt_layout);
        linearContainer = findViewById(R.id.play_container);
        tab_container = findViewById(R.id.tab_container);
        gestureLayoutWrapper = new GestureLayoutWrapper(this, pptLayout);
        audioManagerHelper = new AudioManagerHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScreenSwitchUtils.getInstance(this).start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
        HtSdk.getInstance().onResume();
        registerNetWorkStateReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HtSdk.getInstance().onPause();
        unRegisterNetWorkStateReceiver();
    }


    @Override
    protected void onStop() {
        super.onStop();
        ScreenSwitchUtils.getInstance(this).stop();
        HtSdk.getInstance().onStop();
        if (wakeLock != null && wakeLock.isHeld())
            wakeLock.release();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        handler.removeCallbacksAndMessages(null);
        EventBusUtil.unregister(this);
        ScreenSwitchUtils.getInstance(this).isOpenSwitchAuto(false);
        HtSdk.getInstance().release();

    }

    /**
     * 返回
     *
     * @return
     */
    public void gobackAction() {
           /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ScreenSwitchUtils.getInstance(this).toggleScreen(false);
        } else {
            showExitDialog();
        }*/
        showExitDialog();
    }

    //------------------------------------------- 布局更新--------------------------------------------

    //标题栏计时器. 3秒后自动隐藏
    protected void autoDismissTitleBar() {
        stopDismissTitleBar();
        Runnable sendBeatRunnable = new Runnable() {
            @Override
            public void run() {
                if (isTitleBarShow) {
                    if (lance != null && !lance.isShutdown() && !isLongShowTitleBar) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isTitleBarShow) {
                                    hideTitleBar();
                                } else {
                                    stopDismissTitleBar();
                                }
                            }
                        });
                    }
                }
            }
        };

        lance = Executors.newSingleThreadScheduledExecutor();
        lance.scheduleAtFixedRate(sendBeatRunnable, 5, 5, TimeUnit.SECONDS);
    }

    protected void stopDismissTitleBar() {
        if (lance != null) {
            if (!lance.isShutdown()) {
                lance.shutdown();
            }
            lance = null;
        }
    }

    /**
     * 隐藏标题栏和操作按钮
     */
    protected final void hideTitleBar() {
        if (isLongShowTitleBar)
            return;
        stopDismissTitleBar();
        hideController();
        isTitleBarShow = false;
    }

    /**
     * 显示标题栏和操作按钮
     */
    protected final void showTitleBar() {
        if (lance != null && !lance.isShutdown())
            lance.shutdown();
        showController();
        isTitleBarShow = true;
        autoDismissTitleBar();
    }

    abstract void showController();

    abstract void hideController();


    public void showFullScreenInput(boolean isShow) {
    }

    /**
     * 视频容器的偏移量
     */
    public int getVideoYOffset() {
        return (int) (getResources().getDimension(R.dimen.tab_height));
    }

    /**
     * 全屏和非全屏切换
     */
    public void onFullScreenChange() {
        ScreenSwitchUtils.getInstance(this).setIsFullScreen(!ScreenSwitchUtils.getInstance(this).isFullScreen());
        if (ScreenSwitchUtils.getInstance(this).isSensorSwitchLandScreen()) {  //重力切换的横屏的话
            updateLayout();
        } else {
            ScreenSwitchUtils.getInstance(this).toggleScreen();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        boolean isPortrait = ScreenSwitchUtils.getInstance(this).isPortrait();
//        boolean isFullScreen = ScreenSwitchUtils.getInstance(this).isFullScreen();
//        if (!isPortrait && isFullScreen) {
//            ActivityUtil.setFullScreen(this, true);
//        } else if (isPortrait && !isFullScreen) {
//            ActivityUtil.setFullScreen(this, false);
//        }
        ActivityUtil.setFullScreen(this, !isPortrait);
        hideController();
        updateLayout();
        HtSdk.getInstance().onConfigurationChanged();
        super.onConfigurationChanged(newConfig);
    }

    public void layoutChanged() {
        if (isShowWatermark) {
            handler.removeCallbacks(watermarkRandomPositionRun);
            handler.post(watermarkRandomPositionRun);
        }
    }

    /**
     * 抽取
     * 更新布局
     */
    public void updateLayout() {
        layoutChanged();
        width = DimensionUtils.getScreenWidth(this);
        int height = DimensionUtils.getScreenHeight(this);
        boolean isPortrait = ScreenSwitchUtils.getInstance(this).isPortrait();
        if (!ActivityUtil.isFullScreen(this) && isPortrait) {
            height -= DimensionUtils.getStatusBarHeight(this);
        }
        screenHeight = height;
        //获取宽高
        int pptLayoutWidth = 0;
        isFirst = true;
        if (linearContainer != null && tab_container != null) {
            linearContainer.setOrientation(isPortrait ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            FrameLayout.LayoutParams videoViewParams = (FrameLayout.LayoutParams) videoViewContainer.getLayoutParams();
            LinearLayout.LayoutParams tablp = (LinearLayout.LayoutParams) tab_container.getLayoutParams();
            LinearLayout.LayoutParams flp = (LinearLayout.LayoutParams) pptLayout.getLayoutParams();
            RelativeLayout.LayoutParams pptParams = (RelativeLayout.LayoutParams) pptContainer.getLayoutParams();
            RelativeLayout.LayoutParams inputParams = vgInputLayout != null ? (RelativeLayout.LayoutParams) vgInputLayout.getLayoutParams() : null;
            if (!DimensionUtils.isPad(this) && isPortrait)
                videoViewParams.width = (int) (width * 0.4);
            else
                videoViewParams.width = (int) (width * 0.28);
            videoViewParams.height = videoViewParams.width / 4 * 3;
            pptLayout.setBackgroundColor(Color.TRANSPARENT);
            if (isPortrait) {   //竖屏
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
                pptLayoutWidth = width;
                height = 3 * width / 4;
                tablp.width = pptLayoutWidth;
                tablp.height = 0;
                tablp.weight = 1.0f;
                tablp.topMargin = 0;
                flp.height = height;
                if (inputParams != null)
                    inputParams.width = pptLayoutWidth;
                videoViewParams.leftMargin = width - videoViewContainer.getLayoutParams().width;
                videoViewParams.topMargin = height + getVideoYOffset();
                tab_container.setVisibility(View.VISIBLE);
                showFullScreenInput(false);

            } else {  //横屏
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }

                flp.height = height;
                tablp.width = 0;
                tablp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                tablp.weight = 1.0f;
                videoViewParams.leftMargin = width - videoViewContainer.getLayoutParams().width;
                videoViewParams.topMargin = 0;
                tab_container.setVisibility(View.GONE);
                //!ScreenSwitchUtils.getInstance(this).isFullScreen())
                if (DimensionUtils.isPad(this)) {
                    tab_container.setVisibility(View.VISIBLE);
                    showFullScreenInput(false);
                    if (videoViewContainer.getVisibility() == View.VISIBLE)
                        tablp.topMargin = videoViewParams.height;
                    else
                        tablp.topMargin = 0;
                    pptLayoutWidth = (int) (width * 0.72);
                    height = pptLayoutWidth * 3 / 4;
                    pptLayout.setBackgroundColor(Color.BLACK);
                    if (inputParams != null)
                        inputParams.width = (int) (width * 0.28);
                } else {
                    tab_container.setVisibility(View.GONE);
                    showFullScreenInput(true);
                    pptLayoutWidth = width;
                }
            }

            flp.width = pptLayoutWidth;
            pptParams.width = pptLayoutWidth;
            pptParams.height = height;
            pptLayout.setLayoutParams(flp);
            pptContainer.setLayoutParams(pptParams);
            tab_container.setLayoutParams(tablp);
            videoViewContainer.setLayoutParams(videoViewParams);
            if (vgInputLayout != null) {
                tablp.bottomMargin = vgInputLayout.getHeight();
                vgInputLayout.setLayoutParams(inputParams);
                vgInputLayout.updateInputBarWidth(pptLayoutWidth);
            }
        }
    }

    //------------------------------------------- 更新视频位置----------------------------------------
    float downTouchX, downTouchY, downRawX, downRawY;
    private boolean isFull = false;
    private FrameLayout.LayoutParams layoutParams;
    private boolean isFirst = true;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTouchX = event.getX();
                downTouchY = event.getY();
                downRawX = x;
                downRawY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(downRawX - x) <= ViewConfiguration.getTouchSlop() && Math.abs(downRawY - y) <= ViewConfiguration.getTouchSlop()) {
                    return false;
                }
                updateVideoPosition((int) (x - downTouchX), (int) (y - downTouchY));
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(downRawX - x) <= ViewConfiguration.getTouchSlop() && Math.abs(downRawY - y) <= ViewConfiguration.getTouchSlop()) {
                    isFull = !isFull;
                    if (videoViewContainer != null && layoutParams != null) {
                        videoViewContainer.setLayoutParams(isFull ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) : layoutParams);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 更新视频位置
     *
     * @param x
     * @param y
     */
    protected void updateVideoPosition(int x, int y) {
        boolean isPortrait = ScreenSwitchUtils.getInstance(this).isPortrait();
        if (!isPortrait && DimensionUtils.isPad(this)) {
            return;
        }
//        更新浮动窗口位置参数
        int orientation = getRequestedOrientation();
        int width = DimensionUtils.getScreenWidth(this);
        int height = DimensionUtils.getScreenHeight(this);
        //获取宽高
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || orientation == -1) {
            height -= statusBarHeight;
            y -= statusBarHeight;
        }

        x = Math.min(Math.max(0, x), width - videoViewContainer.getWidth());
        y = Math.min(Math.max(0, y), height - videoViewContainer.getHeight());

        ViewUtil.setViewXY(videoViewContainer, x, y);
//        layoutParams = (FrameLayout.LayoutParams) videoViewContainer.getLayoutParams();
    }

    //-------------抽取---------------------------------弹窗--------------------------------------------
    private void showExitDialog() {
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.exit),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                new AlertDialogFragment.AlertDialogListener() {
                    @Override
                    public void onConfirm() {
                        stopDismissTitleBar();
                        finish();
                    }
                });
    }


    //-----------------------------------
    protected void showVideoContainer(ImageView videoVisibleIv, boolean isShow) {
        if (videoViewContainer != null) {
            videoViewContainer.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);

        }
        if (videoVisibleIv != null) {
            videoVisibleIv.setSelected(isShow);
        }
    }

    /**
     * 视频显隐
     *
     * @param videoVisibleIv
     */
    protected void onVideoVisible(ImageView videoVisibleIv) {
        userVideoShow = videoVisibleIv != null && videoVisibleIv.isSelected() ? false : true;

        if (isVideoViewContainerVisiable()) {
            exchangeViewContainer();
            showVideoContainer(videoVisibleIv, false);
        } else {
            showVideoContainer(videoVisibleIv, HtSdk.getInstance().isVideoShow());
        }

        if (ScreenSwitchUtils.getInstance(this).isSensorNotFullLandScreen()) {
            updateLayout();
        }
    }

    protected void exchangeViewContainer() {
        if (isExchangeViewContainer) {
            HtSdk.getInstance().exchangeVideoAndWhiteboard();
            isExchangeViewContainer = !isExchangeViewContainer;
        }
    }


    protected boolean isVideoViewContainerVisiable() {
        return videoViewContainer != null && (videoViewContainer.getVisibility() == View.VISIBLE);
    }


    protected void registerNetWorkStateReceiver() {
        if (!mReceiverTag) {
            mReceiverTag = true;
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(netWorkStateReceiver, intentFilter);
        }
    }

    protected void unRegisterNetWorkStateReceiver() {
        if (mReceiverTag) {
            mReceiverTag = false;
            this.unregisterReceiver(netWorkStateReceiver);
            netWorkStateReceiver = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCallback(Event message) {
        if (message == null)
            return;
        if (message.getType() == EventType.NETWORK_STATE_CHANGE) {
            int netStatus = (int) message.getData();
            if (netStatus == NetMonitor.NETWORK_NONE) {
                AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.not_connect), null);
            }
        }
    }

    protected boolean isShowWatermark = false;

    /**
     * 显示防盗录水印
     *
     * @param makeStr
     */
    protected void startShowWatermark(String makeStr) {
        startShowWatermark(pptLayout, makeStr);
    }

    /**
     * 显示防盗录水印
     *
     * @param viewGroup 显示水印的父布局
     * @param makeStr
     */
    protected void startShowWatermark(ViewGroup viewGroup, String makeStr) {
        if (viewGroup == null) {
            return;
        }
        ensureWatermak();
        tvWatermak.setText(makeStr);
        if (tvWatermak.getParent() == null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewGroup.addView(tvWatermak, layoutParams);
        }
        isShowWatermark = true;
        handler.removeCallbacks(watermarkRandomPositionRun);
        handler.post(watermarkRandomPositionRun);
    }

    /**
     * 停止显示防盗录水
     */
    protected void stopShowWatermark() {
        isShowWatermark = false;
        handler.removeCallbacks(watermarkRandomPositionRun);
        if (tvWatermak == null || tvWatermak.getParent() == null)
            return;
        ((ViewGroup) tvWatermak.getParent()).removeView(tvWatermak);
    }

    protected void ensureWatermak() {
        if (tvWatermak == null) {
            tvWatermak = new TextView(this);
            tvWatermak.setTextColor(Color.parseColor("#66ffffff"));
            //tvWatermak.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            tvWatermak.setShadowLayer(10, 0, 0, Color.parseColor("#000000"));
            //tvWatermak.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12,getResources().getDisplayMetrics()));
        }
    }

    final protected Runnable watermarkRandomPositionRun = new Runnable() {
        @Override
        public void run() {

            //LiveNativeActivity.this.getWindowManager().getDefaultDisplay().getSize(sizePoint);
            ViewGroup parent = (ViewGroup) tvWatermak.getParent();
            if (parent == null) {
                return;
            }
            float x = (float) (Math.random() * (parent.getWidth() - tvWatermak.getWidth()));
            float y = (float) (Math.random() * (parent.getHeight() - tvWatermak.getHeight()));
            tvWatermak.setX(x);
            tvWatermak.setY(y);

            int delay = 6000 + (int) (Math.random() * 12000);
            handler.postDelayed(this, delay);
        }
    };


}
