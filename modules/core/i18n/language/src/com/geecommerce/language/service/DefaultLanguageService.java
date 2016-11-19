package com.geecommerce.language.service;

import com.google.inject.Inject;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.system.model.Language;
import com.geecommerce.country.service.CountryService;
import com.geecommerce.language.repository.LanguageRepository;

/**
 */
@Service
public class DefaultLanguageService implements LanguageService {

    private CountryService countryService;
    private LanguageRepository repository;

    @Inject
    public DefaultLanguageService(CountryService countryService, LanguageRepository repository) {
	this.countryService = countryService;
	this.repository = repository;
    }

    @Override
    public Language getByISO639_1(String code) {
	return repository.getByISO639_1(code);
    }

    @Override
    public Language getByPhoneCode(String phoneCode) {
	Country country = countryService.getByPhoneCode(phoneCode);
	if (country == null || country.getCode() == null)
	    return null;
	return repository.getByISO639_1(country.getCode().toLowerCase());
    }
}
