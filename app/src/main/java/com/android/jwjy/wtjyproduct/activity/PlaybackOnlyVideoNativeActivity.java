package com.android.jwjy.wtjyproduct.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.consts.MainConsts;
import com.android.jwjy.wtjyproduct.event.SimpleGestureLayoutListener;
import com.android.jwjy.wtjyproduct.fragment.PlaybackSectionFragment;
import com.android.jwjy.wtjyproduct.helper.AudioManagerHelper;
import com.android.jwjy.wtjyproduct.helper.NetChoiseDiologHelper;
import com.android.jwjy.wtjyproduct.util.ACache;
import com.android.jwjy.wtjyproduct.util.GestureLayoutWrapper;
import com.android.jwjy.wtjyproduct.util.LogUtil;
import com.android.jwjy.wtjyproduct.util.SeekBarHelper;
import com.android.jwjy.wtjyproduct.util.StringUtils;
import com.android.jwjy.wtjyproduct.util.TimeUtil;
import com.android.jwjy.wtjyproduct.view.MultipleStatusLayout;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.PlayerLoadState;
import com.talkfun.sdk.consts.TFMode;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.ErrorEvent;
import com.talkfun.sdk.event.OnVideoStatusChangeListener;
import com.talkfun.sdk.event.PlaybackListener;
import com.talkfun.sdk.module.AlbumItemEntity;
import com.talkfun.sdk.module.ModuleConfigHelper;
import com.talkfun.sdk.module.PlaybackInfo;
import com.talkfun.sdk.module.User;
import com.talkfun.utils.PreventRepeatedUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 纯视频点播
 */
public class PlaybackOnlyVideoNativeActivity extends BasePlayActivity implements
        PlaybackListener, PlaybackSectionFragment.PlaybackSectionInterface, View.OnTouchListener
        , OnVideoStatusChangeListener, ErrorEvent.OnErrorListener {
    //-------------------------------ui---------------------------------
    @BindView(R.id.operation_btn_container)
    LinearLayout operationContainer;
    @BindView(R.id.video_visibility_iv)
    ImageView videoVisibleIv;
    @BindView(R.id.seek_bar_layout)
    LinearLayout seekbarLayout;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.iv_play)
    ImageView playBtn;
    @BindView(R.id.total_duration)
    TextView totalDuration;
    @BindView(R.id.current_duration)
    TextView currentDuration;
    @BindView(R.id.iv_go_back)
    ImageView goBack;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;

    @BindView(R.id.tv_speed)
    TextView tvSpeed;

    @BindView(R.id.iv_danmu_switch)
    ImageView ivDanmuSwitch;
    @BindView(R.id.exchange)
    ImageView ivExchange;
    @BindView(R.id.fullScreen_iv)
    ImageView ivFullScreen;
    @BindView(R.id.parent_Layout)
    RelativeLayout parentLayout;
    @BindView(R.id.status_layout)
    MultipleStatusLayout multipleStatusLayout;
    //--------------------------------------------------------------------------------

    private boolean mIsPlaying = true;
    private String mId;
    private SeekBarHelper seekBarHelper;
    private static final String TAG = PlaybackOnlyVideoNativeActivity.class.getName();
    private HtSdk mHtSdk;
    private NetChoiseDiologHelper mNetChoiseDiologHelper; //网络选择弹框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.playback_only_video_layout;
    }

    @Override
    protected void init() {
        super.init();
        mId = getIntent().getStringExtra("id");
        MainConsts.PlayBackID = mId;

    }

    /**
     * 初始化布局
     */
    protected void initView() {
        videoViewContainer = findViewById(R.id.video_container);
        linearContainer = findViewById(R.id.play_container);
        gestureLayoutWrapper = new GestureLayoutWrapper(this, parentLayout);
        audioManagerHelper = new AudioManagerHelper(this);
        showVideoContainer(videoVisibleIv, false);
        operationContainer.bringToFront();  //将控件移动到前面
        seekbarLayout.bringToFront();
        goBack.bringToFront();

        hideTitleBar();  // 隐藏标题栏和操作按钮
        updateLayout();
        seekBarHelper = new SeekBarHelper(this, seekBar);
        mHtSdk = HtSdk.getInstance();
        mHtSdk.init(this,null, videoViewContainer, mToken, TFMode.PLAYBACK_NORMAL);
        //启动本地服务器和播放设置
        //如果为true,在连网情况下，如果点播下载完成播放本地点播，未下载或下载未完成则播放网络点播，如果本地下载完成断网播放本地点播
        //如果为false,在连网情况下播放网络点播，如果本地下载完成断网播放本地点播
        //默认为true
        mHtSdk.setIsPlayOffline(mId, true);
        goneView();
    }

    private void goneView() {
        gone(ivDanmuSwitch, videoVisibleIv, ivExchange, ivFullScreen, tvSpeed);
    }

    private void gone(View... view) {
        for (View view1 : view) {
            view1.setVisibility(View.GONE);
        }
    }

    /**
     * 监听初始化
     */
    private void initEvent() {
        //点播必须初始化的接口
        mHtSdk.setPlaybackListener(this);
        //视频播放状态改变的监听回调
        mHtSdk.setOnVideoStatusChangeListener(this);
        //错误监听
        mHtSdk.setOnErrorListener(this);
        //添加快进快退的滑动监听事件
        //seekBarHelper.addTouchSlidSeekEvent(pptLayout);

        /***
         * 获取缓冲状态
         */
        mHtSdk.setOnPlayerLoadStateChangeListener(loadState -> {
            if (loadState == PlayerLoadState.MEDIA_INFO_BUFFERING_START) {
                Log.d(TAG, "缓冲开始");
            } else if (loadState == PlayerLoadState.MEDIA_INFO_BUFFERING_END) {
                Log.d(TAG, "缓冲结束");
            }
        });

        mHtSdk.setUpdatePlayTimeListener(time -> {
            if(seekBarHelper != null){
                seekBarHelper.updatePlayTime(time);
            }
        });

        gestureLayoutWrapper.setGestureLayoutListener(new SimpleGestureLayoutListener() {
            @Override
            public void onStartVolumeOffset() {
                audioManagerHelper.startVolumeOffset();
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
            public void onStartFastSeekOffset() {
                seekBarHelper.startFastSeek(videoViewContainer);
            }

            @Override
            public void onFastSeekOffset(float offsetPercentage) {
                seekBarHelper.fastSeekOffset(offsetPercentage);
            }

            @Override
            public void onStopFastSeekOffset() {
                seekBarHelper.stopFastSeek();
            }

            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onClick() {
                if (seekBarHelper.isShowPoped) {
                    seekBarHelper.isShowPoped = false;
                    return true;
                }
                if (isTitleBarShow) {
                    hideTitleBar();
                } else {
                    showTitleBar();
                }
                return true;
            }

            @Override
            public boolean onDoubleClick() {
//                onFullScreenChange();
                return true;
            }
        });
    }

    @OnClick({  R.id.iv_play, R.id.iv_go_back,R.id.network_choice_iv, R.id.iv_refresh})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:  //开始或暂停
                if (mIsPlaying) {
                    pause();
                    mIsPlaying = false;
                } else {
                    play();
                    mIsPlaying = true;
                }
                break;
            case R.id.iv_go_back:  //返回
                gobackAction();
                break;
            case R.id.network_choice_iv: //切换视频路线
                if (mNetChoiseDiologHelper == null) {
                    mNetChoiseDiologHelper = new NetChoiseDiologHelper(PlaybackOnlyVideoNativeActivity.this);
                }
                mNetChoiseDiologHelper.showNetworkChoiceDialog();
                break;
            case R.id.iv_refresh: //刷新
                if (PreventRepeatedUtil.canClickable("iv_refresh")) {
                    cachePlayTime();
                    mHtSdk.reload();
                    multipleStatusLayout.showLoading();
                }
                break;
            default:
                break;
        }
    }
    /**
     * 显示控制按钮和进度条
     */
    @Override
    void showController() {
        seekbarLayout.setVisibility(View.VISIBLE);
        operationContainer.setVisibility(View.VISIBLE);
        titleBar.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏控制按钮和进度条
     */
    @Override
    void hideController() {
        if (seekbarLayout == null)
            return;
        seekbarLayout.setVisibility(View.INVISIBLE);
        operationContainer.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
        playBtn.setVisibility(View.GONE);
    }


    //TODO-------------------------------------------点播初始化以及状态提示------------------------------
    /**
     * 点播初始化完成回调
     */
    @Override
    public void initSuccess() {
        multipleStatusLayout.showContent();
        showTitleBar();
        setSeekBar();

        ModuleConfigHelper moduleConfigHelper = mHtSdk.getModuleConfigHelper();
        if (moduleConfigHelper != null && moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_THEFTPROOF_PLAYBACK)) {
            User user = PlaybackInfo.getInstance().getUser();
            startShowWatermark(parentLayout, user != null ? user.getUid() : PlaybackInfo.getInstance().getLiveId());
        }

        Long playTime = getCachePlayTime();
        if (playTime > 0) {
            mHtSdk.playbackSeekTo(playTime);
        }

    }


    private void setSeekBar() {
        seekBar.setClickable(true);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        long duration = PlaybackInfo.getInstance().getDurationLong();

        seekBar.setMax((int) duration);
        totalDuration.setText(TimeUtil.displayHHMMSS((int) duration));
    }

    /**
     * 初始化失败
     *
     * @param msg
     */
    @Override
    public void onInitFail(String msg) {
        multipleStatusLayout.showError();
        Log.d(TAG, "onInitFail: msg");
    }

    protected void registerNetWorkStateReceiver() {
        //如果是离线播放本地回放，不需要网络状态监测
        if (mHtSdk != null && mHtSdk.isPlayLocal()) {
            return;
        }
        super.registerNetWorkStateReceiver();
    }

    //TODO-----------------------------------------activity life------------------------------------------------
    @Override
    public void onBackPressed() {
        gobackAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        multipleStatusLayout.showLoading();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHtSdk.onPause();
    }

    @Override
    protected void onDestroy() {
        cachePlayTime();
        super.onDestroy();
        onSeekBarChangeListener = null;

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    //TODO--------------------------------------------视频播放状态-----------------------------------------

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentDuration.setText(TimeUtil.displayHHMMSS(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            seekTo(progress);
        }
    };


    private void play() {
        setPlayingStatus();
        mHtSdk.playbackResume();
    }

    private void pause() {
        setPauseStatus();
        mHtSdk.playbackPause();
    }

    private void setPlayingStatus() {
        playBtn.setImageResource(R.mipmap.pause);
        mIsPlaying = true;
    }

    private void setPauseStatus() {
        playBtn.setImageResource(R.mipmap.play);
        mIsPlaying = false;
    }

    private void setStopStatus() {
        setPauseStatus();
        seekBarHelper.resetSeekBarProgress();
    }

    /**
     * 跳转到指定时间点
     */
    @Override
    public void seekTo(int progress) {
        seekBarHelper.seekTo(progress);
    }

    /**
     * 视频播放状态
     *
     * @param status 当前状态
     * @param msg    信息
     */
    @Override
    public void onVideoStatusChange(int status, String msg) {
        switch (status) {
            case OnVideoStatusChangeListener.STATUS_PAUSE:
                setPauseStatus();
                break;
            case OnVideoStatusChangeListener.STATUS_PLAYING:
                setPlayingStatus();
                break;
            case OnVideoStatusChangeListener.STATUS_ERROR:
                StringUtils.tip(getApplicationContext(), msg);
                break;
            case OnVideoStatusChangeListener.STATUS_IDLE:
                setStopStatus();
                break;
            case OnVideoStatusChangeListener.STATUS_COMPLETED:
                Log.d(TAG, "completed");
                // 播放完毕后重新播放
                if (PlaybackInfo.getInstance().isAlbum()) {
                    int currentIndex = PlaybackInfo.getInstance().getCurrentAlbumIndex();
                    List<AlbumItemEntity> albumItemEntities = PlaybackDataManage.getInstance().getAlbumList();
                    if ((albumItemEntities.size() <= 1) || (currentIndex >= albumItemEntities.size() - 1)) {
                        currentIndex = 0;
                    } else {
                        currentIndex++;
                    }
                    seekBarHelper.resetSeekBarProgress();
                    mHtSdk.playAlbum(albumItemEntities.get(currentIndex));
                    return;
                }
                mHtSdk.replayVideo();
                seekBarHelper.resetSeekBarProgress();
                break;
        }
    }

    @Override
    public void error(int code, String msg) {
        Toast.makeText(this, code + "------>>" + msg, Toast.LENGTH_SHORT).show();
        LogUtil.e("failed:", code + "------>>" + msg);
    }




  /*  private static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
    private static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量*/

    static final int CACHE_VOD_PLAY_TIME_MAX_COUNT = 100;
    static final int CACHE_VOD_PLAY_TIME_MAX_SIZE = 1000 * 1000 * 50; //50mb
    static final String CACHE_VOD_PLAY_TIME_FILE_NAME = "vod_cache_play_time";
    ACache aCache;

    /**
     * 获取缓存播放时间
     *
     * @return
     */
    public long getCachePlayTime() {
        String id = mHtSdk.getPlaybackInfo().getLiveId();
        ensureACache();
        return StringUtils.getLong(aCache.getAsString(id), 0);
    }

    /**
     * 缓存播放时间
     */
    public void cachePlayTime() {
        ensureACache();
        String id = mHtSdk.getPlaybackInfo().getLiveId();
        long time = mHtSdk.getVideoCurrentTime();
        aCache.put(id, String.valueOf(time));
    }


    private void ensureACache() {
        if (aCache == null) {
            File f = new File(getCacheDir(), CACHE_VOD_PLAY_TIME_FILE_NAME);
            aCache = ACache.get(f, CACHE_VOD_PLAY_TIME_MAX_SIZE, CACHE_VOD_PLAY_TIME_MAX_COUNT);
        }
    }

}
