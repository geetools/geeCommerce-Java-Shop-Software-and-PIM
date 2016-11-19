package com.geecommerce.checkout.flow.service;

import com.geecommerce.checkout.flow.model.CheckoutFlow;
import com.geecommerce.checkout.flow.repository.CheckoutFlows;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultCheckoutFlowService implements CheckoutFlowService {

    private final CheckoutFlows flows;

    @Inject
    public DefaultCheckoutFlowService(CheckoutFlows flows) {
        this.flows = flows;
    }

    @Override
    public List<CheckoutFlow> getFlowEnabled() {
        Map<String, Object> filter = new HashMap<>();
        filter.put(GlobalColumn.ENABLED, true);
        return flows.find(CheckoutFlow.class, filter);
    }

    @Override
    public CheckoutFlow getFlowActive() {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CheckoutFlow.Col.FLOW_ACTIVE, true);
        return flows.findOne(CheckoutFlow.class, filter);
    }

    @Override
    public CheckoutFlow createCheckoutFlow(CheckoutFlow checkoutFlow) {
        return flows.add(checkoutFlow);
    }

    @Override
    public void updateCheckoutFlow(CheckoutFlow checkoutFlow) {
        flows.update(checkoutFlow);
    }

    @Override
    public void deleteCheckoutFlow(CheckoutFlow checkoutFlow) {
        flows.remove(checkoutFlow);
    }

    @Override
    public CheckoutFlow getFlow(Id id) {
        return flows.findById(CheckoutFlow.class, id);
    }

    @Override
    public CheckoutFlow getFlow(String flowName) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(CheckoutFlow.Col.FLOW_NAME, flowName);
        return flows.findOne(CheckoutFlow.class, filter);
    }

    @Override
    public List<CheckoutFlow> getAll() {
        return flows.findAll(CheckoutFlow.class);
    }
}
