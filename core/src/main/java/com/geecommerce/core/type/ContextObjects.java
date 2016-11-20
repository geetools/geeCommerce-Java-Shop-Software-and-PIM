package com.geecommerce.core.type;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;

public class ContextObjects {
    @SuppressWarnings("unchecked")
    public static final <T> T findCurrentLanguageOrGlobal(ContextObject<T> ctxObj) {
        if (ctxObj == null)
            return null;

        Object val = ctxObj.getValueFor(language());

        if (val == null)
            val = ctxObj.getGlobalValue();

        return (T) val;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T findCurrentLanguage(ContextObject<T> ctxObj) {
        if (ctxObj == null)
            return null;

        Object val = ctxObj.getValueFor(language());

        return (T) val;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T findCurrentStoreOrGlobal(ContextObject<T> ctxObj) {
        if (ctxObj == null)
            return null;

        Object val = ctxObj.getValueForStore(store().getId());

        if (val == null)
            val = ctxObj.getGlobalValue();

        return (T) val;
    }

    public static final <T> ContextObject<T> global(T value) {
        return new ContextObject<T>(value);
    }

    public static final <T> ContextObject<T> forLanguage(T value, String language) {
        return new ContextObject<T>(language, value);
    }

    public static final <T> ContextObject<T> forLanguageAndCountry(T value, String language, String country) {
        return new ContextObject<T>(language, country, value);
    }

    public static final <T> ContextObject<T> forCurrentLanguageAndCountry(T value) {
        return new ContextObject<T>(language(), country(), value);
    }

    public static final <T> ContextObject<T> forCurrentLanguageAndView(T value) {
        return new ContextObject<T>().addForView(view().getId(), language(), value);
    }

    public static final <T> ContextObject<T> forCurrentLanguageAndStore(T value) {
        return new ContextObject<T>().addForStore(store().getId(), language(), value);
    }

    public static final <T> ContextObject<T> forCurrentLanguageAndMerchant(T value) {
        return new ContextObject<T>().addForMerchant(merchant().getId(), language(), value);
    }

    public static final <T> ContextObject<T> forCurrentLanguage(T value) {
        return new ContextObject<T>(language(), value);
    }

    public static final <T> ContextObject<T> forCurrentView(T value) {
        return new ContextObject<T>().addForView(view().getId(), value);
    }

    public static final <T> ContextObject<T> forCurrentStore(T value) {
        return new ContextObject<T>().addForStore(store().getId(), value);
    }

    public static final <T> ContextObject<T> forCurrentMerchant(T value) {
        return new ContextObject<T>().addForMerchant(merchant().getId(), value);
    }

    private static final String language() {
        return appCtx() == null ? null : appCtx().getLanguage();
    }

    private static final String country() {
        return appCtx() == null ? null : appCtx().getCountry();
    }

    private static final View view() {
        return appCtx() == null ? null : appCtx().getView();
    }

    private static final Store store() {
        return appCtx() == null ? null : appCtx().getStore();
    }

    private static final Merchant merchant() {
        return appCtx() == null ? null : appCtx().getMerchant();
    }

    private static final ApplicationContext appCtx() {
        return App.get().context();
    }
}
