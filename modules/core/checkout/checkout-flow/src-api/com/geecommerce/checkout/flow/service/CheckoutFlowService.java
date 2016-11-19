package com.geecommerce.checkout.flow.service;

import com.geecommerce.checkout.flow.model.CheckoutFlow;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

import java.util.List;

public interface CheckoutFlowService extends Service {

    List<CheckoutFlow> getFlowEnabled();

    CheckoutFlow getFlowActive();

    CheckoutFlow createCheckoutFlow(CheckoutFlow checkoutFlow);

    void updateCheckoutFlow(CheckoutFlow checkoutFlow);

    void deleteCheckoutFlow(CheckoutFlow checkoutFlow);

    CheckoutFlow getFlow(Id id);

    CheckoutFlow getFlow(String flowName);

    List<CheckoutFlow> getAll();

}
