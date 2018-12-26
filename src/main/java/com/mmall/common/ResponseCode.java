package com.mmall.common;

/**
 * 响应状态码枚举类
 */
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    //私有属性
    private final int code;
    private final String desc;

    //构造器
    ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
    //开放属性
    public int getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }
}
