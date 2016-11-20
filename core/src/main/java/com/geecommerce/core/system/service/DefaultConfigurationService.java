package com.geecommerce.core.system.service;

import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.repository.Configurations;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.google.inject.Inject;

public class DefaultConfigurationService implements ConfigurationService {
    @Inject
    protected App app;

    protected final Configurations configurations;

    @Inject
    public DefaultConfigurationService(Configurations configurations) {
        this.configurations = configurations;
    }

    @Override
    public void storeProperty(String key, Object value, RequestContext reqCtx) {
        if (key == null || reqCtx == null)
            throw new NullPointerException(
                "Unable to insert ConfigurationProperty: [key=" + key + ", reqCtx=" + reqCtx + "]");

        ConfigurationProperty cp = findProperty(key, reqCtx);

        if (cp == null) {
            cp = app.model(ConfigurationProperty.class);
            cp.setKey(key).setValue(new ContextObject<>().addOrUpdateForRequestContext(reqCtx.getId(), value));

            configurations.add(cp);
        } else {
            ContextObject<Object> ctxVal = cp.getValue();

            if (ctxVal != null) {
                ctxVal.addOrUpdateForRequestContext(reqCtx.getId(), value);
            } else {
                cp.setValue(new ContextObject<>().addOrUpdateForRequestContext(reqCtx.getId(), value));
            }

            configurations.update(cp);
        }
    }

    @Override
    public void storeProperty(String key, Object value, Store store) {
        if (key == null || store == null)
            throw new NullPointerException(
                "Unable to insert ConfigurationProperty: [key=" + key + ", store=" + store + "]");

        ConfigurationProperty cp = findProperty(key, store);

        if (cp == null) {
            cp = app.model(ConfigurationProperty.class);
            cp.setKey(key).setValue(new ContextObject<>().addOrUpdateForStore(store.getId(), value));

            configurations.add(cp);
        } else {
            ContextObject<Object> ctxVal = cp.getValue();

            if (ctxVal != null) {
                ctxVal.addOrUpdateForStore(store.getId(), value);
            } else {
                cp.setValue(new ContextObject<>().addOrUpdateForStore(store.getId(), value));
            }

            configurations.update(cp);
        }
    }

    @Override
    public void storeProperty(String key, Object value, Merchant merchant) {
        if (key == null || merchant == null)
            throw new NullPointerException(
                "Unable to insert ConfigurationProperty: [key=" + key + ", merchant=" + merchant + "]");

        ConfigurationProperty cp = findProperty(key, merchant);

        if (cp == null) {
            cp = app.model(ConfigurationProperty.class);
            cp.setKey(key).setValue(new ContextObject<>().addOrUpdateForMerchant(merchant.getId(), value));

            configurations.add(cp);
        } else {
            ContextObject<Object> ctxVal = cp.getValue();

            if (ctxVal != null) {
                ctxVal.addOrUpdateForMerchant(merchant.getId(), value);
            } else {
                cp.setValue(new ContextObject<>().addOrUpdateForMerchant(merchant.getId(), value));
            }

            configurations.update(cp);
        }
    }

    @Override
    public void storeProperty(String key, Object value) {
        if (key == null || value == null)
            throw new NullPointerException(
                "Unable to insert ConfigurationProperty: [key=" + key + ", value=" + value + "]");

        ConfigurationProperty cp = findGlobalProperty(key);

        if (cp == null) {
            cp = app.model(ConfigurationProperty.class);
            cp.setKey(key).setValue(ContextObjects.global(value));

            configurations.add(cp);
        } else {
            ContextObject<Object> ctxVal = cp.getValue();

            if (ctxVal != null) {
                ctxVal.addOrUpdateGlobal(value);
            } else {
                cp.setValue(ContextObjects.global(value));
            }

            configurations.update(cp);
        }
    }

    @Override
    public ConfigurationProperty findProperty(String key, RequestContext reqCtx) {
        return configurations.havingKey(key, reqCtx);
    }

    @Override
    public ConfigurationProperty findProperty(String key, Store store) {
        return configurations.havingKey(key, store);
    }

    @Override
    public ConfigurationProperty findProperty(String key, Merchant merchant) {
        return configurations.havingKey(key, merchant);
    }

    @Override
    public ConfigurationProperty findGlobalProperty(String key) {
        return configurations.havingGlobalKey(key);
    }

    @Override
    public ConfigurationProperty findProperty(String key) {
        return configurations.havingKey(key);
    }

    @Override
    public List<ConfigurationProperty> findProperties(String regex, RequestContext reqCtx) {
        return configurations.havingKeysLike(regex, reqCtx);
    }

    @Override
    public List<ConfigurationProperty> findProperties(String regex, Store store) {
        return configurations.havingKeysLike(regex, store);
    }

    @Override
    public List<ConfigurationProperty> findProperties(String regex, Merchant merchant) {
        return configurations.havingKeysLike(regex, merchant);
    }

    @Override
    public List<ConfigurationProperty> findGlobalProperties(String regex) {
        return configurations.havingGlobalKeysLike(regex);
    }

    @Override
    public List<ConfigurationProperty> findProperties(String regex) {
        return configurations.havingKeysLike(regex);
    }
}
