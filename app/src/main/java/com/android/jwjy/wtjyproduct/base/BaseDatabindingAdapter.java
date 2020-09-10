package com.android.jwjy.wtjyproduct.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDatabindingAdapter<T> extends RecyclerView.Adapter<BaseDatabindingAdapter.ViewHolder<T>> {
    protected List<T> dataList = new ArrayList<>();
    /**
     * 选中位置
     */
    protected int selectPosition = 0;
    protected Context mContext;
    private OnItemClickListener<T> mOnItemClickListener;
    protected OnChildClickListener<T> mOnChildClickListener;

    public BaseDatabindingAdapter() {
        this(null);
    }

    public BaseDatabindingAdapter(List<T> list) {
        if (list != null) {
            this.dataList = list;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutId(), parent, false);
        return new ViewHolder(binding, getVariableId());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final T data = getItem(position);
        holder.binding(data);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<T> holder, final int position, @NonNull List<Object> payloads) {
        final T data = getItem(position);
        //设置item点击监听
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenSelectFunction()) {
                    setSelectPosition(position);
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(data, position);
                }
            }
        });
        holder.binding(data);
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return (position >= 0 && position < dataList.size()) ? dataList.get(position) : null;
    }


    public void setDataList(List<T> list) {
        if (dataList != list) {
            this.dataList = list;
        }
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void addData(T data) {
        if (dataList == null) {
            return;
        }
        dataList.add(data);
    }


    /**
     * 是否开启选中功能
     *
     * @return
     */
    public boolean isOpenSelectFunction() {
        return false;
    }

    /***
     * 设置 选中状态
     * @param selectPosition
     */
    public void setSelectPosition(int selectPosition) {
        if (this.selectPosition == selectPosition) {
            return;
        }
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    protected abstract int getLayoutId();

    protected abstract int getVariableId();

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * item 点击
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {
        void onItemClick(T data, int position);
    }

    /**
     * 监听子view
     *
     * @param listener
     */
    public void setOnChildClickListener(OnChildClickListener<T> listener) {
        this.mOnChildClickListener = listener;
    }

    /**
     * 子view
     *
     * @param <T>
     */
    public interface OnChildClickListener<T> {
        void onClick(View view, T data, int position);
    }

    public static class ViewHolder<T> extends RecyclerView.ViewHolder {

        private ViewDataBinding mBinding;
        private int variableId;

        public ViewHolder(ViewDataBinding viewDataBinding, int variableId) {
            super(viewDataBinding.getRoot());
            this.mBinding = viewDataBinding;
            this.variableId = variableId;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }


        public ViewDataBinding getBinding() {
            return mBinding;
        }

        public void binding(T data) {
            mBinding.setVariable(variableId, data);
            mBinding.executePendingBindings();
        }

    }
}
