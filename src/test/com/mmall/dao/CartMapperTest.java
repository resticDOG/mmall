package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class CartMapperTest {
    @Autowired
    private CartMapper cartMapper;

    @Test
    public void testSelectByUserIdAndProductId(){
        Cart cart = this.cartMapper.selectByUserIdAndProductId(1, 26);
        System.out.println(cart);
    }

}
