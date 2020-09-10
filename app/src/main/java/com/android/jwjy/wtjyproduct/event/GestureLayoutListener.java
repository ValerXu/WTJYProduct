package com.android.jwjy.wtjyproduct.event;

public interface GestureLayoutListener {
    //void onVolumenGesture(int width,int height,MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
    void onStartVolumeOffset();
    void onVolumeOffset(float offsetPercentage);
    void onStopVolumeOffset();
    void onStartFastSeekOffset();
    void onFastSeekOffset(float offsetPercentage);
    void onStopFastSeekOffset();
    boolean onDown();
    boolean onClick();
    boolean onDoubleClick();
}
