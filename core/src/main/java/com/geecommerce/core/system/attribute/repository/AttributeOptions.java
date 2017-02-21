package com.geecommerce.core.system.attribute.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.type.Id;

public interface AttributeOptions extends Repository {
    List<AttributeOption> thatBelongTo(Attribute attribute);

    List<String> findOptionTags(Id attributeId);

    List<AttributeOption> thatBelongTo(List<Attribute> attributes);

    List<AttributeOption> havingLabel(Id attributeId, String label, String language, Integer limit, boolean isMatchCase, boolean isMatchExact);
}
