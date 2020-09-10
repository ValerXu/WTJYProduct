package com.android.jwjy.wtjyproduct.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.databinding.ItemDialogFragmentOtmVoteBinding;
import com.android.jwjy.wtjyproduct.databinding.ItemVoteListEmptyViewBinding;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.util.List;

/**
 * Created by ccy on 2019/5/22/11:41
 */
public class OTMVoteListAdapter extends BaseDatabindingAdapter<Object> {
    private final int EMPTY_VIEW = 0;
    private final int NOT_EMPTY_VIEW = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.item_dialog_fragment_otm_vote;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //展示空视图或者调用传入adapter方法
        mContext = parent.getContext();
        if (viewType == EMPTY_VIEW) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_vote_list_empty_view, parent, false);
            return new ViewHolder(binding, getVariableId());
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Object> holder, final int position, @NonNull List<Object> payloads) {
        if (holder.getBinding() instanceof ItemVoteListEmptyViewBinding) {
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
        Object data = getItem(position);
        if (data == null) {
            return;
        }
        ItemDialogFragmentOtmVoteBinding itemDialogFragmentOtmVoteBinding = (ItemDialogFragmentOtmVoteBinding) holder.getBinding();
        if (data instanceof VoteEntity) {
            VoteEntity voteEntity = (VoteEntity) data;
            boolean isVoted = voteEntity.getVoted() == 1;
            boolean isVoteStop = !(TextUtils.isEmpty(voteEntity.getEndTime()) || TextUtils.equals("0", voteEntity.getEndTime()));
            String label = TextUtils.isEmpty(voteEntity.getLabel()) ? voteEntity.getTitle() : voteEntity.getLabel();
            itemDialogFragmentOtmVoteBinding.tvLabel.setText(label);
            itemDialogFragmentOtmVoteBinding.nickname.setText(voteEntity.getNickname());
            itemDialogFragmentOtmVoteBinding.tvVoteStartTime.setText(String.format("(%s)", voteEntity.getStartTime()));
            itemDialogFragmentOtmVoteBinding.tvVoteStatus.setText(isVoteStop ? "已结束" : isVoted ? "已投票" : "投票中");
            itemDialogFragmentOtmVoteBinding.tvVoteStatus.setTextColor(isVoteStop ? Color.parseColor("#6D829E") : isVoted ? Color.parseColor("#6D829E") : Color.parseColor("#FF9F2D"));
            itemDialogFragmentOtmVoteBinding.btnCheck.setText(isVoteStop ? "查看" : isVoted ? "查看" : "投票");
            itemDialogFragmentOtmVoteBinding.btnCheck.setSelected(!(isVoteStop ||isVoted));
        } else {
            VotePubEntity mVotePubEntity = (VotePubEntity) data;
            String label = TextUtils.isEmpty(mVotePubEntity.getLabel()) ? mVotePubEntity.getTitle() : mVotePubEntity.getLabel();
            itemDialogFragmentOtmVoteBinding.tvLabel.setText(label);
            itemDialogFragmentOtmVoteBinding.nickname.setText(mVotePubEntity.getNickname());
            itemDialogFragmentOtmVoteBinding.tvVoteStartTime.setText(String.format("(%s)", mVotePubEntity.getStartTime()));
            itemDialogFragmentOtmVoteBinding.tvVoteStatus.setText("已结束");
            itemDialogFragmentOtmVoteBinding.tvVoteStatus.setTextColor(Color.parseColor("#6D829E"));
            itemDialogFragmentOtmVoteBinding.btnCheck.setText("查看");
            itemDialogFragmentOtmVoteBinding.btnCheck.setSelected(false);
        }
        itemDialogFragmentOtmVoteBinding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnChildClickListener != null) {
                    mOnChildClickListener.onClick(v, getItem(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (dataList.size() == 0) {
            return 1;
        }
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.size() > 0) {
            return NOT_EMPTY_VIEW;
        }
        return EMPTY_VIEW;
    }

    @Override
    protected int getVariableId() {
        return 0;
    }

}
