package com.geecommerce.calculation.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationContext;
import com.geecommerce.calculation.model.CalculationData;
import com.geecommerce.calculation.model.CalculationStep;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.system.service.ConfigurationService;
import com.google.inject.Inject;

@Helper
public class DefaultCalculationHelper implements CalculationHelper {
    @Inject
    protected App app;

    protected final ConfigurationService confService;

    @Inject
    public DefaultCalculationHelper(ConfigurationService confService) {
        this.confService = confService;
    }

    @Override
    public List<CalculationStep> sortCalculationSteps(List<CalculationStep> calculationSteps) {
        Collections.sort(calculationSteps, new Comparator<CalculationStep>() {
            @Override
            public int compare(CalculationStep cs1, CalculationStep cs2) {
                return (cs1.getSortOrder() < cs2.getSortOrder() ? -1
                    : (cs1.getSortOrder() > cs2.getSortOrder() ? 1 : 0));
            }
        });
        return new ArrayList<>(calculationSteps);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CalculationContext newCalculationContext(CalculationData calcData) {
        // Find configuration properties for calculation-module.
        List<ConfigurationProperty> confProps = confService.findProperties("^calculation/");

        CalculationContext calcCtx = null;

        if (confProps == null || confProps.size() == 0) {
            throw new RuntimeException(
                "Cannot create new CalculationContext because no configuration properties could be found.");
        } else {
            calcCtx = app.injectable(CalculationContext.class);

            // Add all properties to context.
            for (ConfigurationProperty cp : confProps) {
                calcCtx.addConfigurationProperty(cp.getKey(), cp.getValue());
            }
        }

        // Get data we can pass onto the calculationService for doing the
        // calculation work.
        Map<String, Object> dataMap = calcData.toCalculationData();

        // Add items to the calculationContext.
        calcCtx.setItems((List<Map<String, Object>>) dataMap.get(CalculationData.FIELD.ITEMS));

        return calcCtx;
    }
}
