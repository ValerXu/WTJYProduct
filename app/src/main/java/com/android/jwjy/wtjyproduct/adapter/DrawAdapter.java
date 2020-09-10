package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.jwjy.wtjyproduct.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ccy on 2018/9/3/23:56
 */
public class DrawAdapter extends BAdapter<Integer> {
    public DrawAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            View view = loadView(R.layout.panel_cmd_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Drawable drawable = context.getResources().getDrawable(itemList.get(position));
        holder.bg_fl.setBackgroundColor(selectItem == position ? context.getResources().getColor(R.color.panel_cmd_bg) : context.getResources().getColor(R.color.transparency));
        holder.cmdBtn.setImageDrawable(drawable);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.cmd_btn)
        ImageView cmdBtn;
        @BindView(R.id.bg_fl)
        View bg_fl;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
