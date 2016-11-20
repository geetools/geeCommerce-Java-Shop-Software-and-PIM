package com.geecommerce.country.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.model.Country;

/**
 */
public interface CountryRepository extends Repository {

    List<Country> getAll();

    Country getByCode(String code);

    Country getByPhoneCode(String code);

    List<Country> getWithNotEmptyField(String... fields);
}
