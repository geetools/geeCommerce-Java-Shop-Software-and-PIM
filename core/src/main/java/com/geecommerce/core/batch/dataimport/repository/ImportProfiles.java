package com.geecommerce.core.batch.dataimport.repository;

import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.service.api.Repository;

public interface ImportProfiles extends Repository {

    ImportProfile havingToken(String token);

}
