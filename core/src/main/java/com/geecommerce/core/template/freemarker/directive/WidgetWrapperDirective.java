package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.template.freemarker.FreemarkerConstant;
import com.geecommerce.core.template.freemarker.FreemarkerWidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geemodule.api.Module;
import com.geemodule.api.ModuleClassLoader;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class WidgetWrapperDirective implements TemplateDirectiveModel {
    private final Class<WidgetController> controllerClass;

    public WidgetWrapperDirective(Class<WidgetController> controllerClass) {
        this.controllerClass = controllerClass;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        App app = App.get();
        HttpServletRequest request = app.servletRequest();
        HttpServletResponse response = app.servletResponse();
        ServletContext servletContext = app.servletContext();
        WidgetController widgetController = ModuleInjector.get().getInstance(controllerClass);
        FreemarkerWidgetContext widgetCtx = new FreemarkerWidgetContext();
        app.injectMembers(widgetCtx);

        try {
            ClassLoader cl = controllerClass.getClassLoader();
            Module m = null;

            if (cl instanceof ModuleClassLoader) {
                ModuleClassLoader mcl = (ModuleClassLoader) cl;
                m = mcl.getModule();
            }

            ((FreemarkerWidgetContext) widgetCtx).init(m, env, params, loopVars, body, app.servletRequest(),
                app.servletResponse(), servletContext);
            ((FreemarkerWidgetContext) widgetCtx).setParam(FreemarkerConstant.FREEMARKER_TEMPLATE_SELF_VAR,
                widgetController);

            widgetController.execute(widgetCtx, request, response, servletContext);

            // System.out.println("Widget '" +
            // widgetController.getClass().getSimpleName() + "' took: " +
            // (System.currentTimeMillis()-start));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
