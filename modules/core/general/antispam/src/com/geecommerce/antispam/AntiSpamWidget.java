package com.geecommerce.antispam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.antispam.configuration.Key;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "antispam")
public class AntiSpamWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_HONEYPOT = "honeypot";
    private final String PARAM_RECAPTURE = "recapture";
    private final String PARAM_RECAPTURE_KEY = "recaptureKey";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        boolean useHoneyPot = app.cpBool_(Key.HONEYPOT, true);
        boolean useRecapture = app.cpBool_(Key.RECAPTURE, false);
        String recaptureKey = app.cpStr_(Key.RECAPTURE_PUBLIC_KEY);

        widgetCtx.setParam(PARAM_HONEYPOT, useHoneyPot);
        widgetCtx.setParam(PARAM_RECAPTURE, useRecapture);
        widgetCtx.setParam(PARAM_RECAPTURE_KEY, recaptureKey);

        widgetCtx.render("antispam/antispam");
    }
}
