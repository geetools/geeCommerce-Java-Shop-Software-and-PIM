package com.geecommerce.core.system.cpanel.attribute.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("cpanel_attribute_tab_mapping")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributeTabMapping")
public class DefaultAttributeTabMapping extends AbstractModel implements AttributeTabMapping {
    private static final long serialVersionUID = 5996844565715870568L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.TAB_ID)
    private Id tabId = null;

    @Column(Col.ATTRIBUTE_ID)
    private Id attributeId = null;

    @Column(Col.POSITION)
    private int position = 0;

    public DefaultAttributeTabMapping() {
    }

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public AttributeTabMapping setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getTabId() {
	return tabId;
    }

    @Override
    public AttributeTabMapping setTabId(Id tabId) {
	this.tabId = tabId;
	return this;
    }

    @Override
    public Id getAttributeId() {
	return attributeId;
    }

    @Override
    public AttributeTabMapping setAttributeId(Id attributeId) {
	this.attributeId = attributeId;
	return this;
    }

    @Override
    public int getPosition() {
	return position;
    }

    @Override
    public AttributeTabMapping setPosition(int position) {
	this.position = position;
	return this;
    }

    @Override
    public String toString() {
	return "DefaultAttributeTabMapping [id=" + id + ", tabId=" + tabId + ", attributeId=" + attributeId + ", position=" + position + "]";
    }

}
