package com.geecommerce.core.template.repository;


import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.template.model.Template;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class DefaultTemplates extends AbstractRepository implements Templates{
    @Override
    public Template getByUri(String uri) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Template.Col.URI, uri);

        return multiContextFindOne(Template.class, filter);
    }
}
