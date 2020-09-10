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

import java.util.List;


/**
 * Created by ccy on 2019/4/9/15:38
 */
public class OTOChatAdapter extends BaseDatabindingAdapter<ChatEntity> {
    private static final int MXA_LENGTH = 200;

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
        int layoutId = viewType == TYPE_MESSAGE_RIGHT ? R.layout.item_live_rtc_chat_right : R.layout.item_live_rtc_chat_left;
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutId, parent, false);
        return new ViewHolder(binding, BR.chatEntity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<ChatEntity> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList == null || dataList.isEmpty()) {
            return super.getItemViewType(position);
        }
        ChatEntity chatEntity = dataList.get(position);
        return chatEntity.isMe() ? TYPE_MESSAGE_RIGHT : TYPE_MESSAGE_LEFT;
    }

    public void appendList(ChatEntity chatMessageEntity) {
        dataList.add(chatMessageEntity);
        if (dataList.size() >= MXA_LENGTH) {
            dataList.remove(0);
        }
        notifyDataSetChanged();
    }
}
