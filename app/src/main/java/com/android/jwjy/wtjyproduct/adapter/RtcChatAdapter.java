package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.android.jwjy.wtjyproduct.util.ExpressionUtil;
import com.talkfun.sdk.module.ChatEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 小班聊天适配器
 */
public class RtcChatAdapter extends BAdapter<ChatEntity> {
    private String message;
    private int icon;
    private static final int color = Color.parseColor("#B356acf5");
    private static final int LIMIT_NICKNAME_NUM = 8;

    public RtcChatAdapter(Context context) {
        super(context);
        ExpressionUtil.tvImgWidth = DimensionUtils.dip2px(context, 55);
        ExpressionUtil.tvImgHeight = DimensionUtils.dip2px(context, 45);
    }

    public void appendList(ChatEntity chatMessageEntity) {
        itemList.add(chatMessageEntity);
        if (itemList.size() >= MXA_LENGTH) {
            itemList.remove(0);
        }
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
     * @param chatEntity
     */
    private void setChatMessage(SimpleViewHolder simpleViewHolder, ChatEntity chatEntity) {
//        ChatEntity chatMessageEntity = chatEntity;
//        if (simpleViewHolder != null) {
//            simpleViewHolder.nameTv.setText("");
//            String name = chatMessageEntity.getNickname();
//            if (name != null & !TextUtils.isEmpty(name)) {
//                simpleViewHolder.nameTv.setText(name + ":");
//            }
//            String content = chatMessageEntity.getMsg();
//            if (content != null & !TextUtils.isEmpty(content)) {
//                SpannableString spannableString = ExpressionUtil.getExpressionString(context, content, "mipmap");
//                simpleViewHolder.contentTv.setText(spannableString);
//            }
//        }
        ChatEntity chatMessageEntity = chatEntity;
        if (simpleViewHolder != null) {
            String name = limitNickName(chatMessageEntity.getNickname());
            String content = chatMessageEntity.getMsg();
            if (content != null & !TextUtils.isEmpty(content)) {
                String msg = name + content;
                SpannableString spannableString = ExpressionUtil.getExpressionString(context, msg, "mipmap");
                spannableString.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                simpleViewHolder.contentTv.setText(spannableString);
            }
        }

    }
    private String limitNickName(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            return "";
        }
        char[] names = nickname.toCharArray();
        if (names.length >= LIMIT_NICKNAME_NUM) {
            return nickname.substring(0, LIMIT_NICKNAME_NUM) + "...:";
        }
        return nickname + ":";
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
