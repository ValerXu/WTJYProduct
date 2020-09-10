package com.android.jwjy.wtjyproduct.entity;

import com.talkfun.sdk.model.converter.DataConverter;

import org.json.JSONObject;

/**
 * Created by ccy on 2019/4/24/11:33
 */
public class PlaybackDataConverter implements DataConverter<PrePlaybackEntity> {
    private PrePlaybackEntity mPrePlaybackEntity;

    @Override
    public PrePlaybackEntity convert(JSONObject result) {
        mPrePlaybackEntity = new PrePlaybackEntity();
        if (result == null) {
            return mPrePlaybackEntity;
        }
        JSONObject baseData = result.optJSONObject("data");
        if (baseData == null) {
            return mPrePlaybackEntity;
        }
        String videoType = baseData.optString("videoType", "1");
        mPrePlaybackEntity.setVideoType(videoType);
        return mPrePlaybackEntity;
    }
}
