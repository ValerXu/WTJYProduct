package com.android.jwjy.wtjyproduct.adapter;//package com.talkfun.cloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.R;
import com.talkfun.sdk.module.ChatEntity;

/**
 * Created by ccy on 2019/4/23/14:54
 */

public class ChatEmptyViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter mAdapter; //需要装饰的Adapter
    private Context mContext;
    private final int EMPTY_VIEW = 0;
    private final int NOT_EMPTY_VIEW = 1;

    public ChatEmptyViewAdapter(Context context, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //展示空视图或者调用传入adapter方法
        if (viewType == EMPTY_VIEW) {
            return new EmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_empty_view, parent, false));
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            return;
        }
        mAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        //获取传入adapter的条目数，没有则返回 1
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            return mAdapter.getItemCount();
        }
        //位空视图保留一个条目
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        //根据传入adapter来判断是否有数据
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            return mAdapter.getItemViewType(position);
        }
        return EMPTY_VIEW;
    }

    public void appendList(ChatEntity value) {
        ((OTOChatAdapter) mAdapter).appendList(value);
    }


    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}