package com.geecommerce.core.utils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Annotations {
    private static final Map<Long, Annotation> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A declaredAnnotation(Class<?> annotatedClass, Class<A> annotationClass) {
	if (annotatedClass == null || annotationClass == null)
	    throw new NullPointerException();

	long cacheKey = new StringBuilder(annotatedClass.getName()).append(annotationClass.getName()).toString().hashCode();

	Annotation foundAnnotation = cache.get(cacheKey);

	if (foundAnnotation == null) {
	    Annotation[] declaredAnnotations = annotatedClass.getDeclaredAnnotations();
	    for (Annotation declaredAnnotation : declaredAnnotations) {
		if (annotationClass.equals(declaredAnnotation.annotationType())) {
		    foundAnnotation = declaredAnnotation;
		    break;
		}
	    }

	    Annotation cachedFoundAnnotation = cache.putIfAbsent(cacheKey, foundAnnotation);

	    if (cachedFoundAnnotation != null)
		foundAnnotation = cachedFoundAnnotation;
	}

	return (A) foundAnnotation;
    }
}
