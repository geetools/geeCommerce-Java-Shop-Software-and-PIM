package com.geecommerce.retail.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.model.RetailStoreCertificateInformation;
import com.geecommerce.retail.repository.RetailStoreCertificateInformations;
import com.geecommerce.retail.repository.RetailStoreDistances;
import com.geecommerce.retail.repository.RetailStores;
import com.google.inject.Inject;

@Service
public class DefaultRetailStoreService implements RetailStoreService {
    protected final RetailStores retailStores;
    protected final RetailStoreDistances retailStoreDistances;
    protected final LocationService locationService;
    protected final RetailStoreCertificateInformations retailStoreCertificateInformationRepository;

    @Inject
    public DefaultRetailStoreService(RetailStores retailStores, RetailStoreDistances retailStoreDistances, LocationService locationService,
        RetailStoreCertificateInformations retailStoreCertificateInformationRepository) {
        this.retailStores = retailStores;
        this.retailStoreDistances = retailStoreDistances;
        this.locationService = locationService;
        this.retailStoreCertificateInformationRepository = retailStoreCertificateInformationRepository;
    }

    public RetailStore createRetailStore(RetailStore retailStore) {
        return retailStores.add(retailStore);
    }

    public void update(RetailStore retailStore) {
        retailStores.update(retailStore);
    }

    public RetailStore getRetailStore(Id id) {
        return retailStores.findById(RetailStore.class, id);
    }

    public RetailStore getRetailStore(String id2) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(RetailStore.Column.ID2, id2);

        return retailStores.findOne(RetailStore.class, filter);
    }

    public List<RetailStore> getEnabledRetailStores() {
        return retailStores.enabledRetailStores();
    }

    public RetailStore findClosestRetailStore(String zipCode) {
        return retailStoreDistances.closestTo(zipCode);
    }

    public List<RetailStore> findClosestRetailStore(String zipCode, Integer count) {
        return retailStoreDistances.closestTo(zipCode, count);
    }

    public RetailStore find(String name) {
        return retailStores.find(RetailStore.class, "name", name).get(0);
    }

    public RetailStore findByNumber(String storeNumber) {
        return findByNumber(Collections.singletonList(storeNumber)).get(0);
    }

    public List<RetailStore> findByNumber(List<String> storeNumber) {
        return retailStores.find(RetailStore.class, "storeNumber", storeNumber);
    }

    public List<RetailStore> getAllRetailStores() {
        return retailStores.findAll(RetailStore.class);
    }

    public List<RetailStore> getRetailStores() {
        return retailStores.findAll(RetailStore.class).stream().filter(retailStore -> retailStore.getId2() != null).collect(Collectors.toList());
    }

    @Override
    public List<RetailStore> findByNumbers(List<String> numbers) {
        return retailStores.findByNumbers(numbers);
    }

    @Override
    public RetailStore findByZipCode(String zipCode) {
        return retailStores.findByZipCode(zipCode);
    }

    @Override
    public RetailStoreCertificateInformation getCertificateInformation() {
        return retailStoreCertificateInformationRepository.findOne(RetailStoreCertificateInformation.class, Collections.emptyMap());
    }

    public List<RetailStore> getStoresHavingField(String fieldName) {
        return retailStores.getStoresHavingField(fieldName);
    }

    private List<RetailStore> calculateStoreDistances(String zip) {
        LocationService.Location location = locationService.findByZipCode(zip);
        List<RetailStore> result;
        if (location != null) {
            result = getRetailStores();
            result.forEach(retailStore -> retailStore.setDistance(locationService
                .distance(new LocationService.Location(retailStore.attr(RetailStore.Column.LATITUDE).getDouble(), retailStore.attr(RetailStore.Column.LONGITUDE).getDouble()), location)));

            result = result.stream().sorted((retailStore1, retailStore2) -> Double.valueOf(retailStore1.getDistance() - retailStore2.getDistance()).intValue()).collect(Collectors.toList());
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    public List<RetailStore> findClosestByZipCode(String zip, Integer storeCount) {
        return calculateStoreDistances(zip).stream().limit(storeCount).collect(Collectors.toList());
    }

    public RetailStore findClosestByZipCode(String zip) {
        List<RetailStore> stores = findClosestByZipCode(zip, 1);
        return stores.size() > 0 ? stores.get(0) : null;
    }

    public List<RetailStore> findClosestByZipCode(String zip, Predicate<RetailStore> filter) {
        return calculateStoreDistances(zip).stream().filter(filter).collect(Collectors.toList());
    }
}
