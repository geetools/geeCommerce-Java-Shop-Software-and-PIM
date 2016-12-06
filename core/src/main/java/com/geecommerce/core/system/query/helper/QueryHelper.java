package com.geecommerce.core.system.query.helper;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.query.model.QueryNode;
import org.elasticsearch.index.query.FilterBuilder;

public interface QueryHelper extends Helper {
    public FilterBuilder buildQuery(QueryNode queryNode);

}
