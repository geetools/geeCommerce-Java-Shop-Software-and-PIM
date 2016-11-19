package com.geecommerce.checkout.flow.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.List;
import java.util.Set;

public interface CheckoutFlow extends Model {

    Id getId();

    CheckoutFlow setId(Id id);

    Set<Id> getRequestContextIds();

    CheckoutFlow setRequestContextIds(Set<Id> requestContextIds);

    Boolean getEnabled();

    CheckoutFlow setEnabled(Boolean enabled);

    Boolean getActive();

    CheckoutFlow setActive(Boolean active);

    String getName();

    CheckoutFlow setName(String name);

    String getDescription();

    CheckoutFlow setDescription(String description);

    List<CheckoutFlowStep> getSteps();

    CheckoutFlow setSteps(List<CheckoutFlowStep> steps);

    CheckoutFlow setBaseUri(String baseUri);

    String getBaseUri();

    static final class Col {
        public static final String ID = "_id";
        public static final String FLOW_NAME = "name";
        public static final String FLOW_DESCR = "desc";
        public static final String FLOW_STEPS = "step";
        public static final String FLOW_ACTIVE = "active";
        public static final String FLOW_BASE_URI = "baseUri";
    }
}
