package com.geecommerce.core.system.query.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public class DefaultQuery extends AbstractModel implements Query {
    @Override
    public Id getId() {
        return null;
    }

    @Override
    public Query setId(Id id) {
        return null;
    }

    @Override
    public ContextObject<String> getLabel() {
        return null;
    }

    @Override
    public Query setLabel(ContextObject<String> label) {
        return null;
    }

    @Override
    public QueryNode getQueryNode() {
        return null;
    }

    @Override
    public Query setQueryNode(QueryNode queryNode) {
        return null;
    }
}
