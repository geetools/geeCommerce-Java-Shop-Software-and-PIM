package com.geecommerce.core.interceptor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.geecommerce.core.Char;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.interceptor.annotation.Intercept;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.util.NullableConcurrentHashMap;
import com.google.inject.Injector;

public class GuiceMethodInterceptor implements MethodInterceptor {
    private static final List<LocatedMethodInterceptor> EMPTY_LIST = new ArrayList<>();

    private static final Map<String, Object> cache = new NullableConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        List<LocatedMethodInterceptor> foundInterceptorTypes = locateInterceptorTypes(invocation);

        Injector injector = ModuleInjector.get();

        Object[] args = invocation.getArguments();

        boolean exceptionThrown = false;

        // ------------------------------------------------------------------------------
        // ON BEFORE
        // ------------------------------------------------------------------------------

        for (LocatedMethodInterceptor interceptorType : foundInterceptorTypes) {
            AbstractMethodInterceptor interceptor = injector.getInstance(interceptorType.methodInterceptor);
            if (interceptor instanceof AbstractMethodInterceptor) {
                ((AbstractMethodInterceptor) interceptor).invocation = invocation;
                interceptor.onBefore(args);
            }
        }

        Object returnObject = null;

        try {
            returnObject = invocation.proceed();
        } catch (Throwable t) {
            exceptionThrown = true;

            // ------------------------------------------------------------------------------
            // ON AFTER THROWING
            // ------------------------------------------------------------------------------

            for (LocatedMethodInterceptor interceptorType : foundInterceptorTypes) {
                AbstractMethodInterceptor interceptor = injector.getInstance(interceptorType.methodInterceptor);
                if (interceptor instanceof AbstractMethodInterceptor) {
                    ((AbstractMethodInterceptor) interceptor).invocation = invocation;
                    interceptor.onAfterThrowing(t);
                }
            }
        }

        if (!exceptionThrown) {
            // ------------------------------------------------------------------------------
            // ON AFTER
            // ------------------------------------------------------------------------------

            for (LocatedMethodInterceptor interceptorType : foundInterceptorTypes) {
                AbstractMethodInterceptor interceptor = injector.getInstance(interceptorType.methodInterceptor);
                if (interceptor instanceof AbstractMethodInterceptor) {
                    ((AbstractMethodInterceptor) interceptor).invocation = invocation;
                    interceptor.onAfter(args);
                }
            }

            // ------------------------------------------------------------------------------
            // ON AFTER RETURN
            // ------------------------------------------------------------------------------

            if (!isVoidMethod(invocation)) {
                Object[] newResult = { returnObject };

                for (LocatedMethodInterceptor interceptorType : foundInterceptorTypes) {
                    AbstractMethodInterceptor interceptor = injector.getInstance(interceptorType.methodInterceptor);
                    if (interceptor instanceof AbstractMethodInterceptor) {
                        ((AbstractMethodInterceptor) interceptor).invocation = invocation;
                        interceptor.onAfterReturning(newResult);
                    }
                }
            }
        }

        return returnObject;
    }

    private boolean isVoidMethod(MethodInvocation invocation) {
        return "void".equals(invocation.getMethod().getReturnType().toString());
    }

    @SuppressWarnings("unchecked")
    private List<LocatedMethodInterceptor> locateInterceptorTypes(MethodInvocation invocation) {
        Class<?> invocationClass = invocation.getThis().getClass().getSuperclass();
        String invocationMethod = invocation.getMethod().getName();

        String key = new StringBuilder(invocationClass.getName()).append(Char.DOT)
            .append(invocation.getMethod().toString()).toString();

        // System.out.println("INTERCEPTOR KEY: " + key);

        List<LocatedMethodInterceptor> interceptorTypes = (List<LocatedMethodInterceptor>) cache.get(key);

        if (interceptorTypes == null) {
            interceptorTypes = new ArrayList<LocatedMethodInterceptor>();

            // Find classes annotated with the Intercept class
            Set<Class<?>> annotatedTypes = Reflect.getTypesAnnotatedWith(Intercept.class, false);

            // Iterate through all the @Intercept classes and find one (or more)
            // that matches the target joinPoint-type
            // and
            // target joinPoint-method.
            for (Class<?> annotatedType : annotatedTypes) {
                // Find @Intercept annotation
                Annotation declaredAnnotation = Reflect.getDeclaredAnnotation(annotatedType, Intercept.class);

                if (declaredAnnotation != null) {
                    // What do we want to intercept?
                    Class<?> classToIntercept = ((Intercept) declaredAnnotation).type();
                    String name = ((Intercept) declaredAnnotation).name();
                    String methodToIntercept = ((Intercept) declaredAnnotation).method();

                    // Class and method currently being invoked.
                    // Class<?> invocationClass =
                    // invocation.getThis().getClass().getSuperclass();
                    // String invocationMethod =
                    // invocation.getMethod().getName();

                    // All interfaces from the invocation class (also from
                    // sub-types).
                    Set<Class<?>> allInterfaces = Reflect.getInterfaces(invocationClass, true);

                    if ((!Object.class.equals(classToIntercept) && classToIntercept.equals(invocationClass)
                        && methodToIntercept.equals(invocationMethod))
                        || (Object.class.equals(classToIntercept) && !"".equals(name.trim())
                            && invocationClass.getName().endsWith(name.trim())
                            && methodToIntercept.equals(invocationMethod))
                        || (allInterfaces != null && allInterfaces.size() > 0
                            && Object.class.equals(classToIntercept) && !"".equals(name.trim())
                            && interfacesMatch(allInterfaces, name.trim())
                            && methodToIntercept.equals(invocationMethod))) {
                        interceptorTypes.add(new LocatedMethodInterceptor((Intercept) declaredAnnotation,
                            (Class<AbstractMethodInterceptor>) annotatedType));
                    }
                }
            }

            if (interceptorTypes != null && interceptorTypes.size() > 0) {
                // Sort method interceptors according to the specified order in
                // Intercept annotation.
                Collections.sort(interceptorTypes, new Comparator<LocatedMethodInterceptor>() {
                    @Override
                    public int compare(LocatedMethodInterceptor o1, LocatedMethodInterceptor o2) {
                        return (o1.interceptorAnnotation.order() < o2.interceptorAnnotation.order() ? -1
                            : (o1.interceptorAnnotation.order() > o2.interceptorAnnotation.order() ? 1 : 0));
                    }
                });
            }

            List<LocatedMethodInterceptor> cachedInterceptorTypes = (List<LocatedMethodInterceptor>) cache
                .putIfAbsent(key, interceptorTypes == null ? EMPTY_LIST : interceptorTypes);

            if (cachedInterceptorTypes != null)
                interceptorTypes = cachedInterceptorTypes;
        }

        return interceptorTypes;
    }

    private boolean interfacesMatch(Set<Class<?>> allInterfaces, String name) {
        for (Class<?> interf : allInterfaces) {
            if (interf.getName().endsWith(name))
                return true;
        }

        return false;
    }
}

final class LocatedMethodInterceptor {
    final Intercept interceptorAnnotation;
    final Class<AbstractMethodInterceptor> methodInterceptor;

    LocatedMethodInterceptor(Intercept interceptorAnnotation, Class<AbstractMethodInterceptor> methodInterceptor) {
        this.interceptorAnnotation = interceptorAnnotation;
        this.methodInterceptor = methodInterceptor;
    }
}
