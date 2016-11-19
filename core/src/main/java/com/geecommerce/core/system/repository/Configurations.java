package com.geecommerce.core.system.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.RequestContext;

public interface Configurations extends Repository {
    public ConfigurationProperty havingKey(String key, Merchant merchant);

    public ConfigurationProperty havingKey(String key, Store store);

    public ConfigurationProperty havingKey(String key, RequestContext reqCtx);

    public ConfigurationProperty havingGlobalKey(String key);

    public ConfigurationProperty havingKey(String key);

    public List<ConfigurationProperty> havingKeysLike(String regex, RequestContext reqCtx);

    public List<ConfigurationProperty> havingKeysLike(String regex, Store store);

    public List<ConfigurationProperty> havingKeysLike(String regex, Merchant merchant);

    public List<ConfigurationProperty> havingGlobalKeysLike(String regex);

    public List<ConfigurationProperty> havingKeysLike(String regex);
}
