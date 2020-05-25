package com.learn.agg.net;

import android.content.Context;
import android.content.IntentFilter;

import com.learn.agg.net.base.NetBroadcastReceiver;

/**
 * author : fengzhangwei
 * date : 2019/9/12
 */
public class NetConfig {


    private static NetBroadcastReceiver netBroadcastReceiver = null;

    public static void init(Context context) {
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netBroadcastReceiver = new NetBroadcastReceiver();
        //注册广播接收
        context.registerReceiver(netBroadcastReceiver, filter);
    }

    public static void unRegisterReceiver(Context context){
        if (netBroadcastReceiver != null){
            context.unregisterReceiver(netBroadcastReceiver);
        }
    }



}
