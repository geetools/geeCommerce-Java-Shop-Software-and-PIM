package com.geecommerce.coupon.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Model;

public class CouponCartHandler implements InvocationHandler {
    private Object cart;

    public CouponCartHandler(Object cart) {
        this.cart = cart;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHandle mh = Reflect.getMethodHandle((Class<Model>) cart.getClass(), method.getName());

        return mh == null ? null : mh.invoke(cart);
    }
}
