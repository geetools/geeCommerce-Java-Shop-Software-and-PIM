package com.geecommerce.catalog.product.model;

import java.util.List;

import com.geecommerce.catalog.product.enums.ProductListQueryNodeType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;

public interface ProductListQueryNode extends Model {

    public ProductListQueryNodeType getType();

    public ProductListQueryNode setType(ProductListQueryNodeType type);

    public String getOperator();

    public ProductListQueryNode setOperator(String operator);

    public List<ProductListQueryNode> getNodes();

    public ProductListQueryNode setNodes(List<ProductListQueryNode> nodes);

    public AttributeValue getValue();

    public ProductListQueryNode setValue(AttributeValue value);

    public boolean isValid();

    static final class Col {
        public static final String ID = "_id";
        public static final String NODES = "nodes";
        public static final String TYPE = "type";
        public static final String OPERATOR = "operator";
        public static final String VALUE = "val";
    }

}
