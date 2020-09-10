package com.android.jwjy.wtjyproduct.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.jwjy.wtjyproduct.R;
import com.android.jwjy.wtjyproduct.entity.VideoStatusData;
import com.android.jwjy.wtjyproduct.event.OnDoubleTapListener;
import com.android.jwjy.wtjyproduct.event.VideoViewEventListener;
import com.android.jwjy.wtjyproduct.view.VideoUserStatusHolder;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.rtc.consts.DrawPowerStatus;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VideoStatusData> mList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final VideoViewEventListener mListener;
    private int mItemWidth;
    private int mItemHeight;
    private VideoStatusData lastDrawPowerData = null;

    public static final int VIDEO = 0x1000;
    public static final int AUDIO = 0x10001;
    public static final int PAINT = 0x10002;
    public static final int VIDEOLOADING = 0x10003;

    public VideoAdapter(Context context, VideoViewEventListener listener) {
        mContext = context;
        mInflater = ((Activity) context).getLayoutInflater();
        this.mList = new CopyOnWriteArrayList<>();
        mListener = listener;
        setLayoutWeightAndHeight();
    }


    public void addItem(VideoStatusData videoStatusData) {
        if (videoStatusData == null) {
            return;
        }
        if (this.mList.contains(videoStatusData)) {
            int index = this.mList.indexOf(videoStatusData);
            this.mList.set(index, videoStatusData);
            this.notifyItemChanged(index);
        } else {
            this.mList.add(videoStatusData);
            this.notifyDataSetChanged();
        }
    }


    public void addItem(int index, VideoStatusData videoStatusData) {
        if (videoStatusData == null) {
            return;
        }
        if (this.mList.contains(videoStatusData)) {
            this.mList.set(index, videoStatusData);
            this.notifyItemChanged(index);
        } else {
            this.mList.add(index, videoStatusData);
            this.notifyItemInserted(index);
            notifyItemRangeChanged(index, mList.size());
        }
    }

    public void updateItemOfPart(int type, VideoStatusData videoStatusData) {
        setDrawPower(type, videoStatusData);
        if (this.mList.contains(videoStatusData)) {
            int index = this.mList.indexOf(videoStatusData);
            this.mList.set(index, videoStatusData);
            this.notifyItemChanged(index, type);
        }
    }

    private void setDrawPower(int type, VideoStatusData videoStatusData) {
        if (videoStatusData != null && videoStatusData.getRtcUserEntity() != null && type == PAINT) {
            if (this.mList.contains(lastDrawPowerData) && lastDrawPowerData != null && lastDrawPowerData.getXid() != videoStatusData.getXid()) {
                lastDrawPowerData.getRtcUserEntity().setDrawPower(DrawPowerStatus.CLOSE);
                int index = this.mList.indexOf(lastDrawPowerData);
                this.mList.set(index, lastDrawPowerData);
                this.notifyItemChanged(index, type);
            }
        }
    }

    public void removeItem(VideoStatusData videoStatusData) {
        if (videoStatusData == null) {
            return;
        }
        int index = this.mList.indexOf(videoStatusData);
        if (lastDrawPowerData == videoStatusData) {
            lastDrawPowerData = null;
        }
        removeItem(index);
    }

    public void removeItem(int index) {
        this.mList.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mList.size());
    }

    public void clear() {
        lastDrawPowerData = null;
        final int size = getItemCount();
        this.mList.clear();
        notifyItemRangeRemoved(0, size);
    }


    public void close(VideoStatusData videoStatusData) {
        if (this.mList == null && this.mList.size() <= 1) {
            return;
        }
        this.mList.clear();
        this.mList.add(videoStatusData);
        this.notifyDataSetChanged();
    }

    private void setLayoutWeightAndHeight() {
        if (mItemWidth == 0 || mItemHeight == 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
//            mItemHeight = (outMetrics.heightPixels - DimensionUtils.dip2px(mContext, 10)) / 4;
            mItemHeight = outMetrics.heightPixels / 4;
            mItemWidth = mItemHeight * 4 / 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.video_view_container, parent, false);
        v.getLayoutParams().width = mItemWidth;
        v.getLayoutParams().height = mItemHeight;
        return new VideoUserStatusHolder(v);
    }

    private  void stripSurfaceView(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeAllViews();
//            ((FrameLayout) parent).removeView(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        VideoUserStatusHolder myHolder = (VideoUserStatusHolder) holder;
        final VideoStatusData user = mList.get(position);
        RtcUserEntity rtcUserEntity = user.getRtcUserEntity();
        boolean audioEnable = rtcUserEntity.isAudioOpen();
        boolean videoEnable = rtcUserEntity.isVideoOpen();
        int drawPower = rtcUserEntity.getDrawPower();
        String role = rtcUserEntity.getRole();
        boolean isMe = rtcUserEntity.isMe();
        int videoOfflineStatus = user.getVideoOfflineStatus();
        if (drawPower == DrawPowerStatus.OPEN) {
            recordDrawPower(user);
        }
        if (payloads.size() != 0) {//局部刷新
            int type = (int) payloads.get(0);
            switch (type) {
                case VIDEO:
                    setShadeVisibility(myHolder.shadeRL, myHolder.shadebgIV, videoEnable, role);
                    break;
                case AUDIO:
                    setAudioVisibility(myHolder.audioIV, audioEnable, role, isMe);
                    break;
                case PAINT:
                    setPaintVisibility(myHolder.paintIV, drawPower);
                    break;
                case VIDEOLOADING:
                    setVideoOfflineVisibility(myHolder.videoOffineProgressRL, videoOfflineStatus);
                    break;
                default:
            }
            return;
        }
        FrameLayout holderView = (FrameLayout) myHolder.itemView;
        holderView.setOnTouchListener(new OnDoubleTapListener(mContext) {
            @Override
            public void onDoubleTap(View view, MotionEvent e) {
                if (mListener != null) {
                    mListener.onItemDoubleClick(view, user);
                }
            }

            @Override
            public void onSingleTapUp() {
            }
        });

        String name = rtcUserEntity.getNickname();
        //景色的设置
        myHolder.bottom_ll.setBackgroundColor(role == MemberRole.MEMBER_ROLE_SUPER_ADMIN ? mContext.getResources().getColor(R.color.adapter_small_item_spadin) : mContext.getResources().getColor(R.color.translucence));
        myHolder.name.setText(name);
        setShadeVisibility(myHolder.shadeRL, myHolder.shadebgIV, videoEnable, role);
        setAudioVisibility(myHolder.audioIV, audioEnable, role, isMe);
        setPaintVisibility(myHolder.paintIV, drawPower);
        setVideoOfflineVisibility(myHolder.videoOffineProgressRL, videoOfflineStatus);
        View target = user.getVideoView();
        if (target == null) {
            return;
        }
//        stripSurfaceView(target);
//        myHolder.video.removeAllViews();
//        myHolder.video.addView(target);

        if (myHolder.video.getChildCount() != 0) {
            myHolder.video.removeAllViews();
        }
        stripSurfaceView(target);
        myHolder.video.addView(target);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public VideoStatusData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置遮罩层的显隐
     *
     * @param shadebgRL
     * @param view
     * @param isVideoOpen
     * @param role
     */
    private void setShadeVisibility(View shadebgRL, View view, boolean isVideoOpen, String role) {
        shadebgRL.setVisibility(isVideoOpen ? View.GONE : View.VISIBLE);
        if (shadebgRL.getVisibility() == View.VISIBLE) {
            view.setBackgroundResource(role == MemberRole.MEMBER_ROLE_SUPER_ADMIN ? R.mipmap.adater_video_item_spadin_photo : R.mipmap.adater_video_item_student_photo);
        }
    }

    private void setAudioVisibility(View view, boolean isAudioOpen, String role, boolean isMe) {
        view.setVisibility(isAudioOpen ? View.GONE : View.VISIBLE);
    }

    private void setPaintVisibility(View view, int drawPower) {
        view.setVisibility(drawPower == DrawPowerStatus.OPEN ? View.VISIBLE : View.GONE);
    }

    private void resetDrawPowerRecord() {
        this.lastDrawPowerData = null;
    }

    private void recordDrawPower(VideoStatusData lastvideoStatusData) {
        this.lastDrawPowerData = lastvideoStatusData;
    }

    /**
     * 设置用户离线时Video加载圈
     *
     * @param view
     * @param status
     */
    private void setVideoOfflineVisibility(View view, int status) {

        view.setVisibility(status == 1 ? View.VISIBLE : View.GONE);
    }

}
