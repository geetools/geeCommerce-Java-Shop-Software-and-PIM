package com.geecommerce.country.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.model.Country;

/**
 */
public interface CountryService extends Service {

    List<Country> getAll();

    Country getByCode(String code);

    Country getByPhoneCode(String phoneCode);

    List<Country> getWithNotEmptyField(String... fields);
}
