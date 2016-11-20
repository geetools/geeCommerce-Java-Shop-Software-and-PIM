package com.geecommerce.catalog.product.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ProductLinkType extends Model {
    public Id getId();

    public ProductLinkType setId(Id id);

    public String getCode();

    public ProductLinkType setCode(String code);

    public ContextObject<String> getLabels();

    public String getLabel();

    public ProductLinkType setLabel(ContextObject<String> label);

    static final class Column {
        public static final String ID = "_id";
        public static final String CODE = "code";
        public static final String LABEL = "label";
    }
}
