package com.geecommerce.core.reflect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import com.geecommerce.core.App;
import com.geecommerce.core.Constant;

public class ClasspathHelper {
    public static URL getCoreJar() {
        boolean isLocalMode = Boolean.getBoolean("cp.localmode");

        ServletContext servletContext = App.get().getServletContext();

        if (servletContext == null && !isLocalMode)
            return null;

        URL foundJar = null;

        if (servletContext != null) {
            Set<String> libJars = servletContext.getResourcePaths("/WEB-INF/lib");

            // First look for WEB-INF/lib/cb-core-*.jar
            for (String jar : libJars) {
                if (jar.startsWith("/WEB-INF/lib/" + Constant.CORE_JAR_PREFIX)) {
                    try {
                        File f = new File(servletContext.getRealPath(jar));

                        if (f.exists()) {
                            foundJar = f.toURI().toURL();
                            break;
                        } else {
                            throw new FileNotFoundException(f.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // ------------------------------------------------------------------------------------
            // Return /WEB-INF/classes path if core-jar could not be located.
            // Normally the jar file
            // should be found, except perhaps in a development environment
            // where the container
            // points directly to the webapp-folder of the project.
            // ------------------------------------------------------------------------------------

            if (foundJar == null) {
                try {
                    List<URL> urls = Collections.list(Thread.currentThread().getContextClassLoader().getResources(""));

                    for (URL url : urls) {
                        if (url.getPath().contains("/WEB-INF/classes")) {
                            foundJar = url;
                        }
                    }
                } catch (IOException e1) {
                }
            }
        } else if (isLocalMode) {
            String cp = System.getProperty("java.class.path");

            String[] files = cp.split(File.pathSeparator);

            if (files != null && files.length > 0) {
                for (String f : files) {
                    if (f.contains("geecommerce-core")) {
                        File file = new File(f);

                        if (file.exists()) {
                            try {
                                foundJar = file.toURI().toURL();

                                break;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }

        return foundJar;
    }
}
