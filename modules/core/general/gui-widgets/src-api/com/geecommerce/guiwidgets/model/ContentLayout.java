package com.geecommerce.guiwidgets.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ContentLayout extends Model {

    public ContentLayout setId(Id id);

    public String getPath();

    public ContentLayout setPath(String path);

    public ContextObject<String> getLabel();

    public ContentLayout setLabel(ContextObject<String> label);

    static final class Col {
        public static final String ID = "_id";
        public static final String LABEL = "label";
        public static final String PATH = "path";
    }

}
