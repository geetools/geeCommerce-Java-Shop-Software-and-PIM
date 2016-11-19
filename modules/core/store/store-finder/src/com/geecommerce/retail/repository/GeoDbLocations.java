package com.geecommerce.retail.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mysql.MySqlDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.model.GeoCoordinate;
import com.geecommerce.retail.model.GeoText;
import com.geecommerce.retail.service.LocationService;
import com.google.inject.Inject;

//@Repository
public class GeoDbLocations extends AbstractRepository implements Locations {
    private final MySqlDao sqlDao;

    private final static Integer GEO_ZIP_CODE_TYPE = 500300000;

    @Inject
    public GeoDbLocations(MySqlDao sqlDao) {
        this.sqlDao = sqlDao;
    }

    @Override
    public Dao dao() {
        return sqlDao;
    }

    @Override
    public LocationService.Location findByZipCode(String zipCode) { // TODO one
                                                                    // query
                                                                    // better
        LocationService.Location location = null;

        Map<String, Object> filter1 = new HashMap<>();
        filter1.put(GeoText.Column.TYPE, GEO_ZIP_CODE_TYPE);
        filter1.put(GeoText.Column.VALUE, zipCode);
        List<GeoText> locationIds = sqlDao.find(GeoText.class, filter1);
        Id locationId = locationIds != null && !locationIds.isEmpty() ? locationIds.get(0).getId() : null;

        if (locationId != null) {
            Map<String, Object> filter = new HashMap<>();
            filter.put(GeoCoordinate.Column.ID, locationId.intValue());
            List<GeoCoordinate> coordinates = sqlDao.find(GeoCoordinate.class, filter);
            location = coordinates != null && !coordinates.isEmpty() ? new LocationService.Location(coordinates.get(0).getLatitude(), coordinates.get(0).getLongitude()) : null;
        }
        return location;
    }
}
