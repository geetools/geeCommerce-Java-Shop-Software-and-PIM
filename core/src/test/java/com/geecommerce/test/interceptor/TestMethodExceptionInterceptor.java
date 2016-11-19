package com.geecommerce.test.interceptor;

import com.geecommerce.core.interceptor.AbstractMethodInterceptor;
import com.geecommerce.core.interceptor.annotation.Intercept;

@Intercept(type = ClassToIntercept.class, method = "methodToInterceptException")
public class TestMethodExceptionInterceptor extends AbstractMethodInterceptor {
    @Override
    public void onAfterThrowing(Throwable e) {
	if (e instanceof TestRuntimeException) {
	    throw new TestAnotherRuntimeException();
	}
    }
}
