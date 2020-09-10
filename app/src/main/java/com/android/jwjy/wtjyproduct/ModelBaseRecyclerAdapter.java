/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dayuer on 19/7/2.
 * 基本的适配器（在课程表中的分组适配器中会使用）
 */
@SuppressWarnings("unused")
public abstract class ModelBaseRecyclerAdapter<T extends ClassBean> extends RecyclerView.Adapter {

    protected LayoutInflater mInflater;
    protected List<T> mItems;
    private OnItemClickListener onItemClickListener;
    private OnClickListener onClickListener;

   public ModelBaseRecyclerAdapter(Context context) {
        this.mItems = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position, long itemId) {
                if (onItemClickListener != null || mItems != null) {
                    if (position < mItems.size())
                        onItemClickListener.onItemClick(position, itemId, mItems.get(position));
                }
            }
        };

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = onCreateDefaultViewHolder(parent, viewType);
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(onClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(holder, mItems.get(position), position);
    }

    protected abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type);

    protected abstract void onBindViewHolder(RecyclerView.ViewHolder holder, T item, int position);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    void addAll(List<T> items) {
        if (items != null && items.size() > 0) {
            mItems.addAll(items);
            notifyItemRangeInserted(mItems.size(), items.size());
        }
    }

    final void addItem(T item) {
        if (item != null) {
            this.mItems.add(item);
            notifyItemChanged(mItems.size());
        }
    }

    final List<T> getItems() {
        return mItems;
    }


    final T getItem(int position) {
        if (position < 0 || position >= mItems.size())
            return null;
        return mItems.get(position);
    }

    static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }


    interface OnItemClickListener {
        void onItemClick(int position, long itemId, ClassBean item);
    }

    public final void removeItem(T item) {
        if (this.mItems.contains(item)) {
            int position = mItems.indexOf(item);
            this.mItems.remove(item);
            notifyItemRemoved(position);
        }
    }

    protected final void removeItem(int position) {
        if (this.getItemCount() > position) {
            this.mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected final void clear(){
        mItems.clear();
        notifyDataSetChanged();
    }
}
