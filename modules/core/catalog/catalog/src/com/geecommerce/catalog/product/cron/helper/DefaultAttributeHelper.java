package com.geecommerce.catalog.product.cron.helper;

import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.google.inject.Inject;

@Helper
public class DefaultAttributeHelper implements AttributeHelper {
    private final AttributeService attributeService;
    private final Attributes attributes;

    @Inject
    public DefaultAttributeHelper(AttributeService attributeService, Attributes attributes) {
        this.attributeService = attributeService;
        this.attributes = attributes;
    }

    @Override
    public List<Attribute> getProductAttributes() {
        return attributes.thatBelongTo(attributeService.getAttributeTargetObject(Product.class),
            QueryOptions.builder().sortBy("code").build());
    }

    @Override
    public List<Attribute> getMandatoryProductAttributes() {
        return attributes.thatAreMandatoryAndEditable(attributeService.getAttributeTargetObject(Product.class),
            QueryOptions.builder().sortBy("code").build());
    }
}
