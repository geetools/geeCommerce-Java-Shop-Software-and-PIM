package com.geecommerce.test.controller;

import com.geecommerce.test.core.reflect.CustomClassTestInterface;

public class DefaultMerchantExtendsTestController implements CustomClassTestInterface {
    @Override
    public String test() {
	return "default";
    }

    @Override
    public String parentTest() {
	return "parent-default";
    }
}
