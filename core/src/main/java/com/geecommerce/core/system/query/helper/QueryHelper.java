package com.geecommerce.core.system.query.helper;

import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.query.model.QueryNode;
import org.elasticsearch.index.query.FilterBuilder;

public interface QueryHelper extends Helper {
    public FilterBuilder buildQuery(QueryNode queryNode);

    public QueryNode getQueryNode(Update update, String field);

    public QueryNode combine(QueryNode queryNode1, QueryNode queryNode2);

}
