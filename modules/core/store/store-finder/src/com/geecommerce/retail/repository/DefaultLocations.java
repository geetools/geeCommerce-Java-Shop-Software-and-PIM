package com.geecommerce.retail.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.retail.model.GeoInfo;
import com.geecommerce.retail.service.LocationService;

@Repository
public class DefaultLocations extends AbstractRepository implements Locations {
    @Override
    public LocationService.Location findByZipCode(String zipCode) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("zip", zipCode);
        GeoInfo geoInfo = findOne(GeoInfo.class, filter);
        return geoInfo != null ? new LocationService.Location(geoInfo.getLatitude(), geoInfo.getLongitude()) : null;
    }
}
