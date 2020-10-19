package com.learn.agg;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.learn.agg.act.MainActivity;
import com.learn.agg.widgets.CustomDialog;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.lib.xiangxiang.im.ImService;
import com.lib.xiangxiang.im.ImSocketClient;
import com.lib.xiangxiang.im.SocketManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.senyint.ihospital.HttpConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

public class LearnApplication extends Application implements AppFrontBackHelper.OnAppStatusListener {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.deepskyblue, android.R.color.white);//全局设置主题颜色
                return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(1)         // (Optional) How many method line to show. Default 2
                .tag("log")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        new AppFrontBackHelper().register(this,this);
        NotificationUtils.initChannel(this,NotificationUtils.Chat_channelId,NotificationUtils.Chat_channelName, NotificationManager.IMPORTANCE_HIGH);
        HttpConfig.INSTANCE.init(Constant.BASE_GROUP_URL,getHeader(),getParams(),true);
    }




    /**
     * 公共Header
     * @return
     */
    public static HashMap<String,String> getHeader(){
        HashMap<String, String> hashMap = new HashMap<>();
        return hashMap;
    }

    /**
     * 公共参数
     * @return
     */
    public static HashMap<String,String> getParams(){
        HashMap<String, String> hashMap = new HashMap<>();
        return hashMap;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    public void onResumed(Activity activity) {

    }

    @Override
    public void onPaused(Activity activity) {

    }

    @Override
    public void onStarted(Activity activity) {

    }

    @Override
    public void onStopped(Activity activity) {
       
    }

    public static Context getContext() {
        return sInstance;
    }

    @Override
    public void onFront() {
        NotificationUtils.setIsSwitch(false);
        Log.i("Net","App回到前台"+ NotificationUtils.getIsSwitch());
        NotificationUtils.cancelAllNotification(this);
    }

    @Override
    public void onBack() {
        NotificationUtils.setIsSwitch(true);
        Log.i("Net","App遁入后台" + NotificationUtils.getIsSwitch());
    }

}
