package com.geecommerce.checkout.flow.model;


import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.LinkedHashMap;
import java.util.Map;

@Model
public class DefaultCheckoutFlowStep extends AbstractModel implements CheckoutFlowStep {

    private static final long serialVersionUID = -6407783328833601202L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.NAME)
    private String name = null;

    @Column(Col.URI)
    private String uri = null;

    @Column(Col.FLOW_ID)
    private Id flowId = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CheckoutFlowStep setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public CheckoutFlowStep setUri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CheckoutFlowStep setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getFlowId() {
        return flowId;
    }

    @Override
    public CheckoutFlowStep setFlowId(Id flowId) {
        this.flowId = flowId;
        return this;
    }

    @Override
    public CheckoutFlowStep belongsTo(CheckoutFlow flow) {
        if (flow == null || flow.getId() == null)
            throw new IllegalStateException("Checkout flow cannot be null");
        this.flowId = flow.getId();
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Col.ID));
        this.name = str_(map.get(Col.NAME));
        this.uri = str_(map.get(Col.URI));
        this.flowId = id_(map.get(Col.FLOW_ID));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Col.ID, getId());
        m.put(Col.NAME, getName());
        m.put(Col.URI, getUri());

        if (getFlowId() != null) {
            m.put(Col.FLOW_ID, getFlowId());
        }
        return m;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        DefaultCheckoutFlowStep other = (DefaultCheckoutFlowStep) obj;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        if (uri == null) {
            if (other.getUri() != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;

        return true;
    }


    @Override
    public String toString() {
        return "CheckoutFlowStep [id=" + id + ", name=" + name + ", uri=" + uri +"]";
    }


}
