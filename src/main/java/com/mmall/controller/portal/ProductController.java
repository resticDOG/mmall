package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 前台商品控制器
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 用户端获取商品详情的方法
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(Integer productId){
        //不需要登录也能获取商品详情
        return this.iProductService.getProductDetail(productId);
    }

    /**
     * 用户端商品搜索
     * @param keyword 关键字
     * @param categoryId 查询的分页id
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @param orderBy 排序规则
     * @return返回商品的分页数据
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(@RequestParam(value = "keyword",required = false)String keyword,
                                            @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                            @RequestParam(value = "orderBy",defaultValue = "")String orderBy){
        return this.iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }


}
