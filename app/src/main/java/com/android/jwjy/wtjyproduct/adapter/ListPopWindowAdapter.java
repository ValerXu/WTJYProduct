package com.android.jwjy.wtjyproduct.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tony on 2015/8/3.
 */
public class ListPopWindowAdapter extends BAdapter<String> {


    public ListPopWindowAdapter(Context context) {
        super(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.list_popup_window_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(itemList.get(position).trim());
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.list_pop_item)
        TextView textView;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
