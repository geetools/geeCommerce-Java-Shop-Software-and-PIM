package com.geecommerce.core.web.geemvc.intercept;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.type.Id;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;
import com.geemodule.util.Strings;
import com.geemvc.RequestContext;
import com.geemvc.annotation.Request;
import com.geemvc.handler.RequestHandler;
import com.geemvc.intercept.InvocationContext;
import com.geemvc.view.GeemvcKey;
import com.google.inject.Inject;

public class DefaultHandlerInterceptor implements HandlerInterceptor {
    @Inject
    protected App app;

    @Override
    public Object invokeAround(InvocationContext invocationCtx) {

        setCurrentId(invocationCtx);
        setCurrentModule(invocationCtx);
        setCurrentController(invocationCtx);
        setCurrentEvent(invocationCtx);

        setLoggedInCustomer(invocationCtx);
        setIsCustomerLoggedIn(invocationCtx);

        return invocationCtx.proceed();
    }

    protected void setCurrentId(InvocationContext invocationCtx) {
        RequestContext requestCtx = invocationCtx.requestContext();
        ServletRequest request = requestCtx.getRequest();

        String id = (String) request.getAttribute(GeemvcKey.RESOLVED_ID_PARAM);

        if (!Str.isEmpty(id)) {
            request.setAttribute("modelId", Id.valueOf(id));
        }
    }

    protected void setCurrentModule(InvocationContext invocationCtx) {
        Class<?> controllerClass = invocationCtx.controllerClass();
        RequestContext requestCtx = invocationCtx.requestContext();
        ServletRequest request = requestCtx.getRequest();

        ClassLoader cl = Reflect.getModuleClassLoader(controllerClass);

        if (cl instanceof ModuleClassLoader) {
            ModuleClassLoader mcl = (ModuleClassLoader) (cl);
            Module m = mcl.getModule();

            if (m != null) {
                app.setCurrentModule(m);
                request.setAttribute("moduleCode", m.getCode());
            }
        }
    }

    protected void setCurrentController(InvocationContext invocationCtx) {
        Class<?> controllerClass = ensureNoneGuiceClass(invocationCtx.controllerClass());
        RequestContext requestCtx = invocationCtx.requestContext();
        ServletRequest request = requestCtx.getRequest();

        String controller = controllerClass.getSimpleName().replaceFirst("Controller$", Str.EMPTY).replaceFirst("Action$", Str.EMPTY);

        if (controller.startsWith("My") && controllerClass.getName().startsWith("custom."))
            controller = controller.replaceFirst("^My", Str.EMPTY);

        controller = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(controller), Char.UNDERSCORE);

        String controllerCode = Strings.slugify(controller).replace(Char.MINUS, Char.UNDERSCORE).toLowerCase();
        request.setAttribute("controllerCode", controllerCode);
    }

    protected void setCurrentEvent(InvocationContext invocationCtx) {
        RequestHandler rh = invocationCtx.requestHandler();
        Request mapping = rh.handlerRequestMapping();
        RequestContext requestCtx = invocationCtx.requestContext();
        ServletRequest request = requestCtx.getRequest();

        String event = null;

        if (!Str.isEmpty(mapping.name())) {
            event = mapping.name();
        } else {
            event = rh.handlerMethod().getName();
        }

        request.setAttribute("eventCode", Strings.slugify(event).replace(Char.MINUS, Char.UNDERSCORE).toLowerCase());
    }

    protected Class<?> ensureNoneGuiceClass(Class<?> controllerClass) {
        if (controllerClass.getSimpleName().contains("$$EnhancerByGuice$$")) {
            return controllerClass.getSuperclass();
        } else {
            return controllerClass;
        }
    }

    protected void setLoggedInCustomer(InvocationContext invocationCtx) {
        RequestContext requestCtx = invocationCtx.requestContext();
        ServletRequest request = requestCtx.getRequest();
        request.setAttribute("loggedInCustomer", app.getLoggedInCustomer());
    }

    protected void setIsCustomerLoggedIn(InvocationContext invocationCtx) {
        RequestContext requestCtx = invocationCtx.requestContext();
        ServletRequest request = requestCtx.getRequest();
        request.setAttribute("isCustomerLoggedIn", app.isCustomerLoggedIn());
    }
}
