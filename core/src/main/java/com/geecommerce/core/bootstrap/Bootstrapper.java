package com.geecommerce.core.bootstrap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.geecommerce.core.App;
import com.geecommerce.core.RegistryKey;
import com.geecommerce.core.bootstrap.annotation.Bootstrap;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.util.NullableConcurrentHashMap;
import com.geecommerce.core.utils.Annotations;
import com.google.inject.Injector;

public class Bootstrapper {
    private static final Logger log = LogManager.getLogger(Bootstrapper.class);

    private static final String CACHE_NAME = "gc/bootstrapper";
    private static final String CACHE_KEY = "bootstrap.classes";

    private static final Map<String, LocatedBootstrap[]> cache = new NullableConcurrentHashMap<>();

    public static final void run(HttpServletRequest request, HttpServletResponse response)
        throws InstantiationException, IllegalAccessException {
        long start = System.currentTimeMillis();

        LocatedBootstrap[] bootstrapTypes = locateBootstrapTypes();

        long end = System.currentTimeMillis();

        if (log.isTraceEnabled()) {
            log.trace("Located bootstrap-classes in " + (end - start) + "ms.");
        }

        start = System.currentTimeMillis();

        Injector injector = ModuleInjector.get();

        for (LocatedBootstrap locatedBootstrap : bootstrapTypes) {
            long startBootstrap = System.currentTimeMillis();

            if (log.isTraceEnabled()) {
                log.trace("Attempting to bootstrap class '" + locatedBootstrap.bootstrapableClass.getName()
                    + "' with order number #" + locatedBootstrap.bootstrapAnnotation.order() + ".");
            }

            try {
                AbstractBootstrap bootstrap = (AbstractBootstrap) injector
                    .getInstance(locatedBootstrap.bootstrapableClass);

                bootstrap.setRequest(request);
                bootstrap.setResponse(response);
                bootstrap.init();
            } catch (Throwable t) {
                throw new RuntimeException(
                    "Bootstrap-class '" + locatedBootstrap.getClass().getName() + "' caused a FATAL error", t);
            }

            long endBootstrap = System.currentTimeMillis();

            if (log.isTraceEnabled()) {
                log.trace("Processed bootstrap class '" + locatedBootstrap.bootstrapableClass.getName() + "' in "
                    + (endBootstrap - startBootstrap) + "ms.");
            }

        }

        end = System.currentTimeMillis();

        if (log.isTraceEnabled()) {
            log.trace("Completed bootstrapping in " + (end - start) + "ms.");
        }
    }

    @SuppressWarnings("unchecked")
    private static final LocatedBootstrap[] locateBootstrapTypes() {
        LocatedBootstrap[] locatedBootstraps = cache.get(CACHE_KEY);
        if (locatedBootstraps == null) {
            Boolean isUnitTest = App.get().registryGet(RegistryKey.UNIT_TEST);

            List<LocatedBootstrap> bootstrapTypes = new ArrayList<>();

            Reflections reflections = Reflect.getReflections();

            // Find classes annotated with the Bootstrap class
            Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(Bootstrap.class, false);

            if (log.isTraceEnabled()) {
                log.trace("Found " + annotatedTypes.size() + " bootstrap classes.");
            }

            // Iterate through all the @Bootstrap
            for (Class<?> annotatedType : annotatedTypes) {
                // Find @Bootstrap annotation
                Annotation declaredAnnotation = Annotations.declaredAnnotation(annotatedType, Bootstrap.class);

                if (declaredAnnotation != null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Found bootstrap class: " + annotatedType.getName());
                    }

                    boolean includeInUnitTest = ((Bootstrap) declaredAnnotation).unitTest();

                    // Do not process if we are in a unit-test and the bootstrap
                    // annotation has been set to not start in
                    // a
                    // test-case.
                    if (isUnitTest != null && isUnitTest && !includeInUnitTest)
                        continue;

                    bootstrapTypes.add(new LocatedBootstrap((Bootstrap) declaredAnnotation,
                        (Class<AbstractBootstrap>) annotatedType));
                }
            }

            if (bootstrapTypes != null && bootstrapTypes.size() > 0) {
                // Sort located bootstrap types according to the specified order
                // in Bootstrap annotation.
                Collections.sort(bootstrapTypes, new Comparator<LocatedBootstrap>() {
                    @Override
                    public int compare(LocatedBootstrap o1, LocatedBootstrap o2) {
                        return (o1.bootstrapAnnotation.order() < o2.bootstrapAnnotation.order() ? -1
                            : (o1.bootstrapAnnotation.order() > o2.bootstrapAnnotation.order() ? 1 : 0));
                    }
                });

                locatedBootstraps = bootstrapTypes.toArray(new LocatedBootstrap[bootstrapTypes.size()]);
            }

            LocatedBootstrap[] cachedLocatedBootstraps = cache.putIfAbsent(CACHE_KEY,
                locatedBootstraps == null ? new LocatedBootstrap[0] : locatedBootstraps);

            if (cachedLocatedBootstraps != null)
                locatedBootstraps = cachedLocatedBootstraps;
        }

        return locatedBootstraps;
    }
}

final class LocatedBootstrap {
    final Bootstrap bootstrapAnnotation;
    final Class<AbstractBootstrap> bootstrapableClass;

    LocatedBootstrap(Bootstrap bootstrapAnnotation, Class<AbstractBootstrap> bootstrapableClass) {
        this.bootstrapAnnotation = bootstrapAnnotation;
        this.bootstrapableClass = bootstrapableClass;
    }
}
