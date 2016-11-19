package com.geecommerce.guiwidgets;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.helper.ContentHelper;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Widget(name = "cms_google_map", cms = true, css = true)
public class CmsGoogleMapWidget extends AbstractWidgetController implements WidgetController {
    private final String PARAM_LOCATION = "location";
    private final String PARAM_LONGITUDE = "longitude";
    private final String PARAM_LATITUDE = "latitude";
    private final String PARAM_ZOOM = "zoom";
/*    private final String PARAM_HEIGHT = "height";*/
    private final String PARAM_WIDTH = "width";

    private final ContentHelper contentHelper;

    @Inject
    public CmsGoogleMapWidget( ContentHelper contentHelper) {
        this.contentHelper = contentHelper;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String location = widgetCtx.getParam(PARAM_LOCATION);
        String latitude = widgetCtx.getParam(PARAM_LATITUDE); //широта
        String longitude = widgetCtx.getParam(PARAM_LONGITUDE); //долгота
        String zoom = widgetCtx.getParam(PARAM_ZOOM);
       String width = widgetCtx.getParam(PARAM_WIDTH);
/*         String height = widgetCtx.getParam(PARAM_HEIGHT);*/


       if(StringUtils.isBlank(width) || "0".equals(width)){
            width = "100%";
        }
       /* if(StringUtils.isBlank(height)){
            height = "400px";
        } else {
            height = height + "px";
        }*/

        String style = contentHelper.generateStyle(widgetCtx, null);
        if (!StringUtils.isBlank(style)) {
            widgetCtx.setParam("wStyle", style);
        }

        String mapUrl = "https://www.google.com/maps/embed/v1/place?key=AIzaSyCGjJC9SVFNZ8mtgDAlG71d8Rsr-ViJitM";

        if(StringUtils.isBlank(longitude) && StringUtils.isBlank(latitude)){
            mapUrl += "&q=" + location.replace(" ", "+");
        } else {
            mapUrl += "&center=" + latitude + "," + longitude;
        }

        if(!StringUtils.isBlank(zoom)){
            mapUrl += "&zoom=" + zoom;
        }

       widgetCtx.setParam("width", width);
 /*        widgetCtx.setParam("height", height);*/
        widgetCtx.setParam("mapUrl", mapUrl);

        widgetCtx.render();
    }

}