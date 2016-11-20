package com.geecommerce.coupon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.enums.CouponFilterNodeType;
import com.google.common.collect.Maps;

@Model
public class DefaultCouponFilterNode extends AbstractMultiContextModel implements CouponFilterNode {
    @Column(Col.TYPE)
    private CouponFilterNodeType type = null;
    @Column(Col.OPERATION)
    private String operation = null;
    @Column(Col.OPERATION_VALUE)
    private Boolean operationValue = null;
    @Column(Col.NODES)
    private List<CouponFilterNode> nodes = null;
    @Column(Col.ATTRIBUTE_TYPE)
    private CouponFilterAttributeType attributeType = null;
    @Column(Col.ATTRIBUTE_CODE)
    private String attributeCode = null;
    @Column(Col.OPERATOR)
    private String operator = null;
    @Column(Col.VALUE)
    private Object value = null;

    @Override
    public CouponFilterNodeType getType() {
        return type;
    }

    @Override
    public CouponFilterNode setType(CouponFilterNodeType type) {
        this.type = type;
        return this;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public CouponFilterNode setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    @Override
    public Boolean getOperationValue() {
        return operationValue;
    }

    @Override
    public CouponFilterNode setOperationValue(Boolean operationValue) {
        this.operationValue = operationValue;
        return this;
    }

    @Override
    public List<CouponFilterNode> getNodes() {
        return nodes;
    }

    @Override
    public CouponFilterNode setNodes(List<CouponFilterNode> nodes) {
        this.nodes = nodes;
        return this;
    }

    @Override
    public CouponFilterAttributeType getAttributeType() {
        return attributeType;
    }

    @Override
    public CouponFilterNode setAttributeType(CouponFilterAttributeType type) {
        this.attributeType = type;
        return this;
    }

    @Override
    public String getAttributeCode() {
        return attributeCode;
    }

    @Override
    public CouponFilterNode setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
        return this;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public CouponFilterNode setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public CouponFilterNode setValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean isValid() {
        if (type == null)
            return false;

        if (type.equals(CouponFilterNodeType.BOOLEAN_OPERATION) || type.equals(CouponFilterNodeType.FOUND)
            || type.equals(CouponFilterNodeType.NOT_FOUND)) {
            if (operation == null || operation.isEmpty())
                return false;
            if (nodes == null || nodes.size() == 0)
                return false;
        }

        if (type.equals(CouponFilterNodeType.FIlTER_ATTRIBUTE_OPERATION)) {
            if (attributeCode == null || attributeCode.isEmpty())
                return false;
            if (attributeType == null)
                return false;
            if (operator == null || operator.isEmpty())
                return false;
            if (value == null)
                return false;
            if (value instanceof String) {
                if (((String) value).isEmpty())
                    return false;
            }

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

        this.type = enum_(CouponFilterNodeType.class, map.get(Col.TYPE));

        this.operation = str_(map.get(Col.OPERATION));
        if (type.equals(CouponFilterNodeType.BOOLEAN_OPERATION)) {
            this.operationValue = bool_(map.get(Col.OPERATION_VALUE), true);
        }

        this.value = map.get(Col.VALUE);

        this.attributeType = enum_(CouponFilterAttributeType.class, map.get(Col.ATTRIBUTE_TYPE));

        this.attributeCode = str_(map.get(Col.ATTRIBUTE_CODE));
        this.operator = str_(map.get(Col.OPERATOR));

        List<Map<String, Object>> nodes = list_(map.get(Col.NODES));
        if (nodes != null) {
            this.nodes = new ArrayList<>();
            for (Map<String, Object> node : nodes) {
                CouponFilterNode n = app.model(CouponFilterNode.class);
                n.fromMap(node);
                this.nodes.add(n);
            }
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.TYPE, getType().toId());
        map.put(Col.OPERATION, getOperation());
        if (getType().equals(CouponFilterNodeType.BOOLEAN_OPERATION)) {
            map.put(Col.OPERATION_VALUE, getOperationValue());
        }
        if (getAttributeType() != null)
            map.put(Col.ATTRIBUTE_TYPE, getAttributeType().toId());
        map.put(Col.ATTRIBUTE_CODE, getAttributeCode());
        map.put(Col.OPERATOR, getOperator());
        map.put(Col.VALUE, getValue());

        List<Map<String, Object>> nodesList = new ArrayList<>();
        if (getNodes() != null)
            for (CouponFilterNode node : getNodes()) {
                nodesList.add(node.toMap());
            }
        map.put(Col.NODES, nodesList);
        return map;
    }

}
