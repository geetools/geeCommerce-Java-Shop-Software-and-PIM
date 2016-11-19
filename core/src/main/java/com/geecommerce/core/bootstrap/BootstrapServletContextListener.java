package com.geecommerce.core.bootstrap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.ThreadContext;
import org.im4java.process.ProcessStarter;

import com.geecommerce.core.App;
import com.geecommerce.core.AppRegistry;
import com.geecommerce.core.Constant;
import com.geecommerce.core.RegistryKey;
import com.geecommerce.core.ThreadClearer;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.type.Id;
import com.google.inject.Injector;

public class BootstrapServletContextListener implements ServletContextListener {
    protected static final String INJECTOR_NAME = Injector.class.getName();

    protected ServletContext servletContext = null;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Turn off the custom Guice ClassLoader, so that the CommerceBoard
        // ModuleClassLoader is used instead.
        System.setProperty("guice.custom.loader", "false");

        System.setProperty("mvel2.disable.jit", "true");

        this.servletContext = servletContextEvent.getServletContext();

        AppRegistry.put(RegistryKey.SERVLET_CONTEXT.key(), servletContext);

        SystemInjector.bootstrap();

        App app = SystemInjector.get().getInstance(App.class);

        String bootstrapLogPath = SystemConfig.GET.val(Constant.BOOTSTRAP_LOGPATH);

        ThreadContext.put("log.route.name", "CB-Bootstrap-Log");
        ThreadContext.put("log.path", bootstrapLogPath);

        try {
            // LogManager.getLogManager().readConfiguration(Thread.currentThread().getContextClassLoader().getResourceAsStream("commons-logging.properties"));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        app.init();

        Id testId = app.nextId();

        System.out.println("Generator returned new test-id: " + testId);

        if (testId == null || testId.equals(0)) {
            throw new IllegalStateException("FATAL: Id could not be generated during startup!");
        }

        // Globally set ImageMagick path if it exists.
        String imageMagickPath = SystemConfig.GET.val(SystemConfig.IMAGEMAGICK_PATH);

        if (imageMagickPath != null) {
            try {
                System.out.println("Setting global ImageMagick path: " + imageMagickPath);
                ProcessStarter.setGlobalSearchPath(imageMagickPath);
            } catch (Throwable t) {
                t.printStackTrace();
                throw t;
            }
        }

        Connections.initSystemConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Connections.destroy();

        // Clear Guice from ServletContext
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.removeAttribute(INJECTOR_NAME);

        ThreadClearer.clear();
    }
}
