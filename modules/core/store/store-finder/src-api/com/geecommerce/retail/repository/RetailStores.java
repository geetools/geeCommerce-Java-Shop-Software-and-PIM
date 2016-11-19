package com.geecommerce.retail.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.retail.model.RetailStore;

public interface RetailStores extends Repository {
    public List<RetailStore> enabledRetailStores();

    public RetailStore havingId2(String id2);

    List<RetailStore> findByNumbers(List<String> numbers);

    RetailStore findByZipCode(String zipCode);

    List<RetailStore> getStoresHavingField(String fieldName);
}
