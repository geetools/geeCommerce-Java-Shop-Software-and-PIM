package com.geecommerce.core.web.stripes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;

import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.vfs.DefaultVFS;

/**
 * Makes resources from external modules available to Stripes if they are
 * exported by Geemodule.
 * 
 * @author Michael Delamere
 */
public class ModuleVFS extends DefaultVFS {
    private Log log = Log.getInstance(ModuleVFS.class);

    protected List<URL> getModuleResources(String path) throws IOException {
        List<URL> resources = new ArrayList<URL>();

        try {
            App app = App.get();
            ApplicationContext appCtx = app.context();

            if (appCtx != null) {
                ModuleLoader loader = app.moduleLoader();

                Collection<Module> modules = loader.getLoadedModules();

                // Iterate through the modules and locate all the resources with
                // the specified path.
                for (Module module : modules) {
                    resources.addAll(Collections.list(module.getModuleClassLoader().getResources(path)));
                }

                // Also add resources that are not in any module - the Stripes
                // default.
                resources.addAll(super.getResources(path));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return resources;
    }

    @Override
    public List<String> list(URL url, String path) throws IOException {
        InputStream is = null;
        try {
            List<String> resources = new ArrayList<String>();

            // First, try to find the URL of a JAR file containing the requested
            // resource. If a JAR
            // file is found, then we'll list child resources by reading the
            // JAR.
            URL jarUrl = findJarForResource(url);
            if (jarUrl != null) {
                is = jarUrl.openStream();
                log.debug("Listing ", url);
                resources = listResources(new JarInputStream(is), path);
            } else {
                List<String> children = new ArrayList<String>();
                try {
                    if (isJar(url)) {
                        // Some versions of JBoss VFS might give a JAR stream
                        // even if the resource
                        // referenced by the URL isn't actually a JAR
                        is = url.openStream();
                        JarInputStream jarInput = new JarInputStream(is);
                        log.debug("Listing ", url);
                        for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null;) {
                            log.trace("Jar entry: ", entry.getName());
                            children.add(entry.getName());
                        }
                    } else {
                        /*
                         * Some servlet containers allow reading from directory
                         * resources like a text file, listing the child
                         * resources one per line. However, there is no way to
                         * differentiate between directory and file resources
                         * just by reading them. To work around that, as each
                         * line is read, try to look it up via the class loader
                         * as a child of the current resource. If any line fails
                         * then we assume the current resource is not a
                         * directory.
                         */
                        is = url.openStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        List<String> lines = new ArrayList<String>();
                        for (String line; (line = reader.readLine()) != null;) {
                            log.trace("Reader entry: ", line);
                            lines.add(line);
                            if (getModuleResources(path + "/" + line).isEmpty()) {
                                lines.clear();
                                break;
                            }
                        }

                        if (!lines.isEmpty()) {
                            log.debug("Listing ", url);
                            children.addAll(lines);
                        }
                    }
                } catch (FileNotFoundException e) {
                    /*
                     * For file URLs the openStream() call might fail, depending
                     * on the servlet container, because directories can't be
                     * opened for reading. If that happens, then list the
                     * directory directly instead.
                     */
                    if ("file".equals(url.getProtocol())) {
                        File file = new File(url.getFile());
                        log.trace("Listing directory ", file.getAbsolutePath());
                        if (file.isDirectory()) {
                            log.debug("Listing ", url);
                            children = Arrays.asList(file.list());
                        }
                    } else {
                        // No idea where the exception came from so rethrow it
                        throw e;
                    }
                }

                // The URL prefix to use when recursively listing child
                // resources
                String prefix = url.toExternalForm();
                if (!prefix.endsWith("/"))
                    prefix = prefix + "/";

                // Iterate over immediate children, adding files and recursing
                // into directories
                for (String child : children) {
                    String resourcePath = path + "/" + child;
                    resources.add(resourcePath);
                    URL childUrl = new URL(prefix + child);
                    resources.addAll(list(childUrl, resourcePath));
                }
            }

            return resources;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Recursively list the full resource path of all the resources that are
     * children of all the resources found at the specified path.
     * 
     * @param path
     *            The path of the resource(s) to list.
     * @return A list containing the names of the child resources.
     * @throws IOException
     *             If I/O errors occur
     */
    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<String>();
        for (URL url : getModuleResources(path)) {
            names.addAll(list(url, path));
        }
        return names;
    }
}
