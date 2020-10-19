package com.lib.xiangxiang.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;


import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.VoiceBody;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.NetState;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * author : fengzhangwei
 * date : 2019/12/19
 */
public class ImSocketClient {

    private static String SocketEvent = "chat";
    private static Socket mSocket;
    private static String mToken;
    private static String mSocketUrl = Constant.BASE_CHAT_URL;
    public static String TAG = "socket";
    private static Boolean isConnect = false;
    private static Emitter.Listener mDisConnectListener;  // 断开连接监听
    private static Emitter.Listener mConnectListener;     // 连接监听 / 成功
    private static Emitter.Listener mConnectErrorListener;// 连接错误
    private static Emitter.Listener mChatListener;        // 消息监听
    private static Context mContext;
    private static NetBroadcastReceiver netBroadcastReceiver;
//    public static Map<String, String> msgMap = new HashMap<>();

    static class NetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "NetBroadcastReceiver changed");
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Boolean netWorkState = NetState.hasNetWorkConnection(context);
                Log.i(TAG, "---当前是否有网络---NetBroadcastReceiver:::::::" + netWorkState);
                if (netWorkState) {
                    if (!checkSocket()) {
                        openSocket();
                    }
                }
            }
        }
    }

    /**
     * 判断Socket是否可以复用
     *
     * @param token
     * @param context
     * @return
     */
    public static void initSocket(String token, Context context) {
        if (mSocket != null) {
            if (mToken != token) {
                newSocket(context, token);
            } else {
                Log.i(TAG, "-----复用Socket----");
                openSocket();
            }
        } else {
            newSocket(context, token);
        }
    }

    /**
     * 断开Socket
     *
     * @return
     */
    public static void release() {
        if (mSocket != null) {
            closeSocket();
            removeSocketListener();
            mSocket = null;
            Log.i(TAG, "Socket --- 已释放.");
        }
    }

    /**
     * 清空监听
     */
    private static void removeSocketListener() {
        if (mSocket != null) {
            mSocket.off(SocketEvent, mChatListener);
            mSocket.off(Socket.EVENT_CONNECT, mConnectListener);
            mSocket.off(Socket.EVENT_DISCONNECT, mDisConnectListener);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, mConnectErrorListener);
        }
    }

    /**
     * 关闭Socket
     */
    private static void closeSocket() {
        if (mSocket != null) {
            mSocket.disconnect();
            mToken = null;
            Log.i(TAG, "Socket --- 链接已关闭.");
        }
    }

    /**
     * 发送消息
     *
     * @return
     */
    public static void sendMsg(Context context, String msg, String msgId) {
//        msgMap.put(msgId, msg);
        sendMsgSocket(context, msg, msgId);
    }

    /**
     * Socket 发送消息
     *
     * @param context
     * @param msg
     * @param msgId
     */
    private static void sendMsgSocket(Context context, String msg, String msgId) {
        if (checkSocket()) {
            if (mSocket != null) {
                Log.i(TAG, "Socket send msg -------- " + msg);
                mSocket.emit(SocketEvent, msg, new SocketSendMsgCallBackAck(context, msgId));
            }
        } else {
            Log.i(TAG, "Socket Connect Error");
            sendMsgError(context, msg, msgId);
            initSocket(mToken, mContext);
        }
    }

    private static void sendMsgError(Context context, String msg, String msgId) {
        ChatMessage message = GsonUtil.GsonToBean(msg, ChatMessage.class);
        message.setMsgStatus(ChatMessage.MSG_SEND_ERROR);
        Intent intent = new Intent(mContext, ImService.class);
        intent.putExtra(ImService.SOCKET_CMD, ImService.SOCKET_SEND_MSG_CALLBACK);
        intent.putExtra(ImService.SOCKET_PID, msgId);
        intent.putExtra(ImService.SOCKET_DATA, GsonUtil.BeanToJson(message));
        context.startService(intent);
    }

    /**
     * 创建Socket
     *
     * @param context
     * @param token
     */
    private static void newSocket(Context context, String token) {
        mContext = context;
        mToken = token;
        IO.Options options = new IO.Options();
        options.forceNew = false;
        options.reconnection = true;
        options.reconnectionDelay = 3000;
        options.reconnectionDelayMax = 5000;
        options.timeout = -1;
        options.query = token;
        if (!mSocketUrl.isEmpty()) {
            try {
                mSocket = IO.socket(mSocketUrl, options);
                Log.i(TAG, "socket 创建-----" + token);
                initSocketListener(context);
                openSocket();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "socket 创建失败-----" + e.toString());
            }
        } else {
            Log.i(TAG, "socket 创建失败-----url Null");
        }

    }

    private static void openSocket() {
        if (mSocket != null) {
            mSocket.connect();
        }
    }

    /**
     * 创建监听
     *
     * @param context
     */
    private static void initSocketListener(final Context context) {

        mChatListener = new SocketChatMsgListener(context);
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
        }
        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //注册广播接收
        context.getApplicationContext().registerReceiver(netBroadcastReceiver, filter);
        mDisConnectListener = new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                isConnect = false;
                Log.i(TAG, "socket ---断开连接---");
                if (mToken != null) {
                    EventBus.getDefault().postSticky("connect");
                    if (NetState.hasNetWorkConnection(mContext)) {
                        openSocket();
                    }
                }
            }
        };

        mConnectListener = new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                isConnect = true;
                EventBus.getDefault().postSticky("connect");
                Log.i(TAG, "socket ---连接成功---" + checkSocket());
//                for (Map.Entry<String, String> entry : msgMap.entrySet()) {
//                    sendMsgSocket(mContext, entry.getValue(), entry.getKey());
//                }
                List<SessionMessage> list = DataBaseHelp.getInstance(mContext).getSessionList();
                for (int i = 0; i < list.size(); i++) {
                    SessionMessage sessionMessage = list.get(i);
                    String conversation = sessionMessage.getConversation();
                    List<ChatMessage> messageList = DataBaseHelp.getInstance(mContext).getChatMessage(conversation, 1, 30);
                    for (int j = 0; j < messageList.size(); j++) {
                        ChatMessage message = messageList.get(j);
                        if (message.getMsgStatus() == ChatMessage.MSG_SEND_LOADING){
                           SocketManager.sendMsgSocket(mContext,GsonUtil.BeanToJson(message),null);
                        }
                    }
                }

            }
        };

        mConnectErrorListener = new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                isConnect = false;
                if (NetState.hasNetWorkConnection(mContext)) {
                    openSocket();
                }
                if (args.toString().isEmpty()) {
                    Log.i(TAG, "socket ---连接错误---" + args.toString());
                } else {
                    Log.i(TAG, "socket ---连接错误---" + args[0].toString());
                }
            }
        };
        addSocketListener();
    }

    /**
     * Socket添加监听
     */
    private static void addSocketListener() {
        if (mSocket != null) {
            mSocket.on(SocketEvent, mChatListener);
            mSocket.on(Socket.EVENT_CONNECT, mConnectListener);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, mConnectErrorListener);
            mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);
        }
    }

    /**
     * 判断Socket是否可用
     *
     * @return
     */
    public static Boolean checkSocket() {
        return mSocket != null && isConnect;
    }

}
