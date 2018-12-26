package com.mmall.utils;

import java.math.BigDecimal;

/**
 * java在浮点型进行数学运算的时候会丢失精度，需要使用BigDecimal类的String构造器解决精度丢失问题
 */
public class BigDecimalUtil {
    /**
     * 构造器私有
     */
    private BigDecimalUtil(){

    }

    /**
     * 加法
     */
    public static BigDecimal add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    /**
     * 减法
     * @param v1减数
     * @param v2被减数
     * @return 返回其差值
     */
    public static BigDecimal sub(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    /**
     * 乘法
     */
    public static BigDecimal mul(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    /**
     * 除法
     * @param v1除数
     * @param v2被除数
     * @return 返回值四舍五入保留2位小数
     */
    public static BigDecimal div(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_DOWN);
    }
}
