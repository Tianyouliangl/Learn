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
import android.media.Image;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.LocationBody;
import com.learn.commonalitylibrary.body.VoiceBody;
import com.learn.commonalitylibrary.util.FileUpLoadManager;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.orhanobut.logger.Logger;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    public static Boolean startService = false;
    private Map<String, String> upLoadMap;


    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        upLoadMap = new HashMap<>();
        final String name = EasySP.init(this).getString(Constant.SPKey_userName(this));
        final String icon = EasySP.init(this).getString(Constant.SPKey_icon(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannel(this, NotificationUtils.System_channelId, NotificationUtils.System_channelName, NotificationManager.IMPORTANCE_LOW);
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


    public void initChannel(Context context, String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                channel.enableLights(false); //设置开启指示灯，如果设备有的话
                channel.setLightColor(Color.RED); //设置指示灯颜色
                channel.setShowBadge(true); //设置是否显示角标
                channel.setSound(null, null);
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
        ChatMessage chatMessage = GsonUtil.GsonToBean(msg, ChatMessage.class);
        if (chatMessage.getBodyType() == ChatMessage.MSG_BODY_TYPE_VOICE) {
            VoiceBody voiceBody = GsonUtil.GsonToBean(chatMessage.getBody(), VoiceBody.class);
            if (!voiceBody.getUrl().isEmpty()) {
                ImSocketClient.sendMsg(ImService.this, GsonUtil.BeanToJson(chatMessage), msg_id);
            } else {
                upLoadFile(voiceBody.getFileAbsPath(), chatMessage.getBodyType(), chatMessage, msg_id);
            }
        } else if (chatMessage.getBodyType() == ChatMessage.MSG_BODY_TYPE_IMAGE) {
            ImageBody imageBody = GsonUtil.GsonToBean(chatMessage.getBody(), ImageBody.class);
            if (imageBody.getImage().startsWith("https://")) {
                ImSocketClient.sendMsg(ImService.this, GsonUtil.BeanToJson(chatMessage), msg_id);
            } else {
                Boolean upLoad = isUpLoad(imageBody);
                Logger.t(ImSocketClient.TAG).i("是否存在同图片上传:" + upLoad);
                upLoadMap.put(msg_id, msg);
                if (!upLoad) {
                    upLoadFile(imageBody.getImage(), chatMessage.getBodyType(), chatMessage, msg_id);
                }
            }
        } else if (chatMessage.getBodyType() == ChatMessage.MSG_BODY_TYPE_LOCATION) {
            LocationBody locationBody = GsonUtil.GsonToBean(chatMessage.getBody(), LocationBody.class);
            if (!locationBody.getUrl().isEmpty()) {
                ImSocketClient.sendMsg(ImService.this, GsonUtil.BeanToJson(chatMessage), msg_id);
            } else {
                upLoadFile(locationBody.getLocation_url(), chatMessage.getBodyType(), chatMessage, msg_id);
            }
        } else {
            ImSocketClient.sendMsg(this, msg, msg_id);
        }
    }

    private void initSocket(Intent intent) {
        startService = true;
        String token = intent.getStringExtra(SOCKET_DATA);
        ImSocketClient.initSocket(token, this);
    }

    private Boolean isUpLoad(ImageBody body) {
        for (String value : upLoadMap.values()) {
            ChatMessage chatMessage = GsonUtil.GsonToBean(value, ChatMessage.class);
            String messageBody = chatMessage.getBody();
            ImageBody imageBody = GsonUtil.GsonToBean(messageBody, ImageBody.class);
            if (imageBody.getImage().equals(body.getImage())) {
                return true;
            }
        }
        return false;
    }

    private void upLoadFile(String file_Location_path, final int body_type, final ChatMessage chatMessage, final String msg_id) {
        if (file_Location_path.isEmpty()) return;
        new FileUpLoadManager().upLoadFile(file_Location_path, new FileUpLoadManager.FileUpLoadCallBack() {
            @Override
            public void onError(Throwable e) {

                if (body_type != ChatMessage.MSG_BODY_TYPE_IMAGE){
                    chatMessage.setMsgStatus(ChatMessage.MSG_SEND_ERROR);
                    String result = GsonUtil.BeanToJson(chatMessage);
                    Intent mIntent = new Intent();
                    mIntent.setAction(SocketManager.sendMsgBRCallReceiver.ACTION);
                    mIntent.putExtra(SocketManager.sendMsgBRCallReceiver.MSG_ID, msg_id);
                    mIntent.putExtra(SocketManager.sendMsgBRCallReceiver.RESULT, result);
                    sendBroadcast(mIntent);
                }else {
                    ImageBody imageBody = GsonUtil.GsonToBean(chatMessage.getBody(), ImageBody.class);
                    Iterator<Map.Entry<String, String>> iterator = upLoadMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        String msg = entry.getValue();
                        ChatMessage message = GsonUtil.GsonToBean(msg, ChatMessage.class);
                        String messageBody = message.getBody();
                        ImageBody body = GsonUtil.GsonToBean(messageBody, ImageBody.class);
                        if (body.getImage().equals(imageBody.getImage())) {
                            message.setMsgStatus(ChatMessage.MSG_SEND_ERROR);
                            String result = GsonUtil.BeanToJson(message);
                            Intent mIntent = new Intent();
                            mIntent.setAction(SocketManager.sendMsgBRCallReceiver.ACTION);
                            mIntent.putExtra(SocketManager.sendMsgBRCallReceiver.MSG_ID, msg_id);
                            mIntent.putExtra(SocketManager.sendMsgBRCallReceiver.RESULT, result);
                            sendBroadcast(mIntent);
                            iterator.remove();
                        }
                    }
                }

            }

            @Override
            public void onSuccess(String url) {

                if (body_type == ChatMessage.MSG_BODY_TYPE_VOICE) {
                    VoiceBody body = GsonUtil.GsonToBean(chatMessage.getBody(), VoiceBody.class);
                    body.setUrl(url);
                    chatMessage.setBody(GsonUtil.BeanToJson(body));
                    ImSocketClient.sendMsg(ImService.this, GsonUtil.BeanToJson(chatMessage), msg_id);
                }
                if (body_type == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                    ImageBody imageBody = GsonUtil.GsonToBean(chatMessage.getBody(), ImageBody.class);
                    Iterator<Map.Entry<String, String>> iterator = upLoadMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        String pid = entry.getKey();
                        String msg = entry.getValue();
                        ChatMessage message = GsonUtil.GsonToBean(msg, ChatMessage.class);
                        String messageBody = message.getBody();
                        ImageBody body = GsonUtil.GsonToBean(messageBody, ImageBody.class);
                        if (body.getImage().equals(imageBody.getImage())) {
                            message.setBody(GsonUtil.BeanToJson(imageBody));
                            ImSocketClient.sendMsg(ImService.this, GsonUtil.BeanToJson(message), pid);
                            iterator.remove();
                        }
                    }

                }
                if (body_type == ChatMessage.MSG_BODY_TYPE_LOCATION) {
                    LocationBody locationBody = GsonUtil.GsonToBean(chatMessage.getBody(), LocationBody.class);
                    locationBody.setUrl(url);
                    chatMessage.setBody(GsonUtil.BeanToJson(locationBody));
                    ImSocketClient.sendMsg(ImService.this, GsonUtil.BeanToJson(chatMessage), msg_id);
                }
            }

            @Override
            public void onProgress(int pro) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
