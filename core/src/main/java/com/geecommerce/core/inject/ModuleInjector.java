package com.geecommerce.core.inject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.inject.GuiceModule;
import com.geecommerce.core.system.model.RequestContext;
import com.geemvc.inject.GeeMvcModule;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Takes care of initializing the Google Guice injector
 * com.google.inject.Injector with a list of modules defined in
 * System.properties (key
 * 'inject.modules'). Objects created by the Guice injector may use the Guice
 * annotations for injecting objects such as services and DAOs. <br/>
 * <br/>
 * For example:
 * <p>
 * 
 * <pre>
 * <code>
 * public class DefaultOrderService implements OrderService
 * {
 *    {@literal @}Inject
 * 	private OrderDao orderDao;
 *
 * [...]
 * }
 * </code>
 * </pre>
 * <p>
 * or:
 * <p>
 * 
 * <pre>
 * <code>
 * public class Frontend
 * {
 * 	private final OrderService orderService;
 *
 *    {@literal @}Inject
 * 	private Frontend(OrderService orderService)
 *    {
 * 		this.orderService = orderService;
 *    }
 *
 * [...]
 * }
 * </code>
 * </pre>
 *
 * @author Michael Delamere
 */
public final class ModuleInjector {
    private static final Logger log = LogManager.getLogger(ModuleInjector.class);

    private static volatile ConcurrentHashMap<String, Injector> injectorCache = new ConcurrentHashMap<>();

    /**
     * Central class for creating Guice objects.
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static final Injector get() {
        App app = App.get();
        ApplicationContext appCtx = app.getApplicationContext();

        if (appCtx == null)
            throw new IllegalStateException("The ModuleInjector depends on the ApplicationContext object which has not been initialized yet");

        RequestContext reqCtx = appCtx.getRequestContext();

        if (reqCtx != null && reqCtx.getId() != null) {
            String cacheKey = reqCtx.toKey();

            Injector injector = injectorCache.get(cacheKey);

            if (injector == null) {
                injector = createInjector();

                Injector cachedInjector = injectorCache.putIfAbsent(cacheKey, injector);

                if (cachedInjector != null)
                    injector = cachedInjector;
            }

            return injector;
        } else {
            Injector injector = app.registryGet(Injector.class.getName());

            if (injector == null) {
                injector = createInjector();
                app.registryPut(Injector.class.getName(), injector);
            }

            if (log.isDebugEnabled()) {
                log.exit(injector);
            }

            return injector;
        }
    }

    public static Injector createInjector() {
        List<Module> modulesToInject = new ArrayList<>();
        modulesToInject.add(new GeeMvcModule());
        modulesToInject.addAll(SystemInjector.modules());
        modulesToInject.add(new GuiceModule());

        modulesToInject.add(new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(GuiceContainer.class);

                Set<Class<?>> foundClasses1 = Reflect.getReflections().getTypesAnnotatedWith(Path.class, false);
                Set<Class<?>> foundClasses2 = Reflect.getReflections().getTypesAnnotatedWith(Provider.class, false);

                Set<Class<?>> foundClasses = new HashSet<>();
                foundClasses.addAll(foundClasses1);
                foundClasses.addAll(foundClasses2);

                if (foundClasses != null && foundClasses.size() > 0) {
                    for (Class<?> foundClass : foundClasses) {
                        if (foundClass == null)
                            continue;

                        if (!isSystemClass(foundClass) && (ResourceConfig.isRootResourceClass(foundClass) || ResourceConfig.isProviderClass(foundClass))) {
                            bind(foundClass);
                        }
                    }
                }

                serve("/api/*").with(GuiceContainer.class, ImmutableMap.of(JSONConfiguration.FEATURE_POJO_MAPPING, "true"));
            }

            protected boolean isSystemClass(Class<?> foundClass) {
                return foundClass.getName().startsWith("com.owlike.genson") || foundClass.getName().startsWith("com.sun.jersey");
            }
        });

        return Guice.createInjector(modulesToInject);
    }
}
