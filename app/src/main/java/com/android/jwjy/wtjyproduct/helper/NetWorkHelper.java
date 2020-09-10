package com.android.jwjy.wtjyproduct.helper;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.android.jwjy.wtjyproduct.net.NetWorkStateReceiver;

import java.lang.ref.WeakReference;

/**
 * Created by ccy on 2019/5/10/11:41
 */
public class NetWorkHelper {
    protected NetWorkStateReceiver netWorkStateReceiver;
    private WeakReference<Context> contextWeakReference;

    public NetWorkHelper(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    private Context getContext() {
        return contextWeakReference.get();
    }

    public void registerNetWorkStateReceiver() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
//        EventBusUtil.register(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (getContext() != null) {
            getContext().registerReceiver(netWorkStateReceiver, intentFilter);
        }

    }

    public void unRegisterNetWorkStateReceiver() {
        if (netWorkStateReceiver == null || getContext() == null)
            return;
        getContext().unregisterReceiver(netWorkStateReceiver);
    }
}
