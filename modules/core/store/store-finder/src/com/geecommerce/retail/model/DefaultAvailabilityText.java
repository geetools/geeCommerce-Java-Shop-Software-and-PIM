package com.geecommerce.retail.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model("availibility_text")
public class DefaultAvailabilityText extends AbstractMultiContextModel implements AvailabilityText {
    private static final long serialVersionUID = -7264641574025113798L;
    private final String NO_DESC = "no description available";
    private Id id = null;
    private ContextObject<String> text = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public AvailabilityText setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getText() {
	if (text != null)
	    return text.getString();
	return NO_DESC;
    }

    @Override
    public ContextObject<String> getTexts() {
	return text;
    }

    @Override
    public AvailabilityText setText(ContextObject<String> text) {
	this.text = text;
	return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(Column.ID));
	this.text = ctxObj_(map.get(Column.TEXT));

    }

    @Override
    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(Column.ID, getId());
	map.put(Column.TEXT, getTexts());
	return map;

    }
}
