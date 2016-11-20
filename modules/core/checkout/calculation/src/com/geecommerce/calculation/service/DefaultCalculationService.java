package com.geecommerce.calculation.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.geecommerce.calculation.helper.CalculationHelper;
import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.model.CalculationRule;
import com.geecommerce.calculation.model.CalculationScriptlet;
import com.geecommerce.calculation.model.CalculationStep;
import com.geecommerce.calculation.repository.CalculationRules;
import com.geecommerce.calculation.repository.CalculationScriptlets;
import com.geecommerce.core.App;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.annotation.Service;
import com.google.inject.Inject;

import groovy.lang.GroovyClassLoader;

@Service
public class DefaultCalculationService implements CalculationService {
    @Inject
    protected App app;

    protected final CalculationScriptlets calculationScriptlets;
    protected final CalculationRules calculationRules;
    protected final CalculationHelper calculationHelper;

    protected static final String CONTEXT_KEY = "ctx";

    protected static final GroovyClassLoader GCL = new GroovyClassLoader(CalculationService.class.getClassLoader());

    protected static final Set<String> groovyImports = new HashSet<>();
    static {
        groovyImports.add("com.geecommerce.calculation.*\n");
        groovyImports.add("com.geecommerce.calculation.model.*\n");
        groovyImports.add("com.geecommerce.calculation.helper.*\n");
        groovyImports.add("static com.geecommerce.calculation.model.ParamKey.*");
        groovyImports.add("static com.geecommerce.calculation.model.ResultKey.*");
        groovyImports.add("static com.geecommerce.calculation.model.ResultItemKey.*");
        groovyImports.add("static com.geecommerce.calculation.model.ResultSubItemKey.*");
        groovyImports.add("static com.geecommerce.calculation.model.CalculationItem.FIELD.*");
        groovyImports.add("static com.geecommerce.calculation.model.CalculationItemDiscount.FIELD.*");
    }

    @Inject
    public DefaultCalculationService(CalculationScriptlets calculationScriptlets, CalculationRules calculationRules,
        CalculationHelper calculationHelper) {
        this.calculationScriptlets = calculationScriptlets;
        this.calculationRules = calculationRules;
        this.calculationHelper = calculationHelper;
    }

    @Override
    public CalculationResult calculateTotals(CalculationContext ctx) throws Exception {
        // Get the calculation-rule for this context
        CalculationRule calcRule = getCalculationRule();
        List<CalculationStep> calcSteps = calcRule.getCalculationSteps();

        // Now run each calculation scriptlet with the groovy engine
        for (CalculationStep calculationStep : calcSteps) {
            try {
                CalculationScriptlet scriptlet = calculationStep.getScriptlet();
                // groovyEngine.execute(scriptlet.getBody(), ctx);
                Groovy.eval(scriptlet.getBody(), CONTEXT_KEY, ctx, groovyImports, GCL);
            } catch (Exception e) {
                throw new Exception(
                    "Error in groovy-calculation-script '"
                        + (calculationStep.getScriptlet() != null ? calculationStep.getScriptlet().getCode()
                            : calculationStep.getId() + " (" + calculationStep.getSortOrder() + ")")
                        + "'",
                    e);
            }
        }

        return app.injectable(CalculationResult.class).setItemResults(ctx.getItemResults())
            .setResults(ctx.getResults());
    }

    // --------------------------------------------------------------
    // Calculation scriptlets
    // --------------------------------------------------------------

    @Override
    public CalculationScriptlet insertCalculationScriptlet(CalculationScriptlet calculationScriptlet) {
        if (calculationScriptlet == null || !calculationScriptlet.isValid())
            return null;

        return calculationScriptlets.add(calculationScriptlet);
    }

    // --------------------------------------------------------------
    // Calculation rules
    // --------------------------------------------------------------

    @Override
    public CalculationRule getCalculationRule() {
        String calcRuleCode = app.cpStr_("calculation/rule/code", "calculation_rule_eu");

        return calculationRules.havingCode(calcRuleCode);
    }

    @Override
    public CalculationRule insertCalculationRule(CalculationRule calculationRule) {
        if (calculationRule == null)
            return null;

        return calculationRules.add(calculationRule);
    }

}
