package com.android.jwjy.wtjyproduct.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.jwjy.wtjyproduct.R;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.module.PlaybackInfo;

/**
 * Created by Wallace on 2017/1/22.
 * <p/>
 * 快进快退
 */
public class SeekBarHelper {
    private TextView forwardDetailTime;
    private TextView forwardTime;
    private ImageView ivForward;
    private PopupWindow forwardPopup;
    public int deviceWidth; //设备宽度
    private int seekBarProgress = 0;
    private static final int OFFSET_SECONDS = 250;//固定秒数
    float ratioSeconds = Float.MIN_VALUE;  //滑动的秒数
    float downX = 0f;

    private SeekBar mSeekBar;
    private Context mContext;
    private int mPopWidth;  //弹窗宽
    private int mPopHeight;  //弹窗高
    private static float touchSlop = 10.0f;

    public boolean isShowPoped = false;

    public SeekBarHelper(Context context, SeekBar seekBar) {
        deviceWidth = DimensionUtils.getScreenWidth(context);
        this.mSeekBar = seekBar;
        mContext = context;
        mPopWidth = DimensionUtils.dip2px(mContext, 160);
        mPopHeight = DimensionUtils.dip2px(mContext, 100);
        //ViewConfiguration.get(getContext().getScaledTouchSlop())

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initPopWindow();
    }

    /**
     * 初始化快进快退的弹窗popupWindow
     */
    private void initPopWindow() {
        View forwardView = View.inflate(mContext, R.layout.popupwindow_forward, null);
        forwardDetailTime = (TextView) forwardView.findViewById(R.id.tv_forwardDetailTime);
        forwardTime = (TextView) forwardView.findViewById(R.id.tv_forwardTime);
        ivForward = (ImageView) forwardView.findViewById(R.id.iv_forward);
        forwardPopup = new PopupWindow(forwardView, mPopWidth, mPopHeight);
        forwardPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forwardPopup.setOutsideTouchable(false);
    }



    /**
     * 开始快进快退
     * @param anchorView
     */
   public void startFastSeek(View anchorView){
       showForwardPopup(anchorView);
       //fastSeekOffset(offsetPercentage);
   }

    /**
     * 显示快进快退提示框
     * @param anchorView
     */
    private void showForwardPopup(View anchorView){
        if (!isShowForwardPopup()) {
            if(forwardPopup == null)
                initPopWindow();
            isShowPoped = true;
            if (ScreenSwitchUtils.getInstance(mContext).isPortrait()) {  //竖屏
                forwardPopup.showAsDropDown(anchorView, (anchorView.getWidth() - mPopWidth) / 2,
                        -(anchorView.getWidth() / 4 * 3 + mPopHeight) / 2);
            } else {   //横屏拿宽的比例求高又误差，所以要直接拿高
                forwardPopup.showAsDropDown(anchorView, (anchorView.getWidth() - mPopWidth) / 2,
                        -(anchorView.getHeight() + mPopHeight) / 2);
            }
        }
    }

    /**
     * 设置快进快退百分比
     * @param offsetPercentage
     */
   public void fastSeekOffset(float offsetPercentage){

       ratioSeconds = offsetPercentage * OFFSET_SECONDS;
       int currentProgress = (int) (mSeekBar.getProgress() + ratioSeconds);
       currentProgress = currentProgress < 0 ? 0 : currentProgress;
       currentProgress = currentProgress >= mSeekBar.getMax() ? mSeekBar.getMax() : currentProgress;
       forwardDetailTime.setText(TimeUtil.displayHHMMSS(currentProgress) + "/" + TimeUtil.displayHHMMSS(mSeekBar.getMax()));
       if (currentProgress > 0) {
           if (ratioSeconds >= 0) {
               ivForward.setSelected(false);
               if (currentProgress < mSeekBar.getMax()) {
                   forwardTime.setText("+" + (int) +(ratioSeconds) + "秒");
               }
           } else {
               forwardTime.setText((int) +(ratioSeconds) + "秒");
               ivForward.setSelected(true);
           }
       }
   }

   public void stopFastSeek(){
       hideForwardPopup();
       performFastSeek();
   }

    private void hideForwardPopup(){
        if (isShowForwardPopup()) {
            forwardPopup.dismiss();
        }
    }

    private void performFastSeek(){
        if(ratioSeconds == Float.MIN_VALUE)
            return;
        int progress = (int) (mSeekBar.getProgress() + ratioSeconds);
        progress = progress > 0 ? progress : 0;
        if (Math.abs(seekBarProgress - progress) > 10) {
            mSeekBar.setProgress(progress);
            seekTo(progress);
        }
        ratioSeconds = Float.MIN_VALUE;
        forwardTime.setText("-0秒");
    }


   public boolean isShowForwardPopup(){
       return forwardPopup != null && forwardPopup.isShowing();
   }



    /**
     * 跳转到某个时间点
     *
     * @param progress
     */
    public void seekTo(int progress) {
        HtSdk.getInstance().playbackSeekTo(progress);
        seekBarProgress = (int) progress;
        updatePlayTime(progress);
    }


    /**
     * 重置
     */
    public void resetSeekBarProgress() {
        seekBarProgress = 0;
        if (mSeekBar != null)
            mSeekBar.setProgress(seekBarProgress);
    }

    public void updatePlayTime(int currentTime){
        if (seekBarProgress >= 0 && currentTime >= seekBarProgress) {
            seekBarProgress = (int)currentTime;
            if (seekBarProgress >= 0 && seekBarProgress <= Integer.valueOf(PlaybackInfo.getInstance().getDuration())) {
                mSeekBar.setProgress(seekBarProgress);
            }
        }
    }

}
