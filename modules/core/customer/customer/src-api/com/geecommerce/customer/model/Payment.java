package com.geecommerce.customer.model;


import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.Map;

public interface Payment extends Model {

    Payment setId(Id id);

    Id getCustomerId();

    Payment belongsTo(Customer customer);

    boolean isDefaultPayment();

    Payment setDefaultPayment(boolean defaultPayment);

    String getPaymentCode();

    Payment setPaymentCode(String paymentCode);

    Map<String, String> getParameters();

    Payment setParameters(Map<String, String> parameters);

    static final class Column {
        public static final String ID = "_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String PAYMENT_CODE = "payment";
        public static final String DEFAULT_PAYMENT = "def_payment";
        public static final String PARAMETERS = "param";
    }

}
