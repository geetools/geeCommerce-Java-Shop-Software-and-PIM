package com.geecommerce.core.web.stripes;

import com.geecommerce.core.Constant;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

@Intercepts({ LifecycleStage.BindingAndValidation, LifecycleStage.CustomValidation })
public class StripesInterceptor implements Interceptor {
    @Override
    public Resolution intercept(ExecutionContext context) throws Exception {
        String eventName = context.getActionBeanContext().getEventName();

        Resolution r = context.proceed();

        // If we have validation errors, Stripes will automatically try to
        // forward to the previous action. This unfortunately
        // ends up in a StripesRuntimeException "Multiple event parameters [...]
        // are present in this request ..." because
        // the previous event was not cleared and now stripes has 2 possible
        // events. The problem with this is that Stripes
        // does not know which one to choose. Here we attempt to overcome this
        // by offering Stripes an event that it may
        // ignore. See
        // com.geecommerce.core.web.AnnotatedClassActionResolver:548.
        if (eventName != null && context.getActionBeanContext().getValidationErrors().size() > 0
            && context.getActionBeanContext().getRequest().getAttribute(Constant.STRIPES_IGNORE_EVENT) == null) {
            context.getActionBeanContext().getRequest().setAttribute(Constant.STRIPES_IGNORE_EVENT, eventName);
        }

        return r;
    }
}
