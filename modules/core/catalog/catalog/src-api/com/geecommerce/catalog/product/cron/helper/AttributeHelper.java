package com.geecommerce.catalog.product.cron.helper;

import java.util.List;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;

public interface AttributeHelper extends Helper {
    public List<Attribute> getProductAttributes();

    public List<Attribute> getMandatoryProductAttributes();
}
