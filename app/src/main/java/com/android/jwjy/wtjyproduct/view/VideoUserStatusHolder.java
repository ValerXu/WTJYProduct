package com.android.jwjy.wtjyproduct.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoUserStatusHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.user_name_tv)
    public TextView name;
    @BindView(R.id.video_view_container)
    public FrameLayout video;
    @BindView(R.id.shade_iv)
    public View shadebgIV;
    @BindView(R.id.shade_rl)
    public View shadeRL;
    @BindView(R.id.audio_iv)
    public ImageView audioIV;
    @BindView(R.id.paint_iv)
    public ImageView paintIV;
    @BindView(R.id.video_offline_progress_rl)
    public RelativeLayout videoOffineProgressRL;
    @BindView(R.id.bottom_ll)
    public LinearLayout bottom_ll;

    public VideoUserStatusHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }
}
