package com.geecommerce.core.reflect;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geemodule.api.ModuleLoader;
import com.geemvc.reflect.DefaultReflectionsProvider;

public class ApplicationReflectionsProvider extends DefaultReflectionsProvider {

    @Override
    protected Set<URL> appendURLs() {
        Set<URL> urls = new HashSet<>();

        URL coreJar = ClasspathHelper.getCoreJar();
        urls.add(coreJar);

        ModuleLoader loader = App.get().moduleLoader();

        if (loader != null) {
            URL[] moduleClassPath = loader.getPublicClasspath();

            for (URL url : moduleClassPath) {
                urls.add(url);
            }
        }

        ApplicationContext appCtx = App.get().context();

        if (appCtx != null) {
            Merchant merchant = appCtx.getMerchant();
            File customClassesDir = new File(merchant.getClassesPath());

            Class<?> classToReturn = null;

            if (customClassesDir.exists()) {
                try {
                    urls.add(customClassesDir.toURL());
                } catch (MalformedURLException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return urls;
    }

    @Override
    protected Set<ClassLoader> appendClassLoaders() {
        Set<ClassLoader> classLoaders = new HashSet<>();

        ModuleLoader loader = App.get().moduleLoader();

        if (loader != null) {
            classLoaders.add(new ApplicationContextClassLoader(loader.getPublicClassLoader()));

            /*
             * ClassLoader[] moduleClassLoaders =
             * loader.getModuleClassLoaders();
             * 
             * for (ClassLoader classLoader : moduleClassLoaders) {
             * classLoaders.add(classLoader); }
             */
        }

        return classLoaders;
    }

    @Override
    protected ServletContext servletContext() {
        return App.get().servletContext();
    }
}
