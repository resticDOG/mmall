package com.mmall.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class BigDecimalUtilTest {

    @Test
    public void add() {
        System.out.println("1.2 + 0.3 = " + BigDecimalUtil.add(1.2, 0.3));
    }

    @Test
    public void sub() {
        System.out.println("1.2 - 0.3 = " + BigDecimalUtil.sub(1.2, 0.3));
    }

    @Test
    public void mul() {
        System.out.println("1.2 * 0.3 = " + BigDecimalUtil.mul(1.2, 0.3));
    }

    @Test
    public void div() {
        System.out.println("1.0 / 0.3 = " + BigDecimalUtil.div(1.0, 0.3));
    }
}