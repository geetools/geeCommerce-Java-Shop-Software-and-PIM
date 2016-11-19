package com.geecommerce.test.interceptor;

import com.geecommerce.core.interceptor.annotation.Interceptable;
import com.google.inject.Inject;

public class ClassToIntercept {
    private final TestObjectToInject testObj;

    /**
     * Test to make sure that MethodInterceptor is injecting objects via Guice.
     * 
     * @param testObj
     *            TestObjectToInject object to inject via Guice.
     * 
     */
    @Inject
    public ClassToIntercept(TestObjectToInject testObj) {
	this.testObj = testObj;
    }

    /**
     * Test to see the intercepting of mutable and immutable parameters. Immutable objects will not change, whereas changes to mutable objects by an
     * interceptor will be reflected in the objects passed to this method.
     * 
     * @param testMethodString
     *            Mutable object passed to this method.
     * @param testMethodObject
     *            Immutable object passed to this method.
     * @return testString Returns immutable object.
     */
    @Interceptable
    public String methodToIntercept(String testMethodString, TestMethodObject testMethodObject) {
	if (testObj == null) {
	    throw new IllegalStateException("Injected TestObjectToInject is null.");
	}

	this.testObj.setTestString(testMethodString);

	return "original_return_string";
    }

    /**
     * Tests the interceptor-manipulation of a mutable object that is returned by this method. Immutable objects such as String or Integer cannot be
     * changed by the interceptor.
     * 
     * @return TestMethodObject immutable object
     */
    @Interceptable
    public TestMethodObject methodToInterceptResult() {
	return new TestMethodObject(0, "original_test_return_object");
    }

    /**
     * Tests intercepting an exception by a method interceptor.
     */
    @Interceptable
    public void methodToInterceptException() {
	throw new TestRuntimeException();
    }

    public TestObjectToInject getTestObj() {
	return testObj;
    }
}
