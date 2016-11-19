package com.geecommerce.guiwidgets;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "discount_promotion", cms = false)
public class DiscountPromotionWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_KEY = "key";
    private final String PARAM_TEMPLATE = "template";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String promotionKey = widgetCtx.getParam(PARAM_KEY);
        String promotionTemplate = widgetCtx.getParam(PARAM_TEMPLATE);
        if (promotionTemplate == null)
            promotionTemplate = "promotion";

        widgetCtx.setParam("promotion", promotionKey);
        widgetCtx.setParam("template", promotionTemplate);

        widgetCtx.render("discount_promotion/discount_promotion");
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public List<WidgetParameter> getParameters() {
        WidgetParameters widgetParameters = app.getRepository(WidgetParameters.class);

        return null;
    }

    @Override
    public List<WidgetParameterOption> getParameterOptions(Id parameterId) {
        return null;
    }

}