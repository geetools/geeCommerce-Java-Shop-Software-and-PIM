package com.geecommerce.core.template.model;


import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface Template extends MultiContextModel {

    public Id getId();

    public Template setId(Id id);

    public ContextObject<String> getLabel();

    public Template setLabel(ContextObject<String> label);

    public String getUri();

    public Template setUri(String uri);

    public String getTemplate();

    public Template setTemplate(String template);

    static final class Col {
        public static final String ID = "_id";
        public static final String LABEL = "label";
        public static final String TEMPLATE = "template";
        public static final String URI = "uri";
    }
}
