package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.R;
import com.talkfun.widget.ColorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ccy on 2018/9/3/23:57
 */
public class ColorAdapter extends BAdapter<String> {
    private String checkColor = "#330000";

    public ColorAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            View view = loadView(R.layout.panel_color_item, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cmdCV.setColor(Color.parseColor(itemList.get(position)));
        holder.cmdCV.setCheckedBorderColor(Color.parseColor(checkColor));
        holder.cmdCV.setChecked(selectItem == position);

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.color_cv)
        ColorView cmdCV;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
