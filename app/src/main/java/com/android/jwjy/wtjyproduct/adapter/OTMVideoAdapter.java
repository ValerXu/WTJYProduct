package com.android.jwjy.wtjyproduct.adapter;//package com.talkfun.cloud.adapter;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.BR;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.databinding.ItemOneToMultiVideoBinding;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.android.jwjy.wtjyproduct.util.DimensionUtils;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.rtc.consts.DrawPowerStatus;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;

import java.util.List;


/**
 * 1 V 16 适配器
 * <p>
 * Created by ccy on 2019/4/9/11:40
 */
public class OTMVideoAdapter extends BaseDatabindingAdapter<VideoStatusData> {
    /**
     * 局部刷新
     */
    public static final int PARTIAL_REFRESH = 0x10000;
    /**
     * 涂鸦刷新
     */
    public static final int PAINT = 0x10002;
    /**
     * 奖励刷新
     */
    public static final int AWARD = 0x10003;
    public static final int AUDIO = 1;
    public static final int VIDEO = 2;
    private int lastDrawXid = -1;
    private ImageView copyImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.item_one_to_multi_video;
    }

    @Override
    protected int getVariableId() {
        return BR.videoData;
    }

    /**
     * 局部更新
     *
     * @param position
     * @param type
     */
    public void notifyDataOfPart(int position, int type) {
        if ((type == PAINT)) {
            resetDefaultDraw(position, type);
        }
        notifyItemChanged(position, type);
    }

    /**
     * 重置画笔
     *
     * @param type
     */
    private void resetDefaultDraw(int position, int type) {
        if (lastDrawXid == -1) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getRtcUserEntity().getXid() == lastDrawXid) {
                if (i == position) {
                    break;
                }
                dataList.get(i).getRtcUserEntity().setDrawPower(DrawPowerStatus.CLOSE);
                notifyItemChanged(i, type);
                break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<VideoStatusData> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        VideoStatusData data = getItem(position);
        View view = data.getVideoView();
        boolean isLoading = data.getVideoOfflineStatus() == 1;
        final RtcUserEntity entity = data.getRtcUserEntity();
        int drawVisibility = TextUtils.equals(entity.getRole(), MemberRole.MEMBER_ROLE_SUPER_ADMIN) ? View.GONE : entity.getDrawPower() == DrawPowerStatus.OPEN ? View.VISIBLE : View.GONE;
        final ItemOneToMultiVideoBinding binding = (ItemOneToMultiVideoBinding) holder.getBinding();
        int score = entity.getScore();
        if (data.getRtcUserEntity().getDrawPower() == DrawPowerStatus.OPEN) {
            lastDrawXid = data.getXid();
        }
        if (payloads.size() != 0 && ((int) payloads.get(0)) != -1) {//局部刷新
            int type = (int) payloads.get(0);
            switch (type) {
                case PARTIAL_REFRESH:
                    setValue(entity, isLoading, binding);
                    break;
                case PAINT:
                    setPaintVisibility(binding.ivDraw, drawVisibility);
                    break;
                case AWARD:
                    loadingAwardAnimation(binding, entity.isMe());
                    setAwardData(binding.tvAwardCount, score);
                    break;
                default:
            }
            return;
        }
        if (view == null) {
/*            binding.allClose.setBackgroundResource(R.color.one_to_one_theme);
            binding.ivAllCloseAvatar.setVisibility(View.GONE);
            binding.videoLayout.removeAllViews();
            binding.tvName.setVisibility(View.GONE);
            binding.ivAvatar.setImageResource(entity.getRole() == MemberRole.MEMBER_ROLE_SUPER_ADMIN ? R.mipmap.item_live_one_to_one_video_default_avatar_spadmin : R.mipmap.item_live_one_to_one_video_default_avatar_user);*/
            return;
        }
        if (view.getParent() != null && view.getParent() == binding.videoLayout) {
            return;
        } else {
            if (view.getParent() != null) {
                ViewGroup viewParent = (ViewGroup) view.getParent();
                viewParent.removeView(view);
            }
            binding.videoLayout.removeAllViews();
        }
        binding.videoLayout.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setValue(entity, isLoading, binding);
    }

    private void setAwardData(TextView tvAwardCount, int score) {
        tvAwardCount.setText("x " + score / 2);
    }

    /**
     * 加载奖励动画
     * 若是当前用户，则不播放奖励动画
     *
     * @param binding
     * @param isMe
     */
    private void loadingAwardAnimation(final ItemOneToMultiVideoBinding binding, boolean isMe) {
        if (isMe) {
            return;
        }
        final ViewGroup contentView = (ViewGroup) binding.getRoot().getRootView().findViewById(android.R.id.content);
        int[] location = new int[2];
        binding.ivAward.getLocationOnScreen(location);  //获取目标View的相对屏幕位置
        final ImageView copyImageView = new ImageView(mContext);
        copyImageView.setImageResource(R.mipmap.item_otm_award);
        int width = getDimission(R.dimen.dp_10);
        int height = getDimission(R.dimen.dp_10);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.leftMargin = location[0]+getDimission(R.dimen.dp_3);
        int toolbarHeight = DimensionUtils.isPad(mContext) ? getDimission(R.dimen.dp_22) : getDimission(R.dimen.dp_40);
        params.topMargin = location[1] + toolbarHeight+getDimission(R.dimen.dp_3);   //给副本View设置位置，与目标View重合
        copyImageView.setLayoutParams(params);
        copyImageView.setPivotX(width * 0.5f);
        copyImageView.setPivotY(height * 0.5f);
        copyImageView.setScaleX(2f);
        copyImageView.setScaleY(2f);
        contentView.addView(copyImageView);

        //Animation scaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.award_scale);
        //copyImageView.setAnimation(scaleAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 0.5f, 2f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2_000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (contentView != null && copyImageView != null) {
                    contentView.removeView(copyImageView);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        copyImageView.startAnimation(scaleAnimation);
    }

    private int getDimission(int id) {
        return (int) mContext.getResources().getDimension(id);
    }

    private void setValue(RtcUserEntity entity, boolean isLoading, ItemOneToMultiVideoBinding binding) {
        if (isLoading) {
            binding.allClose.setBackgroundResource(R.color.one_to_one_theme);
            binding.allClose.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.VISIBLE);
            binding.ivAllCloseAvatar.setVisibility(View.GONE);
        } else if (!entity.isVideoOpen() && entity.isAudioOpen()) {
            binding.allClose.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.GONE);
            binding.ivAllCloseAvatar.setVisibility(View.VISIBLE);
            binding.allClose.setBackgroundResource(R.color.one_to_one_theme);
            binding.ivAllCloseAvatar.setImageResource(R.mipmap.item_live_one_to_one_audio);
        } else if (!entity.isVideoOpen() && !entity.isAudioOpen()) {
            binding.allClose.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.GONE);
            binding.ivAllCloseAvatar.setVisibility(View.VISIBLE);
            binding.allClose.setBackgroundResource(TextUtils.equals(MemberRole.MEMBER_ROLE_SUPER_ADMIN, entity.getRole()) ? R.drawable.item_teacher_all_close_bg : R.drawable.item_student_all_close_bg);
            binding.ivAllCloseAvatar.setImageResource(TextUtils.equals(MemberRole.MEMBER_ROLE_SUPER_ADMIN, entity.getRole()) ? R.mipmap.item_live_one_to_one_teacher_all_close : R.mipmap.item_live_one_to_one_student_all_close);
        } else {
            binding.allClose.setVisibility(View.GONE);
        }
        int audioVisibility =/* entity.isMe() ? View.VISIBLE : */entity.isAudioOpen() ? View.GONE : View.VISIBLE;
        int videoVisibility = /*entity.isMe() ? View.VISIBLE :*/ entity.isVideoOpen() ? View.GONE : View.VISIBLE;
        int drawVisibility = TextUtils.equals(entity.getRole(), MemberRole.MEMBER_ROLE_SUPER_ADMIN) ? View.GONE : entity.getDrawPower() == DrawPowerStatus.OPEN ? View.VISIBLE : View.GONE;
        binding.ivAudio.setVisibility(audioVisibility);
        binding.ivVideo.setVisibility(videoVisibility);
        binding.ivDraw.setVisibility(drawVisibility);
    }

    private void setPaintVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }
}
