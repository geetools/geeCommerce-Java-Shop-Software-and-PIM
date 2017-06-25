package com.geecommerce.core.template.model;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.CopySupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Model(collection = "templates")
public class DefaultTemplate extends AbstractMultiContextModel implements Template, CopySupport<Template> {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.TEMPLATE)
    private String template = null;

    @Column(Col.URI)
    private String uri = null;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Template setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public Template setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public Template setUri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public Template setTemplate(String template) {
        this.template = template;
        return this;
    }


    @Override
    public Template makeCopy() {
        Template template = new DefaultTemplate();
        template.setTemplate(getTemplate());
        template.setUri(getUri());
        template.setLabel(getLabel());
        template.setMerchantIds(getMerchantIds());
        template.setStoreIds(getStoreIds());
        template.setRequestContextIds(getRequestContextIds());

        return template;
    }
}
