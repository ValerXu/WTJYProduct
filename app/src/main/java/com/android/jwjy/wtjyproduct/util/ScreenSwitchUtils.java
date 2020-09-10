package com.android.jwjy.wtjyproduct.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.inputmethod.InputMethodManager;

import java.lang.ref.WeakReference;

/**
 * Created by Wallace on 2016/12/28.
 */
public class ScreenSwitchUtils {
    private static final String TAG = ScreenSwitchUtils.class.getSimpleName();
    private volatile static ScreenSwitchUtils mInstance;

    // 是否是竖屏
    private boolean isPortrait = true;
    private boolean mIsSensorSwitch = false;
    boolean mIsOpenSwitchAuto = false;  //是否开启自动重力切换
    private SensorManager sm;
    //    private OrientationSensorListener listener;
    private Sensor sensor;

    private SensorManager sm1;
    private Sensor sensor1;
//    private OrientationSensorListener1 listener1;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 888:
                    Activity activity = wrActivity != null ? wrActivity.get() : null;
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < 135) {

                    } else if (orientation > 135 && orientation < 225) {

                    } else if (orientation > 225 && orientation < 315) {
                        if (activity != null && isPortrait && DimensionUtils.isPad(activity) && mIsOpenSwitchAuto) {
                            isPortrait = false;
                            mIsSensorSwitch = true;
                            mIsFullScreen = false;
                            if (activity.getCurrentFocus() != null) {
                                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                            }
                            this.postDelayed(landscapeRunnale, 100);
                        }
                    } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                        if (!isPortrait && mIsOpenSwitchAuto) {
                            isPortrait = true;
                            mIsSensorSwitch = false;
                            mIsFullScreen = false;
                            if (onSensorChangedListener != null)
                                onSensorChangedListener.beforeOrientationChange(false);
                            if (activity != null && activity.getCurrentFocus() != null) {
                                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                            }
                            this.postDelayed(portraitRunnale, 100);
                        }
                    }

                    break;
                default:
                    break;
            }

        }
    };
    private final InputMethodManager imm;

    /**
     * 返回ScreenSwitchUtils单例
     **/
    public static ScreenSwitchUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ScreenSwitchUtils.class) {
                if (mInstance == null) {
                    mInstance = new ScreenSwitchUtils(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private ScreenSwitchUtils(Context contet) {
        sm = (SensorManager) contet.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        listener = new OrientationSensorListener(mHandler);

        // 根据 旋转之后/点击全屏之后 两者方向一致,激活sm.
        sm1 = (SensorManager) contet.getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sm1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        listener1 = new OrientationSensorListener1();

        imm = (InputMethodManager) contet.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    /**
     * 开始监听
     */
    WeakReference<Activity> wrActivity;
    public void start(Activity activity) {
        //WeakReference<Activity> mActivitySoft = new WeakReference<>(activity);
        if(wrActivity != null)
            wrActivity.clear();
        wrActivity = new WeakReference<>(activity);
    }

    /**
     * 停止监听
     */
    public void stop() {
        clear();
    }

    public void destroy() {
        clear();
        wrActivity = null;
        mInstance = null;
    }

    public void toggleScreen(boolean isPortrait) {
        this.isPortrait = isPortrait;
        toggleScreen();
    }

    /**
     * 手动横竖屏切换方向
     */
    public void toggleScreen() {
//        sm.unregisterListener(listener);
//        sm1.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI);
        if (mHandler == null) {
            return;
        }
        Activity activity = wrActivity != null ? wrActivity.get() : null;
        if (isPortrait) {
            isPortrait = false;
            // 切换成横屏

            if (activity != null && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }

            mHandler.postDelayed(landscapeRunnale, 100);
        } else {
            isPortrait = true;
            mIsFullScreen = false;
            mIsSensorSwitch = false;
            // 切换成竖屏
            if (activity != null && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
            mHandler.postDelayed(portraitRunnale, 100);
        }
    }

    private Runnable portraitRunnale = new Runnable() {
        @Override
        public void run() {
            Activity activity = wrActivity != null ? wrActivity.get() : null;
            if (activity == null) {
                return;
            }
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    };
    private Runnable landscapeRunnale = new Runnable() {
        @Override
        public void run() {
            Activity activity = wrActivity != null ? wrActivity.get() : null;
            if (activity == null) {
                return;
            }
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    };


    private boolean mIsFullScreen = false; //全屏

    public void setIsFullScreen(boolean b) {
        mIsFullScreen = b;
    }

    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    public boolean isPortrait() {
        return this.isPortrait;
    }

    public void setIsPortrait(boolean b) {
        isPortrait = b;
    }

    public boolean isSensorSwitchLandScreen() {  //是否为重力切换
        return mIsSensorSwitch;
    }

    public boolean isSensorNotFullLandScreen() {  //是否为平板横屏但不全屏
        return mIsSensorSwitch && !isFullScreen();
    }

    public void isOpenSwitchAuto(boolean isOpen) {
        mIsOpenSwitchAuto = isOpen;
    }

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private WeakReference<Handler> rotateHandler;

        public OrientationSensorListener(Handler handler) {
            rotateHandler = new WeakReference<>(handler);
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (rotateHandler != null) {
                rotateHandler.get().obtainMessage(888, orientation, 0).sendToTarget();
            }
        }
    }

    public class OrientationSensorListener1 implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        public OrientationSensorListener1() {
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (orientation > 225 && orientation < 315) {// 检测到当前实际是横屏
                if (!isPortrait) {
//                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
//                    sm1.unregisterListener(listener1);
                }
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {// 检测到当前实际是竖屏
                if (isPortrait) {
//                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
//                    sm1.unregisterListener(listener1);
                }
            }
        }
    }

    public void clear() {
        if (mHandler != null) {
            mHandler.removeCallbacks(portraitRunnale);
            mHandler.removeCallbacks(landscapeRunnale);
        }
        if(wrActivity != null){
            wrActivity.clear();
        }
    }

    //监听重力切换事件
    private OnSensorChangedListener onSensorChangedListener;

    public void setOnSensorChangedListener(OnSensorChangedListener onSensorChangedListener) {
        this.onSensorChangedListener = onSensorChangedListener;
    }

    public interface OnSensorChangedListener {
        void beforeOrientationChange(boolean isPortrait);
    }
}