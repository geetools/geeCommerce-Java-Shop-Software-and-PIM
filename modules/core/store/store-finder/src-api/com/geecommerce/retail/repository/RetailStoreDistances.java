package com.geecommerce.retail.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;

import java.util.List;

public interface RetailStoreDistances extends Repository {
    RetailStore closestTo(String zipCode);

    List<RetailStore> closestTo(String zipCode, Integer count);

    Double distance(String zipCode, Id storeId);
}
