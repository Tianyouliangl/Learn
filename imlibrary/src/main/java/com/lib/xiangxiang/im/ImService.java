package com.lib.xiangxiang.im;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
        final String name = EasySP.init(this).getString(Constant.SPKey_userName(this));
        final String icon = EasySP.init(this).getString(Constant.SPKey_icon(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannel(this,NotificationUtils.System_channelId,NotificationUtils.System_channelName, NotificationManager.IMPORTANCE_LOW);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        URL url = new URL(icon);
                        URLConnection conn = url.openConnection();
                        conn.connect();
                        InputStream in;
                        in = conn.getInputStream();
                        Bitmap map = BitmapFactory.decodeStream(in);
                        Bitmap cirleBitmap = NotificationUtils.getCirleBitmap(map);
                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(ImService.this);
                        builder.setChannelId(NotificationUtils.System_channelId);
                        builder.setContentTitle(name);
                        builder.setContentText("Learn正在运行");
                        builder.setSmallIcon(com.learn.commonalitylibrary.R.mipmap.icon_logo_round);
                        builder.setSound(null);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setShowWhen(false);
                        builder.setLargeIcon(cirleBitmap);
                        builder.setContentIntent(NotificationUtils.getSystemPendingIntent());
                        Notification notification = builder.build();
                        startForeground(NotificationUtils.System_Id, notification);
                        Log.i("Net", "创建通知-----");
                    } catch (IOException e) {
                        Log.i("Net", "错误：" + e.toString());
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    public  void initChannel(Context context, String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                channel.enableLights(false); //设置开启指示灯，如果设备有的话
                channel.setLightColor(Color.RED); //设置指示灯颜色
                channel.setShowBadge(true); //设置是否显示角标
                if (importance == NotificationManager.IMPORTANCE_LOW) {
                    channel.setSound(null, null);
                }
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知
                channel.setDescription(NotificationUtils.channelDescription);//设置渠道描述
//                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 600});//设置震动频率
//                channel.setBypassDnd(true);//设置是否绕过免打扰模式
                //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    if (NotificationUtils.channelGroup == null) {
                        NotificationUtils.channelGroup = new NotificationChannelGroup(NotificationUtils.channeGroupId, NotificationUtils.channelDescription);
                        notificationManager.createNotificationChannelGroup(NotificationUtils.channelGroup);
                    }
                    channel.setGroup(NotificationUtils.channeGroupId);
                }
            }
        }
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
                default:

                    break;
            }
        }
        return START_STICKY;
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
