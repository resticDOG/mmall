package com.mmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间转换工具类
 * 使用joda-time
 */
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间字符串转换成给定格式的时间
     * @param 日期字符串
     * @param 需要转换成的格式
     * @return 返回时间对象
     */
    public static Date strToDate(String dateTimeStr, String format){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 日期对象转换成指定格式的时间字符串
     * @param 日期对象
     * @param 需要转换的对象
     * @return 时间字符串
     */
    public static String dateToStr(Date date, String format){
        if (null == date){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    /**
     * 没有给定指定格式的时候按照此类自定义的标准格式"yyyy-MM-dd HH:mm:ss"转换
     */
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 没有给定指定格式的时候按照此类自定义的标准格式"yyyy-MM-dd HH:mm:ss"转换
     */
    public static String dateToStr(Date date){
        if (null == date){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
