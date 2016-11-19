package com.geecommerce.core;

import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;

public class DefaultApplicationContext implements ApplicationContext {
    protected final RequestContext requestCtx;
    protected final Merchant merchant;
    protected UrlRewrite urlRewrite;

    public DefaultApplicationContext(final RequestContext requestCtx, final Merchant merchant) {
        this.requestCtx = requestCtx;
        this.merchant = merchant;
    }

    public DefaultApplicationContext(final ApplicationContext appCtx) {
        App app = App.get();

        if (appCtx != null) {
            if (appCtx.getRequestContext() != null) {
                this.requestCtx = app.inject(RequestContext.class);
                this.requestCtx.fromMap(appCtx.getRequestContext().toMap());
            } else {
                this.requestCtx = null;
            }

            if (appCtx.getMerchant() != null) {
                this.merchant = app.inject(Merchant.class);
                this.merchant.fromMap(appCtx.getMerchant().toMap());
            } else {
                this.merchant = null;
            }

            if (appCtx.getUrlRewrite() != null) {
                this.urlRewrite = app.inject(UrlRewrite.class);
                this.urlRewrite.fromMap(appCtx.getUrlRewrite().toMap());
            }
        } else {
            this.requestCtx = null;
            this.merchant = null;
        }
    }

    @Override
    public final RequestContext getRequestContext() {
        return requestCtx;
    }

    @Override
    public final Merchant getMerchant() {
        return merchant;
    }

    @Override
    public final Store getStore() {
        return merchant.getStoreFor(requestCtx);
    }

    @Override
    public final String getLanguage() {
        return requestCtx == null ? null : requestCtx.getLanguage();
    }

    @Override
    public final String getCountry() {
        return requestCtx == null ? null : requestCtx.getCountry();
    }

    @Override
    public final View getView() {
        return merchant.getViewFor(requestCtx);
    }

    @Override
    public final UrlRewrite getUrlRewrite() {
        return urlRewrite;
    }

    public final void setUrlRewrite(final UrlRewrite urlRewrite) {
        this.urlRewrite = urlRewrite;
    }
}
