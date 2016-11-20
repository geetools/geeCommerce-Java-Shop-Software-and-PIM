package com.geecommerce.retail.service;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.retail.repository.Locations;
import com.google.inject.Inject;

/**
 * Created by Andrey on 22.09.2015.
 */
@Service
public class DefaultLocationService implements LocationService {

    private final static double EARTH_RADIUS = 6372795;
    private final Locations locations;

    @Inject
    public DefaultLocationService(Locations locations) {
        this.locations = locations;
    }

    public Location findByZipCode(String zipCode) {
        return locations.findByZipCode(zipCode);
    }

    /**
     *
     * @param locationFrom
     * @param locationTo
     * @return distance in meters
     */
    public Double distance(Location locationFrom, Location locationTo) {

        double lat1 = Math.toRadians(locationFrom.getLatitude());
        double lat2 = Math.toRadians(locationTo.getLatitude());
        double long1 = Math.toRadians(locationFrom.getLongitude());
        double long2 = Math.toRadians(locationTo.getLongitude());

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;

        double ad = Math.atan2(y, x);
        double dist = ad * EARTH_RADIUS;

        return dist;
    }

    public Double distance(String zipCode, Location location) {
        Location locationByZip = findByZipCode(zipCode);
        return distance(locationByZip, location);
    }

    public Double distance(String zipFrom, String zipTo) {
        return distance(findByZipCode(zipFrom), findByZipCode(zipTo));
    }

}
