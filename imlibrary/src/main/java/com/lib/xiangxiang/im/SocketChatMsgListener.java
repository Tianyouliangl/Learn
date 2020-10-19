package com.lib.xiangxiang.im;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.orhanobut.logger.Logger;

import io.socket.emitter.Emitter;

/**
 * author : fengzhangwei
 * date : 2019/12/19
 */
public class SocketChatMsgListener implements Emitter.Listener {

    private final Context mContext;

    public SocketChatMsgListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void call(Object... args) {

        String result = "";
        if (args.toString().isEmpty()){
            result = args.toString();
        }else {
            result = args[0].toString();
        }
        Intent intent = new Intent(mContext, ImService.class);
        intent.putExtra(ImService.SOCKET_CMD,ImService.SOCKET_RECEIVER_MSG);
        intent.putExtra(ImService.SOCKET_MSG,result);
        startService(mContext,intent);
        Logger.t("socket").json("{"+"收到消息:"+result+"}");

    }

    private void startService(Context appContext, Intent intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            appContext.startForegroundService(intent);
//        } else {
//            appContext.startService(intent);
//        }
        appContext.startService(intent);
    }
}
