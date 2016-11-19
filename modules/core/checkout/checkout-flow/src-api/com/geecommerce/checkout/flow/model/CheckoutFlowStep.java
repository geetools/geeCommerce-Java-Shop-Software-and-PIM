package com.geecommerce.checkout.flow.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface CheckoutFlowStep extends Model {

    String getName();

    CheckoutFlowStep setName(String name);

    String getUri();

    CheckoutFlowStep setUri(String uri);

    Id getId();

    CheckoutFlowStep setId(Id id);

    Id getFlowId();

    CheckoutFlowStep setFlowId(Id flowId);

    CheckoutFlowStep belongsTo(CheckoutFlow flow);

    static final class Col {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String URI = "uri";
        public static final String FLOW_ID = "flowId";
    }

}
