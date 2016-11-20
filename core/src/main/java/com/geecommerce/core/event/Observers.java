package com.geecommerce.core.event;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.cron.Environment;
import com.geecommerce.core.event.annotation.Observe;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.util.NullableConcurrentHashMap;

public class Observers {
    private static final String OBSERVERS_CACHE_KEY_PART = "@observers->";

    private static final List<Class<Observer>> EMPTY_LIST = new ArrayList<>();

    private static final Map<String, Object> cache = new NullableConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static final <T extends Observable> List<Class<Observer>> forClass(Class<T> modelClass, Event e) {
        if (!Environment.areObserversEnabled())
            return EMPTY_LIST;

        String key = new StringBuilder(modelClass.getName()).append(OBSERVERS_CACHE_KEY_PART).append(e.name())
            .toString();

        List<Class<Observer>> observerClasses = (List<Class<Observer>>) cache.get(key);

        if (observerClasses == null) {
            // This list will contain all the found Observers
            observerClasses = new ArrayList<Class<Observer>>();

            List<Class<Observer>> cachedObserverClasses = (List<Class<Observer>>) cache.putIfAbsent(key,
                observerClasses);

            if (cachedObserverClasses != null)
                observerClasses = cachedObserverClasses;

            Set<Class<?>> allInterfaces = Reflect.getInterfaces(modelClass, true);

            // Find classes annotated with the @Observe class
            Set<Class<?>> annotatedTypes = Reflect.getTypesAnnotatedWith(Observe.class, false);

            // Iterate through them and find Observer observing 'this' class for
            // the event passed to this method
            for (Class<?> annotatedType : annotatedTypes) {
                // Find @Observe annotation
                Annotation declaredAnnotation = Reflect.getDeclaredAnnotation(annotatedType, Observe.class);

                if (declaredAnnotation != null) {
                    String name = ((Observe) declaredAnnotation).name();
                    Event[] eventToObserve = ((Observe) declaredAnnotation).event();

                    if ((!"".equals(name.trim()) && modelClass.getName().endsWith(name.trim())
                        && Arrays.asList(eventToObserve).contains(e))
                        || (allInterfaces != null && allInterfaces.size() > 0 && !"".equals(name.trim())
                            && interfacesMatch(allInterfaces, name.trim())
                            && Arrays.asList(eventToObserve).contains(e))) {
                        observerClasses.add((Class<Observer>) annotatedType);
                    }
                }
            }
        }

        return observerClasses;
    }

    private static boolean interfacesMatch(Set<Class<?>> allInterfaces, String name) {
        for (Class<?> interf : allInterfaces) {
            if (interf.getName().endsWith(name))
                return true;
        }

        return false;
    }
}
