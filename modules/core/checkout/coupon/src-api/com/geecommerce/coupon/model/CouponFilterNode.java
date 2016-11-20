package com.geecommerce.coupon.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.enums.CouponFilterNodeType;

public interface CouponFilterNode extends Model {

    public CouponFilterNodeType getType();

    public CouponFilterNode setType(CouponFilterNodeType type);

    public String getOperation();

    public CouponFilterNode setOperation(String operation);

    public Boolean getOperationValue();

    public CouponFilterNode setOperationValue(Boolean operationValue);

    public List<CouponFilterNode> getNodes();

    public CouponFilterNode setNodes(List<CouponFilterNode> nodes);

    public CouponFilterAttributeType getAttributeType();

    public CouponFilterNode setAttributeType(CouponFilterAttributeType type);

    public String getAttributeCode();

    public CouponFilterNode setAttributeCode(String attributeCode);

    public String getOperator();

    public CouponFilterNode setOperator(String operator);

    public Object getValue();

    public CouponFilterNode setValue(Object value);

    public boolean isValid();

    static final class Col {
        public static final String ID = "_id";
        public static final String NODES = "nodes";
        public static final String TYPE = "type";
        public static final String OPERATION = "operation";
        public static final String OPERATION_VALUE = "operation_val";
        public static final String ATTRIBUTE_TYPE = "attr_type";
        public static final String ATTRIBUTE_CODE = "attr_code";
        public static final String OPERATOR = "operator";
        public static final String VALUE = "val";
    }

}
