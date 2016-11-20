package com.geecommerce.coupon.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Model;

public class CouponCartItemHandler implements InvocationHandler {
    private Object cartItem;

    public CouponCartItemHandler(Object cartItem) {
        this.cartItem = cartItem;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHandle mh = Reflect.getMethodHandle((Class<Model>) cartItem.getClass(), method.getName());

        return mh == null ? null : mh.invoke(cartItem);
    }
}
