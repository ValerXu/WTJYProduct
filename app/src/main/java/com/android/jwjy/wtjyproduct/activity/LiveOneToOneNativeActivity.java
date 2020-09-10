package com.android.jwjy.wtjyproduct.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cpiz.android.bubbleview.BubbleStyle;
import com.jakewharton.rxbinding.view.RxView;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.adapter.OTOChatAdapter;
import com.android.jwjy.wtjyproduct.adapter.OTOVideoAdapter;
import com.android.jwjy.wtjyproduct.bean.ViewModelEvent;
import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.databinding.ActivityLiveOneToOneNativeBinding;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.helper.GuideHelper;
import com.android.jwjy.wtjyproduct.manager.RTCPanelManager;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.TimeUtil;
import com.android.jwjy.wtjyproduct.util.ToastUtil;
import com.android.jwjy.wtjyproduct.view.InputMsgDialog;
import com.android.jwjy.wtjyproduct.view.OTOChatPopView;
import com.android.jwjy.wtjyproduct.view.RecycleViewDivider;
import com.android.jwjy.wtjyproduct.viewmodel.BaseLiveRtcViewModel;
import com.android.jwjy.wtjyproduct.viewmodel.LiveOneToMultiViewModel;
import com.android.jwjy.wtjyproduct.viewmodel.LiveOneToOneViewModel;
import com.talkfun.sdk.consts.DocType;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.consts.TFMode;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.OnMultiMediaStatusChangeListener;
import com.talkfun.sdk.log.TalkFunLogger;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.rtc.consts.MediaStatus;
import com.talkfun.utils.NetMonitor;
import com.talkfun.utils.PreventRepeatedUtil;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import q.rorbin.badgeview.QBadgeView;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 小班1V1
 */
public class LiveOneToOneNativeActivity extends BaseLiveRtcActivity<ActivityLiveOneToOneNativeBinding, LiveOneToOneViewModel> implements LiveOneToOneViewModel.OnVideoDataChangeListener,
        OTOVideoAdapter.MediaSwitchListener, BaseLiveRtcViewModel.OnTimeListener, OnMultiMediaStatusChangeListener {
    private int videoWidth;
    private OTOChatAdapter mOTOChatAdapter;
    private boolean isSelectEraser;
    long noNetWorkTime;
    private RTCPanelManager mRTCPanelManager;

    private OTOChatPopView mOTOChatPopView;
    private QBadgeView mQBadgeView;
    private int toastXOffset = 50;
    private Handler handler = new Handler();

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    protected LiveOneToOneViewModel initViewModel() {
        return new LiveOneToOneViewModel(this.getApplication());
    }

    @Override
    public void initParam() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void initData() {
        String accessToken = getIntent().getStringExtra("token");
        baseViewModel.initSdk(accessToken, mDatabinding.whiteboardFl, TFMode.LIVE_RTC);
        baseViewModel.setOnVideoDataChangeListener(this);
        baseViewModel.setDesktopVideoContainer(mDatabinding.flDesktopLayout);
        baseViewModel.setMultiMediaViewContainer(mDatabinding.multimediaLayout.flMultiMedia);
        baseViewModel.setOnMultiMediaStatusChangeListener(this);
        baseViewModel.setOnTimeListener(this);
        baseViewModel.getLiveData().observe(this, liveObserver);
    }

    private Observer liveObserver = new Observer<ViewModelEvent>() {
        @Override
        public void onChanged(@Nullable ViewModelEvent viewModelEvent) {
            if (viewModelEvent == null) {
                return;
            }
            Object msg = viewModelEvent.getObject();
            switch (viewModelEvent.getType()) {
                case LiveOneToOneViewModel.RECEIVER_CHAT:
                    receiverChatEntity((ChatEntity) msg);
                    break;
                case LiveOneToOneViewModel.LIVE_WAIT:
                    hideMultipleStatusLayout();
                    break;
                case LiveOneToOneViewModel.LIVE_START:
                    titleAndTimeVisibility(View.VISIBLE);
                    hideMultipleStatusLayout();
                    setMultiMediaViewVisiable(View.GONE);
                    mDatabinding.toolBar.titleTv.setText(baseViewModel.getLiveTitle());
                    break;
                case LiveOneToOneViewModel.LIVE_STOP:
                    reset();
                    titleAndTimeVisibility(View.GONE);
                    setMultiMediaViewVisiable(View.GONE);
                    if (isMuitiMediaFullScreen) {
                        multiMediaViewFullScreen(false);
                    }
                    hideMultipleStatusLayout();
                    drawVisibility(false);
                    if (mRTCPanelManager != null)
                        mRTCPanelManager.dismiss();
                    break;
//                case LiveOneToOneViewModel.LIVE_TIME:
//                    setLiveTime(msg);
//                    break;
                case LiveOneToOneViewModel.LIVE_DRAW:
                    isDraw = (int) msg == 1;
                    if (isDraw) {
                        initOTODrawManager();
                    }
                    drawTip(isDraw);
                    if (isApplicateMuitiMedia) {
                        return;
                    }
                    drawVisibility(isDraw);
                    break;
                case LiveOneToOneViewModel.LIVE_FAIL:
                    showFail(msg.toString());
                    break;
                case LiveOneToOneViewModel.AUDIO_ENABLE:
                    if (msg != null)
                        audioTips((int) msg);
                    break;
                case LiveOneToOneViewModel.VIDEO_ENABLE:
                    if (msg != null)
                        videoTips((int) msg);
                    break;
                case LiveOneToOneViewModel.ERROR_MESSAGE:
                    if (msg != null)
                        errorTips((String) msg);
                    break;
                case LiveOneToOneViewModel.LIVE_SOCKET_CONNNECT_FAIL:
                    socketConnectFail();
                    break;
                case LiveOneToOneViewModel.MEMBER_KICK:
                    memberKick();
                    break;
                case LiveOneToOneViewModel.MEMBER_FORCEOUT:
                    memberForceout();
                    break;
                case LiveOneToMultiViewModel.RTC_DESTOP_MODE:
                    if (isMuitiMediaFullScreen) {
                        multiMediaViewFullScreen(false);
                    }
                    break;
            }
        }
    };

    /**
     * socket连接失败
     */
    private void socketConnectFail() {
        if (!NetMonitor.isNetworkAvailable(LiveOneToOneNativeActivity.this)) {
            return;
        }
        if (LiveOneToOneNativeActivity.this.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(LiveOneToOneNativeActivity.this);
        builder.setMessage("服务器连接失败，请检测网络是否正常，或重新刷新试试");
        builder.setTitle(R.string.tips);
        builder.setPositiveButton(R.string.refresh, (dialog, which) -> {
            if (baseViewModel != null) {
                reset();
                baseViewModel.reload();
            }
        }).setNegativeButton(R.string.goback, (dialog, which) -> LiveOneToOneNativeActivity.this.finish());
        AlertDialog socketFailTips = builder.create();
        socketFailTips.setCancelable(false);
        socketFailTips.show();
        return;
    }


    private void errorTips(String msg) {
        this.showToastShort(msg);
    }

    private void videoTips(int msg) {
        this.showToastShort(msg == 0 ? "你已被关闭摄像头了!" : "你可以使用摄像头了!");
    }

    private void audioTips(int i) {
        this.showToastShort(i == 0 ? "你已被禁止说话了!" : "你可以开始说话了!");
    }

    /**
     * 直播失败
     *
     * @param msg
     */
    private void showFail(String msg) {
        if (msg.contains("房间已满")) {
            mDatabinding.multipleStatusLayout.showEmpty();
        } else {
            mDatabinding.multipleStatusLayout.showError();
        }
    }

    private void selectedDrawOrEraser(boolean isSelectEraser) {
        mDatabinding.bottomBar.drawIv.setSelected(!isSelectEraser);
        mDatabinding.bottomBar.eraserIv.setSelected(isSelectEraser);
        this.isSelectEraser = isSelectEraser;
    }

    /**
     * 涂鸦提示
     *
     * @param isDraw
     */
    private void drawTip(boolean isDraw) {
        this.showToastShort(isDraw ? "你可以使用画笔涂鸦了！" : "你已不能使用画笔涂鸦!");
    }

    private void initOTODrawManager() {
        baseViewModel.setDefaultDrawValue();
        selectedDrawOrEraser(isSelectEraser);
        if (mRTCPanelManager != null) {
            return;
        }
        boolean isIPad = isIPad();
        int width = isIPad ? getDimension(R.dimen.dp_160) : getDimension(
                R.dimen.dp_240);
        int height = isIPad ? getDimension(R.dimen.dp_115) : getDimension(R.dimen.dp_189);
        mRTCPanelManager = new RTCPanelManager(this, width, height);
        mRTCPanelManager.setArrowDirection(BubbleStyle.ArrowDirection.Down);
        mRTCPanelManager.setOnDrawCmdListener(new RTCPanelManager.OnDrawCmdListener() {
            @Override
            public void onPaintColor(int color) {
                if (baseViewModel != null) {
                    baseViewModel.setPaintColor(color);
                }
            }

            @Override
            public void onStrokeWidth(int size) {
                if (baseViewModel != null) {
                    baseViewModel.setStokeWidth(size);
                }
            }

            @Override
            public void onDrawType(int type) {
                if (baseViewModel != null) {
                    baseViewModel.setDrawType(type);
                }
            }
        });
        mRTCPanelManager.getDrawLiveData().observeForever(integer -> mDatabinding.bottomBar.drawIv.setImageDrawable(LiveOneToOneNativeActivity.this.getResources().getDrawable(integer)));
    }

    /**
     * 时间设置
     */
    private void setLiveTime(Object time) {
        if (time != null) {
            mDatabinding.toolBar.timeTv.setText(TimeUtil.displayHHMMSS((long) time));
        }
    }

    /**
     * 涂鸦控件及橡皮檫的显隐
     */
    private void drawVisibility(boolean isDraw) {
        drawVisibility(isDraw, true);
    }

    /**
     * 涂鸦控件及橡皮檫的显隐
     *
     * @param isDraw
     * @param clear  是否清除标记
     */
    private void drawVisibility(boolean isDraw, boolean clear) {
        int visibility = isDraw ? View.VISIBLE : View.GONE;
        mDatabinding.bottomBar.eraserIv.setVisibility(visibility);
        mDatabinding.bottomBar.drawIv.setVisibility(visibility);
        if (!isDraw && mRTCPanelManager != null && mRTCPanelManager.isShowing()) {
            mRTCPanelManager.dismiss();
        }
        if (!isDraw && clear) {//没有涂鸦权限时清除
            mRTCPanelManager = null;
            isSelectEraser = false;
        }
    }

    private void titleAndTimeVisibility(int visible) {
        mDatabinding.toolBar.titleTv.setVisibility(visible);
        mDatabinding.toolBar.timeTv.setVisibility(visible);
        mDatabinding.toolBar.timeIv.setVisibility(visible);

    }

    private void hideMultipleStatusLayout() {
        mDatabinding.multipleStatusLayout.showContent();
    }

    /**
     * 聊天接受
     */
    private void receiverChatEntity(ChatEntity value) {
        if (isIPad()) {
            mOTOChatAdapter.appendList(value);
            mDatabinding.chatShowLayout.scrollToPosition(mOTOChatAdapter.getItemCount() - 1);
        } else {
            int number = mOTOChatPopView != null && !mOTOChatPopView.isShowing() && value.getRole().equals(MemberRole.MEMBER_ROLE_SUPER_ADMIN) ? -1 : 0;
            mQBadgeView.setBadgeNumber(number);
            mOTOChatPopView.receiveChatEntity(value);
        }

    }

    @Override
    protected void initView() {
        onClick();
        initAdapter();
        calculateWidthAndHeight();
        initGuide();
        if (!isIPad()) {
            mOTOChatPopView = new OTOChatPopView(this);
            mQBadgeView = new QBadgeView(this);
            mQBadgeView.bindTarget(mDatabinding.bottomBar.bottomChatIv);
            mQBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCallback(Event message) {
        if (message.getType() == EventType.NETWORK_STATE_CHANGE) {
            int netStatus = (int) message.getData();
            if (netStatus == NetMonitor.TYPE_NOT_CONNECTED) {
                noNetWorkTime = System.currentTimeMillis();
                mDatabinding.llBadNetStatus.badNetStatusLl.setVisibility(View.VISIBLE);
            } else if (netStatus == NetMonitor.TYPE_MOBILE) {
                mDatabinding.llBadNetStatus.badNetStatusLl.setVisibility(View.GONE);
                showToastShort(this.getString(R.string.network_4G_tip));
            } else {
                mDatabinding.llBadNetStatus.badNetStatusLl.setVisibility(View.GONE);
                if (noNetWorkTime != 0 && System.currentTimeMillis() - noNetWorkTime > 15_00) {//断网超过1.5秒就重新加载
                    noNetWorkTime = 0;
                    //有网后延迟100豪秒执行
                    handler.postDelayed(() -> {
                        if (baseViewModel != null) {
                            TalkFunLogger.i("network baseViewModel reload");
                            reset();
                            baseViewModel.reload();
                        }
                    }, 100);
                }
            }
        }
    }

    private void initGuide() {
        new GuideHelper(this, videoWidth).show();
    }

    private void calculateWidthAndHeight() {
        if (isIPad()) {
            int screenHeight = DimensionUtils.getScreenHeight(this) - getDimension(R.dimen.dp_24);
            int screenWidth = DimensionUtils.getScreenWidth(this);
            int videoHeight = screenHeight / 5 * 3 - getDimension(R.dimen.dp_2_5);
            videoWidth = screenHeight / 5 * 2;
            int chatHeight = screenHeight - videoHeight;
            toastXOffset = chatHeight / 2;
            RelativeLayout.LayoutParams videoLayoutParams = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
            mDatabinding.videoLayout.setLayoutParams(videoLayoutParams);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoWidth, chatHeight);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mDatabinding.chatLl.setLayoutParams(layoutParams);
        } else {
            int screenWidth = DimensionUtils.getScreenWidth(this);
            int screenHeight = DimensionUtils.getScreenHeight(this) - getDimension(R.dimen.dp_40);
            videoWidth = screenHeight * 2 / 3;
            toastXOffset = videoWidth / 2;
            mDatabinding.videoLayout.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, screenHeight));
        }

    }

    private void initAdapter() {
        mDatabinding.videoLayout.setLayoutManager(new LinearLayoutManager(this));
        OTOVideoAdapter otoVideoAdapter = new OTOVideoAdapter();
        otoVideoAdapter.setDataList(baseViewModel.getVideoList());
        otoVideoAdapter.setMediaSwitchListener(this);
        mDatabinding.videoLayout.setAdapter(otoVideoAdapter);
        if (isIPad()) {
            mDatabinding.chatShowLayout.setLayoutManager(new LinearLayoutManager(this));
            mOTOChatAdapter = new OTOChatAdapter();
            mDatabinding.chatShowLayout.setAdapter(mOTOChatAdapter);
            mDatabinding.chatShowLayout.addItemDecoration(new RecycleViewDivider.Build().context(this.getApplicationContext()).
                    orientation(LinearLayout.VERTICAL).dividerHeight(getDimension(R.dimen.dp_0_5))
                    .dividerColor(Color.parseColor("#29303B")).leftOffset(getDimension(R.dimen.dp_5))
                    .rightOffset(getDimension(R.dimen.dp_5))
                    .create());
        }
    }

    private void onClick() {
        banFrequentlyClick(mDatabinding.toolBar.backIv, o -> {
//                socketConnectFail();
            popupBackDialog();
        });
        banFrequentlyClick(mDatabinding.bottomBar.drawIv, aVoid -> {
            selectedDrawOrEraser(false);
            popupDrawPopView();
        });
        banFrequentlyClick(mDatabinding.bottomBar.eraserIv, o -> {
            selectedDrawOrEraser(true);
            baseViewModel.setEraser();
        });

        if (isIPad()) {
            banFrequentlyClick(mDatabinding.chatLayout.chatMessageTv, o -> popupMsgDialog());
            banFrequentlyClick(mDatabinding.chatLayout.sendBtn, o -> sendMsg(getMsgText()));

        } else {
            banFrequentlyClick(mDatabinding.bottomBar.bottomChatMessageTv, o -> popupMsgDialog());
            banFrequentlyClick(mDatabinding.bottomBar.bottomChatIv, o -> {
                if (mQBadgeView != null) {
                    mQBadgeView.setBadgeNumber(0);
                }
                if (mOTOChatPopView != null) {
                    mOTOChatPopView.show(mDatabinding.bottomBar.rlBottomBar);
                }
            }
            );
        }
        /**
         * 音视频全屏
         */
        banFrequentlyClick(mDatabinding.multimediaLayout.ivFullScreen, aVoid -> {
            boolean isSelect = mDatabinding.multimediaLayout.ivFullScreen.isSelected();

            multiMediaViewFullScreen(!isSelect);
        });
        mDatabinding.multimediaLayout.flMultiMediaParent.setOnClickListener(v -> {
            if (mDatabinding.multimediaLayout.progressBarParent == null) {
                return;
            }
            if (mDatabinding.multimediaLayout.progressBarParent.getVisibility() == View.VISIBLE) {
                hideProgressBar();
            } else {
                showProgressBar();
            }
        });
    }

    private void popupMsgDialog() {
        InputMsgDialog msgDialog = new InputMsgDialog(this);
        if (isIPad()) {
            msgDialog.setMessageEdit(mDatabinding.chatLayout.chatMessageTv.getText().toString());
            msgDialog.setOnSendMessageListener(new InputMsgDialog.OnSendMessageListener() {
                @Override
                public void onSendMsg(String msg) {
                    sendMsg(msg);
                }

                @Override
                public void onMsg(String msg) {
                    setMsgText(msg);
                    setSendBtnEnable(!TextUtils.isEmpty(msg));
                }
            });
        } else {
            msgDialog.setMessageEdit(mDatabinding.bottomBar.bottomChatMessageTv.getText().toString());
            msgDialog.setOnSendMessageListener(new InputMsgDialog.OnSendMessageListener() {
                @Override
                public void onSendMsg(String msg) {
                    sendMsg(msg);
                }

                @Override
                public void onMsg(String msg) {
                    setMsgText(msg);
                }
            });
        }
        msgDialog.show();
    }

    /**
     * 发送聊天
     *
     * @param msg
     */
    private void sendMsg(String msg) {
        baseViewModel.sendMessage(msg, new Callback<JSONObject>() {
            @Override
            public void success(JSONObject result) {
                resetMsgTextAndSendBtn();
            }

            @Override
            public void failed(String failed) {
                resetMsgTextAndSendBtn();
                LiveOneToOneNativeActivity.this.showToastShort(failed);
            }
        });
    }

    private void resetMsgTextAndSendBtn() {
        setMsgText("");
        setSendBtnEnable(false);
    }

    private void setSendBtnEnable(boolean b) {
        if (!isIPad()) {
            return;
        }
        mDatabinding.chatLayout.sendBtn.setEnabled(b);
    }

    private String getMsgText() {
        return mDatabinding.chatLayout.chatMessageTv.getText().toString();
    }

    private void setMsgText(String msgText) {
        if (isIPad())
            mDatabinding.chatLayout.chatMessageTv.setText(msgText);
        else
            mDatabinding.bottomBar.bottomChatMessageTv.setText(msgText);
    }

    private void popupDrawPopView() {
        if (mRTCPanelManager == null) {
            return;
        }
        int offy = -getDimension(R.dimen.dp_2);
        mRTCPanelManager.show(mDatabinding.bottomBar.drawIv, VerticalGravity.ABOVE, HorizontalGravity.ALIGN_LEFT, 0, offy);
        if (!isSelectEraser) {
            baseViewModel.setDrawType(mRTCPanelManager.getLastSelectDrawType());
        }
    }

    private List<Subscription> subscriptionList = new ArrayList<>();

    private void banFrequentlyClick(View view, Action1<Void> action) {
        Subscription subscription = RxView.clicks(view).throttleFirst(600, TimeUnit.MILLISECONDS).subscribe(action);
        subscriptionList.add(subscription);
    }

    @Override
    protected int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_live_one_to_one_native;
    }

    @Override
    public void updateItemOfPart(int position, int type) {
        mDatabinding.videoLayout.getAdapter().notifyItemChanged(position, type);
    }

    @Override
    public void notifyItemRangeChanged(int position, int itemCount) {

    }

    @Override
    public void notifyItemRemoved(int position, int itemCount) {

    }

    @Override
    public void notifyDataSetChanged() {
        mDatabinding.videoLayout.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void mediaSwitch(int type, boolean isOpen, int enable) {
        if (!PreventRepeatedUtil.canClickable(String.valueOf(type))) {
            showToastShort(getString(R.string.quick_click_tips));
            return;
        }
        if (!NetMonitor.isNetworkAvailable(LiveOneToOneNativeActivity.this)) {
            showToastShort("网络不可用,请稍后尝试");
            return;
        }
        if (enable == MediaStatus.CLOSE_FOR_ZHUBO) {
            if (type == OTOVideoAdapter.AUDIO) {
                showToastShort("你已被禁止说话了！");
            } else if (type == OTOVideoAdapter.VIDEO) {
                showToastShort("你已被关闭摄像头了！");
            }
            return;
        }
        baseViewModel.switchMedia(type, isOpen);
    }

    private void showToastShort(String msg) {
        ToastUtil.show(this, msg, Gravity.CENTER, -toastXOffset, 0, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        baseViewModel.onResume();
        mDatabinding.multipleStatusLayout.showLoading();
    }

    @Override
    protected void onDestroy() {
        baseViewModel.release();
        liveObserver = null;
        if (mRTCPanelManager != null) {
            mRTCPanelManager.release();
            mRTCPanelManager = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        for (Subscription subscription : subscriptionList) {
            subscription.unsubscribe();
        }
        subscriptionList = null;
        super.onDestroy();
    }

    @Override
    public void onLiveTime(long time) {
        setLiveTime(time);
    }

    //-----------------------------------------音视频--------------------------------------------------------
    @Override
    public void onMultiMediaApplicate(int id, int docType, String title, int duration) {
        isApplicateMuitiMedia = true;
        this.duration = duration;
        isMp4 = DocType.MP4 == docType;
        initMultiMediaLayout(isMp4);
        mDatabinding.multimediaLayout.timeProgress.setMax(duration);
        setProgressBarTime(0, false);
        drawVisibility(false, false);
    }

    @Override
    public void onMultiMediaStatusChange(int status, int time, String msg) {
        switch (status) {
            case OnMultiMediaStatusChangeListener.STATUS_PLAY:
                setMultiMediaViewPauseVisiable(View.GONE);
                setProgressBarTime(time);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_SEEK:
                setProgressBarTime(time, false);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_PAUSE:
                setProgressBarTime(time);
                setMultiMediaViewPauseVisiable(View.VISIBLE);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_CLOSE:
                if (isMuitiMediaFullScreen) {
                    multiMediaViewFullScreen(false);
                }
                setMultiMediaViewVisiable(View.GONE);
                isApplicateMuitiMedia = false;
                drawVisibility(isDraw, false);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_COMPLETE:
                setProgressBarTime(0, false);
                setMultiMediaViewPauseVisiable(View.VISIBLE);
                break;
        }

    }

    @Override
    public void multiMediaViewFullScreen(boolean isFull) {
        mDatabinding.multimediaLayout.ivFullScreen.setSelected(isFull);
        isMuitiMediaFullScreen = isFull;
        if (isFull) {
            mDatabinding.flFullScreen.setVisibility(View.VISIBLE);
            if (mDatabinding.multimediaLayout.flMultiMediaParent.getParent() != null) {
                ((FrameLayout) mDatabinding.multimediaLayout.flMultiMediaParent.getParent()).removeView(mDatabinding.multimediaLayout.flMultiMediaParent);
            }
            mDatabinding.flFullScreen.addView(mDatabinding.multimediaLayout.flMultiMediaParent);
        } else {
            View view = mDatabinding.flFullScreen.getChildAt(0);
            if (view != null) {
                mDatabinding.flFullScreen.removeAllViews();
                mDatabinding.parentViewgroup.addView(view, 1);
            }
            mDatabinding.flFullScreen.setVisibility(View.GONE);
        }
        if (!isMp4 && lastRotate) {
            mDatabinding.multimediaLayout.ivMultiMediaDoctype.startRotate();
        }
    }

    @Override
    public void initMultiMediaLayout(boolean isMp4) {
        setMultiMediaViewVisiable(View.VISIBLE);
        mDatabinding.multimediaLayout.viewShade.setBackgroundColor(
                this.getResources().getColor(isMp4 ? R.color.transparency : R.color.multi_media_bg)
        );
        setMultiMediaViewPauseVisiable(View.VISIBLE);
        mDatabinding.multimediaLayout.ivMultiMediaDoctype.setVisibility(isMp4 ? View.GONE : View.VISIBLE);

    }

    @Override
    public void setMultiMediaViewVisiable(int visibility) {
        mDatabinding.multimediaLayout.flMultiMediaParent.setVisibility(visibility);
        rotate(false);
    }

    @Override
    public void setMultiMediaViewPauseVisiable(int visibility) {
        lastVisibility = visibility;
        mDatabinding.multimediaLayout.tvPause.setVisibility(visibility);
        if (!isMp4) {
            rotate(visibility == View.GONE);
        }
    }

    @Override
    public void setProgressBarTime(int time) {
        setProgressBarTime(time, true);
    }

    @Override
    public void setProgressBarTime(int time, boolean filter) {
        if (filter && mDatabinding.multimediaLayout.timeProgress.getProgress() > time) {
            return;
        }
        mDatabinding.multimediaLayout.timeProgress.setProgress(time);
        mDatabinding.multimediaLayout.tvTime.setText(TimeUtil.displayHHMMSS(time) + "/" + TimeUtil.displayHHMMSS(duration));
    }

    @Override
    public void rotate(boolean isRotate) {
        if (lastRotate == isRotate) {
            return;
        }
        lastRotate = isRotate;
        if (isRotate) {
            mDatabinding.multimediaLayout.ivMultiMediaDoctype.startRotate();
        } else {
            mDatabinding.multimediaLayout.ivMultiMediaDoctype.stopRotate();
        }
    }

    @Override
    public void hideProgressBar() {
        mDatabinding.multimediaLayout.progressBarParent.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        mDatabinding.multimediaLayout.progressBarParent.setVisibility(View.VISIBLE);
    }


}