package com.geecommerce.core.security;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ProfilerInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
	long start = System.currentTimeMillis();

	Object o = invocation.proceed();

	long timeTaken = System.currentTimeMillis() - start;

	if (timeTaken > 0) {
	    System.out.println("---------------------------------------------------------------------------------");
	    System.out.println("Method: " + invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName() + "() took " + timeTaken + "ms");
	    System.out.println("Args: " + Arrays.asList(invocation.getArguments()));
	}

	return o;
    }
}
