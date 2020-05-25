package com.lib.xiangxiang.im;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.learn.commonalitylibrary.BuildConfig;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.net.URL;

import static android.app.Notification.VISIBILITY_SECRET;

/**
 * author : fengzhangwei
 * date : 2019/12/19
 */
public class ImService extends Service {

    // 命令key
    public static final String SOCKET_CMD = "cmd";

    //  命令操作
    public static final int SOCKET_INIT = 0;   // 初始化,连接
    public static final int SOCKET_RESET = 1; //  断开,关闭所有监听
    public static final int SOCKET_SEND_MSG = 2; // 发送消息
    public static final int SOCKET_SEND_MSG_CALLBACK = 3; // 发送消息回调
    public static final int SOCKET_RECEIVER_MSG = 5; // 收到消息
    public static final int NOTIFICATION_START = 6; // 通知栏显示系统Learn图标
    public static final int NOTIFICATION_CANCEL = 7; // 取消系统Learn图标

    // 传递参数 key
    public static final String SOCKET_PID = "pid";
    public static final String SOCKET_DATA = "data";
    public static final String SOCKET_MSG = "msg";
    public static final String SOCKET_EVENT = "event";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createSystemNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int cmd = intent.getIntExtra(SOCKET_CMD, -1);
            switch (cmd) {
                case SOCKET_INIT:
                    initSocket(intent);
                    break;
                case SOCKET_RESET:
                    releaseSocket();
                    break;
                case SOCKET_SEND_MSG:
                    sendMsgSocket(intent);
                    break;
                case SOCKET_SEND_MSG_CALLBACK:
                    callMsg2UI(intent);
                    break;
                case SOCKET_RECEIVER_MSG:
                    callChatMsg2UI(intent);
                    break;
                case NOTIFICATION_START:
                    createSystemNotification();
                    break;
                case NOTIFICATION_CANCEL:
                    cancelSystemNotification();
                    break;
                default:

                    break;
            }
        }
        return START_STICKY;
    }

    private void cancelSystemNotification() {
        stopForeground(true);
    }

    private void createSystemNotification() {
        startForeground(1069,NotificationUtils.createSystemNotification(this));
    }

    private void releaseSocket() {
        ImSocketClient.release();
        stopSelf();
    }

    private void callChatMsg2UI(Intent intent) {
        String result = intent.getStringExtra(SOCKET_MSG);
        Intent mIntent = new Intent();
        mIntent.setAction(SocketManager.msgBRCallReceiver.ACTION);
        mIntent.putExtra(SocketManager.msgBRCallReceiver.RESULT, result);
        sendBroadcast(mIntent);
    }

    private void callMsg2UI(Intent intent) {
        String msg_id = intent.getStringExtra(SOCKET_PID);
        String result = intent.getStringExtra(SOCKET_DATA);
        Intent mIntent = new Intent();
        mIntent.setAction(SocketManager.sendMsgBRCallReceiver.ACTION);
        mIntent.putExtra(SocketManager.sendMsgBRCallReceiver.MSG_ID, msg_id);
        mIntent.putExtra(SocketManager.sendMsgBRCallReceiver.RESULT, result);
        sendBroadcast(mIntent);
    }

    private void sendMsgSocket(Intent intent) {
        String msg = intent.getStringExtra(SOCKET_MSG);
        String msg_id = intent.getStringExtra(SOCKET_PID);
        ImSocketClient.sendMsg(this, msg, msg_id);
    }

    private void initSocket(Intent intent) {
        String token = intent.getStringExtra(SOCKET_DATA);
        ImSocketClient.initSocket(token, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

}
