package com.android.jwjy.wtjyproduct.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import com.android.jwjy.wtjyproduct.adapter.OTOVideoAdapter;
import com.android.jwjy.wtjyproduct.bean.ViewModelEvent;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;

/**
 * Created by ccy on 2019/5/8/16:48
 */
public class LiveOneToOneViewModel extends BaseLiveRtcViewModel {
    private boolean isShowDrawTips = false;

    public LiveOneToOneViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    void initData() {
        initVideoList();
    }


    /**
     * 音频开关
     */
    public void switchMedia(int type, boolean isOpen) {
        if (mRtcOperatorProxy == null) {
            return;
        }
        if (type == OTOVideoAdapter.AUDIO) {
            if (isOpen) {
                mRtcOperatorProxy.closeAudio(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                    }

                    @Override
                    public void failed(String failed) {
                        liveData.setValue(new ViewModelEvent(ERROR_MESSAGE, failed));
                    }
                });
            } else {
                mRtcOperatorProxy.openAudio(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                    }

                    @Override
                    public void failed(String failed) {
                        liveData.setValue(new ViewModelEvent(ERROR_MESSAGE, failed));
                    }
                });
            }
        } else {
            if (isOpen) {
                mRtcOperatorProxy.closeVideo(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                    }

                    @Override
                    public void failed(String failed) {
                        liveData.setValue(new ViewModelEvent(ERROR_MESSAGE, failed));
                    }
                });
            } else {
                mRtcOperatorProxy.openVideo(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        switchSuccess(result);
                    }

                    @Override
                    public void failed(String failed) {
                        liveData.setValue(new ViewModelEvent(ERROR_MESSAGE, failed));
                    }
                });
            }
        }
    }

    private void switchSuccess(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity == null) {
            return;
        }
        if (rtcUserEntity.getRole().equals(MemberRole.MEMBER_ROLE_USER)) {
            videoList.get(1).setRtcUserEntity(rtcUserEntity);
            notifyItemChanged(1, OTOVideoAdapter.PARTIAL_REFRESH);
        }
    }

    private void notifyItemChanged() {
        notifyItemChanged(-1, -1);
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
     * 添加默认的视图
     */
    private void initVideoList() {
        videoList.clear();
        RtcUserEntity spadin = new RtcUserEntity();
        spadin.setRole(MemberRole.MEMBER_ROLE_SUPER_ADMIN);
        RtcUserEntity user = new RtcUserEntity();
        user.setRole(MemberRole.MEMBER_ROLE_USER);
        videoList.add(new VideoStatusData(spadin, null));
        videoList.add(new VideoStatusData(user, null));
    }

    @Override
    public void onLiveStop() {
        reset();
        liveData.setValue(new ViewModelEvent(LIVE_STOP));
    }

    private void reset() {
        isShowDrawTips = false;
        resetVideoAdapter();
    }


    private void resetVideoAdapter() {
        initVideoList();
        notifyItemChanged();
    }

    @Override
    public void memberForceout() {

    }

    @Override
    public void memberKick() {

    }


    @Override
    public void onVideoClose(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            liveData.setValue(new ViewModelEvent(VIDEO_ENABLE, 0));
        }
        updateRtcVideo(rtcUserEntity);
    }

    @Override
    public void onVideoOpen(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            liveData.setValue(new ViewModelEvent(VIDEO_ENABLE, 1));
        }
        updateRtcVideo(rtcUserEntity);
    }

    @Override
    public void onAudioOpen(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            liveData.setValue(new ViewModelEvent(AUDIO_ENABLE, 1));
        }
        updateRtcVideo(rtcUserEntity);
    }

    @Override
    public void onAudioClose(RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity.isMe()) {
            liveData.setValue(new ViewModelEvent(AUDIO_ENABLE, 0));
        }
        updateRtcVideo(rtcUserEntity);
    }

    @Override
    public void onKick(RtcUserEntity rtcUserEntity) {

    }

    private void updateRtcVideo(RtcUserEntity mRtcUserEntity) {
        if (mRtcUserEntity == null) {
            return;
        }
        int index = mRtcUserEntity.isMe() ? 1 : 0;
        videoList.get(index).setRtcUserEntity(mRtcUserEntity);
        notifyItemChanged(index, OTOVideoAdapter.PARTIAL_REFRESH);
    }

    @Override
    public void onUp(RtcUserEntity rtcUserEntity, View videoView) {
        if (videoView == null || rtcUserEntity == null) {
            return;
        }
        if (rtcUserEntity.isMe() || rtcUserEntity.getRole() == MemberRole.MEMBER_ROLE_SUPER_ADMIN) {
            int index = rtcUserEntity.isMe() ? 1 : 0;
            VideoStatusData videoStatusData = videoList.get(index);
            videoStatusData.setRtcUserEntity(rtcUserEntity);
            videoStatusData.setVideoView(videoView);
            videoStatusData.setVideoOfflineStatus(0);
            notifyItemChanged();
        }
    }

    @Override
    public void onDown(RtcUserEntity rtcUserEntity) {

    }

    @Override
    public void onOffline(RtcUserEntity rtcUserEntity, int reason) {

    }

    @Override
    public void onInvite() {

    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onConnectionInterrupted() {
        videoLoading(1);
    }

    @Override
    public void onReConnectSuccess() {
        videoLoading(0);
    }

    private void videoLoading(int offlineStatus) {
        for (VideoStatusData videoStatusData : videoList) {
            videoStatusData.setVideoOfflineStatus(offlineStatus);
        }
        notifyItemChanged(0, OTOVideoAdapter.PARTIAL_REFRESH);
        notifyItemChanged(1, OTOVideoAdapter.PARTIAL_REFRESH);
    }

    @Override
    public void onDrawEnable(RtcUserEntity rtcUserEntity) {
        if (!rtcUserEntity.isMe()) {
            return;
        }
        if (mWhiteBoardOperator == null && mHtSdk != null) {
            mWhiteBoardOperator = mHtSdk.getWhiteboardOperator();
        }
        isShowDrawTips = true;
        liveData.setValue(new ViewModelEvent(LIVE_DRAW, 1));
    }


    @Override
    public void onDrawDisable(RtcUserEntity rtcUserEntity) {
        if (!isShowDrawTips) {
            return;
        }
        isShowDrawTips = false;
        resetDrawValue();
        liveData.setValue(new ViewModelEvent(LIVE_DRAW, 0));
    }


    public void reload() {

        if (mHtSdk == null) {
            return;
        }
        mHtSdk.reload();
    }


    public void release() {
        onVideoDataChangeListener = null;
        mRtcOperatorProxy = null;
        mWhiteBoardOperator = null;
        super.release();
    }
}
