package com.geecommerce.core.reflect;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import com.geecommerce.core.system.merchant.model.Merchant;

/**
 * Created by Michael on 11.07.2016.
 */
public class MerchantClassLoader extends URLClassLoader {

    private static final String[] STANDARD_PACKAGES = new String[] { "java.", "javax.", "sun.", "com.sun.", "oracle.",
        "com.yourkit." };

    static {
        try {
            registerAsParallelCapable();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Merchant merchant;

    public MerchantClassLoader(Merchant merchant) throws MalformedURLException {
        super(merchant.getClasspath());

        this.merchant = merchant;
    }

    @Override
    public URL findResource(String name) {
        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        return super.findResources(name);
    }

    @Override
    public final Class<?> loadClass(final String name) throws ClassNotFoundException {
        Class<?> c = null;
        boolean isStandardClass;

        synchronized (getClassLoadingLock(name)) {
            c = findLoadedClass(name);

            if (c != null) {
                return c;
            }

            isStandardClass = isStandardClass(name);

            boolean isLocalMode = Boolean.getBoolean("cp.localmode");

            if (isStandardClass || isLocalMode) {
                try {
                    c = super.loadClass(name);
                } catch (Throwable t) {
                }

                return c;
            }

            c = findClass(name);
        }

        if (c == null) {
            throw new ClassNotFoundException("[" + merchant.getCode() + "] Class '" + name + "' could not be found.");
        }

        return c;
    }

    private final boolean isStandardClass(final String name) {
        boolean isStandardClass = false;

        for (String standardPackagePrefix : STANDARD_PACKAGES) {
            if (name.startsWith(standardPackagePrefix)) {
                isStandardClass = true;
                break;
            }
        }

        return isStandardClass;
    }

}
