package com.geecommerce.country.service;

import com.google.inject.Inject;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.system.model.Language;
import com.geecommerce.country.repository.CountryRepository;

import java.util.List;

/**
 */
@Service
public class DefaultCountryService implements CountryService {
    private final CountryRepository repository;

    @Inject
    public DefaultCountryService(CountryRepository repository) {
	this.repository = repository;
    }

    @Override
    public List<Country> getAll() {
	return repository.getAll();
    }

    @Override
    public Country getByCode(String code) {
	return repository.getByCode(code);
    }

    @Override
    public Country getByPhoneCode(String phoneCode) {
	return repository.getByPhoneCode(phoneCode);
    }

    @Override
    public List<Country> getWithNotEmptyField(String... fields) {
	return repository.getWithNotEmptyField(fields);
    }
}
