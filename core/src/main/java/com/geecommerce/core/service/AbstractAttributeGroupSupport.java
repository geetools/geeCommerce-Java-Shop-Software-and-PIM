package com.geecommerce.core.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.geecommerce.core.system.attribute.model.AttributeGroup;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.repository.AttributeGroups;
import com.google.inject.Inject;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractAttributeGroupSupport extends AbstractAttributeSupport implements AttributeGroupSupport {

    @Inject
    private AttributeGroups attributeGroups;

    public List<AttributeValue> getAttributes(String groupCode) {
        AttributeGroup attributeGroup = attributeGroups.findOne(AttributeGroup.class,
            Collections.singletonMap("code", groupCode));
        return attributeGroup != null ? attributeGroup.getAttributeIds().stream()
            .map(attributeId -> getAttribute(attributeId)).collect(Collectors.toList()) : Collections.emptyList();
    }

    public AttributeGroup getAttributeGroup(String groupCode) {
        AttributeGroup attributeGroup = attributeGroups.findOne(AttributeGroup.class,
            Collections.singletonMap("code", groupCode));
        return attributeGroup;
    }

}
