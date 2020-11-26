package com.learn.commonalitylibrary;

import android.content.Context;

import com.white.easysp.EasySP;

import java.util.ArrayList;

public class Constant {
    public static String BASE_GROUP_URL = "http://111.229.231.206:8082/"; //172.16.201.143  //111.229.231.206   192.168.2.169
    public static String BASE_CHAT_URL =   "http://111.229.231.206:9092/";   // im连接地址
    public static String SPKey_UID = "user_uid";
    private static ArrayList<String> list;

    public static String getImageUrl(){
        if (list == null){
            list = new ArrayList<>();
            list.add("https://ss0.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2611652800,2596506430&fm=26&gp=0.jpg");
            list.add("https://b-ssl.duitang.com/uploads/item/201707/06/20170706164810_kiCre.jpeg");
            list.add("https://b-ssl.duitang.com/uploads/item/201807/06/20180706112250_3iBxt.thumb.700_0.jpeg");
            list.add("https://b-ssl.duitang.com/uploads/item/201807/06/20180706112251_niE3Y.thumb.700_0.jpeg");
            list.add("https://b-ssl.duitang.com/uploads/item/201804/29/20180429111927_4i2Ks.thumb.700_0.jpeg");
            list.add("https://b-ssl.duitang.com/uploads/item/201709/10/20170910110429_5J8jt.jpeg");
            list.add("https://b-ssl.duitang.com/uploads/item/201808/03/20180803090324_qrygh.thumb.700_0.jpeg");
        }
        return list.get((int)(0+Math.random()*(list.size()-1)));
    }

    public final static int BOOK_BG_DEFAULT = 0;
    public final static int BOOK_BG_1 = 1;
    public final static int BOOK_BG_2 = 2;
    public final static int BOOK_BG_3 = 3;
    public final static int BOOK_BG_4 = 4;

    public final static int PAGE_MODE_SIMULATION = 0;
    public final static int PAGE_MODE_COVER = 1;
    public final static int PAGE_MODE_SLIDE = 2;
    public final static int PAGE_MODE_NONE = 3;

    public static final int CLIP_SAVE_FLAG = 0x02;

    public enum Status {
        OPENING,
        FINISH,
        FAIL,
    }

    public static String SPKey_phone(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_phone";
    }

    public static String SPKey_pwd(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_pwd";
    }

    public static String SPKey_userName(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_name";
    }

    public static String SPKey_icon(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_icon";
    }

    public static String SPKey_info(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_info";
    }

    public static String SPKey_switch(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_switch";
    }

    public static String SPKey_token(Context context){
        return EasySP.init(context).getString(SPKey_UID) + "_user_token";
    }
}
