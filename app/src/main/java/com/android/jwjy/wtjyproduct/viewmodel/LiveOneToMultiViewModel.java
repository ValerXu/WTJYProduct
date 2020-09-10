package com.android.jwjy.wtjyproduct.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.android.jwjy.wtjyproduct.adapter.OTMVideoAdapter;
import com.android.jwjy.wtjyproduct.adapter.OTOVideoAdapter;
import com.android.jwjy.wtjyproduct.bean.ViewModelEvent;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.android.jwjy.wtjyproduct.util.VoteDataUtils;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.LiveStatus;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtVoteExtListener;
import com.talkfun.sdk.module.VoteDelEntity;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;
import com.talkfun.sdk.rtc.consts.ApplyStatus;
import com.talkfun.sdk.rtc.consts.MediaStatus;
import com.talkfun.sdk.rtc.entity.AwardEntity;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;
import com.talkfun.sdk.rtc.interfaces.OnAwardListener;

import org.json.JSONException;

/**
 * Created by ccy on 2019/5/8/16:48
 */
public class LiveOneToMultiViewModel extends BaseLiveRtcViewModel implements OnAwardListener, HtVoteExtListener {
    /**
     * 开启讲台
     */
    public static final int RTC_OPEN = 41;
    /**
     * 关闭讲台
     */
    public static final int RTC_CLOSE = 42;
    /**
     * 申请中
     */
    public static final int RTC_APPLYING = 43;

    /**
     * 已经上讲台
     */
    public static final int RTC_ALLOW = 44;
    /**
     * 踢下讲台
     */
    public static final int RTC_KICK = 45;
    /**
     * 主动下讲台
     */
    public static final int RTC_DOWN = 46;
    /**
     * 取消上讲台
     */
    public static final int RTC_CANCLE = 47;
    /**
     * 奖励
     */
    public static final int RTC_AWARD = 48;
    /**
     * 投票开始
     */
    public static final int LIVE_VOTE_START = 49;
    /**
     * 投票结束
     */
    public static final int LIVE_VOTE_STOP = 50;
    /**
     * 获取所有投票
     */
    public static final int LIVE_VOTE_ALL = 51;
    /**
     * 投票删除
     */
    public static final int LIVE_VOTE_DEL = 52;

    /**
     * 记录当前用户是否有涂鸦权限
     */
    private boolean isDrawPower = false;
    /**
     * 视频是否被主播关闭
     */
    private boolean isCloseVideoForZhubo = false;
    /**
     * 是否被主播关闭
     */
    private boolean isCloseAudioForZhubo = false;


    public LiveOneToMultiViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void addListener() {
        super.addListener();
        mHtSdk.setAwardListener(this);
        mHtSdk.setHtVoteListener(this);
    }

    @Override
    void initData() {

    }

    @Override
    public void onLaunch() {
        super.onLaunch();

    }

    /**
     * 音频开关
     */
    public void switchMedia(int type, boolean isOpen, final Callback<String> callback) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        if (type == OTOVideoAdapter.AUDIO) {
            if (isCloseAudioForZhubo) {
                if (callback != null) {
                    callback.failed("主播已关闭你的音频，无法打开");
                }

                return;
            }
            if (isOpen) {
                mRtcOperatorProxy.closeAudio(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                        if (callback != null) {
                            callback.success("");
                        }
                    }

                    @Override
                    public void failed(String failed) {
                        callback.failed(failed);
                    }
                });
            } else {
                mRtcOperatorProxy.openAudio(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                        if (callback != null) {
                            callback.success("");
                        }
                    }

                    @Override
                    public void failed(String failed) {
                        if (callback != null) {
                            callback.failed(failed);
                        }
                    }
                });
            }
        } else {
            if (isCloseVideoForZhubo) {
                if (callback != null) {
                    callback.failed("主播已关闭你的摄像头，无法打开");
                }
                return;
            }
            if (isOpen) {
                mRtcOperatorProxy.closeVideo(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                        if (callback != null) {
                            callback.success("");
                        }
                    }

                    @Override
                    public void failed(String failed) {
                        if (callback != null) {
                            callback.failed(failed);
                        }
                    }
                });
            } else {
                mRtcOperatorProxy.openVideo(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                        if (callback != null) {
                            callback.success("");
                        }
                    }

                    @Override
                    public void failed(String failed) {
                        if (callback != null) {
                            callback.failed(failed);
                        }
                    }
                });
            }
        }
    }

    private void switchSuccess(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity == null) {
            return;
        }
        int position = searchPosition(rtcUserEntity);
        if (position == -1 || videoList == null || videoList.isEmpty()) {
            return;
        }
        videoList.get(position).setRtcUserEntity(rtcUserEntity);
        notifyItemChanged(position, OTOVideoAdapter.PARTIAL_REFRESH);
    }

    private void notifyItemChanged() {
        notifyItemChanged(-1, -1);
    }

    private void notifyItemChanged(int index) {
        notifyItemChanged(index, -1);
    }

    private void notifyItemChanged(int index, int type) {
        if (onVideoDataChangeListener == null) {
            return;
        }
        if (index != -1) {
            onVideoDataChangeListener.updateItemOfPart(index, type);
        } else {
            onVideoDataChangeListener.notifyDataSetChanged();
        }
    }

    /**
     * 插入刷新
     *
     * @param index
     * @param count
     */
    private void notifyItemRangeChanged(int index, int count) {
        if (onVideoDataChangeListener == null) {
            return;
        }
        onVideoDataChangeListener.notifyItemRangeChanged(index, count);
    }

    /**
     * 移除刷新
     *
     * @param index
     * @param count
     */
    private void notifyItemRemoved(int index, int count) {
        if (onVideoDataChangeListener == null) {
            return;
        }
        onVideoDataChangeListener.notifyItemRemoved(index, count);
    }

    private void onLiveWait() {
        liveData.setValue(new ViewModelEvent(LIVE_WAIT));
    }

    @Override
    public void onInitFail(String msg) {
        liveData.setValue(new ViewModelEvent(LIVE_FAIL, msg));
    }

    @Override
    public void onLiveStart() {
        super.onLiveStart();
        getAllVotes();
    }

    @Override
    public void onLiveStop() {
        super.onLiveStop();
        reset();
    }

    private void reset() {
        isReload = false;
        recoverDefault();
        resetVideoAdapter();
    }


    private void resetVideoAdapter() {
        notifyItemChanged();
    }


    @Override
    public void onVideoClose(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            setIsCloseVideoForZhubo(rtcUserEntity.getVideo());
            liveData.setValue(new ViewModelEvent(VIDEO_ENABLE, false));
        }
        updateVideolistMedia(rtcUserEntity);
    }

    @Override
    public void onVideoOpen(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            setIsCloseVideoForZhubo(rtcUserEntity.getVideo());
            liveData.setValue(new ViewModelEvent(VIDEO_ENABLE, true));
        }
        updateVideolistMedia(rtcUserEntity);
    }

    @Override
    public void onAudioOpen(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            setIsCloseAudioForZhubo(rtcUserEntity.getAudio());
            liveData.setValue(new ViewModelEvent(AUDIO_ENABLE, true));
        }
        updateVideolistMedia(rtcUserEntity);
    }

    @Override
    public void onAudioClose(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            setIsCloseAudioForZhubo(rtcUserEntity.getAudio());
            liveData.setValue(new ViewModelEvent(AUDIO_ENABLE, false));
        }
        updateVideolistMedia(rtcUserEntity);
    }


    @Override
    public void onUp(RtcUserEntity rtcUserEntity, View videoView) {
        if (videoView == null || rtcUserEntity == null) {
            return;
        }
        addVideoData(rtcUserEntity, videoView);
        if (rtcUserEntity.isMe()) {//上讲台通知
            setIsCloseAudioForZhubo(rtcUserEntity.getAudio());
            setIsCloseVideoForZhubo(rtcUserEntity.getVideo());
            liveData.setValue(new ViewModelEvent(RTC_ALLOW, rtcUserEntity));
        }
    }

    @Override
    public void onDown(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity == null) {
            return;
        }
        removeUser(rtcUserEntity);
    }

    @Override
    public void onKick(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity == null) {
            return;
        }
        removeUser(rtcUserEntity);
        if (rtcUserEntity.isMe()) {
            recoverDefault();
            liveData.setValue(new ViewModelEvent(RTC_KICK));
        }
    }

    /**
     * 移除某个视频列表用户
     *
     * @param rtcUserEntity
     */
    private void removeUser(RtcUserEntity rtcUserEntity) {
        int position = searchPosition(rtcUserEntity);
        if (position == -1) {
            return;
        }
        videoList.remove(position);
        notifyItemRemoved(position, videoList.size());
    }

    /**
     * 设置video（主播关闭时不可申请）
     */
    private void setIsCloseVideoForZhubo(int video) {
        this.isCloseVideoForZhubo = video == MediaStatus.CLOSE_FOR_ZHUBO;
    }

    /**
     * 设置audio（主播关闭时不可点开）
     */
    private void setIsCloseAudioForZhubo(int audio) {
        this.isCloseAudioForZhubo = audio == MediaStatus.CLOSE_FOR_ZHUBO;
    }

    /**
     * 下角标搜索
     *
     * @param rtcUserEntity
     * @return
     */
    private int searchPosition(RtcUserEntity rtcUserEntity) {
        int index = -1;
        if (rtcUserEntity == null) {
            return index;
        }
        return searchPositionByXid(rtcUserEntity.getXid());
    }

    /**
     * 下角标搜索
     *
     * @param xid 用户ID
     * @return
     */
    private int searchPositionByXid(int xid) {
        int index = -1;
        if (videoList == null || videoList.isEmpty()) {
            return index;
        }
        for (int i = 0; i < videoList.size(); i++) {
            if (videoList.get(i).getXid() == xid) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onOffline(RtcUserEntity rtcUserEntity, int reason) {
        removeUser(rtcUserEntity);
    }

    @Override
    public void onInvite() {
        liveData.setValue(new ViewModelEvent(RTC_INVITE));

    }

    @Override
    public void onOpen() {
        int status = applyStatus();
        if (ApplyStatus.APPLYING == (status)) {
            liveData.setValue(new ViewModelEvent(RTC_APPLYING));
        } else if (ApplyStatus.ALLOW == (status)) {
        } else {
            liveData.setValue(new ViewModelEvent(RTC_OPEN));
        }

    }

    @Override
    public void onClose() {
        liveData.setValue(new ViewModelEvent(RTC_CLOSE));
        removeUpUserList();
        recoverDefault();
    }

    /**
     * 数据重置
     */
    private void recoverDefault() {
        resetDrawValue();
        isDrawPower = false;
        isCloseVideoForZhubo = false;
        isCloseAudioForZhubo = false;
    }

    /**
     * 移除上讲台的学生
     */
    private void removeUpUserList() {
        if (videoList == null && videoList.isEmpty()) {
            return;
        }
        if (videoList.size() == 1 && TextUtils.equals(MemberRole.MEMBER_ROLE_SUPER_ADMIN, videoList.get(0).getRtcUserEntity().getRole())) {
            return;//视频列表只有讲师一人，无需刷新
        }
        VideoStatusData videoStatusDataParams = null;
        for (VideoStatusData videoStatusData : videoList) {
            if (TextUtils.equals(MemberRole.MEMBER_ROLE_SUPER_ADMIN, videoStatusData.getRtcUserEntity().getRole())) {
                videoStatusDataParams = videoStatusData;
                break;
            }
        }
        if (videoStatusDataParams == null) {
            return;
        }
        videoList.clear();
        videoList.add(videoStatusDataParams);
        notifyItemChanged();
    }

    @Override
    public void onConnectionInterrupted() {
        videoLoading(1);
    }

    @Override
    public void onReConnectSuccess() {
        videoLoading(0);
    }

    @Override
    public void onDrawEnable(RtcUserEntity rtcUserEntity) {
        boolean isMe = rtcUserEntity.isMe();
        if (isMe && mWhiteBoardOperator == null && mHtSdk != null) {
            mWhiteBoardOperator = mHtSdk.getWhiteboardOperator();
        }
        callDraw(isMe, true);
        notifyItemChanged(searchPosition(rtcUserEntity), OTMVideoAdapter.PAINT);

    }

    /**
     * 涂鸦权限通知
     *
     * @param isMe
     * @param drawEnable
     */
    private void callDraw(boolean isMe, boolean drawEnable) {
        if (isMe) {
            isDrawPower = drawEnable;
            liveData.setValue(new ViewModelEvent(LIVE_DRAW, drawEnable));
            if (!drawEnable) {
                resetDrawValue();
            }
        } else if (isDrawPower) {
            isDrawPower = false;
            liveData.setValue(new ViewModelEvent(LIVE_DRAW, false));
            resetDrawValue();
        }

    }

    @Override
    public void onDrawDisable(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity == null) {
            return;
        }
        if (rtcUserEntity.isMe() && !isDrawPower) {
            return;
        }
        callDraw(rtcUserEntity.isMe(), false);
        notifyItemChanged(searchPosition(rtcUserEntity), OTMVideoAdapter.PAINT);
    }

    /**
     * 添加视频View
     *
     * @param rtcUserEntity
     * @param videoView
     */
    private void addVideoData(RtcUserEntity rtcUserEntity, View videoView) {
        VideoStatusData videoStatusData = new VideoStatusData(rtcUserEntity, videoView);
        if (videoList.contains(videoStatusData)) {
            int index = videoList.indexOf(videoStatusData);
            videoList.set(index, videoStatusData);
            notifyItemChanged(index);
            return;
        }
        int position = -1;
        if (rtcUserEntity.isMe()) {//当前用户
            if (videoList.size() > 1) {
                videoList.add(1, videoStatusData);
                position = 1;
            } else {
                videoList.add(videoStatusData);
                position = videoList.size() - 1;
            }
        } else if (MemberRole.MEMBER_ROLE_SUPER_ADMIN == rtcUserEntity.getRole()) {//主播
            videoList.add(0, videoStatusData);
            position = 0;
        } else {//其余用户
            videoList.add(videoStatusData);
            position = videoList.size() - 1;
        }

        notifyItemRangeChanged(position, videoList.size());
    }

    /**
     * 局部刷新多媒体
     *
     * @param mRtcUserEntity
     */
    private void updateVideolistMedia(RtcUserEntity mRtcUserEntity) {
        if (mRtcUserEntity == null) {
            return;
        }
        notifyItemChanged(searchPosition(mRtcUserEntity), OTOVideoAdapter.PARTIAL_REFRESH);
    }

    private void videoLoading(int offlineStatus) {
        for (int i = 0; i < videoList.size(); i++) {
            VideoStatusData mVideoStatusData = videoList.get(i);
            if (mVideoStatusData == null) {
                continue;
            }
            mVideoStatusData.setVideoOfflineStatus(offlineStatus);
            notifyItemChanged(i, OTOVideoAdapter.PARTIAL_REFRESH);
        }
    }

    /**
     * 用户申请讲台的状态
     *
     * @return
     */
    public int applyStatus() {
        return mHtSdk != null && mHtSdk.getRtcInfo() == null ? ApplyStatus.NO_APPLY : mHtSdk.getRtcInfo().userApplyStatus;
    }

    /**
     * 是否为自动上讲台
     *
     * @return
     */
    public boolean isAutoUp() {
        return mHtSdk != null && mHtSdk.getRtcInfo() == null ? false : mHtSdk.getRtcInfo().autoUp == 1;
    }

    /**
     * 申请上讲台
     *
     * @param callback
     */
    public void rtcApply(Callback<String> callback) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.apply(callback);
    }

    /**
     * 取消上讲台
     *
     * @param callback
     */
    public void rtcCancel(Callback<String> callback) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.cancel(callback);
    }

    /**
     * 主动下讲台
     *
     * @param callback
     */
    public void rtcDown(final Callback<RtcUserEntity> callback) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.down(new Callback<RtcUserEntity>() {
            @Override
            public void success(RtcUserEntity result) {
                recoverDefault();
                removeUser(result);
                if (callback != null) {
                    callback.success(result);
                }
            }

            @Override
            public void failed(String failed) {
                if (callback != null) {
                    callback.failed(failed);
                }
            }
        });
    }


    @Override
    public void receiveAward(AwardEntity awardEntity) {
        if (awardEntity == null) {
            return;
        }
        liveData.setValue(new ViewModelEvent(RTC_AWARD, awardEntity));
        int position = searchPositionByXid(awardEntity.getToXid());
        if (position > -1) {
            VideoStatusData videoStatusData = videoList.get(position);
            videoStatusData.getRtcUserEntity().setScore(awardEntity.getScoreTotal());
            notifyItemChanged(position, OTMVideoAdapter.AWARD);
        }
    }

    @Override
    public void voteStart(VoteEntity voteEntity) {
        liveData.setValue(new ViewModelEvent(LIVE_VOTE_START, voteEntity));
    }

    @Override
    public void voteStop(VotePubEntity votePubEntity) {
        liveData.setValue(new ViewModelEvent(LIVE_VOTE_STOP, votePubEntity));
    }

    @Override
    public void voteDel(VoteDelEntity voteDelEntity) {
        liveData.setValue(new ViewModelEvent(LIVE_VOTE_DEL, voteDelEntity));
    }

    /**
     * 获取UID
     *
     * @return
     */
    public String getUid() {
        if (HtSdk.getInstance().getRoomInfo() == null || HtSdk.getInstance().getRoomInfo().getUser() == null) {
            return "";
        }
        return HtSdk.getInstance().getRoomInfo().getUser().getUid();
    }

    /**
     * 获取所有的投票记录
     */
    private void getAllVotes() {
        if (mHtSdk == null) {
            return;
        }
        mHtSdk.getAllVotes(new int[]{1, 2}, new Callback() {
            @Override
            public void success(Object result) {
                try {
                    liveData.setValue(new ViewModelEvent(LIVE_VOTE_ALL, VoteDataUtils.transfer(result)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(String failed) {

            }
        });
    }

    /**
     * 是否正在直播
     *
     * @return
     */
    public boolean isLiving() {
        return mHtSdk == null ? false : TextUtils.equals(LiveStatus.START, mHtSdk.getInitLiveStatus());
    }
}
