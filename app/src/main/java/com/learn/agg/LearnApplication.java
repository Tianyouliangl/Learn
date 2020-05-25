package com.learn.agg;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.senyint.ihospital.HttpConfig;
import java.util.HashMap;

public class LearnApplication extends Application{

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

    private int count = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtils.initChannel(this,NotificationUtils.System_channelId,NotificationUtils.System_channelName, NotificationManager.IMPORTANCE_LOW);
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
}
