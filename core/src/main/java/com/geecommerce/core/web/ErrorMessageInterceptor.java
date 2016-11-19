package com.geecommerce.core.web;

import java.util.List;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

// NOT WORKING -> TODO!

@Intercepts(LifecycleStage.EventHandling)
public class ErrorMessageInterceptor implements Interceptor {
    private static final String CTX_KEY = "__GLOBAL_ERRORS";

    @SuppressWarnings("unchecked")
    @Override
    public Resolution intercept(ExecutionContext ctx) throws Exception {
	List<ValidationError> globalErrors = (List<ValidationError>) ctx.getActionBeanContext().getRequest().getAttribute(CTX_KEY);

	if (globalErrors != null) {
	    for (ValidationError globalError : globalErrors) {
		ValidationErrors errors = ctx.getActionBeanContext().getValidationErrors();
		errors.addGlobalError(globalError);
	    }

	    ctx.getActionBeanContext().getRequest().removeAttribute(CTX_KEY);
	}

	Resolution resolution = ctx.proceed();

	ValidationErrors errors = ctx.getActionBeanContext().getValidationErrors();

	globalErrors = errors.get(ValidationErrors.GLOBAL_ERROR);

	if (globalErrors != null && globalErrors.size() > 0 && resolution instanceof RedirectResolution) {
	    FlashScope scope = FlashScope.getCurrent(ctx.getActionBeanContext().getRequest(), true);
	    scope.put(CTX_KEY, globalErrors);
	}

	return resolution;
    }
}
