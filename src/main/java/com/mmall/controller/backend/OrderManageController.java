package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台订单管理类
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    /**
     *  获取订单列表
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageNum", defaultValue = "10") Integer pageSize){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            return this.iOrderService.manageOrderList(pageNum, pageSize);
        }
        return response;
    }


    /**
     *  获取商品详情
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            return this.iOrderService.manageOrderDetail(orderNo);
        }
        return response;
    }

    /**
     *  根据订单号精确匹配搜索
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse search(HttpSession session,
                                 Long orderNo,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageNum", defaultValue = "10") Integer pageSize){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            return this.iOrderService.manageOrderSearch(orderNo, pageNum, pageSize);
        }
        return response;
    }

    /**
     *  发货固定订单号
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session, Long orderNo){
        //判断登录和管理员权限
        ServerResponse response = this.iUserService.checkLoginAndAdmin(session);
        if (response.isSuccess()){
            return this.iOrderService.manageSendGoods(orderNo);
        }
        return response;
    }
}
