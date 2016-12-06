package com.geecommerce.core.batch.dataimport.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.batch.dataimport.model.ImportToken;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultImportProfiles extends AbstractRepository implements ImportProfiles {

    @Override
    public ImportProfile havingToken(String token) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(ImportToken.Col.TOKEN, token);

        return findOne(ImportProfile.class, filter);
    }

}
