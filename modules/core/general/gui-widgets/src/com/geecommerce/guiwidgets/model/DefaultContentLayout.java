package com.geecommerce.guiwidgets.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model(collection = "content_layouts")
public class DefaultContentLayout extends AbstractModel implements ContentLayout {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.PATH)
    private String path = null;

    @Override
    public ContentLayout setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public ContentLayout setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ContentLayout setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

}
