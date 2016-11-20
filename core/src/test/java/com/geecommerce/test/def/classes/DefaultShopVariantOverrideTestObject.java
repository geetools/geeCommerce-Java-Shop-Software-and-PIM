package com.geecommerce.test.def.classes;

import com.geecommerce.test.core.reflect.CustomClassTestInterface;

public class DefaultShopVariantOverrideTestObject implements CustomClassTestInterface {
    @Override
    public String test() {
        return "default";
    }

    @Override
    public String parentTest() {
        return "parent-default";
    }
}
