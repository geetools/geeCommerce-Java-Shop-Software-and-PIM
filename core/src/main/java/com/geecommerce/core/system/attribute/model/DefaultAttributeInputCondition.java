package com.geecommerce.core.system.attribute.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("attribute_input_conditions")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributeInputCondition")
public class DefaultAttributeInputCondition extends AbstractModel implements AttributeInputCondition {
    private static final long serialVersionUID = 5996844565715870568L;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.WHEN_ATTRIBUTE_ID)
    protected Id whenAttributeId = null;

    @Column(Col.HAS_OPTION_IDS)
    protected List<Id> hasOptionIds = null;

    @Column(Col.SHOW_ATTRIBUTE_ID)
    protected Id showAttributeId = null;

    @Column(Col.SHOW_OPTIONS_HAVING_TAG)
    protected String showOptionsHavingTag = null;

    @Column(Col.APPLY_TO_PRODUCT_TYPES)
    protected Set<ProductType> applyToProductTypes = null;

    public DefaultAttributeInputCondition() {
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AttributeInputCondition setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getWhenAttributeId() {
        return whenAttributeId;
    }

    @Override
    public AttributeInputCondition setWhenAttributeId(Id whenAttributeId) {
        this.whenAttributeId = whenAttributeId;
        return this;
    }

    @Override
    public List<Id> getHasOptionIds() {
        return hasOptionIds;
    }

    @Override
    public AttributeInputCondition setHasOptionIds(List<Id> hasOptionIds) {
        this.hasOptionIds = hasOptionIds;
        return this;
    }

    @Override
    public Id getShowAttributeId() {
        return showAttributeId;
    }

    @Override
    public AttributeInputCondition setShowAttributeId(Id showAttributeId) {
        this.showAttributeId = showAttributeId;
        return this;
    }

    @Override
    public String getShowOptionsHavingTag() {
        return showOptionsHavingTag;
    }

    @Override
    public AttributeInputCondition setShowOptionsHavingTag(String showOptionsHavingTag) {
        this.showOptionsHavingTag = showOptionsHavingTag;
        return this;
    }

    @Override
    public Set<ProductType> getApplyToProductTypes() {
        return applyToProductTypes;
    }

    @Override
    public AttributeInputCondition setApplyToProductTypes(Set<ProductType> applyToProductTypes) {
        this.applyToProductTypes = applyToProductTypes;
        return this;
    }

    @Override
    public AttributeInputCondition addApplyToProductTypes(ProductType... productTypes) {
        if (this.applyToProductTypes == null) {
            this.applyToProductTypes = new HashSet<ProductType>();
        }

        this.applyToProductTypes.addAll(Arrays.asList(productTypes));

        return this;
    }

    @Override
    public String toString() {
        return "DefaultAttributeInputCondition [id=" + id + ", whenAttributeId=" + whenAttributeId + ", hasOptionIds="
            + hasOptionIds + ", showAttributeId=" + showAttributeId + ", showOptionsHavingTag="
            + showOptionsHavingTag + ", applyToProductTypes=" + applyToProductTypes + "]";
    }
}
