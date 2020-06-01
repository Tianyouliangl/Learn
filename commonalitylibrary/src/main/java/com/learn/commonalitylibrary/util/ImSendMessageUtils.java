package com.learn.commonalitylibrary.util;

import android.text.TextUtils;

import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.RedEnvelopeBody;
import com.learn.commonalitylibrary.body.TextBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * author : fengzhangwei
 * date : 2019/12/19
 */
public class ImSendMessageUtils {

    /**
     * 随机产生一个4个字节的int
     */
    public static int getRandomInt() {
        int min = 10;
        int max = 99;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }


    public static String getRandomString() {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < 2; i++) {
            int number = random.nextInt(62);// 0~61
            sf.append(str.charAt(number));

        }
        return sf.toString();
    }

    public static String getPid(){
        StringBuilder stringBuffer = new StringBuilder();
        String time = String.valueOf(System.currentTimeMillis());
        stringBuffer.append("AN")
                .append(getRandomInt())
                .append(getRandomString())
                .append(getRandomInt())
                .append(getRandomString())
                .append(getRandomInt())
                .append(time.substring(time.length() - 4, time.length()));
        return stringBuffer.toString();
    }

    public static String getChatMessageText(String body, String fromId, String toId, String conversation, int displaytime){
        JSONObject object = new JSONObject();
        try {
            object.put("fromId",fromId);
            object.put("toId",toId);
            object.put("pid",getPid());
            object.put("bodyType",ChatMessage.MSG_BODY_TYPE_TEXT);
            object.put("body",body);
            object.put("msgStatus", ChatMessage.MSG_SEND_LOADING);
            object.put("time",System.currentTimeMillis());
            object.put("type", ChatMessage.MSG_SEND_CHAT);
            object.put("conversation",conversation);
            object.put("displaytime",displaytime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getTextBody(String text){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getChatMessageEmoji(String msg,String fromId,String toId,int bodyType,int displaytime){
        JSONObject object = new JSONObject();
        try {
            object.put("fromId",fromId);
            object.put("toId",toId);
            object.put("pid",getPid());
            object.put("bodyType",bodyType);
            object.put("body",GsonUtil.BeanToJson(new ImageBody(msg)));
            object.put("msgStatus",ChatMessage.MSG_SEND_LOADING);
            object.put("time",System.currentTimeMillis());
            object.put("type", ChatMessage.MSG_SEND_CHAT);
            object.put("conversation", OfTenUtils.getConviction(fromId,toId));
            object.put("displaytime",displaytime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getChatMessageRedEnvelope(String money,String fromId,String toId,int bodyType,int displaytime){
        JSONObject object = new JSONObject();
        try {
            object.put("fromId",fromId);
            object.put("toId",toId);
            object.put("pid",getPid());
            object.put("bodyType",bodyType);
            object.put("body",GsonUtil.BeanToJson(new RedEnvelopeBody(money)));
            object.put("msgStatus",ChatMessage.MSG_SEND_LOADING);
            object.put("time",System.currentTimeMillis());
            object.put("type", ChatMessage.MSG_SEND_CHAT);
            object.put("conversation", OfTenUtils.getConviction(fromId,toId));
            object.put("displaytime",displaytime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getChatBodyType(ChatMessage message){
        int bodyType = message.getBodyType();
        int type = message.getType();
        String body = message.getBody();
        if (type == ChatMessage.MSG_SEND_CHAT){
            if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT){
                TextBody content = GsonUtil.GsonToBean(body, TextBody.class);
                return content.getMsg();
            }
            if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE){
                return "[图片]";
            }
            if (bodyType == ChatMessage.MSG_BODY_TYPE_VOICE){
                return "[语音]";
            }
            if (bodyType == ChatMessage.MSG_BODY_TYPE_VOIDE){
                return "[视频]";
            }
            if (bodyType == ChatMessage.MSG_BODY_TYPE_LOCATION){
                return "[位置]";
            }
            if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI){
                return "[表情]";
            }
            if (bodyType == ChatMessage.MSG_BODY_TYPE_RED_ENVELOPE){
                return "[红包]";
            }
        }else {
            return body;
        }
        return "未知消息类型";
    }

    public static String getMessageType(ChatMessage message){
        int type = message.getType();
        if (type == ChatMessage.MSG_SEND_CHAT){
            return "新消息";
        }
        if (type == ChatMessage.MSG_SEND_SYS){
            return "系统消息";
        }
        return "未知消息类型";
    }
}
