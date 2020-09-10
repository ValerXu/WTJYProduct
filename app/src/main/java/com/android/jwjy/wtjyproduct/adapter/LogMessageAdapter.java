package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.bean.LogBean;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.ExpressionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 小班聊天适配器
 */
public class LogMessageAdapter extends BAdapter<LogBean> {
    private String message;
    private int icon;

    public LogMessageAdapter(Context context) {
        super(context);
        ExpressionUtil.tvImgWidth = DimensionUtils.dip2px(context, 55);
        ExpressionUtil.tvImgHeight = DimensionUtils.dip2px(context, 45);
    }

    public void appendList(LogBean logBean) {
        itemList.add(logBean);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleViewHolder simpleViewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.small_room_chat_item_layout, null);
            simpleViewHolder = new SimpleViewHolder(convertView);
            convertView.setTag(simpleViewHolder);
        } else {
            simpleViewHolder = (SimpleViewHolder) convertView.getTag();
        }
        setChatMessage(simpleViewHolder, itemList.get(position));
        return convertView;
    }

    /**
     * 设置聊天信息
     *
     * @param simpleViewHolder
     */
    private void setChatMessage(SimpleViewHolder simpleViewHolder, LogBean logBean) {
        LogBean chatMessageEntity = logBean;
        if (simpleViewHolder != null) {
            simpleViewHolder.nameTv.setText("");
            int name = chatMessageEntity.getLevel();
            simpleViewHolder.nameTv.setText(name + ":");

            String content = chatMessageEntity.getMessage();
            if (content != null & !TextUtils.isEmpty(content)) {
                simpleViewHolder.contentTv.setText(content);
            }
        }

    }

    class SimpleViewHolder {
        @BindView(R.id.name)
        TextView nameTv;
        @BindView(R.id.content)
        TextView contentTv;

        SimpleViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }


}
