package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        //判断参数是否有效
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = this.cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null){
            //该产品不在该购物车中，需要新增一条记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setProductId(productId);
            cartItem.setChecked(Const.Cart.CHECKED);    //默认该商品是选中的
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else {
            //这个产品已存在购物车中，如果产品存在，则数量相加
            count += cart.getQuantity();
            cart.setQuantity(count);
            //更新数据库
            this.cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        //判断参数是否有效
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = this.cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null){
            cart.setQuantity(count);
        }
        //更新数据库
        this.cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        //使用guava提供的方法，将字符串分割放入集合中
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新数据库
        this.cartMapper.deleteByUserIdProductIds(userId, productIdList);
        //封装Vo
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        this.cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(this.cartMapper.selectCartProductCount(userId));
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = this.cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //初始化购物车总价
        BigDecimal cartTotalPrice = new BigDecimal("0");    //String构造器
        //声明需要用到的对象
        Product product = null;
        CartProductVo cartProductVo = null;
        Cart cartForQuantity = null;
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem: cartList){
                cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                product = this.productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //开始判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()){
                        //库存充足
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        //库存不足
                        buyLimitCount = product.getStock();     //顶多只能买最后剩的库存的量了
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        this.cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(
                                    BigDecimalUtil.mul(product.getPrice().doubleValue(),
                                    cartProductVo.getQuantity().doubleValue())
                    );
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if (cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果商品是勾选状态，将其总价加入购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * 判断购物车中商品是否全勾选
     * @param userId
     * @return
     */
    private Boolean getAllCheckedStatus(Integer userId) {
        if (userId == null){
            return false;
        }
        return this.cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

}
