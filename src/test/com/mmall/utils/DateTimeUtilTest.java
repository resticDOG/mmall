package com.mmall.utils;

import org.junit.Test;

import java.util.Date;

public class DateTimeUtilTest {

    @Test
    public void strToDate() {
        Date date = DateTimeUtil.strToDate("2018-12-12 12:12:12", "yyyy-MM-dd HH:mm:ss");
        System.out.println(date);
    }

    @Test
    public void dateToStr() {
        System.out.println(DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    public void testFileName(){
        String fileName = "1235.ad";
        System.out.println(fileName.substring(fileName.lastIndexOf(".") + 1));
    }
}