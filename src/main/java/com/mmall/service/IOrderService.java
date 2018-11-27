package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallback(Map<String,String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse detail(Integer userId, Long orderNo);

    ServerResponse getOrderList(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse manageOrderList(Integer pageNum, Integer pageSize);

    ServerResponse manageOrderDetail(Long orderNo);

    ServerResponse manageOrderSearch(Long orderNo, Integer pageNum, Integer pageSize);

    ServerResponse manageSendGoods(Long orderNo);
}
