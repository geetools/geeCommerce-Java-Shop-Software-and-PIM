package com.geecommerce.core.system.attribute.repository;

import java.util.List;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;

public interface Attributes extends Repository {
    public List<Attribute> thatBelongTo(AttributeTargetObject targetObject, QueryOptions queryOptions);

    public List<Attribute> thatAreMandatory(AttributeTargetObject targetObject, QueryOptions queryOptions);

    public List<Attribute> thatAreMandatory(AttributeTargetObject targetObject, QueryOptions queryOptions, boolean includeOptOutAttributes);

    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject);

    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject, boolean includeOptOutAttributes);

    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject, QueryOptions queryOptions);

    public List<Attribute> thatAreMandatoryAndEditable(AttributeTargetObject targetObject, QueryOptions queryOptions, boolean includeOptOutAttributes);

    public Attribute havingCode(AttributeTargetObject targetObject, String code);

    public Attribute havingCode2(AttributeTargetObject targetObject, String code2);

    public List<Attribute> havingCodeBeginningWith(AttributeTargetObject targetObject, String codePrefix);

    public List<Attribute> havingCode2BeginningWith(AttributeTargetObject targetObject, String code2Prefix);

    public List<Attribute> forSearchFilter(List<AttributeTargetObject> targetObjects);

    public List<Attribute> forSearchFilter(String... targetObjectCodes);
}
