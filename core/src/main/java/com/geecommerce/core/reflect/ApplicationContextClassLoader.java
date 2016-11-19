package com.geecommerce.core.reflect;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.ProxyClassLoader;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.util.NullableConcurrentHashMap;
import com.geecommerce.core.utils.Classes;
import com.geemodule.api.ModuleLoader;

/**
 * ClassLoader for finding application context specific classes. In most cases
 * this class loader will not find a shop-specific version of a class. The
 * following explains in which order the class loader would attempt to find the
 * example class com.geecommerce.service.test.DefaultTestService: <br/>
 * <br/>
 * <ol>
 * <li>Does a shop-view specific version exist? -
 * <code>{@literal custom.<merchant>.<shop>.<shop-variant>.<shop-view>.service.test.MyService}</code></li>
 * <li>If not, does a shop-variant specific version exist? -
 * <code>{@literal custom.<merchant>.<shop>.<shop-variant>.service.test.MyService}</code></li>
 * <li>If not, does a shop specific version exist? -
 * <code>{@literal custom.<merchant>.<shop>.service.test.MyService}</code></li>
 * <li>If not, does a merchant specific version exist? -
 * <code>{@literal custom.<merchant>.service.test.MyService}</code></li>
 * <li>If none of the above exist, then just return the default class
 * com.geecommerce.service.test.DefaultTestService.</li>
 * </ol>
 * <br/>
 * Note: this only works for classes starting with the package
 * com.geecommerce.core.reflect <i>com.cb</i> and starting with the class name prefix
 * <i>Default</i>.
 *
 * @author Michael Delamere
 */
public final class ApplicationContextClassLoader extends ClassLoader {
    static {
        try {
            registerAsParallelCapable();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static final String BASE_PACKAGE_PREFIX = "com.geecommerce";
    private static final String BASE_PROJECTS_PACKAGE_PREFIX = "com.geecommerce.projects";
    private static final String CORE_PACKAGE_PART = ".geecommerce.core.";
    private static final String CUSTOM_PACKAGE_PREFIX = "custom.";
    private static final String DEFAULT_CLASS_PREFIX = ".Default";
    private static final String DEFAULT_ACTION_SUFFIX = "Action";
    private static final String CUSTOM_CLASS_PREFIX = ".My";
    private static final String CUSTOM_ACTION_CLASS_PREFIX = "My";

    private final ClassLoader customClassLoader;

    private static final Map<String, Class<?>> cache = new NullableConcurrentHashMap<>();

    // private static final Logger log =
    // LogManager.getLogger(ApplicationContextClassLoader.class);

    public ApplicationContextClassLoader() {
        this.customClassLoader = null;
    }

    public ApplicationContextClassLoader(ClassLoader customClassLoader) {
        this.customClassLoader = customClassLoader;
    }

    @Override
    public Class<?> loadClass(String classFQN) throws ClassNotFoundException {
        // ----------------------------------------------------
        // Attempt to get class from cache first
        // ----------------------------------------------------

        Class<?> foundClass = cache.get(classFQN);

        if (foundClass != null) {
            return foundClass;
        }

        // Overriding core classes is not allowed.
        if (classFQN.contains(CORE_PACKAGE_PART)) {
            throw new ClassNotFoundException("This classloader cannot be used for core-classes.");
        }

        ModuleLoader moduleLoader = App.get().getModuleLoader();
        ApplicationContext appCtx = App.get().getApplicationContext();

        View view = appCtx.getView();
        Store store = appCtx.getStore();
        Merchant merchant = appCtx.getMerchant();

        JarClassLoader jcl = new JarClassLoader();
        jcl.getParentLoader().setEnabled(false);
        jcl.getThreadLoader().setEnabled(false);
        jcl.getCurrentLoader().setEnabled(false);

        // Also look in the custom class-loader, if it exists. This will most
        // likely be the module-class-loader.
        if (this.customClassLoader != null) {
            jcl.addLoader(new ProxyClassLoader() {
                @Override
                public InputStream loadResource(String name) {
                    return customClassLoader.getResourceAsStream(name);
                }

                public URL findResource(String s) {
                    return null;
                }

                @Override
                public Class<?> loadClass(String name, boolean initialize) {
                    try {
                        // Use the geemodule public classloader so that the
                        // project's custom classes folder
                        // also sees classes located in modules.
                        return App.get().getModuleLoader().getPublicClassLoader(this.getClass().getClassLoader()).loadClass(name);
                    } catch (Throwable t) {
                    }

                    return null;
                }
            });
        }

        // -----------------------------------------------------------------------------------------
        // Add merchant's project classpath to class-loader.
        // -----------------------------------------------------------------------------------------
        File customClassesDir = new File(merchant.getClassesPath());

        Class<?> classToReturn = null;

        if (customClassesDir.exists()) {
            jcl.add(customClassesDir.getAbsolutePath());
        }

        // -----------------------------------------------------------------------------------------
        // If the class is from a module we allow a project specific override.
        // -----------------------------------------------------------------------------------------

        if (moduleLoader.exportsPackage(classFQN)) {
            Class<?> defaultClass = null;

            if (this.customClassLoader != null) {
                defaultClass = this.customClassLoader.loadClass(classFQN);
            } else {
                defaultClass = this.getClass().getClassLoader().loadClass(classFQN);
            }

            // if (view == null && store == null && merchant == null) {
            // return defaultClass;
            // }

            // -----------------------------------------------------------------------------------------
            // Attempt to find context specific overrides for nodule classes.
            // -----------------------------------------------------------------------------------------

            String viewClassFQN = null;
            if (view != null)
                viewClassFQN = toCustomFQN(classFQN, getViewPackage());

            String storeClassFQN = null;
            if (store != null)
                storeClassFQN = toCustomFQN(classFQN, getStorePackage());

            String merchantClassFQN = null;
            if (merchant != null)
                merchantClassFQN = toCustomFQN(classFQN, getMerchantPackage());

            // ----------------------------------------------------
            // View specific class
            // ----------------------------------------------------
            try {
                foundClass = jcl.loadClass(viewClassFQN);
            } catch (Throwable t) {
            }

            // ----------------------------------------------------
            // Store specific class
            // ----------------------------------------------------
            if (foundClass == null) {
                try {
                    foundClass = jcl.loadClass(storeClassFQN);
                } catch (Throwable t) {
                }
            }

            // ----------------------------------------------------
            // Merchant specific class
            // ----------------------------------------------------
            if (foundClass == null) {
                try {
                    foundClass = jcl.loadClass(merchantClassFQN);
                } catch (Throwable t) {
                }
            }

            if (foundClass != null && isInheritanceValid(foundClass, defaultClass)) {
                classToReturn = foundClass;
            } else {
                classToReturn = defaultClass;
            }
        } else {
            classToReturn = jcl.loadClass(classFQN);
        }

        // Put found class in cache to speed up retrieval next time
        Class<?> cachedClassToReturn = cache.putIfAbsent(classFQN, classToReturn);

        if (cachedClassToReturn != null)
            classToReturn = cachedClassToReturn;

        return classToReturn;
    }

    private boolean isInheritanceValid(Class<?> foundClass, Class<?> defaultModuleClass) throws ClassNotFoundException {
        // ----------------------------------------------------------------------------
        // Check inheritance. Overriding custom class must either inherit from
        // the
        // default class or implement the same interfaces.
        // ----------------------------------------------------------------------------
        if (foundClass != null) {
            Class<?> extendedClass = foundClass.getSuperclass();

            if (extendedClass == null || !defaultModuleClass.equals(extendedClass)) {
                // -------------------------------------------------------------------------
                // Make sure that the default and custom classes share the same
                // interfaces
                // -------------------------------------------------------------------------
                Class<?>[] customClassInterfaces = foundClass.getInterfaces();
                Class<?>[] defaultClassInterfaces = defaultModuleClass.getInterfaces();

                if (defaultClassInterfaces == null || defaultClassInterfaces.length == 0)
                    throw new ClassNotFoundException("The custom class <" + foundClass.getName() + "> was found for the default class <" + defaultModuleClass.getName()
                        + ">, but the default class has not declared any interfaces. Both the default and custom class muss share the same interfaces when the custom class does not extend the default class.");

                for (Class<?> defaultClassInterface : defaultClassInterfaces) {
                    boolean foundInterface = false;
                    for (Class<?> customClassInterface : customClassInterfaces) {
                        if (defaultClassInterface.equals(customClassInterface)) {
                            foundInterface = true;
                            break;
                        }
                    }

                    if (!foundInterface)
                        throw new ClassNotFoundException("The custom class <" + foundClass.getName() + "> was found for the default class <" + defaultModuleClass.getName()
                            + ">, but it does not implement the interfaces declared in that class ("
                            + Arrays.toString(defaultClassInterfaces) + ").");
                }
            }
        }

        return true;
    }

    private String toCustomFQN(String defaultClassFQN, String shopContextClassFQN) {
        if (defaultClassFQN.indexOf(DEFAULT_CLASS_PREFIX) == -1 && defaultClassFQN.endsWith(DEFAULT_ACTION_SUFFIX)) {
            StringBuilder customFQN = new StringBuilder(defaultClassFQN.replace(BASE_PACKAGE_PREFIX, shopContextClassFQN));
            customFQN.insert(customFQN.lastIndexOf(Str.DOT) + 1, CUSTOM_ACTION_CLASS_PREFIX);

            return customFQN.toString();
        } else {
            return defaultClassFQN.replace(BASE_PACKAGE_PREFIX, shopContextClassFQN).replace(DEFAULT_CLASS_PREFIX, CUSTOM_CLASS_PREFIX);
        }
    }

    private String getMerchantPackage() {
        ApplicationContext appCtx = getApplicationContext();

        return new StringBuilder(CUSTOM_PACKAGE_PREFIX).append(Classes.ensureSafePackagePath(appCtx.getMerchant().getCode())).toString();
    }

    private String getStorePackage() {
        ApplicationContext appCtx = getApplicationContext();

        return new StringBuffer(CUSTOM_PACKAGE_PREFIX).append(Classes.ensureSafePackagePath(appCtx.getMerchant().getCode())).append(Char.DOT)
            .append(Classes.ensureSafePackagePath(appCtx.getStore().getCode())).toString();
    }

    private String getViewPackage() {
        ApplicationContext appCtx = getApplicationContext();

        return new StringBuffer(CUSTOM_PACKAGE_PREFIX).append(Classes.ensureSafePackagePath(appCtx.getMerchant().getCode())).append(Char.DOT)
            .append(Classes.ensureSafePackagePath(appCtx.getStore().getCode())).append(Char.DOT)
            .append(Classes.ensureSafePackagePath(appCtx.getView().getCode())).toString();
    }

    private ApplicationContext getApplicationContext() {
        return App.get().getApplicationContext();
    }
}
