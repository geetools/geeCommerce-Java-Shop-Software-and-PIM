package com.geecommerce.core.payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.inject.SystemInjector;
import com.geecommerce.core.payment.annotation.PaymentMethod;
import com.geemodule.api.ModuleLoader;

public class PaymentHelper {
    private static final Logger log = LogManager.getLogger(SystemInjector.class);

    @SuppressWarnings("unchecked")
    public static List<AbstractPaymentMethod> locatePaymentMethods() {
        long startx = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("ENTER locatePaymentMethods()");
        }

        List<AbstractPaymentMethod> paymentMethods = new ArrayList<>();

        App app = App.get();
        ApplicationContext appCtx = app.getApplicationContext();

        if (appCtx != null) {
            // Find payment methods in modules
            ModuleLoader loader = app.getModuleLoader();

            Class<AbstractPaymentMethod>[] types = (Class<AbstractPaymentMethod>[]) loader.findAllTypesAnnotatedWith(PaymentMethod.class, false);

            for (Class<AbstractPaymentMethod> type : types) {
                AbstractPaymentMethod paymentMethodInstance = null;

                try {
                    paymentMethodInstance = app.inject(type);

                    if (paymentMethodInstance != null && paymentMethodInstance.isEnabled())
                        paymentMethods.add(paymentMethodInstance);

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            // Sort payment methods according to the sort-index
            if (paymentMethods != null && paymentMethods.size() > 0) {
                Collections.sort(paymentMethods, new Comparator<AbstractPaymentMethod>() {
                    @Override
                    public int compare(AbstractPaymentMethod o1, AbstractPaymentMethod o2) {
                        return (o1.getSortIndex() < o2.getSortIndex() ? -1 : (o1.getSortIndex() > o2.getSortIndex() ? 1 : 0));
                    }
                });
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("EXIT locatePaymentMethods() - " + (System.currentTimeMillis() - startx));
        }

        return paymentMethods;
    }

    public static AbstractPaymentMethod findPaymentMethodByCode(String paymentMethodCode) {
        if (paymentMethodCode == null)
            return null;

        List<AbstractPaymentMethod> paymentMethods = locatePaymentMethods();

        AbstractPaymentMethod foundPaymentMethod = null;

        for (AbstractPaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethodCode.equals(paymentMethod.getCode())) {
                foundPaymentMethod = paymentMethod;
                break;
            }
        }

        return foundPaymentMethod;
    }

    public static final Map<String, Object> filterRequestParameters(String formFieldPrefix, Map<String, String[]> requestParameters) {
        Map<String, Object> filteredRequestParameters = new HashMap<>();

        if (formFieldPrefix == null || "".equals(formFieldPrefix.trim()) || requestParameters.isEmpty())
            return filteredRequestParameters;

        for (String key : requestParameters.keySet()) {
            if (key.startsWith(formFieldPrefix)) {
                String[] value = requestParameters.get(key);

                if (value != null && value.length == 1) {
                    filteredRequestParameters.put(key, value[0]);
                } else {
                    filteredRequestParameters.put(key, value);
                }
            }
        }

        return filteredRequestParameters;
    }
}
