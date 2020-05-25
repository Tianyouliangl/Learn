package com.learn.commonalitylibrary.util;

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

    public static String getChatMessageText(String msg,String fromId,String toId,int bodyType,int displaytime){
        JSONObject object = new JSONObject();
        try {
            object.put("fromId",fromId);
            object.put("toId",toId);
            object.put("pid",getPid());
            object.put("bodyType",bodyType);
            object.put("body", GsonUtil.BeanToJson(new TextBody(msg)));
            object.put("msgStatus", ChatMessage.MSG_SEND_LOADING);
            object.put("time",System.currentTimeMillis());
            object.put("type", ChatMessage.MSG_SEND_CHAT);
            object.put("conversation", OfTenUtils.getConviction(fromId,toId));
            object.put("displaytime",displaytime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
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
}
