package com.geecommerce.retail.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.retail.service.LocationService;

public interface Locations extends Repository {
    LocationService.Location findByZipCode(String zipCode);
}
