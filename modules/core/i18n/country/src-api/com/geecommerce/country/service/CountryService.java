package com.geecommerce.country.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.system.model.Language;

import java.util.List;

/**
 */
public interface CountryService extends Service {

    List<Country> getAll();

    Country getByCode(String code);

    Country getByPhoneCode(String phoneCode);

    List<Country> getWithNotEmptyField(String... fields);
}
