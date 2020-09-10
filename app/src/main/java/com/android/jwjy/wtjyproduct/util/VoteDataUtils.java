package com.android.jwjy.wtjyproduct.util;

import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 投票列表解析
 * Created by ccy on 2019/5/23/16:17
 */
public class VoteDataUtils {
    public static List<Object> transfer(Object result) throws JSONException {
        List<Object> mVoteList = new ArrayList<>();
        if (result == null) {
            return mVoteList;
        }
        JSONObject jsonObject = new JSONObject(result.toString());
        int code = jsonObject.optInt("code", -1);
        if (code == -1) {
            return mVoteList;
        }
        JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("votes");
        for (int i = 0; i < jsonArray.length(); i++) {
            Object string = jsonArray.get(i);
            if (string == null) {
                continue;
            }
            if (string.toString().contains("vote:new")) {
                mVoteList.add(VoteEntity.objectFromData(string.toString()));
            } else if (string.toString().contains("vote:pub")) {
                mVoteList.add(VotePubEntity.objectFromData(string.toString()));
            }
        }
        return mVoteList;
    }
}
