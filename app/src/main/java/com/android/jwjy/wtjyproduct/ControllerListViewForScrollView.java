package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 可以滚动的list列表视图（解决嵌套scrollview 无法滚动问题）
 * Created by dayuer on 2019/12/4.
 */
public class ControllerListViewForScrollView extends ListView {

    public ControllerListViewForScrollView(Context context) {
        super(context);
    }

    public ControllerListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllerListViewForScrollView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * 只需要重写这个方法即可
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
