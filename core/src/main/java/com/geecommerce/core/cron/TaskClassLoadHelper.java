package com.geecommerce.core.cron;

import java.io.InputStream;
import java.net.URL;

import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;

import com.geecommerce.core.App;

public class TaskClassLoadHelper implements ClassLoadHelper {
    protected ClassLoader geemoduleClassLoader;
    protected CascadingClassLoadHelper cascadeClassLoadHelper;

    public TaskClassLoadHelper() {
        App.get().getModuleLoader();
    }

    @Override
    public void initialize() {
        geemoduleClassLoader = App.get().getModuleLoader().getPublicClassLoader();

        // Initialize a fall back ClassLoadHelper.
        cascadeClassLoadHelper = new CascadingClassLoadHelper();
        cascadeClassLoadHelper.initialize();
    }

    @Override
    public ClassLoader getClassLoader() {
        return geemoduleClassLoader;
    }

    @Override
    public URL getResource(String name) {
        URL ret = geemoduleClassLoader.getResource(name);

        if (ret == null) {
            ret = cascadeClassLoadHelper.getResource(name);
        }

        return ret;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream ret = geemoduleClassLoader.getResourceAsStream(name);

        if (ret == null) {
            ret = cascadeClassLoadHelper.getResourceAsStream(name);
        }

        return ret;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> ret = null;

        try {
            ret = geemoduleClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            ret = cascadeClassLoadHelper.loadClass(name);
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<? extends T> loadClass(String name, Class<T> clazz) throws ClassNotFoundException {
        return (Class<? extends T>) loadClass(name);
    }
}
