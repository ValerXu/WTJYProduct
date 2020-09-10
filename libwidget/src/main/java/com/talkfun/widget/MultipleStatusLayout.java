package com.talkfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.talkfun.libwidget.R;


/**
 * Created by asus on 2018/1/19.
 */

public class MultipleStatusLayout extends FrameLayout {
    public static int STATUS_LOADING = 0;
    public static int STATUS_EMPTY = 1;
    public static int STATUS_CONTENT = 2;
    public static int STATUS_ERROR = 3;
    public static int STATUS_NETWORK_BAD = 4;

    private static final int NULL_LAYOUT_ID = Integer.MIN_VALUE;

    private static final FrameLayout.LayoutParams DEFAULT_LAYOUT_PARAMS = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View loadingView;
    private View emptyView;
    private View contentView;
    private View errorView;
    private View networkBadView;

    private int errorLayoutId = NULL_LAYOUT_ID;
    private int loadingLaoutId = NULL_LAYOUT_ID;
    private int emptyLayoutId = NULL_LAYOUT_ID;
    private int contentLayoutId = NULL_LAYOUT_ID;
    private int networkBadLayoutId = NULL_LAYOUT_ID;

    private int status;
    private LayoutInflater layoutInflater;

    public MultipleStatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public MultipleStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleStatusLayout, defStyleAttr, 0);
        loadingLaoutId = a.getResourceId(R.styleable.MultipleStatusLayout_loadingView, NULL_LAYOUT_ID);
        emptyLayoutId = a.getResourceId(R.styleable.MultipleStatusLayout_emptyView, NULL_LAYOUT_ID);
        errorLayoutId = a.getResourceId(R.styleable.MultipleStatusLayout_errorView, NULL_LAYOUT_ID);
        contentLayoutId = a.getResourceId(R.styleable.MultipleStatusLayout_contentView, NULL_LAYOUT_ID);
        networkBadLayoutId = a.getResourceId(R.styleable.MultipleStatusLayout_networdbadView, NULL_LAYOUT_ID);
        a.recycle();
        init(context);

    }

    private void init(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (contentLayoutId == NULL_LAYOUT_ID) {
            if (getChildCount() > 0) {
                contentView = getChildAt(0);
            }
        }
        showContent();
    }

    private View inflateView(@LayoutRes int layoutId) {
        return layoutInflater.inflate(layoutId, null);
    }

    private void showView(View view) {
        int childCount = getChildCount();
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            child.setVisibility((child == view) ? VISIBLE : GONE);
        }
    }

    public MultipleStatusLayout setLoadingView(View view) {
        if (loadingView == view)
            return this;
        if (loadingView != null && indexOfChild(loadingView) >= 0) {
            removeView(loadingView);
        }
        loadingView = view;
        return this;
    }

    public MultipleStatusLayout setLoadingLaoutId(@LayoutRes int layoutId) {
        if (loadingLaoutId == layoutId)
            return this;
        if (loadingView != null && indexOfChild(loadingView) >= 0) {
            removeView(loadingView);
        }
        loadingLaoutId = layoutId;
        return this;
    }

    public MultipleStatusLayout setEmptyView(View view) {
        if (emptyView == view)
            return this;
        if (emptyView != null && indexOfChild(emptyView) >= 0) {
            removeView(emptyView);
        }
        emptyView = view;
        return this;
    }

    public MultipleStatusLayout setEmptyLayoutId(@LayoutRes int layoutId) {
        if (emptyLayoutId == layoutId)
            return this;
        if (emptyView != null && indexOfChild(emptyView) >= 0) {
            removeView(emptyView);
        }
        emptyLayoutId = layoutId;
        return this;
    }

    public MultipleStatusLayout setErrorView(View view) {
        if (errorView == view)
            return this;
        if (errorView != null && indexOfChild(errorView) >= 0) {
            removeView(errorView);
        }
        errorView = view;
        return this;
    }

    public MultipleStatusLayout setErrorLayoutId(@LayoutRes int layoutId) {
        if (errorLayoutId == layoutId)
            return this;
        if (errorView != null && indexOfChild(errorView) >= 0) {
            removeView(errorView);
        }
        errorLayoutId = layoutId;
        return this;
    }


    public MultipleStatusLayout setContentView(View view) {
        if (contentView == view)
            return this;
        if (contentView != null && indexOfChild(contentView) >= 0) {
            removeView(contentView);
        }
        contentView = view;
        return this;
    }

    public MultipleStatusLayout setContentLayoutId(@LayoutRes int layoutId) {
        if (contentLayoutId == layoutId)
            return this;
        if (contentView != null && indexOfChild(contentView) >= 0) {
            removeView(contentView);
        }
        contentLayoutId = layoutId;
        return this;
    }

    public MultipleStatusLayout setNetworkBadView(View view) {
        if (networkBadView == view)
            return this;
        if (networkBadView != null && indexOfChild(networkBadView) >= 0) {
            removeView(networkBadView);
        }
        networkBadView = view;
        return this;
    }

    public MultipleStatusLayout setNetworkBadLayoutId(@LayoutRes int layoutId) {
        if (networkBadLayoutId == layoutId)
            return this;
        if (networkBadView != null && indexOfChild(networkBadView) >= 0) {
            removeView(networkBadView);
        }

        networkBadLayoutId = layoutId;
        return this;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 显示加载中布局
     */
    public void showLoading() {
        if (loadingView == null && loadingLaoutId != NULL_LAYOUT_ID) {
            loadingView = inflateView(loadingLaoutId);
            addView(loadingView, 0, DEFAULT_LAYOUT_PARAMS);
        } else if (indexOfChild(loadingView) == -1) {
            ViewParent viewParent = loadingView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(loadingView);
            }
            addView(loadingView, 0, DEFAULT_LAYOUT_PARAMS);
        }
        status = STATUS_LOADING;
        showView(loadingView);
    }

    public void showContent() {
        if (contentView == null && contentLayoutId != NULL_LAYOUT_ID) {
            contentView = inflateView(contentLayoutId);
            addView(contentView, 0, DEFAULT_LAYOUT_PARAMS);
        } else if (indexOfChild(contentView) == -1) {
            ViewParent viewParent = contentView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(contentView);
            }
            addView(contentView, 0, DEFAULT_LAYOUT_PARAMS);
        }
        status = STATUS_CONTENT;
        showView(contentView);
    }

    public void showEmpty() {
        if (emptyView == null && emptyLayoutId != NULL_LAYOUT_ID) {
            emptyView = inflateView(emptyLayoutId);
            addView(emptyView, 0, DEFAULT_LAYOUT_PARAMS);
        } else if (indexOfChild(emptyView) == -1) {
            ViewParent viewParent = emptyView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(emptyView);
            }
            addView(emptyView, 0, DEFAULT_LAYOUT_PARAMS);
        }
        status = STATUS_EMPTY;
        showView(emptyView);
    }

    public void showError() {
        if (errorView == null && errorLayoutId != NULL_LAYOUT_ID) {
            errorView = inflateView(errorLayoutId);
            addView(errorView, 0, DEFAULT_LAYOUT_PARAMS);
        } else if (indexOfChild(errorView) == -1) {
            ViewParent viewParent = errorView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(errorView);
            }
            addView(errorView, 0, DEFAULT_LAYOUT_PARAMS);
        }
        status = STATUS_ERROR;
        showView(errorView);
    }

    public void showNetworkBad() {
        if (networkBadView == null && networkBadLayoutId != NULL_LAYOUT_ID) {
            networkBadView = inflateView(networkBadLayoutId);
            addView(networkBadView, 0, DEFAULT_LAYOUT_PARAMS);
        } else if (indexOfChild(networkBadView) == -1) {
            ViewParent viewParent = networkBadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(networkBadView);
            }
            addView(networkBadView, 0, DEFAULT_LAYOUT_PARAMS);
        }
        status = STATUS_NETWORK_BAD;
        showView(networkBadView);
    }
}