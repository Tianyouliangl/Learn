package com.learn.agg.trtc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.learn.agg.R;
import com.learn.agg.trtc.VideoCallActivity;

import org.jetbrains.annotations.Nullable;

public class TRTCService extends Service {

    private final String TAG = "TRTCService";
    private final int WINDOW_MANAGER_CREATE = 1;
    private final int WINDOW_MANAGER_ADD_VIEW = 2;
    private final int WINDOW_MANAGER_REMOVE_VIEW = 3;
    private Boolean isRemoveView = false;
    private TRTCBinder binder = new TRTCBinder();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handlerWhat:" + msg.what);
            switch (msg.what) {
                case WINDOW_MANAGER_CREATE:
                    initWindow();
                    isRemoveView = false;
                    break;
                case WINDOW_MANAGER_ADD_VIEW:
                    if (windowManager != null && windowParams != null) {
                        // 添加悬浮窗的视图
                        isRemoveView = false;
                        windowManager.addView(window_view_video, windowParams);
                    }
                    break;
                case WINDOW_MANAGER_REMOVE_VIEW:
                    if (windowManager != null && !isRemoveView) {
                        isRemoveView = true;
                        windowManager.removeViewImmediate(window_view_video);
                    }
                    break;
            }
        }
    };
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;
    private View window_view_video;

    @Override
    public void onCreate() {
        super.onCreate();
        if (handler != null) {
            sendMessage(handler.obtainMessage(), WINDOW_MANAGER_CREATE);
        }

        Log.i(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void sendMessage(Message message, int what) {
        message.what = what;
        sendMessage(message, 0l);
    }

    private void sendMessage(Message message, Long time) {
        if (handler != null) {
            if (time > 0) {
                handler.sendMessage(message);
            } else {
                handler.sendMessageDelayed(message, time);
            }
        }

    }

    private void handlerDestroy() {
        if (handler != null) {
            handler.removeMessages(WINDOW_MANAGER_CREATE);
            handler.removeMessages(WINDOW_MANAGER_ADD_VIEW);
            handler.removeMessages(WINDOW_MANAGER_REMOVE_VIEW);
            handler = null;
        }
    }

    private void initWindow() {
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowParams = getParams();
        // 悬浮窗默认显示以左上角为起始坐标
        windowParams.gravity = Gravity.RIGHT | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
        windowParams.x = 80;
        windowParams.y = 180;
        // 获取浮动窗口视图所在布局
        window_view_video = LayoutInflater.from(getApplicationContext()).inflate(R.layout.window_view_video, null);
        window_view_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TRTCService.this, VideoCallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                sendMessage(handler.obtainMessage(), WINDOW_MANAGER_REMOVE_VIEW);
            }
        });
        window_view_video.setOnTouchListener(new ViewOnTouchListener());
    }

    private WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置可以显示在状态栏上
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return wmParams;
    }

    public class TRTCBinder extends Binder {

        public void showWindowView() {
            if (handler != null) {
                sendMessage(handler.obtainMessage(), WINDOW_MANAGER_ADD_VIEW);
            }

        }

        public void removeWindowView() {
            if (handler != null) {
                sendMessage(handler.obtainMessage(), WINDOW_MANAGER_REMOVE_VIEW);
            }
        }
    }

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private float mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private float mStartX, mStartY, mStopX, mStopY;
    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
    private boolean isMove;


    private class ViewOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    windowParams.x += -(mTouchCurrentX - mTouchStartX);
                    windowParams.y += mTouchCurrentY - mTouchStartY;
                    windowManager.updateViewLayout(window_view_video, windowParams);

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }
            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return isMove;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerDestroy();
    }
}
