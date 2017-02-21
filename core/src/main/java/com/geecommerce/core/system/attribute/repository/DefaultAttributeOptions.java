package com.geecommerce.core.system.attribute.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.pojo.Label;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.mongodb.QueryBuilder;

public class DefaultAttributeOptions extends AbstractRepository implements AttributeOptions {
    @Inject
    protected App app;

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

    @Override
    public List<AttributeOption> havingLabel(Id attributeId, String label, String language, Integer limit, boolean isMatchCase, boolean isMatchExact) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(AttributeOption.Col.ATTRIBUTE_ID, attributeId);

        if (isMatchCase) {
            MongoQueries.addCtxObjFilter(filter, AttributeOption.Col.LABEL,
                Pattern.compile(Str.CARET + label.replaceAll(Str.SLASH, Str.SLASH_ESCAPED) + (isMatchExact ? Str.DOLLAR : Str.EMPTY)), language);
        } else {
            MongoQueries.addCtxObjFilter(filter, AttributeOption.Col.LABEL,
                Pattern.compile(Str.CARET + label.replaceAll(Str.SLASH, Str.SLASH_ESCAPED) + (isMatchExact ? Str.DOLLAR : Str.EMPTY), Pattern.CASE_INSENSITIVE), language);
        }

        return find(AttributeOption.class, filter, limit == null ? null : QueryOptions.builder().limitTo(limit).noCache(true).sortByDesc(GlobalColumn.CREATED_ON).build());
    }

}
