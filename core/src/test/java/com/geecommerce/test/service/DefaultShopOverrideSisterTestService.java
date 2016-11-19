package com.geecommerce.test.service;

import com.geecommerce.test.core.reflect.CustomClassTestInterface;
import com.geecommerce.test.core.reflect.SisterCustomClassTestInterface;

public class DefaultShopOverrideSisterTestService implements CustomClassTestInterface, SisterCustomClassTestInterface {
    @Override
    public String test() {
	return "default";
    }

    @Override
    public String parentTest() {
	return "parent-default";
    }

    @Override
    public String sisterTest() {
	return "sister-default";
    }
}
