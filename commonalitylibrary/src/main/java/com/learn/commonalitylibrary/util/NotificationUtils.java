package com.learn.commonalitylibrary.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.learn.commonalitylibrary.BuildConfig;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.R;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class NotificationUtils {


    static String channeGroupId = "com.learn.agg.group";
    public static String System_channelId = "com.learn.agg.system";
    public static String System_channelName = "通知栏显示Learn图标";
    public static String Chat_channelId = "com.learn.agg,chat";
    public static String Chat_channelName = "普通消息";
    static String channelDescription = "消息通知";
    static NotificationChannelGroup channelGroup;
    private static Bitmap cirleBitmap;

    public static void initChannel(Context context,String channelId,String channelName,int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName,importance);
                channel.enableLights(false); //设置开启指示灯，如果设备有的话
                channel.setLightColor(Color.RED); //设置指示灯颜色
                channel.setShowBadge(true); //设置是否显示角标
                if (importance == NotificationManager.IMPORTANCE_LOW){
                    channel.setSound(null,null);
                }
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知
                channel.setDescription(channelDescription);//设置渠道描述
//                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 600});//设置震动频率
//                channel.setBypassDnd(true);//设置是否绕过免打扰模式
                //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    if (channelGroup == null){
                        channelGroup = new NotificationChannelGroup(channeGroupId, channelDescription);
                        notificationManager.createNotificationChannelGroup(channelGroup);
                    }
                    channel.setGroup(channeGroupId);
                }
            }
        }
    }

    public static void getUserThreadCirBitmap(final String image_url){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(image_url);
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    InputStream in;
                    in = conn.getInputStream();
                    Bitmap map = BitmapFactory.decodeStream(in);
                    cirleBitmap = NotificationUtils.getCirleBitmap(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static Notification createSystemNotification(final Context context){
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String name = EasySP.init(context).getString(Constant.SPKey_userName(context));
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setChannelId(System_channelId);
            builder.setContentTitle(name);
            builder.setContentText("Learn正在后台运行");
            builder.setSmallIcon(R.mipmap.icon_logo_round);
            builder.setSound(null);
            builder.setWhen(System.currentTimeMillis());
            builder.setLargeIcon(cirleBitmap);
            notification = builder.build();
        }
        return notification;
    }

    public static Bitmap getCirleBitmap(Bitmap bmp) {
        //获取bmp的宽高 小的一个做为圆的直径r
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
}
