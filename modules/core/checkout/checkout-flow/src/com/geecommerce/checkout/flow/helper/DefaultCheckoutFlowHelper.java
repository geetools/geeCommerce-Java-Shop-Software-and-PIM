package com.geecommerce.checkout.flow.helper;

import java.util.List;

import com.geecommerce.checkout.flow.configuration.Key;
import com.geecommerce.checkout.flow.model.CheckoutFlow;
import com.geecommerce.checkout.flow.model.CheckoutFlowStep;
import com.geecommerce.checkout.flow.service.CheckoutFlowService;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.google.inject.Inject;

@Helper
public class DefaultCheckoutFlowHelper implements CheckoutFlowHelper {
    @Inject
    protected App app;

    protected CheckoutFlowService flowService;

    @Inject
    public DefaultCheckoutFlowHelper(CheckoutFlowService flowService) {
        this.flowService = flowService;
    }

    @Override
    public List<CheckoutFlow> getFlowEnabled() {
        return flowService.getFlowEnabled();
    }

    @Override
    public CheckoutFlow getFlowActive() {
        return flowService.getFlowActive();
    }

    @Override
    public CheckoutFlowStep getFirstActiveFlowStep() {
        CheckoutFlow defaultFlow = getFlowActive();
        if (defaultFlow != null) {
            return getFirstFlowStep(defaultFlow.getName());
        }
        return null;
    }

    @Override
    public CheckoutFlowStep getFirstFlowStep(String flowName) {
        CheckoutFlow flow = flowService.getFlow(flowName);
        return getFirstFlowStep(flow);
    }


    @Override
    public CheckoutFlowStep getFirstFlowStep(CheckoutFlow flow) {
        if (flow != null) {
            List<CheckoutFlowStep> steps = flow.getSteps();
            if (steps != null && steps.size() > 0) {
                CheckoutFlowStep step = steps.get(0);
                setCurrentStep(step);
                return step;
            }
        }
        return null;
    }


    @Override
    public CheckoutFlowStep getNextActiveFlowStep(boolean setAsCurrent) {
        CheckoutFlow flowDefault = getFlowActive();
        CheckoutFlowStep currentStep = getCurrentStep();
        if (currentStep == null) {
            currentStep = getFirstActiveFlowStep();
            setCurrentStep(currentStep);
        }
        return getNextFlowStep(flowDefault, currentStep, setAsCurrent);
    }


    @Override
    public CheckoutFlowStep getNextActiveFlowStep(CheckoutFlowStep currentStep, boolean setAsCurrent) {
        CheckoutFlow flowDefault = getFlowActive();
        return getNextFlowStep(flowDefault, currentStep, setAsCurrent);
    }


    @Override
    public CheckoutFlowStep getNextFlowStep(String flowName, CheckoutFlowStep currentStep, boolean setAsCurrent) {
        CheckoutFlow flow = flowService.getFlow(flowName);
        return getNextFlowStep(flow, currentStep, setAsCurrent);
    }

    @Override
    public void setCurrentStep(CheckoutFlowStep currentStep) {
        app.sessionSet(Key.SESSION_KEY_CURR_CHECKOUT_FLOW_STEP, currentStep);
    }

    @Override
    public CheckoutFlowStep getCurrentStep() {
        return app.sessionGet(Key.SESSION_KEY_CURR_CHECKOUT_FLOW_STEP);
    }


    @Override
    public CheckoutFlowStep getNextFlowStep(CheckoutFlow flow, CheckoutFlowStep currentStep, boolean setAsCurrent) {
        CheckoutFlowStep nextStep = null;
        if (flow != null) {
            List<CheckoutFlowStep> steps = flow.getSteps();
            if (steps != null) {
                for (int i = 0; i < steps.size(); i++) {
                    CheckoutFlowStep step = steps.get(i);
                    if (step.equals(currentStep)) {
                        if (steps.indexOf(step) != (steps.size() - 1))
                            nextStep = steps.get(i + 1);
                        else
                            nextStep = step;

                        break;
                    }
                }
            }
        }

        if (setAsCurrent)
            setCurrentStep(nextStep);

        return nextStep;
    }


    @Override
    public CheckoutFlowStep getPreviousActiveFlowStep(boolean setAsCurrent) {
        CheckoutFlow flowDefault = getFlowActive();
        CheckoutFlowStep currentStep = getCurrentStep();
        if (currentStep == null) {
            currentStep = getFirstActiveFlowStep();
            setCurrentStep(currentStep);
        }
        return getPreviousFlowStep(flowDefault, currentStep, setAsCurrent);
    }

    @Override
    public CheckoutFlowStep getPreviousActiveFlowStep(CheckoutFlowStep currentStep, boolean setAsCurrent) {
        CheckoutFlow flowDefault = getFlowActive();
        return getPreviousFlowStep(flowDefault, currentStep, setAsCurrent);
    }

    @Override
    public CheckoutFlowStep getPreviousFlowStep(String flowName, CheckoutFlowStep currentStep, boolean setAsCurrent) {
        CheckoutFlow flow = flowService.getFlow(flowName);
        return getPreviousFlowStep(flow, currentStep, setAsCurrent);
    }

    @Override
    public CheckoutFlowStep getPreviousFlowStep(CheckoutFlow flow, CheckoutFlowStep currentStep, boolean setAsCurrent) {
        CheckoutFlowStep prevStep = null;
        if (flow != null) {
            List<CheckoutFlowStep> steps = flow.getSteps();
            if (steps != null) {
                for (int i = 0; i < steps.size(); i++) {
                    CheckoutFlowStep step = steps.get(i);
                    if (step.equals(currentStep)) {
                        if (steps.indexOf(step) == 0)
                            prevStep = step;
                        else
                            prevStep = steps.get(i - 1);

                        break;
                    }
                }
            }
        }

        if (setAsCurrent)
            setCurrentStep(prevStep);

        return prevStep;
    }

    @Override
    public String getOriginalURI(CheckoutFlowStep step) {

        if (step == null)
            throw new IllegalArgumentException("Step doesn't exist.");

        if (step != null && step.getFlowId() != null) {
            CheckoutFlow flow = flowService.getFlow(step.getFlowId());
            if (flow == null)
                throw new IllegalStateException("There is no flow for the step with id = " + step.getFlowId());

            String baseUri = flow.getBaseUri();
            String separator = "/";
            if (baseUri.lastIndexOf("/") != -1) {
                separator = "";
            }

            return flow.getBaseUri() + separator + step.getUri();
        }

        return step.getUri();
    }

    @Override
    public CheckoutFlowStep getFlowStep(String uri) {
        List<CheckoutFlow> all = flowService.getAll();
        for (CheckoutFlow flow : all) {
            List<CheckoutFlowStep> steps = flow.getSteps();
            for (CheckoutFlowStep step : steps) {
                if (getOriginalURI(step).equals(uri))
                    return step;
            }
        }
        return null;
    }
}
