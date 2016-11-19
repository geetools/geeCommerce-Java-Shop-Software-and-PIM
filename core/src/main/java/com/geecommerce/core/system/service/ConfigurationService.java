package com.geecommerce.core.system.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.RequestContext;

public interface ConfigurationService extends Service {
    public void storeProperty(String key, Object value, RequestContext reqCtx);

    public void storeProperty(String key, Object value, Store store);

    public void storeProperty(String key, Object value, Merchant merchant);

    public void storeProperty(String key, Object value);

    public ConfigurationProperty findProperty(String key, Merchant merchant);

    public ConfigurationProperty findProperty(String key, Store store);

    public ConfigurationProperty findProperty(String key, RequestContext reqCtx);

    public ConfigurationProperty findGlobalProperty(String key);

    public ConfigurationProperty findProperty(String key);

    public List<ConfigurationProperty> findProperties(String regex, Merchant merchant);

    public List<ConfigurationProperty> findProperties(String regex, Store store);

    public List<ConfigurationProperty> findProperties(String regex, RequestContext reqCtx);

    public List<ConfigurationProperty> findGlobalProperties(String regex);

    public List<ConfigurationProperty> findProperties(String regex);
}
