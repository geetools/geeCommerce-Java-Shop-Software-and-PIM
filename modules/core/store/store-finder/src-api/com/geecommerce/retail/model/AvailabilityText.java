package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AvailabilityText extends MultiContextModel {
    public Id getId();

    public AvailabilityText setId(Id id);

    public String getText();

    public ContextObject<String> getTexts();

    public AvailabilityText setText(ContextObject<String> text);

    static final class Column {
	public static final String ID = "_id";
	public static final String TEXT = "text";
    }
}
