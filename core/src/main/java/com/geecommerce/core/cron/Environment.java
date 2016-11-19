package com.geecommerce.core.cron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.ThreadClearer;
import com.geecommerce.core.app.standalone.helper.AppHelper;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.type.Id;

public class Environment {
    private static Boolean messageBusEnabled = true;
    private static Boolean observersEnabled = true;
    private static Boolean interceptorsEnabled = true;

    public static final void init() {
        AppHelper.systemInit();

        SystemService service = App.get().getSystemService(SystemService.class);

        // Attempt to find a matching request-context by its URL-prefix.
        String urlPrefix = System.getProperty("reqctx.urlprefix");

        if (!Str.isEmpty(urlPrefix)) {
            RequestContext reqCtx = service.findRequestContextsForUrlPrefix(urlPrefix);

            AppHelper.init(reqCtx);
        }
        // Otherwise attempt to find a matching request-context by any of the
        // values
        // merchantId, storeId, viewId, language or country.
        else {
            String merchantId = System.getProperty("merchant.id");
            String storeId = System.getProperty("store.id");
            String viewId = System.getProperty("view.id");
            String lang = System.getProperty("lang.code");
            String country = System.getProperty("country.code");

            Merchant merchant = null;
            Store store = null;
            View view = null;

            if (merchantId != null)
                merchant = service.findMerchantBy(Id.valueOf(merchantId));

            if (storeId != null) {
                if (merchant != null) {
                    store = merchant.getStore(Id.valueOf(storeId));
                } else {
                    merchant = service.findMerchantByStoreId(Id.valueOf(storeId));

                    if (merchant != null)
                        store = merchant.getStore(Id.valueOf(storeId));
                }
            }

            if (viewId != null) {
                if (merchant != null) {
                    view = merchant.getView(Id.valueOf(viewId));
                } else {
                    merchant = service.findMerchantByViewId(Id.valueOf(viewId));

                    if (merchant != null)
                        view = merchant.getView(Id.valueOf(viewId));
                }
            }

            AppHelper.init(merchant, store, view, lang, country);
        }

        Logger log = LogManager.getLogger(Environment.class);

        // ----------------------------------------------------------------------------
        // Make sure that the application has been initialized properly.
        // ----------------------------------------------------------------------------

        ApplicationContext appCtx = App.get().getApplicationContext();

        if (appCtx == null)
            throw new IllegalStateException("ApplicationContext is null after it should have been initialized.");

        RequestContext reqCtx = appCtx.getRequestContext();

        if (reqCtx == null)
            throw new IllegalStateException("RequestContext is null after it should have been initialized.");

        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();
        View view = appCtx.getView();

        System.out.println("Initialized environment with merchant=" + merchant.getCompanyName() + " (" + merchant.getId() + "),  store=" + store.getName() + " (" + store.getId() + "),  view="
            + (view == null ? null : view.getName()) + " ("
            + (view == null ? null : view.getId()) + ", language=" + reqCtx.getLanguage() + ", country=" + reqCtx.getCountry() + ").");
        log.debug("Initialized environment with merchant=" + merchant.getCompanyName() + " (" + merchant.getId() + "),  store=" + store.getName() + " (" + store.getId() + "),  view="
            + (view == null ? null : view.getName()) + " ("
            + (view == null ? null : view.getId()) + ", language=" + reqCtx.getLanguage() + ", country=" + reqCtx.getCountry() + ").");
    }

    public static void enableMessageBus() {
        synchronized (messageBusEnabled) {
            messageBusEnabled = true;
        }
    }

    public static void disableMessageBus() {
        synchronized (messageBusEnabled) {
            messageBusEnabled = false;
        }
    }

    public static boolean isMessageBusEnabled() {
        return messageBusEnabled;
    }

    public static void enableObservers() {
        synchronized (observersEnabled) {
            observersEnabled = true;
        }
    }

    public static void disableObservers() {
        synchronized (observersEnabled) {
            observersEnabled = false;
        }
    }

    public static boolean areObserversEnabled() {
        return observersEnabled;
    }

    public static void enableInterceptors() {
        synchronized (interceptorsEnabled) {
            interceptorsEnabled = true;
        }
    }

    public static void disableInterceptors() {
        synchronized (interceptorsEnabled) {
            interceptorsEnabled = false;
        }
    }

    public static boolean areInterceptorsEnabled() {
        return interceptorsEnabled;
    }

    public static final void cleanUp() {
        // Connections.closeSQLConnection(); // TODO

        ThreadClearer.clear();
    }
}
