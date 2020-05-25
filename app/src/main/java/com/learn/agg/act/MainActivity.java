package com.learn.agg.act;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.learn.agg.R;
import com.learn.agg.base.BaseAppCompatActivity;
import com.learn.agg.msg.fragment.MessageFragment;
import com.learn.agg.net.NetConfig;
import com.learn.agg.video.VideoFragment;
import com.learn.agg.widgets.CustomDialog;
import com.learn.agg.widgets.TabLayout;
import com.learn.commonalitylibrary.Constant;
import com.lib.xiangxiang.im.SocketManager;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseAppCompatActivity implements TabLayout.OnTabClickListener, View.OnClickListener {


    private TabLayout tab_layout;
    private Fragment mCurrentFragment;
    private CustomDialog customDialog;
    private Button btn_notification;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        tab_layout = findViewById(R.id.bottom_tab);

    }

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected void initData() {
        super.initData();
        NetConfig.init(getApplicationContext());
        SocketManager.loginSocket(this,"uid="+ EasySP.init(this).getString(Constant.SPKey_UID));
        customDialog = new CustomDialog(this, false, R.layout.layout_notification);
        ArrayList<TabLayout.Tab> list = new ArrayList<>();
        list.add(new TabLayout.Tab(R.drawable.selector_tab_msg, R.string.msg, MessageFragment.class));
        list.add(new TabLayout.Tab(R.drawable.selector_tab_video, R.string.video, VideoFragment.class));
        tab_layout.setUpData(list, this);
        tab_layout.setCurrentTab(0);
        View view = customDialog.getView();
        btn_notification = view.findViewById(R.id.btn_notification);
        btn_notification.setOnClickListener(this);

    }

    // 判断是否打开了通知
    private boolean isNotificationEnabled(Context context) {
        boolean isOpened = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            try {
                isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
            } catch (Exception e) {
                e.printStackTrace();
                isOpened = false;
            }
        }

        return isOpened;
    }

    // 去设置中开启
    private void goToSetNotification(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    public void onTabClick(TabLayout.Tab tab) {
        try {
            Fragment tmpFragment = getSupportFragmentManager().findFragmentByTag(tab.targetFragmentClz.getSimpleName());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (tmpFragment == null) {
                tmpFragment = tab.targetFragmentClz.newInstance();
                transaction.add(R.id.fl_learn, tmpFragment, tab.targetFragmentClz.getSimpleName());
                if (mCurrentFragment != null) {
                    transaction.hide(mCurrentFragment);
                }
                transaction.commit();
            } else {
                transaction.show(tmpFragment);
                if (mCurrentFragment != null) {
                    transaction.hide(mCurrentFragment);
                }
                transaction.commit();
            }
            mCurrentFragment = tmpFragment;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean b = isNotificationEnabled(this);
        if (!b){
            if (customDialog != null && !customDialog.isShowing()){
                customDialog.show();
            }
        }else {
//            SocketManager.startSystemNotification(this);
        }
    }

    //黏性事件的 订阅   

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void MessageEvent(HashMap<String, Object> map) {
        String fragmentName = (String) map.get("FragmentName");
        if (fragmentName != null && !fragmentName.isEmpty()){
            int count = (int) map.get("count");
            int index = tab_layout.findFragmentIndex(fragmentName);
            if (index != -1){
                tab_layout.onDataChanged(index,count);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("main","onBackPressed");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("main","keyCode == " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME){

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        NetConfig.unRegisterReceiver(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_notification:
                goToSetNotification(this);
                if (customDialog != null && customDialog.isShowing()){
                    customDialog.dismiss();
                }
                break;
            default:
                break;
        }
    }
}
