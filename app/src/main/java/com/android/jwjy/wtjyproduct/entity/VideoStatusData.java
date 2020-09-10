package com.android.jwjy.wtjyproduct.entity;

import android.view.View;

import com.talkfun.sdk.rtc.entity.RtcUserEntity;

public class VideoStatusData {
    private int xid;
    private View mView;
    private RtcUserEntity rtcUserEntity;


    public int videoOfflineStatus = 0;

    public VideoStatusData(RtcUserEntity rtcUserEntity, View mView) {
        this.mView = mView;
        this.rtcUserEntity = rtcUserEntity;
    }

    public View getVideoView() {
        return mView;
    }

    public void setVideoView(View mView) {
        this.mView = mView;
    }

    public RtcUserEntity getRtcUserEntity() {
        return rtcUserEntity;
    }

    public void setRtcUserEntity(RtcUserEntity rtcUserEntity) {
        this.rtcUserEntity = rtcUserEntity;
        this.xid = this.rtcUserEntity.getXid();
    }

    public void setDrawPower(int drawPower) {
        if (this.rtcUserEntity != null) {
            this.rtcUserEntity.setDrawPower(drawPower);
        }
    }

    public int getXid() {
        return this.rtcUserEntity == null ? 0 : this.rtcUserEntity.getXid();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof VideoStatusData) {
            VideoStatusData mVideoStatusData = (VideoStatusData) obj;
            return rtcUserEntity.getXid() == mVideoStatusData.getRtcUserEntity().getXid();
        }
        return false;
    }

    public int getVideoOfflineStatus() {
        return videoOfflineStatus;
    }

    public void setVideoOfflineStatus(int videoOfflineStatus) {
        this.videoOfflineStatus = videoOfflineStatus;
    }


    @Override
    public int hashCode() {
        return rtcUserEntity.getXid();
    }
}
