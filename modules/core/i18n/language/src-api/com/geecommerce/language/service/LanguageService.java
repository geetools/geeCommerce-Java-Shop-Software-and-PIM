package com.geecommerce.language.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.model.Language;

/**
 */
public interface LanguageService extends Service {
    Language getByISO639_1(String code);

    Language getByPhoneCode(String phoneCode);
}
