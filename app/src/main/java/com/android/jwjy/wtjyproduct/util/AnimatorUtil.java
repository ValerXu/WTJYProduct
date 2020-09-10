package com.android.jwjy.wtjyproduct.util;

import android.animation.ObjectAnimator;
import android.view.View;

public class AnimatorUtil {

    public static void rotate(View view, float start, float end) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", start, end);
        objectAnimator.setRepeatCount(0);
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }
}
