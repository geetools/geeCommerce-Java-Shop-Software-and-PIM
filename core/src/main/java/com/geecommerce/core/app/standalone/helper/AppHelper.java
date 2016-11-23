package com.geecommerce.core.app.standalone.helper;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.ThreadContext;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.DefaultApplicationContext;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.repository.RequestContexts;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.type.Id;
import com.geemodule.Geemodule;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleLoader;

public class AppHelper {
    public static final List<RequestContext> getRequestContext(String host) {
        return ServiceHelper.getSystemService().findRequestContextsForHost(host);
    }

    public static final RequestContext getRequestContext(Id reqCtxId) {
        return ServiceHelper.getSystemService().getRequestContext(reqCtxId);
    }

    public static final List<RequestContext> getRequestContexts() {
        RequestContexts requestContexts = SystemInjector.get().getInstance(RequestContexts.class);
        return requestContexts.findAll(RequestContext.class);
    }

    public static final void systemInit() {
        String logPath = System.getProperty("log.path");

        if (logPath == null)
            logPath = "log";

        ThreadContext.put("log.path", logPath);

        SystemInjector.bootstrap();
        
        Connections.initSystemConnection();
    }

    public static final void init(RequestContext reqCtx) {
        initApplicationContext(reqCtx);

        initConnections();

        initModules();
    }

    public static final void init(Merchant m) {
        initApplicationContext(m);

        initConnections();

        initModules();
    }

    public static final void init(Merchant m, Store s) {
        initApplicationContext(m, s);

        initConnections();

        initModules();
    }

    public static final void init(Merchant m, Store s, View v) {
        initApplicationContext(m, s, v);

        initConnections();

        initModules();
    }

    public static final void init(Merchant m, Store s, View v, String language) {
        initApplicationContext(m, s, v, language);

        initConnections();

        initModules();
    }

    public static final void init(Merchant m, Store s, View v, String language, String country) {
        initApplicationContext(m, s, v, language, country);

        initConnections();

        initModules();
    }

    private static void initConnections() {
        Connections.init();
    }

    private static final void initApplicationContext(RequestContext reqCtx) {
        if (reqCtx == null)
            throw new IllegalStateException(
                "Unable to initialize application context because the request-context is null");

        SystemService systemService = App.get().systemService(SystemService.class);

        Merchant merchant = systemService.findMerchantBy(reqCtx.getMerchantId());

        ApplicationContext appCtx = new DefaultApplicationContext(reqCtx, merchant);

        App.get().setApplicationContext(appCtx);
    }

    private static final void initApplicationContext(Merchant m) {
        SystemService systemService = App.get().systemService(SystemService.class);

        RequestContext reqCtx = systemService.findRequestContext(m, null, null, null, null);

        if (reqCtx == null)
            throw new IllegalStateException(
                "Unable to initialize application context as no request-context could be found for the prameters  [merchant="
                    + (m == null ? null : m.getId()) + ", store=" + null + ", language=" + null + ", country="
                    + null + ", view=" + null + "]");

        Merchant merchant = systemService.findMerchantBy(reqCtx.getMerchantId());

        ApplicationContext appCtx = new DefaultApplicationContext(reqCtx, merchant);

        App.get().setApplicationContext(appCtx);
    }

    private static final void initApplicationContext(Merchant m, Store s) {
        SystemService systemService = App.get().systemService(SystemService.class);

        RequestContext reqCtx = systemService.findRequestContext(m, s, null, null, null);

        if (reqCtx == null)
            throw new IllegalStateException(
                "Unable to initialize application context as no request-context could be found for the prameters  [merchant="
                    + (m == null ? null : m.getId()) + ", store=" + (s == null ? null : s.getId())
                    + ", language=" + null + ", country=" + null + ", view=" + null + "]");

        Merchant merchant = systemService.findMerchantBy(reqCtx.getMerchantId());

        ApplicationContext appCtx = new DefaultApplicationContext(reqCtx, merchant);

        App.get().setApplicationContext(appCtx);
    }

    private static final void initApplicationContext(Merchant m, Store s, View v) {
        SystemService systemService = App.get().systemService(SystemService.class);

        RequestContext reqCtx = systemService.findRequestContext(m, s, null, null, v);

        if (reqCtx == null)
            throw new IllegalStateException(
                "Unable to initialize application context as no request-context could be found for the prameters  [merchant="
                    + (m == null ? null : m.getId()) + ", store=" + (s == null ? null : s.getId())
                    + ", language=" + null + ", country=" + null + ", view=" + (v == null ? null : v.getId())
                    + "]");

        Merchant merchant = systemService.findMerchantBy(reqCtx.getMerchantId());

        ApplicationContext appCtx = new DefaultApplicationContext(reqCtx, merchant);

        App.get().setApplicationContext(appCtx);
    }

    private static final void initApplicationContext(Merchant m, Store s, View v, String language) {
        SystemService systemService = App.get().systemService(SystemService.class);

        RequestContext reqCtx = systemService.findRequestContext(m, s, language, null, v);

        if (reqCtx == null)
            throw new IllegalStateException(
                "Unable to initialize application context as no request-context could be found for the prameters  [merchant="
                    + (m == null ? null : m.getId()) + ", store=" + (s == null ? null : s.getId())
                    + ", language=" + language + ", country=" + null + ", view="
                    + (v == null ? null : v.getId()) + "]");

        Merchant merchant = systemService.findMerchantBy(reqCtx.getMerchantId());

        ApplicationContext appCtx = new DefaultApplicationContext(reqCtx, merchant);

        App.get().setApplicationContext(appCtx);
    }

    private static final void initApplicationContext(Merchant m, Store s, View v, String language, String country) {
        SystemService systemService = App.get().systemService(SystemService.class);

        RequestContext reqCtx = systemService.findRequestContext(m, s, language, country, v);

        if (reqCtx == null)
            throw new IllegalStateException(
                "Unable to initialize application context as no request-context could be found for the prameters  [merchant="
                    + (m == null ? null : m.getId()) + ", store=" + (s == null ? null : s.getId())
                    + ", language=" + language + ", country=" + country + ", view="
                    + (v == null ? null : v.getId()) + "]");

        Merchant merchant = systemService.findMerchantBy(reqCtx.getMerchantId());

        ApplicationContext appCtx = new DefaultApplicationContext(reqCtx, merchant);

        App.get().setApplicationContext(appCtx);
    }

    private static final void initModules() {
        ApplicationContext appCtx = App.get().context();

        ModuleLoader loader = Geemodule.createModuleLoader(appCtx.getMerchant().getModulesPath());

        System.out.println(
            "!!!!! INITIAIZED MODULE LOADER ::::: " + loader + " - " + appCtx.getMerchant().getModulesPath());

        Collection<Module> modules = loader.getLoadedModules();

        if (modules != null && modules.size() > 0) {
            for (Module module : modules) {
                System.out.println("Loaded module: " + module.toUniqueId());
            }

            App.get().setModuleLoader(loader);
        }
    }
}
