package com.android.jwjy.wtjyproduct.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by ccy on 2019/5/8/16:45
 */
public class BaseViewModel extends AndroidViewModel {
    protected Application application;
    protected Context context;
    public BaseViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setContext(Context context) {
        this.context =context;
    }
}
