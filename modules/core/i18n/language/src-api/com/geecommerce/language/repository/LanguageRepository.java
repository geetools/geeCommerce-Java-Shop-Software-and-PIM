package com.geecommerce.language.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.model.Language;

/**
 */
public interface LanguageRepository extends Repository {
    Language getByISO639_1(String code);

    Language getByPhoneCode(String phoneCode);
}
