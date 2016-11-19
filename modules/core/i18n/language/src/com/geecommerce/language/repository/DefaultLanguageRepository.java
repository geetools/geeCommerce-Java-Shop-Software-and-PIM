package com.geecommerce.language.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.system.model.Language;

/**
 */
@Repository
public class DefaultLanguageRepository extends AbstractRepository implements LanguageRepository {
    @Override
    public Language getByISO639_1(String code) {
        Map<String, Object> filter = new HashMap<String, Object>(1);
        filter.put(Language.Col.ISO639_1, code);
        return findOne(Language.class, filter);
    }

    @Override
    public Language getByPhoneCode(String phoneCode) {
        return null;
    }
}
