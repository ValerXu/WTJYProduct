package com.android.jwjy.wtjyproduct.helper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.android.jwjy.wtjyproduct.dialog.OTMVoteDialogFragment;
import com.talkfun.sdk.event.HtVoteListener;
import com.talkfun.sdk.module.VoteDelEntity;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccy on 2019/5/22/14:33
 */
public class OTMVoteHelper implements HtVoteListener {
    private WeakReference<Context> contextWeakReference;
    private List<Object> voteList = new ArrayList<>();
    /**
     * 投票弹框
     */
    private OTMVoteDialogFragment otmVoteDialogFragment;

    public OTMVoteHelper(Context context) {
        contextWeakReference = new WeakReference<>(context);
        otmVoteDialogFragment = OTMVoteDialogFragment.create();
        otmVoteDialogFragment.addVoteList(voteList);

    }

    public void show() {
        AppCompatActivity appCompatActivity = getAppCompatActivity();
        if (appCompatActivity == null) {
            return;
        }
        if (otmVoteDialogFragment.isAdded())
            return;
        otmVoteDialogFragment.show(appCompatActivity.getSupportFragmentManager(), "OTMVoteDialogFragment");
    }

    public AppCompatActivity getAppCompatActivity() {
        if (contextWeakReference.get() == null) {
            return null;
        }
        return (AppCompatActivity) contextWeakReference.get();
    }

    public void addVoteList(List<Object> voteList) {
        if (otmVoteDialogFragment == null) {
            otmVoteDialogFragment = OTMVoteDialogFragment.create();
        }
        this.voteList.clear();
        this.voteList.addAll(voteList);
        otmVoteDialogFragment.notifyDataSetChanged();
//        otmVoteDialogFragment.addVoteList(voteList);
    }

    @Override
    public void voteStart(VoteEntity voteEntity) {
        voteList.add(0, voteEntity);
        otmVoteDialogFragment.notifyDataSetChanged();
    }

    @Override
    public void voteStop(VotePubEntity votePubEntity) {
        int position = searchIndex(votePubEntity);
        otmVoteDialogFragment.voteStop(votePubEntity);
        if (position == -1) {
            voteList.add(votePubEntity);
            otmVoteDialogFragment.notifyDataSetChanged();
        } else {
            voteList.set(position, votePubEntity);
            otmVoteDialogFragment.notifyItemChanged(position);
        }
    }

    /**
     * 下角标搜索
     *
     * @param
     * @return
     */
    private int searchIndex(VotePubEntity votePubEntity) {
        int index = -1;
        if (voteList == null || voteList.isEmpty()) {
            return index;
        }
        for (int i = 0; i < voteList.size(); i++) {
            if (voteList.get(i) instanceof VoteEntity) {
                VoteEntity voteEntity = (VoteEntity) voteList.get(i);
                if (TextUtils.equals(voteEntity.getVid(), votePubEntity.getVid())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 判断是否显示
     *
     * @return
     */
    public boolean isShowing() {
        if (otmVoteDialogFragment != null && otmVoteDialogFragment.getDialog() != null
                && otmVoteDialogFragment.getDialog().isShowing()) {
            return true;
        }
        return false;
    }

    public void dismiss() {
        if (otmVoteDialogFragment != null && otmVoteDialogFragment.isShow()) {
            otmVoteDialogFragment.dismiss();
        }
    }

    /**
     * 删除投票
     *
     * @param voteDelEntity
     */
    public void voteDel(VoteDelEntity voteDelEntity) {
        if (voteDelEntity == null || voteList == null || voteList.isEmpty()) {
            return;
        }
        int index = -1;
        for (int i = 0; i < voteList.size(); i++) {
            if (voteList.get(i) instanceof VoteEntity) {
                VoteEntity voteEntity = (VoteEntity) voteList.get(i);
                if (TextUtils.equals(voteEntity.getVid(), voteDelEntity.getVid())) {
                    index = i;
                    break;
                }
            } else if (voteList.get(i) instanceof VotePubEntity) {
                VotePubEntity votePubEntity = (VotePubEntity) voteList.get(i);
                if (TextUtils.equals(votePubEntity.getVid(), voteDelEntity.getVid())) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) {
            return;
        }
        otmVoteDialogFragment.voteDel(voteDelEntity);
        voteList.remove(index);
        otmVoteDialogFragment.notifyDataSetChanged();
    }
}
