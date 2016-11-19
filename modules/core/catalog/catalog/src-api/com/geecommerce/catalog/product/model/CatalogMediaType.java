package com.geecommerce.catalog.product.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface CatalogMediaType extends MultiContextModel {
    public Id getId();

    public CatalogMediaType setId(Id id);

    public String getKey();

    public CatalogMediaType setKey(String key);

    public ContextObject<String> getLabel();

    public CatalogMediaType setLabel(ContextObject<String> label);

    public int getPriority();

    public CatalogMediaType setPriority(int priority);

    static final class Col {
	public static final String ID = "_id";
	public static final String KEY = "key";
	public static final String LABEL = "label";
	public static final String PRIORITY = "pri";
    }
}
