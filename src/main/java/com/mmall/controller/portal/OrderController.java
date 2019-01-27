package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * 订单控制器
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    //日志对象
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * 支付接口
     * @param session
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, HttpServletRequest request, Long orderNo){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return this.iOrderService.pay(user.getId(), orderNo, path);
    }

    /**
     * 订单创建
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return this.iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return this.iOrderService.cancel(user.getId(), orderNo);
    }

    /**
     * 获取购物车中已经选中的商品详情
     */
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return this.iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 获取订单详情
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return this.iOrderService.detail(user.getId(), orderNo);
    }

    /**
     *  获取订单列表
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return this.iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }


    /**
     * 验证支付宝回调
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String, String> params = Maps.newHashMap();
        //拿到支付宝回调的参数
        Map<String, String[]> requestParams = request.getParameterMap();
        //for循环遍历Map,用keySet方式
           //声明需要组装的字符串
        for (Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext(); ){
            String name = (String) iterator.next();
            String[] values = requestParams.get(name);
            //遍历数组
            String valueStr = "";
            for (int i = 0; i < values.length; i++){
                //逗号隔开参数，最后一个元素后不加逗号
                valueStr =  (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //把组装好的字符串放入map中
            params.put(name, valueStr);
        }
        //日志记录
        logger.info("支付宝回调：sign:{}, trade_status:{}, 参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
        //验证签名，验证回调的正确性
        //根据sdk源码和官方文档说明，需要先移除参数，sign_type
        params.remove("sign_type");
        try {
            boolean checkResult = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!checkResult){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调验证异常", e);
        }
        //todo 验证其他数据
        ServerResponse response = this.iOrderService.alipayCallback(params);
        if (response.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 前台轮询查询订单是否支付成功的方法
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        //判断登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = this.iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (response.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        //不需要报错，只是查询订单是否付款成功
        return ServerResponse.createBySuccess(false);
    }
}
