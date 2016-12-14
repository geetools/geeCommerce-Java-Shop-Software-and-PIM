package com.geecommerce.core.system.query.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.query.QueryNodeType;

import java.util.List;

public interface QueryNode extends Model {

    public QueryNodeType getType();

    public QueryNode setType(QueryNodeType type);

    public String getOperator();

    public QueryNode setOperator(String operator);

    public String getComparator();

    public QueryNode setComparator(String comparator);

    public List<QueryNode> getNodes();

    public QueryNode setNodes(List<QueryNode> nodes);

    public AttributeValue getValue();

    public QueryNode setValue(AttributeValue value);

    public boolean isValid();

    static final class Col {
        public static final String ID = "_id";
        public static final String NODES = "nodes";
        public static final String TYPE = "type";
        public static final String OPERATOR = "operator";
        public static final String COMPARATOR = "comparator";
        public static final String VALUE = "val";
    }

}
