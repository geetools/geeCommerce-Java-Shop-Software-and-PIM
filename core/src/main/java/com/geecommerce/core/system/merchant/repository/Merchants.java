package com.geecommerce.core.system.merchant.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.type.Id;

public interface Merchants extends Repository {
    public Merchant havingStoreId(Id storeId);

    public Merchant havingViewId(Id viewId);
}
