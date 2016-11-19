package com.geecommerce.core.system.attribute.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.type.Id;

public interface AttributeOptions extends Repository {
    public List<AttributeOption> thatBelongTo(Attribute attribute);

    public List<String> findOptionTags(Id attributeId);

    public List<AttributeOption> thatBelongTo(List<Attribute> attributes);
}
