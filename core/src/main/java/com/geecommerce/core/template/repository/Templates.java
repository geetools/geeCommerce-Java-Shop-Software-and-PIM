package com.geecommerce.core.template.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.template.model.Template;

public interface Templates extends Repository {

    public Template getByUri(String uri);

}
