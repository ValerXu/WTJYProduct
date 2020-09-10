package com.android.jwjy.wtjyproduct.activity;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.TextParams;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.VideoAdapter;
import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.consts.PlatformType;
import com.android.jwjy.wtjyproduct.dialog.AlertDialogFactory;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.android.jwjy.wtjyproduct.event.SimpleGestureLayoutListener;
import com.android.jwjy.wtjyproduct.helper.AudioManagerHelper;
import com.android.jwjy.wtjyproduct.impl.OnRtcMemberImpl;
import com.android.jwjy.wtjyproduct.manager.ColorViewPopManager;
import com.android.jwjy.wtjyproduct.manager.DrawAndStrokePopManager;
import com.android.jwjy.wtjyproduct.manager.RtcChatPopManager;
import com.android.jwjy.wtjyproduct.manager.SwitchModePopManager;
import com.android.jwjy.wtjyproduct.net.NetMonitor;
import com.android.jwjy.wtjyproduct.net.NetWorkStateReceiver;
import com.android.jwjy.wtjyproduct.util.AnimatorUtil;
import com.android.jwjy.wtjyproduct.util.ClickUtil;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.EventBusUtil;
import com.android.jwjy.wtjyproduct.util.GestureLayoutWrapper;
import com.android.jwjy.wtjyproduct.util.ScreenSwitchUtils;
import com.android.jwjy.wtjyproduct.util.SoftKeyboardStateWatcher;
import com.android.jwjy.wtjyproduct.util.TimeUtil;
import com.android.jwjy.wtjyproduct.util.ToastUtil;
import com.android.jwjy.wtjyproduct.util.ViewUtil;
import com.android.jwjy.wtjyproduct.view.LoadingImageView;
import com.android.jwjy.wtjyproduct.view.SectorLayout;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.consts.Interaction;
import com.talkfun.sdk.consts.LiveStatus;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.consts.TFMode;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtDispatchChatMessageListener;
import com.talkfun.sdk.event.LiveInListener;
import com.talkfun.sdk.event.OnLiveDurationListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.VideoModeType;
import com.talkfun.sdk.rtc.RtcOperatorProxy;
import com.talkfun.sdk.rtc.consts.ApplyStatus;
import com.talkfun.sdk.rtc.consts.DrawPowerStatus;
import com.talkfun.sdk.rtc.consts.MediaStatus;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;
import com.talkfun.sdk.rtc.interfaces.OnRtcErrorListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcMediaStatusListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcMemberListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcStatusListener;
import com.talkfun.sdk.rtc.interfaces.OnWhiteboardPowerListener;
import com.talkfun.widget.ColorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.emitter.Emitter;
import q.rorbin.badgeview.QBadgeView;

public class LiveMixNativeActivity extends BaseActivity implements LiveInListener, HtDispatchChatMessageListener, OnLiveDurationListener, View.OnTouchListener {
    @BindView(R.id.base_container)
    LinearLayout base_container;
    @BindView(R.id.ppt_fl_layout)
    FrameLayout pptLayout; // 白板父类
    /**
     * 视频控制器
     */
    @BindView(R.id.video_container)
    FrameLayout videoLayout;
    @BindView(R.id.ppt_container)
    FrameLayout pptContainer; // 白板
    @BindView(R.id.video_view_container_rv)
    RecyclerView videoContainerRV;//视频区域
    @BindView(R.id.activity_small_container)
    LinearLayout controllContainer;//控制栏区域
    @BindView(R.id.activity_small_titlebar)
    RelativeLayout titlebarContainer;//标题栏区域
    @BindView(R.id.bad_netStatus_ll)
    LinearLayout badNetStatusLL;//标题栏区域
    //-------------------控制栏相关控件-----------------------------
    /**
     * 申请和取消讲台按钮
     */
    @BindView(R.id.platform_iv)
    LoadingImageView platformIV;
    /**
     * 下讲台按钮
     */
    @BindView(R.id.down_platform_iv)
    View downPlatformIV;
    /**
     * 聊天按钮
     */
    @BindView(R.id.chat_iv)
    View chatFab;
    /**
     * 视频开关
     */
    @BindView(R.id.video_switch_iv)
    ImageView videoSwitchIV;
    /**
     * 音频开关
     */
    @BindView(R.id.audio_switch_iv)
    ImageView audioSwitchIV;
    /***
     * 聊天小红点
     */
    private QBadgeView qBadgeView;

    //----------标题栏相关控件---------------------------------
    /**
     * 标题栏
     */
    @BindView(R.id.activity_small_title_tv)
    TextView titleTV;
    /**
     * 播放时间
     */
    @BindView(R.id.activity_small_play_total_time_tv)
    TextView cuurentPlayTotalTimeTV;
    @BindView(R.id.network_state_tv)
    TextView networkStateTV;

    //-------------------涂鸦扇形盘-------------------------------
    @BindView(R.id.paint_sl)
    SectorLayout paintSL;
    @BindView(R.id.sector_layout_paint_iv)
    ImageView paintIV;
    @BindView(R.id.sector_layout_cmd_iv)
    ImageView cmdIV;
    @BindView(R.id.sector_layout_stroke_cv)
    ColorView strokeCV;
    @BindView(R.id.sector_layout_color_cv)
    ColorView colorCV;
    @BindView(R.id.sector_layout_eraser_iv)
    ImageView eraserIV;


    /**
     * 涂鸦类型和粗细的弹窗管理类
     */
    private DrawAndStrokePopManager mDrawAndStrokePopManager;
    /**
     * 聊天弹窗管理类
     */
    private RtcChatPopManager chatPopManager;
    /**
     * 涂鸦及颜色弹窗管理类
     */
    private ColorViewPopManager mColorViewPopManager;
    private Map<Integer, VideoStatusData> mVideoMap = new ConcurrentHashMap<>();
    private RtcOperatorProxy mRtcOperatorProxy;
    private VideoAdapter videoAdapter;
    private Unbinder unbinder;
    private String mToken;
    /**
     * 主播Id
     */
    private int spidId;
    private HtSdk mHtSdk;
    /**
     * 视频是否被主播关闭
     */
    private boolean isCloseVideoForZhubo = false;

    /**
     * 是否被主播关闭
     */
    private boolean isCloseAudioForZhubo = false;
    /**
     * 是否正在直播
     */
    private boolean isLiving;
    /**
     * 橡皮檫是否选中
     */
    private boolean eraserIsSelect = false;
    /**
     * 是否有涂鸦权限
     */
    private boolean isDrawPower;
    private NetWorkStateReceiver netWorkStateReceiver;
    private int videoWidth;
    private boolean isReload;
    private int pptWidth;
    private int screenWidth;
    private int screenHeight;
    private SwitchModePopManager mSwitchModePopManager;
    private AlertDialog socketFailTips;
    private boolean isFront;
//    private TextView loadFailTv;

    GestureLayoutWrapper gestureLayoutWrapper;
    AudioManagerHelper audioManagerHelper;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕唤醒
        setContentView(R.layout.activity_mix_live_native);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//SurfaceView闪屏修复
        unbinder = ButterKnife.bind(this);
        EventBusUtil.register(this);
        init();
        initView();
        initHtSdk();
        initListener();
    }

    private void init() {
        mToken = getIntent().getStringExtra("token");
        setVideoLayoutParams();
    }

    private void setVideoLayoutParams() {
        screenWidth = DimensionUtils.getScreenWidth(this);
        screenHeight = DimensionUtils.getScreenHeight(this);
        videoWidth = screenHeight / 3;
        videoContainerRV.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(videoWidth, screenHeight / 4);
        layoutParams.topMargin = this.getResources().getDimensionPixelSize(R.dimen.activity_small_titlebar_height);
        videoLayout.setLayoutParams(layoutParams);
    }


    private void initView() {
        setVideoAdapter();
        chatPopManager = new RtcChatPopManager(this);
        mSwitchModePopManager = new SwitchModePopManager(this);
        qBadgeView = new QBadgeView(this);
        qBadgeView.bindTarget(chatFab);
//        qBadgeView.setGravityOffset(5, true);pkw2
        qBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        gestureLayoutWrapper = new GestureLayoutWrapper(this, pptContainer);
        audioManagerHelper = new AudioManagerHelper(this);
    }

    private void setVideoAdapter() {
        videoAdapter = new VideoAdapter(this, null);
        videoAdapter.setHasStableIds(true);
        videoContainerRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        videoContainerRV.setAdapter(videoAdapter);
        videoContainerRV.setHasFixedSize(true);
    }

    private void initHtSdk() {
        mHtSdk = HtSdk.getInstance();
        mHtSdk.init(this, pptContainer, videoLayout, mToken, TFMode.LIVE_MIX);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View liveWaitView = layoutInflater.inflate(R.layout.live_wait_layout, null);
        View liveOverView = layoutInflater.inflate(R.layout.live_over_layout, null);
        View loadingView = layoutInflater.inflate(R.layout.loading_layout, null);
        View loadFailView = layoutInflater.inflate(R.layout.load_fail_layout, null);
//        loadFailTv = loadFailView.findViewById(R.id.load_fail_tv);
        mHtSdk.setLiveWaitView(liveWaitView);//设置直播未开始显示view
        mHtSdk.setLiveOverView(liveOverView);//设置直播结束显示view
        mHtSdk.setLoadingView(loadingView); //设置正在加载初始化数据显示view
        mHtSdk.setLoadFailView(loadFailView);//设置加载初始化数据失败显示view
        mHtSdk.setWhiteboardLoadFailDrawable(getResources().getDrawable(R.mipmap.image_broken));
        mHtSdk.setOnVideoChangeListener(new OnPlayVideoChangeLister());

        //socket连接失败提示
        mHtSdk.on(BroadcastCmdType.SOCKET_CONNECT_FAIL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (LiveMixNativeActivity.this.isFinishing()) {
                    return;
                }
                if (socketFailTips == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LiveMixNativeActivity.this);
                    builder.setMessage("socket连接失败，请检测网络是否正常，并重新刷新试试");
                    builder.setTitle(R.string.tips);
                    builder.setPositiveButton(R.string.refresh, (dialog, which) -> HtSdk.getInstance().reload()).setNegativeButton(R.string.goback, (dialog, which) -> LiveMixNativeActivity.this.finish());

                    socketFailTips = builder.create();
                    socketFailTips.setCancelable(false);
                }

                socketFailTips.show();
            }
        });
    }


    private void initListener() {
        /**设置聊天信息事件监听*/
        mHtSdk.setHtDispatchChatMessageListener(this);
        mHtSdk.setLiveDurationListener(this);
        mHtSdk.setRtcStatusListener(mOnRtcStatusListener);
        mHtSdk.setRtcMemberListener(mOnRtcMemberListener);
        mHtSdk.setRtcErrorListener(mOnRtcErrorListener);
        mHtSdk.setRtcMediaStatusListener(mOnRtcMediaStatusListener);
        mHtSdk.setWhiteboardPowerListener(mOnWhiteboardPowerListenter);
        mHtSdk.setLiveListener(this);
        videoLayout.setOnTouchListener(this);
//        CheckNetSpeed.getInstance().startCheckNetSpeed(new CheckNetSpeed.OnNetSpeedChangeListener() {
//            @Override
//            public void getNetSpeedAndState(int speed, String netState) {
//                networkStateTV.setVisibility(CheckNetSpeed.POOR_STATE.equals(netState) ? View.VISIBLE : View.GONE);
//            }
//        });
        /**
         * 软键盘监听
         */
        SoftKeyboardStateWatcher stateWatcher = new SoftKeyboardStateWatcher(pptLayout);
        stateWatcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
            }

            @Override
            public void onSoftKeyboardClosed() {
                if (chatPopManager != null) {
                    chatPopManager.inputTextMsgDialogDismiss();
                }
            }
        });

        paintSL.setOnDrawEnableListener(new SectorLayout.OnDrawEnableListener() {
            @Override
            public void enable() {
                gestureLayoutWrapper.setMaskEnable(false);
                AnimatorUtil.rotate(paintIV, 45, 0);
            }

            @Override
            public void disable() {
                gestureLayoutWrapper.setMaskEnable(true);
                AnimatorUtil.rotate(paintIV, 0, 45);
            }
        });

        gestureLayoutWrapper.setGestureLayoutListener(new SimpleGestureLayoutListener() {
            @Override
            public void onStartVolumeOffset() {
                int streamType = applyStatus() == ApplyStatus.ALLOW ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
                audioManagerHelper.startVolumeOffset(streamType);
            }

            @Override
            public void onVolumeOffset(float offsetPercentage) {
                audioManagerHelper.volumeOffset(offsetPercentage);
            }

            @Override
            public void onStopVolumeOffset() {
                audioManagerHelper.stopVolumeOffset();
            }

            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onClick() {
                if (isTitleBarShow) {
                    hideController();
                } else {
                    showController();
                }
                return false;
            }

            @Override
            public boolean onDoubleClick() {
                return true;
            }
        });
    }

    /**
     * 聊天的接受
     *
     * @param entity
     */
    @Override
    public void receiveChatMessage(ChatEntity entity) {
        if (chatPopManager != null) {
            chatPopManager.receiveMessage(entity);
            if (qBadgeView != null) {//红点提示
                qBadgeView.setBadgeNumber(chatPopManager.isShow() ? 0 : -1);
            }
        }
    }

    /**
     * 点击事件
     *
     * @param
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.platform_iv, R.id.video_switch_iv, R.id.down_platform_iv, R.id.audio_switch_iv, R.id.paint_sl, R.id.activity_small_refresh_iv
            , R.id.activity_small_back_rl, R.id.ppt_container, R.id.chat_iv, R.id.sector_layout_color_cv, R.id.sector_layout_cmd_iv, R.id.sector_layout_stroke_cv, R.id.sector_layout_eraser_iv})
    void onClick(View v) {
        if (!ClickUtil.getInstance().isClickable(v.getId()))
            return;
        switch (v.getId()) {
            case R.id.platform_iv:
                if (mRtcOperatorProxy != null) {
                    if (ApplyStatus.NO_APPLY == applyStatus()) {//申请上讲台
                        showApplyPlatformDialog();
                    } else if (ApplyStatus.APPLYING == applyStatus()) {//取消上讲台
                        showCanclePlatformDialog();
                    }
                }

                break;
            case R.id.down_platform_iv: //下讲台
                showDownPlatformDialog();
                break;
            case R.id.video_switch_iv: //摄像头操作
                if (isCloseVideoForZhubo) {
//                    ToastUtil.show(LiveSmallNativeActivity.this, "主播已关闭你的摄像头，无法打开", videoWidth / 2);
                    showToast("主播已关闭你的摄像头，无法打开");
                    return;
                }
                if (mRtcOperatorProxy != null) {
                    if (videoSwitchIV.isSelected()) {//关闭摄像头
                        mRtcOperatorProxy.closeVideo(new Callback<RtcUserEntity>() {
                            @Override
                            public void success(RtcUserEntity result) {
                                setVideoSwitch(false);
                                updateVideoLayout(result, VideoAdapter.VIDEO);
                            }

                            @Override
                            public void failed(String failed) {
//                                ToastUtil.show(LiveSmallNativeActivity.this, failed);
                                showToast(failed);
                            }
                        });
                    } else {//开启摄像头
                        mRtcOperatorProxy.openVideo(new Callback<RtcUserEntity>() {
                            @Override
                            public void success(RtcUserEntity result) {
                                setVideoSwitch(true);
                                updateVideoLayout(result, VideoAdapter.VIDEO);
                            }

                            @Override
                            public void failed(String failed) {
//                                ToastUtil.show(LiveSmallNativeActivity.this, failed);
                                showToast(failed);
                            }
                        });
                    }

                }
                break;
            case R.id.audio_switch_iv: //音频
                if (isCloseAudioForZhubo) {
//                    ToastUtil.show(LiveSmallNativeActivity.this, "主播已关闭你的音频，无法打开");
                    showToast("主播已关闭你的音频，无法打开");
                    return;
                }
                if (mRtcOperatorProxy != null) {
                    if (audioSwitchIV.isSelected()) {
                        mRtcOperatorProxy.closeAudio(new Callback<RtcUserEntity>() {
                            @Override
                            public void success(RtcUserEntity result) {
                                setAudioSwitch(false);
                                updateVideoLayout(result, VideoAdapter.AUDIO);
                            }

                            @Override
                            public void failed(String failed) {
//                                ToastUtil.show(LiveSmallNativeActivity.this, failed);
                                showToast(failed);
                            }
                        });
                    } else {
                        mRtcOperatorProxy.openAudio(new Callback<RtcUserEntity>() {
                            @Override
                            public void success(RtcUserEntity result) {
                                setAudioSwitch(true);
                                updateVideoLayout(result, VideoAdapter.AUDIO);
                            }

                            @Override
                            public void failed(String failed) {
//                                ToastUtil.show(LiveSmallNativeActivity.this, failed);
                                showToast(failed);
                            }
                        });
                    }
                }
                break;
            case R.id.activity_small_refresh_iv:
                mVideoMap.clear();
                if (videoAdapter != null) {
                    videoAdapter.clear();
                }
                reload();
                break;
            case R.id.chat_iv:
                showChatPop();
                break;
            case R.id.activity_small_back_rl:
                showExitDialog();
                break;
            case R.id.ppt_container:  //标题栏的显隐
                if (isTitleBarShow) {
                    hideController();
                } else {
                    showController();
                }
                break;
            case R.id.sector_layout_color_cv:  //扇形盘-颜色
                if (eraserIsSelect) {
                    resetDrawCmd();
                }
                mColorViewPopManager.show(strokeCV);
                break;
            case R.id.sector_layout_cmd_iv:  //扇形盘-形状
                if (eraserIsSelect) {
                    resetDrawCmd();
                }
                mDrawAndStrokePopManager.setShowType(DrawAndStrokePopManager.CMD_TYPE);
                mDrawAndStrokePopManager.show(strokeCV);
                break;
            case R.id.sector_layout_stroke_cv:  //扇形盘-粗细
                if (eraserIsSelect) {
                    resetDrawCmd();
                }
                mDrawAndStrokePopManager.setShowType(DrawAndStrokePopManager.STROKE_TYPE);
                mDrawAndStrokePopManager.show(strokeCV);
                break;
            case R.id.sector_layout_eraser_iv:  //扇形盘-橡皮擦
                eraserIsSelect = !eraserIsSelect;
                eraserIV.setSelected(eraserIsSelect);
                mDrawAndStrokePopManager.setEraser(eraserIsSelect);
                break;
        }
        if (v.getId() != R.id.ppt_container) {//当有操作的时候，不对titlebar进行隐藏
            stopDismissTitleBar();
        }
    }

    /**
     * 当橡皮檫关闭的时候，设置橡皮檫未选中，重置下涂鸦的类型
     */
    private void resetDrawCmd() {
        if (mDrawAndStrokePopManager != null) {
            mDrawAndStrokePopManager.setEraser(false);
        }
        if (eraserIV != null) {
            eraserIV.setSelected(false);
        }
        eraserIsSelect = false;
    }

    /**
     * 用户申请讲台的状态
     *
     * @return
     */
    private int applyStatus() {
        return mHtSdk != null && mHtSdk.getRtcInfo() == null ? ApplyStatus.NO_APPLY : mHtSdk.getRtcInfo().userApplyStatus;
    }

    /**
     * 聊天窗口的显隐
     */
    private void showChatPop() {
        if (chatPopManager != null) {
            if (chatPopManager.isShow()) {
                chatPopManager.dismiss();
            } else {
                if (qBadgeView != null) {
                    qBadgeView.setBadgeNumber(0);
                }
                chatPopManager.show(pptLayout);
                stopDismissTitleBar();
            }
        }


    }


    //------------------------------------------------------------------------------------------------------
    private OnRtcStatusListener mOnRtcStatusListener = new OnRtcStatusListener() {
        /**
         * rtc开启
         */
        @Override
        public void onOpen() {
            isOpen = true;
            int status = applyStatus();
            if (ApplyStatus.APPLYING == status) {
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.applying_platform));
                setPlatformStatus(PlatformType.APPLYING);
            } else if (ApplyStatus.ALLOW == status) {

            } else {
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.start_platform));
                setPlatformStatus(PlatformType.START);
            }
        }

        /**
         * rtc关闭
         */
        @Override
        public void onClose() {
            isOpen = false;
            showToast(LiveMixNativeActivity.this.getResources().getString(R.string.close_platform));
            setVideoContainerRVGone();
            setPlatformStatus(PlatformType.CLOSE);

        }

        @Override
        public void onConnectionInterrupted() {
            if (mVideoMap != null) {
                for (VideoStatusData videoStatusData : mVideoMap.values()) {
                    videoStatusData.setVideoOfflineStatus(1);
                    videoAdapter.updateItemOfPart(VideoAdapter.VIDEOLOADING, videoStatusData);
                }
            }
        }

        @Override
        public void onReConnectSuccess() {
            for (VideoStatusData videoStatusData : mVideoMap.values()) {
                videoStatusData.setVideoOfflineStatus(0);
                videoAdapter.updateItemOfPart(VideoAdapter.VIDEOLOADING, videoStatusData);
            }
        }

    };
    private OnRtcMemberListener mOnRtcMemberListener = new OnRtcMemberImpl() {
        /**
         * 用户被踢下讲台
         * @param rtcUserEntity
         */
        @Override
        public void onKick(RtcUserEntity rtcUserEntity) {
            if (!mVideoMap.containsKey(rtcUserEntity.getXid())) {
                return;
            }
            VideoStatusData videoStatusData = mVideoMap.remove(rtcUserEntity.getXid());
            videoAdapter.removeItem(videoStatusData);
            if (rtcUserEntity.isMe()) {
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.kick_platform));
                setPlatformStatus(PlatformType.KICK);
            }
        }

        /**
         * 用户上讲台（包含主播）
         *
         * @param rtcUserEntity
         * @param videoView
         */
        @Override
        public void onUp(RtcUserEntity rtcUserEntity, View videoView) {
            if (videoView == null && rtcUserEntity != null) {
                showToast(rtcUserEntity.getNickname() + "上讲台！");
                return;
            }
            if (videoContainerRV != null && videoContainerRV.getVisibility() == View.GONE) {
                videoContainerRV.setVisibility(View.VISIBLE);
            }
            addUpData(rtcUserEntity, videoView);
            if (rtcUserEntity.isMe()) {
                AlertDialogFactory.dismiss();
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.up_platform));
                setVideoSwitch(rtcUserEntity.isVideoOpen());
                setAudioSwitch(rtcUserEntity.isAudioOpen());
                setIsCloseVideoForZhubo(rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO);
                setIsCloseAudioForZhubo(rtcUserEntity.getAudio() == MediaStatus.CLOSE_FOR_ZHUBO);
                setPlatformStatus(PlatformType.ALLOW);

            }
        }

        /**
         * 用户下讲台
         * @param rtcUserEntity
         */
        @Override
        public void onDown(RtcUserEntity rtcUserEntity) {
            videoAdapter.removeItem(mVideoMap.remove(rtcUserEntity.getXid()));
            if (rtcUserEntity.isMe()) {
                setVideoContainerRVGone();
                setPlatformStatus(PlatformType.DOWN);
            }
        }

        /**
         *
         * @param rtcUserEntity
         * @param reason
         */
        @Override
        public void onOffline(RtcUserEntity rtcUserEntity, int reason) {
            if (mVideoMap != null && mVideoMap.containsKey(rtcUserEntity.getXid())) {
                VideoStatusData mVideoStatusData = mVideoMap.remove(rtcUserEntity.getXid());
                videoAdapter.removeItem(mVideoStatusData);
            }
        }

        @Override
        public void onInvite() {
            popupInviteDialog();
        }

    };
    private OnRtcErrorListener mOnRtcErrorListener = (code, msg) -> showToast(msg);

    /**
     * 邀请上讲台弹框
     */
    protected BaseCircleDialog popupInviteDialog() {
        BaseCircleDialog baseCircleDialog = new CircleDialog.Builder().setCanceledOnTouchOutside(false).setWidth(0.3f).setText(this.getResources().getString(R.string.invite_rtc_up_tips)).configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.textColor = Color.parseColor("#1D334E");
            }
        }).setTitle("提示").setPositive("同意", v -> respondInvite(true, new Callback<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void failed(String failed) {

            }
        })).configPositive(params -> params.textColor = getResources().getColor(R.color.rtc_invite_accept)).setNegative("拒绝", v -> respondInvite(false, new Callback<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void failed(String failed) {

            }
        })).configNegative(params -> params.textColor = Color.parseColor("#263548")).create();
        baseCircleDialog.show(this.getSupportFragmentManager(), "InviteDialog");
        return baseCircleDialog;
    }

    public void respondInvite(boolean isAccept, final Callback<String> callback) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.respondInvite(isAccept ? Interaction.InviteStatus.ACCEPT : Interaction.InviteStatus.REJECT, callback);
    }

    private synchronized void addUpData(RtcUserEntity rtcUserEntity, View view) {
        VideoStatusData videoStatusData = new VideoStatusData(rtcUserEntity, view);
        mVideoMap.put(rtcUserEntity.getXid(), videoStatusData);

        if (rtcUserEntity.isMe()) {
            if (mVideoMap.size() > 1) {
                videoAdapter.addItem(1, videoStatusData);
            } else {
                videoAdapter.addItem(videoStatusData);
            }
        } else if (MemberRole.MEMBER_ROLE_SUPER_ADMIN == rtcUserEntity.getRole()) {
            spidId = rtcUserEntity.getXid();
            videoAdapter.addItem(0, videoStatusData);
        } else {
            videoAdapter.addItem(videoStatusData);
        }
    }

    private OnRtcMediaStatusListener mOnRtcMediaStatusListener = new OnRtcMediaStatusListener() {
        @Override
        public void onVideoClose(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.VIDEO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.close_video));
                setVideoSwitch(false);
                isCloseVideoForZhubo = rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO;
            }

        }

        @Override
        public void onVideoOpen(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.VIDEO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.open_video));
                setVideoSwitch(true);
                isCloseVideoForZhubo = rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO;
            }
        }

        @Override
        public void onAudioOpen(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.AUDIO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                setAudioSwitch(true);
                isCloseAudioForZhubo = rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO;
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.open_audio));
            }
        }

        @Override
        public void onAudioClose(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.AUDIO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                setAudioSwitch(false);
                isCloseAudioForZhubo = rtcUserEntity.getAudio() == MediaStatus.CLOSE_FOR_ZHUBO;
                showToast(LiveMixNativeActivity.this.getResources().getString(R.string.close_audio));
            }
        }
    };

    /**
     * 涂鸦权限的监听
     */
    private OnWhiteboardPowerListener mOnWhiteboardPowerListenter = new OnWhiteboardPowerListener() {
        @Override
        public void onDrawEnable(RtcUserEntity rtcUserEntity) {
            receiveDrawPower(rtcUserEntity);
        }

        @Override
        public void onDrawDisable(RtcUserEntity rtcUserEntity) {
            receiveDrawPower(rtcUserEntity);
        }
    };


    /**
     * 接受涂鸦权限
     *
     * @param rtcUserEntity
     */

    private void receiveDrawPower(RtcUserEntity rtcUserEntity) {
//        if (mVideoMap.containsKey(rtcUserEntity.getXid())) {
//            VideoStatusData videoStatusData = mVideoMap.get(rtcUserEntity.getXid());
////            videoStatusData.setRtcUserEntity(rtcUserEntity);
//            videoAdapter.updateItemOfPart(VideoAdapter.PAINT, videoStatusData);
//        }
        updateVideoLayout(VideoAdapter.PAINT, rtcUserEntity);
        if (rtcUserEntity.isMe()) {
            isDrawPower = rtcUserEntity.getDrawPower() == DrawPowerStatus.OPEN;
            showToast(isDrawPower ? this.getResources().getString(R.string.use_paint) : this.getResources().getString(R.string.no_use_paint));
//            ToastUtil.show(LiveSmallNativeActivity.this, isDrawPower ? this.getResources().getString(R.string.use_paint) : this.getResources().getString(R.string.no_use_paint));
            setPaintSLVisibility(isDrawPower ? View.VISIBLE : View.GONE);
            if (isDrawPower) {
                paintSL.open();
                paintIV.setRotation(0);
                gestureLayoutWrapper.setMaskEnable(false);
            }
            dynamicLayout(isDrawPower);
            initPop(isDrawPower);
        } else if (isDrawPower) {
            isDrawPower = false;
            setPaintSLVisibility(isDrawPower ? View.VISIBLE : View.GONE);
            dynamicLayout(isDrawPower);
        }
    }

    /**
     * 初始化涂鸦操作的弹框
     *
     * @param isDraw
     */
    private void initPop(boolean isDraw) {
        if (isDraw) {
            if (mColorViewPopManager == null) {
                mColorViewPopManager = new ColorViewPopManager(this);
            }
            if (mDrawAndStrokePopManager == null) {
                mDrawAndStrokePopManager = new DrawAndStrokePopManager(this);
                if (eraserIsSelect) {
                    mDrawAndStrokePopManager.setEraser(eraserIsSelect);
                }
            }
            if (isReload) {
                mColorViewPopManager.setColor();
                mDrawAndStrokePopManager.setDrawTypeAndStroke();
                if (eraserIsSelect) {
                    mDrawAndStrokePopManager.setEraser(eraserIsSelect);
                }
            }
            stopDismissTitleBar();
        }
    }

    @Override
    public void onLaunch() {
        if (mHtSdk != null && LiveStatus.STOP.equals(mHtSdk.getInitLiveStatus())) {
            isReload = false;
            reset();
            hideController();
        }
    }

    @Override
    public void onInitFail(String msg) {
//        if (loadFailTv != null) {
//            if (TextUtils.isEmpty(msg)) {
//                loadFailTv.setText(this.getResources().getString(R.string.load_fail_default_tip));
//            } else {
//                loadFailTv.setText(msg);
//            }
//        }
    }

    @Override
    public void onLiveStart() {
        isLiving = true;
        showController();
        chatFab.setVisibility(View.VISIBLE);
        mRtcOperatorProxy = HtSdk.getInstance().getRtcOperatorProxy();
        setTitle();
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        String title = mHtSdk.getRoomInfo().getLiveTitle();
        titleTV.setVisibility(View.VISIBLE);
        titleTV.setText(title);

    }

    @Override
    public void onLiveStop() {
        isReload = false;
        reset();
        hideController();
    }

    @Override
    public void memberForceout() {
        String reason = getResources().getString(R.string.member_forceout);
        memberOut(reason);
    }

    @Override
    public void memberKick() {
        String reason = getResources().getString(R.string.member_kick);
        memberOut(reason);
    }


    /**
     * 设置video（主播关闭时不可申请）
     */
    private void setIsCloseVideoForZhubo(boolean isCloseVideoForZhubo) {
        this.isCloseVideoForZhubo = isCloseVideoForZhubo;
    }

    /**
     * 设置audio（主播关闭时不可点开）
     */
    private void setIsCloseAudioForZhubo(boolean isCloseAudioForZhubo) {
        this.isCloseAudioForZhubo = isCloseAudioForZhubo;
    }


    /**
     * 用户主动关闭摄像头和音频时，对视频区域的刷新
     *
     * @param result
     * @param type
     */
    private void updateVideoLayout(RtcUserEntity result, int type) {
        if (mVideoMap != null && result != null && mVideoMap.containsKey(result.getXid())) {
            VideoStatusData videoStatusData = mVideoMap.get(result.getXid());
            if (videoStatusData == null) {
                return;
            }
//            if (type == VideoAdapter.VIDEO) {
//                videoStatusData.getRtcUserEntity().setVideo(job);
//            } else {
//                videoStatusData.getRtcUserEntity().setAudio(job);
//            }
            videoAdapter.updateItemOfPart(type, videoStatusData);
        }
    }

    /**
     * 更新视频区域
     *
     * @param type
     * @param rtcUserEntity
     */
    private void updateVideoLayout(int type, RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity != null && mVideoMap != null && mVideoMap.containsKey(rtcUserEntity.getXid())) {
            VideoStatusData videoStatusData = mVideoMap.get(rtcUserEntity.getXid());
            if (videoStatusData == null || videoAdapter == null)
                return;
            videoAdapter.updateItemOfPart(type, videoStatusData);
        }
    }


    /**
     * 讲台状态相关操作
     *
     * @param platformStatus
     */
    private void setPlatformStatus(String platformStatus) {
        switch (platformStatus) {
            case PlatformType.START://开启上讲台
                platformIV.setVisibility(View.VISIBLE);
                platformIV.setIsRotate(false);
                downPlatformIV.setVisibility(View.GONE);
                controllContainer.setVisibility(View.VISIBLE);
                stopDismissTitleBar();
                break;
            case PlatformType.APPLYING://申请中
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
                platformIV.setIsRotate(true);
                break;
            case PlatformType.ALLOW://已经上讲台
                downPlatformIV.setVisibility(View.VISIBLE);
                platformIV.setIsRotate(false);
                platformIV.setVisibility(View.GONE);

                break;
            case PlatformType.CLOSE://关闭讲台
                downPlatformIV.setVisibility(View.GONE);
                platformIV.setIsRotate(false);
                platformIV.setVisibility(View.GONE);
                setVideoVisibility(View.GONE);
                setAudioVisibility(View.GONE);
                setPaintSLVisibility(View.GONE);
                dynamicLayout(false);
                isDrawPower = false;
                break;
            case PlatformType.CANCLE://取消上讲台
                downPlatformIV.setVisibility(View.GONE);
                platformIV.setVisibility(View.VISIBLE);
                platformIV.setIsRotate(false);
                break;
            case PlatformType.KICK://踢下讲台
            case PlatformType.DOWN://主动下讲台
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
                setVideoVisibility(View.GONE);
                setAudioVisibility(View.GONE);
                setPaintSLVisibility(View.GONE);
                dynamicLayout(false);
                eraserIsSelect = false;
                setDefault();
                break;
            default:
                break;
        }
    }

    /**
     * Event监听
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCallback(Event message) {
        switch (message.getType()) {
            case EventType.SMALL_ROOM_POP_COLOR:
                colorCV.setColor((Integer) message.getData());
                break;
            case EventType.SMALL_ROOM_POP_CMD:
                cmdIV.setBackgroundResource((Integer) message.getData());
                break;
            case EventType.SMALL_ROOM_POP_STROKE:
                strokeCV.setCheckedRingWidthPercent((float) message.getData());
                break;
            case EventType.NETWORK_STATE_CHANGE://网络监听
                int netStatus = (int) message.getData();
                network(netStatus);
                break;
        }
    }


    /**
     * 设置视频控件开关
     */
    private void setVideoSwitch(boolean enable) {
        setVideoVisibility(View.VISIBLE);
        videoSwitchIV.setSelected(enable);
    }

    /**
     * 设置音频控件开关
     */
    private void setAudioSwitch(boolean enable) {
        setAudioVisibility(View.VISIBLE);
        audioSwitchIV.setSelected(enable);
    }

    /**
     * 设置视频控件显隐
     */
    private void setVideoVisibility(int visibility) {
        if (videoSwitchIV != null) {
            videoSwitchIV.setVisibility(visibility);
        }
    }

    /**
     * 设置音频控件显隐
     */
    private void setAudioVisibility(int visibility) {
        if (audioSwitchIV != null) {
            audioSwitchIV.setVisibility(visibility);
        }

    }

    /**
     * 设置画笔显隐
     *
     * @param visibility
     */
    private void setPaintSLVisibility(int visibility) {
        paintSL.setVisibility(visibility);
    }

    /**
     * 动态改变扇形控件和 操作栏之间的位置
     *
     * @param isDraw
     */
    private void dynamicLayout(boolean isDraw) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (isDraw && !DimensionUtils.isPad(this)) {
            params.addRule(RelativeLayout.ABOVE, R.id.paint_sl);
        } else {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        }
        controllContainer.setLayoutParams(params);
    }


    /**
     * 主动下讲台的操作
     */
    private void downPlatform() {
        if (mRtcOperatorProxy != null) {
            mRtcOperatorProxy.down(new Callback<RtcUserEntity>() {
                @Override
                public void success(RtcUserEntity result) {
                    setVideoContainerRVGone();
                    setPlatformStatus(PlatformType.DOWN);
//                    ToastUtil.show(LiveRtcNativeActivity.this, LiveRtcNativeActivity.this.getResources().getString(R.string.down_platform));
                    showToast(LiveMixNativeActivity.this.getResources().getString(R.string.down_platform));
                }

                @Override
                public void failed(String failed) {
//                    ToastUtil.show(LiveSmallNativeActivity.this, failed);
                    showToast(failed);
                }
            });

        }
    }

    /**
     * 隐藏rtc 视频容器
     */
    private void setVideoContainerRVGone() {
        mVideoMap.clear();
        videoAdapter.clear();
        videoContainerRV.removeAllViews();
        videoContainerRV.setVisibility(View.GONE);
    }


    /**
     * 主动上讲台
     */
    private void applyPlatform() {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.apply(new Callback<String>() {
            @Override
            public void success(String result) {
                setPlatformStatus(PlatformType.APPLYING);
                showToast(getString(R.string.apply_platform));
            }

            @Override
            public void failed(String failed) {
                showToast(failed);
            }
        });
    }

    /**
     * 取消上讲台
     */
    private void canclePlatform() {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.cancel(new Callback() {
            @Override
            public void success(Object result) {
                setPlatformStatus(PlatformType.CANCLE);
                showToast(getString(R.string.cancle_platform));
            }

            @Override
            public void failed(String failed) {
                showToast(failed);
            }
        });
    }


    private ScheduledExecutorService lance;
    private boolean isTitleBarShow = false;

    /**
     * 显示标题栏和操作按钮
     */
//    private void showTitleBarAndOprationBar() {
//        if (lance != null && !lance.isShutdown())
//            lance.shutdown();
//        showController();
//        autoDismissTitleBar();
//    }
    private void showController() {
        if (isLiving) {
            controllContainer.setVisibility(View.VISIBLE);
        }
        titlebarContainer.setVisibility(View.VISIBLE);
        if (isDrawPower) {
            paintSL.setVisibility(View.VISIBLE);

        }
        isTitleBarShow = true;
    }

    private void hideController() {
        controllContainer.setVisibility(View.GONE);
        titlebarContainer.setVisibility(View.GONE);
        paintSL.setVisibility(View.GONE);
        isTitleBarShow = false;
    }

    //标题栏计时器. 3秒后自动隐藏
    protected void autoDismissTitleBar() {
        Runnable sendBeatRunnable = () -> {
            if (isTitleBarShow) {
                if (lance != null && !lance.isShutdown()) {
                    runOnUiThread(() -> {
                        if (isTitleBarShow) {
                            hideController();
                        }
                    });
                }
            }
        };

        lance = Executors.newSingleThreadScheduledExecutor();
        lance.scheduleAtFixedRate(sendBeatRunnable, 5, 5, TimeUnit.SECONDS);
    }

    protected void stopDismissTitleBar() {
//        if (lance != null) {
//            if (!lance.isShutdown()) {
//                lance.shutdown();
//            }
//            lance = null;
//        }
    }

    /**
     * @param totalTime 播放总时长
     */
    @Override
    public void onTime(int totalTime) {
        if (cuurentPlayTotalTimeTV != null) {
            cuurentPlayTotalTimeTV.setText(TimeUtil.displayHHMMSS(totalTime));
        }
    }

    //------------------------------------生命周期------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        mHtSdk.onResume();
        registerNetWorkStateReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
        HtSdk.getInstance().onPause();
        unRegisterNetWorkStateReceiver();

    }


    @Override
    protected void onDestroy() {
        unbinder.unbind();
        EventBusUtil.unregister(this);
        stopDismissTitleBar();
        ToastUtil.cancel();
        mHtSdk.release();
        removeHandler();
        mOnRtcStatusListener = null;
        mOnRtcMemberListener = null;
        mOnRtcErrorListener = null;
        mOnRtcMediaStatusListener = null;
        mOnWhiteboardPowerListenter = null;
        mRtcOperatorProxy = null;
        handler = null;
        mVideoMap.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }


    //-------------------------------------------------网络相关-----------------------------------------------------------------------
    private boolean isNetworkAvailable = true;
    private long noNetWorkTime = 0;

    /**
     * 网络相关
     *
     * @param netStatus
     */
    private void network(int netStatus) {
        if (netStatus == NetMonitor.NETWORK_NONE) {
            isNetworkAvailable = false;
            noNetWorkTime = System.currentTimeMillis();
        } else {
            if (!isNetworkAvailable && System.currentTimeMillis() - noNetWorkTime > 2 * 1000) {//断网超过2秒就重新加载
                if (socketFailTips != null && socketFailTips.isShowing()) {
                    socketFailTips.dismiss();
                }
                reload();
            }
            isNetworkAvailable = true;
        }
        badNetStatusLL.setVisibility(netStatus == NetMonitor.NETWORK_NONE ? View.VISIBLE : View.GONE);
        if (netStatus == NetMonitor.NETWORK_MOBILE) {//4G 提示
            AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.mobile_connect), null);
        }
    }

    private void reload() {
        if (isDrawPower) {
            isReload = true;
        }
        reset();
        hideController();
        if (mHtSdk != null) {
            mHtSdk.reload();
        }
    }

    protected void registerNetWorkStateReceiver() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netWorkStateReceiver, intentFilter);
    }

    protected void unRegisterNetWorkStateReceiver() {
        if (netWorkStateReceiver == null)
            return;
        this.unregisterReceiver(netWorkStateReceiver);
    }
    //--------------------------------------------各种弹框--------------------------------------------------------------------------

    /**
     * 下讲台的弹框
     */
    private void showDownPlatformDialog() {
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.down_platform_tip),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> downPlatform());
    }

    /**
     * 取消讲台弹框
     */
    private void showCanclePlatformDialog() {
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.cancle_platform_tip),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> canclePlatform());
    }

    /**
     * 上讲台弹框
     */
    private void showApplyPlatformDialog() {
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.apply_platform_tip),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> applyPlatform());
    }


    /**
     * 退出的弹框
     */
    private void showExitDialog() {

        if (isNetworkAvailable && applyStatus() == ApplyStatus.ALLOW) {
            AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.exit_before_tip),
                    getResources().getString(R.string.confirm), null, null);
            return;
        }
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.exit),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> {
                    stopDismissTitleBar();
                    finish();
                });
    }

    //用户退出
    private void memberOut(String reason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(reason);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), (dialog, which) -> {
            dialog.dismiss();
            LiveMixNativeActivity.this.finish();
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 显示特定位置的Toast
     *
     * @param msg
     */
    private void showToast(String msg) {
        if (!isFront) {
            return;
        }
        ToastUtil.show(this, msg, videoContainerRV != null && videoContainerRV.getVisibility() == View.VISIBLE ? videoWidth / 2 : 0);
    }

    private void reset() {
        isOpen = false;
        setDefault();
        isLiving = false;
        setVideoContainerRVGone();
        if (chatPopManager != null && chatPopManager.isShow()) {
            chatPopManager.dismiss();
        }
        setPlatformStatus(PlatformType.CLOSE);
    }

    private void setDefault() {

        isDrawPower = false;
        isCloseAudioForZhubo = false;
        isCloseVideoForZhubo = false;
    }

    //TODO-----------------------------------视频播放切换事件监听------------------------------------

    /**
     * 视频播放切换事件监听
     */
    class OnPlayVideoChangeLister implements OnVideoChangeListener {
        /**
         * 视频开始播放
         *
         * @param mode 视频类型（VideoModeType.CAMERA_MODE：摄像头类型视频;VideoModeType.DESKTOP_MODE：桌面分享类型视频)
         */
        @Override
        public void onVideoStart(int mode) {
        }

        /**
         * 视频停止播放
         *
         * @param mode 视频类型（VideoModeType.CAMERA_MODE：摄像头类型视频;VideoModeType.DESKTOP_MODE：桌面分享类型视频)
         */
        @Override
        public void onVideoStop(int mode) {
        }

        /**
         * 视频播放模式类型切换
         *
         * @param beforeMode  切换前类型
         * @param currentMode 切换后类型
         *                    视频类型（VideoModeType.CAMERA_MODE：摄像头类型视频;VideoModeType.DESKTOP_MODE：桌面分享类型视频)
         */
        @Override
        public void onVideoModeChanging(int beforeMode, int currentMode) {
            if (mSwitchModePopManager == null) {
                mSwitchModePopManager = new SwitchModePopManager(LiveMixNativeActivity.this);
            }
            removeHandler();
            showVideoContainer(currentMode == VideoModeType.CAMERA_MODE);
            platformIVStatus(currentMode);
            mSwitchModePopManager.setMode(currentMode);
            mSwitchModePopManager.show(base_container);
            if (currentMode == VideoModeType.CAMERA_MODE && beforeMode == VideoModeType.RTC_MODE) {
                setVideoContainerRVGone();
            }
            delayDismiss();
        }

        /**
         * 视频切换完成
         */
        @Override
        public void onVideoModeChanged() {

        }

        /**
         * 摄像头视频显示
         */
        @Override
        public void onCameraShow() {
            showVideoContainer(true);
        }

        /**
         * 摄像头视频隐藏
         */
        @Override
        public void onCameraHide() {
        }

    }

    private void removeHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void delayDismiss() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(0, 1200);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && mSwitchModePopManager != null) {
                mSwitchModePopManager.dismiss();
            }
        }
    };

    protected void showVideoContainer(boolean isShow) {
        if (videoLayout != null) {
            videoLayout.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private boolean isOpen;

    private void platformIVStatus(int currentMode) {
        if (currentMode == VideoModeType.DESKTOP_MODE) {
            platformIV.setVisibility(View.GONE);
            downPlatformIV.setVisibility(View.GONE);
        } else {
            if (applyStatus() == ApplyStatus.ALLOW) {
                platformIV.setVisibility(View.GONE);
                downPlatformIV.setVisibility(View.VISIBLE);
            } else if (applyStatus() == ApplyStatus.APPLYING) {
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
            } else {
                platformIV.setVisibility(isOpen ? View.VISIBLE : View.GONE);
                downPlatformIV.setVisibility(View.GONE);
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
                    if (videoLayout != null && layoutParams != null) {
                        videoLayout.setLayoutParams(isFull ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) : layoutParams);
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
//        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || orientation == -1) {
//            height -= statusBarHeight;
//            y -= statusBarHeight;
//        }

        x = Math.min(Math.max(0, x), width - videoLayout.getWidth());
        y = Math.min(Math.max(0, y), height - videoLayout.getHeight());

        ViewUtil.setViewXY(videoLayout, x, y);
//        layoutParams = (FrameLayout.LayoutParams) videoViewContainer.getLayoutParams();
    }

}
