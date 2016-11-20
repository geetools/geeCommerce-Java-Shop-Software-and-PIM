package com.geecommerce.test.def.classes;

import com.geecommerce.test.core.reflect.CustomClassTestInterface;

public class DefaultPetStoreOverrideTestObject implements CustomClassTestInterface {
    @Override
    public String parentTest() {
        return "default-parent-pet";
    }

    @Override
    public String test() {
        return "default-pet";
    }
}
