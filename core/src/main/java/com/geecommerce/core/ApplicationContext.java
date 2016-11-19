package com.geecommerce.core;

import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.UrlRewrite;

public interface ApplicationContext {
    public RequestContext getRequestContext();

    public Merchant getMerchant();

    public Store getStore();

    public String getLanguage();

    public String getCountry();

    public View getView();

    public UrlRewrite getUrlRewrite();
}
