package com.learn.commonalitylibrary.util;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class LoggerUtil {

    public  LoggerUtil setTAG(String tag){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(1)         // (Optional) How many method line to show. Default 2
                .tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        return this;
    }

    public LoggerUtil  msg(String msg){
        Logger.i(msg);
        return this;
    }

    public LoggerUtil  json(String json){
        Logger.json(json);
        return this;
    }

    public LoggerUtil xml(String xml){
        Logger.xml(xml);
        return this;
    }
}
