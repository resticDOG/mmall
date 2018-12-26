package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    //注入dao
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 把保存和更新接口结合在一起
     */
    @Override
    public ServerResponse saveOrUpdate(Product product) {
        if (null == product){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        if (StringUtils.isNotBlank(product.getSubImages())){
            //设置主图，取第一张子图作为主图
            String[] subImgArray = product.getSubImages().split(",");
            product.setMainImage(subImgArray[0]);
        }
        //判断需要更新还是插入
        if (product.getId() != null){
            //更新操作
            int rowCount = this.productMapper.updateByPrimaryKey(product);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("更新商品成功");
            }
            return ServerResponse.createByErrorMessage("更新商品失败");
        }
        //保存操作
        int rowCount = this.productMapper.insert(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("保存商品成功");
        }
        return ServerResponse.createByErrorMessage("保存商品失败");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (null == productId || null == status){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = this.productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("变更商品状态成功");
        }
        return ServerResponse.createByErrorMessage("变更商品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (null == productId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }
        //开始查找产品
        Product product = this.productMapper.selectByPrimaryKey(productId);
        //转换成vo -> view object
        if (null == product){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        ProductDetailVo productDetailVo = this.assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        /*使用pageHelper的步骤：
        1.startPage;
        2.填充自己的sql逻辑；
        3.pageHelper收尾*/
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        //显示在前端的数据封装
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = this.assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //将list填充到构造器中
        PageInfo pageResult = new PageInfo(productList);
        //重置List
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(Integer pageNum, Integer pageSize, String productName, Integer productId) {
        //开始分页
        PageHelper.startPage(pageNum,pageSize);
        //填充分页逻辑
        StringBuilder builder = new StringBuilder();
        //拼接sql的通配符
        productName = builder.append("%").append(productName).append("%").toString();
        List<Product> productList = this.productMapper.selectByIdAndName(productId, productName);
        //显示在前端的数据封装
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = this.assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //将list填充到构造器中
        PageInfo pageResult = new PageInfo(productList);
        //重置List
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (null == productId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }
        //开始查找产品
        Product product = this.productMapper.selectByPrimaryKey(productId);
        //转换成vo -> view object
        if (null == product){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架");
        }
        ProductDetailVo productDetailVo = this.assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();
        if (categoryId != null){
            Category category = this.categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类，并且没有该关键字，此时需要返回一个空结果集，不需要报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //如果查出就将其子类的id全查出一起放入list中
            categoryIdList = this.iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        //开始分页
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        //需要做简单空判断
        List<Product> productList = this.productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword,
                                                                                    categoryIdList.size() == 0 ? null : categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        ProductListVo productListVo = null;
        for (Product product : productList) {
            productListVo = this.assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 转换ListVo
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        productListVo.setPrice(product.getPrice());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        return productListVo;
    }

    /**
     * pojo转换成vo的方法
     * @param 需要转换的商品对象
     * @return view object
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setPrice(product.getPrice());

        //设置ftp服务器地址
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        //设置父分类id
        Category category = this.categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (null == category){
            //如果查不到父分类id，则将其父分类id设置为0，即设为根节点
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        /*createTime和updateTime通过MyBatis取出的时候是一个毫秒数，不利于展示，因此需要将其转换成时间*/
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }
}
