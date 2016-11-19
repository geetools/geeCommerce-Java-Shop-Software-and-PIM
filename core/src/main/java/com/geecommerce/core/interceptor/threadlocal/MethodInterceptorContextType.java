package com.geecommerce.core.interceptor.threadlocal;

/**
 * Keys for the ThreadLocal map {@link com.geecommerce.core.interceptor.threadlocal.MethodInterceptorContextThreadLocal}.
 * 
 * @see com.geecommerce.core.interceptor.threadlocal.MethodInterceptorContextThreadLocal
 * 
 * @author Michael Delamere
 */
public enum MethodInterceptorContextType {
    ORIGINAL_ARGS_VALUE, ORIGINAL_RETURN_VALUE, LAST_ARGS_VALUE, LAST_RETURN_VALUE;
}
