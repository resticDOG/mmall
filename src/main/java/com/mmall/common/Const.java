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

}
