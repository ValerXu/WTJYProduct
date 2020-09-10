package com.android.jwjy.wtjyproduct.interfaces;

/**
 * 音视频容器管理
 * created by ccy on 2019/7/20/15:01
 */
public interface IMultiMediaViewManager {
    void initMultiMediaLayout(boolean isMp4);

    void setMultiMediaViewVisiable(int visibility);

    void setMultiMediaViewPauseVisiable(int visibility);

    void multiMediaViewFullScreen(boolean isFull);

    void setProgressBarTime(int time);

    void setProgressBarTime(int time, boolean filter);

    void rotate(boolean isRotate);

    void hideProgressBar();

    void showProgressBar();
}
