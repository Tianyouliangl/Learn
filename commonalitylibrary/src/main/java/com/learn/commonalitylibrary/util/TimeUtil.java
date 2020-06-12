package com.learn.commonalitylibrary.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * author : fengzhangwei
 * date : 2019/12/27
 */
public class TimeUtil {
    /**
     * 一些时间格式
     */
    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_MONTH_DAY_TIME = "MM-dd HH:mm";
    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_YEAR = "yyyy";
    public final static String FORMAT_VOICE_TIME = "mm:ss";

    public static String getFormatToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(currentTime);
    }

    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    public static String getTimeString(Long timestamp) {
        String result = "";
        String[] weekNames = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String hourTimeFormat = "HH:mm";
        String monthTimeFormat = "M月d日 HH:mm";
        String yearTimeFormat = "yyyy年M月d日 HH:mm";
        try {
            Calendar todayCalendar = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {//当年
                if (todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {//当月
                    int temp = todayCalendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
                    switch (temp) {
                        case 0://今天
                            result = getTime(timestamp, hourTimeFormat);
                            break;
                        case 1://昨天
                            result = "昨天 " + getTime(timestamp, hourTimeFormat);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            result = weekNames[dayOfWeek - 1] + " " + getTime(timestamp, hourTimeFormat);
                            break;
                        default:
                            result = getTime(timestamp, monthTimeFormat);
                            break;
                    }
                } else {
                    result = getTime(timestamp, monthTimeFormat);
                }
            } else {
                result = getTime(timestamp, yearTimeFormat);
            }
            return result;
        } catch (Exception e) {
            Log.e("getTimeString", e.getMessage());
            return "";
        }
    }


    /**
     * 判断是否在五分钟之内
     * @param startTime
     * @param endTime
     * @return
     */
    public static Boolean getTimeExpend(Long startTime, Long endTime){
        //传入字串类型 2016/06/28 08:30
        long longExpend = startTime - endTime;  //获取时间差

        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
        int longMinutes = (int) ((longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000));   //根据时间差来计算分钟数
        if (longMinutes >= 5){
            return false;
        }
        return true;
    }

    public static String getTime(long time, String pattern) {
        Date date = new Date(time);
        return dateFormat(date, pattern);
    }

    public static String dateFormat(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
