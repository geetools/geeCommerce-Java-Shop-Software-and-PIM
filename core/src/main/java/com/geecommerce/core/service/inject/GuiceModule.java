package com.geecommerce.core.service.inject;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.reflect.ApplicationContextClassLoader;
import com.geecommerce.core.service.annotation.Implements;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.service.api.Injectable;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.service.persistence.PersistenceProvider;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geemodule.api.ModuleLoader;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class GuiceModule extends AbstractModule {
    private static final String CP_INJECT_WITH_IMPL_REGEX = "inject\\/[a-zA-Z0-9\\.]+\\/with_impl";

    private static final String CP_INJECT_WITH_IMPL_SUFFIX = "/with_impl";

    private static final String INJECTOR_PROVIDERS_ENABLED_KEY = "injector.providers.enabled";

    private static final Logger log = LogManager.getLogger(GuiceModule.class);

    @Override
    protected void configure() {
        ModuleLoader loader = App.get().moduleLoader();

        Map<String, String> configuredInjectMapping = getConfiguredInjectMapping();

        if (loader != null) {
            registerProvider(com.geecommerce.core.service.annotation.Model.class, Model.class, configuredInjectMapping,
                false);
            registerProvider(com.geecommerce.core.service.annotation.Pojo.class, Pojo.class, configuredInjectMapping,
                false);
            registerProvider(com.geecommerce.core.service.annotation.Dao.class, Dao.class, configuredInjectMapping,
                true);
            registerProvider(com.geecommerce.core.service.annotation.Repository.class, Repository.class,
                configuredInjectMapping, true);
            registerProvider(com.geecommerce.core.service.annotation.Helper.class, Helper.class,
                configuredInjectMapping, true);
            registerProvider(com.geecommerce.core.service.annotation.Service.class, Service.class,
                configuredInjectMapping, true);
            registerProvider(com.geecommerce.core.service.annotation.Injectable.class, Injectable.class,
                configuredInjectMapping, false);
        } else {
            throw new IllegalStateException(
                "Unable to register Guice providers because the ModuleLoader has not been initialized");
        }
    }

    private Map<String, String> getConfiguredInjectMapping() {
        List<ConfigurationProperty> configProps = App.get().getConfigProperties(CP_INJECT_WITH_IMPL_REGEX);

        Map<String, String> configuredInjectMap = new HashMap<>();

        if (configProps != null && configProps.size() > 0) {
            for (ConfigurationProperty cp : configProps) {
                if (cp != null && cp.getKey() != null) {
                    String key = cp.getKey().trim();
                    String val = cp.getStringValue();

                    if (val != null) {
                        String interfaceType = key.substring(17, key.indexOf(CP_INJECT_WITH_IMPL_SUFFIX));
                        configuredInjectMap.put(interfaceType, val.trim());
                    }
                }
            }
        }

        return configuredInjectMap;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void registerProvider(Class<? extends Annotation> annotation, Class<?> interfaceType,
        Map<String, String> configuredInjectMapping, boolean isSingleton) {
        ModuleLoader loader = App.get().moduleLoader();

        Class<?>[] foundClasses = (Class<?>[]) loader.findAllTypesAnnotatedWith(annotation, true);

        Class currentFoundClass = null;
        Class currentFoundClassInterface = null;

        if (foundClasses != null && foundClasses.length > 0) {
            for (Class foundClass : foundClasses) {
                if (foundClass == null)
                    continue;

                Class foundClassInterface = toInterface(foundClass, configuredInjectMapping);

                currentFoundClass = foundClass;
                currentFoundClassInterface = foundClassInterface;

                if (foundClassInterface == null) {
                    System.out.println("No interface found for: " + foundClass);
                    continue;
                }

                try {
                    // See if there is an inject-mapping which enables us to
                    // specify a specific implementation.
                    // For example, there may be a MySQL DAO and a MongoDB DAO
                    // for the same interface.
                    // By defining a configuration property like
                    // "inject/interface/com.geecommerce.price.dao.PriceDao/with_impl"
                    // => "com.geecommerce.price.dao.DefaultPriceMySQLDao", we
                    // can change
                    // the desired implementation.
                    String implType = configuredInjectMapping.get(foundClassInterface.getName());

                    if (implType != null) {
                        System.out.println("Found DB impl: " + implType);

                        if (implType.equals(foundClass.getName())) {
                            if (log.isTraceEnabled()) {
                                log.trace(
                                    "Found an implementation in mongdo db to override standard binding [interface="
                                        + foundClassInterface.getName() + ", new type=" + implType
                                        + ", overriden type=" + foundClass.getName() + "].");
                            }

                            boolean providersAreEnabled = Boolean.getBoolean(INJECTOR_PROVIDERS_ENABLED_KEY);

                            if (providersAreEnabled) {
                                if (isSingleton) {
                                    bind(foundClassInterface)
                                        .toProvider(new GuiceProvider(foundClassInterface, foundClass))
                                        .in(Singleton.class);
                                } else {
                                    bind(foundClassInterface)
                                        .toProvider(new GuiceProvider(foundClassInterface, foundClass));
                                }
                            } else {
                                // Use the custom ApplicationContextClassLoader
                                // to find class
                                ClassLoader classLoader = new ApplicationContextClassLoader(
                                    foundClassInterface.getClassLoader());

                                // Load merchant or store-specific class if one
                                // exists
                                Class appClass = classLoader.loadClass(foundClass.getName());

                                if (isSingleton) {
                                    super.bind(foundClassInterface).to(appClass).in(Singleton.class);
                                } else {
                                    super.bind(foundClassInterface).to(appClass);
                                }
                            }
                        }
                    } else {
                        if (!foundClass.getSimpleName().startsWith("Default")
                            && !foundClass.getSimpleName().startsWith("My"))
                            continue;

                        if (log.isTraceEnabled()) {
                            log.trace("Binding class [interface=" + foundClassInterface.getName() + ", type="
                                + foundClass.getName() + "].");
                        }

                        boolean providersAreEnabled = Boolean.getBoolean(INJECTOR_PROVIDERS_ENABLED_KEY);

                        if (providersAreEnabled) {
                            if (isSingleton) {
                                bind(foundClassInterface).toProvider(new GuiceProvider(foundClassInterface, foundClass))
                                    .in(Singleton.class);
                            } else {
                                bind(foundClassInterface)
                                    .toProvider(new GuiceProvider(foundClassInterface, foundClass));
                            }
                        } else {
                            // Use the custom ApplicationContextClassLoader to
                            // find class
                            ClassLoader classLoader = new ApplicationContextClassLoader(
                                foundClassInterface.getClassLoader());

                            // Load merchant or store-specific class if one
                            // exists
                            Class appClass = classLoader.loadClass(foundClass.getName());

                            if (isSingleton) {
                                super.bind(foundClassInterface).to(appClass).in(Singleton.class);
                            } else {
                                super.bind(foundClassInterface).to(appClass);
                            }
                        }
                    }
                } catch (Throwable t) {
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX: " + interfaceType + " -> " + currentFoundClass
                        + " -> " + currentFoundClassInterface);

                    log.throwing(t);

                    t.printStackTrace();

                    throw new RuntimeException(t);
                }
            }
        }
    }

    protected Class<?> toInterface(Class<?> clazz, Map<String, String> configuredInjectMapping) {
        String defaultType = clazz.getName();
        String interfaceType = defaultType.replace(".Default", ".");
        String interfaceType2 = defaultType.substring(0, defaultType.lastIndexOf('.') + 1) + 'I'
            + defaultType.substring(defaultType.lastIndexOf('.') + 1);

        Class<?>[] interfaces = clazz.getInterfaces();
        Class<?> foundInterface = null;
        Class<?> foundConfiguredInterface = null;

        for (Class<?> interf : interfaces) {
            if (interf.getName().equals(interfaceType)) {
                foundInterface = interf;
                break;
            }

            // See if a specific implementation mapping exists.
            if (configuredInjectMapping.containsKey(interf.getName())) {
                foundConfiguredInterface = interf;
            }
        }

        // Try IInterface ...
        if (foundInterface == null) {
            for (Class<?> interf : interfaces) {
                if (interf.getName().equals(interfaceType2)) {
                    foundInterface = interf;
                    break;
                }
            }
        }

        if (foundInterface == null && foundConfiguredInterface == null && isPersistenceType(clazz)) {
            PersistenceProvider pp = SystemInjector.get().getInstance(PersistenceProvider.class);
            boolean isCompatible = pp.isCompatible(clazz);

            if (isCompatible) {
                Implements implementsAnnotation = clazz.getDeclaredAnnotation(Implements.class);

                if (implementsAnnotation == null || implementsAnnotation.value() == null) {
                    System.out.println("Unable to bind the persistence type '" + clazz.getName()
                        + "' to Guice as it was not possible to obtain an interface using the standard rules. Try optionally specifying the interface using the @Implements annotation.");
                } else {
                    if (!implementsAnnotation.value().isInterface()) {
                        System.out.println(
                            "Unable to bind the persistence type '" + clazz.getName() + "' to Guice because '"
                                + implementsAnnotation.value().getName() + "' is not an interface.");
                    } else {
                        foundConfiguredInterface = implementsAnnotation.value();
                    }
                }
            }
        }

        if (foundInterface == null && foundConfiguredInterface == null && !isPersistenceType(clazz)) {
            Implements implementsAnnotation = clazz.getDeclaredAnnotation(Implements.class);

            if (implementsAnnotation != null) {
                if (!implementsAnnotation.value().isInterface()) {
                    System.out
                        .println("Unable to bind the persistence type '" + clazz.getName() + "' to Guice because '"
                            + implementsAnnotation.value().getName() + "' is not an interface.");
                } else {
                    foundConfiguredInterface = implementsAnnotation.value();
                }
            }
        }

        // Basic check to make sure that the interfaces are correct.
        if (foundInterface == null && foundConfiguredInterface == null) {
            // throw new IllegalStateException("The class '" + defaultType + "'
            // must implement the interface '" + interfaceType + "'");
            System.out.println("The class '" + defaultType + "' must implement the interface '" + interfaceType + "'");
            return null;
        }

        if (foundInterface != null && !foundInterface.isInterface()) {
            throw new IllegalStateException(
                "The class '" + interfaceType + "' must be an interface and declared in '" + defaultType + "'");
        }

        if (foundConfiguredInterface != null && !foundConfiguredInterface.isInterface()) {
            throw new IllegalStateException("The class '" + foundConfiguredInterface
                + "' must be an interface and declared in '" + defaultType + "'");
        }

        return foundConfiguredInterface == null ? foundInterface : foundConfiguredInterface;
    }

    protected boolean isPersistenceType(Class<?> clazz) {
        return clazz.isAnnotationPresent(Persistence.class);
    }
}
