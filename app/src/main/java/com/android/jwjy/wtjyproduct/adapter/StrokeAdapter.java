package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.view.StrokeView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ccy on 2018/9/3/23:57
 */
public class StrokeAdapter extends BAdapter<Float> {

    private int checkedColor = Color.parseColor("#B356acf5");
    private int unCheckedColor = Color.parseColor("#50000000");

    public StrokeAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            View view = loadView(R.layout.panel_stroke_item, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.strokeSV.setCheckedColor(checkedColor);
        holder.strokeSV.setUnCheckedColor(unCheckedColor);
        holder.strokeSV.setRadiusScale(itemList.get(position));
        holder.strokeSV.setChecked(selectItem == position);
        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.stroke_sv)
        StrokeView strokeSV;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
