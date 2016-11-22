package com.geecommerce.core.web.api;

import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.system.widget.repository.WidgetParameterOptions;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.google.inject.Inject;

public abstract class AbstractWidgetController implements WidgetController {
    @Inject
    protected App app;

    protected static final String PARAM_VIEW = "view";
    protected static final String DEFAULT_VIEW = "default";

    protected AbstractWidgetController() {
        app = App.get();
    }

    @Override
    public String getCode() {
        return this.getClass().getAnnotation(Widget.class).name();
    }

    @Override
    public String getView(WidgetContext ctx) {
        String view = ctx == null ? null : ctx.getParam(PARAM_VIEW);

        if (view == null)
            view = DEFAULT_VIEW;

        return view;
    }

    @Override
    public String getDefaultView() {
        return DEFAULT_VIEW;
    }

    @Override
    public List<WidgetParameter> getParameters() {
        if (getCode() != null) {
            WidgetParameters widgetParameters = app.repository(WidgetParameters.class);
            return widgetParameters.forWidget(getCode());
        }
        return null;
    }

    @Override
    public List<WidgetParameterOption> getParameterOptions(Id parameterId) {
        if (parameterId == null)
            return null;

        WidgetParameterOptions widgetParameterOptions = app.repository(WidgetParameterOptions.class);
        return widgetParameterOptions.thatBelongTo(parameterId);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCmsEnabled() {
        boolean cms = this.getClass().getAnnotation(Widget.class).cms();
        return isEnabled() && cms;
    }

    @Override
    public boolean isJavascriptEnabled() {
        boolean js = this.getClass().getAnnotation(Widget.class).js();
        return isEnabled() && js;
    }

    @Override
    public boolean isCssEnabled() {
        boolean css = this.getClass().getAnnotation(Widget.class).css();
        return isEnabled() && css;
    }
}
