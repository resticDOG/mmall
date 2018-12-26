package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 通用常量类
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";    //当前用户
    public static final String USERNAME = "username";           //用户名
    public static final String EMAIL = "email";                 //邮箱

    /**
     * 用户角色接口类
     */
    public interface Role{
        int ROLE_CUSTOMER = 0;  //普通用户
        int ROLE_ADMIN = 1;     //管理员
    }

    public interface Cart{
        int CHECKED = 1;        //即购物车的选中状态
        int UN_CHECKED = 0;
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";           //给前端的提示信息，限制失败，即库存不足的时候
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";     //给前端的提示信息，限制成功，即库存充足的时候
    }

    /**
     * 排序接口类
     */
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    /**
     * 商品销售状态枚举类
     */
    public enum ProductStatusEnum{
        ON_SALE(1, "在售");

        private String value;
        private int code;
        ProductStatusEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 订单状态枚举类
     */
    public enum OrderStatusEnum{
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAYD(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSED(60, "订单关闭");

        int code;
        String value;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        /**
         * 通过code获取value的方法
         */
        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
                if (orderStatusEnum.code == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    /**
     * 回调状态接口常量类
     */
    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        //返回支付宝的字符串
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    /**
     * 支付平台枚举类
     */
    public enum PayPlatform{
        ALIPAY(1, "支付宝");

        int code;
        String value;

        PayPlatform(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 支付方式枚举类
     */
    public enum PaymentTypeEnum{
        ONLINE_PAY(1, "在线支付");

        int code;
        String value;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        /**
         * 通过code获取value的方法
         */
        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
                if (paymentTypeEnum.code == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }

}
