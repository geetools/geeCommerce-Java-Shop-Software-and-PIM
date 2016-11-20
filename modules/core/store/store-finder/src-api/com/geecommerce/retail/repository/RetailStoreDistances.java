package com.geecommerce.retail.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;

public interface RetailStoreDistances extends Repository {
    RetailStore closestTo(String zipCode);

    List<RetailStore> closestTo(String zipCode, Integer count);

    Double distance(String zipCode, Id storeId);
}
