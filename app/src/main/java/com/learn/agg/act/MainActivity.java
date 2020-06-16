package com.learn.agg.act;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gastudio.downloadloadding.library.GADownloadingView;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.learn.agg.R;
import com.learn.agg.base.BaseSlidingFragmentActivity;
import com.learn.agg.base.IconOnClickListener;
import com.learn.agg.fragment.MenuLeftFragment;
import com.learn.agg.msg.act.ChatActivity;
import com.learn.agg.msg.fragment.MessageFragment;
import com.learn.agg.net.NetConfig;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.util.ActivityUtil;
import com.learn.agg.video.ReadFragment;
import com.learn.agg.widgets.CustomDialog;
import com.learn.agg.widgets.TabLayout;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.lib.xiangxiang.im.ImService;
import com.lib.xiangxiang.im.SocketManager;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class MainActivity extends BaseSlidingFragmentActivity implements TabLayout.OnTabClickListener, View.OnClickListener, IconOnClickListener, DownloadFileListener, UpdateManagerListener {


    private TabLayout tab_layout;
    private Fragment mCurrentFragment;
    private CustomDialog customDialog;
    private TextView tv_notification;
    private CustomDialog updateDialog;
    private TextView updateTvContent;
    private View updateNumberView;
    private GADownloadingView ga_downloading;
    private String updateUrl;
    private LinearLayout line_top;
    private File file;
    private Boolean isUpdateAPK = false;
    private Boolean isOffLine = false;
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                updateDialog.setCancelable(true);
                updateDialog.setCanceledOnTouchOutside(true);
                line_top.setVisibility(View.VISIBLE);
                tv_top.setVisibility(View.INVISIBLE);
                tv_success.setVisibility(View.VISIBLE);
            }
        }
    };
    private TextView tv_top;
    private TextView tv_success;
    private CustomDialog offLineDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        ActivityUtil.getInstance().addActivity(this);
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
        ArrayList<TabLayout.Tab> list = new ArrayList<>();
        list.add(new TabLayout.Tab(R.drawable.selector_tab_msg, R.string.msg, MessageFragment.class));
        list.add(new TabLayout.Tab(R.drawable.selector_tab_read, R.string.books, ReadFragment.class));
        tab_layout.setUpData(list, this);
        tab_layout.setCurrentTab(0);
    }

    private void loginSocket() {
        String token = EasySP.init(this).getString(Constant.SPKey_token(this));
        String uid = EasySP.init(this).getString(Constant.SPKey_UID);
        String mobile = EasySP.init(this).getString(Constant.SPKey_phone(this));
        Log.i("Net", "------------token==========  " + token);
        SocketManager.loginSocket(this, "token=" + token + "&" + "uid=" + uid + "&" + "mobile=" + mobile + "&" + "desc=" + "Android");
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
        tv_notification = view.findViewById(R.id.tv_notification);
        tv_notification.setOnClickListener(this);
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
        } else {
            if (customDialog != null && customDialog.isShowing()) {
                customDialog.dismiss();
            }
            if (isUpdateAPK && !isOffLine) {
                if (getSlidingMenu().isMenuShowing()){
                    closeMenu();
                }
                showUpdateDialog();
                return;
            }
            if (!isOffLine && !ImService.startService) {
                loginSocket();
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

    //事件的 订阅 
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ChatMessage msg) {
        if (msg.getType() == ChatMessage.MSG_OFFLINE) {
            ActivityUtil.getInstance().finishAllActivityExcept(MainActivity.class);
            SocketManager.logOutSocket(this);
            isOffLine = true;
            if (updateDialog != null && updateDialog.isShowing()) {
                updateDialog.dismiss();
            }
            if (getSlidingMenu().isMenuShowing()) {
                getSlidingMenu().toggle();
            }
            if (offLineDialog == null) {
                offLineDialog = new CustomDialog(this, false, R.layout.dialog_offline);
            }
            View view = offLineDialog.getView();
            TextView tv_content = view.findViewById(R.id.tv_content);
            TextView tv_exit = view.findViewById(R.id.tv_exit);
            TextView aga_login = view.findViewById(R.id.tv_again_login);
            tv_exit.setOnClickListener(this);
            aga_login.setOnClickListener(this);
            tv_content.setText(msg.getBody());
            offLineDialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!getSlidingMenu().isMenuShowing()) {
                moveTaskToBack(true);
                return true;
            }else {
                getSlidingMenu().toggle();
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
        PgyUpdateManager.unRegister();
        ActivityUtil.getInstance().removeActivity(this);
        mHandle.removeMessages(1);
        mHandle = null;
        offLineDialog = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_notification:
                goToSetNotification(this);
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
                if (file != null) {
                    PgyUpdateManager.installApk(file);
                }
                break;
            case R.id.tv_exit:
                isOffLine = false;
                dismissOffLineDialog();
                goActivity(LoginActivity.class);
                finish();
                break;
            case R.id.tv_again_login:
                isOffLine = false;
                dismissOffLineDialog();
                agaLogin();
                break;
            default:
                break;
        }
    }

    private void agaLogin() {
        String phone = EasySP.init(this).getString(Constant.SPKey_phone(this));
        final String password = EasySP.init(this).getString(Constant.SPKey_pwd(this));
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("password", password);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .login(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>() {

                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        EasySP.init(MainActivity.this).put(Constant.SPKey_UID, data.getUid())
                                .put(Constant.SPKey_phone(MainActivity.this), data.getMobile())
                                .put(Constant.SPKey_pwd(MainActivity.this), password)
                                .put(Constant.SPKey_userName(MainActivity.this), data.getUsername())
                                .put(Constant.SPKey_icon(MainActivity.this), data.getImageUrl())
                                .put(Constant.SPKey_info(MainActivity.this), new Gson().toJson(data))
                                .put(Constant.SPKey_token(MainActivity.this), data.getToken());
                        loginSocket();
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        goActivity(LoginActivity.class);
                        MainActivity.this.finish();
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        goActivity(LoginActivity.class);
                        MainActivity.this.finish();
                    }
                });
    }

    public void dismissOffLineDialog() {
        if (offLineDialog != null && offLineDialog.isShowing()) {
            offLineDialog.dismiss();
        }
    }


    @Override
    public void onNoUpdateAvailable() {
        //没有更新是回调此方法
    }

    @Override
    public void onUpdateAvailable(AppBean appBean) {
        isUpdateAPK = true;
        //有更新回调此方法
        //调用以下方法，DownloadFileListener 才有效；
        //如果完全使用自己的下载方法，不需要设置DownloadFileListener
        //PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
        String versionName = appBean.getVersionName();
        String downloadURL = appBean.getDownloadURL();
        String releaseNote = appBean.getReleaseNote();
        String versionCode = appBean.getVersionCode();
        Log.i("main", "versionName:" + versionName + "\n" +
                "downloadURL:" + downloadURL + "\n" + "releaseNote:" +
                releaseNote + "\n" + "versionCode:" + versionCode);
        updateUrl = appBean.getDownloadURL();
        if (!releaseNote.isEmpty()) {
            updateTvContent.setGravity(Gravity.LEFT);
            updateTvContent.setText("更新内容:" + "\n" + releaseNote);
        } else {
            updateTvContent.setGravity(Gravity.CENTER);
            updateTvContent.setText("新版本:" + appBean.getVersionName() + "\n" + "赶紧下载体验吧~");
        }
        if (!customDialog.isShowing()) {
            showUpdateDialog();
        }
    }

    public void showUpdateDialog() {
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
        mHandle.sendEmptyMessageDelayed(1, 2000);
    }

    @Override
    public void onProgressUpdate(Integer... args) {
        // 进度  "update download apk progress" + integers
        ga_downloading.updateProgress(args[0]);
    }

}
