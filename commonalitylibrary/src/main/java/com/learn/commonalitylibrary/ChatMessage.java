package com.learn.commonalitylibrary;

/**
 * author : fengzhangwei
 * date : 2019/12/19
 */
public class ChatMessage {


    // 消息类型
    public static int MSG_BODY_TYPE_TEXT = 1;   // 文本
    public static int MSG_BODY_TYPE_IMAGE = 2;  // 图片
    public static int MSG_BODY_TYPE_VOICE = 3;  // 语音
    public static int MSG_BODY_TYPE_VOIDE = 4;  // 视频
    public static int MSG_BODY_TYPE_LOCATION = 5;//位置
    public static int MSG_BODY_TYPE_EMOJI = 6;  // 表情
    public static int MSG_BODY_TYPE_RED_ENVELOPE = 7;//红包
    public static int MSG_BODY_TYPE_RED_ENVELOPE_HINT = 8; // 红包领取提示

    // 消息状态
    public static int MSG_SEND_LOADING = 1;  // 正在发送
    public static int MSG_SEND_SUCCESS = 2;  // 成功
    public static int MSG_SEND_ERROR = 3;    // 失败
    // 红包
    public static int STATUS_UNCLAIMED = 4; // 未领取
    public static int STATUS_ALREADY_RECEIVED = 5;  // 已领取
    public static int STATUS_OVERTIME = 6;   // 超时

    public static int MSG_SEND_CHAT = 1;   // 聊天
    public static int MSG_SEND_SYS = 2;    // 通知
    public static int MSG_OFFLINE = 3;    // 其他设备登录

    // 是否显示时间
    public static int MSG_TIME_FALSE = 0;
    public static int MSG_TIME_TRUE = 1;

    private String fromId;
    private String toId;
    private String pid;
    private int bodyType;
    private String body;
    private int msgStatus;
    private Long time;
    private int type = MSG_SEND_CHAT;
    private String conversation;
    private int displaytime = MSG_TIME_FALSE;

    public int getDisplaytime() {
        return displaytime;
    }

    public void setDisplaytime(int displaytime) {
        this.displaytime = displaytime;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getBodyType() {
        return bodyType;
    }

    public void setBodyType(int bodyType) {
        this.bodyType = bodyType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
