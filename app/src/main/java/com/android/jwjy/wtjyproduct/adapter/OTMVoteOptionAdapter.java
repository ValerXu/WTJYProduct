package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.talkfun.sdk.module.VoteDetailEntity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OTMVoteOptionAdapter extends BAdapter<VoteDetailEntity.StatsListBean> {
    private final int SINGLE = 1;
    private final int MULTIPLE = 2;
    private int checkboxStyle = SINGLE;
    private char[] choice = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private boolean isAllItemEnable = true;
    private boolean isShowAnswer;

    public OTMVoteOptionAdapter(Context context) {
        super(context);
        selectItem = -1;
    }

    public void setCheckboxStyle(boolean style) {
        checkboxStyle = style ? SINGLE : MULTIPLE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.item_otm_vote_option, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VoteDetailEntity.StatsListBean option = itemList.get(position);
        viewHolder.itemContentTV.setText(option.getOp());
        viewHolder.itemCb.setAlpha(isAllItemEnable ? 1f : 0.5f);
        if (checkboxStyle == SINGLE) {
            viewHolder.itemCb.setChecked(selectItem == position);
        } else {
            viewHolder.itemCb.setChecked(selectMultiItemList[position] == 1);
        }
        if (position <= choice.length)
            viewHolder.choiceTV.setText(choice[position] + "");
//        viewHolder.itemPercentLL.setVisibility(isShowAnswer ? View.VISIBLE : View.GONE);
        if (isShowAnswer) {
            viewHolder.itemProgressPB.setVisibility(View.VISIBLE);
            viewHolder.percentTV.setVisibility(View.VISIBLE);
            viewHolder.itemProgressPB.setProgress(Integer.parseInt(option.getPercent()));
            String percent = String.format("%s票(%s%%)", option.getOpNum(), option.getPercent());
            viewHolder.percentTV.setText(percent);
        } else {
            viewHolder.itemProgressPB.setVisibility(View.GONE);
            viewHolder.percentTV.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setIsShowAnswer(boolean isShowAnswer) {
        this.isShowAnswer = isShowAnswer;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return isAllItemEnable;
    }

    @Override
    public boolean isEnabled(int position) {
        return isAllItemEnable;
    }

    public void disableAllItemChoser() {
        isAllItemEnable = false;
        notifyDataSetChanged();
    }

    public void enableItemChoser() {
        isAllItemEnable = true;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R.id.cb_check)
        CheckBox itemCb;
        @BindView(R.id.tv_choice)
        TextView choiceTV;
        @BindView(R.id.tv_choice_content)
        TextView itemContentTV;
        /*   @BindView(R.id.linearLayout)
           LinearLayout itemPercentLL;*/
        @BindView(R.id.pb_progress)
        ProgressBar itemProgressPB;
        @BindView(R.id.tv_percent)
        TextView percentTV;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
//    public interface
}