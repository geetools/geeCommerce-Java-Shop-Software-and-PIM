package com.geecommerce.guiwidgets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.guiwidgets.model.Magazine;
import com.geecommerce.guiwidgets.model.Magazine.Col;

@Repository
public class DefaultMagazines extends AbstractRepository implements Magazines {
    @Override
    public List<Magazine> enabledMagazines() {
        Map<String, Object> filter = new HashMap<>();
        MongoQueries.addCtxObjFilter(filter, Col.ENABLED, true);

        return find(Magazine.class, filter);
    }
}
