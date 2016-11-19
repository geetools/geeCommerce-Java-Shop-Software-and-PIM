package com.geecommerce.core.web.stripes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.reflect.ApplicationContextClassLoader;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.util.NullableConcurrentHashMap;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.config.Configuration;

public class ApplicationContextActionResolver extends NameBasedActionResolver {

    private Configuration configuration;

    private static final String CACHE_KEY = "stripes.actionbean.classes";

    private static final Map<Object, Object> cache = new NullableConcurrentHashMap<>();

    private static final Map<String, Object> parallelLockMap = new NullableConcurrentHashMap<>();

    /**
     * We override the init method, as we do not want to initialize the action
     * beans yet. We have no valid ApplicationContext at this point in order
     * to do this on a per store basis.
     */
    public void init(Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    public void filterInit(Configuration configuration) {
        this.configuration = configuration;

        Set<Class<? extends ActionBean>> foundClasses = (Set<Class<? extends ActionBean>>) cache.get(CACHE_KEY);

        if (foundClasses == null) {
            // synchronized (getLock())
            foundClasses = findClasses();

            if (foundClasses != null) {
                Set<Class<? extends ActionBean>> cachedFoundClasses = (Set<Class<? extends ActionBean>>) cache.putIfAbsent(CACHE_KEY, foundClasses);

                if (cachedFoundClasses != null)
                    foundClasses = cachedFoundClasses;
            }

            // Process each ActionBean
            for (Class<? extends ActionBean> clazz : foundClasses) {
                addActionBean(clazz);
            }
        }
    }

    /**
     * Extended implementation of the findClasses() method to also locate action
     * beans in external modules.
     */
    protected Set<Class<? extends ActionBean>> findClasses() {
        Set<Class<? extends ActionBean>> actionBeans = null;

        try {
            ApplicationContext appCtx = App.get().getApplicationContext();

            if (appCtx != null) {
                Reflections reflections = Reflect.getReflections();
                Set<Class<?>> foundActionBeans = reflections.getTypesAnnotatedWith(UrlBinding.class, false);

                actionBeans = findOverridenClasses(foundActionBeans);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return (Set<Class<? extends ActionBean>>) actionBeans;
    }

    /**
     * Attempts to find action classes overridden by merchant.
     *
     * @param actionClasses
     * @return actionClasses
     */
    @SuppressWarnings("unchecked")
    protected Set<Class<? extends ActionBean>> findOverridenClasses(Set<Class<?>> actionClasses) {
        Set<Class<? extends ActionBean>> checkedClasses = new HashSet<>();

        for (Class<?> clazz : actionClasses) {
            try {
                ClassLoader classLoader = new ApplicationContextClassLoader(clazz.getClassLoader());
                Class<? extends ActionBean> customClass = (Class<? extends ActionBean>) classLoader.loadClass(clazz.getName());

                checkedClasses.add(customClass);
            } catch (ClassNotFoundException e) {
            }
        }

        return checkedClasses;
    }

    protected Configuration getConfiguration() {
        return this.configuration;
    }

    protected Object getLock() {
        Object lock = this;

        if (parallelLockMap != null) {
            Object newLock = new Object();
            lock = parallelLockMap.putIfAbsent("lock", newLock);

            if (lock == null) {
                lock = newLock;
            }
        }
        return lock;
    }

}
