package com.geecommerce.core.system.attribute.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.type.Id;
import com.mongodb.QueryBuilder;

public class DefaultAttributeOptions extends AbstractRepository implements AttributeOptions {
    @Override
    public List<AttributeOption> thatBelongTo(Attribute attribute) {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put(AttributeOption.Col.ATTRIBUTE_ID, attribute.getId());

        List<AttributeOption> options = find(AttributeOption.class, filter,
            QueryOptions.builder().sortBy(AttributeOption.Col.POSITION).build());

        return options;
    }

    @Override
    public List<AttributeOption> thatBelongTo(List<Attribute> attributes) {
        if (attributes != null && attributes.size() > 0) {
            long start = System.currentTimeMillis();

            List<Id> attributeIds = new ArrayList<>();

            for (Attribute attribute : attributes) {
                attributeIds.add(attribute.getId());
            }

            Map<String, Object> filter = new LinkedHashMap<>();

            Map<String, Object> attrInFilter = new LinkedHashMap<>();
            attrInFilter.put("$in", attributeIds);
            filter.put(AttributeOption.Col.ATTRIBUTE_ID, attrInFilter);

            List<AttributeOption> aos = find(AttributeOption.class, filter);

            return aos;
        }

        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findOptionTags(Id attributeId) {
        QueryBuilder query = new QueryBuilder();
        query.put(AttributeOption.Col.ATTRIBUTE_ID).is(attributeId);
        query.put(AttributeOption.Col.TAGS).not().size(0);

        return (List<String>) distinct(AttributeOption.class, query.get().toMap(), AttributeOption.Col.TAGS);
    }
}
