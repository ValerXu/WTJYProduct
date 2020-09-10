package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 适配器
 * Created by huanghaibin on 2017/12/4.
 */

public class ModelClassAdapter extends ModelGroupRecyclerAdapter<String, ClassBean> {


    private RequestManager mLoader;
    private Context mContext ;

    public ModelClassAdapter(Context context,LinkedHashMap<String, List<ClassBean>> map,List<String> titles) {
        super(context);
        mContext = context;
        mLoader = Glide.with(context.getApplicationContext());
        resetGroups(map,titles);
    }


    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ClassViewHolder(mInflater.inflate(R.layout.fragment_classchedulecard2, parent, false));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ClassBean item, int position) {
        ClassViewHolder h = (ClassViewHolder) holder;
        if (item.getClassName().equals("null")){ //暂无课程
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) h.mclasschedulecard2_notnull.getLayoutParams();
            rl.height = 0;
            h.mclasschedulecard2_notnull.setLayoutParams(rl);
            rl = (RelativeLayout.LayoutParams) h.mclasschedulecard2_null.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            h.mclasschedulecard2_null.setLayoutParams(rl);
            h.mclasschedulecard2_notnull.setVisibility(View.INVISIBLE);
            h.mclasschedulecard2_null.setVisibility(View.VISIBLE);
            return;
        } else {
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) h.mclasschedulecard2_null.getLayoutParams();
            rl.height = 0;
            h.mclasschedulecard2_null.setLayoutParams(rl);
            rl = (RelativeLayout.LayoutParams) h.mclasschedulecard2_notnull.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            h.mclasschedulecard2_notnull.setLayoutParams(rl);
            h.mclasschedulecard2_notnull.setVisibility(View.VISIBLE);
            h.mclasschedulecard2_null.setVisibility(View.INVISIBLE);
        }
        h.mclasschedulecard2_coursename.setText(item.getClassName());
        String date = new SimpleDateFormat("MM-dd").format(item.getBegin_class_date());
        String begin = new SimpleDateFormat("HH:mm:ss").format(item.getBegin_class_date());
        String end = new SimpleDateFormat("HH:mm:ss").format(item.getEnd_time_datess());
        h.mclasschedulecard2_time.setText(date + "   " +begin + " - " + end);
        h.mclasschedulecard2_mainteacher.setText(item.getClassTeacher());
        if (item.getStatus() != null) {
            h.mclasschedulecard2_state.setText(item.getStatus());
            if (item.getStatus().equals("进行中")){
                h.mclasschedulecard2_state.setBackground(h.mclasschedulecard2_state.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                h.mclasschedulecard2_state.setTextColor(h.mclasschedulecard2_state.getResources().getColor(R.color.white));
            }
        }
        Glide.with(mContext.getApplicationContext()).
                load(item.getClassTeacherImg()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("Warn","加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                return false;
            }
            @Override
            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("Warn","成功  Drawable Name:" + resource.getClass().getCanonicalName());
                return false;
            }
        })
                .error(mContext.getApplicationContext().getResources().getDrawable(R.drawable.image_teachersdefault)).into(h.mclasschedulecard2_teachers_headportrait);
    }

    private static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView mclasschedulecard2_coursename,
                mclasschedulecard2_time,mclasschedulecard2_mainteacher,mclasschedulecard2_state;
        private ControllerCustomRoundAngleImageView mclasschedulecard2_teachers_headportrait;
        private RelativeLayout mclasschedulecard2_notnull,mclasschedulecard2_null;

        private ClassViewHolder(View itemView) {
            super(itemView);
            mclasschedulecard2_coursename = itemView.findViewById(R.id.classchedulecard2_coursename);
            mclasschedulecard2_time = itemView.findViewById(R.id.classchedulecard2_time);
            mclasschedulecard2_teachers_headportrait = itemView.findViewById(R.id.classchedulecard2_teachers_headportrait);
            mclasschedulecard2_mainteacher = itemView.findViewById(R.id.classchedulecard2_mainteacher);
            mclasschedulecard2_state = itemView.findViewById(R.id.classchedulecard2_state);
            mclasschedulecard2_null = itemView.findViewById(R.id.classchedulecard2_null);
            mclasschedulecard2_notnull = itemView.findViewById(R.id.classchedulecard2_notnull);
        }
    }
}
