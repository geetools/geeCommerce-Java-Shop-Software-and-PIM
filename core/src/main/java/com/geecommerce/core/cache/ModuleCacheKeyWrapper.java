package com.geecommerce.core.cache;

import java.io.Serializable;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.type.Id;

public class ModuleCacheKeyWrapper<T> implements Serializable {
    private static final long serialVersionUID = 7328777097688357786L;

    private Id merchantId = null;
    private T key = null;

    public ModuleCacheKeyWrapper(T key) {
        this.key = key;

        ApplicationContext appCtx = App.get().context();

        if (appCtx != null && appCtx.getMerchant() != null) {
            Merchant m = appCtx.getMerchant();
            merchantId = m.getId();
        } else {
            merchantId = Id.valueOf(0);
        }
    }

    public Id getMerchantId() {
        return merchantId;
    }

    public T getKey() {
        return key;
    }

    public boolean isInContext() {
        ApplicationContext appCtx = App.get().context();

        if (appCtx != null && appCtx.getMerchant() != null) {
            Merchant m = appCtx.getMerchant();
            return m.getId().equals(merchantId);
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((merchantId == null) ? 0 : merchantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModuleCacheKeyWrapper other = (ModuleCacheKeyWrapper) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (merchantId == null) {
            if (other.merchantId != null)
                return false;
        } else if (!merchantId.equals(other.merchantId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ModuleCacheKeyWrapper [merchantId=" + merchantId + ", key=" + key + "]";
    }
}
