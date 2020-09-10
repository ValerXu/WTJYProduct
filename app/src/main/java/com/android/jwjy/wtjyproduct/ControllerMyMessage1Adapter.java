package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

/**
 * 我的消息适配器
 * Created by dayuer on 2019/12/4.
 */
public class ControllerMyMessage1Adapter extends ArrayAdapter<ControllerMyMessage1Adapter.MyMessageInfo> {
    private final LayoutInflater mInflater;
    private final ViewBinderHelper binderHelper;
    private Context mControlMainActivity;
    private ControllerMyMessage1AdapterInterface mCallback;

    public ControllerMyMessage1Adapter(ControllerMyMessage1AdapterInterface fragment,Context context, List<MyMessageInfo> objects) {
        super(context, R.layout.model_my_mymessage1, objects);
        mCallback = fragment;
        mControlMainActivity = context;
        mInflater = LayoutInflater.from(context);
        binderHelper = new ViewBinderHelper();

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.model_my_mymessage1, parent, false);

            holder = new ViewHolder();
            holder.swipeLayout = convertView.findViewById(R.id.swipe_layout);
            holder.modelmy_mymessage1_cover = convertView.findViewById(R.id.modelmy_mymessage1_cover);
            holder.deleteView = convertView.findViewById(R.id.delete_layout);
            holder.modelmy_mymessage1_message = convertView.findViewById(R.id.modelmy_mymessage1_message);
            holder.modelmy_mymessage1_messagecount = convertView.findViewById(R.id.modelmy_mymessage1_messagecount);
            holder.modelmy_mymessage1_name = convertView.findViewById(R.id.modelmy_mymessage1_name);
            holder.modelmy_mymessage1_time = convertView.findViewById(R.id.modelmy_mymessage1_time);
            holder.content_layout = convertView.findViewById(R.id.content_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MyMessageInfo item = getItem(position);
        if (item != null) {
            binderHelper.bind(holder.swipeLayout, item.modelmy_mymessage1_name);
            //加载用户头像
            Glide.with(mControlMainActivity).
                    load(item.modelmy_mymessage1_coverurl).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("Wain","加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("Wain","成功  Drawable Name:"+resource.getClass().getCanonicalName());
                    return false;
                }
            })
                    .error(mControlMainActivity.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(holder.modelmy_mymessage1_cover);
            //删除item的监听事件
            holder.deleteView.setOnClickListener(v -> {
                if (mCallback != null){
                    mCallback.deleteItem(item);
                }
            });
            //消息内容
            holder.modelmy_mymessage1_message.setText(item.modelmy_mymessage1_message);
            //消息未读数量
            if (item.modelmy_mymessage1_messagecount.equals("-1")) {
                holder.modelmy_mymessage1_messagecount.setVisibility(View.INVISIBLE);
            } else {
                holder.modelmy_mymessage1_messagecount.setText(item.modelmy_mymessage1_messagecount);
            }
            //消息标题
            holder.modelmy_mymessage1_name.setText(item.modelmy_mymessage1_name);
            //消息时间
            holder.modelmy_mymessage1_time.setText(item.modelmy_mymessage1_time);
            holder.content_layout.setOnClickListener(view -> {
//                String displayText = "" + item + " clicked";
//                Toast.makeText(getContext(), displayText, Toast.LENGTH_SHORT).show();
//                    Log.d("ListAdapter", displayText);
                if (mCallback != null){
                    mCallback.clickItem(item);
                }
            });
        }

        return convertView;
    }

    public void DeleteItem(MyMessageInfo item){
        remove(item);
        notifyDataSetChanged();
    }
//    /**
//     * Only if you need to restore open/close state when the orientation is changed.
//     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
//     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

//    /**
//     * Only if you need to restore open/close state when the orientation is changed.
//     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
//     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    private class ViewHolder {
        SwipeRevealLayout swipeLayout;
        ControllerCustomRoundAngleImageView modelmy_mymessage1_cover;
        View deleteView;
        TextView modelmy_mymessage1_name;
        TextView modelmy_mymessage1_time;
        TextView modelmy_mymessage1_message ;
        TextView modelmy_mymessage1_messagecount ;
        FrameLayout content_layout;
    }
    //我的消息
    public static class MyMessageInfo {
        String modelmy_mymessage1_coverurl;
        String modelmy_mymessage1_name;
        String modelmy_mymessage1_time;
        String modelmy_mymessage1_message ;
        String modelmy_mymessage1_messagecount ;
        Integer modelmy_mymessage1_Id ;
    }

    public interface ControllerMyMessage1AdapterInterface {
        void deleteItem(MyMessageInfo myMessageInfo);
        void clickItem(MyMessageInfo myMessageInfo);
    }
}
