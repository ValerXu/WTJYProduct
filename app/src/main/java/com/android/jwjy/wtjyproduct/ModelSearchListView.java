package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Carson_Ho on 17/8/10.
 */

public class ModelSearchListView extends ListView {
    public ModelSearchListView(Context context) {
        super(context);
    }

    public ModelSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModelSearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //通过复写其onMeasure方法、达到对ScrollView适配的效果
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
