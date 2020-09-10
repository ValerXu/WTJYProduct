package com.android.jwjy.wtjyproduct.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseViewModel;
import com.android.jwjy.wtjyproduct.bean.ViewModelEvent;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.consts.Interaction;
import com.talkfun.sdk.consts.LiveStatus;
import com.talkfun.sdk.consts.TFMode;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtDispatchChatMessageListener;
import com.talkfun.sdk.event.LiveInListener;
import com.talkfun.sdk.event.OnLiveDurationListener;
import com.talkfun.sdk.event.OnMultiMediaStatusChangeListener;
import com.talkfun.sdk.event.OnSocketConnectListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.VideoModeType;
import com.talkfun.sdk.rtc.RtcOperatorProxy;
import com.talkfun.sdk.rtc.interfaces.OnRtcErrorListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcMediaStatusListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcMemberListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcStatusListener;
import com.talkfun.sdk.rtc.interfaces.OnWhiteboardPowerListener;
import com.talkfun.whiteboard.config.DrawType;
import com.talkfun.whiteboard.presenter.draw.IWhiteBoardOperator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 1VN 基类
 * Created by ccy on 2019/5/16/18:36
 */
public abstract class BaseLiveRtcViewModel extends BaseViewModel implements LiveInListener, OnRtcMemberListener,
        OnWhiteboardPowerListener, OnRtcMediaStatusListener, HtDispatchChatMessageListener, OnRtcStatusListener,
        OnLiveDurationListener, OnRtcErrorListener, OnSocketConnectListener, OnVideoChangeListener {
    protected MutableLiveData<ViewModelEvent> liveData = new MutableLiveData();
    protected List<VideoStatusData> videoList = new ArrayList<>();
    protected HtSdk mHtSdk;
    protected RtcOperatorProxy mRtcOperatorProxy;
    protected IWhiteBoardOperator mWhiteBoardOperator;
    //涂鸦默认值
    protected int drawType = DrawType.DRAW_PATH_MODE;
    protected int mColor = Color.parseColor("#FC2F04");
    protected int mStokeWidth = 5;
    protected boolean isShowDrawTips = false;
    protected boolean isReload = false;
    public OnVideoDataChangeListener onVideoDataChangeListener;
    /**
     * 基类标志 为1--40
     */
    public static final int RECEIVER_CHAT = 1; //聊天
    public static final int LIVE_START = 2; //直播开始
    public static final int LIVE_STOP = 3;  //直播结束
    public static final int LIVE_TIME = 4;  //直播时长
    public static final int MEMBER_FORCEOUT = 5;   //踢出房间
    public static final int LIVE_DRAW = 6;   //涂鸦权限
    public static final int LIVE_FAIL = 7;//直播失败
    public static final int LIVE_WAIT = 8;//直播未开始
    public static final int AUDIO_ENABLE = 9;//音频
    public static final int VIDEO_ENABLE = 10;//视频
    public static final int ERROR_MESSAGE = 11;//错误信息
    public static final int LIVE_SOCKET_CONNNECT_FAIL = 12;//socket连接失败
    public static final int MEMBER_KICK = 13;    //用户被踢
    public static final int RTC_DESTOP_MODE = 14;    //rtc桌面分享模式
    public static final int RTC_INVITE = 15; //邀请学员上讲台
    private OnTimeListener onTimeListener;

    public BaseLiveRtcViewModel(@NonNull Application application) {
        super(application);
    }

    public void initSdk(String token, ViewGroup whiteboardView) {
        initSdk(token, whiteboardView, TFMode.LIVE_RTC);
    }

    public void initSdk(String token, ViewGroup whiteboardView, TFMode tfMode) {
        mHtSdk = HtSdk.getInstance();
        mHtSdk.init(context, whiteboardView, null, token, tfMode);
        addListener();
        initData();
    }

    abstract void initData();

    protected void addListener() {
        mHtSdk.setLiveListener(this);
        mHtSdk.setRtcMemberListener(this);
        mHtSdk.setWhiteboardPowerListener(this);
        mHtSdk.setRtcMediaStatusListener(this);
        mHtSdk.setRtcErrorListener(this);
        mHtSdk.setHtDispatchChatMessageListener(this);
        mHtSdk.setRtcStatusListener(this);
        mHtSdk.setLiveDurationListener(this);
        mHtSdk.setWhiteboardBackgroudColor(Color.parseColor("#0F1012"));
        mHtSdk.addSocketConnectionListener(this);
        mHtSdk.setOnVideoChangeListener(this);
        LayoutInflater layoutInflater = LayoutInflater.from(application.getApplicationContext());
        mHtSdk.setLiveWaitView(layoutInflater.inflate(R.layout.activity_live_one_to_one__wait, null));
        mHtSdk.setLiveOverView(layoutInflater.inflate(R.layout.activity_live_one_to_one__stop, null));
    }

    /**
     * 发送聊天
     *
     * @param content
     * @param callback
     */
    public void sendMessage(String content, Callback<JSONObject> callback) {
        if (content == null || mHtSdk == null) {
            return;
        }
        mHtSdk.emit(BroadcastCmdType.CHAT_SEND, content, callback);
    }

    public void onPause() {
        if (mHtSdk != null) {
            mHtSdk.onPause();
        }
    }


    public interface OnVideoDataChangeListener {
        void updateItemOfPart(int position, int type);

        void notifyItemRangeChanged(int position, int itemCount);

        void notifyItemRemoved(int position, int itemCount);

        void notifyDataSetChanged();
    }

    @Override
    public void receiveChatMessage(ChatEntity entity) {
        liveData.setValue(new ViewModelEvent(RECEIVER_CHAT, entity));
    }

    @Override
    public void onLaunch() {
        if (mHtSdk == null) {
            return;
        }
        if (LiveStatus.STOP.equals(mHtSdk.getInitLiveStatus())) {
            onLiveStop();
        } else if (LiveStatus.WAIT.equals((mHtSdk.getInitLiveStatus()))) {
            onLiveWait();
        }
    }

    private void onLiveWait() {
        liveData.setValue(new ViewModelEvent(LIVE_WAIT));
    }

    @Override
    public void onLiveStart() {
        mRtcOperatorProxy = mHtSdk.getRtcOperatorProxy();
        liveData.setValue(new ViewModelEvent(LIVE_START));
    }

    @Override
    public void onInitFail(String msg) {
        liveData.setValue(new ViewModelEvent(LIVE_FAIL, msg));
    }

    /**
     * 设置涂鸦默认值
     */
    public void setDefaultDrawValue() {
        if (mWhiteBoardOperator != null) {
            drawType = DrawType.DRAW_PATH_MODE;
            mWhiteBoardOperator.setDrawType(drawType);
            mWhiteBoardOperator.setPaintColor(mColor);
            mWhiteBoardOperator.setStrokeWidth(mStokeWidth);
        }
    }

    @Override
    public void memberForceout() {
        liveData.setValue(new ViewModelEvent(MEMBER_FORCEOUT));
    }

    @Override
    public void memberKick() {
        liveData.setValue(new ViewModelEvent(MEMBER_KICK));
    }

    public void setOnMultiMediaStatusChangeListener(OnMultiMediaStatusChangeListener onMultiMediaStatusChangeListener) {
        if (mHtSdk == null || onMultiMediaStatusChangeListener == null) {
            return;
        }
        mHtSdk.setMultiMediaStatusChangeListener(onMultiMediaStatusChangeListener);
    }

    @Override
    public void onTime(int totalTime) {
        if (onTimeListener != null) {
            onTimeListener.onLiveTime(totalTime * 1000);
        }
    }

    @Override
    public void onError(int code, String msg) {

    }

    @Override
    public void onLiveStop() {
        videoList.clear();
        liveData.setValue(new ViewModelEvent(LIVE_STOP));
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onReconnecting() {

    }

    @Override
    public void onReconnectFailed() {

    }

    @Override
    public void onConnectFailed() {
        liveData.setValue(new ViewModelEvent(LIVE_SOCKET_CONNNECT_FAIL));
    }

    @Override
    public void onConnectSuccess() {

    }

    /**
     * 获取视频列表
     */
    public List<VideoStatusData> getVideoList() {
        return videoList;
    }

    /**
     * 获取直播标题
     */
    public String getLiveTitle() {
        if (mHtSdk == null || mHtSdk.getRoomInfo() == null) {
            return "";
        }
        return mHtSdk.getRoomInfo().getLiveTitle();
    }

    public MutableLiveData<ViewModelEvent> getLiveData() {
        return liveData;
    }

    /**
     * 是否接受上讲台邀请
     *
     * @param callback
     */
    public void respondInvite(boolean isAccept, final Callback<String> callback) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.respondInvite(isAccept ? Interaction.InviteStatus.ACCEPT : Interaction.InviteStatus.REJECT, callback);
    }

    /**
     * 设置橡皮檫
     */
    public void setEraser() {
        drawType = DrawType.DRAW_CLEAR_MODE;
        if (mWhiteBoardOperator == null) {
            return;
        }
        mWhiteBoardOperator.setDrawType(DrawType.DRAW_CLEAR_MODE);
    }

    /**
     * 设置画笔类型
     *
     * @param type
     */
    public void setDrawType(int type) {
        drawType = type;
        if (mWhiteBoardOperator == null) {
            return;
        }
        mWhiteBoardOperator.setDrawType(type);
    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(@ColorInt int color) {
        mColor = color;
        if (mWhiteBoardOperator == null) {
            return;
        }
        mWhiteBoardOperator.setPaintColor(color);
    }

    /**
     * 设置画笔粗细
     *
     * @param stokeWidth
     */
    public void setStokeWidth(int stokeWidth) {
        mStokeWidth = stokeWidth;
        if (mWhiteBoardOperator == null) {
            return;
        }
        mWhiteBoardOperator.setStrokeWidth(stokeWidth);
    }

    public void setOnVideoDataChangeListener(OnVideoDataChangeListener onVideoDataChangeListener) {
        this.onVideoDataChangeListener = onVideoDataChangeListener;
    }

    public void setMultiMediaViewContainer(ViewGroup multiMediaViewContainer) {
        if (mHtSdk != null) {
            mHtSdk.setMultiMediaViewContainer(multiMediaViewContainer);
        }
    }

    public void setDesktopVideoContainer(ViewGroup desktopVideoContainer) {
        if (mHtSdk != null) {
            mHtSdk.setDesktopVideoContainer(desktopVideoContainer);
        }
    }

    public void reload() {
        isReload = true;
        if (videoList != null) {
            videoList.clear();
            if (onVideoDataChangeListener != null) {
                onVideoDataChangeListener.notifyDataSetChanged();
            }
        }
        if (mHtSdk == null) {
            return;
        }
        mHtSdk.reload();
    }

    public void onResume() {
        if (mHtSdk == null) {
            return;
        }
        mHtSdk.onResume();
    }

    /**
     * 涂鸦重置
     */
    protected void resetDrawValue() {
        drawType = DrawType.DRAW_PATH_MODE;
        mColor = Color.parseColor("#FC2F04");
        mStokeWidth = 5;
    }

    public void setOnTimeListener(OnTimeListener onTimeListener) {
        this.onTimeListener = onTimeListener;
    }

    public interface OnTimeListener {
        void onLiveTime(long time);
    }

    @Override
    public void onVideoStart(int mode) {

    }

    @Override
    public void onVideoStop(int mode) {

    }

    @Override
    public void onVideoModeChanging(int beforeMode, int currentMode) {
        if (liveData != null && VideoModeType.DESKTOP_MODE == currentMode) {
            liveData.postValue(new ViewModelEvent(RTC_DESTOP_MODE));
        }
    }

    @Override
    public void onVideoModeChanged() {

    }

    @Override
    public void onCameraShow() {

    }

    @Override
    public void onCameraHide() {

    }

    public void release() {
        liveData = null;
        videoList.clear();
        mHtSdk.release();
    }
}
