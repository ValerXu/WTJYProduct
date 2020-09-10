package com.android.jwjy.wtjyproduct.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.jwjy.wtjyproduct.consts.EventType;
import com.android.jwjy.wtjyproduct.entity.Event;
import com.android.jwjy.wtjyproduct.util.EventBusUtil;

/**
 * Created by asus on 2018/1/11.
 */

public class NetWorkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = NetMonitor.getNetWorkState(context);
        EventBusUtil.postEvent(new Event(EventType.NETWORK_STATE_CHANGE,state));
    }
}
