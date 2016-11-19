package com.geecommerce.core.system.cpanel.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ControlPanel extends MultiContextModel {
    public Id getId();

    public ControlPanel setId(Id id);

    public ContextObject<String> getLabel();

    public ControlPanel setLabel(ContextObject<String> label);

    public boolean isEnabled();

    public ControlPanel setEnabled(boolean enabled);

    static final class Col {
	public static final String ID = "_id";
	public static final String LABEL = "label";
	public static final String ENABLED = "enabled";
    }
}
