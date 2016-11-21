package com.geecommerce.core.inject;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.Constant;
import com.geecommerce.core.config.SystemConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class SystemInjector {
    protected static Injector injector;

    protected static final Object LOCK = new Object();

    protected static boolean bootstrapping = true;

    protected SystemInjector() {

    }

    public static final void bootstrap() {
        List<Module> injectableSystemModules = modules();

        if (injectableSystemModules.isEmpty()) {
            System.out.println("[FATAL] Unable to load system modules!");
            throw new IllegalStateException();
        }

        synchronized (LOCK) {
            try {
                injector = Guice.createInjector(injectableSystemModules);
                bootstrapping = false;
            } catch (Throwable t) {
                // Make sure that the stack trace is shown on start up,
                // otherwise it is impossible to tell what the
                // problem is.
                // Tomcat output "SEVERE: Error listenerStart" is not much
                // information.
                t.printStackTrace();
                throw t;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Module> modules() {

        // List of modules to register with Guice
        List<Module> systemModules = new ArrayList<Module>();

        // Get static list of Guice modules from System.properties
        String[] configuredSystemModules = SystemConfig.GET.array(Constant.APPLICATION_KEY_INJECT_MODULES);

        for (String injectModule : configuredSystemModules) {
            Class<Module> moduleClass;

            try {
                moduleClass = (Class<Module>) Class.forName(injectModule);

                Module moduleObj = moduleClass.newInstance();

                System.out.println("Adding new module instance '" + moduleClass.getName() + "'.");

                systemModules.add(moduleObj);
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException(t);
            }
        }

        return systemModules;
    }

    public static final Injector get() {
        if (bootstrapping) {
            synchronized (LOCK) {
                return injector;
            }
        } else {
            return injector;
        }
    }
}
