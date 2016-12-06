package com.geecommerce.core.batch.dataimport.repository;

import com.geecommerce.core.batch.dataimport.model.ImportToken;
import com.geecommerce.core.service.api.Repository;

public interface ImportTokens extends Repository {

    ImportToken havingToken(String token);

}
