package com.geecommerce.checkout.flow.helper;

import java.util.List;

import com.geecommerce.checkout.flow.model.CheckoutFlow;
import com.geecommerce.checkout.flow.model.CheckoutFlowStep;
import com.geecommerce.core.service.api.Helper;

public interface CheckoutFlowHelper extends Helper {

    List<CheckoutFlow> getFlowEnabled();

    CheckoutFlow getFlowActive();

    CheckoutFlowStep getFirstActiveFlowStep();

    CheckoutFlowStep getFirstFlowStep(String flowName);

    CheckoutFlowStep getFirstFlowStep(CheckoutFlow flow);

    CheckoutFlowStep getNextActiveFlowStep(boolean setAsCurrent);

    CheckoutFlowStep getNextActiveFlowStep(CheckoutFlowStep currentStep, boolean setAsCurrent);

    CheckoutFlowStep getNextFlowStep(String flowName, CheckoutFlowStep currentStep, boolean setAsCurrent);

    CheckoutFlowStep getNextFlowStep(CheckoutFlow flow, CheckoutFlowStep currentStep, boolean setAsCurrent);

    void setCurrentStep(CheckoutFlowStep currentStep);

    CheckoutFlowStep getCurrentStep();

    CheckoutFlowStep getPreviousActiveFlowStep(boolean setAsCurrent);

    CheckoutFlowStep getPreviousActiveFlowStep(CheckoutFlowStep currentStep, boolean setAsCurrent);

    CheckoutFlowStep getPreviousFlowStep(String flowName, CheckoutFlowStep currentStep, boolean setAsCurrent);

    CheckoutFlowStep getPreviousFlowStep(CheckoutFlow flow, CheckoutFlowStep currentStep, boolean setAsCurrent);

    String getOriginalURI(CheckoutFlowStep step);

    CheckoutFlowStep getFlowStep(String uri);

}
