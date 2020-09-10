package com.android.jwjy.wtjyproduct.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.jwjy.wtjyproduct.event.GestureLayoutListener;

public class GestureLayoutWrapper extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    private ViewGroup viewGroup;
    private GestureDetector gestureDetector;

    private static final int SCOLL_MODE_NONE = 0x000;
    private static final int SCOLL_MODE_VOLUMEN = 0x001;
    private static final int SCOLL_MODE_FAST_SEEK = 0x002;

    private int scrollMode = SCOLL_MODE_NONE;

    private GestureLayoutListener gestureLayoutListener;
    private View maskView;
    public GestureLayoutWrapper(Context context,@Nullable ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        gestureDetector = new GestureDetector(context,this);
        viewGroup.setOnTouchListener(this);
        maskView = new View(viewGroup.getContext());
        maskView.setOnTouchListener(this);
        maskView.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setMaskEnable(boolean mask){
        if(mask){
            if(viewGroup.indexOfChild(maskView) < 0){
                viewGroup.addView(maskView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }else{
            if(viewGroup.indexOfChild(maskView) >= 0){
                viewGroup.removeView(maskView);
            }
        }
    }

    public void setGestureLayoutListener(GestureLayoutListener listener){
        gestureLayoutListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //实现快进快退松开时候的回调
        if (event.getAction() == MotionEvent.ACTION_UP){
            switch (scrollMode){
                case SCOLL_MODE_VOLUMEN:
                    if(gestureLayoutListener != null){
                        gestureLayoutListener.onStopVolumeOffset();
                    }
                    break;
                case SCOLL_MODE_FAST_SEEK:
                    if(gestureLayoutListener != null){
                        gestureLayoutListener.onStopFastSeekOffset();
                    }
                    break;
            }
            scrollMode = SCOLL_MODE_NONE;
        }
        return gestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        scrollMode = SCOLL_MODE_NONE;
        if(gestureLayoutListener != null){
            return gestureLayoutListener.onDown();
        }
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(gestureLayoutListener != null){
            return gestureLayoutListener.onClick();
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(gestureLayoutListener != null){
            return gestureLayoutListener.onDoubleClick();
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        switch (scrollMode){
            case SCOLL_MODE_NONE:
                if(Math.abs(distanceX) > Math.abs(distanceY)){
                    scrollMode = SCOLL_MODE_FAST_SEEK;
                    if(gestureLayoutListener != null){
                        gestureLayoutListener.onStartFastSeekOffset();
                    }
                }else if(Math.abs(distanceX) < Math.abs(distanceY)){
                    scrollMode = SCOLL_MODE_VOLUMEN;
                    if(gestureLayoutListener != null){
                        gestureLayoutListener.onStartVolumeOffset();
                    }
                }
                break;
            case SCOLL_MODE_VOLUMEN:
                   onVolumenGesture(e1,e2,distanceX,distanceY);
                break;
            case SCOLL_MODE_FAST_SEEK:
                    onFastSeekGesture(e1,e2,distanceX,distanceY);
                break;
        }
        return false;
    }


    private void onVolumenGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        float offsetPercentage = (e1.getY() - e2.getY()) / viewGroup.getHeight();
        if(gestureLayoutListener != null)
            gestureLayoutListener.onVolumeOffset(offsetPercentage);
    }

    private void onFastSeekGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        float offsetPercentage = (e2.getX() - e1.getX()) / viewGroup.getWidth();
        if(gestureLayoutListener != null){
            gestureLayoutListener.onFastSeekOffset(offsetPercentage);
        }
    }




}
