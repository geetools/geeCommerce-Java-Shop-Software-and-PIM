package com.geecommerce.retail.service;

/**
 * Created by Andrey on 22.09.2015.
 */
public interface LocationService {

    Location findByZipCode(String zipCode);

    Double distance(Location locationFrom, Location locationTo);

    Double distance(String zipCode, Location location);

    Double distance(String zipFrom, String zipTo);

    class Location {
        private Double latitude;
        private Double longitude;

        public Location(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }

}
