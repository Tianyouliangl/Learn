package com.learn.commonalitylibrary.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
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
import android.util.Log;
import android.widget.ImageView;


import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.R;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class NotificationUtils {


    public static String channeGroupId = "com.learn.agg.group";
    public static String System_channelId = "com.learn.agg.system";
    public static String System_channelName = "通知栏显示Learn图标";
    public static String Chat_channelId = "com.learn.agg,chat";
    public static String Chat_channelName = "普通消息";
    public static String channelDescription = "消息通知";
    public static int System_Id = 1069;
    public static NotificationChannelGroup channelGroup;
    private static PendingIntent systemPendingIntent;
    private static Boolean isSwitch = false;

    public static void setIsSwitch(Boolean b){
        isSwitch = b;
    }

    public static Boolean getIsSwitch(){
        return isSwitch;
    }

    public static void initChannel(Context context, String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                channel.enableLights(false); //设置开启指示灯，如果设备有的话
                channel.setLightColor(Color.RED); //设置指示灯颜色
                channel.setShowBadge(true); //设置是否显示角标
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);//设置是否应在锁定屏幕上显示此频道的通知
                channel.setDescription(channelDescription);//设置渠道描述
                channel.setVibrationPattern(new long[]{100, 200, 300});//设置震动频率
                channel.setBypassDnd(true);//设置是否绕过免打扰模式
                //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    if (channelGroup == null) {
                        channelGroup = new NotificationChannelGroup(channeGroupId, channelDescription);
                        notificationManager.createNotificationChannelGroup(channelGroup);
                    }
                    channel.setGroup(channeGroupId);
                }
            }
        }
    }

    public static void showNotificationMessage(Context context, ChatMessage chatMessage){
        boolean switck = EasySP.init(context).getBoolean(Constant.SPKey_switch(context));
        switck = true;
        Log.i("socket","----"+getIsSwitch()+"-----"+switck);
        if (getIsSwitch() && switck){
            NotificationData notificationData = new NotificationData();
            notificationData.setId(chatMessage.getConversation().hashCode());
            notificationData.setTitle(ImSendMessageUtils.getMessageType(chatMessage));
            notificationData.setContent(ImSendMessageUtils.getChatBodyType(chatMessage));
            showNotification(context,notificationData);
        }
    }


    private static void showNotification(Context context, NotificationData data) {
        Notification.Builder builder;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, Chat_channelId);
        } else {
            builder = new Notification.Builder(context);
        }
        builder.setSmallIcon(R.mipmap.icon_logo_round)
                .setContentTitle(data.getTitle())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo))
                .setContentIntent(getSystemPendingIntent())
                .setContentText(data.getContent());
        builder.setNumber(1);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        manager.notify(data.getId(),notification);
    }

    /**
     * 取消所有通知
     * @param context
     */
    public static void cancelAllNotification(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }


    public static void setSystemPendingIntent(Activity activity) {
        Intent mIntent = new Intent(Intent.ACTION_MAIN);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mIntent.setComponent(new ComponentName("com.learn.agg", "com.learn.agg.act.MainActivity"));
        mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        systemPendingIntent = PendingIntent.getActivity(activity, 0, mIntent, 0);
    }

    public static PendingIntent getSystemPendingIntent() {
        return systemPendingIntent;
    }

    public static void getUserThreadCirBitmap(final String image_url, final ImageView view, final Activity context) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(image_url);
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    InputStream in;
                    in = conn.getInputStream();
                    final Bitmap map = BitmapFactory.decodeStream(in);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (map != null){
                                final Bitmap cirleBitmap = NotificationUtils.getCirleBitmap(map);
                                if (view != null && cirleBitmap != null){
                                    view.setImageBitmap(cirleBitmap);
                                }
                            }

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static Bitmap getCirleBitmap(Bitmap bmp) {
        //获取bmp的宽高 小的一个做为圆的直径r
        if (bmp != null){
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            int r = Math.min(w, h);

            //创建一个paint
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            //新创建一个Bitmap对象newBitmap 宽高都是r
            Bitmap newBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);

            //创建一个使用newBitmap的Canvas对象
            Canvas canvas = new Canvas(newBitmap);

            //canvas画一个圆形
            canvas.drawCircle(r / 2, r / 2, r / 2, paint);

            //然后 paint要设置Xfermode 模式为SRC_IN 显示上层图像（后绘制的一个）的相交部分
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            //canvas调用drawBitmap直接将bmp对象画在画布上 因为paint设置了Xfermode，所以最终只会显示这个bmp的一部分 也就
            //是bmp的和下层圆形相交的一部分圆形的内容
            canvas.drawBitmap(bmp, 0, 0, paint);
            return newBitmap;
        }
        return null;
    }
}
