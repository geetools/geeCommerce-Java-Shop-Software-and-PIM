package com.geecommerce.catalog.product.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.enums.ProductListQueryNodeType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model
public class DefaultProductListQueryNode extends AbstractMultiContextModel implements ProductListQueryNode {
    private static final long serialVersionUID = -6927376841061370646L;

    @Column(Col.TYPE)
    private ProductListQueryNodeType type = null;
    @Column(Col.OPERATOR)
    private String operator = null;
    @Column(Col.NODES)
    private List<ProductListQueryNode> nodes = null;
    @Column(Col.VALUE)
    private AttributeValue value = null;

    @Override
    public ProductListQueryNodeType getType() {
        return type;
    }

    @Override
    public ProductListQueryNode setType(ProductListQueryNodeType type) {
        this.type = type;
        return this;
    }

    @Override
    public List<ProductListQueryNode> getNodes() {
        return nodes;
    }

    @Override
    public ProductListQueryNode setNodes(List<ProductListQueryNode> nodes) {
        this.nodes = nodes;
        return this;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public ProductListQueryNode setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public AttributeValue getValue() {
        return value;
    }

    @Override
    public ProductListQueryNode setValue(AttributeValue value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean isValid() {
        if (type == null)
            return false;

        if (type.equals(ProductListQueryNodeType.BOOLEAN)) {
            if (operator == null || operator.isEmpty())
                return false;
            if (nodes == null || nodes.size() == 0)
                return false;
        }

        if (type.equals(ProductListQueryNodeType.ATTRIBUTE)) {
            if (getValue() == null)
                return false;
            if (getValue().getOptionIds() == null || getValue().getOptionIds().size() == 0)
                return false;
        }
        return true;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        map = normalize(map);
        super.fromMap(map);

        this.type = enum_(ProductListQueryNodeType.class, map.get(Col.TYPE));

        this.operator = str_(map.get(Col.OPERATOR));

        if (map_(map.get(Col.VALUE)) != null && map_(map.get(Col.VALUE)).size() != 0) {
            AttributeValue attribute = app.model(AttributeValue.class);
            attribute.fromMap(map_(map.get(Col.VALUE)));
            this.value = attribute;
        }

        List<Map<String, Object>> nodes = list_(map.get(Col.NODES));
        if (nodes != null) {
            this.nodes = new ArrayList<>();
            for (Map<String, Object> node : nodes) {
                ProductListQueryNode n = app.model(ProductListQueryNode.class);
                n.fromMap(node);
                this.nodes.add(n);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.TYPE, getType().toId());
        map.put(Col.OPERATOR, getOperator());

        if (getValue() != null) {
            map.put(Col.VALUE, getValue().toMap());
        }

        List<Map<String, Object>> nodesList = new ArrayList<>();
        if (getNodes() != null)
            for (ProductListQueryNode node : getNodes()) {
                nodesList.add(node.toMap());
            }
        map.put(Col.NODES, nodesList);
        return map;
    }

}
