package com.geecommerce.core.system.query.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;


public interface Query extends Model {
    public Id getId();

    public Query setId(Id id);

    public ContextObject<String> getLabel();

    public Query setLabel(ContextObject<String> label);

    public QueryNode getQueryNode();

    public Query setQueryNode(QueryNode queryNode);

    static final class Col {
        public static final String ID = "_id";
        public static final String NODE = "node";
        public static final String LABEL = "label";
    }
}
