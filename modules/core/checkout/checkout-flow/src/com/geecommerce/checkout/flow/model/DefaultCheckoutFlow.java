package com.geecommerce.checkout.flow.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.type.Id;

@Model("checkout_flows")
public class DefaultCheckoutFlow extends AbstractModel implements CheckoutFlow {

    private static final long serialVersionUID = -6407783328833601203L;

    @Column(Col.ID)
    private Id id = null;

    @Column(GlobalColumn.REQUEST_CONTEXT_ID)
    private Set<Id> requestContextIds = null;

    @Column(GlobalColumn.ENABLED)
    private Boolean enabled = false;

    @Column(Col.FLOW_ACTIVE)
    private Boolean active = false;

    @Column(Col.FLOW_NAME)
    private String name = null;

    @Column(Col.FLOW_DESCR)
    private String description = null;

    @Column(Col.FLOW_BASE_URI)
    private String baseUri = null;

    @Column(Col.FLOW_STEPS)
    private List<CheckoutFlowStep> steps = new LinkedList<>();

    @Override
    public Set<Id> getRequestContextIds() {
        return requestContextIds;
    }

    @Override
    public CheckoutFlow setRequestContextIds(Set<Id> requestContextIds) {
        this.requestContextIds = requestContextIds;
        return this;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public CheckoutFlow setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public CheckoutFlow setActive(Boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CheckoutFlow setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public CheckoutFlow setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public List<CheckoutFlowStep> getSteps() {
        if (steps == null)
            steps = new LinkedList<>();
        return steps;
    }

    @Override
    public CheckoutFlow setSteps(List<CheckoutFlowStep> steps) {
        this.steps = steps;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public CheckoutFlow setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    @Override
    public CheckoutFlow setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Col.ID));
        this.name = str_(map.get(Col.FLOW_NAME));
        this.description = str_(map.get(Col.FLOW_DESCR));
        this.enabled = bool_(map.get(GlobalColumn.ENABLED));
        this.active = bool_(map.get(Col.FLOW_ACTIVE));
        this.requestContextIds = set_(GlobalColumn.REQUEST_CONTEXT_ID);
        this.baseUri = str_(map.get(Col.FLOW_BASE_URI));

        List<Map<String, Object>> stepList = list_(map.get(Col.FLOW_STEPS));
        if (stepList != null) {
            this.steps = new ArrayList<>();
            for (Map<String, Object> step : stepList) {
                CheckoutFlowStep fs = app.model(CheckoutFlowStep.class);
                fs.fromMap(step);
                this.steps.add(fs);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Col.ID, getId());
        m.put(Col.FLOW_NAME, getName());
        m.put(Col.FLOW_DESCR, getDescription());
        m.put(Col.FLOW_STEPS, getSteps());
        m.put(GlobalColumn.REQUEST_CONTEXT_ID, getRequestContextIds());
        m.put(GlobalColumn.ENABLED, getEnabled());
        m.put(Col.FLOW_ACTIVE, getActive());
        m.put(Col.FLOW_BASE_URI, getBaseUri());

        List<Map<String, Object>> stepList = new ArrayList<>();
        if (getSteps() != null) {
            stepList.addAll(getSteps().stream().map(CheckoutFlowStep::toMap).collect(Collectors.toList()));
            m.put(Col.FLOW_STEPS, stepList);
        }
        return m;
    }

    @Override
    public String toString() {
        return "CheckoutFlow [id=" + id + ", name=" + name + ", description=" + description + ", steps=" + steps
            + ", enabled=" + enabled + ", active=" + active + "]";
    }
}
