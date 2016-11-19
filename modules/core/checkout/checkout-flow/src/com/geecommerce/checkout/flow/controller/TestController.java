package com.geecommerce.checkout.flow.controller;

import java.util.LinkedList;
import java.util.List;

import com.geecommerce.checkout.flow.helper.CheckoutFlowHelper;
import com.geecommerce.checkout.flow.model.CheckoutFlow;
import com.geecommerce.checkout.flow.model.CheckoutFlowStep;
import com.geecommerce.checkout.flow.service.CheckoutFlowService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/checkoutflow")
public class TestController extends BaseController {

    private CheckoutFlowService flowService;
    private CheckoutFlowHelper flowHelper;

    @Inject
    public TestController(CheckoutFlowService flowService, CheckoutFlowHelper flowHelper) {
        this.flowService = flowService;
        this.flowHelper = flowHelper;
    }

    @Request("/create")
    public Result test_1() {

        // clear everithing before
        List<CheckoutFlow> all = flowService.getAll();
        for (CheckoutFlow checkoutFlow : all) {
            flowService.deleteCheckoutFlow(checkoutFlow);
        }

        CheckoutFlow threePageFlow = app.getModel(CheckoutFlow.class);
        threePageFlow.setName("3-page");
        threePageFlow.setDescription("3-page checkout");
        threePageFlow.setEnabled(true);
        threePageFlow.setActive(true);
        threePageFlow.setBaseUri("/checkout/page");
        threePageFlow.setId(Id.newId());

        List<CheckoutFlowStep> steps = new LinkedList<>();

        CheckoutFlowStep step1 = app.getModel(CheckoutFlowStep.class);
        step1.setId(Id.newId());
        step1.setName("address");
        step1.setUri("/address");
        step1.belongsTo(threePageFlow);
        steps.add(step1);

        CheckoutFlowStep step2 = app.getModel(CheckoutFlowStep.class);
        step2.setId(Id.newId());
        step2.setName("payment");
        step2.setUri("/payment");
        step2.belongsTo(threePageFlow);
        steps.add(step2);

        CheckoutFlowStep step3 = app.getModel(CheckoutFlowStep.class);
        step3.setId(Id.newId());
        step3.setName("preview");
        step3.setUri("/checkout");
        step3.belongsTo(threePageFlow);
        steps.add(step3);

        threePageFlow.setSteps(steps);
        CheckoutFlow flowActiveSaved = flowService.createCheckoutFlow(threePageFlow);

        // 2 page flow
        CheckoutFlow twoPageFlow = app.getModel(CheckoutFlow.class);
        twoPageFlow.setName("2-page");
        twoPageFlow.setDescription("2-page checkout");
        twoPageFlow.setEnabled(true);
        twoPageFlow.setActive(false);
        twoPageFlow.setBaseUri("/checkout/page");
        twoPageFlow.setId(Id.newId());

        steps = new LinkedList<>();

        step1 = app.getModel(CheckoutFlowStep.class);
        step1.setId(Id.newId());
        step1.setName("address");
        step1.setUri("/address");
        step1.belongsTo(twoPageFlow);
        steps.add(step1);

        step2 = app.getModel(CheckoutFlowStep.class);
        step2.setId(Id.newId());
        step2.setName("preview");
        step2.setUri("/checkout");
        step2.belongsTo(twoPageFlow);
        steps.add(step2);

        twoPageFlow.setSteps(steps);
        CheckoutFlow twoPageFlowSaved = flowService.createCheckoutFlow(twoPageFlow);

        // 1 page flow
        CheckoutFlow onePageFlow = app.getModel(CheckoutFlow.class);
        onePageFlow.setName("1-page");
        onePageFlow.setDescription("1-page checkout");
        onePageFlow.setEnabled(true);
        onePageFlow.setActive(false);
        onePageFlow.setBaseUri("/checkout/page");
        onePageFlow.setId(Id.newId());

        steps = new LinkedList<>();

        step1 = app.getModel(CheckoutFlowStep.class);
        step1.setId(Id.newId());
        step1.setName("preview");
        step1.setUri("/checkout");
        step1.belongsTo(onePageFlow);
        steps.add(step1);

        onePageFlow.setSteps(steps);
        CheckoutFlow onePageFlowSaved = flowService.createCheckoutFlow(onePageFlow);

        return Results.stream("text/plain", "Three Page Flow: " + flowActiveSaved.toString()
            + "  Two Page Flow: " + twoPageFlowSaved.toString()
            + " One Page Flow: " + onePageFlowSaved.toString());
    }

    @Request("/update/{id}")
    public Result test_2(@PathParam("id") Id id, @Param("enabled") Boolean isEnabled) {
        CheckoutFlow flow = flowService.getFlow(id);
        if (flow != null) {
            flow.setEnabled(isEnabled);
            flowService.updateCheckoutFlow(flow);
        }
        return Results.stream("text/plain", flow.toString());
    }

    @Request("/getEnabled")
    public Result test_enabled() {
        return Results.stream("text/plain", flowService.getFlowEnabled() != null ? flowService.getFlowEnabled().toString() : "null");
    }

    @Request("/getActive")
    public Result test_active() {
        return Results.stream("text/plain", flowService.getFlowActive() != null ? flowService.getFlowActive().toString() : "null");
    }

    @Request("/get/{name}")
    public Result test_4(@PathParam("String") String name) {
        CheckoutFlow flow = flowService.getFlow(name);
        return Results.stream("text/plain", flow.toString());
    }

    @Request("/delete/{name}")
    public Result test_5(@PathParam("name") String name) {
        CheckoutFlow flow = flowService.getFlow(name);
        flowService.deleteCheckoutFlow(flow);
        return Results.stream("text/plain", "Flow deleted: " + flow.toString());
    }

    @Request("/getFirstStep/{name}")
    public Result test_6(@PathParam("name") String name) {
        CheckoutFlow flow = flowService.getFlow(name);
        CheckoutFlowStep firstFlowStep = flowHelper.getFirstFlowStep(flow);
        if (firstFlowStep != null)
            return Results.stream("text/plain", "First Flow Step: " + firstFlowStep.toString());
        else
            return Results.stream("text/plain", "First Flow Step: Not found");
    }

    @Request("/getNextStep/{name}")
    public Result test_7(@PathParam("name") String name, @Param("setCurrent") Boolean setCurrent) {
        CheckoutFlow flow = null;

        if (name != null) {
            flow = flowService.getFlow(name);
        } else {
            flow = flowService.getFlowActive();
        }

        CheckoutFlowStep firstFlowStep = flowHelper.getFirstFlowStep(flow);
        flowHelper.setCurrentStep(firstFlowStep);

        StringBuffer sb = new StringBuffer();
        CheckoutFlowStep nextFlowStep_1 = flowHelper.getNextFlowStep(flow, firstFlowStep, true);

        sb.append(nextFlowStep_1.toString()).append("----->");

        CheckoutFlowStep nextFlowStep_2 = flowHelper.getNextFlowStep(flow, flowHelper.getCurrentStep(), true);
        sb.append(nextFlowStep_2.toString()).append("----->");

        CheckoutFlowStep nextFlowStep_3 = flowHelper.getNextFlowStep(flow, flowHelper.getCurrentStep(), true);
        sb.append(nextFlowStep_3.toString()).append(":::::::::");

        ////////////////////
        CheckoutFlowStep nextFlowStep_4 = flowHelper.getNextActiveFlowStep(firstFlowStep, true);
        sb.append(nextFlowStep_4.toString()).append("----->");

        CheckoutFlowStep nextFlowStep_5 = flowHelper.getNextActiveFlowStep(true);
        sb.append(nextFlowStep_5.toString()).append("----->");

        CheckoutFlowStep nextFlowStep_6 = flowHelper.getNextActiveFlowStep(true);
        sb.append(nextFlowStep_6.toString());

        return Results.stream("text/plain", "Steps: " + sb.toString());
    }
}
