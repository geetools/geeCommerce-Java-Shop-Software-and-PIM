package com.geecommerce.core.web.stripes;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.model.RequestContext;

import net.sourceforge.stripes.localization.DefaultLocalePicker;

public class LocalePicker extends DefaultLocalePicker {
    @Override
    public Locale pickLocale(HttpServletRequest request) {
        ApplicationContext appCtx = App.get().getApplicationContext();

        if (appCtx != null) {
            RequestContext reqCtx = appCtx.getRequestContext();
            return new Locale(reqCtx.getLanguage(), reqCtx.getCountry());
        } else {
            return super.pickLocale(request);
        }
    }

    @Override
    public String pickCharacterEncoding(HttpServletRequest request, Locale locale) {
        return App.get().getSystemCharset();
    }
}
