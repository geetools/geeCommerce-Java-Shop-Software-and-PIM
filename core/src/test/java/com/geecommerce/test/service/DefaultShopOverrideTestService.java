package com.geecommerce.test.service;

import com.geecommerce.test.core.reflect.CustomClassTestInterface;

public class DefaultShopOverrideTestService implements CustomClassTestInterface {
    @Override
    public String test() {
	return "default";
    }

    @Override
    public String parentTest() {
	return "parent-default";
    }

}
