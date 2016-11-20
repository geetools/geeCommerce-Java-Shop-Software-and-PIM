package com.geecommerce.retail.service;

import java.util.List;
import java.util.function.Predicate;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.model.RetailStoreCertificateInformation;

public interface RetailStoreService extends Service {
    RetailStore createRetailStore(RetailStore retailStore);

    void update(RetailStore retailStore);

    RetailStore getRetailStore(Id id);

    RetailStore getRetailStore(String id2);

    List<RetailStore> getEnabledRetailStores();

    RetailStore findClosestRetailStore(String zipCode);

    List<RetailStore> findClosestRetailStore(String zipCode, Integer count);

    List<RetailStore> getAllRetailStores();

    List<RetailStore> getRetailStores();

    RetailStore find(String name);

    List<RetailStore> findByNumbers(List<String> numbers);

    RetailStore findByZipCode(String zipCode);

    RetailStoreCertificateInformation getCertificateInformation();

    List<RetailStore> getStoresHavingField(String fieldName);

    RetailStore findClosestByZipCode(String zip);

    List<RetailStore> findClosestByZipCode(String zip, Integer storeCount);

    List<RetailStore> findClosestByZipCode(String zip, Predicate<RetailStore> filter);
}
