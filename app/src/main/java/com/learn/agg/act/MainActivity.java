package com.learn.agg.act;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gastudio.downloadloadding.library.GADownloadingView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.learn.agg.R;
import com.learn.agg.base.BaseAppCompatActivity;
import com.learn.agg.base.BaseSlidingFragmentActivity;
import com.learn.agg.base.IconOnClickListener;
import com.learn.agg.fragment.MenuLeftFragment;
import com.learn.agg.msg.fragment.MessageFragment;
import com.learn.agg.net.NetConfig;
import com.learn.agg.video.VideoFragment;
import com.learn.agg.widgets.CustomDialog;
import com.learn.agg.widgets.TabLayout;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.lib.xiangxiang.im.SocketManager;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class MainActivity extends BaseSlidingFragmentActivity implements TabLayout.OnTabClickListener, View.OnClickListener, IconOnClickListener, DownloadFileListener, UpdateManagerListener {


    private TabLayout tab_layout;
    private Fragment mCurrentFragment;
    private CustomDialog customDialog;
    private Button btn_notification;
    private CustomDialog updateDialog;
    private TextView updateTvContent;
    private View updateNumberView;
    private GADownloadingView ga_downloading;
    private String updateUrl;
    private LinearLayout line_top;
    private File file;
    private Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                updateDialog.setCancelable(true);
                updateDialog.setCanceledOnTouchOutside(false);
                line_top.setVisibility(View.VISIBLE);
                tv_top.setVisibility(View.INVISIBLE);
                tv_success.setVisibility(View.VISIBLE);
            }
        }
    };
    private TextView tv_top;
    private TextView tv_success;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        tab_layout = (TabLayout) findViewById(R.id.bottom_tab);
    }

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected void initData() {
        super.initData();
        initMenu();
        initDialog();
        updateCode();
        NetConfig.init(getApplicationContext());
        NotificationUtils.setSystemPendingIntent(this);
        String uid = EasySP.init(this).getString(Constant.SPKey_UID);
        Log.i("Net", "------------uid==========  " + uid);
        SocketManager.loginSocket(this, "uid=" + uid);
        ArrayList<TabLayout.Tab> list = new ArrayList<>();
        list.add(new TabLayout.Tab(R.drawable.selector_tab_msg, R.string.msg, MessageFragment.class));
        list.add(new TabLayout.Tab(R.drawable.selector_tab_video, R.string.video, VideoFragment.class));
        tab_layout.setUpData(list, this);
        tab_layout.setCurrentTab(0);
    }

    private void initDialog() {
        customDialog = new CustomDialog(this, false, R.layout.layout_notification);
        updateDialog = new CustomDialog(this, true, R.layout.dialog_update_apk);
        updateNumberView = View.inflate(this, R.layout.dialog_update_number, null);
        View view = customDialog.getView();
        View updateView = updateDialog.getView();
        updateTvContent = updateView.findViewById(R.id.tv_content);
        TextView tv_update = updateView.findViewById(R.id.tv_update);
        tv_update.setOnClickListener(this);
        btn_notification = view.findViewById(R.id.btn_notification);
        btn_notification.setOnClickListener(this);
        ga_downloading = updateNumberView.findViewById(R.id.ga_downloading);
        line_top = updateNumberView.findViewById(R.id.line_top);
        tv_top = updateNumberView.findViewById(R.id.tv_top);
        tv_success = updateNumberView.findViewById(R.id.tv_success);
        TextView tv_install = updateNumberView.findViewById(R.id.tv_install);
        tv_install.setOnClickListener(this);
    }

    private void updateCode() {
        /** 可选配置集成方式 **/
        new PgyUpdateManager.Builder()
                .setForced(true)                //设置是否强制提示更新
                // v3.0.4+ 以上同时可以在官网设置强制更新最高低版本；网站设置和代码设置一种情况成立则提示强制更新
                .setUserCanRetry(false)         //失败后是否提示重新下载
                .setDeleteHistroyApk(false)     // 检查更新前是否删除本地历史 Apk， 默认为true
                .setUpdateManagerListener(this)
                .setDownloadFileListener(this)
                .register();
    }

    private void initMenu() {
        if (Build.VERSION.SDK_INT >= 21) {
            getSlidingMenu().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        Fragment leftMenuFragment = new MenuLeftFragment();
        setBehindContentView(R.layout.layout_menu);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
        SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(1.0f);
    }

    public void showLeftMenu() {
        getSlidingMenu().showMenu();
    }

    @Override
    public void showMenu() {
        super.showMenu();
        showLeftMenu();
    }

    @Override
    public void closeMenu() {
        getSlidingMenu().toggle();
    }

    // 判断是否打开了通知
    private boolean isNotificationEnabled(Context context) {
        boolean isOpened = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        if (!b) {
            if (customDialog != null && !customDialog.isShowing()) {
                customDialog.show();
            }
        }
    }

    //事件的 订阅 
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(HashMap<String, Object> map) {
        String fragmentName = (String) map.get("FragmentName");
        if (fragmentName != null && !fragmentName.isEmpty()) {
            if (tab_layout != null) {
                int count = (int) map.get("count");
                int index = tab_layout.findFragmentIndex(fragmentName);
                if (index != -1) {
                    tab_layout.onDataChanged(index, count);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("main", "onBackPressed");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("main", "keyCode == " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!getSlidingMenu().isMenuShowing()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        NetConfig.unRegisterReceiver(this);
        EventBus.getDefault().unregister(this);
        SocketManager.logOutSocket(this);
        mHandle.removeMessages(1);
        mHandle= null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_notification:
                goToSetNotification(this);
                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }
                break;
            case R.id.iv_close:

                break;
            case R.id.tv_update:
                if (updateDialog != null && updateDialog.isShowing()) {
                    updateDialog.setCancelable(false);
                    updateDialog.setCanceledOnTouchOutside(false);
                    updateDialog.setContentView(updateNumberView);
                    ga_downloading.performAnimation();
                    ga_downloading.updateProgress(0);
                    PgyUpdateManager.downLoadApk(updateUrl);
                }
                break;
            case R.id.tv_install:
                if (file != null){
                    PgyUpdateManager.installApk(file);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNoUpdateAvailable() {
        //没有更新是回调此方法
    }

    @Override
    public void onUpdateAvailable(AppBean appBean) {
        //有更新回调此方法
        //调用以下方法，DownloadFileListener 才有效；
        //如果完全使用自己的下载方法，不需要设置DownloadFileListener
        //PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
        updateUrl = appBean.getDownloadURL();
        updateTvContent.setText("新版本:" + appBean.getVersionName() + "\n" + "赶紧下载体验吧~");
        if (updateDialog != null && !updateDialog.isShowing()) {
            updateDialog.show();
        }
    }

    @Override
    public void checkUpdateFailed(Exception e) {
        //更新检测失败回调
        //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
    }

    //注意 ：
    //下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
    //此方法是方便用户自己实现下载进度和状态的 UI 提供的回调
    //想要使用蒲公英的默认下载进度的UI则不设置此方法
    @Override
    public void downloadFailed() {
        //下载失败
        ga_downloading.onFail();
    }

    @Override
    public void downloadSuccessful(File f) {
        // 使用蒲公英提供的安装方法提示用户 安装apk
        // PgyUpdateManager.installApk(file);
        file = f;
        mHandle.sendEmptyMessageDelayed(1,2000);
    }

    @Override
    public void onProgressUpdate(Integer... args) {
        // 进度  "update download apk progress" + integers
        ga_downloading.updateProgress(args[0]);
    }


}
