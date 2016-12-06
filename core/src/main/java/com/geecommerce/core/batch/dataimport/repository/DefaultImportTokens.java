package com.geecommerce.core.batch.dataimport.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.batch.dataimport.model.ImportToken;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultImportTokens extends AbstractRepository implements ImportTokens {

    @Override
    public ImportToken havingToken(String token) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(ImportToken.Col.TOKEN, token);

        return findOne(ImportToken.class, filter);
    }
}
