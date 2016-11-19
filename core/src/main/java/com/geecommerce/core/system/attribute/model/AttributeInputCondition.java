package com.geecommerce.core.system.attribute.model;

import java.util.List;
import java.util.Set;

import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface AttributeInputCondition extends Model {
    public Id getId();

    public AttributeInputCondition setId(Id id);

    public Id getWhenAttributeId();

    public AttributeInputCondition setWhenAttributeId(Id whenAttributeId);

    public List<Id> getHasOptionIds();

    public AttributeInputCondition setHasOptionIds(List<Id> hasOptionIds);

    public Id getShowAttributeId();

    public AttributeInputCondition setShowAttributeId(Id showAttributeId);

    public String getShowOptionsHavingTag();

    public AttributeInputCondition setShowOptionsHavingTag(String showOptionsHavingTag);

    public Set<ProductType> getApplyToProductTypes();

    public AttributeInputCondition setApplyToProductTypes(Set<ProductType> applyToProductTypes);

    public AttributeInputCondition addApplyToProductTypes(ProductType... productTypes);

    static final class Col {
	public static final String ID = "_id";
	public static final String WHEN_ATTRIBUTE_ID = "when_attr_id";
	public static final String HAS_OPTION_IDS = "has_opt_ids";
	public static final String SHOW_ATTRIBUTE_ID = "show_attr_id";
	public static final String SHOW_OPTIONS_HAVING_TAG = "show_opt_tag";
	public static final String APPLY_TO_PRODUCT_TYPES = "prd_types";

    }
}
