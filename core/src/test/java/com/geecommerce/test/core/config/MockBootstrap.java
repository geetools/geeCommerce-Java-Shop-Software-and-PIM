package com.geecommerce.test.core.config;

import com.geecommerce.core.bootstrap.AbstractBootstrap;
import com.geecommerce.core.bootstrap.annotation.Bootstrap;

@Bootstrap
public class MockBootstrap extends AbstractBootstrap {
    public static final String TEST_BOOTSTRAP_KEY = "test.bootstrap.key";
    public static final String TEST_BOOTSTRAP_VALUE = "test.bootstrap.value";

    @Override
    public void init() {
	System.setProperty(TEST_BOOTSTRAP_KEY, TEST_BOOTSTRAP_VALUE);
    }
}
