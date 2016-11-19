package com.geecommerce.core.interceptor.threadlocal;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

/**
 * This ThreadLocal map enables objects to be passed from one interceptor to the next when the same method is being intercepted more than once.
 * 
 * @see com.geecommerce.core.interceptor.threadlocal.MethodInterceptorContextType
 * @see com.geecommerce.core.interceptor.AspectMethodInterceptor
 * 
 * @author Michael Delamere
 */
public class MethodInterceptorContext {
    private static ThreadLocal<Map<String, Object>> METHOD_INTERCEPTOR_CTX = new ThreadLocal<Map<String, Object>>() {
	protected Map<String, Object> initialValue() {
	    return new HashMap<String, Object>();
	}
    };

    private MethodInterceptorContext() {
    }

    public static void put(MethodInterceptorContextType type, MethodInvocation invokation, Object value) {
	if (type == null || invokation == null)
	    throw new IllegalArgumentException("Parameters cannot be null.");

	METHOD_INTERCEPTOR_CTX.get().put(toKey(type, invokation), value);
    }

    public static Object get(MethodInterceptorContextType type, MethodInvocation invokation) {
	if (type == null || invokation == null)
	    throw new IllegalArgumentException("Parameters cannot be null.");

	return METHOD_INTERCEPTOR_CTX.get().get(toKey(type, invokation));
    }

    public static void cleanupThread() {
	METHOD_INTERCEPTOR_CTX.get().clear();
	METHOD_INTERCEPTOR_CTX.remove();
    }

    public static void dump() {
	Map<String, Object> ctxMap = METHOD_INTERCEPTOR_CTX.get();

	if (ctxMap != null && ctxMap.size() > 0) {
	    for (String key : ctxMap.keySet()) {
		Object value = ctxMap.get(key);

		System.out.println(key + ": " + value);
	    }
	}
    }

    private static String toKey(MethodInterceptorContextType type, MethodInvocation invokation) {
	StringBuffer key = new StringBuffer();
	key.append(type.name()).append(": ").append(invokation.getMethod().getName());

	return key.toString();
    }
}
