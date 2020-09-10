package com.android.jwjy.wtjyproduct.adapter;//package com.talkfun.cloud.adapter;


import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

//import com.android.databinding.library.baseAdapters.BR;
import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.base.BaseDatabindingAdapter;
import com.android.jwjy.wtjyproduct.databinding.ItemRtcVideoBinding;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;

import java.util.List;


/**
 * 1 V 1
 * Created by ccy on 2019/4/9/11:40
 */
public class OTOVideoAdapter extends BaseDatabindingAdapter<VideoStatusData> {
    /**
     * 局部刷新
     */
    public static final int PARTIAL_REFRESH = 0x10000;

    private MediaSwitchListener mediaSwitchListener;
    public static final int AUDIO = 1;
    public static final int VIDEO = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.item_rtc_video;
    }

    @Override
    protected int getVariableId() {
//        return com.android.jwjy.zkktproduct.BR.videoData;
        return 4;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<VideoStatusData> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        VideoStatusData data = getItem(position);
        View view = data.getVideoView();
        boolean isLoading = data.getVideoOfflineStatus() == 1;
        final RtcUserEntity entity = data.getRtcUserEntity();
        final ItemRtcVideoBinding binding = (ItemRtcVideoBinding) holder.getBinding();
        if (payloads.size() != 0 && ((int) payloads.get(0)) != -1) {//局部刷新
            int type = (int) payloads.get(0);
            switch (type) {
                case PARTIAL_REFRESH:
                    setValue(entity, isLoading, binding);
                    break;
                default:
            }
            return;
        }
        if (view == null) {
            binding.allClose.setBackgroundResource(R.color.item_one_to_one_video_bg);
            binding.ivAllCloseAvatar.setVisibility(View.GONE);
            binding.videoLayout.removeAllViews();
            binding.tvName.setVisibility(View.GONE);
            binding.ivAvatar.setImageResource(entity.getRole() == MemberRole.MEMBER_ROLE_SUPER_ADMIN ? R.mipmap.item_live_one_to_one_video_default_avatar_spadmin : R.mipmap.item_live_one_to_one_video_default_avatar_user);
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
        binding.ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaSwitchListener != null && entity.isMe()) {
                    mediaSwitchListener.mediaSwitch(AUDIO, entity.isAudioOpen(), entity.getAudio());
                }
            }
        });
        binding.ivVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaSwitchListener != null && entity.isMe()) {
                            mediaSwitchListener.mediaSwitch(VIDEO, entity.isVideoOpen(), entity.getVideo());
                        }
                    }
                });
    }

    private void setValue(RtcUserEntity entity, boolean isLoading, ItemRtcVideoBinding binding) {
        if (isLoading) {
            binding.allClose.setBackgroundResource(R.color.item_one_to_one_video_bg);
            binding.allClose.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.VISIBLE);
            binding.ivAllCloseAvatar.setVisibility(View.GONE);
            binding.ivCloseVideoCenter.setVisibility(View.GONE);
        } else if (!entity.isVideoOpen() && entity.isAudioOpen()) {
            binding.allClose.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.GONE);
            binding.ivAllCloseAvatar.setVisibility(View.GONE);
            binding.ivCloseVideoCenter.setVisibility(View.VISIBLE);
            binding.allClose.setBackgroundResource(R.color.item_one_to_one_video_bg);
//            binding.ivAllCloseAvatar.setImageResource(R.mipmap.item_live_one_to_one_audio);
        } else if (!entity.isVideoOpen() && !entity.isAudioOpen()) {
            binding.allClose.setVisibility(View.VISIBLE);
            binding.ivCloseVideoCenter.setVisibility(View.GONE);
            binding.progressLoading.setVisibility(View.GONE);
            binding.ivAllCloseAvatar.setVisibility(View.VISIBLE);
            binding.allClose.setBackgroundResource(entity.isMe() ? R.drawable.item_student_all_close_bg : R.drawable.item_teacher_all_close_bg);
            binding.ivAllCloseAvatar.setImageResource(entity.isMe() ? R.mipmap.item_live_one_to_one_student_all_close : R.mipmap.item_live_one_to_one_teacher_all_close);
        } else {
            binding.allClose.setVisibility(View.GONE);
        }
        int audioVisibility = entity.isMe() ? View.VISIBLE : entity.isAudioOpen() ? View.GONE : View.VISIBLE;
        int videoVisibility = entity.isMe() ? View.VISIBLE : entity.isVideoOpen() ? View.GONE : View.VISIBLE;
        binding.ivAudio.setVisibility(audioVisibility);
        binding.ivVideo.setVisibility(videoVisibility);
        binding.ivAudio.setSelected(!entity.isAudioOpen());
        binding.ivVideo.setSelected(!entity.isVideoOpen());
    }

    public void setMediaSwitchListener(MediaSwitchListener mediaSwitchListener) {
        this.mediaSwitchListener = mediaSwitchListener;
    }

/*    private static class InnerOnClickListener implements View.OnClickListener {
        WeakReference<RtcUserEntity> entityWeakReference;
        WeakReference<MediaSwitchListener> mediaSwitchListenerWeakReference;

        public InnerOnClickListener(RtcUserEntity entity, MediaSwitchListener mediaSwitchListener) {
            this.entityWeakReference = new WeakReference<>(entity);
            mediaSwitchListenerWeakReference = new WeakReference<>(mediaSwitchListener);
        }

        private RtcUserEntity getRtcUserEntity() {
            return entityWeakReference.get();
        }

        @Override
        public void onClick(View v) {
            RtcUserEntity entity = getRtcUserEntity();
            switch (v.getId()) {
                case R.id.iv_video:
                    if (mediaSwitchListenerWeakReference.get() != null && entity.isMe()) {
                        mediaSwitchListenerWeakReference.get().mediaSwitch(VIDEO, entity.isVideoOpen(), entity.getVideo());
                    }
                    break;
                case R.id.iv_audio:
                    if (mediaSwitchListenerWeakReference.get() != null && entity.isMe()) {
                        mediaSwitchListenerWeakReference.get().mediaSwitch(AUDIO, entity.isAudioOpen(), entity.getAudio());
                    }
                    break;
            }
        }
    }*/

    public interface MediaSwitchListener {
        void mediaSwitch(int type, boolean isOpen, int audio);
    }
}
