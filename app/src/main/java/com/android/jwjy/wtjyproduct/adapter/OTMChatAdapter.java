package com.android.jwjy.wtjyproduct.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.BR;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.rtc.entity.AwardEntity;

import java.util.List;

/**
 * Created by ccy on 2019/5/20/15:20
 */
public class OTMChatAdapter extends BaseDatabindingAdapter<Object> {
    private static final int TYPE_MESSAGE = 1;
    private static final int TYPE_AWARD = 2;
    private static final int TYPE_MESSAGE_LEFT = 3;
    private static final int TYPE_MESSAGE_RIGHT = 4;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected int getVariableId() {
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if (viewType == TYPE_AWARD) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_live_rtc_chat_award, parent, false);
            return new ViewHolder(binding, BR.awardEntity);
        } else {
            int layoutId = viewType == TYPE_MESSAGE_RIGHT ? R.layout.item_live_rtc_chat_right : R.layout.item_live_rtc_chat_left;
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutId, parent, false);
            return new ViewHolder(binding, BR.chatEntity);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Object> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList == null || dataList.isEmpty()) {
            return super.getItemViewType(position);
        }
        if (dataList.get(position) instanceof ChatEntity) {
            ChatEntity chatEntity = (ChatEntity) dataList.get(position);

            return chatEntity.isMe() ? TYPE_MESSAGE_RIGHT : TYPE_MESSAGE_LEFT;
        } else if (dataList.get(position) instanceof AwardEntity) {
            return TYPE_AWARD;
        }
        return super.getItemViewType(position);
    }
}
