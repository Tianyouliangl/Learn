package com.lib.xiangxiang.im;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.socket.client.Ack;

/**
 * author : fengzhangwei
 * date : 2019/12/19
 */
public class SocketSendMsgCallBackAck implements Ack {

    private final Context mContext;
    private final String mMsgId;

    public SocketSendMsgCallBackAck(Context context, String msgId) {
        this.mContext = context;
        this.mMsgId = msgId;
    }

    @Override
    public void call(Object... args) {
        String result = "";
        if (args.toString().isEmpty()){
            result = args.toString();
        }else {
            if (args.clone().length > 0){
                result = args[0].toString();
            }
        }
        Intent intent = new Intent(mContext, ImService.class);
        intent.putExtra(ImService.SOCKET_CMD,ImService.SOCKET_SEND_MSG_CALLBACK);
        intent.putExtra(ImService.SOCKET_PID,mMsgId);
        intent.putExtra(ImService.SOCKET_DATA,result);
        startService(mContext,intent);
        if (args.toString().isEmpty()){
            Log.i(ImSocketClient.TAG,
                    "消息发送成功------"
                            + "\n" + "--消息id--" + mMsgId
                            + "\n" + "----msg----" + args.toString());
        }else {
            if (args.clone().length > 0){
                Log.i(ImSocketClient.TAG,
                        "消息发送成功------"
                                + "\n" + "--消息id--" + mMsgId
                                + "\n" + "----msg----" + args[0].toString());
            }
        }

    }

    private void startService(Context appContext, Intent intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            appContext.startForegroundService(intent);
////        } else {
////            appContext.startService(intent);
////        }
        appContext.startService(intent);
    }
}
