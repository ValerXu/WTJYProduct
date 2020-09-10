package com.android.jwjy.wtjyproduct.impl;

import android.view.View;

import com.talkfun.sdk.rtc.entity.RtcUserEntity;
import com.talkfun.sdk.rtc.interfaces.OnRtcMemberListener;

/**
 * Created by ccy on 2019/8/9/14:26
 */
public abstract class OnRtcMemberImpl implements OnRtcMemberListener {
    @Override
    public void onKick(RtcUserEntity rtcUserEntity) {

    }

    @Override
    public void onUp(RtcUserEntity rtcUserEntity, View videoView) {

    }

    @Override
    public void onDown(RtcUserEntity rtcUserEntity) {

    }

    @Override
    public void onOffline(RtcUserEntity rtcUserEntity, int reason) {

    }

    /**
     * 学员被邀请上讲台
     */
    @Override
    public void onInvite() {

    }
}
