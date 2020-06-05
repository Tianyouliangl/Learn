package com.learn.agg.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;
import java.util.Stack;

public class ActivityUtil {
    private static final String TAG = "ActivityUtil";
    /**
     * 单一实例
     */
    private static ActivityUtil sActivityUtil;
    /**
     * Activity堆栈 Stack:线程安全
     */
    public Stack<Activity> mActivityStack = new Stack<>();

    /**
     * 私有构造器 无法外部创建
     */
    private ActivityUtil() {
    }

    /**
     * 获取单一实例 双重锁定
     * @return this
     */
    public static ActivityUtil getInstance() {
        if (sActivityUtil == null) {
            synchronized (ActivityUtil.class) {
                if (sActivityUtil == null) {
                    sActivityUtil = new ActivityUtil();
                }
            }
        }
        return sActivityUtil;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    /**
     * 移除堆栈中的Activity
     * @param activity Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 获取当前Activity (堆栈中最后一个添加的)
     * @return Activity
     */
    public Activity getCurrentActivity() {
        return mActivityStack.lastElement();
    }

    /**
     * 获取指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        if (mActivityStack != null)
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 结束当前Activity (堆栈中最后一个添加的)
     */
    public void finishCurrentActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     * @param activity Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     * @param clazz Activity.class
     */
    public void finishActivity(Class<?> clazz) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(clazz)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {

        for (int i = mActivityStack.size() - 1; i >= 0; i--) {

            if (mActivityStack.get(i) != null) {
                finishActivity(mActivityStack.get(i));
            }
        }
        mActivityStack.clear();
    }

    /**
     * 结束某个Activity之外的所有Activity
     */
    public void finishAllActivityExcept(Class<?> clazz) {
        for (int i = mActivityStack.size() - 1; i >= 0; i--) {

            if (mActivityStack.get(i) != null && !mActivityStack.get(i).getClass().equals(clazz)) {
                finishActivity(mActivityStack.get(i));
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void exitApp() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean  isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
