package com.android.jwjy.wtjyproduct.adapter;

import android.support.annotation.NonNull;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.databinding.ItemPopDrawBindingImpl;
import java.util.List;

/**
 * Created by ccy on 2019/5/8/20:13
 */
public class OTODrawAdapter extends BaseDatabindingAdapter<Integer> {
    @Override
    protected int getLayoutId() {
        return R.layout.item_pop_draw;
    }
    @Override
    public boolean isOpenSelectFunction() {
        return true;
    }
    @Override
    protected int getVariableId() {
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Integer> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        ItemPopDrawBindingImpl item = (ItemPopDrawBindingImpl) holder.getBinding();
        item.drawTypeIv.setSelected(selectPosition == position);

        Integer resourceId = getItem(position);
        if(resourceId != null){
            item.drawTypeIv.setImageResource(resourceId);
        }

    }
}
